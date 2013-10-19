package org.squadra.atenea.dataloader.main;

import org.squadra.atenea.dataloader.DialogLoader;
import org.squadra.atenea.dataloader.ManualDataLoader;
import org.squadra.atenea.dataloader.WikiEnhancedBulkLoader;
import org.squadra.atenea.dataloader.WikipediaBulkLoader;

public class Main {

	public static void main(String args[]) {
		WikipediaBulkLoader.run();
		//WikiEnhancedBulkLoader.run();
		//ManualDataLoader mdl = new ManualDataLoader();
		//mdl.loadData("");
		
		// Cargo las respuestas a dialogos
		//new DialogLoader(true, "dialogType", 0).loadData("./AteneaDialogResponses.txt");
		// Cargo las respuestas a ordenes
		//new DialogLoader(true, "orderType", 500).loadData("./AteneaOrderResponses.txt");
		
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
*/