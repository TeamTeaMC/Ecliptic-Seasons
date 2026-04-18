package com.teamtea.eclipticseasons.mixin.common.block;


import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.misc.IBlockStateFlagger;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BlockState.class)
public abstract class MixinBlockState extends BlockBehaviour.BlockStateBase implements IBlockStateFlagger {
    protected MixinBlockState(Block owner, Property<?>[] propertyKeys, Comparable<?>[] propertyValues) {
        super(owner, propertyKeys, propertyValues);
    }

    // @Shadow
    // protected abstract BlockState asState();

    @Shadow
    protected abstract @NonNull BlockState asState();




    @Unique
    public int eclipticseasons$blockTypeFlag = -1;
    @Unique
    public boolean eclipticseasons$forceTickControl = false;

    @Override
    public void setBlockTypeFlag(int flag) {
        eclipticseasons$blockTypeFlag = flag;
    }

    @Override
    public int getBlockTypeFlag() {
        return eclipticseasons$blockTypeFlag;
    }

    @Override
    public BlockState es$asState() {
        return asState();
    }

    @Override
    public boolean forceTickControl() {
        return eclipticseasons$forceTickControl;
    }

    @Override
    public void setForceTickControl(boolean force) {
        eclipticseasons$forceTickControl = force;
    }

}
