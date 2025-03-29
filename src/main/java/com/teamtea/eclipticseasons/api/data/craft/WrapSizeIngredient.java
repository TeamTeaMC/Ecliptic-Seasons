package com.teamtea.eclipticseasons.api.data.craft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record WrapSizeIngredient(
        HolderSet<Item> item, int count
) {

    public static final Codec<WrapSizeIngredient> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("item").forGetter(WrapSizeIngredient::item),
                    Codec.INT.fieldOf("count").orElse(1).forGetter(WrapSizeIngredient::count)
            ).apply(instance, WrapSizeIngredient::new)
    );

    public boolean test(ItemStack stack) {
        return item.contains(stack.getItemHolder()) && stack.getCount() >= count;
    }

    public ItemStack[] getItems() {
        return item().stream()
                .map(itemHolder -> itemHolder.get().getDefaultInstance())
                .toArray(ItemStack[]::new);
    }
}
