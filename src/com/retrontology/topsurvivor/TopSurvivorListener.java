package com.retrontology.topsurvivor;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import net.ess3.api.IUser;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

public class TopSurvivorListener
  implements Listener
{
  public TopSurvivor plugin;
  
  public TopSurvivorListener(TopSurvivor plugin)
  {
    this.plugin = plugin;
  }
  
  @EventHandler
  public void onLogin(PlayerLoginEvent event)
  {
    final Player player = event.getPlayer();
    
    BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
    scheduler.scheduleSyncDelayedTask(this.plugin, new Runnable()
    {
      public void run()
      {
        if (player.isOnline()) {
          TopSurvivorListener.this.plugin.initPlayer(player);
        }
      }
    }, 10L);
  }
  
  @EventHandler
  public void onQuit(PlayerQuitEvent event)
  {
    Player player = event.getPlayer();
    
    TopSurvivor.tshashmap.onLeave(player);
    
    this.plugin.refreshPlayer(player);
  }
  
  @EventHandler
  public void onDeath(PlayerDeathEvent event)
  {
	  Player player = event.getEntity();
	  TopSurvivorPlayer tsplayer = TopSurvivor.tshashmap.getTopSurvivorPlayer(player);
	  //if(this.plugin.isInSurvivalRegion(player)){
	    tsplayer.setLastDeath(new Date().getTime());
	    
	    TopSurvivor.tshashmap.onDeath(player);
	    
	    this.plugin.refreshPlayer(player);
	    
	    tsplayer.setTotalAfkTime(tsplayer.getTotalAfkTime() + tsplayer.getCurrentAfkTime());
	    
	    tsplayer.setCurrentAfkTime(0);
	  //}else{
	//	  tsplayer.setLastSurvivalTick(this.plugin.timesincedeathobjective.getScore(player).getScore());
	  //}
  }
  
  @EventHandler
  public void onRespawn(PlayerRespawnEvent event)
  {
	  Player player = event.getPlayer();
	  TopSurvivorPlayer tsplayer = TopSurvivor.tshashmap.getTopSurvivorPlayer(player);
	  if(tsplayer.getLastSurvivalTick() != 0){
		  this.plugin.deathsobjective.getScore(player).setScore(this.plugin.deathsobjective.getScore(player).getScore() - 1);
		  this.plugin.timesincedeathobjective.getScore(player).setScore((int) tsplayer.getLastSurvivalTick());
		  tsplayer.setLastSurvivalTick(0);
	  }
  }
  
  @EventHandler
  public void onAFKChange(AfkStatusChangeEvent event)
  {
    if (TopSurvivor.server.getPlayer(event.getAffected().getName()).isOnline()) {
      TopSurvivor.tshashmap.onChangeEssentialsAfk(event);
    }
  }
  
  @EventHandler
  public void updateTSTime(TopSurvivorUpdate event)
  {
    Player p;
    for (Iterator localIterator = TopSurvivor.server.getOnlinePlayers().iterator(); localIterator.hasNext(); this.plugin.refreshPlayer(p)) {
      p = (Player)localIterator.next();
    }
    List<String> list = this.plugin.getSortedList();
    TopSurvivor.survivortimeobjective.unregister();
    this.plugin.makeScoreboard();
    for (int i = 0; (i < this.plugin.getDisplayCount()) && (i < list.size()); i++) {
      TopSurvivor.survivortimeobjective.getScore((String)list.get(i)).setScore(TopSurvivor.tshashmap.getTopSurvivorPlayer((String)list.get(i)).getSurvivorTime());
    }
    TopSurvivor.server.getLogger().info("[Top Survivor] Top Survivors list updated");
    if (this.plugin.getFlagContest()) {
      this.plugin.checkContest();
    }
  }
  
  @EventHandler
  public void updateAFKTTime(TopSurvivorAFKTUpdate event)
  {
    for (Player p : TopSurvivor.server.getOnlinePlayers()) {
      TopSurvivor.tshashmap.onAFKTerminator(p);
    }
  }
  
  @EventHandler
  public void updateDisplay(TopSurvivorDisplay event)
  {
	  List<String> list = this.plugin.getSortedList();
	  if(this.plugin.config.getBoolean("Display.Chat")){
	    	plugin.getServer().broadcastMessage(ChatColor.YELLOW + "---- Top Survivors ----");
	    	for (int i = 0; (i < this.plugin.getDisplayCount()) && (i < list.size()); i++) {
	    		plugin.getServer().broadcastMessage(ChatColor.YELLOW + "" + (i+1) + ". " + ChatColor.WHITE + (String)list.get(i) + ": " + TopSurvivor.tshashmap.getTopSurvivorPlayer((String)list.get(i)).getSurvivorTime());
	    	}
	    }
  }
  @EventHandler
  public void onModeChange(PlayerGameModeChangeEvent event)
  {
	  TopSurvivor.tshashmap.onGameModeChange(event.getPlayer(), event.getNewGameMode());
  }
}
