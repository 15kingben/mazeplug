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
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	public static Main plugin;
	private Logger log = Logger.getLogger("Minecraft");
	public static boolean auto = false;
	private boolean joinPeriod = false;
	private static int wallsorig;

	// ben will start making comments now
	static ArrayList<Player> Players = new ArrayList<Player>();

	// Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
	// public void run(Block block){
	// block.setType(Material.WATER);
	// });

	public static ArrayList<Player> getPlayers() {
		return Players;
	}

	@Override
	public void onDisable() {

	}

	public void onEnable() {
		logMessage("PVP Maze Plugin by 15kingben");
		new WorldLoadListener(this);
		plugin = this;
		this.saveDefaultConfig();
	}

	static boolean start = false;

	public static void setStart(boolean b) {
		start = b;
	}

	public static boolean getStart() {
		return start;
	}

	public static int walls = 400;
	public static int passagewidthpw = 10;

	public static World world;
	public static Location center = new Location(null, 0, 0, 0);

	public void logMessage(String msg) {

		PluginDescriptionFile pdfile = this.getDescription();
		log.info(pdfile.getName() + " " + pdfile.getVersion() + " :" + msg);
	}

	public void setAuto(boolean bool) {
		auto = bool;
	}

	public static Main getPlugin() {
		return plugin;

	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String args[]) {
		if (commandLabel.equalsIgnoreCase("create")) {
			Location center = new Location(null, 0, 0, 0);

			if (args.length > 2) {
				return false;
			} else if (args.length == 1) {
				return false;
			} else if (sender instanceof ConsoleCommandSender) {// determine
																// console vs
																// player, take
																// loccation
																// from config
																// if console
				center.setWorld(Bukkit.getServer().getWorld(
						this.getConfig().getString("worldname")));
				center.setY(0);
				center.setX(this.getConfig().getInt("locx"));
				center.setZ(this.getConfig().getInt("locz"));// location values
				int width;
				int passagewidth;
				if (args.length == 2) {// if width and pw data given
					Integer.parseInt(args[0]);
					width = Integer.parseInt(args[0]);
					passagewidth = Integer.parseInt(args[1]);
					Player p = (Player)sender;
					center.setWorld(p.getWorld());
					
				} else {// if not get from config
					width = this.getConfig().getInt("width");
					passagewidth = this.getConfig().getInt("passagewidth");

				}

				walls = width * 2;
				wallsorig = width * 2;
				generateMaze(center, width * 2, passagewidth);
				sender.sendMessage("Maze created");
				return true;

			} else if (sender instanceof Player) {
				center.setWorld(Bukkit.getServer().getWorld(
						this.getConfig().getString("worldname")));
				center.setY(0);
				Player p = (Player) sender;
				center.setX(p.getLocation().getX() + 1);
				center.setZ(p.getLocation().getZ() + 1);// location values
				int width;
				int passagewidth;
				if (args.length == 2) {// if width and pw data given
					Integer.parseInt(args[0]);
					width = Integer.parseInt(args[0]);
					passagewidth = Integer.parseInt(args[1]);

				} else {// if not get from config
					width = this.getConfig().getInt("width");
					passagewidth = this.getConfig().getInt("passagewidth");
					center.setWorld(Bukkit.getServer().getWorld(
							this.getConfig().getString("worldname")));
					center.setY(0);
					center.setX(this.getConfig().getInt("locx"));
					center.setZ(this.getConfig().getInt("locz"));// location
																	// values
				}

				walls = width * 2;
				wallsorig = width * 2;
				generateMaze(center, width * 2, passagewidth);
				sender.sendMessage("Maze created");
				return true;

			}

		} else if (commandLabel.equalsIgnoreCase("joinmatch")) {
			if (joinPeriod) {
				Players.add((Player) sender);
				sender.sendMessage("You have joined the match");

			} else {
				sender.sendMessage("The match has already started");
			}
		}/*
		 * else if(commandLabel.equalsIgnoreCase("auto")){ auto = true; }else
		 * if(commandLabel.equalsIgnoreCase("manual")){ auto = false; }else
		 * if(commandLabel.equalsIgnoreCase("chests")){ if(sender instanceof
		 * ConsoleCommandSender){ World world =
		 * Bukkit.getServer().getWorld(this.getConfig().getString("worldname"));
		 * 
		 * }
		 * 
		 * 
		 * chests(); }else if(commandLabel.equalsIgnoreCase("startWalls")){
		 * setStart(true); Bukkit.broadcastMessage("walls now on"); }else
		 * if(commandLabel.equalsIgnoreCase("stopWalls")){ setStart(false);
		 * Bukkit.broadcastMessage("walls now off"); }
		 */else if (commandLabel.equalsIgnoreCase("startMatch")) {
			if (getStart())
				this.getServer().getScheduler().cancelTasks(this);
			startMatch(sender.getServer().getOnlinePlayers(), sender);
			sender.getServer().getWorld("world").setPVP(true);

			this.world = Bukkit.getServer().getWorld(
					this.getConfig().getString("worldname"));
			this.center.setX(this.getConfig().getInt("locx"));
			this.center.setZ(this.getConfig().getInt("locz"));
			this.center.setWorld(this.world);
		} else if (commandLabel.equalsIgnoreCase("deleteMaze")) {

			int width;
			Location delcenter = new Location(Bukkit.getServer().getWorld(
					this.getConfig().getString("deletemaze.worldname")), this
					.getConfig().getInt("deletemaze.locx"), 0, this.getConfig()
					.getInt("locz"));
			width = this.getConfig().getInt("deletemaze.width");

			deleteMaze(width, delcenter);
			sender.sendMessage("Maze deleted");

		} else if (commandLabel.equalsIgnoreCase("stopmatch")) {
			setStart(false);
			sender.getServer().getWorld("world").setPVP(false);
			Players.clear();
			Bukkit.broadcastMessage(ChatColor.RED + "Match has stopped");
			Bukkit.getScheduler().cancelTasks(this);
		}

		return false;
	}

	public static void sendmessage(Player[] players) {
		for (Player p : players) {
			p.sendMessage("f");
		}

	}

	public void startMatch(Player[] players, CommandSender senderd) {// tp no
																		// ops
																		// to
																		// inside
																		// the
																		// maze
		final CommandSender sender = senderd;

		if (getStart()) {
			setStart(false);
		}
		for (Player p : players) {
			p.sendMessage(ChatColor.GOLD
					+ "PVPMaze would like you to join a PVP match.\nIf you comply you will be teleported and your inventory will be cleared.\nType /joinmatch to join the match.\n\nTeleportation begins in 1 minute.");

		}

		if (this.getConfig().getInt("chestsfreq") != -1
				&& this.getConfig().getInt("chestsfreq") != 0) {

			this.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(this,
							new Runnable() {// initial chests delay

								public void run() {

									chests();
									chestsDelay();

								}

							},
							(this.getConfig().getInt("chestsdelay") + 1) * 20L * 60L);// 60
																				// L
																				// ==
																				// 3
																				// sec,
																				// 20
																				// ticks
																				// ==
																				// 1
																				// sec

		}

		if (this.getConfig().getInt("wallsfreq") != -1
				&& this.getConfig().getInt("wallsfreq") != 0) {
			this.getServer()
					.getScheduler()
					.scheduleSyncDelayedTask(this,
							new Runnable() {// initial walls delay

								public void run() {
									theyActuallyClose();
									wallsDelay();

								}

							},
							(this.getConfig().getInt("wallsdelay")+1) * 20L * 60L);// 60
																				// L
																				// ==
																				// 3
																				// sec,
																				// 20
																				// ticks
																				// ==
																				// 1
																				// sec
		}

		joinPeriod = true;

		this.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {

						Location center = new Location(null, 0, 0, 0);

						if (sender instanceof ConsoleCommandSender) {// determine
																		// console
																		// vs
																		// player,
																		// take
																		// loccation
																		// from
																		// config
																		// if
																		// console
							center.setWorld(Bukkit.getServer().getWorld(
									plugin.getConfig().getString("worldname")));
							center.setY(0);
							center.setX(plugin.getConfig().getInt("locx"));
							center.setZ(plugin.getConfig().getInt("locz"));// location
																			// values
							int width;
							int passagewidth;
							width = plugin.getConfig().getInt("width");
							passagewidth = plugin.getConfig().getInt(
									"passagewidth");

							walls = width * 2;
							wallsorig = width * 2;
							generateMaze(center, width * 2, passagewidth);
							sender.sendMessage("Maze created");

						} else if (sender instanceof Player) {
							center.setWorld(Bukkit.getServer().getWorld(
									plugin.getConfig().getString("worldname")));
							center.setY(0);
							Player p = (Player) sender;
							center.setX(p.getLocation().getX() + 1);
							center.setZ(p.getLocation().getZ() + 1);// location
																	// values
							int width;
							int passagewidth;

							width = plugin.getConfig().getInt("width");
							passagewidth = plugin.getConfig().getInt(
									"passagewidth");

							walls = width * 2;
							wallsorig = width * 2;
							generateMaze(center, width * 2, passagewidth);
							sender.sendMessage("Maze created");
						}

						reallyStartMatch();
					}

				}, 1200L);

	}

	public static int getWallsOrig() {
		return wallsorig;
	}

	public void chestsDelay() {// chests repeat schedule
		this.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new Runnable() {

					@Override
					public void run() {
						chests();
						chestsDelay();
					}

				}, 60L * 20L * this.getConfig().getInt("chestsfreq"));

	}

	public void wallsDelay() {// walls delay schedule
		Bukkit.broadcastMessage("Walls compress in "
				+ this.getConfig().getInt("wallsfreq") + " minutes by "
				+ getWalls() / 16 + " blocks");

		this.getServer().getScheduler()
				.scheduleSyncDelayedTask(this, new Runnable() {

					@Override
					public void run() {
						theyActuallyClose();
						wallsDelay();
					}

				}, 60L * 20L * this.getConfig().getInt("wallsfreq"));

	}

	private void reallyStartMatch() {

		Random r = new Random();
		int x = 0;
		int y = 0;
		Bukkit.broadcastMessage(ChatColor.RED + "Match has started");
		for (Player p : Players) {
			setStart(true);
			if (p.isOp() == false) {// create random teleport location
				x = center.getBlockX() + walls / 4 - r.nextInt(walls / 2 - 2)
						- 1;
				while (x % this.getConfig().getInt("passagewidth") == 0) {
					x = center.getBlockX() + walls / 4
							- r.nextInt(walls / 2 - 2) - 1;
				}
				y = center.getBlockZ() + walls / 4 - r.nextInt(walls / 4 - 2)
						- 1;
				while (y % this.getConfig().getInt("passagewidth") == 0) {
					y = center.getBlockZ() + walls / 4
							- r.nextInt(walls / 4 - 2) - 1;
				}

				// clear inventory
				p.getInventory().clear();
				// teleport line
				p.teleport(new Location(p.getWorld(), x, Bukkit.getServer()
						.getWorld(this.getConfig().getString("worldname"))
						.getHighestBlockYAt(x, y), y));

			}

		}

		if (this.getConfig().getInt("wallsfreq") != -1
				&& this.getConfig().getInt("wallsfreq") != 0)
			Bukkit.broadcastMessage("Walls compress in "
					+ this.getConfig().getInt("wallsdelay") + " minutes by "
					+ getWalls() / 16 + " blocks");

	}

	public static Location getCenter() {
		return center;
	}

	private void chests() {
		World world = this.world;
		Random r = new Random();
		int x = 0;
		if (r.nextInt(2) == 1) {
			x = center.getBlockX() + r.nextInt(walls / 8 - 1);
		} else {
			x = center.getBlockX() - r.nextInt(walls / 8 + 1);
		}
		int z;
		if (r.nextInt(2) == 1) {
			z = center.getBlockZ() + r.nextInt(walls / 8 - 1);
		} else {
			z = center.getBlockZ() - r.nextInt(walls / 8 + 1);
		}

		while (x % 10 == 0) {
			if (r.nextInt(2) == 1) {
				x = center.getBlockX() + r.nextInt(walls / 8 - 1);
			} else {
				x = center.getBlockX() - r.nextInt(walls / 8 + 1);
			}
		}
		while (z % 10 == 0) {
			if (r.nextInt(2) == 1) {
				z = center.getBlockZ() + r.nextInt(walls / 8 - 1);
			} else {
				z = center.getBlockZ() - r.nextInt(walls / 8 + 1);
			}
		}
		int y = world.getHighestBlockYAt(x, z);
		Location l = new Location(world, x, y, z);

		for (Player p : Players) {
			p.sendMessage(("A chest has appeared at " + x + ", " + y + ", " + z));
		}
		logMessage("A chest has appeared at " + x + ", " + y + ", " + z);

		Block b = l.getBlock();
		b.setType(Material.CHEST);
		b.getState().update(true);
		Chest c = (Chest) b.getState();
		ItemStack stack = new ItemStack(0, 0);
		int h = r.nextInt(6) + 3;
		for (int i = 0; i < h; i++) {
			stack = setLoot(stack);
			c.getInventory().setItem(r.nextInt(26), stack);

		}
	}

	private ItemStack setLoot(ItemStack stack) {
		Random r = new Random();
		int g = r.nextInt(12);
		switch (g) {
		case 0:// food
			int var = r.nextInt(8);
			stack = pickFood(var);
			break;
		case 1:// buckets
			int var1 = r.nextInt(2);
			int type = (var1 == 0) ? 326 : 327;
			stack.setTypeId(type);
			stack.setAmount(1);
			break;
		case 2:// obs cobweb
			int var2 = r.nextInt(2);
			int h1 = r.nextInt(16) + 1;
			stack.setAmount(h1);
			int type1 = (var2 == 0) ? 49 : 30;
			stack.setTypeId(type1);
		case 3:// diamond armor
			stack.setDurability((short) 364);
			int var3 = r.nextInt(4);
			int type3 = 310 + var3;
			stack.setTypeId(type3);
			stack.setAmount(1);
			break;
		case 4:// ender pearl flint steel
			int var4 = r.nextInt(2);
			stack.setAmount(1);
			int type4 = (var4 == 0) ? 259 : 368;
			stack.setTypeId(type4);
			break;
		case 5:// bows swords
			stack.setDurability((short) r.nextInt(385));
			int var5 = r.nextInt(2);
			stack.setAmount(1);
			int type5 = (var5 == 0) ? 261 : 276;
			stack.setTypeId(type5);
			break;
		case 6:// iron stone swords
			stack.setDurability((short) r.nextInt(132));
			int var6 = r.nextInt(2);
			stack.setAmount(1);
			int type6 = (var6 == 0) ? 267 : 272;
			stack.setTypeId(type6);
			break;
		case 7:// tnt arrows
			int var7 = r.nextInt(2);
			int h7 = r.nextInt(16) + 1;
			stack.setAmount(h7);
			int type7 = (var7 == 0) ? 46 : 262;
			if (type7 == 262)
				stack.setAmount(h7 + 9);
			stack.setTypeId(type7);
			break;
		case 8:// potion 8193-8198
			stack.setTypeId(373);
			stack.setDurability((short) (8193 + r.nextInt(6)));
			break;
		case 9:// 01 06
			stack.setTypeId(373);
			short s = (short) ((r.nextInt(2) == 0) ? 8206 : 8201);
			stack.setDurability(s);
			break;
		case 10:
			stack.setTypeId(373);
			short s1 = (short) (r.nextInt(3) * 2 + 16392);
			stack.setDurability(s1);
			break;
		case 12:
			int var12 = r.nextInt(2);
			int type12 = (var12 == 0) ? 116 : 384;
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
		switch (var) {
		case 0:// chicken steak
			size = r.nextInt(5) + 1;
			type = 364 + 2 * (r.nextInt(2));
			break;
		case 1:// golden carrot apple
			size = 1;
			type = (r.nextInt(2) == 0) ? 322 : 396;
			break;
		case 2:// potato carrot
			size = r.nextInt(5) + 1;
			type = 391 + 2 * (r.nextInt(2));
			break;
		case 3:// cake melon cookie
			size = r.nextInt(5) + 1;
			type = 354 + 3 * (r.nextInt(3));
			break;
		case 4:// chops something
			size = r.nextInt(5) + 1;
			type = (r.nextInt(2) == 0) ? 320 : 350;
			break;
		case 5:// food
			size = r.nextInt(10) + 1;
			type = (r.nextInt(2) == 0) ? 260 : 282;
			break;
		case 6:// bread diamonds
			type = (r.nextInt(2) == 0) ? 297 : 264;
			size = r.nextInt(10) + 1;
		case 7:
			egg = true;
			size = 1;
			type = 383;
			durability = 95;
			break;
		}
		ItemStack stack = new ItemStack(0, 0);
		if (egg) {
			stack.setDurability(durability);
			stack.setAmount(size);
			stack.setTypeId(type);
		} else {
			stack.setAmount(size);
			stack.setTypeId(type);
		}
		return stack;
	}

	private void theyActuallyClose() {

		walls = walls - walls / 4;
		int dimension = walls;
		Location center = this.center;
		Location l = new Location(center.getWorld(), 0, 0, 0);

		// gen outer walls 1
		for (int i = (int) (center.getBlockX() - (dimension / 4)); i <= (center
				.getBlockX() + (dimension / 4)); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setX(i);
				l.setZ(center.getBlockZ() - dimension / 4);// ******

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}
		// gen outer walls 2
		for (int i = (int) (center.getBlockX() - (dimension / 4)); i <= (center
				.getBlockX() + dimension / 4); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setX(i);
				l.setZ(center.getBlockZ() + dimension / 4);// ******changed

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}
		// gen outer walls 3 switch x and z
		for (int i = (int) (center.getBlockZ() - dimension / 4); i <= (center
				.getBlockZ() + dimension / 4); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setZ(i);
				l.setX(center.getBlockX() - dimension / 4);// ******

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}
		// gen outer walls 4
		for (int i = (int) (center.getBlockZ() - dimension / 4); i <= (center
				.getBlockZ() + dimension / 4); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setZ(i);
				l.setX(center.getBlockX() + dimension / 4);// ******changed

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}

	}

	public void generateMaze(Location center, int width, int passagewidth) {
		/*
		 * int width = this.getConfig().getInt("width"); int passagewidth =
		 * this.getConfig().getInt("passagewidth"); World world =
		 * Bukkit.getServer().getWorld(this.getConfig().getString("worlname"));
		 * int x = this.getConfig().getInt("locx"); int z =
		 * this.getConfig().getInt("locz"); Location center = new
		 * Location(world,x , 0 , z);
		 */
		Location l = new Location(center.getWorld(), 0, 0, 0);/* 
*/
		// gen outer walls 1
		for (int i = (int) (center.getBlockX() - (width / 4)); i <= (center
				.getBlockX() + (width / 4)); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setX(i);
				l.setZ(center.getBlockZ() - width / 4);// ******

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}
		// gen outer walls 2
		for (int i = (int) (center.getBlockX() - (width / 4)); i <= (center
				.getBlockX() + width / 4); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setX(i);
				l.setZ(center.getBlockZ() + width / 4);// ******changed

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}
		// gen outer walls 3 switch x and z
		for (int i = (int) (center.getBlockZ() - width / 4); i <= (center
				.getBlockZ() + width / 4); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setZ(i);
				l.setX(center.getBlockX() - width / 4);// ******

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}
		// gen outer walls 4
		for (int i = (int) (center.getBlockZ() - width / 4); i <= (center
				.getBlockZ() + width / 4); i++) {
			for (int ii = 0; ii < 128; ii++) {
				l.setY(ii);
				l.setZ(i);
				l.setX(center.getBlockX() + width / 4);// ******changed

				Block b = l.getBlock();
				b.setType(Material.BEDROCK);
				b.getState().update(true);
			}
		}

		Chamber c = new Chamber(width / 2, width / 2, new Location(
				center.getWorld(), center.getX() - (.25 * width), 0,
				center.getZ() - (.25 * width)));
		createWalls(c, passagewidth);

	}

	/*
	 * old generator public static void generateMaze(Location center, int
	 * dimension, int passageWidth, Random r , World world) { Location l = new
	 * Location(center.getWorld(), 0,0,0); center.setX(0); center.setY(0);
	 * center.setZ(0);
	 * 
	 * //gen outer walls 1 for(int i = (int) (center.getBlockX()-(dimension/4));
	 * i <= (center.getBlockX() + (dimension/4)); i++ ){ for(int ii = 0; ii <
	 * 128; ii++){ l.setY(ii); l.setX(i); l.setZ(center.getBlockZ() -
	 * dimension/4);//******
	 * 
	 * Block b = l.getBlock(); b.setType(Material.BEDROCK);
	 * b.getState().update(true); } } //gen outer walls 2 for(int i = (int)
	 * (center.getBlockX()-(dimension/4)); i <= (center.getBlockX() +
	 * dimension/4); i++ ){ for(int ii = 0; ii < 128; ii++){ l.setY(ii);
	 * l.setX(i); l.setZ(center.getBlockZ() + dimension/4);//******changed
	 * 
	 * Block b = l.getBlock(); b.setType(Material.BEDROCK);
	 * b.getState().update(true); } } //gen outer walls 3 switch x and z for(int
	 * i = (int) (center.getBlockZ()-dimension/4); i <= (center.getBlockZ() +
	 * dimension/4); i++ ){ for(int ii = 0; ii < 128; ii++){ l.setY(ii);
	 * l.setZ(i); l.setX(center.getBlockX() - dimension/4);//******
	 * 
	 * Block b = l.getBlock(); b.setType(Material.BEDROCK);
	 * b.getState().update(true); } } //gen outer walls 4 for(int i = (int)
	 * (center.getBlockZ()-dimension/4); i <= (center.getBlockZ() +
	 * dimension/4); i++ ){ for(int ii = 0; ii < 128; ii++){ l.setY(ii);
	 * l.setZ(i); l.setX(center.getBlockX() + dimension/4);//******changed
	 * 
	 * Block b = l.getBlock(); b.setType(Material.BEDROCK);
	 * b.getState().update(true); } }
	 * 
	 * 
	 * 
	 * Chamber c = new Chamber(dimension/2, dimension/2, new
	 * Location(center.getWorld(), -(.25*dimension), 0, -(.25*dimension)));
	 * createWalls(c, passageWidth);
	 * 
	 * }
	 */
	public static int getWalls() {
		return walls;
	}

	private void createWalls(Chamber chamber, int minWidth) {
		String orientation = chamber.getOrientation();
		int poss;
		int hole;
		Location l = new Location(chamber.getBL().getWorld(), 0, 0, 0);
		Random r = new Random();
		if (chamber.getXDimension() <= minWidth
				|| chamber.getZDimension() <= minWidth) {

		} else {

			switch (orientation) {
			case "x":
				// vertical
				poss = r.nextInt((chamber.getXDimension() / minWidth) - 1) + 1;
				hole = r.nextInt((chamber.getZDimension() / minWidth)) + 1;
				// draw wall
				for (int z = chamber.getDown(); z < chamber.getTop(); z++) {
					if ((z > (chamber.getDown() + (hole - 1) * minWidth) && z < (chamber
							.getDown() + (hole) * minWidth)) != true) {
						int poop = r.nextInt(100);
						int pee = r.nextInt(90) + 1;
						for (int y = 0; y < 128; y++) {
							l.setY(y);
							l.setZ(z);
							l.setX(chamber.getLeft() + minWidth * poss);
							if (poop > this.getConfig().getInt("holefreq")
									|| this.getConfig().getInt("holefreq") == -1) {
								l.getBlock().setType(Material.BEDROCK);
							} else {
								if (y == pee || y == (pee + 1)) {
								} else {
									l.getBlock().setType(Material.BEDROCK);
								}

							}
							l.getBlock().getState().update(true);
						}
					}
				}
				int mid = (chamber.getLeft() + (minWidth * poss));
				int right = chamber.getRight();
				int left = chamber.getLeft();
				createWalls(
						new Chamber(right - mid, chamber.getTop()
								- chamber.getDown(), new Location(l.getWorld(),
								mid, 0, chamber.getDown())), 10);
				createWalls(
						new Chamber(mid - left, chamber.getTop()
								- chamber.getDown(), new Location(l.getWorld(),
								left, 0, chamber.getDown())), 10);
				break;
			case "z":
				// horizontal
				poss = r.nextInt((chamber.getZDimension() / minWidth) - 1) + 1;

				hole = r.nextInt((chamber.getXDimension() / minWidth)) + 1;

				for (int x = chamber.getLeft(); x < chamber.getRight(); x++) {
					if ((x > (chamber.getLeft() + (hole - 1) * minWidth) && x < (chamber
							.getLeft() + (hole) * minWidth)) != true) {
						int poop = r.nextInt(100);
						int pee = r.nextInt(90) + 1;

						for (int y = 0; y < 128; y++) {
							l.setY(y);
							l.setX(x);
							l.setZ(chamber.getDown() + minWidth * poss);
							if (poop > this.getConfig().getInt("holefreq")
									|| this.getConfig().getInt("holefreq") == -1) {
								l.getBlock().setType(Material.BEDROCK);
							} else {
								if (y == pee || y == (pee + 1)) {
								} else {
									l.getBlock().setType(Material.BEDROCK);
								}

							}

							l.getBlock().getState().update(true);
						}
					}
				}
				int mid2 = (chamber.getDown() + (minWidth * poss));
				int top = chamber.getTop();
				int down = chamber.getDown();
				createWalls(
						new Chamber(chamber.getRight() - chamber.getLeft(), top
								- mid2, new Location(l.getWorld(),
								chamber.getLeft(), 0, mid2)), 10);
				createWalls(
						new Chamber(chamber.getRight() - chamber.getLeft(),
								mid2 - down, new Location(l.getWorld(),
										chamber.getLeft(), 0, down)), 10);

				break;
			}
		}
	}

	public static boolean getAuto() {

		return auto;
	}

	private void deleteMaze(int width, Location center) {

		Location l = new Location(center.getWorld(), 0, 0, 0);
		Block b = l.getBlock();
		Block[] blocks = new Block[4];// array of surrounding blocks
		for (int x = center.getBlockX() - (width / 2); x <= center.getBlockX()
				+ width / 2; x++) {// x axis
			for (int z = center.getBlockZ() - (width / 2); z <= center
					.getBlockZ() + width / 2; z++) {// z axis
				for (int y = 5; y < 129; y++) {// y axis
					l.setX(x);
					l.setY(y);
					l.setZ(z);
					b = l.getBlock();
					if (b.getType() == Material.BEDROCK) {// if its a maze block

						l.setX(x + 1);
						l.setY(y);
						l.setZ(z);
						blocks[0] = l.getBlock();// set the surrounding blocks
						l.setX(x - 1);
						l.setY(y);
						l.setZ(z);
						blocks[1] = l.getBlock();
						l.setY(y);
						l.setX(x);
						l.setZ(z + 1);
						blocks[2] = l.getBlock();
						l.setY(y);
						l.setX(x);
						l.setZ(z - 1);
						blocks[3] = l.getBlock();
						boolean match = false;
						for (int i = 0; i < 4; i++) {
							if (blocks[i].getType() != Material.BEDROCK) {
								for (int count = i + 1; count < 4; count++) {
									if (blocks[i].getType() == blocks[count]
											.getType()) {

										b.setType(blocks[i].getType());
										match = true;
									}
								}
							}
						}

						if (match == false) {
							for (int i = 0; i < 4; i++) {
								if (blocks[i].getType() != Material.BEDROCK
										|| blocks[i].getType() != Material.BEDROCK) {// test
																						// feature
									b.setType(blocks[i].getType());

									break;
								}
							}
						}
						match = false;
					}

					b.getState().update(true);
				}
			}
		}

	}

}