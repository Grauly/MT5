package grauly.mt5.scheduler;

import grauly.mt5.effects.Circles;
import grauly.mt5.helpers.ColorHelper;
import grauly.mt5.helpers.ExplosionHelper;
import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class MiningChargeTask extends Task {
    private static final int SPECIAL_ACTION_CYCLE = 15;
    private final ServerWorld world;
    private final Vec3d step;
    private final LivingEntity shooter;
    private final float power;
    private final int maxIterations;
    private Vec3d position;
    private int timesRun;

    public MiningChargeTask(ServerWorld world, Vec3d position, Vec3d step, float maxDistance, LivingEntity shooter, float power) {
        this.world = world;
        this.position = position;
        this.step = step;
        this.shooter = shooter;
        this.power = power;
        maxIterations = (int) (maxDistance / step.length());
    }

    @Override
    public void run() {
        if (timesRun >= maxIterations || (world.getBlockState(BlockPos.ofFloored(position.getX(), position.getY(), position.getZ())).getBlock().getBlastResistance() >= 600)) {
            this.setCanceled(true);
            return;
        }
        if (timesRun % SPECIAL_ACTION_CYCLE == 0) specialAction();
        standardAction();
        position = position.add(step);
        timesRun += 1;
    }

    protected void specialAction() {
        ArrayList<Color> colors = ColorHelper.getAdjacentColorsRGB(Color.CYAN, 3, 5);
        Circles.circle(position, step, power / 1.5f, (int) (power * 4), pos -> {
            ParticleHelper.spawnParticle(world, ParticleHelper.getDustParticle(colors.get(ThreadLocalRandom.current().nextInt(colors.size())), 0.25f), pos, 0, step, 0.1f);
        });
    }

    protected void standardAction() {
        ExplosionHelper.miningExplode(world, position, power, shooter);
        Circles.circle(position, 0.25f, 16, world, ParticleTypes.END_ROD);
    }

}
