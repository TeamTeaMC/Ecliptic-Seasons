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
import com.teamtea.eclipticseasons.config.*;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.config.sync.ESConfigSync;
import com.teamtea.eclipticseasons.config.sync.ESConfigToServerPayload;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import com.teamtea.eclipticseasons.mixin.EclipticSeasonsMixinPlugin;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.*;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.Mth;
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

    public ConfigEntry addToHotTab(ConfigEntry entry) {
        addToTab(HOT, HOT, entry);
        return entry;
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
                CommonConfig.Snow.snowyWinter,
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
                            CommonConfig.Temperature.snowDown.getAsBoolean()
                                    && CommonConfig.Temperature.iceMelt.getAsBoolean());
                }));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.DebugInfo",
                "eclipticseasons.configuration.DebugInfo.tooltip",
                ClientConfig.Debug.debugInfo,
                (bt, b) -> {
                    ClientConfig.Debug.debugInfo.set(b);
                }).setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.NaturalSound",
                "eclipticseasons.configuration.NaturalSound.tooltip",
                ClientConfig.Sound.naturalSound,
                (bt, b) -> {
                    ClientConfig.Sound.naturalSound.set(b);
                    ClientConfig.Sound.naturalSound.clearCache();
                }).setRestartType(ModConfigSpec.RestartType.WORLD)
                .setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.ExtraSnowLayer",
                "eclipticseasons.configuration.ExtraSnowLayer.tooltip",
                ClientConfig.Renderer.extraSnowLayer,
                (bt, b) -> {
                    ClientConfig.Renderer.extraSnowLayer.set(b);
                }).setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.ExtraSnowDefinitions",
                "eclipticseasons.configuration.ExtraSnowDefinitions.tooltip",
                StartConfig.Resource.extraSnow,
                (bt, b) -> {
                    StartConfig.Resource.extraSnow.set(b);
                    StartConfig.Resource.extraSnow.clearCache();
                }).setRestartType(ModConfigSpec.RestartType.GAME)
                .setSyncType(SyncType.STARTUP));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.FrozenWater",
                "eclipticseasons.configuration.FrozenWater.tooltip",
                ClientConfig.Debug.frozenWater,
                (bt, b) -> {
                    ClientConfig.Debug.frozenWater.set(b);
                })
                .setSyncType(SyncType.CLIENT));

        addToHotTab(new CallbackEntry(
                "eclipticseasons.configuration.SpringGrass",
                "eclipticseasons.configuration.SpringGrass.tooltip",
                CommonConfig.Resource.springGrass,
                (bt, b) -> {
                    CommonConfig.Resource.springGrass.set(b);
                    CommonConfig.Resource.springGrass.clearCache();
                }).setRestartType(ModConfigSpec.RestartType.WORLD));

        for (UnmodifiableConfig.Entry entry :
                Stream.of(CommonConfig.COMMON_CONFIG, ClientConfig.CLIENT_CONFIG, StartConfig.START_CONFIG)
                        .map(ModConfigSpec::getValues)
                        .map(UnmodifiableConfig::entrySet)
                        .flatMap(Collection::stream)
                        .toList()) {
            if (entry.getValue() instanceof com.electronwill.nightconfig.core.AbstractConfig simpleConfig) {
                // List<ConfigEntry> entriesSelect = new ArrayList<>();
                for (Config.Entry config : simpleConfig.entrySet()) {
                    if (config.getValue() instanceof ModConfigSpec.ConfigValue<?> cv
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
                CommonConfig.Resource.springGrass,
                ClientConfig.Sound.naturalSound
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
                StartConfig.Resource.extraSnow
        );

        put(CROP,
                CommonConfig.Crop.enableCrop,
                CommonConfig.Crop.enableCropHumidityControl,
                CommonConfig.Crop.restrictBoneMeal,
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
                ClientConfig.Weather.tweakPrecipitationParticleTexture
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
                CompatModule.ClientConfig.DistantHorizonsWinterLODForceUpdateAll,
                CompatModule.CommonConfig.voxyTest
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

    public ESModConfigScreen(final ModContainer mod, final Screen parent) {
        this(parent);
        this.mod = mod;
    }

    protected Component selectTab;

    private int tabOffset = 0;
    private static final int TAB_BUTTON_WIDTH = 50;
    private static final int TAB_SPACING = 8;
    private static final int TAB_NAV_WIDTH = 20;

    @Override
    protected void init() {
        selectTab = selectTab == null ? HOT : selectTab;

        this.globalSuggestWidget = new SuggestWidget(0, 0, 0, this.font, (_) -> {
        });

        layout = new HeaderAndFooterLayout(this, 61, 33);
        int buttonWidth = width / 2 - 36;
        int startX = this.width / 2 - buttonWidth / 2;
        int currentY = 40;

        LinearLayout header = this.layout.addToHeader(LinearLayout.vertical().spacing(8));
        header.addChild(new StringWidget(TITLE, this.font), LayoutSettings::alignHorizontallyCenter);

        LinearLayout subHeader = header.addChild(LinearLayout.horizontal()).spacing(TAB_SPACING);

        subHeader.addChild(Button.builder(Component.translatable("eclipticseasons.options.advance"), (button) -> {
            if (mod == null) {
                mod = ModList.get().getModContainerById(EclipticSeasonsApi.MODID).orElse(null);
            }
            if (mod != null) {
                ConfigurationScreen configurationScreen = new ConfigurationScreen(mod, ESModConfigScreen.this.parent);
                Minecraft.getInstance().setScreen(configurationScreen);
            }
        }).width(TAB_BUTTON_WIDTH).build());

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
            ESModConfigScreen.this.init(width, height);
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
                ESModConfigScreen.this.init(width, height);
            }).width(TAB_BUTTON_WIDTH).build());
        }

        boolean canScrollRight = tabOffset < maxOffset;
        Button next = Button.builder(Component.literal(">"), button -> {
            tabOffset = Math.min(maxOffset, tabOffset + 1);
            ESModConfigScreen.this.init(width, height);
        }).width(TAB_NAV_WIDTH).build();
        next.active = canScrollRight;
        subHeader.addChild(next);


        net.minecraft.client.gui.layouts.GridLayout gridLayout = new net.minecraft.client.gui.layouts.GridLayout();
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

        ScrollableLayout scrollableLayout = new ScrollableLayout(this.minecraft, gridLayout, this.layout.getContentHeight());
        layout.addToContents(scrollableLayout);
        LinearLayout footer = layout.addToFooter(LinearLayout.horizontal()).spacing(TAB_SPACING);
        footer.addChild(Button.builder(CommonComponents.GUI_BACK, (button) -> {
            ESModConfigScreen.this.saveOnClose = false;
            this.onClose();
        }).width(buttonWidth).build());
        footer.addChild(Button.builder(CommonComponents.GUI_DONE, (button) -> this.onClose()).width(buttonWidth).build());
        layout.visitWidgets(this::addRenderableWidget);

        this.addRenderableWidget(this.globalSuggestWidget);

        this.layout.arrangeElements();

        // scrollableLayout.container. scrollAmount()
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
                    minecraft.setScreen(new TooltipConfirmScreen(b -> {
                        if (b) {
                            minecraft.stop();
                        } else {
                            super.onClose();
                        }
                    }, ConfigurationScreen.GAME_RESTART_TITLE, ConfigurationScreen.GAME_RESTART_MESSAGE, ConfigurationScreen.GAME_RESTART_YES, ConfigurationScreen.RESTART_NO));
                }
                case WORLD -> {
                    if (minecraft.level != null) {
                        minecraft.setScreen(new TooltipConfirmScreen(b -> {
                            if (b) {
                                TooltipConfirmScreen.onDisconnect();
                            } else {
                                super.onClose();
                            }
                        }, ConfigurationScreen.SERVER_RESTART_TITLE, ConfigurationScreen.SERVER_RESTART_MESSAGE, minecraft.isLocalServer() ? ConfigurationScreen.RETURN_TO_MENU : CommonComponents.GUI_DISCONNECT, ConfigurationScreen.RESTART_NO));
                    }
                }
            }
        } else Objects.requireNonNull(this.minecraft).setScreen(this.parent);
    }

    @Override
    public @NonNull Font getFont() {
        return font;
    }

    public static class TooltipConfirmScreen extends ConfirmScreen {
        private TooltipConfirmScreen(BooleanConsumer callback, Component title, Component message, Component yesButton, Component noButton) {
            super(callback, title, message, yesButton, noButton);
        }

        @Override
        protected void addButtons(@NonNull LinearLayout layout) {
            super.addButtons(layout);
            this.noButton.setTooltip(Tooltip.create(ConfigurationScreen.RESTART_NO_TOOLTIP));
        }

        public static void onDisconnect() {
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = minecraft.isLocalServer();
            ServerData serverdata = minecraft.getCurrentServer();
            minecraft.level.disconnect(ClientLevel.DEFAULT_QUIT_MESSAGE);
            if (flag) {
                minecraft.disconnectWithSavingScreen();
            } else {
                minecraft.disconnectWithProgressScreen();
            }

            TitleScreen titlescreen = new TitleScreen();
            if (flag) {
                minecraft.setScreen(titlescreen);
            } else if (serverdata != null && serverdata.isRealm()) {
                minecraft.setScreen(new RealmsMainScreen(titlescreen));
            } else {
                minecraft.setScreen(new JoinMultiplayerScreen(titlescreen));
            }
        }
    }

}
