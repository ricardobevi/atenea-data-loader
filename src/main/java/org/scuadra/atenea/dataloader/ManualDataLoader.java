package org.scuadra.atenea.dataloader;

import org.atenea.data.definition.NodeDefinition;
import org.atenea.data.server.NeuralDataAccess;

public class ManualDataLoader implements DataLoaderInterface {
	
	public void loadData(String source){
		
		NeuralDataAccess.init();
		
		String[] nodes = { 
				"hola", "Hola, como estas?",
				"quien sos", "Mi nombre es Atenea. No soy muy famosa ahora, pero dentro de muy poco me conocera todo el mundo."};

		NodeDefinition nodeDefinition = new NodeDefinition();
		
		nodeDefinition.beginTransaction();

		try {
			
			for (Integer i = 0; i < nodes.length - i; i++) {
				
				nodeDefinition.relateWords(nodes[i], nodes[i + 1]);
				
			}

		}

		finally {
			nodeDefinition.endTransaction();
		}
		
		
	}
	
}
