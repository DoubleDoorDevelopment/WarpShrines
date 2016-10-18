///*
// * MIT License
// *
// * Copyright (c) 2016 Dries007 & DoubleDoorDevelopment
// *
// * Permission is hereby granted, free of charge, to any person obtaining a copy
// * of this software and associated documentation files (the "Software"), to deal
// * in the Software without restriction, including without limitation the rights
// * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// * copies of the Software, and to permit persons to whom the Software is
// * furnished to do so, subject to the following conditions:
// *
// * The above copyright notice and this permission notice shall be included in all
// * copies or substantial portions of the Software.
// *
// * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// * SOFTWARE.
// */
//
//package net.doubledoordev.warpshrines.util.book;
//
//import net.doubledoordev.warpshrines.WarpShrines;
//import net.minecraft.init.Items;
//import net.minecraft.item.ItemStack;
//import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
//import net.minecraft.nbt.NBTTagString;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import static net.doubledoordev.warpshrines.util.Constants.MOD_NAME;
//
///**
// * @author Dries007
// */
//public class Book
//{
//    private final String title;
//    private final List<Page> pageList = new ArrayList<Page>();
//
//    public Book(String title)
//    {
//        this.title = title;
//    }
//
//    public static boolean isBook(ItemStack stack)
//    {
//        if (stack == null || stack.getItem() != Items.WRITTEN_BOOK) return false;
//        NBTTagCompound tag = stack.getTagCompound();
//        return tag != null && tag.getString("author").equals(MOD_NAME);
//    }
//
//    public void writeTo(ItemStack stack)
//    {
//        NBTTagList pages = new NBTTagList();
//        for (Page page : pageList) pages.appendTag(new NBTTagString(page.toPageString()));
//
//        NBTTagCompound tag = new NBTTagCompound();
//        tag.setString("title", title);
//        tag.setString("author", MOD_NAME);
//        tag.setTag("pages", pages);
//        stack.setTagCompound(tag);
//        WarpShrines.log().info("NBT:\n{}", stack.serializeNBT());
//    }
//
//    public Book addPages(Page... pages)
//    {
//        Collections.addAll(pageList, pages);
//        return this;
//    }
//
//    public int getSize()
//    {
//        return pageList.size();
//    }
//}
