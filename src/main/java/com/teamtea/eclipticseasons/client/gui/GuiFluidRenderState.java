package com.teamtea.eclipticseasons.client.gui;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import org.jspecify.annotations.Nullable;

public record GuiFluidRenderState(
        FluidState state,
        int x0,
        int y0,
        int x1,
        int y1,
        float scale,
        Matrix3x2fc pose,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements PictureInPictureRenderState {

    public GuiFluidRenderState(FluidState state, int x0, int y0, int x1, int y1, float scale,
                               Matrix3x2fc pose, @Nullable ScreenRectangle scissorArea) {
        this(state, x0, y0, x1, y1, scale, pose, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea));
    }
}
