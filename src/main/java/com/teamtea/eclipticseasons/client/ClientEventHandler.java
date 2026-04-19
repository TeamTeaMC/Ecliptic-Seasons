package com.teamtea.eclipticseasons.client;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.event.SolarTermChangeEvent;
import com.teamtea.eclipticseasons.api.misc.client.IBiomeColorHolder;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.render.chunk.IceKeeper;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.common.core.biome.BiomeClimateManager;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.NaturalPlantHandler;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowChecker;
import com.teamtea.eclipticseasons.common.game.AnimalHooks;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.color.season.BiomeColorsHandler;
import com.teamtea.eclipticseasons.client.core.ClientWeatherChecker;
import com.teamtea.eclipticseasons.client.render.WorldRenderer;
import com.teamtea.eclipticseasons.client.render.chunk.CompilerCollector;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.common.core.solar.extra.ClientSolarDataManager;
import com.teamtea.eclipticseasons.common.misc.MapExporter;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.crop.CropHumidityInfo;
import com.teamtea.eclipticseasons.config.ESConfigSync;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

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
    public static void onSolarTermChangeEvent(SolarTermChangeEvent event) {
        if (event.getLevel().isClientSide()) {
            for (Biome biome : event.getLevel().registryAccess().lookupOrThrow(Registries.BIOME)) {
                if (((Object) biome) instanceof IBiomeColorHolder colorHolder) colorHolder.setSeasonChanged();
            }
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
            CompilerCollector.clearAll();
            IceKeeper.clearAll();
        }
    }

    @SubscribeEvent
    public static void onPlayerExit(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Minecraft.getInstance().player != null) {
            CropGrowthHandler.clearOnClientExitOrServerClose();
            NaturalPlantHandler.clearOnClientExitOrServerClose();
            BiomeClimateManager.clearOnClientExitOrServerClose(false);
            SnowChecker.clearOnClientExitOrServerClose();
            ClientRef.onClientPlayerExit();
            ClientCon.onClientPlayerExit();
            ESSortInfo.clearOnClientExitOrServerClose();
        }

        ESConfigSync.INSTANCE.onClientPlayerExit();
    }

    @SubscribeEvent
    public static void onLevelEventLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ClientLevel level) {
            if (CommonConfig.Season.validDimensions.get().contains(level.dimension().identifier().toString()))
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

            if ((!EclipticUtil.canSnowyBlockInteract() || ClientConfig.Renderer.enhancementChunkRenderUpdate.get())
                    && ClientConfig.Renderer.forceChunkRenderUpdate.get()) {
                if (clientLevel.getGameTime() - lastFreshTime > 80
                        || clientLevel.getGameTime() < lastFreshTime - 1) {
                    lastFreshTime = clientLevel.getGameTime();
                    if (Minecraft.getInstance().getCameraEntity() instanceof Player player) {
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
    public static void onAddSectionGeometryEvent(AddSectionGeometryEvent event) {
        if (true) return;
    }

    @SubscribeEvent
    public static void onRegisterClientCommandsEvent(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        for (String modid : EclipticSeasonsApi.MODID_LIST) {
            dispatcher.register(Commands.literal(modid)
                    .then(Commands.literal("c_export")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                            .then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((stackCommandContext) ->
                                    MapExporter.exportMap(stackCommandContext.getSource(), BlockPosArgument.getBlockPos(stackCommandContext, "pos"))))
                    )
                    .then(Commands.literal("debug")
                            .then(Commands.literal("info_hud")
                                    .then(Commands.argument("info_enable", BoolArgumentType.bool()).executes((stackCommandContext) -> {
                                        ClientConfig.Debug.debugInfo.set(BoolArgumentType.getBool(stackCommandContext, "info_enable"));
                                        return 0;
                                    }))
                            )
                    )
            );
        }
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
            ClientRef.updateClientSide(tagsUpdatedEvent.getLookupProvider());
        }
    }


}
