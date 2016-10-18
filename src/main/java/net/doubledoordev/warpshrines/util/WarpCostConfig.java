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

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.util.math.BlockPos;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dries007
 */
public class WarpCostConfig
{
    private final WarpCost defaultCost;
    private final HashMap<Integer, WarpCost> dimensionMap = new HashMap<Integer, WarpCost>();

    public WarpCostConfig(WarpCost defaultCost)
    {
        this.defaultCost = defaultCost;
        if (defaultCost.baseFee == -1 || defaultCost.distanceFee == -1 || defaultCost.entryFee == -1) throw new RuntimeException("You need to fill out all of the default costs!");
    }

    public int calculateCost(BlockPos start, int dimStart, BlockPos end, int dimEnd)
    {
        WarpCost startConf = dimensionMap.get(dimStart);
        WarpCost endConf = dimensionMap.get(dimStart);
        if (startConf == null) startConf = defaultCost;
        int cost = startConf.baseFee;
        if (endConf == null) endConf = defaultCost;
        if (dimStart != dimEnd) cost += endConf.entryFee != -1 ? endConf.entryFee : defaultCost.entryFee;
        WarpCost mostExpensive = startConf;
        if (endConf.distanceFee > mostExpensive.distanceFee) mostExpensive = endConf;
        cost += Math.sqrt(end.distanceSq(start)) * mostExpensive.distanceFee;
        return cost;
    }

    private void add(WarpCost warpCost)
    {
        Helper.parseDimIds(warpCost.specifier);
    }

    public static WarpCostConfig makeDefault()
    {
        WarpCostConfig warpCostConfig = new WarpCostConfig(new WarpCost(Constants.DEFAULT).setBaseFee(55).setDistanceFee(0.1).setEntryFee(0));
        warpCostConfig.add(new WarpCost("-1").setDistanceFee(0.1 * 8).setEntryFee(160));
        warpCostConfig.add(new WarpCost("1").setEntryFee(910));
        return warpCostConfig;
    }

    public static class Json implements JsonSerializer<WarpCostConfig>, JsonDeserializer<WarpCostConfig>
    {
        @Override
        public WarpCostConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject o = json.getAsJsonObject();
            WarpCostConfig w = new WarpCostConfig(context.<WarpCost>deserialize(o.get(Constants.DEFAULT), WarpCost.class));
            for (Map.Entry<String, JsonElement> e : o.entrySet())
                if (!e.getKey().equals(Constants.DEFAULT))
                    w.add(context.<WarpCost>deserialize(e.getValue(), WarpCost.class).setSpecifier(e.getKey()));
            return w;
        }

        @Override
        public JsonElement serialize(WarpCostConfig src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject o = new JsonObject();
            o.add(Constants.DEFAULT, context.serialize(src.defaultCost));
            for (WarpCost w : Sets.newHashSet(src.dimensionMap.values())) o.add(w.specifier, context.serialize(w));
            return o;
        }
    }
}
