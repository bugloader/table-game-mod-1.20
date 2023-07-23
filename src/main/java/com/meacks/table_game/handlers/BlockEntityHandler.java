package com.meacks.table_game.handlers;

import com.meacks.table_game.TableGameMod;
import com.meacks.table_game.blockEntities.SmallGameTableBlockEntity;
import com.meacks.table_game.renderer.SmallGameTableRenderer;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class BlockEntityHandler {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TableGameMod.MODID);

    public static final RegistryObject<BlockEntityType<SmallGameTableBlockEntity>> smallGameTableBlockEntity =
            BLOCK_ENTITY_DEFERRED_REGISTER.register("small_game_table", () -> BlockEntityType.Builder.
                    of(SmallGameTableBlockEntity::new, BlockHandler.small_game_table.get()).build(DSL.remainderType()));

}
