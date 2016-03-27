package com.retrontology.topsurvivor;

import java.util.Comparator;

import org.bukkit.OfflinePlayer;

public class TopSurvivorComparator  implements Comparator<String>{
	@Override
    public int compare(String p1, String p2) {
        return (TopSurvivor.tshashmap.getTopSurvivorPlayer(p2).getTopTick() 
        			- TopSurvivor.tshashmap.getTopSurvivorPlayer(p2).getTopAfkTime() 
        			- TopSurvivor.tshashmap.getTopSurvivorPlayer(p2).getCurrentAfkTPenalty()) 
        		- (TopSurvivor.tshashmap.getTopSurvivorPlayer(p1).getTopTick() 
            			- TopSurvivor.tshashmap.getTopSurvivorPlayer(p1).getTopAfkTime() 
            			- TopSurvivor.tshashmap.getTopSurvivorPlayer(p1).getCurrentAfkTPenalty());
    }
}
