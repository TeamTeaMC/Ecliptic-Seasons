package me.cortex.voxy.client.core;

public interface IGetVoxyRenderSystem {
    VoxyRenderSystem getVoxyRenderSystem();

    void shutdownRenderer();

    void createRenderer();
}
