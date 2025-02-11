package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.AllListener;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.common.network.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;

public class WeatherManager {

    public static Map<Level, ArrayList<BiomeWeather>> BIOME_WEATHER_LIST = new LinkedHashMap<>();
    public static Map<Level, Integer> NEXT_CHECK_BIOME_MAP = new HashMap<>();

    public static ArrayList<BiomeWeather> getBiomeList(Level level) {
        if (level == null) {
            for (ArrayList<BiomeWeather> value : BIOME_WEATHER_LIST.values()) {
                return value;
            }
            return null;
        }
        return BIOME_WEATHER_LIST.getOrDefault(level, null);
    }

    public static Float getMinRainLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()) {
                    return 0.0f;
                }
            }
        return 1.0f;
    }

    public static Float getMaximumRainLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biomeWeather.shouldRain()) {
                    return 1.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isRainingEverywhere(ServerLevel level) {
        if (!level.dimensionType().natural()) return false;
        var ws = getBiomeList(level);
        if (ws != null) {
            var solarTerm = AllListener.getSaveData(level).getSolarTerm();
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()
                        && !(solarTerm.getBiomeRain(biomeWeather.biomeHolder) == FlatRain.RAINLESS)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Float getMinThunderLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldThunder()) {
                    return 0.0f;
                }
            }
        return 1.0f;
    }


    public static Float getMaximumThunderLevel(Level level, float p46723) {
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biomeWeather.shouldThunder()) {
                    return 1.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isThunderEverywhere(ServerLevel level) {
        if (!level.dimensionType().natural()) return false;
        var ws = getBiomeList(level);
        if (ws != null) {
            var solarTerm = AllListener.getSaveData(level).getSolarTerm();
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()
                        && !(solarTerm.getBiomeRain(biomeWeather.biomeHolder) == FlatRain.RAINLESS)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean isThunderAtBiome(ServerLevel serverLevel, Biome biome) {
        var ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder.get()) {
                    return biomeWeather.shouldThunder();
                }
            }
        return false;
    }

    public static Boolean isThunderAt(ServerLevel serverLevel, BlockPos pos) {
        if (!serverLevel.dimensionType().natural()) {
            return false;
        }
        // if (!isThunderAnywhere(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = serverLevel.getBiome(pos);
        return isThunderAtBiome(serverLevel, biome.get());
    }

    public static Boolean isRainingAt(ServerLevel serverLevel, BlockPos pos) {
        if (!serverLevel.dimensionType().natural()) {
            return false;
        }
        // if (!isRainingAnywhere(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = serverLevel.getBiome(pos);
        return isRainingAtBiome(serverLevel, biome.get());
    }

    public static Boolean isRainingAtBiome(ServerLevel serverLevel, Biome biome) {
        var ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder.get()) {
                    return biomeWeather.shouldRain();
                }
            }
        return false;
    }


    public static int getSnowDepthAtBiome(Level serverLevel, Biome biome) {
        var ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder.get()) {
                    return biomeWeather.snowDepth;
                }
            }
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
    public static Level getMainClientLevel() {
        for (Level level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.isClientSide) {
                return level;
            }
        }
        return null;
    }

    public static Level fetchLevelIfNull(Level level) {
        level = level != null || FMLLoader.getDist() != Dist.CLIENT ? level : getMainClientLevel();
        return level != null ? level : getMainServerLevel();
    }

    public static Biome.Precipitation getPrecipitationAt(Biome biome, BlockPos pos) {
        return getPrecipitationAt(null, biome, pos);
    }

    public static Biome.Precipitation getPrecipitationAt(Level levelNull, Biome biome, BlockPos p198905) {

        var level = fetchLevelIfNull(levelNull);
        var provider = SolarUtil.getProvider(level);
        var weathers = getBiomeList(level);
        if (provider != null && weathers != null) {
            var solarTerm = provider.getSolarTerm();
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            var biomes = level.registryAccess().registry(BuiltinRegistries.BIOME.key()).get();
            var loc=biomes.getKey(biome);
            for (BiomeWeather biomeWeather : weathers) {
                if (biomeWeather.location.equals(loc)) {
                    if (biomeWeather.shouldClear())
                        return Biome.Precipitation.NONE;

                    return flag_cold
                            || BiomeClimateManager.getDefaultTemperature(biome) <= BiomeClimateManager.SNOW_LEVEL ?
                            Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
                }
            }
        }

        return Biome.Precipitation.NONE;
    }

    public static void createLevelBiomeWeatherList(Level level) {
        var list = new ArrayList<BiomeWeather>();
        WeatherManager.BIOME_WEATHER_LIST.put(level, list);
        {
            var biomes = level.registryAccess().registry(BuiltinRegistries.BIOME.key());
            if (biomes.isPresent()) {
                for (Biome biome : biomes.get()) {
                    var loc = biomes.get().getKey(biome);
                    var id = biomes.get().getId(biome);
                    var biomeHolder = biomes.get().getHolder(ResourceKey.create(BuiltinRegistries.BIOME.key(), biomes.get().getKey(biome)));
                    if (biomeHolder.isPresent()) {
                        var biomeWeather = new BiomeWeather(biomeHolder.get());
                        biomes.get().getId(biome);
                        biomeWeather.location = loc;
                        biomeWeather.id = id;
                        list.add(biomeWeather);
                    }
                }
            }
        }
    }

    public static void informUpdateBiomes(RegistryAccess registryAccess) {
        var biomes = registryAccess.registry(BuiltinRegistries.BIOME.key());
        if (biomes.isPresent()) {
            biomes.get().forEach(biome ->
            {
                var loc = biomes.get().getKey(biome);
                var id = biomes.get().getId(biome);
                biomes.get().getHolder(ResourceKey.create(BuiltinRegistries.BIOME.key(), biomes.get().getKey(biome))).ifPresent(biomeHolder -> {

                    WeatherManager.BIOME_WEATHER_LIST.entrySet().stream().forEach(levelArrayListEntry ->
                    {
                        var biomeWeathers = levelArrayListEntry.getValue();

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
                            biomes.get().getId(biome);
                            biomeWeather.location = loc;
                            biomeWeather.id = id;
                            biomeWeathers.add(biomeWeather);
                        }
                    });
                });

            });
        }
    }

    public static void tickPlayerSeasonEffecct(ServerPlayer player) {
        var level = player.level;
        if (CommonConfig.Temperature.heatStroke.get()
                &&level.getRandom().nextInt(150) == 0)
            AllListener.getSaveDataLazy(level).ifPresent(solarDataManager -> {
                if (SimpleUtil.getNowSolarTerm(level).isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                    var b = level.getBiome(player.blockPosition()).value();
                    if (b.getTemperature(player.blockPosition()) > 0.5f) {

                        if (!player.isInWaterOrRain()
                                && ((SimpleUtil.isNoon(level) && (level.canSeeSky(player.blockPosition()))))
                        ) {
                            boolean isColdHe = false;
                            for (ItemStack itemstack : player.getArmorSlots()) {
                                Item item = itemstack.getItem();
                                if (item instanceof ArmorItem armorItem) {
                                    if (armorItem.getSlot() == EquipmentSlot.HEAD) {
                                        if (armorItem.getEnchantmentLevel(itemstack, Enchantments.FIRE_PROTECTION) > 0) {
                                            isColdHe = true;
                                        }
                                    }
                                }
                            }
                            if (!player.hasEffect(EclipticSeasons.EffectRegistry.HEAT_STROKE) && !isColdHe) {
                                player.addEffect(new MobEffectInstance(EclipticSeasons.EffectRegistry.HEAT_STROKE, 600));
                            }
                        }
                    }
                }
            });
    }

    public static final IntProvider RAIN_DELAY = UniformInt.of(12000, 180000);
    public static final IntProvider RAIN_DURATION = UniformInt.of(12000, 24000);
    private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);
    public static final IntProvider THUNDER_DURATION = UniformInt.of(3600, 15600);


    public static void runWeather(ServerLevel level, BiomeWeather biomeWeather, RandomSource random, int size) {

        if (biomeWeather.shouldClear()) {
            biomeWeather.clearTime--;
        } else {
            if (biomeWeather.shouldRain()) {
                biomeWeather.rainTime--;
                if (!biomeWeather.shouldThunder()) {
                    BiomeRain biomeRain = AllListener.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.biomeHolder);
                    float weight = biomeRain.getThunderChance();
                    if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                        biomeWeather.thunderTime = THUNDER_DURATION.sample(random) / size;
                    }
                }
            } else {
                BiomeRain biomeRain = AllListener.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.biomeHolder);
                float downfall = biomeWeather.biomeHolder.get().getModifiedClimateSettings().downfall();
                if (biomeWeather.biomeHolder.is(BiomeTags.IS_SAVANNA)) {
                    downfall += 0.2f;
                }
                float weight = biomeRain.getRainChane()
                        * Math.max(0.01f, downfall)
                        * ((CommonConfig.Season.rainChanceMultiplier.get() * 1f) / 100f);
                if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                    biomeWeather.rainTime = RAIN_DURATION.sample(random) / size;
                } else {
                    // biomeWeather.clearTime = 10 / (size / 30);
                    biomeWeather.clearTime = RAIN_DURATION.sample(random) / size;
                }
            }
        }

        if (biomeWeather.shouldThunder()) {
            biomeWeather.thunderTime--;
        }
        // if (biomeWeather.biomeHolder.is(Biomes.JUNGLE)) {
        //     // BiomeRain biomeRain = AllListener.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.biomeHolder);
        //     // float weight = biomeRain.getRainChane() * Math.max(0.01f, biomeWeather.biomeHolder.get().getModifiedClimateSettings().downfall());
        //     //
        //     // Ecliptic.logger(biomeWeather,weight) ;
        // }


        if (biomeWeather.shouldRain() || level.getRandom().nextInt(5) > 1) {
            var snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder.get(), null);
            if (snow == SnowRenderStatus.SNOW) {
                biomeWeather.snowDepth = (byte) Math.min(100, biomeWeather.snowDepth + 1);
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                biomeWeather.snowDepth = (byte) Math.max(0, biomeWeather.snowDepth - 1);
            }
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
        SimpleNetworkHandler.send(new ArrayList<>(level.players()), new EmptyMessage());
    }

    public static void onLoggedIn(ServerPlayer serverPlayer, boolean isLogged) {
        if ((serverPlayer instanceof FakePlayer)) return;
        AllListener.getSaveDataLazy(serverPlayer.level).ifPresent(t ->
        {
            SimpleNetworkHandler.send(serverPlayer, new SolarTermsMessage(t.getSolarTermsDay()));
            if (isLogged&&CommonConfig.Season.enableInform.get()
                    && t.getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                serverPlayer.sendSystemMessage(Component.translatable("info.teastory.environment.solar_term.message", SolarTerm.get(t.getSolarTermIndex()).getAlternationText()), false);
            }
        });
        WeatherManager.sendBiomePacket(WeatherManager.getBiomeList(serverPlayer.level), List.of(serverPlayer));
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

    public static Boolean onCheckWarmEnoughToRain(BlockPos p198905) {
        // return SolarTerm.get(AllListener.provider.resolve().get().worldSolarTime.getSolarTermIndex()).getSeason() != Season.WINTER;
        return true;
    }

    public static boolean onShouldSnow(ServerLevel level, Biome biome, BlockPos pos) {
        // return SolarTerm.get(AllListener.provider.resolve().get().worldSolarTime.getSolarTermIndex()).getSeason() == Season.WINTER;
        return true;
    }

    public static boolean agentAdvanceWeatherCycle(ServerLevel level, ServerLevelData serverLevelData, WritableLevelData levelData, RandomSource random) {

        if (!level.dimensionType().natural()) {
            return true;
        }
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
        var provider = SolarUtil.getProvider(level);
        var status = SnowRenderStatus.NONE;
        if (provider != null) {
            var solarTerm = provider.getSolarTerm();
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (flag_cold) {
                if (isRainingAtBiome(level, biome)) {
                    status = SnowRenderStatus.SNOW;
                }
            } else {
                status = level.getRandom().nextBoolean() | isRainingAtBiome(level, biome) ?
                        SnowRenderStatus.SNOW_MELT : SnowRenderStatus.NONE;
            }

        }
        return status;
    }
}
