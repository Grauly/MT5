package grauly.mt5.registers;

import grauly.mt5.entrypoints.MT5;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModBlockTags {

    public static final TagKey<Block> BRITTLE = tag("brittle");

    private static TagKey<Block> tag(String key) {
        return TagKey.of(RegistryKeys.BLOCK, new Identifier(MT5.MODID, key));
    }
}
