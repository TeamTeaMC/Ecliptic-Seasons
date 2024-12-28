package com.teamtea.eclipticseasons.common.command;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSetCommandMinecartPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.TimeCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class CommandHandler {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        // Reset time command
        dispatcher.register(Commands.literal("time").requires((sourceStack) -> sourceStack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.literal("night")
                                .executes((source) -> TimeCommand.setTime(source.getSource(), EclipticUtil.getNightTime(source.getSource().getLevel()))))));


        dispatcher.register(Commands.literal(EclipticSeasons.SMODID)
                . then(Commands.literal("solar")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("day", IntegerArgumentType.integer())
                                        .executes(commandContext -> setDay(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "day")))))
                        .then(Commands.literal("get")
                                .executes(commandContext -> {
                                    var solar = SolarHolders.getSaveData(commandContext.getSource().getLevel()).getSolarTermsDay();
                                    commandContext.getSource().sendSuccess(() -> Component.literal("" + solar), true);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("setTerm")
                                .then(Commands.argument("term", StringArgumentType.greedyString()).suggests((context, builder) -> {
                                            String pre = "";
                                            try {
                                                pre = context.getArgument("term", String.class);
                                            } catch (IllegalArgumentException e) {
                                                // e.printStackTrace();
                                            }
                                            String finalPre = pre;
                                            for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                                                if (solarTerm != SolarTerm.NONE) {
                                                    MutableComponent translation = solarTerm.getTranslation();
                                                    String s = solarTerm.getName();
                                                    if (s.toLowerCase().contains(finalPre.toLowerCase())) {
                                                       //  if (FMLLoader.getDist() == Dist.DEDICATED_SERVER)
                                                       //      builder.suggest(s, Component.translatable("%s%s%s",
                                                       //              translation.withStyle(solarTerm.getSeason().getColor()),
                                                       //              Component.literal(": ").withStyle(ChatFormatting.GRAY)
                                                       //              ,solarTerm.getAlternationText()));
                                                       // else builder.suggest(s, solarTerm.getAlternationText());
                                                        builder.suggest(s, Component.translatable("%s%s%s%s",
                                                                Component.literal("[").withStyle(ChatFormatting.WHITE),
                                                                translation.withStyle(solarTerm.getSeason().getColor()).withStyle(ChatFormatting.WHITE),
                                                                Component.literal("] ").withStyle(ChatFormatting.WHITE)
                                                                ,solarTerm.getAlternationText()));
                                                    }
                                                }
                                            }

                                            return builder.buildFuture();
                                        })
                                        .executes(commandContext -> {
                                            String s = StringArgumentType.getString(commandContext, "term");
                                            SolarTerm ss = null;
                                            for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                                                if (solarTerm.getTranslation().getString().equals(s)) {
                                                    ss = solarTerm;
                                                    break;
                                                }
                                            }
                                            int day = ss.ordinal() * ServerConfig.Season.lastingDaysOfEachTerm.get();
                                            return setDay(commandContext.getSource(), day);
                                        })))
                        .then(Commands.literal("getTerm")
                                .executes(commandContext -> {
                                    var solar = SolarHolders.getSaveData(commandContext.getSource().getLevel()).getSolarTerm();
                                    commandContext.getSource().sendSuccess(solar::getTranslation, true);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("day", IntegerArgumentType.integer()).executes(commandContext -> addDay(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "day"))))))
                .then(Commands.literal("weather")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(event.getBuildContext(), Registries.BIOME))
                                .then(Commands.literal("rain")
                                        .executes((commandContext) -> setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), true, false)))
                                .then(Commands.literal("thunder")
                                        .executes((commandContext) -> setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), true, true)))
                                .then(Commands.literal("clear")
                                        .executes((commandContext) -> setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), false, false)))
                        )
                )
        );
    }

    public static int setBiomeRain(CommandSourceStack sourceStack, ResourceOrTagArgument.Result<Biome> result, boolean setRain, boolean isThunder) throws CommandSyntaxException {
        var levelBiomeWeather = WeatherManager.getBiomeList(sourceStack.getLevel());
        if (levelBiomeWeather != null) {
            boolean found = false;
            int size = levelBiomeWeather.size();
            for (WeatherManager.BiomeWeather biomeWeather : levelBiomeWeather) {
                if (result.test(biomeWeather.biomeHolder)) {

                    biomeWeather.rainTime = setRain ? ServerLevel.RAIN_DURATION.sample(sourceStack.getLevel().getRandom()) / size : 0;
                    biomeWeather.clearTime = setRain ? 0 : ServerLevel.RAIN_DURATION.sample(sourceStack.getLevel().getRandom()) / size;

                    biomeWeather.thunderTime = isThunder ? ServerLevel.THUNDER_DURATION.sample(sourceStack.getLevel().getRandom()) / size : 0;

                    found = true;
                }
            }
            if (found) {
                WeatherManager.sendBiomePacket(levelBiomeWeather, sourceStack.getLevel().players());
            }
        }
        return 0;
    }

    private static int getDay(ServerLevel worldIn) {
        return SolarHolders.getSaveDataLazy(worldIn).map(SolarDataManager::getSolarTermsDay).orElse(0);
    }

    public static int setDay(CommandSourceStack source, int day) {
        for (ServerLevel ServerLevel : List.of(source.getLevel())) {
            SolarHolders.getSaveDataLazy(ServerLevel).ifPresent(data ->
            {
                data.setSolarTermsDay(day);
                data.sendUpdateMessage(ServerLevel);
            });
        }

        source.sendSuccess(() -> Component.translatable("commands.eclipticseasons.solar.set", day), true);
        return getDay(source.getLevel());
    }

    public static int addDay(CommandSourceStack source, int add) {
        for (ServerLevel ServerLevel : List.of(source.getLevel())) {
            SolarHolders.getSaveDataLazy(ServerLevel).ifPresent(data ->
            {
                data.setSolarTermsDay(data.getSolarTermsDay() + add);
                data.sendUpdateMessage(ServerLevel);
                source.sendSuccess(() -> Component.translatable("commands.eclipticseasons.solar.set", data.getSolarTermsDay()), true);
            });
        }
        return getDay(source.getLevel());
    }


    public static final ResourceOrTagArgument.Result<Biome> ALL_BIOME_RESULT = new ResourceOrTagArgument.Result<Biome>() {
        @Override
        public boolean test(Holder<Biome> biomeHolder) {
            return true;
        }

        @Override
        public Either<Holder.Reference<Biome>, HolderSet.Named<Biome>> unwrap() {
            return null;
        }

        @Override
        public <E> Optional<ResourceOrTagArgument.Result<E>> cast(ResourceKey<? extends Registry<E>> p_249572_) {
            return Optional.empty();
        }

        @Override
        public String asPrintable() {
            return null;
        }
    };


    // InputStream inputStream;
    // try {
    //      inputStream = ServerLifecycleHooks.getCurrentServer().getResourceManager().getResourceStack(EclipticSeasons.rl("lang/zh_cn.json")).get(0).open();
    // } catch (IOException e) {
    //     throw new RuntimeException(e);
    // }
    // try
    // {
    //     Pattern PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    //     Gson GSON = new Gson();
    //     JsonElement jsonelement = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonElement.class);
    //     JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonelement, "strings");
    //     Map<String, String> modTable=new HashMap<>();
    //     for (Map.Entry<String, JsonElement> entry : jsonobject.entrySet()) {
    //         String s = PATTERN.matcher(GsonHelper.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
    //         modTable.put(entry.getKey(), s);
    //         builder.suggest(s);
    //     }
    //
    // } finally
    // {
    //     IOUtils.closeQuietly(inputStream);
    // }
}
