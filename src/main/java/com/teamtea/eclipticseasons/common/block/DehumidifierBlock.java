package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.base.SimpleHumidityProviderBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.HumidityModifierBlockEntity;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class DehumidifierBlock extends SimpleEntityBlock implements SimpleHumidityProviderBlock {

    public DehumidifierBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(DehumidifierBlock::new);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return BlockEntityRegistry.humidity_modifier.get().create(worldPosition, blockState);
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, @NonNull BlockState state, @NonNull BlockEntityType<T> blockEntityType) {
        return !worldIn.isClientSide() ?
                SimpleEntityBlock.createTickerHelper(blockEntityType, BlockEntityRegistry.humidity_modifier.get(), HumidityModifierBlockEntity::tick) : null;
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        // tryMoistenFarmland(level, pos, random);
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {
        if (random.nextFloat() >= 0.12F) {
            return;
        }
        if (!isValid(level, pos, state)) return;

        pos = pos.below();
        if (!CropGrowthHandler.isInRoom(level, pos, state, Optional.empty())) return;

        double x = pos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;
        double y = pos.getY() + 0.8D;
        double z = pos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 0.2D;

        level.addParticle(
                ParticleTypes.CLOUD,
                x, y, z,
                0.0D,
                0.03D,
                0.0D
        );
    }

    @Override
    public float getHumidityModifiedLevel() {
        return -0.5f;
    }

    @Override
    public float getHumidityModifiedRange() {
        return CommonConfig.Crop.humidityTankRange.get().floatValue();
    }

    @Override
    public boolean isValid(Level level, BlockPos pos, BlockState state) {
        BlockPos.MutableBlockPos mutable = pos.mutable();
        return !level.getBlockState(mutable.setY(pos.getY() + 1)).isSolidRender()
                && !level.getBlockState(mutable.setY(pos.getY() - 1)).isSolidRender();
        // Direction facing = state.getValue(FACING);
        // return !CropGrowthHandler.isInRoom(level, pos.relative(facing), state, Optional.empty())
        //         && CropGrowthHandler.isInRoom(level, pos.relative(facing.getOpposite()), state, Optional.empty());
    }
}
