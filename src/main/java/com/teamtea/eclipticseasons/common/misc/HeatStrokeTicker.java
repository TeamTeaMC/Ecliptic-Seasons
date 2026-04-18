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
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jspecify.annotations.NonNull;

import java.util.Set;

@Data
public class HeatStrokeTicker {
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
                Biome biome = level.getBiome(player.blockPosition()).value();
                if (EclipticUtil.getTemperatureFloat(level, biome, player.blockPosition()) > 0.85f) {
                    if (!player.isInWaterOrRain()
                            && ((EclipticUtil.isNoon(level)
                            && (level.canSeeSky(player.blockPosition()))))
                    ) {
                        boolean isColdHe = false;
                        armorChecks:
                        for (EquipmentSlot slot : EquipmentSlot.values()) {
                            ItemStack itemstack = player.getItemBySlot(slot);
                            if (itemstack.get(DataComponents.EQUIPPABLE) instanceof Equippable equippable) {
                                if (equippable.slot() == EquipmentSlot.HEAD) {
                                    if (itemstack.is(ESItemTags.HEAT_PROTECTIVE_HELMETS)) {
                                        isColdHe = true;
                                        break;
                                    }
                                    ItemEnchantments allEnchantments = itemstack.getAllEnchantments(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT));
                                    Set<Holder<Enchantment>> keySet = allEnchantments.keySet();
                                    if (!keySet.isEmpty()) {
                                        for (Holder<Enchantment> enchantment : keySet) {
                                            if (enchantment.is(ESEnchantmentTags.HEATSTROKE_RESISTANT)) {
                                                isColdHe = true;
                                                break armorChecks;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (!isColdHe) {
                            NonNullList<ItemStack> items = player.getInventory().getNonEquipmentItems();
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
                                if (activeEffect.getEffect().is(ESMobEffectTags.HEATSTROKE_RESISTANT)) {
                                    isColdHe = true;
                                    break;
                                }
                            }
                        }

                        var heatStroke = BuiltInRegistries.MOB_EFFECT.get(EffectRegistry.Effects.HEAT_STROKE).get();
                        if (!player.hasEffect(heatStroke) && !isColdHe) {
                            tryApply(level, player, heatStroke);
                        } else if (isColdHe) {
                            tick = 0;
                        }
                    }
                }
            }
        });

    }


    protected void tryApply(Level level, ServerPlayer player, Holder<@NonNull MobEffect> heatStroke) {
        lastTime = level.getGameTime();
        if (tick < MAX_TICK_COUNT) tick++;
        else {
            player.addEffect(new MobEffectInstance(heatStroke, 600));
            ModAdvancements.heatStrokeCriterion.get().trigger(player);
            tick = level.getRandom().nextInt(MAX_TICK_COUNT);
        }
    }

    public static HeatStrokeTicker empty(IAttachmentHolder iAttachmentHolder) {
       return new HeatStrokeTicker();
    }
}
