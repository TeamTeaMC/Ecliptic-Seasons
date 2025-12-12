package com.teamtea.eclipticseasons.common.core.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.serializer.SerializerHolder;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import lombok.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;

@Data
public class NoneSnowArea implements SerializerHolder<NoneSnowArea> {

    public static final Codec<NoneSnowArea> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.LONG.listOf().fieldOf("pos_list").forGetter(o -> o.posMap.longStream().boxed().toList())
    ).apply(ins, longs -> new NoneSnowArea(new LongLinkedOpenHashSet(longs))));

    private final LongLinkedOpenHashSet posMap;

    public boolean neverSnowyAt(BlockPos blockPos) {
        return posMap.contains(blockPos.asLong());
    }

    public boolean canSkip() {
        return posMap.isEmpty();
    }

    public boolean add(BlockPos blockPos) {
        boolean add = posMap.add(blockPos.asLong());
        if (add) setChanged();
        return add;
    }

    public boolean remove(BlockPos blockPos) {
        boolean remove = posMap.remove(blockPos.asLong());
        if (remove) setChanged();
        return remove;
    }

    public static NoneSnowArea empty() {
        return new NoneSnowArea(new LongLinkedOpenHashSet());
    }


    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @Getter
    @Setter
    private transient CompoundTag cacheTag = null;

    // ===================================================================
    // 1.20.1 use


    @Override
    public Codec<NoneSnowArea> codec() {
        return CODEC;
    }

    @Override
    public void copyFrom(NoneSnowArea keeper) {
        this.posMap.clear();
        this.posMap.addAll(keeper.posMap);
    }


    public static final Capability<NoneSnowArea> NONE_SNOW_AREA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    @Override
    public Capability<NoneSnowArea> getCapabilityName() {
        return NONE_SNOW_AREA_CAPABILITY;
    }

    @Getter
    @Setter
    private LazyOptional<NoneSnowArea> cast = null;
}
