package com.teamtea.eclipticseasons.compat.modernui.base;

import icyllis.modernui.text.Editable;
import icyllis.modernui.text.TextWatcher;

@FunctionalInterface
public interface SimpleBeforeTextChangedListener extends TextWatcher {
    @Override
    default void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    default void afterTextChanged(Editable editable) {
    }

    @Override
    void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2);
}
