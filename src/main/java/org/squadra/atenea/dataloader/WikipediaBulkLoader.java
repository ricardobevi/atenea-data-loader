package org.squadra.atenea.dataloader;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.lang.StringEscapeUtils;
import org.squadra.atenea.base.word.*;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

//@Log4j
public class WikipediaBulkLoader {

	static private NodeDefinition nodeDefinition = new NodeDefinition();

	public static void run() {
		//Cargo de a 1000 registros de mysql
		int from = 0, diff = 1000;
		long numberSentence = 0;
		ArrayList<String> articles = null;

		NeuralDataAccess.init();
		do {

			//Cargo registros
			articles = loadRange(from, diff);
			from += diff;

			nodeDefinition.beginTransaction();			
			System.out.println("Escribiendo hasta el registro " + from);

			String actualSentence = null;
			try
			{
				System.out.println("Escribiendo");

				for (String article : articles) {
					if (article != null)
					{
						//Separo las oraciones del articulo
						String[] sentences = article.split("\\.");
						for (String sentence : sentences) {

							actualSentence = article;
							sentence = removeUnnecessaryChars(sentence).trim();
							sentence = StringEscapeUtils.unescapeHtml(sentence);
							//Escribo en neo4j
							write(sentence, numberSentence++);
						}
					}
				}

				System.out.println("Fin de Escritura");
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

	private static ArrayList<String> loadRange(Integer from, Integer size) {
		ArrayList<String> allSentences = new ArrayList<String>();
		try {
			Class.forName("org.postgresql.Driver");
			Connection conexion = DriverManager.getConnection(
					"jdbc:postgresql://bartgentoo.no-ip.org:5432/wiki","wiki", "wiki1621");
			Statement s = conexion.createStatement();

			System.out.println("Leyendo de la base de datos.");

			ResultSet rs = s.executeQuery(
					"select cuerpo from articulo where titulo = 'José de San Martín' "
					+ " limit " + size + " OFFSET " + from);


			while (rs.next()) {
				allSentences.add(rs.getString(1));
			}
			conexion.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Fin de Lectura");
		return allSentences;
	}

	private static void write(String sentence, long numberSentence) {

		//Separo las comas para que queden como una palabra
		sentence = sentence.replaceAll("\\,\\ ", "\\ \\,\\ ");

		//Separo las palabras de la oracion
		String[] words = sentence.split("\\ ");
		ArrayList<Word> results = new ArrayList<Word>();

		for (String word : words) {
			Word w = new Word(word, "");
			// WordClassifier classifier = new WordClassifier();
			// results.add(classifier.classifyWord(word));
			results.add(w);
		}

		// escribir	
		try {
			//Relaciono las palabras
			Integer i = 0;
			for (; i < results.size() - 1; i++) {
				nodeDefinition.relateWords(results.get(i), results.get(i + 1),
						numberSentence, i);
			}

			// Relaciono la ultima palabra con "."
			nodeDefinition.relateWords(results.get(results.size() - 1),
					new Word(".", ""),
					numberSentence, ++i);
		}
		catch (Exception e) {
			nodeDefinition.endTransaction();
			e.printStackTrace();
		}

	}

	private static String removeUnnecessaryChars(String sentence) {
		sentence = sentence.replaceAll("[\\(\\);:\\-\\/!?¡¿\\\"]", "");
		sentence = sentence.replaceAll("\\<.*?>", "");
		sentence = sentence.replaceAll("\\\\", "");
		return sentence;
	}
}
