package com.teamtea.eclipticseasons.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;

public record SolarTermsRecord(ArrayList<SolarTerm> solarTerm) {
    public static final int size = SolarTerm.collectValues().length - 1;
    public static final Codec<SolarTermsRecord> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    solarHolderInstance ->
                            solarHolderInstance.group(Codec.INT.sizeLimitedListOf(size)
                                            .fieldOf("solar_terms").forGetter(solarHolder -> solarHolder
                                                    .solarTerm()
                                                    .stream()
                                                    .map(Enum::ordinal)
                                                    .toList()
                                            )

                                    )
                                    .apply(solarHolderInstance, ss ->
                                            new SolarTermsRecord(new ArrayList<>(ss.stream().map(i -> SolarTerm.collectValues()[i])
                                                    .toList()))
                                    )
            )
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SolarTermsRecord> STREAM_CODEC = new StreamCodec<>() {
        public SolarTermsRecord decode(RegistryFriendlyByteBuf byteBuf) {
            var intlist = byteBuf.readIntIdList();
            return new SolarTermsRecord(new ArrayList<>(intlist.stream().map(i -> SolarTerm.collectValues()[i])
                    .toList()));
        }

        public void encode(RegistryFriendlyByteBuf byteBuf, SolarTermsRecord solarHolder) {

            var intlist = solarHolder
                    .solarTerm()
                    .stream()
                    .map(Enum::ordinal)
                    .toList();

            byteBuf.writeIntIdList(new IntArrayList(intlist));

        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SolarTermsRecord solarHolder)
            return solarTerm.equals(solarHolder.solarTerm);
        return false;
    }

    @Override
    public int hashCode() {
        return solarTerm.hashCode();
    }
}
