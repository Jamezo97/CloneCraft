package net.jamezo97.clonecraft.town;

import java.util.ArrayList;

public class TownList
{
	
	ArrayList<Town> allTowns = new ArrayList<Town>();
	
	public static TownList instance;
	
	long townID = 0;
	
	public TownList()
	{
		this.instance = this;
	}
	
	public static long nextTownID(){return instance.getNextTownID();}
	
	public long getNextTownID()
	{
		return townID++;
	}
	
	

}
