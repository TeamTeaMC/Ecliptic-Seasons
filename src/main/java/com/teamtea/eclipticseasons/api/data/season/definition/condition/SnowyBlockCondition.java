package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import lombok.Builder;
import lombok.Data;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;


@Builder
@Data
public class SnowyBlockCondition implements IChangeCondition {

    public static final MapCodec<SnowyBlockCondition> CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
            Vec3i.CODEC.fieldOf("offset").forGetter(o -> o.offset),
            Codec.BOOL.optionalFieldOf("reverse", false).forGetter(o -> o.reverse)
    ).apply(ins, SnowyBlockCondition::new));


    @Builder.Default
    private final Vec3i offset = Vec3i.ZERO;
    @Builder.Default
    private final boolean reverse = false;

    @Override
    public Identifier getType() {
        return ChangeConditions.IS_SNOWY;
    }

    @Override
    public boolean test(Level level, BlockPos pos, ISeasonChangeContext context) {
        BlockPos checkPos = pos.offset(offset);
        return EclipticSeasonsApi.getInstance().isSnowyBlock(level, level.getBlockState(checkPos), checkPos)
                == !reverse;
    }

    @Override
    public MapCodec<? extends IChangeCondition> codec() {
        return CODEC;
    }
}
