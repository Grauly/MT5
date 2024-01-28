package grauly.mt5.events;

import grauly.mt5.weapons.WeaponItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class WeaponPullEvent {
    public static void pull(LivingEntity entity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack newStack) {
        if (!(equipmentSlot.equals(EquipmentSlot.MAINHAND) || equipmentSlot.equals(EquipmentSlot.OFFHAND))) return;
        if (!(entity instanceof PlayerEntity player)) return;
        var newWeapon = newStack.getItem() instanceof WeaponItem weaponItem ? weaponItem : null;
        if (newWeapon == null) return;
        var previousWeapon = previousStack.getItem() instanceof WeaponItem weaponItem ? weaponItem : null;
        if (previousWeapon != null && !previousWeapon.getWeaponUUID(previousStack).equals(newWeapon.getWeaponUUID(newStack)))
            applyCooldown(player, newWeapon);
        if(previousWeapon == null) applyCooldown(player, newWeapon);
    }

    private static void applyCooldown(PlayerEntity player, WeaponItem weaponItem) {
        player.getItemCooldownManager().set(weaponItem, weaponItem.getPullCooldown());
    }
}
