package org.squadra.atenea.dataloader;

import java.io.InputStream;
import java.net.URL;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.squadra.atenea.data.definition.NodeDefinition;
import org.squadra.atenea.data.server.NeuralDataAccess;
import org.xml.sax.ContentHandler;

public class WikiDataLoader implements DataLoaderInterface {

	public void loadData(String source) {

		InputStream in;
		
		try {

			in = new URL(source)
					.openStream();

			ContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);
			Metadata metadata = new Metadata();
			new HtmlParser().parse(in, handler, metadata, new ParseContext());

			String web = handler.toString();
			
			String[] nodes = web.split(" ");
			
			NeuralDataAccess.init();
			
			NodeDefinition nodeDefinition = new NodeDefinition();
			
			nodeDefinition.beginTransaction();

			try {
				
				for (Integer i = 0; i < nodes.length - 1; i++) {
					
					nodeDefinition.relateWords(nodes[i], nodes[i + 1]);
					
				}

			}

			finally {
				nodeDefinition.endTransaction();
			}

			
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

		

	}

}
