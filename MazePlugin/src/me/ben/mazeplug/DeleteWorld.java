package me.ben.mazeplug;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class DeleteWorld implements Runnable{
	World w;
	public DeleteWorld(World wg){
		this.w = wg;
		
	}
	@Override
	public void run() {
	   Bukkit.getServer().unloadWorld(w, false);
	   Bukkit.broadcastMessage("World was Deleted");
	   try {
		delete(w.getWorldFolder());
		File[] files = w.getWorldFolder().listFiles();
		files[0].delete();
		files[1].delete();
		files[2].delete();
		w.getWorldFolder().delete();
		Bukkit.getServer().shutdown();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	
	private void delete(File f) throws IOException {
		  if (f.isDirectory()) {
			  if(f.getName() == "Players" || f.getName() == "region"){
				  File[] files = f.listFiles();
				  for(int i = 0; i <= files.length; i++){
					  files[i].delete();
				  }
				  
			  }
		    for (File c : f.listFiles())
		      delete(c);
		  }
		  else{
			  Bukkit.broadcastMessage("Deleting " + f.toString());
			  f.delete();	
			  }
		}
}
