package com.teamtea.eclipticseasons.api.misc.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.HolderSet;

public interface HolderMappable<K extends HolderSet<?>,V> {
    Pair<K, V> asHolderMapping();
}
