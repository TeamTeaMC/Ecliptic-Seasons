package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.solar.SolarDataManager;
import com.teamtea.eclipticseasons.common.misc.SimpleVoxelShapeUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HygrometerBlock extends CalendarBlock {

    public final VoxelShape[] shapes1 = new VoxelShape[4];
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public HygrometerBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWER, getPowerFromHumidityLevel(Humidity.AVERAGE.ordinal())));
        VoxelShape base = Shapes.box(1 / 16f, 0, 0.75, 15 / 16f, 11 / 16f, 1);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            shapes1[direction.get2DDataValue()] = SimpleVoxelShapeUtils.rotateVoxelShape(base, Direction.Axis.Y, getRotateYByFacing(defaultBlockState().setValue(FACING, direction)));
        }
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return shapes1[pState.getValue(FACING).get2DDataValue()];
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // super.randomTick(state, level, pos, random);
        if (random.nextInt(30) == 0) {
            updateLevel(level, state, pos);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        updateLevel(level, state, pos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWER));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);
        level.scheduleTick(pos, this, 40);
    }


    private static void updateLevel(ServerLevel level, BlockState state, BlockPos pos) {
        SolarDataManager data = SolarHolders.getSaveData(level);
        if (data != null) {
            BlockPos checkPos = pos;
            float chance = 0;
            for (int i = 0; i < 20; i++) {
                chance += CropGrowthHandler.isInRoom(level, checkPos, level.getBlockState(checkPos), Optional.empty()) ? 1 : 0;
            }
            Humidity humidityAt = EclipticUtil.getHumidityAt(level, checkPos);
            if (chance > 8) {
                humidityAt=humidityAt.cycle(Mth.floor(data.calculateHumidityModification(checkPos)));
            }
            int p = getPowerFromHumidityLevel(humidityAt.ordinal());
            if (state.getValue(POWER) != p) {
                level.setBlock(pos, state.setValue(POWER, p), Block.UPDATE_ALL);
                level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundSource.BLOCKS);
            }
        }
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWER);
    }


    public static int getHumidityLevelFromPower(int power) {
        return Mth.clamp(Math.round(power * 4f / 15f), 0, 4);
    }

    public static int getPowerFromHumidityLevel(int humidityLevel) {
        return Math.round(humidityLevel * 15f / 4f);
    }

}
