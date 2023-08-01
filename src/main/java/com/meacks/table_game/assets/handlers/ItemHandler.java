package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.items.MinoHandCard;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ItemHandler {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.ITEMS, TableGameMod.MODID);
    public static final RegistryObject<Item> small_game_table = blockItem(BlockHandler.small_game_table, "small_game_table");
    public static final RegistryObject<Item> mino_hand_card = item(MinoHandCard::new, "mino_hand_card");

    public static RegistryObject<Item> blockItem(RegistryObject<Block> rBlock, String registryName) {
        return ITEM_DEFERRED_REGISTER.register(registryName,
                () -> new BlockItem(rBlock.get(), new Item.Properties()));
    }

    public static RegistryObject<Item> item(Supplier<Item> itemSupplier, String registryName) {
        return ITEM_DEFERRED_REGISTER.register(registryName, itemSupplier);
    }
}
