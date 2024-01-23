package grauly.mt5.entrypoints;

import grauly.mt5.datagen.MT5BlockTagGenerator;
import grauly.mt5.datagen.MT5DamageTypeTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MT5Datagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(MT5DamageTypeTagGenerator::new);
        pack.addProvider(MT5BlockTagGenerator::new);
    }
}
