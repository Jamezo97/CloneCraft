package net.jamezo97.clonecraft.command;

public class KeyWords {
	
	public static final KeyWord KEY_go = new KeyWord("go");
	public static final KeyWord KEY_dig = new KeyWord("dig", "mine");
	public static final KeyWord KEY_build = new KeyWord("build", "construct", "make");
	public static final KeyWord KEY_attack = new KeyWord("kill", "attack", "maime", "destroy").setStrength(0.7f);
	public static final KeyWord KEY_jump = new KeyWord("jump");
	public static final KeyWord KEY_follow = new KeyWord("follow", "track", "lead");
	public static final KeyWord KEY_stop = new KeyWord("stop", "cancel", "forget", "nevermind");
	public static final KeyWord KEY_stay = new KeyWord("stay", "remain", "still");
	public static final KeyWord KEY_guard = new KeyWord("guard", "defend");
	public static final KeyWord KEY_farm = new KeyWord("farm");
	public static final KeyWord KEY_come = new KeyWord("come");
	public static final KeyWord KEY_give = new KeyWord("give", "bring");

	

}
