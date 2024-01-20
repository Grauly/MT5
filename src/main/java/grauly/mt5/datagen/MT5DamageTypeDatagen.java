package grauly.mt5.datagen;

import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;

import java.util.concurrent.CompletableFuture;

public class MT5DamageTypeDatagen implements DataProvider {

    private final String modid;

    public MT5DamageTypeDatagen(String modid) {
        this.modid = modid;
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        return null;
    }

    @Override
    public String getName() {
        return "Damage Type generator for: " + modid;
    }
}
