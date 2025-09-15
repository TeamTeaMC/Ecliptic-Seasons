package com.teamtea.eclipticseasons.common.registry;

import com.teamtea.eclipticseasons.EclipticSeasons;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import org.jetbrains.annotations.NotNull;

public class SongRegistry {
    public static final ResourceKey<JukeboxSong> SNOWLESS_HOMETOWN = createKey("snowless_hometown");


    private static ResourceKey<JukeboxSong> createKey(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, EclipticSeasons.rl(name));
    }


    private static void register2(
            BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> key, SoundEvent soundEvent, int lengthInSeconds, int comparatorOutput
    ) {
        context.register(
                key,
                new JukeboxSong(BuiltInRegistries.SOUND_EVENT.getHolderOrThrow(BuiltInRegistries.SOUND_EVENT.getResourceKey(soundEvent).get()), Component.translatable(toLangKey(key)), (float) lengthInSeconds, comparatorOutput)
        );
    }

    public static @NotNull String toLangKey(ResourceKey<JukeboxSong> key) {
        return Util.makeDescriptionId("jukebox_song", key.location());
    }

    public static void bootstrap(BootstrapContext<JukeboxSong> context) {
        register2(context, SNOWLESS_HOMETOWN, SoundEventsRegistry.snowless_hometown, 243, 14);
    }
}
