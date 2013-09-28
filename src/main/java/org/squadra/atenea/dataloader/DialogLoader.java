package org.squadra.atenea.dataloader;

import java.util.ArrayList;

import org.squadra.atenea.base.TextFileUtils;
import org.squadra.atenea.base.word.Word;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

public class DialogLoader implements DataLoaderInterface {

	@Override
	public void loadData(String source) {
		
		NeuralDataAccess.init();
		
		NodeDefinition nodeDefinition = new NodeDefinition();
		nodeDefinition.beginTransaction();

		try {
			
			ArrayList<String> content = TextFileUtils.readTextFile(source);
			int sentenceId = 0;
			
			for (String line : content) {
				
				String dialogTypeNode = new String();
				
				// Salteo las lineas vacias o con el numeral (comentario)
				if (!line.equals("") && !line.equals(" ") 
						&& line.charAt(0) != '#' && line.charAt(0) != 0xFEFF) {
					
					// Si es un tipo de dialogo
					if (line.charAt(0) == '$') {
						dialogTypeNode = line;
					}
					
					// Si es una respuesta a un tipo de dialogo
					else {
						sentenceId++;
						
						String[] dialogResponse = line.split("=");
						String[] dialogResponseWords = dialogResponse[0].split(" ");
						
						// TODO: splitear las comas
						
						if (dialogResponse.length > 1) {
							String[] dialogResponseProb = dialogResponse[1].split(" ");
						}
						
						// TODO: relacionar dialogTypeNode con dialogResponseText[0]
						//       con un nuevo tipo de relacion
						
						for (int i = 0; i < dialogResponseWords.length - 1; i++) {
							nodeDefinition.relateDialogWords(
									new Word(dialogResponseWords[i], ""), 
									new Word(dialogResponseWords[i+1], ""), 
									"D" + sentenceId, i + 1);
						}
						
					}			
					
					System.out.println("- " + line);
				}
			}
			
			nodeDefinition.transactionSuccess();
		}
		finally {
			nodeDefinition.endTransaction();
		}
	}

}
