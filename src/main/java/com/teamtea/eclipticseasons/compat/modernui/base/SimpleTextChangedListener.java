package com.teamtea.eclipticseasons.compat.modernui.base;

import icyllis.modernui.text.Editable;
import icyllis.modernui.text.TextWatcher;

@FunctionalInterface
public interface SimpleTextChangedListener extends TextWatcher {
    @Override
    void onTextChanged(CharSequence charSequence, int i, int i1, int i2);

    @Override
    default void afterTextChanged(Editable editable) {
    }

    @Override
    default void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }
}
