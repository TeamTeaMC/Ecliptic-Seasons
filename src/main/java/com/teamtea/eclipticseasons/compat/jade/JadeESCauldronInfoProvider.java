package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
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


public class JadeESCauldronInfoProvider implements IBlockComponentProvider {
    public static JadeESCauldronInfoProvider INSTANCE = new JadeESCauldronInfoProvider();

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        Block block = accessor.getBlock();
        if (block instanceof IceOrSnowCauldronBlock iceOrSnowCauldronBlock) {
            tooltip.add(iceOrSnowCauldronBlock.getTip());
        }
    }


    @Override
    public ResourceLocation getUid() {
        return EclipticSeasons.rl("cauldron");
    }
}
