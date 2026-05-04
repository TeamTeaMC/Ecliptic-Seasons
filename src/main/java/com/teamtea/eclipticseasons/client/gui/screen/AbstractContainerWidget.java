package com.teamtea.eclipticseasons.client.gui.screen;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;


public abstract class AbstractContainerWidget extends AbstractWidget implements ContainerEventHandler {
    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;

    public AbstractContainerWidget(int p_313730_, int p_313819_, int p_313847_, int p_313718_, Component p_313894_) {
        super(p_313730_, p_313819_, p_313847_, p_313718_, p_313894_);
    }

    @Override
    public final boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public final void setDragging(boolean p_313698_) {
        this.isDragging = p_313698_;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener p_313725_) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (p_313725_ != null) {
            p_313725_.setFocused(true);
        }

        this.focused = p_313725_;
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent p_313949_) {
        return ContainerEventHandler.super.nextFocusPath(p_313949_);
    }

    @Override
    public boolean mouseClicked(double p_313764_, double p_313832_, int p_313688_) {
        return ContainerEventHandler.super.mouseClicked(p_313764_, p_313832_, p_313688_);
    }

    @Override
    public boolean mouseReleased(double p_313886_, double p_313935_, int p_313751_) {
        return ContainerEventHandler.super.mouseReleased(p_313886_, p_313935_, p_313751_);
    }

    @Override
    public boolean mouseDragged(double p_313749_, double p_313887_, int p_313839_, double p_313844_, double p_313686_) {
        return ContainerEventHandler.super.mouseDragged(p_313749_, p_313887_, p_313839_, p_313844_, p_313686_);
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean p_313936_) {
        ContainerEventHandler.super.setFocused(p_313936_);
    }
}
