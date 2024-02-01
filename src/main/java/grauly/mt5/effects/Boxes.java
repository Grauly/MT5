package grauly.mt5.effects;

import grauly.mt5.helpers.ParticleHelper;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.function.Consumer;

public class Boxes {
    public static void box(Box box, Consumer<Vec3d> pointAction) {
        pointAction.accept(new Vec3d(box.maxX, box.maxY, box.maxZ));
        pointAction.accept(new Vec3d(box.maxX, box.maxY, box.minZ));
        pointAction.accept(new Vec3d(box.maxX, box.minY, box.maxZ));
        pointAction.accept(new Vec3d(box.maxX, box.minY, box.minZ));
        pointAction.accept(new Vec3d(box.minX, box.maxY, box.maxZ));
        pointAction.accept(new Vec3d(box.minX, box.maxY, box.minZ));
        pointAction.accept(new Vec3d(box.minX, box.minY, box.maxZ));
        pointAction.accept(new Vec3d(box.minX, box.minY, box.minZ));
    }

    public static void box(Box box, ServerWorld serverWorld, ParticleEffect effect) {
        box(box,pos -> ParticleHelper.spawnParticle(serverWorld,effect,pos,0,new Vec3d(0,0,0),0));
    }
}
