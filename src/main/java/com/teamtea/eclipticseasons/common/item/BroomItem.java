package com.teamtea.eclipticseasons.common.item;

import com.google.common.collect.Lists;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.BroomUseMessage;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;

public class BroomItem extends Item {
    public BroomItem(Properties pProperties) {
        super(pProperties);

    }

    public static final int ANIMATION_DURATION = 10;
    private static final int USE_DURATION = 200;
    private static final double MAX_DISTANCE = Math.sqrt(ServerGamePacketListenerImpl.MAX_INTERACTION_DISTANCE) - 1.0D;


    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Player player = pContext.getPlayer();
        if (player != null && this.calculateHitResult(player).getType() == HitResult.Type.BLOCK) {
            if (!(player instanceof FakePlayer fakePlayer)) {
                player.startUsingItem(pContext.getHand());
            } else {
                onUseTick(pContext.getLevel(), fakePlayer, pContext.getItemInHand(), this.getUseDuration(pContext.getItemInHand()) + 1 - 5);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BRUSH;
    }


    @Override
    public int getUseDuration(ItemStack pStack) {
        return USE_DURATION;
    }


    @Override
    public void onUseTick(Level level, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
        if (pRemainingUseDuration >= 0) {
            HitResult hitresult = this.calculateHitResult(pLivingEntity);

            if (hitresult instanceof BlockHitResult blockhitresult && hitresult.getType() == HitResult.Type.BLOCK) {
                int remainTicks = this.getUseDuration(pStack) - pRemainingUseDuration + 1;
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
                    if (blockstate.shouldSpawnParticlesOnBreak()
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
                        pStack.hurtAndBreak(pLivingEntity instanceof Player player && player.isCreative() ? 0 : 1, pLivingEntity, (entity) -> {
                            entity.broadcastBreakEvent(pLivingEntity.getUsedItemHand());
                        });
                    }

                    level.playSound(pLivingEntity, blockpos, soundevent, SoundSource.BLOCKS, 1f, 1f);

                    // if(shouldSet&&level.isClientSide()){
                    //     int startY=level.getMaxBuildHeight() + 1;
                    //     MapChecker.updatePosForce(level,blockpos, level.getMaxBuildHeight() + 1);
                    //     SectionPos sectionPos = SectionPos.of(blockpos);
                    //     Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(),sectionPos.y(),sectionPos.z());
                    //     ClientMapFixer.addPlanner(level,blockstate,blockpos,level.getGameTime()+160, startY);
                    // }
                    if (shouldSet) {
                        if (level instanceof ServerLevel serverLevel) {
                            if (CommonConfig.Map.delayedUpdates.get()) {
                                if (level.getRandom().nextInt(4) == 0)
                                    Block.popResource(level, blockpos, Items.SNOWBALL.getDefaultInstance());
                                ServerMapFixer.addPlanner(level,
                                        blockstate,
                                        blockstate, blockpos,
                                        level.getGameTime() + 160,
                                        MapChecker.getHeight(level, blockpos), true);
                            } else {
                                // var distance = level.getServer() instanceof DedicatedServer dedicatedServer ?
                                //         dedicatedServer.getProperties().viewDistance : 64;
                                // distance = distance * distance;
                                // List<ServerPlayer> nearbyPlayers = Lists.newArrayList();
                                // for (Player player : level.players()) {
                                //     if (player instanceof ServerPlayer serverPlayer && !(player instanceof FakePlayer)) {
                                //         if (serverPlayer.blockPosition().distSqr(blockpos) < distance) {
                                //             nearbyPlayers.add(serverPlayer);
                                //         }
                                //     }
                                // }
                                SimpleNetworkHandler.send(serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(blockpos), false)
                                        , new BroomUseMessage(blockpos, level.getGameTime()));                            }
                        } else if (level.isClientSide()) {
                            int startY = level.getMaxBuildHeight() + 1;
                            MapChecker.updatePosForce(level, blockpos, level.getMaxBuildHeight() + 1);
                            SectionPos sectionPos = SectionPos.of(blockpos);
                            Minecraft.getInstance().levelRenderer.setSectionDirty(sectionPos.x(), sectionPos.y(), sectionPos.z());
                            ClientMapFixer.addPlanner(level, blockstate, blockpos, level.getGameTime() + 160, startY);
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
        return ProjectileUtil.getHitResultOnViewVector(livingEntity, (entity) ->
                !entity.isSpectator() && entity.isPickable(), MAX_DISTANCE);
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
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, pLevel, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;

        boolean use = FMLEnvironment.dist == Dist.CLIENT ?
                ClientConfig.Renderer.realisticSnowyChange.get() : false;
        use |= CommonConfig.Map.delayedUpdates.get();

        if (!use) {
            tooltipComponents.add(Component.translatable("info.eclipticseasons.config.inactive")
            );
        }
    }
}
