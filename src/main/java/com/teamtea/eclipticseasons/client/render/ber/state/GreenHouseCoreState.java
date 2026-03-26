package com.teamtea.eclipticseasons.client.render.ber.state;

import com.teamtea.eclipticseasons.common.block.GreenHouseCoreBlock;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;


@Getter
@Setter
public class GreenHouseCoreState extends BlockEntityRenderState {
    int stage;
    long renderTicks;
    GreenHouseCoreBlock block;
}