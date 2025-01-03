package com.teamtea.eclipticseasons.common.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CalendarBlockItem extends BlockItem {
    public CalendarBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        // var sd = EclipticSeasonsApi.getInstance().getSolarTerm(pContext.level());
        // pTooltipComponents.add(Component.translatable("info.eclipticseasons.environment.solar_term.hint")
        //         .withStyle(ChatFormatting.GRAY));
        // pTooltipComponents.add(sd.getTranslation().withStyle(sd.getSeason().getColor()));
    }

}
