package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.CeilingHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class QuestCeilingHangingSignBlock extends CeilingHangingSignBlock {
    public QuestCeilingHangingSignBlock(Properties properties) {
        super(properties, WoodType.OAK);
    }


    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return ItemRegistry.seasonal_prayer_scroll_item.get().getDefaultInstance();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return BlockEntityRegistry.season_quest_hanging_sign_entity_type.get().create(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        return createTickerHelper(blockEntityType, BlockEntityRegistry.season_quest_hanging_sign_entity_type.get(), QuestHangingSignBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {
        if (level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity questHangingSignBlockEntity) {
            if (!level.isClientSide()&& player instanceof ServerPlayer serverPlayer) {
                questHangingSignBlockEntity.finishSeasonQuest(serverPlayer);
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        QuestWallHangingSignBlock.addSeasonalParticle(state, level, pos, random);
    }


    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // if (level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity blockEntity) {
        //     state= blockEntity.getSignType().defaultBlockState();
        // }
        level.levelEvent(player, 2001, pos, getId(state));
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        super.playerWillDestroy(level, pos, state, player);
        if (!level.isClientSide() && player.isCreative()) {
            QuestHangingSignBlockEntity.removeSign(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !level.isClientSide() && !newState.is(this)) {
            QuestHangingSignBlockEntity.popSign(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
