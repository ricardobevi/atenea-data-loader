package org.squadra.atenea.dataloader;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang.StringEscapeUtils;
import org.squadra.atenea.base.word.*;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;
import org.squadra.atenea.parser.Parser;
import org.squadra.atenea.parser.model.Sentence;
import org.squadra.atenea.parser.model.SimpleSentence;

@Log4j
public class WikipediaBulkLoader implements DataLoaderInterface {

	/** 
	 * Poner en true para que parsee las oraciones con la
	 * gramatica de AteneaParser, sino poner en false.
	 */
	private boolean GRAMMAR_PARSE = false;
	
	/**
	 * Es el numero iniciar con que comienza el contador de IDs
	 * para cada oracion insertada.
	 */
	private long FIRST_SENTENCE_ID = 0;
	
	private NodeDefinition nodeDefinition;
	
	
	/**
	 * Constructor parametrizado.
	 * @param firstSentenceId
	 */
	public WikipediaBulkLoader(long firstSentenceId) {
		this.FIRST_SENTENCE_ID = firstSentenceId;
	}
	
	/**
	 * Constructor parametrizado.
	 * @param grammarParse
	 * @param dialogType
	 * @param firstSentenceId
	 */
	public WikipediaBulkLoader(boolean grammarParse, long firstSentenceId) {
		this.GRAMMAR_PARSE = grammarParse;
		this.FIRST_SENTENCE_ID = firstSentenceId;
	}
	

	@Override
	public void loadData(String source) {

		// Inicio la base de datos
		NeuralDataAccess.init();
		
		// Inicio la transaccion
		nodeDefinition = new NodeDefinition();
		
		//Cargo de a 1000 registros de mysql
		int from = 0, diff = 1000;
		long numberSentence = FIRST_SENTENCE_ID;
		ArrayList<String> articles = null;
		
		do {

			//Cargo registros
			articles = loadRange(source, from, diff);
			from += diff;

			nodeDefinition.beginTransaction();

			String actualSentence = null;
			try
			{
				log.debug("Escribiendo hasta el registro " + from + "...");

				for (String article : articles) {
					if (article != null)
					{
						//Separo las oraciones del articulo
						String[] sentences = article.split("\\. ");
						for (String sentence : sentences) {

							actualSentence = article;
							
							//Elimino las basofias que dejo Lucas mal parseadas 							
							sentence = removeUnnecessaryChars(sentence).trim();
							sentence = StringEscapeUtils.unescapeHtml(sentence);
							
							//Escribo en la base de datos
							write(sentence, numberSentence++);
						}
					}
				}

				log.debug("Fin de Escritura.");
			}
			catch(Exception ex)
			{
				nodeDefinition.endTransaction();
				System.out.println(actualSentence);
				ex.printStackTrace();
			}
			nodeDefinition.endTransaction();
			
		} while (!articles.isEmpty());

		//NeuralDataAccess.stop();
	}

	
	private ArrayList<String> loadRange(String query, Integer from, Integer size) {
		ArrayList<String> allSentences = new ArrayList<String>();
		try {
			Class.forName("org.postgresql.Driver");
			Connection conexion = DriverManager.getConnection(
					"jdbc:postgresql://bartgentoo.no-ip.org:5432/wiki", "wiki", "wiki1621");
			Statement s = conexion.createStatement();

			log.debug("Leyendo de la base de datos...");

			ResultSet rs = s.executeQuery(
					query + " LIMIT " + size + " OFFSET " + from);

			while (rs.next()) {
				allSentences.add(rs.getString(1));
			}
			conexion.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("Fin de Lectura");
		
		return allSentences;
	}

	
	private void write(String sentence, long numberSentence) {

		//Agrego un punto al final de la oracion
		sentence += " .";
		
		ArrayList<Word> results = new ArrayList<Word>();
		
		if (GRAMMAR_PARSE) {
			
			// Parseo la oracion con la gramatica
			System.out.println("Parsing: " + sentence);
			Sentence parsedSentence = new Parser().parse(sentence);
			results = parsedSentence.getAllWords(true);
			System.out.println("Parsed:  " + new SimpleSentence(results).toString());
		}
		
		else {
			
			//Separo las comas para que queden como una palabra
			sentence = sentence.replaceAll("\\,\\ ", "\\ \\,\\ ");

			//Separo las palabras de la oracion
			String[] words = sentence.split("\\ ");

			results = new ArrayList<Word>();
			
			for (String word : words) {
				Word w = new Word(word);
				// WordClassifier classifier = new WordClassifier();
				// results.add(classifier.classifyWord(word));
				results.add(w);
			}
			
		}
		
		// escribir	
		try {
			//Relaciono las palabras
			Integer i = 0;
			for (; i < results.size() - 1; i++) {
				nodeDefinition.relateWords(results.get(i), results.get(i + 1),
						numberSentence, i);
			}
		}
		catch (Exception e) {
			nodeDefinition.endTransaction();
			e.printStackTrace();
		}

	}

	
	private static String removeUnnecessaryChars(String sentence) {
		sentence = sentence.replaceAll("http:.*? ","");
		sentence = sentence.replaceAll("\\<.*?\\>", "");
		//sentence = sentence.replaceAll("[\\.\\(\\);:\\-\\/\\«\\»\\'!?¡¿\\\"]", "");
		sentence = sentence.replaceAll("[\\.;:\\-]", " ");
		sentence = sentence.replaceAll("[\\«\\»\\'’`¡¿\\\"]", "");
		sentence = sentence.replaceAll("\\\\", "");
		return sentence;
	}
	
}
