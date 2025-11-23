package com.teamtea.eclipticseasons.compat.jei.elements;

import mezz.jei.common.gui.elements.DrawableResource;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ScaleDrawableResource extends DrawableResource {

    protected final float scale;

    public ScaleDrawableResource(ResourceLocation resourceLocation, int u, int v, int width, int height, int textureWidth, int textureHeight, float scale) {
        super(resourceLocation, u, v, width, height, 0, 0, 0, 0, textureWidth, textureHeight);
        this.scale = scale;
    }

    @Override
    public void draw(GuiGraphics gui, int x, int y) {
        gui.pose().pushPose();
        gui.pose().translate(x, y, 0);
        gui.pose().translate(7, 7, 0);
        gui.pose().scale(scale, scale, 1);
        super.draw(gui, 0, 0);

        gui.pose().popPose();
    }
}
