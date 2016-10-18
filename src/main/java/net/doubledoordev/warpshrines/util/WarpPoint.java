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

import net.doubledoordev.warpshrines.WarpShrines;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import static net.doubledoordev.warpshrines.util.Constants.MOD_ID;
import static net.minecraft.util.text.TextFormatting.RED;

/**
 * @author Dries007
 */
public class WarpPoint implements INBTSerializable<NBTTagCompound>
{
    private String name;
    private int dim;
    private BlockPos pos;
    private boolean free;

    private WarpPoint()
    {

    }

    public WarpPoint(EntityPlayer player, String name, boolean free)
    {
        this.name = name;
        this.dim = player.dimension;
        this.pos = new BlockPos(player).toImmutable();
        this.free = free;
    }

    public int getCost(EntityPlayer entity)
    {
        if (free) return 0;
        return WarpShrines.getWarpCostConfig().calculateCost(new BlockPos(entity), entity.dimension, pos, dim);
    }

    public void teleportNow(EntityPlayerMP player)
    {
        if (player.dimension != dim)
        {
            // Code stolen from Entity.changeDimension(dim)
            if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(player, dim))
            {
                Helper.chat(player, "Some mod prevented this warp.", RED);
                return;
            }
            player.worldObj.playSound(null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
            player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
            //noinspection ConstantConditions
            player.getServer().getPlayerList().transferPlayerToDimension(player, dim, CustomTeleporter.INSTANCE);
        }
        player.worldObj.playSound(null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, player.getSoundCategory(), 1.0F, 1.0F);
        player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        player.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }

    public boolean queueTeleport(EntityPlayerMP player)
    {
        if (player.getEntityData().hasKey(MOD_ID))
        {
            Helper.chat(player, "Warp already in progress...", RED);
            return false;
        }
        // canCommandSenderUseCommand because it does SSP check too
        if (!free && !Helper.getWarpList(player).contains(name) && !player.canCommandSenderUseCommand(1, "warp"))
        {
            Helper.chat(player, "You don't have access to this warp yet. Visit it first!", RED);
            return false;
        }
        int cost = getCost(player);
        if (!player.capabilities.isCreativeMode && !free)
        {
            if (Helper.getPlayerXP(player) < cost)
            {
                Helper.chat(player, "You are low on XP. You need " + cost + " xp or " + (Helper.getLevelForExperience(cost)) + " levels.", RED);
                return false;
            }
            Helper.addPlayerXP(player, -cost);
        }
        NBTTagCompound root = new NBTTagCompound();
        root.setString("name", name);
        root.setInteger("time", 0);
        player.getEntityData().setTag(MOD_ID, root);
        player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, WarpShrines.getDelay() + (4 * 20), 3, false, false));
        player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, WarpShrines.getDelay() + (2 * 20), 0, false, false));

        player.worldObj.playSound(null, player.prevPosX, player.prevPosY, player.prevPosZ, SoundEvents.BLOCK_PORTAL_TRIGGER, player.getSoundCategory(), 0.1F, 1.0F);
        player.playSound(SoundEvents.BLOCK_PORTAL_TRIGGER, 0.1F, 1.0F);

        return true;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound root = new NBTTagCompound();
        root.setString("name", name);
        root.setInteger("dim", dim);
        root.setLong("pos", pos.toLong());
        root.setBoolean("free", free);
        return root;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        name = nbt.getString("name");
        dim = nbt.getInteger("dim");
        pos = BlockPos.fromLong(nbt.getLong("pos"));
        free = nbt.getBoolean("free");
    }

    public static WarpPoint fromNBT(NBTTagCompound nbt)
    {
        WarpPoint wp = new WarpPoint();
        wp.deserializeNBT(nbt);
        return wp;
    }

    public String getName()
    {
        return name;
    }

    public int getDim()
    {
        return dim;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public boolean isFree()
    {
        return free;
    }
}
