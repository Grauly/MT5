package grauly.mt5.helpers;

import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

public class MathHelper {
    public static Vec3d getVectorPerpendicular(Vec3d original) {
        original = original.normalize();
        var b3 = ThreadLocalRandom.current().nextDouble();
        var b2 = ThreadLocalRandom.current().nextDouble();
        var b1 = (-original.getY() * b2 - original.getZ() * b3) / original.getX();
        return new Vec3d(b1,b2,b3);
    }
}
