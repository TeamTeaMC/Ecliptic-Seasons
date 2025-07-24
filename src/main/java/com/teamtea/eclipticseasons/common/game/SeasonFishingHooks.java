package com.teamtea.eclipticseasons.common.game;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class SeasonFishingHooks {
    public static ObjectArrayList<ItemStack> modify(LootParams pParams, ObjectArrayList<ItemStack> original) {
        if (CommonConfig.Animal.enableFishing.get()) {
            ServerLevel level = pParams.getLevel();
            Entity entity = pParams.getParameter(LootContextParams.THIS_ENTITY);
            boolean badWeather = WeatherManager.isThunderAt(level, entity.getOnPos().above());
            Season season = AnimalHooks.getUseSeason(level,entity);
            if (season != Season.SUMMER || badWeather) {
                for (int i = 0; i < original.size(); i++) {
                    var items = original.get(i);
                    if (items.is(ItemTags.FISHES)) {
                        if (badWeather || level.getRandom().nextInt(4 / Mth.abs(season.ordinal() - Season.SUMMER.ordinal())) == 0) {
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
