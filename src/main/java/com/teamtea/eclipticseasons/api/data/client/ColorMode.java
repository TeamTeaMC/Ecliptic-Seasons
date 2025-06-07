package com.teamtea.eclipticseasons.api.data.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Optional;

public record ColorMode(
        Optional<Integer> color, Optional<Float> fix, Optional<String> sColor
) {

    public static final Codec<ColorMode> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                    Codec.INT.optionalFieldOf("color").forGetter(ColorMode::color),
                    Codec.FLOAT.optionalFieldOf("fix").forGetter(ColorMode::fix),
                    Codec.STRING.optionalFieldOf("color_string").forGetter(ColorMode::sColor)
            )
            .apply(ins, ColorMode::new))
            //         .validate(
            //         ColorMode::check
            // )
            ;

    private static DataResult<ColorMode> check(ColorMode o) {
        if (o.color.isEmpty() && o.sColor().isEmpty()) return DataResult.error(() -> "All Empty");
        if (o.fix.isPresent() && o.fix.get() < 0) return DataResult.error(() -> "Not valid mix " + o.fix.get());
        return DataResult.success(o);
    }

    public @Nullable Instance toInstance() {
        if (color.isEmpty() && sColor.isEmpty()) {
            return null;
        }
        Integer colorInt = color.orElseGet(() ->
                sColor.map(s -> {
                    try {
                        String formattedColor = s.startsWith("#") ? s.substring(1) : s;
                        return Color.decode("0x" + formattedColor).getRGB();
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }).orElse(null)
        );

        if (colorInt == null) {
            return null;
        }
        return new Instance(colorInt, fix.orElse(1f));
    }

    public record Instance(
            int value,
            float fix
    ) {
    }
}
