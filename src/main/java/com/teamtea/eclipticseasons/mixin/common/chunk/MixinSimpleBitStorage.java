package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.teamtea.eclipticseasons.api.misc.IUnpackableBitStorage;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.chunk.Palette;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.lang.reflect.Array;

@Mixin({SimpleBitStorage.class})
public abstract class MixinSimpleBitStorage implements IUnpackableBitStorage {

    @Shadow @Final private long[] data;

    @Shadow @Final private int valuesPerLong;

    @Shadow @Final private int bits;

    @Shadow @Final private int size;

    @Shadow @Final private long mask;

    @SuppressWarnings("unchecked")

    public <T> T[] eclipticseasons$unpack(Class<T> clazz, Palette<T> palette) {
        T[] result = (T[]) Array.newInstance(clazz, this.size);

        int index = 0;
        long[] words = this.data;

        for (long word : words) {
            long current = word;

            for (int j = 0; j < this.valuesPerLong; ++j) {
                result[index++] = palette.valueFor((int)(current & this.mask));
                if (index >= this.size) return result;
                current >>= this.bits;
            }
        }
        return result;
    }
}
