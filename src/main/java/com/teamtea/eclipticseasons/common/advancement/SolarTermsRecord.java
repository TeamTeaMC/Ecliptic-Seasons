package com.teamtea.eclipticseasons.common.advancement;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@AutoRegisterCapability
public class SolarTermsRecord implements ICapabilitySerializable<CompoundTag> {

    final Object2IntLinkedOpenHashMap<SolarTerm> solarTerms = new Object2IntLinkedOpenHashMap<>();

    public SolarTermsRecord() {
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putIntArray("solar_terms", solarTerms.keySet().stream()
                .map(Enum::ordinal)
                .mapToInt(Integer::intValue)
                .toArray());
        compoundTag.putIntArray("solar_terms_counter", solarTerms.values()
                .toIntArray());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        solarTerms.clear();
        if (nbt.contains("solar_terms")) {
            int[] solarTermsCounters =
                    nbt.contains("solar_terms_counter") ?
                            nbt.getIntArray("solar_terms_counter") : null;
            int[] intArray = nbt.getIntArray("solar_terms");
            for (int i = 0, intArrayLength = intArray.length; i < intArrayLength; i++) {
                int id = intArray[i];
                solarTerms.put(
                        SolarTerm.collectValues()[id], solarTermsCounters == null ? 1 : solarTermsCounters[i]
                );
            }
        }
    }

    public boolean addAndCheck(SolarTerm st) {
        solarTerms.addTo(st, 1);
        return solarTerms.size() < 24;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == SOLAR_TERMS_RECORD_CA_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }


    public static final Capability<SolarTermsRecord> SOLAR_TERMS_RECORD_CA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
}
