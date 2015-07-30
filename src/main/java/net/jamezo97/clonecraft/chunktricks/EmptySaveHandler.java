package net.jamezo97.clonecraft.chunktricks;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EmptySaveHandler implements ISaveHandler
{
    private static final String __OBFID = "CL_00000602";

    /**
     * Loads and returns the world info
     */
    public WorldInfo loadWorldInfo()
    {
        return null;
    }

    /**
     * Checks the session lock to prevent save collisions
     */
    public void checkSessionLock() throws MinecraftException {}

    /**
     * Returns the chunk loader with the provided world provider
     */
    public IChunkLoader getChunkLoader(WorldProvider p_75763_1_)
    {
        return null;
    }

    /**
     * Saves the given World Info with the given NBTTagCompound as the Player.
     */
    public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_) {}

    /**
     * Saves the passed in world info.
     */
    public void saveWorldInfo(WorldInfo p_75761_1_) {}

    /**
     * returns null if no saveHandler is relevent (eg. SMP)
     */
    public IPlayerFileData getSaveHandler()
    {
        return null;
    }

    /**
     * Called to flush all changes to disk, waiting for them to complete.
     */
    public void flush() {}

    /**
     * Gets the file location of the given map
     */
    public File getMapFileFromName(String p_75758_1_)
    {
        return null;
    }

    /**
     * Returns the name of the directory where world information is saved.
     */
    public String getWorldDirectoryName()
    {
        return "none";
    }

    /**
     * Gets the File object corresponding to the base directory of this world.
     */
    public File getWorldDirectory()
    {
        return null;
    }
}