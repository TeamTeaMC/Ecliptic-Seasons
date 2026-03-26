package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


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
   public Identifier getUid() {
       return EclipticSeasons.rl("cauldron");
   }
}
