package com.teamtea.eclipticseasons.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.command.impl.LocateBiomeCommand;
import net.minecraft.command.impl.TimeCommand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;

import java.util.ArrayList;
import java.util.Arrays;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID)
public class CommandHandler {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        // Reset time command
        dispatcher.register(Commands.literal("time").requires((sourceStack) -> sourceStack.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.literal("night")
                                .executes((source) -> TimeCommand.setTime(source.getSource(), EclipticUtil.getNightTime(source.getSource().getLevel()))))));


        dispatcher.register(Commands.literal(EclipticSeasons.SMODID).
                then(Commands.literal("solar")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("day", IntegerArgumentType.integer())
                                        .executes(commandContext -> setDay(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "day")))))
                        .then(Commands.literal("get")
                                .executes(commandContext -> {
                                    int solar = SolarHolders.getSaveData(commandContext.getSource().getLevel()).getSolarTermsDay();
                                    commandContext.getSource().sendSuccess(new StringTextComponent("" + solar), true);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("setTerm")
                                .then(Commands.argument("term", StringArgumentType.word()).suggests((context, builder) -> {
                                            String pre = "";
                                            try {
                                                pre = context.getArgument("term", ResourceLocation.class).getPath();
                                            } catch (IllegalArgumentException e) {
                                                // e.printStackTrace();
                                            }
                                            String finalPre = pre;
                                            Arrays.stream(SolarTerm.collectValues())
                                                    .filter(solarTerm -> solarTerm != SolarTerm.NONE)
                                                    .map(Enum::toString)
                                                    .filter(s -> s.contains(finalPre)).forEach(builder::suggest);
                                            return builder.buildFuture();
                                        })
                                        .executes(commandContext -> {
                                            String s = StringArgumentType.getString(commandContext, "term");
                                            int day = SolarTerm.valueOf(s).ordinal() * CommonConfig.Season.lastingDaysOfEachTerm.get();
                                            return setDay(commandContext.getSource(), day);
                                        })))
                        .then(Commands.literal("getTerm")
                                .executes(commandContext -> {
                                    SolarTerm solar = SolarHolders.getSaveData(commandContext.getSource().getLevel()).getSolarTerm();
                                    commandContext.getSource().sendSuccess(solar.getTranslation(), true);
                                    return 0;
                                })
                        )
                        .then(Commands.literal("add")
                                .then(Commands.argument("day", IntegerArgumentType.integer()).executes(commandContext -> addDay(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "day"))))))
                .then(Commands.literal("weather")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("biome", ResourceLocationArgument.id())
                                .then(Commands.literal("rain")
                                        .executes((commandContext) -> setBiomeRain(commandContext.getSource(), commandContext.getArgument("biome", ResourceLocation.class), true, false)))
                                .then(Commands.literal("thunder")
                                        .executes((commandContext) -> setBiomeRain(commandContext.getSource(), commandContext.getArgument("biome", ResourceLocation.class), true, true)))
                                .then(Commands.literal("clear")
                                        .executes((commandContext) -> setBiomeRain(commandContext.getSource(), commandContext.getArgument("biome", ResourceLocation.class), false, false)))
                        )
                )
        );
    }

    public static int setBiomeRain(CommandSource sourceStack, ResourceLocation result, boolean setRain, boolean isThunder) throws CommandSyntaxException {
        Biome biome = sourceStack.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(result).orElseThrow(() -> {
            return LocateBiomeCommand.ERROR_INVALID_BIOME.create(sourceStack);
        });
        ArrayList<WeatherManager.BiomeWeather> levelBiomeWeather = WeatherManager.getBiomeList(sourceStack.getLevel());
        if (levelBiomeWeather != null) {
            boolean found = false;
            int size = levelBiomeWeather.size();
            for (WeatherManager.BiomeWeather biomeWeather : levelBiomeWeather) {
                if (biome.equals(biomeWeather.biomeHolder)) {

                    biomeWeather.rainTime = setRain ? (sourceStack.getLevel().getRandom().nextInt(12000)+12000)/ size : 0;
                    biomeWeather.clearTime = setRain ? 0 : (sourceStack.getLevel().getRandom().nextInt(12000)+12000) / size;

                    biomeWeather.thunderTime = isThunder ? (sourceStack.getLevel().getRandom().nextInt(12000)+3600) / size : 0;

                    found = true;
                }
            }
            if (found) {
                WeatherManager.sendBiomePacket(levelBiomeWeather, sourceStack.getLevel().players());
            }
        }
        return 0;
    }

    private static int getDay(ServerWorld worldIn) {
        return SolarHolders.getSaveDataLazy(worldIn).map(SolarDataManager::getSolarTermsDay).orElse(0);
    }

    public static int setDay(CommandSource source, int day) {
        // for (var ServerLevel : List.of(source.getLevel()))
        {
            SolarHolders.getSaveDataLazy(source.getLevel()).ifPresent(data ->
            {
                data.setSolarTermsDay(day);
                data.sendUpdateMessage(source.getLevel());
            });
        }

        source.sendSuccess(new TranslationTextComponent("commands.eclipticseasons.solar.set", day), true);
        return getDay(source.getLevel());
    }

    public static int addDay(CommandSource source, int add) {
        // for (var ServerLevel : List.of(source.getLevel()))
        {
            SolarHolders.getSaveDataLazy(source.getLevel()).ifPresent(data ->
            {
                data.setSolarTermsDay(data.getSolarTermsDay() + add);
                data.sendUpdateMessage(source.getLevel());
                source.sendSuccess(new TranslationTextComponent("commands.eclipticseasons.solar.set", data.getSolarTermsDay()), true);
            });
        }
        return getDay(source.getLevel());
    }




}
