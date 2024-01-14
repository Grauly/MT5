package grauly.mt5.effects;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;

public class Lines {

    public static void line(Vec3d from, Vec3d to, BiConsumer<Vec3d, Vec3d> pointAction, int pointsPerBlock) {
        Vec3d line = to.subtract(from);
        double step = 1 / (pointsPerBlock * line.length());
        var lineDirection = line.normalize().multiply(1f / pointsPerBlock);
        for (double i = 0; i < 1; i += step) {
            pointAction.accept(from.lerp(to, i), lineDirection);
        }
    }

    public static void line(Vec3d from, Vec3d to, ServerWorld serverWorld, ParticleEffect particleEffect) {
        line(from, to, (position, direction) -> {
            serverWorld.spawnParticles(particleEffect, position.getX(), position.getY(), position.getZ(), 0, direction.getX(), direction.getY(), direction.getZ(), 0.2);
        }, 3);
    }

    public static void line(Vec3d direction, float length, Vec3d start, ServerWorld serverWorld, ParticleEffect particleEffect) {
        line(start, start.add(direction.normalize().multiply(length)), serverWorld, particleEffect);
    }

    public static void line(Vec3d from, Vec3d to, ServerWorld serverWorld, ArrayList<Color> colors) {
        if (colors.isEmpty()) return;
        line(from, to, (position, direction) -> {
            var r = colors.size() == 1 ? 1 : ThreadLocalRandom.current().nextInt(colors.size());
            serverWorld.spawnParticles(
                    new DustParticleEffect(
                            Vec3d.unpackRgb(colors.get(r).getRGB()).toVector3f(),
                            ThreadLocalRandom.current().nextFloat(0.25f, 1f)),
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    0,
                    direction.getX(),
                    direction.getY(),
                    direction.getZ(),
                    0.5);
        }, 3);
    }

    public static void line(Vec3d direction, float length, Vec3d start, ServerWorld serverWorld, ArrayList<Color> colors) {
        line(start, start.add(direction.normalize().multiply(length)), serverWorld, colors);
    }

    public static void line(Vec3d direction, float length, Vec3d start, BiConsumer<Vec3d, Vec3d> pointAction, int pointsPerBlock) {
        line(start, start.add(direction.normalize().multiply(length)), pointAction, pointsPerBlock);
    }
}
