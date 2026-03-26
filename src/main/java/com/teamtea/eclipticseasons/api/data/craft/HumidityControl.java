package com.teamtea.eclipticseasons.api.data.craft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.data.misc.PosAndBlockStateCheck;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.TestOnly;

import java.util.List;

public record HumidityControl(
        SizedIngredient ingredient,
        ItemStackTemplate result,
        int range,
        int level,
        int lasting_time,
        List<PosAndBlockStateCheck> checks,
        boolean infinity
) {
    public HumidityControl(
            SizedIngredient ingredient,
            ItemStackTemplate result,
            int range,
            int level,
            int lasting_time,
            List<PosAndBlockStateCheck> checks
    ) {
        this(ingredient, result, range, level, lasting_time, checks, false);
    }

    public static final Codec<HumidityControl> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            SizedIngredient.NESTED_CODEC.fieldOf("ingredient").forGetter(HumidityControl::ingredient),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(HumidityControl::result),
            Codec.INT.fieldOf("range").forGetter(HumidityControl::range),
            Codec.INT.fieldOf("level").forGetter(HumidityControl::level),
            Codec.INT.optionalFieldOf("lasting_time", 6000).forGetter(HumidityControl::lasting_time),
            PosAndBlockStateCheck.CODEC.listOf().fieldOf("checks").forGetter(HumidityControl::checks),
            Codec.BOOL.optionalFieldOf("infinity", false).forGetter(HumidityControl::infinity)
    ).apply(ins, HumidityControl::new));


    public boolean noCost() {
        return CommonConfig.Crop.noCostHumidifier.get() || infinity || ingredient.test(result.create());
    }
}
