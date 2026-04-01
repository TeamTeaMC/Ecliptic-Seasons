package com.teamtea.eclipticseasons.client.util;

import com.teamtea.eclipticseasons.client.gui.GuiBlockRenderState;
import com.teamtea.eclipticseasons.client.gui.GuiFluidRenderState;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.NotNull;

public class BlockGuiRenderUtil {
    public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();

    public static void renderBlockInGui(GuiGraphicsExtractor guiGraphics, BlockState state) {
        guiGraphics.submitPictureInPictureRenderState(new GuiBlockRenderState(
                state, 16, 16, 30, 30, 1, PictureInPictureRenderState.IDENTITY_POSE,
                guiGraphics.peekScissorStack()
        ));
    }

    public static void renderBlockInGui(GuiGraphicsExtractor guiGraphics, BlockState state, int x0, int y0) {
        // x0 -= 16;
        // y0 += 8;
        guiGraphics.submitPictureInPictureRenderState(new GuiBlockRenderState(
                state, x0, y0, x0 + 32, y0 + 32, 1f, PictureInPictureRenderState.IDENTITY_POSE,
                guiGraphics.peekScissorStack()
        ));
    }


    public static void renderFluidInGui(@NotNull GuiGraphicsExtractor guiGraphics, FluidState state, int x0, int y0, float v, float v1) {
        guiGraphics.submitPictureInPictureRenderState(new GuiFluidRenderState(
                state, x0, y0, x0 + 32, y0 + 32, 1f, PictureInPictureRenderState.IDENTITY_POSE,
                guiGraphics.peekScissorStack()
        ));
    }
}
