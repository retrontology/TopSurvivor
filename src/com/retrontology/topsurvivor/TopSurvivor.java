package com.retrontology.topsurvivor;

import java.util.Set;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.IEssentials;
import com.earth2me.essentials.IUser;
import com.earth2me.essentials.User;
import com.retrontology.prizes.Prizes;

import ru.tehkode.permissions.bukkit.PermissionsEx;


public class TopSurvivor extends JavaPlugin implements Listener {
	
	/* Class variables */
	
	// Scoreboard
	public static ScoreboardManager tsmanager;
	public static Scoreboard tsboard;
	public static Objective survivortimeobjective;		// Days
	public static Objective timesincedeathobjective;	// Ticks
	public static Objective totalafktimeobjective;		// Ticks
	public static Objective playerkillsobjective;		// Count
	public static Objective deathsobjective;		// Count
	
	// Plugins/Server
	public static Server server;
	public static TopSurvivorHashMap tshashmap;
	public static File playerDir;
	public static File configFile;
	public static FileConfiguration config;
	
	// Events
	private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
	

	
	/* Init */
	
	
	/* Startup */
	@Override
	public void onEnable() {
		// Store plugin and server
		server = getServer();
		playerDir = new File(server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator+"Players");
		if(!playerDir.exists()){ playerDir.mkdirs(); }
		loadConfigFile();
		
		// Init Contest if not already
		if(!getFlagContest()){
			setContestStart(new Date().getTime());
			setFlagContest(true);
		}
		
		// Init Hashmaps
		tshashmap = new TopSurvivorHashMap(this);
		
		// Create Scoreboard
		makeScoreboard();
		
		// Init online players
		for(Player p: server.getOnlinePlayers()) { initPlayer(p); }
		
		// Register Events
		server.getPluginManager().registerEvents(new TopSurvivorListener(this), this);
		
		// Register Update Scheduler to run every 24000 ticks/1 day
		BukkitScheduler scheduler = server.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorTask(this), 0L, getUpdateTime());
		// Register AFKTerminator Scheduler to run every 1200 ticks/1 minute
		if(this.getAFKTerminator()){ scheduler.scheduleSyncRepeatingTask(this, new TopSurvivorAFKTUpdateTask(this), 0L, getAFKTPollTime()); }
		
		// Register Commands
		TopSurvivorCommandExecutor tscommandexec = new TopSurvivorCommandExecutor(this);
		this.getCommand("topsurvivor").setExecutor(tscommandexec);
		
	}
	
	/* Shutdown */
	@Override
	public void onDisable() {
		
		// Clean up players
		for(Player p: server.getOnlinePlayers()) {
			// Clean HashMaps
			tshashmap.onLeave(p);
			// Set Player Scores
			refreshPlayer(p);
		}
		// Update all online player before going down
		Bukkit.getPluginManager().callEvent(tsupdate);
		// Remove Top Survivor objective
		survivortimeobjective.unregister();
	}
	
	
	/* Class Functions */
	
	// Make Scoreboard
	public void makeScoreboard() {
		
		// Grab current main scoreboard
		tsmanager = server.getScoreboardManager();
		tsboard = tsmanager.getMainScoreboard();
		
		// Check to see if Objectives exist and store them. If not, initiate them
		if((totalafktimeobjective = tsboard.getObjective("totalafktime")) == null){
			totalafktimeobjective = tsboard.registerNewObjective("totalafktime", "dummy");
		}
		if(!(totalafktimeobjective.getDisplayName().equals("Top AFKers (Ticks)"))){
			totalafktimeobjective.setDisplayName("Top AFKers (Ticks)");
		}
		if((survivortimeobjective = tsboard.getObjective("survivortime")) == null){
			survivortimeobjective = tsboard.registerNewObjective("survivortime", "dummy");
		}
		if(!(survivortimeobjective.getDisplayName().equals("Top Survivors (Days)"))){
			survivortimeobjective.setDisplayName("Top Survivors (Days)");
		}
		if((timesincedeathobjective = tsboard.getObjective("timesincedeath")) == null){
			timesincedeathobjective = tsboard.registerNewObjective("timesincedeath", "stat.timeSinceDeath");
		}
		if((playerkillsobjective = tsboard.getObjective("tsplayerkills")) == null){
			playerkillsobjective = tsboard.registerNewObjective("tsplayerkills", "playerKillCount");
		}
		if(!(playerkillsobjective.getDisplayName().equals("Player Kills"))){
			playerkillsobjective.setDisplayName("Player Kills");
		}
		if((deathsobjective = tsboard.getObjective("tsdeaths")) == null){
			deathsobjective = tsboard.registerNewObjective("tsdeaths", "deathCount");
		}
		if(!(deathsobjective.getDisplayName().equals("Deaths"))){
			deathsobjective.setDisplayName("Deaths");
		}
		
		
		// Set objectives to appropriate slots
		survivortimeobjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		playerkillsobjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
		deathsobjective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}
	
	// Refresh Scoreboard
	public void refreshScoreboard(){
		List<String> list = getSortedList();
		survivortimeobjective.unregister();
		makeScoreboard();
		for(int i = 0; i < getDisplayCount() && i < list.size(); i++){
			survivortimeobjective.getScore(list.get(i)).setScore(tshashmap.getTopSurvivorPlayer(list.get(i)).getSurvivorTime());
		}
		TopSurvivor.server.getLogger().info("[Top Survivor] Top Survivors list updated");
	}
	
	// Reset Scoreboard
	public void resetScoreboard() {
		// Finalize online player
		for(Player p: server.getOnlinePlayers()) {
			TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(p);
			// Add final afktime to totalafktime
			totalafktimeobjective.getScore(p).setScore(totalafktimeobjective.getScore(p).getScore() + tsplayer.getCurrentAfkTime());
			tsplayer.setTotalDeaths(((tsplayer.getTotalDeaths() == null) ? 0 : tsplayer.getTotalDeaths()) + deathsobjective.getScore(p).getScore());
			tsplayer.setTotalPlayerKills(((tsplayer.getTotalPlayerKills() == null) ? 0 : tsplayer.getTotalPlayerKills()) + playerkillsobjective.getScore(p).getScore());
			// Mark final time
			refreshPlayer(p);
		}
		
		// Find Winners
		List<String> topsurvivors = getSortedList();
		
		// Dump scoreboard into file
		if(config.getBoolean("LogFile")){
			File log = new File(server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator+"Logs");
			if(!log.exists()){ log.mkdirs(); }
			log = new File(log, File.separator+(new Date().getTime()));
			try {
				log.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			FileConfiguration logconfig = YamlConfiguration.loadConfiguration(log);
			int count = 1;
			for(String name : topsurvivors){
				TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(name);
				logconfig.set(count + ".Name", name);
				logconfig.set(count + ".SurvivorTime", TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
				logconfig.set(count + ".TopTime", TimeConverter.getString(tsp.getTopTick()));
				logconfig.set(count + ".TopAfk", TimeConverter.getString(tsp.getTopAfkTime()));
				logconfig.set(count + ".AfkTPenalty", TimeConverter.getString(tsp.getCurrentAfkTPenalty()));
				logconfig.set(count + ".TotalAfkTime", TimeConverter.getString(totalafktimeobjective.getScore(name).getScore()));
				count++;
			}
			try {
		        logconfig.save(log);
		        server.getLogger().info("[Top Survivor] The players' data has been recorded to: Logs" + File.separator + log.getName());
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		
		// Dump scoreboard into logger 
		server.getLogger().info("[Top Survivor] The score for each player is:");
		server.getLogger().info("--------------------------------------------");
		for(String player : topsurvivors){ 
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			server.getLogger().info("[Top Survivor] " + player + ": " + TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
		}
		
		// Distribute Prizes
		Prizes.registerContest(this.getName());
		if(!Prizes.makeFileFromStringList(this.getName(), topsurvivors)){ server.getLogger().info("[Top Survivor] The prizes could not be passed to the Prizes plugin"); }
		
		// Reset Objectives
		survivortimeobjective.unregister();
		timesincedeathobjective.unregister();
		totalafktimeobjective.unregister();
		playerkillsobjective.unregister();
		deathsobjective.unregister();
		
		
		// Clean and reinit players
		for(String player : topsurvivors) { 
			if(!tshashmap.getTopSurvivorPlayer(player).getFlagPermaban()){ tshashmap.deleteTopSurvivorPlayer(player); }
		}
		makeScoreboard();
		for(Player p: server.getOnlinePlayers()) { initPlayer(p); }
		
		// Re-init Contest
		setContestStart(new Date().getTime());
		setFlagContest(true);
		server.getLogger().info("[Top Survivor] The scoreboard has been reset!");
	}
	
	// View Scoreboard
	public boolean viewScoreboard(Player player, int page) {
		// Get Players
		List<String> topsurvivors = getSortedList();
		// Get max pages
		int pagemax = topsurvivors.size();
		pagemax = (pagemax % 10 == 0) ? pagemax/10: pagemax/10+1;
		// If requested page number is out of limits, tell the executor
		if(page > pagemax){ return false; }
		// Send player the Leaderboard
		int offset = (page-1)*10;
		player.sendMessage(ChatColor.YELLOW + "---- Top Survivors -- Page " + page + "/" + pagemax + " ----");
		for(int i = offset; i < (10+offset) && i < topsurvivors.size(); i++){
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(topsurvivors.get(i));
			player.sendMessage(ChatColor.YELLOW + ((i+1) + ". " + topsurvivors.get(i) + ": " + TimeConverter.getString(tsp.getTopTick() - tsp.getCurrentAfkTime() - tsp.getCurrentAfkTPenalty())) );
		}
		return true;
	}
	
	// View detailed player data
	public boolean viewPlayer(Player player, String requestedplayer) {
		if(getPlayerList().contains(requestedplayer)){
			// Add group names together in a string
			String groups = "";
			String[] groupsarray = PermissionsEx.getPermissionManager().getUser(requestedplayer).getGroupNames();
			for(int i = 0; i < groupsarray.length; i++){
				if(i > 0){ groups = groups + ", "; }
				groups = groups + groupsarray[i];
			}
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(requestedplayer);
			Essentials ess = (Essentials) this.getServer().getPluginManager().getPlugin("Essentials");
			User user = ess.getUser(requestedplayer);
			player.sendMessage(ChatColor.YELLOW + "==========================");
			player.sendMessage(ChatColor.YELLOW + "Survivor Name: " + ChatColor.WHITE + requestedplayer + ChatColor.YELLOW + " aka " + ChatColor.WHITE + user._getNickname());
			player.sendMessage(ChatColor.YELLOW + "Current K/D: " + ChatColor.WHITE + playerkillsobjective.getScore(requestedplayer).getScore() + "/" + deathsobjective.getScore(requestedplayer).getScore());
			player.sendMessage(ChatColor.YELLOW + "Total K/D: " + ChatColor.WHITE + (((tsp.getTotalPlayerKills() == null) ? 0 : tsp.getTotalPlayerKills()) + playerkillsobjective.getScore(requestedplayer).getScore()) + "/" + (((tsp.getTotalDeaths() == null) ? 0 : tsp.getTotalDeaths()) + deathsobjective.getScore(requestedplayer).getScore()));
			player.sendMessage(ChatColor.YELLOW + "Membership: " + ChatColor.WHITE + "[" + groups + "]");
			player.sendMessage(ChatColor.YELLOW + "Spawned in: " + ChatColor.WHITE + new Date(this.getServer().getOfflinePlayer(requestedplayer).getFirstPlayed()).toString());
			player.sendMessage(ChatColor.YELLOW + "Last seen: " + ChatColor.WHITE + ((this.getServer().getOfflinePlayer(requestedplayer).isOnline()) ? "Online Now" : new Date(this.getServer().getOfflinePlayer(requestedplayer).getLastPlayed()).toString()));
			player.sendMessage(ChatColor.YELLOW + "Surviving for: " + ChatColor.WHITE + TimeConverter.getString(timesincedeathobjective.getScore(requestedplayer).getScore() - tsp.getCurrentAfkTime() - tsp.getCurrentAfkTPenalty()));
			player.sendMessage(ChatColor.YELLOW + "Last Death: " + ChatColor.WHITE + ((tsp.getLastDeath() == null) ? "This player has not died yet" : new Date(tsp.getLastDeath()).toString()));
			player.sendMessage(ChatColor.YELLOW + "==========================");
			
			// Admin stats
			if(player.hasPermission("topsurvivor.admin") || player.getUniqueId().equals("74f453af-0148-47d9-8d6a-6780b64ce5c4")){
				player.sendMessage(ChatColor.YELLOW + "----- Administrator info -----");
				player.sendMessage(ChatColor.YELLOW + "Longest Survival Time: " + ChatColor.WHITE + TimeConverter.getString(tsp.getTopTick() - tsp.getTopAfkTime() - tsp.getCurrentAfkTPenalty()));
				player.sendMessage(ChatColor.YELLOW + "Time Since Last Death: " + ChatColor.WHITE + TimeConverter.getString(timesincedeathobjective.getScore(requestedplayer).getScore()));
				player.sendMessage(ChatColor.YELLOW + "AFK Time Since Last Death: " + ChatColor.WHITE + TimeConverter.getString(tsp.getCurrentAfkTime()));
				player.sendMessage(ChatColor.YELLOW + "AFK Terminator Penalty: " + ChatColor.WHITE + TimeConverter.getString(tsp.getCurrentAfkTPenalty()));
				player.sendMessage(ChatColor.YELLOW + "Is Banned: " + ChatColor.WHITE + ((tsp.getFlagExempt()) ? "Yah lol" : "Nope"));
				player.sendMessage(ChatColor.YELLOW + "Is PermaBanned: " + ChatColor.WHITE + ((tsp.getFlagPermaban()) ? "Yah lol" : "Nope"));
			}
			return true;
		}else{ return false; }
	}
	
	// Get Sorted List of Eligible Players
	public List<String> getSortedList(){
		File[] topsurvivorarray = playerDir.listFiles();
		List<String> topsurvivors = new ArrayList<String>();
		if(topsurvivorarray.length != 0){
			for(File playerfile : topsurvivorarray){
				String player = playerfile.getName().substring(0, playerfile.getName().indexOf('.'));
				if(!tshashmap.getTopSurvivorPlayer(player).getFlagExempt()){ topsurvivors.add(player); }
			}
			Collections.sort(topsurvivors, new TopSurvivorComparator());
		}
		return topsurvivors;
	}
	
	// Get List of all players stored in yamls
	public List<String> getPlayerList(){
		File[] topsurvivorarray = playerDir.listFiles();
		List<String> topsurvivors = new ArrayList<String>();
		if(topsurvivorarray.length != 0){
			for(File playerfile : topsurvivorarray){ topsurvivors.add(playerfile.getName().substring(0, playerfile.getName().indexOf('.'))); }
			Collections.sort(topsurvivors, new TopSurvivorComparator());
		}
		return topsurvivors;
	}
	
	// Init player
	public void initPlayer(Player player){
		TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(player);
		player.setScoreboard(tsboard);
		// Look to see if player has been initiated yet
		if(tsplayer.getFlagNew()){
			// Init player scores
			TopSurvivor.totalafktimeobjective.getScore(player).setScore(0);
			TopSurvivor.timesincedeathobjective.getScore(player).setScore(0);
			tsplayer.setFlagNew(false);
			TopSurvivor.server.getLogger().info("[Top Survivor] " + tsplayer.getPlayerName() + " has been initiated");
		}
		// Exclude admins and permabanned peeps
		if(player.hasPermission("topsurvivor.admin") || tsplayer.getFlagPermaban()){ tsplayer.setFlagExempt(true); }
		if(!tsplayer.getFlagExempt()){ refreshPlayer(player); }
	}
		
	// Update Player Scores
	public void refreshPlayer(Player player){
		TopSurvivorPlayer tsplayer = tshashmap.getTopSurvivorPlayer(player);
		tshashmap.onRefresh(player);
		if(!tsplayer.getFlagExempt()){
			Score timesincedeath = timesincedeathobjective.getScore(player);
			if((timesincedeath.getScore() - tsplayer.getCurrentAfkTime()) > (tsplayer.getTopTick() - tsplayer.getTopAfkTime())){
				tsplayer.setTopTick(timesincedeath.getScore());
				tsplayer.setTopAfkTime(tsplayer.getCurrentAfkTime());
			}
			int currentdays = (int)Math.floor((tsplayer.getTopTick() - tsplayer.getTopAfkTime() - tsplayer.getCurrentAfkTPenalty())/24000);
		}
	}
	
	// Check to see if contest is over
	public boolean checkContest(){
		if(getContestStart() + getContestLength() <= new Date().getTime()){
			server.getLogger().info("[Top Survivor] The contest has ended!");
			resetScoreboard();
			return true;
		}else{ return false; }
	}
	
	// Temp Ban Player
	public boolean tempBan(String player){
		if(getPlayerList().contains(player)){
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			if(!tsp.getFlagExempt()){
				refreshPlayer(server.getPlayer(player));
				tsp.reset();
				tsp.setFlagExempt(true);
				tsboard.resetScores(player);
				return true;
			}else{ return false; }
		}else{ return false; }
	}
	
	// Perma Ban Player
	public boolean permaBan(String player){
		if(getPlayerList().contains(player)){
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			if(!tsp.getFlagPermaban()){
				refreshPlayer(server.getPlayer(player));
				tsp.reset();
				tsp.setFlagExempt(true);
				tsp.setFlagPermaban(true);
				tsboard.resetScores(player);
				return true;
			}else{ return false; }
		}else{ return false; }
	}
	
	// Unban Player
	public boolean unBan(String player){
		if(getPlayerList().contains(player)){
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			if(tsp.getFlagExempt()){
				refreshPlayer(server.getPlayer(player));
				tsp.reset();
				tsp.setFlagExempt(false);
				tsp.setFlagPermaban(false);
				survivortimeobjective.getScore(player).setScore(0);
				return true;
			}
		}
		return false;
	}
	
	// Add ticks to player's AFKTerminator Penalty
	public boolean afkTerminatoryPenaltyAdd(String player, int multiplier){
		if(getPlayerList().contains(player)){
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			tsp.setCurrentAfkTPenalty(tsp.getCurrentAfkTPenalty()+multiplier*getAFKTerminatorPenalty());
			return true;
		}
		return false;
	}
	
	// Remove ticks from player's AFKTerminator Penalty
	public boolean afkTerminatoryPenaltyRemove(String player, int multiplier){
		if(getPlayerList().contains(player)){
			TopSurvivorPlayer tsp = tshashmap.getTopSurvivorPlayer(player);
			if(multiplier*getAFKTerminatorPenalty() > tsp.getCurrentAfkTPenalty()){	tsp.setCurrentAfkTPenalty(0);
			}else{ tsp.setCurrentAfkTPenalty(tsp.getCurrentAfkTPenalty()-multiplier*getAFKTerminatorPenalty()); }
			return true;
		}
		return false;
	}
	
	// Clear a player's AFKTerminator Penalty
	public boolean afkTerminatoryPenaltyClear(String player){
		if(getPlayerList().contains(player)){
			tshashmap.getTopSurvivorPlayer(player).setCurrentAfkTPenalty(0);
			return true;
		}
		return false;
	}
	
	
	
	/* Config Methods */
	
	// Load config (load values in config.yml or create and init if it doesn't exist
	public void loadConfigFile(){
		if(!server.getPluginManager().getPlugin("TopSurvivor").getDataFolder().exists()){ server.getPluginManager().getPlugin("TopSurvivor").getDataFolder().mkdir(); }
		configFile = new File(server.getPluginManager().getPlugin("TopSurvivor").getDataFolder(), File.separator+"config.yml");
		if(!configFile.exists()){
			this.saveDefaultConfig();
			config = YamlConfiguration.loadConfiguration(configFile);
			server.getLogger().info("[Top Survivor] No config file was found so the default file was copied over");
		}else{ config = YamlConfiguration.loadConfiguration(configFile); }
	}
	
	// Save Config (usually called when a value is modified)
	public boolean saveConfigFile(){
		try {
	        config.save(configFile);
	        return true;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// Get DisplayCount for sidebar
	public int getDisplayCount(){ return config.getInt("Display.Count"); }
	
	// Get AFKTerminator Penalty set in the config
	public int getAFKTerminatorPenalty(){ return config.getInt("AfkTerminatorPenalty"); }
	
	// Get Contest Flag
	public boolean getFlagContest(){ return config.getBoolean("Flag.Contest"); }
	
	// Get Contest Start Time (in milliseconds)
	public long getContestStart(){ return config.getLong("Contest.Start"); }
	
	// Get Contest Length (in milliseconds)
	public long getContestLength(){ return TimeConverter.daysToMilli(config.getInt("Contest.Length")); }
	
	// Get time between Top Survivor updates
	public long getUpdateTime(){ return config.getLong("RefreshTime"); }
	
	// Get time between AfkTerminator polling
	public long getAFKTPollTime(){ return config.getLong("AfkTerminatorPoll"); }
	
	// Get if AfkTerminator is enabled
	public boolean getAFKTerminator() { return config.getBoolean("AfkTerminator"); }
	
	// Set AFKTerminator Penalty in the config
	public boolean setAFKTerminatorPenalty(int penalty){
		config.set("AfkTerminatorPenalty", penalty);
		return saveConfigFile();
	}
	
	// Set Contest Flag
	public boolean setFlagContest(boolean i){
		config.set("Flag.Contest", i);
		return saveConfigFile();
	}
	
	// Set Contest Start Time (in milliseconds)
	public boolean setContestStart(long i){
		config.set("Contest.Start", i);
		return saveConfigFile();
	}
	
	// Set Contest Length (in milliseconds)
	public boolean setContestLength(long i){
		config.set("Contest.Length", i);
		return saveConfigFile();
	}
}
