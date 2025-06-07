package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IUnpackableBitStorage;
import net.minecraft.util.ZeroBitStorage;
import net.minecraft.world.level.chunk.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Array;
import java.util.Arrays;

@Mixin({ZeroBitStorage.class})
public abstract class MixinZeroBitStorage implements IUnpackableBitStorage {

    @Shadow
    @Final
    private int size;

    @Shadow
    public abstract int get(int index);

    @SuppressWarnings("unchecked")
    public <T> T[] eclipticseasons$unpack(Class<T> clazz, Palette<T> palette) {
        T[] result = (T[]) Array.newInstance(clazz, this.size);
        Arrays.fill(result, palette.valueFor(0));
        return result;
    }
}
