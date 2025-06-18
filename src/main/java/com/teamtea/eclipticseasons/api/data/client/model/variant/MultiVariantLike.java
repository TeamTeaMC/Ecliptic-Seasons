package com.teamtea.eclipticseasons.api.data.client.model.variant;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

@Getter
public class MultiVariantLike extends MultiVariant {
    public static final Codec<MultiVariantLike> CODEC =
            Codec.either(VariantLike.CODEC, VariantLike.CODEC.listOf())
                    .xmap(either -> new MultiVariantLike(either.map(List::of, list -> list)),
                            multi -> {
                                List<VariantLike> list = multi.getVariantLikes();
                                return list.size() == 1
                                        ? Either.left(list.get(0))
                                        : Either.right(list);
                            });

    private final List<VariantLike> variantLikes;

    public MultiVariantLike(List<VariantLike> variantLikes) {
        super(List.copyOf(variantLikes));
        this.variantLikes = variantLikes;
    }

    @Override
    public @Nullable BakedModel bake(ModelBaker pBaker, Function<Material, TextureAtlasSprite> pSpriteGetter, ModelState pState, ResourceLocation pLocation) {
        return super.bake(pBaker, pSpriteGetter, pState, pLocation);
    }
}
