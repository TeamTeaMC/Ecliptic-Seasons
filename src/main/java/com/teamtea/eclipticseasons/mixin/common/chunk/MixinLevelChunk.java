package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin({LevelChunk.class})
public abstract class MixinLevelChunk extends ChunkAccess implements IChunkBiomeHolder {
    @Shadow
    @Final
    Level level;

    @Shadow(remap = false)
    @NotNull
    public abstract <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);

    public MixinLevelChunk(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable LevelChunkSection[] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, biomeRegistry, inhabitedTime, sections, blendingData);
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 2),
            method = "setBlockState"
    )
    public void eclipticseasons$server_setBlockState(BlockPos pos, BlockState state, boolean p_62867_, CallbackInfoReturnable<BlockState> cir,
                                                     @Local(ordinal = 1) BlockState oldState,
                                                     @Local Block block) {
        if (level != null) {
            // DH do some work for world generation would stick when close server, so we need to check it.
            if (!MapChecker.isLoaded(level, pos)) return;
            MapChecker.getHeightOrUpdate(level, pos, true);
            SnowyMapChecker.updatePos(level,(LevelChunk) (Object) this,pos, state, oldState, block);
        }
    }

    @Inject(
            at = @At(value = "RETURN"),
            method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V"
    )
    public void eclipticseasons$init(ServerLevel pLevel, ProtoChunk pChunk, LevelChunk.PostLoadProcessor pPostLoad, CallbackInfo ci) {
        if (pChunk instanceof IChunkBiomeHolder holderOld && !(pChunk instanceof ImposterProtoChunk)) {
            this.eclipticseasons$setBiomeHolder(holderOld.eclipticseasons$getBiomeHolder());
        }
    }

    @Override
    public void eclipticseasons$setBiomeHolder(BiomeHolder biomeHolderNew) {
        this.eclipticseasons$setBiomeHolder$1201(biomeHolderNew);
        // set to forge
        if (biomeHolderNew != null) {
            Optional<BiomeHolder> biomeHolderLazyOptional = getCapability(BiomeHolder.BIOME_HOLDER_CAPABILITY, null).resolve();
            if (biomeHolderLazyOptional.isPresent()) {
                var biomeHolder = biomeHolderLazyOptional.get();

                if (biomeHolder != biomeHolderNew) biomeHolder.copyFrom(biomeHolderNew);
                if (biomeHolderNew.hasUpdated()
                        && biomeHolderNew.version() != BiomeHolder.FLAG_NEED_VERSION
                        && biomeHolderNew.version() != BiomeHolder.FLAG_FILL_SMALL) {
                    setUnsaved(true);
                }
            }
        }
    }

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder() {
        BiomeHolder biomeHolder = eclipticseasons$getBiomeHolder$1201();
        if (biomeHolder == null) {
            Optional<BiomeHolder> biomeHolderLazyOptional = getCapability(BiomeHolder.BIOME_HOLDER_CAPABILITY, null).resolve();
            if (biomeHolderLazyOptional.isPresent()) {
                biomeHolder = biomeHolderLazyOptional.get();
                if (biomeHolder.hasUpdated()) {
                    eclipticseasons$setBiomeHolder$1201(biomeHolder);
                }
            }
        }
        return biomeHolder;
    }
}
