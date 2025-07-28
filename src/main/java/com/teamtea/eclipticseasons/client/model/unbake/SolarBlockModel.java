package com.teamtea.eclipticseasons.client.model.unbake;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonalTexture;
import com.teamtea.eclipticseasons.client.model.SeasonBiomeGoingModel;
import com.teamtea.eclipticseasons.client.model.SeasonGoingModel;
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
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
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
        return new SolarBlockModel(blockModel.getParentLocation(),
                blockModel.getElements(),
                blockModel.textureMap,
                blockModel.hasAmbientOcclusion(),
                blockModel.getGuiLight(),
                blockModel.getTransforms(),
                blockModel.getOverrides()
        );
    }

    public BlockModel to(Map<String, ResourceLocation> stringStringMap, Map<String, Integer> integerMap) {
        Map<String, Either<Material, String>> map = new HashMap<>();
        map.putAll(this.textureMap);
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
                EnumMap<Direction, BlockElementFace> directionBlockElementFaceEnumMap = new EnumMap<>(Direction.class);
                element.faces.forEach((direction, face) -> {
                    Integer orDefault = integerMap.getOrDefault(face.texture(), null);
                    if (orDefault != null && face.tintIndex() != orDefault) {
                        directionBlockElementFaceEnumMap.put(direction,
                                new BlockElementFace(face.cullForDirection(), orDefault, face.texture(), face.uv(), face.faceData(), face.parent()));
                    }
                });
                element.faces.putAll(directionBlockElementFaceEnumMap);
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
    public BakedModel bake(ModelBaker baker, BlockModel model, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state, boolean guiLight3d) {
        if (seasonalTexture != null) {
            BakedModel bake = UnbakedGeometryHelper.bake(this, baker, model, spriteGetter, state, guiLight3d);

            SeasonGoingModel<BakedModel> end = null;
            List<Pair<SeasonBiomeGoingModel.BiomePredicate, SeasonGoingModel<BakedModel>>> list = new ArrayList<>();
            List<Pair<SeasonBiomeGoingModel.BiomePredicate, SeasonGoingModel<BakedModel>>> defaultList = new ArrayList<>();
            for (SeasonalTexture texture : seasonalTexture) {
                EnumMap<SolarTerm, List<Pair<BakedModel, BakedModel>>> solarTermBakedModelEnumMap = new EnumMap<>(SolarTerm.class);
                texture.getFlatSliceEnumMap()
                        .forEach(
                                (solarTerm, flatSliceHolders) -> {
                                    if (flatSliceHolders.flatSlice().mid() != null)
                                        solarTermBakedModelEnumMap.put(solarTerm,
                                                toList(flatSliceHolders.flatSlice().mid(), flatSliceHolders.flatSlice().tintMap()).stream().map(
                                                        b -> {
                                                            BakedModel sliceModel = UnbakedGeometryHelper.bake(b, baker, model, spriteGetter, state, guiLight3d);
                                                            return Pair.of(sliceModel, sliceModel);
                                                        }
                                                ).toList()
                                        );
                                    if (flatSliceHolders.flatSlice().transitionModels() != null)
                                        solarTermBakedModelEnumMap.put(solarTerm,
                                                toPairList(flatSliceHolders.flatSlice().transitionModels(), flatSliceHolders.flatSlice().tintMap()).stream().map(
                                                        b -> Pair.of(UnbakedGeometryHelper.bake(b.getFirst(), baker, model, spriteGetter, state, guiLight3d),
                                                                UnbakedGeometryHelper.bake(b.getSecond(), baker, model, spriteGetter, state, guiLight3d))
                                                ).toList()
                                        );
                                }
                        );
                end = new SeasonGoingModel<>(bake, solarTermBakedModelEnumMap);
                if (texture.getBiomes().isEmpty()) {
                    defaultList.add(Pair.of((b) -> true, end));
                } else {
                    SeasonBiomeGoingModel.BiomePredicate biomePredicate = (biomeHolder -> {
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

        return super.bake(baker, model, spriteGetter, state, guiLight3d);
    }
}
