package me.cortex.voxy.client.core;

public interface IGetVoxyRenderSystem {
    VoxyRenderSystem getVoxyRenderSystem();

    void voxy$shutdownRenderer();

    void voxy$createRenderer();
}
