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

package net.doubledoordev.warpshrines.cmd;

import com.google.common.base.Strings;
import net.doubledoordev.warpshrines.util.Helper;
import net.doubledoordev.warpshrines.util.WarpPoint;
import net.doubledoordev.warpshrines.util.WarpSavedData;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.util.text.TextFormatting.RED;

/**
 * @author Dries007
 */
public class WarpCommand extends CommandBase
{
    @Override
    public String getName()
    {
        return "warp";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "Use '/warp help' for more info.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if (isUsernameIndex(args, args.length - 1)) return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        else if (args.length == 1) return getListOfStringsMatchingLastWord(args, "help", "go", "back", "cost", "list", "make", "remove");
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("go") || args[0].equalsIgnoreCase("remove"))
            {
                return getListOfStringsMatchingLastWord(args, Helper.getWarpList(((EntityPlayer) sender)));
            }
            else if (args[0].equalsIgnoreCase("cost"))
            {
                ArrayList<String> l = Helper.getWarpList(((EntityPlayer) sender));
                l.add(0, "back");
                return getListOfStringsMatchingLastWord(args, l);
            }
            else if (args[0].equals("make"))
            {
                return getListOfStringsMatchingLastWord(args, "true", "false");
            }
        }
        return super.getTabCompletions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) displayHelp(sender);
        else if (args[0].equalsIgnoreCase("go")) doGo(sender, args);
        else if (args[0].equalsIgnoreCase("back")) doBack(sender, args);
        else if (args[0].equalsIgnoreCase("cost")) doCost(sender, args);
        else if (args[0].equalsIgnoreCase("list")) doList(sender, args);
        else if (args[0].equalsIgnoreCase("make")) doMake(sender, args);
        else if (args[0].equalsIgnoreCase("remove")) doRemove(sender, args);
//        else if (args[0].equalsIgnoreCase("book")) doBook(sender, args);
        else displayHelp(sender);
    }

    private void displayHelp(ICommandSender sender)
    {
        for (String s : new String[]{
                TextFormatting.AQUA + getName() + " sub command help:",
                TextFormatting.GREEN + "ProTip: Use TAB to auto complete a command or warp name!",
                "- help: Display this text.",
                "- go [name...]: Warp to a point.",
                "- back: Warp back to were you last warped from.",
                "- cost [back|name...]: Get the cost of warping to a point.",
                "- list: List of warps you have access to.",
                "- make [free?] [name...]: Make a new warp [OP]",
                "- remove [name...]: Remove a warp [OP]",
        }) sender.sendMessage(new TextComponentString(s));
    }

    private void doGo(ICommandSender sender, String[] args) throws CommandException
    {
        WarpPoint wp = WarpSavedData.get(sender).get(getName(sender, args, 1));
        if (wp == null) throw new CommandException("No warp by that name :(");
        // canUseCommand because it does SSP check too
        if (Helper.getWarpList(getCommandSenderAsPlayer(sender)).contains(wp.getName()))
            wp.queueTeleport(getCommandSenderAsPlayer(sender), false);
        else
            Helper.chat(sender, "You don't have access to this warp yet. Visit it first!", RED);
    }

    private void doBack(ICommandSender sender, String[] args) throws CommandException
    {
        WarpPoint wp = Helper.getBackWarp(getCommandSenderAsPlayer(sender));
        if (wp == null) throw new CommandException("No warp back available :(");
        wp.queueTeleport(getCommandSenderAsPlayer(sender), true);
    }

    private void doCost(ICommandSender sender, String[] args) throws CommandException
    {
        String warpName = getName(sender, args, 1);
        if (warpName.equalsIgnoreCase("back"))
        {
            WarpPoint wp = Helper.getBackWarp(getCommandSenderAsPlayer(sender));
            if (wp == null) throw new CommandException("No warp back available :(");
            int cost = wp.getCost(getCommandSenderAsPlayer(sender));
            Helper.chat(sender, "Warp cost: " + cost + " xp or " + (Helper.getLevelForExperience(cost) + 1) + " levels.");
        }
        else
        {
            WarpPoint wp = WarpSavedData.get(sender).get(warpName);
            if (wp == null) throw new CommandException("No warp by that name :(");
            int cost = wp.getCost(getCommandSenderAsPlayer(sender));
            Helper.chat(sender, "Warp cost: " + cost + " xp or " + (Helper.getLevelForExperience(cost) + 1) + " levels.");
        }
    }

    private void doList(ICommandSender sender, String[] args) throws CommandException
    {
        boolean op = sender.canUseCommand(1, getName());
        Helper.chat(sender, op ? "All warps:" : "List of warps you have access too:", TextFormatting.AQUA);
        for (String s : Helper.getWarpList(getCommandSenderAsPlayer(sender)))
        {
            WarpPoint wp = WarpSavedData.get(sender).get(s);
            sender.sendMessage(new TextComponentString(wp.getName() + (wp.isFree() ? " (free)" : (" (" + wp.getCost(getCommandSenderAsPlayer(sender)) + " xp)"))));
        }
    }

    private void doMake(ICommandSender sender, String[] args) throws CommandException
    {
        if (!sender.canUseCommand(1, getName())) throw new CommandException("Permission denied.");
        if (!WarpSavedData.get(sender).add(new WarpPoint(getCommandSenderAsPlayer(sender), getName(sender, args, 2), parseBoolean(args[1])))) throw new CommandException("Warp name already exists");
        sender.sendMessage(new TextComponentString("Warp added!"));
    }

    private void doRemove(ICommandSender sender, String[] args) throws CommandException
    {
        if (!sender.canUseCommand(1, getName())) throw new CommandException("Permission denied.");
        if (!WarpSavedData.get(sender).remove(getName(sender, args, 1))) throw new CommandException("Warp name did not exist");
        sender.sendMessage(new TextComponentString("Warp removed!"));
    }

    private String getName(ICommandSender sender, String[] args, int offset) throws CommandException
    {
        String name = getChatComponentFromNthArg(sender, args, offset).getUnformattedText().trim();
        if (Strings.isNullOrEmpty(name)) throw new CommandException("You must provide a warp name");
        return name;
    }
}
