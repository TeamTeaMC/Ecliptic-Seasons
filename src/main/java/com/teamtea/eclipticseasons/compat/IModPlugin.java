package com.teamtea.eclipticseasons.compat;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.ModConfigSpec;

public interface IModPlugin {
    String getId();

    boolean isLoad();

    void setStatus(boolean status);

    default void registerGameBus(IEventBus gameBus){};

    default void registerModBus(IEventBus gameBus){};

    default void loadConfig(ModConfigSpec.Builder builder){};

    default void init(){};

    default void setup(){};

}
