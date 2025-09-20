package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.util.List;


public class JadeCropInfoProvider implements IBlockComponentProvider {
    public static JadeCropInfoProvider INSTANCE = new JadeCropInfoProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        Block block = accessor.getBlock();
        if (ClientConfig.GUI.agriculturalInformation.get()
                && block != null) {
            List<Component> components = CropGrowthHandler.appendInfo(accessor.getLevel(), accessor.getBlockState());
            if (!components.isEmpty()) {
                if (accessor.getPlayer() == null
                        || accessor.getPlayer().isShiftKeyDown()) {
                    tooltip.addAll(components);
                } else if(config.get(JadeCompact.SHIFT_HINT)){
                    tooltip.add(Component.translatable("hint.jade.plugin_eclipticseasons.crop.show", Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString()));
                }
            }
        }

        if (config.get(JadeCompact.SNOWY_STATUS)) {
            BlockPos position = accessor.getPosition();
            Level level = accessor.getLevel();
            if (EclipticSeasonsApi.getInstance().isSnowyBlock(level, accessor.getBlockState(), position)) {
                tooltip.add(Component.translatable("hint.jade.plugin_eclipticseasons.snowy_status.snowy"));
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
