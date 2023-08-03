package com.meacks.table_game.assets.handlers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Random;

import static com.meacks.table_game.TableGameMod.random;

public class EntityHandler {
    public static final ItemStack firework = Items.FIREWORK_ROCKET.getDefaultInstance();
    public static final int[][] colorLists = {{6737151,16756938},{16777215,16777215},{5635925,5635925}
            ,{6737151,6737151},{16756938,16756938},{16753920,16753920},{15616811,15616811}};

    public static CompoundTag fireworksTag(){
      ListTag explosionsList = new ListTag();
      CompoundTag explosionsTag = new CompoundTag();
      explosionsTag.putByte("Type", (byte) random.nextInt(0,4));
      explosionsTag.putByte("Flicker", (byte) random.nextInt(0,1));
      explosionsTag.putByte("Trail", (byte) random.nextInt(0,1));
      explosionsTag.putIntArray("Colors", colorLists[random.nextInt(0,7)]);
      explosionsTag.putIntArray("FadeColors", colorLists[random.nextInt(0,7)]);
      explosionsList.add(explosionsTag);
      CompoundTag temp = new CompoundTag();
      temp.putInt("Flight", random.nextInt(1,3));
      temp.put("Explosions", explosionsList);
      CompoundTag result = firework.getOrCreateTag();
      result.put("Fireworks", temp);
      return result;
    }

    public static final int[] LifeTimes = {15,20,25};
    public static CompoundTag lifeTimeNBT(){
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("LifeTime",LifeTimes[random.nextInt(0,3)]);
        return nbt;
    }
    public static void summonFireWork(UseOnContext useOnContext){
        Level level = useOnContext.getLevel();
        Player player = useOnContext.getPlayer();
        assert player!=null;
        firework.setTag(fireworksTag());
        FireworkRocketEntity fireworkRocketEntity=new FireworkRocketEntity(level,
                player.getX()+random.nextDouble()-0.5
                ,player.getY(),player.getZ()+random.nextDouble()-0.5,firework);
        fireworkRocketEntity.addAdditionalSaveData(lifeTimeNBT());
        level.addFreshEntity(fireworkRocketEntity);

    }
}
