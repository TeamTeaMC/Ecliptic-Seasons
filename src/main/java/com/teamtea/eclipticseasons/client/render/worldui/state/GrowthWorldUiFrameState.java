package com.teamtea.eclipticseasons.client.render.worldui.state;

import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public record GrowthWorldUiFrameState(
        Vec3 cameraPos,
        Quaternionf cameraRotation,
        Vec3 uiPos,
        float yaw,
        GrowthWorldUiState uiState
) {
}
