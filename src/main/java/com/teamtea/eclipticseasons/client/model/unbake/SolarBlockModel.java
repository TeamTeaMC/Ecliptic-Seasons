package com.teamtea.eclipticseasons.client.model.unbake;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.api.misc.BiomeHolderPredicate;
import com.teamtea.eclipticseasons.client.model.SeasonBiomeGoingModel;
import com.teamtea.eclipticseasons.client.model.SeasonGoingModel;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class SolarBlockModel extends BlockModel {
    protected List<SeasonalTexture> seasonalTexture = null;

    public SolarBlockModel(@Nullable ResourceLocation parentLocation,
                           List<BlockElement> elements,
                           Map<String, Either<Material, String>> textureMap,
                           @Nullable Boolean hasAmbientOcclusion,
                           @Nullable BlockModel.GuiLight guiLight,
                           ItemTransforms transforms,
                           List<ItemOverride> overrides) {
        super(parentLocation, elements, textureMap, hasAmbientOcclusion, guiLight, transforms, overrides);
    }

    public static SolarBlockModel of(BlockModel blockModel) {
        SolarBlockModel solarBlockModel = new SolarBlockModel(blockModel.getParentLocation(),
                blockModel.getElements(),
                blockModel.textureMap,
                blockModel.hasAmbientOcclusion(),
                blockModel.getGuiLight(),
                blockModel.getTransforms(),
                blockModel.getOverrides()
        );
        solarBlockModel.customData.copyFrom(blockModel.customData);
        return solarBlockModel;
    }

    public BlockModel to(Map<String, ResourceLocation> stringStringMap, Map<String, Integer> integerMap) {
        Map<String, Either<Material, String>> map = new HashMap<>(this.textureMap);
        if(this.parent!=null) map.putAll(this.parent.textureMap);
        stringStringMap.forEach(
                (string, location) -> {
                    if (map.containsKey(string)) {
                        map.put(string, Either.left(new Material(InventoryMenu.BLOCK_ATLAS, location)));
                    }
                }
        );
        List<BlockElement> elements = this.getElements();
        if (!integerMap.isEmpty()) {
            elements = new ArrayList<>(elements);
            for (int i = 0, elementsSize = elements.size(); i < elementsSize; i++) {
                BlockElement element = elements.get(i);
                EnumMap<Direction, BlockElementFace> elementFace = new EnumMap<>(Direction.class);
                element.faces.forEach((direction, face) -> {
                    Integer orDefault = integerMap.getOrDefault(face.texture, null);
                    if (orDefault != null && face.tintIndex != orDefault) {
                        elementFace.put(direction,
                                new BlockElementFace(face.cullForDirection, orDefault, face.texture, face.uv, face.getFaceData()));
                    }else {
                        elementFace.put(direction,face);
                    }
                });
                // element.faces.putAll(elementFace);
                BlockElement blockElement = new BlockElement(element.from, element.to, elementFace, element.rotation, element.shade, element.getFaceData());
                elements.set(i,blockElement);
            }
        }
        return new BlockModel(this.getParentLocation(),
                elements,
                map,
                this.hasAmbientOcclusion(),
                this.getGuiLight(),
                this.getTransforms(),
                this.getOverrides()
        );
    }


    public List<BlockModel> toList(List<Map<String, ResourceLocation>> stringStringMap, Map<String, Integer> stringIntegerMap) {
        return stringStringMap.stream().map(m -> to(m, stringIntegerMap)).toList();
    }

    public List<Pair<BlockModel, BlockModel>> toPairList(List<Pair<Map<String, ResourceLocation>, Map<String, ResourceLocation>>> stringStringMap, Map<String, Integer> stringIntegerMap) {
        return stringStringMap.stream().map(p -> Pair.of(to(p.getFirst(), stringIntegerMap), to(p.getSecond(), stringIntegerMap))).toList();
    }

    public SolarBlockModel setSeasonalTexture(List<SeasonalTexture> seasonalTexture) {
        this.seasonalTexture = seasonalTexture;
        return this;
    }


    @Override
    public @NotNull BakedModel bake(ModelBaker baker, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, ResourceLocation pLocation, boolean guiLight3d) {
        if (seasonalTexture != null) {
            BakedModel bake = UnbakedGeometryHelper.bake(this, baker, model, spriteGetter, state, pLocation, guiLight3d);
            SeasonGoingModel<BakedModel> end = null;
            List<Pair<BiomeHolderPredicate, SeasonGoingModel<BakedModel>>> list = new ArrayList<>();
            List<Pair<BiomeHolderPredicate, SeasonGoingModel<BakedModel>>> defaultList = new ArrayList<>();

            Map<BlockModel, BakedModel> bakedCache =
                    new Object2ObjectOpenCustomHashMap<>(new Hash.Strategy<>() {
                        @Override
                        public int hashCode(BlockModel blockModel) {
                            return Objects.hash(blockModel.getElements(), blockModel.textureMap);
                        }

                        @Override
                        public boolean equals(BlockModel a, BlockModel b) {
                            if (a == b) return true;
                            if (b == null || a.getClass() != b.getClass()) return false;
                            return (a.getElements().equals(b.getElements())
                                    && a.textureMap.equals(b.textureMap));
                        }
                    });

            for (SeasonalTexture texture : seasonalTexture) {
                EnumMap<SolarTerm, List<Pair<BakedModel, BakedModel>>> solarTermBakedModelEnumMap = new EnumMap<>(SolarTerm.class);
                EnumMap<SolarTerm, List<Pair<BakedModel, BakedModel>>> snowSolarTermBakedModelEnumMap = new EnumMap<>(SolarTerm.class);

                texture.getFlatSliceEnumMap()
                        .forEach(
                                (solarTerm, flatSliceHolders) -> {
                                    if (flatSliceHolders.flatSlice().mid() != null)
                                        solarTermBakedModelEnumMap.put(solarTerm,
                                                toList(flatSliceHolders.flatSlice().mid(), flatSliceHolders.flatSlice().tintMap()).stream().map(
                                                        b -> {
                                                            BakedModel sliceModel = bakedCache.computeIfAbsent(b, (blockModel -> UnbakedGeometryHelper.bake(b, baker, model, spriteGetter, state, pLocation, guiLight3d)));
                                                            return Pair.of(sliceModel, sliceModel);
                                                        }
                                                ).toList()
                                        );

                                    if (flatSliceHolders.flatSlice().transitionModels() != null)
                                        snowSolarTermBakedModelEnumMap.put(solarTerm,
                                                toPairList(flatSliceHolders.flatSlice().transitionModels(), flatSliceHolders.flatSlice().tintMap()).stream().map(
                                                                b -> Pair.of(bakedCache.computeIfAbsent(b.getFirst(), (blockModel -> UnbakedGeometryHelper.bake(blockModel, baker, model, spriteGetter, state, pLocation, guiLight3d))),
                                                                        bakedCache.computeIfAbsent(b.getSecond(), (blockModel -> UnbakedGeometryHelper.bake(blockModel, baker, model, spriteGetter, state, pLocation, guiLight3d)))))
                                                        .toList()
                                        );

                                    if (flatSliceHolders.snowSlice().mid() != null)
                                        snowSolarTermBakedModelEnumMap.put(solarTerm,
                                                toList(flatSliceHolders.snowSlice().mid(), flatSliceHolders.snowSlice().tintMap()).stream().map(
                                                        b -> {
                                                            BakedModel sliceModel = bakedCache.computeIfAbsent(b, (blockModel -> UnbakedGeometryHelper.bake(b, baker, model, spriteGetter, state, pLocation, guiLight3d)));
                                                            return Pair.of(sliceModel, sliceModel);
                                                        }
                                                ).toList()
                                        );

                                    if (flatSliceHolders.snowSlice().transitionModels() != null)
                                        snowSolarTermBakedModelEnumMap.put(solarTerm,
                                                toPairList(flatSliceHolders.snowSlice().transitionModels(), flatSliceHolders.snowSlice().tintMap()).stream().map(
                                                                b -> Pair.of(bakedCache.computeIfAbsent(b.getFirst(), (blockModel -> UnbakedGeometryHelper.bake(blockModel, baker, model, spriteGetter, state, pLocation, guiLight3d))),
                                                                        bakedCache.computeIfAbsent(b.getSecond(), (blockModel -> UnbakedGeometryHelper.bake(blockModel, baker, model, spriteGetter, state, pLocation, guiLight3d)))))
                                                        .toList()
                                        );
                                }
                        );
                end = new SeasonGoingModel<>(bake, solarTermBakedModelEnumMap, snowSolarTermBakedModelEnumMap);
                if (texture.getBiomes().isEmpty()) {
                    defaultList.add(Pair.of((b) -> true, end));
                } else {
                    BiomeHolderPredicate biomePredicate = (biomeHolder -> {
                        var biomes = texture.getBiomes();
                        if (biomes.isEmpty()) return true;
                        var either = biomes.get();
                        if (either.left().isPresent()) {
                            Optional<ResourceKey<Biome>> biomeResourceKey = biomeHolder.unwrapKey();
                            if (biomeResourceKey.isPresent()) {
                                return either.left().get().contains(biomeResourceKey.get().location());
                            }
                        }
                        if (either.right().isPresent()) {
                            return biomeHolder.is(either.right().get());
                        }
                        return true;
                    });
                    list.add(Pair.of(biomePredicate, end));
                }
            }

            if (end != null) {
                if (list.size() == 1 && list.get(0).getFirst() == null) {
                    return end;
                } else {
                    if (!defaultList.isEmpty()) {
                        list.add(defaultList.get(defaultList.size() - 1));
                    }
                    return new SeasonBiomeGoingModel<>(bake, list);
                }
            }
        }

        return super.bake(baker, model, spriteGetter, state, pLocation, guiLight3d);
    }

}
