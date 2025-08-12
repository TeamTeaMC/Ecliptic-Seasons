package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GreenHouseFrameBlock extends SimpleEntityBlock {

    public GreenHouseFrameBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(GreenHouseFrameBlock::new);
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
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (stack.getItem() == ItemRegistry.spring_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundSource.BLOCKS);
                level.setBlockAndUpdate(pos, BlockRegistry.spring_greenhouse_core.get().defaultBlockState());
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.summer_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundSource.BLOCKS);
                level.setBlockAndUpdate(pos, BlockRegistry.summer_greenhouse_core.get().defaultBlockState());
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.autumn_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundSource.BLOCKS);
                level.setBlockAndUpdate(pos, BlockRegistry.autumn_greenhouse_core.get().defaultBlockState());
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.winter_greenhouse_essence_item.get()) {
            if (!player.isCreative()) stack.shrink(1);
            if (!level.isClientSide()) {
                level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundSource.BLOCKS);
                level.setBlockAndUpdate(pos, BlockRegistry.winter_greenhouse_core.get().defaultBlockState());
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
