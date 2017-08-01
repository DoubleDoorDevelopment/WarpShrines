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

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author Dries007
 */
public class Constants
{
    public static final String MOD_ID = "warpshrines";
    public static final String MOD_NAME = "WarpShrines";
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(WarpCostConfig.class, new WarpCostConfig.Json())
            .registerTypeAdapter(WarpCost.class, new WarpCost.Json())
            .setPrettyPrinting().create();
    public static final Joiner SPACE_JOINER = Joiner.on(' ');

    public static final String DEFAULT = "default";
    public static final String BASE_FEE = "BaseFee";
    public static final String DISTANCE_FEE = "DistanceFee";
    public static final String ENTRY_FEE = "EntryFee";

    public static final String NBT_KEY_BACK = MOD_ID + "_back";
}
