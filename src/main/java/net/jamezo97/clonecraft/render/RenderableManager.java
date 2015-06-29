package net.jamezo97.clonecraft.render;

public interface RenderableManager {

	public boolean canRenderContinue(Renderable renderable);

	public void onRemoved();
	
}
