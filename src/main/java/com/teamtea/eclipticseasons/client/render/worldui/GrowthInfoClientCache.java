package com.teamtea.eclipticseasons.client.render.worldui;

import com.teamtea.eclipticseasons.common.item.info.GrowthInfo;
import com.teamtea.eclipticseasons.common.item.info.GrowthInfoResolver;
import com.teamtea.eclipticseasons.common.network.clientmesage.GrowthInfoQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class GrowthInfoClientCache {

    private static BlockPos lastPos;
    private static BlockState lastState;
    private static long lastResolveGameTime = -1;
    private static GrowthInfo lastInfo;
    private static boolean hasCachedResult;

    private static final long SCAN_INTERVAL_TICKS = 5;

    public static GrowthInfo get(Level level, BlockPos pos, BlockState state) {
        long now = level.getGameTime();

        boolean sameTarget = hasCachedResult
                && pos.equals(lastPos)
                && state == lastState;

        if (sameTarget) {
            return lastInfo;
        }

        if (now - lastResolveGameTime < SCAN_INTERVAL_TICKS) {
            return null;
        }


        ClientPacketDistributor.sendToServer(new GrowthInfoQuery(pos));
        lastInfo = GrowthInfoResolver.resolveAffectedCrop(level, pos, state);

        GrowthInfo info = lastInfo;
        lastPos = pos.immutable();
        lastState = state;
        lastResolveGameTime = now;
        lastInfo = info;
        hasCachedResult = true;

        return info;
    }

    public static void clear() {
        lastPos = null;
        lastState = null;
        lastResolveGameTime = -1;
        lastInfo = null;
        hasCachedResult = false;
    }

    public static void update(GrowthInfo info) {
        lastInfo = info;
    }
}
