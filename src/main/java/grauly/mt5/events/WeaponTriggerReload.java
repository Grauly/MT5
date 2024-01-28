package grauly.mt5.events;

import grauly.mt5.weapons.WeaponItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class WeaponTriggerReload {
    public static ActionResult trigger(PlayerEntity player, World world, Hand hand, BlockPos blockPos, Direction direction) {
        if (player.isSpectator()) return ActionResult.PASS;
        if (player.getStackInHand(hand).getItem() instanceof WeaponItem weaponItem) {
            weaponItem.reloadWeapon(player.getStackInHand(hand), player);
            return ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }
}
