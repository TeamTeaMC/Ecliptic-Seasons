package com.teamtea.eclipticseasons.mixin.compat.legendarysurvivaloverhaul.client;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.teamtea.eclipticseasons.compat.legendarysurvivaloverhaul.LSO_ESUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sfiomn.legendarysurvivaloverhaul.common.items.SeasonalCalendarItem;

@Mixin({SeasonalCalendarItem.class})
public abstract class MixinRenderSeasonCards {

    @Inject(
            method = "use",
            at = @At(value = "HEAD")
    )
    private void ecliptic$updateSeasonCardFading(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (level.isClientSide()) {
            player.displayClientMessage(LSO_ESUtil.seasonTooltip(player.blockPosition(), player.level()), true);
        }
    }

    @WrapOperation(
            method = "use",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V")
    )
    private void ecliptic$updateSeasonCardFading(Player instance, Component pChatComponent, boolean pActionBar, Operation<Void> original) {
    }
}
