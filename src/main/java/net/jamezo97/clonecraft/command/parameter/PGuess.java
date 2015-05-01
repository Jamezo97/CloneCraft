package net.jamezo97.clonecraft.command.parameter;

import java.util.ArrayList;
import java.util.Collections;

import scala.Array;
import net.minecraft.entity.EntityLivingBase;

public class PGuess{
	
	public final Parameter param;
	
	public PGuess(Parameter param){
		this.param = param;
	}
	
	public ArrayList<ParamGuess> guesses = new ArrayList<ParamGuess>();

	public void add(ParamGuess p){
		this.guesses.add(p);
	}
	
	public void sort(){
		Collections.sort(guesses);
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

	public <E> E[] castToArray(Class<? extends E> theClass) {
		
		ArrayList<E> casted = new ArrayList<E>();
		
		for(int a = 0; a < size(); a++)
		{
			ParamGuess guess = get(a);
			if(guess.value != null && theClass.isAssignableFrom(guess.value.getClass()))
			{
				casted.add(theClass.cast(guess.value));
			}
		}

		E[] array = (E[]) java.lang.reflect.Array.newInstance(theClass, casted.size());
		
		casted.toArray(array);
		
		return array;
	}
	
}
