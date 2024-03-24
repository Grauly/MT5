package grauly.mt5.helpers;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3d;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class MathHelper {
    private static final SecureRandom random = new SecureRandom();

    public static Vec3d getVectorPerpendicular(Vec3d original) {
        original = original.normalize();
        if (original.getX() != 0d) return getVectorPerpendicularWithNonZeroX(original);
        if (original.getY() != 0d) return getVectorPerpendicularWithNonZeroY(original);
        if (original.getZ() != 0d) return getVectorPerpendicularWithNonZeroZ(original);
        return new Vec3d(0, 0, 0);
    }

    private static Vec3d getVectorPerpendicularWithNonZeroX(Vec3d original) {
        var b3 = ThreadLocalRandom.current().nextDouble();
        var b2 = ThreadLocalRandom.current().nextDouble();
        var b1 = (-original.getY() * b2 - original.getZ() * b3) / original.getX();
        return new Vec3d(b1, b2, b3);
    }

    private static Vec3d getVectorPerpendicularWithNonZeroY(Vec3d original) {
        var b1 = ThreadLocalRandom.current().nextDouble();
        var b3 = ThreadLocalRandom.current().nextDouble();
        var b2 = (-original.getX() * b1 - original.getZ() * b3) / original.getY();
        return new Vec3d(b1, b2, b3);
    }

    private static Vec3d getVectorPerpendicularWithNonZeroZ(Vec3d original) {
        var b1 = ThreadLocalRandom.current().nextDouble();
        var b2 = ThreadLocalRandom.current().nextDouble();
        var b3 = (-original.getX() * b1 - original.getY() * b2) / original.getZ();
        return new Vec3d(b1, b2, b3);
    }

    public static Vec3d getReflectionVector(Vec3d original, Vec3d surfaceNormal) {
        //credit to: https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
        surfaceNormal = surfaceNormal.normalize();
        original = original.normalize();
        return original.subtract(surfaceNormal.multiply(2 * original.dotProduct(surfaceNormal)));
    }

    public static Vec3d getReflectionVector(Vec3d original, Direction surface) {
        var multVector = switch (surface.getAxis()) {
            case X -> new Vec3i(-1, 1, 1);
            case Y -> new Vec3i(1, -1, 1);
            case Z -> new Vec3i(1, 1, -1);
        };
        return original.multiply(Vec3d.of(multVector));
    }

    public static Vec3d spreadShot(Vec3d original, float angle) {
        if (angle <= 0) return original;
        if (angle >= 90) return getVectorPerpendicular(original);
        var spreadBase = getVectorPerpendicular(original).normalize();
        var angleRadians = Math.toRadians(angle);
        var spreadVectorLength = random.nextDouble(0, Math.tan(angleRadians));
        var spreadAngle = random.nextDouble(0, 2 * Math.PI);
        return toMCVector(toJomlVector(spreadBase)
                .rotateAxis(spreadAngle, original.getX(), original.getY(), original.getZ())
                .mul(spreadVectorLength)
                .add(toJomlVector(original)));
    }

    public static Vector3d toJomlVector(Vec3d original) {
        return new Vector3d(original.getX(), original.getY(), original.getZ());
    }

    public static Vec3d toMCVector(Vector3d original) {
        return new Vec3d(original.x(), original.y(), original.z());
    }
}
