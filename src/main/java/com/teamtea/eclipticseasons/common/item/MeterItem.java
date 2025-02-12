package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;

public class MeterItem extends Item {
    public MeterItem(Properties properties) {
        super(properties);
    }


    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand usedHand) {
        if (level.isClientSide()) {
            BlockPos blockPosition = player.blockPosition();
            ITextComponent component = new StringTextComponent("");
            if (this == ItemRegistry.hyetometer.get()) {
                component = EclipticUtil.getRainfallAt(level, blockPosition).getTranslation();
            } else if (this == ItemRegistry.thermometer.get()) {
                component = EclipticUtil.getTemperatureAt(level, blockPosition).getTranslation();
            } else if (this == ItemRegistry.hygrometer.get()) {
                component = EclipticUtil.getHumidityAt(level, blockPosition).getTranslation();
            }

            if (!component.getString().isEmpty())
                player.displayClientMessage(component, true);
        }
        return ActionResult.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());

    }


    @Override
    public void inventoryTick(ItemStack pStack, World pLevel, Entity pEntity, int pItemSlot, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pItemSlot, pIsSelected);
    }



}
