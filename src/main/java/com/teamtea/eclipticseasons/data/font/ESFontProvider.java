package com.teamtea.eclipticseasons.data.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.RecordBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.FontOption;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ESFontProvider implements DataProvider {

    private final PackOutput output;
    protected final String modid;
    public final ExistingFileHelper helper;

    protected final Map<ResourceLocation, BitmapProvider.Definition> bits = new HashMap<>();


    public ESFontProvider(PackOutput output, String modid, ExistingFileHelper helper) {
        this.output = output;
        this.modid = modid;
        this.helper = helper;
    }


    public int[][] toInts(List<String> p_286900_) {
        int i = p_286900_.size();
        int[][] aint = new int[i][];
        for (int j = 0; j < i; j++) {
            aint[j] = p_286900_.get(j).codePoints().toArray();
        }
        return aint;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        gather();
        Path path = this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modid).resolve("font");

        RecordBuilder<JsonElement> jsonElementRecordBuilder = JsonOps.INSTANCE.mapBuilder();

        int[][] ints = toInts(List.of("abcd", "efg\u0000"));
        BitmapProvider.Definition definition = new BitmapProvider.Definition(
                EclipticSeasons.rl("t2").withPrefix("font/").withSuffix(".png"),
                9,
                8,
                ints
        );

        jsonElementRecordBuilder = BitmapProvider.Definition.CODEC.encode(
                definition, JsonOps.INSTANCE, jsonElementRecordBuilder
        );

        // JsonObject j = jsonElementRecordBuilder.build(new JsonObject()).result().orElse(null);
        JsonElement j= FontManager.FontDefinitionFile.CODEC.encode(new FontManager.FontDefinitionFile(List.of(new GlyphProviderDefinition.Conditional(definition, FontOption.Filter.ALWAYS_PASS))),
                jsonElementRecordBuilder.ops(), new JsonObject()).result().orElse(new JsonObject());
        // JsonObject outs = new JsonObject();
        // JsonArray jsonArray = new JsonArray();
        // outs.add("providers", jsonArray);
        // jsonArray.add(j);
        return DataProvider.saveStable(output, j, path.resolve(EclipticSeasons.rl("t2").withSuffix(".json").getPath()));
    }


    protected void gather() {

    }

    @Override
    public String getName() {
        return "ES Font Provider";
    }
}
