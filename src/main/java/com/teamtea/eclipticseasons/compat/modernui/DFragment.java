//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.teamtea.eclipticseasons.compat.modernui;

import com.google.gson.JsonObject;
import com.teamtea.eclipticseasons.client.reload.ClientJsonCacheListener;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.compat.modernui.base.BaseF;
import com.teamtea.eclipticseasons.compat.modernui.base.SimpleOnTabSelectedListener;
import com.teamtea.eclipticseasons.compat.modernui.state.SingleEntryState;
import com.teamtea.eclipticseasons.compat.modernui.util.MUIUtil;
import icyllis.modernui.R;
import icyllis.modernui.annotation.NonNull;
import icyllis.modernui.annotation.Nullable;
import icyllis.modernui.graphics.drawable.GradientDrawable;
import icyllis.modernui.text.method.DigitsInputFilter;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.Gravity;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLLoader;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DFragment extends BaseF {


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable DataSet savedInstanceState) {
        LinearLayout layout = new LinearLayout(this.requireContext());
        //layout.setColumnCount(3);
        //layout.setOrientation(0);
        layout.setOrientation(1);
        layout.setGravity(17);


        makeSelectLine(layout, SingleEntryState.builder().id("方块").build(), (s) -> MUIUtil.collectAps(Registries.BLOCK, s.toString()));

        makeSelectLine(layout, SingleEntryState.builder().id("模型").build(), (s) -> MUIUtil.collectModels(ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, s.toString()));
        makeSelectLine(layout, SingleEntryState.builder().id("模型（非顶层）").build(), (s) -> MUIUtil.collectModels(ClientJsonCacheListener.DIRECTORY_MODEL_DEFINITION, s.toString()));
        makeSelectLine(layout, SingleEntryState.builder().id("类别").build(), (s) -> MUIUtil.getSnowyBlockFlag());

        makeCheckBox(layout, SingleEntryState.builder().id("雪可以穿过").build());
        makeTextLine(layout, SingleEntryState.builder().id("检查偏移值").build(), DigitsInputFilter.getInstance((Locale) null));

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

            makeSelectLine(innerLa, SingleEntryState.builder().id("名字").build(), (s) -> MUIUtil.collectPropertiesFromBlocks(MUIUtil.getBlocksFromStr(
                    ((EditText) layout.findViewWithTag("方块")).getText().toString()
            )));

            makeCheckBox(innerLa, SingleEntryState.builder().id("是否反转").build());

            TabLayout tabLayout = new TabLayout(requireContext());

            TabLayout.Tab tab1 = tabLayout.newTab().setText("精准匹配");
            LinearLayout gl1 = new LinearLayout(requireContext());
            gl1.setOrientation(LinearLayout.VERTICAL);
            //gl1.setGravity(Gravity.CENTER);
            //gl1.setPadding(16, 16, 16, 16);

            makeTextLine(gl1, SingleEntryState.builder().id("精确值").build());
            //tab1.setCustomView(gl1);
            tabLayout.addTab(tab1);
            //tabLayout.addView(gl1);
            TabLayout.Tab tab2 = tabLayout.newTab().setText("范围匹配");
            LinearLayout gl2 = new LinearLayout(requireContext());
            gl2.setOrientation(LinearLayout.VERTICAL);
            //gl2.setGravity(Gravity.CENTER);
            //gl2.setPadding(16, 16, 16, 16);

            makeTextLine(gl2, SingleEntryState.builder().id("最大值").build(), DigitsInputFilter.getInstance((Locale) null));
            makeTextLine(gl2, SingleEntryState.builder().id("最小值").build(), DigitsInputFilter.getInstance((Locale) null));
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

        sbutton.setOnClickListener(view -> {
            try {
                // 1. 读取所有 UI 字段
                String blockId = getText("方块");
                String modelTop = getText("模型");
                String modelSub = getText("模型（非顶层）");
                String flagStr = getText("类别");
                String offsetStr = getText("检查偏移值");

                boolean snowPassable = getCheck("雪可以穿过");

                int flag = 0;
                try {
                    flag = Integer.parseInt(flagStr);
                } catch (Exception ignored) {
                }

                int offset = 0;
                try {
                    offset = Integer.parseInt(offsetStr);
                } catch (Exception ignored) {
                }

                // 2. 构造 JSON
                JsonObject root = new JsonObject();
                root.addProperty("blocks", blockId);
                if (!modelTop.isEmpty()) root.addProperty("mid", modelTop);
                if (!modelSub.isEmpty()) root.addProperty("mid2", modelSub);
                root.addProperty("flag", flag);
                root.addProperty("offset", offset);
                root.addProperty("snow_passable", snowPassable);

                // 3. 生成文件名（palegarden:pale_leaves -> palegarden_pale_leaves.json）
                String fileName = blockId.replace(":", "_") + ".json";

                // 4. 写入游戏目录（/config/eclipticseasons/generated/snow_definitions/）
                Path out = FMLLoader.getGamePath().resolve("config/eclipticseasons/generated/snow_definitions");
                Files.createDirectories(out);

                Path file = out.resolve(fileName);
                Files.writeString((Path) file, ClientJsonCacheListener.GSON.toJson(root), StandardCharsets.UTF_8);

                // 5. 提示用户
                ClientCon.agent.getCameraEntity().sendSystemMessage(Component.literal(
                        "✔ 已生成：" + file.toString()
                ));

            } catch (Throwable e) {
                ClientCon.agent.getCameraEntity().sendSystemMessage(Component.literal(
                        "❌ 生成失败：" + e.getMessage()
                ));
                e.printStackTrace();
            }
        });


        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.addView(layout);
        return scrollView;
    }


    @Override
    protected void addToLayout(LinearLayout layout, int width) {

    }


}
