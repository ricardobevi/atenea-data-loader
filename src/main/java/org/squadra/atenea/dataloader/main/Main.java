package org.squadra.atenea.dataloader.main;

import java.io.IOException;

import org.squadra.atenea.data.server.NeuralDataAccess;
import org.squadra.atenea.dataloader.DialogLoader;
import org.squadra.atenea.dataloader.WikiAdditionalDataLoader;
import org.squadra.atenea.dataloader.WikipediaBulkLoader;
import org.squadra.atenea.dataloader.WikiConceptLoader;

public class Main {

	public static void main(String args[]) {
		
		// Cargo las respuestas a dialogos
		new DialogLoader(true, "dialogType", 0).loadData("./AteneaDialogResponses.txt");
		
		// Cargo las respuestas a ordenes
		new DialogLoader(true, "orderType", 500).loadData("./AteneaOrderResponses.txt");
		
		// Cargo las respuestas a afirmaciones o mensajes desconocidos
		new DialogLoader(true, "defaultType", 1000).loadData("./AteneaDefaultResponses.txt");
		
		// Cargo las respuestas de la Wiki
		String query = "SELECT cuerpo FROM articulo WHERE titulo = 'José de San Martín' ORDER BY id ASC ";
		//new WikipediaBulkLoader(true, 2000).loadData(query);
		
		
		// Cargo los cuadritos (info adicional) de la Wiki
		// Es recomendable que esto se ejecute despues del WikipediaBulkLoader
		
		String query2 
					= "SELECT titulo, subtitulo, cuerpo FROM infoadicional_new2 WHERE "
					+ "titulo = 'José de San Martín' OR "
					+ "titulo = 'Barack Obama' OR "
					+ "titulo = 'Juan Domingo Perón' OR "
					+ "titulo = 'Néstor Kirchner' OR "
					+ "titulo = 'Lionel Messi' OR "
					+ "titulo = 'Diego Armando Maradona' "
					+ "ORDER BY titulo ASC ";
		
		new WikiAdditionalDataLoader(100000).loadData(query2);
		
		/*new WikiConceptLoader().loadData(
				"select titulo, subtitulo, sentence "
				+ "from sentencescase as S join articulo as A ON S.id = A.id "
				+ "where A.titulo IN ( "
				+ "'José de San Martín')"
				);
				
				+ "'Misiones jesuíticas guaraníes',"
				+ "'Virreinato del Río de la Plata',"
				+ "'Argentina',"
				+ "'Juan de San Martín',"
				+ "'María de los Remedios de Escalada',"
				+ "'Buenos Aires',"
				+ "'Londres',"
				+ "'Combate de San Lorenzo',"
				+ "'Ejército del Norte (Provincias Unidas del Río de la Plata)',"
				+ "'Ejército de los Andes',"
				+ "'Independencia de Chile',"
				+ "'Expedición Libertadora del Perú',"
				+ "'Simón Bolívar',"
				+ "'París',"
				+ "'Francmasonería'"
				+ ")"
				);*/
		
		NeuralDataAccess.init();
		try {
			System.out.println("Presiona una tecla para parar el servidor.");
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
		
		System.exit(0);
		
	}
	
}

/*
	Devuelve todo
  	START n=node(*)
  	MATCH (n)-[r]->(m)
    RETURN n.word as from, r.id as sentence, r.seq as `->`, m.word as to;
  	
  	devuelve una oracion
  	START n=node(*)
  	MATCH (n)-[r]->(m)
  	WHERE r.id=3
  	RETURN n.word as from, r.id as sentence, r.seq as `->`, m.word as to
  	ORDER BY r.seq;
  	
  	START 
		startNode = node:words('*:*')
	MATCH 
		(startNode)-[relation:SENTENCE]->(endNode)
	WHERE 
		relation.sentenceId = " + id
	RETURN 
		startNode.name, relation.sequence, relation.sentenceId, endNode.name
	ORDER BY 
		relation.sequence ASC;
		
		
	START 
		startNode = node:words('name:San'),
	    endNode = node:words('name:Martín')
	MATCH 
		(startNode)-[relation:SENTENCE]->(endNode)
	RETURN 
		startNode.name, endNode.name
		
		
		
    	
	START 
		startNode = node:words('name:Martín'),
	    endNode = node:words('name:militar')
	MATCH 
		(startNode)-[relation:SENTENCE]->(m),
	    (endNode)-[relation2:SENTENCE]->(n)
	WHERE 
		relation.sentenceId = relation2.sentenceId
	WITH
	    relation
	START startNode2 = node:words('*:*')
	MATCH 
	    (startNode2)-[relation3:SENTENCE]->(endNode2)
	WHERE 
	    relation3.sentenceId = relation.sentenceId
	RETURN 
	    startNode2.name, relation3.sequence, relation2.sentenceId, endNode2.name
	ORDER BY 
	    relation.sequence ASC;
	    
	    
	    
	START 
	node1 = node:words('name:Martín'),
    node2 = node:words('name:militar')
	MATCH 
		(node1)-[relation:SENTENCE]->(m),
	    (node2)-[relation2:SENTENCE]->(n)
	WHERE 
		relation.sentenceId = relation2.sentenceId
	WITH
	    relation, node1, node2
	MATCH 
	    (node1)-[relation3:SENTENCE]->(node2)
	WHERE 
	    relation3.sentenceId = relation.sentenceId
	RETURN DISTINCT
	    node1.name, 
	    relation3.sentenceId, 
	    relation3.sequence, 
	    node2.name
	ORDER BY 
	    relation3.sentenceId, relation3.sequence ASC;
  	
*/