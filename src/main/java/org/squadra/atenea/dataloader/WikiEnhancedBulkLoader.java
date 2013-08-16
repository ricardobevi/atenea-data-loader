package org.squadra.atenea.dataloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.squadra.atenea.base.word.Word;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;

public class WikiEnhancedBulkLoader {
	static private NodeDefinition nodeDefinition = new NodeDefinition();

	static private Connection conexion;
	static private Statement statement;

	static private HashSet<String> unwantedChars;

	public static void run() {
		Integer from = 0;
		Integer	diff = 10000;
		Long sentenceNumber = new Long(0);
		
		ArrayList<String> articles = null;

		unwantedChars = new HashSet<String>();

		unwantedChars.add(">");
		unwantedChars.add("<");
		unwantedChars.add(",");
		unwantedChars.add(" ");
		unwantedChars.add("\n");

		try {
			System.out.println("Conectando a la BD");
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection(
					"jdbc:mysql://192.168.1.8/wiki", "root", "ric7115");
			statement = conexion.createStatement();

		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NeuralDataAccess.init();

		do {

			// Cargo registros
			articles = loadRange(from, diff);				
			from += diff;

			nodeDefinition.beginTransaction();
			System.out.println("Escribiendo hasta el registro " + from);

			String actualArticle = null;

			try {
				System.out.println("Escribiendo");
				
				Iterator<String> articlesIt = articles.iterator();

				while ( articlesIt.hasNext()  ) {

					actualArticle = articlesIt.next();

					if ( actualArticle != null && !actualArticle.equals("")) {

						Integer i = 0;
						Integer ArtLen = actualArticle.length();
						
						String word1 = "";
						String word2 = "";
						
						while (i < ArtLen) {

							// Salteo los caracteres que no me interesen
							while ( i < ArtLen
									&& unwantedChars.contains( actualArticle.substring(i, i + 1) ) ) {

								// Si estoy ante un <tag>, avanzo hasta encontrar un '>'
								
								if (i < ArtLen
										&& actualArticle.charAt(i) == '<') {

									while (i < ArtLen
											&& actualArticle.charAt(i) != '>') {
										i++;
									}

									// sumo 1 para estar en el proximo caracter fuera del tag
									i++;
								}

								i++;
							}
							
							//Acá ya estoy listo para leer caracteres que si me interesan.
							//muy probablemente ya sea una palabra.
							String word = "";
							while ( i < ArtLen
									&& !unwantedChars.contains( actualArticle.substring(i, i + 1) ) ) {
								
								word += actualArticle.charAt(i);
									
								i++;
							}
							
							if( word1.equals("") )
								word1 = word;
							else if (word2.equals(""))
								word2 = word;
							else {
								
								Word wordObject1 = new Word(word1, "", "", "", "", "", "", "", "", true);
								Word wordObject2 = new Word(word2, "", "", "", "", "", "", "", "", true);
								
								nodeDefinition.relateWords(
										wordObject1, 
										wordObject2,
										0,
										0);
								
								word1 = word2 = "";
							}
								

						}

					}

				}

				System.out.println("Fin de Escritura");

			} catch (Exception ex) {

				nodeDefinition.endTransaction();
				System.out.println(actualArticle);
				ex.printStackTrace();

			}

			nodeDefinition.endTransaction();

		} while (!articles.isEmpty());


		NeuralDataAccess.stop();

		try {
			conexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static ArrayList<String> loadRange(Integer from, Integer size) {

		ArrayList<String> sentenceRange = new ArrayList<String>();
		
		System.out.println("Leyendo de la base de datos.");

		ResultSet rs = null;

		try {

			rs = statement
					.executeQuery("select cuerpo from articulo where subtitulo is null limit "
							+ from + " , " + size);
			
			while (rs.next()) {
				sentenceRange.add(rs.getString(1));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Fin de Lectura");

		return sentenceRange;

	}
}
