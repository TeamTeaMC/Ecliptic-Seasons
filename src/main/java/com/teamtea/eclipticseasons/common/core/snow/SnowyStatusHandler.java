package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;


public class SnowyStatusHandler {

    public final boolean initialSync;
    public final SnowyStatusKeeper attachment;
    public final ChunkPos chunkPos;

    public SnowyStatusHandler(boolean initialSync, SnowyStatusKeeper attachment, ChunkPos chunkPos) {
        this.initialSync = initialSync;
        this.attachment = attachment;
        this.chunkPos = chunkPos;
    }

    public SnowyStatusHandler(FriendlyByteBuf buf) {
        if (!EclipticUtil.canSnowyBlockInteract()) {
            this.initialSync = false;
            this.chunkPos = new ChunkPos(0, 0);
            this.attachment = SnowyStatusKeeper.create();
            return;
        }

        this.initialSync = buf.readBoolean();
        this.chunkPos = buf.readChunkPos();
        attachment = SnowyStatusKeeper.create();


        int size = 0;
        try {
            size = buf.readVarInt();
            if (!initialSync) {
                for (int i = 0; i < size; i++) {
                    long pos = buf.readLong();
                    int status = buf.readVarInt();
                    // 1.20 patch for copy
                    attachment.getPosListUpdate().add(pos);
                    attachment.getStatusListUpdate().add(status);
                }
            } else {
                for (int i = 0; i < size; i++) {
                    long pos = buf.readLong();
                    int status = buf.readVarInt();
                    attachment.set(pos, status);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void write(@NotNull FriendlyByteBuf buf) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;
        buf.writeBoolean(initialSync);
        buf.writeChunkPos(chunkPos);
        if (initialSync) {
            int size = attachment.getPosMap().size();
            buf.writeVarInt(size);
            attachment.getPosMap().forEach(
                    (k, v) -> {
                        buf.writeLong(k);
                        buf.writeVarInt(v);
                    }
            );
        } else {
            LongArrayList blockPosList = attachment.getPosListUpdate();
            IntArrayList integerList = attachment.getStatusListUpdate();
            int size = blockPosList.size();
            buf.writeVarInt(size);
            for (int i = 0; i < size; i++) {
                buf.writeLong(blockPosList.getLong(i));
                buf.writeVarInt(integerList.getInt(i));
            }
        }
    }

    public static final Map<ChunkPos, SnowyStatusHandler> SNOWY_STATUS_HANDLER_HASH_MAP = new HashMap<>();

    public static boolean processSnowyStatusMessage(SnowyStatusHandler msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() ->
        {
            if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
                Level useLevel = ClientCon.getUseLevel();
                if (useLevel != null) {
                    LevelChunk chunkAt = useLevel.getChunkAt(msg.chunkPos.getWorldPosition());
                    if (!(chunkAt.isEmpty())) setChunkWithUpdate(msg, chunkAt);
                    else SNOWY_STATUS_HANDLER_HASH_MAP.put(msg.chunkPos, msg);
                }
            }
        });
        return true;
    }

    public static void setChunkWithUpdate(SnowyStatusHandler msg, LevelChunk chunkAt) {
        {
            SnowyStatusKeeper snowyStatusKeeper = SnowyMapChecker.getSnowyStatusKeeper(chunkAt);
            if (msg.initialSync) {
                synchronized (snowyStatusKeeper.getPosMap()) {
                    snowyStatusKeeper.copyFrom(msg.attachment);
                }
            } else {
                LongArrayList posListUpdate = msg.attachment.getPosListUpdate();
                Set<SectionPos> set = new HashSet<>();
                synchronized (snowyStatusKeeper.getPosMap()) {
                    for (int i = 0, posListUpdateSize = posListUpdate.size(); i < posListUpdateSize; i++) {
                        long pos = posListUpdate.getLong(i);
                        snowyStatusKeeper.set(pos, msg.attachment.getStatusListUpdate().getInt(i));
                        set.add(SectionPos.of(BlockPos.of(pos)));
                    }
                }
                for (SectionPos sectionPos : set) {
                    ClientCon.agent.setChunkDirty(sectionPos);
                }
            }
        }
    }
}
