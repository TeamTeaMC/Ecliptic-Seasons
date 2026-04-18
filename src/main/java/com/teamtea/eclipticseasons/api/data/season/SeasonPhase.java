package com.teamtea.eclipticseasons.api.data.season;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.ISolarTerm;
import com.teamtea.eclipticseasons.api.util.codec.ESExtraCodec;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;
import java.util.Optional;

public record SeasonPhase(
        Season season,
        Identifier name,
        ChatFormatting color,
        Optional<Icon> icon,
        FontIcon fontIcon
) implements ISolarTerm {
    @Override
    public Season getSeason() {
        return season;
    }

    public static final Codec<SeasonPhase> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ESExtraCodec.SEASON.fieldOf("season").forGetter(SeasonPhase::season),
            Identifier.CODEC.fieldOf("name").forGetter(SeasonPhase::name),
            StringRepresentable.fromEnum(ChatFormatting::values).fieldOf("color").forGetter(SeasonPhase::color),
            Icon.CODEC.optionalFieldOf("icon").forGetter(SeasonPhase::icon),
            FontIcon.CODEC.fieldOf("font").forGetter(SeasonPhase::fontIcon)
    ).apply(ins, SeasonPhase::new));

    public record Icon(
            Identifier texture,
            int width,
            int height,
            int size,
            int x,
            int y
    ) {

        public Icon(Identifier texture) {
            this(texture, 30, 30, 30, 0, 0);
        }

        public static final Codec<Icon> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Identifier.CODEC.fieldOf("texture").forGetter(Icon::texture),
                Codec.INT.optionalFieldOf("width", 30).forGetter(Icon::width),
                Codec.INT.optionalFieldOf("height", 30).forGetter(Icon::height),
                Codec.INT.optionalFieldOf("size", 30).forGetter(Icon::size),
                Codec.INT.optionalFieldOf("i", 0).forGetter(Icon::x),
                Codec.INT.optionalFieldOf("j", 0).forGetter(Icon::y)
        ).apply(ins, Icon::new));
    }

    public record FontIcon(
            Identifier font,
            String label
    ) {
        public static final Codec<FontIcon> CODEC = RecordCodecBuilder.create(ins -> ins.group(
                Identifier.CODEC.fieldOf("id").forGetter(FontIcon::font),
                Codec.STRING.fieldOf("label").forGetter(FontIcon::label)
        ).apply(ins, FontIcon::new));
    }

    public String getMod() {
        return name().getNamespace().toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return name().getPath().toLowerCase(Locale.ROOT);
    }

    @Override
    public MutableComponent getTranslation() {
        return Component.translatable("info." + getMod() + ".environment.season_phase." + getName()).withStyle(color());
    }

    @Override
    public MutableComponent getTittleTranslation() {
        return Component.translatable("info.eclipticseasons.environment.season_phase.hint").withStyle(color());
    }

    @Override
    public MutableComponent getAlternationText() {
        return Component.translatable("info." + getMod() + ".environment.season_phase.alternation." + getName()).withStyle(color());
    }

    @Override
    public MutableComponent getPatternTranslation() {
        return Component.translatable("info." + getMod() + ".environment.season_phase.pattern." + getName(), getTranslation()).withStyle(color());
    }

    @Override
    public ChatFormatting getColor() {
        return color();
    }

    @Override
    public Identifier getIconFont() {
        return fontIcon().font();
    }

    @Override
    public Identifier getIcon() {
        return icon().isPresent() ? icon().get().texture() :
                name().withPrefix(ESRegistries.SEASON_PHASE.identifier().getPath() + "/");
    }

    @Override
    public String getFontLabel() {
        return fontIcon().label();
    }

    @Override
    public Pair<Integer, Integer> getIconPosition() {
        return icon().isPresent() ?
                Pair.of(icon().get().x, icon().get().y) :
                Pair.of(0, 0)
                ;
    }

    @Override
    public int getIconAtlasSize() {
        return icon().isPresent() ? icon().get().size() : 30;
    }

    @Override
    public int getIconWidth() {
        return icon().isPresent() ? icon().get().width() : 30;
    }

    @Override
    public int getIconHeight() {
        return icon().isPresent() ? icon().get().height() : 30;
    }
}
