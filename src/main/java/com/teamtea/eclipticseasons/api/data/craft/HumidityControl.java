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
        List<PosAndBlockStateCheck> checks
) {


    public static final Codec<HumidityControl> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            WrapSizeIngredient.CODEC.fieldOf("ingredient").forGetter(HumidityControl::ingredient),
            ItemStack.CODEC.fieldOf("result").forGetter(HumidityControl::result),
            Codec.INT.fieldOf("range").forGetter(HumidityControl::range),
            Codec.INT.fieldOf("level").forGetter(HumidityControl::level),
            Codec.INT.fieldOf("lasting_time").forGetter(HumidityControl::lasting_time),
            PosAndBlockStateCheck.CODEC.listOf().fieldOf("checks").forGetter(HumidityControl::checks)
    ).apply(ins, HumidityControl::new));

    public static final Codec<HumidityControl> DIRECT_CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.INT.fieldOf("range").forGetter(HumidityControl::range),
            Codec.INT.fieldOf("level").forGetter(HumidityControl::level),
            Codec.INT.fieldOf("lasting_time").forGetter(HumidityControl::lasting_time)
    ).apply(ins, (integer, integer2, integer3) ->
            new HumidityControl(new WrapSizeIngredient(HolderSet.direct(), 0), ItemStack.EMPTY, integer, integer2, integer3, List.of())));

    public boolean noCost() {
        return CommonConfig.Crop.noCostHumidifier.get() || ingredient.test(result);
    }
}
