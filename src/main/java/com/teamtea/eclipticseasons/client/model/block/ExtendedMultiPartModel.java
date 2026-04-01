package com.teamtea.eclipticseasons.client.model.block;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.multipart.MultiPartModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jspecify.annotations.NonNull;

import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ExtendedMultiPartModel extends MultiPartModel {
    private final List<MultiPartModel.Selector<BlockStateModel>> selectors;
    private final MultiPartModel.SharedBakedState shared;
    private final Map<BlockState, BitSet> selectorCache = new Reference2ObjectOpenHashMap<>();

    public ExtendedMultiPartModel(MultiPartModel.SharedBakedState shared) {
        super(shared, Blocks.AIR.defaultBlockState());
        this.selectors = shared.selectors;
        this.shared = shared;
    }

    public BitSet getSelectors(BlockState state) {
        BitSet bitset = this.selectorCache.get(state);
        if (bitset == null) {
            bitset = new BitSet();

            for (int i = 0; i < this.selectors.size(); i++) {
                MultiPartModel.Selector<BlockStateModel> pair = this.selectors.get(i);
                if (pair.condition().test(state)) {
                    bitset.set(i);
                }
            }
            this.selectorCache.put(state, bitset);
        }
        return bitset;
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> output) {
        super.collectParts(level, pos, state, random, output);
        if (state == null) {
            return;
        } else {

            // Check why not work
            var models = this.shared.selectModels(state);
            long seed = random.nextLong();
            for (int j = 0; j < models.size(); ++j) {
                var model = models.get(j);
                random.setSeed(seed);
                model.collectParts(level, pos, state, random, output);
            }

            // BitSet bitset = getSelectors(state);
            // for (int j = 0; j < bitset.length(); ++j) {
            //     if (bitset.get(j)) {
            //         var model = this.selectors.get(j).model();
            //         model.collectParts(level, pos, state, random, output);
            //     }
            // }
        }
    }

    public static class Unbaked extends MultiPartModel.Unbaked {
        public Unbaked(List<Selector<BlockStateModel.Unbaked>> selectors) {
            super(selectors);
        }

        @Override
        public @NonNull BlockStateModel bake(@NonNull BlockState blockState, ModelBaker modelBakery) {
            MultiPartModel.SharedBakedState shared = modelBakery.compute(this.sharedStateKey);
            return new ExtendedMultiPartModel(shared);
        }

        public @NonNull BlockStateModel bake(ModelBaker modelBakery) {
            return bake(Blocks.AIR.defaultBlockState(), modelBakery);
        }
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
}
