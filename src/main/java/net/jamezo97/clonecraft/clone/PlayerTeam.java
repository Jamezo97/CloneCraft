package net.jamezo97.clonecraft.clone;

import net.minecraft.util.StatCollector;

public enum PlayerTeam {
	Red(0, 0xff0000), Orange(1, 0xff8000), Yellow(2, 0xffff00), Green(3, 0x00ff00), Blue(4, 0x0000ff), LightBlue(5, 0x0080ff),
	Purple(6, 0xc800ff), Pink(7, 0xffaaff), Good(8, 0xffffff), Evil(9, 0x555555);
	
	//Evil attacks everything inc. player
	
	//Colours attack other colours, but not their own
	//Good attacks Evil
	
	public static PlayerTeam getByName(String s){
		PlayerTeam[] allTeam = values();
		for(int a = 0; a < allTeam.length; a++){
			if(allTeam[a].name().equals(s)){
				return allTeam[a];
			}
		}
		for(int a = 0; a < allTeam.length; a++){
			if(allTeam[a].teamID == 3){
				return allTeam[a];
			}
		}
		return allTeam[0];
	}
	
	public static PlayerTeam getByID(int teamID2) {
		PlayerTeam[] allTeam = values();
		for(int a = 0; a < allTeam.length; a++){
			if(allTeam[a].teamID == teamID2){
				return allTeam[a];
			}
		}
		for(int a = 0; a < allTeam.length; a++){
			if(allTeam[a].teamID == 3){
				return allTeam[a];
			}
		}
		return allTeam[0];
	}
	
	public int teamID;
	public int teamColour;
	
	PlayerTeam(int id, int colour){
		teamID = id;
		teamColour = colour;
	}
	
	public boolean doesAttackTeam(PlayerTeam other){
		if(this != other && (this == Evil || other == Evil)){
			return true;
		}
		return other != this && other != Good && this != Good;
	}

	public String getUnloc() {
		return "clonecraft.cteam." + this.name();
	}
	
	public String getNameLoc(){
		return StatCollector.translateToLocal(getUnloc());
	}

	public String getInfo() {
		if(this != Good && this != Evil){
			return StatCollector.translateToLocal("clonecraft.cteam.info.colour").replace("@team", this.getNameLoc());
		}else if(this == Good){
			return StatCollector.translateToLocal("clonecraft.cteam.info.good");
		}else/*(this == Evil)*/{
			return StatCollector.translateToLocal("clonecraft.cteam.info.evil");
		}
		
	}
	
	//!(a && b) = (!a || !b)
	//!(a || b) = (!a && !b)
}
