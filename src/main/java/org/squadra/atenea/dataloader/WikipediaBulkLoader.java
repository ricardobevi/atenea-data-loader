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

@Log4j
public class WikipediaBulkLoader {
	
	public static void run() {
		//Cargo de a 100000 registros de mysql
		int from = 0, diff = 100000;
		long numberSentence = 0;
		ArrayList<String> articles = null;
		NeuralDataAccess.init();
		do {

			//Cargo registros
			articles = loadRange(from, diff);
			from += diff;
			
			String actualSentence = null;
			try{
				System.out.println("Escribiendo");
				for (String article : articles) {
					//Separo las oraciones del articulo
					String[] sentences = article.split("\\.");
					for (String sentence : sentences) {
						actualSentence = sentence;
						sentence = removeUnnecessaryChars(sentence);
						sentence = StringEscapeUtils.unescapeHtml(sentence);
						//Escribo en neo4j
						write(sentence.trim(), numberSentence++);
					}
				}
				System.out.println("Fin de Escritura");
			}catch(Exception ex)
			{
				System.out.println(actualSentence);
				ex.printStackTrace();
			}
			System.out.println("" + from + " REGISTROS");
		} while (!articles.isEmpty());
		NeuralDataAccess.stop();
	}

	private static ArrayList<String> loadRange(Integer from, Integer size) {
		ArrayList<String> allSentences = new ArrayList<String>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection(
					"jdbc:mysql://localhost/wiki", "root", "");
			Statement s = conexion.createStatement();

			System.out.println("Leyendo de la base de datos.");

			ResultSet rs = s.executeQuery("select cuerpo from articulo limit "
					+ from + " , " + size);

			
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
		//Separo las palabras de la oracion
		String[] words = sentence.split("\\ ");
		ArrayList<Word> results = new ArrayList<Word>();

		for (String word : words) {
			Word w = new Word(word, "", "", "", "", "", "", "", "", true);
			// WordClassifier classifier = new WordClassifier();
			// results.add(classifier.classifyWord(word));
			results.add(w);
		}

		// escribir
		NodeDefinition nodeDefinition = new NodeDefinition();
		nodeDefinition.beginTransaction();
		
		try {

			//Relaciono las palabras
			Integer i = 0;
			for (; i < results.size() - 1; i++) {
				nodeDefinition.relateWords(results.get(i), results.get(i + 1),
						numberSentence, i);
			}

			// Relaciono la ultima palabra con "."
			nodeDefinition.relateWords(results.get(results.size() - 1),
					new Word(".", "", "", "", "", "", "", "", "", true),
					numberSentence, ++i);
		}
		catch (Exception e) {
			nodeDefinition.endTransaction();

		} finally {
			nodeDefinition.endTransaction();
		}
		

	}

	private static String removeUnnecessaryChars(String sentence) {
		sentence = sentence.replaceAll("[,\\(\\);:\\-\\/!?¡¿]", "");
		sentence = sentence.replaceAll("\\<.*?>", "");
		sentence = sentence.replaceAll("\\\\", "");
		return sentence;
	}
}
