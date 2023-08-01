package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.blockEntities.UnoLargeTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.UnoTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.UnoTableExtenderBlockEntity;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityHandler {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TableGameMod.MODID);
    public static final RegistryObject<BlockEntityType<UnoTableBlockEntity>> unoTableBlockEntity =
            BLOCK_ENTITY_DEFERRED_REGISTER.register("uno_table", () -> BlockEntityType.Builder.
                    of(UnoTableBlockEntity::new, BlockHandler.uno_table.get()).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<UnoLargeTableBlockEntity>> unoLargeTableBlockEntity =
            BLOCK_ENTITY_DEFERRED_REGISTER.register("uno_large_table", () -> BlockEntityType.Builder.
                    of(UnoLargeTableBlockEntity::new, BlockHandler.uno_large_table.get()).build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<UnoTableExtenderBlockEntity>> unoTableExtenderBlockEntity =
            BLOCK_ENTITY_DEFERRED_REGISTER.register("uno_table_extender", () -> BlockEntityType.Builder.
                    of(UnoTableExtenderBlockEntity::new, BlockHandler.uno_table_extender.get()).build(DSL.remainderType()));

}
