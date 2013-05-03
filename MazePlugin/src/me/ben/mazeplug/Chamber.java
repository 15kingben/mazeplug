package me.ben.mazeplug;

import java.util.Random;

import org.bukkit.Location;

public class Chamber {
	private int xDimension;
	private int zDimension;
	private Location bottomLeft;
	private Random r = new Random();
	
	public Chamber(int xDimension, int zDimension, Location bottomLeft){
		this.xDimension = xDimension;
		this.bottomLeft = bottomLeft;
		this.zDimension = zDimension;
	}
	public int getXDimension(){
		return xDimension;
	}
	public int getZDimension(){
		return zDimension;
	}
	public Location getBL(){
		return bottomLeft;
	}
	public int getTop(){
		return bottomLeft.getBlockZ() + zDimension;
	}
	public int getDown(){
		return bottomLeft.getBlockZ();
	}
	public int getRight(){
		return bottomLeft.getBlockX() + xDimension;
	}
	public int getLeft(){
		return bottomLeft.getBlockX();
	}
	public String getOrientation() {
		int g = r.nextInt(2);
		if(xDimension > zDimension){
			return "x";
		}else if(zDimension > xDimension){
			return "z";
		}else{
			if(g == 0){
				return "x";
			}else{
				return "z";
			}
		}
	}
}
