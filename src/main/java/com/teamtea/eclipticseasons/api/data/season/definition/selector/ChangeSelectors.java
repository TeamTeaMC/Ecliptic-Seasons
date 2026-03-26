package com.teamtea.eclipticseasons.api.data.season.definition.selector;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ChangeSelectors {

    public static final Map<Identifier, MapCodec<? extends IChangeSelector>> CONDITIONS = new HashMap<>();
    public static final Identifier BLOCK = EclipticSeasons.rl("block");
    public static final Identifier FEATURE = EclipticSeasons.rl("feature");
    public static final Identifier MULTI_BLOCKS = EclipticSeasons.rl("multi_blocks");

    static {
        CONDITIONS.put(BLOCK, BlockSelector.CODEC);
        CONDITIONS.put(FEATURE, FeatureSelector.CODEC);
        CONDITIONS.put(MULTI_BLOCKS, MultiBlockSelector.CODEC);
    }
}
