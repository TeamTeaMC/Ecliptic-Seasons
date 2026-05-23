package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;
import java.util.Locale;

public class GreenhouseEssenceItem extends Item {
    public GreenhouseEssenceItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;

        // if (tooltipFlag.hasShiftDown())
        {
            Season season = this == ItemRegistry.spring_greenhouse_essence_item.get() ? Season.SPRING :
                    this == ItemRegistry.summer_greenhouse_essence_item.get() ? Season.SUMMER :
                            this == ItemRegistry.autumn_greenhouse_essence_item.get() ? Season.AUTUMN :
                                    this == ItemRegistry.winter_greenhouse_essence_item.get() ? Season.WINTER :
                                            Season.NONE;
            tooltipComponents.add(Component.translatable("info.eclipticseasons.greenhouse_essence.use", season.getTranslation().getString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
        }
        // else {
        //     tooltipComponents.add(Component.translatable("info.eclipticseasons.show.shift").withStyle(ChatFormatting.GRAY));
        // }
    }
}
