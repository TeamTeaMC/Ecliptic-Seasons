package com.teamtea.eclipticseasons.compat.yuushya;

import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

public class YuushyaChecker {

    public static boolean isyuushyaBlock(BlockState state) {
        return CompatModule.isYuuWithCTMLoad()
                && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().startsWith("yuushya");
    }

    public static boolean isyuushyaRBlock(BlockState state) {
        return CompatModule.isYuuLoad()
                && BuiltInRegistries.BLOCK.getKey(state.getBlock()).getNamespace().startsWith("yuushya");
    }
}
