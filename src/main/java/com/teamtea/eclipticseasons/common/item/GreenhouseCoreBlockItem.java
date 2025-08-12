package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;
import java.util.Locale;

public class GreenhouseCoreBlockItem extends BlockItem {
    public GreenhouseCoreBlockItem(GreenHouseCoreBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;

        tooltipComponents.add(
                Component.translatable("info.eclipticseasons.greenhouse_core.effect",
                        CommonConfig.Crop.seasonCoreRange.get(),
                        ((GreenHouseCoreBlock) getBlock()).getSeason().getTranslation().getString().toLowerCase(Locale.ROOT))
                        .withStyle(ChatFormatting.GRAY)
        );
    }
}
