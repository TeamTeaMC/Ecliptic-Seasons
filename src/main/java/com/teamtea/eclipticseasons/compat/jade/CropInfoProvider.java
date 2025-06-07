package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.crop.CropSeasonInfo;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import com.teamtea.eclipticseasons.common.core.crop.CropInfoManager;
import com.teamtea.eclipticseasons.config.ClientConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.InterModComms;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.config.IWailaConfig;
import snownee.jade.util.ClientProxy;

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
                if (accessor.getPlayer() ==null
                        || accessor.getPlayer().isShiftKeyDown()
                ) {
                    tooltip.addAll(components);

                    // Style s1 = Style.EMPTY.withColor(TextColor.fromRgb(-1)).withFont(EclipticSeasons.rl("test"));
                    // Style aDefault = Style.EMPTY.withFont(ResourceLocation.withDefaultNamespace("default"));
                    // MutableComponent mutableComponent = Component.literal(" ").withStyle(aDefault);
                    // tooltip.add(Component.literal("e").withStyle(s1).append(mutableComponent)
                    //         .append(Component.literal("d").withStyle(s1).append(mutableComponent))
                    //         .append(Component.literal("c").withStyle(s1).append(mutableComponent))
                    //         .append(Component.literal("b").withStyle(s1).append(mutableComponent))
                    //         .append(Component.literal("a").withStyle(s1).append(mutableComponent)));
                    //
                    //
                    // CropSeasonInfo seasonInfo = CropInfoManager.getSeasonInfo(block);
                    // MutableComponent component = Component.empty();
                    // for (int i = Season.collectValues().length-1; i >-1; i--) {
                    //     Season season = Season.collectValues()[i];
                    //     if (seasonInfo.isSuitable(season)) {
                    //         if (season.ordinal() * 6 + 3 < SolarTerm.collectValues().length)
                    //             component = SimpleUtil.addSolarIconBefore(SolarTerm.get(season.ordinal() * 6 + 3), component);
                    //     }
                    // }
                    //
                    // tooltip.add(component);

                } else if (config.get(JadeCompact.SHIFT_HINT)) {
                    tooltip.add(Component.translatable("hint.jade.plugin_eclipticseasons.crop.show"));
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
    public ResourceLocation getUid() {
        return EclipticSeasons.rl("crop");
    }

    @Override
    public int getDefaultPriority() {
        return 1000;
    }
}
