package com.teamtea.eclipticseasons.data.font;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.RecordBuilder;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
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
import java.util.ArrayList;
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

        RecordBuilder<JsonElement> builder = JsonOps.INSTANCE.mapBuilder();


        List<GlyphProviderDefinition.Conditional> conditionals=new ArrayList<>();
        // for (SolarTerm solarTerm : SolarTerm.collectValues()) {
        //     // int[][] ints = toInts(List.of("abcd", "efg\u0000"));
        //     int[][] ints = toInts(List.of(solarTerm.getFontLabel()));
        //     BitmapProvider.Definition definition = new BitmapProvider.Definition(
        //             solarTerm.getIcon().withSuffix(".png"),
        //             9,
        //             8,
        //             ints
        //     );
        //
        //     builder = BitmapProvider.Definition.CODEC.encode(definition, JsonOps.INSTANCE, builder);
        //
        //     GlyphProviderDefinition.Conditional conditional =
        //             new GlyphProviderDefinition.Conditional(definition, FontOption.Filter.ALWAYS_PASS);
        //     conditionals.add(conditional);
        // }
        List<String> strings=new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            StringBuilder stringBuilder=new StringBuilder();
            for (int j = 0; j < 6; j++) {
                stringBuilder.append(SolarTerm.collectValues()[i*6+j].getFontLabel());
            }
            strings.add(stringBuilder.toString());
        }

        int[][] ints = toInts(strings);
        BitmapProvider.Definition definition = new BitmapProvider.Definition(
                SolarTerm.getFontIcon().withSuffix(".png"),
                9,
                7,
              // 16,15,
                ints
        );


        builder = BitmapProvider.Definition.CODEC.encode(definition, JsonOps.INSTANCE, builder);

        GlyphProviderDefinition.Conditional conditional =
                new GlyphProviderDefinition.Conditional(definition, FontOption.Filter.ALWAYS_PASS);
        conditionals.add(conditional);


        // JsonObject j = builder.build(new JsonObject()).result().orElse(null);
        JsonElement j= FontManager.FontDefinitionFile.CODEC
                .encode(new FontManager.FontDefinitionFile(conditionals),
                builder.ops(), new JsonObject()).result().orElse(new JsonObject());
        // JsonObject outs = new JsonObject();
        // JsonArray jsonArray = new JsonArray();
        // outs.add("providers", jsonArray);
        // jsonArray.add(j);
        return DataProvider.saveStable(output, j, path.resolve(SolarTerm.getFont().withSuffix(".json").getPath()));
    }


    protected void gather() {

    }

    @Override
    public String getName() {
        return "ES Font Provider";
    }
}
