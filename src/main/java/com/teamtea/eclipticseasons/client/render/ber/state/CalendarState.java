package com.teamtea.eclipticseasons.client.render.ber.state;

import com.teamtea.eclipticseasons.common.block.CalendarBlock;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

@Getter
@Setter
public class CalendarState extends BlockEntityRenderState {
    Holder<Biome> biome;
    CalendarBlock.DisplayMode displayMode;
    Direction facing;
}
