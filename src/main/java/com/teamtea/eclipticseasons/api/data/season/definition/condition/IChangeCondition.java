package com.teamtea.eclipticseasons.api.data.season.definition.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

public interface IChangeCondition {

    Codec<IChangeCondition> CODEC = Codec.STRING
            .xmap(s -> s.contains(":") ? new ResourceLocation(s) : EclipticSeasons.rl(s),
                    r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
            .dispatch("type", IChangeCondition::getType, c->ChangeConditions.CONDITIONS.get(c).codec());

    ResourceLocation getType();

    boolean test(Level level, BlockPos pos, ISeasonChangeContext context);

    MapCodec<? extends IChangeCondition> codec();
}
