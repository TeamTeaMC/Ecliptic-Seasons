package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.config.ConfigCategory;
import com.teamtea.eclipticseasons.client.gui.screen.entry.spec.*;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import com.teamtea.eclipticseasons.config.util.RestartTypeUtil;
import com.teamtea.eclipticseasons.config.util.SpecUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class SpecEntry<T> extends ConfigEntry {
    @Getter
    protected final ForgeConfigSpec.ConfigValue<T> spec;
    protected final long hashValueCache;

    @Getter
    @Setter
    protected SyncType syncType;

    protected final String translationNamespace;

    public SpecEntry(ForgeConfigSpec.ConfigValue<T> spec) {
        this(spec, findTranslationNamespace(spec));
    }

    protected SpecEntry(ForgeConfigSpec.ConfigValue<T> spec, String translationNamespace) {
        super(translationKey(spec, translationNamespace));
        this.spec = spec;
        this.translationNamespace = translationNamespace;
        this.hashValueCache = spec.get().hashCode();
        this.syncType = SyncType.getTypeFrom(spec);
    }

    protected static String findTranslationNamespace(ForgeConfigSpec.ConfigValue<?> value) {
        ForgeConfigSpec.ValueSpec valueSpec = SpecUtil.getSpec(value);

        for (ModConfig config : ConfigTracker.INSTANCE.fileMap().values()) {
            if (config.getSpec() instanceof ForgeConfigSpec configSpec
                    && configSpec.getSpec().get(value.getPath()) == valueSpec) {
                return config.getModId();
            }
        }

        return EclipticSeasonsApi.MODID;
    }

    protected static String translationKey(ForgeConfigSpec.ConfigValue<?> spec, String namespace) {
        return namespace + ".configuration." + spec.getPath().get(spec.getPath().size() - 1);
    }

    protected String translationKey() {
        return translationNamespace + ".configuration." + spec.getPath().get(spec.getPath().size() - 1);
    }

    protected String translationTypeKey() {
        return translationNamespace + ".configuration." + spec.getPath().get(spec.getPath().size() - 2);
    }

    public static ConfigEntry createNumber(ForgeConfigSpec.ConfigValue<?> spec) {
        if (spec.get() instanceof Number) {
            final SpecUtil.Range<?> range = SpecUtil.getRange(SpecUtil.getSpec(spec));
            if (range != null && (
                    (range.getMax() instanceof Integer i && i > 100)
                            || (range.getMax() instanceof Double d && d > 1))) {
                return new NumberEntry.TextNumberEntry<>((ForgeConfigSpec.ConfigValue) spec);
            }
        }
        if (spec instanceof ForgeConfigSpec.IntValue iv) {
            return new NumberEntry.IntSliderEntry(iv);
        }
        if (spec instanceof ForgeConfigSpec.DoubleValue dv) {
            return new NumberEntry.DoubleSliderEntry(dv);
        }
        throw new UnsupportedOperationException(spec.getPath().get(spec.getPath().size() - 1));
    }

    public boolean isValueChanged() {
        spec.clearCache();
        return spec.get().hashCode() != hashValueCache;
    }

    public boolean shouldRestart(boolean inGame) {
        RestartTypeUtil.RestartType restartType = RestartTypeUtil.get(spec);
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
        MutableComponent comment = getComment();

        applyTooltip(layoutElement, title, comment);
        return layoutElement;
    }

    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        LinearLayout linearLayout = new LinearLayout(x, y, LinearLayout.Orientation.HORIZONTAL);
        linearLayout.addChild(buildModConfigSpec(screen, x, y, width));
        return linearLayout;
    }

    protected MutableComponent getLabel(ESModConfigScreen screen) {
        return screen.getSelectTab() != null && screen.getSelectTab().usesFullPathLabel() && spec.getPath().size() > 1 ?
                Component.translatable(translationTypeKey()).append(" > ").append(label) : label.copy();
    }

    public abstract LayoutElement buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width);

    @Override
    protected <E> Tooltip getTooltipSupplier(E value) {
        Component title = Component.translatable(translationKey());
        MutableComponent comment = getComment();
        return Tooltip.create(title.copy().withStyle(ChatFormatting.BOLD)
                .append(comment.copy().withStyle(style -> style.withBold(false))));
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
        return buildTooltipComment(commentKey, SpecUtil.getSpec(spec).getComment(), fullPathCommentKey);
    }

    public static final Set<Object> dayTimes = Set.of(CommonConfig.Season.springDayTimes, CommonConfig.Season.summerDayTimes, CommonConfig.Season.autumnDayTimes, CommonConfig.Season.winterDayTimes, CommonConfig.Season.noneDayTimes);
    public static final Set<Object> activeSeasons = Set.of(CommonConfig.Animal.beeActiveSeasons, CommonConfig.Animal.beePollinateSeasons, CommonConfig.Animal.fishingSeasons);

    public static <C> com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry<C> parse(ForgeConfigSpec.ConfigValue<C> cv) {
        ConfigEntry specEntry = null;
        if (cv instanceof ForgeConfigSpec.BooleanValue bv) {
            specEntry = (new BooleanEntry(bv));
        } else if (cv instanceof ForgeConfigSpec.IntValue bv) {
            specEntry = (createNumber(bv));
        } else if (cv instanceof ForgeConfigSpec.DoubleValue bv) {
            specEntry = (createNumber(bv));
        } else if (cv instanceof ForgeConfigSpec.EnumValue<?> bv) {
            specEntry = (new EnumEntry<>(bv));
        } else if (cv == CommonConfig.Season.validDimensions) {
            specEntry = (SuggestedListStringEntry.fromRegistry(CommonConfig.Season.validDimensions, Registries.DIMENSION_TYPE));
        } else if (cv == CommonConfig.Snow.blocksNotSnowy) {
            specEntry = (SuggestedListStringEntry.fromRegistry(CommonConfig.Snow.blocksNotSnowy, Registries.BLOCK));
        } else if (activeSeasons.contains(cv)) {
            specEntry = (SuggestedListStringEntry.fromEnum((ForgeConfigSpec.ConfigValue) cv, Season.class));
        } else if (dayTimes.contains(cv)) {
            specEntry = (new FixedIntegerListEntry((ForgeConfigSpec.ConfigValue) cv));
        }
        return (com.teamtea.eclipticseasons.client.gui.screen.entry.base.SpecEntry) specEntry;
    }
}
