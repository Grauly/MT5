package grauly.mt5.effects.explosion.particle;

import grauly.mt5.effects.explosion.DustParticleVisual;
import grauly.mt5.helpers.structures.FallThroughMap;
import grauly.mt5.effects.explosion.ParticleVisual;
import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.scheduler.Task;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.awt.*;

public class HeatAwareParticle extends Task {
    public static final Vec3d BASE_GRAVITY = new Vec3d(0, -0.2, 0);
    public static final Vec3d BASE_BUOYANCY = new Vec3d(0, 0.1f, 0);
    public static final float BASE_BUOYANCY_TEMP_MULTIPLIER = 0.1f;
    protected final ServerWorld world;
    protected Vec3d position;
    protected Vec3d gravity;
    protected Vec3d velocity;
    protected float drag;
    protected float temperature;
    protected Vec3d buoyancy;
    protected float buoyancyTempMultiplier;
    protected float cooling;
    protected int multiRuns;
    protected float variance;
    protected boolean hasHitGround;

    public HeatAwareParticle(ServerWorld world, Vec3d position, Vec3d velocity, float drag, Vec3d gravity, float temperature, float cooling, Vec3d buoyancy, float buoyancyTempMultiplier, int multiRuns, float variance) {
        this.world = world;
        this.position = position;
        this.gravity = gravity;
        this.velocity = velocity;
        this.drag = drag;
        this.temperature = temperature;
        this.buoyancy = buoyancy;
        this.buoyancyTempMultiplier = buoyancyTempMultiplier;
        this.cooling = cooling;
        this.multiRuns = multiRuns;
        this.variance = variance;
    }

    public HeatAwareParticle(ServerWorld world, Vec3d position, Vec3d velocity, float temperature, float cooling, int multiRuns, float variance) {
        this.world = world;
        this.position = position;
        this.gravity = BASE_GRAVITY;
        this.velocity = velocity;
        this.temperature = temperature;
        this.buoyancy = BASE_BUOYANCY;
        this.buoyancyTempMultiplier = BASE_BUOYANCY_TEMP_MULTIPLIER;
        this.cooling = cooling;
        this.multiRuns = multiRuns;
        this.variance = variance;
    }

    public HeatAwareParticle(ServerWorld world, Vec3d position, Vec3d velocity, float temperature, float cooling) {
        this.world = world;
        this.position = position;
        this.gravity = BASE_GRAVITY;
        this.velocity = velocity;
        this.temperature = temperature;
        this.buoyancy = BASE_BUOYANCY;
        this.buoyancyTempMultiplier = BASE_BUOYANCY_TEMP_MULTIPLIER;
        this.cooling = cooling;
        this.multiRuns = 1;
        this.variance = 1;
    }

    @Override
    public void run() {
        for (int i = 0; i < multiRuns; i++) {
            actuallyRun();
        }
    }

    protected void actuallyRun() {
        updatePosition();
        temperature -= cooling;
        visualize();
        if (temperature < 0) this.setCanceled(true);
    }

    protected void updatePosition() {
        if (hasHitGround) return;
        velocity = velocity.add(gravity).add(buoyancy.multiply(Math.max(0, buoyancyTempMultiplier * (temperature)))).multiply(drag);
        Vec3d oldPos = position;
        position = position.add(velocity);
        if (world.getBlockState(BlockPos.ofFloored(position.getX(), position.getY(), position.getZ())).isAir()) return;
        BlockHitResult result = RaycastHelper.rayCastBlock(world, oldPos, velocity, (float) velocity.lengthSquared());
        if (result.getType() == HitResult.Type.MISS) return;
        if (result.getSide() == Direction.UP) hasHitGround = true;
        position = result.getPos();
    }

    protected void visualize() {
        FallThroughMap<ParticleVisual> fieryFancyVisuals = new FallThroughMap<>();

        fieryFancyVisuals.add(7, new ParticleVisual(ParticleTypes.FLAME, 3, 0.05f, new Vec3d(0.05, 0.05, 0.05)));
        fieryFancyVisuals.add(5, new DustParticleVisual(0.25f, 0.75f, Color.ORANGE, 5, 1, new Vec3d(0.03, 0.03, 0.03)));
        fieryFancyVisuals.add(3, new DustParticleVisual(0.25f, 0.75f, Color.DARK_GRAY, 3, 1, new Vec3d(0.03, 0.03, 0.03)));
        fieryFancyVisuals.add(0, new ParticleVisual(ParticleTypes.ASH, 3, 0.5f, new Vec3d(0.01, 0.01, 0.01)));
        fieryFancyVisuals.setDefaultElement(new ParticleVisual(ParticleTypes.CAMPFIRE_COSY_SMOKE, 0, 0.5f, new Vec3d(0, 0.03, 0)));

        fieryFancyVisuals.get((int) Math.floor(temperature)).display(world, position, 1, true);
        //ParticleHelper.spawnParticle(world, ParticleTypes.DRAGON_BREATH, position, 0, new Vec3d(0,0,0), 0);
    }
}
