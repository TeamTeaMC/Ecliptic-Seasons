package com.teamtea.eclipticseasons.common.misc;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.constant.tag.ESEnchantmentTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESItemTags;
import com.teamtea.eclipticseasons.api.constant.tag.ESMobEffectTags;
import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.SolarHolders;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.common.registry.ModAdvancements;
import lombok.Data;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Data
public class HeatStrokeTicker implements ICapabilityProvider {
    public static final Capability<HeatStrokeTicker> HEAT_STROKE_TICKER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });

    private LazyOptional<HeatStrokeTicker> lazyOptional = LazyOptional.of(() -> this);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == HEAT_STROKE_TICKER_CAPABILITY) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    public int tick = 0;

    public long lastTime = -1;

    public static int MAX_TICK_COUNT = 20;

    protected static int getLastCheckTimeLimit() {
        return EclipticUtil.getDayLengthInMinecraftStatic();
    }

    public void tickPlayer(ServerPlayer player, Level level) {
        if (level.getGameTime() - lastTime > getLastCheckTimeLimit()) {
            tick = 0;
        }

        SolarHolders.getSaveDataLazy(level).ifPresent(solarDataManager -> {
            if (EclipticUtil.getNowSolarTerm(level).isInTerms(SolarTerm.BEGINNING_OF_SUMMER, SolarTerm.BEGINNING_OF_AUTUMN)) {
                Biome b = level.getBiome(player.blockPosition()).value();
                if (EclipticUtil.getTemperatureFloat(level, b, player.blockPosition()) > 0.85f) {
                    if (!player.isInWaterOrRain()
                            && ((EclipticUtil.isNoon(level) && (level.canSeeSky(player.blockPosition()))))
                    ) {
                        boolean isColdHe = false;
                        armorChecks:
                        for (ItemStack itemstack : player.getArmorSlots()) {
                            Item item = itemstack.getItem();
                            if (item instanceof Equipable equipable) {
                                if (equipable.getEquipmentSlot() == EquipmentSlot.HEAD) {
                                    if (itemstack.is(ESItemTags.HEAT_PROTECTIVE_HELMETS)) {
                                        isColdHe = true;
                                        break;
                                    }
                                    Map<Enchantment, Integer> allEnchantments = itemstack.getAllEnchantments();
                                    if (!allEnchantments.isEmpty()) {
                                        for (Enchantment enchantment : allEnchantments.keySet()) {
                                            Optional<Holder<Enchantment>> holder = ForgeRegistries.ENCHANTMENTS.getHolder(enchantment);
                                            if (holder.isPresent() && holder.get().is(ESEnchantmentTags.HEATSTROKE_RESISTANT)) {
                                                isColdHe = true;
                                                break armorChecks;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!isColdHe) {
                            NonNullList<ItemStack> items = player.getInventory().items;
                            int selectionSize = Inventory.getSelectionSize();
                            for (int i = 0, itemsSize = items.size(); i < itemsSize && i < selectionSize; i++) {
                                ItemStack itemstack = items.get(i);
                                if (itemstack.is(ESItemTags.COOLING_ITEMS)) {
                                    isColdHe = true;
                                    break;
                                }
                            }
                        }
                        if (!isColdHe) {
                            isColdHe = player.hasEffect(MobEffects.FIRE_RESISTANCE);
                            for (MobEffectInstance activeEffect : player.getActiveEffects()) {
                                Optional<Holder<MobEffect>> holder = ForgeRegistries.MOB_EFFECTS.getHolder(activeEffect.getEffect());
                                if (holder.isPresent() && holder.get().is(ESMobEffectTags.HEATSTROKE_RESISTANT)) {
                                    isColdHe = true;
                                    break;
                                }
                            }
                        }

                        if (!player.hasEffect(EffectRegistry.HEAT_STROKE) && !isColdHe) {
                            tryApply(level, player);
                        } else if (isColdHe) {
                            tick = 0;
                        }
                    }
                }
            }
        });
    }

    protected void tryApply(Level level, ServerPlayer player) {
        lastTime = level.getGameTime();
        if (tick < MAX_TICK_COUNT) tick++;
        else {
            player.addEffect(new MobEffectInstance(EffectRegistry.HEAT_STROKE, 600));
            ModAdvancements.heatStrokeCriterion.trigger(player);
            tick = level.getRandom().nextInt(MAX_TICK_COUNT);
        }
    }

}
