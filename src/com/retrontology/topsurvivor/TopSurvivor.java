package com.retrontology.topsurvivor;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.retrontology.prizes.Prizes;
import com.Ben12345rocks.VotingPlugin.UserManager.UserManager;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
/*
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import com.sk89q.worldguard.*;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
*/
import com.sk89q.worldedit.BlockVector;

public class TopSurvivor
  extends JavaPlugin
  implements Listener
{
  public static ScoreboardManager tsmanager;
  public static Scoreboard tsboard;
  public static Objective survivortimeobjective;
  public static Objective timesincedeathobjective;
  public static Objective playerkillsobjective;
  public static Objective deathsobjective;
  public static Essentials ess;
  public static Server server;
  public static TopSurvivorHashMap tshashmap;
  public static TopSurvivorArchives tsarchives;
  public static File playerDir;
  public static File configFile;
  public static FileConfiguration config;
  public UUID mebb;
  public UUID tox;
  private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
  public static DateFormat dateformat;
  
  public void onEnable()
  {
    server = getServer();
    ess = (Essentials)getServer().getPluginManager().getPlugin("Essentials");
    this.mebb = UUID.fromString("74f453af-0148-47d9-8d6a-6780b64ce5c4");
    this.tox = UUID.fromString("c4b57206-c37c-4e2f-b9c2-b22bcbc80efb");
    
    dateformat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");
    dateformat.setTimeZone(TimeZone.getTimeZone("PST"));
    playerDir = new File(server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator + "Players");
    if (!playerDir.exists()) {
      playerDir.mkdirs();
    }
    loadConfigFile();
    if (!config.contains("Flag.UpdatePlayerFiles")) {
      config.set("Flag.UpdatePlayerFiles", Boolean.valueOf(true));
    }
    if ((config.getBoolean("Flag.UpdatePlayerFiles")) && 
      (updateFilesToUUID()))
    {
      config.set("Flag.UpdatePlayerFiles", Boolean.valueOf(false));
      saveConfigFile();
    }
    if (!getFlagContest())
    {
      setContestStart(new Date().getTime());
      setFlagContest(true);
    }
    tshashmap = new TopSurvivorHashMap(this);
    
    tsarchives = new TopSurvivorArchives(this);
    
    makeScoreboard();
    Player p;
    for (Iterator localIterator = server.getOnlinePlayers().iterator(); localIterator.hasNext(); initPlayer(p)) {
      p = (Player)localIterator.next();
    }
    server.getPluginManager().registerEvents(new TopSurvivorListener(this), this);
    
    BukkitScheduler scheduler = server.getScheduler();
    scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorTask(this), 0L, getUpdateTime());
    if (getAFKTerminator()) {
      scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorAFKTUpdateTask(this), 0L, getAFKTPollTime());
    }
    scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorDisplayTask(this), 0L, getDisplayTime());
    TopSurvivorCommandExecutor tscommandexec = new TopSurvivorCommandExecutor(this);
    getCommand("topsurvivor").setExecutor(tscommandexec);
  }
  
  public void onDisable()
  {
    for (Player p : server.getOnlinePlayers())
    {
      tshashmap.onLeave(p);
      
      refreshPlayer(p);
    }
    Bukkit.getPluginManager().callEvent(this.tsupdate);
    
    survivortimeobjective.unregister();
  }
  
  public void makeScoreboard()
  {
    tsmanager = server.getScoreboardManager();
    tsboard = tsmanager.getMainScoreboard();
    if ((survivortimeobjective = tsboard.getObjective("survivortime")) == null) {
      survivortimeobjective = tsboard.registerNewObjective("survivortime", "dummy");
    }
    if (!survivortimeobjective.getDisplayName().equals("Top Survivors (Days)")) {
      survivortimeobjective.setDisplayName("Top Survivors (Days)");
    }
    if ((timesincedeathobjective = tsboard.getObjective("timesincedeath")) == null) {
      timesincedeathobjective = tsboard.registerNewObjective("timesincedeath", "stat.timeSinceDeath");
    }
    if ((playerkillsobjective = tsboard.getObjective("tsplayerkills")) == null) {
      playerkillsobjective = tsboard.registerNewObjective("tsplayerkills", "playerKillCount");
    }
    if (!playerkillsobjective.getDisplayName().equals("Player Kills")) {
      playerkillsobjective.setDisplayName("Player Kills");
    }
    if ((deathsobjective = tsboard.getObjective("tsdeaths")) == null) {
      deathsobjective = tsboard.registerNewObjective("tsdeaths", "deathCount");
    }
    if (!deathsobjective.getDisplayName().equals("Deaths")) {
      deathsobjective.setDisplayName("Deaths");
    }
    if(config.getBoolean("Display.Sidebar")){ survivortimeobjective.setDisplaySlot(DisplaySlot.SIDEBAR); }
    playerkillsobjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
    deathsobjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
  }
  
  public void refreshScoreboard()
  {
    List<String> list = getSortedList();
    survivortimeobjective.unregister();
    makeScoreboard();
    for (int i = 0; (i < getDisplayCount()) && (i < list.size()); i++) {
      survivortimeobjective.getScore((String)list.get(i)).setScore(tshashmap.getTopSurvivorPlayer((String)list.get(i)).getSurvivorTime());
    }
    server.getLogger().info("[Top Survivor] Top Survivors list updated");
  }
  
  public void resetScoreboard()
  {
    for (Player p : server.getOnlinePlayers())
    {
      TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(p);
      
      tsplayer.setTotalAfkTime(tsplayer.getTotalAfkTime() + tsplayer.getCurrentAfkTime());
      tsplayer.setTotalDeaths((tsplayer.getTotalDeaths() == null ? 0 : tsplayer.getTotalDeaths().intValue()) + deathsobjective.getScore(p).getScore());
      tsplayer.setTotalPlayerKills((tsplayer.getTotalPlayerKills() == null ? 0 : tsplayer.getTotalPlayerKills().intValue()) + playerkillsobjective.getScore(p).getScore());
      
      refreshPlayer(p);
    }
    List<String> topsurvivors = getSortedList();
    if (config.getBoolean("LogFile"))
    {
      File log = new File(server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator + "Logs");
      if (!log.exists()) {
        log.mkdirs();
      }
      log = new File(log, File.separator + new Date().getTime());
      try
      {
        log.createNewFile();
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      FileConfiguration logconfig = YamlConfiguration.loadConfiguration(log);
      int count = 1;
      for (String name : topsurvivors)
      {
        TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(name);
        logconfig.set(count + ".Name", name);
        logconfig.set(count + ".SurvivorTime", TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
        logconfig.set(count + ".TopTime", TimeConverter.getString(tsp.getTopTick()));
        logconfig.set(count + ".TopAfk", TimeConverter.getString(tsp.getTopAfkTime()));
        logconfig.set(count + ".AfkTPenalty", TimeConverter.getString(tsp.getCurrentAfkTPenalty()));
        logconfig.set(count + ".TotalAfkTime", TimeConverter.getString(tsp.getTotalAfkTime()));
        count++;
      }
      try
      {
        logconfig.save(log);
        server.getLogger().info("[Top Survivor] The players' data has been recorded to: Logs" + File.separator + log.getName());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }
    server.getLogger().info("[Top Survivor] The score for each player is:");
    server.getLogger().info("--------------------------------------------");
    for (String player : topsurvivors)
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      server.getLogger().info("[Top Survivor] " + player + ": " + TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
    }
    Prizes.registerContest(getName());
    if (!Prizes.makeFileFromUUIDList(getName(), getSortedUUIDList())) {
      server.getLogger().info("[Top Survivor] The Top Survivor prizes could not be passed to the Prizes plugin");
    }
    survivortimeobjective.unregister();
    timesincedeathobjective.unregister();
    playerkillsobjective.unregister();
    deathsobjective.unregister();
    for (String player : topsurvivors)
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      if (!tsp.getFlagPermaban())
      {
        if (tsp.getBanLength() > 0) {
          tsp.setBanLength(tsp.getBanLength() - 1);
        }
        if ((tsp.getBanLength() == 0) && (tsp.getFlagExempt())) {
          tsp.setFlagExempt(false);
        }
        if (!tsp.getFlagExempt()) {
          tshashmap.deleteTopSurvivorPlayer(player);
        }
      }
    }
    makeScoreboard();
    
    for(Player p:server.getOnlinePlayers()){ initPlayer(p); }
    
    setContestStart(new Date().getTime());
    setFlagContest(true);
    
    //resetVotifier();
    
    server.getLogger().info("[Top Survivor] The scoreboard has been reset!");
  }
  
  public boolean resetVotifier()
  {
    ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
    Prizes.registerContest("Votifier");
    boolean flag = Prizes.makeFileFromUUIDList("Votifier", getVotifierUUIDList());
    Bukkit.dispatchCommand(console, "gal forcequeue");
    if (!flag) {
      server.getLogger().info("[Top Survivor] The Votifier prizes could not be passed to the Prizes plugin");
    }
    Bukkit.dispatchCommand(console, "gal clearqueue");
    Bukkit.dispatchCommand(console, "gal cleartotals");
    return flag;
  }
  
  public boolean viewScoreboard(Player player, int page)
  {
    List<String> topsurvivors = getSortedList();
    
    int pagemax = topsurvivors.size();
    pagemax = pagemax % 10 == 0 ? pagemax / 10 : pagemax / 10 + 1;
    if (page > pagemax) {
      return false;
    }
    int offset = (page - 1) * 10;
    player.sendMessage(ChatColor.YELLOW + "---- Top Survivors -- Page " + page + "/" + pagemax + " ----");
    for (int i = offset; (i < 10 + offset) && (i < topsurvivors.size()); i++)
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer((String)topsurvivors.get(i));
      player.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + (String)topsurvivors.get(i) + ": " + TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
    }
    return true;
  }


  public boolean viewVoteScoreboard(Player player, int page)
  {
    List<OfflinePlayer> topsurvivors = getVotifierList();
    
    int pagemax = topsurvivors.size();
    pagemax = pagemax % 10 == 0 ? pagemax / 10 : pagemax / 10 + 1;
    if (page > pagemax) {
      return false;
    }
    int offset = (page - 1) * 10;
    player.sendMessage(ChatColor.YELLOW + "---- Top Voters -- Page " + page + "/" + pagemax + " ----");
    for (int i = offset; (i < 10 + offset) && (i < topsurvivors.size()); i++) {
      player.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + ((OfflinePlayer)topsurvivors.get(i)).getName() + ": " + UserManager.getInstance().getVotingPluginUser(((OfflinePlayer)topsurvivors.get(i)).getName()).getPoints());
    }
    return true;
  }

  
  public boolean viewAfkScoreboard(Player player, int page)
  {
    List<String> topsurvivors = getAfkList();
    
    int pagemax = topsurvivors.size();
    pagemax = pagemax % 10 == 0 ? pagemax / 10 : pagemax / 10 + 1;
    if (page > pagemax) {
      return false;
    }
    int offset = (page - 1) * 10;
    player.sendMessage(ChatColor.YELLOW + "---- Top AFKers -- Page " + page + "/" + pagemax + " ----");
    for (int i = offset; (i < 10 + offset) && (i < topsurvivors.size()); i++)
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer((String)topsurvivors.get(i));
      player.sendMessage(ChatColor.YELLOW + "" + (i + 1) + ". " + (String)topsurvivors.get(i) + ": " + TimeConverter.getString(tsp.getCurrentAfkTime() + tsp.getTotalAfkTime()));
    }
    return true;
  }
  
  public boolean viewPlayer(Player player, String requestedplayer)
  {
    if (getPlayerList().contains(requestedplayer))
    {
      String groups = "";
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(requestedplayer);
      ess = (Essentials)getServer().getPluginManager().getPlugin("Essentials");
      User user = ess.getUser(requestedplayer);
      player.sendMessage(ChatColor.YELLOW + "==========================");
      if ((user == null) || (user._getNickname() == null) || (user._getNickname().equals(requestedplayer))) {
        player.sendMessage(ChatColor.YELLOW + "Survivor Name: " + ChatColor.WHITE + requestedplayer);
      } else {
        player.sendMessage(ChatColor.YELLOW + "Survivor Name: " + ChatColor.WHITE + requestedplayer + ChatColor.YELLOW + " aka " + ChatColor.WHITE + user._getNickname());
      }
      player.sendMessage(ChatColor.YELLOW + "Current K/D: " + ChatColor.WHITE + playerkillsobjective.getScore(requestedplayer).getScore() + "/" + deathsobjective.getScore(requestedplayer).getScore());
      player.sendMessage(ChatColor.YELLOW + "Total K/D: " + ChatColor.WHITE + ((tsp.getTotalPlayerKills() == null ? 0 : tsp.getTotalPlayerKills().intValue()) + playerkillsobjective.getScore(requestedplayer).getScore()) + "/" + ((tsp.getTotalDeaths() == null ? 0 : tsp.getTotalDeaths().intValue()) + deathsobjective.getScore(requestedplayer).getScore()));
      player.sendMessage(ChatColor.YELLOW + "Membership: " + ChatColor.WHITE + "[" + groups + "]");
      player.sendMessage(ChatColor.YELLOW + "Spawned in: " + ChatColor.WHITE + dateformat.format(new Date(getServer().getOfflinePlayer(requestedplayer).getFirstPlayed())));
      player.sendMessage(ChatColor.YELLOW + "Last seen: " + ChatColor.WHITE + (getServer().getOfflinePlayer(requestedplayer).isOnline() ? "Online Now" : dateformat.format(new Date(getServer().getOfflinePlayer(requestedplayer).getLastPlayed()))));
      player.sendMessage(ChatColor.YELLOW + "Surviving for: " + ChatColor.WHITE + TimeConverter.getString(timesincedeathobjective.getScore(requestedplayer).getScore() - tsp.getCurrentAfkTime() - tsp.getCurrentAfkTPenalty()));
      player.sendMessage(ChatColor.YELLOW + "Last Death: " + ChatColor.WHITE + (tsp.getLastDeath().longValue() == 0L ? "This player has not died yet" : dateformat.format(new Date(tsp.getLastDeath().longValue()))));
      //player.sendMessage(ChatColor.YELLOW + "Number of Votes: " + ChatColor.WHITE + VoteAPI.getVoteTotal(requestedplayer));
      if (tsp.getFlagExempt()) {
        if (tsp.getFlagPermaban()) {
          player.sendMessage(ChatColor.RED + "Banned from participating indefinitely");
        } else {
          player.sendMessage(ChatColor.RED + "Disqualified for " + ChatColor.YELLOW + tsp.getBanLength() + ChatColor.RED + " cycles");
        }
      }
      player.sendMessage(ChatColor.YELLOW + "==========================");
      if ((player.hasPermission("topsurvivor.admin")) || (player.hasPermission("topsurvivor.historian")))
      {
        player.sendMessage(ChatColor.YELLOW + "----- Administrator info -----");
        player.sendMessage(ChatColor.YELLOW + "Longest Survival Time: " + ChatColor.WHITE + TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
        player.sendMessage(ChatColor.YELLOW + "Time Since Last Death: " + ChatColor.WHITE + TimeConverter.getString(timesincedeathobjective.getScore(requestedplayer).getScore()));
        player.sendMessage(ChatColor.YELLOW + "AFK Time Since Last Death: " + ChatColor.WHITE + TimeConverter.getString(tsp.getCurrentAfkTime()));
        player.sendMessage(ChatColor.YELLOW + "AFK Terminator Penalty: " + ChatColor.WHITE + TimeConverter.getString(tsp.getCurrentAfkTPenalty()));
        player.sendMessage(ChatColor.YELLOW + "Is Banned: " + ChatColor.WHITE + (tsp.getFlagExempt() ? "Yah lol" : "Nope"));
        player.sendMessage(ChatColor.YELLOW + "Is PermaBanned: " + ChatColor.WHITE + (tsp.getFlagPermaban() ? "Yah lol" : "Nope"));
      }
      return true;
    }
    return false;
  }
  
  public List<String> getSortedList()
  {
    File[] topsurvivorarray = playerDir.listFiles();
    List<String> topsurvivors = new ArrayList();
    if (topsurvivorarray.length != 0)
    {
      File[] arrayOfFile1;
      int j = (arrayOfFile1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        File playerfile = arrayOfFile1[i];
        String player = getServer().getOfflinePlayer(UUID.fromString(playerfile.getName().substring(0, playerfile.getName().indexOf('.')))).getName();
        if ((player != null) && (!tshashmap.getTopSurvivorPlayer(player).getFlagExempt())) {
          topsurvivors.add(player);
        }
      }
      Collections.sort(topsurvivors, new TopSurvivorComparator());
    }
    return topsurvivors;
  }
  
  public List<UUID> getSortedUUIDList()
  {
    File[] topsurvivorarray = playerDir.listFiles();
    List<UUID> topsurvivors = new ArrayList();
    if (topsurvivorarray.length != 0)
    {
      File[] arrayOfFile1;
      int j = (arrayOfFile1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        File playerfile = arrayOfFile1[i];
        UUID player = UUID.fromString(playerfile.getName().substring(0, playerfile.getName().indexOf('.')));
        if ((getServer().getOfflinePlayer(player).hasPlayedBefore()) && (!tshashmap.getTopSurvivorPlayer(player).getFlagExempt())) {
          topsurvivors.add(player);
        }
      }
      Collections.sort(topsurvivors, new TopSurvivorUUIDComparator());
    }
    return topsurvivors;
  }
  
  public List<UUID> getVotifierUUIDList()
  {
    OfflinePlayer[] topsurvivorarray = server.getOfflinePlayers();
    List<OfflinePlayer> topsurvivors = new ArrayList();
    List<UUID> topsurvivorsUUID = new ArrayList();
    if (topsurvivorarray.length != 0)
    {
      OfflinePlayer[] arrayOfOfflinePlayer1;
      int j = (arrayOfOfflinePlayer1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        OfflinePlayer player = arrayOfOfflinePlayer1[i];
        if (player.hasPlayedBefore()) {
          topsurvivors.add(player);
        }
      }
      Collections.sort(topsurvivors, new TopSurvivorVotifierComparator());
      OfflinePlayer p;
      for (Iterator localIterator = topsurvivors.iterator(); localIterator.hasNext(); topsurvivorsUUID.add(p.getUniqueId())) {
        p = (OfflinePlayer)localIterator.next();
      }
    }
    return topsurvivorsUUID;
  }
  
  public List<OfflinePlayer> getVotifierList()
  {
    OfflinePlayer[] topsurvivorarray = server.getOfflinePlayers();
    List<OfflinePlayer> topvoters = new ArrayList();
    if (topsurvivorarray.length != 0)
    {
      OfflinePlayer[] arrayOfOfflinePlayer1;
      int j = (arrayOfOfflinePlayer1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        OfflinePlayer player = arrayOfOfflinePlayer1[i];
        if (player.hasPlayedBefore()) {
          topvoters.add(player);
        }
      }
      Collections.sort(topvoters, new TopSurvivorVotifierComparator());
    }
    return topvoters;
  }
  
  public List<String> getPlayerList()
  {
    File[] topsurvivorarray = playerDir.listFiles();
    List<String> topsurvivors = new ArrayList();
    if (topsurvivorarray.length != 0)
    {
      File[] arrayOfFile1;
      int j = (arrayOfFile1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        File playerfile = arrayOfFile1[i];
        String player = getServer().getOfflinePlayer(UUID.fromString(playerfile.getName().substring(0, playerfile.getName().indexOf('.')))).getName();
        if (player != null) {
          topsurvivors.add(player);
        }
      }
    }
    return topsurvivors;
  }
  
  public List<String> getAfkList()
  {
    File[] topsurvivorarray = playerDir.listFiles();
    List<String> topsurvivors = new ArrayList();
    if (topsurvivorarray.length != 0)
    {
      File[] arrayOfFile1;
      int j = (arrayOfFile1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        File playerfile = arrayOfFile1[i];
        String player = getServer().getOfflinePlayer(UUID.fromString(playerfile.getName().substring(0, playerfile.getName().indexOf('.')))).getName();
        if ((player != null) && (!tshashmap.getTopSurvivorPlayer(player).getFlagExempt())) {
          topsurvivors.add(player);
        }
      }
      Collections.sort(topsurvivors, new TopSurvivorAfkComparator());
    }
    return topsurvivors;
  }
  
  public boolean updateFilesToUUID()
  {
    boolean result = true;
    File[] topsurvivorarray = playerDir.listFiles();
    if (topsurvivorarray.length != 0)
    {
      File[] arrayOfFile1;
      int j = (arrayOfFile1 = topsurvivorarray).length;
      for (int i = 0; i < j; i++)
      {
        File playerfile = arrayOfFile1[i];
        try
        {
          getServer().getOfflinePlayer(UUID.fromString(playerfile.getName().substring(0, playerfile.getName().indexOf('.'))));
        }
        catch (IllegalArgumentException e)
        {
          OfflinePlayer offplayer = getServer().getOfflinePlayer(playerfile.getName().substring(0, playerfile.getName().indexOf('.')));
          if (offplayer.getFirstPlayed() != 0L)
          {
            if (playerfile.renameTo(new File(playerDir, offplayer.getUniqueId().toString() + ".yml")))
            {
              getLogger().info(offplayer.getName() + ".yml has been renamed to " + offplayer.getUniqueId().toString() + ".yml");
            }
            else
            {
              getLogger().info(offplayer.getName() + ".yml could not be renamed for some reason");
              result = false;
            }
          }
          else
          {
            playerfile.delete();
            getLogger().info(offplayer.getName() + ".yml could not be traced to a player and was deleted");
          }
        }
      }
    }
    return result;
  }
  
  public void initPlayer(Player player)
  {
    TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(player);
    tsplayer.setPlayerName(player.getName());
    player.setScoreboard(tsboard);
    if (tsplayer.getFlagNew())
    {
      timesincedeathobjective.getScore(player).setScore(0);
      tsplayer.setFlagNew(false);
      server.getLogger().info("[Top Survivor] " + tsplayer.getPlayerName() + " has been initiated");
    }
    if ((player.hasPermission("topsurvivor.admin")) || (tsplayer.getFlagPermaban())) {
      tsplayer.setFlagExempt(true);
    }
    if (!tsplayer.getFlagExempt()) {
      refreshPlayer(player);
    }
  }
  
  public void refreshPlayer(OfflinePlayer player)
  {
    TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(player);
    tshashmap.onRefresh(player);
    if (!tsplayer.getFlagExempt())
    {
      Score timesincedeath = timesincedeathobjective.getScore(player);
      if (timesincedeath.getScore() - tsplayer.getCurrentAfkTime() > tsplayer.getTopTick() - tsplayer.getTopAfkTime())
      {
        tsplayer.setTopTick(timesincedeath.getScore());
        tsplayer.setTopAfkTime(tsplayer.getCurrentAfkTime());
      }
      int i = (int)Math.floor((tsplayer.getTopTick() - tsplayer.getTopAfkTime() - tsplayer.getCurrentAfkTPenalty()) / 24000);
    }
  }
  
  public boolean checkContest()
  {
    Date conteststart = new Date(getContestStart());
    Date now = new Date();
    if (conteststart.getMonth() != now.getMonth())
    {
      setFlagContest(false);
      server.getLogger().info("[Top Survivor] A new month has been detected");
      resetScoreboard();
      return true;
    }
    return false;
  }
  
  public boolean tempBan(String player, int length)
  {
    if (getPlayerList().contains(player))
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      refreshPlayer(server.getOfflinePlayer(player));
      tsp.reset();
      tsp.setFlagExempt(true);
      tsp.setBanLength(length);
      tsboard.resetScores(player);
      return true;
    }
    return false;
  }
  
  public boolean permaBan(String player)
  {
    if (getPlayerList().contains(player))
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      if (!tsp.getFlagPermaban())
      {
        refreshPlayer(server.getOfflinePlayer(player));
        tsp.reset();
        tsp.setFlagExempt(true);
        tsp.setFlagPermaban(true);
        tsboard.resetScores(player);
        return true;
      }
      return false;
    }
    return false;
  }
  
  public boolean unBan(String player)
  {
    if (getPlayerList().contains(player))
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      if (tsp.getFlagExempt())
      {
        refreshPlayer(server.getOfflinePlayer(player));
        tsp.reset();
        tsp.setFlagExempt(false);
        tsp.setFlagPermaban(false);
        survivortimeobjective.getScore(player).setScore(0);
        return true;
      }
    }
    return false;
  }
  
  public boolean afkTerminatoryPenaltyAdd(String player, int multiplier)
  {
    if (getPlayerList().contains(player))
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      tsp.setCurrentAfkTPenalty(tsp.getCurrentAfkTPenalty() + multiplier * getAFKTerminatorPenalty());
      return true;
    }
    return false;
  }
  
  public boolean afkTerminatoryPenaltyRemove(String player, int multiplier)
  {
    if (getPlayerList().contains(player))
    {
      TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
      if (multiplier * getAFKTerminatorPenalty() > tsp.getCurrentAfkTPenalty()) {
        tsp.setCurrentAfkTPenalty(0);
      } else {
        tsp.setCurrentAfkTPenalty(tsp.getCurrentAfkTPenalty() - multiplier * getAFKTerminatorPenalty());
      }
      return true;
    }
    return false;
  }
  
  public boolean afkTerminatoryPenaltyClear(String player)
  {
    if (getPlayerList().contains(player))
    {
      tshashmap.getTopSurvivorPlayer(player).setCurrentAfkTPenalty(0);
      return true;
    }
    return false;
  }
  
  public void loadConfigFile()
  {
    if (!server.getPluginManager().getPlugin("TopSurvivor").getDataFolder().exists()) {
      server.getPluginManager().getPlugin("TopSurvivor").getDataFolder().mkdir();
    }
    configFile = new File(server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator + "config.yml");
    if (!configFile.exists())
    {
      saveDefaultConfig();
      server.getLogger().info("[Top Survivor] No config file was found so the default file was copied over");
    }
    config = YamlConfiguration.loadConfiguration(configFile);
    //addRegionsToConfig();
  }
  
  public boolean saveConfigFile()
  {
    try
    {
      config.save(configFile);
      return true;
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return false;
  }
  
  /*
  
  public boolean addRegionsToConfig(){
	  for(World world : Bukkit.getWorlds()){
		  String globalf = world.getName()+".Global";
		  if(!config.contains(globalf)){ config.set(globalf, true); }
		  for(String reg : getWorldGuard().getRegionManager(world).getRegions().keySet()){
			  String regionf = world.getName()+"."+reg;
			  if(!config.contains(regionf)){ config.set(regionf, true); }
		  }
	  }
	  return saveConfigFile();
  }
  
  public boolean isInSurvivalRegion(Player player){
	  WorldGuardPlugin wg = this.getWorldGuard();
	  RegionManager manager = wg.getRegionManager(player.getWorld());
	  ApplicableRegionSet set = manager.getApplicableRegions(player.getLocation());
	  if(set.size() == 0){ return config.getBoolean(player.getWorld().getName() + ".Global"); }
	  else{
		  for(ProtectedRegion pf : set){
			  if(!config.getBoolean(player.getWorld().getName()+"."+pf.getId())){ return false; }
		  }
	  }
	  return true;
  }
  
  */
  
  public int getDisplayCount()
  {
    return config.getInt("Display.Count");
  }
  
  public int getAFKTerminatorPenalty()
  {
    return config.getInt("AfkTerminatorPenalty");
  }
  
  public boolean getFlagContest()
  {
    return config.getBoolean("Flag.Contest");
  }
  
  public long getContestStart()
  {
    return config.getLong("Contest.Start");
  }
  
  public long getContestLength()
  {
    return TimeConverter.daysToMilli(config.getInt("Contest.Length"));
  }
  
  public long getUpdateTime()
  {
    return config.getLong("RefreshTime");
  }
  
  public long getAFKTPollTime()
  {
    return config.getLong("AfkTerminatorPoll");
  }
  
  public long getDisplayTime()
  {
	  return config.getLong("Display.Time");
  }
  
  public boolean getAFKTerminator()
  {
    return config.getBoolean("AfkTerminator");
  }
  
  public boolean setAFKTerminatorPenalty(int penalty)
  {
    config.set("AfkTerminatorPenalty", Integer.valueOf(penalty));
    return saveConfigFile();
  }
  
  public boolean setFlagContest(boolean i)
  {
    config.set("Flag.Contest", Boolean.valueOf(i));
    return saveConfigFile();
  }
  
  public boolean setContestStart(long i)
  {
    config.set("Contest.Start", Long.valueOf(i));
    return saveConfigFile();
  }
  
  public boolean setContestLength(long i)
  {
    config.set("Contest.Length", Long.valueOf(i));
    return saveConfigFile();
  }
  
  /*
  private WorldGuardPlugin getWorldGuard() {
	    Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (wg == null || !(wg instanceof WorldGuardPlugin)) {
	        return null; // Maybe you want throw an exception instead
	    }
	 
	    return (WorldGuardPlugin) wg;
	}
	*/
}
