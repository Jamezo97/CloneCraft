package net.jamezo97.clonecraft.command.word;

import java.util.ArrayList;

import scala.util.Random;

public class WordSet {
	public static final ArrayList<WordSet> registeredWordSets = new ArrayList<WordSet>();
	
//	public static final ActionWord ACTION_go = new ActionWord("go");
	public static final WordSet dig = new WordSet(true, "dig", "mine", "break");
	public static final WordSet build = new WordSet(true, "build", "construct", "make");
	public static final WordSet chop = new WordSet(true, "chop", "cut");
	public static final WordSet attack = new WordSet(true, "kill", "attack", "maime", "destroy", "suicide").setStrength(1.2f);
//	public static final WordSet suicide = new WordSet("suicide", "kill", "destroy").setStrength(0.7f);
	public static final WordSet jump = new WordSet(true, "jump");
	public static final WordSet follow = new WordSet(true, "follow", "track", "lead", "following", "tracking");
	public static final WordSet stop = new WordSet(true, "stop", "cancel", "forget", "nevermind");
	public static final WordSet stay = new WordSet(true, "stay", "remain", "still", "guard", "defend", "guarding", "defending", "staying");
//	public static final WordSet guard = new WordSet(true, );
//	public static final VerbSet farm = new VerbSet("farm");
	public static final WordSet come = new WordSet(true, "come");
	public static final WordSet give = new WordSet(true, "give", "bring");
	public static final WordSet hello = new WordSet("hello", "hey", "hi", "yo", "wassup").setStrength(0.1f);

	
	
	public static int size(){
		return registeredWordSets.size();
	}
	
	public static WordSet get(int index){
		return registeredWordSets.get(index);
	}
	
	private String[] alias = null;
	
	float strength = 1.0f;
	
	public WordSet(boolean addToList, String... alias){
		if(alias == null)
		{
			throw new RuntimeException("A KeyWord must have at least one identifiable word.");
		}
		this.alias = alias;
		if(addToList)
		{
			registeredWordSets.add(this);
		}
	}
	
	public WordSet(String... alias){
		this(false, alias);
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
	
	boolean canPlural = false;
	
	public WordSet setCanHavePlurals(){
		canPlural = true;
		return this;
	}
	
	/**
	 * 
	 * @param words
	 * @return -1 If the verb is not found, otherwise
	 * Returns two shorts compiled into one integer. First 16 bits are the index
	 * The second 16 bits is the word count.
	 * 
	 * otherwise returns the index in the word array where it was found.
	 */
	public int containsWord(String[] words)
	{
		return containsWord(alias, words, canPlural);
	}
	/**
	 * 
	 * @param alias The words to search for. The Needle. The Base Words
	 * @param words The words to search in. The Haystack. The input words.
	 * @param canHavePlurals Whether the will equal if plural.
	 * @return Two shorts in one int. First 16 bytes are the word index.
	 * Second 16 bytes is the word length. i.e. how many words.
	 */
	public static int containsWord(String[] alias, String[] words, boolean canHavePlurals){
		for(int a = 0; a < alias.length; a++)
		{
			String baseWord = alias[a];
			
			for(int b = 0; b < words.length; b++)
			{
				if(baseWord.contains(" "))
				{
					String[] split = baseWord.split(" ");
					int c = 0;
					for(; c < split.length && c + b < words.length; c++)
					{
						if(!areWordsSimilar(split[c], words[c+b], canHavePlurals))
						{
							break;
						}
					}
					if(c == split.length)
					{
						return b | (split.length << 16);
					}
				}
				else
				{
					if(areWordsSimilar(words[b], baseWord, canHavePlurals))
					{
						return b | (1 << 16);
					}
				}
				
			}
		}
		return -1;
	}
	/**
	 * 
	 * @param base The singular version of the word to check
	 * @param toCheck The word to check
	 * @param canPlural Whether the word to check may be a plural
	 * @return
	 */
	public static boolean areWordsSimilar(String base, String toCheck, boolean canPlural){
		return toCheck.equalsIgnoreCase(base) || 
				(canPlural && toCheck.length() > 1 && 
				toCheck.substring(0, toCheck.length()-1).equalsIgnoreCase(base));
	}
	
	public float getStrength(){
		return this.strength;
	}
	
	int lastChosen = 0;
	
	public String getRandom(){
		if(this.alias.length > 0)
		{
			int chosen = rand.nextInt(this.alias.length);
			
			if(chosen == lastChosen)
			{
				chosen++;
				if(chosen >= this.alias.length)
				{
					chosen = 0;
				}
			}
			
			return this.alias[lastChosen=chosen];
		}
		return "";
	}
	
	private static Random rand = new Random();
}
