package grauly.mt5.registers;

import grauly.mt5.ammotypes.*;
import grauly.mt5.entrypoints.MT5;
import grauly.mt5.items.TestingItem;
import grauly.mt5.throwables.GrenadeItem;
import grauly.mt5.weapons.AmmoTypeItem;
import grauly.mt5.weapons.AutomaticWeaponItem;
import grauly.mt5.weapons.ChargingWeaponItem;
import grauly.mt5.weapons.WeaponItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class ModItems {

    public static final Item TESTING_WAND = register(new TestingItem(new FabricItemSettings().rarity(Rarity.EPIC).fireproof()), "testing_item");
    public static final Item BULLET_AMMO = register(new AmmoTypeItem(ModAmmoTypes.MED_CAL_BULLETS, 35, ModPolymerModels.BULLET_MAG), "bullet_ammo");
    public static final Item EXPLOSION_AMMO = register(new AmmoTypeItem(ModAmmoTypes.MINIATURE_GRENADES, 15, ModPolymerModels.EXPLOSIVE_BULLET_MAG), "explosive_ammo");
    public static final Item REFLECTION_AMMO = register(new AmmoTypeItem(ModAmmoTypes.REFRACTION, 15, ModPolymerModels.ENERGY_MAG), "reflection_ammo");
    public static final Item DELAYED_REFLECTION_AMMO = register(new AmmoTypeItem(ModAmmoTypes.CHASM_CASTER, 3, ModPolymerModels.ENERGY_MAG), "delayed_reflection_ammo");
    public static final Item MINING_CHARGE_AMMO = register(new AmmoTypeItem(ModAmmoTypes.DEMOLITION_CHARGE, 15, ModPolymerModels.ENERGY_MAG), "mining_charge_ammo");
    public static final Item FRAG_GRENADE = register(new GrenadeItem(new FabricItemSettings(), ModGrenadeTypes.SHRAPNEL_GRENADE_TYPE, ModPolymerModels.BASE_GRENADE), "frag_grenade");
    public static final Item SMOKE_GRENADE = register(new GrenadeItem(new FabricItemSettings(), ModGrenadeTypes.SMOKE_GRENADE_TYPE, ModPolymerModels.BASE_GRENADE), "smoke_grenade");
    public static final Item TESTING_RIFLE = register(new WeaponItem(new FabricItemSettings().rarity(Rarity.COMMON),
                    ModPolymerModels.SG,
                    35,
                    12,
                    9,
                    (int) (2.5 * 20),
                    5,
                    5,
                    0.2f),
            "testing_rifle");
    public static final Item TESTING_PISTOL = register(new AutomaticWeaponItem(new FabricItemSettings().rarity(Rarity.COMMON),
                    ModPolymerModels.GLOCK,
                    15,
                    3,
                    35,
                    4 * 20,
                    0,
                    20,
                    0.2f),
            "testing_pistol");
    public static final Item TESTING_CHARGE_RIFLE = register(new ChargingWeaponItem(new FabricItemSettings().rarity(Rarity.UNCOMMON),
                    ModPolymerModels.ENERGY_RIFLE,
                    50,
                    15,
                    5,
                    4 * 20,
                    2 * 20,
                    30,
                    0f,
                    30,
                    true),
            "testing_charger");
    public static final Item SG = register(new AutomaticWeaponItem(new FabricItemSettings().rarity(Rarity.COMMON),
                    ModPolymerModels.SG,
                    80,
                    6,
                    30,
                    56, //2.8*20
                    1,
                    20,
                    0.2f),
            "sg_553");

    public static final Item DEAGLE = register(new WeaponItem(new FabricItemSettings().rarity(Rarity.COMMON),
                    ModPolymerModels.DEAGLE,
                    50,
                    7,
                    7,
                    44,
                    4,
                    10,
                    0.5f),
            "desert_eagle");
    public static final Item GLOCK = register(new WeaponItem(new FabricItemSettings().rarity(Rarity.COMMON),
                    ModPolymerModels.GLOCK,
                    50,
                    6,
                    20,
                    46,
                    3,
                    5,
                    0.5f),
            "glock");
    public static final Item AWP = register(new WeaponItem(new FabricItemSettings().rarity(Rarity.COMMON),
                    ModPolymerModels.AWP,
                    200,
                    21,
                    5,
                    74,
                    26,
                    20,
                    0.1f),
            "awp");
    public static final Item MINING_LASER = register(new ChargingWeaponItem(new FabricItemSettings().rarity(Rarity.EPIC),
                    ModPolymerModels.MINING_LASER,
                    200,
                    50,
                    50,
                    80,
                    30,
                    45,
                    0.1f,
                    10 * 20,
                    true),
            "mining_laser");

    private static Item register(Item item, String id) {
        return Registry.register(Registries.ITEM, new Identifier(MT5.MODID, id), item);
    }

    public static void registerItems() {
        //premium java bullshit, this will be empty, till I actually need to do something here, but I do need to interact with this class for it to load
    }
}
