package com.teamtea.eclipticseasons.common.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;

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
            var intArray = byteBuf.readVarIntArray();
            var counter = byteBuf.readVarIntArray();

            final Object2IntLinkedOpenHashMap<SolarTerm> solarTerms = new Object2IntLinkedOpenHashMap<>();

            for (int i = 0, intArrayLength = intArray.length; i < intArrayLength; i++) {
                int id = intArray[i];
                solarTerms.put(
                        SolarTerm.collectValues()[id], counter[i]
                );
            }
            return new SolarTermsRecord(solarTerms);
        }

        public void encode(RegistryFriendlyByteBuf byteBuf, SolarTermsRecord solarHolder) {

            byteBuf.writeVarIntArray(solarHolder.solarTerms
                    .keySet().stream().map(Enum::ordinal)
                            .mapToInt(Integer::intValue)
                            .toArray()
                    // .collect(Collectors.toCollection(IntArrayList::new))
            );

            byteBuf.writeVarIntArray(solarHolder.solarTerms
                    .values()
                    .intStream()
                    // .mapToInt(Integer::intValue)
                    .toArray()
            );

        }
    };

    public boolean addAndCheck(SolarTerm st) {
        solarTerms.addTo(st, 1);
        return solarTerms.size() < 24;
    }


    // @Override
    // public void readData(ValueInput valueInput) {
    //     var snowyStatus = valueInput.read(CODEC);
    //     this.solarTerms.putAll(snowyStatus.map(SolarTermsRecord::solarTerms).orElse(new Object2IntLinkedOpenHashMap<>()));
    // }
    //
    // @Override
    // public void writeData(ValueOutput valueOutput) {
    //     valueOutput.store(CODEC, this);
    // }

    public static SolarTermsRecord empty() {
        return new SolarTermsRecord(new Object2IntLinkedOpenHashMap<>());
    }
}
