package grauly.mt5.effects.explosion;

import grauly.mt5.effects.Shockwave;
import grauly.mt5.effects.explosion.particle.DebrisParticle;
import grauly.mt5.effects.explosion.particle.HeatAwareParticle;
import grauly.mt5.effects.explosion.particle.HeatedParticle;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.MathHelper;
import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

public class ExplosionEffects {
    public static void fragments(ServerWorld world, Vec3d position, Vec3d normal, int count) {
        for (int i = 0; i < count; i++) { //15
            float distribution = 0.5f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(0.01, 1),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0.7, 1.5));
            velocityVector = MathHelper.rotateToNewUp(velocityVector, normal);
            HeatedParticle particle = new HeatedParticle(world, position, velocityVector, 0.95f, ThreadLocalRandom.current().nextInt(7, 10), 0.25f, 0f);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }
    }

    public static void bloom(ServerWorld world, Vec3d position, Vec3d normal, int count) {
        for (int i = 0; i < count; i++) {
            float distribution = 0.2f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(1, 1.5),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(1.2, 2));
            velocityVector = MathHelper.rotateToNewUp(velocityVector, normal);
            HeatedParticle particle = new HeatedParticle(world, position, velocityVector, 0.9f, ThreadLocalRandom.current().nextInt(10, 15), 0.5f, 3);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }
    }

    public static void fancyBloom(ServerWorld world, Vec3d position, Vec3d normal, int count) {
        for (int i = 0; i < count; i++) {
            float distribution = 0.2f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(1, 1.5),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(2.2, 7.7));
            velocityVector = MathHelper.rotateToNewUp(velocityVector, normal);
            HeatAwareParticle particle = new HeatAwareParticle(world, position, velocityVector, 0.6f, new Vec3d(0,-0.2,0), ThreadLocalRandom.current().nextInt(10, 15), 1.2f, new Vec3d(0,0.2,0), 0.25f,1, 1);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }
    }

    public static void debrisBloom(ServerWorld world, Vec3d position, Vec3d normal, int count, BlockState displayState) {
        for (int i = 0; i < count; i++) {
            float distribution = 0.2f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(1, 1.5),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(1.2, 2));
            velocityVector = MathHelper.rotateToNewUp(velocityVector, normal);
            DebrisParticle particle = new DebrisParticle(world, position, velocityVector, 0.9f, ThreadLocalRandom.current().nextInt(5, 7), 0.5f, 3);
            particle.setDebrisState(displayState);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }
    }

    public static void burst(ServerWorld world, Vec3d position, Vec3d normal, int count) {
        for (int i = 0; i < count; i++) {
            float distribution = 0.9f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(0.1, 1),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0.8, 2.8));
            velocityVector = MathHelper.rotateToNewUp(velocityVector, normal);
            HeatedParticle particle = new HeatedParticle(world, position, velocityVector, 0.6f, ThreadLocalRandom.current().nextInt(15, 20), 2.25f, 0, 4);
            particle.startTask(MT5.TASK_SCHEDULER, 0, 1);
        }
    }

    public static void flash(ServerWorld world, Vec3d position, int count) {
        ParticleHelper.spawnParticle(world, ParticleTypes.FLASH, position, count, new Vec3d(0.7, 1, 0.7), 1f, true);
    }

    public static void smoke(ServerWorld world, Vec3d position, Vec3d normal, int count) {
        for (int i = 0; i < count; i++) {
            float distribution = 3f;
            Vec3d velocityVector = new Vec3d(
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution),
                    ThreadLocalRandom.current().nextDouble(0.1, 3),
                    ThreadLocalRandom.current().nextDouble(-distribution, distribution)
            ).normalize().multiply(ThreadLocalRandom.current().nextDouble(0, 3));
            velocityVector = MathHelper.rotateToNewUp(velocityVector, normal);
            ParticleHelper.spawnParticle(world, ParticleTypes.CAMPFIRE_COSY_SMOKE, position.add(velocityVector), 0, new Vec3d(0, 0.1f, 0), 0.2f);
        }
    }

    public static void shockwave(ServerWorld world, Vec3d position, Vec3d normal, int count) {
        for (int i = 0; i < count; i++) {
            Shockwave.actualMovement(position, normal, 32, (pos, dir) -> {
                float distribution = 0.1f;
                dir = dir.add(ThreadLocalRandom.current().nextFloat(-distribution, distribution), ThreadLocalRandom.current().nextFloat(-distribution, distribution), ThreadLocalRandom.current().nextFloat(-distribution, distribution));
                ParticleHelper.spawnParticle(world, ParticleTypes.CLOUD, pos, 0, dir, 1f);
            });
        }
    }
}
