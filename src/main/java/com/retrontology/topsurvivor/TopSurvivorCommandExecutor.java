package com.retrontology.topsurvivor;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class TopSurvivorCommandExecutor
  implements CommandExecutor
{
  private TopSurvivor plugin;
  private TopSurvivorUpdate tsupdate = new TopSurvivorUpdate();
  
  public TopSurvivorCommandExecutor(TopSurvivor plugin)
  {
    this.plugin = plugin;
  }
  
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
  {
    Player player = (Player)sender;
    if (cmd.getName().equalsIgnoreCase("topsurvivor"))
    {
      if (args.length == 0)
      {
        if (player.hasPermission("topsurvivor.citizen")) {
          this.plugin.viewScoreboard(player, 1);
        }
        return true;
      }
      else if (args.length == 1)
      {
        if (args[0].equalsIgnoreCase("reset"))
        {
          if (player.hasPermission("topsurvivor.admin"))
          {
            this.plugin.resetScoreboard();
            player.sendMessage(ChatColor.GREEN + "The Scoreboard has been reset!");
            return true;
          }
          player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
          return true;
        }
        if (args[0].equalsIgnoreCase("update"))
        {
          if (player.hasPermission("topsurvivor.admin"))
          {
            Bukkit.getPluginManager().callEvent(this.tsupdate);
            player.sendMessage(ChatColor.GREEN + "The Scoreboard has been updated!");
            return true;
          }
          player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
          return true;
        }
        if ((args[0].equalsIgnoreCase("view")) && ((player.hasPermission("topsurvivor.citizen")) || (player.getUniqueId().equals(this.plugin.mebb))))
        {
          this.plugin.viewScoreboard(player, 1);
          return true;
        }
        /*
        if ((args[0].equalsIgnoreCase("viewvoters")) && ((player.hasPermission("topsurvivor.citizen")) || (player.getUniqueId().equals(this.plugin.mebb))))
        {
          this.plugin.viewVoteScoreboard(player, 1);
          return true;
        }
        if (args[0].equalsIgnoreCase("resetvotes"))
        {
          if (player.hasPermission("topsurvivor.admin"))
          {
            this.plugin.resetVotifier();
            player.sendMessage(ChatColor.GREEN + "Votifier has been reset!");
            return true;
          }
          player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
          return true;
        }
        */
        if (args[0].equalsIgnoreCase("archives"))
        {
          if ((player.hasPermission("topsurvivor.admin")) || (player.hasPermission("topsurvivor.historian")))
          {
            TopSurvivor.tsarchives.listArchives(player, 1);
            return true;
          }
          player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
          return true;
        }
        if (args[0].equalsIgnoreCase("snoogans"))
        {
          if (!player.isDead())
          {
            player.sendMessage(ChatColor.AQUA + "Snootchie Bootchies :P");
            this.plugin.getServer().broadcastMessage(ChatColor.DARK_AQUA + player.getDisplayName() + " could not cope with the complexities of modern life and chose to end it all");
            player.damage(1000.0D);
            return true;
          }
          player.sendMessage(ChatColor.RED + "You're already dead ya silly goose. How did you even type this?");
          return true;
        }
        if (args[0].equalsIgnoreCase("reload"))
        {
          if (player.hasPermission("topsurvivor.admin"))
          {
            this.plugin.loadConfigFile();
            player.sendMessage(ChatColor.GREEN + "The config has been reloaded!");
            return true;
          }
          player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
          return true;
        }
      }
      /*
      if ((args.length == 2) && (args[0].equalsIgnoreCase("viewvoters")) && ((player.hasPermission("topsurvivor.citizen")) || (player.getUniqueId().equals(this.plugin.mebb))))
      {
        boolean page = true;
        int pagenumber = 0;
        for (int i = 0; i < args[1].length(); i++)
        {
          if (Character.getNumericValue(args[1].charAt(i)) > 9)
          {
            page = false;
            break;
          }
          pagenumber *= 10;
          
          pagenumber += Character.getNumericValue(args[1].charAt(i));
        }
        if (page)
        {
          if (this.plugin.viewVoteScoreboard(player, pagenumber)) {
            return true;
          }
          player.sendMessage(ChatColor.RED + "You did not enter a valid page");
          return true;
        }
        player.sendMessage(ChatColor.RED + "You did not enter a valid page");
        return true;
      }
      */
      if ((args.length == 2) && (args[0].equalsIgnoreCase("view")) && ((player.hasPermission("topsurvivor.citizen")) || (player.getUniqueId().equals(this.plugin.mebb))))
      {
        boolean page = true;
        int pagenumber = 0;
        for (int i = 0; i < args[1].length(); i++)
        {
          if (Character.getNumericValue(args[1].charAt(i)) > 9)
          {
            page = false;
            break;
          }
          pagenumber *= 10;
          
          pagenumber += Character.getNumericValue(args[1].charAt(i));
        }
        if (page)
        {
          if (this.plugin.viewScoreboard(player, pagenumber)) {
            return true;
          }
          player.sendMessage(ChatColor.RED + "You did not enter a valid page");
          return true;
        }
        if ((player.hasPermission("topsurvivor.admin")) || (args[1].equalsIgnoreCase(player.getName())) || (player.hasPermission("topsurvivor.historian")))
        {
          if (this.plugin.viewPlayer(player, args[1])) {
            return true;
          }
          player.sendMessage(ChatColor.RED + "You did not enter a valid player");
          return true;
        }
        player.sendMessage(ChatColor.RED + "You did not enter a valid page/player");
        return true;
      }
      if (args[0].equalsIgnoreCase("tempban"))
      {
        if (player.hasPermission("topsurvivor.admin"))
        {
          if (args.length == 2)
          {
            if (this.plugin.tempBan(args[1], 1))
            {
              player.sendMessage(ChatColor.GREEN + args[1] + " has been banned from the Top Survivor Leaderboard until it is reset");
              TopSurvivor.server.getLogger().info("[Top Survivor] " + args[1] + " has been banned for the rest of the cycle");
              return true;
            }
            player.sendMessage(ChatColor.RED + "You did not enter a valid player");
            return true;
          }
          if (args.length == 3)
          {
            boolean page = true;
            int pagenumber = 0;
            for (int i = 0; i < args[1].length(); i++)
            {
              if (Character.getNumericValue(args[1].charAt(i)) > 9)
              {
                page = false;
                break;
              }
              pagenumber *= 10;
              
              pagenumber += Character.getNumericValue(args[1].charAt(i));
            }
            if (page)
            {
              if (this.plugin.tempBan(args[1], pagenumber))
              {
                player.sendMessage(ChatColor.GREEN + args[1] + " has been banned from the Top Survivor Leaderboard for " + pagenumber + " cycles");
                TopSurvivor.server.getLogger().info("[Top Survivor] " + args[1] + " has been banned for " + pagenumber + " cycles");
                return true;
              }
              player.sendMessage(ChatColor.RED + "You did not enter a valid player");
              return true;
            }
            player.sendMessage(ChatColor.RED + "You did not enter a valid integer");
            return true;
          }
          player.sendMessage(ChatColor.YELLOW + "Usage: ");
          player.sendMessage(ChatColor.YELLOW + "/topsurvivor tempban <player>");
          return true;
        }
        player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
        return true;
      }
      if (args[0].equalsIgnoreCase("permaban"))
      {
        if (player.hasPermission("topsurvivor.admin"))
        {
          if (args.length == 2)
          {
            if (this.plugin.permaBan(args[1]))
            {
              player.sendMessage(ChatColor.GREEN + args[1] + " has been permabanned from the Top Survivor Leaderboard. rip");
              TopSurvivor.server.getLogger().info("[Top Survivor] " + args[1] + " has been permabanned");
              return true;
            }
            player.sendMessage(ChatColor.RED + "You did not enter a valid player");
            return true;
          }
          player.sendMessage(ChatColor.YELLOW + "Usage: ");
          player.sendMessage(ChatColor.YELLOW + "/topsurvivor permaban <player>");
          return true;
        }
        player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
        return true;
      }
      if (args[0].equalsIgnoreCase("unban"))
      {
        if (player.hasPermission("topsurvivor.admin"))
        {
          if (args.length == 2)
          {
            if (this.plugin.unBan(args[1]))
            {
              player.sendMessage(ChatColor.GREEN + args[1] + " has been unbanned from the Top Survivor Leaderboard");
              TopSurvivor.server.getLogger().info("[Top Survivor] " + args[1] + " has been unbanned");
              return true;
            }
            player.sendMessage(ChatColor.RED + "You did not enter a valid player");
            return true;
          }
          player.sendMessage(ChatColor.YELLOW + "Usage: ");
          player.sendMessage(ChatColor.YELLOW + "/topsurvivor unban <player>");
          return true;
        }
        player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
        return true;
      }
      if (args[0].equalsIgnoreCase("archives"))
      {
        if ((player.hasPermission("topsurvivor.admin")) || (player.hasPermission("topsurvivor.historian")))
        {
          int j;
          if (args.length == 2)
          {
            if (args[1].equalsIgnoreCase("last"))
            {
              TopSurvivor.tsarchives.listArchivePlayers(player, (String)TopSurvivor.tsarchives.getArchivesList().get(0), 1);
              return true;
            }
            long ticks = 0L;
            char[] arrayOfChar1;
            j = (arrayOfChar1 = args[1].toCharArray()).length;
            for (int i = 0; i < j; i++)
            {
              char c = arrayOfChar1[i];
              if (Character.getNumericValue(c) > 9)
              {
                player.sendMessage(ChatColor.RED + "Please enter a valid page or filename");
                return true;
              }
              ticks *= 10L;
              
              ticks += Character.getNumericValue(c);
            }
            if (ticks <= 1000000L)
            {
              TopSurvivor.tsarchives.listArchives(player, (int)ticks);
              return true;
            }
            TopSurvivor.tsarchives.listArchivePlayers(player, args[1], 1);
            return true;
          }
          if (args.length == 3)
          {
            String filename = args[1].equals("last") ? (String)TopSurvivor.tsarchives.getArchivesList().get(0) : args[1];
            boolean name = false;
            int ticks = 0;
            char[] arrayOfChar2;
            int k = (arrayOfChar2 = args[2].toCharArray()).length;
            for (j = 0; j < k; j++)
            {
              char c = arrayOfChar2[j];
              if (Character.getNumericValue(c) > 9)
              {
                name = true;
                break;
              }
              ticks *= 10;
              
              ticks += Character.getNumericValue(c);
            }
            if (name)
            {
              TopSurvivor.tsarchives.listPlayerData(player, filename, args[2]);
              return true;
            }
            TopSurvivor.tsarchives.listArchivePlayers(player, filename, ticks);
            return true;
          }
          player.sendMessage(ChatColor.YELLOW + "Usage: ");
          player.sendMessage(ChatColor.YELLOW + "/topsurvivor archives <page>");
          player.sendMessage(ChatColor.YELLOW + "/topsurvivor archives <filename/last> <page>");
          player.sendMessage(ChatColor.YELLOW + "/topsurvivor archives <filename/last> <player>");
          return true;
        }
        player.sendMessage(ChatColor.RED + "What do you think you are doing :I");
        return true;
      }
      if ((args[0].equalsIgnoreCase("viewafk")) && (
        (player.hasPermission("topsurvivor.admin")) || (player.hasPermission("topsurvivor.historian"))))
      {
        if (args.length == 1)
        {
          this.plugin.viewAfkScoreboard(player, 1);
          return true;
        }
        if (args.length == 2)
        {
          boolean page = true;
          int pagenumber = 0;
          for (int i = 0; i < args[1].length(); i++)
          {
            if (Character.getNumericValue(args[1].charAt(i)) > 9)
            {
              page = false;
              break;
            }
            pagenumber *= 10;
            
            pagenumber += Character.getNumericValue(args[1].charAt(i));
          }
          if (page)
          {
            if (this.plugin.viewAfkScoreboard(player, pagenumber)) {
              return true;
            }
            player.sendMessage(ChatColor.RED + "You did not enter a valid page");
            return true;
          }
          player.sendMessage(ChatColor.RED + "You did not enter a valid integer");
          return true;
        }
      }
    }
    else if (cmd.getName().equalsIgnoreCase("profile")){
    	return this.plugin.viewPlayer(player, player.getName());
    }
    return false;
  }
}
