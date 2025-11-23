package com.teamtea.eclipticseasons.compat.modernui.state;


import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.data.client.ui.elements.UIElement;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

@Data
@Builder
public class SingleEntryState {
    @NonNull
    private final String id;
    @Nullable
    private final SingleEntryState parent;

    @Nullable
    private String displayName;

    private UIElement self;

    @Builder.Default
    private int index = -1;

    @NotNull
    public String getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable("ui.label.%s.%s".formatted(EclipticSeasonsApi.MODID, id))
                    .getString();
        }
        return displayName + "";
    }
}
