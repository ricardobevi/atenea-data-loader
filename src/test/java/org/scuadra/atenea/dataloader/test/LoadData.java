package org.scuadra.atenea.dataloader.test;

import org.squadra.atenea.dataloader.DataLoaderInterface;
import org.squadra.atenea.dataloader.ManualDataLoader;

public class LoadData {
	
	public static void main(String[] args) {
		
		DataLoaderInterface manualLoder = new ManualDataLoader();
		
		manualLoder.loadData("");
		
		while(true);
		
	}
	
}
