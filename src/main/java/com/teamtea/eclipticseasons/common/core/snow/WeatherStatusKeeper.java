package com.teamtea.eclipticseasons.common.core.snow;


import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.codec.CodecUtil;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntLongMutablePair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import lombok.*;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;


@Data
public class WeatherStatusKeeper implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Codec<WeatherStatusKeeper> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            CodecUtil.holderCodec(Registries.BIOME).listOf().optionalFieldOf("biomes", List.of())
                    .forGetter(o -> new ArrayList<>(o.snowDepthRecord.keySet())),
            Codec.INT.listOf().optionalFieldOf("snow_depth", List.of())
                    .forGetter(o -> o.snowDepthRecord.values().stream().map(IntLongMutablePair::leftInt).toList()),
            Codec.LONG.listOf().optionalFieldOf("last_rain_time", List.of())
                    .forGetter(o -> o.snowDepthRecord.values().stream().map(IntLongMutablePair::rightLong).toList())
    ).apply(ins, WeatherStatusKeeper::new));

    protected WeatherStatusKeeper() {
        this(List.of(), List.of(), List.of());
    }

    public static WeatherStatusKeeper create() {
        return new WeatherStatusKeeper();
    }

    public WeatherStatusKeeper(List<Holder<Biome>> biomes,
                               List<Integer> snow_depth,
                               List<Long> last_rain_time) {
        if (biomes.size() == snow_depth.size()) {
            boolean lastRainRecord = last_rain_time.size() == biomes.size();
            for (int i = 0, biomesSize = biomes.size(); i < biomesSize; i++) {
                Holder<Biome> biome = biomes.get(i);
                snowDepthRecord.put(biome, IntLongMutablePair.of(
                        snow_depth.get(i), lastRainRecord ? last_rain_time.get(i) : 0
                ));
            }
        }
    }


    private final Map<Holder<Biome>, IntLongMutablePair> snowDepthRecord = new Reference2ObjectLinkedOpenHashMap<>();

    private final Set<Holder<Biome>> biomeUse = new ReferenceLinkedOpenHashSet<>();

    private boolean change = false;


    public void removeBiomeRecord(Holder<Biome> biomeHolder) {
        this.snowDepthRecord.remove(biomeHolder);
        setChange();
    }

    public void updateAndSend(ServerLevel serverLevel, LevelChunk chunk) {
        if (EclipticUtil.canSnowyBlockInteract()) {
            updateBiomeWhenEndTick(serverLevel);
            if (change) {
                chunk.setUnsaved(true);
            }
        }
        change = false;
    }

    protected void updateBiomeWhenEndTick(ServerLevel serverLevel) {
        if (serverLevel.getRandom().nextInt(20) == 0) {
            for (Holder<Biome> biomeHolder : biomeUse) {
                WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(serverLevel, biomeHolder);
                if (biomeWeather == null) continue;
                int snowDepth = biomeWeather.getSnowDepth();
                long lastRainTime = biomeWeather.lastRainTime;
                IntLongMutablePair orDefault = snowDepthRecord.getOrDefault(biomeHolder, null);
                if (orDefault == null || orDefault.leftInt() != snowDepth || orDefault.rightLong() != lastRainTime) {
                    if (orDefault == null) {
                        orDefault = IntLongMutablePair.of(snowDepth, lastRainTime);
                        snowDepthRecord.put(biomeHolder, orDefault);
                    } else {
                        orDefault.left(snowDepth).right(lastRainTime);
                    }
                    setChange();
                }
            }
        }
    }

    protected void setChange() {
        change = true;
        cacheTag = null;
    }

    public Pair<Map<Holder<Biome>, IntIntImmutablePair>, Map<Holder<Biome>, Long>> collectSnowyUpdate(ServerLevel level, @Nullable BiomeHolder biomeHolder) {
        Map<Holder<Biome>, IntIntImmutablePair> biomeSnowyUpdate = new IdentityHashMap<>();
        Map<Holder<Biome>, Long> biomeRainUpdate;
        if (EclipticUtil.canSnowyBlockInteract()) {
            // a chunk never tick without any record
            if (snowDepthRecord.isEmpty()) {
                biomeRainUpdate = null;
                Set<Holder<Biome>> biomeDetect = new ReferenceLinkedOpenHashSet<>();
                if (biomeHolder != null) {
                    Registry<Biome> biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
                    IntOpenHashSet ids = new IntOpenHashSet(biomeHolder.biomes());
                    for (int i : ids.toIntArray()) {
                        biomeDetect.add(MapChecker.idToBiome(biomes, i));
                    }
                }
                var ws = WeatherManager.getBiomeList(level);
                if (ws != null) {
                    // level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes()
                    for (Holder<Biome> holder : biomeDetect) {
                        WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, holder);
                        if (biomeWeather != null) {
                            biomeSnowyUpdate.put(holder, IntIntImmutablePair.of(biomeWeather.getSnowDepth(), 0));
                        }
                    }
                }
            } else {
                biomeRainUpdate = new IdentityHashMap<>();
                int weatherTickFactor = WeatherManager.getWeatherTickFactor(level) >> 2;
                snowDepthRecord.forEach((biome, pair) -> {
                    WeatherManager.BiomeWeather biomeWeather = WeatherManager.getBiomeWeather(level, biome);
                    if (biomeWeather == null) return;
                    int snowDepthAtBiome = biomeWeather.getSnowDepth();
                    long lastRainTime = biomeWeather.lastRainTime;
                    int snowDepthIncrease = snowDepthAtBiome - pair.leftInt();
                    int offsetAbs = Mth.abs(snowDepthIncrease);
                    if ((snowDepthAtBiome < 3 || snowDepthAtBiome > 97) && offsetAbs != 0) {
                        biomeSnowyUpdate.put(biome, IntIntImmutablePair.of(snowDepthAtBiome, 0));
                    } else if (offsetAbs > 20) {
                        biomeSnowyUpdate.put(biome, IntIntImmutablePair.of(snowDepthAtBiome, snowDepthIncrease));
                    }
                    if (pair.rightLong() < lastRainTime - weatherTickFactor) {
                        biomeRainUpdate.put(biome, lastRainTime);
                    }
                });
            }
        } else {
            biomeRainUpdate = null;
        }
        return Pair.of(biomeSnowyUpdate, biomeRainUpdate);
    }

    public void updateSnowDepthRecord(ServerLevel level) {
        updateBiomeWhenEndTick(level);
    }


    public static final WeatherStatusKeeper EMPTY = WeatherStatusKeeper.create();


    // ===================================================================
    // 1.20.1 use


    public static final Capability<WeatherStatusKeeper> WEATHER_STATUS_KEEPER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });


    private LazyOptional<WeatherStatusKeeper> cast = null;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @org.jetbrains.annotations.Nullable Direction side) {
        if (cap == WEATHER_STATUS_KEEPER_CAPABILITY) {
            if (cast == null) cast = LazyOptional.of(() -> this);
            return cast.cast();
        }
        return LazyOptional.empty();
    }

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    private transient CompoundTag cacheTag = null;


    @Override
    public CompoundTag serializeNBT() {
        if (!EclipticUtil.canSnowyBlockInteract()) new CompoundTag();
        if (cacheTag != null) return cacheTag;
        Level level = WeatherManager.fetchLevelIfNull(null);
        if (level != null) {
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            Optional<Tag> result = CODEC.encodeStart(registryOps, this).result();
            if (result.orElse(null) instanceof CompoundTag compoundTag) {
                this.cacheTag = compoundTag;
                return compoundTag;
            }
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;
        if (!biomeUse.isEmpty()) return;
        Level level = WeatherManager.fetchLevelIfNull(null);
        if (level != null) {
            RegistryOps<Tag> registryOps = RegistryOps.create(NbtOps.INSTANCE, level.registryAccess());
            Optional<WeatherStatusKeeper> result = CODEC.parse(registryOps, nbt).result();
            result.ifPresent(this::copyFrom);
        }
    }

    public void copyFrom(WeatherStatusKeeper keeper) {
        this.snowDepthRecord.clear();
        this.snowDepthRecord.putAll(keeper.snowDepthRecord);
    }


}
