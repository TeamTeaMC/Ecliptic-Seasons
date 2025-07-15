package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientExtraUtil;
import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class MeterItem extends Item {
    public MeterItem(Properties properties) {
        super(properties);
    }

    public static void sendInfo(Item meterBlockItem, Level level, Player player) {

        if (level.isClientSide()) {
            BlockPos pos = player.blockPosition();
            Component component = Component.empty();
            if (meterBlockItem == ItemRegistry.hyetometer.get()) {
                component = EclipticUtil.getRainfallAt(level, pos).getTranslation();
            } else if (meterBlockItem == ItemRegistry.thermometer.get()) {
                component = EclipticUtil.getTemperatureAt(level, pos).getTranslation();
            } else if (meterBlockItem == ItemRegistry.hygrometer.get()) {
                float humidityAt = EclipticUtil.getHumidityLevelAt(level, pos);
                humidityAt = ClientExtraUtil.modifyHumidity(level, pos, humidityAt);
                component = Humidity.getHumid(humidityAt).getTranslation();
            }

            if (!component.getString().isEmpty())
                player.displayClientMessage(component, true);
        }
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        sendInfo(this, level, player);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    }

}
