package org.squadra.atenea.dataloader;

import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

public class ManualDataLoader implements DataLoaderInterface {
	
	public void loadData(String source){
		
		NeuralDataAccess.init();
		
		String[] nodes = { 
			"hola", "Hola, �c�mo est�s?",
			"bien","Me alegro por t�.",
			"bien y t�", "Muy bien �cu�l es tu nombre?",
			"leandro", "Que lindo nombre.",
			"lucas", "Un gusto Lucas.",
			"facundo", "Que feo nombre.",
			"ricardo", "Hola Ricardo.",
			"qui�n eres","Mi nombre es Atenea, un gusto conocerte.",
			"c�mo te llamas","Mi nombre es Atenea.",
			"qu� edad tienes","Aun soy muy joven, tengo apenas unos meses.",
			"me gusta hablar contigo","�Gracias! a mi tambien me gusta hablar con vos.",
			"no","Que negativo eres.",
			"si","Me gusta esa respuesta.",
			"gracias","Por nada.",
			"abrir panel de control", "Entendido",
			"abrir bloc de notas", "Entendido",
			"cerrar bloc de notas", "Entendido",
			"abrir administrador de tareas", "Entendido",
			"qu� hora es", "Son las "
		};

		NodeDefinition nodeDefinition = new NodeDefinition();
		
		nodeDefinition.beginTransaction();

		try {
			
			for (Integer i = 0; i < nodes.length - 1; i++) {
				
				//nodeDefinition.relateWords(nodes[i], nodes[i + 1]);
				
			}

		}

		finally {
			nodeDefinition.endTransaction();
		}
		
		
	}
	
}
