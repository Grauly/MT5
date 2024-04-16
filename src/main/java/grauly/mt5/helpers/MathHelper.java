package grauly.mt5.helpers;

import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class MathHelper {
    public static final Vec3d UP = new Vec3d(0, 1, 0);
    public static final double TWO_PI = 2 * Math.PI;
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

    /**
     * transforms the original Vector such that its up axis is now the newUp (think rotation the coordinate system so that up points in direction of newUp)
     *
     * @param original the vector to transform
     * @param newUp    the new Up
     * @return the transformed Vector
     */
    public static Vec3d rotateToNewUp(Vec3d original, Vec3d newUp) {
        //from https://stackoverflow.com/questions/1171849/finding-quaternion-representing-the-rotation-from-one-vector-to-another/1171995#1171995
        Vec3d crossProduct = UP.crossProduct(newUp);
        double w = Math.sqrt(UP.lengthSquared() * newUp.lengthSquared()) + UP.dotProduct(newUp);
        Quaterniond quaternion = new Quaterniond(crossProduct.getX(), crossProduct.getY(), crossProduct.getZ(), w);
        return toMCVector(quaternion.transform(toJomlVector(original)));
    }

    public static Vector3d toJomlVector(Vec3d original) {
        return new Vector3d(original.getX(), original.getY(), original.getZ());
    }

    public static Vec3d toMCVector(Vector3d original) {
        return new Vec3d(original.x(), original.y(), original.z());
    }

    public static double sphereSurface(double radius) {
        return 4 * Math.PI * Math.pow(radius, 2);
    }

    public static double sphereVolume(double radius) {
        return (4f / 3f) * Math.PI * Math.pow(radius, 3);
    }

    public static double circleCircumference(double radius) {
        return 2 * Math.PI * radius;
    }

    public static double circleSurface(double radius) {
        return Math.PI * Math.pow(radius, 2);
    }

    public static Vec3d toSphericalCoordinates(Vec3d pos) {
        double r = pos.length();
        double theta = Math.acos(pos.getY() / r);
        double phi = Math.signum(pos.getZ()) * Math.acos(pos.getX() / Math.sqrt(Math.pow(pos.getX(), 2) + Math.pow(pos.getY(), 2)));
        return new Vec3d(r, theta, phi);
    }

    public static Vec3d fromSphericalCoordinates(Vec3d sph) {
        double x = sph.getX() * Math.sin(sph.getY()) * Math.cos(sph.getZ());
        double y = sph.getX() * Math.sin(sph.getY()) * Math.sin(sph.getZ());
        double z = sph.getX() * Math.cos(sph.getY());
        return new Vec3d(x, z, y);
    }
}
