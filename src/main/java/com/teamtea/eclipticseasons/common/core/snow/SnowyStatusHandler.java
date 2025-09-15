package com.teamtea.eclipticseasons.common.core.snow;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.attachment.AttachmentSyncHandler;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class SnowyStatusHandler implements AttachmentSyncHandler<SnowyStatusKeeper> {

    public static final SnowyStatusHandler INSTANCE = new SnowyStatusHandler();

    @Override
    public boolean sendToPlayer(IAttachmentHolder holder, ServerPlayer to) {
        return AttachmentSyncHandler.super.sendToPlayer(holder, to);
    }

    @Override
    public void write(@NotNull RegistryFriendlyByteBuf buf, @NotNull SnowyStatusKeeper attachment, boolean initialSync) {
        if (!EclipticUtil.canSnowyBlockInteract()) return;

        buf.writeBoolean(initialSync);
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

    @Override
    public @Nullable SnowyStatusKeeper read(@NotNull IAttachmentHolder holder, @NotNull RegistryFriendlyByteBuf buf, @Nullable SnowyStatusKeeper previousValue) {
        if (!EclipticUtil.canSnowyBlockInteract()) return previousValue;
        boolean initialSync = buf.readBoolean();
        int size = buf.readVarInt();
        if (previousValue != null) {
            Set<SectionPos> set = new HashSet<>();
            synchronized (previousValue.getPosMap()) {
                for (int i = 0; i < size; i++) {
                    long pos = buf.readLong();
                    int status = buf.readVarInt();
                    previousValue.set(pos, status);
                    set.add(SectionPos.of(BlockPos.of(pos)));
                }
            }
            for (SectionPos sectionPos : set) {
                ClientCon.agent.setChunkDirty(sectionPos);
            }
        } else if (holder instanceof LevelChunk chunk) {
            previousValue = SnowyMapChecker.getSnowyStatusKeeper(chunk);
            synchronized (previousValue.getPosMap()) {
                for (int i = 0; i < size; i++) {
                    long pos = buf.readLong();
                    int status = buf.readVarInt();
                    previousValue.set(pos, status);
                }
            }
        }
        return previousValue;
    }
}
