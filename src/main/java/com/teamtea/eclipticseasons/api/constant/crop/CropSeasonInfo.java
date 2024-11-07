package com.teamtea.eclipticseasons.api.constant.crop;



import com.teamtea.eclipticseasons.api.constant.solar.Season;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class CropSeasonInfo
{
    private final int season;

    public CropSeasonInfo(int season)
    {
        this.season = season;
    }

    public boolean isSuitable(Season season)
    {
        if (season == Season.NONE)
        {
            return true;
        }
        else
        {
            return ((this.season >> season.ordinal()) & 1) == 1;
        }
    }

    public float getGrowChance(Season sea)
    {
        boolean spring = (season & 1) == 1;
        boolean summer = (season & 2) == 2;
        boolean autumn = (season & 4) == 4;
        boolean winter = (season & 8) == 8;

        switch (sea)
        {
            case SPRING:
                if (spring)
                {
                    return 1.2F;
                }
                break;
            case SUMMER:
                if (summer)
                {
                    return 1.4F;
                }
                break;
            case AUTUMN:
                if (autumn)
                {
                    return 1.0F;
                }
                break;
            case WINTER:
                if (winter)
                {
                    return 0.6F;
                }
        }
        return 1.0F;
    }

    public List<Component> getTooltip()
    {
        List<Component> list = new ArrayList<>();
        list.add(Component.translatable("info.eclipticseasons.environment.season").withStyle(ChatFormatting.GRAY));
        boolean spring = (season & 1) == 1;
        boolean summer = (season & 2) == 2;
        boolean autumn = (season & 4) == 4;
        boolean winter = (season & 8) == 8;
        if (spring && summer && autumn && winter)
        {
            list.add(Season.NONE.getTranslation());
        }
        else
        {
            if (spring)
            {
                list.add(Season.SPRING.getTranslation());
            }
            if (summer)
            {
                list.add(Season.SUMMER.getTranslation());
            }
            if (autumn)
            {
                list.add(Season.AUTUMN.getTranslation());
            }
            if (winter)
            {
                list.add(Season.WINTER.getTranslation());
            }
        }
        return list;
    }
}
