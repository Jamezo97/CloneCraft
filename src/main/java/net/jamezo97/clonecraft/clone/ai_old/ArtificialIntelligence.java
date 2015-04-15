package net.jamezo97.clonecraft.clone.ai_old;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.util.SortedList;

public class ArtificialIntelligence {
	
	SortedList<AITask> tasksOrdered = new SortedList<AITask>();
	
	AITask currentTask;
	
	public final EntityClone clone;
	
	public ArtificialIntelligence(EntityClone clone){
		this.clone = clone;
	}
	
	public void addTask(int priority, AITask task){
		task.setPriority(priority);
		tasksOrdered.add(task);
	}
	
	public void tick(){
		AITask possibleTask = this.getHighestTaskToExecute();
		if(possibleTask != null){
			if(currentTask == null){
				currentTask = possibleTask;
				currentTask.beginTask();
			}else{
				if(currentTask.priority > possibleTask.priority){
					currentTask.endTask();
					currentTask = possibleTask;
					currentTask.beginTask();
				}
			}
		}
		if(currentTask != null){
			currentTask.onTaskTick();
			if(currentTask.canEndTask()){
				currentTask.endTask();
				currentTask = null;
			}
		}
	}
	
	
	public AITask getHighestTaskToExecute(){
		AITask toRun = null;
		AITask temp;
		for(int a = tasksOrdered.size()-1; a >= 0; a--){
			temp = tasksOrdered.get(a);
			if(temp != currentTask && temp.canStartTask()){
				toRun = temp;
			}
		}
		return toRun;
	}
}
