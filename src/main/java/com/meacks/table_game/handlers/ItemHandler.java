package com.meacks.table_game.handlers;

import com.meacks.table_game.TableGameMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class ItemHandler {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, TableGameMod.MODID);
    public static final RegistryObject<Item> small_game_table = blockItem(BlockHandler.small_game_table,"small_game_table");

    public static RegistryObject<Item> blockItem(RegistryObject<Block> rBlock,String registryName){
        return ItemHandler.ITEM_DEFERRED_REGISTER.register(registryName,
                ()->new BlockItem(rBlock.get(),new Item.Properties()));
    }
}
