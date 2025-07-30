package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreItemRenderer;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class GreenhouseCoreBlockItem extends BlockItem {

    public GreenhouseCoreBlockItem(GreenHouseCoreBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, pLevel, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;

        tooltipComponents.add(
                Component.translatable("info.eclipticseasons.greenhouse_core.effect",
                                CommonConfig.Crop.seasonCoreRange.get(),
                                ((GreenHouseCoreBlock) getBlock()).getSeason().getTranslation().getString().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.GRAY)
        );
    }
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer object;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(object==null){
                    object=new GreenHouseCoreItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return object;
            }
        });
    }
}
