package com.teamtea.eclipticseasons.client.gui.screen;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.gui.screen.effect.SeasonalBackgroundEffects;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.TitleEntry;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.builtin.ESConfigScreenDefinition;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigScreenText;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigSaveResult;
import com.teamtea.eclipticseasons.client.gui.screen.config.session.ConfigScreenSession;
import com.teamtea.eclipticseasons.client.gui.screen.config.tab.Tab;
import com.teamtea.eclipticseasons.client.gui.screen.widget.ScrollUtil;
import com.teamtea.eclipticseasons.client.gui.screen.widget.SuggestWidget;
import com.teamtea.eclipticseasons.client.gui.screen.widget.WoodenButtonWidget;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.solar.extra.FixedSolarDataManagerLocal;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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

    public ConfigScreenContext getConfigContext() {
        return configContext;
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
        this.season = ClientCon.nowSeason.isValid() ? ClientCon.nowSeason :
                // Season.collectValidValues()[RandomSource.create(System.currentTimeMillis()).nextInt(4)]
                new FixedSolarDataManagerLocal(null).getSolarTerm().getSeason();
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
    public void resize(int width, int height) {
        scrollAmountAfterResize = ScrollUtil.scrollAmount(configScroll);
        super.resize(width, height);
    }

    protected void prepareLayout() {
        backgroundEffects.resize(width, height);
        List<ConfigCategory> categories = configContext.categories();
        if (selectTab == null || !configContext.containsCategory(selectTab)) {
            selectTab = categories.contains(ConfigCategory.GENERAL)
                    ? ConfigCategory.GENERAL
                    : categories.stream().findFirst().orElseThrow();
        }

        this.globalSuggestWidget = new SuggestWidget(0, 0, 0, this.font, (_) -> {
        });

        layout = new HeaderAndFooterLayout(this, headerHeight, footerHeight);
    }

    protected void buildHeader(int entryWidth) {
        int horizontalPadding = 20;
        int classicButtonWidth = 120;
        int titleWidth = Math.max(80, font.width(getTitle()) + 8);

        int spacerWidth = width
                - horizontalPadding * 2
                - titleWidth
                - entryWidth
                - classicButtonWidth
                - tabSpacing * 3;

        LinearLayout headerLine = layout.addToHeader(LinearLayout.horizontal().spacing(tabSpacing), LayoutSettings::alignVerticallyTop);
        headerLine.defaultCellSetting()
                .alignVerticallyBottom()
                .alignHorizontallyLeft()
                .paddingTop(6);

        StringWidget titleWidget = new StringWidget(titleWidth, widgetHeight, getTitle().copy().withStyle(ChatFormatting.BOLD), font);
        headerLine.addChild(titleWidget, headerLine.newCellSettings().paddingLeft(horizontalPadding));

        allSearchBox = createSearchBox(entryWidth);
        headerLine.addChild(allSearchBox);

        headerLine.addChild(SpacerElement.width(Math.max(0, spacerWidth)));

        headerLine.addChild(createClassicScreenButton(), headerLine.newCellSettings().paddingRight(horizontalPadding));
    }

    protected EditBox createSearchBox(int entryWidth) {
        EditBox searchBox = new EditBox(this.font, entryWidth, widgetHeight, screenText.searchHint());
        searchBox.setHint(screenText.searchHint());
        searchBox.setValue(allSearchQuery);
        searchBox.setResponder(text -> {
            allSearchQuery = text;
            ESModConfigScreen.this.init(width, height);
            ESModConfigScreen.this.setFocused(allSearchBox);
            allSearchBox.setFocused(true);
            allSearchBox.setCursorPosition(allSearchBox.getValue().length());
        });
        return searchBox;
    }

    protected WoodenButtonWidget createClassicScreenButton() {
        return WoodenButtonWidget.simple(120,
                screenText.classicScreen(), button -> {
                    if (mod == null) {
                        mod = ModList.get().getModContainerById(configModId).orElse(null);
                    }
                    if (mod != null) {
                        ConfigurationScreen configurationScreen = new ConfigurationScreen(mod, ESModConfigScreen.this.parent);
                        Minecraft.getInstance().setScreenAndShow(configurationScreen);
                    }
                });
    }

    protected void buildContent(int entryWidth) {
        LinearLayout content = LinearLayout.horizontal().spacing(tabSpacing);
        content.addChild(buildSidebar());
        content.addChild(buildConfigPanel(entryWidth));
        layout.addToContents(content);
    }

    protected LinearLayout buildSidebar() {
        LinearLayout sidebar = LinearLayout.vertical().spacing(2);
        for (ConfigCategory category : configContext.categories()) {
            Component title = category.title();
            Component label = category == selectTab
                    ? title.copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    : title;
            WoodenButtonWidget build = WoodenButtonWidget.simple(sidebarWidth, label, button -> {
                ESModConfigScreen.this.selectTab = category;
                ESModConfigScreen.this.init(width, height);
            });
            build.setSelect(category == selectTab);
            if (category == ConfigCategory.GENERAL)
                build.setOverrideSprites(ConfigEntry.CLIENT_SPRITES_SEASONAL);
            build.setTooltip(Tooltip.create(category.getDescription()));
            sidebar.addChild(build);
        }
        return sidebar;
    }

    protected LinearLayout buildConfigPanel(int entryWidth) {
        LinearLayout right = LinearLayout.vertical().spacing(tabSpacing);
        GridLayout grid = buildEntryGrid(entryWidth);
        configScroll = ScrollUtil.setScrollbarSettings(
                new ScrollableLayout(this.minecraft, grid, this.layout.getContentHeight()));
        right.addChild(configScroll);
        return right;
    }

    protected GridLayout buildEntryGrid(int entryWidth) {
        GridLayout grid = new GridLayout();
        grid.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper helper = grid.createRowHelper(2);
        Tab tab = configContext.tab(selectTab);
        int shownEntries = 0;
        int startX = sidebarWidth + tabSpacing;
        int currentY = 40;

        for (Map.Entry<Component, List<ConfigEntry>> pair : tab.configShown().entrySet()) {
            if (pair.getValue().isEmpty()) continue;
            if (tab.configShown().size() > 1) {
                TitleEntry titleEntry = new TitleEntry(pair.getKey().getString());
                shownEntries++;
                helper.addChild(titleEntry.build(this, startX, currentY, entryWidth * 2 + 20), titleEntry.getColumn());
            }
            for (ConfigEntry entry : pair.getValue()) {
                if (!matchesSearch(entry)) continue;
                LayoutElement build = entry.build(this, startX, currentY, entryWidth);
                if (build != null) {
                    shownEntries++;
                    helper.addChild(build, entry.getColumn());
                }
            }
        }

        if (shownEntries == 0) {
            helper.addChild((new StringWidget(entryWidth * 2 + 20, 30, screenText.noResult()
                    .copy().withStyle(ChatFormatting.ITALIC)
                    .withColor(TextColor.DARK_RED), font)), 2);
        }
        return grid;
    }

    protected void buildFooter(int footerWidth) {
        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal(), LayoutSettings::alignVerticallyBottom).spacing(tabSpacing);
        footer.defaultCellSetting().paddingBottom(8).paddingTop(4);
        footer.addChild(WoodenButtonWidget.simple(footerWidth, CommonComponents.GUI_BACK, (button) -> {
            ESModConfigScreen.this.saveOnClose = false;
            this.onClose();
        }));
        footer.addChild(WoodenButtonWidget.simple(footerWidth, CommonComponents.GUI_DONE, (button) -> this.onClose()));
    }

    protected void finishLayout() {
        layout.visitWidgets(this::addRenderableWidget);
        this.addRenderableWidget(this.globalSuggestWidget);
        this.layout.arrangeElements();
        ScrollUtil.setScrollAmount(configScroll, scrollAmountAfterResize);
        scrollAmountAfterResize = 0;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (globalSuggestWidget.isMouseOver(event.x(), event.y())) {
            return globalSuggestWidget.mouseClicked(event, doubleClick);
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        renderMenuBackground(graphics);
        renderBackgroundEffect(graphics, a);
        renderBackgroundPanels(graphics);
    }

    public static final EnumMap<Season, Identifier> MENU_BACKGROUND = new EnumMap<>(Map.of(
            Season.SPRING, EclipticSeasons.rl("textures/gui/bg_spring.png"),
            Season.SUMMER, EclipticSeasons.rl("textures/gui/bg_summer.png"),
            Season.AUTUMN, EclipticSeasons.rl("textures/gui/bg_autumn.png"),
            Season.WINTER, EclipticSeasons.rl("textures/gui/bg_winter.png")));

    protected void renderMenuBackground(GuiGraphicsExtractor graphics) {
        // int textureWidth = 400;
        // int textureHeight = 246;
        int textureWidth = 288;
        int textureHeight = 208;

        float scale = Math.max(width / (float) textureWidth, height / (float) textureHeight);

        int drawWidth = Math.round(textureWidth * scale);
        int drawHeight = Math.round(textureHeight * scale);

        int drawX = (width - drawWidth) / 2;
        int drawY = (height - drawHeight) / 2;

        graphics.blit(RenderPipelines.GUI_TEXTURED, MENU_BACKGROUND.get(season),
                drawX, drawY,
                0.0F, 0.0F,
                drawWidth, drawHeight,
                textureWidth, textureHeight,
                textureWidth, textureHeight
        );
    }

    protected void renderBackgroundEffect(GuiGraphicsExtractor graphics, float a) {
        backgroundEffects.render(graphics);
    }

    protected void renderBackgroundPanels(GuiGraphicsExtractor graphics) {
        int l = 0, r = width, t = 36, b = height - 36;
        int s = sidebarWidth + 22, m = (t + b) / 2;

        if (!isInGameUi()) {
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
            minecraft.setScreenAndShow(parent);
            return;
        }

        boolean inGame = Minecraft.getInstance().level != null;
        ConfigSaveResult result = configSession.save(configContext, inGame);
        if (result.worldRestartRequired() || result.gameRestartRequired()) {
            var restartType = inGame && !result.gameRestartRequired()
                    ? ModConfigSpec.RestartType.WORLD
                    : ModConfigSpec.RestartType.GAME;
            switch (restartType) {
                case GAME -> {
                    minecraft.setScreenAndShow(new TooltipConfirmScreen(b -> {
                        if (b) {
                            minecraft.stop();
                        } else {
                            super.onClose();
                        }
                    }, ConfigurationScreen.GAME_RESTART_TITLE, ConfigurationScreen.GAME_RESTART_MESSAGE, ConfigurationScreen.GAME_RESTART_YES, ConfigurationScreen.RESTART_NO));
                }
                case WORLD -> {
                    if (minecraft.level != null) {
                        minecraft.setScreenAndShow(new TooltipConfirmScreen(b -> {
                            if (b) {
                                TooltipConfirmScreen.onDisconnect();
                            } else {
                                super.onClose();
                            }
                        }, ConfigurationScreen.SERVER_RESTART_TITLE, ConfigurationScreen.SERVER_RESTART_MESSAGE, minecraft.isLocalServer() ? ConfigurationScreen.RETURN_TO_MENU : CommonComponents.GUI_DISCONNECT, ConfigurationScreen.RESTART_NO));
                    }
                }
            }
        } else minecraft.setScreenAndShow(parent);
    }

    @Override
    public @NonNull Font getFont() {
        return font;
    }

    private static final Music MENU = new Music(SoundEvents.MUSIC_BIOME_MEADOW, 20, 600, true);

    @Override
    public @Nullable Music getBackgroundMusic() {
        return MENU;
    }
}
