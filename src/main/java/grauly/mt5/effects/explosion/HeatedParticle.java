package grauly.mt5.effects.explosion;

import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.scheduler.Task;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class HeatedParticle extends Task {

    protected final ServerWorld world;
    protected final int multiRuns;
    protected final float varianceMultiplier;
    protected Vec3d gravity = new Vec3d(0, -0.05f, 0);
    protected Vec3d velocity;
    protected Vec3d position;
    protected float drag;
    protected float temperature;
    protected float cooling;
    protected boolean hitGround;


    public HeatedParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling) {
        this.world = world;
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.drag = slowdownFactor;
        this.temperature = initialTemperature;
        this.cooling = cooling;
        this.multiRuns = 1;
        varianceMultiplier = 1;
    }

    public HeatedParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling, int multiRuns) {
        this.world = world;
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.drag = slowdownFactor;
        this.temperature = initialTemperature;
        this.cooling = cooling;
        this.multiRuns = multiRuns;
        varianceMultiplier = 1;
    }

    public HeatedParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling, float variance) {
        this.world = world;
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.drag = slowdownFactor;
        this.temperature = initialTemperature;
        this.cooling = cooling;
        this.multiRuns = 1;
        varianceMultiplier = variance;
    }

    public HeatedParticle(ServerWorld world, Vec3d initialPosition, Vec3d initialVelocity, float slowdownFactor, float initialTemperature, float cooling, float variance, int multiRuns) {
        this.world = world;
        this.position = initialPosition;
        this.velocity = initialVelocity;
        this.drag = slowdownFactor;
        this.temperature = initialTemperature;
        this.cooling = cooling;
        this.multiRuns = multiRuns;
        varianceMultiplier = variance;
    }

    @Override
    public void run() {
        for (int i = 0; i < multiRuns; i++) {
            actuallyRun();
        }
    }

    protected void actuallyRun() {
        if (!hitGround) {
            velocity = velocity.add(gravity).multiply(drag);
            Vec3d oldPos = position;
            position = position.add(velocity);
            if (!world.getBlockState(BlockPos.ofFloored(position.getX(), position.getY(), position.getZ())).isAir()) {
                BlockHitResult blockHitResult = RaycastHelper.rayCastBlock(world, oldPos, velocity, (float) velocity.length() * 1.2f);
                if (blockHitResult.getType() != HitResult.Type.MISS) {
                    position = blockHitResult.getPos();
                    velocity = new Vec3d(0, 0, 0);
                    gravity = new Vec3d(0, 0, 0);
                    hitGround = true;
                }
            }
        }

        temperature -= cooling;
        visualize();
        if (temperature <= 0) {
            this.setCanceled(true);
        }
    }

    protected void visualize() {
        ExplosionHeatVisualizers.fieryDefaultVisuals.get((int) temperature).display(world, position, 1, true);
    }
}
