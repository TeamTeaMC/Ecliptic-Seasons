package com.teamtea.eclipticseasons.data.general.sound;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SoundDefinition;
import net.minecraftforge.common.data.SoundDefinitionsProvider;

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
        return new ResourceLocation(input.getNamespace(),input.getPath().replaceAll("\\.","/"));
    }

    public void add(SoundEvent soundEvent){
        add(soundEvent, SoundDefinition.definition().with(SoundDefinition.Sound.sound(
                fixPath(soundEvent.getLocation()), SoundDefinition.SoundType.SOUND
        ).stream(true)));
    }
}
