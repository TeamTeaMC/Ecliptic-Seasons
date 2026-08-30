package com.teamtea.eclipticseasons.client.lod.color;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.client.model.ModelTester;
import com.teamtea.eclipticseasons.api.data.client.model.seasonal.SeasonBlockDefinition;
import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.particle.ParticleUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SeasonalBlockColorCache {

    /**
     * Successful colors calculated here always have an alpha value of 255,
     * so this value can safely represent a missing color internally.
     */
    private static final int NO_COLOR = Integer.MIN_VALUE;

    private static final ColorEntry EMPTY_COLOR = new ColorEntry(NO_COLOR, NO_COLOR, false);

    /**
     * Each block state is processed once. Its unrestricted and biome-specific
     * seasonal model relationships are then read without further mutation.
     */
    private static final ConcurrentHashMap<BlockState, StateColors> COLOR_CACHE = new ConcurrentHashMap<>();

    public static boolean hasSeasonalModelColor(@Nullable BlockState state) {
        if (state == null) return false;
        Block block = state.getBlock();
        return block != Blocks.GRASS_BLOCK
                && block != Blocks.STONE
                && block != Blocks.DIRT
                && block != Blocks.WATER
                && block != Blocks.LAVA
                // && !(block instanceof LiquidBlock)
                && ClientRef.seasonDef.containsKey(block);
    }

    /**
     * Returns the representative seasonal model color at the given position.
     *
     * <p>The seasonal model relationships and their sprite colors are cached
     * per block state. Transition selection remains position-dependent and is
     * evaluated for every call.</p>
     *
     * @return seasonal ARGB color, or null if no supported seasonal model
     * applies
     */
    @Nullable
    public static Integer getColor(
            @Nullable Biome biome,
            BlockPos pos,
            long seed,
            @Nullable BlockState state
    ) {
        if (biome == null || state == null || state.isAir()) {
            return null;
        }

        StateColors colors = COLOR_CACHE.computeIfAbsent(state, SeasonalBlockColorCache::bakeStateColors);

        // if ((Object) biome instanceof IBiomeTagHolder holder) {
        //     int i = holder.eclipticseasons$getBindId();
        //     Holder<Biome> biomeHolder = MapChecker.idToBiome(ClientCon.getUseLevel(), i);
        //     biome = biomeHolder.value();
        // }
        ColorEntry entry = colors.get(biome);


        return entry == null ? null : entry.getColor(state, pos, seed);
    }

    public static void clear() {
        COLOR_CACHE.clear();
    }

    /**
     * Fully processes all seasonal definitions for one block state.
     *
     * <p>Definitions without biome restrictions become the fallback entry.
     * Restricted definitions are expanded into direct biome-to-color
     * mappings.</p>
     */
    public static StateColors bakeStateColors(BlockState state) {
        List<SeasonBlockDefinition> definitions = ClientRef.seasonDef.get(state.getBlock());

        if (definitions == null || definitions.isEmpty()) {
            return StateColors.EMPTY;
        }

        SolarTerm term = ClientCon.nowSolarTerm;
        ColorEntry defaultColor = null;

        Reference2ObjectOpenHashMap<Biome, ColorEntry> biomeColors = new Reference2ObjectOpenHashMap<>();

        for (SeasonBlockDefinition definition : definitions) {
            List<SeasonBlockDefinition.FlatSliceHolder> holders = definition.getFlatSliceEnumMap().get(term);

            if (holders == null || holders.isEmpty()) {
                continue;
            }

            SeasonBlockDefinition.FlatSlice slice = findUsableSlice(holders);

            if (slice == null) {
                continue;
            }

            ColorEntry entry = bakeEntry(state, slice);

            if (entry.isEmpty()) {
                continue;
            }

            HolderSet<Biome> biomes = definition.getBiomes();

            if (biomes.size() == 0) {
                if (defaultColor == null) {
                    defaultColor = entry;
                }

                continue;
            }

            for (int i = 0; i < biomes.size(); i++) {
                biomeColors.putIfAbsent(
                        biomes.get(i).value(),
                        entry
                );
            }
        }

        if (defaultColor == null && biomeColors.isEmpty()) {
            return StateColors.EMPTY;
        }

        return new StateColors(defaultColor, biomeColors);
    }

    private static SeasonBlockDefinition.FlatSlice findUsableSlice(List<SeasonBlockDefinition.FlatSliceHolder> holders) {
        for (SeasonBlockDefinition.FlatSliceHolder holder : holders) {
            SeasonBlockDefinition.FlatSlice slice = holder.flatSlice();
            if (!slice.emptyAbove()) {
                return slice;
            }
        }

        return null;
    }

    private static ColorEntry bakeEntry(
            BlockState state,
            SeasonBlockDefinition.FlatSlice slice
    ) {
        if (slice.transitionModels() == null) {
            int color = bakeModelColor(state, slice.mid());

            return color == NO_COLOR ? EMPTY_COLOR : ColorEntry.fixed(color);
        }

        int first = bakeModelColor(state, slice.transitionModels().getFirst());
        int second = bakeModelColor(state, slice.transitionModels().getSecond());

        if (first == NO_COLOR && second == NO_COLOR) {
            return EMPTY_COLOR;
        }

        return ColorEntry.transition(first, second);
    }

    /**
     * Resolves a seasonal model and calculates the average color of its
     * particle sprite.
     */
    private static int bakeModelColor(BlockState state, @Nullable ResourceLocation modelResourceLocation) {
        if (modelResourceLocation == null) {
            return NO_COLOR;
        }

        ModelTester model = ExtraModelManager.getSeasonalModel(state, modelResourceLocation);

        /*
         * Preserves the condition from the existing implementation.
         * If replacement models should determine the LOD base color instead,
         * change model.replace() to !model.replace().
         */
        if (model == null || !model.replace()) {
            return NO_COLOR;
        }

        BakedModel extraModel = ExtraModelManager.getExtraModel(model.modelResourceLocation());

        if (extraModel == null) {
            return NO_COLOR;
        }

        TextureAtlasSprite sprite = extraModel.getParticleIcon();

        Integer color = calculateColorFromTexture(sprite);

        return color != null ? color : NO_COLOR;
    }

    /**
     * Calculates the average color of all visible sprite pixels.
     */
    @Nullable
    public static Integer calculateColorFromTexture(@Nullable TextureAtlasSprite sprite) {
        if (sprite == null) {
            return null;
        }

        int width = sprite.contents().width();
        int height = sprite.contents().height();

        if (width <= 0 || height <= 0) {
            return null;
        }

        double red = 0.0D;
        double green = 0.0D;
        double blue = 0.0D;
        int count = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = sprite.getPixelRGBA(0, x, y);

                int alpha = argb >>> 24 & 0xFF;

                /*
                 * Match DH's Leaves color mode:
                 * ignore transparent pixels and treat every visible pixel as
                 * fully opaque.
                 */
                if (alpha == 0) {
                    continue;
                }

                int pixelRed = argb >>> 16 & 0xFF;
                int pixelGreen = argb >>> 8 & 0xFF;
                int pixelBlue = argb & 0xFF;

                red += srgbToLinear(pixelRed / 255.0D);
                green += srgbToLinear(pixelGreen / 255.0D);
                blue += srgbToLinear(pixelBlue / 255.0D);
                count++;
            }
        }

        if (count == 0) {
            return null;
        }

        return 0xFF000000
                | linearToSrgb(red / count) << 16
                | linearToSrgb(green / count) << 8
                | linearToSrgb(blue / count);
    }

    private static double srgbToLinear(double color) {
        return color <= 0.04045D
                ? color / 12.92D
                : Math.pow(
                (color + 0.055D) / 1.055D,
                2.4D
        );
    }

    private static int linearToSrgb(double color) {
        double srgb = color <= 0.0031308D
                ? color * 12.92D
                : 1.055D * Math.pow(
                color,
                1.0D / 2.4D
        ) - 0.055D;

        return Mth.clamp(
                (int) Math.round(srgb * 255.0D),
                0,
                255
        );
    }


    public record StateColors(@Nullable ColorEntry defaultColor, Reference2ObjectMap<Biome, ColorEntry> biomeColors) {
        public static final StateColors EMPTY = new StateColors(null, Reference2ObjectMaps.emptyMap());

        @Nullable
        public ColorEntry get(Biome biome) {
            ColorEntry entry = biomeColors.get(biome);

            return entry != null
                    ? entry
                    : defaultColor;
        }
    }

    /**
     * A fixed color or both sides of a seasonal transition.
     */
    public record ColorEntry(int first, int second, boolean transition) {

        public static ColorEntry fixed(int color) {
            return new ColorEntry(color, color, false);
        }

        public static ColorEntry transition(int first, int second) {
            return new ColorEntry(first, second, true);
        }

        public boolean isEmpty() {
            return first == NO_COLOR && second == NO_COLOR;
        }

        @Nullable
        public Integer getColor(BlockState state, BlockPos pos, long seed) {
            if (isEmpty()) {
                return null;
            }
            if (!transition) {
                return first != NO_COLOR ? first : null;
            }
            int value = Mth.abs((int) (seed + pos.getX())) % 100;
            boolean useFirst = value > ClientCon.progress;
            int color = useFirst ? first : second;
            // If one side failed to produce a color, use the other side.
            if (color == NO_COLOR) {
                color = useFirst ? second : first;
            }
            return color != NO_COLOR ? color : null;
        }
    }
}