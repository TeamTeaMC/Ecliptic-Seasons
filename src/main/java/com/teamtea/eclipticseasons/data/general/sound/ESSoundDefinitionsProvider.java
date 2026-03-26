package com.teamtea.eclipticseasons.data.general.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

public class ESSoundDefinitionsProvider extends SoundDefinitionsProvider {

    private final String modId;

    public ESSoundDefinitionsProvider(PackOutput output, String modId) {
        super(output, modId);
        this.modId=modId;
    }

    @Override
    public void registerSounds() {
        BuiltInRegistries.SOUND_EVENT.stream().filter(soundEvent -> soundEvent.location().getNamespace().equals(modId)).forEach(
                this::add
        );
    }

    public Identifier fixPath(Identifier input){
        return Identifier.fromNamespaceAndPath(input.getNamespace(),input.getPath().replaceAll("\\.","/"));
    }

    public void add(SoundEvent soundEvent){
        add(soundEvent, SoundDefinition.definition().with(SoundDefinition.Sound.sound(
                fixPath(soundEvent.location()), SoundDefinition.SoundType.SOUND
        ).stream(true)));
    }
}
