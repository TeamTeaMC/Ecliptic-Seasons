package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.api.ui.IElementHelper;

import java.util.List;


public class CropInfoProvider implements IBlockComponentProvider {
    public static CropInfoProvider INSTANCE = new CropInfoProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        Block block = accessor.getBlock();
        if (ClientConfig.GUI.agriculturalInformation.get()
                && block != null) {
            List<Component> components = CropInfoManager.appendInfo(block);
            if (!components.isEmpty()) {
                if (accessor.getPlayer() == null
                        || accessor.getPlayer().isShiftKeyDown()) {
                    tooltip.addAll(components);
                } else if(config.get(JadeCompact.SHIFT_HINT)){
                    tooltip.add(Component.translatable("hint.jade.plugin_eclipticseasons.crop.show"));
                }
            }
        }
    }


    @Override
    public ResourceLocation getUid() {
        return EclipticSeasons.rl("crop");
    }

    @Override
    public int getDefaultPriority() {
        return 5000;
    }
}
