package com.teamtea.eclipticseasons.client.model.loader;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.teamtea.eclipticseasons.client.model.SnowyBakedModelWrapper;
import lombok.Data;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.RenderTypeGroup;

import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.IModelBuilder;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import net.neoforged.neoforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Data
public class SnowOverlayModel implements IUnbakedGeometry<SnowOverlayModel> {
    // private final ResourceLocation parent;
    private final BlockModel parentModel;
    private final ImmutableList<String> itemPasses;


    @Override
    public @NotNull BakedModel bake(@NotNull IGeometryBakingContext context, @NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState, @NotNull ItemOverrides overrides) {
        return new Baked<>(parentModel.bake(
                baker,spriteGetter,modelState
        ));
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        parentModel.resolveParents(modelGetter);
    }

    @Override
    public @NotNull Set<String> getConfigurableComponentNames() {
        return new HashSet<>(itemPasses);
    }

    public static class Baked<T extends BakedModel> extends SnowyBakedModelWrapper<T> {

        public Baked(T originalModel) {
            super(originalModel);
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            List<List<BakedQuad>> quadLists = new ArrayList<>();

            return ConcatenatedListView.of(quadLists);
        }

        @Override
        public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, ModelData modelData) {
            return modelData.derive().build();
        }

    }



    public static final class Loader implements IGeometryLoader<SnowOverlayModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public SnowOverlayModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            List<String> itemPasses = new ArrayList<>();
            ImmutableMap.Builder<String, BlockModel> childrenBuilder = ImmutableMap.builder();
            readChildren(jsonObject, "children", deserializationContext, childrenBuilder, itemPasses);
            JsonObject jsonObject1=new JsonObject();
            jsonObject1.add("parent",jsonObject.get("parent"));
            return new SnowOverlayModel(
                    BlockModel.fromString(jsonObject1.toString())
                    , ImmutableList.copyOf(itemPasses));
        }

        private void readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses) {
            if (!jsonObject.has(name))
                return;
            var childrenJsonObject = jsonObject.getAsJsonObject(name);
            for (Map.Entry<String, JsonElement> entry : childrenJsonObject.entrySet()) {
                children.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
                itemPasses.add(entry.getKey()); // We can do this because GSON preserves ordering during deserialization
            }

        }
    }
}

