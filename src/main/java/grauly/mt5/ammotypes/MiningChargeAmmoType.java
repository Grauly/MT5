package grauly.mt5.ammotypes;

import grauly.mt5.entrypoints.MT5;
import grauly.mt5.registers.ModDamageTypes;
import grauly.mt5.scheduler.MiningChargeTask;
import grauly.mt5.weapons.AmmoType;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class MiningChargeAmmoType implements AmmoType {
    public static final int MINING_CHARGE_DISTANCE = 50;
    public static final int BLOCK_RESOLUTION = 3;
    @Override
    public void doEntityImpact(Entity impacted, Entity shooter, Vec3d exactImpactLocation) {

    }

    @Override
    public void doEntityDamageImpact(LivingEntity entity, LivingEntity shooter, float distance, boolean headshot) {

    }

    @Override
    public void doBlockImpact(ServerWorld world, Entity shooter, BlockPos blockPos, Vec3d exactImpact, Vec3d impactDirection) {

    }

    @Override
    public void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction) {
        new MiningChargeTask(world, firingLocation, direction.normalize().multiply(1f/BLOCK_RESOLUTION), MINING_CHARGE_DISTANCE, shooter, 3.5f).startTask(MT5.TASK_SCHEDULER,0,1);
    }

    @Override
    public boolean overrideFireAction() {
        return true;
    }

    @Override
    public void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection) {

    }

    @Override
    public RegistryKey<DamageType> getDamageType() {
        return ModDamageTypes.MINING_CHARGE_DAMAGE;
    }

    @Override
    public boolean overridesDamageLogic() {
        return true;
    }

    @Override
    public int getPierceAmount() {
        return 200;
    }

    @Override
    public float getMunitionSize() {
        return 10;
    }

    @Override
    public Text getAmmoName() {
        return Text.translatable("mt5.ammotype.mining_charge");
    }

    @Override
    public boolean willDestroyBlock(Block block) {
        return false;
    }

    @Override
    public float getHeadShotMultiplier() {
        return 1;
    }

    @Override
    public Identifier getIdentifier() {
        return new Identifier(MT5.MODID, "mining_charge");
    }
}
