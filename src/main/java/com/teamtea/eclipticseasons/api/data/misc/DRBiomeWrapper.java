package com.teamtea.eclipticseasons.api.data.misc;

import com.google.gson.JsonElement;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.FieldDecoder;
import com.mojang.serialization.codecs.FieldEncoder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.util.codec.CodecTranferUtil;
import lombok.Data;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Data
public class DRBiomeWrapper<T> {
    // public static final Codec<HolderSet<Biome>> BIOME_HOLDER_SET_CODEC = CodecUtil.holderSetCodec(Registries.BIOME);
    // public static final Codec<DRBiomeWrapper<?>> CODEC = RecordCodecBuilder.create(ins -> ins.group(
    //         ExtraCodecs.JSON.optionalFieldOf("element").forGetter(DRBiomeWrapper::getElement)
    // ).apply(ins, DRBiomeWrapper::new));


    private final Optional<JsonElement> element;
    private Optional<T> value;

    public DRBiomeWrapper(Optional<JsonElement> jsonElement) {
        this.element = jsonElement;
        value = Optional.empty();
    }

    public DRBiomeWrapper(Optional<T> t, Void v) {
        this(Optional.empty());
        setValue(t);
    }

    public static <T> DRBiomeWrapper<T> of(T t) {
        return new DRBiomeWrapper<>(Optional.ofNullable(t), null);
    }

    public static <T> Codec<DRBiomeWrapper<T>> codec() {
        return ExtraCodecs.JSON.xmap(
                c -> new DRBiomeWrapper<T>(Optional.ofNullable(c)),
                c -> c.getElement().orElse(null)
        );
    }

    public static <T> Codec<DRBiomeWrapper<T>> codec2(Codec<T> codecT) {
        return codecT.xmap(
                DRBiomeWrapper::of,
                c -> c.getValue().orElse(null)
        );
    }

    public static <T> Codec<DRBiomeWrapper<T>> mapCodec(Codec<T> codecT) {
        // return MapCodec.of(
        //         new FieldEncoder<>("element", codec2(codecT)),
        //         new FieldDecoder<>("element", codec())
        // ).codec();
        return Codec.of(codec2(codecT), codec());
    }


    public void set(HolderLookup.Provider registries, Codec<T> codec) {
        element.ifPresent(jsonElement -> CodecTranferUtil.decode(registries, JsonOps.INSTANCE, codec, jsonElement,
                biomeHolderSet -> {
                    setValue(Optional.of(biomeHolderSet));
                }));
    }

    public DRBiomeWrapper<T> build(HolderLookup.Provider registries, Codec<T> codec) {
        AtomicReference<DRBiomeWrapper<T>> drBiomeWrapper = new AtomicReference<>();
        value.ifPresent(biomeHolderSet -> {
            CodecTranferUtil.encode(registries, JsonOps.INSTANCE, codec, biomeHolderSet, jsonElement ->
                    drBiomeWrapper.set(new DRBiomeWrapper<>(Optional.ofNullable(jsonElement))));
        });
        return drBiomeWrapper.get();
    }

}
