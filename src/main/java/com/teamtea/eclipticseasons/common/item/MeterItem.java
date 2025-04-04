package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientExtraUtil;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MeterItem extends Item {
    public MeterItem(Properties properties) {
        super(properties);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) {
            BlockPos blockPosition = player.blockPosition();
            Component component = Component.empty();
            if (this == ItemRegistry.hyetometer.get()) {
                component = EclipticUtil.getRainfallAt(level, blockPosition).getTranslation();
            } else if (this == ItemRegistry.thermometer.get()) {
                component = EclipticUtil.getTemperatureAt(level, blockPosition).getTranslation();
            } else if (this == ItemRegistry.hygrometer.get()) {
                Humidity humidityAt = EclipticUtil.getHumidityAt(level, blockPosition);
                humidityAt = ClientExtraUtil.modifyHumidity(level, blockPosition, humidityAt);
                component = humidityAt.getTranslation();
            }

            if (!component.getString().isEmpty())
                player.displayClientMessage(component, true);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    }

}
