package com.teamtea.eclipticseasons.mixin.common.entity;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.quest.SeasonQuest;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(AbstractVillager.class)
public abstract class MixinWanderingTrader extends Entity {
    @Shadow
    private static void addOffersFromItemListingsWithoutDuplicates(LootContext lootContext, MerchantOffers merchantOffers, HolderSet<VillagerTrade> potentialOffers, int numberOfOffers) {
    }

    public MixinWanderingTrader(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = {@At(value = "INVOKE",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/entity/npc/villager/AbstractVillager;addOffersFromItemListingsWithoutDuplicates(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/trading/MerchantOffers;Lnet/minecraft/core/HolderSet;I)V")},
            method = {"addOffersFromTradeSet"})
    public void eclipticseasons$updateTrades_wandering_trader(
            ServerLevel level,
            MerchantOffers offers,
            ResourceKey<TradeSet> resourceKey, CallbackInfo ci,
            @Local LootContext lootContext) {
        if (resourceKey.equals(TradeSets.WANDERING_TRADER_UNCOMMON) && CommonConfig.Crop.enableCrop.getAsBoolean()) {
            List<Holder<VillagerTrade>> trades = new ArrayList<>();
            SeasonQuest.buildTrades(level, trades);
            addOffersFromItemListingsWithoutDuplicates(lootContext, offers, HolderSet.direct(trades), 1);
        }
    }
}
