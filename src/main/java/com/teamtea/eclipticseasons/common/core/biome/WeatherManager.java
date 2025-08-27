package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.api.constant.climate.WeatherMode;
import com.teamtea.eclipticseasons.api.constant.tag.ESEnchantmentTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESMobEffectTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.common.network.message.UpdateTempChangeMessage;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecordCa;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.message.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;

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

    public static BiomeWeather getBiomeWeather(Level level, Holder<Biome> biomeHolder) {
        if (biomeHolder == null) return null;
        return getBiomeWeather(level, biomeHolder.value());
    }

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

    private static final ThreadLocal<Boolean> IS_ON_SERVER_THREAD =
            ThreadLocal.withInitial(() ->
                    "SERVER".equals(Thread.currentThread().getThreadGroup().getName())
            );

    public static Level fetchLevelIfNull(Level level) {
        if (level != null) return level;
        boolean isClient = ServerLifecycleHooks.getCurrentServer() == null ||
                (!ServerLifecycleHooks.getCurrentServer().isSameThread()
                        && !IS_ON_SERVER_THREAD.get());
        return isClient ? ClientCon.getUseLevel() : getMainServerLevel();
    }

    public static float getMinRainLevel(Level level, float p46723) {
        return 0.0f;
    }

    public static float getMaximumRainLevel(Level level, float p46723) {
        return 1.0f;
    }

    public static boolean isRainingEverywhere(ServerLevel level) {
        return false;
    }

    public static float getMinThunderLevel(Level level, float p46723) {
        return 0.0f;
    }


    public static float getMaximumThunderLevel(Level level, float p46723) {
        return 1.0f;
    }

    public static boolean isThunderEverywhere(ServerLevel level) {
        return false;
    }

    public static boolean isThunderAtBiome(Level level, BlockPos pos) {
        Holder<Biome> surfaceBiome = MapChecker.getSurfaceBiome(level, pos);
        return isThunderAtBiome(level, surfaceBiome.get());
    }

    public static boolean isThunderAtBiome(Level level, Biome biome) {
        var biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null)
            return biomeWeather.shouldThunder();
        return false;
    }

    public static boolean isThunderAt(Level level, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     return false;
        // }
        // if (!isThunderAnywhere(level)) {
        //     return false;
        // }
        if (!level.canSeeSky(pos)) {
            return false;
        } else if (level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(level, pos);
        return isThunderAtBiome(level, biome.get());
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
        return isRainingOrSnowAtBiome(level, biome.value());
    }

    public static boolean isRainingOrSnowAtBiome(Level level, Biome biome) {
        var biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null)
            return biomeWeather.shouldRain();
        return false;
    }

    @Deprecated
    public static boolean isRainingAtBiome(Level level, Biome biome) {
        var biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            if (!flag_cold) {
                return biomeWeather.shouldRain();
            }
        }
        return false;
    }

    @Deprecated
    public static boolean isSnowingAtBiome(Level level, Biome biome) {
        var biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            if (flag_cold) {
                return biomeWeather.shouldRain();
            }
        }
        return false;
    }


    public static int getSnowDepthAtBiome(Level level, Biome biome) {
        var biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null)
            return biomeWeather.snowDepth;
        return 0;
    }

    public static ServerLevel getMainServerLevel() {
        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == Level.OVERWORLD && level instanceof ServerLevel serverLevel) {
                return serverLevel;
            }
        }
        return null;
    }

    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        // if (!MapChecker.isValidDimension(level)) {
        //     if (!biome.hasPrecipitation()) {
        //         return Biome.Precipitation.NONE;
        //     } else {
        //         return biome.coldEnoughToSnow(pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        //     }
        // }
        if (!biome.hasPrecipitation()) {
            return Biome.Precipitation.NONE;
        }
        var biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            if (biomeWeather.shouldClear()) return Biome.Precipitation.NONE;
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            return flag_cold
                    // || BiomeClimateManager.getDefaultTemperature(biome, level instanceof ServerLevel) <= BiomeClimateManager.SNOW_LEVEL
                    ?
                    Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        }
        return Biome.Precipitation.NONE;
    }

    public static Biome.Precipitation getPrecipitationAt(Biome biome, BlockPos pos) {
        var level = fetchLevelIfNull(null);
        if (level != null && CompatModule.CommonConfig.fixBiome.get() && MapChecker.isSmallBiome(biome)) {
            // if (MapChecker.isLoadNearByOnlyServer(level, pos))
            {
                biome = MapChecker.getSurfaceBiome(level, pos).value();
            }
        }
        return getPrecipitationAt(level, biome, pos);
    }

    public static Biome.Precipitation getPrecipitationAt(@Nullable Level level, Biome biome, BlockPos pos) {

        if (!biome.hasPrecipitation()) {
            return Biome.Precipitation.NONE;
        }

        var biomeWeather = getBiomeWeather(level, biome);

        if (level != null && biomeWeather != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            return flag_cold ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        }
        return Biome.Precipitation.NONE;
    }

    public static void createLevelBiomeWeatherList(Level level) {
        var biomesWeathers = new ArrayList<WeatherManager.BiomeWeather>();
        WeatherManager.BIOME_WEATHER_LIST.put(level, biomesWeathers);
        if (level instanceof IBiomeWeatherProvider iBiomeWeatherProvider) {
            iBiomeWeatherProvider.es$set(biomesWeathers);
        }
        {
            var biomes = level.registryAccess().registry(Registries.BIOME);
            if (biomes.isPresent()) {
                for (Biome biome : biomes.get()) {
                    var loc = biomes.get().getKey(biome);
                    var id = biomes.get().getId(biome);
                    var biomeHolder = biomes.get().getHolder(ResourceKey.create(Registries.BIOME, biomes.get().getKey(biome)));
                    if (biomeHolder.isPresent()) {
                        var biomeWeather = new WeatherManager.BiomeWeather(biomeHolder.get());
                        biomes.get().getId(biome);
                        biomeWeather.location = loc;
                        biomeWeather.id = id;
                        biomesWeathers.add(biomeWeather);
                        ((IBiomeTagHolder) (Object) biome).eclipticseasons$setBindId(id);
                    }
                }

                // add copy
                Map<Biome, BiomeWeather> biomeBiomeWeatherMap = new IdentityHashMap<>();
                for (BiomeWeather biomesWeather : biomesWeathers) {
                    biomeBiomeWeatherMap.put(biomesWeather.biomeHolder.value(), biomesWeather);
                }
                WeatherManager.BIOME_WEATHER_QUERY_LIST.put(level, biomeBiomeWeatherMap);
            }
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
        var level = player.level();
        if (level.getRandom().nextInt(150) == 0)
            SolarHolders.getSaveDataLazy(level).ifPresent(solarDataManager -> {
                if (EclipticUtil.getNowSolarTerm(level).isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                    Biome b = level.getBiome(player.blockPosition()).value();
                    if (EclipticUtil.getTemperatureFloat(level, b, player.blockPosition()) > 0.85f) {
                        if (!player.isInWaterOrRain()
                                && ((EclipticUtil.isNoon(level) && (level.canSeeSky(player.blockPosition()))))
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
                                        Map<Enchantment, Integer> allEnchantments = itemstack.getAllEnchantments();
                                        if (!allEnchantments.isEmpty()) {
                                            for (Enchantment enchantment : allEnchantments.keySet()) {
                                                Optional<Holder<Enchantment>> holder = ForgeRegistries.ENCHANTMENTS.getHolder(enchantment);
                                                if (holder.isPresent() && holder.get().is(ESEnchantmentTags.HEATSTROKE_RESISTANT)) {
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
                                    Optional<Holder<MobEffect>> holder = ForgeRegistries.MOB_EFFECTS.getHolder(activeEffect.getEffect());
                                    if (holder.isPresent() && holder.get().is(ESMobEffectTags.HEATSTROKE_RESISTANT)) {
                                        isColdHe = true;
                                        break;
                                    }
                                }
                            }

                            if (!player.hasEffect(EffectRegistry.HEAT_STROKE) && !isColdHe) {
                                player.addEffect(new MobEffectInstance(EffectRegistry.HEAT_STROKE, 600));
                                ModAdvancements.heatStrokeCriterion.trigger(player);
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
            if (onwer != null) {
                BiomeWeather ownerBiomeWeather = getBiomeWeather(level, onwer);
                if (ownerBiomeWeather != null) {
                    biomeWeather.rainTime = ownerBiomeWeather.rainTime;
                    biomeWeather.thunderTime = ownerBiomeWeather.thunderTime;
                    biomeWeather.clearTime = ownerBiomeWeather.clearTime;
                    updateSnowOrMelt(level, biomeWeather, random);
                    return;
                }
            }
        }
        if (!biomeWeather.biomeHolder.value().hasPrecipitation())
            return;
        boolean isEcliptic = EclipticUtil.hasLocalWeather(level);

        size = (int) (size * (Mth.clamp(7f / CommonConfig.Season.lastingDaysOfEachTerm.get(), 0.8f, 3f)));

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
                    if (biomeWeather.biomeHolder.is(BiomeTags.IS_SAVANNA)) {
                        downfall += 0.2f;
                    }
                    float weight = biomeRain.getRainChance()
                            * Math.max(0.01f, downfall)
                            * ((CommonConfig.Weather.rainChanceMultiplier.get() * 1f) / 100f);
                    if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                        biomeWeather.rainTime = biomeRain.getRainDuration(random) / size;
                    } else {
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

            updateSnowOrMelt(level, biomeWeather, random);
        } else {
            VanillaWeather.runVanillaSnowyWeather(level, biomeWeather, random, size);
        }
    }


    protected static void updateSnowOrMelt(ServerLevel level, BiomeWeather biomeWeather, RandomSource randomSource) {
        if ((biomeWeather.shouldRain() || randomSource.nextInt(5) > 1)) {
            var snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder.value(), null);
            if (snow == SnowRenderStatus.SNOW) {
                biomeWeather.snowDepth = (byte) Math.min(100, biomeWeather.snowDepth + 1);
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                biomeWeather.snowDepth = (byte) Math.max(0, biomeWeather.snowDepth - 1);
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

        int size = (int) (biomeList.size() * (Mth.clamp(7f / CommonConfig.Season.lastingDaysOfEachTerm.get(), 0.8f, 3f)));
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
                if (biomeWeather.biomeHolder.is(BiomeTags.IS_SAVANNA)) {
                    downfall += 0.2f;
                }
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
                int size = ws.size();
                for (WeatherManager.BiomeWeather biomeWeather : ws) {
                    for (int i = 0; i < (newTime - oldDayTime) / size; i++) {
                        WeatherManager.runWeather(level, biomeWeather, random, size);
                    }
                }

                if (!level.players().isEmpty()) {
                    WeatherManager.sendBiomePacket(ws, level.players());
                }
            }
        }
        SimpleNetworkHandler.send(new ArrayList<>(level.players()), new EmptyMessage());
    }

    public static void onLoggedIn(ServerPlayer serverPlayer, boolean isLogged) {
        if ((serverPlayer instanceof FakePlayer)) return;
        SolarHolders.getSaveDataLazy(serverPlayer.level()).ifPresent(t ->
        {
            SimpleNetworkHandler.send(serverPlayer, new SolarTermsMessage(t.getSolarTermsDay()));
            if (isLogged
                    && CommonConfig.Season.enableInform.get()
                    && MapChecker.isValidDimension(serverPlayer.level())
                    && t.getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                SolarTerm solarTerm = t.getSolarTerm();
                if (solarTerm != SolarTerm.NONE)
                    SimpleUtil.sendSolarTermMessage(serverPlayer, solarTerm, isLogged);
            }
            SimpleNetworkHandler.send(serverPlayer, new UpdateTempChangeMessage(t.getSolarTempChange()));
        });
        WeatherManager.sendBiomePacket(WeatherManager.getBiomeList(serverPlayer.level()), List.of(serverPlayer));
    }

    public static void tickPlayerForSeasonCheck(ServerPlayer serverPlayer) {
        var level = serverPlayer.level();
        if (level.getGameTime() % 200 == 0) {
            var holder = serverPlayer.getCapability(SolarTermsRecordCa.SOLAR_TERMS_RECORD_CA_CAPABILITY);
            holder.ifPresent(
                    solarTermsRecordCa ->
                    {
                        var st = EclipticSeasonsApi.getInstance().getSolarTerm(level);
                        if (solarTermsRecordCa.addAndCheck(st)) {
                        } else ModAdvancements.solarTermsCriterion.trigger(serverPlayer);
                    }
            );

        }
    }

    public static int getSkyDarken(Level level, BlockPos pos, int amount) {
        WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, MapChecker.getSurfaceBiome(level, pos));
        amount += biomeWeather == null || biomeWeather.shouldClear() ? 0 :
                biomeWeather.shouldThunder() ? 8 : 4;
        return Mth.clamp(amount, 0, 15);
    }


    public static class BiomeWeather implements INBTSerializable<CompoundTag> {
        public Holder<Biome> biomeHolder;
        public int id;
        public SnowTerm snowTerm;

        public ResourceLocation location;
        public int rainTime = 0;
        public int thunderTime = 0;
        public int clearTime = 0;
        public byte snowDepth = 0;


        public BiomeWeather(Holder<Biome> biomeHolder) {
            this.biomeHolder = biomeHolder;
        }

        // 雨天也可能是晴天
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

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("biome", location.toString());
            tag.putInt("rainTime", rainTime);
            tag.putInt("thunderTime", thunderTime);
            tag.putInt("clearTime", clearTime);
            tag.putByte("snowDepth", snowDepth);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            location = new ResourceLocation(nbt.getString("biome"));
            rainTime = nbt.getInt("rainTime");
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

    public static boolean agentAdvanceWeatherCycle(ServerLevel level, ServerLevelData serverLevelData, WritableLevelData levelData, RandomSource random) {

        // if (!MapChecker.isValidDimension(level)) {
        //     return true;
        // }
        int pos = NEXT_CHECK_BIOME_MAP.getOrDefault(level, -1);

        var levelBiomeWeather = getBiomeList(level);

        if (pos >= 0 && levelBiomeWeather != null && pos < levelBiomeWeather.size()) {
            int size = levelBiomeWeather.size();
            var biomeWeather = getBiomeList(level).get(pos);

            runWeather(level, biomeWeather, random, size);

            pos++;
        } else {
            pos = 0;
        }
        // Ecliptic.logger(level.getGameTime(),level.getGameTime() & 100);
        if (levelBiomeWeather != null && (level.getGameTime() % 100) == 0 && !level.players().isEmpty()) {
            // Ecliptic.logger(level.getGameTime());
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

    public static SnowRenderStatus getSnowStatus(ServerLevel level, Biome biome, BlockPos pos) {
        // var provider = SolarHolders.getSaveData(level);
        var status = SnowRenderStatus.NONE;
        // if (provider != null)
        {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, level instanceof ServerLevel, EclipticUtil.getSnowTempChange(level));
            boolean flag_cold = snowTerm.maySnow(solarTerm);
            if (flag_cold) {
                if (biome.hasPrecipitation() && isRainingOrSnowAtBiome(level, biome)) {
                    status = SnowRenderStatus.SNOW;
                }
            } else {
                status = level.getRandom().nextBoolean() | isRainingOrSnowAtBiome(level, biome) ?
                        SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            }

        }
        return status;
    }

    public record WeatherCheck(Optional<Boolean> isRaining, Optional<Boolean> isThundering) {
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
}
