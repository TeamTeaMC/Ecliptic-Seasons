package com.teamtea.eclipticseasons.api.constant.tag;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ESItemTags {
    public static final TagKey<Item> COOLING_ITEMS = create("cooling_items");
    public static final TagKey<Item> HEAT_PROTECTIVE_HELMETS = create("heat_protective_helmets");
    public static final TagKey<Item> UNAFFECTED_BY_SEASONS = create(("crops/unaffected_by_seasons"));
    public static final TagKey<Item> UNAFFECTED_BY_HUMIDITY = create(("crops/unaffected_by_humidity"));
    public static TagKey<Item> create(String s) {
        return TagKey.create(Registries.ITEM, EclipticSeasons.rl(s));
    }
}
