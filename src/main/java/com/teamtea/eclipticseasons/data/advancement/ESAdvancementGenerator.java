package com.teamtea.eclipticseasons.data.advancement;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import com.teamtea.eclipticseasons.common.registry.ItemRegistry;
import net.minecraft.advancements.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Consumer;

public class ESAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
    @Override
    public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
        AdvancementHolder seasons = Advancement.Builder.advancement()
                .display(ItemRegistry.calendar_item.get(),
                        Component.translatable("advancement.eclipticseasons.root"),
                        Component.translatable("advancement.eclipticseasons.root.desc"),
                        ResourceLocation.parse("minecraft:textures/block/bricks.png"),
                        AdvancementType.TASK, true, true, false)
                .addCriterion("solar_terms", SolarTermsCriterion.TriggerInstance.simple())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(consumer, getNameId("main/root"));

        AdvancementHolder heatStroke = Advancement.Builder.advancement()
                .parent(seasons)
                .display(Items.MAGMA_BLOCK,
                        Component.translatable("advancement.eclipticseasons.heat_stroke"),
                        Component.translatable("advancement.eclipticseasons.heat_stroke.desc"),
                        null,
                        AdvancementType.TASK, true, false, false)
                .addCriterion("heat_stroke", SolarTermsCriterion.TriggerInstance.simple2())
                .requirements(AdvancementRequirements.Strategy.AND)
                .save(consumer, getNameId("main/heat_stroke"));
    }


    private String getNameId(String id) {
        return EclipticSeasonsApi.MODID + ":" + id;
    }
}
