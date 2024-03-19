package grauly.mt5.throwables;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface GrenadeType {
    int getFuseTimeTicks();
    boolean explodeOnImpact();
    void explode(ServerWorld world, Vec3d position, ServerPlayerEntity thrower);
    default float getBounceVelocityMultiplier() {return 0.2f;}
    default void tick(GrenadeProjectileEntity grenade) {}
    default void onBounce(GrenadeProjectileEntity grenade) {}
    default void onThrow(GrenadeProjectileEntity grenade) {}
}
