package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreBlockEntity;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class GreenHouseCoreBlock extends SimpleEntityBlock {
    public static final IntegerProperty POWER = BlockStateProperties.POWER;

    public static final MapCodec<GreenHouseCoreBlock> CODEC = RecordCodecBuilder.mapCodec(
            blockInstance -> blockInstance.group(
                            ESExtraCodec.SEASON.fieldOf("season").forGetter(GreenHouseCoreBlock::getSeason),
                            propertiesCodec())
                    .apply(blockInstance, GreenHouseCoreBlock::new)
    );

    private final Season season;

    public GreenHouseCoreBlock(Season season, Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(POWER, 0));
        this.season = season;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(POWER));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return BlockEntityRegistry.greenhouse_core_entity_type.get().create(pPos, pState);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (!ClientConfig.Particle.seasonGreenhouse.get()) return;
        int count = ClientConfig.Particle.SeasonGreenhouseParticleSpawnCount.get();

        Direction direction = Direction.DOWN;

        Integer color = getSeason().getColor().getColor();
        float r = FastColor.ARGB32.red(color) / 255.0F;
        float g = FastColor.ARGB32.green(color) / 255.0F;
        float b = FastColor.ARGB32.blue(color) / 255.0F;
        ColorParticleOption colorParticleOption = ColorParticleOption.create(ParticleRegistry.GREENHOUSE, r, g, b);
        for (int i = 0; i < count; i++) {
            double d0 = pos.getX() + (random.nextDouble() * 32.0 - 16.0);
            double d1 = pos.getY() - 0.5 - (random.nextDouble() * 10.0) + 2;
            double d2 = pos.getZ() + (random.nextDouble() * 32.0 - 16.0);

            double d3 = (random.nextDouble() - 0.5) * 0.4;


            // if (random.nextInt(1) == 0)
            {
                double x = d0 + direction.getStepX() * d3;
                double y = d1 + direction.getStepY() * d3;
                double z = d2 + direction.getStepZ() * d3;
                BlockPos blockPos = new BlockPos((int) x, (int) y, (int) z);
                boolean inRoom =
                        level.isEmptyBlock(blockPos) &&
                                CropGrowthHandler.isInRoom(level, blockPos, Blocks.AIR.defaultBlockState(), Optional.empty());
                if (inRoom)
                    level.addParticle(
                            // ParticleTypes.END_ROD,
                            colorParticleOption,
                            x, y, z,
                            random.nextGaussian() * 0.005,
                            0.005 + random.nextDouble() * 0.02,
                            random.nextGaussian() * 0.005
                    );
            }
        }

    }

    public Season getSeason() {
        return season;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level worldIn, BlockState state, BlockEntityType<T> blockEntityType) {
        // return null;
        return !worldIn.isClientSide ?
                createTickerHelper(blockEntityType, BlockEntityRegistry.greenhouse_core_entity_type.get(), GreenHouseCoreBlockEntity::tick) : null;
    }

    @Override
    protected int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        return blockState.getValue(POWER);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            if (level instanceof ServerLevel) {
                level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_BREAK, SoundSource.BLOCKS);
                Item item = switch (getSeason()) {
                    case SPRING -> ItemRegistry.spring_greenhouse_essence_item.get();
                    case SUMMER -> ItemRegistry.summer_greenhouse_essence_item.get();
                    case AUTUMN -> ItemRegistry.autumn_greenhouse_essence_item.get();
                    case WINTER -> ItemRegistry.winter_greenhouse_essence_item.get();
                    default -> Items.AIR;
                };
                Block.popResource(level, pos.above(), new ItemStack(item));
                level.setBlockAndUpdate(pos, BlockRegistry.greenhouse_core_container.get().defaultBlockState());
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

}
