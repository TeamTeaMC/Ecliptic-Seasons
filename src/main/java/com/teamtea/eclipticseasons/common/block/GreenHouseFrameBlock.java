package com.teamtea.eclipticseasons.common.block;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.block.base.SimpleEntityBlock;
import com.teamtea.eclipticseasons.common.registry.BlockEntityRegistry;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
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
        if (stack.getItem() == ItemRegistry.seasonal_prayer_scroll_item.get()) {
            if (!level.isClientSide() && tryActivate(level, pos)) {
                if (!player.isCreative()) stack.shrink(1);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        } else if (stack.getItem() == ItemRegistry.spring_greenhouse_essence_item.get()) {
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

    private static boolean tryActivate(Level level, BlockPos pos) {
        //if (CommonConfig.Debug.disableSeasonalPrayerRitual.get()) return false;

        Season agroSeason = EclipticSeasonsApi.getInstance().getAgroSeason(level, pos);
        Block block = switch (agroSeason) {
            case SPRING -> BlockRegistry.spring_greenhouse_core.get();
            case SUMMER -> BlockRegistry.summer_greenhouse_core.get();
            case AUTUMN -> BlockRegistry.autumn_greenhouse_core.get();
            case WINTER -> BlockRegistry.winter_greenhouse_core.get();
            default -> null;
        };

        if (block instanceof GreenHouseCoreBlock greenHouseCoreBlock) {
            level.playSound(null, pos, SoundEvents.SMALL_AMETHYST_BUD_PLACE, SoundSource.BLOCKS);
            level.setBlockAndUpdate(pos, block.defaultBlockState().setValue(GreenHouseCoreBlock.AGE, 0));

            {

                double centerX = pos.getX() + 0.5;
                double centerY = pos.getY() + 1.0;
                double centerZ = pos.getZ() + 0.5;

                Integer color = greenHouseCoreBlock.getSeason().getColor().getColor();
                float r = FastColor.ARGB32.red(color) / 255.0F;
                float g = FastColor.ARGB32.green(color) / 255.0F;
                float b = FastColor.ARGB32.blue(color) / 255.0F;
                var particle = ColorParticleOption.create(ParticleRegistry.FLYING_BLOOM, r, g, b);

                double time = (level.getGameTime() % 360) / 10.0;
                int count = 8;
                double radius = 0.5;

                for (int i = 0; i < count; i++) {

                    double angle = Math.toRadians(i * (360.0 / count)) + time * 0.1;
                    double offsetX = Math.cos(angle) * radius;
                    double offsetZ = Math.sin(angle) * radius;
                    double y = centerY + Math.sin(time * 0.05 + i * 0.5) * 0.1;


                    if (level instanceof ServerLevel serverLevel)
                        serverLevel.sendParticles(particle,
                                centerX + offsetX, y, centerZ + offsetZ,
                                1,
                                0.0D, 0.0D, 0.0D, 0.0D
                        );
                    else level.addParticle(particle, centerX + offsetX, y, centerZ + offsetZ, 0, -0.5D, 0);

                }
            }
        }
        return block != null;
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        if (level instanceof ServerLevel && entity instanceof ItemEntity itemEntity
                && itemEntity.getItem().getItem() == ItemRegistry.seasonal_prayer_scroll_item.get()
                && tryActivate(level, pos)) {
            itemEntity.getItem().shrink(1);
        }
    }


}
