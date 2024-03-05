package grauly.mt5.helpers;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.registers.ModEntityTags;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

public class ExplosionHelper {
    public static void explode(ServerWorld world, Vec3d position, float power, Entity source) {
        world.createExplosion(source, position.getX(), position.getY(), position.getZ(), power, getExplosionSourceType(world));
    }

    public static void miningExplode(ServerWorld world, Vec3d position, float power, Entity source) {
        world.createExplosion(source,
                new DamageSource(world.getDamageSources().registry.entryOf(ModDamageTypes.MINING_CHARGE_DAMAGE)),
                new MiningChargeExplosionBehavior(),
                position,
                power,
                false,
                getExplosionSourceType(world));
    }

    protected static World.ExplosionSourceType getExplosionSourceType(ServerWorld world) {
        return world.getGameRules().getBoolean(MT5.DESTRUCTION_ENABLED) ? World.ExplosionSourceType.BLOCK : World.ExplosionSourceType.NONE;
    }

    protected static class MiningChargeExplosionBehavior extends ExplosionBehavior {
        @Override
        public boolean shouldDamage(Explosion explosion, Entity entity) {
            return !entity.getType().isIn(ModEntityTags.MINING_CHARGE_IMMUNE);
        }
    }
}
