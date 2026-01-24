package com.teamtea.eclipticseasons.compat.voxy;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.mixin.compat.voxy.MixinMinecraftBundle;
import com.teamtea.eclipticseasons.mixin.compat.voxy.MixinModelTextureBakery;
import me.cortex.voxy.client.core.model.bakery.ModelTextureBakery;
import me.cortex.voxy.client.core.model.bakery.ReuseVertexConsumer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Set;

public class VoxyTool {


    public static void updateChunk(Level level, ChunkAccess chunk, ChunkInfoMap chunkMap) {
        ChunkPos chunkPos = chunk.getPos();
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ());        // MapChecker.updatePosForce(level, checkPos , level.getMinBuildHeight() - 1);

        if (chunkMap != null) {
            for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                    checkPos.setX(i);
                    checkPos.setZ(j);
                    int max_y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, i, j);
                    int k = chunkMap.getHeight(checkPos);
                    for (int l = k; l <= max_y; l++) {
                        checkPos.setY(l);
                        BlockState blockState = chunk.getBlockState(checkPos);
                        if (VoxyConstant.shouldSkipCheck(blockState.getBlock())) continue;
                        if (MapChecker.getDefaultBlockTypeFlag(blockState) > MapChecker.FLAG_NONE) {
                            BlockState newState =
                                    MapChecker.shouldSnowAtBiome(level, MapChecker.getSurfaceBiome(level, checkPos).value(), blockState, level.getRandom(), blockState.getSeed(checkPos), checkPos) ?
                                            blockState.setValue(VoxyConstant.SNOWY, false) :
                                            blockState.setValue(VoxyConstant.SNOWY, true);
                            if (newState != blockState) {
                                chunk.setBlockState(checkPos, newState, false);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void renderToStream(BlockState state, RenderType layer, ReuseVertexConsumer vc) {
        if (!CompatModule.isVoxyTest()) return;
        if (VoxyConstant.shouldSkipCheck(state.getBlock())) return;

        if (state.getRenderShape() != RenderShape.INVISIBLE) {
            //if (state.is(BlockTags.LOGS)) return;
            if (state.getValue(VoxyConstant.SNOWY)) return;
            int defaultBlockTypeFlag = MapChecker.getDefaultBlockTypeFlag(state);
            BakedModel model = ExtraModelManager.getSnowyModel(state, null, defaultBlockTypeFlag, MapChecker.getSnowOffset(state, defaultBlockTypeFlag));
            if (model == null) {
                //modelLocalRef.set(sm);
                return;
            }
            int meta = ModelTextureBakery.getMetaFromLayer(state.getBlock() instanceof LeavesBlock ?
                    layer :
                    ExtraModelManager.getRenderType(state));
            for (Direction direction : new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, null}) {
                //int SNOW_FLAG = 1 << 30;

                for (BakedQuad quad : model.getQuads(state, direction, new SingleThreadedRandomSource(42L))) {
                    int quadMeta = meta | (quad.isTinted() ? 4 : 0);
                    //quadMeta |= SNOW_FLAG;
                    vc.quad(quad, quadMeta);
                }
            }
        }
    }

    public static void registerExtraProperties(Block instance, BlockBehaviour.Properties pProperties, StateDefinition.Builder<Block, BlockState> pBuilder) {
        if (!CompatModule.isVoxyTest()) return;
        if (VoxyConstant.shouldSkipCheck(instance)) return;

        try {
            //if (!defaultBlockState().isAir())
            {
                pBuilder = pBuilder.add(VoxyConstant.SNOWY);
                //defaultBlockState().setValue(BlockStateProperties.SNOWY, false);
            }
        } catch (Exception e) {
        }
    }

    public static void parseForBlockExtraProperties(BlockStateParser instance) {
        if (!CompatModule.isVoxyTest()) return;
        if (VoxyConstant.shouldSkipCheck(((MixinMinecraftBundle.MixinVoxyBlockStateParserAccessor) instance).eclipticseasons$voxy_getState().getBlock()))
            return;
        ((MixinMinecraftBundle.MixinVoxyBlockStateParserAccessor) instance).eclipticseasons$voxy_getProperties().putIfAbsent(VoxyConstant.SNOWY, true);
    }

    public static void fixBigGlobeOfBlockStates(String name, CallbackInfoReturnable<BlockState> cir) {
		if (!CompatModule.isVoxyTest()) return;
        try {
            BlockStateParser.BlockResult result = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), name, false);
            Set<Property<?>> remaining = new HashSet<>(result.blockState().getProperties());
            remaining.removeAll(result.properties().keySet());
            //remaining.remove(VoxyConstant.SNOWY);
            if (!remaining.isEmpty()) {
                throw new IllegalArgumentException("22Missing properties for state " + name + ": " + remaining);
            }
            cir.setReturnValue(result.blockState());
        } catch (CommandSyntaxException e) {
            throw new IllegalArgumentException("22Invalid block specifier: " + name, e);
        }
    }
}
