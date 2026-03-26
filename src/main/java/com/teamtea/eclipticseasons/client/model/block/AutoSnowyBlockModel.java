package com.teamtea.eclipticseasons.client.model.block;

import com.teamtea.eclipticseasons.client.core.ExtraModelManager;
import com.teamtea.eclipticseasons.client.model.block.part.SimpleBlockModelPart;
import com.teamtea.eclipticseasons.client.model.block.quad.ReUVBakedQuad;
import com.teamtea.eclipticseasons.client.model.block.quad.QuadFilter;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class AutoSnowyBlockModel implements BlockStateModel {
    public static final AutoSnowyBlockModel INSTANCE = new AutoSnowyBlockModel();
    public static final AutoSnowyBlockModel CUSTOM = new AutoSnowyBlockModel();
    public static final AutoSnowyBlockModel CUSTOM_AO = new AutoSnowyBlockModel();

    public static final Map<BlockState, SimpleBlockModelPart> PART_MAP = new IdentityHashMap<>();

    private AutoSnowyBlockModel() {
    }

    @Override
    public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
        changeSprite(state, parts);
    }

    @Override
    public void collectParts(@NonNull RandomSource random, @NonNull List<BlockStateModelPart> output) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Material.@NonNull Baked particleMaterial() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @BakedQuad.MaterialFlags int materialFlags() {
        throw new UnsupportedOperationException();
    }

    private static final Direction[] DIRECTIONS_TO_CHECK = {
            Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, null};

    public static void changeSprite(BlockState state, List<BlockStateModelPart> parts) {
        if (parts.isEmpty()) return;
        SimpleBlockModelPart simpleBlockModelPart = PART_MAP.get(state);
        if (simpleBlockModelPart != null) {
            parts.add(simpleBlockModelPart);
            return;
        }

        Map<Direction, List<BakedQuad>> map = new IdentityHashMap<>();
        QuadCollection.Builder quadCollection = new QuadCollection.Builder();
        for (BlockStateModelPart object : parts) {
            for (Direction value : DIRECTIONS_TO_CHECK) {
                List<BakedQuad> quads = object.getQuads(value);
                if (quads.isEmpty()) continue;
                for (BakedQuad quad : quads) {
                    quadCollection = value == null ?
                            quadCollection.addUnculledFace(quad) :
                            quadCollection.addCulledFace(value, quad);
                }
            }
        }
        QuadCollection build = quadCollection.build();

        boolean tooTiny = false;
        Block block = state.getBlock();
        tooTiny |= block instanceof FenceBlock;
        tooTiny |= block instanceof FenceGateBlock;
        tooTiny |= block instanceof IronBarsBlock;
        tooTiny |= block instanceof StairBlock;

        // MutableQuad mutableQuad = new MutableQuad();
        var bqr = new ReUVBakedQuad();
        // bqr.setMutableQuad(mutableQuad);
        // for (BlockStateModelPart modelPart : parts) {
        //     for (Direction value : DIRECTIONS_TO_CHECK) {
        //         List<BakedQuad> quads = modelPart.getQuads(value);
        //         if (quads.isEmpty()) continue;
        //         quads = new ArrayList<>(quads);
        //         // if (!tooTiny)
        //         //     quads = QuadFixer.fixQuadCTM(quads);
        //         map.put(value, makeSnowyBakedQuads(bqr, quads, tooTiny));
        //         // List<BakedQuad> results = map.computeIfAbsent(value, (_) -> new ArrayList<>());
        //         // for (BakedQuad quad : quads) {
        //         //     results.add(makeSnowyQuad(mutableQuad, quad));
        //         // }
        //     }
        // }

        List<BakedQuad> quads = new ArrayList<>(build.getAll());
        if (!tooTiny)
            quads = QuadFilter.fixQuadCTM(quads);
        map.put(Direction.UP, makeSnowyBakedQuads(bqr, quads, tooTiny));
        // parts.clear();
        SimpleBlockModelPart part = new SimpleBlockModelPart(map);
        PART_MAP.put(state, part);
        parts.add(part);
        bqr.reset();
    }


    public static List<BakedQuad> makeSnowyBakedQuads(ReUVBakedQuad bqr, List<BakedQuad> quadsCTM, boolean tooTiny) {

        TextureAtlasSprite snow_overlay_sprite = ExtraModelManager.getSprite(ExtraModelManager.snow_overlay);
        TextureAtlasSprite snow_overlay_tiny_sprite = ExtraModelManager.getSprite(ExtraModelManager.snow_overlay_tiny);
        TextureAtlasSprite snow_sprite = ExtraModelManager.getSprite(ExtraModelManager.snow);

        float offset = 0.5f;
        boolean isSlabDown = false;
        List<BakedQuad> original = new ArrayList<>(quadsCTM.size());
        for (BakedQuad bakedQuad : quadsCTM) {
            Direction bakedQuadDirection = bakedQuad.direction();
            if (bakedQuadDirection != Direction.DOWN) {
                TextureAtlasSprite spriteUse;
                if (bakedQuadDirection != Direction.UP) {
                    isSlabDown = true;
                    float maxY = QuadFilter.getMaxY(bakedQuad);
                    offset = 1 - maxY;
                    if (offset < 0.00001f) {
                        offset = 0;
                        isSlabDown = false;
                    }
                }

                if (bakedQuadDirection == Direction.UP) spriteUse = snow_sprite;
                else {
                    if (tooTiny)
                        spriteUse = snow_overlay_tiny_sprite;
                    else
                        spriteUse = QuadFilter.getMaxY(bakedQuad) - QuadFilter.getMinY(bakedQuad) > 0.4002f ? snow_overlay_sprite : snow_overlay_tiny_sprite;

                }
                original.add(
                        bqr.setQuad(bakedQuad)
                                .setTexture(spriteUse)
                                .setSlabDown(isSlabDown)
                                .setOffset(offset)
                                .to()
                );

            }
        }

        return original;
    }
}
