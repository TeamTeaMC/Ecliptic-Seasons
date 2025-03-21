package com.teamtea.eclipticseasons.common.core.crop;


public final class HumidityControlProvider {
    private float range;
    private int level;
    private int remainTime;


    public HumidityControlProvider(int level, float range, int remainTime) {
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRemainTime() {
        return remainTime;
    }

    public void addRemainTime(int remainTime) {
        this.remainTime += remainTime;
    }
}
