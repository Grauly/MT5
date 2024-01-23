package grauly.mt5.helpers;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class ParticleHelper {
    public static void spawnParticle(ServerWorld serverWorld, ParticleEffect particleEffect, Vec3d pos, int amount, Vec3d dir, double speed, boolean force) {
        serverWorld.getPlayers().forEach(sp -> {
            serverWorld.spawnParticles(sp, particleEffect, force, pos.getX(), pos.getY(), pos.getZ(), amount, dir.getX(), dir.getY(), dir.getZ(), speed);
        });
    }

    public static void spawnParticle(ServerWorld serverWorld, ParticleEffect particleEffect, Vec3d pos, int amount, Vec3d dir, double speed) {
        serverWorld.spawnParticles(particleEffect, pos.getX(), pos.getY(), pos.getZ(), amount, dir.getX(), dir.getY(), dir.getZ(), speed);
    }
}
