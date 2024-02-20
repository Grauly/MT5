package grauly.mt5.scheduler;

import com.google.common.collect.EvictingQueue;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.PlayerManager;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class PlayerSpeedTask extends Task {
    public static final int RECORDED_ELEMENTS = 20;
    private final HashMap<UUID, EvictingQueue<Vec3d>> speedMap = new HashMap<>();
    private PlayerManager playerManager;
    public PlayerSpeedTask() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> this.playerManager = server.getPlayerManager());
        ServerPlayConnectionEvents.DISCONNECT.register((serverPlayNetworkHandler, server) -> speedMap.remove(serverPlayNetworkHandler.getPlayer().getUuid()));
    }
    @Override
    public void run() {
        playerManager.getPlayerList().forEach(p -> {
            if(speedMap.containsKey(p.getUuid())) {
                speedMap.get(p.getUuid()).add(p.getPos());
            } else {
                speedMap.put(p.getUuid(),EvictingQueue.create(RECORDED_ELEMENTS));
            }
        });
    }

    public float getPlayerSpeed(UUID playerUUID) {
        if(!speedMap.containsKey(playerUUID)) return 0;
        Vec3d[] previousPositions = speedMap.get(playerUUID).toArray(new Vec3d[RECORDED_ELEMENTS]);
        ArrayList<Double> speedsSquared = new ArrayList<>();
        for (int i = 1; i < previousPositions.length; i++) {
            speedsSquared.add(previousPositions[i].squaredDistanceTo(previousPositions[i-1]));
        }
        Collections.sort(speedsSquared);
        return (float) Math.sqrt(getMedian(speedsSquared)) * 20;
    }

    private Double getMedian(ArrayList<Double> speeds) {
        return speeds.get((int) Math.floor(speeds.size()/2f));
    }
}
