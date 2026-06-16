package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.api.util.EclipticUtil;
import com.teamtea.eclipticseasons.common.core.map.NoneSnowArea;
import com.teamtea.eclipticseasons.common.core.snow.SnowyMapChecker;
import com.teamtea.eclipticseasons.common.network.SimpleNetworkHandler;
import com.teamtea.eclipticseasons.common.network.message.EmptyMessage;
import com.teamtea.eclipticseasons.common.network.message.NoneSnowAreaMessage;
import com.teamtea.eclipticseasons.config.ClientConfig;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;

public class SaltWandItem extends Item {
    public SaltWandItem(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        ItemStack itemInHand = context.getItemInHand();
        Player player = context.getPlayer();
        boolean shiftKeyDown = player == null ||
                player.isShiftKeyDown();
        if (level instanceof ServerLevel serverLevel
                && level.isLoaded(clickedPos)) {
            CompoundTag tag = itemInHand.getTag();
            if (tag != null && tag.contains("click_pos")) {
                BlockPos last = BlockPos.of(tag.getLong("click_pos"));
                tag.remove("click_pos");
                doPos(serverLevel, last, clickedPos, itemInHand, player, shiftKeyDown, false);
            } else {
                if (player != null) ((ServerPlayer) player).sendSystemMessage(
                        Component.translatable("info.eclipticseasons.item.sal_wand.select_first", clickedPos.getX(), clickedPos.getY(), clickedPos.getZ()), false
                );
                itemInHand.getOrCreateTag().putLong("click_pos", clickedPos.asLong());
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
                noneSnowArea = levelChunk.getCapability(NoneSnowArea.NONE_SNOW_AREA_CAPABILITY).orElseGet(NoneSnowArea::empty);
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
                // levelChunk.syncData(AttachmentRegistry.NONE_SNOW_AREA);
                List<ServerPlayer> playersInChunk = level.getChunkSource().chunkMap.getPlayers(levelChunk.getPos(), false);
                players.addAll(playersInChunk);
                NoneSnowArea noneSnowArea = map.get(levelChunk.getPos().toLong());
                if (noneSnowArea != null) {
                    SimpleNetworkHandler.send(playersInChunk, new NoneSnowAreaMessage(levelChunk.getPos(), noneSnowArea));
                }
            }
            SimpleNetworkHandler.send(players.stream().toList(), new EmptyMessage());
        }
        chunkPosSet.clear();
        map.clear();
        if (player != null && !silent) ((ServerPlayer) player).sendSystemMessage(
                Component.translatable("info.eclipticseasons.item.sal_wand.apply", count), false
        );
        if (player == null || !player.isCreative())
            stack.setDamageValue(stack.getDamageValue() + count);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> builder, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, builder, pIsAdvanced);
        if (FMLLoader.getDist() != Dist.CLIENT || !ClientConfig.GUI.itemInformation.get()) return;
        builder.add(Component.translatable("tooltip.eclipticseasons.salt_wand.0"));
        builder.add(Component.translatable("tooltip.eclipticseasons.salt_wand.1"));
        builder.add(Component.translatable("tooltip.eclipticseasons.salt_wand.2"));
    }

}
