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

import com.google.common.base.Throwables;
import net.doubledoordev.warpshrines.cmd.WarpCommand;
import net.doubledoordev.warpshrines.util.Constants;
import net.doubledoordev.warpshrines.util.WarpCostConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static net.doubledoordev.warpshrines.ServerEventHandler.EVENT_HANDLER;
import static net.doubledoordev.warpshrines.util.Constants.MOD_ID;
import static net.doubledoordev.warpshrines.util.Constants.MOD_NAME;

@Mod(modid = MOD_ID, name = MOD_NAME, acceptableRemoteVersions = "*")
public class WarpShrines
{
    @Mod.Instance(MOD_ID)
    private static WarpShrines instance;

    private Logger logger;
    private Configuration config;
    private File costFile;
    private WarpCostConfig warpCostConfig;
    private int delay;
    private boolean backPersists;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        costFile = new File(event.getModConfigurationDirectory(), MOD_ID + "_cost.json");
        config = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig(config);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(EVENT_HANDLER);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new WarpCommand());
    }

    @SubscribeEvent
    public void onConfigChangedOnConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MOD_ID)) syncConfig(config);
    }

    private void syncConfig(Configuration config)
    {
        delay = config.getInt("delay", MOD_ID, 20 * 5, 1, Integer.MAX_VALUE, "The time delay before warping. Also does the potion effect. In ticks.");
        backPersists = config.getBoolean("backPersists", MOD_ID, false, "If true, your last warp will be remembered after death.");
        if (config.hasChanged()) config.save();

        try
        {
            if (costFile.exists() && costFile.isFile()) warpCostConfig = Constants.GSON.fromJson(FileUtils.readFileToString(costFile), WarpCostConfig.class);
            else
            {
                warpCostConfig = WarpCostConfig.makeDefault();
                FileUtils.write(costFile, Constants.GSON.toJson(warpCostConfig));
            }
        }
        catch (Exception e)
        {
            Throwables.propagate(e);
        }
    }

    public static Logger log()
    {
        return instance.logger;
    }

    public static int getDelay()
    {
        return instance.delay;
    }

    public static boolean doesBackPersists()
    {
        return instance.backPersists;
    }

    public static WarpCostConfig getWarpCostConfig()
    {
        return instance.warpCostConfig;
    }
}
