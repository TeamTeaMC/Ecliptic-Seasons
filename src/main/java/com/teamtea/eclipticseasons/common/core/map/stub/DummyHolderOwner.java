package com.teamtea.eclipticseasons.common.core.map.stub;

import net.minecraft.core.HolderOwner;
import org.jetbrains.annotations.NotNull;

public class DummyHolderOwner<T> implements HolderOwner<T> {
    @Override
    public boolean canSerializeIn(@NotNull HolderOwner<T> context) {
        return false;
    }
}
