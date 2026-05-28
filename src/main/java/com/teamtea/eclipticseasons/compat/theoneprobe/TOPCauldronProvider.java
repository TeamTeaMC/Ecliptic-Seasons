package com.teamtea.eclipticseasons.compat.theoneprobe;//package com.teamtea.eclipticseasons.compat.theoneprobe;
//
//import com.teamtea.eclipticseasons.EclipticSeasons;
//import com.teamtea.eclipticseasons.common.block.IceOrSnowCauldronBlock;
//import mcjty.theoneprobe.api.IProbeHitData;
//import mcjty.theoneprobe.api.IProbeInfo;
//import mcjty.theoneprobe.api.IProbeInfoProvider;
//import mcjty.theoneprobe.api.ProbeMode;
//import net.minecraft.resources.Identifier;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.state.BlockState;
//
//public class TOPCauldronProvider implements IProbeInfoProvider {
//    @Override
//    public Identifier getID() {
//        return EclipticSeasons.rl("cauldron");
//    }
//
//    @Override
//    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
//        Block block = blockState.getBlock();
//        if (block instanceof IceOrSnowCauldronBlock iceOrSnowCauldronBlock) {
//            iProbeInfo.mcText(iceOrSnowCauldronBlock.getTip());
//        }
//    }
//}
