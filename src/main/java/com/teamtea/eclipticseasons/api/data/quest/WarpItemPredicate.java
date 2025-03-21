package com.teamtea.eclipticseasons.api.data.quest;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public record WarpItemPredicate(HolderSet<Item> items,
                                int count) {
    public static final Codec<WarpItemPredicate> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(WarpItemPredicate::items),
            Codec.INT.fieldOf("count").forGetter(WarpItemPredicate::count)
    ).apply(ins, WarpItemPredicate::new));

    public boolean test(ItemStack stack) {
        return items.contains(stack.getItemHolder());
    }
}
