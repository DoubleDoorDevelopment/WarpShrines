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

import com.google.common.collect.ImmutableList;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dries007
 */
public class WarpSavedData extends WorldSavedData
{
    private final Map<String, WarpPoint> map = new HashMap<String, WarpPoint>();

    @SuppressWarnings("WeakerAccess")
    public WarpSavedData(String name)
    {
        super(name);
    }

    public boolean add(WarpPoint wp)
    {
        String name = wp.getName().toLowerCase();
        if (map.containsKey(name)) return false;
        map.put(name, wp);
        markDirty();
        return true;
    }

    public boolean remove(String name)
    {
        name = name.toLowerCase();
        if (!map.containsKey(name)) return false;
        map.remove(name);
        markDirty();
        return true;
    }

    public boolean has(String name)
    {
        return map.containsKey(name.toLowerCase());
    }

    public WarpPoint get(String name)
    {
        return map.get(name.toLowerCase());
    }

    public Collection<String> getAllNames()
    {
        ImmutableList.Builder<String> ilb = new ImmutableList.Builder<String>();
        for (WarpPoint wp : map.values()) ilb.add(wp.getName());
        return ilb.build();
    }

    public List<WarpPoint> getAll()
    {
        return ImmutableList.copyOf(map.values());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList(mapName, NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            add(WarpPoint.fromNBT(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        NBTTagList list = new NBTTagList();
        for (WarpPoint wp : map.values()) list.appendTag(wp.serializeNBT());
        nbt.setTag(mapName, list);
        return nbt;
    }

    public static WarpSavedData get(ICommandSender sender)
    {
        return get(sender.getEntityWorld());
    }

    public static WarpSavedData get(World world)
    {
        WarpSavedData data = (WarpSavedData) world.loadItemData(WarpSavedData.class, Constants.MOD_ID);
        if (data == null)
        {
            data = new WarpSavedData(Constants.MOD_ID);
            world.setItemData(Constants.MOD_ID, data);
        }
        return data;
    }
}
