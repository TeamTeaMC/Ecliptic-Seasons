package com.teamtea.eclipticseasons.client;


import com.mojang.blaze3d.vertex.*;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.map.ClientMapFixer;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.render.chunk.CompilerCollector;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.ClientSolarDataManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.ServerConfig;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddSectionGeometryEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.awt.*;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID, value = Dist.CLIENT)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void onRenderTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null) {
            WorldRenderer.applyEffect(Minecraft.getInstance().gameRenderer, Minecraft.getInstance().player);
        }
    }

    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() instanceof BlockItem) {
            if (ClientConfig.GUI.agriculturalInformation.getAsBoolean()) {
                if (ServerConfig.Crop.enableCropHumidityControl.get()) {
                    if (CropInfoManager.getHumidityCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                        CropHumidityInfo info = CropInfoManager.getHumidityInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                        if (info != null) event.getToolTip().addAll(info.getTooltip());
                    }
                }
                if (ServerConfig.Crop.enableCrop.get()) {
                    if (CropInfoManager.getSeasonCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                        CropSeasonInfo info = CropInfoManager.getSeasonInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                        if (info != null) event.getToolTip().addAll(info.getTooltip());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            ClientMapFixer.clearChunk(event.getChunk().getPos());
            CompilerCollector.clearChunk(event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onLevelUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            ClientCon.useLevel = null;
            ClientWeatherChecker.unloadLevel(clientLevel);
            ClientMapFixer.clearAll();
            CompilerCollector.clearAll();
        }
    }

    @SubscribeEvent
    public static void onLevelEventLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {

            ClientCon.useLevel = clientLevel;

            // ModelManager.quadMap.clear();
            // ModelManager.quadMap_1.clear();

            WeatherManager.createLevelBiomeWeatherList(clientLevel);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
            SolarHolders.createSaveData(clientLevel, ClientSolarDataManager.get(clientLevel));

        }
    }

    private static long lastFreshTime = -1;
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
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
                            if (event.getLevel().getRandom().nextInt(2) == 0) {
                                WorldRenderer.setAllDirty(sectionPos);
                            }
                        }
                    }

                }
            }
        }


    }


    public static float prevFogDensity = -1f;
    public static long prevFogTick = -1L;

    public static float r = 0.0f;
    public static float g = 0.0f;
    public static float b = 0.0f;

    @SubscribeEvent
    public static void onFogEvent(ViewportEvent.ComputeFogColor event) {
        if (true) return;
        WorldRenderer.renderFogColors(event.getCamera(), (float) event.getPartialTick(), event);
    }

    @SubscribeEvent
    public static void onFogEvent(ViewportEvent.RenderFog event) {
        if (true) return;
        WorldRenderer.renderFogDensity(event.getCamera(), event);
    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (true) return;
        var level = Minecraft.getInstance().level;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS
                && level != null
                && ClientCon.nowSolarTerm.getSeason() == Season.SPRING
                && ClientCon.isDay) {
            var multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            var blockpos4 = new BlockPos(141, -59, 220);

            // Vec3 vec3c = new Vec3(cameraEntity.xo - 0.5f, cameraEntity.yOld , cameraEntity.zo - 0.5f);
            Vec3 vec3c = blockpos4.getCenter().add(0.5f, -0.5f, 0.5f);

            var state = Blocks.CAMPFIRE.defaultBlockState();
            var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            Vec3 vec3 = event.getCamera().getPosition();
            double d0 = vec3.x();
            double d1 = vec3.y();
            double d2 = vec3.z();

            var poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate((double) vec3c.x() - d0, (double) vec3c.y() - d1, (double) vec3c.z() - d2);

            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                    event.getPoseStack().last(),
                    multiBufferSource.getBuffer(RenderType.cutoutMipped()), null,
                    model,
                    1f, 1f, 1f, event.getRenderTick(), OverlayTexture.NO_OVERLAY
            );

            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onAddSectionGeometryEvent(AddSectionGeometryEvent event) {
        if (true) return;

        event.addRenderer(context -> {


            var type = ItemBlockRenderTypes.getRenderLayer(Fluids.WATER.defaultFluidState());
            VertexConsumer buffer = context.getOrCreateChunkBuffer(RenderType.cutoutMipped());
            PoseStack poseStack = context.getPoseStack();


            poseStack.pushPose();

            var pos = event.getSectionOrigin();
            TextureAtlasSprite still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(IClientFluidTypeExtensions.of(Fluids.WATER).getStillTexture());
            still = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(EclipticSeasons.rl("block/snow_overlay_2"));
            int color = IClientFluidTypeExtensions.of(Fluids.WATER).getTintColor();
            color = Color.WHITE.getRGB();

            int r = color >> 16 & 0xFF;
            int g = color >> 8 & 0xFF;
            int b = color & 0xFF;
            int a = color >> 24 & 0xFF;

            float height = 1f;


            int light = 15728880;
            light = LevelRenderer.getLightColor(context.getRegion(), pos);
            // light=LevelRenderer.getLightColor(context.getRegion(), BlockPos.ZERO);;
            light = 15728880;
            //
            // Minecraft.getInstance().getBlockRenderer().renderLiquid(pos, context.getRegion(), buffer, Blocks.WATER.defaultBlockState(), Blocks.WATER.defaultBlockState().getFluidState());


            // buffer.addVertex(poseStack.last().pose(), 0, 1, 1).setColor(r, g, b, a).setUv(still.getU0(), still.getV0()).setLight(light).setNormal(1.0F, 0, 0);
            // buffer.addVertex(poseStack.last().pose(), 0, 0, 1).setColor(r, g, b, a).setUv(still.getU0(), still.getV1()).setLight(light).setNormal(1.0F, 0, 0);
            // buffer.addVertex(poseStack.last().pose(), 1, 0, 1).setColor(r, g, b, a).setUv(still.getU1(), still.getV1()).setLight(light).setNormal(1.0F, 0, 0);
            // buffer.addVertex(poseStack.last().pose(), 1, 1, 1).setColor(r, g, b, a).setUv(still.getU1(), still.getV0()).setLight(light).setNormal(1.0F, 0, 0);
            //
            // buffer.addVertex(poseStack.last().pose(), 0, 1, 0).setColor(r, g, b, a).setUv(still.getU0(), still.getV0()).setLight(light).setNormal(1.0F, 0, 0);
            // buffer.addVertex(poseStack.last().pose(), 0, 0, 0).setColor(r, g, b, a).setUv(still.getU0(), still.getV1()).setLight(light).setNormal(1.0F, 0, 0);
            // buffer.addVertex(poseStack.last().pose(), 1, 0, 0).setColor(r, g, b, a).setUv(still.getU1(), still.getV1()).setLight(light).setNormal(1.0F, 0, 0);
            // buffer.addVertex(poseStack.last().pose(), 1, 1, 0).setColor(r, g, b, a).setUv(still.getU1(), still.getV0()).setLight(light).setNormal(1.0F, 0, 0);

            if (!context.getRegion().getBlockState(pos).isEmpty())
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                        poseStack.last(),
                        buffer,
                        null,
                        Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(EclipticSeasons.ModContents.snowyLeaves.get().defaultBlockState())),
                        1, 1, 1,
                        light, 0, ModelData.EMPTY, RenderType.cutoutMipped()
                );

            poseStack.popPose();


        });
    }
}
