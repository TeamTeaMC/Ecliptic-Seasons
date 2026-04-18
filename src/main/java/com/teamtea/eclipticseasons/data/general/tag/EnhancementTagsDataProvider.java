package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.api.constant.tag.ESEnchantmentTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EnhancementTagsDataProvider extends KeyTagProvider<Enchantment> {

    public EnhancementTagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, Registries.ENCHANTMENT, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(ESEnchantmentTags.HEATSTROKE_RESISTANT).add(Enchantments.FIRE_PROTECTION);
    }
}
