package com.teamtea.eclipticseasons.api.misc;

import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface IUnpackablePalettedContainer<T> {

    static <T> IUnpackablePalettedContainer<T> of(PalettedContainerRO<T> container) {
        return (IUnpackablePalettedContainer)container;
    }

    T[] eclipticseasons$unpack(Class<T> clazz);
}
