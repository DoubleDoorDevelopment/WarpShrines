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
import java.util.List;

/**
 * @author Dries007
 */
public class WarpCommand extends CommandBase
{
    @Override
    public String getCommandName()
    {
        return "warp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "Use '/warp help' for more info.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return sender instanceof EntityPlayer;
    }

    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
    {
        if (isUsernameIndex(args, args.length - 1)) return getListOfStringsMatchingLastWord(args, server.getAllUsernames());
        else if (args.length == 1) return getListOfStringsMatchingLastWord(args, "help", "go", "cost", "list", "make", "remove");
        else if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("go") || args[0].equalsIgnoreCase("cost") || args[0].equalsIgnoreCase("remove"))
                return getListOfStringsMatchingLastWord(args, Helper.getWarpList(((EntityPlayer) sender)));
            else if (args[0].equals("make"))
                return getListOfStringsMatchingLastWord(args, "true", "false");
        }
        return super.getTabCompletionOptions(server, sender, args, pos);
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) displayHelp(sender);
        else if (args[0].equalsIgnoreCase("go")) doGo(sender, args);
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
                TextFormatting.AQUA + getCommandName() + " sub command help:",
                TextFormatting.GREEN + "ProTip: Use TAB to auto complete a command or warp name!",
                "- help: Display this text.",
                "- go [name]: Warp to a point.",
                "- cost [name]: Found out the cost of warping.",
                "- list: List of warps you have access too.",
        }) sender.addChatMessage(new TextComponentString(s));
    }

    private void doGo(ICommandSender sender, String[] args) throws CommandException
    {
        WarpPoint wp = WarpSavedData.get(sender).get(getName(sender, args, 1));
        if (wp == null) throw new CommandException("No warp by that name :(");
        wp.queueTeleport(getCommandSenderAsPlayer(sender));
    }

    private void doCost(ICommandSender sender, String[] args) throws CommandException
    {
        WarpPoint wp = WarpSavedData.get(sender).get(getName(sender, args, 1));
        if (wp == null) throw new CommandException("No warp by that name :(");
        int cost = wp.getCost(getCommandSenderAsPlayer(sender));
        Helper.chat(sender, "Warp cost: " + cost + " xp or " + (Helper.getLevelForExperience(cost) + 1) + " levels.");
    }

    private void doList(ICommandSender sender, String[] args) throws CommandException
    {
        boolean op = sender.canCommandSenderUseCommand(1, getCommandName());
        Helper.chat(sender, op ? "All warps:" : "List of warps you have access too:", TextFormatting.AQUA);
        for (String s : Helper.getWarpList(getCommandSenderAsPlayer(sender))) sender.addChatMessage(new TextComponentString(s));
    }

    private void doMake(ICommandSender sender, String[] args) throws CommandException
    {
        if (!sender.canCommandSenderUseCommand(1, getCommandName())) throw new CommandException("Permission denied.");
        if (!WarpSavedData.get(sender).add(new WarpPoint(getCommandSenderAsPlayer(sender), getName(sender, args, 2), parseBoolean(args[1])))) throw new CommandException("Warp name already exists");
        sender.addChatMessage(new TextComponentString("Warp added!"));
    }

    private void doRemove(ICommandSender sender, String[] args) throws CommandException
    {
        if (!sender.canCommandSenderUseCommand(1, getCommandName())) throw new CommandException("Permission denied.");
        if (!WarpSavedData.get(sender).remove(getName(sender, args, 1))) throw new CommandException("Warp name did not exist");
        sender.addChatMessage(new TextComponentString("Warp removed!"));
    }

    private String getName(ICommandSender sender, String[] args, int offset) throws CommandException
    {
        String name = getChatComponentFromNthArg(sender, args, offset).getUnformattedText().trim();
        if (Strings.isNullOrEmpty(name)) throw new CommandException("You must provide a warp name");
        return name;
    }

//
//    private void doBook(ICommandSender sender, String[] args) throws CommandException
//    {
//        EntityPlayer player = getCommandSenderAsPlayer(sender);
//
//        Book book = new Book("Book 'o Warps");
//
//        Page index = new Page(book);
//
//        Multimap<Integer, WarpPoint> dimToWarpMap = HashMultimap.create();
//        for (WarpPoint wp : WarpSavedData.get(sender).getAllWarpPoints()) dimToWarpMap.put(wp.getDim(), wp);
//
//        for (Integer dim : dimToWarpMap.keySet())
//        {
//            Page dimPage = new Page(book).add(TextFormatting.GRAY + "Dimension " + dim + "\n");
//            index.add(new TextComponentString("-> Dimension " + dim).setStyle(new Style()
//                    .setClickEvent(new ClickEvent(ClickEvent.Action.CHANGE_PAGE, String.valueOf(book.getSize())))));
//            List<WarpPoint> warpPoints = new ArrayList<WarpPoint>(dimToWarpMap.get(dim));
//            Collections.sort(warpPoints);
//            for (WarpPoint wp : warpPoints)
//            {
//
//                ITextComponent wpText = new TextComponentString("").setStyle(new Style()
//                        .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/warp go " + wp.getUuid()))
//                        .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to queueTeleport"))));
//                wpText.appendText(wp.getName()).appendText("\nBy ").appendSibling(wp.getCreatorComponent(sender.getServer()));
//                wpText.appendText("\n");
//                dimPage.add(wpText);
//            }
//        }
//
//        ItemStack stack = null;
//        for (final ItemStack heldStack : player.getHeldEquipment())
//        {
//            if (!Book.isBook(heldStack)) continue;
//            stack = heldStack;
//            break;
//        }
//        if (stack == null)
//        {
//            stack = new ItemStack(Items.WRITTEN_BOOK);
//            Helper.dropItem(player, stack);
//        }
//        book.writeTo(stack);
//        player.addChatComponentMessage(new TextComponentString("Warp book updated!"));
//    }
}
