package com.retrontology.topsurvivor;

import java.util.Comparator;

public class TopSurvivorAfkComparator implements Comparator<String>{
	@Override
    public int compare(String player1, String player2) {
		TopSurvivorPlayer p1 = TopSurvivor.tshashmap.getTopSurvivorPlayer(player1);
		TopSurvivorPlayer p2 = TopSurvivor.tshashmap.getTopSurvivorPlayer(player2);
        return p2.getTotalAfkTime() - p1.getTotalAfkTime();
    }
}
