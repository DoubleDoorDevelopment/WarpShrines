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

import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * @author Dries007
 */
public class WarpCost
{
    public String specifier;
    public int baseFee = -1;
    public double distanceFee = -1;
    public int entryFee = -1;

    public WarpCost(String specifier)
    {
        this.specifier = specifier;
    }

    private WarpCost()
    {

    }

    public WarpCost setBaseFee(int baseFee)
    {
        this.baseFee = baseFee;
        return this;
    }

    public WarpCost setDistanceFee(double distanceFee)
    {
        this.distanceFee = distanceFee;
        return this;
    }

    public WarpCost setEntryFee(int entryFee)
    {
        this.entryFee = entryFee;
        return this;
    }

    public WarpCost setSpecifier(String key)
    {
        this.specifier = key;
        return this;
    }

    public static class Json implements JsonSerializer<WarpCost>, JsonDeserializer<WarpCost>
    {
        @Override
        public WarpCost deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject o = json.getAsJsonObject();
            WarpCost w = new WarpCost();
            if (o.has(Constants.BASE_FEE)) w.setBaseFee(o.get(Constants.BASE_FEE).getAsInt());
            if (o.has(Constants.DISTANCE_FEE)) w.setDistanceFee(o.get(Constants.DISTANCE_FEE).getAsFloat());
            if (o.has(Constants.ENTRY_FEE)) w.setEntryFee(o.get(Constants.ENTRY_FEE).getAsInt());
            return w;
        }

        @Override
        public JsonElement serialize(WarpCost src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject o = new JsonObject();
            if (src.baseFee != -1) o.addProperty(Constants.BASE_FEE, src.baseFee);
            if (src.distanceFee != -1) o.addProperty(Constants.DISTANCE_FEE, src.distanceFee);
            if (src.entryFee != -1) o.addProperty(Constants.ENTRY_FEE, src.entryFee);
            return o;
        }
    }
}
