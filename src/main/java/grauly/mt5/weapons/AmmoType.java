package grauly.mt5.weapons;

import grauly.mt5.registers.ModRegistries;
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

public interface AmmoType {
    /**
     * called when an entity is hit, before doEntityDamageImpact
     *
     * @param impacted the impacted Entity
     */
    void doEntityImpact(Entity impacted, Entity shooter, Vec3d exactImpactLocation);

    /**
     * called when a living entity is hit
     *
     * @param entity   the hit entity, with the weapons damage already applied, unless #overrideDamageLogic returns true
     * @param shooter  the entity that shot
     * @param distance the distance from the shooter
     * @param headshot
     */
    void doEntityDamageImpact(LivingEntity entity, LivingEntity shooter, float distance, boolean headshot);

    /**
     * called whenever a shot impacts a block
     *
     * @param world       the world that hit happened in
     * @param blockPos    the blockPos of the block that was hit
     * @param exactImpact the precise impact location
     */
    void doBlockImpact(ServerWorld world, Entity shooter, BlockPos blockPos, Vec3d exactImpact, Vec3d impactDirection);

    /**
     * called when a shot has been fired
     *
     * @param world          the world the shot has been fired in
     * @param firingLocation the precise location the shot was fired at
     * @param direction      the direction the shot was fired in
     */
    void doFireAction(LivingEntity shooter, ServerWorld world, Vec3d firingLocation, Vec3d direction);

    /**
     * whether the weapons shot logic should be discarded. This will lead to #doEntityImpact, #doEntityDamageImpact #doBlockImpact #doTrailAction no longer being called by the weapon.
     * #doFireAction will however be called, so handle the logic there
     *
     * @return true if the logic should be overridden
     */
    boolean overrideFireAction();

    /**
     * called for every point of the bullet trail
     *
     * @param world          the world the trail is in
     * @param position       the position of this point in the trail
     * @param trailDirection the direction of that trail
     */
    void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection);

    /**
     * the damage type this ammunition inflicts. For composite Damage types, handle that in doLivingEntityImpact
     *
     * @return the damage type
     */
    RegistryKey<DamageType> getDamageType();

    /**
     * whether the base weapon damage should be applied
     *
     * @return true if the base weapon damage should not be applies
     */
    boolean overridesDamageLogic();

    /**
     * the amount of entites the shot can pierce
     *
     * @return the amount of entites to pierce
     */
    int getPierceAmount();

    /**
     * the size of the munition. used to figure out how many shots a weapon may fire before needing to reload
     *
     * @return a positive float giving the size of the munition
     */
    float getMunitionSize();
    Text getAmmoName();

    boolean willDestroyBlock(Block block);

    float getHeadShotMultiplier();
    default Identifier getIdentifier() {
        return ModRegistries.AMMO_TYPE_REGISTRY.getId(this);
    };
    default boolean isSame(AmmoType other) {
        return other.getIdentifier().equals(getIdentifier());
    }

}
