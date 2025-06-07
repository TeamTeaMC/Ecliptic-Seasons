package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;

public class ESEnchantmentTags {
    public static final TagKey<Enchantment> HEATSTROKE_RESISTANT = create("heatstroke_resistant");

    public static TagKey<Enchantment> create(String s) {
        return TagKey.create(Registries.ENCHANTMENT, EclipticSeasons.rl(s));
    }

}
