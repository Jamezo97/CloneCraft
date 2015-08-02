package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIDoorInteract;

/**
 * From net.minecraft.entity.ai.EntityAIOpenDoor
 * @author James
 *
 */
public class EntityAIOpenDoorClone extends EntityAIDoorInteract
{
    boolean field_75361_i;
    int field_75360_j;
    private static final String __OBFID = "CL_00001603";

    EntityClone clone;
    
    public EntityAIOpenDoorClone(EntityClone clone, boolean p_i1644_2_)
    {
        super(clone);
        this.theEntity = clone;
        this.clone = clone;
        this.field_75361_i = p_i1644_2_;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
	@Override
    public boolean continueExecuting()
    {
        return this.field_75361_i && this.field_75360_j > 0 && super.continueExecuting();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
	@Override
    public void startExecuting()
    {
        this.field_75360_j = 20;
        this.field_151504_e.func_150014_a(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, true);
        clone.swingItem();
    }

    /**
     * Resets the task
     */
	@Override
    public void resetTask()
    {
        if (this.field_75361_i)
        {
            this.field_151504_e.func_150014_a(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, false);
            clone.swingItem();
        }
    }

    /**
     * Updates the task
     */
	@Override
    public void updateTask()
    {
        --this.field_75360_j;
        super.updateTask();
    }
}