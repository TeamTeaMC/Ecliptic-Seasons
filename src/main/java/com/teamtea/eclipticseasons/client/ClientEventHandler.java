package com.teamtea.eclipticseasons.client;


import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.core.ModelManager;
import com.teamtea.eclipticseasons.client.render.ClientRenderer;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.ClientSolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID, value = Dist.CLIENT)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            ClientRenderer.applyEffect(Minecraft.getInstance().gameRenderer, Minecraft.getInstance().player);
        }
    }

    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem) {
            if (CommonConfig.Season.enableCropHumidityControl.get()) {
                if (CropInfoManager.getHumidityCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                    CropHumidityInfo info = CropInfoManager.getHumidityInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                    if (info != null) event.getToolTip().addAll(info.getTooltip());
                }
            }
            if (CommonConfig.Season.enableCrop.get()) {
                if (CropInfoManager.getSeasonCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                    CropSeasonInfo info = CropInfoManager.getSeasonInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                    if (info != null) event.getToolTip().addAll(info.getTooltip());
                }

            }
        }
    }


    @SubscribeEvent
    public static void onLevelUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            ModelManager.clearHeightMap();
            ClientWeatherChecker.unloadLevel(clientLevel);
        }
    }

    @SubscribeEvent
    public static void onLevelEventLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            // synchronized (ModelManager.RegionList) {
            //     ModelManager.RegionList.clear();
            // }

            // ModelManager.quadMap.clear();
            // ModelManager.quadMap_1.clear();

            WeatherManager.createLevelBiomeWeatherList(clientLevel);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
            SolarHolders.createSaveData(clientLevel, ClientSolarDataManager.get(clientLevel));
        }
    }

    // 强制区块渲染
    @SubscribeEvent
    public static void onWorldTick(TickEvent.LevelTickEvent event) {
        if (ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
            if (event.level.isClientSide() && event.phase.equals(TickEvent.Phase.END)) {
                ClientWeatherChecker.tickAllCheck((ClientLevel) event.level);
                if (event.level.getGameTime() % 100 == 0) {
                    var lr = Minecraft.getInstance().levelRenderer;
                    if (lr != null) {
                        //
                        // ((ClientChunkCache) event.level.getChunkSource()).storage.
                        if (Minecraft.getInstance().cameraEntity instanceof Player player) {
                            BlockPos pos = player.getOnPos();
                            SectionPos sectionPos = SectionPos.of(pos);
                            // lr.setSectionDirtyWithNeighbors(sectionPos.x(),sectionPos.y(),sectionPos.z());
                            int x = sectionPos.x();
                            int y = sectionPos.y();
                            int z = sectionPos.z();
                            for (int i = x - 2; i <= x + 2; ++i) {
                                for (int j = z - 2; j <= z + 2; ++j) {
                                    for (int k = y - 1; k <= y + 1; ++k) {
                                        lr.setSectionDirty(j, k, i);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        var level = Minecraft.getInstance().level;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS
                && level != null
                && SimpleUtil.getNowSolarTerm(level).getSeason() == Season.SPRING
                && SimpleUtil.isDay(level)) {
            var multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            // var itr = Minecraft.getInstance().getItemRenderer();
            // var mds = itr.getItemModelShaper();
            // var stack = Items.ACACIA_BOAT.getDefaultInstance();

            var cameraEntity = Minecraft.getInstance().cameraEntity;
            // var blockpos4 = Minecraft.getInstance().cameraEntity.blockPosition();
            var blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            var random = level.getRandom();
            int b = 32;
            random = RandomSource.create();
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
                            BlockState blockstate = event.getLevelRenderer().level.getBlockState(blockpos$mutableblockpos);

                            if (blockstate.is(BlockTags.FLOWERS)) {


                                var blockpos4 = blockpos$mutableblockpos;

                                Vec3 vec3c = Vec3.atCenterOf(blockpos4).add(-0.5f, -0.5f + 0.25f, -0.5f);
                                vec3c.add(blockstate.getOffset(level, blockpos$mutableblockpos));

                                var state = Blocks.CAMPFIRE.defaultBlockState();
                                Vec3 vec3 = event.getCamera().getPosition();
                                double d0 = vec3.x();
                                double d1 = vec3.y();
                                double d2 = vec3.z();

                                var poseStack = event.getPoseStack();
                                poseStack.pushPose();
                                // poseStack.scale(0.25f, 0.25f, 0.25f);
                                poseStack.translate((double) vec3c.x() - d0, (double) vec3c.y() - d1, (double) vec3c.z() - d2);
                                // poseStack.scale(0.25f, 0.25f, 0.25f);
                                // poseStack.translate(2f, (double) vec3c.y() - d1, 2f);
                                // ((ModelPart)Minecraft.getInstance().getEntityModels().roots.entrySet().toArray()[9].value.bakeRoot()).render();
                                var rs = ModelManager.butterfly1;
                                if (random.nextBoolean()) {
                                    rs = ModelManager.butterfly2;
                                } else if (random.nextBoolean()) {
                                    rs = ModelManager.butterfly3;
                                }

                                int ii;
                                if (level != null) {
                                    ii = LevelRenderer.getLightColor(level, blockpos$mutableblockpos);
                                } else {
                                    ii = 15728880;
                                }
                                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                                        event.getPoseStack().last(),
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
