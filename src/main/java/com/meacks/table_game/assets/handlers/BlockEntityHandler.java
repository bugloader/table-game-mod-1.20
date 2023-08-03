package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.assets.blockEntities.MinoLargeTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.MinoTableBlockEntity;
import com.meacks.table_game.assets.blockEntities.MinoTableExtenderBlockEntity;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntityHandler {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_DEFERRED_REGISTER = DeferredRegister.create(
        ForgeRegistries.BLOCK_ENTITY_TYPES,
        TableGameMod.MODID);
    public static final RegistryObject<BlockEntityType<MinoTableBlockEntity>> minoTableBlockEntity =
        BLOCK_ENTITY_DEFERRED_REGISTER.register(
        "mino_table",
        () -> BlockEntityType.Builder.of(MinoTableBlockEntity::new, BlockHandler.mino_table.get())
                                     .build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<MinoLargeTableBlockEntity>> minoLargeTableBlockEntity =
        BLOCK_ENTITY_DEFERRED_REGISTER.register(
        "mino_large_table",
        () -> BlockEntityType.Builder.of(MinoLargeTableBlockEntity::new, BlockHandler.mino_large_table.get())
                                     .build(DSL.remainderType()));
    public static final RegistryObject<BlockEntityType<MinoTableExtenderBlockEntity>> minoTableExtenderBlockEntity =
        BLOCK_ENTITY_DEFERRED_REGISTER.register(
        "mino_table_extender",
        () -> BlockEntityType.Builder.of(MinoTableExtenderBlockEntity::new, BlockHandler.mino_table_extender.get())
                                     .build(DSL.remainderType()));
    
}
