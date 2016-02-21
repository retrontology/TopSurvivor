package com.retrontology.topsurvivor;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TopSurvivorAFKTUpdate extends Event{

	private static final HandlerList handlers = new HandlerList();
	
	public HandlerList getHandlers() {
		return TopSurvivorAFKTUpdate.handlers;
	}
	
	public static HandlerList getHandlerList() {
        	return TopSurvivorAFKTUpdate.handlers;
	}
}
