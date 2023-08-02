package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTabHandler {
        public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TableGameMod.MODID);


    public static final RegistryObject<CreativeModeTab> TABLE_GAME_TAB = CREATIVE_MODE_TABS.register("table_game_tab",
            () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.table_game_tab"))
                    .icon(Items.SNOWBALL::getDefaultInstance).displayItems((parameters, output) -> {
                output.accept(ItemHandler.small_game_table.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ItemHandler.mino_hand_card.get());
                output.accept(ItemHandler.mino_table.get());
                output.accept(ItemHandler.mino_large_table.get());
                output.accept(ItemHandler.mino_table_extender.get());
            }).build());
}
