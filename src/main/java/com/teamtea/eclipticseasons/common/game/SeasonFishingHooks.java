package com.teamtea.eclipticseasons.common.game;

import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class SeasonFishingHooks {
    public static ObjectArrayList<ItemStack> modify(LootParams pParams, ObjectArrayList<ItemStack> original) {
        if (CommonConfig.Animal.enableFishing.get()) {
            ServerLevel level = pParams.getLevel();
            Entity entity = pParams.getParameter(LootContextParams.THIS_ENTITY);
            BlockPos blockPos = entity.getOnPos().above();
            boolean badWeather = WeatherManager.isThunderAt(level, blockPos)
                    && !CommonConfig.Animal.lessFishInThunder.get();
            Season season = AnimalHooks.getUseSeason(level, entity);
            List<Season> seasons = (List<Season>) CommonConfig.Animal.fishingSeasons.get();
            if ((!seasons.contains(season)
                    && (!CommonConfig.Animal.enableCoreWork.get() || AnimalHooks.withoutSeasonBonus(level, blockPos, seasons)))
                    || badWeather) {
                for (int i = 0; i < original.size(); i++) {
                    var items = original.get(i);
                    if (items.is(ItemTags.FISHES)) {
                        if (badWeather || level.getRandom().nextInt(2) == 0) {
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
