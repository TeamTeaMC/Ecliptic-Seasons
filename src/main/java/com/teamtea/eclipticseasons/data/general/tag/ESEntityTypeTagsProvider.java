package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.api.constant.game.BreedSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.AnimalBehaviorTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.EntityTypeIds;
import net.minecraft.world.entity.EntityTypes;
import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ESEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ESEntityTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture, String modId) {
        super(packOutput, providerCompletableFuture, modId);
    }

    @Override
    public void addTags(HolderLookup.@NonNull Provider lookupProvider) {

        for (BreedSeasonType value : BreedSeasonType.values()) {
            tag(value.getTag());
        }
        tag(BreedSeasonType.SPRING.getTag())
                .add(EntityTypeIds.WOLF)
                .add(EntityTypeIds.BEE)
        ;
        tag(BreedSeasonType.SP_SU.getTag())
                .add(EntityTypeIds.COW)
                .add(EntityTypeIds.SHEEP)
                .add(EntityTypeIds.PIG)
                .add(EntityTypeIds.HORSE)
                .add(EntityTypeIds.DONKEY)
                .add(EntityTypeIds.PANDA)
                .add(EntityTypeIds.TURTLE)
                .add(EntityTypeIds.LLAMA)
                .add(EntityTypeIds.FROG)
        ;
        tag(BreedSeasonType.SP_AU.getTag())
                .add(EntityTypeIds.CAT)
                .add(EntityTypeIds.OCELOT)
                .add(EntityTypeIds.RABBIT)
        ;
        tag(BreedSeasonType.SU_AU.getTag())
                .add(EntityTypeIds.CAMEL)
        ;
        tag(BreedSeasonType.WINTER.getTag())
                .add(EntityTypeIds.FOX)
        ;
        tag(BreedSeasonType.ALL.getTag())
                .add(EntityTypeIds.STRIDER)
        ;

        tag(AnimalBehaviorTag.DAY)
                .add(EntityTypeIds.WOLF)
                .add(EntityTypeIds.BEE)
                .add(EntityTypeIds.COW)
                .add(EntityTypeIds.SHEEP)
                .add(EntityTypeIds.PIG)
                .add(EntityTypeIds.HORSE)
                .add(EntityTypeIds.DONKEY)
                .add(EntityTypeIds.PANDA)
                .add(EntityTypeIds.TURTLE)
                .add(EntityTypeIds.LLAMA)
                .add(EntityTypeIds.CAT)
                .add(EntityTypeIds.OCELOT)
                .add(EntityTypeIds.RABBIT)
                .add(EntityTypeIds.CAMEL)
                .add(EntityTypeIds.FOX)
        ;

        tag(AnimalBehaviorTag.NIGHT)
                .add(EntityTypeIds.FROG)
        ;

        tag(AnimalBehaviorTag.ALL_TIME)
                .add(EntityTypeIds.STRIDER)
        ;
    }
    
    
}
