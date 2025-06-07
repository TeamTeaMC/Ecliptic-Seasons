package com.teamtea.eclipticseasons.client.render.item;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public class ClientGreenHouseItem implements IClientItemExtensions {

    private final BlockEntityWithoutLevelRenderer BlockEntityWithoutLevelRenderer;

    public ClientGreenHouseItem(@NotNull BlockEntityWithoutLevelRenderer blockEntityWithoutLevelRenderer) {
        this.BlockEntityWithoutLevelRenderer = blockEntityWithoutLevelRenderer;
    }

    @Override
    public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
        return BlockEntityWithoutLevelRenderer;
    }
}
