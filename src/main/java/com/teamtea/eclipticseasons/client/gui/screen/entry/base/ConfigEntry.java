package com.teamtea.eclipticseasons.client.gui.screen.entry.base;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.entry.*;
import com.teamtea.eclipticseasons.config.CommonConfig;
import com.teamtea.eclipticseasons.config.sync.SyncType;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;

import java.util.Set;

public abstract class ConfigEntry {
    protected static final WidgetSprites CLIENT_SPRITES = new WidgetSprites(EclipticSeasons.rl("widget/es_button"), EclipticSeasons.rl("widget/es_button_disabled"), EclipticSeasons.rl("widget/es_button_highlighted"));

    protected final Component label;

    public ConfigEntry(String translationKey) {
        this.label = Component.translatable(translationKey);
    }

    public boolean isValueChanged() {
        return false;
    }

    public boolean shouldRestart(boolean inGame) {
        return false;
    }

    public int getPosition() {
        return 10;
    }

    public int getColumn() {
        return 1;
    }

    public SyncType getSyncType() {
        return SyncType.NONE;
    }

    public abstract LayoutElement build(ESModConfigScreen screen, int x, int y, int width);

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

    public @NonNull
    static MultiLineTextWidget getMultiLineTextWidget(Component label, ESModConfigScreen screen, int width) {
        Component label1 = label;
        int width1 = screen.getFont().width(label1.getString());
        if (width1 > width - 20) {
            float cut = (width - 20) / (float) width1;
            String substring = label1.getString().substring(0, (int) (cut * label1.getString().length()));
            label1 = Component.literal(substring + "...");
        }
        MultiLineTextWidget multiLineTextWidget = new MultiLineTextWidget(label1, screen.getFont());
        multiLineTextWidget.setHeight(20);
        multiLineTextWidget.setWidth(width);
        return multiLineTextWidget;
    }

    public abstract static class SpecEntry<T> extends ConfigEntry {
        @Getter
        protected final ModConfigSpec.ConfigValue<T> spec;
        protected final long hashValueCache;
        @Getter
        protected final SyncType syncType;

        public SpecEntry(ModConfigSpec.ConfigValue<T> spec) {
            super("eclipticseasons.configuration." + spec.getPath().getLast());
            this.spec = spec;
            this.hashValueCache = spec.get().hashCode();
            syncType = SyncType.getTypeFrom(spec);
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
        public LayoutElement build(ESModConfigScreen screen, int x, int y, int width) {
            screen.configRegistered.add(spec);

            LayoutElement layoutElement = buildLayout(screen, x, y, width);

            MutableComponent title = Component.translatable("eclipticseasons.configuration." + spec.getPath().getLast())
                    .withStyle(ChatFormatting.BOLD);

            String commentKey = "eclipticseasons.configuration." + spec.getPath().getLast() + ".tooltip";
            MutableComponent comment = Component.literal("\n\n")
                    .withStyle(Style.EMPTY.withBold(false))
                    .append(Language.getInstance().has(commentKey) ?
                            Component.translatable(commentKey) :
                            Component.literal(spec.getSpec().getComment() + ""));

            layoutElement.visitWidgets(aw -> {
                if (aw.tooltip.get() == null) {
                    // aw.setTooltip(Tooltip.create(title.copy().append(comment)));
                    aw.setTooltip(Tooltip.create(title.copy().withStyle(ChatFormatting.BOLD).append(comment.withStyle(style -> style.withBold(false)))));
                }
            });
            // layoutElement.setTooltip(Tooltip.create(title.append(comment)));
            return layoutElement;
        }

        public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
            LinearLayout linearLayout = new LinearLayout(x, y, LinearLayout.Orientation.HORIZONTAL);
            linearLayout.addChild(buildModConfigSpec(screen, x, y, width));
            return linearLayout;
        }

        public abstract AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width);

        public static final Set<Object> dayTimes = Set.of(CommonConfig.Season.springDayTimes, CommonConfig.Season.summerDayTimes, CommonConfig.Season.autumnDayTimes, CommonConfig.Season.winterDayTimes, CommonConfig.Season.noneDayTimes);
        public static final Set<Object> activeSeasons = Set.of(CommonConfig.Animal.beeActiveSeasons, CommonConfig.Animal.beePollinateSeasons, CommonConfig.Animal.fishingSeasons);

        public static <C> SpecEntry<C> parse(ModConfigSpec.ConfigValue<C> cv) {
            ConfigEntry specEntry = null;
            if (cv instanceof ModConfigSpec.BooleanValue bv) {
                specEntry = (new BoolEntry(bv));
            } else if (cv instanceof ModConfigSpec.IntValue bv) {
                specEntry = (ConfigEntry.createNumber(bv));
            } else if (cv instanceof ModConfigSpec.DoubleValue bv) {
                specEntry = (ConfigEntry.createNumber(bv));
            }  else if (cv instanceof ModConfigSpec.EnumValue<?> bv) {
                specEntry = (new EnumEntry<>(bv));
            }else if (cv == CommonConfig.Season.validDimensions) {
                specEntry = (SuggestedListStringEntry.fromRegistry(CommonConfig.Season.validDimensions, Registries.DIMENSION_TYPE));
            } else if (cv == CommonConfig.Snow.blocksNotSnowy) {
                specEntry = (SuggestedListStringEntry.fromRegistry(CommonConfig.Snow.blocksNotSnowy, Registries.BLOCK));
            } else if (activeSeasons.contains(cv)) {
                specEntry = (SuggestedListStringEntry.fromEnum((ModConfigSpec.ConfigValue) cv, Season.class));
            } else if (dayTimes.contains(cv)) {
                specEntry = (new FixedIntegerListEntry((ModConfigSpec.ConfigValue) cv));
            }
            return (SpecEntry) specEntry;
        }
    }
}
