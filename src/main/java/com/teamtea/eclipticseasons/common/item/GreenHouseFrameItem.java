package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreFrameItemRenderer;
import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class GreenHouseFrameItem extends BlockItem {


    public GreenHouseFrameItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer object;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(object==null){
                    object=new GreenHouseCoreFrameItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return object;
            }
        });
    }
}
