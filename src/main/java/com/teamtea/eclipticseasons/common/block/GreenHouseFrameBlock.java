package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class GreenHouseFrameBlock extends SimpleEntityBlock {

    public GreenHouseFrameBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityRegistry.greenhouse_core_container_entity_type.get().create(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ItemRegistry.spring_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide())
                level.setBlockAndUpdate(pos, BlockRegistry.spring_greenhouse_core.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.summer_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide())
                level.setBlockAndUpdate(pos, BlockRegistry.summer_greenhouse_core.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.autumn_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide())
                level.setBlockAndUpdate(pos, BlockRegistry.autumn_greenhouse_core.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.winter_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide())
                level.setBlockAndUpdate(pos, BlockRegistry.winter_greenhouse_core.get().defaultBlockState());
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.use( state, level, pos, player, hand, hitResult);
    }
}
