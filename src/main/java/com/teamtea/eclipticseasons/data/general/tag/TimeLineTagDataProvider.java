package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.common.registry.TimeLineRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TimelineTags;
import net.minecraft.world.timeline.Timeline;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class TimeLineTagDataProvider extends TagsProvider<Timeline> {

    public TimeLineTagDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, Registries.TIMELINE, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(TimelineTags.IN_OVERWORLD)
                .addOptional(TimeLineRegistry.SEASON_GOING);
    }
}
