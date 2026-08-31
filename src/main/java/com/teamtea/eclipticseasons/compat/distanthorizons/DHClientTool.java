package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.common.wrappers.block.BlockStateWrapper;
import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import com.seibel.distanthorizons.core.api.internal.SharedApi;
import com.seibel.distanthorizons.core.enums.EDhDirection;
import com.seibel.distanthorizons.core.level.ClientLevelModule;
import com.seibel.distanthorizons.core.level.DhClientLevel;
import com.seibel.distanthorizons.core.level.DhClientServerLevel;
import com.seibel.distanthorizons.core.level.IDhClientLevel;
import com.seibel.distanthorizons.core.pos.DhSectionPos;
import com.seibel.distanthorizons.core.pos.blockPos.DhBlockPos;
import com.seibel.distanthorizons.core.render.QuadTree.LodQuadTree;
import com.seibel.distanthorizons.core.render.QuadTree.LodRenderSection;
import com.seibel.distanthorizons.core.util.gridList.MovableGridRingList;
import com.seibel.distanthorizons.core.util.objects.quadTree.QuadNode;
import com.seibel.distanthorizons.core.world.IDhClientWorld;
import com.seibel.distanthorizons.coreapi.util.ColorUtil;
import com.teamtea.eclipticseasons.client.util.ClientCon;
import com.teamtea.eclipticseasons.compat.CompatModule;
import com.teamtea.eclipticseasons.mixin.compat.distanthorizons.MixinAbstractDhTintGetter;
import com.teamtea.eclipticseasons.mixin.compat.distanthorizons.MixinQuadTree;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DHClientTool {
    public static void forceReloadAll() {
        if (ClientCon.getAgent().isTermChange()) {
            MixinAbstractDhTintGetter.getBiomeColorCache().clear();
        }

        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return;
        if (!CompatModule.ClientConfig.DistantHorizonsWinterLODForceUpdateAll.get()) return;

        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || minecraft.player == null
                || level.getGameTime() % (20 * 15) != 0) {
            return;
        }

        IDhClientWorld clientWorld = SharedApi.tryGetDhClientWorld();
        if (!ClientCon.getAgent().isChange()
                || !(ClientLevelWrapper.getWrapper(level) instanceof ClientLevelWrapper clientLevelWrapper)
                || !(clientWorld.getLevel(clientLevelWrapper) instanceof IDhClientLevel clientLevel)) {
            return;
        }

        if (ClientCon.getAgent().isTermChange()) {
            ClientCon.getAgent().setTermChange(false);
            MixinAbstractDhTintGetter.getBiomeColorCache().clear();
        }

        DHTool.setSnowColor(ColorUtil.setAlpha(
                clientLevelWrapper.getBlockColor(
                        DhBlockPos.ZERO,
                        null,
                        null,
                        BlockStateWrapper.fromBlockState(
                                Blocks.SNOW.defaultBlockState(),
                                clientLevelWrapper
                        )
                ),
                255
        ));
        ClientCon.getAgent().setSnowChange(false);

        AtomicReference<ClientLevelModule.ClientRenderState> clientRenderStateAtomicReference = null;
        if (clientLevel instanceof DhClientServerLevel dhClientServerLevel) {
            clientRenderStateAtomicReference =
                    dhClientServerLevel.clientside.ClientRenderStateRef;
        } else if (clientLevel instanceof DhClientLevel dhClientLevel) {
            clientRenderStateAtomicReference =
                    dhClientLevel.clientside.ClientRenderStateRef;
        }

        if (clientRenderStateAtomicReference == null
                || clientRenderStateAtomicReference.get() == null) {
            return;
        }

        LodQuadTree quadtree =
                clientRenderStateAtomicReference.get().quadtree;
        MixinQuadTree quadtree1 = (MixinQuadTree) quadtree;

        List<Long> reloadList = new ArrayList<>();
        MovableGridRingList<QuadNode> topRingList =
                quadtree1.getTopRingList();
        Stack<QuadNode> stack = new Stack<>();

        for (int i = 0, topRingListSize = topRingList.size();
             i < topRingListSize;
             i++) {
            stack.push(topRingList.get(i));
        }

        while (!stack.isEmpty()) {
            QuadNode node = stack.pop();
            if (node == null || node.value == null) continue;

            if (node.value instanceof LodRenderSection lodRenderSection
                    && lodRenderSection.getRenderingEnabled()) {
                reloadList.add(node.sectionPos);
            }

            for (int i = 3; i >= 0; i--) {
                stack.push(node.getChildByIndex(i));
            }
        }

        // Map<Byte, List<Long>> groupedByDetail = reloadList.stream()
        //         .collect(Collectors.groupingBy(DhSectionPos::getDetailLevel));
        // List<Byte> sortedDetailLevels = new ArrayList<>(groupedByDetail.keySet());
        // sortedDetailLevels.sort(Byte::compare);
        // DhBlockPos2D centerBlockPos = quadtree.getCenterBlockPos();
        // for (Byte detailLevel : sortedDetailLevels) {
        //     List<Long> group = groupedByDetail.get(detailLevel);
        //
        //     group.sort((l1, l2) -> {
        //         int dx1 = Math.abs(centerBlockPos.x - DhSectionPos.getCenterBlockPosX(l1));
        //         int dz1 = Math.abs(centerBlockPos.z - DhSectionPos.getCenterBlockPosZ(l1));
        //         int dx2 = Math.abs(centerBlockPos.x - DhSectionPos.getCenterBlockPosX(l2));
        //         int dz2 = Math.abs(centerBlockPos.z - DhSectionPos.getCenterBlockPosZ(l2));
        //         int dist1 = dx1 + dz1;
        //         int dist2 = dx2 + dz2;
        //         return Integer.compare(dist1, dist2);
        //     });
        //
        //     Set<Long> setsLong = new LongLinkedOpenHashSet();
        //     for (Long pos : group) {
        //         if (setsLong.contains(pos)) continue;
        //         quadtree.reloadPos(pos);
        //         setsLong.add(pos);
        //         for (EDhDirection direction : EDhDirection.ADJ_DIRECTIONS) {
        //             long adjacentPos = DhSectionPos.getAdjacentPos(pos, direction);
        //             setsLong.add(adjacentPos);
        //         }
        //     }
        // }

        int playerX = minecraft.player.getBlockX();
        int playerZ = minecraft.player.getBlockZ();

        reloadList.sort((a, b) -> {
            long distanceA = getDistanceSq(a, playerX, playerZ);
            long distanceB = getDistanceSq(b, playerX, playerZ);

            if (distanceA != distanceB) {
                return Long.compare(distanceA, distanceB);
            }

            return Byte.compare(
                    DhSectionPos.getDetailLevel(b),
                    DhSectionPos.getDetailLevel(a)
            );
        });

        Set<Long> setsLong = new LongLinkedOpenHashSet();
        for (long pos : reloadList) {
            if (setsLong.contains(pos)) continue;

            quadtree.queuePosToReload(pos);
            setsLong.add(pos);

            for (EDhDirection direction : EDhDirection.CARDINAL_COMPASS) {
                setsLong.add(DhSectionPos.getAdjacentPos(pos, direction));
            }
        }

        //     // 也许未来需要定向刷新，但是目前来看只需要全部刷新即可
        // int d = (int) Config.Client.quickLodChunkRenderDistance.get().get() / 2;

        // SectionPos sectionPos = SectionPos.of(pos);
        // int pSectionX = SectionPos.blockToSectionCoord(pos.x);
        // int pSectionZ = SectionPos.blockToSectionCoord(pos.z);
        //
        // byte treeMinDetailLevel = quadtree.treeMinDetailLevel;
        // byte treeMaxDetailLevel = quadtree.treeMaxDetailLevel;
        // for (int i = pSectionX - d; i <= pSectionX + d; i++) {
        //     for (int j = pSectionZ - d; j <= pSectionZ + d; j++) {
        //         for (byte k = treeMaxDetailLevel; k <= treeMinDetailLevel; k++) {
        //             // 注意这里是dh的sectionpos，其实与mc中类似
        //             // long rootPos = DhSectionPos.encode(k, i, j);
        //             // clientRenderStateAtomicReference.get().quadtree.reloadPos(rootPos);
        //
        //         }
        //     }
        // }
    }

    protected static long getDistanceSq(
            long pos,
            int playerX,
            int playerZ
    ) {
        long dx = DhSectionPos.getCenterBlockPosX(pos) - playerX;
        long dz = DhSectionPos.getCenterBlockPosZ(pos) - playerZ;
        return dx * dx + dz * dz;
    }
}