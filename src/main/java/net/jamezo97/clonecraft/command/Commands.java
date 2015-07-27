package net.jamezo97.clonecraft.command;

import java.util.ArrayList;

public class Commands {
	
	
	private static ArrayList<Command> allCommands = new ArrayList<Command>();
	
	public static int size(){
		return allCommands.size();
	}
	
	public static Command get(int index){
		return allCommands.get(index);
	}
	
	public static void registerCommand(Command command, int id){
		allCommands.add(command.setId(id));
	}
	
	public static Command getCommandById(int id) {
		for(int a = 0; a < allCommands.size(); a++)
		{
			if(allCommands.get(a).getId() == id)
			{
				return allCommands.get(a);
			}
		}
		return null;
	}
	
	static{
		registerCommand(new CommandHello(), 0);
		registerCommand(new CommandFollow(), 1);
		registerCommand(new CommandKill(), 2);
		registerCommand(new CommandStop(), 3);
		registerCommand(new CommandStay(), 4);
		registerCommand(new CommandCome(), 5);
	}

	

}
