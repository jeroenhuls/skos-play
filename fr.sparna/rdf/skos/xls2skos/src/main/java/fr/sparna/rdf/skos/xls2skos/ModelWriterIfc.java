package fr.sparna.rdf.skos.xls2skos;

import java.util.Map;

import org.eclipse.rdf4j.model.Model;

public interface ModelWriterIfc {

	public void saveGraphModel(String graph, Model model, Map<String, String> prefixes);
	
	public void beginWorkbook();
	
	public void endWorkbook();
	
	

}