package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.items.CardShape;
import com.meacks.table_game.assets.items.MinoHandCard;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.meacks.table_game.assets.items.MinoHandCard.CARD_TYPES_COUNT;
import static com.meacks.table_game.assets.items.MinoHandCard.getCardName;

public class ItemHandler {
    public static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS,
                                                                                                TableGameMod.MODID);
    public static final RegistryObject<Item> small_game_table = blockItem(BlockHandler.small_game_table,
                                                                          "small_game_table");
    public static final RegistryObject<Item> mino_table_yellow = blockItem(BlockHandler.mino_table_yellow,
                                                                           "mino_table_yellow");
    public static final RegistryObject<Item> mino_large_table_yellow = blockItem(BlockHandler.mino_large_table_yellow,
                                                                                 "mino_large_table_yellow");
    public static final RegistryObject<Item> mino_table = blockItem(BlockHandler.mino_table, "mino_table");
    public static final RegistryObject<Item> mino_large_table = blockItem(BlockHandler.mino_large_table,
                                                                          "mino_large_table");
    public static final RegistryObject<Item> mino_table_extender = blockItem(BlockHandler.mino_table_extender,
                                                                             "mino_table_extender");
    public static final RegistryObject<Item> mino_hand_card = item(MinoHandCard::new, "mino_hand_card");
    public static final List<RegistryObject<Item>> mino_card_shapes = new ArrayList<>();
    
    public static RegistryObject<Item> blockItem(RegistryObject<Block> rBlock, String registryName) {
        return ITEM_DEFERRED_REGISTER.register(registryName, () -> new BlockItem(rBlock.get(), new Item.Properties()));
    }
    
    public static void initialize() {
        for (int i = 0; i < CARD_TYPES_COUNT; i++) {
            mino_card_shapes.add(item(CardShape::new, "mino" + getCardName(i)));
        }
    }
    
    public static RegistryObject<Item> item(Supplier<Item> itemSupplier, String registryName) {
        return ITEM_DEFERRED_REGISTER.register(registryName, itemSupplier);
    }
    
    
}
