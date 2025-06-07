package com.teamtea.eclipticseasons.mixin.common.chunk;


import com.llamalad7.mixinextras.sugar.Local;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.api.misc.IChunkBiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LevelChunk.class})
public abstract class MixinLevelChunk extends ChunkAccess implements IChunkBiomeHolder {
    @Shadow
    @Final
    Level level;

    public MixinLevelChunk(ChunkPos chunkPos, UpgradeData upgradeData, LevelHeightAccessor levelHeightAccessor, Registry<Biome> biomeRegistry, long inhabitedTime, @Nullable LevelChunkSection[] sections, @Nullable BlendingData blendingData) {
        super(chunkPos, upgradeData, levelHeightAccessor, biomeRegistry, inhabitedTime, sections, blendingData);
    }

    @Inject(
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/Heightmap;update(IIILnet/minecraft/world/level/block/state/BlockState;)Z", ordinal = 1),
            method = "setBlockState"
    )
    public void eclipticseasons$Client_setBlockState(BlockPos pos, BlockState state, boolean p_62867_, CallbackInfoReturnable<BlockState> cir,
                                                     @Local(ordinal = 1) BlockState oldState) {
        if (level != null && !level.isClientSide()) {
            // int j = pos.getX() & 15;
            // int l = pos.getZ() & 15;
            // MapChecker.updatePosForce(level, pos, level.getHeight(Heightmap.Types.MOTION_BLOCKING, j, l));
            ServerMapFixer.addPlanner(level, state, oldState, pos, level.getGameTime(), MapChecker.getHeight(level, pos), false);
        }
    }

    @Unique
    private BiomeHolder eclipticseasons$biomeHolder = null;

    @Override
    public BiomeHolder eclipticseasons$getBiomeHolder() {
        if (this.eclipticseasons$biomeHolder == null) {
            if (hasData(AttachmentRegistry.BIOME_HOLDER)) {
                BiomeHolder biomeHolder = getData(AttachmentRegistry.BIOME_HOLDER);
                if (biomeHolder.hasUpdated()) {
                    this.eclipticseasons$biomeHolder = biomeHolder;
                }
            }
        }
        return this.eclipticseasons$biomeHolder;
    }
}
