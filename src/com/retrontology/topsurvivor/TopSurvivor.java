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
	private final IEssentials ess;
	
	
	/* Init */
	
	public TopSurvivor(IEssentials ess)
	{
		this.ess = ess;
	}
	
	
	/* Startup */
	
	@Override
	public void onEnable() {
		makeScoreboard();
		for(Player p: getServer().getOnlinePlayers()) {
			
		}
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	
	/* Shutdown */
	
	@Override
	public void onDisable() {
		
	}
	
	
	/* Class Functions */
	
	
	
	
	/* User commands */
	
	// Template
	@Override
    public boolean onCommand(CommandSender sender,
            Command command,
            String label,
            String[] args) {
        if (command.getName().equalsIgnoreCase("mycommand")) {
            sender.sendMessage("You ran /mycommand!");
            return true;
        }
        return false;
    }
	
	// Reset
	
	
	
	// View
	
	
	
	
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
    public void onLogin(AfkStatusChangeEvent event) {
		final User user = ess.getUser((Player)event.getEntity());
		if (user.isAfk()){
			
		}
		else {
			
		}
		
    }
	
}
