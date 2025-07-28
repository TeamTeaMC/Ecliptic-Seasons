package com.teamtea.eclipticseasons.common.core.biome;

import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.api.constant.climate.ISnowTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ESEnchantmentTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESMobEffectTags;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.misc.IBiomeTagHolder;
import com.teamtea.eclipticseasons.api.misc.IBiomeWeatherProvider;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecord;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.message.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
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
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
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

    public static Level fetchLevelIfNull(Level level, Biome biome) {

        level = level != null || !BiomeClimateManager.CLIENT_BIOME_TAG_KEY_MAP.containsKey(biome)
                ? level : ClientCon.getUseLevel();
        return level != null ? level : getMainServerLevel();
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


    public static float getMinRainLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()) {
                    return 0.0f;
                }
            }
        return 1.0f;
    }

    public static float getMaximumRainLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biomeWeather.shouldRain()) {
                    return 1.0f;
                }
            }
        return 0.0f;
    }

    // todo other mods should not use it but we not sure
    public static boolean isRainingEverywhere(ServerLevel level) {
        // if (!MapChecker.isValidDimension(level)) return false;
        var ws = getBiomeList(level);
        if (ws != null) {
            // SolarTerm solarTerm = EclipticUtil.getNowSolarTerm(level);
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()
                ) {
                    return false;
                }
            }
        }
        return true;
    }

    public static float getMinThunderLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldThunder()) {
                    return 0.0f;
                }
            }
        return 1.0f;
    }


    public static float getMaximumThunderLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biomeWeather.shouldThunder()) {
                    return 1.0f;
                }
            }
        return 0.0f;
    }

    public static boolean isThunderEverywhere(ServerLevel level) {
        // if (!MapChecker.isValidDimension(level)) return false;
        var ws = getBiomeList(level);
        if (ws != null) {
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldThunder()) {
                    return false;
                }
            }
        }
        return true;
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

    public static boolean isThunderAt(Level serverLevel, BlockPos pos) {
        // if (!MapChecker.isValidDimension(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return isThunderAtBiome(serverLevel, biome);
    }

    public static boolean isRainingUnderSky(Level serverLevel, BlockPos pos) {
        // if (!MapChecker.isValidDimension(serverLevel)) {
        //     return false;
        // }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return getRainOrSnow(serverLevel, biome.value(), pos) == Biome.Precipitation.RAIN;
    }


    public static boolean isRainingAt(Level serverLevel, BlockPos pos) {
        // if (!MapChecker.isValidDimension(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return getRainOrSnow(serverLevel, biome.value(), pos) == Biome.Precipitation.RAIN;
    }

    public static boolean isRainingOrSnowAt(Level serverLevel, BlockPos pos) {
        // if (!MapChecker.isValidDimension(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return isRainingOrSnowAtBiome(serverLevel, biome);
    }

    public static boolean isRainingOrSnowAtBiome(Level level, Holder<Biome> biome) {
        BiomeWeather biomeWeather = getBiomeWeather(level, biome);
        if (biomeWeather != null) {
            return biomeWeather.shouldRain();
        }
        return false;
    }

    @Deprecated
    public static boolean isRainingAtBiome(Level level, Holder<Biome> biome) {
        var ws = getBiomeList(level);
        if (ws != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome.value());
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (!flag_cold) {
                BiomeWeather biomeWeather = getBiomeWeather(level, biome);
                if (biomeWeather != null) {
                    return biomeWeather.shouldRain();
                }
            }
        }
        return false;
    }

    @Deprecated
    public static boolean isSnowingAtBiome(Level level, Holder<Biome> biome) {
        var ws = getBiomeList(level);
        if (ws != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome.value());
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (!flag_cold) {
                BiomeWeather biomeWeather = getBiomeWeather(level, biome);
                if (biomeWeather != null) {
                    return biomeWeather.shouldRain();
                }
            }
        }
        return false;
    }

    // TODO：这里好像没法解决黑名单群系，得想办法那里不代理
    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        // note 这里给不下雨的群系带来了麻烦，一个问题是，假如没有正确添加有效维度，就会错误的下雨
        // note 对于无效维度，应该直接按照原版天气去做，不搞局部雨，是否useSolarWeather应该考虑入Level参数
        // if (!MapChecker.isValidDimension(level)) {
        //     // return Biome.Precipitation.NONE;
        //     if (!biome.hasPrecipitation()) {
        //         return Biome.Precipitation.NONE;
        //     } else {
        //         return biome.coldEnoughToSnow(pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
        //     }
        // }
        if (!biome.hasPrecipitation()) {
            return Biome.Precipitation.NONE;
        }
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
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
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
        return getPrecipitationAt(null, biome, pos);
    }

    // TODO：make a cache here
    public static Biome.Precipitation getPrecipitationAt(@Nullable Level levelNull, Biome biome, BlockPos pos) {

        // TODO：we need to know the biome instance from server side or client registry
        var level = fetchLevelIfNull(levelNull, biome);

        if (level != null) {
            if (MapChecker.isLoadNearByOnlyServer(level, pos)) {
                biome = MapChecker.getSurfaceBiome(level, pos).value();
            }
            // else {
            //     return biome.coldEnoughToSnow(pos) ?
            //             Biome.Precipitation.SNOW :
            //             Biome.Precipitation.RAIN;
            // }
        }

        // check if it has predication
        if (((IBiomeTagHolder) (Object) biome).eclipticseasons$getBindTag().equals(ClimateTypeBiomeTags.RAINLESS)) {
            return Biome.Precipitation.NONE;
        }

        // var provider = SolarHolders.getSaveData(level);
        var weathers = getBiomeList(level);

        // Not add 'has' check because we have checked it
        if (
            // biome.hasPrecipitation() &&
                weathers != null) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome, levelNull instanceof ServerLevel);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
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

        WeatherManager.BIOME_WEATHER_LIST.entrySet().stream().forEach(biomeWeathers1 ->
        {

            var biomeWeathers = biomeWeathers1.getValue();
            var level = biomeWeathers1.getKey();


            level.registryAccess().registry(Registries.BIOME)
                    .ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
                    {
                        var loc = biomeRegistry.getKey(biome);
                        var id = biomeRegistry.getId(biome);
                        biomeRegistry.getHolder(loc).ifPresent(biomeHolder -> {
                            {
                                boolean inList = false;
                                for (BiomeWeather biomeWeather : biomeWeathers) {
                                    // 这里需要根据holder确定一下
                                    if (biomeWeather.location.equals(loc)) {
                                        biomeWeather.id = id;
                                        biomeWeather.biomeHolder = biomeHolder;
                                        inList = true;
                                        break;
                                    }
                                }
                                if (!inList) {
                                    var biomeWeather = new BiomeWeather(biomeHolder);
                                    biomeRegistry.getId(biome);
                                    biomeWeather.location = loc;
                                    biomeWeather.id = id;
                                    biomeWeathers.add(biomeWeather);
                                }
                            }
                        });
                    }));
        });

        WeatherManager.BIOME_WEATHER_LIST.forEach((key, value) -> value.sort(Comparator.comparing(c -> c.id)));
    }

    public static void tickPlayerSeasonEffecct(ServerPlayer player) {
        if (  // player.isCreative() ||
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
        if (!biomeWeather.biomeHolder.value().hasPrecipitation())
            return;
        boolean isEcliptic = EclipticUtil.hasLocalWeather(level);


        size = (int) (size * (Mth.clamp(7f / CommonConfig.Season.lastingDaysOfEachTerm.get(), 0.8f, 3f)));
        size = Math.max(1, size);
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

            if ((biomeWeather.shouldRain() || level.getRandom().nextInt(5) > 1)) {
                var snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder, null);
                if (snow == SnowRenderStatus.SNOW) {
                    biomeWeather.snowDepth = (byte) Math.min(100, biomeWeather.snowDepth + 1);
                } else if (snow == SnowRenderStatus.SNOW_MELT) {
                    biomeWeather.snowDepth = (byte) Math.max(0, biomeWeather.snowDepth - 1);
                }
            }
        } else {
            VanillaWeather.runVanillaSnowyWeather(level, biomeWeather, random, size);
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

            var snowTerm = SolarTerm.getSnowTerm(biomeWeather.biomeHolder.value(), !level.isClientSide());
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            boolean flag_little_cold = lastSolarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
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
                for (BiomeWeather biomeWeather : ws) {
                    for (int i = 0; i < (newTime - oldDayTime) / size; i++) {
                        WeatherManager.runWeather(level, biomeWeather, random, size);
                    }
                }

                if (!level.players().isEmpty()) {
                    WeatherManager.sendBiomePacket(ws, level.players());
                }
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

    public static void tickPlayerForSeasonCheck(ServerPlayer serverPlayer) {
        var level = serverPlayer.level();
        // if (level.getGameTime() % 12000 == 0)
        {
            var holder = serverPlayer.getData(AttachmentRegistry.SOLAR_TERMS_RECORD.get());
            if (holder.solarTerm().size() < SolarTermsRecord.size) {
                var st = EclipticSeasonsApi.getInstance().getSolarTerm(level);
                if (!holder.solarTerm().contains(st))
                    holder.solarTerm().add(st);
                serverPlayer.setData(AttachmentRegistry.SOLAR_TERMS_RECORD.get(), holder);
            } else ModAdvancements.solarTermsCriterion.get().trigger(serverPlayer);
        }
    }

    public static int getSkyDarken(Level level, BlockPos pos, int amount) {
        WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, MapChecker.getSurfaceBiome(level, pos));
        amount += biomeWeather == null || biomeWeather.shouldClear() ? 0 :
                biomeWeather.shouldThunder() ? 8 : 4;
        // todo 后续添加缓存值避免反复计算
        return Mth.clamp(amount, 0, 15);
    }

    public static class BiomeWeather {
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


        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("biome", location.toString());
            tag.putInt("rainTime", rainTime);
            tag.putInt("thunderTime", thunderTime);
            tag.putInt("clearTime", clearTime);
            tag.putByte("snowDepth", snowDepth);
            return tag;
        }


        public void deserializeNBT(CompoundTag nbt) {
            location = ResourceLocation.parse(nbt.getString("biome"));
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
        MELT,
        NONE;
        public static final Codec<SnowStatus> CODEC = StringRepresentable.fromEnum(SnowStatus::values);

        @Override
        public Component getTranslation() {
            return Component.translatable("info.eclipticseasons.environment.snow_status." + getName());
        }
    }

    public static SnowRenderStatus getSnowStatus(ServerLevel level, Holder<Biome> biome, BlockPos pos) {
        // var provider = SolarHolders.getSaveData(level);
        var status = SnowRenderStatus.NONE;
        if (biome.value().hasPrecipitation()) {
            var solarTerm = EclipticUtil.getNowSolarTerm(level);
            var snowTerm = SolarTerm.getSnowTerm(biome.value(), !level.isClientSide());
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (flag_cold) {
                if (isRainingOrSnowAtBiome(level, biome)) {
                    status = SnowRenderStatus.SNOW;
                }
            } else {
                status = level.getRandom().nextBoolean() | isRainingOrSnowAtBiome(level, biome) ?
                        SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            }

        }
        return status;
    }
}
