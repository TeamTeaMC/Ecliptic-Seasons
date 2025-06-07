package com.teamtea.eclipticseasons.mixin.data;

import com.teamtea.eclipticseasons.api.constant.biome.Humidity;
import com.teamtea.eclipticseasons.api.constant.solar.Season;
import com.teamtea.eclipticseasons.api.constant.solar.SolarTerm;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.data.DataProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin({DataProvider.class})
public interface MixinsDataProvider {


    // @Inject(at = {@At("HEAD")}, method = {"lambda$static$0"}, cancellable = true)
    // private static void eclipticseasons$lambda$static$0(Object2IntOpenHashMap intOpenHashMap, CallbackInfo ci) {
    //     for (SolarTerm solarTerm : SolarTerm.collectValues()) {
    //         intOpenHashMap.put(solarTerm.getName(),solarTerm.ordinal()+1000);
    //     }
    //     for (Season season : Season.collectValues()) {
    //         intOpenHashMap.put(season.getName(),season.ordinal()+500);
    //     }
    //
    //     for (Humidity humidity : Humidity.collectValues()) {
    //         intOpenHashMap.put(humidity.getName(),humidity.ordinal()+2000);
    //     }
    //
    //     List<String> strings = List.of(
    //             "solar_terms",
    //             "seasons",
    //             "humidity",
    //             "grow_chance",
    //             "fertile_chance",
    //             "death_chance");
    //     for (int i = 0, stringsSize = strings.size(); i < stringsSize; i++) {
    //         String s = strings.get(i);
    //         intOpenHashMap.put(s, i + 2100);
    //     }
    // }


}
