package com.teamtea.eclipticseasons.data.general.advancement;

import com.teamtea.eclipticseasons.api.constant.simulation.SeasonalSimulationLevel;
import com.teamtea.eclipticseasons.common.resource.conditions.SeasonalSimulationLevelCondition;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeAdvancementProvider;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Advancements implements DataProvider  {
    private final PackOutput.PathProvider pathProvider;
    private final List<AdvancementSubProvider> subProviders;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public Advancements(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "advancements");
        this.subProviders = Stream.of(new ESAdvancementGenerator()).map(generator -> generator.toSubProvider(existingFileHelper)).toList();
        this.registries = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        SeasonalSimulationLevelCondition condition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.AGRICULTURE).build();
        SeasonalSimulationLevelCondition surviveCondition = SeasonalSimulationLevelCondition.builder().level(SeasonalSimulationLevel.SURVIVAL).build();

        return this.registries.thenCompose((p_255484_) -> {
            Set<ResourceLocation> set = new HashSet<>();
            List<CompletableFuture<?>> list = new ArrayList<>();
            Consumer<Advancement> consumer = (p_253397_) -> {
                if (!set.add(p_253397_.getId())) {
                    throw new IllegalStateException("Duplicate advancement " + p_253397_.getId());
                } else {
                    Path path = this.pathProvider.json(p_253397_.getId());
                    ResourceLocation id = p_253397_.getId();
                    ConditionalAdvancement.Builder builder = ConditionalAdvancement.builder();
                    if (id.getPath().equals("main/heat_stroke")) {
                        builder.addCondition(surviveCondition).addAdvancement(p_253397_.deconstruct());
                        list.add(DataProvider.saveStable(pOutput, builder.write(), path));
                    } else if (Set.of("main/green_house", "main/greenhouse_core_container","main/humidity_tank","main/dehumidifier","main/seasonal_prayer_scroll","main/seasonal_ritual","main/quest").contains(id.getPath())
                            || id.getPath().startsWith("quests/")) {
                        builder.addCondition(condition).addAdvancement(p_253397_.deconstruct());
                        list.add(DataProvider.saveStable(pOutput, builder.write(), path));
                    } else {
                        list.add(DataProvider.saveStable(pOutput, p_253397_.deconstruct().serializeToJson(), path));
                    }

                }
            };

            for (AdvancementSubProvider advancementsubprovider : this.subProviders) {
                advancementsubprovider.generate(p_255484_, consumer);
            }

            return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "ES Ads";
    }
}
