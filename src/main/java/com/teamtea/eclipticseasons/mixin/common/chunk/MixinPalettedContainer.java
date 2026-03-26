package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IUnpackableBitStorage;
import com.teamtea.eclipticseasons.api.misc.IUnpackablePalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;


@Mixin({PalettedContainer.class})
public abstract class MixinPalettedContainer<T> implements IUnpackablePalettedContainer<T> {


    @Shadow
    private volatile PalettedContainer.Data<T> data;

    @Override
    public T[] eclipticseasons$unpack(Class<T> clazz) {
        IUnpackableBitStorage storage = (IUnpackableBitStorage) data.storage();
        return storage.eclipticseasons$unpack(clazz, data.palette());
    }
}
