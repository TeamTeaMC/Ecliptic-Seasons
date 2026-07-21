package com.teamtea.eclipticseasons.mixin.common.worldgen;


import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.river.RiverBiomeResolver;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.level.levelgen.SurfaceSystem.class)
public abstract class MixinSurfaceSystem {

    @Inject(at = {@At(value = "HEAD")},
            method = {"buildSurface"})
    public void eclipticseasons$buildSurface_cacheBiome_init(
            RandomState randomState,
            BiomeManager biomeManager,
            Registry<Biome> biomes,
            boolean useLegacyRandomSource,
            WorldGenerationContext context,
            ChunkAccess chunk,
            NoiseChunk noiseChunk,
            SurfaceRules.RuleSource ruleSource,
            CallbackInfo ci,
            @Share("biomeArrays") LocalRef<int[]> biomeHolderLocalRef,
            @Share("intCounter") LocalIntRef localIntRef,
            @Share("signal") LocalIntRef signal
    ) {
        // BiomeHolder biomeHolder1 = chunk.getData(AttachmentRegistry.BIOME_HOLDER);
        biomeHolderLocalRef.set(new int[256]);
        localIntRef.set(0);
        signal.set(BiomeHolder.FLAG_NEED_VERSION);
    }

    @Inject(at = {@At(value = "INVOKE_ASSIGN",
            shift = At.Shift.AFTER,
            target = "Lnet/minecraft/world/level/biome/BiomeManager;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")},
            method = {"buildSurface"})
    public void eclipticseasons$buildSurface_cacheBiome(
            RandomState randomState,
            BiomeManager biomeManager,
            Registry<Biome> biomes,
            boolean useLegacyRandomSource,
            WorldGenerationContext context,
            ChunkAccess chunk,
            NoiseChunk noiseChunk,
            SurfaceRules.RuleSource ruleSource,
            CallbackInfo ci,
            @Local Holder<Biome> biomeHolder,
            @Local(ordinal = 1) BlockPos.MutableBlockPos blockPos,
            @Share("biomeArrays") LocalRef<int[]> biomeHolderLocalRef,
            @Share("intCounter") LocalIntRef localIntRef,
            @Share("signal") LocalIntRef signal) {


        int i = MapChecker.biomeToId(biomes, biomeHolder.value());
        if (i > -1 && i < biomes.size()) {
            biomeHolderLocalRef.get()[((blockPos.getX() & 15) * 16) + (blockPos.getZ() & 15)] = i;
            localIntRef.set(localIntRef.get() + 1);
            if (MapChecker.isSmallBiome(biomeHolder)) {
                // signal.set(BiomeHolder.FLAG_FILL_SMALL);
                Climate.TargetPoint sample = RiverBiomeResolver.getClimateTargetPoint(randomState, blockPos);
                ResourceKey<Biome> biomeResourceKey = RiverBiomeResolver.getClimateBiome(sample);
                int newBiomeID = MapChecker.biomeToId(biomes, biomes.get(biomeResourceKey));
                if (newBiomeID > -1 && i < biomes.size()) {
                    biomeHolderLocalRef.get()[((blockPos.getX() & 15) * 16) + (blockPos.getZ() & 15)] = newBiomeID;
                }
            }
        }
    }

    @Inject(at = {@At(value = "RETURN")},
            method = {"buildSurface"})
    public void eclipticseasons$buildSurface_cacheBiome_end(
            RandomState randomState,
            BiomeManager biomeManager,
            Registry<Biome> biomes,
            boolean useLegacyRandomSource,
            WorldGenerationContext context,
            ChunkAccess chunk,
            NoiseChunk noiseChunk,
            SurfaceRules.RuleSource ruleSource,
            CallbackInfo ci,
            @Share("biomeArrays") LocalRef<int[]> biomeHolderLocalRef,
            @Share("intCounter") LocalIntRef localIntRef,
            @Share("signal") LocalIntRef signal
    ) {
        // BiomeHolder biomeHolder1 = chunk.getData(AttachmentRegistry.BIOME_HOLDER);
        if (localIntRef.get() == 256) {
            chunk.setData(AttachmentRegistry.BIOME_HOLDER,
                    new BiomeHolder(biomeHolderLocalRef.get(), true, signal.get()));
        }
    }
}
