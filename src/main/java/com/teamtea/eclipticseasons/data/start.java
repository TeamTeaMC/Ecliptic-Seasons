package com.teamtea.eclipticseasons.data;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.data.api.MutablePackOutput;
import com.teamtea.eclipticseasons.data.extend.example.*;
import com.teamtea.eclipticseasons.data.extend.extra_snow.DatapackRegistryGeneratorExtra;
import com.teamtea.eclipticseasons.data.extend.extra_snow.ExtraClientModelDefinitionProvider;
import com.teamtea.eclipticseasons.data.extend.regional_snow_time.RegionalSnowTimeProvider;
import com.teamtea.eclipticseasons.data.general.advancement.Advancements;
import com.teamtea.eclipticseasons.data.general.datapack.DatapackRegistryGenerator;
import com.teamtea.eclipticseasons.data.general.datapack.ESDataMapProvider;
import com.teamtea.eclipticseasons.data.general.datapack.client.*;
import com.teamtea.eclipticseasons.data.general.font.ESFontProvider;
import com.teamtea.eclipticseasons.data.general.lang.Lang_EN;
import com.teamtea.eclipticseasons.data.general.lang.Lang_ZH;
import com.teamtea.eclipticseasons.data.general.loot.EclipticSeasonsLootTableProvider;
import com.teamtea.eclipticseasons.data.general.model.ES2ModelProvider;
import com.teamtea.eclipticseasons.data.general.recipe.ESRecipeProvider;
import com.teamtea.eclipticseasons.data.general.sound.ESSoundDefinitionsProvider;
import com.teamtea.eclipticseasons.data.general.tag.*;
import net.minecraft.util.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.data.event.GatherDataEvent;


import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;


public class start {
    public final static String MODID = EclipticSeasonsApi.MODID;

    public static void dataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();

        MutablePackOutput packOutput = new MutablePackOutput(generator.getPackOutput());
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (event instanceof GatherDataEvent.Server) {
            EclipticSeasons.logger("Generate We Data!!!");

            generator.addProvider(true, new TagsDataProvider(packOutput, lookupProvider, MODID));

            RegistryAccess.Frozen registryaccess$frozen = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            HolderLookup.Provider modProvider = DatapackRegistryGenerator.REGISTRY_SET_BUILDER.build(registryaccess$frozen);
            CompletableFuture<HolderLookup.Provider> modFuture = CompletableFuture.supplyAsync(() -> modProvider, Util.backgroundExecutor());
            generator.addProvider(true, new CropClimateTagsDataProvider(packOutput, modFuture, MODID));
            generator.addProvider(true, new TimeLineTagDataProvider(packOutput, modFuture, MODID));

            generator.addProvider(true, new EffectTagsDataProvider(packOutput, lookupProvider, MODID));
            generator.addProvider(true, new EnhancementTagsDataProvider(packOutput, lookupProvider, MODID));

            var esb = new ESBlockTagProvider(packOutput, lookupProvider, MODID);
            generator.addProvider(true, esb);
            generator.addProvider(true, new ESItemTagProvider(packOutput, lookupProvider));
            generator.addProvider(true, new ESEntityTypeTagsProvider(packOutput, lookupProvider, MODID));

            generator.addProvider(true, new ESRecipeProvider.Runner(packOutput, lookupProvider));

            generator.addProvider(true, new Advancements(packOutput, lookupProvider));
            generator.addProvider(true, new EclipticSeasonsLootTableProvider(packOutput, lookupProvider));

            generator.addProvider(true, new DatapackRegistryGenerator(packOutput, lookupProvider));

            generator.addProvider(true, new ESDataMapProvider(packOutput, lookupProvider));


        }
        if (event instanceof GatherDataEvent.Client) {
            generator.addProvider(true, new Lang_EN(packOutput));
            generator.addProvider(true, new Lang_ZH(packOutput));

            generator.addProvider(true, new ES2ModelProvider(packOutput, MODID));
            // generator.addProvider(true, new ESBlockModelProvider(packOutput, MODID));
            // generator.addProvider(true, new BlockStatesDataProvider(packOutput));
            // generator.addProvider(true, new ESItemModelProvider(packOutput, MODID));

            generator.addProvider(true, new ESFontProvider(packOutput, MODID));

            generator.addProvider(true, new ESSoundDefinitionsProvider(packOutput, MODID));


            generator.addProvider(true, new SeasonalBiomeAmbientProvider(packOutput, MODID, lookupProvider));
            generator.addProvider(true, new ClientModelDefinitionProvider(packOutput, MODID, lookupProvider));
            // generator.addProvider(true, new ClientTestProvider(packOutput, MODID, lookupProvider));
            generator.addProvider(true, new ClientSeasonModelDefinitionProvider(packOutput, MODID, lookupProvider));

        }

        // Extra Snow
        packOutput = packOutput.move(Path.of("resourcepacks", "extra_snow"));
        if (event instanceof GatherDataEvent.Server) {
            generator.addProvider(true, new DatapackRegistryGeneratorExtra(packOutput, lookupProvider));
        }
        if (event instanceof GatherDataEvent.Client) {
            generator.addProvider(true, new ExtraClientModelDefinitionProvider(packOutput, MODID, lookupProvider));
        }

        // Example
        packOutput = packOutput.move(Path.of("resourcepacks", "example"));
        if (event instanceof GatherDataEvent.Server) {
            generator.addProvider(true, new DatapackRegistryGeneratorExample(packOutput, lookupProvider));
            generator.addProvider(true, new ExampleLootTableProvider(packOutput, lookupProvider));
        }
        if (event instanceof GatherDataEvent.Client) {
            generator.addProvider(true, new SeasonTextureProvider(packOutput, MODID, lookupProvider));
            generator.addProvider(true, new BiomeColorProvider(packOutput, MODID, lookupProvider));
            generator.addProvider(true, new LeafColorProvider(packOutput, MODID, lookupProvider));
            generator.addProvider(true, new ClientSnowDefinitionProvider(packOutput, MODID, lookupProvider));
            generator.addProvider(true, new SeasonalBackgroundMusicProvider(packOutput, MODID, lookupProvider));
        }

        // Regional Snow
        packOutput = packOutput.move(Path.of("resourcepacks", "Regional Snow Time"));
        if (event instanceof GatherDataEvent.Server) {
            generator.addProvider(true, PackMetadataGenerator.forFeaturePack(packOutput, Component.translatable("pack.eclipticseasons.regional_snow_time.description")));
            generator.addProvider(true, new RegionalSnowTimeProvider(packOutput, lookupProvider));
        }
    }
}
