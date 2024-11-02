package com.teamtea.eclipticseasons.game;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.ServerConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class SeasonFishingHooks {
    public static ObjectArrayList<ItemStack> modify(LootParams pParams, ObjectArrayList<ItemStack> original) {

        if(ServerConfig.Animal.enableFishing.get())
        {
            boolean badWeather = WeatherManager.isThunderAt(pParams.getLevel(), pParams.getParameter(LootContextParams.THIS_ENTITY).getOnPos().above());

            Season season = EclipticSeasonsApi.getInstance().getSolarTerm(pParams.getLevel()).getSeason();
            if (season != Season.SUMMER || badWeather) {
                for (int i = 0; i < original.size(); i++) {
                    var items = original.get(i);
                    if (items.is(ItemTags.FISHES)) {
                        if (badWeather || pParams.getLevel().getRandom().nextInt(4 / Mth.abs(season.ordinal() - Season.SUMMER.ordinal())) == 0) {
                            original.remove(i);
                            i--;
                        }
                    }
                }
            }
        }

        return original;
    }
}
