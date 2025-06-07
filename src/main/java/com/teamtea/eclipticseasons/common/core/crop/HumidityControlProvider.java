package com.teamtea.eclipticseasons.common.core.crop;


// TODO 距离衰减等级
public final class HumidityControlProvider {
    private float range;
    private float level;
    private int remainTime;


    public HumidityControlProvider(float level, float range, int remainTime) {
        this.level = level;
        this.range = range;
        this.remainTime = remainTime;
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
}
