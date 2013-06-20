package org.squadra.atenea.dataloader.test;

import org.squadra.atenea.dataloader.DataLoaderInterface;
import org.squadra.atenea.dataloader.ManualDataLoader;

public class LoadData {
	
	public static void main(String[] args) {
		
		DataLoaderInterface loder = new ManualDataLoader();
		
		loder.loadData("http://es.wikipedia.org/wiki/Carabanchel");
		
		//while(true);
		
		System.exit(0);
		
	}
	
}
