package com.retrontology.topsurvivor;

import com.swifteh.GAL.VoteAPI;
import java.util.Comparator;
import org.bukkit.OfflinePlayer;

public class TopSurvivorVotifierComparator
  implements Comparator<OfflinePlayer>
{
  public int compare(OfflinePlayer player1, OfflinePlayer player2)
  {
    return VoteAPI.getVoteTotal(player2.getName()) - VoteAPI.getVoteTotal(player1.getName());
  }
}
