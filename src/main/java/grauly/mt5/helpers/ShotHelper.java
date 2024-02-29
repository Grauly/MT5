package grauly.mt5.helpers;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ShotHelper {
    public static final float HEAD_SIZE_RADIUS = 0.125f;
    public static final float WEAPON_LENIENCE = 0.1f;

    public static boolean isHeadShot(LivingEntity hit, Vec3d shotOrigin, Vec3d shotVector, float maxRange) {
        Vec3d headBoxCenter = hit.getEyePos();
        Vec3d headBoxSize = new Vec3d(HEAD_SIZE_RADIUS, HEAD_SIZE_RADIUS, HEAD_SIZE_RADIUS);
        Box headBox = new Box(headBoxCenter.add(headBoxSize), headBoxCenter.subtract(headBoxSize)).expand(WEAPON_LENIENCE);
        Vec3d fullShotVector = shotVector.normalize().multiply(maxRange);
        Optional<Vec3d> headHit = headBox.raycast(shotOrigin, shotOrigin.add(fullShotVector));
        return headHit.isPresent();
    }
}
