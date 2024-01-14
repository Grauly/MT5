package grauly.mt5.effects;

import grauly.mt5.helpers.MathHelper;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class Circles {

    public static void circle(Vec3d center, float radius, Consumer<Vec3d> pointAction, int segmentCount) {
        double radStep = Math.toRadians(360f / segmentCount);
        for (int i = 0; i < segmentCount; i++) {
            pointAction.accept(center.add(new Vec3d(Math.sin(i * radStep) * radius, 0, Math.cos(i * radStep) * radius)));
        }

    }

    public static void circle(Vec3d center, float radius, int segmentCount, ServerWorld serverWorld, ParticleEffect particleEffect) {
        circle(center, radius, position -> serverWorld.spawnParticles(particleEffect, position.getX(), position.getY(), position.getZ(), 0, 0, 0, 0, 0), segmentCount);
    }

    public static void circle(Vec3d center, float radius, int segmentCount, ServerWorld serverWorld, ArrayList<Color> colors) {
        if (colors.isEmpty()) return;
        circle(center, radius, (position) -> {
            var r = colors.size() == 1 ? 1 : ThreadLocalRandom.current().nextInt(colors.size());
            serverWorld.spawnParticles(
                    new DustParticleEffect(
                            Vec3d.unpackRgb(colors.get(r).getRGB()).toVector3f(),
                            ThreadLocalRandom.current().nextFloat(0.25f, 1f)),
                    position.getX(),
                    position.getY(),
                    position.getZ(),
                    0,
                    0,
                    0,
                    0,
                    0);
        }, segmentCount);
    }

    public static void circle(Vec3d origin, Vec3d normal, float radius, int segmentCount, Consumer<Vec3d> pointAction) {
        normal = normal.normalize();
        Vec3d normalNormal = MathHelper.getVectorPerpendicular(normal).normalize().multiply(radius);
        Vector3d normalNormalRotatable = new Vector3d(normalNormal.getX(), normalNormal.getY(), normalNormal.getZ());
        double radStep = (float) Math.toRadians(360f / segmentCount);

        for (int i = 0; i < segmentCount; i++) {
            var point = normalNormalRotatable.rotateAxis(radStep, normal.getX(), normal.getY(), normal.getZ());
            pointAction.accept(new Vec3d(point.x(), point.y(), point.z()).add(origin));
        }
    }

    public static void circle(Vec3d origin, Vec3d normal, float radius, int segmentCount, ServerWorld serverWorld, ParticleEffect particleEffect) {
        circle(origin, normal, radius, segmentCount, pos -> {
            serverWorld.spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), 0, normal.getX(), normal.getY(), normal.getZ(), 0);
        });
    }
}
