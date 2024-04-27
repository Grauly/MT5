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
    public static void velocityParametrizedFancyBloom(ServerWorld world, Vec3d position, Vec3d normal, int count, float bloomSpreadDegrees, float height) {
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
    public static void temperatureParametrizedFancyBloom(ServerWorld world, Vec3d position, Vec3d normal, int count, float bloomSpreadDegrees, float height) {
        double thetaMax = Math.toRadians(bloomSpreadDegrees);
        float tempVariation = height / 10;
        //Magic numbers. fitted a curve to: (3, 3.3) (6, 2.48) (12, 1.8) (18, 1.46) (24, 1.25) (30, 1.115)
        float tempBase = (float) (height * (-0.06183905 + (8.749258 - -0.06183905) / (1 + Math.pow((height / 1.347752), 0.6038271)))) - (tempVariation / 2);
        for (int i = 0; i < count; i++) {
            Vec3d velocity = MathHelper.fromSphericalCoordinates(new Vec3d(
                    1,
                    ThreadLocalRandom.current().nextDouble(0, thetaMax),
                    ThreadLocalRandom.current().nextDouble(0, MathHelper.TWO_PI)
            ));
            velocity = MathHelper.rotateToNewUp(velocity, normal);
            TemperatureAwareParticle particle = new TemperatureAwareParticle(world, position, velocity, 0.6f, ThreadLocalRandom.current().nextFloat(tempBase - tempVariation, tempBase + tempVariation), 1.2f);
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
            DebrisParticle particle = new DebrisParticle(world, position, velocityVector, 0.9f, displayState, 3);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
        }
    }

    /**
     * Spawns a debris bloom.
     * Inaccurate in regard to height by ~ 0-2 blocks
     *
     * @param world              the world this takes place in
     * @param position           the position to start at
     * @param normal             the direction of the
     * @param count              how many bloom particles should be spawned
     * @param displayState       the blockstate to be used
     * @param bloomSpreadDegrees angle in degrees of inaccuracy from the normal
     * @param height             how high this should be thrown
     */
    public static void parametrizedDebrisBloom(ServerWorld world, Vec3d position, Vec3d normal, int count, BlockState displayState, float bloomSpreadDegrees, float height) {
        double thetaMax = Math.toRadians(bloomSpreadDegrees);
        //Magic values. Fitted a curve to: (3,0.74) (6,1.23) (9,1.68) (12,2.1) (15,2.1) (18,2.895) (21,3.28) (24,3.66) (27,4.035)
        double velocityBase = 0.40107142856992684 + (0.13110064934877974 * height) + (0.000160533908352134 * Math.pow(height, 2));
        float velocityVariation = 0.01f;
        for (int i = 0; i < count; i++) {
            Vec3d velocity = MathHelper.fromSphericalCoordinates(new Vec3d(
                    ThreadLocalRandom.current().nextDouble(velocityBase - velocityVariation, velocityBase + velocityVariation),
                    ThreadLocalRandom.current().nextDouble(thetaMax),
                    ThreadLocalRandom.current().nextDouble(MathHelper.TWO_PI)
            ));
            velocity = MathHelper.rotateToNewUp(velocity, normal);
            DebrisParticle particle = new DebrisParticle(world, position, velocity, 0.9f, displayState);
            particle.startTask(ModSchedulers.VISUALS, 0, 1);
        }
    }

    /**
     * Spawns a burst effect
     * The Burst in this case is the "core" part of the explosion
     *
     * @param world             the world this takes place in
     * @param position          the position this happens at
     * @param normal            the direction this is taking place to
     * @param range             the range to which this will go
     * @param floorAngleDegrees how much "floor space" this should have
     * @param count             how many particles should be spawned
     */
    public static void parametrizedBurst(ServerWorld world, Vec3d position, Vec3d normal, float range, float floorAngleDegrees, int count) {
        double thetaMax = Math.toRadians(90 - floorAngleDegrees);
        double velocityBase = range / 1.5;
        double velocityVariation = 0.5f;
        //Magic numbers: curve fitted to: (3,2) (6,1.98) (12,1.8) (18,1.6) (24,1.35)
        float temperatureBase = (float) (range * (2.055198255355241 + (-0.011749273065915888 * range) + (-0.0007399330359303402 * Math.pow(range, 2))));
        float temperatureVariation = 10;
        for (int i = 0; i < count; i++) {
            Vec3d velocity = MathHelper.fromSphericalCoordinates(new Vec3d(
                    ThreadLocalRandom.current().nextDouble(Math.max(0, velocityBase - velocityVariation), velocityBase + velocityVariation),
                    ThreadLocalRandom.current().nextDouble(thetaMax),
                    ThreadLocalRandom.current().nextDouble(MathHelper.TWO_PI)
            ));
            velocity = MathHelper.rotateToNewUp(velocity, normal);
            TemperatureDisplayingParticle particle = new TemperatureDisplayingParticle(world,
                    position,
                    velocity,
                    0.6f,
                    ThreadLocalRandom.current().nextFloat(temperatureBase - temperatureVariation, temperatureBase + temperatureVariation),
                    2.25f,
                    4);
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
