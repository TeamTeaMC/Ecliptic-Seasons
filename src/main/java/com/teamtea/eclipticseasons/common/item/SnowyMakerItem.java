package com.teamtea.eclipticseasons.common.item;

import com.teamtea.eclipticseasons.EclipticSeasons;
import com.teamtea.eclipticseasons.client.util.SimplePair;
import com.teamtea.eclipticseasons.common.core.map.MapChecker;
import com.teamtea.eclipticseasons.common.core.map.ServerMapFixer;
import com.teamtea.eclipticseasons.common.core.map.SnowyRemover;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class SnowyMakerItem extends Item {
    public SnowyMakerItem(Properties pProperties) {
        super(pProperties);
    }

    public enum MakerMode {BLOCK, CHUNK;}

    private static List<SimplePair<MakerMode, Integer>> preModels =
            List.of(SimplePair.of(MakerMode.BLOCK, 1),
                    SimplePair.of(MakerMode.BLOCK, 3),
                    SimplePair.of(MakerMode.BLOCK, 5),
                    SimplePair.of(MakerMode.CHUNK, 1),
                    SimplePair.of(MakerMode.CHUNK, 3),
                    SimplePair.of(MakerMode.CHUNK, 5),
                    SimplePair.of(MakerMode.CHUNK, 7));


    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        ItemStack itemInHand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemInHand.is(this)) {
            CustomData customData = itemInHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag unsafe = customData.getUnsafe();

            String makerModeString = unsafe.getString("range");
            String[] split = makerModeString.split("-");

            MakerMode makerMode = MakerMode.BLOCK;
            int range = 1;
            if (split.length == 2) {
                makerMode = MakerMode.valueOf(split[0]);
                range = Integer.parseInt(split[1]);
            }
            SimplePair<MakerMode, Integer> simplePair = SimplePair.of(makerMode, range);
            int mode = unsafe.getInt("mode");
            if (!pPlayer.isShiftKeyDown()) {
                int index = preModels.indexOf(simplePair);
                int nextIndex = index > -1 && index < preModels.size() - 1 ? (index + 1) : 0;
                simplePair = preModels.get(nextIndex);
            } else {
                mode = Mth.abs(mode - 1);
            }

            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putInt("mode", mode);
            compoundTag.putString("range", "%s-%s".formatted(simplePair.getKey(), simplePair.getValue()));
            itemInHand.set(DataComponents.CUSTOM_DATA, CustomData.of(compoundTag));

            MutableComponent mutableComponent = Component.literal("%s %sx%s, ".formatted(simplePair.getKey(),
                    simplePair.getValue(), simplePair.getValue()));
            mutableComponent.append(mode == 0 ? "None" : "Snowy");

            if (pPlayer instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendSystemMessage(mutableComponent, true);
            }

        }
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {

        if (pContext.getHand() == InteractionHand.MAIN_HAND) {
            var level = pContext.getLevel();
            Player contextPlayer = pContext.getPlayer();
            var clickedPos = pContext.getClickedPos();
            ItemStack itemInHand = pContext.getItemInHand();


            CustomData customData = itemInHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
            CompoundTag unsafe = customData.getUnsafe();
            int type = unsafe.getInt("mode") == 0 ?
                    SnowyRemover.NONE_SNOWY : SnowyRemover.SNOWY;
            String makerModeString = unsafe.getString("range");
            String[] split = makerModeString.split("-");

            MakerMode makerMode = MakerMode.BLOCK;
            int range = 1;
            if (split.length == 2) {
                makerMode = MakerMode.valueOf(split[0]);
                range = Integer.parseInt(split[1]);
            }
            if (makerMode == MakerMode.CHUNK) {
                Stream<ChunkPos> chunkPosStream = ChunkPos.rangeClosed(new ChunkPos(clickedPos), (range - 1) / 2);
                chunkPosStream.forEach(
                        chunkPos -> modifySnowyBlocks(level, contextPlayer, chunkPos, type)
                );
            } else {
                int half = (range - 1) / 2;
                for (int i = -half; i <= half; i++) {
                    for (int j = -half; j <= half; j++) {
                        modifySnowyBlocks(level, contextPlayer, clickedPos.offset(
                                i,0,j
                        ), type);
                    }
                }

            }
            ;
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(pContext);
    }

    private InteractionResult modifySnowyBlocks(Level level, Player contextPlayer, ChunkPos chunkPos, int type) {
        ServerMapFixer.unloadChunk(level, chunkPos);
        BlockPos worldPosition = chunkPos.getMiddleBlockPosition(MapChecker.getMCHeightWithCheck(level, new BlockPos(chunkPos.getMiddleBlockX(), 0, chunkPos.getMiddleBlockZ())));
        if (level.isLoaded(chunkPos.getWorldPosition())
                && (contextPlayer == null
                || (level.mayInteract(contextPlayer, worldPosition)
                // 这个东西也许应该冒险模式可以用
                // && pContext.getPlayer().mayUseItemAt(chunkPos, pContext.getClickedFace(), pContext.getItemInHand())
        )
        )
        ) {
            if (level instanceof ServerLevel serverLevel) {
                var chunk = serverLevel.getChunk(chunkPos.x, chunkPos.z);
                if (!chunk.hasData(EclipticSeasons.ModContents.SNOWY_REMOVER)) {
                    chunk.setData(EclipticSeasons.ModContents.SNOWY_REMOVER, new SnowyRemover(new int[16][16]));
                }
                var data = chunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER);


                int[][] ints1 = new int[16][16];
                for (int[] ints : ints1) {
                    Arrays.fill(ints, type);
                }
                data = new SnowyRemover(ints1);
                chunk.setData(EclipticSeasons.ModContents.SNOWY_REMOVER, data);

                // var distance =
                //         (serverLevel.getServer() instanceof DedicatedServer dedicatedServer ?
                //                 dedicatedServer.getProperties().viewDistance :
                //                 Minecraft.getInstance().options.renderDistance().get())
                //                 * 16;
                // var players = serverLevel.getPlayers(
                //         serverPlayer -> {
                //             var onPos = serverPlayer.getOnPos();
                //             return onPos.distToCenterSqr(worldPosition.getCenter()) < distance;
                //         }
                // );

                List<Integer> ys = new ArrayList<>();
                List<BlockPos> blockPoss = new ArrayList<>();
                for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                    for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                        int k = level.getHeight(Heightmap.Types.MOTION_BLOCKING, i, j) - 1;
                        BlockPos newPos = new BlockPos(i, k, j);
                        int sk = SectionPos.of(newPos).y();
                        if (!ys.contains(sk)) ys.add(sk);
                        blockPoss.add(newPos);
                    }
                }

                for (ServerPlayer player : serverLevel.players()) {
                    MapChecker.sendChunkInfo(chunk, chunkPos, player, ys, blockPoss);
                }

                if (data.allSnowAble()) {
                    chunk.removeData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                }
                chunk.setUnsaved(true);

            } else {

                var data = level.getChunk(chunkPos.x, chunkPos.z).getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                for (int i = chunkPos.getMinBlockX(); i <= chunkPos.getMaxBlockX(); i++) {
                    for (int j = chunkPos.getMinBlockZ(); j <= chunkPos.getMaxBlockZ(); j++) {
                        boolean snowy = type == SnowyRemover.NONE_SNOWY;
                        boolean notSnowyAtBefore = data.notSnowyAt(new BlockPos(i, 64, j));
                        if (snowy != notSnowyAtBefore) {
                            var particleType = !snowy
                                    ? ParticleTypes.SMOKE : ParticleTypes.SNOWFLAKE;
                            for (int k = 0; k < 10; k++) {
                                level.addParticle(
                                        particleType,
                                        i + 0.5f,
                                        MapChecker.getHeight(level, new BlockPos(i, 0, j)) + 1,
                                        j + 0.5f,
                                        Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336F,
                                        0.05F,
                                        Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336F
                                );
                            }
                        }
                    }
                }

            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return null;
    }

    private InteractionResult modifySnowyBlocks(Level level, Player contextPlayer, BlockPos clickedPos, int type) {
        var chunkPos = new ChunkPos(clickedPos);
        var sectionPos = SectionPos.of(clickedPos);
        if (level.isLoaded(clickedPos)
                && (contextPlayer == null
                || (level.mayInteract(contextPlayer, clickedPos)
                // 这个东西也许应该冒险模式可以用
                // && pContext.getPlayer().mayUseItemAt(clickedPos, pContext.getClickedFace(), pContext.getItemInHand())
        )
        )
        ) {
            if (level instanceof ServerLevel serverLevel) {
                var chunk = serverLevel.getChunkAt(clickedPos);
                if (!chunk.hasData(EclipticSeasons.ModContents.SNOWY_REMOVER)) {
                    chunk.setData(EclipticSeasons.ModContents.SNOWY_REMOVER, new SnowyRemover(new int[16][16]));
                }
                var data = chunk.getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                data.setChunkPos(clickedPos, type);

                var distance =
                        (serverLevel.getServer() instanceof DedicatedServer dedicatedServer ?
                                dedicatedServer.getProperties().viewDistance :
                                Minecraft.getInstance().options.renderDistance().get())
                                * 16;
                var players = serverLevel.getPlayers(
                        serverPlayer -> {
                            var onPos = serverPlayer.getOnPos();
                            return onPos.distToCenterSqr(clickedPos.getCenter()) < distance;
                        }
                );
                for (ServerPlayer player : players) {
                    MapChecker.sendChunkInfo(chunk, chunkPos, player, List.of(sectionPos.y()), List.of(clickedPos));
                }

                if (data.allSnowAble()) {
                    chunk.removeData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                }

                chunk.setUnsaved(true);
                // serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                //         clickedPos.getX() + 0.5d,
                //         clickedPos.getY() + 1.2d,
                //         clickedPos.getZ() + 0.5d,
                //         10,
                //         Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336D,
                //         0.05D,
                //         Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336D
                //         , 0.01d);

            } else {

                var data = level.getChunkAt(clickedPos).getData(EclipticSeasons.ModContents.SNOWY_REMOVER);
                boolean snowy = type == SnowyRemover.NONE_SNOWY;
                boolean notSnowyAtBefore = data.notSnowyAt(clickedPos);
                if (snowy != notSnowyAtBefore) {
                    var particleType = !snowy
                            ? ParticleTypes.SMOKE : ParticleTypes.SNOWFLAKE;
                    for (int i = 0; i < 10; i++) {
                        level.addParticle(
                                particleType,
                                clickedPos.getX() + 0.5f,
                                clickedPos.getY() + 1,
                                clickedPos.getZ() + 0.5f,
                                Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336F,
                                0.05F,
                                Mth.randomBetween(level.getRandom(), -1.0F, 1.0F) * 0.083333336F
                        );
                    }
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return null;
    }
}
