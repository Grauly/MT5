package grauly.mt5.weapons;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public interface AmmoType {
    /**
     * called when an entity is hit, but is not a living Entity
     * @param impacted the impacted Entity
     */
    void doEntityImpact(Entity impacted);

    /**
     * called when a living entity is hit
     * @param entity the hit entity, with the weapons damage already applied, unless #overrideDamageLogic returns true
     * @param distance the distance from the shooter
     */
    void doEntityDamageImpact(LivingEntity entity, float distance);

    /**
     * called whenever a shot impacts a block
     * @param world the world that hit happened in
     * @param blockPos the blockPos of the block that was hit
     * @param impact the precise impact location
     */
    void doBlockImpact(ServerWorld world, BlockPos blockPos, Vec3d impact);

    /**
     * called when a shot has been fired
     * @param world the world the shot has been fired in
     * @param firingLocation the precise location the shot was fired at
     * @param direction the direction the shot was fired in
     */
    void doFireAction(ServerWorld world, Vec3d firingLocation, Vec3d direction);

    /**
     * called for every point of the bullet trail
     * @param world the world the trail is in
     * @param position the position of this point in the trail
     * @param trailDirection the direction of that trail
     */
    void doTrailAction(ServerWorld world, Vec3d position, Vec3d trailDirection);

    /**
     * the damage type this ammunition inflicts. For composite Damage types, handle that in doLivingEntityImpact
     * @return the damage type
     */
    DamageType getDamageType();

    /**
     * whether the base weapon damage should be applied
     * @return true if the base weapon damage should not be applies
     */
    boolean overridesDamageLogic();

    /**
     * the amount of entites the shot can pierce
     * @return the amount of entites to pierce
     */
    int getPierceAmount();

    boolean willDestroyBlock(Block block);

}
