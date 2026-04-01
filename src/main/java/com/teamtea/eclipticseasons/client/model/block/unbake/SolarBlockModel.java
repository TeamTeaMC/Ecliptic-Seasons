package com.teamtea.eclipticseasons.client.model.block.unbake;

import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.api.misc.BiomeHolderPredicate;
import com.teamtea.eclipticseasons.client.model.block.state.SeasonGoingModel;
import com.teamtea.eclipticseasons.client.model.block.unbake.variant.ResolvedModelWarper;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.cuboid.CuboidFace;
import net.minecraft.client.resources.model.cuboid.CuboidModelElement;
import net.minecraft.client.resources.model.cuboid.UnbakedCuboidGeometry;
import net.minecraft.client.resources.model.geometry.UnbakedGeometry;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class SolarBlockModel {
    protected List<SeasonalTexture> seasonalTexture = null;

    public static TextureSlots toTextureSlots(
            ResolvedModel resolvedModel,
            Map<String, Identifier> textureMap) {
        HashMap<String, Material> map = new HashMap<>(resolvedModel.getTopTextureSlots().resolvedValues);

        textureMap.forEach(
                (string, location) -> {
                    if (map.containsKey(string)) {
                        map.put(string, new Material(location));
                    }
                }
        );
        return new TextureSlots(map);
    }

    public static UnbakedGeometry toUnbakedGeometry(
            ResolvedModel resolvedModel,
            Map<String, Integer> integerMap) {
        UnbakedGeometry topGeometry = resolvedModel.getTopGeometry();
        if (topGeometry instanceof UnbakedCuboidGeometry(List<CuboidModelElement> cuboidModelElements)) {
            if (cuboidModelElements.isEmpty() || integerMap.isEmpty()) return topGeometry;
            List<CuboidModelElement> elements = cuboidModelElements;
            for (int i = 0; i < cuboidModelElements.size(); i++) {
                CuboidModelElement element = cuboidModelElements.get(i);
                EnumMap<Direction, CuboidFace> elementFace = new EnumMap<>(Direction.class);
                element.faces().forEach((direction, face) -> {
                    Integer orDefault = integerMap.getOrDefault(face.texture(), null);
                    if (orDefault != null && face.tintIndex() != orDefault) {
                        elementFace.put(direction,
                                new CuboidFace(face.cullForDirection(), orDefault, face.texture(), face.uvs(), face.rotation(), face.faceData(), face.parent()));
                    } else {
                        elementFace.put(direction, face);
                    }
                });

                // element.faces.putAll(elementFace);
                CuboidModelElement blockElement = new CuboidModelElement(element.from(), element.to(), elementFace, element.rotation(), element.shade(), element.lightEmission(), element.faceData());
                elements.set(i, blockElement);
            }
            topGeometry = new UnbakedCuboidGeometry(elements);
        }
        return topGeometry;
    }

    public static ResolvedModel to(ResolvedModel resolvedModel, Map<String, Identifier> textureMap, Map<String, Integer> tintMap) {
        TextureSlots topTextureSlots = toTextureSlots(resolvedModel, textureMap);
        UnbakedGeometry unbakedGeometry = toUnbakedGeometry(resolvedModel, tintMap);
        return new ResolvedModelWarper(resolvedModel)
                .setNewT(topTextureSlots)
                .setNewG(unbakedGeometry);
    }

    public static List<ResolvedModel> toList(ResolvedModel resolvedModel, List<Map<String, Identifier>> stringStringMap, Map<String, Integer> stringIntegerMap) {
        return stringStringMap.stream().map(m -> to(resolvedModel, m, stringIntegerMap)).toList();
    }

    public static List<Pair<ResolvedModel, ResolvedModel>> toPairList(ResolvedModel resolvedModel, List<Pair<Map<String, Identifier>, Map<String, Identifier>>> stringStringMap, Map<String, Integer> stringIntegerMap) {
        return stringStringMap.stream().map(p -> Pair.of(to(resolvedModel, p.getFirst(), stringIntegerMap), to(resolvedModel, p.getSecond(), stringIntegerMap))).toList();
    }


    public static BlockStateModel bake(@NonNull ModelBaker baker, ResolvedModel original, List<SeasonalTexture> seasonalTexture) {

        BlockStateModelPart bake = SimpleModelWrapper.bake(baker, original, BlockModelRotation.IDENTITY);

        // BlockStateModel end = null;
        List<SeasonGoingModel.Holder> defaultList = new ArrayList<>();
        List<Pair<BiomeHolderPredicate, SeasonGoingModel.Holder>> list = new ArrayList<>();

        Map<ResolvedModel, BlockStateModelPart> bakedCache =
                new Object2ObjectOpenCustomHashMap<>(new Hash.Strategy<>() {
                    @Override
                    public int hashCode(ResolvedModel blockModel) {
                        return Objects.hash(blockModel.getTopTextureSlots());
                    }

                    @Override
                    public boolean equals(ResolvedModel a, ResolvedModel b) {
                        if (a == b) return true;
                        if (b == null || a.getClass() != b.getClass()) return false;
                        return (a.getTopTextureSlots().equals(b.getTopTextureSlots())
                                && a.getTopGeometry().equals(b.getTopGeometry()));
                    }
                });

        for (SeasonalTexture texture : seasonalTexture) {
            EnumMap<SolarTerm, List<Pair<BlockStateModelPart, BlockStateModelPart>>> solarTermBlockStateModelPartEnumMap = new EnumMap<>(SolarTerm.class);
            EnumMap<SolarTerm, List<Pair<BlockStateModelPart, BlockStateModelPart>>> snowSolarTermBlockStateModelPartEnumMap = new EnumMap<>(SolarTerm.class);
            texture.getFlatSliceEnumMap()
                    .forEach(
                            (solarTerm, flatSliceHolders) -> {
                                if (flatSliceHolders.flatSlice().mid() != null)
                                    solarTermBlockStateModelPartEnumMap.put(solarTerm,
                                            toList(original, flatSliceHolders.flatSlice().mid(), flatSliceHolders.flatSlice().tintMap()).stream().map(
                                                    b -> {
                                                        BlockStateModelPart sliceModel = bakedCache.computeIfAbsent(b, (blockModel -> SimpleModelWrapper.bake(baker, blockModel, BlockModelRotation.IDENTITY)));
                                                        return Pair.of(sliceModel, sliceModel);
                                                    }
                                            ).toList()
                                    );
                                if (flatSliceHolders.flatSlice().transitionModels() != null)
                                    solarTermBlockStateModelPartEnumMap.put(solarTerm,
                                            toPairList(original, flatSliceHolders.flatSlice().transitionModels(), flatSliceHolders.flatSlice().tintMap()).stream().map(
                                                    b -> Pair.of(bakedCache.computeIfAbsent(b.getFirst(), (blockModel -> SimpleModelWrapper.bake(baker, blockModel, BlockModelRotation.IDENTITY))),
                                                            bakedCache.computeIfAbsent(b.getSecond(), (blockModel -> SimpleModelWrapper.bake(baker, blockModel, BlockModelRotation.IDENTITY))))
                                            ).toList()
                                    );
                                if (flatSliceHolders.snowSlice().mid() != null)
                                    snowSolarTermBlockStateModelPartEnumMap.put(solarTerm,
                                            toList(original, flatSliceHolders.snowSlice().mid(), flatSliceHolders.snowSlice().tintMap()).stream().map(
                                                    b -> {
                                                        BlockStateModelPart sliceModel = bakedCache.computeIfAbsent(b, (blockModel -> SimpleModelWrapper.bake(baker, blockModel, BlockModelRotation.IDENTITY)));
                                                        return Pair.of(sliceModel, sliceModel);
                                                    }
                                            ).toList()
                                    );
                                if (flatSliceHolders.snowSlice().transitionModels() != null)
                                    snowSolarTermBlockStateModelPartEnumMap.put(solarTerm,
                                            toPairList(original, flatSliceHolders.snowSlice().transitionModels(), flatSliceHolders.snowSlice().tintMap()).stream().map(
                                                    b -> Pair.of(bakedCache.computeIfAbsent(b.getFirst(), (blockModel -> SimpleModelWrapper.bake(baker, blockModel, BlockModelRotation.IDENTITY))),
                                                            bakedCache.computeIfAbsent(b.getSecond(), (blockModel -> SimpleModelWrapper.bake(baker, blockModel, BlockModelRotation.IDENTITY))))
                                            ).toList()
                                    );
                            }
                    );

            if (texture.getBiomes().isEmpty()) {
                defaultList.add(new SeasonGoingModel.Holder(
                        solarTermBlockStateModelPartEnumMap,
                        snowSolarTermBlockStateModelPartEnumMap
                ));
            } else {
                BiomeHolderPredicate biomePredicate = (biomeHolder -> {
                    var biomes = texture.getBiomes();
                    if (biomes.isEmpty()) return true;
                    var either = biomes.get();
                    if (either.left().isPresent()) {
                        Optional<ResourceKey<Biome>> biomeResourceKey = biomeHolder.unwrapKey();
                        if (biomeResourceKey.isPresent()) {
                            return either.left().get().contains(biomeResourceKey.get().identifier());
                        }
                    }
                    if (either.right().isPresent()) {
                        return biomeHolder.is(either.right().get());
                    }
                    return true;
                });
                list.add(Pair.of(biomePredicate, new SeasonGoingModel.Holder(
                        solarTermBlockStateModelPartEnumMap,
                        snowSolarTermBlockStateModelPartEnumMap
                )));
            }
        }


        return new SeasonGoingModel(bake, defaultList, list);
    }

}

