package com.teamtea.eclipticseasons.api.data.crop;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.BlockStatePropertyCondition;
import com.teamtea.eclipticseasons.common.misc.SimplePair;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;

@TestOnly
public record CropGrowControlBuilder(
        BlockPredicate applyTarget,
        Optional<ResourceLocation> parent,
        IdentityHashMap<SolarTerm,CropSeasonControl> seasonList,
        List<CropHumidControl> humidList) {

    public static final Codec<CropGrowControlBuilder> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            BlockPredicate.CODEC.fieldOf("applyTarget").forGetter(CropGrowControlBuilder::applyTarget),
            ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(CropGrowControlBuilder::parent),
            CropSeasonControl.IDENTITY_HASH_CODEC.fieldOf("seasonList").forGetter(CropGrowControlBuilder::seasonList),
            CropHumidControl.CODEC.listOf().fieldOf("humidList").forGetter(CropGrowControlBuilder::humidList)
    ).apply(ins, CropGrowControlBuilder::new));




    public SimplePair<List<CropGrowBlockControl>, List<CropGrowBlockStateControl>> build() {
        SimplePair<List<CropGrowBlockControl>, List<CropGrowBlockStateControl>> pair = SimplePair.of(new ArrayList<>(), new ArrayList<>());


        // if (applyTarget.tag().isPresent()) {
        //     Optional<HolderSet.Named<Block>> holders = BuiltInRegistries.BLOCK.getTag(applyTarget.tag().get());
        //     if (holders.isPresent()) {
        //         for (Block block : holders.get().stream().map(Holder::value).toList()) {
        //             pair.getKey().add(new CropGrowBlockControl(block, seasonMap, humidMap));
        //         }
        //     }
        // } else if (applyTarget.id().isPresent()) {
        //     if (applyTarget.properties().isEmpty()) {
        //         pair.getKey().add(new CropGrowBlockControl(applyTarget.id().get().value(), seasonMap, humidMap));
        //     } else {
        //         for (BlockState possibleState : applyTarget.id().get().value().getStateDefinition().getPossibleStates()) {
        //             if (applyTarget.properties().get().matches(possibleState))
        //                 pair.getValue().add(new CropGrowBlockStateControl(possibleState, seasonMap, humidMap));
        //         }
        //
        //     }
        // }
        return pair;
    }

    public record CropGrowBlockStateControl(
            BlockState target,
            // TODO:这里是不是用array查表更快
            IdentityHashMap<SolarTerm, CropSeasonControl> seasonMap,
            IdentityHashMap<Humidity, CropHumidControl> humidMap) {
    }

    public record CropGrowBlockControl(
            Block target,
            IdentityHashMap<SolarTerm, CropSeasonControl> seasonMap,
            IdentityHashMap<Humidity, CropHumidControl> humidMap) {
    }


}
