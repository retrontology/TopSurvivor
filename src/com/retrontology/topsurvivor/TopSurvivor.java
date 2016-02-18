package com.retrontology.topsurvivor;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.User;

import me.edge209.afkTerminator.AfkTerminatorAPI;
import net.ess3.api.events.AfkStatusChangeEvent;


public class TopSurvivor extends JavaPlugin implements Listener {
	
	/* Class variables */
	
	private ScoreboardManager tsmanager;
	private Scoreboard tsboard;
	private IEssentials ess;
	
	
	/* Init */
	
	
	/* Startup */
	
	
	@Override
	public void onEnable() {
		
		// Create Scoreboard
		makeScoreboard();
		
		// Init online players
		for(Player p: getServer().getOnlinePlayers()) {
			
		}
		
		// Register Events
		getServer().getPluginManager().registerEvents(this, this);
		
		// Register Commands
		TopSurvivorCommandExecutor tscommandexec = new TopSurvivorCommandExecutor(this);
		this.getCommand("topsurvivor reset").setExecutor(tscommandexec);
		this.getCommand("topsurvivor view").setExecutor(tscommandexec);
		
	}
	
	/* Shutdown */
	
	@Override
	public void onDisable() {
		
	}
	
	
	/* Class Functions */
	
	
	
	/* Scoreboard */
	
	// Make Scoreboard
	public void makeScoreboard() {
		tsmanager = Bukkit.getScoreboardManager();
		tsboard = tsmanager.getMainScoreboard();
	}
	
	
	/* Events */
	
	// Player Login
	@EventHandler
	public void onLogin(PlayerLoginEvent event) {
	    
	}
	
	
	// Player AFK Status Change
	@EventHandler
    public void onAFKChange(AfkStatusChangeEvent event) {
		final User user = ess.getUser(((Player) event).getEntityId());
		if (user.isAfk()){
			
		}
		else {
			
		}
		
    }
	
}
