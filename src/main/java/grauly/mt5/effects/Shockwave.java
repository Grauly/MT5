package grauly.mt5.effects;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.function.BiConsumer;

public class Shockwave {
    public static void actualMovement(Vec3d position, Vec3d normal, int segments, BiConsumer<Vec3d, Vec3d> rayAction) {
        Circles.circle(position, normal, 1f, segments, p -> {
            rayAction.accept(position, position.subtract(p));
        });
    }

    public static void actualMovement(ServerWorld world, Vec3d position, Vec3d normal, int segments, ParticleEffect effect, float speed) {
        actualMovement(position, normal, segments, (pos, dir) -> {
            ParticleHelper.spawnParticle(world, effect, pos, 0, dir, speed);
        });
    }
}
