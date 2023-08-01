package com.meacks.table_game;

import com.meacks.table_game.assets.handlers.BlockEntityHandler;
import com.meacks.table_game.assets.handlers.BlockHandler;
import com.meacks.table_game.assets.handlers.ItemHandler;
import com.meacks.table_game.client.renderer.UnoLargeTableRenderer;
import com.meacks.table_game.client.renderer.UnoTableExtenderRenderer;
import com.meacks.table_game.client.renderer.UnoTableRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.Random;

import static com.meacks.table_game.assets.handlers.SoundHandler.SOUND_EVENT_DEFERRED_REGISTER;

@Mod(TableGameMod.MODID)
public class TableGameMod {
    public static final String MODID="table_game";
    public static Random random = new Random();
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final Item EXAMPLE_ITEM= Items.SNOWBALL;

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("table_game_tab",
            () -> CreativeModeTab.builder().withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(EXAMPLE_ITEM::getDefaultInstance).displayItems((parameters, output) -> {
                output.accept(ItemHandler.small_game_table.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ItemHandler.mino_hand_card.get());
                output.accept(ItemHandler.uno_table.get());
                output.accept(ItemHandler.uno_large_table.get());
                output.accept(ItemHandler.uno_table_extender.get());
            }).build());

    public TableGameMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BlockHandler.BLOCK_DEFERRED_REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ItemHandler.initialize();
        ItemHandler.ITEM_DEFERRED_REGISTER.register(modEventBus);

        BlockEntityHandler.BLOCK_ENTITY_DEFERRED_REGISTER.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);
        // Register ourselves for server and other game events we are interested in
        SOUND_EVENT_DEFERRED_REGISTER.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM table game SETUP");
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
        {

        }
            //event.accept(EXAMPLE_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

        }

        @SubscribeEvent
        public static void onRegisterRenderers (EntityRenderersEvent.RegisterRenderers  event){
            event.registerBlockEntityRenderer(BlockEntityHandler.unoTableBlockEntity.get(), UnoTableRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityHandler.unoLargeTableBlockEntity.get(), UnoLargeTableRenderer::new);
            event.registerBlockEntityRenderer(BlockEntityHandler.unoTableExtenderBlockEntity.get(), UnoTableExtenderRenderer::new);
        }
    }
}
