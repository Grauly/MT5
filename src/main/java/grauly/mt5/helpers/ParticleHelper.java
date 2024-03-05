package grauly.mt5.helpers;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ParticleHelper {
    public static void spawnParticle(ServerWorld serverWorld, ParticleEffect particleEffect, Vec3d pos, int amount, Vec3d dir, double speed, boolean force) {
        serverWorld.getPlayers().forEach(sp -> {
            serverWorld.spawnParticles(sp, particleEffect, force, pos.getX(), pos.getY(), pos.getZ(), amount, dir.getX(), dir.getY(), dir.getZ(), speed);
        });
    }

    public static void spawnParticle(ServerWorld serverWorld, ParticleEffect particleEffect, Vec3d pos, int amount, Vec3d dir, double speed) {
        serverWorld.spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), amount, dir.getX(), dir.getY(), dir.getZ(), speed);
    }

    public static DustParticleEffect getDustParticle(Color color, float lowerRange, float upperRange) {
        return getDustParticle(color, ThreadLocalRandom.current().nextFloat(lowerRange, upperRange));
    }

    public static DustParticleEffect getDustParticle(Color color, float size) {
        return new DustParticleEffect(Vec3d.unpackRgb(color.getRGB()).toVector3f(), size);
    }
}
