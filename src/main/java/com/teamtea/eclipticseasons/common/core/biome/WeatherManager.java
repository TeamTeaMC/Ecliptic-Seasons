package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ClimateTypeBiomeTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsRecordCa;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.network.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.compat.vanilla.VanillaWeather;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;

public class WeatherManager {

    public static Map<Level, ArrayList<BiomeWeather>> BIOME_WEATHER_LIST = new IdentityHashMap<>();
    public static Map<Level, Integer> NEXT_CHECK_BIOME_MAP = new IdentityHashMap<>();

    public static ArrayList<BiomeWeather> getBiomeList(Level level) {
        if (level == null) {
            for (ArrayList<BiomeWeather> value : BIOME_WEATHER_LIST.values()) {
                return value;
            }
            return null;
        }
        return BIOME_WEATHER_LIST.getOrDefault(level, null);
    }

    public static Level fetchLevelIfNull(Level level) {
        level = level != null || FMLLoader.getDist() != Dist.CLIENT ? level : ClientCon.getUseLevel();
        return level != null ? level : getMainServerLevel();
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

    public static boolean isRainingEverywhere(ServerLevel level) {
        if (!MapChecker.isValidDimension(level)) return false;
        var ws = getBiomeList(level);
        if (ws != null) {
            var solarTerm = SolarHolders.getSaveData(level).getSolarTerm();
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

    public static boolean isThunderEverywhere(ServerLevel level) {
        if (!MapChecker.isValidDimension(level)) return false;
        var ws = getBiomeList(level);
        if (ws != null) {
            var solarTerm = SolarHolders.getSaveData(level).getSolarTerm();
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()
                        && !(solarTerm.getBiomeRain(biomeWeather.biomeHolder) == FlatRain.RAINLESS)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isThunderAtBiome(Level serverLevel, Biome biome) {
        var ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder.get()) {
                    return biomeWeather.shouldThunder();
                }
            }
        return false;
    }

    public static boolean isThunderAt(Level serverLevel, BlockPos pos) {
        if (!MapChecker.isValidDimension(serverLevel)) {
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
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return isThunderAtBiome(serverLevel, biome.get());
    }

    public static boolean isRainingUnderSky(Level serverLevel, BlockPos pos) {
        if (!MapChecker.isValidDimension(serverLevel)) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return getRainOrSnow(serverLevel, biome.value(), pos) == Biome.Precipitation.RAIN;
    }

    public static boolean isRainingAt(Level serverLevel, BlockPos pos) {
        if (!MapChecker.isValidDimension(serverLevel)) {
            return false;
        }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return getRainOrSnow(serverLevel, biome.value(), pos) == Biome.Precipitation.RAIN;
    }

    public static Boolean isRainingOrSnowAt(Level serverLevel, BlockPos pos) {
        if (!MapChecker.isValidDimension(serverLevel)) {
            return false;
        }

        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        var biome = MapChecker.getSurfaceBiome(serverLevel, pos);
        return isRainingOrSnowAtBiome(serverLevel, biome.value());
    }

    public static boolean isRainingOrSnowAtBiome(Level serverLevel, Biome biome) {
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

    public static Biome.Precipitation getRainOrSnow(Level level, Biome biome, BlockPos pos) {
        if (!MapChecker.isValidDimension(level)) {
            if (!biome.hasPrecipitation()) {
                return Biome.Precipitation.NONE;
            } else {
                return biome.coldEnoughToSnow(pos) ? Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
            }
        }
        if (!biome.hasPrecipitation()) {
            return Biome.Precipitation.NONE;
        }
        var ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder.value()) {
                    if (biomeWeather.shouldClear()) return Biome.Precipitation.NONE;
                    var solarTerm = EclipticUtil.getNowSolarTerm(level);
                    var snowTerm = SolarTerm.getSnowTerm(biome);
                    boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
                    return flag_cold
                            || BiomeClimateManager.getDefaultTemperature(biome, level instanceof ServerLevel) <= BiomeClimateManager.SNOW_LEVEL ?
                            Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
                }
            }
        return Biome.Precipitation.NONE;
    }

    public static Biome.Precipitation getPrecipitationAt(Biome biome, BlockPos pos) {
        return getPrecipitationAt(null, biome, pos);
    }

    public static Biome.Precipitation getPrecipitationAt(Level levelNull, Biome biome, BlockPos p198905) {

        if (BiomeClimateManager.getTag(biome).equals(ClimateTypeBiomeTags.RAINLESS)) {
            return Biome.Precipitation.NONE;
        }

        // TODO:Replay会加载一个本地level
        var level = levelNull != null ? levelNull : getMainServerLevel();
        if (level == null && ClientCon.getUseLevel() != null) {
            level = ClientCon.getUseLevel();
        }
        var provider = SolarHolders.getSaveData(level);
        var weathers = getBiomeList(level);


        if (provider != null && weathers != null) {
            // biome= MapChecker.getSurfaceBiome(level,p198905).value();
            var solarTerm = provider.getSolarTerm();
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            var biomes = level.registryAccess().registry(Registries.BIOME).get();
            var loc = biomes.getKey(biome);
            for (BiomeWeather biomeWeather : weathers) {
                if (biomeWeather.location.equals(loc)) {
                    // if (biomeWeather.shouldClear())
                    //     return Biome.Precipitation.NONE;

                    return flag_cold
                            || BiomeClimateManager.getDefaultTemperature(biome, levelNull instanceof ServerLevel) <= BiomeClimateManager.SNOW_LEVEL ?
                            Biome.Precipitation.SNOW : Biome.Precipitation.RAIN;
                }
            }
        }
        return Biome.Precipitation.NONE;
    }

    public static void createLevelBiomeWeatherList(Level level) {
        var list = new ArrayList<WeatherManager.BiomeWeather>();
        WeatherManager.BIOME_WEATHER_LIST.put(level, list);
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
                        list.add(biomeWeather);
                    }
                }
            }
        }
    }

    public static void informUpdateBiomes(RegistryAccess registryAccess) {
        var biomes = registryAccess.registry(Registries.BIOME);
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        {
            var loc = biomeRegistry.getKey(biome);
            var id = biomeRegistry.getId(biome);
            biomeRegistry.getHolder(ResourceKey.create(Registries.BIOME, biomeRegistry.getKey(biome))).ifPresent(biomeHolder -> {

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
                        biomeRegistry.getId(biome);
                        biomeWeather.location = loc;
                        biomeWeather.id = id;
                        biomeWeathers.add(biomeWeather);
                    }
                });
            });

        }));
    }

    public static void tickPlayerSeasonEffecct(ServerPlayer player) {
        var level = player.level();
        if (CommonConfig.Temperature.heatStroke.get()
                && level.getRandom().nextInt(150) == 0)
            SolarHolders.getSaveDataLazy(level).ifPresent(solarDataManager -> {
                if (EclipticUtil.getNowSolarTerm(level).isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                    var b = level.getBiome(player.blockPosition()).value();
                    if (b.getTemperature(player.blockPosition()) > 0.5f) {

                        if (!player.isInWaterOrRain()
                                && ((EclipticUtil.isNoon(level) && (level.canSeeSky(player.blockPosition()))))
                        ) {
                            boolean isColdHe = false;
                            for (ItemStack itemstack : player.getArmorSlots()) {
                                Item item = itemstack.getItem();
                                if (item instanceof ArmorItem armorItem) {
                                    if (armorItem.getType() == ArmorItem.Type.HELMET) {
                                        if (armorItem.getEnchantmentLevel(itemstack, Enchantments.FIRE_PROTECTION) > 0) {
                                            isColdHe = true;
                                        }
                                    } else if (armorItem.getType() == ArmorItem.Type.BOOTS) {
                                        if (armorItem.getEnchantmentLevel(itemstack, Enchantments.FROST_WALKER) > 0) {
                                            isColdHe = true;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!isColdHe) {
                                for (ItemStack itemstack : player.getInventory().items) {
                                    var item = itemstack.getItem();
                                    if (item == Items.SNOWBALL ||
                                            (item instanceof BlockItem blockItem &&
                                                    (blockItem.getBlock().defaultBlockState().is(BlockTags.SNOW)
                                                            || blockItem.getBlock().defaultBlockState().is(BlockTags.ICE)))) {
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
        if (!biomeWeather.biomeHolder.value().hasPrecipitation())
            return;
        boolean isEcliptic = EclipticUtil.useSolarWeather();

        size = (int) (size * (Mth.clamp(7f / CommonConfig.Season.lastingDaysOfEachTerm.get(), 0.8f, 3f)));

        if (isEcliptic) {
            if (biomeWeather.shouldClear()) {
                biomeWeather.clearTime--;
            } else {
                if (biomeWeather.shouldRain()) {
                    biomeWeather.rainTime--;
                    if (!biomeWeather.shouldThunder()) {
                        BiomeRain biomeRain = SolarHolders.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.biomeHolder);
                        float weight = biomeRain.getThunderChance()
                                * ((CommonConfig.Weather.thunderChanceMultiplier.get() * 1f) / 100f);
                        if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                            biomeWeather.thunderTime = ServerLevel.THUNDER_DURATION.sample(random) / size;
                        }
                    }
                } else {
                    BiomeRain biomeRain = SolarHolders.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.biomeHolder);
                    float downfall = biomeWeather.biomeHolder.value().getModifiedClimateSettings().downfall();
                    if (biomeWeather.biomeHolder.is(BiomeTags.IS_SAVANNA)) {
                        downfall += 0.2f;
                    }
                    float weight = biomeRain.getRainChane()
                            * Math.max(0.01f, downfall)
                            * ((CommonConfig.Weather.rainChanceMultiplier.get() * 1f) / 100f);
                    if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                        biomeWeather.rainTime = ServerLevel.RAIN_DURATION.sample(random) / size;
                    } else {
                        biomeWeather.clearTime = ServerLevel.RAIN_DURATION.sample(random) / size;
                    }
                }
            }

            if (biomeWeather.shouldThunder()) {
                biomeWeather.thunderTime--;
            }

            if ((biomeWeather.shouldRain() || level.getRandom().nextInt(5) > 1)) {
                var snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder.value(), null);
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
                serverPlayer.sendSystemMessage(SimpleUtil.getSolarTermMessage(t.getSolarTerm()), false);
            }
        });
        WeatherManager.sendBiomePacket(WeatherManager.getBiomeList(serverPlayer.level()), List.of(serverPlayer));
    }

    public static void tickPlayerForSeasonCheck(ServerPlayer serverPlayer) {
        var level = serverPlayer.level();
        if (level.getGameTime() % 200 == 0) {
            var holder = serverPlayer.getCapability(SolarTermsRecordCa.SolarTermsRecordCa_CAPABILITY);
            holder.ifPresent(
                    solarTermsRecordCa ->
                    {
                        var st = EclipticSeasonsApi.getInstance().getSolarTerm(level);
                        if (solarTermsRecordCa.addSolarTerm(st)) {
                        } else ModAdvancements.solarTermsCriterion.trigger(serverPlayer);
                    }
            );

        }
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

        if (!MapChecker.isValidDimension(level)) {
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
        var provider =SolarHolders.getSaveData(level);
        var status = SnowRenderStatus.NONE;
        if (provider != null) {
            var solarTerm = provider.getSolarTerm();
            var snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            if (flag_cold) {
                // 为了呼应之前的修改，这里设置为有预测
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
