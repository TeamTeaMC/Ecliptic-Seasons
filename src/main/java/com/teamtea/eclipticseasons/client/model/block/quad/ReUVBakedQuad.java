package com.teamtea.eclipticseasons.client.model.block.quad;


import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.client.model.quad.MutableQuad;
import org.joml.Vector3fc;

@Setter
@Accessors(chain = true)
public class ReUVBakedQuad {
    private TextureAtlasSprite texture;
    private boolean isSlabDown;
    private float offset;
    private final MutableQuad mutableQuad = new MutableQuad();
    private BakedQuad quad;

    public void reset() {
        this.texture = null;
        // this.mutableQuad = null;
        this.quad = null;
        offset = 0;
        isSlabDown = false;
    }

    // public void clear() {
    //     reset();
    //     this.mutableQuad = null;
    // }

    public BakedQuad to() {
        remapQuad();
        BakedQuad bakedQuad = mutableQuad.toBakedQuad();
        reset();
        return bakedQuad;
    }

    private void remapQuad() {
        // Direction direction1 = getDirection();
        mutableQuad.setFrom(quad);
        // mutableQuad.setSprite(texture.)
        mutableQuad.setSprite(texture, ChunkSectionLayer.CUTOUT, RenderTypes.cutoutMovingBlock());
        // if(true)return;

        float x0 = quad.position0().x();
        float y0 = quad.position0().y();
        float z0 = quad.position0().z();

        float x1 = quad.position1().x();
        float y1 = quad.position1().y();
        float z1 = quad.position1().z();

        float x3 = quad.position3().x();
        float y3 = quad.position3().y();
        float z3 = quad.position3().z();

        // 边向量
        float edge1X = x1 - x0;
        float edge1Y = y1 - y0;
        float edge1Z = z1 - z0;

        float edge2X = x3 - x0;
        float edge2Y = y3 - y0;
        float edge2Z = z3 - z0;

        // 边长度
        float lenU = (float) Math.sqrt(edge2X * edge2X + edge2Y * edge2Y + edge2Z * edge2Z);
        float lenV = (float) Math.sqrt(edge1X * edge1X + edge1Y * edge1Y + edge1Z * edge1Z);

        for (int i = 0; i < 4; ++i) {
            Vector3fc position = quad.position(i);
            float x = position.x();
            float y = position.y();
            float z = position.z();

            // 面局部坐标投影（交换顺序避免 90° 偏转）
            float du = ((x - x0) * edge2X + (y - y0) * edge2Y + (z - z0) * edge2Z)
                    / (edge2X * edge2X + edge2Y * edge2Y + edge2Z * edge2Z);
            float dv = ((x - x0) * edge1X + (y - y0) * edge1Y + (z - z0) * edge1Z)
                    / (edge1X * edge1X + edge1Y * edge1Y + edge1Z * edge1Z);

            // if (isSlabDown) {
            //     dv -= offset;
            // }

            du *= lenU;
            dv *= lenV;

            du = Mth.clamp(du, 0f, 1f);
            dv = Mth.clamp(dv, 0f, 1f);

            mutableQuad.setUv(i,
                    this.texture.getU(du),
                    this.texture.getV(dv)
            );

        }
    }

    // We need not to mul it to 16f because internal changes
    private static float getUnInterpolatedU(TextureAtlasSprite sprite, float u) {
        float f = sprite.getU1() - sprite.getU0();
        return (u - sprite.getU0()) / f * 16.0F;
    }

    private static float getUnInterpolatedV(TextureAtlasSprite sprite, float v) {
        float f = sprite.getV1() - sprite.getV0();
        return (v - sprite.getV0()) / f * 16.0F;
    }

}
