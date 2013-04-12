package me.ben.mazeplug;

import java.util.logging.Logger;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	public static Main plugin;
	private Logger log = Logger.getLogger("Minecraft");
	
	
	//Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
//		public void run(Block block){
	//	    block.setType(Material.WATER);
//		});
	
	@Override
	public void onDisable(){
		
	}
	public void onEnable(){
		logMessage("Enable");
		new WorldLoadListener(this);
		
	}
	
	public void logMessage(String msg){
		
		PluginDescriptionFile pdfile = this.getDescription();
		log.info(pdfile.getName() + " " + pdfile.getVersion() + " :"+ msg);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
		if(commandLabel.equalsIgnoreCase("broadcast")){
			if(args[0].equalsIgnoreCase("blue")){
				String toBroadcast = "";
				for(String s : args){
					if(s != "blue"){
						toBroadcast = toBroadcast + s + " ";
					}
					
				}
				Bukkit.broadcastMessage(ChatColor.BLUE + "[SERVER]" +  toBroadcast);
			}
			
		}else if(commandLabel.equalsIgnoreCase("create")){
		
			Random r = new Random();
			generateMaze(new Location(Bukkit.getServer().getWorld("world"), 0, 0, 0), 500, 5, r, Bukkit.getServer().getWorld("world"));
		
		
		}else if(commandLabel.equalsIgnoreCase("cube")){
			Player p = (Player)sender;
			Location pp = new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY() + 10, p.getLocation().getBlockZ());
			cube(pp ,5);
		}
	return false;
	}
	
	
	
	
	
	
	
	
	public static void generateMaze(Location center, int dimension, int passageWidth, Random r , World world)
	{
				Location l = new Location(center.getWorld(), 0,0,0);
				center.setX(0);
				center.setY(0);
				center.setZ(0);
		
		//gen outer walls 1
		for(int i = (int) (center.getBlockX()-(dimension/4)); i <= (center.getBlockX() + (dimension/4)); i++ ){
			for(int ii = 0; ii < 128; ii++){
			l.setY(ii);
			l.setX(i);
			l.setZ(center.getBlockZ() - dimension/4);//******
			
			Block b = l.getBlock();
			b.setType(Material.BEDROCK);
			b.getState().update(true);
			}
		}
		//gen outer walls 2
		for(int i = (int) (center.getBlockX()-(dimension/4)); i <= (center.getBlockX() + dimension/4); i++ ){
			for(int ii = 0; ii < 128; ii++){
			l.setY(ii);
			l.setX(i);
			l.setZ(center.getBlockZ() + dimension/4);//******changed
			
			Block b = l.getBlock();
			b.setType(Material.BEDROCK);
			b.getState().update(true);
			}
		}	
		//gen outer walls 3 switch x and z
		for(int i = (int) (center.getBlockZ()-dimension/4); i <= (center.getBlockZ() + dimension/4); i++ ){
			for(int ii = 0; ii < 128; ii++){
			l.setY(ii);
			l.setZ(i);
			l.setX(center.getBlockX() - dimension/4);//******
			
			Block b = l.getBlock();
			b.setType(Material.BEDROCK);
			b.getState().update(true);
			}
		}
		//gen outer walls 4
		for(int i = (int) (center.getBlockZ()-dimension/4); i <= (center.getBlockZ() + dimension/4); i++ ){
			for(int ii = 0; ii < 128; ii++){
			l.setY(ii);
			l.setZ(i);
			l.setX(center.getBlockX() + dimension/4);//******changed
			
			Block b = l.getBlock();
			b.setType(Material.BEDROCK);
			b.getState().update(true);
			}
		}
		Bukkit.broadcastMessage("Outer walls generated");
		
		
		
		Chamber c = new Chamber(dimension/2, dimension/2, center);
		createWalls(c, passageWidth);
		
	}
	
	private static void createWalls(Chamber chamber, int minWidth){
		String orientation = chamber.getOrientation();
		int poss;
		int hole;
		Location l = new Location(chamber.getCenter().getWorld(), 0, 0, 0);
		Random r = new Random();
		if(chamber.getXDimension() == minWidth || chamber.getZDimension() == minWidth){
			//base case
		}else{
			Bukkit.broadcastMessage(orientation);
			switch(orientation){
			   case "x": 
				   //vertical
				   poss = r.nextInt(chamber.getXDimension()/minWidth-1)+1;
				   Bukkit.broadcastMessage("poss = " + poss);
				   hole = r.nextInt(chamber.getZDimension()/minWidth)+1;
				   Bukkit.broadcastMessage("hole = " + hole);
				   Bukkit.broadcastMessage(Integer.toString(chamber.getDown()));
				   Bukkit.broadcastMessage(Integer.toString(chamber.getTop()));
				   //draw wall
				   for(int z = chamber.getDown(); z < chamber.getTop(); z++){
					 
					   //Bukkit.broadcastMessage(Integer.toString(chamber.getLeft() + (hole-1)*minWidth));
					   //Bukkit.broadcastMessage(Integer.toString(chamber.getLeft() + (hole)*minWidth));
					   if(!(z > (chamber.getLeft() + (hole-1)*minWidth) && z < (chamber.getLeft() + (hole)*minWidth))){
					   	for(int y = 0; y < 128; y++){
					   		l.setY(y);
					   		l.setZ(z);
					   		l.setX(chamber.getLeft() + minWidth*poss);
					   		//Bukkit.broadcastMessage("poss = poss" + "\t\t" + "x =  " + l.getBlockX() + "\t\t" + "z = "+ l.getBlockZ() + "\n");
					   		l.getBlock().setType(Material.BEDROCK);
					   		l.getBlock().getState().update(true);
				   		}
					   }
				   }
				   createWalls(new Chamber(chamber.getRight()-(chamber.getLeft()+minWidth*poss), chamber.getTop()-chamber.getDown(), new Location(l.getWorld(),(chamber.getLeft()+minWidth*poss) + (chamber.getRight()-(chamber.getLeft()+minWidth*poss)/2) ,0 ,chamber.getDown() + (chamber.getTop()-chamber.getDown())/2)),5);
				   createWalls(new Chamber((chamber.getLeft()+minWidth*poss)-chamber.getLeft(), chamber.getTop()-chamber.getDown(), new Location(l.getWorld(), chamber.getLeft() + ((chamber.getLeft()+minWidth*poss) - chamber.getLeft())/2, 0 ,chamber.getDown() + (chamber.getTop()-chamber.getDown())/2)),5);
				   break;
			   case "z":
				   //horizontal
				   poss = r.nextInt(chamber.getZDimension()/minWidth-1)+1;
				   Bukkit.broadcastMessage("poss = " + poss);
				   hole = r.nextInt(chamber.getXDimension()/minWidth)+1;
				   Bukkit.broadcastMessage("hole = " + hole);
				   //draw wall
				   Bukkit.broadcastMessage(Integer.toString(chamber.getLeft()));
				   Bukkit.broadcastMessage(Integer.toString(chamber.getRight()));
				   
				   for(int x = chamber.getLeft(); x < chamber.getRight(); x++){
					   if(!(x > (chamber.getDown() + (hole-1)*minWidth) && x < (chamber.getDown() + (hole)*minWidth))){
					   	for(int y = 0; y < 128; y++){
					   		l.setY(y);
					   		l.setX(x);
					   		l.setZ(chamber.getDown() + minWidth*poss);
					   		l.getBlock().setType(Material.BEDROCK);
					   		l.getBlock().getState().update(true);
					   		//Bukkit.broadcastMessage("poss = poss" + "\t\t" + "z =  " + l.getBlockZ() + "\t\t" + "x = "+ l.getBlockX() + "\n");
					   		
				   		}
					   }
				   }
				   createWalls(new Chamber(chamber.getTop()-(chamber.getDown()+minWidth*poss), chamber.getRight()-chamber.getLeft(), new Location(l.getWorld(),(chamber.getDown()+minWidth*poss) + (chamber.getTop()-(chamber.getDown()+minWidth*poss)/2) ,0 ,chamber.getLeft() + (chamber.getRight()-chamber.getLeft())/2)),5);
				   createWalls(new Chamber((chamber.getDown()+minWidth*poss)-chamber.getDown(), chamber.getRight()-chamber.getLeft(), new Location(l.getWorld(), chamber.getDown() + ((chamber.getDown()+minWidth*poss) - chamber.getDown())/2, 0 ,chamber.getLeft() + (chamber.getRight()-chamber.getLeft())/2)),5);
				   
				   
				   
				   break;
			}
			
		}
	}
	
	
	
	private static void drawWall(){
		
	}
	
	
	
	public void cube(Location point, int length){  // public visible method generateCube() with 2 parameters point and location
		World world = point.getWorld();
		Bukkit.broadcastMessage("This has run");
		int x_start = point.getBlockX();     // Set the startpoints to the coordinates of the given location
		int y_start = point.getBlockY() + 10;     // I use getBlockX() instead of getX() because it gives you a int value and so you dont have to cast it with (int)point.getX()
		int z_start = point.getBlockZ();
	 
		int x_length = x_start + length;    // now i set the lengths for each dimension... should be clear.
		int y_length = y_start + length;
		int z_length = z_start + length;
	 
		for(int x_operate = x_start; x_operate <= x_length; x_operate++){ 
			// Loop 1 for the X-Dimension "for x_operate (which is set to x_start) 
			//do whats inside the loop while x_operate is 
			//<= x_length and after each loop increase 
			//x_operate by 1 (x_operate++ is the same as x_operate=x_operate+1;)
			for(int y_operate = y_start; y_operate <= y_length; y_operate++){// Loop 2 for the Y-Dimension
				for(int z_operate = z_start; z_operate <= z_length; z_operate++){// Loop 3 for the Z-Dimension
	 
					Block blockToChange = world.getBlockAt(x_operate,y_operate,z_operate); // get the block with the current coordinates
					BlockState blockToChangeState = blockToChange.getState();
					blockToChangeState.setType(Material.BEDROCK);
					blockToChangeState.update(true);
					
					
					//blockToChange.setType(Material.BEDROCK);
					//blockToChange.getState().update();
					
					
					
					//blockToChange.setTypeId(34);    // set the block to Type 34
					//blockToChange.getState().update(true);
					//blockToChange.getState().setType(Material.BEDROCK);
				}
			}
		}
		
	}
	
	

}