package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.util.SimpleList;

public class PGuess {
	
	public SimpleList<ParamGuess> guesses = new SimpleList<ParamGuess>();

	public void add(ParamGuess p){
		this.guesses.add(p);
	}
	
	public ParamGuess[] build(){
		if(this.guesses.size() > 0){
			return this.guesses.toArray(new ParamGuess[guesses.size()]);
		}
		return null;
	}
	
	public ParamGuess get(int index){
		return guesses.get(index);
	}
	
	public int size(){
		return this.guesses.size();
	}
	
}
