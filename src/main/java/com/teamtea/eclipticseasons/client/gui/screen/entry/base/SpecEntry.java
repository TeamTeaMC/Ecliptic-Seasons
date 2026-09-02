package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.spec.BooleanEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.spec.EnumEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.spec.FixedIntegerListEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.spec.NumberEntry;
import com.teamtea.eclipticseasons.client.gui.screen.entry.spec.SuggestedListStringEntry;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.config.ModConfigs;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * An entry whose value is provided by a {@link ModConfigSpec.ConfigValue}.
 */
public abstract class SpecEntry<T> extends ConfigEntry {
    @Getter
    protected final ModConfigSpec.ConfigValue<T> spec;
    protected final long hashValueCache;

    @Getter
    @Setter
    protected SyncType syncType;

    protected final String translationNamespace;

    public SpecEntry(ModConfigSpec.ConfigValue<T> spec) {
        this(spec, findTranslationNamespace(spec));
    }

    protected SpecEntry(ModConfigSpec.ConfigValue<T> spec, String translationNamespace) {
        super(translationKey(spec, translationNamespace));
        this.spec = spec;
        this.translationNamespace = translationNamespace;
        this.hashValueCache = spec.get().hashCode();
        this.syncType = SyncType.getTypeFrom(spec);
    }

    protected static String findTranslationNamespace(ModConfigSpec.ConfigValue<?> value) {
        ModConfigSpec.ValueSpec valueSpec = value.getSpec();

        for (ModConfig config : ModConfigs.getFileMap().values()) {
            if (config.getSpec() instanceof ModConfigSpec configSpec
                    && configSpec.getSpec().get(value.getPath()) == valueSpec) {
                return config.getModId();
            }
        }

        return EclipticSeasonsApi.MODID;
    }

    protected static String translationKey(ModConfigSpec.ConfigValue<?> spec, String namespace) {
        return namespace + ".configuration." + spec.getPath().get(spec.getPath().size() - 1);
    }

    protected String translationKey() {
        return translationNamespace + ".configuration." + spec.getPath().get(spec.getPath().size() - 1);
    }

    protected String translationTypeKey() {
        return translationNamespace + ".configuration." + spec.getPath().get(spec.getPath().size() - 2);
    }

    public boolean isValueChanged() {
        spec.clearCache();
        return spec.get().hashCode() != hashValueCache;
    }

    public boolean shouldRestart(boolean inGame) {
        ModConfigSpec.RestartType restartType = spec.getSpec().restartType();
        return switch (restartType) {
            case WORLD -> inGame;
            case GAME -> true;
            default -> false;
        };
    }

    @Override
    public String getSearchText() {
        return label.getString() + " " + spec.getPath().get(spec.getPath().size() - 1) + " "
                + (spec.getPath().size() > 1 ?
                Component.translatable(translationTypeKey()).getString() : "");
    }

    @Override
    public LayoutElement build(ESModConfigScreen screen, int x, int y, int width) {
        // screen.configRegistered.add(spec);

        LayoutElement layoutElement = buildLayout(screen, x, y, width);

        Component title = Component.translatable(translationKey());
        applyTooltip(layoutElement, title, getComment());
        return layoutElement;
    }

    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        LinearLayout linearLayout = new LinearLayout(x, y, LinearLayout.Orientation.HORIZONTAL);
        linearLayout.addChild(buildModConfigSpec(screen, x, y, width));
        return linearLayout;
    }

    protected MutableComponent getLabel(ESModConfigScreen screen) {
        return screen.getSelectTab() == ConfigCategory.ALL && spec.getPath().size() > 1 ?
                Component.translatable(translationTypeKey()).append(" > ").append(label) : label.copy();
    }

    public abstract LayoutElement buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width);

    @Override
    protected <E> Tooltip getTooltipSupplier(E value) {
        Component title = Component.translatable(translationKey());
        return Tooltip.create(title.copy().withStyle(ChatFormatting.BOLD)
                .append(getComment().copy().withStyle(style -> style.withBold(false))));
    }

    protected String fullPathTranslationKey() {
        List<String> path = new ArrayList<>(spec.getPath());
        Collections.reverse(path);
        String reversedPath = String.join(".", path);
        return translationNamespace + ".configuration." + reversedPath;
    }

    protected MutableComponent getComment() {
        String commentKey = translationKey() + ".tooltip";
        String fullPathCommentKey = fullPathTranslationKey() + ".tooltip";
        return buildTooltipComment(commentKey, spec.getSpec().getComment(), fullPathCommentKey);
    }

    public static ConfigEntry createNumber(ModConfigSpec.ConfigValue<?> spec) {
        if (spec.get() instanceof Number) {
            final var range = spec.getSpec().getRange();
            if (range != null && (
                    (range.getMax() instanceof Integer i && i > 100)
                            || (range.getMax() instanceof Double d && d > 1))) {
                return new NumberEntry.TextNumberEntry<>((ModConfigSpec.ConfigValue) spec);
            }
        }
        if (spec instanceof ModConfigSpec.IntValue iv) {
            return new NumberEntry.IntSliderEntry(iv);
        }
        if (spec instanceof ModConfigSpec.DoubleValue dv) {
            return new NumberEntry.DoubleSliderEntry(dv);
        }
        throw new UnsupportedOperationException(spec.getPath().getLast());
    }

    public static final Set<Object> dayTimes = Set.of(
            CommonConfig.Season.springDayTimes,
            CommonConfig.Season.summerDayTimes,
            CommonConfig.Season.autumnDayTimes,
            CommonConfig.Season.winterDayTimes,
            CommonConfig.Season.noneDayTimes
    );

    public static final Set<Object> activeSeasons = Set.of(
            CommonConfig.Animal.beeActiveSeasons,
            CommonConfig.Animal.beePollinateSeasons,
            CommonConfig.Animal.fishingSeasons
    );

    public static <C> SpecEntry<C> parse(ModConfigSpec.ConfigValue<C> cv) {
        ConfigEntry specEntry = null;
        if (cv instanceof ModConfigSpec.BooleanValue bv) {
            specEntry = new BooleanEntry(bv);
        } else if (cv instanceof ModConfigSpec.IntValue bv) {
            specEntry = createNumber(bv);
        } else if (cv instanceof ModConfigSpec.DoubleValue bv) {
            specEntry = createNumber(bv);
        } else if (cv instanceof ModConfigSpec.EnumValue<?> bv) {
            specEntry = new EnumEntry<>(bv);
        } else if (cv == CommonConfig.Season.validDimensions) {
            specEntry = SuggestedListStringEntry.fromRegistry(CommonConfig.Season.validDimensions, Registries.DIMENSION_TYPE);
        } else if (cv == CommonConfig.Snow.blocksNotSnowy) {
            specEntry = SuggestedListStringEntry.fromRegistry(CommonConfig.Snow.blocksNotSnowy, Registries.BLOCK);
        } else if (activeSeasons.contains(cv)) {
            specEntry = SuggestedListStringEntry.fromEnum((ModConfigSpec.ConfigValue) cv, Season.class);
        } else if (dayTimes.contains(cv)) {
            specEntry = new FixedIntegerListEntry((ModConfigSpec.ConfigValue) cv);
        }
        return (SpecEntry) specEntry;
    }
}
