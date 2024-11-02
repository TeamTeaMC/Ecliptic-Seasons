package com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.config.ServerConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sfiomn.legendarysurvivaloverhaul.common.integration.sereneseasons.SereneSeasonsUtil;


public class LSO_SeasonalCalendarTimeProperty implements ClampedItemPropertyFunction {

    @OnlyIn(Dist.CLIENT)
    @Override
    public float unclampedCall(@NotNull ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity entity, int i)
    {
        Level level = clientLevel;
        Entity holder = (entity != null ? entity : itemStack.getFrame());

        if (level == null && holder != null)
        {
            level = holder.level();
        }

        if (level == null)
        {
            return 0;
        }
        else
        {
            try
            {
                double d0=0;

                SereneSeasonsUtil.SeasonType seasonType = LSO_ESUtil.getSeasonType(level.getBiome(holder.blockPosition()));
                if(  seasonType== SereneSeasonsUtil.SeasonType.NORMAL_SEASON) {
                    int seasonCycleTicks = EclipticUtil.getNowSolarDay(level);
                    d0 = (double) ((float) seasonCycleTicks / (float) (24 * ServerConfig.Season.lastingDaysOfEachTerm.get()));
                }else if(seasonType== SereneSeasonsUtil.SeasonType.TROPICAL_SEASON){
                    int seasonCycleTicks = EclipticUtil.getNowSolarTerm(level).ordinal()+1+6;
                    seasonCycleTicks=seasonCycleTicks>24?seasonCycleTicks-24:seasonCycleTicks;
                    if(seasonCycleTicks<5){
                        d0=0.25f;
                    }else  if(seasonCycleTicks<9){
                        d0=0.5f;
                    }else  if(seasonCycleTicks<13){
                        d0=2/3f;
                    }else  if(seasonCycleTicks<17){
                        d0=0.75f;
                    }else  if(seasonCycleTicks<21){
                        d0=0;
                    }else {
                        d0=1/6f;
                    }
                }

                return Mth.positiveModulo((float)d0, 1.0F);
            }
            catch (NullPointerException e)
            {
                return 0;
            }

        }
    }
}
