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

package net.doubledoordev.warpshrines;

import net.doubledoordev.warpshrines.util.Constants;
import net.doubledoordev.warpshrines.util.Helper;
import net.doubledoordev.warpshrines.util.WarpPoint;
import net.doubledoordev.warpshrines.util.WarpSavedData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.List;

import static net.doubledoordev.warpshrines.util.Constants.MOD_ID;
import static net.minecraft.util.text.TextFormatting.GREEN;
import static net.minecraft.util.text.TextFormatting.WHITE;

/**
 * @author Dries007
 */
public class ServerEventHandler
{
    public static final ServerEventHandler EVENT_HANDLER = new ServerEventHandler();

    private ServerEventHandler() {}

    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.side.isClient() || event.phase == TickEvent.Phase.END) return;

        WarpSavedData wsd = WarpSavedData.get(event.player);

        if (wsd.hasWarpsAround((int) event.player.posX, (int) event.player.posZ))
        {
            List<WarpPoint> points = wsd.getWarpsAround((int) event.player.posX, (int) event.player.posZ);
            List<String> names = Helper.getWarpList(event.player);
            List<String> toAdd = new ArrayList<String>();
            for (WarpPoint point : points) if (!names.contains(point.getName())) toAdd.add(point.getName());
            int size = toAdd.size();
            if (size == 1)
            {
                Helper.addToWarpList(event.player, toAdd);
                Helper.chat(event.player, "You have discovered a warp point: " + WHITE + toAdd.get(0), GREEN);
            }
            else if (size > 1)
            {
                Helper.addToWarpList(event.player, toAdd);
                Helper.chat(event.player, "You have discovered multiple warp points: " + WHITE + Constants.SPACE_JOINER.join(toAdd), GREEN);
            }
        }

        if (event.player.getEntityData().hasKey(MOD_ID))
        {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            WorldServer world = player.getServerWorld();
            NBTTagCompound root = event.player.getEntityData().getCompoundTag(MOD_ID);
            int time = root.getInteger("time");
            root.setInteger("time", time + 1);
            final int MAX = WarpShrines.getDelay();
            int particles = 20 * (MAX - time);
            if (time >= MAX)
            {
                WarpPoint.fromNBT(root.getCompoundTag("warpPoint")).teleportNow(player, root.getBoolean("isBack"));
                event.player.getEntityData().removeTag(MOD_ID);
                particles = 1000;
            }
            world.spawnParticle(EnumParticleTypes.PORTAL, false, player.posX, player.posY, player.posZ, particles, world.rand.nextGaussian() * 0.02D, world.rand.nextGaussian() * 0.02D, world.rand.nextGaussian() * 0.02D, 1F);
        }
    }
}
