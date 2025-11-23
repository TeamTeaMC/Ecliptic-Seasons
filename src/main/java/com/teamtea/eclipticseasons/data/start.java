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
import com.teamtea.eclipticseasons.data.general.model.BlockStatesDataProvider;
import com.teamtea.eclipticseasons.data.general.model.ESBlockModelProvider;
import com.teamtea.eclipticseasons.data.general.model.ESItemModelProvider;
import com.teamtea.eclipticseasons.data.general.recipe.ESRecipeProvider;
import com.teamtea.eclipticseasons.data.general.sound.ESSoundDefinitionsProvider;
import com.teamtea.eclipticseasons.data.general.tag.*;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;


import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;


public class start {
    public final static String MODID = EclipticSeasonsApi.MODID;

    public static void dataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        MutablePackOutput packOutput = new MutablePackOutput(generator.getPackOutput());
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (event.includeServer()) {
            EclipticSeasons.logger("Generate We Data!!!");

            generator.addProvider(event.includeServer(), new TagsDataProvider(packOutput, lookupProvider, MODID, helper));

            RegistryAccess.Frozen registryaccess$frozen = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            HolderLookup.Provider modProvider = DatapackRegistryGenerator.REGISTRY_SET_BUILDER.build(registryaccess$frozen);
            CompletableFuture<HolderLookup.Provider> modFuture = CompletableFuture.supplyAsync(() -> modProvider, Util.backgroundExecutor());
            generator.addProvider(event.includeServer(), new CropClimateTagsDataProvider(packOutput, modFuture, MODID, helper));

            generator.addProvider(event.includeServer(), new EffectTagsDataProvider(packOutput, lookupProvider, MODID, helper));
            generator.addProvider(event.includeServer(), new EnhancementTagsDataProvider(packOutput, lookupProvider, MODID, helper));

            var esb = new ESBlockTagProvider(packOutput, lookupProvider, MODID, helper);
            generator.addProvider(event.includeServer(), esb);
            generator.addProvider(event.includeServer(), new ESItemTagProvider(packOutput, lookupProvider, esb.contentsGetter()));
            generator.addProvider(event.includeServer(), new ESEntityTypeTagsProvider(packOutput, lookupProvider, MODID, helper));

            generator.addProvider(event.includeServer(), new ESRecipeProvider(packOutput, lookupProvider));

            generator.addProvider(event.includeServer(), new Advancements(packOutput, lookupProvider, helper));
            generator.addProvider(event.includeServer(), new EclipticSeasonsLootTableProvider(packOutput, lookupProvider));

            generator.addProvider(event.includeServer(), new DatapackRegistryGenerator(packOutput, lookupProvider));

            generator.addProvider(event.includeServer(), new ESDataMapProvider(packOutput, lookupProvider));

        }
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(), new Lang_EN(packOutput, helper));
            generator.addProvider(event.includeClient(), new Lang_ZH(packOutput, helper));

            generator.addProvider(event.includeClient(), new ESBlockModelProvider(packOutput, MODID, helper));
            generator.addProvider(event.includeClient(), new BlockStatesDataProvider(packOutput, helper));
            generator.addProvider(event.includeClient(), new ESItemModelProvider(packOutput, MODID, helper));

            generator.addProvider(event.includeClient(), new ESFontProvider(packOutput, MODID, helper));

            generator.addProvider(event.includeClient(), new ESSoundDefinitionsProvider(packOutput, MODID, helper));


            generator.addProvider(event.includeClient(), new SeasonalBiomeAmbientProvider(packOutput,MODID, helper,lookupProvider));
            generator.addProvider(event.includeClient(), new ClientModelDefinitionProvider(packOutput,MODID, helper,lookupProvider));
            generator.addProvider(event.includeClient(), new ClientTestProvider(packOutput,MODID, helper,lookupProvider));
            generator.addProvider(event.includeClient(), new ClientSeasonModelDefinitionProvider(packOutput,MODID, helper,lookupProvider));

        }

        // Extra Snow
        packOutput = packOutput.move(Path.of("resourcepacks", "extra_snow"));
        if (event.includeServer()) {
            generator.addProvider(event.includeServer(), new DatapackRegistryGeneratorExtra(packOutput, lookupProvider));
        }
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(), new ExtraClientModelDefinitionProvider(packOutput, MODID, helper, lookupProvider));
        }

        // Example
        packOutput = packOutput.move(Path.of("resourcepacks", "example"));
        if (event.includeServer()) {
            generator.addProvider(event.includeServer(), new DatapackRegistryGeneratorExample(packOutput, lookupProvider));
            generator.addProvider(event.includeServer(), new ExampleLootTableProvider(packOutput, lookupProvider));
        }
        if(event.includeClient()){
            generator.addProvider(event.includeClient(), new SeasonTextureProvider(packOutput,MODID, helper,lookupProvider));
            generator.addProvider(event.includeClient(), new BiomeColorProvider(packOutput,MODID, helper,lookupProvider));
            generator.addProvider(event.includeClient(), new LeafColorProvider(packOutput,MODID, helper,lookupProvider));
            generator.addProvider(event.includeClient(), new ClientSnowDefinitionProvider(packOutput,MODID, helper,lookupProvider));
        }

        // Regional Snow
        packOutput = packOutput.move(Path.of("resourcepacks", "Regional Snow Time"));
        if (event.includeServer()) {
            generator.addProvider(event.includeServer(), PackMetadataGenerator.forFeaturePack(packOutput, Component.translatable("pack.eclipticseasons.regional_snow_time.description")));
            generator.addProvider(event.includeServer(), new RegionalSnowTimeProvider(packOutput, lookupProvider));
        }
    }
}
