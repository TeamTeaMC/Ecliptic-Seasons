package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.item.attachment.ClickPos;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.registry.AttachmentRegistry;
import com.teamtea.eclipticseasons.common.registry.DataComponentTypeRegistry;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class SaltWandItem extends Item {
    public SaltWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        boolean shiftKeyDown = player == null ||
                player.isShiftKeyDown();
        if (level instanceof ServerLevel serverLevel
                && level.isLoaded(clickedPos)) {
            if (itemInHand.has(DataComponentTypeRegistry.CLICK_POS)) {
                BlockPos last = itemInHand.get(DataComponentTypeRegistry.CLICK_POS).last();
                itemInHand.remove(DataComponentTypeRegistry.CLICK_POS);
                doPos(serverLevel, last, clickedPos, itemInHand, player, shiftKeyDown, false);
            } else {
                if (player != null) player.displayClientMessage(
                        Component.translatable("info.eclipticseasons.item.sal_wand.select_first", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), false
                );
                itemInHand.set(DataComponentTypeRegistry.CLICK_POS, new ClickPos(clickedPos));
            }
        }
        return super.useOn(context);
    }

    private static void doPos(ServerLevel level, BlockPos last, BlockPos clickedPos, ItemStack stack, @Nullable Player player, boolean shiftKeyDown, boolean silent) {
        Long2ObjectOpenHashMap<NoneSnowArea> map = new Long2ObjectOpenHashMap<>();
        HashSet<LevelChunk> chunkPosSet = new HashSet<>();
        int count = 0;
        for (BlockPos blockPos : BlockPos.betweenClosed(last, clickedPos)) {
            count++;
            if (EclipticUtil.canSnowyBlockInteract()) {
                SnowyMapChecker.removeSnowyStatus(level, blockPos);
                continue;
            }

            ChunkPos chunkPos = new ChunkPos(blockPos);
            long aLong = chunkPos.toLong();
            NoneSnowArea noneSnowArea;
            if (map.containsKey(aLong)) noneSnowArea = map.get(aLong);
            else {
                int x = chunkPos.x, z = chunkPos.z;
                LevelChunk levelChunk = level.getChunk(x, z);
                noneSnowArea = levelChunk.getData(AttachmentRegistry.NONE_SNOW_AREA);
                map.put(aLong, noneSnowArea);
                chunkPosSet.add(levelChunk);
            }
            if (!shiftKeyDown) noneSnowArea.add(blockPos);
            else noneSnowArea.remove(blockPos);
        }

        if (!EclipticUtil.canSnowyBlockInteract()) {
            HashSet<ServerPlayer> players = new HashSet<>();
            for (LevelChunk levelChunk : chunkPosSet) {
                levelChunk.setUnsaved(true);
                levelChunk.syncData(AttachmentRegistry.NONE_SNOW_AREA);
                players.addAll(level.getChunkSource().chunkMap.getPlayers(levelChunk.getPos(), false));
            }
            SimpleNetworkHandler.send(players, new EmptyMessage());
        }
        chunkPosSet.clear();
        map.clear();
        if (player != null && !silent) player.displayClientMessage(
                Component.translatable("info.eclipticseasons.item.sal_wand.apply", count), false
        );
        if (player == null || !player.isCreative())
            stack.setDamageValue(stack.getDamageValue() + count);
    }


    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);
        if (entity instanceof ServerPlayer serverPlayer
                && level instanceof ServerLevel serverLevel) {
            doPos(serverLevel, serverPlayer.getOnPos(), serverPlayer.getOnPos().above(2), stack, serverPlayer, serverPlayer.isShiftKeyDown(), true);
        }
    }
}
