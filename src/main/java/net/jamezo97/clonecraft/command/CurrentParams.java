package net.jamezo97.clonecraft.command;

import java.util.HashMap;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.command.parameter.Parameter;

public class CurrentParams {
	
	HashMap<Parameter, Object> parameterToValue = new HashMap<Parameter, Object>();
	
	public boolean isParameterSet(Parameter parameter){
		return this.parameterToValue.containsKey(parameter);
	}
	
	public void setParameter(Parameter param, Object value){
		this.parameterToValue.put(param, value);
	}
	
	public int size(){
		return this.parameterToValue.size();
	}
	
	public Parameter[] getParameters(){
		Parameter[] keys = new Parameter[this.parameterToValue.size()];
		
		int index = 0;
		
		for(Entry<Parameter, Object> entry : this.parameterToValue.entrySet()){
			keys[index++] = entry.getKey();
		}
		
		return keys;
	}
	
	public void clear(){
		this.parameterToValue.clear();
	}
	

}
