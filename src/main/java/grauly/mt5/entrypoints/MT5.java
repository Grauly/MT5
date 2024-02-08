package grauly.mt5.entrypoints;

import grauly.mt5.events.WeaponPullEvent;
import grauly.mt5.events.WeaponTriggerReload;
import grauly.mt5.registers.ModItemGroups;
import grauly.mt5.registers.ModItems;
import grauly.mt5.scheduler.PlayerSpeedTask;
import grauly.mt5.scheduler.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MT5 implements ModInitializer {

    public static final String MODID = "mt5";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final TaskScheduler TASK_SCHEDULER = new TaskScheduler();
    public static final PlayerSpeedTask PLAYER_SPEED_TASK = new PlayerSpeedTask();
    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModItemGroups.registerItemGroups();
        PLAYER_SPEED_TASK.startTask(TASK_SCHEDULER,0,1);
        AttackBlockCallback.EVENT.register(WeaponTriggerReload::trigger);
        ServerEntityEvents.EQUIPMENT_CHANGE.register(WeaponPullEvent::pull);
    }
}
