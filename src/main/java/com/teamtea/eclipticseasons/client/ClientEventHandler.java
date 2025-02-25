package com.teamtea.eclipticseasons.client;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.core.map.ClientMapFixer;
import com.teamtea.eclipticseasons.client.render.ClientRenderer;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.ClientSolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Random;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID, value = Dist.CLIENT)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ModelManager.SNOW_OVERLAY_CAN_SURVIVE_ON_MAP.clear();
            ModelManager.SNOW_LINE_BIOME_MAP.clear();
            ModelManager.STATE_BLOCK_TYPE_MAP.clear();
        }
    }

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            ClientRenderer.applyEffect(Minecraft.getInstance().gameRenderer, Minecraft.getInstance().player);
        }
    }

    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem) {
            if (CommonConfig.Crop.enableCropHumidityControl.get()) {
                if (CropInfoManager.getHumidityCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                    CropHumidityInfo info = CropInfoManager.getHumidityInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                    if (info != null) event.getToolTip().addAll(info.getTooltip());
                }
            }
            if (CommonConfig.Crop.enableCrop.get()) {
                if (CropInfoManager.getSeasonCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                    CropSeasonInfo info = CropInfoManager.getSeasonInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                    if (info != null) event.getToolTip().addAll(info.getTooltip());
                }

            }
        }
    }
    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        if (event.getWorld() instanceof ClientWorld ) {
            ClientMapFixer.clearChunk(event.getChunk().getPos());
        }
    }


    @SubscribeEvent
    public static void onLevelUnloadEvent(WorldEvent.Unload event) {
        if (event.getWorld() instanceof ClientWorld) {
            ClientMapFixer.clearAll();
            ModelManager.clearHeightMap();
            ClientWeatherChecker.unloadLevel((ClientWorld) event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onLevelEventLoad(WorldEvent.Load event) {
        if (event.getWorld() instanceof ClientWorld) {
            // synchronized (ModelManager.RegionList) {
            //     ModelManager.RegionList.clear();
            // }

            // ModelManager.quadMap.clear();
            // ModelManager.quadMap_1.clear();
            ClientWorld clientLevel = (ClientWorld) event.getWorld();
            WeatherManager.createLevelBiomeWeatherList(clientLevel);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
            SolarHolders.createSaveData(clientLevel, ClientSolarDataManager.get(clientLevel));
        }
    }

    // 强制区块渲染
    @SubscribeEvent
    public static void onWorldTick(TickEvent.ClientTickEvent event) {
        if (ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
            ClientWorld level = Minecraft.getInstance().level;
            if (event.phase.equals(TickEvent.Phase.END) && level != null) {
                ClientWeatherChecker.tickAllCheck(level);
                ClientMapFixer.tick(level);
                if (level.getGameTime() % 100 == 0) {
                    WorldRenderer lr = Minecraft.getInstance().levelRenderer;
                    if (lr != null) {
                        //
                        // ((ClientChunkCache) event.level.getChunkSource()).storage.
                        if (Minecraft.getInstance().player != null) {

                            BlockPos pos = Minecraft.getInstance().player.blockPosition().below();
                            SectionPos sectionPos = SectionPos.of(pos);
                            lr.setSectionDirtyWithNeighbors(sectionPos.x(), sectionPos.y(), sectionPos.z());
                            Random random = level.random;
                            int lastViewDistance = Minecraft.getInstance().options.renderDistance-1;
                            for (int i = 0; i < random.nextInt(8)+4; i++) {
                                {
                                    lr.setSectionDirtyWithNeighbors(sectionPos.x()+2*(random.nextInt(lastViewDistance))-lastViewDistance,
                                            sectionPos.y(),
                                            sectionPos.z()+2*(random.nextInt(lastViewDistance))-lastViewDistance);
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderWorldLastEvent event) {
        ClientWorld level = Minecraft.getInstance().level;
        if (
            // event.getStage() == RenderWorldLastEvent.Stage.AFTER_CUTOUT_BLOCKS
                Minecraft.getInstance().getEntityRenderDispatcher().camera != null
                        && level != null
                        && EclipticUtil.getNowSolarTerm(level).getSeason() == Season.SPRING
                        && EclipticUtil.isDay(level)) {
            IRenderTypeBuffer.Impl multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            // var itr = Minecraft.getInstance().getItemRenderer();
            // var mds = itr.getItemModelShaper();
            // var stack = Items.ACACIA_BOAT.getDefaultInstance();

            Entity cameraEntity = Minecraft.getInstance().cameraEntity;
            // var blockpos4 = Minecraft.getInstance().cameraEntity.blockPosition();
            BlockPos.Mutable blockpos$mutableblockpos = new BlockPos.Mutable();
            Random random = level.getRandom();
            int b = 32;
            random = new Random();
            for (int i = 0; i < 20; ++i)
                for (int j = 0; j < 20; ++j)
                    for (int k = 0; k < 20; ++k)
                    // for (int z = 0; z < 667; ++z)
                    {
                        blockpos$mutableblockpos.set(
                                cameraEntity.xo - 10 + i,
                                cameraEntity.yo - 15 + j,
                                cameraEntity.zo - 10 + k);
                        random.setSeed(blockpos$mutableblockpos.asLong());
                        if (random.nextInt(63) == 0) {
                            BlockState blockstate = Minecraft.getInstance().level.getBlockState(blockpos$mutableblockpos);

                            if (blockstate.is(BlockTags.FLOWERS)) {


                                BlockPos.Mutable blockpos4 = blockpos$mutableblockpos;

                                Vector3d vec3c = Vector3d.atCenterOf(blockpos4).add(-0.5f, -0.5f + 0.25f, -0.5f);
                                vec3c.add(blockstate.getOffset(level, blockpos$mutableblockpos));

                                BlockState state = Blocks.CAMPFIRE.defaultBlockState();
                                Vector3d vec3 = Minecraft.getInstance().getEntityRenderDispatcher().camera.getPosition();
                                double d0 = vec3.x();
                                double d1 = vec3.y();
                                double d2 = vec3.z();

                                MatrixStack poseStack = event.getMatrixStack();
                                poseStack.pushPose();
                                // poseStack.scale(0.25f, 0.25f, 0.25f);
                                poseStack.translate((double) vec3c.x() - d0, (double) vec3c.y() - d1, (double) vec3c.z() - d2);
                                // poseStack.scale(0.25f, 0.25f, 0.25f);
                                // poseStack.translate(2f, (double) vec3c.y() - d1, 2f);
                                // ((ModelPart)Minecraft.getInstance().getEntityModels().roots.entrySet().toArray()[9].value.bakeRoot()).render();
                                ResourceLocation rs = ModelManager.butterfly1;
                                if (random.nextBoolean()) {
                                    rs = ModelManager.butterfly2;
                                } else if (random.nextBoolean()) {
                                    rs = ModelManager.butterfly3;
                                }

                                int ii;
                                if (level != null) {
                                    ii = WorldRenderer.getLightColor(level, blockpos$mutableblockpos);
                                } else {
                                    ii = 15728880;
                                }
                                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                                        event.getMatrixStack().last(),
                                        multiBufferSource.getBuffer(RenderType.cutoutMipped()), (BlockState) null,
                                        Minecraft.getInstance().getModelManager().getModel(rs),
                                        1f, 1f, 1f, ii, OverlayTexture.NO_OVERLAY
                                );


                                // var panda = EntityType.PANDA.create(event.getLevelRenderer().level);
                                // panda.moveTo(new BlockPos(-365,-60,-435),0f,0f);
                                // var livingEntityRenderer = ((LivingEntityRenderer) Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(panda));
                                // PandaModel<Panda> livingEntityRendererModel = (PandaModel<Panda>) livingEntityRenderer.getModel();
                                // // livingEntityRendererModel.setupAnim();
                                // var renderType = livingEntityRendererModel.renderType(livingEntityRenderer.getTextureLocation(panda));
                                // livingEntityRendererModel.setupAnim(panda, event.getPartialTick(), 0.0F, -0.1F, 0.0F, 0.0F);
                                //
                                // livingEntityRendererModel.renderToBuffer(event.getPoseStack(), multiBufferSource.getBuffer(renderType), 15728880, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
                                // // look LivingEntityRenderer

                                poseStack.popPose();
                            }
                        }
                    }
            // var blockpos4=new BlockPos(-365,-60,-435);
            // // Vec3 vec3c = new Vec3(cameraEntity.xo - 0.5f, cameraEntity.yOld , cameraEntity.zo - 0.5f);
            //
            // Vec3   vec3c=blockpos4.getCenter().add(0.5f,-0.5f,0.5f);
            //
            // var state = Blocks.CAMPFIRE.defaultBlockState();
            // var model=Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            // Vec3 vec3 = event.getCamera().getPosition();
            // double d0 = vec3.x();
            // double d1 = vec3.y();
            // double d2 = vec3.z();
            //
            // var poseStack = event.getPoseStack();
            // poseStack.pushPose();
            // // poseStack.scale(0.25f, 0.25f, 0.25f);
            // poseStack.translate((double) vec3c.x() - d0, (double) vec3c.y() - d1, (double) vec3c.z() - d2);
            // // poseStack.scale(0.25f, 0.25f, 0.25f);
            // // poseStack.translate(2f, (double) vec3c.y() - d1, 2f);
            //
            // Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
            //         event.getPoseStack().last(),
            //         multiBufferSource.getBuffer(RenderType.solid()), null,
            //         Minecraft.getInstance().getModelManager().getModel(ModelManager.butterfly),
            //         1f, 1f, 1f, event.getRenderTick(), OverlayTexture.NO_OVERLAY
            // );
            //
            //
            // poseStack.popPose();
        }
    }
}
