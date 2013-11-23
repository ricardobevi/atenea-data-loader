package org.squadra.atenea.dataloader;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import lombok.extern.log4j.Log4j;

import org.squadra.atenea.base.word.Word;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;
import org.squadra.atenea.parser.Parser;
import org.squadra.atenea.parser.model.Sentence;


@Log4j
public class WikiConceptLoader implements DataLoaderInterface {

	private NodeDefinition nodeDefinition;
	
	
	/**
	 * Constructor por defecto.
	 */
	public WikiConceptLoader() {
	}
	
	@Override
	public void loadData(String source) {

		// Inicio la base de datos
		NeuralDataAccess.init();
		
		// Inicio la transaccion
		nodeDefinition = new NodeDefinition();
		
		//Cargo de a 1000 registros de la base
		int from = 0, diff = 1000;
		ArrayList<Article> articles = null;
		
		do {

			//Cargo registros
			articles = loadRange(source, from, diff);
			from += diff;

			nodeDefinition.beginTransaction();

			String actualSentence = null;
			try
			{
				log.debug("Escribiendo hasta el registro " + from + "...");

				Parser parser = new Parser();
				ArrayList<Word> previousWords = new ArrayList<Word>();
				
				for (Article article : articles) {
					
					if (article != null)
					{
						try{
							
							Sentence articleNameSentence = parser.parse( " " + article.getTitle() );
							Word articleName = articleNameSentence.getNouns().get(0);
							
							Sentence paragraphNameSentence = parser.parse( " " + article.getSubtitle() );
							Word paragraphName = paragraphNameSentence.getNouns().get(0);
							
							
							Sentence parsedSentence = parser.parse(article.getBody());
							
							ArrayList<Word> nounsAndVerbs = parsedSentence.getNouns();
							nounsAndVerbs.addAll(parsedSentence.getMainVerbs());
						
							for (Word word : nounsAndVerbs) {
	
								if(previousWords.size() > 0){
									for(Word previousWord : previousWords){
										nodeDefinition.relateWords(previousWord, word);
										nodeDefinition.relateWords(word, articleName);
										nodeDefinition.relateWords(word, paragraphName);
									}
								}
								
								System.out.println(word.getBaseWord());
								
								previousWords.clear();

								previousWords.add(word);							
								
							}
							
							previousWords.clear();
							
						} catch (Exception e){
							e.printStackTrace();
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

	
	private ArrayList<Article> loadRange(String query, Integer from, Integer size) {
		ArrayList<Article> articles = new ArrayList<Article>();
		
		try {
			Class.forName("org.postgresql.Driver");
			Connection conexion = DriverManager.getConnection(
					"jdbc:postgresql://bartgentoo.no-ip.org:5432/wiki", "wiki", "wiki1621");
			Statement s = conexion.createStatement();

			log.debug("Leyendo de la base de datos...");

			ResultSet rs = s.executeQuery(
					query + " LIMIT " + size + " OFFSET " + from);

			while (rs.next()) {
				articles.add( 
						new Article()
							.setTitle(rs.getString(1))
							.setSubtitle(rs.getString(2))
							.setBody(rs.getString(3))
							
						);
			}
			conexion.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.debug("Fin de Lectura");
		
		return articles;
	}
	
	
	@SuppressWarnings("unused")
	private class Article{
		
		String title;
		String subtitle;
		String body;


		public Article() {
			this.title = "";
			this.subtitle = "";
			this.body = "";
		}


		public Article(String title, String subtitle, String body) {
			this.title = title;
			this.subtitle = subtitle;
			this.body = body;
		}

		
		public String getTitle() {
			return title;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public String getBody() {
			return body;
		}

		public Article setTitle(String title) {
			this.title = title;
			return this;
		}

		public Article setSubtitle(String subtitle) {
			this.subtitle = subtitle;
			return this;
		}

		public Article setBody(String body) {
			this.body = body;
			return this;
		}		
		
		
	}

	
}
