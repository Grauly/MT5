package grauly.mt5.entrypoints;

import grauly.mt5.datagen.MT5BlockTagGenerator;
import grauly.mt5.datagen.MT5EntityTagGenerator;
import grauly.mt5.datagen.MT5ItemTagGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MT5Datagen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(MT5BlockTagGenerator::new);
        pack.addProvider(MT5ItemTagGenerator::new);
        pack.addProvider(MT5EntityTagGenerator::new);
    }
}
