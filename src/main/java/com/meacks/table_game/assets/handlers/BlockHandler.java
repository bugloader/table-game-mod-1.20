package com.meacks.table_game.assets.handlers;


import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.blocks.SmallGameTable;
import com.meacks.table_game.assets.blocks.UnoLargeTable;
import com.meacks.table_game.assets.blocks.UnoTable;
import com.meacks.table_game.assets.blocks.UnoTableExtender;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockHandler {
    public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TableGameMod.MODID);

    public static final RegistryObject<Block> small_game_table = block(SmallGameTable::new, "small_game_table");
    public static final RegistryObject<Block> uno_table = block(UnoTable::new, "uno_table");
    public static final RegistryObject<Block> uno_large_table = block(UnoLargeTable::new, "uno_large_table");
    public static final RegistryObject<Block> uno_table_extender = block(UnoTableExtender::new, "uno_table_extender");

    public static RegistryObject<Block> block(BlockBehaviour.Properties properties, String registryName) {
        return BLOCK_DEFERRED_REGISTER.register(registryName, () -> new Block(properties));
    }

    public static RegistryObject<Block> block(Supplier<Block> blockSupplier, String registryName) {
        return BLOCK_DEFERRED_REGISTER.register(registryName, blockSupplier);
    }

    public static boolean areSameBlockType(Block block1, Block block2) {
        return block1 == block2;
    }

    public static boolean areSameBlockType(Block block1, RegistryObject<Block> block2) {
        return block1 == block2.get();
    }

    public static boolean areSameBlockType(RegistryObject<Block> block1, Block block2) {
        return block1.get() == block2;
    }

    public static boolean areSameBlockType(RegistryObject<Block> block1, RegistryObject<Block> block2) {
        return block1.get() == block2.get();
    }
}