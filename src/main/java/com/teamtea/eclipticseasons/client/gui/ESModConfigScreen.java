package com.teamtea.eclipticseasons.client.gui;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsList;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ESModConfigScreen extends Screen {
    private final List<ConfigEntry> entries = new ArrayList<>();
    private final Screen lastScreen;
    private HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
    private static final Component TITLE = Component.translatable("options.title");


    public ESModConfigScreen(Screen lastScreen) {
        super(Component.literal("Ecliptic Seasons"));
        this.lastScreen = lastScreen;

        // entries.add(new TitleEntry("Season Settings"));
        // entries.add(new BoolEntry(CommonConfig.Season.enableInform));
        // entries.add(new BoolEntry(CommonConfig.Season.daylightChange));
        // entries.add(new BoolEntry(CommonConfig.Season.dynamicSnowTerm));
        // entries.add(new BoolEntry(CommonConfig.Season.realWorldSolarTerms));
        // // entries.add(new IntSliderEntry(CommonConfig.Season.lastingDaysOfEachTerm, 1, 5000));
        //
        // entries.add(new TitleEntry("Weather Settings"));
        // entries.add(new BoolEntry(CommonConfig.Weather.shouldInitWeather));

        for (UnmodifiableConfig.Entry entry : CommonConfig.COMMON_CONFIG.getValues().entrySet()) {
            if (entry.getValue() instanceof com.electronwill.nightconfig.core.AbstractConfig simpleConfig) {
                entries.add(new TitleEntry(entry.getKey() + " Settings"));
                for (Config.Entry config : simpleConfig.entrySet()) {
                    if (config.getValue() instanceof ModConfigSpec.ConfigValue<?> cv) {
                        if (cv instanceof ModConfigSpec.BooleanValue bv) {
                            entries.add(new BoolEntry(bv));
                        }
                    }
                }
            }
        }

    }

    private ConfigSelectionList list;

    @Override
    protected void init() {
        int buttonWidth = 150;
        int startX = this.width / 2 - buttonWidth / 2;
        int currentY = 40;
        int spacing = 24;
        this.list = new ConfigSelectionList(this.minecraft, this.width, this.height - 64, 32, 25);
        // for (ConfigEntry entry : entries) {
        //     entry.build(this, startX, currentY, buttonWidth);
        //     currentY += (entry instanceof TitleEntry) ? 5 : spacing;
        // }

        LinearLayout header = (LinearLayout) this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        header.addChild(new StringWidget(TITLE, this.font), LayoutSettings::alignHorizontallyCenter);
        LinearLayout subHeader = ((LinearLayout) header.addChild(LinearLayout.horizontal())).spacing(8);
        // subHeader.addChild(this.options.fov().createButton(this.minecraft.options));
        // if (this.inWorld) {
        //     subHeader.addChild(this.createWorldOptionsButtonOrDifficultyButton((Level) Objects.requireNonNull(this.minecraft.level)));
        // } else {
        //     subHeader.addChild(this.createOnlineButton());
        // }

        // GridLayout gridLayout = new GridLayout();
        // gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        // GridLayout.RowHelper helper = gridLayout.createRowHelper(2);

        for (ConfigEntry entry : entries) {
            AbstractWidget build = entry.build(this, startX, currentY, buttonWidth);
            // currentY += (entry instanceof TitleEntry) ? 5 : spacing;
            // build
            int column = build instanceof StringWidget ? 2 : 1;
            if (build != null) {

                // helper.addChild(build, column);
                if (entry instanceof TitleEntry) list.end();
                this.list.addWidget(build);

                // break;
            }
        }


        this.addRenderableWidget(this.list);
        // this.list.addWidget();
        layout.addToContents(this.list);
        layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(200).build());
        layout.visitWidgets(this::addRenderableWidget);

        // this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> {
        //     CommonConfig.COMMON_CONFIG.save();
        //     this.onClose();
        // }).bounds(startX, this.height - 30, buttonWidth, 20).build());
        this.repositionElements();
    }

    protected void repositionElements() {
        this.layout.arrangeElements();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.layout.arrangeElements();
        this.list.updateSize(width, layout);
    }


    @Override
    public void onClose() {
        super.onClose();
    }

    public abstract static class ConfigEntry {
        protected final Component label;

        public ConfigEntry(String translationKey) {
            this.label = Component.translatable(translationKey);
        }

        public abstract AbstractWidget build(ESModConfigScreen screen, int x, int y, int width);
    }

    public static class TitleEntry extends ConfigEntry {
        public TitleEntry(String text) {
            super(text);
        }

        @Override
        public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
            // screen.addRenderableOnly((guiGraphics, mouseX, mouseY, partialTick) ->
            //         guiGraphics.text(screen.getFont(), this.label, (screen.width - screen.font.width(this.label)) / 2, y + 5, -2142128)
            // );
            return new StringWidget(this.label, screen.getFont());
        }
    }

    public Font getFont() {
        return font;
    }

    public static class BoolEntry extends ConfigEntry {
        private final ModConfigSpec.BooleanValue spec;

        public BoolEntry(ModConfigSpec.BooleanValue spec) {
            super("eclipticseasons.configuration." + spec.getPath().getLast());
            this.spec = spec;
        }

        @Override
        public AbstractWidget build(ESModConfigScreen screen, int x, int y, int width) {
            CycleButton<Boolean> booleanCycleButton = CycleButton.onOffBuilder(spec.get())
                    .create(x, y, width, 20, this.label, (button, value) -> spec.set(value));
            booleanCycleButton.setTooltip(Tooltip.create(Component.translatable("eclipticseasons.configuration." + spec.getPath().getLast()).withStyle(ChatFormatting.BOLD).append(Component.translatable("\n\n"+spec.getSpec().getComment()+"")).withStyle(ChatFormatting.RESET)));

            return booleanCycleButton
                    ;
        }
    }

    // public static class IntSliderEntry extends ConfigEntry {
    //     private final ModConfigSpec.IntValue spec;
    //     private final int min, max;
    //
    //     public IntSliderEntry(ModConfigSpec.IntValue spec, int min, int max) {
    //         super(spec.getPath().getLast());
    //         this.spec = spec;
    //         this.min = min;
    //         this.max = max;
    //     }
    //
    //     @Override
    //     public void build(ESModConfigScreen screen, int x, int y, int width) {
    //     }
    // }
}
