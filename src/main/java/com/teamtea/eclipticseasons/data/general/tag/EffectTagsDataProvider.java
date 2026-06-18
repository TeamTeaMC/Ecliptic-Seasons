package com.teamtea.eclipticseasons.data.general.tag;

import com.teamtea.eclipticseasons.api.constant.tag.ESMobEffectTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;

import org.jspecify.annotations.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class EffectTagsDataProvider extends TagsProvider<MobEffect> {


    public EffectTagsDataProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, Registries.MOB_EFFECT, lookupProvider, modId);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider provider) {
        tag(ESMobEffectTags.HEATSTROKE_RESISTANT).add(MobEffects.FIRE_RESISTANCE.getKey());
    }
}
