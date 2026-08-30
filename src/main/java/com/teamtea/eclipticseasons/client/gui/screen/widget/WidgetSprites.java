package com.teamtea.eclipticseasons.client.gui.screen.widget;

import net.minecraft.resources.ResourceLocation;

public record WidgetSprites(
        ResourceLocation enabled,
        ResourceLocation disabled,
        ResourceLocation enabledFocused,
        ResourceLocation disabledFocused
) {
    public WidgetSprites {
        enabled = textureLocation(enabled);
        disabled = textureLocation(disabled);
        enabledFocused = textureLocation(enabledFocused);
        disabledFocused = textureLocation(disabledFocused);
    }

    public WidgetSprites(ResourceLocation sprite) {
        this(sprite, sprite, sprite, sprite);
    }

    public WidgetSprites(
            ResourceLocation sprite,
            ResourceLocation focused
    ) {
        this(sprite, sprite, focused, focused);
    }

    public WidgetSprites(
            ResourceLocation enabled,
            ResourceLocation disabled,
            ResourceLocation focused
    ) {
        this(enabled, disabled, focused, disabled);
    }

    public ResourceLocation get(boolean enabled, boolean focused) {
        if (enabled) {
            return focused ? enabledFocused : this.enabled;
        }
        return focused ? disabledFocused : disabled;
    }

    public static ResourceLocation textureLocation(ResourceLocation sprite) {
        String path = sprite.getPath();

        if (path.startsWith("textures/") && path.endsWith(".png")) {
            return sprite;
        }

        return new ResourceLocation(
                sprite.getNamespace(),
                "textures/gui/sprites/" + path + ".png"
        );
    }
}