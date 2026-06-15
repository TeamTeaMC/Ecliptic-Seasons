package com.teamtea.eclipticseasons.common.block;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.block.base.SimpleHorizontalEntityBlock;
import com.teamtea.eclipticseasons.common.block.base.WallPlacedBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreBlockEntity;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.SimpleVoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SeasonSensorBlock extends SimpleHorizontalEntityBlock {

    public static final EnumProperty<Season> SEASON = EnumProperty.create("season", Season.class, Season.collectValidValues());
    public static final BooleanProperty ON_SIGNAL = BooleanProperty.create("on_signal");
    public static final BooleanProperty AUTO = BooleanProperty.create("auto");
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public final VoxelShape[] SHAPES = new VoxelShape[4];

    public SeasonSensorBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(this.defaultBlockState()
                .setValue(SEASON, Season.SPRING)
                .setValue(ON_SIGNAL, true)
                .setValue(AUTO, false)
                .setValue(POWER, 0)
        );

        VoxelShape base = Shapes.box(
                0, 0,
                0,
                1, 6 / 16.0,
                1
        );

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPES[direction.get2DDataValue()] =
                    SimpleVoxelShapeUtils.rotateVoxelShape(
                            base,
                            Direction.Axis.Y,
                            getRotateYByFacing(defaultBlockState().setValue(FACING, direction))
                    );
        }
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(SEASON, POWER, AUTO, ON_SIGNAL));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(SeasonSensorBlock::new);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        BlockState newState;

        if (player.isShiftKeyDown()) {
            newState = cycleMode(state);
            newState = getNewState(level, newState, pos);

            if (player instanceof ServerPlayer serverPlayer) {
                informMode(newState, serverPlayer);
            }
        } else {
            if (state.getValue(AUTO)) {
                if (player instanceof ServerPlayer serverPlayer) {
                    informAutoLocked(state, serverPlayer);
                }
                return InteractionResult.CONSUME;
            }

            newState = state.cycle(SEASON);
            newState = getNewState(level, newState, pos);

            if (player instanceof ServerPlayer serverPlayer) {
                displaySeason(newState, serverPlayer);
            }
        }

        level.setBlock(pos, newState, Block.UPDATE_ALL);
        level.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS);
        return InteractionResult.CONSUME;
    }

    public static void informMode(BlockState state, ServerPlayer serverPlayer) {
        Component mode = Component.translatable(
                state.getValue(AUTO)
                        ? "block.eclipticseasons.season_sensor.mode.auto"
                        : "block.eclipticseasons.season_sensor.mode.manual"
        );

        Component source = Component.translatable(
                state.getValue(ON_SIGNAL)
                        ? "block.eclipticseasons.season_sensor.source.signal"
                        : "block.eclipticseasons.season_sensor.source.natural"
        );

        serverPlayer.sendSystemMessage(
                Component.translatable("block.eclipticseasons.season_sensor.mode", mode, source),
                true
        );
    }

    public static void informAutoLocked(BlockState state, ServerPlayer player) {
        player.sendSystemMessage(
                Component.translatable("block.eclipticseasons.season_sensor.auto_locked"),
                true
        );
    }

    public static void displaySeason(BlockState state, ServerPlayer player) {
        player.sendSystemMessage(
                Component.translatable(
                        "block.eclipticseasons.season_sensor.detect",
                        state.getValue(SEASON).getTranslation()),
                true
        );
    }

    public static final int AUTO_FLAG = 2;
    public static final int SIGNAL_FLAG = 1;

    public static int getMode(BlockState state) {
        int mode = 0;

        if (state.getValue(AUTO)) {
            mode |= AUTO_FLAG;
        }

        if (state.getValue(ON_SIGNAL)) {
            mode |= SIGNAL_FLAG;
        }

        return mode;
    }

    public static BlockState cycleMode(BlockState state) {
        int mode = getMode(state);
        mode = (mode -1 + 4) % 4;
        return state
                .setValue(AUTO, (mode & AUTO_FLAG) == AUTO_FLAG)
                .setValue(ON_SIGNAL, (mode & SIGNAL_FLAG) == SIGNAL_FLAG);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        updateLevel(level, state, pos);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        if (!level.isClientSide()) {
            updateLevel((ServerLevel) level, state, pos);
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return getNewState(context.getLevel(), super.getStateForPlacement(context), context.getClickedPos());
    }

    private static void updateLevel(ServerLevel level, BlockState state, BlockPos pos) {
        BlockState newState = getNewState(level, state, pos);
        if (newState != state) {
            level.setBlock(pos, newState, Block.UPDATE_ALL);
            level.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS);
        }
    }

    private static BlockState getNewState(Level level, BlockState state, BlockPos pos) {
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        boolean onSignal = state.getValue(ON_SIGNAL);
        Season season = onSignal ? EclipticSeasonsApi.getInstance().getSeasonSignal(level, pos) : solarTerm.getSeason();
        boolean auto = state.getValue(AUTO);
        Season seasonTo = state.getValue(SEASON);

        if (!auto) {
            // if (season != seasonTo) return state;
        } else if (season != seasonTo) {
            seasonTo = season;
            state = state.setValue(SEASON, season);
        }

        if (onSignal) {
            Pair<Season, Integer> currentSeason = GreenHouseCoreBlockEntity.getCurrentSeason(level, pos);
            state = state.setValue(POWER, seasonTo == currentSeason.getFirst() ?
                    currentSeason.getSecond() : 0);
        } else {
            state = state.setValue(POWER, seasonTo == season ? (int) ((solarTerm.ordinal() % 6 + 1) * 5 / 2) : 0);
        }
        return state;
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWER);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}