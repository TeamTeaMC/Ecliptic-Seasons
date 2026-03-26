package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.common.registry.TimeLineRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.tags.TimelineTags;
import net.minecraft.world.timeline.Timeline;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class TimeLineTagDataProvider extends KeyTagProvider<Timeline> {

    public TimeLineTagDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, Registries.TIMELINE, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        tag(TimelineTags.IN_OVERWORLD)
                .addOptional(TimeLineRegistry.SEASON_GOING);
    }
}
