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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HumidityModifierBlockItem extends BlockItem {
    public HumidityModifierBlockItem(Block block, Properties properties) {
        super(block, properties);
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;
        if (getBlock() instanceof SimpleHumidityProviderBlock shpb)
            pTooltip.add(Component.translatable(
                            shpb instanceof HumidityTankBlock ? "info.eclipticseasons.humidity_tank.use" : "info.eclipticseasons.dehumidifier.use",
                            shpb.getHumidityModifiedRange(), (int) (shpb.getHumidityModifiedLevel() / Humidity.collectValues().length * 100F))
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));

    }
}
