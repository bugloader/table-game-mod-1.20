package com.meacks.table_game.handlers;


import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.blocks.SmallGameTable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class BlockHandler {
    public static final DeferredRegister<Block> BLOCK_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCKS, TableGameMod.MODID);

    public static final RegistryObject<Block> small_game_table = block(SmallGameTable::new, "small_game_table");

    public static RegistryObject<Block> block(BlockBehaviour.Properties properties, String registryName) {
        return BLOCK_DEFERRED_REGISTER.register(registryName, () -> new Block(properties));
    }

    public static RegistryObject<Block> block(Supplier<Block> blockSupplier, String registryName) {
        return BLOCK_DEFERRED_REGISTER.register(registryName, blockSupplier);
    }

    public static boolean areSameBlockType(Block block1, Block block2) {
        return block1 == block2;
    }

}