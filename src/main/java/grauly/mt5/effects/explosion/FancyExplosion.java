package grauly.mt5.effects.explosion;

import grauly.mt5.effects.Spheres;
import grauly.mt5.helpers.ExplosionHelper;
import grauly.mt5.helpers.ParticleHelper;
import grauly.mt5.helpers.RaycastHelper;
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

public abstract class FancyExplosion {

    public static final int SAMPLES_PER_SQUARE_BLOCK = 1;
    public static final float STEP_SIZE_BLOCKS = 0.3f;
    protected static final Random RANDOM = new Random();
    protected final float explosionPower;
    protected final float explosionRange;
    protected final float entityDamage;
    protected final float entityRange;
    protected final float visualRange;
    protected final Vec3d position;
    protected final Vec3d direction;
    protected final Vec3d entityExplosionCenter;
    protected final ServerWorld world;
    protected final Explosion dummyExplosion;
    protected final Entity explosionSourceEntity;
    protected final DamageSource damageSource;
    protected Vec3d visualDirection;

    public FancyExplosion(float explosionPower, float explosionRange, Vec3d position, Vec3d direction, ServerWorld world) {
        this.explosionPower = explosionPower;
        this.explosionRange = explosionRange;
        this.position = position;
        this.direction = direction;
        this.world = world;
        entityExplosionCenter = position.add(direction.multiply(explosionPower / 30));
        explosionSourceEntity = null;
        damageSource = Explosion.createDamageSource(world, explosionSourceEntity);
        dummyExplosion = createDummyExplosion();
        entityDamage = 4 * explosionPower;
        entityRange = 1.5f * explosionRange;
        visualDirection = direction;
        visualRange = explosionRange;
    }

    public FancyExplosion(float explosionPower, float entityDamage, float explosionRange, float entityRange, float visualRange, Vec3d visualDirection, Vec3d position, Vec3d direction, ServerWorld world, Entity explosionSourceEntity) {
        this.explosionPower = explosionPower;
        this.entityDamage = entityDamage;
        this.explosionRange = explosionRange;
        this.entityRange = entityRange;
        this.visualRange = visualRange;
        this.visualDirection = visualDirection;
        this.direction = direction.normalize();
        this.position = position;
        this.entityExplosionCenter = position.add(direction.multiply(explosionPower / 30));
        this.world = world;
        this.explosionSourceEntity = explosionSourceEntity;
        this.damageSource = Explosion.createDamageSource(world, explosionSourceEntity);
        dummyExplosion = createDummyExplosion();
    }

    public FancyExplosion(float explosionPower, float entityDamage, float explosionRange, float entityRange, float visualRange, Vec3d visualDirection, Vec3d position, Vec3d direction, ServerWorld world, Entity explosionSourceEntity, DamageSource damageSource) {
        this.explosionPower = explosionPower;
        this.entityDamage = entityDamage;
        this.explosionRange = explosionRange;
        this.entityRange = entityRange;
        this.visualRange = visualRange;
        this.visualDirection = visualDirection;
        this.direction = direction.normalize();
        this.position = position;
        this.entityExplosionCenter = position.add(direction.multiply(explosionPower / 30));
        this.world = world;
        this.explosionSourceEntity = explosionSourceEntity;
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
        return new Explosion(world, explosionSourceEntity, position.getX(), position.getY(), position.getZ(), (float) Math.cbrt(explosionPower), false, ExplosionHelper.getExplosionBehavior(world));
    }

    public void setOff() {
        explode();
        visualize();
    }

    public abstract void explode();

    public abstract void visualize();

    protected double getPowerByDistance(double distance) {
        return Math.min(explosionPower, (explosionPower / distance) - 1);
    }

    protected float getBlastResistance(BlockState blockState, FluidState fluidState) {
        return Math.max(blockState.getBlock().getBlastResistance(), fluidState.getBlastResistance());
    }

    protected double getBlockDamageRadius() {
        return explosionRange;
    }

    protected Vec3d getBlockExplosionOrigin() {
        return position;
    }

    protected BlockExplosionData collectDestroyedBlocks() {
        double radius = getBlockDamageRadius();
        Vec3d origin = getBlockExplosionOrigin();
        double step = STEP_SIZE_BLOCKS / radius;
        int amountOfSamples = MathHelper.floor(grauly.mt5.helpers.MathHelper.sphereSurface(radius) * SAMPLES_PER_SQUARE_BLOCK / 3);
        Set<BlockPos> blocks = new HashSet<>();
        Set<Vec3d> points = new HashSet<>();
        Spheres.heightParametrizedFibonacciSphere(origin, (float) radius, 0.4f, amountOfSamples, (spherePoint) -> {
            //Cull any air paths, might be a tad expensive tho
            if (RaycastHelper.hasCollisionLineOfSight(world, origin, spherePoint)) {
                points.add(spherePoint);
                return;
            }
            double spentPower = 0;
            for (double delta = 0; delta < 1; delta += step) {
                Vec3d workingPos = origin.lerp(spherePoint, delta);
                BlockPos pos = BlockPos.ofFloored(workingPos);
                if (!world.isInBuildLimit(pos)) {
                    points.add(workingPos);
                    return;
                }
                if (blocks.contains(pos)) continue;
                double currentPower = getPowerByDistance(delta) - spentPower;
                if (currentPower < 0) {
                    points.add(workingPos);
                    return;
                }
                BlockState blockState = world.getBlockState(pos);
                FluidState fluidState = world.getFluidState(pos);
                if (blockState.isAir() && fluidState.isEmpty()) continue;
                float blastResistance = getBlastResistance(blockState, fluidState);
                if (blastResistance >= currentPower) {
                    points.add(workingPos);
                    return;
                }
                blocks.add(pos);
                spentPower += blastResistance;
            }
        });
        return new BlockExplosionData(blocks, getCloudCenter(points));
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

    protected Vec3d getCloudCenter(Set<Vec3d> points) {
        double x = 0;
        double y = 0;
        double z = 0;
        for (Vec3d point : points) {
            x += point.getX();
            y += point.getY();
            z += point.getZ();
        }
        int size = points.size();
        return new Vec3d(x / size, y / size, z / size);
    }

    protected double getEntityDamageRadius() {
        return entityRange;
    }

    protected Vec3d getEntityDamageOrigin() {
        return entityExplosionCenter;
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

    protected void applyEffectsToEntities(List<EntityExposureData> entities) {
        entities.forEach(e -> {
            float impact = calculateImpact(e.distanceSquared) * e.exposure;
            e.entity.damage(damageSource, entityDamage * impact);
            e.entity.setVelocity(e.entity.getVelocity().add(e.accelerationVector.multiply(explosionPower * impact)));
        });
    }

    protected float calculateImpact(float distanceToCenterSquared) {
        double distance = Math.sqrt(distanceToCenterSquared);
        if (distance > Math.max(explosionRange, entityRange)) return 0;
        if (distance < Math.min(explosionRange, entityRange)) return 1;
        return (float) Math.pow((distance - entityRange) / (-explosionRange + entityRange), 2);
    }

    protected record EntityExposureData(Entity entity, float exposure, float distanceSquared,
                                        Vec3d accelerationVector) {
    }

    protected record BlockExplosionData(Set<BlockPos> blocks, Vec3d cloudCenter) {

    }

}
