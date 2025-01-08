package com.teamtea.eclipticseasons.data.advancement;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.advancement.SolarTermsCriterion;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.util.function.Consumer;

public class ESAdvancementGenerator implements ForgeAdvancementProvider.AdvancementGenerator {

    private String getNameId(String id) {
        return EclipticSeasonsApi.MODID + ":" + id;
    }


    @Override
    public void generate(HolderLookup.Provider registries, Consumer<Advancement> consumer, ExistingFileHelper existingFileHelper) {
        Advancement seasons = Advancement.Builder.advancement()
                .display(EclipticSeasons.ModContents.calendar_item.get(),
                        Component.translatable("advancement.eclipticseasons.root"),
                        Component.translatable("advancement.eclipticseasons.root.desc"),
                        new ResourceLocation("minecraft:textures/block/bricks.png"),
                        FrameType.TASK, true, true, false)
                .addCriterion("solar_terms", SolarTermsCriterion.TriggerInstance.simple())
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId("main/root"));

        Advancement heatStroke = Advancement.Builder.advancement()
                .parent(seasons)
                .display(Items.MAGMA_BLOCK,
                        Component.translatable("advancement.eclipticseasons.heat_stroke"),
                        Component.translatable("advancement.eclipticseasons.heat_stroke.desc"),
                        null,
                        FrameType.TASK, true, false, false)
                .addCriterion("heat_stroke", SolarTermsCriterion.TriggerInstance.simple2())
                .requirements(RequirementsStrategy.AND)
                .save(consumer, getNameId("main/heat_stroke"));
    }
}
