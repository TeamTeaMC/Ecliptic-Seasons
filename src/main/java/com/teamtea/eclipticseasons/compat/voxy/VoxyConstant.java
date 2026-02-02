package com.teamtea.eclipticseasons.compat.voxy;

import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VoxyConstant {
    public static final BooleanProperty SNOWY = BooleanProperty.create("voxy_snowy");

    public static boolean shouldSkipCheck(Block block) {
        return block instanceof LiquidBlock
                || block instanceof AirBlock
                || block instanceof BarrierBlock
                || block instanceof BaseFireBlock
                || block instanceof AbstractChestBlock
                || block instanceof BedBlock

                || block instanceof AbstractBannerBlock
                || block instanceof AbstractCandleBlock
                || block instanceof HalfTransparentBlock
                || block instanceof AbstractSkullBlock
                || block instanceof BaseRailBlock
                || block instanceof BeaconBlock

                || block instanceof BellBlock
                || block instanceof LanternBlock
                || block instanceof TorchBlock
                || block instanceof ChainBlock
                || block instanceof SignBlock
                || block instanceof EnchantingTableBlock

                || block instanceof EndPortalBlock
                || block instanceof EndPortalFrameBlock
                || block instanceof EndGatewayBlock
                || block instanceof EndRodBlock
                //|| block instanceof FallingBlock
                || block instanceof FrogspawnBlock
                || block instanceof KelpBlock
                || block instanceof KelpPlantBlock
                || block instanceof LadderBlock
                || block instanceof AbstractCauldronBlock

                || block instanceof LightBlock
                || block instanceof NetherPortalBlock
                || block instanceof NoteBlock
                || block instanceof RedStoneWireBlock
                || block instanceof SeagrassBlock
                || block instanceof StructureVoidBlock
                || block instanceof TripWireBlock
                || block instanceof TripWireHookBlock
                || block instanceof WebBlock
                || block == Blocks.SNOW_BLOCK
                || block instanceof SnowLayerBlock
                || block instanceof PowderSnowBlock;
    }

    public static VoxelShape getShape(BlockState blockState) {
        return Shapes.block();
    }
}
