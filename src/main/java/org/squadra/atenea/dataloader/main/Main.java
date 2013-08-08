package org.squadra.atenea.dataloader.main;

import org.squadra.atenea.dataloader.ManualDataLoader;

public class Main {

	public static void main(String args[]) {
		
		ManualDataLoader dataLoader = new ManualDataLoader();
		
		dataLoader.loadData("http://es.wikipedia.org/wiki/Los_Simpson");
		
		while(true);
		
		//System.exit(0);		
	}
	
}
