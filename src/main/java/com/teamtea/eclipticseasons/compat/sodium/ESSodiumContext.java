package com.teamtea.eclipticseasons.compat.sodium;

import net.caffeinemc.mods.sodium.client.render.frapi.mesh.EncodingFormat;
import net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl;
import net.caffeinemc.mods.sodium.client.services.SodiumModelData;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;


// TODO：暂时废弃这个思路，因为似乎会缓存前一次查询的结果，清理不干净，而且Sodium环境下和Fabric Render API环境下RenderContext不直接兼容。
// 特别是MutableQuadViewImpl等一些细节上。
@Deprecated
@SuppressWarnings("removal")
public class ESSodiumContext extends net.caffeinemc.mods.sodium.client.render.frapi.render.AbstractBlockRenderContext {

    private final List<BakedQuad> quadViews = new ArrayList<>();

    private final MutableQuadViewImpl editorQuad = new MutableQuadViewImpl() {

        {
            this.data = new int[EncodingFormat.TOTAL_STRIDE];
            this.clear();
        }

        @Override
        public void emitDirectly() {
            if (cullFace() != Direction.UP) {
                SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));
                getQuadViews().add(this.toBakedQuad(spriteFinder.find(this)));
            }
        }
    };


    @Override
    public QuadEmitter getEmitter() {
        editorQuad.clear();
        return editorQuad;
    }

    @Override
    public void pushTransform(QuadTransform transform) {
        super.pushTransform(transform);
    }

    @Override
    public void popTransform() {
        super.popTransform();
    }

    @Override
    public BakedModelConsumer bakedModelConsumer() {
        return super.bakedModelConsumer();
    }

    @Override
    protected void processQuad(net.caffeinemc.mods.sodium.client.render.frapi.mesh.MutableQuadViewImpl mutableQuadView) {
        if (mutableQuadView.cullFace() != Direction.UP) {
            SpriteFinder spriteFinder = SpriteFinder.get(Minecraft.getInstance().getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS));
            getQuadViews().add(mutableQuadView.toBakedQuad(spriteFinder.find(mutableQuadView)));
        }
    }

    public void updateContext(BlockAndTintGetter level, BlockPos pos, BlockState state, BakedModel model, RandomSource random, SodiumModelData modelData) {
        this.random = random;
        this.level = level;
        this.pos = pos;
        this.state = state;
        this.getQuadViews().clear();
        this.modelData = modelData;
    }

    public List<BakedQuad> getQuadViews() {
        return quadViews;
    }

    public void reset() {
        this.random = null;
        this.level = null;
        this.pos = null;
        this.state = null;
        this.getQuadViews().clear();
        this.modelData = null;
    }
}
