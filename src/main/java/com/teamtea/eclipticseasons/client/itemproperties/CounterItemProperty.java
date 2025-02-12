package com.teamtea.eclipticseasons.client.itemproperties;

import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CounterItemProperty implements IItemPropertyGetter {

    private final BiomePosFunction<World, BlockPos, ITranslatable> biomeIntegerFunction;
    private final float maxLength;

    public CounterItemProperty(BiomePosFunction<World, BlockPos, ITranslatable> biomeIntegerFunction, float maxLength) {
        this.biomeIntegerFunction = biomeIntegerFunction;
        this.maxLength = maxLength;
    }


    @javax.annotation.Nullable
    private ClientWorld tryFixLevelAnyway(Entity entity, @javax.annotation.Nullable ClientWorld level) {
        if (entity == null) return null;
        return level == null && entity.level instanceof ClientWorld ? (ClientWorld) entity.level : level;
    }

    @Override
    public float call(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        Entity newEntity = entity != null ? entity : stack.getEntityRepresentation();

        world = tryFixLevelAnyway(entity, world);
        if (world != null) {
            Object c = null;

            BlockPos blockPosition = newEntity.blockPosition();
            return biomeIntegerFunction.apply(world, blockPosition).ordinal() * (1f / (maxLength - 1));
        }
        return 0f;
    }

    @FunctionalInterface
    public static interface BiomePosFunction<B, P, I> {
        I apply(B b, P p);
    }
}
