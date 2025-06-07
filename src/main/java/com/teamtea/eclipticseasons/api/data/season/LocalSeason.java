package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;

public record LocalSeason(
        Season season,
        Component name,
        Component description,
        Icon icon,
        FontIcon fontIcon
) {


    public static final Codec<LocalSeason> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ESExtraCodec.SEASON.fieldOf("season").forGetter(LocalSeason::season),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(LocalSeason::name),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(LocalSeason::description),
            Icon.CODEC.fieldOf("icon").forGetter(LocalSeason::icon),
            FontIcon.CODEC.fieldOf("font_icon").forGetter(LocalSeason::fontIcon)
    ).apply(ins, LocalSeason::new));

    public record Icon(
            ResourceLocation texture,
            int size,
            int y,
            int x
    ){

        public static final Codec<Icon> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ResourceLocation.CODEC.fieldOf("texture").forGetter(Icon::texture),
                Codec.INT.fieldOf("size").forGetter(Icon::size),
                Codec.INT.fieldOf("y").forGetter(Icon::y),
                Codec.INT.fieldOf("x").forGetter(Icon::x)
        ).apply(ins, Icon::new));
    }

    public record FontIcon(
            ResourceLocation font,
            String name
    ){
        public static final Codec<FontIcon> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(FontIcon::font),
                Codec.STRING.fieldOf("name").forGetter(FontIcon::name)
        ).apply(ins, FontIcon::new));
    }
}
