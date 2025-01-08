package com.teamtea.eclipticseasons.common.block;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CalendarBlockItem extends BlockItem {
    public CalendarBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag);
        // var sd = EclipticSeasonsApi.getInstance().getSolarTerm(pContext.level());
        // pTooltipComponents.add(Component.translatable("info.eclipticseasons.environment.solar_term.hint")
        //         .withStyle(ChatFormatting.GRAY));
        // pTooltipComponents.add(sd.getTranslation().withStyle(sd.getSeason().getColor()));
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
        if (MapChecker.isValidDimension(level)|| CommonConfig.Season.calendarItemHint.get()) {
            var season = EclipticUtil.getNowSolarTerm(level);
            player.displayClientMessage(
                    season.getTranslation().append(", %s/%s".formatted(
                            EclipticUtil.getTimeInSolarTerm(level),
                            CommonConfig.Season.lastingDaysOfEachTerm.get()
                    )), true);
            return true;
        }
        return false;
    }
}
