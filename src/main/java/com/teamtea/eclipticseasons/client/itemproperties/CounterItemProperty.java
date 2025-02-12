package com.teamtea.eclipticseasons.client.itemproperties;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CounterItemProperty implements ClampedItemPropertyFunction {

    private final BiomePosFunction<Level, BlockPos, ITranslatable> biomeIntegerFunction;
    private final float maxLength;

    public CounterItemProperty(BiomePosFunction<Level, BlockPos, ITranslatable> biomeIntegerFunction, float maxLength) {
        this.biomeIntegerFunction = biomeIntegerFunction;
        this.maxLength = maxLength;
    }


    @Override
    public float unclampedCall(@NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        Entity newEntity = entity != null ? entity : stack.getEntityRepresentation();

        level = tryFixLevelAnyway(entity, level);
        if (level != null) {
            Object c=null;

            BlockPos blockPosition = newEntity.blockPosition();
            return biomeIntegerFunction.apply(level, blockPosition).ordinal() * (1f / (maxLength - 1));
        }
        return 0f;

    }

    @javax.annotation.Nullable
    private ClientLevel tryFixLevelAnyway(Entity entity, @javax.annotation.Nullable ClientLevel level) {
        if (entity == null) return null;
        return level == null && entity.level instanceof ClientLevel ? (ClientLevel) entity.level : level;
    }

    @FunctionalInterface
    public static interface BiomePosFunction<B, P, I> {
        I apply(B b, P p);
    }
}
