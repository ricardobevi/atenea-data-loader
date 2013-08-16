package org.squadra.atenea.dataloader.test;

import org.squadra.atenea.dataloader.DataLoaderInterface;
import org.squadra.atenea.dataloader.ManualDataLoader;
import org.squadra.atenea.dataloader.WikipediaBulkLoader;

public class LoadData {
	
	public static void main(String[] args) {
		
		//DataLoaderInterface loder = new ManualDataLoader();
		
		//loder.loadData("http://es.wikipedia.org/wiki/Carabanchel");
		
		//while(true);
		
		WikipediaBulkLoader.run();
		
		System.exit(0);
		
	}
	
}
