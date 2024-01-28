package grauly.mt5.scheduler;

import grauly.mt5.weapons.WeaponItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ReloadTask extends Task {
    private final int runTime;
    private final Entity reloader;
    private final ItemStack weaponStack;
    private int timesRun;

    public ReloadTask(ItemStack weaponStack, Entity reloader) {
        this.weaponStack = weaponStack;
        this.runTime = ((WeaponItem) weaponStack.getItem()).getReloadTime();
        this.reloader = reloader;
    }

    @Override
    public void run() {
        if (timesRun == 0) {
            startReload();
        }
        if (reloader instanceof PlayerEntity player) {
            if (!player.getInventory().getMainHandStack().equals(weaponStack)) cancelReload();
        }
        if (timesRun >= runTime) {
            finishReload();
        }
        if (timesRun % 5 == 0) {
            reloadEffect();
        }
        timesRun += 1;
    }

    protected void startReload() {
        if (!(reloader instanceof PlayerEntity player)) return;
        player.getItemCooldownManager().set(weaponStack.getItem(), runTime);
        player.sendMessage(Text.translatable("mt5.text.reloading"), true);
    }

    protected void cancelReload() {
        if (reloader instanceof PlayerEntity player) player.getItemCooldownManager().set(weaponStack.getItem(), 0);
        this.setCanceled(true);
    }

    protected void finishReload() {
        var weaponItem = ((WeaponItem) weaponStack.getItem());
        weaponItem.reload(weaponStack, reloader);
        this.setCanceled(true);
    }

    protected void reloadEffect() {

    }
}
