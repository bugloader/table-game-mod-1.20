package com.meacks.table_game;

import com.meacks.table_game.assets.handlers.*;
import com.meacks.table_game.client.gui.MinoTableScreen;
import com.meacks.table_game.client.renderer.MinoLargeTableRenderer;
import com.meacks.table_game.client.renderer.MinoTableExtenderRenderer;
import com.meacks.table_game.client.renderer.MinoTableRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Random;

@Mod(TableGameMod.MODID)
public class TableGameMod {
    public static final String MODID = "table_game";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static Random random = new Random();
    
    
    public TableGameMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
                                                        .getModEventBus();
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        
        // Register the Deferred Register to the mod event bus so blocks get registered
        BlockHandler.BLOCK_DEFERRED_REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ItemHandler.initialize();
        ItemHandler.ITEM_DEFERRED_REGISTER.register(modEventBus);
        
        BlockEntityHandler.BLOCK_ENTITY_DEFERRED_REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CreativeTabHandler.CREATIVE_MODE_TABS.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        SoundHandler.SOUND_EVENT_DEFERRED_REGISTER.register(modEventBus);
        MenuHandler.MENU_DEFERRED_REGISTER.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        
        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM table game SETUP");
    }
    
    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
        
        }
        //event.accept(EXAMPLE_ITEM);
    }
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        
    }
    
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with
    // @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> MenuScreens.register(MenuHandler.TABLE_GAME_MENU.get(), MinoTableScreen::new));
        }
        
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(BlockEntityHandler.minoTableBlockEntity.get(), MinoTableRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityHandler.minoLargeTableBlockEntity.get(),
                                              MinoLargeTableRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityHandler.minoTableExtenderBlockEntity.get(),
                                              MinoTableExtenderRenderer::new);
        }
    }
}
