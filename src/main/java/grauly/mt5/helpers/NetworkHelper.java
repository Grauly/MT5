package grauly.mt5.helpers;


import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class NetworkHelper {
    public static void reSyncState(PlayerEntity user, Hand hand, ItemStack weaponStack) {
        int slot = hand == Hand.MAIN_HAND ? user.getInventory().selectedSlot : PlayerInventory.OFF_HAND_SLOT;
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            serverPlayerEntity.networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, slot, weaponStack));
        }
    }
}
