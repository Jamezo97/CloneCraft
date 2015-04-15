package net.jamezo97.clonecraft.musics;

import java.util.ArrayList;

public class MusicBase {
	
	private static ArrayList<MusicGetter> musics = new ArrayList<MusicGetter>();
	
	static
	{
		load();
	}
	
	private static void load()
	{
		musics.add(new CountingStars());	//0
		musics.add(new CrazyFrog());		//1
		musics.add(new Levels());			//2
		musics.add(new Popcorn());			//3
		musics.add(new Sandstorm());		//4
//		musics.add(new ShakeItOff());		//5
		musics.add(new Turkish());			//6
		musics.add(new WakeMeUp());			//7
		musics.add(new Clocks());			//8
	}
	
	public static int getSize(int id)
	{
		return musics.get(id).getData().length;
	}
	
	public static float[] getData(int id)
	{
		return musics.get(id).getData();
	}
	
	public static float getPitch(int id, int index)
	{
		return musics.get(id).getData()[index];
	}

	public static int getSize()
	{
		return musics.size();
	}

}
