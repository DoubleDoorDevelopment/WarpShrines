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
//import com.google.common.base.Strings;
//import com.google.gson.JsonArray;
//import com.google.gson.JsonObject;
//import com.google.gson.JsonPrimitive;
//import net.minecraft.util.JsonUtils;
//import net.minecraft.util.text.*;
//import net.minecraft.util.text.event.ClickEvent;
//import net.minecraft.util.text.event.HoverEvent;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * @author Dries007
// */
//public class Page
//{
//    private final List<Object> parts = new ArrayList<Object>();
//
//    public Page()
//    {
//
//    }
//
//    public Page(Book book)
//    {
//        book.addPages(this);
//    }
//
//    public String toPageString()
//    {
//        JsonArray out = new JsonArray();
//        for (Object o : parts)
//        {
//            if (o instanceof String) out.add(new JsonPrimitive((String) o));
//            else if (o instanceof ITextComponent) parse(out, (ITextComponent) o);
//            else out.add(new JsonPrimitive(String.valueOf(o)));
//        }
//        return out.toString();
//    }
//
//    private void parse(final JsonArray out, ITextComponent tc)
//    {
//        JsonObject root = new JsonObject();
//
//        Style style = tc.getStyle();
//        //noinspection ConstantConditions
//        if (style != null && !style.isEmpty())
//        {
//            if (style.getColor() != null) root.addProperty("color", style.getColor().getFriendlyName());
//            if (style.getBold()) root.addProperty("bold", true);
//            if (style.getUnderlined()) root.addProperty("underlined", true);
//            if (style.getItalic()) root.addProperty("italic", true);
//            if (style.getStrikethrough()) root.addProperty("strikethrough", true);
//            if (style.getObfuscated()) root.addProperty("obfuscated", true);
//            if (!Strings.isNullOrEmpty(style.getInsertion())) root.addProperty("insertion", style.getInsertion());
//        }
//
//        ClickEvent ce = style.getClickEvent();
//        if (ce != null)
//        {
//            JsonObject ceJson = new JsonObject();
//            ceJson.addProperty("action", ce.getAction().getCanonicalName());
//            ceJson.addProperty("value", ce.getValue());
//            root.add("clickEvent", ceJson);
//        }
//
//        HoverEvent he = style.getHoverEvent();
//        if (he != null)
//        {
//            JsonObject heJson = new JsonObject();
//            heJson.addProperty("action", he.getAction().getCanonicalName());
//            heJson.addProperty("value", he.getValue().getFormattedText()); // Shortcut.
//            root.add("hoverEvent", heJson);
//        }
//
//        if (tc instanceof TextComponentString) root.addProperty("text", ((TextComponentString) tc).getText());
//        if (tc instanceof TextComponentSelector) root.addProperty("selector", ((TextComponentSelector) tc).getSelector());
//        else if (tc instanceof TextComponentTranslation)
//        {
//            root.addProperty("translate", ((TextComponentTranslation) tc).getKey());
//            JsonArray args = new JsonArray();
//            for (Object arg : ((TextComponentTranslation) tc).getFormatArgs()) args.add(new JsonPrimitive(String.valueOf(arg)));
//            root.add("with", args);
//        }
//        else if (tc instanceof TextComponentScore)
//        {
//            JsonObject score = new JsonObject();
//            score.addProperty("name", ((TextComponentScore) tc).getName());
//            score.addProperty("objective", ((TextComponentScore) tc).getObjective());
//            if (!Strings.isNullOrEmpty(tc.getUnformattedComponentText())) score.addProperty("value", tc.getUnformattedComponentText());
//            root.add("score", score);
//        }
//
//        out.add(root);
//
//        for (ITextComponent sibling : tc.getSiblings()) parse(out, sibling);
//    }
//
//    public Page add(Object... objects)
//    {
//        Collections.addAll(parts, objects);
//        return this;
//    }
//}
