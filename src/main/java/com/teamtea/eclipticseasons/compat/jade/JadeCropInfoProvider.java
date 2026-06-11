package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.render.worldui.GrowthInfoClientCache;
import com.teamtea.eclipticseasons.client.render.worldui.state.GrowthWorldUiState;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.item.info.GrowthInfo;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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
                        || accessor.getPlayer().isShiftKeyDown()
                ) {
                    tooltip.addAll(components);

                    if (CompatModule.CommonConfig.showCropGrowthInfoInProbe.get()) {
                        GrowthInfo info = GrowthInfoClientCache.get(accessor.getLevel(), accessor.getPosition(), accessor.getBlockState());
                        if (info != null && info.growChance() < 1) {
                            GrowthWorldUiState from = GrowthWorldUiState.from(info);
                            tooltip.addAll(from.lines());
                        }
                    }

                } else if (config.get(JadeCompact.SHIFT_HINT)) {
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
        // IWailaConfig.get().getGeneral().getDisplayMode()
        // ClientProxy.isShowDetailsPressed()

    }


    @Override
    public Identifier getUid() {
        return EclipticSeasons.rl("crop");
    }

    @Override
    public int getDefaultPriority() {
        return 1000;
    }
}
