package net.jamezo97.clonecraft.command.word;

import java.util.ArrayList;

public class VerbSet {
	
//	public static final ActionWord ACTION_go = new ActionWord("go");
	public static final VerbSet dig = new VerbSet("dig", "mine", "break");
	public static final VerbSet build = new VerbSet("build", "construct", "make");
	public static final VerbSet chop = new VerbSet("chop", "cut");
	public static final VerbSet attack = new VerbSet("kill", "attack", "maime", "destroy").setStrength(0.7f);
	public static final VerbSet jump = new VerbSet("jump");
	public static final VerbSet follow = new VerbSet("follow", "track", "lead");
	public static final VerbSet stop = new VerbSet("stop", "cancel", "forget", "nevermind");
	public static final VerbSet stay = new VerbSet("stay", "remain", "still");
	public static final VerbSet guard = new VerbSet("guard", "defend");
//	public static final VerbSet farm = new VerbSet("farm");
	public static final VerbSet come = new VerbSet("come");
	public static final VerbSet give = new VerbSet("give", "bring");
	public static final VerbSet hello = new VerbSet("hello", "hi", "hey", "yo", "wassup");

	
	
	private String[] alias = null;
	
	float strength = 0.5f;
	
	public VerbSet(String... alias){
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
	public VerbSet setStrength(float strength){
		this.strength = strength;
		return this;
	}
	
	/**
	 * 
	 * @param words
	 * @return -1 If the verb is not found, otherwise returns the index in the word array where it was found.
	 */
	public int containsVerb(String[] words){
		for(int a = 0; a < alias.length; a++)
		{
			for(int b = 0; b < words.length; b++)
			{
				if(words[b].equals(alias[a]))
				{
					return b;
				}
			}
		}
		return -1;
	}
	
	public float getStrength(){
		return this.strength;
	}

}
