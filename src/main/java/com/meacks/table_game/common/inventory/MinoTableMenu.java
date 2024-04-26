package com.meacks.table_game.common.inventory;

import com.meacks.table_game.assets.handlers.ItemHandler;
import com.meacks.table_game.assets.handlers.MenuHandler;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MinoTableMenu extends AbstractContainerMenu {
    public static final int CONTAINER_SIZE = 1;
    private final Container minoTable;
    
    public MinoTableMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv, new SimpleContainer(1));
    }
    
    public MinoTableMenu(int containerId, Inventory playerInv, Container container) {
        super(MenuHandler.TABLE_GAME_MENU.get(), containerId);
        this.minoTable = container;
        checkContainerSize(container, 1);
        container.startOpen(playerInv.player);
        int i = 51;
        
        // White card slot
        this.addSlot(new Slot(container, 0, 9, 30){
            @Override
            public boolean mayPlace(@NotNull ItemStack pStack) {
                return pStack.is(ItemHandler.mino_control_card.get());
            }
        });
        
        for (int l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInv, k + l * 9 + 9, 25 + k * 18, 70 + l * 18));
            }
        }
        
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(playerInv, i1, 25 + i1 * 18, 128));
        }
        
    }
    
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot_id) {
        var itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slot_id);
        if (slot.hasItem()) {
            var itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (slot_id < this.minoTable.getContainerSize()) {
                if (!this.moveItemStackTo(itemstack1, this.minoTable.getContainerSize(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.minoTable.getContainerSize(), false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        
        return itemstack;
    }
    
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.minoTable.stopOpen(player);
    }
    
    public boolean stillValid(@NotNull Player player) {
        return this.minoTable.stillValid(player);
    }
}
