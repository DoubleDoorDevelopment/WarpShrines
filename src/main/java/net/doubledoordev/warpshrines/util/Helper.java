/*
 * MIT License
 *
 * Copyright (c) 2016 Dries007 & DoubleDoorDevelopment
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.doubledoordev.warpshrines.util;

import com.google.common.collect.Lists;
import net.doubledoordev.warpshrines.WarpShrines;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.ArrayList;

/**
 * @author Dries007
 */
public class Helper
{
    public static int[] parseDimIds(String dimension)
    {
        try
        {
            String[] split = dimension.split(", ?");
            int[] ids = new int[split.length];
            for (int i = 0; i < split.length; i++) ids[i] = Integer.parseInt(split[i]);
            return ids;
        }
        catch (NumberFormatException ignored)
        {
        }
        try
        {
            String[] split = dimension.split(" ?# ?", 2);
            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);
            if (end < start) throw new IllegalArgumentException(end + "  < " + start);

            int[] ids = new int[end - start];
            for (int i = 0; i < ids.length; i++) ids[i] = start + i;
            return ids;
        }
        catch (NumberFormatException ignored)
        {
        }
        throw new IllegalArgumentException(dimension + " isn't a valid dimension range.");
    }

    public static void addToWarpList(EntityPlayer player, Iterable<String> names)
    {
        NBTTagCompound root = player.getEntityData();
        NBTTagCompound persist = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
        NBTTagList list = persist.getTagList(Constants.MOD_ID, NBT.TAG_STRING);
        persist.setTag(Constants.MOD_ID, list);
        for (String name : names) list.appendTag(new NBTTagString(name));
    }

    public static ArrayList<String> getWarpList(EntityPlayer player)
    {
        if (player.canUseCommand(1, "warp")) return Lists.newArrayList(WarpSavedData.get(player).getAllNames());
        NBTTagList list = player.getEntityData().getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG).getTagList(Constants.MOD_ID, NBT.TAG_STRING);
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < list.tagCount(); i++) out.add(list.getStringTagAt(i));
        out.retainAll(WarpSavedData.get(player).getAllNames());
        return out;
    }

    public static int getExperienceForLevel(int level)
    {
        if (level <= 0) return 0;
        if (level < 16) return level * 17;
        if (level < 31) return (int)(1.5 * Math.pow(level, 2) - 29.5 * level + 360);
        return (int)(3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
    }

    public static int getLevelForExperience(int experience)
    {
        int i = 0;
        while (getExperienceForLevel(i) <= experience) i++;
        return i - 1;
    }

    public static int getPlayerXP(EntityPlayer player)
    {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
    }

    public static void addPlayerXP(EntityPlayer player, int amount)
    {
        int experience = getPlayerXP(player) + amount;
        player.experienceTotal = experience;
        player.experienceLevel = getLevelForExperience(experience);
        int expForLevel = getExperienceForLevel(player.experienceLevel);
        player.experience = (float)(experience - expForLevel) / (float)player.xpBarCap();
    }

    public static void chat(ICommandSender target, String message)
    {
        target.sendMessage(new TextComponentString(message));
    }

    public static void chat(ICommandSender target, String message, TextFormatting color)
    {
        target.sendMessage(new TextComponentString(message).setStyle(new Style().setColor(color)));
    }

    private static NBTTagCompound getBackTagParent(EntityPlayer player)
    {
        NBTTagCompound root = player.getEntityData();
        if (WarpShrines.doesBackPersists())
        {
            NBTTagCompound persist = root.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
            root.setTag(EntityPlayer.PERSISTED_NBT_TAG, persist);
            root = persist;
        }
        return root;
    }

    public static WarpPoint getBackWarp(EntityPlayerMP player)
    {
        NBTTagCompound root = getBackTagParent(player);
        if (!root.hasKey(Constants.NBT_KEY_BACK)) return null;
        return WarpPoint.fromNBT(root.getCompoundTag(Constants.NBT_KEY_BACK));
    }

    public static void removeBackWarp(EntityPlayerMP player)
    {
        getBackTagParent(player).removeTag(Constants.NBT_KEY_BACK);
    }

    public static void setBackWarp(EntityPlayerMP player, boolean free)
    {
        NBTTagCompound root = getBackTagParent(player);
        root.setTag(Constants.NBT_KEY_BACK, new WarpPoint(player, "Back", free).serializeNBT());
    }
}
