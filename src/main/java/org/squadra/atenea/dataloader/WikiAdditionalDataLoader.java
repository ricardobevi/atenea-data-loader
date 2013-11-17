package org.squadra.atenea.dataloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import lombok.extern.log4j.Log4j;

import org.squadra.atenea.base.word.Word;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;
import org.squadra.atenea.parser.Parser;
import org.squadra.atenea.parser.model.Sentence;
import org.squadra.atenea.parser.model.SimpleSentence;

@Log4j
public class WikiAdditionalDataLoader implements DataLoaderInterface {

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
	public WikiAdditionalDataLoader(long firstSentenceId) {
		this.FIRST_SENTENCE_ID = firstSentenceId;
	}
	
	/**
	 * Constructor parametrizado.
	 * @param grammarParse
	 * @param dialogType
	 * @param firstSentenceId
	 */
	public WikiAdditionalDataLoader(boolean grammarParse, long firstSentenceId) {
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
		ArrayList< HashMap<String, String> > rows = null;
		
		do {

			//Cargo registros
			rows = loadRange(source, from, diff);
			from += diff;

			nodeDefinition.beginTransaction();

			String actualSentence = null;
			try
			{
				log.debug("Escribiendo hasta el registro " + from + "...");

				for (HashMap<String, String> row : rows) {
					
					String title = removeUnnecessaryChars(row.get("title")).trim();
					String subtitle = removeUnnecessaryChars(row.get("subtitle")).trim();
					String body = removeUnnecessaryChars(row.get("body")).trim();
					
					//Escribo en la base de datos
					write(title, subtitle, body, numberSentence++);
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
			
		} while (!rows.isEmpty());

		//NeuralDataAccess.stop();
	}

	
	private ArrayList<HashMap<String, String>> loadRange(String query, Integer from, Integer size) {
		
		ArrayList< HashMap<String, String> > allRegs = new ArrayList< HashMap<String, String> >();
		try {
			Class.forName("org.postgresql.Driver");
			Connection conexion = DriverManager.getConnection(
					"jdbc:postgresql://bartgentoo.no-ip.org:5432/wiki", "wiki", "wiki1621");
			Statement s = conexion.createStatement();

			log.debug("Leyendo de la base de datos...");

			ResultSet rs = s.executeQuery(
					query + " LIMIT " + size + " OFFSET " + from);

			while (rs.next()) {
				HashMap<String, String> reg = new HashMap<>();
				reg.put("title", rs.getString(1));
				reg.put("subtitle", rs.getString(2));
				reg.put("body", rs.getString(3));
				allRegs.add(reg);
			}
			conexion.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("Fin de Lectura");
		
		return allRegs;
	}

	
	private void write(String title, String subtitle, String body, long numberSentence) {

		//Agrego un punto al final de la oracion
		body += " .";
		
		ArrayList<Word> results = new ArrayList<Word>();
		
		if (GRAMMAR_PARSE) {
			
			// Parseo la oracion con la gramatica
			System.out.println("Parsing: " + title + " - " + subtitle + " - " + body);
			Sentence parsedSentence = new Parser().parse(body);
			results = parsedSentence.getAllWords(true);
			System.out.println("Parsed:  " + title + " - " + subtitle + " - " + new SimpleSentence(results).toString());
		}
		
		else {
			
			//Separo las comas para que queden como una palabra
			body = body.replaceAll("\\,\\ ", "\\ \\,\\ ");

			//Separo las palabras de la oracion
			String[] words = body.split("\\ ");

			results = new ArrayList<Word>();
			
			for (String word : words) {
				Word w = new Word(word);
				results.add(w);
			}
			
		}

		try {
			// Relaciono el titulo con el subtitulo
			nodeDefinition.relateWikiInfoWords(
					new Word(title), new Word(subtitle), numberSentence, 0, 100);
			
			// Relaciono el subtitulo con la primera palabra de la oracion
			nodeDefinition.relateWikiInfoWords(
					new Word(subtitle), results.get(0), numberSentence, 1, 100);
			System.out.println(subtitle + " -> " + results.get(0).getName());
			
			// Relaciono las palabras de la oracion del body
			for (int i = 0; i < results.size() - 1; i++) {
				System.out.println(results.get(i).getName() + " -> " + results.get(i + 1).getName());
				nodeDefinition.relateWikiInfoWords(results.get(i), results.get(i + 1),
						numberSentence, i + 2, 100);
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
