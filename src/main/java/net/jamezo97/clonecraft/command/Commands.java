package net.jamezo97.clonecraft.command;

import java.util.ArrayList;

public class Commands {
	
	
	private static ArrayList<Command> allCommands = new ArrayList<Command>();
	
	public static void registerCommand(Command command){
		allCommands.add(command);
	}
	
	static{
		registerCommand(new CommandFollow());
	}

}
