package grauly.mt5.scheduler;

import grauly.mt5.effects.explosion.ExplosionEffects;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class FancyExplosionTask extends Task {
    private final ServerWorld serverWorld;
    private final Vec3d pos;
    private final Vec3d nor;
    private final BlockState displayState;

    public FancyExplosionTask(ServerWorld serverWorld, Vec3d pos, Vec3d nor, BlockState displayState) {
        this.serverWorld = serverWorld;
        this.pos = pos;
        this.nor = nor;
        this.displayState = displayState;
    }

    @Override
    public void run() {
        ExplosionEffects.fragments(serverWorld, pos, nor, 15);
        ExplosionEffects.fancyBloom(serverWorld, pos, nor, 30);
        ExplosionEffects.debrisBloom(serverWorld, pos, nor, 15, displayState);
        ExplosionEffects.burst(serverWorld, pos, nor, 150);
        ExplosionEffects.smoke(serverWorld, pos, nor, 50);
        ExplosionEffects.shockwave(serverWorld, pos, nor, 1);
        this.setCanceled(true);
    }
}
