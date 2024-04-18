package grauly.mt5.effects.explosion;

import grauly.mt5.effects.Shockwave;
import grauly.mt5.effects.explosion.particle.DebrisParticle;
import grauly.mt5.effects.explosion.particle.TemperatureAwareParticle;
import grauly.mt5.effects.explosion.particle.TemperatureDisplayingParticle;
import grauly.mt5.helpers.MathHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.registers.ModSchedulers;
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
            TemperatureDisplayingParticle particle = new TemperatureDisplayingParticle(world, position, velocityVector, 0.95f, ThreadLocalRandom.current().nextInt(7, 10), 0.25f);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
        }
    }

    /**
     * spawn Fragment Particles
     *
     * @param world                   the world this takes place in
     * @param position                the position to originate at
     * @param normal                  the direction of the explosion
     * @param count                   how many particles to spawn
     * @param floorAngleOffsetDegrees angle from the floor, the minimum angle they are to be launched at
     * @param range                   how far the particles should be able to reach (not guaranteed)
     */
    public static void parametricFragments(ServerWorld world, Vec3d position, Vec3d normal, int count, float floorAngleOffsetDegrees, float range) {
        double thetaMax = Math.toRadians(90 - floorAngleOffsetDegrees);
        double lengthVariation = 0.5f;
        double lengthBase = range / 10;
        for (int i = 0; i < count; i++) {
            Vec3d velocity = MathHelper.fromSphericalCoordinates(new Vec3d(
                    ThreadLocalRandom.current().nextDouble(lengthBase - lengthVariation, lengthBase + lengthVariation),
                    ThreadLocalRandom.current().nextDouble(0, thetaMax),
                    ThreadLocalRandom.current().nextDouble(0, MathHelper.TWO_PI)
            ));
            velocity = MathHelper.rotateToNewUp(velocity, normal);
            TemperatureDisplayingParticle particle = new TemperatureDisplayingParticle(world, position, velocity, 0.9f, ThreadLocalRandom.current().nextInt(7, 10), (float) (0.5f * Math.max(-0.02 * range + 1, 0.00001)), 3);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
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
            TemperatureDisplayingParticle particle = new TemperatureDisplayingParticle(world, position, velocityVector, 0.9f, ThreadLocalRandom.current().nextInt(10, 15), 0.5f, 3);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
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
            TemperatureAwareParticle particle = new TemperatureAwareParticle(world, position, velocityVector, 0.6f, new Vec3d(0, -0.2, 0), ThreadLocalRandom.current().nextInt(10, 15), 1.2f, new Vec3d(0, 0.2, 0), 0.25f, 1);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
        }
    }

    /**
     * Spawns Bloom Particles.
     * Inaccurate in regard to height by ~ 1-2 blocks
     * heights below ~7 will almost be uniform
     *
     * @param world              the world this takes place ij
     * @param position           the position to start at
     * @param normal             the direction of the explosion
     * @param count              the amount of bloom effects
     * @param bloomSpreadDegrees the spread cone angle
     * @param height             the height up to which this goes
     */
    public static void parametrizedFancyBloom(ServerWorld world, Vec3d position, Vec3d normal, int count, float bloomSpreadDegrees, float height) {
        //magic numbers. I fitted a curve for this with: (6,1) (13,1.2) (31,1.3)
        height *= (float) (0.9714252 + 0.01692272 * height - 0.0001988311 * Math.pow(height, 2));
        double thetaMax = Math.toRadians(bloomSpreadDegrees);
        double lengthVariation = height / 5;
        height = Math.max(0, height - 2);
        double lengthBase = Math.max(lengthVariation, (height / 2) - lengthVariation);
        for (int i = 0; i < count; i++) {
            Vec3d velocity = MathHelper.fromSphericalCoordinates(new Vec3d(
                    ThreadLocalRandom.current().nextDouble(lengthBase - lengthVariation, lengthBase + lengthVariation),
                    ThreadLocalRandom.current().nextDouble(0, thetaMax),
                    ThreadLocalRandom.current().nextDouble(0, MathHelper.TWO_PI)
            ));
            velocity = MathHelper.rotateToNewUp(velocity, normal);
            TemperatureAwareParticle particle = new TemperatureAwareParticle(world, position, velocity, 0.6f, new Vec3d(0, -0.2, 0), ThreadLocalRandom.current().nextInt(10, 15), 1.2f, new Vec3d(0, 0.2, 0), 0.25f, 1);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
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
            DebrisParticle particle = new DebrisParticle(world, position, velocityVector, 0.9f,  displayState,3);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
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
            TemperatureDisplayingParticle particle = new TemperatureDisplayingParticle(world, position, velocityVector, 0.6f, ThreadLocalRandom.current().nextInt(15, 20), 2.25f, 4);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
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
