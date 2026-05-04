package com.teamtea.eclipticseasons.client.color.season;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FoliageColorSource() {

    public static Impl createOrNull(String s) {
        FoliageColorSourceDefault.ColorHolder parse = FoliageColorSourceDefault.parse(s);
        return parse == null ? null : new Impl(parse);
    }


    public record Impl(FoliageColorSourceDefault.ColorHolder content) implements BlockColor {

        @Override
        public int getColor(@NotNull BlockState state, @Nullable net.minecraft.world.level.BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos pos, int i) {
            return BiomeColorsHandler.getLeavesColor(content.base(), content.values(), pos);
        }
    }

}
