package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.api.constant.game.BreedSeasonType;
import com.teamtea.eclipticseasons.api.constant.tag.AnimalBehaviorTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ESEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ESEntityTypeTagsProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> providerCompletableFuture, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, providerCompletableFuture, modId, existingFileHelper);
    }

    @Override
    public void addTags(HolderLookup.@NotNull Provider lookupProvider) {

        for (BreedSeasonType value : BreedSeasonType.values()) {
            tag(value.getTag());
        }
        tag(BreedSeasonType.SPRING.getTag())
                .add(EntityType.WOLF)
                .add(EntityType.BEE)
        ;
        tag(BreedSeasonType.SP_SU.getTag())
                .add(EntityType.COW)
                .add(EntityType.SHEEP)
                .add(EntityType.PIG)
                .add(EntityType.HORSE)
                .add(EntityType.DONKEY)
                .add(EntityType.PANDA)
                .add(EntityType.TURTLE)
                .add(EntityType.LLAMA)
                .add(EntityType.FROG)
        ;
        tag(BreedSeasonType.SP_AU.getTag())
                .add(EntityType.CAT)
                .add(EntityType.OCELOT)
                .add(EntityType.RABBIT)
        ;
        tag(BreedSeasonType.SU_AU.getTag())
                .add(EntityType.CAMEL)
        ;
        tag(BreedSeasonType.WINTER.getTag())
                .add(EntityType.FOX)
        ;
        tag(BreedSeasonType.ALL.getTag())
                .add(EntityType.STRIDER)
        ;

        tag(AnimalBehaviorTag.DAY)
                .add(EntityType.WOLF)
                .add(EntityType.BEE)
                .add(EntityType.COW)
                .add(EntityType.SHEEP)
                .add(EntityType.PIG)
                .add(EntityType.HORSE)
                .add(EntityType.DONKEY)
                .add(EntityType.PANDA)
                .add(EntityType.TURTLE)
                .add(EntityType.LLAMA)
                .add(EntityType.CAT)
                .add(EntityType.OCELOT)
                .add(EntityType.RABBIT)
                .add(EntityType.CAMEL)
                .add(EntityType.FOX)
        ;

        tag(AnimalBehaviorTag.NIGHT)
                .add(EntityType.FROG)
        ;

        tag(AnimalBehaviorTag.ALL_TIME)
                .add(EntityType.STRIDER)
        ;
    }
}
