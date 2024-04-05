package grauly.mt5.effects.explosion;

import grauly.mt5.effects.Spheres;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.helpers.ExplosionHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.registers.ModBlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.fluid.FluidState;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

import static java.lang.Math.sqrt;

public abstract class ParametrizedFancyExplosion {

    public static final int SAMPLES_PER_SQUARE_BLOCK = 4;
    public static final float STEP_SIZE_BLOCKS = 0.3f;
    protected static final Random RANDOM = new Random();
    protected final float power;
    protected final Vec3d position;
    protected final Vec3d direction;
    protected final Vec3d explosionCenter; //offset the explosion center to more accurately model blast waves
    protected final ServerWorld world;
    protected final Explosion dummyExplosion;
    protected final Entity source;
    protected final DamageSource damageSource;

    public ParametrizedFancyExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source) {
        this.power = power;
        this.direction = direction.normalize();
        this.position = position;
        this.explosionCenter = position.add(direction.multiply(power / 30));
        this.world = world;
        this.source = source;
        this.damageSource = Explosion.createDamageSource(world, source);
        dummyExplosion = createDummyExplosion();
    }

    public ParametrizedFancyExplosion(float power, Vec3d position, Vec3d direction, ServerWorld world, Entity source, DamageSource damageSource) {
        this.power = power;
        this.direction = direction.normalize();
        this.position = position;
        this.explosionCenter = position.add(direction.multiply(power / 30));
        this.world = world;
        this.source = source;
        this.damageSource = damageSource;
        dummyExplosion = createDummyExplosion();
    }

    public static float getExposure(Box box, Vec3d origin, int samplesPerBlock, int guaranteedSamples, ServerWorld world) {
        double deltaX = 1 / ((box.maxX - box.minX) * samplesPerBlock + guaranteedSamples);
        double deltaY = 1 / ((box.maxY - box.minY) * samplesPerBlock + guaranteedSamples);
        double deltaZ = 1 / ((box.maxZ - box.minZ) * samplesPerBlock + guaranteedSamples);
        int hits = 0;
        int checks = 0;
        for (double x = 0; x < 1; x += deltaX) {
            for (double y = 0; y < 1; y += deltaY) {
                for (double z = 0; z < 1; z += deltaZ) {
                    Vec3d targetPos = new Vec3d(MathHelper.lerp(x, box.minX, box.maxX), MathHelper.lerp(y, box.minY, box.maxY), MathHelper.lerp(z, box.minZ, box.maxZ));
                    if (RaycastHelper.hasCollisionLineOfSight(world, origin, targetPos)) hits++;
                    checks++;
                }
            }
        }
        return ((float) hits) / checks;
    }

    protected Explosion createDummyExplosion() {
        return new Explosion(world, source, position.getX(), position.getY(), position.getZ(), (float) Math.cbrt(power), false, ExplosionHelper.getExplosionBehavior(world));
    }

    public void setOff() {
        explode();
        visualize();
    }

    public abstract void explode();

    public abstract void visualize();

    protected void applyEffectsToEntities(List<EntityExposureData> entities) {
        entities.forEach(e -> {
            float impact = calculateImpact(e.distanceSquared) * e.exposure;
            e.entity.damage(damageSource, impact * 2);
            e.entity.setVelocity(e.entity.getVelocity().add(e.accelerationVector.multiply(sqrt(impact))));
        });
    }

    protected void applyEffectsToBlocks(Set<BlockPos> blocks) {
        blocks.forEach((blockPos) -> {
            BlockState state = world.getBlockState(blockPos);
            for (int i = 0; i < 15; i++) {
                Vec3d pos = blockPos.toCenterPos().add(ThreadLocalRandom.current().nextFloat(-0.5f, 0.5f), ThreadLocalRandom.current().nextFloat(-0.5f, 0.5f), ThreadLocalRandom.current().nextFloat(-0.5f, 0.5f));
                ParticleHelper.spawnParticle(world, new BlockStateParticleEffect(ParticleTypes.BLOCK, state), pos, 0, new Vec3d(0, 0, 0), 0.1f);
            }
            world.playSound(null, blockPos, state.getSoundGroup().getBreakSound(), SoundCategory.BLOCKS);
            state.onExploded(world, blockPos, dummyExplosion, (stack, pos) -> {
            });
        });
    }

    protected float calculateImpact(float distanceToCenterSquared) {
        return (float) MathHelper.clamp(((6 * power) / (sqrt(distanceToCenterSquared))) - 6, 0, power);
    }

    protected Set<BlockPos> collectAffectedBlocks() {
        if (!world.getGameRules().getBoolean(MT5.DESTRUCTION_ENABLED)) return new HashSet<>();
        Set<BlockPos> finalSet = new HashSet<>();
        Set<BlockPos> innerBlocks = collectDirectDamageBlocks();
        finalSet.addAll(innerBlocks);
        finalSet.addAll(collectBlastWaveBlocks(innerBlocks));
        return finalSet;
    }

    protected double getBlockDamageRadius() {
        return power;
    }

    protected double getPowerByDistance(double distance) {
        return Math.min(power, (power / distance) - 1);
    }

    protected Vec3d getBlockExplosionOrigin() {
        return position;
    }

    protected Set<BlockPos> collectDestroyedBlocks() {
        double radius = getBlockDamageRadius();
        Vec3d origin = getBlockExplosionOrigin();
        double step = STEP_SIZE_BLOCKS / radius;
        int amountOfSamples = MathHelper.floor(4 * Math.PI * Math.pow(radius, 3) * SAMPLES_PER_SQUARE_BLOCK / 3);
        Set<BlockPos> blocks = new HashSet<>();
        Spheres.fibonacciSphere(origin, (float) radius, amountOfSamples, (spherePoint) -> {
            double spentPower = 0;
            for (double delta = 0; delta < 1; delta += step) {
                Vec3d workingPos = explosionCenter.lerp(spherePoint, delta);
                BlockPos pos = BlockPos.ofFloored(workingPos);
                if (!world.isInBuildLimit(pos)) return;
                if (blocks.contains(pos)) continue;
                double currentPower = getPowerByDistance(delta * radius) - spentPower;
                if (currentPower < 0) return;
                BlockState blockState = world.getBlockState(pos);
                FluidState fluidState = world.getFluidState(pos);
                if (blockState.isAir() && fluidState.isEmpty()) continue;
                float blastResistance = getBlastResistance(blockState, fluidState);
                if (blastResistance > currentPower) return;
                blocks.add(pos);
                spentPower += blastResistance;
            }
        });
        return new HashSet<>();
    }

    @Deprecated
    protected double getDirectImpactRadius() {
        return Math.cbrt(power);
    }

    @Deprecated
    protected Set<BlockPos> collectDirectDamageBlocks() {
        double innerExplosionRadius = getDirectImpactRadius();
        double step = STEP_SIZE_BLOCKS / innerExplosionRadius;
        int amountOfSamples = MathHelper.floor(4 * Math.PI * Math.pow(innerExplosionRadius, 3) * SAMPLES_PER_SQUARE_BLOCK / 3);
        Set<BlockPos> blocks = new HashSet<>();
        Spheres.heightParametrizedFibonacciSphere(position, (float) innerExplosionRadius, 0.4f, amountOfSamples, (spherePoint) -> {
            double powerRemaining = power;
            double powerStep = power * step;
            for (double delta = 0; delta < 1; delta += step) {
                BlockPos pos = BlockPos.ofFloored(explosionCenter.lerp(spherePoint, delta));
                powerRemaining -= powerStep;
                if (powerRemaining <= 0) return;
                if (!world.isInBuildLimit(pos)) return;
                if (blocks.contains(pos)) continue;
                BlockState blockState = world.getBlockState(pos);
                FluidState fluidState = world.getFluidState(pos);
                if (blockState.isAir() && fluidState.isEmpty()) continue;
                float blastResistance = getBlastResistance(blockState, fluidState);
                if (powerRemaining >= blastResistance) {
                    blocks.add(pos);
                    powerRemaining = Math.max(0, powerRemaining - blastResistance);
                }
            }
        });
        return blocks;
    }

    @Deprecated
    protected double getBlastWaveRadius() {
        return power / 1.7f;
    }

    @Deprecated
    protected Set<BlockPos> collectBlastWaveBlocks(Set<BlockPos> removedBlocks) {
        double outerExplosionRadius = getBlastWaveRadius();
        double step = STEP_SIZE_BLOCKS / outerExplosionRadius;
        int amountOfSamples = MathHelper.floor(4 * Math.PI * Math.pow(outerExplosionRadius, 3) * SAMPLES_PER_SQUARE_BLOCK / 3);
        Set<BlockPos> blocks = new HashSet<>();
        Spheres.fibonacciSphere(explosionCenter, (float) outerExplosionRadius, amountOfSamples, (spherePoint) -> {
            for (double delta = 0; delta < 1; delta += step) {
                BlockPos pos = BlockPos.ofFloored(explosionCenter.lerp(spherePoint, delta));
                BlockState state = world.getBlockState(pos);
                boolean isBrittle = state.isIn(ModBlockTags.BRITTLE);
                boolean isRemoved = removedBlocks.contains(pos);
                if (!isBrittle && !isRemoved && !state.isAir()) return;
                if (isRemoved) continue;
                if (state.isAir()) continue;
                if (RANDOM.nextDouble(0.99 - delta, 1) > 0.5) blocks.add(pos);
            }
        });
        return blocks;
    }

    protected float getBlastResistance(BlockState blockState, FluidState fluidState) {
        return Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance());
    }

    protected double getEntityDamageRadius() {
        return power * 1.5;
    }

    protected Vec3d getEntityDamageOrigin() {
        return explosionCenter;
    }

    protected List<EntityExposureData> collectAffectedEntities(Predicate<Entity> entityPredicate) {
        double entityDamageRange = getEntityDamageRadius();
        Vec3d origin = getEntityDamageOrigin();
        Vec3d edgeVector = new Vec3d(entityDamageRange, entityDamageRange, entityDamageRange);
        List<Entity> entities = world.getOtherEntities(null, new Box(origin.add(edgeVector), origin.subtract(edgeVector)), entityPredicate);
        ArrayList<EntityExposureData> entityList = new ArrayList<>();
        for (Entity entity : entities) {
            Vec3d entityMidPos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()) / 2, 0);
            float distanceSquared = entityMidPos.toVector3f().distanceSquared(origin.toVector3f());
            if (distanceSquared >= entityDamageRange * entityDamageRange) continue;
            float exposure = getExposure(entity.getBoundingBox(), origin, 2, 1, world);
            if (exposure <= 0) continue;
            entityList.add(new EntityExposureData(entity, exposure, distanceSquared, entityMidPos.subtract(origin).normalize()));
        }
        return entityList;
    }

    protected record EntityExposureData(Entity entity, float exposure, float distanceSquared,
                                        Vec3d accelerationVector) {
    }

}
