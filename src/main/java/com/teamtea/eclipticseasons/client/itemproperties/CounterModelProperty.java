package com.teamtea.eclipticseasons.client.itemproperties;

import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngle;
import net.minecraft.client.renderer.item.properties.numeric.CompassAngleState;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class CounterModelProperty implements RangeSelectItemModelProperty {
    public static final MapCodec<CounterModelProperty> MAP_CODEC =
            CounterState.MAP_CODEC.xmap(CounterModelProperty::new, c -> c.state);
    private final CounterState state;

    public CounterModelProperty(CounterState.Query query, float maxLength) {
        this(new CounterState(query, maxLength));
    }

    private CounterModelProperty(CounterState state) {
        this.state = state;
    }

    @Override
    public float get(@NonNull ItemStack itemStack, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return this.state.get(itemStack, level, owner, seed);
    }

    @Override
    public @NonNull MapCodec<CounterModelProperty> type() {
        return MAP_CODEC;
    }

}
