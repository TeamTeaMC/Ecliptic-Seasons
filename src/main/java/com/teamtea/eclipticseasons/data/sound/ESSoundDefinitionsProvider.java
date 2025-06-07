package com.teamtea.eclipticseasons.data.sound;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.common.registry.SoundEventsRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ESSoundDefinitionsProvider extends SoundDefinitionsProvider {

    private final String modId;

    public ESSoundDefinitionsProvider(PackOutput output, String modId, ExistingFileHelper helper) {
        super(output, modId, helper);
        this.modId=modId;
    }

    @Override
    public void registerSounds() {
        BuiltInRegistries.SOUND_EVENT.stream().filter(soundEvent -> soundEvent.getLocation().getNamespace().equals(modId)).forEach(
                this::add
        );
    }

    public ResourceLocation fixPath(ResourceLocation input){
        return ResourceLocation.fromNamespaceAndPath(input.getNamespace(),input.getPath().replaceAll("\\.","/"));
    }

    public void add(SoundEvent soundEvent){
        add(soundEvent, SoundDefinition.definition().with(SoundDefinition.Sound.sound(
                fixPath(soundEvent.getLocation()), SoundDefinition.SoundType.SOUND
        ).stream(true)));
    }
}
