package net.jamezo97.clonecraft.command.word;

import java.util.ArrayList;

public class WordSet {
	
//	public static final ActionWord ACTION_go = new ActionWord("go");
	public static final WordSet dig = new WordSet("dig", "mine", "break");
	public static final WordSet build = new WordSet("build", "construct", "make");
	public static final WordSet chop = new WordSet("chop", "cut");
	public static final WordSet attack = new WordSet("kill", "attack", "maime", "destroy", "suicide").setStrength(1.2f);
//	public static final WordSet suicide = new WordSet("suicide", "kill", "destroy").setStrength(0.7f);
	public static final WordSet jump = new WordSet("jump");
	public static final WordSet follow = new WordSet("follow", "track", "lead");
	public static final WordSet stop = new WordSet("stop", "cancel", "forget", "nevermind");
	public static final WordSet stay = new WordSet("stay", "remain", "still");
	public static final WordSet guard = new WordSet("guard", "defend");
//	public static final VerbSet farm = new VerbSet("farm");
	public static final WordSet come = new WordSet("come");
	public static final WordSet give = new WordSet("give", "bring");
	public static final WordSet hello = new WordSet("hello", "hey", "hi", "yo", "wassup").setStrength(0.1f);

	
	
	private String[] alias = null;
	
	float strength = 1.0f;
	
	public WordSet(String... alias){
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
	public WordSet setStrength(float strength){
		this.strength = strength;
		return this;
	}
	
	/**
	 * 
	 * @param words
	 * @return -1 If the verb is not found, otherwise returns the index in the word array where it was found.
	 */
	public int containsWord(String[] words){
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
