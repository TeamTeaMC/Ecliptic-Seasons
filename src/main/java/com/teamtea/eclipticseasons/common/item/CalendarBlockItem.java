package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CalendarBlockItem extends BlockItem {
    public CalendarBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (showHint(level, pPlayer)) {
            return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), level.isClientSide());
        }
        return super.use(level, pPlayer, pUsedHand);
    }

    @Override
    public @NotNull InteractionResult place(@NotNull BlockPlaceContext context) {
        InteractionResult interactionResult = super.place(context);
        if (interactionResult == InteractionResult.FAIL) {
            if (showHint(context.getLevel(), context.getPlayer()))
                return InteractionResult.sidedSuccess(context.getLevel().isClientSide());
        }
        return interactionResult;
    }

    public boolean showHint(Level level, Player player) {
        if (CommonConfig.Season.calendarItemHint.getAsBoolean() && MapChecker.isValidDimension(level)) {
            var season = EclipticUtil.getNowSolarTerm(level);
            player.displayClientMessage(
                    Component.translatable(
                            "item.eclipticseasons.calendar.pop_hint",
                            season.getTranslation(), EclipticUtil.getTimeInSolarTerm(level) + 1,
                            CommonConfig.Season.lastingDaysOfEachTerm.get()
                    ), true);
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;
        tooltipComponents.add(Component.translatable("info.eclipticseasons.calendar.use"));
    }
}
