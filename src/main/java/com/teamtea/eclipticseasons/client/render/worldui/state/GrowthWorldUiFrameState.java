package com.teamtea.eclipticseasons.client.render.worldui.state;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public record GrowthWorldUiFrameState(
        Vec3 cameraPos,
        Quaternionf cameraRotation,
        Vec3 uiPos,
        float yaw,
        GrowthWorldUiState uiState
) {
    public static final ContextKey<GrowthWorldUiFrameState> GROWTH_UI =
            new ContextKey<>(EclipticSeasons.rl("growth_ui"));
}
