package com.teamtea.eclipticseasons.client.gui.screen;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.TitleEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.callback.SimpleBoolEntry;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigRegistry;
import com.teamtea.eclipticseasons.client.gui.screen.tab.Tab;
import com.teamtea.eclipticseasons.client.gui.screen.widget.SuggestWidget;
import com.teamtea.eclipticseasons.config.*;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import com.teamtea.eclipticseasons.config.sync.ESConfigToServerPayload;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.permissions.Permissions;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.config.ConfigTracker;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class ESModConfigScreen extends Screen {
    private final Screen parent;
    private HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this, 61, 33);
    private static final Component TITLE = Component.translatable("options.title");
    private ModContainer mod;
    @Getter
    private SuggestWidget globalSuggestWidget;
    public boolean saveOnClose = true;
    public final Map<Object, ConfigCategory> configTabs = new IdentityHashMap<>();

    public final Map<ConfigCategory, Tab> tabs = new LinkedHashMap<>();
    public final HashSet<Object> configRegistered = new HashSet<>();

    private static final Component ALL_SECTION = Component.translatable("eclipticseasons.options.all");

    private String allSearchQuery = "";
    private EditBox allSearchBox;

    public void addToTab(ConfigCategory category, Component subTabName, ConfigEntry entry) {
        if (entry == null) return;
        Tab tab = tabs.get(category);
        if (tab == null) {
            EclipticSeasons.LOGGER.warn("Unknown configuration category: {}", category);
            return;
        }
        tab.configShown().computeIfAbsent(subTabName, k -> new ArrayList<>()).add(entry);
    }

    public void put(ConfigCategory category, Component subTabName, ModConfigSpec.ConfigValue<?>... values) {
        Tab tab = tabs.get(category);
        if (tab == null) {
            EclipticSeasons.LOGGER.warn("Unknown configuration category: {}", category);
            return;
        }
        for (ModConfigSpec.ConfigValue<?> value : values) {
            if (value == null) continue;
            SpecEntry<?> parse = SpecEntry.parse(value);
            if (parse == null) continue;

            configTabs.put(value, category);
            tab.configShown().computeIfAbsent(subTabName, k -> new ArrayList<>()).add(parse);
        }
    }

    /**
     * Marks a config value as classified without creating a new SpecEntry.
     * Used for CallbackEntry items in the GENERAL recommended section so that
     * automatic scanning does not report them as unclassified.
     */
    public void markClassified(ConfigCategory category, Object value) {
        if (value != null) {
            configTabs.put(value, category);
        }
    }

    @SuppressWarnings({"raw_use"})
    public ESModConfigScreen(Screen parent) {
        super(Component.literal("Ecliptic Seasons"));
        initConfigCache();
        this.parent = parent;

        for (ConfigCategory category : ConfigCategory.values()) {
            tabs.put(category, new Tab(category.title(), new LinkedHashMap<>()));
        }

        ConfigRegistry.register(this);

        for (UnmodifiableConfig.Entry entry :
                Stream.of(CommonConfig.COMMON_CONFIG, ClientConfig.CLIENT_CONFIG, StartConfig.START_CONFIG)
                        .map(ModConfigSpec::getValues)
                        .map(UnmodifiableConfig::entrySet)
                        .flatMap(Collection::stream)
                        .toList()) {
            Object value = entry.getValue();
            if (value instanceof com.electronwill.nightconfig.core.Config config) {
                collectConfigValues(config);
            } else if (value instanceof ModConfigSpec.ConfigValue<?> cv) {
                buildConfigValue(cv);
            }
        }

        // Sorts
        for (ConfigCategory category : new ArrayList<>(tabs.keySet())) {
            Tab tab = tabs.get(category);
            for (Component subT : new ArrayList<>(tab.configShown().keySet())) {
                List<ConfigEntry> entriesSelect = new ArrayList<>(tab.configShown().get(subT));
                entriesSelect.sort(Comparator.comparing(ConfigEntry::getPosition));
                tab.configShown().put(subT, entriesSelect);
            }
        }

        traverseConfig(EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig(), "");
    }

    protected void collectConfigValues(com.electronwill.nightconfig.core.Config config) {
        for (Config.Entry entry : config.entrySet()) {
            Object value = entry.getValue();

            if (value instanceof ModConfigSpec.ConfigValue<?> cv) {
                buildConfigValue(cv);
            } else if (value instanceof com.electronwill.nightconfig.core.Config childConfig) {
                collectConfigValues(childConfig);
            }
        }
    }

    protected void buildConfigValue(ModConfigSpec.ConfigValue<?> cv) {
        ConfigCategory category = classify(cv);
        if (category == null) {
            // EclipticSeasons.LOGGER.warn("Unclassified configuration: {}", cv.getPath());
        }

        // ALL tab shows every parseable value regardless of classification.
        SpecEntry<?> parse = SpecEntry.parse(cv);
        if (parse != null) {
            addToTab(ConfigCategory.ALL, ALL_SECTION, parse);
        }
    }

    protected void traverseConfig(Config config, String path) {
        for (Config.Entry entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (value instanceof Config nested) {
                traverseConfig(nested, fullPath);
            } else if (value instanceof Boolean bool) {
                addToTab(ConfigCategory.ADVANCED, mixinModule(fullPath),
                        new SimpleBoolEntry(key, entry::getValue, b -> config.set(key, b)));
            }
        }
    }

    private static Component mixinModule(String fullPath) {
        String[] parts = fullPath.split("\\.");
        String[] module = Arrays.copyOf(parts, Math.max(0, parts.length - 1));

        if (module.length == 0) {
            return Component.literal("Mixin");
        }
        if (module.length == 1) {
            return Component.literal("Mixin - " + capitalize(module[0]));
        }
        if ("compat".equals(module[0])) {
            return Component.literal("Mixin - Compat: " + compatModuleName(module[1]));
        }
        return Component.literal("Mixin - " + capitalize(module[0]) + ": " + capitalize(module[1]));
    }

    private static String compatModuleName(String moduleId) {
        return switch (moduleId) {
            case "neoforge" -> "NeoForge";
            case "sodium" -> "Sodium";
            case "iris" -> "Iris";
            case "distanthorizons" -> "Distant Horizons";
            case "fabric_renderer_indigo" -> "Fabric Renderer Indigo";
            case "voxy" -> "Voxy";
            default -> moduleId;
        };
    }

    private static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return "Mixin";
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    private boolean matchesSearch(ConfigEntry entry) {
        if (allSearchQuery == null || allSearchQuery.isBlank()) {
            return true;
        }
        return entry.getSearchText().toLowerCase(Locale.ROOT)
                .contains(allSearchQuery.toLowerCase(Locale.ROOT).trim());
    }

    protected ConfigCategory classify(Object obj) {
        return configTabs.get(obj);
    }

    public ESModConfigScreen(final ModContainer mod, final Screen parent) {
        this(parent);
        this.mod = mod;
    }

    protected ConfigCategory selectTab;

    private static final int TAB_SPACING = 8;
    private static final int SIDEBAR_WIDTH = 70;

    @Override
    protected void init() {
        selectTab = selectTab == null ? ConfigCategory.GENERAL : selectTab;

        this.globalSuggestWidget = new SuggestWidget(0, 0, 0, this.font, (_) -> {
        });

        layout = new HeaderAndFooterLayout(this, 30, 33);
        layout.setHeaderHeight(40);
        layout.setFooterHeight(40);

        int contentWidth = Math.max(1, this.width - SIDEBAR_WIDTH - TAB_SPACING);
        int entryWidth = contentWidth / 2 - 36;
        int footerWidth = this.width / 2 - 36;
        int startX = SIDEBAR_WIDTH + TAB_SPACING;
        int currentY = 40;

        LinearLayout headerLine = this.layout.addToHeader(LinearLayout.horizontal().spacing(TAB_SPACING), LayoutSettings::alignVerticallyTop);
        headerLine.defaultCellSetting().alignVerticallyBottom().paddingTop(6);
        StringWidget titleWidget = new StringWidget(this.width - 130 - TAB_SPACING - 40, 20, TITLE.copy().withStyle(ChatFormatting.BOLD), this.font);
        // titleWidget.setAlign(0f);
        headerLine.addChild(titleWidget, headerLine.newCellSettings().paddingLeft(20));
        headerLine.addChild(Button.builder(Component.translatable("eclipticseasons.options.configure_in_classic_screen"), (button) -> {
            if (mod == null) {
                mod = ModList.get().getModContainerById(EclipticSeasonsApi.MODID).orElse(null);
            }
            if (mod != null) {
                ConfigurationScreen configurationScreen = new ConfigurationScreen(mod, ESModConfigScreen.this.parent);
                Minecraft.getInstance().setScreenAndShow(configurationScreen);
            }
        }).width(120).size(120, 20).build());

        LinearLayout content = LinearLayout.horizontal().spacing(TAB_SPACING);

        LinearLayout sidebar = LinearLayout.vertical().spacing(2);
        for (ConfigCategory category : List.of(
                ConfigCategory.GENERAL,
                ConfigCategory.ENVIRONMENT,
                ConfigCategory.GAMEPLAY,
                ConfigCategory.VISUAL,
                ConfigCategory.ADVANCED,
                ConfigCategory.ALL)) {
            Component title = category.title();
            Component label = category == selectTab
                    ? title.copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    : title;
            Button build = Button.builder(label, button -> {
                ESModConfigScreen.this.selectTab = category;
                ESModConfigScreen.this.init(width, height);
            }).width(SIDEBAR_WIDTH).build();
            build.setTooltip(Tooltip.create(category.getDescription()));
            sidebar.addChild(build);
        }
        content.addChild(sidebar);

        LinearLayout right = LinearLayout.vertical().spacing(TAB_SPACING);

        int searchHeight = 0;
        if (selectTab == ConfigCategory.ALL) {
            EditBox searchBox = new EditBox(this.font, entryWidth, 20, Component.translatable("eclipticseasons.options.search"));
            searchBox.setHint(Component.translatable("eclipticseasons.options.search"));
            searchBox.setValue(allSearchQuery);
            searchBox.setResponder(text -> {
                allSearchQuery = text;
                ESModConfigScreen.this.init(width, height);
                if (allSearchBox != null) {
                    ESModConfigScreen.this.setFocused(allSearchBox);
                    allSearchBox.setFocused(true);
                    allSearchBox.setCursorPosition(allSearchBox.getValue().length());
                }
            });
            allSearchBox = searchBox;
            // GridLayout gridLayout = new GridLayout();
            // gridLayout.defaultCellSetting().paddingLeft(20).alignHorizontallyCenter();
            // GridLayout.RowHelper helper = gridLayout.createRowHelper(2);
            // // helper.addChild(new StringWidget(Component.translatable("xx"), this.font),1);
            // helper.addChild(searchBox,2);
            right.addChild(searchBox,
                    right.newCellSettings()
                            .paddingLeft(20)
                            .paddingRight(20));
            searchHeight = 20 + TAB_SPACING;
        } else {
            allSearchBox = null;
        }

        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);

        Tab tab = tabs.get(selectTab);

        int entryChoseSize = 0;
        for (Map.Entry<Component, List<ConfigEntry>> pair : tab.configShown().entrySet()) {
            if (pair.getValue().isEmpty()) continue;
            if (tab.configShown().size() > 1) {
                TitleEntry titleEntry = new TitleEntry(pair.getKey().getString());
                entryChoseSize++;
                helper.addChild(titleEntry.build(this, startX, currentY, entryWidth), titleEntry.getColumn());
            }
            for (ConfigEntry entry : pair.getValue()) {
                if (selectTab == ConfigCategory.ALL && !matchesSearch(entry)) {
                    continue;
                }
                LayoutElement build = entry.build(this, startX, currentY, entryWidth);
                int column = entry.getColumn();
                if (build != null) {
                    entryChoseSize++;
                    helper.addChild(build, column);
                }
            }
        }
        if (entryChoseSize == 0) {
            helper.addChild((new StringWidget(entryWidth * 2 + 20, 30, Component.translatable("eclipticseasons.options.search.no_result")
                    .withStyle(ChatFormatting.ITALIC)
                    .withColor(TextColor.DARK_RED), font)), 2);
        }

        ScrollableLayout scrollableLayout = new ScrollableLayout(this.minecraft, gridLayout, this.layout.getContentHeight() - searchHeight);
        right.addChild(scrollableLayout);
        content.addChild(right);

        layout.addToContents(content);

        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal(), LayoutSettings::alignVerticallyBottom).spacing(TAB_SPACING);
        footer.defaultCellSetting().paddingBottom(8).paddingTop(4);
        footer.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            ESModConfigScreen.this.saveOnClose = false;
            this.onClose();
        }).width(footerWidth).build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(footerWidth).build());

        layout.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(this.globalSuggestWidget);

        this.layout.arrangeElements();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (globalSuggestWidget.isMouseOver(event.x(), event.y())) {
            return globalSuggestWidget.mouseClicked(event, doubleClick);
        }
        return super.mouseClicked(event, doubleClick);
    }

    protected void repositionElements() {
        // this.rebuildWidgets();
        super.repositionElements();
        // this.layout.arrangeElements();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    protected Map<String, byte[]> configCache = new HashMap<>();

    public void initConfigCache() {
        for (ModConfig modConfig : ModConfigs.getModConfigs(EclipticSeasonsApi.MODID)) {
            try {
                configCache.put(modConfig.getFileName(), Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName())));
            } catch (IOException e) {
                EclipticSeasons.logger(e);
            }
        }
    }

    public void backupConfigCache() {
        for (Map.Entry<String, byte[]> entry : configCache.entrySet()) {
            ModConfig modConfig = ModConfigs.getFileMap().get(entry.getKey());
            if (modConfig != null) {
                ConfigTracker.INSTANCE.acceptSyncedConfig(modConfig, entry.getValue());
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        renderDividers(graphics);
    }

    private void renderDividers(GuiGraphicsExtractor graphics) {
        int l = 0, r = width, t = 36, b = height - 36;
        int s = SIDEBAR_WIDTH + 22, m = (t + b) / 2;

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
        super.onClose();
        if (!saveOnClose) {
            backupConfigCache();
            Objects.requireNonNull(this.minecraft).setScreenAndShow(this.parent);
            return;
        }

        boolean needRestart = false;
        boolean needGameRestart = false;
        boolean isChanged = false;
        boolean inGame = Minecraft.getInstance().level != null;

        Set<SyncType> syncTypes = new HashSet<>();
        for (Map.Entry<ConfigCategory, Tab> componentTabEntry : tabs.entrySet()) {
            for (Map.Entry<Component, List<ConfigEntry>> componentListEntry : componentTabEntry.getValue().configShown().entrySet()) {
                for (ConfigEntry configEntry : componentListEntry.getValue()) {
                    boolean valueChange = configEntry.isValueChanged();
                    isChanged |= valueChange;
                    needRestart |= valueChange && configEntry.shouldRestart(inGame);
                    needGameRestart |= valueChange && configEntry.shouldRestart(false);
                    if (valueChange && configEntry instanceof SpecEntry<?> specEntry) {
                        specEntry.getSpec().clearCache();
                    }
                    if (valueChange) {
                        syncTypes.add(configEntry.getSyncType());
                    }
                    // if (needRestart) break;
                }
            }
        }

        if (isChanged) {
            if (syncTypes.contains(SyncType.COMMON)) CommonConfig.COMMON_CONFIG.save();
            List<ModConfig> modConfigNotBackup = ModConfigs.getModConfigs(EclipticSeasonsApi.MODID).stream().filter(m -> m.getType() != ModConfig.Type.CLIENT
                    && syncTypes.contains(SyncType.of(m.getType()))).toList();
            for (ModConfig modConfig : modConfigNotBackup) {
                ESConfigSync.INSTANCE.notBackup(modConfig);
            }
            if (syncTypes.contains(SyncType.CLIENT)) ClientConfig.CLIENT_CONFIG.save();
            if (syncTypes.contains(SyncType.STARTUP)) StartConfig.START_CONFIG.save();
            if (syncTypes.contains(SyncType.MIXINS)) EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig().save();

            if (Minecraft.getInstance().getConnection() != null
                    && !Minecraft.getInstance().isLocalServer()
                    && Minecraft.getInstance().player.permissions().hasPermission(Permissions.COMMANDS_ADMIN)
            ) {
                try {
                    for (ModConfig modConfig : modConfigNotBackup) {
                        byte[] bytes = Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName()));
                        ClientPacketDistributor.sendToServer(new ESConfigToServerPayload(modConfig.getFileName(), needRestart, SyncType.of(modConfig.getType()), bytes));
                    }

                    if (syncTypes.contains(SyncType.MIXINS)) {
                        byte[] bytes = Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(SyncType.MIXINS.configName(EclipticSeasonsApi.MODID)));
                        ClientPacketDistributor.sendToServer(new ESConfigToServerPayload(SyncType.MIXINS.configName(EclipticSeasonsApi.MODID), true, SyncType.MIXINS, bytes));
                    }
                } catch (IOException e) {
                    EclipticSeasons.logger(e);
                }
            }
        }

        if (needRestart || needGameRestart) {
            var restartType = inGame && !needGameRestart ? ModConfigSpec.RestartType.WORLD : ModConfigSpec.RestartType.GAME;
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
        } else Objects.requireNonNull(this.minecraft).setScreenAndShow(this.parent);
    }

    @Override
    public @NonNull Font getFont() {
        return font;
    }

}
