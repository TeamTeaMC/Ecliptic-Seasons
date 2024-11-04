package com.teamtea.eclipticseasons.client.core.map;

import java.util.Objects;

public record XZPos(int x, int z, long startTick, int startY) {

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
}
