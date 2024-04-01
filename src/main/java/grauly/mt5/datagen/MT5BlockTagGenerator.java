package grauly.mt5.datagen;

import grauly.mt5.registers.ModBlockTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class MT5BlockTagGenerator extends FabricTagProvider.BlockTagProvider {
    public MT5BlockTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ModBlockTags.BRITTLE)
                .add(Blocks.SUGAR_CANE)
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.TALL_GRASS)
                .add(Blocks.DEAD_BUSH)
                .add(Blocks.TORCH)
                .add(Blocks.SOUL_TORCH)
                .add(Blocks.FERN)
                .add(Blocks.LARGE_FERN)
                .forceAddTag(TagKey.of(RegistryKeys.BLOCK, new Identifier("c", "glass_panes")))
                .forceAddTag(BlockTags.LEAVES)
                .forceAddTag(BlockTags.FLOWERS)
                .forceAddTag(BlockTags.BAMBOO_BLOCKS)
                .forceAddTag(BlockTags.ALL_HANGING_SIGNS)
                .forceAddTag(BlockTags.BANNERS)
                .forceAddTag(BlockTags.CANDLES)
                .forceAddTag(BlockTags.CROPS)
                .forceAddTag(BlockTags.FIRE)
                .forceAddTag(BlockTags.FLOWER_POTS);
    }

    
}
