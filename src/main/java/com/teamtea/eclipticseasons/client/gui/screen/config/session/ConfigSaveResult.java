package com.teamtea.eclipticseasons.client.gui.screen.config.session;

public record ConfigSaveResult(
        boolean worldRestartRequired,
        boolean gameRestartRequired
) {
}
