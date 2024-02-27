package grauly.mt5.effects;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

public class Splashes {
    public static void splash(ServerWorld world, Vec3d position, Vec3d splashNormal, ParticleEffect particle, int amount){
        for (int i = 0; i < amount; i++) {
            Vec3d direction = splashNormal.add(ThreadLocalRandom.current().nextFloat(-0.1f,0.1f), 1,ThreadLocalRandom.current().nextFloat(-0.1f,0.1f));
            ParticleHelper.spawnParticle(world,particle,position,0,direction,0.5);
        }
    }
}
