package org.squadra.atenea.dataloader;

import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

public class ManualDataLoader implements DataLoaderInterface {
	
	public void loadData(String source){
		
		NeuralDataAccess.init();
		
		String[] nodes = { 
			"hola", "Hola, ¿cómo estás?",
			"bien y vos", "Muy bien ¿cuál es tu nombre?",
			"leandro", "Que lindo nombre",
			"lucas", "Un gusto Lucas",
			"facundo", "Que feo nombre",
			"ricardo", "Hola Ricardo",
			"quién sos","Mi nombre es Atenea, un gusto conocerte.",
			"quién eres","Mi nombre es Atenea, un gusto conocerte.",
			"como te llamas","Mi nombre es Atenea.",
			"que edad tienes","Aun soy muy joven, tengo apenas unos meses.",
			"me gusta hablar con vos","Gracias! a mi tambien me gusta hablar con vos."
		};

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
