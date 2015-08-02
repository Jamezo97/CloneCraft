package net.jamezo97.clonecraft.clone;

import net.jamezo97.clonecraft.render.Renderable;
import net.jamezo97.clonecraft.render.RenderableManager;

public class RenderEntry {
	
	public RenderableManager manager;
	public Renderable renderer;
	
	public RenderEntry(RenderableManager manager, Renderable renderer)
	{
		this.manager = manager;
		this.renderer = renderer;
	}

	public void onRemoved()
	{
		this.manager.onRemoved();
		this.renderer.onRemoved();
	}

}
