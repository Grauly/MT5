package grauly.mt5.effects;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Primitives {

    public static void line(Vec3d from, Vec3d to, BiConsumer<Vec3d, Vec3d> pointAction, int pointsPerBlock) {
        Vec3d line = to.subtract(from);
        double step = 1/(pointsPerBlock * line.length());
        var lineDirection = line.normalize().multiply(1f/pointsPerBlock);
        for (double i = 0; i < 1; i += step) {
            pointAction.accept(from.lerp(to,i), lineDirection);
        }
    }

    public static void line(Vec3d from, Vec3d to, ServerWorld serverWorld, ParticleEffect particleEffect) {
        line(from, to, (position, direction) -> {
            serverWorld.spawnParticles(particleEffect, position.getX(),position.getY(),position.getZ(), 1, direction.getX(), direction.getY(), direction.getZ(), 0.1);
        }, 5);
    }
}
