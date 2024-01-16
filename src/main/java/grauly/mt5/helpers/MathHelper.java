package grauly.mt5.helpers;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix4f;

import java.util.concurrent.ThreadLocalRandom;

public class MathHelper {
    public static Vec3d getVectorPerpendicular(Vec3d original) {
        original = original.normalize();
        var b3 = ThreadLocalRandom.current().nextDouble();
        var b2 = ThreadLocalRandom.current().nextDouble();
        var b1 = (-original.getY() * b2 - original.getZ() * b3) / original.getX();
        return new Vec3d(b1, b2, b3);
    }

    public static Vec3d getReflectionVector(Vec3d original, Vec3d surfaceNormal) {
        //credit to: https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
        surfaceNormal = surfaceNormal.normalize();
        original = original.normalize();
        return original.subtract(surfaceNormal.multiply(2*original.dotProduct(surfaceNormal)));
    }

    public static Vec3d getReflectionVector(Vec3d original, Direction surface) {
        var multVector = switch (surface.getAxis()) {
            case X -> new Vec3i(surface.getDirection().offset(),1,1);
            case Y -> new Vec3i(1,surface.getDirection().offset(),1);
            case Z -> new Vec3i(1,1,surface.getDirection().offset());
        };
        return original.multiply(Vec3d.of(multVector));
    }
}
