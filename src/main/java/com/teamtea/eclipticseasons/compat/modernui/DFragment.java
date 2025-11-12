//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.teamtea.eclipticseasons.compat.modernui;

import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import icyllis.arc3d.core.Color;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.drawable.ColorDrawable;
import icyllis.modernui.text.TextWatcher;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class DFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        GridLayout layout = new GridLayout(this.requireContext());
        layout.setColumnCount(3);
        layout.setOrientation(0);

        makeSelectLine(layout, "群系", Registries.BIOME);
        makeSelectLine(layout, "方块", Registries.BLOCK);

        makeTextLine(layout,"模型");
        makeTextLine(layout,"模型（非顶层）");

        //ScrollView s0 = new ScrollView(requireContext());
        //layout.addView(s0);
        TextView pre = new TextView(this.requireContext());
        layout.addView(pre, new ViewGroup.LayoutParams(-2, -2));
        pre.setText("属性检测器");
        Button button = new Button(this.requireContext());
        button.setText("+");
        layout.addView(button);
        layout.addView(new View(this.requireContext()));

        GridLayout innerlayout = new GridLayout(this.requireContext());
        innerlayout.setColumnCount(3);
        innerlayout.setOrientation(0);
        button.setOnClickListener(view -> {
            makeTextLine(innerlayout,"名字");
            makeCheckBox(innerlayout,"是否反转");
        });
        layout.addView(innerlayout, new ViewGroup.LayoutParams(-2, -2));


        Button button2 = new Button(this.requireContext());
        button2.setText("提交");
        layout.addView(button2);

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.addView(layout);
        return scrollView;
    }

    private void makeTextLine(ViewGroup layout, String label) {
        TextView pre = new TextView(this.requireContext());
        layout.addView(pre, new ViewGroup.LayoutParams(-2, -2));
        pre.setText(label);

        EditText input = new EditText(this.requireContext());
        input.setBackground(new ColorDrawable(Color.GRAY));
        //input.setPadding(20,0,10,20);
        layout.addView(input, new ViewGroup.LayoutParams(400, 20));
        layout.addView(new View(requireContext()));
    }

    private void makeCheckBox(ViewGroup layout, String label) {
        TextView pre = new TextView(this.requireContext());
        layout.addView(pre, new ViewGroup.LayoutParams(-2, -2));
        pre.setText(label);

        CheckBox input = new CheckBox(this.requireContext());
        layout.addView(input, new ViewGroup.LayoutParams(-2, -2));
        layout.addView(new View(requireContext()));
    }

    private void makeSelectLine(ViewGroup layout, String label, ResourceKey<? extends Registry<?>> key) {
        TextView pre = new TextView(this.requireContext());
        layout.addView(pre, new ViewGroup.LayoutParams(-2, -2));
        pre.setText(label);

        EditText input = new EditText(this.requireContext());
        input.setBackground(new ColorDrawable(Color.GRAY));
        layout.addView(input, new ViewGroup.LayoutParams(400, 20));

        Spinner spinner = new Spinner(this.requireContext());
        ArrayAdapter<AP> apArrayAdapter = setSpinner(spinner, "", key);
        spinner.setOnItemSelectedListener((adapterView, view, i, l) -> {
            if (adapterView.getItemAtPosition(i) instanceof AP ap) {
                input.setText(ap.resourceLocation.toString());
                input.getText();
            }
        });
        layout.addView(spinner, new ViewGroup.LayoutParams(-2, -2));

        input.addTextChangedListener((SimpleTextChangedListener) editable ->
                resetSpinner(apArrayAdapter, editable, key));

    }

    private ArrayAdapter<AP> setSpinner(Spinner spinner, Object editable, ResourceKey<? extends Registry<?>> key) {
        ArrayAdapter<AP> adapter = new ArrayAdapter<>(requireContext(),
                collectAps(key, editable.toString()));
        spinner.setAdapter(adapter);
        return adapter;
    }

    private ArrayAdapter<AP> resetSpinner(ArrayAdapter<AP> adapter, Object editable, ResourceKey<? extends Registry<?>> key) {
        adapter.clear();
        adapter.addAll(collectAps(key,editable.toString()));
        return adapter;
    }

    public List<AP> collectAps(ResourceKey<? extends Registry<?>> key, String limit) {
        String typeKey = getTypeKey(key);
        return ClientCon.getUseLevel().registryAccess().registryOrThrow(key).keySet().
                stream()
                .map(k -> {
                    String biomeName = Component.translatable(Util.makeDescriptionId(typeKey, k)).getString();
                    return (k.toString().contains(limit)
                            || biomeName.contains(limit)) ? new AP(
                            k, biomeName
                    ) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private static String getTypeKey(ResourceKey<? extends Registry<?>> key) {
        String type;
        if (key.equals(Registries.BIOME)) {
            type = "biome";
        } else if (key.equals(Registries.BLOCK)) {
            type = "block";
        } else if (key.equals(Registries.ITEM)) {
            type = "item";
        } else {
            type = key.registry().getPath();
        }
        return type;
    }

    public record AP(ResourceLocation resourceLocation, String text) {
        @Override
        public @NotNull String toString() {
            return text;
        }
    }

    @FunctionalInterface
    public interface SimpleTextChangedListener extends TextWatcher {
        @Override
        default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
    }
}
