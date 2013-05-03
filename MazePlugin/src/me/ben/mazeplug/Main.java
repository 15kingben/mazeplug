package me.ben.mazeplug;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
public class Main extends JavaPlugin{
        public static Main plugin;
        private Logger log = Logger.getLogger("Minecraft");
        public static boolean auto = false;
        private boolean joinPeriod = false;
        
        //ben will start making comments now 
        static ArrayList<Player> Players = new ArrayList<Player>();
        
        //Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
//                public void run(Block block){
        //            block.setType(Material.WATER);
//                });
        
        
        public static ArrayList<Player> getPlayers(){
        	return Players;
        }
        
        @Override
        public void onDisable(){

        }
        public void onEnable(){
                logMessage("PVP Maze Plugin by 15kingben");
                new WorldLoadListener(this);
                plugin =this;
        }
        static boolean start = false;
        public static void setStart(boolean b){
                start = b;
        }
        public static boolean getStart(){
                return start;
        }
        public static int walls = 400;
        public static int passagewidthpw = 10;
        public void logMessage(String msg){

                PluginDescriptionFile pdfile = this.getDescription();
                log.info(pdfile.getName() + " " + pdfile.getVersion() + " :"+ msg);
        }
        public void setAuto(boolean bool){
                auto = bool;
        }
        public static Main getPlugin(){
        	return plugin;
        	
        }
        
        public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]){
                if(commandLabel.equalsIgnoreCase("create")){
                		if(args.length > 2){
                			return false;
                        }else if(args.length < 2){
                        	return false;
                        }else{
                        	 int passagewidth = Integer.parseInt(args[1]);
                             passagewidthpw = passagewidth;
                        	 int width = Integer.parseInt(args[0]);
                             walls = width;
                		Random r = new Random();
                        generateMaze(new Location(Bukkit.getServer().getWorld("world"), 0, 0, 0), width*2, passagewidth, r, Bukkit.getServer().getWorld("world"));
                        sender.sendMessage("Maze created");
                        return true;
                        }
                
                }
                else if(commandLabel.equalsIgnoreCase("close")){
                        wallsClose();
                }else if(commandLabel.equalsIgnoreCase("joinmatch")){
                	if(joinPeriod){
                		Players.add((Player)sender);
                		sender.sendMessage("You have joined the match");                	
                		
                	}else{
                		sender.sendMessage("The match has already started");
                	}
                }else if(commandLabel.equalsIgnoreCase("auto")){
                        auto = true;
                }else if(commandLabel.equalsIgnoreCase("manual")){
                        auto = false;
                }else if(commandLabel.equalsIgnoreCase("chests")){
                        chests();
                }else if(commandLabel.equalsIgnoreCase("startWalls")){
                        setStart(true);
                        Bukkit.broadcastMessage("walls now on");
                }else if(commandLabel.equalsIgnoreCase("stopWalls")){
                        setStart(false);
                        Bukkit.broadcastMessage("walls now off");
                }else if(commandLabel.equalsIgnoreCase("startMatch")){
                	startMatch(sender.getServer().getOnlinePlayers());
                	sender.getServer().getWorld("world").setPVP(true);
                	
                	
                }else if(commandLabel.equalsIgnoreCase("deleteMaze")){
                		if(args.length >1 || args.length < 1){
                			return false;
                		}else {
                		int width = Integer.parseInt(args[0]);
                		deletMaze(width);
                		sender.sendMessage("Maze deleted");
                        return true;
                		}
              
                }else if(commandLabel.equalsIgnoreCase("stopmatch")){
                	setStart(false);
                	sender.getServer().getWorld("world").setPVP(false);
                	Players.clear();
                	Bukkit.broadcastMessage(ChatColor.RED + "Match has stopped");
                }
			return false;    
         }
        
        public static void sendmessage(Player[] players){
        	for(Player p : players){
        		p.sendMessage("f");
        	}
        	
        }
        
        public void startMatch(Player[] players){// tp no ops to inside the maze
			
        	if(getStart()){
        		setStart(false);
        	}
        	for(Player p : players){
				p.sendMessage(ChatColor.GOLD + "PVPMaze would like you to join a PVP match.\nIf you comply you will be teleported and your inventory will be cleared.\nType /joinmatch to join the match.\n\nTeleportation begins in 1 minute.");
        		
        		
			}
        	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                public void run() {
                   Random r = new Random();
                	if(r.nextInt(2) == 0){
                	   chests();
                   }else{
                	   wallsClose();
                   }
                	doInFive();
                }

              }, 9000L);// 60 L == 3 sec, 20 ticks == 1 sec
        	
        	joinPeriod = true;
        	
        	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
        		public void run(){
        			generateMaze(new Location(Bukkit.getServer().getWorld("world"), 0, 0, 0), 200*2, 10, new Random(), Bukkit.getServer().getWorld("world"));
                    
        			reallyStartMatch();     		
        			joinPeriod = false;
        		}
        	}, 1200L);
        	
			
		}
        
        
        private void reallyStartMatch(){
        	
        	Random r = new Random();
			int x = 0;
			int y = 0;
			Bukkit.broadcastMessage(ChatColor.RED + "Match has started");
        	for(Player p : Players){
        		setStart(true);
        	if(p.isOp() == false){//create random teleport location
				x = r.nextInt(walls/4 -1);
				while(x % passagewidthpw == 0){
					x = r.nextInt(walls/4 -1);
				}
				y = r.nextInt(walls/4 - 1);
				while(y % passagewidthpw == 0){
					y = r.nextInt(walls/4 - 1);
				}
				
				//clear inventory
				p.getInventory().clear();
				//teleport line
				p.teleport(new Location(p.getWorld(), x, p.getWorld().getHighestBlockYAt(x, y), y));
				
        	}
				
			}
        	
        	
        	
        	
        }
        
        
        private void doInFive() {
			// TODO Auto-generated method stub
        	this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                public void run() {
                	Random r = new Random();
                	if(r.nextInt(2) == 0){
                	   chests();
                   }else{
                	   wallsClose();
                   }
                	
                	if(!getStart()){
                	doInFive();
                	}
                }
 
              }, 3600L);// 60 L == 3 sec, 20 ticks == 1 sec
		}
		





        private void chests() {
                World world = Bukkit.getServer().getWorld("world");
                Random r = new Random();
                int x = r.nextInt(walls/8);
                int z = r.nextInt(walls/8);
                while(x % 10 == 0){
                        x = r.nextInt(walls/8);
                }
                while(z % 10 == 0){
                        z = r.nextInt(walls/8);
                }
                int y = world.getHighestBlockYAt(x, z);
                Location l = new Location(world , x,y,z);
                Bukkit.broadcastMessage("A chest has appeared at " + x + ", " + y + ", " + z);
                Block b = l.getBlock();
                b.setType(Material.CHEST);
                b.getState().update(true);
                Chest c = (Chest)b.getState();
                ItemStack stack = new ItemStack(0,0);
                int h = r.nextInt(6) + 3;
                for(int i = 0; i < h; i++){
                        stack = setLoot(stack);
                        c.getInventory().setItem(r.nextInt(26), stack );

                }
        }


        private ItemStack setLoot(ItemStack stack) {
                Random r = new Random();
                int g = r.nextInt(12);
                switch(g){
                		case 0://food
                			int var = r.nextInt(8);
                			stack = pickFood(var);
                			break;
                        case 1://buckets
                                int var1 = r.nextInt(2);
                                int type= (var1==0) ? 326 : 327;
                                stack.setTypeId(type);
                                stack.setAmount(1);
                                break;
                        case 2://obs cobweb
                                int var2 = r.nextInt(2);
                                int h1 = r.nextInt(16) + 1;
                                stack.setAmount(h1);
                                int type1= (var2==0) ? 49 : 30;
                                stack.setTypeId(type1);
                        case 3:// diamond armor
                                stack.setDurability((short)364);
                        	    int var3 = r.nextInt(4);
                                int type3 = 310 + var3;
                                stack.setTypeId(type3);
                                stack.setAmount(1);
                                break;
                        case 4:// ender pearl flint steel
                        	 int var4 = r.nextInt(2);
                             stack.setAmount(1);
                             int type4= (var4==0) ? 259 : 368;
                             stack.setTypeId(type4);
                             break;
                        case 5://bows swords
                        	 stack.setDurability((short)r.nextInt(385));
                        	 int var5 = r.nextInt(2);
                             stack.setAmount(1);
                             int type5= (var5==0) ? 261 : 276;
                             stack.setTypeId(type5);
                             break;
                        case 6://iron stone swords
                        	 stack.setDurability((short)r.nextInt(132));
                        	 int var6 = r.nextInt(2);
                        	 stack.setAmount(1);
                        	 int type6 = (var6==0) ? 267 : 272;
                        	 stack.setTypeId(type6);
                             break;
                        case 7://tnt arrows
                        	 int var7 = r.nextInt(2);
                             int h7 = r.nextInt(16) + 1;
                             stack.setAmount(h7);
                             int type7 = (var7==0) ? 46 : 262;
                             stack.setTypeId(type7);
                             break;
                        case 8://potion 8193-8198
                        	stack.setTypeId(373);
                        	stack.setDurability((short)(8193 + r.nextInt(6)));
                            break;
                        case 9://01 06
                        	stack.setTypeId(373);
                        	short s = (short)((r.nextInt(2) == 0) ? 8206: 8201);
                        	stack.setDurability(s);
                            break;
                        case 10:
                        	stack.setTypeId(373);
                        	short s1 = (short)(r.nextInt(3)*2 + 16392);
                        	stack.setDurability(s1);
                            break;
                        case 12:
                        	int var12 = r.nextInt(2);
                        	int type12 = (var12==0) ? 116 : 384;
                        	stack.setTypeId(type12);
                        	break;

                }

                return stack;
        }
        
		private ItemStack pickFood(int var) {
        	int size = 0;
        	int type = 0;
        	short durability = 0;
        	boolean egg = false;
        	Random r = new Random();
        	switch(var){
        	case 0://chicken steak
        		size = r.nextInt(5)+1;
        		type = 364 + 2*(r.nextInt(2));
        		break;
        	case 1 ://golden carrot apple
        		size = 1;
        		type = (r.nextInt(2) == 0) ? 322 : 396;
        		break;
        	case 2://potato carrot
        		size = r.nextInt(5)+1;
        		type = 391 + 2*(r.nextInt(2));
        		break;
        	case 3://cake melon cookie
        		size = r.nextInt(5)+1;
        		type = 354 + 3*(r.nextInt(3));
        		break;
        	case 4://chops something
        		size = r.nextInt(5)+1;
        		type = (r.nextInt(2) == 0) ? 320 : 350;
        		break;
        	case 5://food
        		size = r.nextInt(10)+1;
        		type = (r.nextInt(2) == 0) ? 260 : 282;
        		break;
        	case 6://bread diamonds
        		type = (r.nextInt(2) == 0) ? 297 : 264;
        		size = r.nextInt(10)+1;
        	case 7:
        		egg = true;
        		size = 1;
        		type = 383;
        		durability = 95;
        	break;
        	}	
        	ItemStack stack = new ItemStack(0,0);
        	if(egg){
        		stack.setDurability(durability);
        		stack.setAmount(size);
        		stack.setTypeId(type);
        	}else{
        		stack.setAmount(size);
        		stack.setTypeId(type);
        	}
			return stack;
		}
		private void theyActuallyClose(){
                walls = walls - walls/4;
                int dimension = walls;
                Location center = new Location(Bukkit.getServer().getWorld("world"), 0,0,0);
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

        }


        private void wallsClose() {
                Bukkit.broadcastMessage("Walls compress in 1.5 minutes by " + getWalls()/16 +" blocks");
                this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

                          public void run() {
                             theyActuallyClose();
                          }
                        }, 1800L);// 60 L == 3 sec, 20 ticks == 1 sec

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


                walls = 400;
                Chamber c = new Chamber(dimension/2, dimension/2, new Location(center.getWorld(), -(.25*dimension), 0, -(.25*dimension)));
                createWalls(c, passageWidth);

        }
        public static int getWalls(){
                return walls;
        }

        private static void createWalls(Chamber chamber, int minWidth){
                String orientation = chamber.getOrientation();
                int poss;
                int hole;
                Location l = new Location(chamber.getBL().getWorld(), 0, 0, 0);
                Random r = new Random();
                if(chamber.getXDimension() <= minWidth || chamber.getZDimension() <= minWidth){
                        
                }else{
                        
                        switch(orientation){
                           case "x":
                                   //vertical
                                   poss = r.nextInt((chamber.getXDimension()/minWidth)-1)+1;
                                   hole = r.nextInt((chamber.getZDimension()/minWidth))+1;
                                   //draw wall
                                   for(int z = chamber.getDown(); z < chamber.getTop(); z++){
                                           if((z > (chamber.getDown() + (hole-1)*minWidth) && z < (chamber.getDown() + (hole)*minWidth))!= true){
                                                   int poop = r.nextInt(13);
                                                   int pee = r.nextInt(90) + 1;
                                                   for(int y = 0; y < 128; y++){
                                                           l.setY(y);
                                                           l.setZ(z);
                                                           l.setX(chamber.getLeft() + minWidth*poss);
                                                           if(poop != 5){
                                                               l.getBlock().setType(Material.BEDROCK);
                                                           }else{
                                                                   if(y == pee || y == (pee + 1)){
                                                                   }else{
                                                                           l.getBlock().setType(Material.BEDROCK);
                                                                   }

                                                           }
                                                           l.getBlock().getState().update(true);
                                                   }
                                           }
                                   }
                                   int mid = (chamber.getLeft()+(minWidth*poss));
                                   int right = chamber.getRight();
                                   int left = chamber.getLeft();
                                   createWalls(new Chamber(right - mid, chamber.getTop()-chamber.getDown(), new Location(l.getWorld(), mid, 0 ,chamber.getDown())),10);
                                   createWalls(new Chamber(mid -  left, chamber.getTop()-chamber.getDown(), new Location(l.getWorld(), left, 0 ,chamber.getDown())),10);
                                   break;
                           case "z":
                                   //horizontal
                                   poss = r.nextInt((chamber.getZDimension()/minWidth)-1)+1;
                                   
                                   hole = r.nextInt((chamber.getXDimension()/minWidth))+1;
                                  
                                   for(int x = chamber.getLeft(); x < chamber.getRight(); x++){
                                           if((x > (chamber.getLeft() + (hole-1)*minWidth) && x < (chamber.getLeft() + (hole)*minWidth)) != true){
                                                       int poop = r.nextInt(13);
                                                       int pee = r.nextInt(90) + 1;

                                                   for(int y = 0; y < 128; y++){
                                                           l.setY(y);
                                                           l.setX(x);
                                                           l.setZ(chamber.getDown() + minWidth*poss);
                                                           if(poop != 5){
                                                               l.getBlock().setType(Material.BEDROCK);
                                                           }else{
                                                                   if(y == pee || y == (pee + 1)){
                                                                         }else{
                                                                           l.getBlock().setType(Material.BEDROCK);
                                                                   }

                                                           }

                                                           l.getBlock().getState().update(true);
                                                   }
                                }
                        }
                                   int mid2 = (chamber.getDown()+(minWidth*poss));
                                   int top = chamber.getTop();
                                   int down = chamber.getDown();
                                   createWalls(new Chamber(chamber.getRight()-chamber.getLeft(), top-mid2, new Location(l.getWorld(), chamber.getLeft() , 0, mid2)),10);
                                   createWalls(new Chamber(chamber.getRight()-chamber.getLeft(), mid2-down, new Location(l.getWorld(), chamber.getLeft(), 0, down)),10);


                                   break;
                        }
                }
        }
        public static boolean getAuto() {

                return auto;
        }
        private void deletMaze(int width) {
			Location l = new Location(Bukkit.getServer().getWorld("world"), 0,0,0);
			Block b = l.getBlock();
			Block[] blocks = new Block[4];//array of surrounding blocks
			for(int x = -(width/2); x <= width/2; x++){//x axis
				for(int z = -(width/2); z <= width/2; z++){//z axis
					for(int y = 5; y < 129; y++){//y axis
						l.setX(x);
						l.setY(y);
						l.setZ(z);
						b=l.getBlock();
						if(b.getType() == Material.BEDROCK){//if its a maze block
							
							l.setX(x+1);
							l.setY(y);
							l.setZ(z);
							blocks[0] = l.getBlock();//set the surrounding blocks
							l.setX(x-1);
							l.setY(y);
							l.setZ(z);
							blocks[1] = l.getBlock();
							l.setY(y);
							l.setX(x);
							l.setZ(z+1);
							blocks[2] = l.getBlock();
							l.setY(y);
							l.setX(x);
							l.setZ(z-1);
							blocks[3] = l.getBlock();
							boolean match = false;
							for(int i = 0; i < 4; i++){
								if(blocks[i].getType() != Material.BEDROCK){
									for(int count = i+1; count < 4; count++){
										if(blocks[i].getType() == blocks[count].getType()){
											
											b.setType(blocks[i].getType());
											match = true;
										}
									}
								}
							}
							
							if(match == false){
								for(int i = 0; i < 4; i++){
									if(blocks[i].getType() != Material.BEDROCK || blocks[i].getType() != Material.BEDROCK ){//test feature
										b.setType(blocks[i].getType());
										
										break;
									}
								}
							}match = false;
						}
						
						b.getState().update(true);
					}
				}
			}
		}

}