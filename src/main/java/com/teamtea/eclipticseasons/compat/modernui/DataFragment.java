package com.teamtea.eclipticseasons.compat.modernui;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.api.data.client.ui.UIParser;
import com.teamtea.eclipticseasons.api.data.client.ui.elements.*;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.client.util.ClientRef;
import com.teamtea.eclipticseasons.compat.modernui.base.BaseF;
import com.teamtea.eclipticseasons.compat.modernui.state.AP;
import com.teamtea.eclipticseasons.compat.modernui.state.SingleEntryState;
import com.teamtea.eclipticseasons.compat.modernui.state.UIState;
import com.teamtea.eclipticseasons.compat.modernui.util.MUIUtil;
import icyllis.modernui.R;
import icyllis.modernui.graphics.drawable.ColorDrawable;
import icyllis.modernui.graphics.drawable.GradientDrawable;
import icyllis.modernui.text.Editable;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import icyllis.modernui.widget.Button;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.loading.FMLLoader;
import org.apache.commons.lang3.math.NumberUtils;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class DataFragment extends BaseF {

    @Getter
    protected final UIState state = new UIState();

    protected LinearLayout container;

    @Override
    protected void addToLayout(LinearLayout layout, int width) {
        if (ClientRef.uiParsers.isEmpty()) return;

        container = new LinearLayout(requireContext());
        layout.addView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        initSet();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        container.removeAllViews();
        listMapHolder.clear();
        container = null;
    }

    private void initSet() {
        //if (true) return;
        container.removeAllViews();
        state.setMode(null);
        container.setOrientation(LinearLayout.HORIZONTAL);
        for (UIParser uiParser : ClientRef.uiParsers) {
            Button sbutton = new Button(this.requireContext(), null, R.attr.buttonOutlinedStyle);
            sbutton.setText(uiParser.getFile().orElse(uiParser.getKey().orElse(EclipticSeasons.rl("null"))).toString());
            //button.setBackground(new ColorDrawable(java.awt.Color.PINK.getRGB()));
            container.addView(sbutton, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 200));
            sbutton.setOnClickListener(view -> {
                state.setMode(uiParser);
                renderFromSchema(state.getMode());
            });
        }
    }


    public void renderFromSchema(UIParser schema) {
        listMapHolder.clear();
        container.removeAllViews();
        container.setOrientation(LinearLayout.VERTICAL);

        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        if (!schema.isStack()) {
            makeTextLine(linearLayout, SingleEntryState.builder().id("namespace").build());
            makeTextLine(linearLayout, SingleEntryState.builder().id("path").build());
        }
        Button sbutton = new Button(this.requireContext(), null, R.attr.buttonOutlinedStyle);
        sbutton.setText(Component.translatable("ui.label.eclipticseasons.submit").getString());
        //button.setBackground(new ColorDrawable(java.awt.Color.PINK.getRGB()));

        sbutton.setOnClickListener(view -> {
            boolean result = save(ResourceLocation.fromNamespaceAndPath(
                            Optional.ofNullable(((EditText) (linearLayout.findViewByPredicate(v ->
                                            v instanceof EditText && v.getTag() instanceof SingleEntryState entryState
                                                    && entryState.getId().equals("namespace")))))
                                    .map(EditText::getText).map(Editable::toString)
                                    .orElse("ignore"),
                            Optional.ofNullable(((EditText) (linearLayout.findViewByPredicate(v ->
                                            v instanceof EditText && v.getTag() instanceof SingleEntryState entryState
                                                    && entryState.getId().equals("path")))))
                                    .map(EditText::getText).map(Editable::toString)
                                    .orElse("ignore")),
                    exportUIToJson());
            if (result) initSet();
        });
        container.addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        container.addView(sbutton);

        View view = new View(requireContext());
        view.setBackground(new ColorDrawable(Color.PINK.getRGB()));
        container.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

        for (UIElement field : schema.getFields()) {
            makeSingleEntry(container, field, null);
        }
    }


    protected Map<SingleEntryState, List<SingleEntryState>> listMapHolder = new IdentityHashMap<>();

    private void makeSingleEntry(ViewGroup layout, UIElement field, SingleEntryState parent) {
        String id = field.getId();
        SingleEntryState entryState = SingleEntryState.builder()
                .id(id)
                .parent(parent)
                .index(-1)
                .self(field)
                .build();
        if (parent != null && parent.getSelf() != null && parent.getSelf().isList()) {
            List<SingleEntryState> stateList = listMapHolder.computeIfAbsent(parent, (e) -> new ArrayList<>());
            entryState.setIndex(stateList.size());
            stateList.add(entryState);
        }

        if (field instanceof ListElement element) {
            LinearLayout listHolder = new LinearLayout(requireContext());
            listHolder.setOrientation(LinearLayout.VERTICAL);
            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setStroke(1, Color.PINK.getRGB());
            listHolder.setBackground(gradientDrawable);
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(5, 5, 5, 5);

            if ((field.isMap()) || (parent != null && parent.getSelf() != null && parent.getSelf().isMap())) {
                for (UIElement item : element.getItems()) {
                    makeSingleEntry(listHolder, item, entryState);
                }
            } else {
                Button button = new Button(this.requireContext(), null, R.attr.buttonOutlinedStyle);
                button.setText("+");
                layout.addView(button);
                button.setOnClickListener(view -> {
                    for (UIElement item : element.getItems()) {
                        makeSingleEntry(listHolder, item, entryState);
                    }
                });
            }

            layout.addView(listHolder, marginLayoutParams);
        } else if (field instanceof BoolElement) {
            makeCheckBox(layout, entryState);
        } else if (field instanceof TextElement) {
            makeTextLine(layout, entryState);
        } else if (field instanceof NumberElement) {
            makeTextLine(layout, entryState, DigitsInputFilter.getInstance((Locale) null));
        } else if (field instanceof AutoCompletionElement element) {
            var provide = element.getProvide();
            String key = element.getKey();
            if (provide == AutoCompletionElement.Provide.HOLDER_SET) {
                makeSelectLine(layout, entryState, (s) -> MUIUtil.collectAps(ResourceKey.createRegistryKey(ResourceLocation.parse(key)), s.toString()));
            } else if (provide == AutoCompletionElement.Provide.CLIENT_RESOURCE) {
                makeSelectLine(layout, entryState, (s) -> MUIUtil.collectModels(key.replaceAll(":", "/"), s.toString()));
            } else if (provide == AutoCompletionElement.Provide.BUILTIN) {
                if (key.equals("snowy_block_flag")) {
                    makeSelectLine(layout, entryState, (s) -> MUIUtil.getSnowyBlockFlag());
                }
            } else if (provide == AutoCompletionElement.Provide.EXTRA) {
                makeSelectLine(layout, entryState, (s) -> {
                    String string = s.toString();
                    return element.getExtraInfo().stream().filter(s1 -> s1.getValue().contains(string))
                            .map(s1 -> new AP(s1.getValue(), Component.translatable(s1.getDisplay()).getString()))
                            .collect(Collectors.toCollection(ArrayList::new));
                });
            }
        }
    }


    public JsonObject exportUIToJson() {
        JsonObject json = new JsonObject();

        //for (EditText editText : editTexts) {
        //    String key = (String) editText.getTag();
        //    if (key == null) continue;
        //    String string = editText.getText().toString();
        //    if (string.isEmpty()) continue;
        //    json.addProperty(key, string);
        //}

        traverseViews(getView(), json);

        return json;
    }

    private JsonElement resolveJsonPath(JsonObject root, Collection<SingleEntryState> chain) {
        JsonElement cursor = root;
        for (SingleEntryState state : chain) {
            String id = state.getId();
            EclipticSeasons.logger(state.getIndex());
            if (state.getSelf().isList()) {
                JsonArray obj = null;
                if (cursor instanceof JsonArray jsonArray) {
                    obj = new JsonArray();
                    jsonArray.add(obj);
                } else if (cursor instanceof JsonObject jsonObject) {
                    obj = jsonObject.has(id) ?
                            jsonObject.getAsJsonArray(id) : new JsonArray();
                    jsonObject.add(state.getId(), obj);
                }
                cursor = obj;
            } else {
                JsonObject obj = null;
                if (cursor instanceof JsonArray jsonArray) {
                    if (state.getSelf().isMap()
                            && !jsonArray.isEmpty()
                            && jsonArray.size() > state.getIndex()) {
                        int i = Math.max(0, state.getIndex());
                        obj = jsonArray.get(i).getAsJsonObject();
                    } else {
                        obj = new JsonObject();
                        jsonArray.add(obj);
                    }
                } else if (cursor instanceof JsonObject jsonObject) {
                    obj = jsonObject.has(id) ?
                            jsonObject.getAsJsonObject(id) : new JsonObject();
                    jsonObject.add(state.getId(), obj);
                }
                cursor = obj;
            }
        }

        return cursor;
    }

    private void traverseViews(View view, JsonObject json) {
        if (view instanceof ViewGroup vg) {
            for (int i = 0; i < vg.getChildCount(); i++) {
                traverseViews(vg.getChildAt(i), json);
            }
        } else if (view.getTag() instanceof SingleEntryState entryState
                && entryState.getSelf() != null) {
            String id = entryState.getId();
            Deque<SingleEntryState> stack = new ArrayDeque<>();
            SingleEntryState cur = entryState.getParent();
            while (cur != null) {
                stack.push(cur);
                cur = cur.getParent();
            }

            JsonArray useArray = null;
            JsonObject useObj = null;

            JsonElement jsonElement = resolveJsonPath(json, stack);
            if (jsonElement instanceof JsonArray) {
                useArray = jsonElement.getAsJsonArray();
            } else if (jsonElement instanceof JsonObject) {
                useObj = jsonElement.getAsJsonObject();
            }


            if (view instanceof EditText editText) {
                String text = editText.getText().toString();
                if (!text.isEmpty()) {
                    if (entryState.getSelf().isNumber()) {
                        Number number = NumberUtils.createNumber(text);
                        if (useArray == null) {
                            useObj.addProperty(id, number);
                        } else {
                            useArray.add(number);
                        }
                    } else {
                        if (useArray == null) {
                            useObj.addProperty(id, text);
                        } else {
                            useArray.add(text);
                        }
                    }
                }
            } else if (view instanceof CheckBox cb) {
                if (useArray == null) {
                    useObj.addProperty(id, cb.isChecked());
                } else {
                    useArray.add(cb.isChecked());
                }
            } else if (view instanceof Spinner spinner) {
                if (spinner.getSelectedItem() instanceof AP ap) {
                    if (useArray == null) {
                        useObj.addProperty(id, ap.getText());
                    } else {
                        useArray.add(ap.getText());
                    }
                }
            }
        }
    }

    public boolean save(ResourceLocation fileId, JsonObject root) {
        try {
            UIParser parser = getState().getMode();

            Path out = FMLLoader.getGamePath()
                    .resolve("config/eclipticseasons/generated/")
                    .resolve(parser.isSeverData() ? "data" : "asset");

            String fileName =
                    (parser.getFile().map(f -> f.toString())
                            .orElseGet(() -> parser.getKey()
                                    .map(ResourceLocation::toString)
                                    .orElse("") + "/" + fileId.getPath() + ".json"));

            fileName = fileName.replace(":", "/");
            if (!parser.isStack()) {
                fileName = fileId.getNamespace() + "/" + fileName;
            }

            Path file = out.resolve(fileName);

            Files.createDirectories(file.getParent());

            Files.writeString(file, ClientJsonCacheListener.GSON.toJson(root), StandardCharsets.UTF_8);

            ClientCon.agent.getCameraEntity().sendSystemMessage(Component.literal(
                    "✔ 已生成：" + file.toString()
            ));
            return true;
        } catch (IOException | InvalidPathException e) {
            ClientCon.agent.getCameraEntity().sendSystemMessage(Component.literal(
                    "❌ 生成失败：" + e.getMessage()
            ));
            return false;
        }
    }

}
