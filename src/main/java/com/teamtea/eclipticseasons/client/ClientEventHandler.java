package com.teamtea.eclipticseasons.client;


import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.core.map.ClientMapFixer;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.solar.ClientSolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.teamtea.eclipticseasons.EclipticSeasons;

@Mod.EventBusSubscriber(modid = EclipticSeasons.MODID, value = Dist.CLIENT)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void onRenderTick(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && Minecraft.getInstance().player != null) {
            WorldRenderer.applyEffect(Minecraft.getInstance().gameRenderer, Minecraft.getInstance().player);
        }
    }


    public static float prevFogDensity = -1f;
    public static long prevFogTick = -1L;

    public static float r = 0.0f;
    public static float g = 0.0f;
    public static float b = 0.0f;

    @SubscribeEvent
    public static void onFogEvent(ViewportEvent.ComputeFogColor event) {
        // ClientRenderer.renderFogColors(event.getCamera(), (float) event.getPartialTick(), event);
    }

    @SubscribeEvent
    public static void onFogEvent(ViewportEvent.RenderFog event) {
        // ClientRenderer.renderFogDensity(event.getCamera(), event);
    }

    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (ClientConfig.GUI.agriculturalInformation.get()
                && event.getItemStack().getItem() instanceof BlockItem) {
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
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            ClientMapFixer.clearChunk(event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onLevelUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            MapChecker.unloadLevel(clientLevel);
            ClientWeatherChecker.unloadLevel(clientLevel);
            ClientMapFixer.clearAll();
            ClientCon.setUseLevel(null);
        }
    }

    // TODO:似乎未真正解决进入末地后更新失败的消息
    @SubscribeEvent
    public static void onLevelEventLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            ClientCon.setUseLevel(clientLevel);
            ClientCon.tick(clientLevel);

            WeatherManager.createLevelBiomeWeatherList(clientLevel);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
            SolarHolders.createSaveData(clientLevel, ClientSolarDataManager.get(clientLevel));
        }
    }

    private static long lastFreshTime = -1;

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.level instanceof ClientLevel clientLevel
                && event.phase.equals(TickEvent.Phase.END)) {
            ClientWeatherChecker.tickAllCheck(clientLevel);
            ClientMapFixer.tick(clientLevel);
            ClientCon.tick(clientLevel);

            if (ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
                if (clientLevel.getGameTime() - lastFreshTime > 80
                        || clientLevel.getGameTime() < lastFreshTime - 1) {
                    lastFreshTime = clientLevel.getGameTime();
                    if (Minecraft.getInstance().cameraEntity instanceof Player player) {
                        BlockPos pos = player.getOnPos();
                        SectionPos sectionPos = SectionPos.of(pos);
                        if (!ClientConfig.Renderer.enhancementChunkRenderUpdate.get()) {
                            WorldRenderer.setSectionDirtyWithNeighbors(sectionPos);
                        } else {
                            if (clientLevel.getRandom().nextInt(2) == 0) {
                                WorldRenderer.setAllDirty(sectionPos);
                            }
                        }
                    }

                }
            }
        }

    }


    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (true) return;
        var level = Minecraft.getInstance().level;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS
                && level != null
                && EclipticUtil.getNowSolarTerm(level).getSeason() == Season.SPRING
                && EclipticUtil.isDay(level)) {
            // var multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            // var itr = Minecraft.getInstance().getItemRenderer();
            // var mds = itr.getItemModelShaper();
            // var stack = Items.ACACIA_BOAT.getDefaultInstance();

            // var cameraEntity = Minecraft.getInstance().cameraEntity;
            // // var blockpos4 = Minecraft.getInstance().cameraEntity.blockPosition();
            // var blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
            // var random = level.getRandom();
            // int b = 32;
            // random = RandomSource.create();
            // for (int i = 0; i < 20; ++i)
            //     for (int j = 0; j < 20; ++j)
            //         for (int k = 0; k < 20; ++k)
            //         // for (int z = 0; z < 667; ++z)
            //         {
            //             blockpos$mutableblockpos.set(
            //                     cameraEntity.xo - 10 + i,
            //                     cameraEntity.yo - 15 + j,
            //                     cameraEntity.zo - 10 + k);
            //             random.setSeed(blockpos$mutableblockpos.asLong());
            //             if (random.nextInt(63) == 0) {
            //                 BlockState blockstate = event.getLevelRenderer().level.getBlockState(blockpos$mutableblockpos);
            //
            //                 if (blockstate.is(BlockTags.FLOWERS)) {
            //
            //
            //                     var blockpos4 = blockpos$mutableblockpos;
            //                     Vec3 vec3c = blockpos4.getCenter().add(-0.5f, -0.5f + 0.25f, -0.5f);
            //                     vec3c.add(blockstate.getOffset(level, blockpos$mutableblockpos));
            //
            //                     var state = Blocks.CAMPFIRE.defaultBlockState();
            //                     Vec3 vec3 = event.getCamera().getPosition();
            //                     double d0 = vec3.x();
            //                     double d1 = vec3.y();
            //                     double d2 = vec3.z();
            //
            //                     var poseStack = event.getPoseStack();
            //                     poseStack.pushPose();
            //                     // poseStack.scale(0.25f, 0.25f, 0.25f);
            //                     poseStack.translate((double) vec3c.x() - d0, (double) vec3c.y() - d1, (double) vec3c.z() - d2);
            //                     // poseStack.scale(0.25f, 0.25f, 0.25f);
            //                     // poseStack.translate(2f, (double) vec3c.y() - d1, 2f);
            //                     // ((ModelPart)Minecraft.getInstance().getEntityModels().roots.entrySet().toArray()[9].value.bakeRoot()).render();
            //                     var rs = ModelManager.butterfly1;
            //                     if (random.nextBoolean()) {
            //                         rs = ModelManager.butterfly2;
            //                     } else if (random.nextBoolean()) {
            //                         rs = ModelManager.butterfly3;
            //                     }
            //
            //                     int ii;
            //                     if (level != null) {
            //                         ii = LevelRenderer.getLightColor(level, blockpos$mutableblockpos);
            //                     } else {
            //                         ii = 15728880;
            //                     }
            //                     Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
            //                             event.getPoseStack().last(),
            //                             multiBufferSource.getBuffer(RenderType.cutoutMipped()), (BlockState) null,
            //                             Minecraft.getInstance().getModelManager().getModel(rs),
            //                             1f, 1f, 1f, ii, OverlayTexture.NO_OVERLAY
            //                     );
            //                     poseStack.popPose();
            //                 }
            //             }
            //         }
        }
    }
}
