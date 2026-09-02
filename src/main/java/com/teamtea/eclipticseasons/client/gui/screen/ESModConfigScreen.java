package com.teamtea.eclipticseasons.client.gui.screen;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.gui.screen.effect.SeasonalBackgroundEffects;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.TitleEntry;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenText;
import com.teamtea.eclipticseasons.client.gui.screen.config.builtin.ESConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigSaveResult;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigScreenSession;
import com.teamtea.eclipticseasons.client.gui.screen.config.tab.Tab;
import com.teamtea.eclipticseasons.client.gui.screen.widget.ScrollableLayout;
import com.teamtea.eclipticseasons.client.gui.screen.widget.SuggestWidget;
import com.teamtea.eclipticseasons.client.gui.screen.widget.WoodenButtonWidget;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.solar.FixedSolarDataManagerLocal;
import com.teamtea.eclipticseasons.compat.configured.ConfiguredUtil;
import com.teamtea.eclipticseasons.config.util.RestartTypeUtil;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;

public class ESModConfigScreen extends Screen {
    protected Screen parent;
    protected String configModId;
    protected ConfigScreenText screenText;
    protected HeaderAndFooterLayout layout;
    protected ScrollableLayout configScroll;
    protected double scrollAmountAfterResize;
    protected ModContainer mod;
    protected Season season;
    protected SeasonalBackgroundEffects backgroundEffects;
    protected ConfigScreenContext configContext;
    protected ConfigScreenSession configSession;
    @Getter
    protected SuggestWidget globalSuggestWidget;
    public boolean saveOnClose = true;
    protected String allSearchQuery = "";
    protected EditBox allSearchBox;

    public ESModConfigScreen(Minecraft minecraft, Screen parent, ConfigScreenDefinition definition) {
        this(parent, definition);
    }

    public ConfigScreenContext getConfigContext() {
        return configContext;
    }

    public ESModConfigScreen() {
        this(null);
    }

    public ESModConfigScreen(Minecraft minecraft, Screen parent) {
        this(parent);
    }

    @SuppressWarnings({"raw_use"})
    public ESModConfigScreen(Screen parent) {
        this(parent, ESConfigScreenDefinition.INSTANCE);
    }

    public ESModConfigScreen(Screen parent, ConfigScreenDefinition definition) {
        super(definition.text().title());
        this.parent = parent;
        this.configModId = definition.modId();
        this.screenText = definition.text();
        this.season = ClientCon.nowSeason.isValid()
                ? ClientCon.nowSeason
                : new FixedSolarDataManagerLocal(null).getSolarTerm().getSeason();
        backgroundEffects = new SeasonalBackgroundEffects(season);
        configContext = new ConfigScreenContext();
        definition.initialize(configContext);
        configContext.loadSources();
        configContext.sortEntries();
        if (configContext.categories().isEmpty()) {
            throw new IllegalStateException("No config categories registered for " + configModId);
        }
        configSession = definition.createSession(configContext);
    }

    protected boolean matchesSearch(ConfigEntry entry) {
        if (allSearchQuery == null || allSearchQuery.isBlank()) {
            return true;
        }
        return entry.getSearchText().toLowerCase(Locale.ROOT)
                .contains(allSearchQuery.toLowerCase(Locale.ROOT).trim());
    }

    public ESModConfigScreen(ModContainer mod, Screen parent) {
        this(parent);
        this.mod = mod;
    }

    public ESModConfigScreen(
            ModContainer mod,
            Screen parent,
            ConfigScreenDefinition definition
    ) {
        this(parent, definition);
        this.mod = mod;
    }

    @Getter
    protected ConfigCategory selectTab;

    protected int tabSpacing = 8;
    protected int sidebarWidth = 70;
    protected int headerHeight = 40;
    protected int footerHeight = 40;
    protected int widgetHeight = 20;

    @Override
    protected void init() {
        prepareLayout();

        int contentWidth = Math.max(1, width - sidebarWidth - tabSpacing);
        int entryWidth = contentWidth / 2 - 36;
        int footerWidth = width / 2 - 36;

        buildHeader(entryWidth);
        buildContent(entryWidth);
        buildFooter(footerWidth);
        finishLayout();
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        if (configScroll != null) scrollAmountAfterResize = configScroll.scrollAmount();
        super.resize(minecraft, width, height);
    }

    protected void prepareLayout() {
        backgroundEffects.resize(width, height);
        List<ConfigCategory> categories = configContext.categories();
        if (selectTab == null || !configContext.containsCategory(selectTab)) {
            selectTab = categories.contains(ConfigCategory.GENERAL)
                    ? ConfigCategory.GENERAL
                    : categories.stream().findFirst().orElseThrow();
        }

        this.globalSuggestWidget = new SuggestWidget(0, 0, 0, this.font, (i) -> {
        });

        layout = new HeaderAndFooterLayout(this, headerHeight, footerHeight);
        fixContentAlignment();
    }

    protected void fixContentAlignment() {
        try {
            Field contentsFrameField = layout.getClass().getDeclaredField(FMLEnvironment.production ? "f_268466_" : "contentsFrame");
            contentsFrameField.setAccessible(true);
            FrameLayout contentsFrame = (FrameLayout) contentsFrameField.get(layout);
            contentsFrame.defaultChildLayoutSetting().align(0.5F, 0.0F).paddingTop(0);
        } catch (ReflectiveOperationException ignored) {
        }
        layout.newHeaderLayoutSettings().align(0.5F, 0.0F);
        layout.newContentLayoutSettings().align(0.5F, 0.0F);
    }

    protected void buildHeader(int entryWidth) {
        int horizontalPadding = 20;
        int classicButtonWidth = 120;
        int titleWidth = Math.min(100, Math.max(80, font.width(getTitle()) + 8));
        int spacerWidth = Math.max(0, width
                - horizontalPadding * 2
                - titleWidth
                - entryWidth
                - classicButtonWidth
                - tabSpacing * 2);

        LinearLayout headerLine = layout.addToHeader(
                new LinearLayout(width, 26, LinearLayout.Orientation.HORIZONTAL),
                layout.newHeaderLayoutSettings().alignVerticallyTop()
        );
        headerLine.defaultChildLayoutSetting()
                .alignVerticallyBottom()
                .paddingTop(6);

        StringWidget titleWidget = new StringWidget(
                titleWidth,
                widgetHeight,
                ConfigEntry.trimText(font, getTitle().copy().withStyle(ChatFormatting.BOLD), titleWidth - 10),
                font
        );
        headerLine.addChild(
                titleWidget,
                headerLine.newChildLayoutSettings()
                        .paddingLeft(horizontalPadding)
                        .paddingRight(tabSpacing)
        );

        allSearchBox = createSearchBox(entryWidth);
        headerLine.addChild(
                allSearchBox,
                headerLine.newChildLayoutSettings().paddingRight(tabSpacing + spacerWidth)
        );
        headerLine.addChild(
                createClassicScreenButton(),
                headerLine.newChildLayoutSettings().paddingRight(horizontalPadding)
        );
    }

    protected EditBox createSearchBox(int entryWidth) {
        EditBox searchBox = new EditBox(this.font, 0, 0, entryWidth, widgetHeight, screenText.searchHint());
        searchBox.setHint(screenText.searchHint());
        searchBox.setValue(allSearchQuery);
        searchBox.setResponder(text -> {
            allSearchQuery = text;
            ESModConfigScreen.this.init(getMinecraft(), width, height);
            ESModConfigScreen.this.setFocused(allSearchBox);
            allSearchBox.setFocused(true);
            allSearchBox.setCursorPosition(allSearchBox.getValue().length());
        });
        return searchBox;
    }

    protected WoodenButtonWidget createClassicScreenButton() {
        return WoodenButtonWidget.simple(120,
                screenText.classicScreen(), button -> {
                    Screen configurationScreen = ConfiguredUtil.getSafe(
                            configModId,
                            parent
                    );
                    if (configurationScreen != null) {
                        Minecraft.getInstance().setScreen(configurationScreen);
                    }
                });
    }

    protected void buildContent(int entryWidth) {
        int contentHeight = layout.getHeight() - layout.getHeaderHeight() - layout.getFooterHeight();
        LinearLayout content = new LinearLayout(width, contentHeight, LinearLayout.Orientation.HORIZONTAL);
        content.addChild(
                buildSidebar(),
                content.newChildLayoutSettings().paddingRight(tabSpacing)
        );
        content.addChild(
                buildConfigPanel(entryWidth, contentHeight),
                content.newChildLayoutSettings().paddingRight(20)
        );
        layout.addToContents(
                content,
                layout.newContentLayoutSettings()
                        .alignHorizontallyLeft()
                        .alignVerticallyTop()
                        .paddingLeft(10)
        );
    }

    protected LinearLayout buildSidebar() {
        List<ConfigCategory> categories = configContext.categories();
        LinearLayout sidebar = new LinearLayout(
                sidebarWidth,
                categories.size() * widgetHeight + Math.max(0, categories.size() - 1) * 2,
                LinearLayout.Orientation.VERTICAL
        );
        for (int i = 0; i < categories.size(); i++) {
            ConfigCategory category = categories.get(i);
            Component title = category.title();
            Component label = category == selectTab
                    ? title.copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    : title;
            WoodenButtonWidget build = WoodenButtonWidget.simple(sidebarWidth, label, button -> {
                ESModConfigScreen.this.selectTab = category;
                ESModConfigScreen.this.init(getMinecraft(), width, height);
            });
            build.setSelect(category == selectTab);
            if (category == ConfigCategory.GENERAL)
                build.setOverrideSprites(ConfigEntry.CLIENT_SPRITES_SEASONAL);
            build.setTooltip(Tooltip.create(category.getDescription()));
            sidebar.addChild(
                    build,
                    sidebar.newChildLayoutSettings()
                            .paddingBottom(i == categories.size() - 1 ? 0 : 2)
            );
        }
        return sidebar;
    }

    protected LinearLayout buildConfigPanel(int entryWidth, int contentHeight) {
        LinearLayout right = new LinearLayout(
                entryWidth * 2 + 16,
                contentHeight,
                LinearLayout.Orientation.VERTICAL
        );
        GridLayout grid = buildEntryGrid(entryWidth);
        configScroll = new ScrollableLayout(this.minecraft, grid, contentHeight);
        right.addChild(configScroll);
        return right;
    }

    protected GridLayout buildEntryGrid(int entryWidth) {
        GridLayout grid = new GridLayout();
        grid.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper helper = grid.createRowHelper(2);
        Tab tab = configContext.tab(selectTab);
        int shownEntries = 0;
        boolean twoColumnWidth = false;
        int startX = sidebarWidth + tabSpacing;
        int currentY = 40;

        for (Map.Entry<Component, List<ConfigEntry>> pair : tab.configShown().entrySet()) {
            List<ConfigEntry> matchedEntries = pair.getValue().stream()
                    .filter(this::matchesSearch)
                    .toList();

            boolean showTitle = tab.configShown().size() > 1 && !matchedEntries.isEmpty();

            if (showTitle) {
                TitleEntry titleEntry = new TitleEntry(pair.getKey().getString());
                LayoutElement title = titleEntry.build(
                        this, startX, currentY, entryWidth * 2 + 20
                );

                if (title != null) {
                    shownEntries++;
                    helper.addChild(title, titleEntry.getColumn());
                }
            }

            for (ConfigEntry entry : matchedEntries) {
                LayoutElement build = entry.build(this, startX, currentY, entryWidth);
                if (build != null) {
                    shownEntries++;
                    twoColumnWidth |= entry.getColumn() > 1;
                    helper.addChild(build, entry.getColumn());
                }
            }
        }

        if (shownEntries == 0) {
            StringWidget stringWidget = new StringWidget(entryWidth * 2 + 20, 30, screenText.noResult()
                    .copy().withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_RED), font);
            stringWidget.alignLeft();
            helper.addChild(stringWidget, 2);
        } else if (!twoColumnWidth) {
            helper.addChild((new StringWidget(entryWidth * 2 + 20, 1, Component.empty()
                    , font)), 2);
        }
        return grid;
    }

    protected void buildFooter(int footerWidth) {
        LinearLayout footer = layout.addToFooter(
                new LinearLayout(footerWidth * 2 + tabSpacing, 32, LinearLayout.Orientation.HORIZONTAL),
                layout.newFooterLayoutSettings().alignVerticallyBottom()
        );
        footer.defaultChildLayoutSetting().paddingBottom(8).paddingTop(4);
        footer.addChild(
                WoodenButtonWidget.simple(footerWidth, CommonComponents.GUI_BACK, (button) -> {
                    ESModConfigScreen.this.saveOnClose = false;
                    this.onClose();
                }),
                footer.newChildLayoutSettings().paddingRight(tabSpacing)
        );
        footer.addChild(WoodenButtonWidget.simple(footerWidth, CommonComponents.GUI_DONE, (button) -> this.onClose()));
    }

    protected void finishLayout() {
        this.layout.arrangeElements();
        if (configScroll != null) configScroll.setScrollAmount(scrollAmountAfterResize);
        scrollAmountAfterResize = 0;
        layout.visitWidgets(this::addRenderableWidget);
        this.addRenderableWidget(this.globalSuggestWidget);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (allSearchBox != null
                && allSearchBox.visible
                && allSearchBox.active
                && allSearchBox.isMouseOver(mouseX, mouseY)) {
            setFocused(allSearchBox);
            allSearchBox.setFocused(true);
            allSearchBox.mouseClicked(mouseX, mouseY, button);
            return true;
        }
        if (globalSuggestWidget.isMouseOver(mouseX, mouseY)) {
            return globalSuggestWidget.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        renderSeasonalBackground(graphics);
        renderBackgroundEffect(graphics);
        renderBackgroundPanels(graphics);
    }

    public static final EnumMap<Season, ResourceLocation> MENU_BACKGROUND = new EnumMap<>(Map.of(
            Season.SPRING, EclipticSeasons.rl("textures/gui/bg_spring.png"),
            Season.SUMMER, EclipticSeasons.rl("textures/gui/bg_summer.png"),
            Season.AUTUMN, EclipticSeasons.rl("textures/gui/bg_autumn.png"),
            Season.WINTER, EclipticSeasons.rl("textures/gui/bg_winter.png")));

    protected void renderSeasonalBackground(GuiGraphics graphics) {
        int textureWidth = 288;
        int textureHeight = 208;
        float scale = Math.max(width / (float) textureWidth, height / (float) textureHeight);
        int drawWidth = Math.round(textureWidth * scale);
        int drawHeight = Math.round(textureHeight * scale);
        int drawX = (width - drawWidth) / 2;
        int drawY = (height - drawHeight) / 2;

        graphics.blit(
                MENU_BACKGROUND.get(season),
                drawX,
                drawY,
                drawWidth,
                drawHeight,
                0,
                0,
                textureWidth,
                textureHeight,
                textureWidth,
                textureHeight
        );
    }

    protected void renderBackgroundEffect(GuiGraphics graphics) {
        backgroundEffects.render(graphics);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    protected void renderBackgroundPanels(GuiGraphics graphics) {
        int l = 0, r = width, t = 36, b = height - 36;
        int s = sidebarWidth + 22, m = (t + b) / 2;

        if (minecraft.level == null) {
            graphics.fillGradient(l, 0, r, t - 4, 0x66AFC9B0, 0x66C8BD9D);       // 顶栏
            graphics.fillGradient(l, b + 4, r, height, 0x66C2AE98, 0x66A9C0CC); // 底栏

            graphics.fillGradient(l, t, s, m, 0x776F8C76, 0x777E806B);           // 左栏上部
            graphics.fillGradient(l, m, s, b, 0x777E806B, 0x776D8390);           // 左栏下部

            graphics.fillGradient(s + 4, t, r - 6, m, 0x448CA891, 0x449E967A);       // 内容区上部
            graphics.fillGradient(s + 4, m, r - 6, b, 0x449E967A, 0x448098A4);       // 内容区下部

            graphics.fill(s - 1, t, s, b, 0x669DB7A5);
        } else {
            graphics.fill(l, 0, r, t - 4, 0x44787874);         // 暖灰顶栏
            graphics.fill(l, b + 4, r, height - 5, 0x446D7378); // 冷灰底栏
            graphics.fill(l, t, s, b, 0x66505050);              // 深灰左栏
            graphics.fill(s + 4, t, r - 6, b, 0x335F6260);          // 浅灰内容区
            graphics.fill(s - 1, t, s, b, 0x558E9491);          // 灰白分隔线
        }

        int q = width / 4;
        graphics.fill(0, 0, q, 2, 0xCC78B86B); // 春
        graphics.fill(q, 0, q * 2, 2, 0xCCD5B84A); // 夏
        graphics.fill(q * 2, 0, q * 3, 2, 0xCCD8793D); // 秋
        graphics.fill(q * 3, 0, width, 2, 0xCC79AFC4); // 冬
    }

    @Override
    public void onClose() {
        if (!saveOnClose) {
            configSession.restore();
            minecraft.setScreen(parent);
            return;
        }

        boolean inGame = Minecraft.getInstance().level != null;
        ConfigSaveResult result = configSession.save(configContext, inGame);
        if (result.worldRestartRequired() || result.gameRestartRequired()) {
            RestartTypeUtil.RestartType restartType = inGame && !result.gameRestartRequired()
                    ? RestartTypeUtil.RestartType.WORLD
                    : RestartTypeUtil.RestartType.GAME;
            switch (restartType) {
                case GAME -> {
                    minecraft.setScreen(new TooltipConfirmScreen(b -> {
                        if (b) {
                            minecraft.stop();
                        } else {
                            super.onClose();
                        }
                    }, TooltipConfirmScreen.GAME_RESTART_TITLE, TooltipConfirmScreen.GAME_RESTART_MESSAGE, TooltipConfirmScreen.GAME_RESTART_YES, TooltipConfirmScreen.RESTART_NO));
                }
                case WORLD -> {
                    if (minecraft.level != null) {
                        minecraft.setScreen(new TooltipConfirmScreen(b -> {
                            if (b) {
                                TooltipConfirmScreen.onDisconnect();
                            } else {
                                super.onClose();
                            }
                        }, TooltipConfirmScreen.SERVER_RESTART_TITLE, TooltipConfirmScreen.SERVER_RESTART_MESSAGE, minecraft.isLocalServer() ? TooltipConfirmScreen.RETURN_TO_MENU : CommonComponents.GUI_TO_TITLE, TooltipConfirmScreen.RESTART_NO));
                    }
                }
            }
        } else minecraft.setScreen(parent);
    }


    public Font getFont() {
        return font;
    }

    protected static final Music MENU = new Music(SoundEvents.MUSIC_BIOME_MEADOW, 20, 600, true);

    @Override
    public @Nullable Music getBackgroundMusic() {
        return MENU;
    }
}
