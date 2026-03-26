package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.ItemAbility;
import net.neoforged.neoforge.common.util.FakePlayer;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;


public class BroomItem extends Item {
    public BroomItem(Properties pProperties) {
        super(pProperties);

    }

    public static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
            if (!(player instanceof FakePlayer fakePlayer)) {
                player.startUsingItem(pContext.getHand());
            } else {
                onUseTick(pContext.getLevel(), fakePlayer, pContext.getItemInHand(), this.getUseDuration(pContext.getItemInHand(), player) + 1 - 5);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack pStack) {
        return ItemUseAnimation.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return USE_DURATION;
    }

    @Override
    public void onUseTick(Level level, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (pRemainingUseDuration >= 0) {
            HitResult hitresult = this.calculateHitResult(pLivingEntity);
            double attributeValue = pLivingEntity.getAttributeValue(Attributes.BLOCK_INTERACTION_RANGE);

            if (hitresult instanceof BlockHitResult blockhitresult && hitresult.getType() == HitResult.Type.BLOCK) {
                int remainTicks = this.getUseDuration(pStack, pLivingEntity) - pRemainingUseDuration + 1;
                if (remainTicks % ANIMATION_DURATION == 5) {
                    BlockPos blockpos = blockhitresult.getBlockPos();
                    BlockState blockstate = level.getBlockState(blockpos);
                    BlockPos pickPos = blockpos.above();
                    BlockState pickState = level.getBlockState(pickPos);
                    HumanoidArm humanoidarm = pLivingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND
                            ? pLivingEntity.getMainArm()
                            : pLivingEntity.getMainArm().getOpposite();

                    // boolean shouldSet= MapChecker.shouldSnowAt(level,blockpos,blockstate,level.getRandom(),blockstate.getSeed(blockpos))
                    //         &&MapChecker.getHeight(level,blockpos)==blockpos.getY();
                    boolean shouldSet = EclipticSeasonsApi.getInstance().isSnowyBlock(level, blockstate, blockpos);
                    if (blockstate.shouldSpawnTerrainParticles()
                            && blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                        this.spawnDustParticles(level, blockhitresult, shouldSet ? Blocks.SNOW_BLOCK.defaultBlockState() : blockstate,
                                pLivingEntity.getViewVector(0.0F), humanoidarm);
                    }

                    SoundEvent soundevent;
                    if (blockstate.getBlock() instanceof BrushableBlock brushableblock) {
                        soundevent = brushableblock.getBrushSound();
                    } else {
                        soundevent = SoundEvents.BRUSH_GENERIC;
                    }

                    if (!level.isClientSide() && pickState.is(Blocks.SNOW)) {
                        level.destroyBlock(pickPos, true, pLivingEntity);
                        pStack.hurtAndBreak(pLivingEntity instanceof Player player && player.isCreative() ? 0 : 1, pLivingEntity, pLivingEntity.getUsedItemHand());
                    }

                    level.playSound(pLivingEntity, blockpos, soundevent, SoundSource.BLOCKS, 1f, 1f);

                    if (shouldSet && EclipticUtil.canSnowyBlockInteract()) {
                        if (level instanceof ServerLevel serverLevel) {
                            SnowyMapChecker.removeSnowyStatus(serverLevel, blockpos);
                        } else {
                            ClientCon.agent.setChunkDirty(SectionPos.of(blockpos));
                        }
                    }
                }

                return;
            }

            pLivingEntity.releaseUsingItem();
        } else {
            pLivingEntity.releaseUsingItem();
        }
    }

    private HitResult calculateHitResult(LivingEntity livingEntity) {
        return calculateHitResult2(
                livingEntity, entity -> !entity.isSpectator() && entity.isPickable(),
                livingEntity instanceof Player player ?
                        player.blockInteractionRange() : 4.5f
        );
    }

    private HitResult calculateHitResult2(Entity projectile, Predicate<Entity> filter, double scale) {
        Vec3 vec3 = projectile.getViewVector(0.0F).scale(scale);
        Level level = projectile.level();
        Vec3 vec31 = projectile.getEyePosition();
        return ProjectileUtil.getHitResult(vec31, projectile, filter, vec3, level, 0.0F, ClipContext.Block.OUTLINE);
    }

    private void spawnDustParticles(Level pLevel, BlockHitResult pHitResult, BlockState pState, Vec3 pPos, HumanoidArm pArm) {
        double speed = 3.0;
        int right = pArm == HumanoidArm.RIGHT ? 1 : -1;
        int count = pLevel.getRandom().nextInt(7, 12);
        BlockParticleOption blockparticleoption = new BlockParticleOption(ParticleTypes.BLOCK, pState);
        Direction direction = pHitResult.getDirection();
        DustParticlesDelta brushitem$dustparticlesdelta = DustParticlesDelta.fromDirection(pPos, direction);
        Vec3 vec3 = pHitResult.getLocation();

        for (int k = 0; k < count; k++) {
            pLevel.addParticle(
                    blockparticleoption,
                    vec3.x - (double) (direction == Direction.WEST ? 1.0E-6F : 0.0F),
                    vec3.y,
                    vec3.z - (double) (direction == Direction.NORTH ? 1.0E-6F : 0.0F),
                    brushitem$dustparticlesdelta.xd() * (double) right * speed * pLevel.getRandom().nextDouble(),
                    0.0,
                    brushitem$dustparticlesdelta.zd() * (double) right * speed * pLevel.getRandom().nextDouble()
            );
        }
    }

    record DustParticlesDelta(double xd, double yd, double zd) {
        private static final double ALONG_SIDE_DELTA = 1.0;
        private static final double OUT_FROM_SIDE_DELTA = 0.1;

        public static DustParticlesDelta fromDirection(Vec3 pPos, Direction pDirection) {
            double yd = 0.0;

            return switch (pDirection) {
                case DOWN, UP -> new DustParticlesDelta(pPos.z(), yd, -pPos.x());
                case NORTH -> new DustParticlesDelta(ALONG_SIDE_DELTA, yd, -OUT_FROM_SIDE_DELTA);
                case SOUTH -> new DustParticlesDelta(-ALONG_SIDE_DELTA, yd, OUT_FROM_SIDE_DELTA);
                case WEST -> new DustParticlesDelta(-OUT_FROM_SIDE_DELTA, yd, -ALONG_SIDE_DELTA);
                case EAST -> new DustParticlesDelta(OUT_FROM_SIDE_DELTA, yd, ALONG_SIDE_DELTA);
            };
        }
    }

    @Override
    public boolean canPerformAction(ItemInstance stack, ItemAbility itemAbility) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_BRUSH_ACTIONS.contains(itemAbility);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);

        if (FMLLoader.getCurrent().getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;

        if (!EclipticUtil.canSnowyBlockInteract()) {
            builder.accept(Component.translatable("info.eclipticseasons.config.inactive")
            );
        }
    }

}
