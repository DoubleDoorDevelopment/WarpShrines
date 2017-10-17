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
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants.NBT;

import java.util.*;

/**
 * @author Dries007
 */
public class WarpSavedData extends WorldSavedData
{
    private final Map<String, WarpPoint> map = new HashMap<String, WarpPoint>();
    private final TLongObjectHashMap<List<WarpPoint>> chunkShortcut = new TLongObjectHashMap<List<WarpPoint>>();

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
        long key = ChunkPos.asLong(wp.getPos().getX() >> 4, wp.getPos().getZ() >> 4);
        List<WarpPoint> list = chunkShortcut.get(key);
        if (list == null) chunkShortcut.put(key, list = new ArrayList<WarpPoint>());
        list.add(wp);
        markDirty();
        return true;
    }

    public boolean remove(String name)
    {
        name = name.toLowerCase();
        if (!map.containsKey(name)) return false;
        WarpPoint wp = map.remove(name);
        long key = ChunkPos.asLong(wp.getPos().getX() >> 4, wp.getPos().getZ() >> 4);
        List<WarpPoint> list = chunkShortcut.get(key);
        list.remove(wp);
        if (list.isEmpty()) chunkShortcut.remove(key);
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

    public boolean hasWarpsAround(int x, int z)
    {
        x >>= 4;
        z >>= 4;
        for (int xx = -1; xx <= 1; xx ++)
            for (int zz = -1; zz <= 1; zz ++)
                if (chunkShortcut.containsKey(ChunkPos.asLong(x + xx, z + zz))) return true;
        return false;
    }

    public List<WarpPoint> getWarpsAround(int x, int z)
    {
        x >>= 4;
        z >>= 4;
        List<WarpPoint> out = new ArrayList<WarpPoint>();
        for (int xx = -1; xx <= 1; xx ++)
            for (int zz = -1; zz <= 1; zz ++)
            {
                List<WarpPoint> list = chunkShortcut.get(ChunkPos.asLong(x + xx, z + zz));
                if (list != null) out.addAll(list);
            }
        return out;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt)
    {
        NBTTagList list = nbt.getTagList(mapName, NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) add(WarpPoint.fromNBT(list.getCompoundTagAt(i)));
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
        WarpSavedData data = (WarpSavedData) world.loadData(WarpSavedData.class, Constants.MOD_ID);
        if (data == null)
        {
            data = new WarpSavedData(Constants.MOD_ID);
            world.setData(Constants.MOD_ID, data);
        }
        return data;
    }
}
