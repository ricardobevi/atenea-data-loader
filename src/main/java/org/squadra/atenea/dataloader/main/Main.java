package org.squadra.atenea.dataloader.main;

import java.io.IOException;

import org.squadra.atenea.data.server.NeuralDataAccess;
import org.squadra.atenea.dataloader.WikiConceptLoader;


public class Main {

	public static void main(String args[]) {
		/*
		// Cargo las respuestas a dialogos
		new DialogLoader(true, "dialogType", 0).loadData("./AteneaDialogResponses.txt");
		
		// Cargo las respuestas a ordenes
		new DialogLoader(true, "orderType", 500).loadData("./AteneaOrderResponses.txt");
		
		// Cargo las respuestas de la Wiki
		String query = "SELECT cuerpo FROM articulo WHERE titulo = 'José de San Martín' ORDER BY id ASC ";
		
		new WikipediaBulkLoader(true, "wikiSentence", 2000).loadData(query);
		
		
		*/
		 
		
		new WikiConceptLoader().loadData(
				"select titulo, subtitulo, sentence "
				+ "from sentencescase as S join articulo as A ON S.id = A.id "
				+ "where A.titulo IN ( "
				+ "'José de San Martín')"
				);
				/*
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
		
		  
		
		NeuralDataAccess.init("/home/ric/Documentos/Universidad/Proyecto/workspace/AteneaDataLoader/graphDB");
		
		
		//NeuralDataAccess.init();
		
		try {
			System.out.println("Presiona una tecla para parar el servidor.");
			System.in.read();
		} catch (IOException e) {    
			NeuralDataAccess.stop();
			e.printStackTrace();  
		}
		
		NeuralDataAccess.stop();
		
		/*
		String str = "hola 100. hola 1000 sisi. 2.000 si s«eñor. ";
		String[] array = str.split("\\.[a-zA-Z| ]");
		for (String s : array) {
			System.out.println(s.replaceAll("[\\«\\»]", ""));
		}*/
	
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
	    
	    
	    
	    
	    
	START neo=node:words('name:"San Martín"'),
      other=node:words('name:madre')
	MATCH path= neo-[r:SENTENCE*..5]->other 
	WHERE ALL(n in nodes(path) where 
	          1=length(filter(m in nodes(path) : m=n))) 
	RETURN neo, LENGTH(path) AS length, EXTRACT(p in NODES(path) : p.baseWord), other 
	ORDER BY length
	
	
START 
  a = node:words('baseWord:Guayaquil'),
  b = node:words('baseWord:encontrar'),
  c = node:words('baseWord:"José de San Martín"')
MATCH path = a-[r:CONCEPT*..2]->b-[r:CONCEPT*..2]->c
WHERE 
  ALL(
    n in nodes(path) where 
    length( filter(m in nodes(path) : m=n) ) = 1 
  ) 
RETURN LENGTH(path) AS length, EXTRACT(p in NODES(path) : p.baseWord) 
ORDER BY length



START 
  a = node:words('baseWord:Guayaquil'),
  b = node:words('baseWord:encontrar'),
  c = node:words('baseWord:"José de San Martín"')
MATCH path = a-[relation:CONCEPT*..2]->b-[relation:CONCEPT*..2]->c
WHERE 
  ALL(
    n in nodes(path) where 
    length( filter(m in nodes(path) : m=n) ) = 1 
  ) 
RETURN 
  LENGTH(path) AS length, 
  EXTRACT(p in NODES(path) : p.baseWord),
  EXTRACT(r in relation : r.weight)
  
ORDER BY length


START 
  a = node:words('baseWord:Guayaquil'),
  b = node:words('baseWord:encontrar'),
  c = node:words('baseWord:"José de San Martín"')
MATCH path = a-[relation:CONCEPT*..2]->b-[relation:CONCEPT*..2]->c
RETURN 
  LENGTH(path) AS length, 
  EXTRACT(p in NODES(path) : p.baseWord),
  reduce(acc=0, r in relationships(path): acc + r.weight) as totalWeight
  
ORDER BY totalWeight DESC


START 
  a = node:words('baseWord:*'),
  b = node:words('baseWord:*')
MATCH path = a-[relation:CONCEPT*..2]->b
WHERE
	a.baseWord = "madre" AND
    b.baseWord =~ '.*San Martín.*'
    
RETURN 
  LENGTH(path) AS length, 
  EXTRACT(p in NODES(path) : p.baseWord),
  reduce(acc=0, r in relationships(path): acc + r.weight) as totalWeight
  
ORDER BY totalWeight DESC


START 
  a = node:words('baseWord:*'),
  b = node:words('baseWord:*'),
  x = node:words('baseWord:*')
MATCH path = a-[relation:CONCEPT*..1]->x-[relation:CONCEPT*..1]->b
WHERE
	a.baseWord = "madre" AND
    x.baseWord = "Gregoria Matorras del Ser" AND
    b.baseWord =~ "José de San Martín"

FOREACH (n IN relationships(path) : SET n.weight = n.weight + 100)
  
RETURN 
  LENGTH(path) AS length, 
  EXTRACT(p in NODES(path) : p.baseWord),
  reduce(acc=0, r in relationships(path): acc + r.weight) as totalWeight,
  EXTRACT(r in relationships(path) : r.contextSentence)
  
ORDER BY totalWeight DESC



*/