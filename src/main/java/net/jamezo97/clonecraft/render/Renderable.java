package net.jamezo97.clonecraft.render;

public interface Renderable {

	public void render(float partialTicks);

	public void onTick();

	public void onRemoved();
	
}
