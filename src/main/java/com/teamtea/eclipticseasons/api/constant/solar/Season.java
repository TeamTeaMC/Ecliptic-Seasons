package com.teamtea.eclipticseasons.api.constant.solar;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.data.climate.AgroClimaticZone;
import com.teamtea.eclipticseasons.api.misc.ITranslatableWithPlaceholder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

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

    public TextColor getTextColor(){
        return TextColor.fromLegacyFormat(getColor());
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

    public enum Sub implements ITranslatableWithPlaceholder {
        EARLY_SPRING, MID_SPRING, LATE_SPRING,
        EARLY_SUMMER, MID_SUMMER, LATE_SUMMER,
        EARLY_AUTUMN, MID_AUTUMN, LATE_AUTUMN,
        EARLY_WINTER, MID_WINTER, LATE_WINTER,
        NONE;

        @Override
        public String getName() {
            return this.toString().toLowerCase(Locale.ROOT);
        }

        @Override
        public boolean isValid() {
            return this != NONE;
        }

        @Override
        public Component getTranslation() {
            return Component.translatable("info.eclipticseasons.environment.sub_season." + getName()).withStyle(getSeason().color);
        }

        public Season getSeason() {
            return isValid() ? Season.collectValidValues()[ordinal() / 3] : Season.NONE;
        }

        public static Sub of(SolarTerm solarTerm) {
            return solarTerm.isValid() ? collectValidValues()[solarTerm.ordinal() / 2] : NONE;
        }

        private static final Sub[] values = Sub.values();

        public static Sub[] collectValues() {
            return values;
        }


        private static final Sub[] validValues = Arrays.stream(Sub.values())
                .filter(Sub::isValid).toArray(Sub[]::new);

        public static Sub[] collectValidValues() {
            return validValues;
        }

        public SolarTerm getFirstSolarTerm() {
            return isValid() ? SolarTerm.get(ordinal() * 2) : SolarTerm.NONE;
        }

        public SolarTerm getEndSolarTerm() {
            return isValid() ? SolarTerm.get(ordinal() * 2 + 1) : SolarTerm.NONE;
        }

        // public SolarTerm getFirstSolarTerm(AgroClimaticZone climate) {
        //     if (climate == null || !isValid()) return getFirstSolarTerm();
        //     List<Pair<Season, Integer>> durations = climate.seasonalSignalDurations();
        //     if (durations.isEmpty()) return SolarTerm.NONE;
        //
        //     Season target = getSeason();
        //     int totalLen = 0;
        //     for (Pair<Season, Integer> p : durations) if (p.getFirst() == target) totalLen += p.getSecond();
        //     if (totalLen == 0) return SolarTerm.NONE;
        //
        //     int subIdx = ordinal() % 3;
        //     int offset = subIdx * (totalLen / 3) + Math.min(subIdx, totalLen % 3);
        //
        //     // Find the global start index of the season (considering wrap-around)
        //     int currentPos = 0;
        //     if (durations.get(0).getFirst() == target && durations.get(durations.size() - 1).getFirst() == target) {
        //         // If it wraps, the "real" start is the beginning of the last pair
        //         int lastLen = durations.get(durations.size() - 1).getSecond();
        //         currentPos = 24 - lastLen;
        //     } else {
        //         for (Pair<Season, Integer> p : durations) {
        //             if (p.getFirst() == target) break;
        //             currentPos += p.getSecond();
        //         }
        //     }
        //
        //     return SolarTerm.get((currentPos + offset) % 24);
        // }
        //
        // public SolarTerm getEndSolarTerm(AgroClimaticZone climate) {
        //     if (climate == null || !isValid()) return getEndSolarTerm();
        //     List<Pair<Season, Integer>> durations = climate.seasonalSignalDurations();
        //
        //     Season target = getSeason();
        //     int totalLen = 0;
        //     for (Pair<Season, Integer> p : durations) if (p.getFirst() == target) totalLen += p.getSecond();
        //
        //     int subIdx = ordinal() % 3;
        //     int subLen = (totalLen / 3) + (subIdx < (totalLen % 3) ? 1 : 0);
        //     int start = getFirstSolarTerm(climate).ordinal();
        //
        //     return SolarTerm.get((start + subLen - 1) % 24);
        // }
        //
        // public static Sub cast(SolarTerm solarTerm, AgroClimaticZone climate) {
        //     if (!solarTerm.isValid() || climate == null) {
        //         return solarTerm.isValid() ? collectValidValues()[solarTerm.ordinal() / 2] : NONE;
        //     }
        //
        //     List<Pair<Season, Integer>> durations = climate.seasonalSignalDurations();
        //     if (durations.isEmpty()) return NONE;
        //
        //     int termIdx = solarTerm.ordinal();
        //     Season currentSeason = Season.NONE;
        //     int acc = 0;
        //     for (Pair<Season, Integer> p : durations) {
        //         if (termIdx >= acc && termIdx < acc + p.getSecond()) {
        //             currentSeason = p.getFirst();
        //             break;
        //         }
        //         acc += p.getSecond();
        //     }
        //
        //     if (currentSeason == Season.NONE) return NONE;
        //
        //     // Calculate total season length and the relative offset of the term
        //     int totalLen = 0;
        //     int offset = 0;
        //     boolean wrapped = durations.get(0).getFirst() == durations.get(durations.size() - 1).getFirst();
        //     Season wrappedSeason = wrapped ? durations.get(0).getFirst() : null;
        //
        //     if (currentSeason == wrappedSeason) {
        //         int lastLen = durations.get(durations.size() - 1).getSecond();
        //         // If term is in the first part (start of year), its offset is (lastPartLength + indexInFirstPart)
        //         if (termIdx < durations.get(0).getSecond()) {
        //             offset = lastLen + termIdx;
        //         } else {
        //             // If term is in the last part (end of year), its offset is just its distance from the start of that part
        //             offset = termIdx - (24 - lastLen);
        //         }
        //         for (Pair<Season, Integer> p : durations) if (p.getFirst() == currentSeason) totalLen += p.getSecond();
        //     } else {
        //         int startAcc = 0;
        //         for (Pair<Season, Integer> p : durations) {
        //             if (p.getFirst() == currentSeason) break;
        //             startAcc += p.getSecond();
        //         }
        //         offset = termIdx - startAcc;
        //         for (Pair<Season, Integer> p : durations) if (p.getFirst() == currentSeason) totalLen += p.getSecond();
        //     }
        //
        //     int base = totalLen / 3;
        //     int rem = totalLen % 3;
        //     int b1 = base + (rem > 0 ? 1 : 0);
        //     int b2 = b1 + base + (rem > 1 ? 1 : 0);
        //
        //     int subIdx = (offset < b1) ? 0 : (offset < b2 ? 1 : 2);
        //     return collectValidValues()[currentSeason.ordinal() * 3 + subIdx];
        // }
        //

    }
}
