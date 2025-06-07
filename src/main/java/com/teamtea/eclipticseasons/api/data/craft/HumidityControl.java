package com.teamtea.eclipticseasons.api.data.craft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.HolderSet;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.TestOnly;

import java.util.List;

public record HumidityControl(
        WrapSizeIngredient ingredient,
        ItemStack result,
        int range,
        int level,
        int lasting_time,
        List<PosAndBlockStateCheck> checks,
        boolean infinity
) {
    public HumidityControl(
            WrapSizeIngredient ingredient,
            ItemStack result,
            int range,
            int level,
            int lasting_time,
            List<PosAndBlockStateCheck> checks
    ) {
        this(ingredient, result, range, level, lasting_time, checks, false);
    }

    public static final Codec<HumidityControl> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            WrapSizeIngredient.CODEC.fieldOf("ingredient").forGetter(HumidityControl::ingredient),
            ItemStack.CODEC.fieldOf("result").forGetter(HumidityControl::result),
            Codec.INT.fieldOf("range").forGetter(HumidityControl::range),
            Codec.INT.fieldOf("level").forGetter(HumidityControl::level),
            Codec.INT.optionalFieldOf("lasting_time", 6000).forGetter(HumidityControl::lasting_time),
            PosAndBlockStateCheck.CODEC.listOf().fieldOf("checks").forGetter(HumidityControl::checks),
            Codec.BOOL.optionalFieldOf("infinity", false).forGetter(HumidityControl::infinity)
    ).apply(ins, HumidityControl::new));

    public static final Codec<HumidityControl> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("range").forGetter(HumidityControl::range),
            Codec.INT.fieldOf("level").forGetter(HumidityControl::level),
            Codec.INT.optionalFieldOf("lasting_time", 6000).forGetter(HumidityControl::lasting_time),
            Codec.BOOL.optionalFieldOf("infinity", false).forGetter(HumidityControl::infinity)
    ).apply(ins, (range, level, lasting_time, infinity) ->
            new HumidityControl(new WrapSizeIngredient(HolderSet.direct(), 0), ItemStack.EMPTY, range, level, lasting_time, List.of(), infinity)));

    public boolean noCost() {
        return CommonConfig.Crop.noCostHumidifier.get() || infinity || ingredient.test(result);
    }
}
