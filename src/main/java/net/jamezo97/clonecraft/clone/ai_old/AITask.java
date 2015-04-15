package net.jamezo97.clonecraft.clone.ai_old;

public class AITask implements Comparable<AITask>{
	
	@Override
	public int compareTo(AITask task) {
		return this.priority == task.priority?0:(this.priority > task.priority?+1:-1);
	}

	public boolean canStartTask(){
		return false;
	}
	
	public boolean canEndTask(){
		return !canStartTask();
	}
	
	public void onTaskTick(){
		
	}
	
	public void beginTask(){
		
	}
	
	public void endTask(){
		
	}
//	
//	boolean iWantToRun = false;
//	
//	public void tryForceStart(){
//		iWantToRun = true;
//	}
//	
//	public boolean shouldForceStart(){
//		if(iWantToRun){
//			iWantToRun = false;
//			return true;
//		}
//		return false;
//	}
	
	int priority = 0;
	public void setPriority(int priority){
		this.priority = priority;
	}
	public int getPriority(){
		return priority;
	}

	
	

}
