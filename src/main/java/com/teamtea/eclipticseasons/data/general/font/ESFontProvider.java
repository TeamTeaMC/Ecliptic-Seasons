package com.teamtea.eclipticseasons.data.general.font;

import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.font.providers.BitmapProvider;
import net.minecraft.client.gui.font.providers.GlyphProviderDefinition;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ESFontProvider implements DataProvider {

    private final PackOutput output;
    protected final String modid;
    public final ExistingFileHelper helper;

    protected final Map<ResourceLocation, FontManager.FontDefinitionFile> outMap = new HashMap<>();


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

        // RecordBuilder<JsonElement> builder
        //  = BitmapProvider.Definition.CODEC.encode(definition, JsonOps.INSTANCE, builder);
        //
        // JsonElement j = FontManager.FontDefinitionFile.CODEC
        //         .encode(new FontManager.FontDefinitionFile(conditionals),
        //                 builder.ops(), new JsonObject()).result().orElse(new JsonObject());

        return CompletableFuture.allOf(outMap.entrySet().stream()
                .map(e -> DataProvider.saveStable(output, FontManager.FontDefinitionFile.CODEC
                        .encode(e.getValue(),
                                JsonOps.INSTANCE, new JsonObject()).result().orElse(new JsonObject()), path.resolve(e.getKey().withSuffix(".json").getPath()))
                ).toArray(CompletableFuture[]::new));
        // return DataProvider.saveStable(output, j, path.resolve(SolarTerm.getFont().withSuffix(".json").getPath()));
    }


    protected void gather() {

        outMap.put(SolarTerm.getFont(), new FontManager.FontDefinitionFile(List.of(buildFont(SolarTerm.getFontIcon(), IntStream.range(0, 4)
                .mapToObj(ESFontProvider::buildRow)
                .toList()))));

        outMap.put(EclipticSeasons.rl("monsoon_icons"), new FontManager.FontDefinitionFile(List.of(
                buildFont(EclipticSeasons.rl("font/season_phase/dry_end"), List.of("a")),
                buildFont(EclipticSeasons.rl("font/season_phase/dry_middle"), List.of("b")),
                buildFont(EclipticSeasons.rl("font/season_phase/dry_start"), List.of("c")),
                buildFont(EclipticSeasons.rl("font/season_phase/rain_end"), List.of("d")),
                buildFont(EclipticSeasons.rl("font/season_phase/rain_middle"), List.of("e")),
                buildFont(EclipticSeasons.rl("font/season_phase/rain_start"), List.of("f")),
                buildFont(EclipticSeasons.rl("font/season_phase/wet_end"), List.of("g")),
                buildFont(EclipticSeasons.rl("font/season_phase/wet_middle"), List.of("h")),
                buildFont(EclipticSeasons.rl("font/season_phase/wet_start"), List.of("i"))
        )));
    }

    private GlyphProviderDefinition buildFont(ResourceLocation iconCollection, List<String> a) {
        return new BitmapProvider.Definition(
                iconCollection.withSuffix(".png"),
                9,
                7,
                toInts(a)
        );
    }

    private static String buildRow(int rowIndex) {
        return IntStream.range(0, 6)
                .mapToObj(j -> SolarTerm.collectValues()[rowIndex * 6 + j].getFontLabel())
                .collect(Collectors.joining());
    }

    @Override
    public String getName() {
        return "ES Font Provider";
    }
}
