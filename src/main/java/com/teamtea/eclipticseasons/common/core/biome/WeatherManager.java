package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.constant.climate.WeatherMode;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESEnchantmentTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESMobEffectTags;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.network.message.UpdateTempChangeMessage;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.message.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.WeatherCheck;
import net.neoforged.neoforge.common.util.FakePlayer;

import javax.annotation.Nullable;
import java.util.*;

public class WeatherManager {

    public static final Map<Level, ArrayList<BiomeWeather>> BIOME_WEATHER_LIST = new IdentityHashMap<>();
    public static final Map<Level, Integer> NEXT_CHECK_BIOME_MAP = new IdentityHashMap<>();
    public static final Map<Level, Map<Biome, BiomeWeather>> BIOME_WEATHER_QUERY_LIST = new IdentityHashMap<>();

    public static ArrayList<BiomeWeather> getBiomeList(Level level) {
        if (level == null) {
            for (ArrayList<BiomeWeather> value : BIOME_WEATHER_LIST.values()) {
                return value;
            }
            return null;
        }
        if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
            return iBiomeWeatherProvider.es$get();
        }
        return BIOME_WEATHER_LIST.getOrDefault(level, null);
    }

    public static int getWeatherTickFactor(Level level) {
        ArrayList<BiomeWeather> biomeList = getBiomeList(level);
        int size = biomeList == null ? 1 : biomeList.size();
        size = (int) (size * (Mth.clamp(7f / EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm(level), 0.8f, 3f)));
        size = Math.max(1, size);
        return size;
    }

    public static Level fetchLevelIfNull(Level level, Biome biome) {
        level = level != null || !BiomeClimateManager.CLIENT_BIOME_TAG_KEY_MAP.containsKey(biome)
                ? level : ClientCon.getUseLevel();
        return level != null ? level : getMainServerLevel();
    }

    @Nullable
    public static BiomeWeather getBiomeWeather(Level level, Holder<Biome> biomeHolder) {
        if (biomeHolder == null) return null;
        return getBiomeWeather(level, biomeHolder.value());
    }

    @Nullable
    public static BiomeWeather getBiomeWeather(Level level, Biome biome) {
        BiomeWeather weather = null;
        // var weatherQueryListOrDefault = BIOME_WEATHER_QUERY_LIST.getOrDefault(level, null);
        // if (weatherQueryListOrDefault != null) {
        //     weather = weatherQueryListOrDefault.getOrDefault(biome, null);
        // }
        // var weatherQueryListOrDefault = BIOME_WEATHER_LIST.getOrDefault(level, null);
        if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
            var weatherQueryListOrDefault = iBiomeWeatherProvider.es$get();
            if (weatherQueryListOrDefault != null) {
                Object object = biome;
                if (object instanceof IBiomeTagHolder iBiomeTagHolder) {
                    int id = iBiomeTagHolder.eclipticseasons$getBindId();
                    if (weatherQueryListOrDefault.size() > id && id > -1)
                        weather = weatherQueryListOrDefault.get(id);
                }
            }
        }
        return weather;
    }

    public static void tickAverageWeather(Level level) {
        if (level instanceof IBiomeWeatherProvider provider
                && EclipticSeasonsApi.getInstance().hasLocalWeather(level)) {
            provider.es$setAverageRainLevel(calculateAverageRainLevel(level, 1f));
            provider.es$setAverageThunderLevel(calculateAverageThunderLevel(level, 1f));
        }
    }


    public static float calculateAverageRainLevel(Level level, float delta) {
        return getAverageWeatherLevel(level, delta, BiomeWeather::shouldRain);
    }

    public static float calculateAverageThunderLevel(Level level, float delta) {
        return getAverageWeatherLevel(level, delta, BiomeWeather::shouldThunder);
    }

    public static float getAverageRainLevel(Level level, float delta) {
        return ((IBiomeWeatherProvider) level).es$getAverageRainLevel(delta);
    }

    public static boolean isEffectiveRaining(ServerLevel level) {
        return getAverageRainLevel(level, 1f) > CompatModule.CommonConfig.weatherVotePercent.get();
    }

    public static float getAverageThunderLevel(Level level, float delta) {
        return ((IBiomeWeatherProvider) level).es$getAverageThunderLevel(delta);
    }

    public static boolean isEffectiveThundering(ServerLevel level) {
        return getAverageThunderLevel(level, 1f) > CompatModule.CommonConfig.weatherVotePercent.get();
    }

    @FunctionalInterface
    public interface BiomeWeatherPredicate {
        boolean test(BiomeWeather biome);
    }

    // todo cache weather query for it
    public static float getAverageWeatherLevel(Level level, float delta, BiomeWeatherPredicate function) {
        List<? extends Player> players = level.players();
        if (!players.isEmpty()) {
            if (players.size() == 1) {
                return function.test(getBiomeWeather(level, MapChecker.getSurfaceBiome(level, players.get(0).blockPosition()))) ? 1f : 0;
            } else {
                float thunder = 0f;
                for (int i = 0, playersSize = players.size(); i < playersSize; i++) {
                    Player player = players.get(i);
                    thunder += function.test(getBiomeWeather(level, MapChecker.getSurfaceBiome(level, player.blockPosition())))
                            ? 1f : 0;
                }
                return thunder / (float) players.size();
            }
        }

        return function.test(getBiomeWeather(level, MapChecker.getSurfaceBiome(level, level.getSharedSpawnPos()))) ? 1f : 0;
    }


    public static void onSetWeatherParameters(ServerLevel level, int pClearTime, int pWeatherTime, boolean pIsRaining, boolean pIsThundering) {
        ArrayList<BiomeWeather> biomeList = getBiomeList(level);
        if (biomeList == null) return;
        int size = getWeatherTickFactor(level);
        List<? extends Player> players = level.players();
        int clearTime = pIsRaining ? 0 : pClearTime / size;
        int rainTime = pIsRaining ? pWeatherTime / size : 0;
        int thunderTime = pIsRaining && pIsThundering ? pWeatherTime / size : 0;
        if (!players.isEmpty()) {
            if (players.size() == 1) {
                BiomeWeather biomeWeather = getBiomeWeather(level, MapChecker.getSurfaceBiome(level, players.get(0).blockPosition()));
                if (biomeWeather != null) {
                    setBiomeWeather(level, biomeWeather, clearTime, rainTime, thunderTime);
                }
            } else {
                for (Player player : players) {
                    BiomeWeather biomeWeather = getBiomeWeather(level, MapChecker.getSurfaceBiome(level, player.blockPosition()));
                    if (biomeWeather != null) {
                        setBiomeWeather(level, biomeWeather, clearTime, rainTime, thunderTime);
                    }
                }
            }
        } else {
            for (BiomeWeather biomeWeather : biomeList) {
                Biome biome = biomeWeather.biomeHolder.value();
                if (!hasNonePrecipitation(biome)) {
                    setBiomeWeather(level, biomeWeather, clearTime, rainTime, thunderTime);
                }
            }
        }
    }

    public static void setBiomeWeather(ServerLevel level, BiomeWeather biomeWeather, int clearTime, int rainTime, int thunderTime) {
        biomeWeather.clearTime = clearTime;
        biomeWeather.rainTime = rainTime;
        biomeWeather.thunderTime = thunderTime;
        biomeWeather.lastRainTime = rainTime > 0 ? level.getGameTime() : biomeWeather.lastRainTime;
    }

    public static boolean isThunderAtBiome(Level level, BlockPos pos) {
        Holder<Biome> surfaceBiome = MapChecker.getSurfaceBiome(level, pos);
        return isThunderAtBiome(level, surfaceBiome);
    }

    public static boolean isThunderAtBiome(Level level, Holder<Biome> biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.shouldThunder();
        }
        return false;
    }

    public static boolean isThunderAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return isThunderAtBiome(level, biome);
    }

    public static boolean isRainingUnderSky(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return getRainOrSnow(level, biome.value(), pos) == Biome.Precipitation.RAIN;
    }


    public static boolean isRainingAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return getRainOrSnow(level, biome.value(), pos) == Biome.Precipitation.RAIN;
    }

    public static boolean isRainingOrSnowAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return isRainingOrSnowAtBiome(level, biome);
    }

    public static boolean isRainingOrSnowAtBiome(Level level, Holder<Biome> biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.shouldRain();
        }
        return false;
    }

    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        if (hasNonePrecipitation(biome)) return Biome.Precipitation.NONE;

        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            if (biomeWeather.shouldClear()) return Biome.Precipitation.NONE;

            // check attach
            // SnowyRemover snowyRemover = level.getChunk(pos).getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
            // if (snowyRemover != null) {
            //     SnowyRemover.SnowyFlag snowyFlag = snowyRemover.getSnowyFlag(pos);
            //     if (snowyFlag == SnowyRemover.SnowyFlag.NONE_SNOWY)
            //         return Biome.Precipitation.RAIN;
            //     else if (snowyFlag == SnowyRemover.SnowyFlag.SNOWY_ALWAYS)
            //         return Biome.Precipitation.SNOW;
            // }

            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm, biome, pos, level instanceof ServerLevel);
            return flag_cold
                    // || BiomeClimateManager.getDefaultTemperature(biome, !level.isClientSide()) <= BiomeClimateManager.SNOW_LEVEL
                    ?
                    Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        }

        return Biome.Precipitation.NONE;
    }


    public static int getSnowDepthAtBiome(Level level, Biome biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.snowDepth;
        }
        return 0;
    }

    public static long getLastRainTimeAtBiome(Level level, Biome biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.lastRainTime;
        }
        return 0;
    }

    public static ServerLevel getMainServerLevel() {
        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == Level.OVERWORLD
                    && !level.isClientSide()
                    && level instanceof ServerLevel serverLevel) {
                return serverLevel;
            }
        }
        return null;
    }


    public static Biome.Precipitation getPrecipitationAt(Biome biome, BlockPos pos) {
        var level = fetchLevelIfNull(null, biome);
        if (level != null && CompatModule.CommonConfig.fixBiome.get() && MapChecker.isSmallBiome(biome)) {
            // if (MapChecker.isLoadNearByOnlyServer(level, pos))
            {
                biome = MapChecker.getSurfaceBiome(level, pos).value();
            }
        }
        return getPrecipitationAt(level, biome, pos);
    }

    public static Biome.Precipitation getPrecipitationAt(@Nullable Level levelNull, Biome biome, BlockPos pos) {

        // check if it has predication
        if (hasNonePrecipitation(biome)) return Biome.Precipitation.NONE;

        var level = fetchLevelIfNull(levelNull, biome);

        // var provider = SolarHolders.getSaveData(level);
        // var weathers = getBiomeList(level);

        // Not add 'has' check because we have checked it
        if (level != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, levelNull instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm, biome, pos, level instanceof ServerLevel);
            // var biomes = level.registryAccess().registry(Registries.BIOME).get();
            // var loc = biomes.getKey(biome);
            BiomeWeather biomeWeather = getBiomeWeather(level, biome);
            if (biomeWeather != null) {
                // if (biomeWeather.shouldClear())
                //     return Biome.Precipitation.NONE;
                return flag_cold
                        // || BiomeClimateManager.getDefaultTemperature(biome, levelNull instanceof ServerLevel) <= BiomeClimateManager.SNOW_LEVEL
                        ?
                        Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
            }

        }

        return Biome.Precipitation.NONE;
    }

    public static boolean hasNonePrecipitation(Biome biome) {
        if (!biome.hasPrecipitation()) {
            return true;
        }

        return CommonConfig.Weather.notRainInDesert.get()
                && !biome.getModifiedClimateSettings().hasPrecipitation()
                && BiomeClimateManager.getTag(biome) == ClimateTypeBiomeTags.MONSOONAL;
    }

    public static void createLevelBiomeWeatherList(Level level) {
        var biomes = level.registryAccess().registry(Registries.BIOME);
        if (biomes.isPresent()) {
            var biomesWeathers = new ArrayList<BiomeWeather>(biomes.get().size());
            var biomesWeathersArray = new BiomeWeather[biomes.get().size()];
            for (Biome biome : biomes.get()) {
                // biomes.get().holders().toList().getFirst().getDelegate()
                ResourceLocation loc = biomes.get().getKey(biome);
                int id = biomes.get().getId(biome);
                Optional<Holder.Reference<Biome>> biomesHolder = biomes.get().getHolder(loc);
                if (biomesHolder.isPresent()) {
                    var biomeWeather = new BiomeWeather(biomesHolder.get());
                    biomeWeather.location = loc;
                    biomeWeather.id = id;
                    // biomesWeathers.set(id, biomeWeather);
                    biomesWeathersArray[id] = biomeWeather;
                    ((IBiomeTagHolder) (Object) biome).eclipticseasons$setBindId(id);
                }
            }
            biomesWeathers = new ArrayList<>(Arrays.stream(biomesWeathersArray).toList());
            WeatherManager.BIOME_WEATHER_LIST.put(level, biomesWeathers);

            if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
                iBiomeWeatherProvider.es$set(biomesWeathers);
            }

            // add copy
            Map<Biome, BiomeWeather> biomeBiomeWeatherMap = new IdentityHashMap<>();
            for (BiomeWeather biomesWeather : biomesWeathers) {
                biomeBiomeWeatherMap.put(biomesWeather.biomeHolder.value(), biomesWeather);
            }
            WeatherManager.BIOME_WEATHER_QUERY_LIST.put(level, biomeBiomeWeatherMap);
        }
    }

    public static void informUpdateBiomes(RegistryAccess registryAccess, boolean isServer) {

        WeatherManager.BIOME_WEATHER_LIST.forEach((key, biomeWeathers) -> {
            if ((key instanceof ServerLevel) == isServer) {
                registryAccess.registry(Registries.BIOME)
                        .ifPresent(biomeRegistry -> biomeRegistry
                                .holders().forEach(biomeHolder ->
                                {
                                    ResourceLocation loc = biomeHolder.key().location();
                                    var id = biomeRegistry.getId(biomeHolder.value());
                                    boolean inList = false;
                                    for (BiomeWeather biomeWeather : biomeWeathers) {
                                        if (biomeWeather.biomeHolder.is(loc)) {
                                            biomeWeather.id = id;
                                            biomeWeather.biomeHolder = biomeHolder;
                                            inList = true;
                                            break;
                                        }
                                    }
                                    if (!inList) {
                                        var biomeWeather = new BiomeWeather(biomeHolder);
                                        biomeWeather.location = loc;
                                        biomeWeather.id = id;
                                        biomeWeathers.add(biomeWeather);
                                    }
                                }));
            }
        });

        WeatherManager.BIOME_WEATHER_LIST.forEach((key, value) -> value.sort(Comparator.comparing(c -> c.id)));
    }

    public static void tickPlayerSeasonEffect(ServerPlayer player) {
        if (player.isCreative() || player.isSpectator() ||
                !CommonConfig.Temperature.heatStroke.get()) return;
        Level level = player.level();
        if (MapChecker.isValidDimension(level)
                && level.getRandom().nextInt(150) == 0)
            SolarHolders.getSaveDataLazy(level).ifPresent(solarDataManager -> {
                if (EclipticUtil.getNowSolarTerm(level).isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                    Biome biome = level.getBiome(player.blockPosition()).value();
                    if (EclipticUtil.getTemperatureFloat(level, biome, player.blockPosition()) > 0.85f) {
                        if (!player.isInWaterOrRain()
                                && ((EclipticUtil.isNoon(level)
                                && (level.canSeeSky(player.blockPosition()))))
                        ) {
                            boolean isColdHe = false;
                            armorChecks:
                            for (ItemStack itemstack : player.getArmorSlots()) {
                                Item item = itemstack.getItem();
                                if (item instanceof Equipable equipable) {
                                    if (equipable.getEquipmentSlot() == EquipmentSlot.HEAD) {
                                        if (itemstack.is(ESItemTags.HEAT_PROTECTIVE_HELMETS)) {
                                            isColdHe = true;
                                            break;
                                        }
                                        ItemEnchantments allEnchantments = itemstack.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
                                        Set<Holder<Enchantment>> keySet = allEnchantments.keySet();
                                        if (!keySet.isEmpty()) {
                                            for (Holder<Enchantment> enchantment : keySet) {
                                                if (enchantment.is(ESEnchantmentTags.HEATSTROKE_RESISTANT)) {
                                                    isColdHe = true;
                                                    break armorChecks;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            if (!isColdHe) {
                                NonNullList<ItemStack> items = player.getInventory().items;
                                int selectionSize = Inventory.getSelectionSize();
                                for (int i = 0, itemsSize = items.size(); i < itemsSize && i < selectionSize; i++) {
                                    ItemStack itemstack = items.get(i);
                                    if (itemstack.is(ESItemTags.COOLING_ITEMS)) {
                                        isColdHe = true;
                                        break;
                                    }
                                }
                            }
                            if (!isColdHe) {
                                isColdHe = player.hasEffect(MobEffects.FIRE_RESISTANCE);
                                for (MobEffectInstance activeEffect : player.getActiveEffects()) {
                                    if (activeEffect.getEffect().is(ESMobEffectTags.HEATSTROKE_RESISTANT)) {
                                        isColdHe = true;
                                        break;
                                    }
                                }
                            }
                            if (!isColdHe) {
                                var heatStroke = BuiltInRegistries.MOB_EFFECT.getHolder(EffectRegistry.Effects.HEAT_STROKE).get();
                                player.addEffect(new MobEffectInstance(heatStroke, 600));
                                ModAdvancements.heatStrokeCriterion.get().trigger(player);
                            }
                        }
                    }
                }
            });
    }

    public static void runWeather(ServerLevel level, BiomeWeather biomeWeather, RandomSource random, int size) {
        WeatherMode weatherMode = EclipticUtil.getWeatherMode(level);
        if (weatherMode == WeatherMode.REGION) {
            Holder<Biome> onwer = BiomeClimateManager.getWeatherRegionOnwer(biomeWeather.biomeHolder.value());
            if (onwer != null&& !onwer.equals(biomeWeather.biomeHolder)) {
                BiomeWeather ownerBiomeWeather = getBiomeWeather(level, onwer);
                if (ownerBiomeWeather != null) {
                    biomeWeather.rainTime = ownerBiomeWeather.rainTime;
                    biomeWeather.thunderTime = ownerBiomeWeather.thunderTime;
                    biomeWeather.clearTime = ownerBiomeWeather.clearTime;
                    updateSnowOrMelt(level, biomeWeather, random, size, biomeWeather.shouldRain());
                    return;
                }
            }
        }

        if (!biomeWeather.biomeHolder.value().hasPrecipitation())
            return;
        boolean isEcliptic = EclipticUtil.hasLocalWeather(level);


        if (isEcliptic) {
            if (biomeWeather.shouldClear()) {
                biomeWeather.clearTime--;
            } else {
                if (biomeWeather.shouldRain()) {
                    biomeWeather.rainTime--;
                    if (!biomeWeather.shouldThunder()) {
                        SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);
                        BiomeRain biomeRain = getBiomeRain(level, solarTerm, biomeWeather.biomeHolder);
                        float weight = biomeRain.getThunderChance()
                                * ((CommonConfig.Weather.thunderChanceMultiplier.get() * 1f) / 100f)
                                * size / 3000f;
                        if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                            biomeWeather.thunderTime = biomeRain.getThunderDuration(random) / size;
                        }
                    }
                } else {
                    SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);
                    BiomeRain biomeRain = getBiomeRain(level, solarTerm, biomeWeather.biomeHolder);
                    float downfall = EclipticUtil.getDownfallFloatConstant(solarTerm, biomeWeather.biomeHolder.value(), !level.isClientSide());
                    float weight = biomeRain.getRainChance()
                            * Math.max(0.01f, downfall)
                            * ((CommonConfig.Weather.rainChanceMultiplier.get() * 1f) / 100f);
                    if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                        biomeWeather.rainTime = biomeRain.getRainDuration(random) / size;
                    } else {
                        // biomeWeather.clearTime = 10 / (size / 30);
                        biomeWeather.clearTime = biomeRain.getRainDelay(random) / size;
                    }
                }
            }

            if (biomeWeather.shouldThunder()) {
                biomeWeather.thunderTime--;
                if (!biomeWeather.shouldRain()) {
                    biomeWeather.thunderTime = 0;
                }
            }
            updateSnowOrMelt(level, biomeWeather, random, size, biomeWeather.shouldRain());
        } else {
            updateSnowOrMelt(level, biomeWeather, random, size, level.isRaining());
        }
    }

    protected static void updateSnowOrMelt(ServerLevel level, BiomeWeather biomeWeather, RandomSource randomSource, int size, boolean rain) {
        if (rain) biomeWeather.lastRainTime = level.getGameTime();
        if ((rain || randomSource.nextInt(5) > 1)) {
            var snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder, BlockPos.ZERO, rain);
            int rate = Math.max(1, size >> 6);
            if (snow == SnowRenderStatus.SNOW) {
                biomeWeather.snowDepth = (byte) Math.min(100, biomeWeather.snowDepth + rate);
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                biomeWeather.snowDepth = (byte) Math.max(0, biomeWeather.snowDepth - rate);
            }
        }
    }

    public static BiomeRain getBiomeRain(ServerLevel level, SolarTerm solarTerm, Holder<Biome> biomeWeather) {
        return getBiomeRain(solarTerm, biomeWeather).resolve(level);
    }

    public static BiomeRain getBiomeRain(SolarTerm solarTerm, Holder<Biome> biomeWeather) {
        return solarTerm.getBiomeRain(biomeWeather);
    }

    public static void initNewWorldWeather(ServerLevel level, RandomSource random, SolarTerm solarTerm) {
        if (!CommonConfig.Weather.shouldInitWeather.get()
                || level.isClientSide() || !MapChecker.isValidDimension(level)) {
            return;
        }
        ArrayList<BiomeWeather> biomeList = getBiomeList(level);
        if (biomeList == null) return;

        int size = getWeatherTickFactor(level);
        SolarTerm lastSolarTerm =
                solarTerm == SolarTerm.NONE ? SolarTerm.NONE :
                        SolarTerm.collectValues()[(solarTerm.ordinal() - 1 + 24) % 24];
        boolean weatherLocal = EclipticUtil.hasLocalWeather(level);
        for (BiomeWeather biomeWeather : biomeList) {
            if (!biomeWeather.biomeHolder.value().hasPrecipitation())
                continue;
            if (weatherLocal) {
                float ramdomKey = level.getRandom().nextInt(1000) / 1000.f * 3;

                BiomeRain biomeRain = getBiomeRain(solarTerm, biomeWeather.biomeHolder);
                float downfall = EclipticUtil.getDownfallFloatConstant(solarTerm, biomeWeather.biomeHolder.value(), !level.isClientSide());

                float weight = biomeRain.getRainChance()
                        * Math.max(0.01f, downfall)
                        * ((CommonConfig.Weather.rainChanceMultiplier.get() * 1f) / 100f);
                if (ramdomKey < weight) {
                    biomeWeather.rainTime = biomeRain.getRainDuration(random) / size;
                } else {
                    biomeWeather.clearTime = biomeRain.getRainDelay(random) / size;
                }
                if (biomeWeather.shouldRain()) {
                    weight = biomeRain.getThunderChance()
                            * ((CommonConfig.Weather.thunderChanceMultiplier.get() * 1f) / 100f);
                    if (ramdomKey / 1000.f < weight) {
                        biomeWeather.thunderTime = biomeRain.getThunderDuration(random) / size;
                    }
                }
            }

            var snowTerm = SolarTerm.getSnowTerm(biomeWeather.biomeHolder.value(), !level.isClientSide(), EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            boolean flag_little_cold = snowTerm.maySnow(lastSolarTerm);
            SnowRenderStatus snow = flag_cold ? SnowRenderStatus.SNOW :
                    flag_little_cold ? SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            if (snow == SnowRenderStatus.SNOW) {
                biomeWeather.snowDepth = 100;
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                biomeWeather.snowDepth = (byte) random.nextInt(50);
            } else biomeWeather.snowDepth = 0;
        }
    }

    public static void updateAfterSleep(ServerLevel level, long newTime, long oldDayTime) {
        if (newTime > oldDayTime) {
            var ws = WeatherManager.getBiomeList(level);
            if (ws != null) {
                var random = level.getRandom();
                int size = getWeatherTickFactor(level);
                for (BiomeWeather biomeWeather : ws) {
                    for (int i = 0; i < (newTime - oldDayTime) / size; i++) {
                        WeatherManager.runWeather(level, biomeWeather, random, size);
                    }
                }

                if (CommonConfig.Weather.clearAfterSleep.get()) {
                    SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);
                    for (BiomeWeather biomeWeather : ws) {
                        if (biomeWeather.shouldRain()) {
                            biomeWeather.thunderTime = 0;
                            biomeWeather.rainTime = 0;
                            BiomeRain biomeRain = getBiomeRain(level, solarTerm, biomeWeather.biomeHolder);
                            biomeWeather.clearTime = biomeRain.getRainDelay(random) / size;
                        }
                    }
                }

                if (!level.players().isEmpty()) {
                    WeatherManager.sendBiomePacket(ws, level.players());
                }

                SnowyMapChecker.updateAllChunks(level);
            }
        }
        SimpleNetworkHandler.send(new ArrayList<>(level.players()), new EmptyMessage(true));
    }

    public static void onLoggedIn(ServerPlayer serverPlayer, boolean isLogged) {
        if ((serverPlayer instanceof FakePlayer)) return;
        SolarHolders.getSaveDataLazy(serverPlayer.level()).ifPresent(t ->
        {
            SimpleNetworkHandler.send(serverPlayer, new SolarTermsMessage(t.getSolarTermsDay()));
            if ((CommonConfig.Season.enableInform.get())
                    && isLogged
                    && MapChecker.isValidDimension(serverPlayer.level())
                    && t.getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                SolarTerm solarTerm = t.getSolarTerm();
                // if (solarTerm != SolarTerm.NONE)
                SimpleUtil.sendSolarTermMessage(serverPlayer, solarTerm, isLogged);
            }
            SimpleNetworkHandler.send(serverPlayer, new UpdateTempChangeMessage(t.getSolarTempChange()));
        });
        WeatherManager.sendBiomePacket(WeatherManager.getBiomeList(serverPlayer.level()), List.of(serverPlayer));
    }

    public static boolean testWeatherCheck(LootContext pContext, WeatherCheck weatherCheck) {
        boolean needThunder = weatherCheck.isThundering().isPresent();
        boolean needRain = weatherCheck.isRaining().isPresent();
        if (needThunder) {
            var pos = pContext.getParamOrNull(LootContextParams.ORIGIN);
            if (pos != null) {
                boolean isThunderAt = isThunderAt(pContext.getLevel(), new BlockPos((int) pos.x, (int) pos.y + 1, (int) pos.z));
                if (weatherCheck.isThundering().get() != isThunderAt) {
                    return false;
                }
            }
        }
        if (needRain) {
            var pos = pContext.getParamOrNull(LootContextParams.ORIGIN);
            if (pos != null) {
                boolean isRainingAt = pContext.getLevel().isRainingAt(new BlockPos((int) pos.x, (int) pos.y + 1, (int) pos.z));
                if (weatherCheck.isRaining().get() != isRainingAt) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void tickPlayerForSeasonCheck(ServerPlayer serverPlayer, SolarTerm st) {
        // if (level.getGameTime() % 200 == 0)
        {
            var solarTermsRecordCa = serverPlayer.getData(AttachmentRegistry.SOLAR_TERMS_RECORD.get());

            // int timeInTerm = EclipticSeasonsApi.getInstance().getTimeInTerm(level);
            // if (timeInTerm != 0) return;

            if (solarTermsRecordCa.addAndCheck(st)) {
            } else ModAdvancements.solarTermsCriterion.get().trigger(serverPlayer);
        }
    }

    public static int getSkyDarken(Level level, BlockPos pos, int amount) {
        WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, MapChecker.getSurfaceBiome(level, pos));
        amount += biomeWeather == null || biomeWeather.shouldClear() ? 0 :
                biomeWeather.shouldThunder() ? 8 : 4;
        return Mth.clamp(amount, 0, 15);
    }

    public static class BiomeWeather {
        public Holder<Biome> biomeHolder;
        public int id;

        public ResourceLocation location;
        public int rainTime = 0;
        public int thunderTime = 0;
        public int clearTime = 0;
        public byte snowDepth = 0;
        public long lastRainTime = 0;


        public BiomeWeather(Holder<Biome> biomeHolder) {
            this.biomeHolder = biomeHolder;
        }

        public boolean shouldRain() {
            return rainTime > 0;
        }

        public boolean shouldThunder() {
            return thunderTime > 0;
        }

        public boolean shouldClear() {
            return clearTime > 0;
        }


        @Override
        public String toString() {
            return serializeNBT().toString();
        }


        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("biome", location.toString());
            tag.putInt("rainTime", rainTime);
            tag.putLong("lastRainTime", lastRainTime);
            tag.putInt("thunderTime", thunderTime);
            tag.putInt("clearTime", clearTime);
            tag.putByte("snowDepth", snowDepth);
            return tag;
        }


        public void deserializeNBT(CompoundTag nbt) {
            location = ResourceLocation.parse(nbt.getString("biome"));
            rainTime = nbt.getInt("rainTime");
            lastRainTime = nbt.getLong("lastRainTime");
            thunderTime = nbt.getInt("thunderTime");
            clearTime = nbt.getInt("clearTime");
            snowDepth = nbt.getByte("snowDepth");
        }
    }

    public static boolean onCheckWarmEnoughToRain(BlockPos p198905) {
        // return SolarTerm.get(AllListener.provider.resolve().get().worldSolarTime.getSolarTermIndex()).getSeason() != Season.WINTER;
        return true;
    }

    public static boolean onShouldSnow(ServerLevel level, Biome biome, BlockPos pos) {
        // return SolarTerm.get(AllListener.provider.resolve().get().worldSolarTime.getSolarTermIndex()).getSeason() == Season.WINTER;
        return true;
    }

    public static boolean agentAdvanceWeatherCycle(ServerLevel level, RandomSource random) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return true;
        // }

        int pos = NEXT_CHECK_BIOME_MAP.getOrDefault(level, -1);

        var levelBiomeWeather = getBiomeList(level);

        if (pos >= 0 && levelBiomeWeather != null && pos < levelBiomeWeather.size()) {
            int size = getWeatherTickFactor(level);
            var biomeWeather = getBiomeList(level).get(pos);

            runWeather(level, biomeWeather, random, size);

            pos++;
        } else {
            pos = 0;
        }
        // Ecliptic.logger(level.getGameTime(),level.getGameTime() & 100);
        if (levelBiomeWeather != null && (level.getGameTime() % 100) == 0 && !level.players().isEmpty()) {
            // EclipticSeasonsMod.logger(level.getGameTime());
            sendBiomePacket(levelBiomeWeather, level.players());
        }

        NEXT_CHECK_BIOME_MAP.put(level, pos);
        return true;
    }

    public static void sendBiomePacket(ArrayList<BiomeWeather> levelBiomeWeather, List<ServerPlayer> players) {
        if (players.isEmpty()) return;
        byte[] rains = new byte[levelBiomeWeather.size()];
        byte[] thunders = new byte[levelBiomeWeather.size()];
        byte[] clears = new byte[levelBiomeWeather.size()];
        byte[] snows = new byte[levelBiomeWeather.size()];
        for (BiomeWeather biomeWeather : levelBiomeWeather) {
            int index = biomeWeather.id;
            rains[index] = (byte) (biomeWeather.shouldRain() ? 1 : 0);
            thunders[index] = (byte) (biomeWeather.shouldThunder() ? 1 : 0);
            clears[index] = (byte) (biomeWeather.shouldClear() ? 1 : 0);
            snows[index] = biomeWeather.snowDepth;
        }
        var msg = new BiomeWeatherMessage(rains, thunders, clears, snows);
        SimpleNetworkHandler.send(players, msg);
    }

    public enum SnowRenderStatus {
        SNOW,
        SNOW_MELT,
        // RAIN,
        // CLOUD,
        NONE
    }

    public enum SnowStatus implements ITranslatable {
        SNOW,
        // MELT,
        NONE;
        public static final Codec<SnowStatus> CODEC = StringRepresentable.fromEnum(SnowStatus::values);

        @Override
        public Component getTranslation() {
            return Component.translatable("info.eclipticseasons.environment.snow_status." + getName());
        }
    }


    @Deprecated(forRemoval = true, since = "0.12.0.1")
    public static SnowRenderStatus getSnowStatus(ServerLevel level, Holder<Biome> biome, BlockPos pos) {
        return getSnowStatus(level, biome, pos, isRainingOrSnowAtBiome(level, biome));
    }

    public static SnowRenderStatus getSnowStatus(ServerLevel level, Holder<Biome> biome, BlockPos pos, boolean rain) {
        var status = SnowRenderStatus.NONE;
        if (biome.value().hasPrecipitation()) {
            Biome.Precipitation precipitation = getPrecipitationAt(level, biome.value(), pos);
            if (precipitation == Biome.Precipitation.SNOW) {
                if (rain) status = SnowRenderStatus.SNOW;
            } else {
                status = level.getRandom().nextBoolean() | (rain && precipitation == Biome.Precipitation.RAIN) ?
                        SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            }
        }
        return status;
    }
}
