package com.teamtea.eclipticseasons.common.core.solar;

import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.common.util.time4jUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

@Deprecated(forRemoval = true)
public class FixedSolarDataManager extends SolarDataManager {

    public FixedSolarDataManager(Level level, CompoundTag nbt) {
        super(level, nbt);
    }

    public FixedSolarDataManager(Level level) {
        super(level);
    }

    @Override
    public SolarTerm getSolarTerm() {
        return SolarTerm.get(getSolarTermIndex());
    }

    @Override
    public int getSolarTermIndex() {
        return time4jUtil.getCurrent().ordinal();
    }


    @Override
    public int getSolarYear() {
        return time4jUtil.getYear();
    }

    @Override
    public int getSolarTermsDay() {
        return (getSolarTermIndex() * getSolarTermLastingDays()) + getSolarTermDaysInPeriod();
    }

    @Override
    public int getSolarTermDaysInPeriod() {
        return (int) (time4jUtil.getSolarTermProgress() * getSolarTermLastingDays());
    }


    @Override
    public boolean isTodayLastDay() {
        return false;
    }

}
