package com.teamtea.eclipticseasons.common.core.crop;


import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

// TODO 距离衰减等级
public final class HumidityControlProvider implements INBTSerializable<CompoundTag> {
    private float range;
    private float level;
    private int remainTime;
    private boolean save;

    public HumidityControlProvider(float level, float range, int remainTime) {
        this(level, range, remainTime, false);
    }

    public HumidityControlProvider(float level, float range, int remainTime, boolean save) {
        this.level = level;
        this.range = range;
        this.remainTime = remainTime;
        this.save = save;
    }

    public float getRange() {
        return range;
    }

    public void setRange(float range) {
        this.range = range;
    }

    public void setRemainTime(int remainTime) {
        this.remainTime = remainTime;
    }

    public float getLevel() {
        return level;
    }

    public void setLevel(float level) {
        this.level = level;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void addRemainTime(int remainTime) {
        this.remainTime += remainTime;
    }

    public boolean shouldSave() {
        return save;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("remain_time", getRemainTime());
        compoundTag.putFloat("range", getRange());
        compoundTag.putFloat("level", getLevel());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag nbt) {
        this.remainTime = nbt.getInt("remain_time");
        this.range = nbt.getFloat("range");
        this.level = nbt.getFloat("level");
    }
}