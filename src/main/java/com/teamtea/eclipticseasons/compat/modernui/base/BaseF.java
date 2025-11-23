package com.teamtea.eclipticseasons.compat.modernui.base;

import com.teamtea.eclipticseasons.compat.modernui.state.AP;
import com.teamtea.eclipticseasons.compat.modernui.state.SingleEntryState;
import icyllis.arc3d.core.Color;
import icyllis.modernui.fragment.Fragment;
import icyllis.modernui.graphics.drawable.ColorDrawable;
import icyllis.modernui.mc.ui.ThemeControl;
import icyllis.modernui.text.InputFilter;
import icyllis.modernui.util.DataSet;
import icyllis.modernui.view.LayoutInflater;
import icyllis.modernui.view.View;
import icyllis.modernui.view.ViewGroup;
import icyllis.modernui.widget.*;

import java.util.*;
import java.util.function.Function;

public abstract class BaseF extends Fragment {


    protected Map<View, Boolean> onSet = new IdentityHashMap<>();

    public void on(View view) {
        onSet.put(view, true);
        //onSet = true;
    }

    public void off(View view) {
        onSet.put(view, false);
        //onSet = false;
    }

    public boolean isOn(View view) {
        return onSet.getOrDefault(view, true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onSet.clear();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, DataSet savedInstanceState) {
        LinearLayout layout = new LinearLayout(this.requireContext());
        layout.setOrientation(1);
        layout.setGravity(17);
        ScrollView scrollView = new ScrollView(requireContext());
        scrollView.addView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //if (container != null)
        //    container.addView(scrollView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //else container = scrollView;
        container = scrollView;
        addToLayout(layout, container.getWidth());
        return container;
    }

    abstract protected void addToLayout(LinearLayout layout, int width);


    protected String getText(String label) {
        EditText et = getView().findViewWithTag(label);
        return et == null ? "" : et.getText().toString().trim();
    }

    protected boolean getCheck(String label) {
        CheckBox cb = getView().findViewWithTag(label);
        return cb != null && cb.isChecked();
    }


    protected void makeTextLine(ViewGroup layout, SingleEntryState label) {
        makeTextLine(layout, label, null);
    }

    protected void makeTextLine(ViewGroup layout, SingleEntryState label, InputFilter justNumber) {
        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setDividerDrawable(ThemeControl.makeDivider(linearLayout, true));
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView pre = new TextView(this.requireContext());
        linearLayout.addView(pre, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pre.setText(label.getDisplayName());
        EditText input = new EditText(this.requireContext());
        if (justNumber != null) input.setFilters(justNumber);
        input.setTag(label);
        input.setBackground(new ColorDrawable(Color.GRAY));
        //input.setPadding(20,0,10,20);
        linearLayout.addView(input, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    protected void makeCheckBox(ViewGroup layout, SingleEntryState label) {
        LinearLayout linearLayout = new LinearLayout(requireContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView pre = new TextView(this.requireContext());
        linearLayout.addView(pre, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        pre.setText(label.getDisplayName());

        CheckBox input = new CheckBox(this.requireContext());
        input.setTag(label);
        linearLayout.addView(input, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        layout.addView(linearLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    protected void makeSelectLine(ViewGroup layout, SingleEntryState label, Function<Object, List<AP>> apFunction) {
        LinearLayout gridLayout = new LinearLayout(requireContext());
        //gridLayout.setColumnCount(3);
        gridLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView pre = new TextView(this.requireContext());
        gridLayout.addView(pre, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        pre.setText(label.getDisplayName());

        EditText editText = new EditText(this.requireContext());
        editText.setBackground(new ColorDrawable(Color.GRAY));
        editText.setMinimumWidth(400);
        editText.setTag(label);
        gridLayout.addView(editText, new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        Spinner spinner = new Spinner(this.requireContext());
        ArrayAdapter<AP> apArrayAdapter = setSpinner(spinner, "", apFunction);

        gridLayout.addView(spinner, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        //spinner.setOnItemClickListener((adapterView, view, i, l) -> {
        //    if (adapterView.getSelectedItem() instanceof AP ap) {
        //        editText.setText(ap.getText());
        //    }
        //});

        spinner.setOnItemSelectedListener((adapterView, view, i, l) -> {
            if (adapterView.getSelectedItem() instanceof AP ap) {
                if (isOn(spinner)) {
                    off(spinner);
                } else {
                    editText.setText(ap.getText());
                    on(spinner);
                }
            }
        });

        editText.addTextChangedListener(
                (SimpleTextChangedListener) (charSequence, i, i1, i2) -> {
                    if (charSequence.toString().endsWith("\n")
                            && spinner.getAdapter().getCount() > 0
                            && spinner.getAdapter().getItem(0) instanceof AP ap) {
                        charSequence = ap.getText();
                        setSpinner(spinner, charSequence, apFunction);
                        editText.setText(charSequence);
                        on(spinner);
                        return;
                    }

                    if (isOn(spinner)) {
                        off(spinner);
                    } else {
                        //resetSpinner(apArrayAdapter, charSequence, apFunction);
                        setSpinner(spinner, charSequence, apFunction);
                        on(spinner);
                    }
                }
        );


        layout.addView(gridLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

    }

    protected ArrayAdapter<AP> setSpinner(Spinner spinner, Object editable, Function<Object, List<AP>> apFunction) {
        List<AP> apply = apFunction.apply(editable);
        if (apply.size() == 1) apply.addLast(new AP("", "--"));
        ArrayAdapter<AP> adapter = new ArrayAdapter<>(requireContext(), apply);
        spinner.setAdapter(adapter);
        return adapter;
    }

    protected ArrayAdapter<AP> resetSpinner(ArrayAdapter<AP> adapter, Object editable, Function<Object, List<AP>> apFunction) {
        adapter.clear();
        List<AP> apply = apFunction.apply(editable);
        if (apply.size() == 1) apply.addLast(new AP("", "--"));
        adapter.addAll(apply);
        return adapter;
    }
}
