package org.squadra.atenea.dataloader;

import org.squadra.atenea.base.word.Word;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

public class ManualDataLoader implements DataLoaderInterface {
	
	public void loadData(String source){
		
		NeuralDataAccess.init();
		
		String[] nodes = { 
			"hola", "Hola, ¿cómo estás?",
			"bien","Me alegro por tí.",
			"bien y tú", "Muy bien ¿cuál es tu nombre?",
			"leandro", "Que lindo nombre.",
			"lucas", "Un gusto Lucas.",
			"facundo", "Que feo nombre.",
			"ricardo", "Hola Ricardo.",
			"quién eres","Mi nombre es Atenea, un gusto conocerte.",
			"cómo te llamas","Mi nombre es Atenea.",
			"qué edad tienes","Aun soy muy joven, tengo apenas unos meses.",
			"me gusta hablar contigo","¡Gracias! a mi también me gusta hablar con vos.",
			"no","Que negativo eres.",
			"si","Me gusta esa respuesta.",
			"gracias","Por nada."
		};

		NodeDefinition nodeDefinition = new NodeDefinition();
		
		nodeDefinition.beginTransaction();

		try {
			
			for (Integer i = 0; i < nodes.length - 1; i++) {
				
				nodeDefinition.relateWords(new Word(nodes[i]), new Word(nodes[i + 1]), i, i);
				
			}

		}

		finally {
			nodeDefinition.endTransaction();
		}
		
		
	}
	
}
