package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.blockEntities.MinoTableBlockEntity;
import com.meacks.table_game.assets.blocks.MinoTable;
import com.meacks.table_game.common.inventory.MinoTableMenu;
import com.mojang.datafixers.DSL;
import net.minecraft.stats.StatType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class MenuHandler {
        public static final DeferredRegister<MenuType<?>> MENU_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, TableGameMod.MODID);

        public static final RegistryObject<MenuType<MinoTableMenu>> TABLE_GAME_MENU =
                MENU_DEFERRED_REGISTER.register("mino_table_menu", () -> new MenuType(MinoTableMenu::new, FeatureFlags.DEFAULT_FLAGS));


}
