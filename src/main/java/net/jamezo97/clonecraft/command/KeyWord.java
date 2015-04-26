package net.jamezo97.clonecraft.command;

public class KeyWord {
	
	
	private String[] alias = null;
	
	float strength = 0.5f;
	
	public KeyWord(String... alias){
		if(alias == null)
		{
			throw new RuntimeException("A KeyWord must have at least one identifiable word.");
		}
		this.alias = alias;
	}
	
	/**
	 * Sets how strong or powerful this keyword is. Some keywords are more important than others, and can in a way, override the others
	 * @param strength How strong this KeyWord is compared to other keywords. Should be between 0 and 1. Default is 0.5;
	 * @return
	 */
	public KeyWord setStrength(float strength){
		this.strength = strength;
		return this;
	}
	
	public float getStrength(){
		return this.strength;
	}


}
