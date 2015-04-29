package net.jamezo97.clonecraft.command;

import java.util.HashMap;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.command.parameter.PGuess;
import net.jamezo97.clonecraft.command.parameter.ParamGuess;
import net.jamezo97.clonecraft.command.parameter.Parameter;

public class CurrentParams {
	
	HashMap<Parameter, PGuess> parameterToValue = new HashMap<Parameter, PGuess>();
	
	public boolean isParameterSet(Parameter parameter){
//		for(Entry<PGuess, Object> entry : parameterToValue.entrySet())
//		{
//			if(entry.getKey().param == parameter){
//				return true;
//			}
//		}
		return this.parameterToValue.containsKey(parameter);
	}
	
	public void setParameter(Parameter param, PGuess value){
		this.parameterToValue.put(param, value);
	}
	
	public int size(){
		return this.parameterToValue.size();
	}
	
	/**
	 * Merges another parameter set into this one. Overwrites
	 * @param other
	 */
	public void merge(CurrentParams other){
		for(Entry<Parameter, PGuess> entry : other.parameterToValue.entrySet())
		{
			this.setParameter(entry.getKey(), entry.getValue());
		}
	}
	
	public Parameter[] getParameters(){
		Parameter[] keys = new Parameter[this.parameterToValue.size()];
		
		int index = 0;
		
		for(Entry<Parameter, PGuess> entry : this.parameterToValue.entrySet()){
			keys[index++] = entry.getKey();
		}
		
		return keys;
	}
	
	public Parameter getMissingParam(Parameter[] params){
		if(params != null)
		{
			for(int a = 0; a < params.length; a++)
			{
				if(this.isParameterSet(params[a]))
				{
					return params[a];
				}
			}
		}
		
		return null;
	}
	
	public void clear(){
		this.parameterToValue.clear();
	}
	

}
