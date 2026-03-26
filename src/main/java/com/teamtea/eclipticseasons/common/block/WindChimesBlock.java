package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.WindChimesBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WindChimesBlock extends SimpleHorizontalEntityBlock {

    public static final VoxelShape BOX_P = Block.box(3, 3, 3, 13, 15, 13);
    public static final VoxelShape BOX_0 = Block.box(3, 0, 3, 13, 15, 13);
    public static final VoxelShape BOX_B = Block.box(4, 1, 4, 12, 15, 12);

    public WindChimesBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(WindChimesBlock::new);
    }


    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        Direction direction = Direction.UP;
        return Block.canSupportCenter(level, pos.relative(direction), direction.getOpposite());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (directionToNeighbour == Direction.UP && neighbourState.isAir())
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityRegistry.wind_chimes_entity_type.get().create(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Block block = state.getBlock();
        if (block == BlockRegistry.wind_chimes.get()) {
            return BOX_0;
        } else if (block == BlockRegistry.bamboo_wind_chimes.get()) {
            return BOX_B;
        }
        return BOX_P;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        super.entityInside(state, level, pos, entity, effectApplier, isPrecise);
        if (entity instanceof LivingEntity livingEntity) {
            Vec3 deltaMovement = livingEntity.getDeltaMovement();
            Direction facing = state.getValue(FACING);
            if (level.getBlockEntity(pos) instanceof WindChimesBlockEntity windChimesBlockEntity
                    && level.isClientSide()
                // && ((deltaMovement.x != 0 && (facing ==Direction.SOUTH||facing ==Direction.NORTH))||(deltaMovement.z != 0&& (facing ==Direction.EAST||facing ==Direction.WEST)))
            ) {
                windChimesBlockEntity.setShaking(true);
                // if (level.isClientSide() && level.getGameTime() % 10 == 0)
                //     level.playSound(Minecraft.getInstance().player, pos, getSoundEvent(), SoundSource.BLOCKS, 0.5f, 0.5f);
            }
        }
    }


    // @Override
    // public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
    //     super.animateTick(state, level, pos, random);
    //
    //     if (level.getGameTime() % 25 == 0)
    //         level.playSound(Minecraft.getInstance().player, pos, getSoundEvent(), SoundSource.BLOCKS, 0.1f, 0.1f);
    // }

    public static SoundEvent getSoundEvent(Block block) {
        return block == BlockRegistry.paper_wind_chimes.get() ? SoundEventsRegistry.paper_wind_chimes :
                block == BlockRegistry.bamboo_wind_chimes.get() ? SoundEventsRegistry.bamboo_wind_chimes :
                        SoundEventsRegistry.wind_chimes;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        if (EclipticSeasonsApi.getInstance().isRainingOrSnowing(level, pos)
                && level.getBlockEntity(pos) instanceof WindChimesBlockEntity windChimesBlockEntity
        ) {
            windChimesBlockEntity.setShaking(true);
        }
    }
}
