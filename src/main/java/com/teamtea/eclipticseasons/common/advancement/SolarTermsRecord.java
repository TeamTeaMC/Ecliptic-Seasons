package com.teamtea.eclipticseasons.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.stream.Collectors;

public record SolarTermsRecord(Object2IntLinkedOpenHashMap<SolarTerm> solarTerms) {
    public static final int size = SolarTerm.collectValues().length;

    public static final MapCodec<SolarTermsRecord> CODEC = RecordCodecBuilder.mapCodec(
            solarHolderInstance ->
                    solarHolderInstance.group(Codec.INT.sizeLimitedListOf(size)
                                            .fieldOf("solar_terms").forGetter(solarHolder -> solarHolder
                                                    .solarTerms()
                                                    .keySet()
                                                    .stream()
                                                    .map(Enum::ordinal)
                                                    .toList()
                                            ),
                                    Codec.INT.sizeLimitedListOf(size)
                                            .optionalFieldOf("solar_terms_counter").forGetter(solarHolder ->
                                                    Optional.of(solarHolder
                                                            .solarTerms()
                                                            .values()
                                                            .stream().toList())
                                            )
                            )
                            .apply(solarHolderInstance, (intArray, counter) ->
                                    {
                                        final Object2IntLinkedOpenHashMap<SolarTerm> solarTerms = new Object2IntLinkedOpenHashMap<>();

                                        for (int i = 0, intArrayLength = intArray.size(); i < intArrayLength; i++) {
                                            int id = intArray.get(i);
                                            int finalI = i;
                                            solarTerms.put(
                                                    SolarTerm.collectValues()[id], (int) counter.map(c -> c.get(finalI)).orElse(1)
                                            );
                                        }
                                        return new SolarTermsRecord(solarTerms);
                                    }
                            )
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, SolarTermsRecord> STREAM_CODEC = new StreamCodec<>() {
        public SolarTermsRecord decode(RegistryFriendlyByteBuf byteBuf) {
            var intArray = byteBuf.readIntIdList();
            var counter = byteBuf.readIntIdList();

            final Object2IntLinkedOpenHashMap<SolarTerm> solarTerms = new Object2IntLinkedOpenHashMap<>();

            for (int i = 0, intArrayLength = intArray.size(); i < intArrayLength; i++) {
                int id = intArray.getInt(i);
                solarTerms.put(
                        SolarTerm.collectValues()[id], counter.getInt(i)
                );
            }
            return new SolarTermsRecord(solarTerms);
        }

        public void encode(RegistryFriendlyByteBuf byteBuf, SolarTermsRecord solarHolder) {

            byteBuf.writeIntIdList(solarHolder.solarTerms
                    .keySet().stream().map(Enum::ordinal)
                    .collect(Collectors.toCollection(IntArrayList::new))
            );

            byteBuf.writeIntIdList(solarHolder.solarTerms
                    .values().stream()
                    .collect(Collectors.toCollection(IntArrayList::new))
            );

        }
    };

    public boolean addAndCheck(SolarTerm st) {
        solarTerms.addTo(st, 1);
        return solarTerms.size() < 24;
    }
}
