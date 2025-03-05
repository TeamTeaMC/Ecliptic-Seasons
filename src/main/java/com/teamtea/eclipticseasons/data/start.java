package com.teamtea.eclipticseasons.data;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.data.advancement.Advancements;
import com.teamtea.eclipticseasons.data.datapack.DatapackRegistryGenerator;
import com.teamtea.eclipticseasons.data.font.ESFontProvider;
import com.teamtea.eclipticseasons.data.lang.Lang_EN;
import com.teamtea.eclipticseasons.data.lang.Lang_ZH;
import com.teamtea.eclipticseasons.data.loot.EclipticSeasonsLootTableProvider;
import com.teamtea.eclipticseasons.data.model.BlockStatesDataProvider;
import com.teamtea.eclipticseasons.data.model.ESBlockModelProvider;
import com.teamtea.eclipticseasons.data.model.ESItemModelProvider;
import com.teamtea.eclipticseasons.data.recipe.ESRecipeProvider;
import com.teamtea.eclipticseasons.data.tag.CropClimateTagsDataProvider;
import com.teamtea.eclipticseasons.data.tag.ESBlockTagProvider;
import com.teamtea.eclipticseasons.data.tag.ESItemTagProvider;
import com.teamtea.eclipticseasons.data.tag.TagsDataProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;


import java.util.concurrent.CompletableFuture;


public class start {
    public final static String MODID = EclipticSeasons.MODID;

    public static void dataGen(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        if (event.includeServer()) {
            EclipticSeasons.logger("Generate We Data!!!");

            RegistryAccess.Frozen registryaccess$frozen = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
            HolderLookup.Provider modProvider = DatapackRegistryGenerator.REGISTRY_SET_BUILDER.build(registryaccess$frozen);
            CompletableFuture<HolderLookup.Provider> modFuture = CompletableFuture.supplyAsync(() -> modProvider, Util.backgroundExecutor());
            generator.addProvider(event.includeServer(), new CropClimateTagsDataProvider(packOutput, modFuture, MODID, helper));

            generator.addProvider(event.includeServer(), new TagsDataProvider(packOutput, lookupProvider, MODID, helper));
            var esb = new ESBlockTagProvider(packOutput, lookupProvider, MODID, helper);
            generator.addProvider(event.includeServer(), esb);
            generator.addProvider(event.includeServer(),new ESItemTagProvider(packOutput,lookupProvider,esb.contentsGetter()));

            generator.addProvider(event.includeServer(),new Advancements(packOutput,lookupProvider,helper));

            generator.addProvider(event.includeServer(),new ESRecipeProvider(packOutput));
            generator.addProvider(event.includeServer(),new EclipticSeasonsLootTableProvider(packOutput));


            generator.addProvider(event.includeServer(), new DatapackRegistryGenerator(packOutput, lookupProvider));

        }
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(),new Lang_EN(packOutput,helper));
            generator.addProvider(event.includeClient(),new Lang_ZH(packOutput,helper));

            generator.addProvider(event.includeClient(), new BlockStatesDataProvider(packOutput, helper));
            generator.addProvider(event.includeClient(), new ESItemModelProvider(packOutput, MODID, helper));
            generator.addProvider(event.includeClient(), new ESBlockModelProvider(packOutput, MODID, helper));
            generator.addProvider(event.includeClient(), new ESFontProvider(packOutput, MODID, helper));

        }


    }
}
