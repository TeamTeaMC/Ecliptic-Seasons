package com.teamtea.eclipticseasons.client;


import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.CommandDispatcher;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.event.stub.SeasonalLevelLoadEvent;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.client.render.worldui.GrowthInfoClientCache;
import com.teamtea.eclipticseasons.client.render.worldui.GrowthWorldUiRenderer;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.core.solar.extra.SpecialDaysManager;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import com.teamtea.eclipticseasons.common.registry.BlockRegistry;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.render.chunk.CompilerCollector;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.ClientSolarDataManager;
import com.teamtea.eclipticseasons.common.misc.MapExporter;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.awt.*;

@EventBusSubscriber(modid = EclipticSeasonsApi.MODID, value = Dist.CLIENT)
public final class ClientEventHandler {

    @SubscribeEvent
    public static void onRenderTick(ClientTickEvent.Post event) {
        if (Minecraft.getInstance().player != null) {
            WorldRenderer.applyEffect(Minecraft.getInstance().gameRenderer, Minecraft.getInstance().player);
        }
        if (BiomeColorsHandler.needRefresh) {
            BiomeColorsHandler.reloadColors();
        }

    }

    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (ClientConfig.GUI.agriculturalInformation.get()) {
            if (event.getItemStack().getItem() instanceof BlockItem blockItem) {
                event.getToolTip().addAll(CropGrowthHandler.appendInfo(
                        event.getContext().level(),
                        blockItem.getBlock().defaultBlockState()));
            } else {
                event.getToolTip().addAll(CropInfoManager.appendInfo(event.getItemStack().getItem()));
            }
            if (event.getItemStack().getItem() instanceof SpawnEggItem blockItem) {
                event.getToolTip().addAll(AnimalHooks.getBreedInfo(
                        blockItem.getType(event.getItemStack())));
            }
        }
    }

    public static class ccc implements ClientTooltipComponent {

        public ccc(mccc ccc) {
        }

        @Override
        public int getHeight() {
            return 18;
        }

        @Override
        public int getWidth(Font font) {
            return 16;
        }

        @Override
        public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
            guiGraphics.renderItem(Items.APPLE.getDefaultInstance(), x + 0, y + 1, 0);
        }
    }


    public static class mccc implements TooltipComponent {
        public mccc() {

        }
    }

    @SubscribeEvent
    public static void addTooltips(RenderTooltipEvent.GatherComponents event) {

        // https://codepen.io/devbobcorn/full/YzZMZvV
        if (event.getItemStack().getItem() instanceof BlockItem) {
            if (ClientConfig.GUI.agriculturalInformation.getAsBoolean()) {
                if (CommonConfig.Crop.enableCropHumidityControl.get()) {
                    if (CropInfoManager.getHumidityCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                        CropHumidityInfo info = CropInfoManager.getHumidityInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                        if (info != null) {

                            // event.getTooltipElements().add(event.getTooltipElements().size()-1,Either.right(new mccc()));

                        }
                    }
                }
                if (CommonConfig.Crop.enableCrop.get()) {
                    if (CropInfoManager.getSeasonCrops().contains(((BlockItem) event.getItemStack().getItem()).getBlock())) {
                        CropSeasonInfo info = CropInfoManager.getSeasonInfo(((BlockItem) event.getItemStack().getItem()).getBlock());
                        if (info != null) {

                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnloadEvent(ChunkEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            CompilerCollector.clearChunk(event.getChunk().getPos());
        }
    }

    @SubscribeEvent
    public static void onLevelUnloadEvent(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            ClientCon.setUseLevel(null);
            ClientWeatherChecker.unload(clientLevel);
            CompilerCollector.clearAll();
            IceKeeper.clearAll();
            GrowthInfoClientCache.clear();
        }
    }

    @SubscribeEvent
    public static void onPlayerExit(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Minecraft.getInstance().player != null) {
            CropGrowthHandler.clearOnClientExitOrServerClose();
            NaturalPlantHandler.clearOnClientExitOrServerClose();
            BiomeClimateManager.clearOnClientExitOrServerClose(false);
            SpecialDaysManager.clearOnClientExitOrServerClose(false);
            SnowChecker.clearOnClientExitOrServerClose();
            ClientRef.onClientPlayerExit();
            ClientCon.onClientPlayerExit();
            ESSortInfo.clearOnClientExitOrServerClose();
        }

        ESConfigSync.INSTANCE.onClientPlayerExit();
    }

    @SubscribeEvent
    public static void onLevelEventLoad(SeasonalLevelLoadEvent event) {
        if (event.getLevel() instanceof ClientLevel level) {
            if (CommonConfig.Season.validDimensions.get().contains(level.dimension().location().toString()))
                MapChecker.validDimension.add(level);

            ClientCon.setUseLevel(level);
            ClientCon.tick(level);
            // BiomeColorsHandler.reloadColors();
            // BiomeColorsHandler.needRefresh=true;

            WeatherManager.createLevelBiomeWeatherList(level);
            // 这里需要恢复一下数据
            // 客户端登录时同步天气数据，此处先放入
            SolarHolders.createSaveData(level, ClientSolarDataManager.get(level));

        }
    }

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (event.getEntity().level() instanceof ClientLevel)
            IceKeeper.checkIfPlayerStepInFrozenWater(event.getEntity());
    }

    private static long lastFreshTime = -1;

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ClientLevel clientLevel) {
            ClientCon.tick(clientLevel);
            ClientWeatherChecker.tickLevel(clientLevel);

            if ((!EclipticUtil.canSnowyBlockInteract() || ClientConfig.Renderer.enhancementChunkRenderUpdate.get())
                    && ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
                if (clientLevel.getGameTime() - lastFreshTime > 80
                        || clientLevel.getGameTime() < lastFreshTime - 1) {
                    lastFreshTime = clientLevel.getGameTime();
                    if (Minecraft.getInstance().cameraEntity instanceof Player player) {
                        BlockPos pos = player.getOnPos();
                        SectionPos sectionPos = SectionPos.of(pos);
                        if (!ClientConfig.Renderer.enhancementChunkRenderUpdate.get()) {
                            WorldRenderer.setSectionDirtyWithNeighbors(sectionPos);
                            WorldRenderer.setSectionDirtyRandomly(sectionPos);
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
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }

        GrowthWorldUiRenderer.renderLevelStage(event);
    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent event) {
        if (true) return;
        var level = Minecraft.getInstance().level;
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {

            var multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            // var blockpos4 = new BlockPos(141, -59, 220);
            //
            Entity cameraEntity = Minecraft.getInstance().cameraEntity;
            Vec3 vec3c = new Vec3(cameraEntity.xo - 0.5f, cameraEntity.yOld + 2f, cameraEntity.zo - 0.5f);
            // Vec3 vec3c = blockpos4.getCenter().add(0.5f, -0.5f, 0.5f);

            var state = Blocks.CAMPFIRE.defaultBlockState();
            var model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            Vec3 vec3 = event.getCamera().getPosition();
            double d0 = vec3.x();
            double d1 = vec3.y();
            double d2 = vec3.z();

            var poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate((double) vec3c.x() - d0, (double) vec3c.y() - d1, (double) vec3c.z() - d2);
            poseStack.scale(10, 10, 10);
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
                        Minecraft.getInstance().getModelManager().getModel(BlockModelShaper.stateToModelLocation(BlockRegistry.snowyLeaves.get().defaultBlockState())),
                        1, 1, 1,
                        light, 0, ModelData.EMPTY, RenderType.cutoutMipped()
                );

            poseStack.popPose();


        });
    }

    @SubscribeEvent
    public static void onRegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal(EclipticSeasonsApi.SMODID)
                .then(Commands.literal("c_export")
                        .requires((source) -> source.hasPermission(2))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((stackCommandContext) ->
                                MapExporter.exportMap(stackCommandContext.getSource(), BlockPosArgument.getBlockPos(stackCommandContext, "pos"))))
                )
                .then(Commands.literal("config")
                        .executes(context -> {
                            Minecraft.getInstance().execute(() -> {
                                Minecraft.getInstance().setScreen(new ESModConfigScreen(null));
                            });
                            return 0;
                        })
                )
        );
    }

    @SubscribeEvent
    public static void onLoggingIn(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientCon.ServerName =
                event.getPlayer().connection.getServerData() == null ? "Client" :
                        event.getPlayer().connection.getServerData().name;
        // ClientCon.ServerName=event.getPlayer().connection.getConnection().getRemoteAddress().toString();
    }

    @SubscribeEvent
    public static void onTagsUpdatedEvent(TagsUpdatedEvent tagsUpdatedEvent) {
        if (tagsUpdatedEvent.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED) {
            ClientRef.updateClientSide(tagsUpdatedEvent.getRegistryAccess());

            // Registry<Biome> biomes = tagsUpdatedEvent.getRegistryAccess()
            //         .registryOrThrow(Registries.BIOME);
            // biomes.bindTags(
            //
            // );
        }
    }

    // @SubscribeEvent
    // public static void onRenderLivingEvent(RenderLivingEvent.Pre<Pig, EntityModel<Pig>> event) {
    //     RenderSystem.setShaderColor(0.5f,0.0f,1,0.2f);
    //
    // }
    //
    // @SubscribeEvent
    // public static void onRenderLivingEvent(RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
    //     RenderSystem.setShaderColor(1,1,1,1);
    // }
}
