package com.teamtea.eclipticseasons.client.core.map;

import java.util.Objects;

public final class XZPos {
    private final int x;
    private final int z;
    private final long startTick;
    private final int startY;

    public XZPos(int x, int z, long startTick, int startY) {
        this.x = x;
        this.z = z;
        this.startTick = startTick;
        this.startY = startY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XZPos xzPos = (XZPos) o;
        return x == xzPos.x && z == xzPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    @Override
    public String toString() {
        return "XZPos{" +
                "startTick=" + startTick +
                ", x=" + x +
                ", z=" + z +
                ", startY=" + startY +
                '}';
    }

    public int x() {
        return x;
    }

    public int z() {
        return z;
    }

    public long startTick() {
        return startTick;
    }

    public int startY() {
        return startY;
    }

}
