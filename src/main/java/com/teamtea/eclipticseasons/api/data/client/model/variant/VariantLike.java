package com.teamtea.eclipticseasons.api.data.client.model.variant;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.resources.ResourceLocation;

@Getter
public class VariantLike extends Variant {
    public static final Codec<VariantLike> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("model").forGetter(Variant::getModelLocation),
            Codec.INT.optionalFieldOf("x", 0).forGetter(VariantLike::getX),
            Codec.INT.optionalFieldOf("y", 0).forGetter(VariantLike::getY),
            Codec.BOOL.optionalFieldOf("uvlock", false).forGetter(Variant::isUvLocked),
            Codec.INT.optionalFieldOf("weight", 1).forGetter(Variant::getWeight)
    ).apply(ins, VariantLike::new));

    private final int x;
    private final int y;

    public VariantLike(ResourceLocation modelLocation, int x, int y, boolean uvLock, int weight) {
        super(modelLocation, BlockModelRotation.by(x, y).getRotation(), uvLock, weight);
        this.x = x;
        this.y = y;
    }

    public static VariantBuilder builder(ResourceLocation modelLocation){
        return new VariantBuilder(modelLocation);
    }

    public static class VariantBuilder {
        private ResourceLocation modelLocation;
        private int rotationX = 0;
        private int rotationY = 0;
        private boolean uvLocked = false;
        private int weight = 1;

        public VariantBuilder(ResourceLocation modelLocation) {
            this.modelLocation = modelLocation;
        }

        public VariantBuilder model(ResourceLocation modelLocation) {
            this.modelLocation = modelLocation;
            return this;
        }

        public VariantBuilder rotationX(int rotationX) {
            this.rotationX = rotationX;
            return this;
        }

        public VariantBuilder rotationY(int rotationY) {
            this.rotationY = rotationY;
            return this;
        }

        public VariantBuilder uvLocked(boolean uvLocked) {
            this.uvLocked = uvLocked;
            return this;
        }

        public VariantBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        public VariantLike build() {
            if (modelLocation == null) {
                throw new IllegalStateException("Model location must be set");
            }
            Preconditions.checkArgument(BlockModelRotation.by(rotationX, rotationY) != null, "Invalid model rotation x=%d, y=%d", rotationX, rotationY);
            return new VariantLike(modelLocation, rotationX, rotationY, uvLocked, weight);
        }
    }
}
