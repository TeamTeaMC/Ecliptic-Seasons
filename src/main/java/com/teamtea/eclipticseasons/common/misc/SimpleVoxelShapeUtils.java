package com.teamtea.eclipticseasons.common.misc;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public class SimpleVoxelShapeUtils {


    public static VoxelShape rotateVoxelShape(VoxelShape shape, Direction.Axis axis, float angleDegrees, Vec3 origin) {
        List<AABB> rotatedBoxes = new ArrayList<>();
        for (AABB box : shape.toAabbs()) {
            rotatedBoxes.add(rotateAABB(box, axis, angleDegrees, origin));
        }

        VoxelShape rotatedShape = Shapes.empty();
        for (AABB box : rotatedBoxes) {
            rotatedShape = Shapes.or(rotatedShape, Shapes.create(box));
        }
        return rotatedShape.optimize();
    }

    public static VoxelShape rotateVoxelShape(VoxelShape shape, Direction.Axis axis, float angleDegrees) {
        return rotateVoxelShape(shape, axis, angleDegrees, new Vec3(0.5, 0.5, 0.5));
    }


    private static AABB rotateAABB(AABB box, Direction.Axis axis, float angleDegrees, Vec3 origin) {
        Vec3 min = rotateVec(new Vec3(box.minX, box.minY, box.minZ), axis, angleDegrees, origin);
        Vec3 max = rotateVec(new Vec3(box.maxX, box.maxY, box.maxZ), axis, angleDegrees, origin);

        double minX = Math.min(min.x, max.x);
        double minY = Math.min(min.y, max.y);
        double minZ = Math.min(min.z, max.z);
        double maxX = Math.max(min.x, max.x);
        double maxY = Math.max(min.y, max.y);
        double maxZ = Math.max(min.z, max.z);

        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }


    private static Vec3 rotateVec(Vec3 vec, Direction.Axis axis, float angleDegrees, Vec3 origin) {

        double x = vec.x - origin.x;
        double y = vec.y - origin.y;
        double z = vec.z - origin.z;

        double radians = Math.toRadians(angleDegrees);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        double rx = x, ry = y, rz = z;

        switch (axis) {
            case X -> {
                ry = y * cos - z * sin;
                rz = y * sin + z * cos;
            }
            case Y -> {
                rx = x * cos - z * sin;
                rz = x * sin + z * cos;
            }
            case Z -> {
                rx = x * cos - y * sin;
                ry = x * sin + y * cos;
            }
        }

        return new Vec3(rx + origin.x, ry + origin.y, rz + origin.z);
    }
}
