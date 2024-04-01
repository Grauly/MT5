package grauly.mt5.effects.explosion;

import grauly.mt5.effects.Circles;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

import java.util.*;
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

    protected Explosion createDummyExplosion() {
        return new Explosion(world, source, position.getX(), position.getY(), position.getZ(), (float) Math.cbrt(power), false, ExplosionHelper.getExplosionBehavior(world));
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

    protected float calculateImpact(float distanceToCenterSquared) {
        return (float) MathHelper.clamp(((6 * power) / (sqrt(distanceToCenterSquared))) - 6, 0, power);
    }

    protected Set<BlockPos> collectAffectedBlocks() {
        if (!world.getGameRules().getBoolean(MT5.DESTRUCTION_ENABLED)) return new HashSet<>();
        Set<BlockPos> finalSet = new HashSet<>();
        Set<BlockPos> innerBlocks = collectInnerExplosionRadius();
        finalSet.addAll(innerBlocks);
        finalSet.addAll(collectOuterExplosionRadius(innerBlocks));
        return finalSet;
    }

    protected Set<BlockPos> collectInnerExplosionRadius() {
        double innerExplosionRadius = Math.cbrt(power);
        double step = STEP_SIZE_BLOCKS / innerExplosionRadius;
        int amountOfSamples = MathHelper.floor(4 * Math.PI * Math.pow(innerExplosionRadius, 3) * SAMPLES_PER_SQUARE_BLOCK / 3);
        Set<BlockPos> blocks = new HashSet<>();
        Spheres.heightParametrizedFibonacciSphere(position, (float) innerExplosionRadius, 0.4f, amountOfSamples, (spherePoint) -> {
            double powerRemaining = innerExplosionRadius;
            for (double delta = 0; delta < 1; delta += step) {
                BlockPos pos = BlockPos.ofFloored(explosionCenter.lerp(spherePoint, delta));
                powerRemaining -= STEP_SIZE_BLOCKS;
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

    protected Set<BlockPos> collectOuterExplosionRadius(Set<BlockPos> removedBlocks) {
        double outerExplosionRadius = power / 1.7;
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

    protected List<EntityExposureData> collectAffectedEntities(Predicate<Entity> entityPredicate) {
        Vec3d edgeVector = new Vec3d(power, power, power);
        List<Entity> entities = world.getOtherEntities(null, new Box(explosionCenter.add(edgeVector), explosionCenter.subtract(edgeVector)), entityPredicate);
        ArrayList<EntityExposureData> entityList = new ArrayList<>();
        for (Entity entity : entities) {
            Vec3d entityMidPos = entity.getPos().add(0, entity.getEyeHeight(entity.getPose()) / 2, 0);
            float distanceSquared = entityMidPos.toVector3f().distanceSquared(explosionCenter.toVector3f());
            if (distanceSquared >= power * power) continue;
            float exposure = getExposure(entity.getBoundingBox(), explosionCenter, 2, 1, world);
            if (exposure <= 0) continue;
            entityList.add(new EntityExposureData(entity, exposure, distanceSquared, entityMidPos.subtract(explosionCenter).normalize()));
        }
        return entityList;
    }

    protected record EntityExposureData(Entity entity, float exposure, float distanceSquared, Vec3d accelerationVector) {
    }

}
