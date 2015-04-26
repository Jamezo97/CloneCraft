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
public class ParamGuess {
	
	public Object value;
	
	public float confidence = 0.5f;
	
	public ParamGuess(Object value, float confidence){
		if(confidence < 0 || confidence > 1){
			throw new InvalidParameterException("Confidence level must be between 0 and 1");
		}
		this.value = value;
		this.confidence = confidence;
	}

}
