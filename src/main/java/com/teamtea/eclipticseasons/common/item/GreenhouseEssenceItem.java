package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.render.item.GreenHouseCoreCoreItemRenderer;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class GreenhouseEssenceItem extends Item {
    public GreenhouseEssenceItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BlockEntityWithoutLevelRenderer object;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if(object==null){
                    object=new GreenHouseCoreCoreItemRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
                }
                return object;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, Level pLevel, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, pLevel, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;

        // if (tooltipFlag.hasShiftDown())
        {
            Season season = this == ItemRegistry.spring_greenhouse_essence_item.get() ? Season.SPRING :
                    this == ItemRegistry.summer_greenhouse_essence_item.get() ? Season.SUMMER :
                            this == ItemRegistry.autumn_greenhouse_essence_item.get() ? Season.AUTUMN :
                                    this == ItemRegistry.winter_greenhouse_essence_item.get() ? Season.WINTER :
                                            Season.NONE;
            tooltipComponents.add(Component.translatable("info.eclipticseasons.greenhouse_essence.source", season.getTranslation().getString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
        }
        // else {
        //     tooltipComponents.add(Component.translatable("info.eclipticseasons.show.shift").withStyle(ChatFormatting.GRAY));
        // }
    }
}
