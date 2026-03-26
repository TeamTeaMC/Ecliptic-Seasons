package com.teamtea.eclipticseasons.compat.jade;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import com.teamtea.eclipticseasons.common.block.blockentity.GreenHouseCoreBlockEntity;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;


public class JadeESGreenHouseCoreProvider implements IBlockComponentProvider {
   public static JadeESGreenHouseCoreProvider INSTANCE = new JadeESGreenHouseCoreProvider();

   @Override
   public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
       BlockState blockState = accessor.getBlockState();
       if (accessor.getBlockEntity() instanceof GreenHouseCoreBlockEntity entity
               && !GreenHouseCoreBlock.isPowered(blockState)) {
           tooltip.add(1,GreenHouseCoreBlockEntity.getProgressComponent(entity, blockState));
       }
   }


   @Override
   public Identifier getUid() {
       return EclipticSeasons.rl("greenhouse_core");
   }
}
