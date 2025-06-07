package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.block.blockentity.QuestHangingSignBlockEntity;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuestWallHangingSignBlock extends WallHangingSignBlock {
    protected String descriptionId=null;

    public QuestWallHangingSignBlock(Properties properties) {
        super(WoodType.OAK, properties);
    }

    @Override
    public @NotNull String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("block", BuiltInRegistries.BLOCK.getKey(this));
        }
        return this.descriptionId;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player) {
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
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
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
        QuestWallHangingSignBlock.addSeasonalParticle(state,level, pos, random);
    }


    public static void addSeasonalParticle(BlockState state, Level level, BlockPos pos, RandomSource random) {
        Direction direction = Direction.DOWN;
        int color = Season.collectValues()[random.nextInt(Season.collectValues().length)].getColor().getColor();
        float r = FastColor.ARGB32.red(color) / 255.0F;
        float g = FastColor.ARGB32.green(color) / 255.0F;
        float b = FastColor.ARGB32.blue(color) / 255.0F;
        ColorParticleOption colorParticleOption = ColorParticleOption.create(ParticleRegistry.GREENHOUSE, r, g, b);

        for (int i = 0; i < 2; i++)
        {
            double d0 = pos.getX() + (random.nextDouble() - 0.5) +0.5;
            double d1 = pos.getY() + (random.nextDouble() - 0.5)-0.125;
            double d2 = pos.getZ() + (random.nextDouble()  - 0.5)+0.5;

            double d3 = (random.nextDouble() - 0.5) * 0.4;

            if (random.nextInt(6) == 0)
            {
                double x = d0 + direction.getStepX() * d3;
                double y = d1 + direction.getStepY() * d3;
                double z = d2 + direction.getStepZ() * d3;
                if (level.isEmptyBlock(new BlockPos((int) x, (int) (y), (int) z)))
                    level.addParticle(
                            // ParticleTypes.END_ROD,
                            colorParticleOption,
                             x, y, z,
                            random.nextGaussian() * 0.005,
                            0.001 + random.nextDouble() * 0.02,
                            random.nextGaussian() * 0.005
                    );
            }
        }
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        // if (level.getBlockEntity(pos) instanceof QuestHangingSignBlockEntity blockEntity) {
        //   state= blockEntity.getSignType().defaultBlockState();
        // }
        level.levelEvent(player, 2001, pos, getId(state));
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockState blockState = super.playerWillDestroy(level, pos, state, player);
        if ( !level.isClientSide()&& player.isCreative()) {
            QuestHangingSignBlockEntity.removeSign(level, pos);
        }
        return blockState;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!movedByPiston && !level.isClientSide()&& !newState.is(this)) {
            QuestHangingSignBlockEntity.popSign(level, pos);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
