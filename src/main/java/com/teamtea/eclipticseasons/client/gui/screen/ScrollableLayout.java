package com.teamtea.eclipticseasons.client.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ScrollableLayout implements Layout {
    private static final int SCROLL_RATE = 12;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_SPACING = 4;
    private static final int SCROLLBAR_MIN_HEIGHT = 32;

    private final Layout content;
    private final Container container;

    private int minWidth;
    private int minHeight;
    private int maxHeight;

    public ScrollableLayout(Minecraft minecraft, Layout content, int maxHeight) {
        this.content = content;
        this.maxHeight = Math.max(1, maxHeight);
        this.container = new Container(minecraft, 0, 0, 1, 1);
    }

    public void setMinWidth(int minWidth) {
        this.minWidth = Math.max(0, minWidth);
        this.arrangeElements();
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = Math.max(0, minHeight);
        this.arrangeElements();
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = Math.max(1, maxHeight);
        this.arrangeElements();
    }

    @Override
    public void arrangeElements() {
        this.content.arrangeElements();

        int contentWidth = Math.max(1, this.content.getWidth());
        int contentHeight = Math.max(1, this.content.getHeight());

        boolean needScrollbar = contentHeight > this.maxHeight;
        int scrollbarReserve = needScrollbar ? SCROLLBAR_WIDTH + SCROLLBAR_SPACING : 0;

        int width = Math.max(contentWidth + scrollbarReserve, this.minWidth);


        // int height = Math.max(
        //         this.minHeight,
        //         Math.min(contentHeight, this.maxHeight)
        // );
        int height = this.maxHeight;

        this.container.setWidthValue(width);
        this.container.setHeightValue(Math.max(1, height));
        this.container.refreshChildren();
        this.container.clampScroll();

        this.content.setX(this.container.getX());
        this.content.setY(this.container.getY() - this.container.scrollAmount);

    }

    @Override
    public void visitChildren(Consumer<LayoutElement> consumer) {
        consumer.accept(this.container);
    }

    @Override
    public void setX(int x) {
        this.container.setX(x);
        this.content.setX(x);
    }

    @Override
    public void setY(int y) {
        this.container.setY(y);
        this.content.setY(y - this.container.scrollAmount);
    }

    @Override
    public int getX() {
        return this.container.getX();
    }

    @Override
    public int getY() {
        return this.container.getY();
    }

    @Override
    public int getWidth() {
        return this.container.getWidth();
    }

    @Override
    public int getHeight() {
        return this.container.getHeight();
    }

    private class Container extends AbstractContainerWidget {
        private final Minecraft minecraft;
        private final List<AbstractWidget> widgets = new ArrayList<>();

        private int scrollAmount;
        private boolean draggingScrollbar;
        private int scrollbarGrabOffset;

        public Container(Minecraft minecraft, int x, int y, int width, int height) {
            super(x, y, width, height, CommonComponents.EMPTY);
            this.minecraft = minecraft;
            this.refreshChildren();
        }

        private void setWidthValue(int width) {
            this.width = Math.max(1, width);
        }

        private void setHeightValue(int height) {
            this.height = Math.max(1, height);
        }

        private void refreshChildren() {
            this.widgets.clear();
            ScrollableLayout.this.content.visitWidgets(this.widgets::add);
        }

        private int contentHeight() {
            return Math.max(1, ScrollableLayout.this.content.getHeight());
        }

        private int maxScroll() {
            return Math.max(0, this.contentHeight() - this.height);
        }

        private boolean scrollable() {
            return this.maxScroll() > 0;
        }

        private void clampScroll() {
            this.setScrollAmount(this.scrollAmount);
        }

        private void setScrollAmount(int amount) {
            this.scrollAmount = Mth.clamp(amount, 0, this.maxScroll());
            ScrollableLayout.this.content.setY(this.getY() - this.scrollAmount);
        }

        private int scrollbarX() {
            return this.getX() + this.width - SCROLLBAR_WIDTH;
        }

        private int scrollbarHeight() {
            if (!this.scrollable()) {
                return this.height;
            }

            return Mth.clamp(
                    this.height * this.height / this.contentHeight(),
                    SCROLLBAR_MIN_HEIGHT,
                    Math.max(SCROLLBAR_MIN_HEIGHT, this.height)
            );
        }

        private int scrollbarY() {
            if (!this.scrollable()) {
                return this.getY();
            }

            int travel = Math.max(1, this.height - this.scrollbarHeight());
            return this.getY() + this.scrollAmount * travel / Math.max(1, this.maxScroll());
        }

        private boolean overScrollbar(double mouseX, double mouseY) {
            return this.scrollable()
                    && mouseX >= this.scrollbarX()
                    && mouseX < this.scrollbarX() + SCROLLBAR_WIDTH
                    && mouseY >= this.getY()
                    && mouseY < this.getY() + this.height;
        }

        private boolean overScrollbarThumb(double mouseX, double mouseY) {
            int y = this.scrollbarY();
            int h = this.scrollbarHeight();

            return this.overScrollbar(mouseX, mouseY)
                    && mouseY >= y
                    && mouseY < y + h;
        }

        private void scrollToMouse(double mouseY) {
            int barHeight = this.scrollbarHeight();
            int travel = Math.max(1, this.height - barHeight);
            int localY = (int) mouseY - this.getY() - this.scrollbarGrabOffset;
            int maxScroll = Math.max(1, this.maxScroll());

            this.setScrollAmount(localY * maxScroll / travel);
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            graphics.enableScissor(
                    this.getX(),
                    this.getY(),
                    this.getX() + this.width,
                    this.getY() + this.height
            );

            for (AbstractWidget widget : this.widgets) {
                if (this.isWidgetVisible(widget)) {
                    widget.render(graphics, mouseX, mouseY, partialTick);
                }
            }

            graphics.disableScissor();

            this.renderScrollbar(graphics);
        }

        private boolean isWidgetVisible(AbstractWidget widget) {
            return widget.getY() + widget.getHeight() > this.getY()
                    && widget.getY() < this.getY() + this.height;
        }

        private void renderScrollbar(GuiGraphics graphics) {
            if (!this.scrollable()) {
                return;
            }

            int x0 = this.scrollbarX();
            int x1 = x0 + SCROLLBAR_WIDTH;
            int y0 = this.getY();
            int y1 = this.getY() + this.height;

            int barY = this.scrollbarY();
            int barH = this.scrollbarHeight();

            graphics.fill(x0, y0, x1, y1, 0x44000000);
            graphics.fill(x0, barY, x1, barY + barH, 0xAAFFFFFF);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            if (!this.visible || !this.isMouseOver(mouseX, mouseY)) {
                return false;
            }

            this.setScrollAmount(this.scrollAmount - (int) (scrollY * SCROLL_RATE));
            return true;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.isMouseOver(mouseX, mouseY)) {
                return false;
            }

            if (button == 0 && this.overScrollbar(mouseX, mouseY)) {
                if (this.overScrollbarThumb(mouseX, mouseY)) {
                    this.scrollbarGrabOffset = (int) mouseY - this.scrollbarY();
                } else {
                    this.scrollbarGrabOffset = this.scrollbarHeight() / 2;
                    this.scrollToMouse(mouseY);
                }

                this.draggingScrollbar = true;
                return true;
            }

            return super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            this.draggingScrollbar = false;
            return super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            if (this.draggingScrollbar && button == 0 && this.scrollable()) {
                this.scrollToMouse(mouseY);
                return true;
            }

            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        @Override
        public void setFocused(@Nullable GuiEventListener focused) {
            super.setFocused(focused);

            if (focused != null && this.minecraft.getLastInputType().isKeyboard()) {
                var area = this.getRectangle();
                var rect = focused.getRectangle();

                int topDelta = rect.top() - area.top();
                int bottomDelta = rect.bottom() - area.bottom();

                if (topDelta < 0) {
                    this.setScrollAmount(this.scrollAmount + topDelta - SCROLL_RATE);
                } else if (bottomDelta > 0) {
                    this.setScrollAmount(this.scrollAmount + bottomDelta + SCROLL_RATE);
                }
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return this.widgets;
        }

        // @Override
        // public Collection<? extends NarratableEntry> getNarratables() {
        //     return this.widgets;
        // }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
        }
    }
}