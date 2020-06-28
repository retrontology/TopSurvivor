package com.retrontology.topsurvivor;

import com.Ben12345rocks.VotingPlugin.UserManager.UserManager;
import java.util.Comparator;
import org.bukkit.OfflinePlayer;

public class TopSurvivorVotifierComparator
  implements Comparator<OfflinePlayer>
{
  public int compare(OfflinePlayer player1, OfflinePlayer player2)
  {
    return UserManager.getInstance().getVotingPluginUser(player2.getName()).getPoints() - UserManager.getInstance().getVotingPluginUser(player1.getName()).getPoints();
  }
}
