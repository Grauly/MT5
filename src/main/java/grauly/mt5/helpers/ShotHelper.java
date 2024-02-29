package grauly.mt5.helpers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ShotHelper {
    public static final float HEAD_SIZE_RADIUS = 0.125f;
    public static final float WEAPON_LENIENCE = 0.1f;
    public static final float ENTITY_SPEED_MODIFIER = 5f;

    public static boolean isHeadShot(LivingEntity hit, Vec3d shotOrigin, Vec3d shotVector, float maxRange) {
        Vec3d headBoxCenter = hit.getEyePos();
        Vec3d headBoxSize = new Vec3d(HEAD_SIZE_RADIUS, HEAD_SIZE_RADIUS, HEAD_SIZE_RADIUS);
        Box headBox = new Box(headBoxCenter.add(headBoxSize), headBoxCenter.subtract(headBoxSize)).expand(WEAPON_LENIENCE);
        Vec3d fullShotVector = shotVector.normalize().multiply(maxRange);
        Optional<Vec3d> headHit = headBox.raycast(shotOrigin, shotOrigin.add(fullShotVector));
        return headHit.isPresent();
    }

    public static Vec3d getShotVector(LivingEntity shooter, Vec3d baseVector, float weaponBaseSpread) {
        float speedModifier = shooter instanceof ServerPlayerEntity player ? MT5.PLAYER_SPEED_TASK.getPlayerSpeed(player.getUuid()) : ENTITY_SPEED_MODIFIER;
        var stabilityModifier = shooter.isSneaking() ? -3 : 0;
        stabilityModifier += shooter.isFallFlying() ? 5 : 0;
        stabilityModifier += shooter.isClimbing() ? 2 : 0;
        stabilityModifier += shooter.isOnGround() ? 0 : 5;
        return MathHelper.spreadShot(baseVector, Math.max(0, weaponBaseSpread + speedModifier + stabilityModifier));
    }
}
