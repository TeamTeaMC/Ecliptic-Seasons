package com.teamtea.eclipticseasons.api.misc.client;

import com.teamtea.eclipticseasons.common.core.map.BiomeHolder;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import com.teamtea.eclipticseasons.common.core.snow.SnowyStatusKeeper;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.ApiStatus;

public interface ISnowyGetter extends ISnowyGetterProvider {
    SnowyRemover getSnowyRemover();

    BiomeHolder getBiomeHolder();

    SnowyStatusKeeper getSnowyStatusKeeper();
}
