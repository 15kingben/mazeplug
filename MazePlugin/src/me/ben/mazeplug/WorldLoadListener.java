package me.ben.mazeplug;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class WorldLoadListener implements Listener {
	Main plugin = new Main();
	public WorldLoadListener(Plugin plugin){
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.plugin = (Main) plugin;
	}
	
	
	private int size = 400;
	public int getSize(){
		return size;
	}
	public void setSize(int k){
		size = k;
	
	}
	
	
	
	public static boolean first = false;
	//@EventHandler
	//public void onWorldInit(WorldInitEvent wit){
	//	Random r = new Random();
	//	Main.generateMaze(new Location(wit.getWorld(),0,0,0), 500, 5, r, wit.getWorld());
	//}
	@EventHandler    
	public void onWorldInit(PlayerJoinEvent wit){
		Random r = new Random();
		if(first == false && Main.getAuto() == true){
		Main.sendmessage(wit.getPlayer().getServer().getOnlinePlayers());
		plugin.startMatch(wit.getPlayer().getServer().getOnlinePlayers());
		
		first = true;
		wit.getPlayer().getWorld().setPVP(false);
		}
		
	}
	
	public void onPlayerJoin(PlayerJoinEvent pje){
		if(Main.getStart() == true && !Main.getPlayers().contains(pje.getPlayer()) && pje.getPlayer().getLocation().getBlockX() < Main.getWalls()/4 && pje.getPlayer().getLocation().getBlockZ() < Main.getWalls()/4 ){
			pje.getPlayer().teleport(new Location(pje.getPlayer().getWorld(), pje.getPlayer().getLocation().getX(), pje.getPlayer().getWorld().getHighestBlockYAt(pje.getPlayer().getLocation().getBlockX(), Main.getWalls()/4 + 10),  Main.getWalls()/4 + 10 ));
			pje.getPlayer().sendMessage("You have been teleported out of the maze because there is a match going on");
		}
	}
	@EventHandler
	public void onBlockPlace(PlayerMoveEvent event){
		boolean outside = false;
		Player p = event.getPlayer();
		
		if(Main.getStart() == true && !Main.getPlayers().contains(event.getPlayer()) && event.getPlayer().getLocation().getBlockX() < Main.getWalls()/4 && event.getPlayer().getLocation().getBlockZ() < Main.getWalls()/4 ){
			event.getPlayer().teleport(new Location(event.getPlayer().getWorld(), event.getPlayer().getLocation().getX(), event.getPlayer().getWorld().getHighestBlockYAt(event.getPlayer().getLocation().getBlockX(), Main.getWalls()/4 + 10),  Main.getWalls()/4 + 10 ));
			event.getPlayer().sendMessage("You have been teleported out of the maze because there is a match going on");
		}
		
		if(Main.getPlayers().contains(p) && Main.getStart()){
		
			
			
			
		if(p.getLocation().getBlockY() > 120 && p.isOp() == false){
			p.sendMessage(ChatColor.GOLD + "Your too high, Go Down!");
		}
		if(p.getLocation().getBlockY() > 128 && p.isOp() == false && Main.getStart() == true){
			p.kickPlayer("You went too high!");
		}
		if(!p.isOp()){
		    if(p.getLocation().getBlockZ() > Main.getWalls() / 4 || p.getLocation().getBlockZ() < -(Main.getWalls() / 4) || p.getLocation().getBlockX() > Main.getWalls() / 4 || p.getLocation().getBlockX() < -(Main.getWalls() / 4) ){
		    	outside = true;
			}
		if(outside == true && Main.getStart() == true){
			p.kickPlayer("The walls crushed you");
		}
		}
		//if(p.getLocation().getBlockY() > 128){
		//	p.kickPlayer("You were too high");
		//	
		//}
	}
		
	}//end contains(p)
	
	//todo winning protocol
	public void onDeath(PlayerDeathEvent evt){
		
		
	}
	
}
