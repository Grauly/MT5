package grauly.mt5.scheduler;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;

public class TaskScheduler {
    private final ArrayList<Task> tasks = new ArrayList<>();

    public TaskScheduler() {
        ServerTickEvents.END_SERVER_TICK.register(this::tick);
    }

    private void tick(MinecraftServer minecraftServer) {
        tasks.forEach(Task::tick);
        tasks.removeIf(Task::isCanceled);
    }

}
