package com.retrontology.topsurvivor;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class TopSurvivorArchives
{
  private TopSurvivor plugin;
  private File dir;
  
  public TopSurvivorArchives(TopSurvivor plugin)
  {
    this.plugin = plugin;
    this.dir = new File(plugin.getDataFolder(), File.separator + "Logs");
  }
  
  public boolean listArchives(Player player, int page)
  {
    List<String> list = getArchivesList();
    if (!list.isEmpty())
    {
      int pagemax = list.size();
      pagemax = pagemax % 10 == 0 ? pagemax / 10 : pagemax / 10 + 1;
      if (page > pagemax)
      {
        player.sendMessage(ChatColor.RED + "lrn2page");
        return false;
      }
      int offset = page < 1 ? 0 : page - 1;
      player.sendMessage(ChatColor.YELLOW + "Contest list from Logs   Page " + page + "/" + pagemax);
      player.sendMessage(ChatColor.YELLOW + "==========================");
      for (int i = offset * 10; (i < list.size()) && (i < offset * 10 + 10); i++) {
        player.sendMessage(ChatColor.WHITE + (String)list.get(i));
      }
      return true;
    }
    player.sendMessage(ChatColor.RED + "There are no recorded contests in the Logs folder");
    return false;
  }
  
  public boolean listArchivePlayers(Player player, String filename, int page)
  {
    List<String> list = getArchivesList();
    if (list.contains(filename))
    {
      List<String> players = getOffsetPlayerList(filename, page < 1 ? 0 : page - 1);
      if (players != null)
      {
        int pagemax = getPageMax(filename);
        player.sendMessage(ChatColor.YELLOW + "Player list from Log " + TopSurvivor.dateformat.format(new Date(Long.parseLong(filename))) + "   Page " + page + "/" + pagemax);
        player.sendMessage(ChatColor.YELLOW + "==========================");
        String s;
        for (Iterator localIterator = players.iterator(); localIterator.hasNext(); player.sendMessage(s)) {
          s = (String)localIterator.next();
        }
        return true;
      }
      player.sendMessage(ChatColor.RED + "The file requested does not contain any data");
      return false;
    }
    player.sendMessage(ChatColor.RED + "The specified file was not found in the Logs folder");
    return false;
  }
  
  public boolean listPlayerData(Player player, String filename, String request)
  {
    List<String> list = getArchivesList();
    if (list.contains(filename))
    {
      List<String> data = getPlayerData(filename, request);
      if (data != null)
      {
        String s;
        for (Iterator localIterator = data.iterator(); localIterator.hasNext(); player.sendMessage(s)) {
          s = (String)localIterator.next();
        }
        return true;
      }
      player.sendMessage(ChatColor.RED + "The specified player was not found in the specified archive");
      return false;
    }
    player.sendMessage(ChatColor.RED + "The specified file was not found in the Logs folder");
    return false;
  }
  
  public List<String> getArchivesList()
  {
    File[] logs = this.dir.listFiles();
    List<String> archives = new ArrayList();
    File[] arrayOfFile1;
    int j = (arrayOfFile1 = logs).length;
    for (int i = 0; i < j; i++)
    {
      File f = arrayOfFile1[i];archives.add(f.getName());
    }
    Collections.sort(archives);
    Collections.reverse(archives);
    return archives;
  }
  
  private List<String> getPlayerList(String filename)
  {
    if (getArchivesList().contains(filename))
    {
      List<String> players = new ArrayList();
      File file = new File(this.dir, File.separator + filename);
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      for (int i = 1; config.contains(i + ".Name"); i++) {
        players.add(ChatColor.YELLOW + "" + i + ". " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(i)).append(".Name").toString()) + ": " + config.getString(new StringBuilder(String.valueOf(i)).append(".SurvivorTime").toString()));
      }
      return players;
    }
    return null;
  }
  
  private List<String> getOffsetPlayerList(String filename, int offset)
  {
    if (getArchivesList().contains(filename))
    {
      List<String> players = new ArrayList();
      File file = new File(this.dir, File.separator + filename);
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      int pagemax = getPageMax(filename);
      if (offset < pagemax)
      {
        for (int i = offset * 10 + 1; (config.contains(i + ".Name")) && (i < offset * 10 + 11); i++) {
          players.add(ChatColor.YELLOW + "" + i + ". " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(i)).append(".Name").toString()) + ": " + config.getString(new StringBuilder(String.valueOf(i)).append(".SurvivorTime").toString()));
        }
        return players;
      }
    }
    return null;
  }
  
  private List<String> getPlayerData(String filename, String player)
  {
    List<String> data = new ArrayList();
    if (getArchivesList().contains(filename))
    {
      File file = new File(this.dir, File.separator + filename);
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      int place = 0;
      for (int i = 1; config.contains(i + ".Name"); i++) {
        if (config.getString(i + ".Name").equalsIgnoreCase(player))
        {
          place = i;
          data.add(ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(place)).append(".Name").toString()) + ((player.charAt(player.length() - 1) == 's') || (player.charAt(player.length() - 1) == 'S') ? "'" : "'s") + ChatColor.YELLOW + " data from " + ChatColor.WHITE + TopSurvivor.dateformat.format(new Date(Long.parseLong(filename))));
          data.add(ChatColor.YELLOW + "==========================");
          data.add(ChatColor.YELLOW + "Place: " + ChatColor.WHITE + place);
          data.add(ChatColor.YELLOW + "Survivor Time: " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(place)).append(".SurvivorTime").toString()));
          data.add(ChatColor.YELLOW + "Top Time Alive: " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(place)).append(".TopTime").toString()));
          data.add(ChatColor.YELLOW + "Top AFK Time " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(place)).append(".TopAfk").toString()));
          data.add(ChatColor.YELLOW + "AFKTerminator Penalty: " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(place)).append(".AfkTPenalty").toString()));
          data.add(ChatColor.YELLOW + "Total AFK Time: " + ChatColor.WHITE + config.getString(new StringBuilder(String.valueOf(place)).append(".TotalAfkTime").toString()));
          return data;
        }
      }
    }
    return null;
  }
  
  private int getPageMax(String filename)
  {
    if (getArchivesList().contains(filename))
    {
      File file = new File(this.dir, File.separator + filename);
      FileConfiguration config = YamlConfiguration.loadConfiguration(file);
      int pagemax = 0;
      while (config.contains(pagemax * 10 + 1 + ".Name")) {
        pagemax++;
      }
      return pagemax;
    }
    return 0;
  }
}
