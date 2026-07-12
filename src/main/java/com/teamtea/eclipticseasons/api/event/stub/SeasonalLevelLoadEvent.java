package com.teamtea.eclipticseasons.api.event.stub;

import com.teamtea.eclipticseasons.api.event.IESEvent;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a level is about to become the active level used by
 * Ecliptic Seasons.
 *
 * <p>On the client, this event is fired from {@code Minecraft#setLevel}
 * before NeoForge posts {@link Unload} for the previous active
 * level. This ordering intentionally mirrors the usual level-load lifecycle.</p>
 *
 * <p>Unlike {@link Load}, this event is only posted for a level
 * being bound as the active client level, so temporary or fake
 * {@code ClientLevel} instances created by other mods do not trigger it.</p>
 */
public class SeasonalLevelLoadEvent extends LevelEvent implements IESEvent {
    public SeasonalLevelLoadEvent(Level level) {
        super(level);
    }

    @Override
    public @NotNull Level getLevel() {
        return (Level) super.getLevel();
    }
}
