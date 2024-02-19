package grauly.mt5.helpers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ExplosionHelper {
    public static void explode(ServerWorld world, Vec3d position, float power, Entity source) {
        world.createExplosion(source,position.getX(),position.getY(),position.getZ(),power, world.getGameRules().getBoolean(MT5.DESTRUCTION_ENABLED) ? World.ExplosionSourceType.BLOCK : World.ExplosionSourceType.NONE);
    }
}
