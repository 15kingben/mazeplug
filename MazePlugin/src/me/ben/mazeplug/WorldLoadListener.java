package me.ben.mazeplug;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.Plugin;

public class WorldLoadListener implements Listener {
	public WorldLoadListener(Plugin plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		
	}
	public static boolean first = false;
	//@EventHandler
	//public void onWorldInit(WorldInitEvent wit){
	//	Random r = new Random();
	//	Main.generateMaze(new Location(wit.getWorld(),0,0,0), 500, 5, r, wit.getWorld());
	//}
	@EventHandler
	public void onWordInit(PlayerLoginEvent wit){
		Random r = new Random();
		if(!first){
		Main.generateMaze(wit.getPlayer().getLocation(), 800, 10, r, wit.getPlayer().getWorld());
		first = true;
		}
	}
/*	@EventHandler
	public void onWodInit(PlayerQuitEvent wit){
		//*****************ONLINE PLAYERS IS ONE AT THE TIME******************
			Bukkit.broadcastMessage(Integer.toString(Bukkit.getServer().getOnlinePlayers().length));
			Player[] players = Bukkit.getServer().getOnlinePlayers();
	        
	            if (players.length == 0){
	            	Bukkit.broadcastMessage("ONe Player left worked");
	        		wit.getPlayer().getServer().getScheduler().scheduleSyncDelayedTask(new Main(), new DeleteWorld(wit.getPlayer().getWorld()), 60L);
             }
	}*/
	
	@EventHandler
	public void onBlockPlace(PlayerMoveEvent event){
		Player p = event.getPlayer();
		if(p.getLocation().getBlockY() > 120){
			p.sendMessage(ChatColor.GOLD + "Your too high, Go Down!");
		}
		//if(p.getLocation().getBlockY() > 128){
		//	p.kickPlayer("You were too high");
		//	
		//}
	}
	 // @EventHandler
     //public void onPlayerMove(PlayerMoveEvent event){
     //        Player player = event.getPlayer();
     //        Location playerLoc = player.getLocation();
     //         player.sendMessage("Your X Coordinates : " + playerLoc.getX());
     //         player.sendMessage("Your Y Coordinates : " + playerLoc.getY());
     //         player.sendMessage("Your Z Coordinates : " + playerLoc.getZ());
     //}
}
