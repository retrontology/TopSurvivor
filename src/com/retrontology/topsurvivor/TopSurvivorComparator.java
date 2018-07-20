package com.retrontology.topsurvivor;

import java.util.Comparator;

public class TopSurvivorComparator
  implements Comparator<String>
{
  public int compare(String player1, String player2)
  {
    TopSurvivorPlayer p1 = TopSurvivor.tshashmap.getTopSurvivorPlayer(player1);
    TopSurvivorPlayer p2 = TopSurvivor.tshashmap.getTopSurvivorPlayer(player2);
    return p2.getTopTick() - p2.getTopAfkTime() - p2.getCurrentAfkTPenalty() - (p1.getTopTick() - p1.getTopAfkTime() - p1.getCurrentAfkTPenalty());
  }
}
