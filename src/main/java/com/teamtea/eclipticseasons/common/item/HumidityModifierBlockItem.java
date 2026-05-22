package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.common.block.HumidityTankBlock;
import com.teamtea.eclipticseasons.common.block.base.SimpleHumidityProviderBlock;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;

import java.util.List;
import java.util.function.Consumer;

public class HumidityModifierBlockItem extends BlockItem {
    public HumidityModifierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;
        if (getBlock() instanceof SimpleHumidityProviderBlock shpb)
            tooltipComponents.add(Component.translatable(
                            shpb instanceof HumidityTankBlock ? "info.eclipticseasons.humidity_tank.use" : "info.eclipticseasons.dehumidifier.use",
                            shpb.getHumidityModifiedRange(), (int) (shpb.getHumidityModifiedLevel() / Humidity.collectValues().length * 100F))
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

    }
}