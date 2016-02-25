package com.retrontology.topsurvivor;

import java.util.Comparator;

import org.bukkit.OfflinePlayer;

public class TopSurvivorComparator  implements Comparator<OfflinePlayer>{
	@Override
    public int compare(OfflinePlayer p1, OfflinePlayer p2) {
        return (TopSurvivor.toptickobjective.getScore(p2).getScore() - TopSurvivor.topafktimeobjective.getScore(p2).getScore() - TopSurvivor.afktpenaltyobjective.getScore(p2).getScore()) - (TopSurvivor.toptickobjective.getScore(p1).getScore() - TopSurvivor.topafktimeobjective.getScore(p1).getScore() - TopSurvivor.afktpenaltyobjective.getScore(p1).getScore());
    }
}
