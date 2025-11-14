//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.teamtea.eclipticseasons.compat.modernui;

import com.teamtea.eclipticseasons.api.data.season.SnowDefinition;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import icyllis.arc3d.core.Color;
import icyllis.modernui.R;
import icyllis.modernui.TestFragment;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.core.Context;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.drawable.ColorDrawable;
import icyllis.modernui.graphics.drawable.GradientDrawable;
import icyllis.modernui.graphics.drawable.ShapeDrawable;
import icyllis.modernui.mc.ui.CenterFragment2;
import icyllis.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.text.TextWatcher;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.util.AttributeSet;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DFragment extends Fragment {
    private List<EditText> editTexts = new ArrayList<>();

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        editTexts.clear();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        LinearLayout layout = new LinearLayout(this.requireContext());
        //layout.setColumnCount(3);
        //layout.setOrientation(0);
        layout.setOrientation(1);
        layout.setGravity(17);


        makeSelectLine(layout, "方块", (s) -> collectAps(Registries.BLOCK, s.toString()));

        makeSelectLine(layout, "模型", (s) -> collectModels(ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, s.toString()));
        makeSelectLine(layout, "模型（非顶层）", (s) -> collectModels(ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, s.toString()));
        makeSelectLine(layout, "类别", (s) -> {
            ArrayList<AP> objects = new ArrayList<>();
            objects.add(new AP(MapChecker.FLAG_NONE, "非覆雪方块"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON, "简单覆雪方块"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_PLANTS, "简单覆雪植物"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP, "覆雪方块（分层）"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_WITH_TOP_LEAVES, "覆雪树叶"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM_JSON_VINE_LIKE, "藤蔓类方块"));
            objects.add(new AP(MapChecker.FLAG_BLOCK, "内置-标准方块"));
            objects.add(new AP(MapChecker.FLAG_SLAB, "内置-半砖"));
            objects.add(new AP(MapChecker.FLAG_STAIRS, "内置-楼梯"));
            objects.add(new AP(MapChecker.FLAG_STAIRS_TOP, "内置-楼梯顶部"));
            objects.add(new AP(MapChecker.FLAG_LEAVES, "内置-树叶"));
            objects.add(new AP(MapChecker.FLAG_GRASS, "内置-草"));
            objects.add(new AP(MapChecker.FLAG_GRASS_LARGE, "内置-高草"));
            objects.add(new AP(MapChecker.FLAG_FARMLAND, "内置-耕地"));
            objects.add(new AP(MapChecker.FLAG_VINE, "内置-藤蔓"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM, "内置-自动处理方块"));
            objects.add(new AP(MapChecker.FLAG_CUSTOM_AO, "内置-自定义处理方块（AO）"));
            return objects;
        });

        makeCheckBox(layout, "雪可以穿过");
        makeTextLine(layout, "检查偏移值", DigitsInputFilter.getInstance((Locale) null));

        //ScrollView s0 = new ScrollView(requireContext());
        //layout.addView(s0);
        TextView pre = new TextView(this.requireContext());
        layout.addView(pre, new LinearLayout.LayoutParams(-2, -2));
        pre.setText("属性检测器");
        Button button = new Button(this.requireContext(), null, R.attr.buttonOutlinedStyle);
        button.setText("+");
        //button.setBackground(new ColorDrawable(java.awt.Color.PINK.getRGB()));
        layout.addView(button);


        //GridLayout innerlayout = new GridLayout(this.requireContext());
        //innerlayout.setColumnCount(3);
        //innerlayout.setOrientation(0);

        button.setOnClickListener(view -> {

            LinearLayout innerLa = new LinearLayout(this.requireContext());

            GradientDrawable gradientDrawable = new GradientDrawable();
            gradientDrawable.setStroke(1, java.awt.Color.PINK.getRGB());

            innerLa.setBackground(gradientDrawable);
            innerLa.setPadding(5, 5, 5, 5);
            innerLa.setOrientation(1);
            innerLa.setGravity(Gravity.CENTER);

            makeSelectLine(innerLa, "名字", (s) -> collectPropertiesFromBlocks(getBlocksFromStr(
                    ((EditText) layout.findViewWithTag("方块")).getText().toString()
            )));

            makeCheckBox(innerLa, "是否反转");

            TabLayout tabLayout = new TabLayout(requireContext());

            TabLayout.Tab tab1 = tabLayout.newTab().setText("精准匹配");
            LinearLayout gl1 = new LinearLayout(requireContext());
            gl1.setOrientation(LinearLayout.VERTICAL);
            //gl1.setGravity(Gravity.CENTER);
            //gl1.setPadding(16, 16, 16, 16);

            makeTextLine(gl1, "精确值");
            //tab1.setCustomView(gl1);
            tabLayout.addTab(tab1);
            //tabLayout.addView(gl1);
            TabLayout.Tab tab2 = tabLayout.newTab().setText("范围匹配");
            LinearLayout gl2 = new LinearLayout(requireContext());
            gl2.setOrientation(LinearLayout.VERTICAL);
            //gl2.setGravity(Gravity.CENTER);
            //gl2.setPadding(16, 16, 16, 16);

            makeTextLine(gl2, "最大值", DigitsInputFilter.getInstance((Locale) null));
            makeTextLine(gl2, "最小值", DigitsInputFilter.getInstance((Locale) null));
            //tab2.setCustomView(gl2);

            tabLayout.addTab(tab2);
            //tabLayout.addView(gl2);
            innerLa.addView(tabLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            innerLa.addView(gl1, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            innerLa.addView(gl2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            gl1.setVisibility(View.VISIBLE);
            gl2.setVisibility(View.GONE);

            tabLayout.addOnTabSelectedListener((SimpleOnTabSelectedListener) tab -> {
                if (tab == tab1) {
                    gl1.setVisibility(View.VISIBLE);
                    gl2.setVisibility(View.GONE);
                } else {
                    gl1.setVisibility(View.GONE);
                    gl2.setVisibility(View.VISIBLE);
                }
            });

            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            marginLayoutParams.setMargins(5, 5, 5, 5);
            layout.addView(innerLa, marginLayoutParams);
        });
        //layout.addView(innerlayout, new ViewGroup.LayoutParams(-2, -2));


        //Button button2 = new Button(this.requireContext());
        //button2.setText("提交");
        //layout.addView(button2);

        Button sbutton = new Button(this.requireContext(), null, R.attr.buttonOutlinedStyle);
        sbutton.setText("准备提交");
        //button.setBackground(new ColorDrawable(java.awt.Color.PINK.getRGB()));
        layout.addView(sbutton);

        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.addView(layout);
        return scrollView;
    }

    private void makeTextLine(ViewGroup layout, String label) {
        makeTextLine(layout, label, null);
    }

    private void makeTextLine(ViewGroup layout, String label, InputFilter justNumber) {
        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setDividerDrawable(ThemeControl.makeDivider(linearLayout, true));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView pre = new TextView(this.requireContext());
        linearLayout.addView(pre, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pre.setText(label);
        EditText input = new EditText(this.requireContext());
        if (justNumber != null) input.setFilters(justNumber);

        input.setBackground(new ColorDrawable(Color.GRAY));
        //input.setPadding(20,0,10,20);
        linearLayout.addView(input, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void makeCheckBox(ViewGroup layout, String label) {
        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView pre = new TextView(this.requireContext());
        linearLayout.addView(pre, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pre.setText(label);

        CheckBox input = new CheckBox(this.requireContext());
        linearLayout.addView(input, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void makeSelectLine(ViewGroup layout, String label, Function<Object, List<AP>> apFunction) {
        LinearLayout gridLayout = new LinearLayout(requireContext());
        //gridLayout.setColumnCount(3);
        gridLayout.setOrientation(0);
        TextView pre = new TextView(this.requireContext());
        gridLayout.addView(pre, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        pre.setText(label);

        EditText input = new EditText(this.requireContext());
        input.setBackground(new ColorDrawable(Color.GRAY));
        input.setMinimumWidth(400);
        input.setTag(label);
        gridLayout.addView(input, new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        Spinner spinner = new Spinner(this.requireContext());
        ArrayAdapter<AP> apArrayAdapter = setSpinner(spinner, "", apFunction);

        gridLayout.addView(spinner, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        spinner.setOnItemSelectedListener((adapterView, view, i, l) -> {
            if (adapterView.getSelectedItem() instanceof AP ap) {
                input.setText(ap.getText());
            }
        });

        input.addTextChangedListener((SimpleTextChangedListener) editable ->
                resetSpinner(apArrayAdapter, editable, apFunction));

        layout.addView(gridLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        editTexts.add(input);
    }

    private ArrayAdapter<AP> setSpinner(Spinner spinner, Object editable, Function<Object, List<AP>> apFunction) {
        List<AP> apply = apFunction.apply(editable);
        if (apply.size() == 1) apply.add(0, new AP("", "--"));
        ArrayAdapter<AP> adapter = new ArrayAdapter<>(requireContext(), apply);
        spinner.setAdapter(adapter);
        return adapter;
    }

    private ArrayAdapter<AP> resetSpinner(ArrayAdapter<AP> adapter, Object editable, Function<Object, List<AP>> apFunction) {
        adapter.clear();
        List<AP> apply = apFunction.apply(editable);
        if (apply.size() == 1) apply.add(0, new AP("", "--"));
        adapter.addAll(apply);
        return adapter;
    }

    public List<AP> collectAps(ResourceKey<? extends Registry<?>> key, String limit) {
        Registry<?> registry = ClientCon.getUseLevel().registryAccess().registryOrThrow(key);
        Stream<AP> apStream;
        if (!limit.startsWith("#")) {
            String typeKey = getTypeKey(key);
            apStream = registry.keySet().stream()
                    .map(k -> {
                        String biomeName = Component.translatable(Util.makeDescriptionId(typeKey, k)).getString();
                        return (k.toString().contains(limit) || biomeName.contains(limit)) ?
                                new AP(k, biomeName, false) : null;
                    });
        } else {

            apStream = registry.getTagNames()
                    .map(kk -> {
                        ResourceLocation k = kk.location();
                        String biomeName = "#" + k;
                        String substring = limit.substring(1);
                        return (k.toString().contains(substring) || biomeName.contains(substring)) ?
                                new AP(k, biomeName, true) : null;
                    });
        }
        return apStream
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public List<AP> collectModels(String path, String limit) {
        return ClientJsonCacheListener.ALL_MAP.getOrDefault(path, HashSet::new).get()
                .stream()
                .map(k -> {
                    return (k.toString().contains(limit)) ? new AP(
                            k, k.toString(), false
                    ) : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public Collection<Block> getBlocksFromStr(String s) {
        try {
            if (!s.startsWith("#")) {
                ResourceLocation resourceLocation = new ResourceLocation(s);
                return List.of(BuiltInRegistries.BLOCK.get(resourceLocation));
            } else {
                ResourceLocation resourceLocation = new ResourceLocation(s.substring(1));
                return BuiltInRegistries.BLOCK.getTag(TagKey.create(Registries.BLOCK, resourceLocation))
                        .map(ssss -> ssss.stream().map(Holder::value).toList())
                        .orElseGet(ArrayList::new);
            }
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<AP> collectPropertiesFromBlocks(Collection<Block> blocks) {
        if (blocks.isEmpty()) return new ArrayList<>();
        Set<Property<?>> properties =
                new LinkedHashSet<>(blocks.stream().findFirst().get().getStateDefinition().getProperties());
        for (Block block : blocks) {
            LinkedHashSet<Property<?>> properties1 = new LinkedHashSet<>(block.getStateDefinition().getProperties());
            properties.retainAll(properties1);
        }
        return properties.stream().map(
                p -> new AP(p.getName(), p.getName()))
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

    public record AP(Object resourceLocation, String text, boolean isTag) {
        public AP(Object resourceLocation, String text) {
            this(resourceLocation, text, false);
        }

        public String getText() {
            return isTag ? "#" + resourceLocation.toString() : resourceLocation.toString();
        }

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

    @FunctionalInterface
    public interface SimpleOnTabSelectedListener extends TabLayout.OnTabSelectedListener {
        @Override
        default void onTabSelected(TabLayout.@NotNull Tab tab) {
            on(tab);
        }

        void on(TabLayout.Tab tab);
    }
}
