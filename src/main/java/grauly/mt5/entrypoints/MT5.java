package grauly.mt5.entrypoints;

import grauly.mt5.registers.ModItemGroups;
import grauly.mt5.registers.ModItems;
import grauly.mt5.scheduler.TaskScheduler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MT5 implements ModInitializer {

    public static final String MODID = "mt5";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final TaskScheduler TASK_SCHEDULER = new TaskScheduler();
    @Override
    public void onInitialize() {
        ModItems.registerItems();
        ModItemGroups.registerItemGroups();
    }
}
