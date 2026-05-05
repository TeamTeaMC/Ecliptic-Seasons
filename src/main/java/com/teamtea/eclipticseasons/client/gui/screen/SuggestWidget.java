package com.teamtea.eclipticseasons.client.gui.screen;

import lombok.Setter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SuggestWidget extends AbstractWidget {
    private final Font font;
    @Setter
    private Consumer<String> onSelect;
    @Setter
    private EditBox editBox;
    private List<String> suggestions = new ArrayList<>();
    private int hoveredIndex = -1;

    public SuggestWidget(int x, int y, int width, Font font, Consumer<String> onSelect) {
        super(x, y, 150, 0, Component.empty());
        this.font = font;
        this.onSelect = onSelect;
    }

    public void setSuggestions(List<String> newSuggestions) {
        if (editBox != null && newSuggestions.size() == 1 && Objects.equals(newSuggestions.getFirst(), editBox.getValue()))
            newSuggestions = List.of();
        this.suggestions = newSuggestions;
        this.height = suggestions.size() * 14;
        this.visible = !suggestions.isEmpty();
    }

    @Override
    public int getX() {
        return editBox == null ? super.getX() : editBox.getX();
    }

    @Override
    public int getY() {
        return editBox == null ? super.getY() : editBox.getY() - height;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return editBox != null && !suggestions.isEmpty() && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean doubleClick) {
        if (this.visible && this.isMouseOver(event.x(), event.y()) && hoveredIndex != -1) {
            onSelect.accept(suggestions.get(hoveredIndex));
            return true;
        }
        return false;
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, Component.literal("Suggestions"));
        setPosition(getX(), getY());
    }

    @Override
    protected void extractWidgetRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (suggestions.isEmpty()) return;
        if (editBox == null) return;

        int startX = getX();
        int startY = getY();

        graphics.fill(startX, startY, startX + width, startY + height, 0xCC000000);
        graphics.outline(startX, startY, width, height, 0xFFFFFFFF);

        for (int i = 0; i < suggestions.size(); i++) {
            int itemY = startY + (i * 14);
            boolean isHovered = mouseX >= startX && mouseX <= startX + width
                    && mouseY >= itemY && mouseY < itemY + 14;
            String rawText = suggestions.get(i);

            if (isHovered) {
                hoveredIndex = i;
                graphics.fill(startX + 1, itemY + 1, startX + width - 1, itemY + 13, 0x44FFFFFF);
                graphics.setTooltipForNextFrame(Component.translatable(BuiltInRegistries.BLOCK
                        .get(Identifier.parse(rawText)).get().value().getDescriptionId()),mouseX,mouseY);
            }
            int maxWidth = width - 8;
            String displayedText = font.ellipsize(FormattedText.of(rawText), maxWidth).getString();
            graphics.text(font, displayedText, startX + 4, itemY + 3, 0xFFFFFFFF, false);
        }
    }
}
