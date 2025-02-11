package com.teamtea.eclipticseasons.client.render;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.common.registry.EffectRegistry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostPass;

public class ClientRenderer {
    public static long reMainTick = 0;

    private static float getProgress(boolean fadeIn) {
        return Math.min(fadeIn ? (1 - reMainTick / 100f) : reMainTick / 100f, 1);
    }

    public static final int NONE_BLUR = 1;
    public static final int ON_BLUR = 2;
    public static final int TO_BLUR = 3;
    public static final int CLEAR_BLUR = 3;

    public static int oldBlurStatus = NONE_BLUR;

    public static void applyEffect(GameRenderer gameRenderer, LocalPlayer player) {
        if (player == null) return;


        int blurStatus =
                CommonConfig.Temperature.heatStroke.get()&&
                player.hasEffect(EffectRegistry.HEAT_STROKE)
                ? ON_BLUR : NONE_BLUR;
        if (blurStatus != oldBlurStatus) {
            if (blurStatus == ON_BLUR) {
                {
                    gameRenderer.loadEffect(EclipticSeasons.rl("shaders/post/fade_in_blur.json"));
                }
            }

            if (reMainTick > 0) {
                reMainTick--;
            } else reMainTick = 100;

            float progress = getProgress(blurStatus == ON_BLUR) * 0.03f;
            // if (progress != prevProgress)
            {
                // prevProgress = progress;
                updateUniform("Progress", progress);
            }
            // EclipticSeasons.logger(reMainTick, progress, blurStatus, oldBlurStatus);
            if (reMainTick == 0) {
                oldBlurStatus = blurStatus;
                if (oldBlurStatus == NONE_BLUR) {
                    gameRenderer.shutdownEffect();
                }
            }
        }


    }

    public static void updateUniform(String name, float value) {
        var postChain = Minecraft.getInstance().gameRenderer.currentEffect();
        if (postChain != null)
            for (PostPass postPass : postChain.passes) {
                var uniform = postPass.getEffect().getUniform(name);
                if (uniform != null) {
                    uniform.set(value);
                }
            }
    }

}
