package com.teamtea.eclipticseasons.common.advancement;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;


@AutoRegisterCapability
public class SolarTermsRecordCa implements ICapabilitySerializable<CompoundTag> {

    ArrayList<SolarTerm> solarTerms;

    public SolarTermsRecordCa() {
        solarTerms = new ArrayList<>();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putIntArray("solarTerms", solarTerms.stream()
                .map(Enum::ordinal)
                .mapToInt(Integer::intValue)
                .toArray());
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("solarTerms")) {
            solarTerms = new ArrayList<>(Arrays.stream(nbt.getIntArray("solarTerms"))
                    .mapToObj(i -> SolarTerm.collectValues()[i]).toList());
        } else {
            solarTerms.clear();
        }
    }

    public boolean addAndCheck(SolarTerm st) {
        if (!solarTerms.contains(st)) {
            solarTerms.add(st);
            // return true;
        }
        return solarTerms.size()>=24;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap== SOLAR_TERMS_RECORD_CA_CAPABILITY){
            return LazyOptional.of(()->this).cast();
        }
        return LazyOptional.empty();
    }


    public static final Capability<SolarTermsRecordCa> SOLAR_TERMS_RECORD_CA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
}
