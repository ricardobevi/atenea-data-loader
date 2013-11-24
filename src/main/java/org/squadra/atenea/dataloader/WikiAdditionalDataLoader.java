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

@Log4j
public class WikiAdditionalDataLoader implements DataLoaderInterface {
	
	/**
	 * Contador de IDs para cada oracion insertada.
	 */
	private long currentSentenceId = 0;
	
	private NodeDefinition nodeDefinition;
	
	/**
	 * Mapa que contiene la lista de sinonimos correspondientes a cada
	 * subtitulo de los cuadritos de la wikipedia.
	 * Key: subtitulo, Value: array de sinonimos y tipo (en un obj Synonim).
	 */
	private HashMap< String, Synonim > subtitleSynonims = new HashMap<>();
	
	
	/**
	 * Constructor parametrizado.
	 * @param firstSentenceId
	 */
	public WikiAdditionalDataLoader(long firstSentenceId) {
		this.currentSentenceId = firstSentenceId;
		this.loadSubtitleSynonims();
	}
	

	@Override
	public void loadData(String source) {
		
		// Inicio la base de datos
		NeuralDataAccess.init();
		
		// Inicio la transaccion
		nodeDefinition = new NodeDefinition();
		
		//Cargo de a 1000 registros de mysql
		int from = 0, diff = 1000;
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
					
					String title = row.get("title").trim();
					String subtitle = row.get("subtitle").trim();
					String body = "";
					if (row.get("body") != null) {
						body = row.get("body").trim();
					}
					
					//Escribo en la base de datos
					write(title, subtitle, body);
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

	
	private void write(String title, String subtitle, String body) {
		
		try {			
			Synonim synonims = subtitleSynonims.get(subtitle.replaceAll("[0-9]", ""));
			
			if (synonims != null) {
				
				System.out.println("Loading: " + title + " - " + subtitle + " - " + body);

				for (String synonim : synonims.getSynonims()) {
					
					// Relaciono el titulo con los sinonimos
					nodeDefinition.relateWikiInfoWords(
							new Word(title), 
							new Word(synonim), 
							currentSentenceId, 1, 100, "synonim");
					
					// Relaciono los sinonimos con el contenido
					nodeDefinition.relateWikiInfoWords(
							new Word(synonim), 
							new Word(body), 
							currentSentenceId, 2, 100, synonims.getType());

					currentSentenceId++;
				}
				
			}
			
		}
		catch (Exception e) {
			nodeDefinition.endTransaction();
			e.printStackTrace();
		}

	}


	private void loadSubtitleSynonims() {
		
		/* PARA PERSONAS 
		 * probado con Juan Domingo Perón, José de San Martín, Barack Obama, Néstor Kirchner
		 * Lionel Messi, Diago Armando Maradona
		 */
		
		subtitleSynonims.put("cónyuge", 
				new Synonim(new String[] {"cónyuge", "esposo", "esposa", "marido", "mujer", "casar"}, "nombre"));
		
		subtitleSynonims.put("hijos", 
				new Synonim(new String[] {"hijo", "hija", "descendiente", "heredero"}, "nombre"));
		
		subtitleSynonims.put("profesión", 
				new Synonim(new String[] {"profesión", "ocupación", "empleo", "carrera", "trabajo", "trabajar", 
								"ocupar", "dedicar", "oficio" }, "sustantivo"));
		
		subtitleSynonims.put("ocupación", 
				new Synonim(new String[] {"profesión", "ocupación", "empleo", "carrera", "trabajo", "trabajar", 
								"ocupar", "dedicar", "oficio" }, "sustantivo"));
		
		subtitleSynonims.put("ciudadfallecimiento", 
				new Synonim(new String[] {"morir", "muerte", "fallecer", "fallecimiento", "sucumbir"}, "lugar"));
		
		subtitleSynonims.put("paisfallecimiento", 
				new Synonim(new String[] {"morir", "muerte", "fallecer", "fallecimiento", "sucumbir"}, "lugar"));
		
		subtitleSynonims.put("lugarmuerte", 
				new Synonim(new String[] {"morir", "muerte", "fallecer", "fallecimiento", "sucumbir"}, "lugar"));
		
		subtitleSynonims.put("fechamuerte", 
				new Synonim(new String[] {"morir", "muerte", "fallecer", "fallecimiento", "sucumbir"}, "fecha"));
		
		subtitleSynonims.put("fechafallecimiento", 
				new Synonim(new String[] {"morir", "muerte", "fallecer", "fallecimiento", "sucumbir"}, "fecha"));
		
		subtitleSynonims.put("lugarnac", 
				new Synonim(new String[] {"nacer", "nacimiento"}, "lugar"));
		
		subtitleSynonims.put("ciudaddenacimiento", 
				new Synonim(new String[] {"nacer", "nacimiento"}, "lugar"));
		
		subtitleSynonims.put("paisdenacimiento", 
				new Synonim(new String[] {"nacer", "nacimiento"}, "lugar"));
		
		subtitleSynonims.put("fechanac", 
				new Synonim(new String[] {"nacer", "nacimiento"}, "fecha"));
		
		subtitleSynonims.put("fechadenacimiento", 
				new Synonim(new String[] {"nacer", "nacimiento"}, "fecha"));
		
		subtitleSynonims.put("nombre", 
				new Synonim(new String[] {"nombre", "apellido", "llamar", "apellidar"}, "nombre"));
		
		subtitleSynonims.put("título", 
				new Synonim(new String[] {"título", "cargo", "puesto", "función"}, "sustantivo"));
		
		subtitleSynonims.put("cargo", 
				new Synonim(new String[] {"título", "cargo", "puesto", "función"}, "sustantivo"));
		
		subtitleSynonims.put("partido", 
				new Synonim(new String[] {"partido", "política", "político"}, "sustantivo"));
		
		subtitleSynonims.put("almamáter", 
				new Synonim(new String[] {"almamáter", "estudiar", "educar", "formar", "estudio", "educación",
								"formación"}, "lugar"));
		
		subtitleSynonims.put("religión", 
				new Synonim(new String[] {"religión", "creencia", "creer", "profesar"}, "sustantivo"));
		
		subtitleSynonims.put("residencia", 
				new Synonim(new String[] {"residencia", "residir", "vivir", "hogar", "domicilio", "habitar"}, "sustantivo"));
		
		subtitleSynonims.put("apodo", 
				new Synonim(new String[] {"apodo", "apodar", "sobrenombre"}, "nombre"));
		
		subtitleSynonims.put("posición", 
				new Synonim(new String[] {"posición"}, "lugar"));
		
		subtitleSynonims.put("club", 
				new Synonim(new String[] {"club", "jugar"}, "lugar"));
		
		subtitleSynonims.put("clubdebut", 
				new Synonim(new String[] {"club", "debut", "debutar", "jugar"}, "lugar"));
		
		subtitleSynonims.put("clubdebutjug", 
				new Synonim(new String[] {"club", "debut", "debutar", "jugar"}, "lugar"));
		
		subtitleSynonims.put("clubretiro", 
				new Synonim(new String[] {"club", "retiro", "retirar", "jugar"}, "lugar"));
		
		subtitleSynonims.put("clubretirojug", 
				new Synonim(new String[] {"club", "retiro", "retirar", "jugar"}, "lugar"));
		
		subtitleSynonims.put("goles", 
				new Synonim(new String[] {"gol", "conversión", "convertir", "anotación", "anotar"}, "cantidad"));
	}
	
	
	/**
	 * Clase para guardar los sinonimos relacionados con un campo de la wiki determinado
	 * @author Leandro
	 *
	 */
	private class Synonim {
		private String[] synonims;
		private String type;
		
		public Synonim(String[] synonims, String type) {
			this.synonims = synonims;
			this.type = type;
		}
		
		public String getType() {
			return type;
		}
		
		public String[] getSynonims() {
			return synonims;
		}
	}

}
