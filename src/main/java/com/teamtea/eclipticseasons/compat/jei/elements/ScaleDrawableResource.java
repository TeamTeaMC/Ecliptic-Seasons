package com.teamtea.eclipticseasons.compat.jei.elements;

import mezz.jei.common.gui.elements.DrawableResource;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;

public class ScaleDrawableResource extends DrawableResource {

    protected final float scale;

    public ScaleDrawableResource(Identifier Identifier, int u, int v, int width, int height, int textureWidth, int textureHeight, float scale) {
        super(Identifier, u, v, width, height, 0, 0, 0, 0, textureWidth, textureHeight);
        this.scale = scale;
    }

    @Override
    public void draw(GuiGraphicsExtractor gui, int x, int y) {
        gui.pose().pushMatrix();
        gui.pose().translate(x, y);
        gui.pose().translate(7, 7);
        gui.pose().scale(scale, scale);
        super.draw(gui, 0, 0);

        gui.pose().popMatrix();
    }
}
