package net.jamezo97.clonecraft.clone.ai;

import net.minecraft.item.ItemStack;

/**
 * Used to call external methods letting another AI section know the outcome of the item fetch, so it can be handled appropriately.
 * @author James
 *
 */
public interface Notifier{
	
	void onSuccess(ItemStack fetched);
	
	void onFetchedSome(ItemStack fetched);
	
	void onFailure(ItemStack failedToFetch);
	
	/**
	 * If true, the fetch AI will stop fetching once something has been fetched.
	 * @return
	 */
	boolean mustFetchAll();
	
}
