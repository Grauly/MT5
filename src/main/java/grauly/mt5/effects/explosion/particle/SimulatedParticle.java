package grauly.mt5.effects.explosion.particle;

import grauly.mt5.helpers.RaycastHelper;
import grauly.mt5.scheduler.Task;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public abstract class SimulatedParticle extends Task {
    protected final ServerWorld world;
    protected final Vec3d gravity;
    protected Vec3d velocity;
    protected Vec3d position;
    protected float drag;
    protected boolean hasHitGround = false;

    protected int multiRuns = 1;

    public SimulatedParticle(ServerWorld world, Vec3d gravity, Vec3d velocity, Vec3d position, float drag) {
        this.world = world;
        this.gravity = gravity;
        this.velocity = velocity;
        this.position = position;
        this.drag = drag;
    }

    public SimulatedParticle(ServerWorld world, Vec3d gravity, Vec3d velocity, Vec3d position, float drag, int multiRuns) {
        this.world = world;
        this.gravity = gravity;
        this.velocity = velocity;
        this.position = position;
        this.drag = drag;
        this.multiRuns = multiRuns;
    }

    @Override
    public void run() {
        for (int i = 0; i < multiRuns; i++) {
            if(this.isCanceled()) return;
            step();
        }
    }

    protected void step() {
        updateVelocity();
        updatePosition();
        updateExtra();
        visualize();
        if(shouldDie()) this.setCanceled(true);
    }

    protected void updateVelocity() {
        velocity = velocity.add(gravity).multiply(drag);
    };

    protected void updatePosition() {
        if(hasHitGround) return;
        Vec3d oldPos = position;
        position = position.add(velocity);
        //TODO finish
        BlockState state = world.getBlockState(BlockPos.ofFloored(position.getX(),position.getY(),position.getZ()));
        if(state.isAir()) return;
        BlockHitResult blockHitResult = RaycastHelper.rayCastBlock(world, oldPos, position);
        if(blockHitResult.getType() == HitResult.Type.MISS) return;
        position = blockHitResult.getPos();
        if(velocity.getY() > 0) return;
        hasHitGround = true;
    }
    protected abstract void visualize();
    protected abstract void updateExtra();
    protected abstract boolean shouldDie();
}
