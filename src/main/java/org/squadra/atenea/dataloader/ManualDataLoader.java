package org.squadra.atenea.dataloader;

import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

public class ManualDataLoader implements DataLoaderInterface {
	
	public void loadData(String source){
		
		NeuralDataAccess.init();
		
		String[] nodes = { "hola", "Hola, como estas?",
				           "quien sos","Mi nombre es Atenea, un gusto conocerte.",
				           "quién eres","Mi nombre es Atenea, un gusto conocerte.",
				           "cómo te llamas","Mi nombre es Atenea.",
				           "qué edad tienes","Aun soy muy joven, tengo apenas unos meses.",
				           "qué edad tiene","Aun soy muy joven, tengo apenas unos meses.",
				           "me gusta hablar con vos","Oh, gracias! a mi tambien me gusta hablar con vos."};

		NodeDefinition nodeDefinition = new NodeDefinition();
		
		nodeDefinition.beginTransaction();

		try {
			
			for (Integer i = 0; i < nodes.length - 1; i++) {
				
				nodeDefinition.relateWords(nodes[i], nodes[i + 1]);
				
			}

		}

		finally {
			nodeDefinition.endTransaction();
		}
		
		
	}
	
}
