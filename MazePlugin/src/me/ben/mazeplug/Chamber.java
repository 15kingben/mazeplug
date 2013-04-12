package me.ben.mazeplug;

import java.util.Random;

import org.bukkit.Location;

public class Chamber {
	private int xDimension;
	private int zDimension;
	private Location center;
	private Random r = new Random();
	
	public Chamber(int xDimension, int zDimension, Location center){
		this.xDimension = xDimension;
		this.center = center;
		this.zDimension = zDimension;
	}
	public int getXDimension(){
		return xDimension;
	}
	public int getZDimension(){
		return zDimension;
	}
	public Location getCenter(){
		return center;
	}
	public int getTop(){
		return center.getBlockZ() + (int)(.5 * getZDimension());
	}
	public int getDown(){
		return center.getBlockZ() - (int)(.5 * getZDimension());
	}
	public int getRight(){
		return center.getBlockX() + (int)(.5 * getZDimension());
	}
	public int getLeft(){
		return center.getBlockX() - (int)(.5 * getZDimension());
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
