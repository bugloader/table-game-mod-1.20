package com.meacks.table_game.items;

import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.world.item.ComplexItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class UnoCard extends ComplexItem {
    public UnoCard(){
        super(new Item.Properties().stacksTo(1));
        //Items.MAP;
        //ItemEntityRenderer
    }
}
