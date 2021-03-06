package org.squadra.atenea.dataloader;

import java.util.ArrayList;

import org.squadra.atenea.base.TextFileUtils;
import org.squadra.atenea.base.word.Word;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;
import org.squadra.atenea.parser.Parser;
import org.squadra.atenea.parser.model.Sentence;

public class DialogLoader implements DataLoaderInterface {

	/** 
	 * Poner en true para que parsee las oraciones con la
	 * gramatica de AteneaParser, sino poner en false.
	 */
	private boolean GRAMMAR_PARSE = true;
	
	/** 
	 * Tipo de nodo origen almacenado. Es el nombre del indice
	 * con que se guardara en la base de datos. 
	 */
	private String DIALOG_TYPE = "dialogType";
	
	/**
	 * Es el numero iniciar con que comienza el contador de IDs
	 * para cada oracion insertada.
	 */
	private long FIRST_SENTENCE_ID = 0;
	
	private NodeDefinition nodeDefinition;
	
	
	/**
	 * Constructor por defecto
	 */
	public DialogLoader() {
	}
	
	/**
	 * Constructor parametrizado.
	 * @param grammarParse
	 * @param dialogType
	 * @param firstSentenceId
	 */
	public DialogLoader(boolean grammarParse, String dialogType, long firstSentenceId) {
		this.GRAMMAR_PARSE = grammarParse;
		this.DIALOG_TYPE = dialogType;
		this.FIRST_SENTENCE_ID = firstSentenceId;
	}
	
	
	@Override
	public void loadData(String source) {
		
		// Inicio la base de datos
		NeuralDataAccess.init();
		
		// Inicio la transaccion
		nodeDefinition = new NodeDefinition();
		nodeDefinition.beginTransaction();

		try {
			// Leo el txt con los datos a cargar
			ArrayList<String> content = TextFileUtils.readTextFile(source);
			
			if (GRAMMAR_PARSE) {
				loadParsedWords(content);
			}
			else {
				loadNonParsedWords(content);
			}
			nodeDefinition.transactionSuccess();
		}
		finally {
			nodeDefinition.endTransaction();
		}
	}
	
	
	/**
	 * Carga los datos con los dialogos en la base pasando las
	 * oraciones por la gramatica de AteneaParser. De esta forma
	 * se guarda el objeto Word completo.
	 * @param content
	 */
	private void loadParsedWords(ArrayList<String> content) {
		
		long sentenceId = FIRST_SENTENCE_ID;
		String dialogTypeNode = new String();
		
		for (String line : content) {	
			
			// Salteo las lineas vacias o con el numeral (comentario)
			if (!line.equals("") && !line.equals(" ") 
					&& line.charAt(0) != '#' && line.charAt(0) != 0xFEFF) {
				
				// Si es un tipo de dialogo
				if (line.charAt(0) == '$') {
					dialogTypeNode = line.toString();
					System.out.println("\n$$$$ tipo: " + dialogTypeNode);
				}
				
				// Si es una frase de un tipo de dialogo
				else {
					sentenceId++;
					
					// Obtengo la frase
					String[] dialogResponse = line.split("=");
					
					Sentence sentence = new Parser().parse(dialogResponse[0].trim());
					ArrayList<Word> dialogResponseWords = sentence.getAllWords(true);
					
					Integer[] dialogResponseProb = {10,10,10,10,10};
					
					// Obtengo las ponderaciones
					if (dialogResponse.length > 1) {
						String[] dialogResponseProbStr = 
								dialogResponse[1].trim().replaceAll("\\s+", " ").split(" ");
						
						if (dialogResponseProbStr.length == 5) {
							for (int i = 0; i < 5; i++) {
								dialogResponseProb[i] = Integer.parseInt(dialogResponseProbStr[i]);
							}
						}
					}
					
					if (dialogResponseWords.size() > 0) {
						// Relaciono la primera palabra de la respuesta con el tipo
						nodeDefinition.relateTypeOfDialogWords(
								new Word(dialogTypeNode), 
								dialogResponseWords.get(0), 
								sentenceId, 0, dialogResponseProb, 
								DIALOG_TYPE);
						
						// Relaciono el resto de las palabras de la respuesta
						for (int i = 0; i < dialogResponseWords.size() - 1; i++) {
							nodeDefinition.relateWords(
									dialogResponseWords.get(i), 
									dialogResponseWords.get(i+1), 
									sentenceId, i + 1);
						}
					}
					
					System.out.println("- " + line);
				}			
				
			}
		}
	}
	
	
	/**
	 * Carga los datos con los dialogos en la base sin pasar 
	 * las oraciones por la gramatica.
	 * @param content
	 */
	private void loadNonParsedWords(ArrayList<String> content) {
		
		long sentenceId = FIRST_SENTENCE_ID;
		String dialogTypeNode = new String();
		
		for (String line : content) {	
			
			// Salteo las lineas vacias o con el numeral (comentario)
			if (!line.equals("") && !line.equals(" ") 
					&& line.charAt(0) != '#' && line.charAt(0) != 0xFEFF) {
				
				// Si es un tipo de dialogo
				if (line.charAt(0) == '$') {
					dialogTypeNode = line.toString();
					System.out.println("\n$$$$ tipo: " + dialogTypeNode);
				}
				
				// Si es una frase a un tipo de dialogo
				else {
					sentenceId++;
					
					// Obtengo las palabras de la frase
					String[] dialogResponse = line.split("=");
					
					String[] dialogResponseWords = dialogResponse[0].trim().split(" ");
					
					Integer[] dialogResponseProb = {10,10,10,10,10};
					
					// Obtengo las ponderaciones
					if (dialogResponse.length > 1) {
						String[] dialogResponseProbStr = 
								dialogResponse[1].trim().replaceAll("\\s+", " ").split(" ");
						
						if (dialogResponseProbStr.length == 5) {
							for (int i = 0; i < 5; i++) {
								dialogResponseProb[i] = Integer.parseInt(dialogResponseProbStr[i]);
							}
						}
					}
					
					// Relaciono la primera palabra de la respuesta con el tipo
					nodeDefinition.relateTypeOfDialogWords(
							new Word(dialogTypeNode, ""), 
							new Word(dialogResponseWords[0], ""), 
							sentenceId, 0, dialogResponseProb,
							DIALOG_TYPE);
					
					// Relaciono el resto de las palabras de la respuesta
					for (int i = 0; i < dialogResponseWords.length - 1; i++) {
						nodeDefinition.relateWords(
								new Word(dialogResponseWords[i], ""), 
								new Word(dialogResponseWords[i+1], ""), 
								sentenceId, i + 1);
					}
					
					System.out.println("- " + line);
				}			
				
			}
		}
	}

}
