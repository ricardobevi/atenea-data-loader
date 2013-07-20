package org.squadra.atenea.dataloader.main;

import org.squadra.atenea.dataloader.ManualDataLoader;
import org.squadra.atenea.dataloader.WikiDataLoader;

public class Main {

	public static void main(String args[]) {
		
		ManualDataLoader dataLoader = new ManualDataLoader();
		
		dataLoader.loadData("http://es.wikipedia.org/wiki/Los_Simpson");
		
		while(true);
		
		//System.exit(0);		
	}
	
}
