package com.teamtea.eclipticseasons.client.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.common.registry.ParticleRegistry;
import net.minecraft.core.particles.DustParticleOptionsBase;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

public class ColorParticleOptions extends DustParticleOptionsBase {
    
    public static final Codec<ColorParticleOptions> CODEC = RecordCodecBuilder.create((p_253370_) -> p_253370_.group(ExtraCodecs.VECTOR3F.fieldOf("color").forGetter(DustParticleOptionsBase::getColor), Codec.FLOAT.fieldOf("scale").forGetter(DustParticleOptionsBase::getScale)).apply(p_253370_, ColorParticleOptions::new));
    public static final ParticleOptions.Deserializer<ColorParticleOptions> DESERIALIZER = new ParticleOptions.Deserializer<ColorParticleOptions>() {
        public ColorParticleOptions fromCommand(ParticleType<ColorParticleOptions> p_123689_, StringReader p_123690_) throws CommandSyntaxException {
            Vector3f vector3f = DustParticleOptionsBase.readVector3f(p_123690_);
            p_123690_.expect(' ');
            float f = p_123690_.readFloat();
            return new ColorParticleOptions(vector3f, f);
        }

        public ColorParticleOptions fromNetwork(ParticleType<ColorParticleOptions> p_123692_, FriendlyByteBuf p_123693_) {
            return new ColorParticleOptions(DustParticleOptionsBase.readVector3f(p_123693_), p_123693_.readFloat());
        }
    };
    
    public ColorParticleOptions(Vector3f p_253868_, float p_254154_) {
        super(p_253868_, p_254154_);
    }

    @Override
    public ParticleType<ColorParticleOptions> getType() {
        return ParticleRegistry.FALLEN_LEAVES;
    }
}
