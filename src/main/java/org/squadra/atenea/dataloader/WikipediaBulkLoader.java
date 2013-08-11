package org.squadra.atenea.dataloader;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;

import lombok.extern.log4j.Log4j;

import org.squadra.atenea.base.word.*;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

@Log4j
public class WikipediaBulkLoader {

	public static void run() {
		int from = 0, to = 100;
		long numberSentence = 0;
		ArrayList<String> articles;
		//do {
		{
			articles = loadRange(from, to);
			from = to;
			to += to;

			NeuralDataAccess.init();

			for (String article : articles) {
				String[] sentences = article.split("\\.");
				for (String sentence : sentences) {
					sentence = removeUnnecessaryChars(sentence);
					write(sentence, numberSentence++);
				}
			}
		} //while (!articles.isEmpty());
	}

	private static ArrayList<String> loadRange(Integer from, Integer to) {
		ArrayList<String> allSentences = new ArrayList<String>();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conexion = DriverManager.getConnection(
					"jdbc:mysql://localhost/wiki", "root", "");
			Statement s = conexion.createStatement();
			ResultSet rs = s.executeQuery("select cuerpo from articulo limit "
					+ from + " , " + to);

			while (rs.next()) {
				allSentences.add(rs.getString(1));
			}

			conexion.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return allSentences;
	}

	private static void write(String sentence, long numberSentence) {
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
		return sentence;
	}
}
