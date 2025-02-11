package com.teamtea.eclipticseasons.api.constant.crop;



import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import net.minecraft.util.text.*;

import java.util.ArrayList;
import java.util.List;

public class CropHumidityInfo
{
    private final Humidity min;
    private final Humidity max;

    public CropHumidityInfo(Humidity min, Humidity max)
    {
        this.min = min;
        this.max = max;
    }

    public CropHumidityInfo(Humidity env)
    {
        this.min = env;
        this.max = env;
    }

    public boolean isSuitable(Humidity env)
    {
        return min.getId() <= env.getId() && env.getId() <= max.getId();
    }

    public float getGrowChance(Humidity env)
    {
        if (isSuitable(env))
        {
            return 1.0F;
        }
        else if (env.getId() < min.getId())
        {
            return Math.max(0, 1.0F - 0.5F * (min.getId() - env.getId()) * (min.getId() - env.getId()));
        }
        else
        {
            return Math.max(0, 1.0F - 0.5F * (env.getId() - max.getId()) * (env.getId() - max.getId()));
        }
    }

    public List<ITextComponent> getTooltip()
    {
        List<ITextComponent> list = new ArrayList<>();
        list.add(new TranslationTextComponent("info.eclipticseasons.environment.humidity").withStyle(TextFormatting.GRAY));
        if (min != max)
        {
            list.add(((TranslationTextComponent) min.getTranslation()).append(new StringTextComponent(" - ").withStyle(TextFormatting.GRAY)).append(max.getTranslation()));
        }
        else
        {
            list.add(min.getTranslation());
        }
        return list;
    }
}
