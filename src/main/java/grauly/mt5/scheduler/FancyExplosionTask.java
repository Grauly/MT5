package grauly.mt5.scheduler;

import grauly.mt5.effects.explosion.FancyExplosion;
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
        FancyExplosion.fragments(serverWorld, pos, nor, 15);
        grauly.mt5.effects.explosion.FancyExplosion.fancyBloom(serverWorld, pos, nor, 30);
        grauly.mt5.effects.explosion.FancyExplosion.debrisBloom(serverWorld, pos, nor, 15, displayState);
        grauly.mt5.effects.explosion.FancyExplosion.burst(serverWorld, pos, nor, 150);
        grauly.mt5.effects.explosion.FancyExplosion.smoke(serverWorld, pos, nor, 50);
        grauly.mt5.effects.explosion.FancyExplosion.shockwave(serverWorld, pos, nor, 1);
        this.setCanceled(true);
    }
}
