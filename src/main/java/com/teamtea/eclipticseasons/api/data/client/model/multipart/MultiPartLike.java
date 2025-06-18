package com.teamtea.eclipticseasons.api.data.client.model.multipart;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.teamtea.eclipticseasons.client.model.MultiPartBakedModelLike;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.multipart.MultiPart;
import net.minecraft.client.renderer.block.model.multipart.Selector;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
public class MultiPartLike extends MultiPart {
    public static final Codec<MultiPartLike> CODEC =
            Codec.either(SelectorLike.CODEC, SelectorLike.CODEC.listOf())
                    .xmap(either -> new MultiPartLike(either.map(List::of, list -> list)),
                            multi -> {
                                List<SelectorLike> list = multi.getSelectorLikes();
                                return list.size() == 1
                                        ? Either.left(list.get(0))
                                        : Either.right(list);
                            });
    public static final MultiPartLike EMPTY = new MultiPartLike(List.of());

    private final List<SelectorLike> selectorLikes;

    public MultiPartLike(List<SelectorLike> selectors) {
        super(FakeStateDefinition.of(), List.copyOf(selectors));
        this.selectorLikes = selectors;
    }

    public boolean isValid() {
        return !getSelectorLikes().isEmpty();
    }

    public static class FakeStateDefinition extends StateDefinition<Block, BlockState> {

        private static FakeStateDefinition EMPTY;

        protected FakeStateDefinition(Function<Block, BlockState> stateValueFunction, Block owner, Factory<Block, BlockState> valueFunction, Map<String, Property<?>> propertiesByName) {
            super(stateValueFunction, owner, valueFunction, propertiesByName);
        }

        public static FakeStateDefinition of() {
            if (EMPTY == null) {
                EMPTY = new FakeStateDefinition(Block::defaultBlockState, Blocks.AIR, BlockState::new, ImmutableMap.of());
            }
            return EMPTY;
        }
    }

    @Override
    public BakedModel bake(@NotNull ModelBaker baker, @NotNull Function<Material, TextureAtlasSprite> spriteGetter, @NotNull ModelState modelState, @NotNull ResourceLocation location) {
        MultiPartBakedModelLike.Builder multipartbakedmodel$builder = new MultiPartBakedModelLike.Builder();

        for (Selector selector : this.getSelectors()) {
            BakedModel bakedmodel = selector.getVariant().bake(baker, spriteGetter, modelState, location);
            if (bakedmodel != null) {
                multipartbakedmodel$builder.add(selector.getPredicate(FakeStateDefinition.of()), bakedmodel);
            }
        }

        return multipartbakedmodel$builder.build();
    }
}
