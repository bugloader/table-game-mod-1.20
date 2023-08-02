package com.meacks.table_game.assets.handlers;

import com.meacks.table_game.TableGameMod;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Random;

public class SoundHandler {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT_DEFERRED_REGISTER =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, TableGameMod.MODID);
    public static final RegistryObject<SoundEvent> shuffle = SOUND_EVENT_DEFERRED_REGISTER.register("shuffle",
            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(TableGameMod.MODID, "shuffle"),16));
    public static final RegistryObject<SoundEvent> play_card = SOUND_EVENT_DEFERRED_REGISTER.register("play_card",
            () -> SoundEvent.createFixedRangeEvent(new ResourceLocation(TableGameMod.MODID, "play_card"),16));

    public static float[] soundVolume={
            10,//0
            6,//1
            8,//2
            0.3f,//3
            1.5f,//4
            10,//5
            10,//6
            10,//7
            10,//8
            5,//9
    };

    public static void playSound(UseOnContext useOnContext, int soundType){
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        playSound(level,blockPos,soundType);
    }

    public static void playSound(Level level,BlockPos blockPos, int soundType){
        SoundEvent[] soundEvents={
                SoundEvents.EXPERIENCE_ORB_PICKUP,//0
                SoundEvents.PLAYER_LEVELUP,//1
                SoundEvents.ANVIL_FALL,//2
                SoundEvents.ENDER_DRAGON_AMBIENT,//3
                SoundEvents.ENDER_DRAGON_HURT,//4
                SoundEvents.VILLAGER_NO,//5
                SoundEvents.VILLAGER_AMBIENT,//6
                shuffle.get(),//7
                play_card.get(),//8
                SoundEvents.PORTAL_TRAVEL//9
        };
        publicPlaySound(level,soundEvents[soundType],blockPos,soundVolume[soundType]);
    }


    public static void publicPlaySound(Level level,SoundEvent soundEvent,BlockPos blockPos,float volume){
        level.playSound(null, blockPos,soundEvent, SoundSource.PLAYERS, volume, 1f);
    }


}
