package com.teamtea.eclipticseasons.common.core.biome;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.api.constant.climate.BiomeRain;
import com.teamtea.eclipticseasons.api.constant.climate.FlatRain;
import com.teamtea.eclipticseasons.api.constant.climate.SnowTerm;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.handler.SolarUtil;
import com.teamtea.eclipticseasons.common.network.BiomeWeatherMessage;
import com.teamtea.eclipticseasons.common.network.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.SolarTermsMessage;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WeatherManager {

    public static Map<World, ArrayList<BiomeWeather>> BIOME_WEATHER_LIST = new LinkedHashMap<>();
    public static Map<World, Integer> NEXT_CHECK_BIOME_MAP = new HashMap<>();

    public static ArrayList<BiomeWeather> getBiomeList(World world) {
        if (world == null) {
            for (ArrayList<BiomeWeather> value : BIOME_WEATHER_LIST.values()) {
                return value;
            }
            return null;
        }
        return BIOME_WEATHER_LIST.getOrDefault(world, null);
    }

    public static Float getMinRainLevel(World level, float p46723) {
        ArrayList<BiomeWeather> ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()) {
                    return 0.0f;
                }
            }
        return 1.0f;
    }

    public static Float getMaximumRainLevel(World level, float p46723) {
        ArrayList<BiomeWeather> ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biomeWeather.shouldRain()) {
                    return 1.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isRainingEverywhere(ServerWorld level) {
        if (!level.dimensionType().natural()) return false;
        ArrayList<BiomeWeather> ws = getBiomeList(level);
        if (ws != null) {
            SolarTerm solarTerm = SolarHolders.getSaveData(level).getSolarTerm();
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()
                        && !(solarTerm.getBiomeRain(biomeWeather.getBiomeKey()) == FlatRain.RAINLESS)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Float getMinThunderLevel(World level, float p46723) {
        ArrayList<BiomeWeather> ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldThunder()) {
                    return 0.0f;
                }
            }
        return 1.0f;
    }


    public static Float getMaximumThunderLevel(World level, float p46723) {
        ArrayList<BiomeWeather> ws = getBiomeList(level);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biomeWeather.shouldThunder()) {
                    return 1.0f;
                }
            }
        return 0.0f;
    }

    public static Boolean isThunderEverywhere(ServerWorld level) {
        if (!level.dimensionType().natural()) return false;
        ArrayList<BiomeWeather> ws = getBiomeList(level);
        if (ws != null) {
            SolarTerm solarTerm = SolarHolders.getSaveData(level).getSolarTerm();
            for (BiomeWeather biomeWeather : ws) {
                if (!biomeWeather.shouldRain()
                        && !(solarTerm.getBiomeRain(biomeWeather.getBiomeKey()) == FlatRain.RAINLESS)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static Boolean isThunderAtBiome(ServerWorld serverLevel, Biome biome) {
        ArrayList<BiomeWeather> ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder) {
                    return biomeWeather.shouldThunder();
                }
            }
        return false;
    }

    public static Boolean isThunderAt(ServerWorld serverLevel, BlockPos pos) {
        if (!serverLevel.dimensionType().natural()) {
            return false;
        }
        // if (!isThunderAnywhere(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        Biome biome = serverLevel.getBiome(pos);
        return isThunderAtBiome(serverLevel, biome);
    }

    public static Boolean isRainingAt(ServerWorld serverLevel, BlockPos pos) {
        if (!serverLevel.dimensionType().natural()) {
            return false;
        }
        // if (!isRainingAnywhere(serverLevel)) {
        //     return false;
        // }
        if (!serverLevel.canSeeSky(pos)) {
            return false;
        } else if (serverLevel.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, pos).getY() > pos.getY()) {
            return false;
        }
        Biome biome = serverLevel.getBiome(pos);
        return isRainingAtBiome(serverLevel, biome);
    }

    public static Boolean isRainingAtBiome(ServerWorld serverLevel, Biome biome) {
        ArrayList<BiomeWeather> ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder) {
                    return biomeWeather.shouldRain();
                }
            }
        return false;
    }


    public static int getSnowDepthAtBiome(World serverLevel, Biome biome) {
        ArrayList<BiomeWeather> ws = getBiomeList(serverLevel);
        if (ws != null)
            for (BiomeWeather biomeWeather : ws) {
                if (biome == biomeWeather.biomeHolder) {
                    return biomeWeather.snowDepth;
                }
            }
        return 0;
    }

    public static ServerWorld getMainServerWorld() {
        for (World level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.dimension() == World.OVERWORLD && level instanceof ServerWorld) {
                return (ServerWorld) level;
            }
        }
        return null;
    }

    public static World getMainClientWorld() {
        for (World level : WeatherManager.BIOME_WEATHER_LIST.keySet()) {
            if (level.isClientSide) {
                return level;
            }
        }
        return null;
    }

    public static World fetchWorldIfNull(World world) {
        world = world != null || FMLLoader.getDist() != Dist.CLIENT ? world : getMainClientWorld();
        return world != null ? world : getMainServerWorld();
    }

    public static Biome.RainType getPrecipitationAt(Biome biome, BlockPos pos) {
        return getPrecipitationAt(null, biome, pos);
    }

    public static Biome.RainType getPrecipitationAt(World levelNull, Biome biome, BlockPos p198905) {
        World level = fetchWorldIfNull(levelNull);
        SolarDataManager provider = SolarUtil.getProvider(level);
        ArrayList<BiomeWeather> weathers = getBiomeList(level);
        if (provider != null && weathers != null) {
            SolarTerm solarTerm = provider.getSolarTerm();
            SnowTerm snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            MutableRegistry<Biome> biomes = level.registryAccess().registry(Registry.BIOME_REGISTRY).get();
            ResourceLocation loc = biomes.getKey(biome);
            for (BiomeWeather biomeWeather : weathers) {
                if (biomeWeather.location.equals(loc)) {
                    return flag_cold
                            || BiomeClimateManager.getDefaultTemperature(biome) <= BiomeClimateManager.SNOW_LEVEL ?
                            Biome.RainType.SNOW : Biome.RainType.RAIN;
                }
            }
        }

        return Biome.RainType.NONE;
    }


    public static Biome.RainType getRainOrSnow(World levelNull, Biome biome, BlockPos p198905) {
        World level = fetchWorldIfNull(levelNull);
        SolarDataManager provider = SolarUtil.getProvider(level);
        ArrayList<BiomeWeather> weathers = getBiomeList(level);
        if (provider != null && weathers != null) {
            SolarTerm solarTerm = provider.getSolarTerm();
            SnowTerm snowTerm = SolarTerm.getSnowTerm(biome);
            boolean flag_cold = solarTerm.isInTerms(snowTerm.getStart(), snowTerm.getEnd());
            MutableRegistry<Biome> biomes = level.registryAccess().registry(Registry.BIOME_REGISTRY).get();
            ResourceLocation loc = biomes.getKey(biome);
            for (BiomeWeather biomeWeather : weathers) {
                if (biomeWeather.location.equals(loc)) {
                    if (biomeWeather.shouldClear())
                        return Biome.RainType.NONE;

                    return flag_cold
                            || BiomeClimateManager.getDefaultTemperature(biome) <= BiomeClimateManager.SNOW_LEVEL ?
                            Biome.RainType.SNOW : Biome.RainType.RAIN;
                }
            }
        }

        return Biome.RainType.NONE;
    }


    public static void createLevelBiomeWeatherList(World level) {
        ArrayList<BiomeWeather> list = new ArrayList<BiomeWeather>();
        WeatherManager.BIOME_WEATHER_LIST.put(level, list);
        {
            Optional<MutableRegistry<Biome>> biomes = level.registryAccess().registry(Registry.BIOME_REGISTRY);
            if (biomes.isPresent()) {
                for (Biome biome : biomes.get()) {
                    ResourceLocation loc = biomes.get().getKey(biome);
                    int id = biomes.get().getId(biome);
                    // Biome biomeHolder = biomes.get().getHolder(ResourceKey.create(BuiltinRegistries.BIOME.key(), biomes.get().getKey(biome)));
                    // if (biomeHolder.isPresent())
                    {
                        BiomeWeather biomeWeather = new BiomeWeather(biome);
                        biomes.get().getId(biome);
                        biomeWeather.location = loc;
                        biomeWeather.id = id;
                        list.add(biomeWeather);
                    }
                }
            }
        }
    }

    public static void informUpdateBiomes() {

        Optional<MutableRegistry<Biome>> biomes = DynamicRegistries.builtin().registry(Registry.BIOME_REGISTRY);
        biomes.ifPresent(biomeRegistry -> biomeRegistry.forEach(biome ->
        {
            ResourceLocation loc = biomeRegistry.getKey(biome);
            int id = biomeRegistry.getId(biome);
            Optional.of(biome)
                    .ifPresent(biomeHolder -> {
                        WeatherManager.BIOME_WEATHER_LIST.entrySet().stream().forEach(levelArrayListEntry ->
                        {
                            ArrayList<BiomeWeather> biomeWeathers = levelArrayListEntry.getValue();

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
                                BiomeWeather biomeWeather = new BiomeWeather(biomeHolder);
                                biomeRegistry.getId(biome);
                                biomeWeather.location = loc;
                                biomeWeather.id = id;
                                biomeWeathers.add(biomeWeather);
                            }
                        });
                    });

        }));
    }

    public static void tickPlayerSeasonEffecct(ServerPlayerEntity player) {
        World level = player.level;
        if (CommonConfig.Temperature.heatStroke.get()
                && level.getRandom().nextInt(150) == 0)
            SolarHolders.getSaveDataLazy(level).ifPresent(solarDataManager -> {
                if (EclipticUtil.getNowSolarTerm(level).isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                    Biome b = level.getBiome(player.blockPosition());
                    if (b.getTemperature(player.blockPosition()) > 0.5f) {

                        if (!player.isInWaterOrRain()
                                && ((EclipticUtil.isNoon(level) && (level.canSeeSky(player.blockPosition()))))
                        ) {
                            boolean isColdHe = false;
                            for (ItemStack itemstack : player.getArmorSlots()) {
                                Item item = itemstack.getItem();
                                if (item instanceof ArmorItem) {
                                    if (((ArmorItem) item).getSlot() == EquipmentSlotType.HEAD) {

                                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_PROTECTION, itemstack) > 0) {
                                            isColdHe = true;
                                        }
                                    }
                                }
                            }
                            if (!player.hasEffect(EffectRegistry.HEAT_STROKE) && !isColdHe) {
                                player.addEffect(new EffectInstance(EffectRegistry.HEAT_STROKE, 600));
                            }
                        }
                    }
                }
            });
    }

    public static void runWeather(ServerWorld level, BiomeWeather biomeWeather, Random random, int size) {

        if (biomeWeather.shouldClear()) {
            biomeWeather.clearTime--;
        } else {
            if (biomeWeather.shouldRain()) {
                biomeWeather.rainTime--;
                if (!biomeWeather.shouldThunder()) {
                    BiomeRain biomeRain = SolarHolders.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.getBiomeKey());
                    float weight = biomeRain.getThunderChance();
                    if (level.getRandom().nextInt(1000) / 1000.f < weight) {

                        biomeWeather.thunderTime = (random.nextInt(12000) + 3600) / size;
                    }
                }
            } else {
                BiomeRain biomeRain = SolarHolders.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.getBiomeKey());
                float downfall = biomeWeather.biomeHolder.getDownfall();


                if (BiomeDictionary.getTypes(biomeWeather.getBiomeKey()).contains(BiomeDictionary.Type.SAVANNA)) {
                    downfall += 0.2f;
                }
                float weight = biomeRain.getRainChane()
                        * Math.max(0.01f, downfall)
                        * ((CommonConfig.Season.rainChanceMultiplier.get() * 1f) / 100f);
                if (level.getRandom().nextInt(1000) / 1000.f < weight) {
                    biomeWeather.rainTime = (random.nextInt(12000) + 12000) / size;
                } else {
                    // biomeWeather.clearTime = 10 / (size / 30);
                    biomeWeather.clearTime = (random.nextInt(12000) + 12000) / size;
                }
            }
        }

        if (biomeWeather.shouldThunder()) {
            biomeWeather.thunderTime--;
        }
        // if (biomeWeather.biomeHolder.is(Biomes.JUNGLE)) {
        //     // BiomeRain biomeRain = AllListener.getSaveData(level).getSolarTerm().getBiomeRain(biomeWeather.biomeHolder);
        //     // float weight = biomeRain.getRainChane() * Math.max(0.01f, biomeWeather.biomeHolder.getModifiedClimateSettings().downfall());
        //     //
        //     // Ecliptic.logger(biomeWeather,weight) ;
        // }


        if (biomeWeather.shouldRain() || level.getRandom().nextInt(5) > 1) {
            SnowRenderStatus snow = WeatherManager.getSnowStatus(level, biomeWeather.biomeHolder, null);
            if (snow == SnowRenderStatus.SNOW) {
                biomeWeather.snowDepth = (byte) Math.min(100, biomeWeather.snowDepth + 1);
            } else if (snow == SnowRenderStatus.SNOW_MELT) {
                biomeWeather.snowDepth = (byte) Math.max(0, biomeWeather.snowDepth - 1);
            }
        }

    }

    public static void updateAfterSleep(ServerWorld level, long newTime, long oldDayTime) {
        if (newTime > oldDayTime) {
            ArrayList<BiomeWeather> ws = WeatherManager.getBiomeList(level);
            if (ws != null) {
                Random random = level.getRandom();
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

    public static void onLoggedIn(ServerPlayerEntity serverPlayer, boolean isLogged) {
        if ((serverPlayer instanceof FakePlayer)) return;
        SolarHolders.getSaveDataLazy(serverPlayer.level).ifPresent(t ->
        {
            SimpleNetworkHandler.send(serverPlayer, new SolarTermsMessage(t.getSolarTermsDay()));
            if (isLogged && CommonConfig.Season.enableInform.get()
                    && t.getSolarTermsDay() % CommonConfig.Season.lastingDaysOfEachTerm.get() == 0) {
                serverPlayer.sendMessage(new TranslationTextComponent("info.eclipticseasons.environment.solar_term.message", SolarTerm.get(t.getSolarTermIndex()).getAlternationText()), Util.NIL_UUID);
            }
        });
        WeatherManager.sendBiomePacket(WeatherManager.getBiomeList(serverPlayer.level), Stream.of(serverPlayer).collect(Collectors.toList()));
    }


    public static class BiomeWeather implements INBTSerializable<CompoundNBT> {
        public Biome biomeHolder;
        public int id;
        public SnowTerm snowTerm;

        public ResourceLocation location;
        public int rainTime = 0;
        public int thunderTime = 0;
        public int clearTime = 0;
        public byte snowDepth = 0;


        public BiomeWeather(Biome biomeHolder) {
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
        public CompoundNBT serializeNBT() {
            CompoundNBT tag = new CompoundNBT();
            tag.putString("biome", location.toString());
            tag.putInt("rainTime", rainTime);
            tag.putInt("thunderTime", thunderTime);
            tag.putInt("clearTime", clearTime);
            tag.putByte("snowDepth", snowDepth);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            location = new ResourceLocation(nbt.getString("biome"));
            rainTime = nbt.getInt("rainTime");
            thunderTime = nbt.getInt("thunderTime");
            clearTime = nbt.getInt("clearTime");
            snowDepth = nbt.getByte("snowDepth");
        }

        public RegistryKey<Biome> getBiomeKey() {
            return RegistryKey.create(Registry.BIOME_REGISTRY, location);
        }
    }

    public static Boolean onCheckWarmEnoughToRain(BlockPos p198905) {
        // return SolarTerm.get(AllListener.provider.resolve().get().worldSolarTime.getSolarTermIndex()).getSeason() != Season.WINTER;
        return true;
    }

    public static boolean onShouldSnow(ServerWorld level, Biome biome, BlockPos pos) {
        // return SolarTerm.get(AllListener.provider.resolve().get().worldSolarTime.getSolarTermIndex()).getSeason() == Season.WINTER;
        return true;
    }

    public static boolean agentAdvanceWeatherCycle(ServerWorld level, ServerWorld serverLevelData, ServerWorld levelData, Random random) {

        if (!level.dimensionType().natural()) {
            return true;
        }
        int pos = NEXT_CHECK_BIOME_MAP.getOrDefault(level, -1);

        ArrayList<BiomeWeather> levelBiomeWeather = getBiomeList(level);

        if (pos >= 0 && levelBiomeWeather != null && pos < levelBiomeWeather.size()) {
            int size = levelBiomeWeather.size();
            BiomeWeather biomeWeather = getBiomeList(level).get(pos);

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

    public static void sendBiomePacket(ArrayList<BiomeWeather> levelBiomeWeather, List<ServerPlayerEntity> players) {
        if (players.isEmpty()) return;
        byte[] rains = new byte[levelBiomeWeather.size()];
        byte[] thunders = new byte[levelBiomeWeather.size()];
        byte[] clears = new byte[levelBiomeWeather.size()];
        byte[] snows = new byte[levelBiomeWeather.size()];
        int[] ids = new int[levelBiomeWeather.size()];
        for (int i = 0; i < levelBiomeWeather.size(); i++) {
            BiomeWeather biomeWeather = levelBiomeWeather.get(i);
            rains[i] = (byte) (biomeWeather.shouldRain() ? 1 : 0);
            thunders[i] = (byte) (biomeWeather.shouldThunder() ? 1 : 0);
            clears[i] = (byte) (biomeWeather.shouldClear() ? 1 : 0);
            snows[i] = biomeWeather.snowDepth;
            ids[i] = biomeWeather.id;
        }

        BiomeWeatherMessage msg = new BiomeWeatherMessage(rains, thunders, clears, snows, ids);
        SimpleNetworkHandler.send(players, msg);
    }

    public enum SnowRenderStatus {
        SNOW,
        SNOW_MELT,
        // RAIN,
        // CLOUD,
        NONE
    }

    public static SnowRenderStatus getSnowStatus(ServerWorld level, Biome biome, BlockPos pos) {
        SolarDataManager provider = SolarUtil.getProvider(level);
        SnowRenderStatus status = SnowRenderStatus.NONE;
        if (provider != null) {
            SolarTerm solarTerm = provider.getSolarTerm();
            SnowTerm snowTerm = SolarTerm.getSnowTerm(biome);
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
