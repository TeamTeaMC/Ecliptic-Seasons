package com.teamtea.eclipticseasons.client.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.biome.Biome;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;

public class EmbeddiumRenderChunkReflector {
    static final Class<?> worldSliceClass;
    static final Class<?> worldSliceLocalClass;
    static final Class<?> biomeSliceClass;
    static final VarHandle viewHandle;
    static final VarHandle biomeSliceHandle;
    private static final MethodHandle getBiomeHandle;

    static {
        worldSliceLocalClass = forName("org.embeddedt.embeddium.render.world.WorldSliceLocal");
        worldSliceClass = forName("me.jellysquid.mods.sodium.client.world.WorldSlice");
        biomeSliceClass = forName("me.jellysquid.mods.sodium.client.world.biome.BiomeSlice");
        viewHandle = lookHandle(worldSliceLocalClass, "view", worldSliceClass);
        biomeSliceHandle = lookHandle(worldSliceClass, "biomeSlice", biomeSliceClass);
        getBiomeHandle = getGetBiome();

    }

    private static MethodHandle getGetBiome() {
        try {
            return MethodHandles.privateLookupIn(EmbeddiumRenderChunkReflector.biomeSliceClass, MethodHandles.lookup())
                    .findVirtual(EmbeddiumRenderChunkReflector.biomeSliceClass, "getBiome",
                            MethodType.methodType(Holder.class, int.class, int.class, int.class))
                    .asType(MethodType.methodType(Holder.class, Object.class, int.class, int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException | NullPointerException e) {
            return null;
        }
    }

    private static VarHandle lookHandle(Class<?> recv, String name, Class<?> type) {
        try {
            return MethodHandles.privateLookupIn(recv, MethodHandles.lookup())
                    .findVarHandle(recv, name, type);
        } catch (NoSuchFieldException | IllegalAccessException | NullPointerException e) {
            return null;
        }
    }

    private static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ignore) {
        }
        return null;
    }

    public static Holder<Biome> getBiome(BlockAndTintGetter blockAndTintGetter, BlockPos pos) {
        if (blockAndTintGetter.getClass() == EmbeddiumRenderChunkReflector.worldSliceLocalClass)
            try {
                Object worldSliceView = EmbeddiumRenderChunkReflector.viewHandle.get(blockAndTintGetter);
                if (EmbeddiumRenderChunkReflector.worldSliceClass.isInstance(worldSliceView)) {
                    Object worldSliceObject = EmbeddiumRenderChunkReflector.biomeSliceHandle.get(worldSliceView);
                    if (EmbeddiumRenderChunkReflector.biomeSliceClass.isInstance(worldSliceObject)) {
                        return (Holder<Biome>) getBiomeHandle.invokeExact(worldSliceObject, pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            } catch (Throwable e) {
                System.err.println("Invocation failed: " + e.getMessage());
            }
        return null;
    }
}
