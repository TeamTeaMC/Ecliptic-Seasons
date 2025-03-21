package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.client.render.item.ClientGreenHouseItem;
import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreCoreItemRenderer;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class GreenHouseCoreCoreItem extends Item {
    public GreenHouseCoreCoreItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new ClientGreenHouseItem(new GreenHouseCoreCoreItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels())));
    }
}
