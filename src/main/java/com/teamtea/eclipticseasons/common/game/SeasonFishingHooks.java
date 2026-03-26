package com.teamtea.eclipticseasons.common.game;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.config.CommonConfig;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.List;

public class SeasonFishingHooks {

    public static boolean isFishingSeason(Level level, BlockPos pos) {
        if (!CommonConfig.Animal.enableFishing.get()) return true;

        Season current = AnimalHooks.getUseSeason(level, pos);
        List<Season> allowed = CommonConfig.castSeasonList(CommonConfig.Animal.fishingSeasons.get());
        boolean coreEnabled = CommonConfig.Animal.enableCoreWork.get();
        boolean hasBonus = !AnimalHooks.withoutSeasonBonus(level, pos, allowed);

        return allowed.contains(current) || (coreEnabled && hasBonus);
    }


    public static boolean isBadWeatherNow(Level level, BlockPos pos) {
        return CommonConfig.Animal.lessFishInThunder.get()
                && EclipticSeasonsApi.getInstance().isThundering(level, pos);
    }

    public static ObjectArrayList<ItemStack> modify(LootParams pParams, ObjectArrayList<ItemStack> original) {
        if (CommonConfig.Animal.enableFishing.get()) {
            ServerLevel level = pParams.getLevel();
            Entity entity = pParams.contextMap().getOrThrow(LootContextParams.THIS_ENTITY);
            BlockPos blockPos = entity.getOnPos().above();
            boolean badWeather = isBadWeatherNow(level, blockPos);
            boolean notFishingSeason = !isFishingSeason(level, blockPos);
            if (badWeather || notFishingSeason) {
                original.removeIf(stack ->
                        stack.is(ItemTags.FISHES) &&
                                (badWeather || level.getRandom().nextInt(2) == 0)
                );
            }
        }
        return original;
    }
}
