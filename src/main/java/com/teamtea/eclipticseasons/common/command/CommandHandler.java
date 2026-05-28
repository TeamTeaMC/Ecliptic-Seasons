package com.teamtea.eclipticseasons.common.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Either;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.weather.special_effect.WeatherEffect;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.teamtea.eclipticseasons.common.misc.MapExporter;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.message.UpdateTempChangeMessage;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.commands.arguments.ResourceOrTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.Biome;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID)
public class CommandHandler {

    private static final DynamicCommandExceptionType ERROR_WEATHER_EFFECT = new DynamicCommandExceptionType(
            p_304101_ -> Component.translatableEscape("commands.weather_effect.invalid", p_304101_)
    );

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        CommandBuildContext buildContext = event.getBuildContext();
        // Reset time command
        // dispatcher.register(Commands.literal("time")
        //        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
        //        .then(Commands.literal("set")
        //                .then(Commands.literal("night")
        //                        .executes((source) -> TimeCommand.setTime(source.getSource(), EclipticUtil.getNightTime(source.getSource().getLevel()))))));

        for (String modId : EclipticSeasonsApi.MODID_LIST) {
            dispatcher.register(Commands.literal(modId)
                    .then(Commands.literal("debug")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.literal("reset")
                                    .then(Commands.literal("surface_biome_cache")
                                            .executes(context -> {
                                                SolarHolders.getSaveDataLazy(context.getSource().getLevel())
                                                        .ifPresent(SolarDataManager::updateBiomeVersion);
                                                return 0;
                                            })
                                            .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((context) ->
                                            {
                                                MapChecker.resetBiomeHolder(context.getSource().getLevel(), BlockPosArgument.getLoadedBlockPos(context, "pos"));
                                                return 0;
                                            }))
                                    )
                            )
                    )
                    .then(Commands.literal("solar")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.literal("set")
                                    .then(Commands.argument("day", IntegerArgumentType.integer())
                                            .executes(commandContext -> setDay(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "day")))))
                            .then(Commands.literal("get")
                                    .executes(commandContext -> {
                                        int solar = EclipticUtil.getNowSolarDay(commandContext.getSource().getLevel());
                                        commandContext.getSource().sendSuccess(() -> Component.literal("" + solar), true);
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("setSnowTempChange")
                                    .then(Commands.argument("tempChange", FloatArgumentType.floatArg(-0.25f, 0.25f))
                                            .executes(commandContext -> setTempChange(commandContext.getSource(), FloatArgumentType.getFloat(commandContext, "tempChange")))))
                            .then(Commands.literal("getSnowTempChange")
                                    .executes(commandContext -> {
                                        float snowTempChange = EclipticUtil.getSnowTempChange(commandContext.getSource().getLevel());
                                        commandContext.getSource().sendSuccess(() -> Component.literal("" + snowTempChange), true);
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("setTerm")
                                    .then(Commands.argument("term", StringArgumentType.word()).suggests((context, builder) -> {
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
                                                                if (s.contains(finalPre.toLowerCase(Locale.ROOT))) {
                                                                    builder.suggest(s, Component.translatable("%s%s%s%s",
                                                                            Component.literal("[").withStyle(ChatFormatting.WHITE),
                                                                            translation.withStyle(solarTerm.getSeason().getColor()).withStyle(ChatFormatting.WHITE),
                                                                            Component.literal("] ").withStyle(ChatFormatting.WHITE)
                                                                            , solarTerm.getAlternationText()));
                                                                }
                                                            }
                                                        }

                                                        return builder.buildFuture();
                                                    })
                                                    .executes(commandContext -> {
                                                        String s = StringArgumentType.getString(commandContext, "term");
                                                        SolarTerm ss = null;
                                                        for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                                                            if (solarTerm.getName().equals(s)) {
                                                                ss = solarTerm;
                                                                break;
                                                            }
                                                        }
                                                        int day = ss.ordinal() * CommonConfig.Season.lastingDaysOfEachTerm.get();
                                                        return setDay(commandContext.getSource(), day);
                                                    })
                                                    .then(Commands.argument("find_next", BoolArgumentType.bool())
                                                            .executes(commandContext -> {
                                                                String s = StringArgumentType.getString(commandContext, "term");
                                                                SolarTerm ss = null;
                                                                for (SolarTerm solarTerm : SolarTerm.collectValues()) {
                                                                    if (solarTerm.getName().equals(s)) {
                                                                        ss = solarTerm;
                                                                        break;
                                                                    }
                                                                }

                                                                ServerLevel level = commandContext.getSource().getLevel();

                                                                int currentYearIndex = EclipticSeasonsApi.getInstance().getSolarYear(level) - 1;
                                                                int currentTermIndex = EclipticSeasonsApi.getInstance().getSolarTerm(level).ordinal();
                                                                int targetTermIndex = ss.ordinal();

                                                                boolean nextClosest = BoolArgumentType.getBool(commandContext, "find_next");
                                                                int yearIndex = nextClosest ? currentYearIndex : 0;
                                                                if (nextClosest && targetTermIndex < currentTermIndex) {
                                                                    yearIndex++;
                                                                }
                                                                int day = (yearIndex * 24 + targetTermIndex)
                                                                        * CommonConfig.Season.lastingDaysOfEachTerm.get();
                                                                return setDay(commandContext.getSource(), day);
                                                            })
                                                    )
                                    ))
                            .then(Commands.literal("getTerm")
                                    .executes(commandContext -> {
                                        var solar = EclipticUtil.getNowSolarTerm(commandContext.getSource().getLevel());
                                        commandContext.getSource().sendSuccess(solar::getTranslation, true);
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("getSeasonSignal")
                                    .executes(commandContext -> {
                                        Entity entity = commandContext.getSource().getEntity();
                                        Season season = EclipticSeasonsApi.getInstance().getSeasonSignal(commandContext.getSource().getLevel(), entity == null ? BlockPos.ZERO : entity.getOnPos());
                                        commandContext.getSource().sendSuccess(season::getTranslation, true);
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("getSubSeason")
                                    .executes(commandContext -> {
                                        Season.Sub sub = Season.Sub.of(EclipticSeasonsApi.getInstance().getSolarTerm(commandContext.getSource().getLevel()));
                                        commandContext.getSource().sendSuccess(sub::getTranslation, true);
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("getMonth")
                                    .executes(commandContext -> {
                                        ITranslatable sub = (EclipticSeasonsApi.getInstance().getGregorianMonth(commandContext.getSource().getLevel()));
                                        commandContext.getSource().sendSuccess(sub::getTranslation, true);
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("getSpecialDay")
                                    .executes(commandContext -> {
                                        Entity entity = commandContext.getSource().getEntity();
                                        var specialDays = EclipticSeasonsApi.getInstance().getSpecialDays(commandContext.getSource().getLevel(), entity == null ? BlockPos.ZERO : entity.getOnPos());
                                        if (specialDays.isEmpty())
                                            commandContext.getSource().sendSuccess(Component::empty, true);
                                        else {
                                            var first = specialDays.getFirst().getKey();
                                            if (first != null)
                                                commandContext.getSource().sendSuccess(() -> Component.literal(first.identifier().toString()), true);
                                        }
                                        return 0;
                                    })
                            )
                            .then(Commands.literal("add")
                                    .then(Commands.argument("day", IntegerArgumentType.integer()).executes(commandContext -> addDay(commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "day"))))))
                    .then(Commands.literal("weather")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.argument("biome", ResourceOrTagArgument.resourceOrTag(event.getBuildContext(), Registries.BIOME))
                                    .then(Commands.literal("rain")
                                            .executes((commandContext) -> setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), true, false))
                                            .then(Commands.argument("effect", ResourceKeyArgument.key(ESRegistries.WEATHER_EFFECT))
                                                    .executes((commandContext) -> {
                                                        setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), true, false);
                                                        return setEffect(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), ResourceKeyArgument.resolveKey(commandContext, "effect", ESRegistries.WEATHER_EFFECT, ERROR_WEATHER_EFFECT));
                                                    }))
                                    )
                                    .then(Commands.literal("thunder")
                                            .executes((commandContext) -> setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), true, true))
                                            .then(Commands.argument("effect", ResourceKeyArgument.key(ESRegistries.WEATHER_EFFECT))
                                                    .executes((commandContext) -> {
                                                        setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), true, true);
                                                        return setEffect(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), ResourceKeyArgument.resolveKey(commandContext, "effect", ESRegistries.WEATHER_EFFECT, ERROR_WEATHER_EFFECT));
                                                    }))
                                    )
                                    .then(Commands.literal("clear")
                                            .executes((commandContext) -> setBiomeRain(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), false, false)))
                                    .then(Commands.literal("snow_depth")
                                            .then(Commands.argument("depth", IntegerArgumentType.integer(0, 100))
                                                    .executes((commandContext) -> setSnowDepth(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), IntegerArgumentType.getInteger(commandContext, "depth"))))
                                    ).then(Commands.literal("effect")
                                            .then(Commands.literal("clear").executes(context -> setEffect(context.getSource(), ResourceOrTagArgument.getResourceOrTag(context, "biome", Registries.BIOME), null)))
                                            .then(Commands.argument("effect", ResourceKeyArgument.key(ESRegistries.WEATHER_EFFECT))
                                                    .executes((commandContext) -> setEffect(commandContext.getSource(), ResourceOrTagArgument.getResourceOrTag(commandContext, "biome", Registries.BIOME), ResourceKeyArgument.resolveKey(commandContext, "effect", ESRegistries.WEATHER_EFFECT, ERROR_WEATHER_EFFECT))))
                                    )
                            )
                    )
                    .then(Commands.literal("export")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.literal("biome_map")
                                    .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((stackCommandContext) ->
                                            MapExporter.exportMap(stackCommandContext.getSource(), BlockPosArgument.getLoadedBlockPos(stackCommandContext, "pos")))))

                            .then(Commands.literal("humid_charts")
                                    .then(Commands.argument("namespace", StringArgumentType.word())
                                            .suggests((context, builder) -> {
                                                String pre = "";
                                                try {
                                                    pre = context.getArgument("namespace", String.class);
                                                } catch (IllegalArgumentException e) {
                                                    // e.printStackTrace();
                                                }
                                                Registry<Biome> biomes = context.getSource().getLevel().registryAccess().lookupOrThrow(Registries.BIOME);
                                                Set<String> collect = biomes.keySet().stream().map(Identifier::getNamespace).collect(Collectors.toSet());

                                                for (String s : collect) {
                                                    if (s.contains(pre)) {
                                                        builder.suggest(s);
                                                    }
                                                }

                                                return builder.buildFuture();
                                            }).executes((stackCommandContext) ->
                                            {
                                                try {
                                                    String s = StringArgumentType.getString(stackCommandContext, "namespace");
                                                    SimpleUtil.exportHumidityChart(stackCommandContext.getSource().getLevel(), s);
                                                    stackCommandContext.getSource().sendSuccess(() ->
                                                                    Component.literal("Can find them in " + "%s/humid/%s".formatted(EclipticSeasonsApi.MODID, s))
                                                            , true);
                                                } catch (Exception e) {
                                                    stackCommandContext.getSource().sendFailure(Component.literal(e.getMessage()));
                                                    return 1;
                                                }

                                                return 0;
                                            })))
                    )
            );
        }
    }

    private static int setEffect(CommandSourceStack sourceStack, ResourceOrTagArgument.Result<Biome> result, Holder<WeatherEffect> effect) {
        ServerLevel level = sourceStack.getLevel();
        var levelBiomeWeather = WeatherManager.getBiomeList(level);
        if (levelBiomeWeather != null) {
            boolean found = false;
            for (WeatherManager.BiomeWeather biomeWeather : levelBiomeWeather) {
                if (result.test(biomeWeather.biomeHolder)) {
                    biomeWeather.effect = effect;
                    biomeWeather.lastRainTime = level.getGameTime();
                    found = true;
                }
            }
            if (found) {
                WeatherManager.sendBiomePacket(level, levelBiomeWeather, level.players());
                SnowyMapChecker.updateAllChunks(level);
                SimpleNetworkHandler.send(level.players(), new EmptyMessage());
            }
        }
        return 0;
    }

    private static int setSnowDepth(CommandSourceStack sourceStack, ResourceOrTagArgument.Result<Biome> result, int depth) {
        ServerLevel level = sourceStack.getLevel();
        var levelBiomeWeather = WeatherManager.getBiomeList(level);
        if (levelBiomeWeather != null) {
            boolean found = false;
            for (WeatherManager.BiomeWeather biomeWeather : levelBiomeWeather) {
                if (result.test(biomeWeather.biomeHolder)) {
                    biomeWeather.setSnowDepth((byte) depth);
                    biomeWeather.lastRainTime = level.getGameTime();
                    found = true;
                }
            }
            if (found) {
                WeatherManager.sendBiomePacket(level, levelBiomeWeather, level.players());
                SnowyMapChecker.updateAllChunks(level);
                SimpleNetworkHandler.send(level.players(), new EmptyMessage());
            }
        }
        return 0;
    }

    public static int setBiomeRain(CommandSourceStack sourceStack, Predicate<Holder<Biome>> result, boolean setRain, boolean isThunder) throws CommandSyntaxException {
        ServerLevel level = sourceStack.getLevel();
        var levelBiomeWeather = WeatherManager.getBiomeList(level);
        if (levelBiomeWeather != null) {
            boolean found = false;
            int size = WeatherManager.getWeatherTickFactor(level);
            SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
            for (WeatherManager.BiomeWeather biomeWeather : levelBiomeWeather) {
                if (result.test(biomeWeather.biomeHolder)) {
                    BiomeRain biomeRain = WeatherManager.getBiomeRain(level, solarTerm, biomeWeather.biomeHolder);
                    biomeWeather.lastRainTime = setRain ? level.getGameTime() : biomeWeather.lastRainTime;
                    biomeWeather.setBiomeRain(biomeRain);
                    found = true;
                }
            }
            if (found) {
                WeatherManager.sendBiomePacket(level, levelBiomeWeather, level.players());
            }
        }
        return 0;
    }

    private static int getDay(ServerLevel worldIn) {
        return SolarHolders.getSaveDataLazy(worldIn).map(SolarDataManager::getSolarTermsDay).orElse(0);
    }

    public static int setDay(CommandSourceStack source, int day) {
        for (ServerLevel serverLevel : List.of(source.getLevel())) {
            SolarHolders.getSaveDataLazy(serverLevel).ifPresent(data ->
            {
                data.setSolarTermsDay(day);
                data.sendAndUpdate(serverLevel);
            });
        }

        source.sendSuccess(() -> Component.translatable("commands.eclipticseasons.solar.set", day), true);
        return getDay(source.getLevel());
    }

    public static int setTempChange(CommandSourceStack source, float tempChange) {
        for (ServerLevel serverLevel : List.of(source.getLevel())) {
            SolarHolders.getSaveDataLazy(serverLevel).ifPresent(data ->
            {
                data.setSolarTempChange(tempChange);
                SimpleNetworkHandler.send(serverLevel.players(), new UpdateTempChangeMessage(tempChange));
            });
        }

        source.sendSuccess(() -> Component.literal(tempChange + ""), true);
        return 0;
    }

    public static int addDay(CommandSourceStack source, int add) {
        for (ServerLevel ServerLevel : List.of(source.getLevel())) {
            SolarHolders.getSaveDataLazy(ServerLevel).ifPresent(data ->
            {
                data.setSolarTermsDay(data.getSolarTermsDay() + add);
                data.sendAndUpdate(ServerLevel);
                source.sendSuccess(() -> Component.translatable("commands.eclipticseasons.solar.set", data.getSolarTermsDay()), true);
            });
        }
        return getDay(source.getLevel());
    }


    public static ResourceOrTagArgument.Result<Biome> createAllResult(RegistryAccess registryAccess) {
        Registry<Biome> biomes = registryAccess.lookupOrThrow(Registries.BIOME);
        return new crs(biomes.get(0).orElse(null));
    }

    private record crs(Holder.Reference<Biome> biomeReference) implements ResourceOrTagArgument.Result<Biome> {
        @Override
        public boolean test(Holder<Biome> biomeHolder) {
            return true;
        }

        @Override
        public @NonNull Either<Holder.Reference<Biome>, HolderSet.Named<Biome>> unwrap() {
            try {
                throw new IllegalCallerException("Should not call the method because it just use for internal.");
            } catch (IllegalCallerException e) {
                e.printStackTrace();
            }
            return Either.left(biomeReference);
        }

        @Override
        public <E> @NonNull Optional<ResourceOrTagArgument.Result<E>> cast(@NonNull ResourceKey<? extends Registry<E>> p_249572_) {
            return Optional.empty();
        }

        @Override
        public @NonNull String asPrintable() {
            return EclipticSeasons.rl("all").toLanguageKey("ResourceOrTagArgument.Result");
        }
    }
}
