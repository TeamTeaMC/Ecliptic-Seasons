package com.teamtea.eclipticseasons.compat.distanthorizons;

import com.seibel.distanthorizons.common.wrappers.world.ClientLevelWrapper;
import com.teamtea.eclipticseasons.compat.CompatModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DHClientTool {

    public static void forceReloadAll() {
        if (!CompatModule.CommonConfig.DistantHorizonsWinterLOD.get()) return;
        if (!CompatModule.ClientConfig.DistantHorizonsWinterLODForceUpdateAll.get()) return;

        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        ClientLevelWrapper levelWrapper = (ClientLevelWrapper) ClientLevelWrapper.getWrapper(level);
        if (levelWrapper == null) return;

        Object quadtree = getQuadTree(levelWrapper);
        if (quadtree == null) return;

        List<Long> allPositions = collectAllPositions(quadtree);
        if (allPositions.isEmpty()) return;

        try {
            Method queuePosToReload = quadtree.getClass().getMethod("queuePosToReload", long.class);
            int reloadCount = 0;
            for (long pos : allPositions) {
                queuePosToReload.invoke(quadtree, pos);
                reloadCount++;
                if (reloadCount % 200 == 0) {
                    try { Thread.sleep(1); } catch (InterruptedException ignored) {}
                }
            }

            levelWrapper.clearBlockColorCache();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Long> collectAllPositions(Object quadtree) {
        List<Long> positions = new ArrayList<>();
        try {
            Field ringField = findField(quadtree.getClass(), "topRingList");
            if (ringField == null) return positions;
            ringField.setAccessible(true);
            Object ringList = ringField.get(quadtree);
            if (ringList == null) return positions;

            Method sizeMethod = ringList.getClass().getMethod("size");
            int size = (int) sizeMethod.invoke(ringList);
            Method getMethod = ringList.getClass().getMethod("get", int.class);

            for (int i = 0; i < size; i++) {
                Object node = getMethod.invoke(ringList, i);
                if (node == null) continue;
                collectPositionsRecursively(node, positions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    private static void collectPositionsRecursively(Object node, List<Long> positions) {
        try {
            Field sectionPosField = findField(node.getClass(), "sectionPos");
            if (sectionPosField != null) {
                sectionPosField.setAccessible(true);
                long pos = sectionPosField.getLong(node);
                positions.add(pos);
            }

            Method getChildMethod = node.getClass().getMethod("getChildByIndex", int.class);
            for (int i = 0; i < 4; i++) {
                Object child = getChildMethod.invoke(node, i);
                if (child != null) {
                    collectPositionsRecursively(child, positions);
                }
            }
        } catch (Exception e) {
            try {
                Field childrenField = findField(node.getClass(), "children");
                if (childrenField != null) {
                    childrenField.setAccessible(true);
                    Object[] children = (Object[]) childrenField.get(node);
                    if (children != null) {
                        for (Object child : children) {
                            if (child != null) {
                                collectPositionsRecursively(child, positions);
                            }
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private static Object getQuadTree(ClientLevelWrapper levelWrapper) {
        try {
            Field dhLevelField = ClientLevelWrapper.class.getDeclaredField("dhLevel");
            dhLevelField.setAccessible(true);
            Object dhLevel = dhLevelField.get(levelWrapper);
            if (dhLevel == null) return null;

            Field clientsideField = findField(dhLevel.getClass(), "clientside");
            if (clientsideField == null) return null;
            clientsideField.setAccessible(true);
            Object clientside = clientsideField.get(dhLevel);
            if (clientside == null) return null;

            Field stateRefField = findField(clientside.getClass(), "ClientRenderStateRef");
            if (stateRefField == null) return null;
            stateRefField.setAccessible(true);
            AtomicReference<?> stateRef = (AtomicReference<?>) stateRefField.get(clientside);
            if (stateRef == null) return null;
            Object renderState = stateRef.get();
            if (renderState == null) return null;

            Field quadtreeField = findField(renderState.getClass(), "quadtree");
            if (quadtreeField == null) return null;
            quadtreeField.setAccessible(true);
            return quadtreeField.get(renderState);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}