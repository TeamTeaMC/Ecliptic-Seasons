package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.common.block.base.WallPlacedBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.CalendarBlockEntity;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class CalendarBlock extends WallPlacedBlock {

    protected final static VoxelShape shape_N = Shapes.box(0.1875, 0, 0.75, 0.8125, 0.875, 1);
    protected final static VoxelShape shape_S = Shapes.box(0.1875, 0, 0, 0.8125, 0.875, 0.25);
    protected final static VoxelShape shape_W = Shapes.box(0.75, 0, 0.1875, 1, 0.875, 0.8125);
    protected final static VoxelShape shape_E = Shapes.box(0, 0, 0.1875, 0.25, 0.875, 0.8125);
    protected final static VoxelShape[] shapes = new VoxelShape[]{
            shape_S, shape_W, shape_N, shape_E
    };
    public static final EnumProperty<DisplayMode> MODE = EnumProperty.create("display_mode", DisplayMode.class);

    public CalendarBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(MODE, DisplayMode.NORMAL));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(CalendarBlock::new);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(MODE));
    }

    @Override
    protected VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shapes[pState.getValue(FACING).get2DDataValue()];
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityRegistry.calendar_entity_type.get().create(pPos, pState);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (level instanceof ServerLevel) {
                BlockState cycle = state.cycle(MODE);
                level.setBlock(pos, cycle, Block.UPDATE_CLIENTS);
                ((ServerPlayer) player).sendSystemMessage(
                        Component.translatable("info.eclipticseasons.calendar.model",
                                Component.translatable("info.eclipticseasons.calendar.model." + cycle.getValue(MODE).getSerializedName())),
                        true
                );
            }
            return InteractionResult.SUCCESS;
        } else if (level.isClientSide()
                && level.getBlockEntity(pos) instanceof CalendarBlockEntity calendarBlockEntity) {
            Holder<Biome> cropBiome = CropGrowthHandler.getCropBiome(level, pos);
            calendarBlockEntity.setBiome(cropBiome);
            if (!calendarBlockEntity.isInit()) calendarBlockEntity.setInit(true);
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    public enum DisplayMode implements StringRepresentable {
        NORMAL, YEAR, NEXT, DAY, SUB_SEASON, MONTH;

        @Override
        public @NonNull String getSerializedName() {
            return toString().toLowerCase(Locale.ROOT);
        }
    }
}
