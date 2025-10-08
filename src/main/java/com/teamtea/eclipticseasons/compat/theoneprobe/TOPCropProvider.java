package com.teamtea.eclipticseasons.compat.theoneprobe;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import mcjty.theoneprobe.api.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class TOPCropProvider implements IProbeInfoProvider {
    @Override
    public ResourceLocation getID() {
        return EclipticSeasons.rl("crop");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        Block block = blockState.getBlock();
        if (ClientConfig.GUI.agriculturalInformation.get()
                && block != null) {
            List<Component> components = CropGrowthHandler.appendInfo(level,blockState);
            if (!components.isEmpty()) {
                if (player == null
                        || player.isShiftKeyDown()) {
                    components.forEach(iProbeInfo::mcText);
                } else {
                    iProbeInfo.mcText(Component.translatable("hint.jade.plugin_eclipticseasons.crop.show", Minecraft.getInstance().options.keyShift.getKey().getDisplayName().getString()).withStyle(ChatFormatting.GRAY));
                }
            }
        }

        if (iProbeHitData != null && (player == null
                || player.isShiftKeyDown())) {
            if (EclipticSeasonsApi.getInstance().isSnowyBlock(level, blockState, iProbeHitData.getPos())) {
                iProbeInfo.mcText(Component.translatable("hint.jade.plugin_eclipticseasons.snowy_status.snowy"));
            }
        }
    }
}
