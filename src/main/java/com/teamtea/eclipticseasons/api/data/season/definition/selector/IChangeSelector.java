package com.teamtea.eclipticseasons.api.data.season.definition.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.season.definition.ISeasonChangeContext;
import com.teamtea.eclipticseasons.api.data.season.definition.condition.IChangeCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.List;
import java.util.Optional;

public interface IChangeSelector {

    //@SuppressWarnings("unchecked")
    //Codec<IChangeSelector> CCODEC =
    //        Codec.either(
    //                        BlockSelector.CODEC.codec(),
    //                        Codec.STRING
    //                                .xmap(s -> s.contains(":") ? ResourceLocation.parse(s) : EclipticSeasons.rl(s),
    //                                        r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
    //                                .dispatch("type", IChangeSelector::getType, id -> (MapCodec<IChangeSelector>) ChangeSelectors.CONDITIONS.get(id)))
    //                .xmap(e -> e.left().map((bs -> (IChangeSelector) bs)).orElseGet(() -> e.right().orElseThrow()),
    //                        ics -> ics instanceof BlockSelector bs ? Either.left(bs) : Either.right(ics));
    @SuppressWarnings("unchecked")
    Codec<IChangeSelector> CCODEC =
            Codec.either(

                            Codec.STRING
                                    .xmap(s -> s.contains(":") ? Identifier.parse(s) : EclipticSeasons.rl(s),
                                            r -> r.getNamespace().equals(EclipticSeasonsApi.MODID) ? r.getPath() : r.toString())
                                    .dispatch("type", IChangeSelector::getType, id -> (MapCodec<IChangeSelector>) ChangeSelectors.CONDITIONS.get(id)),
                            BlockSelector.CODEC.codec())
                    .xmap(e -> e.right().map((bs -> (IChangeSelector) bs)).orElseGet(() -> e.left().orElseThrow()),
                            ics -> ics instanceof BlockSelector bs ? Either.right(bs) : Either.left(ics));

    int DEFAULT_WEIGHT = 10;

    Identifier getType();

    MapCodec<? extends IChangeSelector> codec();

    boolean place(ServerLevel level, BlockPos origin, ISeasonChangeContext context);


    default int getWeight() {
        return DEFAULT_WEIGHT;
    }


    default List<IChangeCondition> getConditions() {
        return List.of();
    }


    default Optional<ResourceKey<LootTable>> getLoot() {
        return Optional.empty();
    }

    default boolean shouldApply(Level level, BlockPos pos, ISeasonChangeContext context) {
        for (IChangeCondition condition : getConditions()) {
            if (!condition.test(level, pos, context)) return false;
        }
        return true;
    }

    default boolean dropWhenApplied(Level level, BlockPos pos, ISeasonChangeContext context) {
        return true;
    }
}
