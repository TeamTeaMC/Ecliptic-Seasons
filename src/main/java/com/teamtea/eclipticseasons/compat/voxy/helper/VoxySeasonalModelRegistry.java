package com.teamtea.eclipticseasons.compat.voxy.helper;

import com.teamtea.eclipticseasons.client.lod.SeasonalModelEntry;
import com.teamtea.eclipticseasons.client.lod.SeasonalModelKey;
import com.teamtea.eclipticseasons.compat.voxy.VoxyTool;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.cortex.voxy.common.world.other.Mapper;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class VoxySeasonalModelRegistry {

    public static final int FIRST_SEASONAL_ID = 0x80000;

    private static final Object2IntOpenHashMap<SeasonalModelKey> KEY_TO_ID = new Object2IntOpenHashMap<>();

    private static final Int2ObjectOpenHashMap<SeasonalModelEntry> ID_TO_ENTRY = new Int2ObjectOpenHashMap<>();

    private static int nextId = FIRST_SEASONAL_ID;

    static {
        KEY_TO_ID.defaultReturnValue(-1);
    }

    public static synchronized int getOrCreate(Mapper mapper, int originalBlockId, Identifier modelIdentifier, boolean snowy) {
        SeasonalModelKey key = new SeasonalModelKey(originalBlockId, modelIdentifier, snowy);

        int existing = KEY_TO_ID.getInt(key);
        if (existing >= 0) {
            return existing;
        }

        /*
         * 保证不碰到顶部向下增长的旧覆雪 ID 区间。
         */
        int firstSnowyId = VoxyTool.MAX_VOXY_BLOCK_ID - mapper.getBlockStateCount();

        if (nextId >= firstSnowyId) {
            return originalBlockId;
        }

        int id = nextId++;

        KEY_TO_ID.put(key, id);
        ID_TO_ENTRY.put(id, new SeasonalModelEntry(originalBlockId, modelIdentifier, snowy));

        return id;
    }

    @Nullable
    public static synchronized SeasonalModelEntry get(int id) {
        return ID_TO_ENTRY.get(id);
    }

    public static synchronized int resolveOriginalId(int id) {
        SeasonalModelEntry entry = ID_TO_ENTRY.get(id);
        return entry == null ? id : entry.originalBlockId();
    }

    public static synchronized void clear() {
        KEY_TO_ID.clear();
        ID_TO_ENTRY.clear();
        nextId = FIRST_SEASONAL_ID;
    }

}
