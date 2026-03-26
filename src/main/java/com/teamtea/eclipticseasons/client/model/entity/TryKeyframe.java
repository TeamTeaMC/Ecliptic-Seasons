package com.teamtea.eclipticseasons.client.model.entity;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TryKeyframe {

    public static void animate(ModelPart model, AnimationDefinition animationDefinition, long accumulatedTime, float scale, Vector3f animationVecCache) {
        float f = getElapsedSeconds(animationDefinition, accumulatedTime);

        for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.boneAnimations().entrySet()) {
            if (!model.hasChild(entry.getKey())) continue;
            Optional<ModelPart> optional = Optional.of(model.getChild(entry.getKey()));
            List<AnimationChannel> list = entry.getValue();
            optional.ifPresent(modelPart -> list.forEach(animationChannel -> {
                Keyframe[] akeyframe = animationChannel.keyframes();
                int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, i1 -> f <= akeyframe[i1].timestamp()) - 1);
                int j = Math.min(akeyframe.length - 1, i + 1);
                Keyframe keyframe = akeyframe[i];
                Keyframe keyframe1 = akeyframe[j];
                float f1 = f - keyframe.timestamp();
                float f2;
                if (j != i) {
                    f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
                } else {
                    f2 = 0.0F;
                }

                keyframe1.interpolation().apply(animationVecCache, f2, akeyframe, i, j, scale);
                animationChannel.target().apply(modelPart, animationVecCache);
            }));
        }
    }

    // KeyframeAnimations
    private static float getElapsedSeconds(AnimationDefinition animationDefinition, long accumulatedTime) {
        float f = (float) accumulatedTime / 1000.0F;
        return animationDefinition.looping() ? f % animationDefinition.lengthInSeconds() : f;
    }

}
