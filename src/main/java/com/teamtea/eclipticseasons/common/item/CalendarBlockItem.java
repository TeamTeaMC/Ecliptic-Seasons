package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLLoader;
import org.jspecify.annotations.NonNull;

import java.util.List;
import java.util.function.Consumer;

public class CalendarBlockItem extends BlockItem {
    public CalendarBlockItem(Block pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public InteractionResult use(@NonNull Level level, @NonNull Player pPlayer, @NonNull InteractionHand pUsedHand) {
        if (showHint(level, pPlayer)) {
            return InteractionResult.CONSUME;
        }
        return super.use(level, pPlayer, pUsedHand);
    }

    @Override
    public @NonNull InteractionResult place(@NonNull BlockPlaceContext context) {
        InteractionResult interactionResult = super.place(context);
        if (interactionResult == InteractionResult.FAIL) {
            if (showHint(context.getLevel(), context.getPlayer()))
                return InteractionResult.CONSUME;
        }
        return interactionResult;
    }

    public boolean showHint(Level level, Player player) {
        if (CommonConfig.Season.calendarItemHint.getAsBoolean() && MapChecker.isValidDimension(level)) {
            var season = EclipticUtil.getNowSolarTerm(level);
            player.sendSystemMessage(
                    Component.translatable(
                            "item.eclipticseasons.calendar.pop_hint",
                            season.getTranslation(), EclipticUtil.getTimeInSolarTerm(level) + 1,
                            CommonConfig.Season.lastingDaysOfEachTerm.get()
                    ));
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
        if (FMLLoader.getCurrent().getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;
        builder.accept(Component.translatable("info.eclipticseasons.calendar.use"));
    }
}
