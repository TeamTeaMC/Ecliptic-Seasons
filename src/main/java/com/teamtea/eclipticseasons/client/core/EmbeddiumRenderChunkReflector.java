package com.teamtea.eclipticseasons.client.core;


import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.biome.Biome;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

// not sure if we should keep use java8
public class EmbeddiumRenderChunkReflector {
    static final Class<?> worldSliceClass;
    static final Class<?> worldSliceLocalClass;
    static final Class<?> biomeSliceClass;
    static final Field viewHandle;
    static final Field biomeSliceHandle;
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
            return MethodHandles.lookup()
                    .findVirtual(EmbeddiumRenderChunkReflector.biomeSliceClass, "getBiome",
                            MethodType.methodType(Biome.class, int.class, int.class, int.class))
                    .asType(MethodType.methodType(Biome.class, Object.class, int.class, int.class, int.class));
        } catch (NoSuchMethodException | IllegalAccessException | NullPointerException e) {
            return null;
        }
    }

    private static Field lookHandle(Class<?> recv, String name, Class<?> type) {
        try {
            Field field = recv.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException | NullPointerException e) {
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

    public static Biome getBiome(IBlockDisplayReader blockAndTintGetter, BlockPos pos) {
        if (blockAndTintGetter.getClass() == EmbeddiumRenderChunkReflector.worldSliceLocalClass)
            try {
                Object worldSliceView = EmbeddiumRenderChunkReflector.viewHandle.get(blockAndTintGetter);
                if (EmbeddiumRenderChunkReflector.worldSliceClass.isInstance(worldSliceView)) {
                    Object worldSliceObject = EmbeddiumRenderChunkReflector.biomeSliceHandle.get(worldSliceView);
                    if (EmbeddiumRenderChunkReflector.biomeSliceClass.isInstance(worldSliceObject)) {
                        return (Biome) getBiomeHandle.invokeExact(worldSliceObject, pos.getX(), pos.getY(), pos.getZ());
                    }
                }
            } catch (ReflectiveOperationException | RuntimeException ignored) {
            } catch (Throwable e) {
                System.err.println("Invocation failed: " + e.getMessage());
            }
        return null;
    }
}
