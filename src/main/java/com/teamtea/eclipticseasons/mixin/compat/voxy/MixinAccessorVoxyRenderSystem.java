package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.teamtea.eclipticseasons.common.mixin.condition.ConditionalMixin;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxyAsyncNodeManager;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxyGeometryCache;
import com.teamtea.eclipticseasons.compat.voxy.client.IVoxySectionUpdateRouter;
import com.teamtea.eclipticseasons.compat.voxy.client.VoxyGeometryRefreshManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import me.cortex.voxy.client.core.gl.GlBuffer;
import me.cortex.voxy.client.core.rendering.GeometryCache;
import me.cortex.voxy.client.core.rendering.SectionUpdateRouter;
import me.cortex.voxy.client.core.rendering.building.RenderGenerationService;
import me.cortex.voxy.client.core.rendering.hierachical.AsyncNodeManager;
import me.cortex.voxy.client.core.rendering.hierachical.NodeCleaner;
import me.cortex.voxy.client.core.rendering.section.geometry.IGeometryData;
import me.cortex.voxy.common.world.WorldEngine;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Reuses an already registered mixin filename so no mixin-json edit is needed.
 */
@Mixin(AsyncNodeManager.class)
@ConditionalMixin(value = "voxy", version = "0.2.14-alpha")
public abstract class MixinAccessorVoxyRenderSystem implements IVoxyAsyncNodeManager {

    @Unique
    private static final int ECLIPTICSEASONS_STABLE_FRAMES = 10;

    @Unique
    private static final long ECLIPTICSEASONS_NOTIFY_INTERVAL_MS = 2000L;

    @Shadow
    @Final
    private SectionUpdateRouter router;

    @Shadow
    @Final
    private GeometryCache geometryCache;

    @Unique
    private RenderGenerationService eclipticseasons$renderService;

    @Unique
    private boolean eclipticseasons$seasonalRefreshPending;

    @Unique
    private int eclipticseasons$refreshPass;

    @Unique
    private int eclipticseasons$stableFrames;

    @Unique
    private int eclipticseasons$lastSectionCount;

    @Unique
    private long eclipticseasons$refreshStartedAt;

    @Unique
    private long eclipticseasons$lastNotificationAt;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void eclipticseasons$register(int maxNodeCount, IGeometryData geometryData,
                                          RenderGenerationService renderService, CallbackInfo ci) {
        eclipticseasons$renderService = renderService;
        VoxyGeometryRefreshManager.register(this);
    }

    @Inject(method = "stop", at = @At("HEAD"))
    private void eclipticseasons$unregister(CallbackInfo ci) {
        VoxyGeometryRefreshManager.unregister(this);
    }

    @Override
    public void eclipticseasons$refreshAllGeometry() {
        eclipticseasons$refreshPass = 1;
        eclipticseasons$stableFrames = 0;
        eclipticseasons$refreshStartedAt = System.currentTimeMillis();
        eclipticseasons$lastNotificationAt = 0L;
        eclipticseasons$seasonalRefreshPending = true;
        ((IVoxyGeometryCache) geometryCache).eclipticseasons$clearForSeasonChange();
        eclipticseasons$lastSectionCount = eclipticseasons$queueCurrentSections();

        eclipticseasons$notifyPlayer("Seasonal LOD refresh started: "
                + eclipticseasons$lastSectionCount + " sections queued.");
    }

    @Override
    public boolean eclipticseasons$canRefreshGeometry() {
        return eclipticseasons$renderService != null
                && !eclipticseasons$seasonalRefreshPending
                && eclipticseasons$renderService.getTaskCount() < 100;
    }

    @Unique
    private int eclipticseasons$queueCurrentSections() {
        LongArrayList positions = ((IVoxySectionUpdateRouter) router).eclipticseasons$getWatchedSections();

        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera != null) {
            int cameraX = camera.getBlockX();
            int cameraZ = camera.getBlockZ();
            positions.sort((left, right) -> Long.compare(
                    eclipticseasons$horizontalDistanceSquared(left, cameraX, cameraZ),
                    eclipticseasons$horizontalDistanceSquared(right, cameraX, cameraZ)
            ));
        }

        for (int i = 0; i < positions.size(); i++) {
            router.triggerRemesh(positions.getLong(i));
        }

        return positions.size();
    }

    @Unique
    private static long eclipticseasons$horizontalDistanceSquared(long sectionPos, int cameraX, int cameraZ) {
        int level = WorldEngine.getLevel(sectionPos);
        long centerX = (((long) WorldEngine.getX(sectionPos) << 5) + 16L) << level;
        long centerZ = (((long) WorldEngine.getZ(sectionPos) << 5) + 16L) << level;
        long deltaX = centerX - cameraX;
        long deltaZ = centerZ - cameraZ;
        return deltaX * deltaX + deltaZ * deltaZ;
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void eclipticseasons$trackSeasonalRefresh(GlBuffer nodeBuffer, NodeCleaner cleaner, CallbackInfo ci) {
        if (!eclipticseasons$seasonalRefreshPending || eclipticseasons$renderService == null) return;

        int queued = eclipticseasons$renderService.getTaskCount();
        boolean nodeWork = ((AsyncNodeManager) (Object) this).hasWork();

        if (queued == 0 && !nodeWork) {
            eclipticseasons$stableFrames++;
        } else {
            eclipticseasons$stableFrames = 0;
        }

        long now = System.currentTimeMillis();
        if (now - eclipticseasons$lastNotificationAt >= ECLIPTICSEASONS_NOTIFY_INTERVAL_MS) {
            eclipticseasons$lastNotificationAt = now;
            eclipticseasons$notifyPlayer("Seasonal LOD refresh pass "
                    + eclipticseasons$refreshPass + "/2: " + queued
                    + " mesh tasks queued, waiting " + (now - eclipticseasons$refreshStartedAt) + " ms.");
        }

        if (eclipticseasons$stableFrames < ECLIPTICSEASONS_STABLE_FRAMES) return;

        eclipticseasons$stableFrames = 0;
        if (eclipticseasons$refreshPass == 1) {
            eclipticseasons$refreshPass = 2;
            eclipticseasons$lastSectionCount = eclipticseasons$queueCurrentSections();
            eclipticseasons$notifyPlayer("Seasonal LOD refresh retry pass: "
                    + eclipticseasons$lastSectionCount + " sections checked.");
            return;
        }

        eclipticseasons$seasonalRefreshPending = false;
        eclipticseasons$notifyPlayer("Seasonal LOD refresh completed in "
                + (now - eclipticseasons$refreshStartedAt) + " ms.");
    }

    @Unique
    private static void eclipticseasons$notifyPlayer(String message) {
        if (!ClientConfig.Debug.debugInfo.get()) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null) {
            minecraft.player.sendSystemMessage(Component.literal("[Ecliptic Seasons/Voxy] ")
                    .withStyle(ChatFormatting.AQUA).append(Component.literal(message)
                            .withStyle(ChatFormatting.GRAY)));
        }
    }
}
