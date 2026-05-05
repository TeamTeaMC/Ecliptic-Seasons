package com.teamtea.eclipticseasons.common.core.solar.extra;

import com.teamtea.eclipticseasons.api.EclipticSeasonsApi;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import com.teamtea.eclipticseasons.api.data.misc.ESSortInfo;
import com.teamtea.eclipticseasons.api.data.season.SpecialDays;
import com.teamtea.eclipticseasons.common.registry.ESRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

public class SpecialDaysManager {

    public static final Map<SolarTerm, List<Holder<SpecialDays>>> SPECIAL_DAYS = new EnumMap<>(SolarTerm.class);
    public static final Map<SolarTerm, List<Holder<SpecialDays>>> SPECIAL_DAYS_CLIENT = new EnumMap<>(SolarTerm.class);

    public static void init(HolderLookup.Provider registryAccess, boolean isServer) {
        HolderLookup.RegistryLookup<SpecialDays> specialDaysRegistryLookup = registryAccess.lookupOrThrow(ESRegistries.SPECIAL_DAYS);
        List<Holder.Reference<SpecialDays>> specialDays = ESSortInfo.sorted(specialDaysRegistryLookup.listElements().toList());
        Map<SolarTerm, List<Holder<SpecialDays>>> map = isServer ? SPECIAL_DAYS : SPECIAL_DAYS_CLIENT;
        map.clear();
        for (Holder.Reference<SpecialDays> specialDay : specialDays) {
            List<Holder<SpecialDays>> holders = map.computeIfAbsent(specialDay.value().term, (e) -> new ArrayList<>());
            holders.add(specialDay);
        }
        map.forEach((_, holders) -> holders.sort(Comparator.comparing(h -> h.value().getStart())));
    }

    public static void clearOnClientExitOrServerClose(boolean serverCause) {
        if (serverCause) {
            SPECIAL_DAYS.clear();
        } else {
            SPECIAL_DAYS_CLIENT.clear();
        }
    }

    public static List<Holder<SpecialDays>> getSpecialDays(Level level, BlockPos pos) {
        Map<SolarTerm, List<Holder<SpecialDays>>> map = level instanceof ServerLevel ? SPECIAL_DAYS : SPECIAL_DAYS_CLIENT;
        SolarTerm solarTerm = EclipticSeasonsApi.getInstance().getSolarTerm(level);
        List<Holder<SpecialDays>> holders = map.get(solarTerm);
        if (holders == null) return List.of();
        List<Holder<SpecialDays>> sp = new ArrayList<>();
        int timeInTerm = EclipticSeasonsApi.getInstance().getTimeInTerm(level);
        int lastingDaysOfEachTerm = EclipticSeasonsApi.getInstance().getLastingDaysOfEachTerm(level);
        float progress = timeInTerm / (float) lastingDaysOfEachTerm;
        for (Holder<SpecialDays> holder : holders) {
            SpecialDays days = holder.value();
            if (days.lastingDays == 0) {
                if (progress >= days.start && progress <= days.end)
                    sp.add(holder);
            } else {
                if (progress >= days.start && timeInTerm - days.start * lastingDaysOfEachTerm <= days.lastingDays)
                    sp.add(holder);
            }
        }
        return List.copyOf(sp);
    }
}
