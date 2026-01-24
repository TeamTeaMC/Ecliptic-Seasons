package com.teamtea.eclipticseasons.mixin.compat.voxy;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.CustomRandomTick;
import com.teamtea.eclipticseasons.common.AllListener;
import com.teamtea.eclipticseasons.common.core.map.ChunkInfoMap;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.voxy.VoxyConstant;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.level.ChunkEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

public class MixinMinecraftBundle {

    @Mixin(Block.class)
    public abstract static class MixinVoxyBlock {
        @Shadow
        public abstract BlockState defaultBlockState();

        @WrapOperation(
                method = "<init>",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;createBlockStateDefinition(Lnet/minecraft/world/level/block/state/StateDefinition$Builder;)V")
        )
        private void eclipticseasons$voxy_init_createBlockDefinition(Block instance,
                                                                     StateDefinition.Builder<Block, BlockState> pBuilder,
                                                                     Operation<Void> original,
                                                                     @Local(argsOnly = true) BlockBehaviour.Properties pProperties) {
            original.call(instance, pBuilder);
            VoxyTool.registerExtraProperties(instance, pProperties, pBuilder);
        }
    }

    @Mixin(BlockStateParser.class)
    public interface MixinVoxyBlockStateParserAccessor {

        @Accessor("properties")
        Map<Property<?>, Comparable<?>> eclipticseasons$voxy_getProperties();

        @Accessor("state")
        BlockState eclipticseasons$voxy_getState();
    }

    @Mixin(BlockStateParser.class)
    public abstract static class MixinVoxyBlockStateParser {
        @WrapOperation(
                method = "parseForBlock(Lnet/minecraft/core/HolderLookup;Lcom/mojang/brigadier/StringReader;Z)Lnet/minecraft/commands/arguments/blocks/BlockStateParser$BlockResult;",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/commands/arguments/blocks/BlockStateParser;parse()V")
        )
        private static void eclipticseasons$voxy_init_createBlockDefinition(BlockStateParser instance, Operation<Void> original) {
            original.call(instance);
            VoxyTool.parseForBlockExtraProperties(instance);
        }
    }

    @Mixin(BlockBehaviour.BlockStateBase.Cache.class)
    public abstract static class MixinVoxyBlockCache {
        @ModifyExpressionValue(
                method = "<init>",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Block;getCollisionShape(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;")
        )
        private VoxelShape eclipticseasons$voxy_init_getCollisionShape(VoxelShape original, @Local(argsOnly = true) BlockState pState) {
            if (original == null && CompatModule.isVoxyTest()) return VoxyConstant.getShape(pState);
            return original;
        }
    }

    @Mixin(BlockBehaviour.BlockStateBase.class)
    public abstract static class MixinVoxyBlockStateBase implements CustomRandomTick {
        @Shadow
        protected abstract BlockState asState();

        @Inject(
                method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
                at = @At(value = "RETURN"),
                cancellable = true)
        private void eclipticseasons$voxy_getCollisionShape_Fake(BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
            if (!CompatModule.isVoxyTest()) return;
            if (cir.getReturnValue() == null)
                cir.setReturnValue(VoxyConstant.getShape(asState()));
        }

        @Inject(
                method = "getShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;",
                at = @At(value = "RETURN"),
                cancellable = true)
        private void eclipticseasons$voxy_getShape_Fake(BlockGetter pLevel, BlockPos pPos, CollisionContext pContext, CallbackInfoReturnable<VoxelShape> cir) {
            if (!CompatModule.isVoxyTest()) return;
            if (cir.getReturnValue() == null)
                cir.setReturnValue(VoxyConstant.getShape(asState()));
        }

        @Inject(
                method = "getBlockSupportShape",
                at = @At(value = "RETURN"),
                cancellable = true)
        private void eclipticseasons$voxy_getBlockSupportShape_Fake(BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<VoxelShape> cir) {
            if (!CompatModule.isVoxyTest()) return;
            if (cir.getReturnValue() == null)
                cir.setReturnValue(VoxyConstant.getShape(asState()));
        }
    }

    @Mixin(AllListener.class)
    public abstract static class MixinVoxyChunkLoad {
        @Inject(
                remap = false,
                method = "onChunkLoad",
                at = @At(value = "INVOKE", target = "Lcom/teamtea/eclipticseasons/api/util/EclipticUtil;canSnowyBlockInteract()Z")
        )
        private static void eclipticseasons$voxy_onChunkLoad(ChunkEvent.Load event, CallbackInfo ci,
                                                             @Local ChunkInfoMap chunkMap,
                                                             @Local ChunkAccess chunk) {

            if (!CompatModule.isVoxyTest()) return;
            if (!(event.getLevel() instanceof Level level)) return;
            VoxyTool.updateChunk(level,chunk,chunkMap);
        }
    }
}
