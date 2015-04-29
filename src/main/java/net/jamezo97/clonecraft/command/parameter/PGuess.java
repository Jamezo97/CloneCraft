package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.util.SimpleList;

public class PGuess{
	
	public final Parameter param;
	
	
	
/*	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PGuess)
		{
			return ((PGuess)obj).param == param;
		}
		if(obj instanceof Parameter)
		{
			return ((Parameter)obj)==param;
		}
		return super.equals(obj);
	}*/

	public PGuess(Parameter param){
		this.param = param;
	}
	
	public SimpleList<ParamGuess> guesses = new SimpleList<ParamGuess>();

	public void add(ParamGuess p){
		this.guesses.add(p);
	}
	
	public ParamGuess getBestGuess(){
		ParamGuess guess = null;
		float confidence = 0;
		for(int a = 0; a < size(); a++)
		{
			if(guess == null || guesses.get(a).confidence > confidence){
				guess = guesses.get(a);
				confidence = guess.confidence;
			}
		}
		
		return guess;
	}
	
	public void addTo(PGuess p){
		for(int a = 0; a < guesses.size(); a++){
			p.guesses.add(guesses.get(a));
		}
	}
	
	public void amplify(float factor){
		for(int a = 0; a < guesses.size(); a++){
			guesses.get(a).confidence *= factor;
		}
	}
	
	public void addFrom(PGuess p){
		p.addTo(this);
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
