package com.teamtea.eclipticseasons.client.util;

import com.teamtea.eclipticseasons.common.core.crop.CropGrowthHandler;
import it.unimi.dsi.fastutil.longs.LongBooleanImmutablePair;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ClientExtraUtil {
    public static float modifyHumidity(Level level, BlockPos pos, float original) {
        if (ClientCon.humidityModificationLevel != 0
                && ClientCon.getAgent().getCameraEntity() != null
                && pos.closerThan(ClientCon.getAgent().getCameraEntity().blockPosition(), 2)) {
            long aLong = pos.asLong();
            LongBooleanImmutablePair orDefault = ClientCon.roomCache.getOrDefault(aLong, null);
            if (orDefault == null) {
                float chance = 0;
                for (int i = 0; i < 20; i++) {
                    chance += CropGrowthHandler.isInRoom(level, pos, level.getBlockState(pos), Optional.empty()) ? 1 : 0;
                }
                orDefault = LongBooleanImmutablePair.of(level.getGameTime(), chance > 8);
                ClientCon.roomCache.put(aLong, orDefault);
            }
            if (orDefault.rightBoolean())
                original += (ClientCon.humidityModificationLevel);
        }
        return original;
    }
}
