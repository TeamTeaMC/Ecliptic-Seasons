package com.teamtea.eclipticseasons.client.gui.screen.entry;

import com.google.common.base.CaseFormat;
import com.teamtea.eclipticseasons.api.misc.ITranslatable;
import com.teamtea.eclipticseasons.client.gui.screen.ESModConfigScreen;
import com.teamtea.eclipticseasons.client.gui.screen.SuggestWidget;
import com.teamtea.eclipticseasons.client.gui.screen.entry.base.ConfigEntry;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.NonNull;

import java.lang.ref.WeakReference;
import java.util.*;

public class SuggestedListStringEntry extends ConfigEntry.SpecEntry<List<? extends String>> {

    private final Set<WK> possibleValues;
    // private @Nullable Predicate<String> validTest;

    public SuggestedListStringEntry(ModConfigSpec.ConfigValue<List<? extends String>> spec, Set<WK> possibleValues) {
        super(spec);
        this.possibleValues = possibleValues;
    }

    public record WK(String value, String tipKey) {
    }

    public static <T> String getName(Identifier key, ResourceKey<Registry<T>> rk) {
        String[] split = rk.identifier().getPath().split("/");
        return Component.translatable(Util.makeDescriptionId(split[split.length - 1].replace("dimension_type", "dimension"), key)).getString();
    }

    public static <T> SuggestedListStringEntry fromRegistry(
            ModConfigSpec.ConfigValue<List<? extends String>> spec,
            ResourceKey<Registry<T>> rk) {
        LinkedHashSet<WK> wks = new LinkedHashSet<>();
        if (Minecraft.getInstance().level != null) {
            for (Identifier identifier : Minecraft.getInstance().level.registryAccess().lookupOrThrow(rk).keySet()) {
                wks.add(new WK(identifier.toString(), getName(identifier, rk)));
            }
        } else {
            wks = null;
        }
        return new SuggestedListStringEntry(spec, wks);
    }

    public static <T extends Enum<T>> SuggestedListStringEntry fromEnum(
            ModConfigSpec.ConfigValue<List<? extends String>> spec,
            Class<T> rk) {
        LinkedHashSet<WK> wks = new LinkedHashSet<>();
        T[] enumValues = rk.getEnumConstants();
        if (enumValues != null) {
            for (T value : enumValues) {
                String string = value.toString();
                String result = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, string);
                String friendly = result.replaceAll("([A-Z])", " $1").trim();
                wks.add(new WK(string, value instanceof ITranslatable it ? it.getTranslation().getString() : friendly));
            }
        }
        return new SuggestedListStringEntry(spec, wks);
    }


    @Override
    public LayoutElement buildLayout(ESModConfigScreen screen, int x, int y, int width) {
        GridLayout gridLayout = new GridLayout();
        gridLayout.defaultCellSetting().paddingHorizontal(4).paddingBottom(3).alignHorizontallyCenter();
        GridLayout.RowHelper helper = gridLayout.createRowHelper(3);
        helper.addChild(new StringWidget(Component.empty().append(label).withStyle(ChatFormatting.ITALIC), screen.getFont()), 3);
        List<String> strings = new ArrayList<>(spec.get());
        if (possibleValues != null) {
            if (possibleValues.size() < 26) {
                for (WK possibleValue : possibleValues) {
                    MutableComponent literal = Component.translatable(possibleValue.tipKey())
                            .withStyle(strings.contains(possibleValue.value()) ? ChatFormatting.WHITE : ChatFormatting.GRAY);
                    List<String> finalStrings = strings;
                    CycleButton<Boolean> cycleButton = CycleButton.onOffBuilder(strings.contains(possibleValue.value())).create(
                            x, y, width * 2 / 3, 20, literal, (button, value) -> {
                                literal.withStyle(value ? ChatFormatting.WHITE : ChatFormatting.GRAY);
                                if (value && !finalStrings.contains(possibleValue.value())) {
                                    finalStrings.add(possibleValue.value());
                                } else if (!value) {
                                    finalStrings.remove(possibleValue.value());
                                }
                                spec.set(finalStrings);
                                // spec.save();
                            }
                    );
                    cycleButton.setTooltip(Tooltip.create(Component.translatable(possibleValue.tipKey()).append(" (").append(possibleValue.value).append(")")));
                    helper.addChild(cycleButton);
                }
            } else {
                ArrayList<String> strings1 = new ArrayList<>(new LinkedHashSet<>(strings));
                strings1.removeIf(String::isBlank);
                // strings1.removeIf(String::isEmpty);
                if (strings1.size() != strings.size()) {
                    strings = strings1;
                    spec.set(strings);
                }
                do strings1.add("");
                while (strings1.size() < 3);
                for (int i = 0; i < strings1.size(); i++) {
                    String string = strings1.get(i);
                    createNewBox(screen, width, string, helper, i, strings);
                }

            }
        }
        return gridLayout;
    }

    private void createNewBox(ESModConfigScreen screen, int width, String string, GridLayout.RowHelper helper, int index, List<String> strings) {
        EditBox editBox = new FocuseListnerEditBox(screen, width);
        editBox.setValue(string);
        helper.addChild(editBox);
        SuggestWidget suggestWidget = screen.getGlobalSuggestWidget();
        editBox.setResponder(text -> {
            List<String> matches = getSuggestions(text);
            // suggestWidget.setPosition(editBox.getX(), editBox.getY() + editBox.getHeight());
            suggestWidget.setWidth(200);
            suggestWidget.setEditBox(editBox);
            suggestWidget.setSuggestions(matches);
            suggestWidget.setOnSelect(editBox::setValue);

            Identifier identifier = Identifier.tryParse(text);
            if (identifier != null && (BuiltInRegistries.BLOCK.get(identifier).isPresent() || text.isEmpty())) {
                var stringsCopy = new ArrayList<String>(List.copyOf(spec.get()));
                if (index >= stringsCopy.size() && !text.isEmpty()) {
                    stringsCopy.add(text);
                } else {
                    stringsCopy.set(index, text);
                }
                spec.set(stringsCopy);
            }
        });
        editBox.setTooltip(Tooltip.create(Component.empty()));
    }


    private @NonNull List<String> getSuggestions(String text) {
        return possibleValues.stream()
                .map(WK::value)
                .filter(value -> value.contains(text))
                .limit(5)
                .toList();
    }

    @Override
    public AbstractWidget buildModConfigSpec(ESModConfigScreen screen, int x, int y, int width) {
        return new StringWidget(width, 20, Component.translatable(possibleValues.stream().findFirst().map(WK::tipKey).orElse("Invalid Option")), screen.getFont());
    }

    @Override
    public int getColumn() {
        return 2;
    }

    private class FocuseListnerEditBox extends EditBox {
        private final WeakReference<ESModConfigScreen> screen;

        public FocuseListnerEditBox(ESModConfigScreen screen, int width) {
            super(screen.getFont(), width * 2 / 3, 20, Component.empty());
            this.screen = new WeakReference<>(screen);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
            ESModConfigScreen esModConfigScreen = screen.get();
            if (esModConfigScreen == null) return;
            SuggestWidget suggestWidget = esModConfigScreen.getGlobalSuggestWidget();
            if (!focused) {
                suggestWidget.setSuggestions(Collections.emptyList());
                suggestWidget.setEditBox(null);
            } else {
                suggestWidget.setSuggestions(getSuggestions(getValue()));
                suggestWidget.setEditBox(this);
                suggestWidget.setOnSelect(this::setValue);
            }
        }
    }
}
