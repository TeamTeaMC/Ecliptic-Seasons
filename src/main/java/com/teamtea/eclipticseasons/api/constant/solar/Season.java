package com.teamtea.eclipticseasons.api.constant.solar;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.misc.ITranslatableWithPlaceholder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.Arrays;
import java.util.Locale;


public enum Season implements ITranslatableWithPlaceholder {
    SPRING(ChatFormatting.DARK_GREEN),
    SUMMER(ChatFormatting.RED),
    AUTUMN(ChatFormatting.GOLD),
    WINTER(ChatFormatting.BLUE),
    NONE(ChatFormatting.DARK_AQUA);

    private final ChatFormatting color;

    Season(ChatFormatting color) {
        this.color = color;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ROOT);
    }

    @Override
    public MutableComponent getTranslation() {
        return Component.translatable("info.eclipticseasons.environment.season." + getName()).withStyle(color);
    }

    public ChatFormatting getColor() {
        return color;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    private static final Season[] seasons = Season.values();

    public static Season[] collectValues() {
        return seasons;
    }

    private static final Season[] validSeasons = Arrays.stream(Season.values())
            .filter(Season::isValid).toArray(Season[]::new);

    public static Season[] collectValidValues() {
        return validSeasons;
    }

    public boolean isValid() {
        return this != NONE;
    }

    public boolean isInTerms(Season start, Season end) {
        if (start == NONE || end == NONE) return false;
        else if (start == end)
            return this == start; // es patch: if A is B then use single if B is next to A ,then means all
        else if (start.ordinal() <= end.ordinal()) {
            return start.ordinal() <= this.ordinal() && this.ordinal() <= end.ordinal();
        } else
            return start.ordinal() <= this.ordinal() || this.ordinal() <= end.ordinal();
    }

    public SolarTerm getFirstSolarTerm() {
        return SolarTerm.get(ordinal() * 6);
    }

    public SolarTerm getEndSolarTerm() {
        return SolarTerm.get(ordinal() * 6 + 5);
    }

    public SolarTerm getFirstSolarTerm(AgroClimaticZone climate) {
        if (climate == null) return getFirstSolarTerm();
        if (climate.seasonalSignalDurations().isEmpty()) return SolarTerm.NONE;

        int ordinal = 0;
        int foundCount = 0;
        for (Pair<Season, Integer> pair : climate.seasonalSignalDurations()) {
            if (pair.getFirst() == this) {
                if (foundCount > 0 || ordinal > 0) {
                    return SolarTerm.get(ordinal);
                }
                foundCount++;
            }
            ordinal += pair.getSecond();
        }
        return SolarTerm.get(foundCount == 1 ? 0 : ordinal - 1);
    }

    public SolarTerm getEndSolarTerm(AgroClimaticZone climate) {
        if (climate == null) return getEndSolarTerm();
        if (climate.seasonalSignalDurations().isEmpty()) return SolarTerm.NONE;
        int ordinal = 0;
        for (Pair<Season, Integer> pair : climate.seasonalSignalDurations()) {
            ordinal += pair.getSecond();
            if (pair.getFirst() == this) {
                return SolarTerm.collectValues()[ordinal - 1];
            }
        }
        return SolarTerm.NONE;
    }
}
