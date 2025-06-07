package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.world.level.chunk.Palette;

public interface IUnpackableBitStorage {
     <T> T[] eclipticseasons$unpack(Class<T> clazz, Palette<T> palette);
}
