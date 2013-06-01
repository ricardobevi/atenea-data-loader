package org.scuadra.atenea.dataloader.test;

import org.scuadra.atenea.dataloader.DataLoaderInterface;
import org.scuadra.atenea.dataloader.ManualDataLoader;

public class LoadData {
	
	public static void main(String[] args) {
		
		DataLoaderInterface manualLoder = new ManualDataLoader();
		
		manualLoder.loadData("");
		
		while(true);
		
	}
	
}
