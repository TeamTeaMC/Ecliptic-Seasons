package com.teamtea.eclipticseasons.client.gui.screen;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.mojang.realmsclient.RealmsMainScreen;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.CallbackEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.SimpleBoolEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.TitleEntry;
import com.teamtea.eclipticseasons.client.gui.screen.tab.Tab;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.compat.configured.ConfiguredUtil;
import com.teamtea.eclipticseasons.config.ClientConfig;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import com.teamtea.eclipticseasons.config.sync.ESConfigToServerPayload;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import com.teamtea.eclipticseasons.config.util.RestartTypeUtil;
import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;

import java.io.IOException;
import java.lang.reflect.Field;
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
    public final Map<Object, Component> configTabs = new IdentityHashMap<>();

    public final Map<Component, Tab> tabs = new LinkedHashMap<>();
    public final HashSet<Object> configRegistered = new HashSet<>();

    public static final Component HOT = Component.translatable("eclipticseasons.options.hot");
    public static final Component SEASON = Component.translatable("eclipticseasons.options.season");
    public static final Component SNOW = Component.translatable("eclipticseasons.options.snow_related");
    public static final Component CROP = Component.translatable("eclipticseasons.options.crop");
    public static final Component ANIMAL = Component.translatable("eclipticseasons.options.animal");
    public static final Component WEATHER = Component.translatable("eclipticseasons.options.weather");
    public static final Component RENDER = Component.translatable("eclipticseasons.options.renderer");
    public static final Component DEBUG = Component.translatable("eclipticseasons.options.debug");
    public static final Component COMPAT = Component.translatable("eclipticseasons.options.compat");
    public static final Component OTHERS = Component.translatable("eclipticseasons.options.others");
    public static final Component MIXINS = Component.translatable("eclipticseasons.options.mixins");

    public void addToTab(Component tabName, Component subTabName, ConfigEntry entry) {
        Tab tab = tabs.get(tabName);
        tab.configShown().computeIfAbsent(subTabName, k -> new ArrayList<>()).add(entry);
    }

    public void addToHotTab(ConfigEntry entry) {
        addToTab(HOT, HOT, entry);
    }

    public ESModConfigScreen() {
        this(null);
    }

    @SuppressWarnings({"raw_use"})
    public ESModConfigScreen(Screen parent) {
        super(Component.literal("Ecliptic Seasons"));
        initConfigCache();
        this.parent = parent;

        tabs.put(HOT, new Tab(HOT, new LinkedHashMap<>()));
        tabs.put(SEASON, new Tab(SEASON, new LinkedHashMap<>()));
        tabs.put(SNOW, new Tab(SNOW, new LinkedHashMap<>()));
        tabs.put(CROP, new Tab(CROP, new LinkedHashMap<>()));
        tabs.put(ANIMAL, new Tab(ANIMAL, new LinkedHashMap<>()));
        tabs.put(WEATHER, new Tab(WEATHER, new LinkedHashMap<>()));
        tabs.put(RENDER, new Tab(RENDER, new LinkedHashMap<>()));
        tabs.put(DEBUG, new Tab(DEBUG, new LinkedHashMap<>()));
        tabs.put(COMPAT, new Tab(COMPAT, new LinkedHashMap<>()));
        tabs.put(OTHERS, new Tab(OTHERS, new LinkedHashMap<>()));
        tabs.put(MIXINS, new Tab(MIXINS, new LinkedHashMap<>()));

        registerBuiltinConfigTabs();

        // entries.add(new TitleEntry("Hot Selections"));
        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.RenderedSnow",
                "eclipticseasons.configuration.RenderedSnow.tooltip",
                () -> CommonConfig.Snow.snowyWinter.get(),
                (bt, b) -> {
                    CommonConfig.Snow.snowyWinter.set(b);
                }));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.BlockSnow",
                "eclipticseasons.configuration.BlockSnow.tooltip",
                CommonConfig::isVanillaSnowAndIce,
                (bt, b) -> {
                    CommonConfig.Temperature.snowDown.set(b);
                    CommonConfig.Temperature.iceMelt.set(b);
                    CommonConfig.setVanillaSnowAndIce(
                            CommonConfig.Temperature.snowDown.get()
                                    && CommonConfig.Temperature.iceMelt.get());
                }));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.DebugInfo",
                "eclipticseasons.configuration.DebugInfo.tooltip",
                () -> ClientConfig.Debug.debugInfo.get(),
                (bt, b) -> {
                    ClientConfig.Debug.debugInfo.set(b);
                }).setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.NaturalSound",
                "eclipticseasons.configuration.NaturalSound.tooltip",
                () -> ClientConfig.Sound.sound.get(),
                (bt, b) -> {
                    ClientConfig.Sound.sound.set(b);
                    ClientConfig.Sound.sound.clearCache();
                }).setRestartType(RestartTypeUtil.RestartType.WORLD)
                .setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.ExtraSnowLayer",
                "eclipticseasons.configuration.ExtraSnowLayer.tooltip",
                () -> ClientConfig.Renderer.extraSnowLayer.get(),
                (bt, b) -> {
                    ClientConfig.Renderer.extraSnowLayer.set(b);
                })
                .setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.ExtraSnowDefinitions",
                "eclipticseasons.configuration.ExtraSnowDefinitions.tooltip",
                () -> CommonConfig.Resource.extraSnow.get(),
                (bt, b) -> {
                    CommonConfig.Resource.extraSnow.set(b);
                    CommonConfig.Resource.extraSnow.clearCache();
                }).setRestartType(RestartTypeUtil.RestartType.GAME));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.FrozenWater",
                "eclipticseasons.configuration.FrozenWater.tooltip",
                () -> ClientConfig.Debug.frozenWater.get(),
                (bt, b) -> {
                    ClientConfig.Debug.frozenWater.set(b);
                }).setSyncType(SyncType.CLIENT));


        for (UnmodifiableConfig.Entry entry :
                Stream.of(CommonConfig.COMMON_CONFIG, ClientConfig.CLIENT_CONFIG)
                        .map(ForgeConfigSpec::getValues)
                        .map(UnmodifiableConfig::entrySet)
                        .flatMap(Collection::stream)
                        .toList()) {
            if (entry.getValue() instanceof com.electronwill.nightconfig.core.AbstractConfig simpleConfig) {
                // List<ConfigEntry> entriesSelect = new ArrayList<>();
                for (Config.Entry config : simpleConfig.entrySet()) {
                    if (config.getValue() instanceof ForgeConfigSpec.ConfigValue<?> cv
                    ) {
                        Component tabKey = classify(cv);
                        if (tabKey == null) continue;
                        ConfigEntry.SpecEntry<?> parse = ConfigEntry.SpecEntry.parse(cv);
                        if (parse == null) continue;
                        addToTab(tabKey, tabKey == OTHERS ? Component.translatable("eclipticseasons.configuration." + entry.getKey()) : tabKey, parse);
                    }
                }
            }

        }

        // Sorts
        for (Component component : new ArrayList<>(tabs.keySet())) {
            Tab tab = tabs.get(component);
            for (Component subT : new ArrayList<>(tab.configShown().keySet())) {
                List<ConfigEntry> entriesSelect = new ArrayList<>(tab.configShown().get(subT));
                entriesSelect.sort(Comparator.comparing(ConfigEntry::getPosition));
                tab.configShown().put(subT, entriesSelect);
            }
        }

        traverseConfig(EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig(), "");
    }

    protected void traverseConfig(Config config, String path) {
        for (Config.Entry entry : config.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            String fullPath = path.isEmpty() ? key : path + "." + key;

            if (value instanceof Config nested) {
                traverseConfig(nested, fullPath);
            } else if (value instanceof Boolean bool) {
                // System.out.println(fullPath + " = " + value);
                addToTab(MIXINS, Component.literal(path), new SimpleBoolEntry(key, entry::getValue, b -> {
                    config.set(key, b);
                }));
            }
        }
    }

    private void registerBuiltinConfigTabs() {
        put(SEASON,
                CommonConfig.Season.enableInform,
                CommonConfig.Season.validDimensions,
                CommonConfig.Season.lastingDaysOfEachTerm,
                CommonConfig.Season.initialSolarTermIndex,
                CommonConfig.Season.monthOffset,
                CommonConfig.Season.dayOffset,
                ClientConfig.GUI.showGregorianYear,
                CommonConfig.Season.daylightChange,
                CommonConfig.Season.springDayTimes,
                CommonConfig.Season.summerDayTimes,
                CommonConfig.Season.autumnDayTimes,
                CommonConfig.Season.winterDayTimes,
                CommonConfig.Season.noneDayTimes,
                CommonConfig.Season.dynamicSnowTerm,
                CommonConfig.Season.realWorldSolarTerms,
                // CommonConfig.Resource.springGrass,
                ClientConfig.Sound.sound
        );

        put(SNOW,
                CommonConfig.Temperature.heatStroke,
                CommonConfig.Temperature.iceMelt,
                CommonConfig.Temperature.snowDown,
                CommonConfig.Snow.snowyWinter,
                CommonConfig.Snow.blocksNotSnowy,
                CommonConfig.Snow.snowInWorld,
                CommonConfig.Resource.SnowTogether,
                CommonConfig.Resource.RegionalSnowTime,
                // CommonConfig.Map.changeMapColor,
                CommonConfig.Resource.extraSnow
        );

        put(CROP,
                CommonConfig.Crop.enableCrop,
                CommonConfig.Crop.enableCropHumidityControl,
                CommonConfig.Crop.greenHouseMaxDiameter,
                CommonConfig.Crop.greenHouseMaxHeight,
                CommonConfig.Crop.complexGreenHouseCheck,
                CommonConfig.Crop.forceCompatMode,
                CommonConfig.Crop.simpleGreenHouse,
                CommonConfig.Crop.seasonalPrayerRitualTimeCost
        );

        put(ANIMAL,
                CommonConfig.Animal.enableBreed,
                CommonConfig.Animal.enableTimeBreed,
                CommonConfig.Animal.enableBee,
                CommonConfig.Animal.enableFishing,
                CommonConfig.Animal.beePollinateSeasons,
                CommonConfig.Animal.beeActiveSeasons,
                CommonConfig.Animal.fishingSeasons
        );

        put(WEATHER,
                CommonConfig.Weather.notRainInDesert,
                CommonConfig.Weather.shouldInitSnowForExtremeColdBiomes,
                CommonConfig.Weather.rainChanceMultiplier,
                CommonConfig.Weather.thunderChanceMultiplier,
                CommonConfig.Weather.snowAccumulationSpeedMultiplier,
                CommonConfig.Weather.snowMeltSpeedMultiplier,
                ClientConfig.Debug.fogWeather
        );

        put(RENDER,
                ClientConfig.Renderer.forceChunkRenderUpdate,
                ClientConfig.Renderer.enhancementChunkRenderUpdate,
                ClientConfig.Renderer.flowerOnGrass,
                ClientConfig.Renderer.seasonalGrassColorChange,
                ClientConfig.Renderer.seasonalColorChangeExtend,
                ClientConfig.Renderer.smootherSeasonalGrassColorChange,
                ClientConfig.Renderer.snowInFence,
                ClientConfig.Renderer.extraSnowLayer,
                ClientConfig.Particle.seasonParticle,
                ClientConfig.Particle.snowLeafParticles,
                ClientConfig.GUI.simpleSeasonHud
        );

        put(DEBUG,
                ClientConfig.Debug.debugInfo,
                ClientConfig.Debug.smoothSnowyEdges,
                ClientConfig.Debug.frozenWater,
                CommonConfig.Resource.NotIgnoreRiver
        );

        put(COMPAT,
                CompatModule.CommonConfig.sereneSeasons,
                CompatModule.CommonConfig.DistantHorizonsWinterLOD,
                CompatModule.ClientConfig.DistantHorizonsWinterLODForceUpdateAll
        );
    }

    protected void put(Component tab, Object... values) {
        for (Object value : values) {
            if (value != null) {
                configTabs.put(value, tab);
            }
        }
    }

    protected Component classify(Object obj) {
        return configTabs.getOrDefault(obj, OTHERS);
    }

    public ESModConfigScreen(final Minecraft mod, final Screen parent) {
        this(parent);
        // this.mod = mod;
    }

    protected Component selectTab;

    private int tabOffset = 0;
    private static final int TAB_BUTTON_WIDTH = 50;
    private static final int TAB_SPACING = 8;
    private static final int TAB_NAV_WIDTH = 20;

    @Override
    protected void init() {
        selectTab = selectTab == null ? HOT : selectTab;

        this.globalSuggestWidget = new SuggestWidget(0, 0, 0, this.font, (s) -> {
        });

        layout = new HeaderAndFooterLayout(this, 61, 33);

        // 1.20.1 patch for HeaderAndFooterLayout
        try {
            Field storageField = layout.getClass().getDeclaredField("contentsFrame");
            storageField.setAccessible(true);
            FrameLayout frameLayout = (FrameLayout) storageField.get(layout);
            frameLayout.defaultChildLayoutSetting().align(0.5F, 0.0F).paddingTop(0);
        } catch (ReflectiveOperationException ignored) {
        }

        layout.newHeaderLayoutSettings().align(0.5F, 0.0F);
        layout.newContentLayoutSettings().align(0.5F, 0.0F).paddingTop(-30);

        int buttonWidth = width / 2 - 36;
        int startX = this.width / 2 - buttonWidth / 2;
        int currentY = 0;

        LinearLayout header$linearLayout1 = new LinearLayout(0, 0, LinearLayout.Orientation.VERTICAL);
        // header$linearLayout1.defaultChildLayoutSetting().paddingBottom(8);
        LinearLayout header = this.layout.addToHeader(header$linearLayout1);

        LinearLayout subHeader$linearLayout = new LinearLayout(width - 40, 0, LinearLayout.Orientation.HORIZONTAL);
        // subHeader$linearLayout.defaultChildLayoutSetting().paddingHorizontal(TAB_SPACING);
        LinearLayout subHeader = header.addChild(subHeader$linearLayout);


        header.addChild(new StringWidget(0, 30, TITLE, this.font), header.defaultChildLayoutSetting().alignHorizontallyCenter());


        Button adOption = Button.builder(Component.translatable(CompatModule.isConfigured() ?
                "eclipticseasons.options.advance" : "eclipticseasons.options.configured_uninstalled"), (button) -> {
            Screen configurationScreen = ConfiguredUtil.getSafe(ESModConfigScreen.this.parent);
            if (configurationScreen != null)
                Minecraft.getInstance().setScreen(configurationScreen);
        }).width(TAB_BUTTON_WIDTH).build();
        adOption.active = CompatModule.isConfigured();
        subHeader.addChild(adOption);

        List<Component> tabList = new ArrayList<>(tabs.keySet());

        int availableWidth = this.width - 40;

        availableWidth -= TAB_BUTTON_WIDTH + TAB_SPACING;
        availableWidth -= (TAB_NAV_WIDTH + TAB_SPACING) * 2;
        int maxVisibleTabs = Math.max(1, (availableWidth + TAB_SPACING) / (TAB_BUTTON_WIDTH + TAB_SPACING));

        int maxOffset = Math.max(0, tabList.size() - maxVisibleTabs);
        tabOffset = Mth.clamp(tabOffset, 0, maxOffset);

        boolean canScrollLeft = tabOffset > 0;
        Button prev = Button.builder(Component.literal("<"), button -> {
            tabOffset = Math.max(0, tabOffset - 1);
            ESModConfigScreen.this.init(getMinecraft(), width, height);
        }).width(TAB_NAV_WIDTH).build();
        prev.active = canScrollLeft;
        subHeader.addChild(prev);

        for (int i = tabOffset; i < Math.min(tabOffset + maxVisibleTabs, tabList.size()); i++) {
            Component component = tabList.get(i);

            Component label = Objects.equals(component, selectTab)
                    ? component.copy().withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
                    : component;

            subHeader.addChild(Button.builder(label, button -> {
                ESModConfigScreen.this.selectTab = component;
                ESModConfigScreen.this.init(getMinecraft(), width, height);
            }).width(TAB_BUTTON_WIDTH).build());
        }

        boolean canScrollRight = tabOffset < maxOffset;
        Button next = Button.builder(Component.literal(">"), button -> {
            tabOffset = Math.min(maxOffset, tabOffset + 1);
            ESModConfigScreen.this.init(getMinecraft(), width, height);
        }).width(TAB_NAV_WIDTH).build();
        next.active = canScrollRight;
        subHeader.addChild(next);


        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(4).alignHorizontallyCenter();
        GridLayout.RowHelper helper = gridLayout.createRowHelper(2);

        Tab tab = tabs.get(selectTab);

        for (Map.Entry<Component, List<ConfigEntry>> pair : tab.configShown().entrySet()) {
            if (pair.getValue().isEmpty()) continue;
            if (tab.configShown().size() > 1) {
                TitleEntry titleEntry = new TitleEntry(pair.getKey().getString());
                helper.addChild(titleEntry.build(this, startX, currentY, buttonWidth), titleEntry.getColumn());
            }
            for (ConfigEntry entry : pair.getValue()) {
                LayoutElement build = entry.build(this, startX, currentY, buttonWidth);
                int column = entry.getColumn();
                if (build != null) {
                    helper.addChild(build, column);
                }
            }
        }

        ScrollableLayout scrollableLayout = new ScrollableLayout(this.minecraft, gridLayout, this.layout.getHeight() - this.layout.getFooterHeight() - this.layout.getHeaderHeight());

        layout.addToContents(scrollableLayout);


        LinearLayout footer = new LinearLayout(0, 0, LinearLayout.Orientation.HORIZONTAL);
        footer.defaultChildLayoutSetting().paddingHorizontal(TAB_SPACING);
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(buttonWidth).build());
        footer.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            ESModConfigScreen.this.saveOnClose = false;
            this.onClose();
        }).width(buttonWidth).build());
        layout.addToFooter(footer);
        layout.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(this.globalSuggestWidget);

        this.layout.arrangeElements();

        // 1.20.1 patch for HeaderAndFooterLayout
        // scrollableLayout.setY(gridLayout.getY() - 30);
        // scrollableLayout.arrangeElements();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (globalSuggestWidget.isMouseOver(mouseX, mouseY)) {
            return globalSuggestWidget.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(GuiGraphics pGuiGraphics) {
        super.renderBackground(pGuiGraphics);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
    }

    protected Map<String, byte[]> configCache = new HashMap<>();

    public void initConfigCache() {
        for (ModConfig modConfig : Stream.of(ModConfig.Type.COMMON, ModConfig.Type.CLIENT)
                .map(c -> EclipticSeasons.defaultConfigName(ModConfig.Type.COMMON, EclipticSeasonsApi.MODID))
                .map(s -> ConfigTracker.INSTANCE.fileMap().get(s))
                .filter(Objects::nonNull)
                .toList()) {
            try {
                configCache.put(modConfig.getFileName(), Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(modConfig.getFileName())));
            } catch (IOException e) {
                EclipticSeasons.logger(e);
            }
        }
    }

    public void backupConfigCache() {
        for (Map.Entry<String, byte[]> entry : configCache.entrySet()) {
            ModConfig modConfig = ConfigTracker.INSTANCE.fileMap().get(entry.getKey());
            if (modConfig != null) {
                modConfig.acceptSyncedConfig(entry.getValue());
            }
        }
    }

    @Override
    public void onClose() {
        super.onClose();
        if (!saveOnClose) {
            backupConfigCache();
            Objects.requireNonNull(this.minecraft).setScreen(this.parent);
            return;
        }

        boolean needRestart = false;
        boolean needGameRestart = false;
        boolean isChanged = false;
        boolean inGame = Minecraft.getInstance().level != null;

        Set<SyncType> syncTypes = new HashSet<>();
        for (Map.Entry<Component, Tab> componentTabEntry : tabs.entrySet()) {
            for (Map.Entry<Component, List<ConfigEntry>> componentListEntry : componentTabEntry.getValue().configShown().entrySet()) {
                for (ConfigEntry configEntry : componentListEntry.getValue()) {
                    boolean valueChange = configEntry.isValueChanged();
                    isChanged |= valueChange;
                    needRestart |= valueChange && configEntry.shouldRestart(inGame);
                    needGameRestart |= valueChange && configEntry.shouldRestart(false);
                    if (valueChange && configEntry instanceof ConfigEntry.SpecEntry<?> specEntry) {
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
            ModConfig commonModConfig = ConfigTracker.INSTANCE.fileMap().get(SyncType.COMMON.configName(EclipticSeasonsApi.MODID));
            if (syncTypes.contains(SyncType.COMMON)) {
                CommonConfig.COMMON_CONFIG.save();
                ESConfigSync.INSTANCE.notBackup(commonModConfig);
            }
            if (syncTypes.contains(SyncType.CLIENT)) ClientConfig.CLIENT_CONFIG.save();
            if (syncTypes.contains(SyncType.MIXINS)) EclipticSeasonsMixinPlugin.PreloadedConfig.getConfig().save();

            if (Minecraft.getInstance().getConnection() != null
                    && !Minecraft.getInstance().isLocalServer()
                    && Minecraft.getInstance().player.hasPermissions(Commands.LEVEL_ADMINS)
            ) {
                try {
                    if (syncTypes.contains(SyncType.COMMON)) {
                        byte[] bytes = Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(commonModConfig.getFileName()));
                        SimpleNetworkHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new ESConfigToServerPayload(commonModConfig.getFileName(), needRestart, SyncType.of(commonModConfig.getType()), bytes));
                    }

                    if (syncTypes.contains(SyncType.MIXINS)) {
                        byte[] bytes = Files.readAllBytes(FMLPaths.CONFIGDIR.get().resolve(SyncType.MIXINS.configName(EclipticSeasonsApi.MODID)));
                        SimpleNetworkHandler.CHANNEL.send(PacketDistributor.SERVER.noArg(), new ESConfigToServerPayload(SyncType.MIXINS.configName(EclipticSeasonsApi.MODID), true, SyncType.MIXINS, bytes));
                    }
                } catch (IOException e) {
                    EclipticSeasons.logger(e);
                }
            }
        }

        if (needRestart || needGameRestart) {
            var restartType = inGame && !needGameRestart ? RestartTypeUtil.RestartType.WORLD : RestartTypeUtil.RestartType.GAME;
            switch (restartType) {
                case GAME -> {
                    minecraft.setScreen(new TooltipConfirmScreen(b -> {
                        if (b) {
                            minecraft.stop();
                        } else {
                            super.onClose();
                        }
                    }, GAME_RESTART_TITLE, GAME_RESTART_MESSAGE, GAME_RESTART_YES, RESTART_NO));
                }
                case WORLD -> {
                    if (minecraft.level != null) {
                        minecraft.setScreen(new TooltipConfirmScreen(b -> {
                            if (b) {
                                TooltipConfirmScreen.onDisconnect();
                            } else {
                                super.onClose();
                            }
                        }, SERVER_RESTART_TITLE, SERVER_RESTART_MESSAGE, minecraft.isLocalServer() ? RETURN_TO_MENU : CommonComponents.GUI_TO_TITLE, RESTART_NO));
                    }
                }
            }
        } else Objects.requireNonNull(this.minecraft).setScreen(this.parent);
    }

    private static final String LANG_PREFIX = "eclipticseasons.configuration.uitext.";
    public static final Component GAME_RESTART_TITLE = Component.translatable(LANG_PREFIX + "restart.game.title");
    public static final Component SERVER_RESTART_TITLE = Component.translatable(LANG_PREFIX + "restart.server.title");
    public static final Component SERVER_RESTART_MESSAGE = Component.translatable(LANG_PREFIX + "restart.server.text");
    private static final Component RETURN_TO_MENU = Component.translatable("menu.returnToMenu");
    public static final Component GAME_RESTART_MESSAGE = Component.translatable(LANG_PREFIX + "restart.game.text");
    public static final Component GAME_RESTART_YES = Component.translatable("menu.quit");

    public static final Component RESTART_NO = Component.translatable(LANG_PREFIX + "restart.return");
    public static final Component RESTART_NO_TOOLTIP = Component.translatable(LANG_PREFIX + "restart.return.tooltip").withStyle(ChatFormatting.RED, ChatFormatting.BOLD);


    public Font getFont() {
        return font;
    }

    public static class TooltipConfirmScreen extends ConfirmScreen {
        private TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
            super(callback, title, message, yesButton, noButton);
        }


        @Override
        protected void addButtons(int y) {
            super.addButtons(y);
            this.noButton = (RESTART_NO_TOOLTIP);
        }

        public static void onDisconnect() {
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = minecraft.isLocalServer();
            boolean flag1 = minecraft.isConnectedToRealms();
            minecraft.level.disconnect();
            if (flag) {
                minecraft.clearLevel(new GenericDirtMessageScreen(Component.translatable("menu.savingLevel")));
            } else {
                minecraft.clearLevel();
            }

            TitleScreen titlescreen = new TitleScreen();
            if (flag) {
                minecraft.setScreen(titlescreen);
            } else if (flag1) {
                minecraft.setScreen(new RealmsMainScreen(titlescreen));
            } else {
                minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
            }
        }
    }

}
