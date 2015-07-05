package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.jamezo97.clonecraft.schematic.SchematicEntry;
import net.jamezo97.clonecraft.tileentity.CCTileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class EntityAIBuild extends EntityAIBase{
	
	EntityClone clone;
	
	Schematic schem;
	
	boolean isBuilding = false;
	
	/**
	 * From above, in OpenGL a + y rotation rotates anti clockwise.<br>
	 * When rotate =<ul>
	 * <li><b>0:</b> X+ Z+</li>
	 * <li><b>1:</b> X+ Z-</li>
	 * <li><b>2:</b> X- Z-</li>
	 * <li><b>3:</b> X- Z+</li>
	 * </ul>
	 */
	int rotate = 0;
	
	//The corner from which the current building is being created from.
	public int posX;
	public int posY;
	public int posZ;
	
	int index = 0;
	
	public EntityAIBuild(EntityClone clone)
	{
		this.clone = clone;
		this.setMutexBits(1);
	}
	
	public void setSchematic(Schematic schem)
	{
		this.schem = schem;
		this.index = 0;
	}
	
	public float getAngularRotation()
	{
		return (this.rotate % 4) * 90;
	}

	public void setBuilding(boolean b)
	{
		this.isBuilding = b;
	}
	
	@Override
	public boolean shouldExecute()
	{
		return schem != null && isBuilding;
	}
	
	@Override
	public boolean continueExecuting()
	{
		return shouldExecute();
	}

	@Override
	public void updateTask()
	{
//		System.out.println("Build");
		
		int max = 300;
		
		for(int a = 0; a < 60 && max-- > 0; a++)
		{
//			System.out.println("G");
			if(index < schem.blockIds.length)
			{
				int[] pos = schem.indexToPos(index);
				int x = posX + pos[0];
				int y = posY + pos[1];
				int z = posZ + pos[2];
				
				if(y < 0 || y >= clone.worldObj.getHeight())
				{
					a--;
					index++;
					continue;
				}
				
				Block current = clone.worldObj.getBlock(x, y, z);
				int currentMeta = clone.worldObj.getBlockMetadata(x, y, z);
				
				Block newBlock = schem.blockAt(index);
				int newMeta = schem.blockMetaAt(index);
				
				if(current == newBlock && currentMeta == newMeta)
				{
					a--;
					index++;
					continue;
				}
				
				
				
				
				
				clone.worldObj.setBlock(x, y, z, schem.blockAt(index), schem.blockMetaAt(index), 3);
				
				if(schem.hasTileEntityAt(index))
				{
					TileEntity te = schem.getTileEntity(index, clone.worldObj);
					te.xCoord = x;
					te.yCoord = y;
					te.zCoord = z;
					
					//Makes sure we're not cloning ItemStacks
					CCTileEntityRegistry.clearItemstacks(te);
					
					if(y >= 0 && y < clone.worldObj.getHeight())
					{
						clone.worldObj.setTileEntity(x, y, z, te);
					}
					
				}
				
				clone.getLookHelper().setLookPosition(x, y, z, 1.0f, clone.getVerticalFaceSpeed());
				
				index++;
				
				clone.swingItem();
			}
		}
		
		if(index >= schem.blockIds.length)
		{
			this.isBuilding = false;
			this.schem = null;
			this.index = 0;
			clone.say("Done!", 64);
		}
		
	}

	public NBTTagCompound saveState(NBTTagCompound nbt)
	{
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setInteger("posZ", posZ);
		
		nbt.setInteger("index", index);
		
		nbt.setInteger("rotate", rotate);
		
		nbt.setBoolean("isBuilding", isBuilding);
		
		if(schem != null)
		{
			nbt.setBoolean("savedSchem", true);
			
			nbt.setLong("schemHash", schem.myHashCode());
			nbt.setInteger("xSize", schem.xSize);
			nbt.setInteger("ySize", schem.ySize);
			nbt.setInteger("zSize", schem.zSize);
		}
		else
		{
			nbt.setBoolean("savedSchem", false);
		}
		
		return nbt;
	}
	
	public void loadState(NBTTagCompound nbt)
	{
		this.posX = nbt.getInteger("posX");
		this.posY = nbt.getInteger("posY");
		this.posZ = nbt.getInteger("posZ");
		
		this.index = nbt.getInteger("index");
		
		this.rotate = nbt.getInteger("rotate");
		
		this.isBuilding = nbt.getBoolean("isBuilding");
		
		boolean savedSchematic = nbt.getBoolean("savedSchem");
		
		if(savedSchematic)
		{
			long hash = nbt.getLong("schemHash");
			int xSize = nbt.getInteger("xSize");
			int ySize = nbt.getInteger("ySize");
			int zSize = nbt.getInteger("zSize");
			
			SchematicEntry entry = CloneCraft.INSTANCE.schematicList.getSchematic(hash, xSize, ySize, zSize);
			
			if(entry != null)
			{
				this.schem = entry.schem;
			}
		}
	}

	public boolean renderOverlay() {
		return schem != null;
	}

	public Schematic getSchematic() {
		return schem;
	}


	
	
	
	
}
