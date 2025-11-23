package com.teamtea.eclipticseasons.api.event;


import com.teamtea.eclipticseasons.common.core.biome.WeatherManager;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;


/**
 * The event is fired on the {@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class BeforeCheckSnowStatusEvent extends net.neoforged.bus.api.Event implements IESEvent {
    private final ServerLevel level;
    private final Holder<Biome> biome;
    /**
     * If it equals the {@link BlockPos#ZERO }, it should be ignored.
     **/
    private final BlockPos pos;

    /**
     * Set a value to change the result.
     **/
    @Builder.Default
    private WeatherManager.SnowRenderStatus status = null;
    /**
     * You can modify it if you are making an extra check.
     **/
    private boolean rain;
}
