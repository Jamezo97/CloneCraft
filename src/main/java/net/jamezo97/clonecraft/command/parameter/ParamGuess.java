package net.jamezo97.clonecraft.command.parameter;

import java.security.InvalidParameterException;

/**
 * When a parameter is asked to interpret the String, it returns this class. It's a guess at what the user wants
 * Because it's hard to be sure without delving into some complex englishes that I'm not qualified to do
 * Basically, if it required an integer, and the parameter finds a number like 2.0. Then it is an integer, but not exactly
 * So it may return a value of about 0.3.
 * @author James
 *
 */
public class ParamGuess <E> implements Comparable<ParamGuess>{
	
	public E value;
	
	//Confidence level values of 0 should be confirmed by the command before execution.
	public float confidence = 0.5f;
	
	public ParamGuess(E value, float confidence){
		if(confidence < 0){
			throw new InvalidParameterException("Confidence level must be non-negative");
		}
		this.value = value;
		this.confidence = confidence;
	}

	@Override
	public int compareTo(ParamGuess p) {
		if(this.confidence > p.confidence)
		{
			return 1;
		}
		else if(this.confidence < p.confidence)
		{
			return -1;
		}
		return 0;
	}
	
	

}
