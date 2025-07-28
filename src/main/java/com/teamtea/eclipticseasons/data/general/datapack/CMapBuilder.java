package com.teamtea.eclipticseasons.data.general.datapack;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.RecordBuilder;

import java.util.LinkedHashMap;

public class CMapBuilder<T> extends RecordBuilder.AbstractUniversalBuilder<T, LinkedHashMap<T, T>> {


    public CMapBuilder(DynamicOps<T> ops) {
        super(ops);
    }

    @Override
    protected LinkedHashMap<T, T> initBuilder() {
        return LinkedHashMap.newLinkedHashMap(0);
    }

    @Override
    protected LinkedHashMap<T, T> append(final T key, final T value, final LinkedHashMap<T, T> builder) {
        // if ((key + "").contains("solar_terms")) {
        //     EclipticSeasons.logger(key);
        // }EclipticSeasons.logger(key);
        builder.put(key, value);
        return builder;
    }

    @Override
    protected DataResult<T> build(final LinkedHashMap<T, T> builder, final T prefix) {
        return ops().mergeToMap(prefix, builder);
    }
}
