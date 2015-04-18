package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.potion.PotionEffect;

public class SyncEffects extends Sync{
	
	public SyncEffects(int id) {
		super(id);
	}

	int lastPotionSize = 0;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return this.isDirty;// || clone.getActivePotionEffects().size() != lastPotionSize;
	}

	@Override
	public void updateValues(EntityClone clone) {
		this.isDirty = false;//lastPotionSize = clone.getActivePotionEffects().size();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException {
		out.writeShort(clone.getActivePotionEffects().size());
		for(Iterator<?> it = clone.getActivePotionEffects().iterator();it.hasNext();){
			PotionEffect effect = (PotionEffect)it.next();
			int potionID = effect.getPotionID();
	        int duration = effect.getDuration();
	        int amplifier = effect.getAmplifier();
	        out.writeInt(potionID);
	        out.writeInt(duration);
	        out.writeInt(amplifier);
		}
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		int length = in.readShort();
//		System.out.println("READ: " + length);
		clone.forceClearActivePotions();
		for(int a = 0; a < length; a++){
			int potionID = in.readInt();
	        int duration = in.readInt();
	        int amplifier = in.readInt();
			PotionEffect pe = new PotionEffect(potionID, duration, amplifier);
			clone.addPotionEffect(pe);
		}
//		System.out.println("NOW HAS: " + clone.getActivePotionEffects().size());
	}
	

}
