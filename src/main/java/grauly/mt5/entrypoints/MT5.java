package grauly.mt5.entrypoints;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import grauly.mt5.events.WeaponPullEvent;
import grauly.mt5.events.WeaponTriggerReload;
import grauly.mt5.registers.*;
import grauly.mt5.scheduler.PlayerSpeedTask;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MT5 implements ModInitializer {

    public static final String MODID = "mt5";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static final PlayerSpeedTask PLAYER_SPEED_TASK = new PlayerSpeedTask();
    public static final GameRules.Key<GameRules.BooleanRule> DESTRUCTION_ENABLED = GameRuleRegistry.register("mt5Destruction", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false));
    @Override
    public void onInitialize() {
        ModRegistries.init();
        ModAmmoTypes.init();
        ModGrenadeTypes.init();
        ModEntityTypes.init();
        ModItems.registerItems();
        ModItemGroups.registerItemGroups();
        PLAYER_SPEED_TASK.startTask(ModSchedulers.MAIN,0,1);
        AttackBlockCallback.EVENT.register(WeaponTriggerReload::trigger);
        ServerEntityEvents.EQUIPMENT_CHANGE.register(WeaponPullEvent::pull);
        PolymerResourcePackUtils.addModAssets(MODID);
        LOGGER.info("Orbital base, reporting in");
    }
}
