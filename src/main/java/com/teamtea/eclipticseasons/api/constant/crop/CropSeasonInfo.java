package com.teamtea.eclipticseasons.api.constant.crop;


import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.SimpleUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CropSeasonInfo {
    private final int season;

    public CropSeasonInfo(int season) {
        this.season = season;
    }

    public boolean isSuitable(Season season) {
        if (season == Season.NONE) {
            return true;
        } else {
            return ((this.season >> season.ordinal()) & 1) == 1;
        }
    }

    public float getGrowChance(Season sea) {
        boolean spring = (season & 1) == 1;
        boolean summer = (season & 2) == 2;
        boolean autumn = (season & 4) == 4;
        boolean winter = (season & 8) == 8;

        switch (sea) {
            case SPRING:
                if (spring) {
                    return 1.2F;
                }
                break;
            case SUMMER:
                if (summer) {
                    return 1.4F;
                }
                break;
            case AUTUMN:
                if (autumn) {
                    return 1.0F;
                }
                break;
            case WINTER:
                if (winter) {
                    return 0.6F;
                }
        }
        return 1.0F;
    }

    public List<Component> getTooltip() {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("info.eclipticseasons.environment.season").withStyle(ChatFormatting.GRAY));
        boolean spring = (season & 1) == 1;
        boolean summer = (season & 2) == 2;
        boolean autumn = (season & 4) == 4;
        boolean winter = (season & 8) == 8;
        if (spring && summer && autumn && winter) {
            list.add(Season.NONE.getTranslation());
        } else {
            MutableComponent mutableComponent = Component.empty();
            int hashCode = mutableComponent.hashCode();
            if (spring) {
                list.add(Season.SPRING.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.SPRING_EQUINOX,mutableComponent);
            }
            if (summer) {
                list.add(Season.SUMMER.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.SUMMER_SOLSTICE,mutableComponent);
            }

            if (autumn) {
                list.add(Season.AUTUMN.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.AUTUMNAL_EQUINOX,mutableComponent);
            }

            if (winter) {
                list.add(Season.WINTER.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.WINTER_SOLSTICE,mutableComponent);
            }

            if (mutableComponent.hashCode() != hashCode) {
                list.add(mutableComponent);
            }
        }
        return list;
    }

    public static int getSeason(List<Season> seasons) {
        int season = 0;
        for (Season season1 : seasons) {
            season += switch (season1) {
                case SPRING -> 1;
                case SUMMER -> 2;
                case AUTUMN -> 4;
                case WINTER -> 8;
                default -> 0;
            };
        }
        return season;
    }

    public static List<Component> getTooltip(int season) {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("info.eclipticseasons.environment.season").withStyle(ChatFormatting.GRAY));
        boolean spring = (season & 1) == 1;
        boolean summer = (season & 2) == 2;
        boolean autumn = (season & 4) == 4;
        boolean winter = (season & 8) == 8;
        if (spring && summer && autumn && winter) {
            list.add(Season.NONE.getTranslation());
        } else {
            MutableComponent mutableComponent = Component.empty();
            int hashCode = mutableComponent.hashCode();
            if (spring) {
                list.add(Season.SPRING.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.SPRING_EQUINOX,mutableComponent);
            }
            if (summer) {
                list.add(Season.SUMMER.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.SUMMER_SOLSTICE,mutableComponent);
            }

            if (autumn) {
                list.add(Season.AUTUMN.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.AUTUMNAL_EQUINOX,mutableComponent);
            }

            if (winter) {
                list.add(Season.WINTER.getTranslation());
                // mutableComponent= SimpleUtil.addSolarIconBefore(SolarTerm.WINTER_SOLSTICE,mutableComponent);
            }

            if (mutableComponent.hashCode() != hashCode) {
                list.add(mutableComponent);
            }
        }
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CropSeasonInfo that = (CropSeasonInfo) o;
        return season == that.season;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(season);
    }
}
