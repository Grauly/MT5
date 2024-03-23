package grauly.mt5.grenadetypes;

import grauly.mt5.helpers.ExplosionHelper;
import grauly.mt5.throwables.GrenadeType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class FragGrenadeType implements GrenadeType {
    @Override
    public int getFuseTimeTicks() {
        return 35;
    }

    @Override
    public boolean explodeOnImpact() {
        return false;
    }

    @Override
    public void explode(ServerWorld world, Vec3d position, ServerPlayerEntity thrower) {
        ExplosionHelper.shrapnelExplode(world, position, 3, thrower);
    }
}
