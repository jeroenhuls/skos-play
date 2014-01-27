package fr.sparna.rdf.sesame.toolkit.query.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.sparna.commons.io.FileUtil;
import fr.sparna.commons.io.ResourceList;

public final class SparqlQueryBuilderList {

	
	/**
	 * Turns a List<String> containing SPARQL queries into a List<SparqlQueryBuilder>
	 * 
	 * @param strings
	 * 
	 * @return a list of SparqlQueryBuilder, each wrapping one of the Strings.
	 */
	public static List<SparqlQueryBuilder> fromStringList(List<String> strings) {
		if(strings == null) {
			return null;
		}
		
		ArrayList<SparqlQueryBuilder> result = new ArrayList<SparqlQueryBuilder>();
		for (String aString : strings) {
			result.add(new SparqlQueryBuilder(aString));
		}
		return result;
	}
	
	
	/**
	 * Turns a directory containing files expressing SPARQL queries into a List<SparqlQueryBuilder>
	 * 
	 * @param directory		The directory to read from, or a file for a single query
	 * 
	 * @return a list of SparqlQueryBuilder
	 */
	public static List<SparqlQueryBuilder> fromDirectory(File directory) {
		if(directory == null) {
			return null;
		}
		
		ArrayList<SparqlQueryBuilder> result = new ArrayList<SparqlQueryBuilder>();
		if(directory.exists()) {
			// iterate in each file of the dir
			if(directory.isDirectory()) {
				List<File> files = FileUtil.listFilesRecursive(directory);
				for (File aFile : files) {
					result.add(new SparqlQueryBuilder(aFile));
				}
			} else {
				// or use a single file
				result.add(new SparqlQueryBuilder(directory));
			}
		}
		return result;
	}
	
	/**
	 * Reads all resources in a directory on the classpath and turn them into a List<SparqlQueryBuilder>
	 * 
	 * @param directory		The classpath directory to read from
	 * 
	 * @return a list of SparqlQueryBuilder
	 */
	public static List<SparqlQueryBuilder> fromClasspathDirectory(String classpathDirectory) {
		if(classpathDirectory == null) {
			return null;
		}
		
		ArrayList<SparqlQueryBuilder> result = new ArrayList<SparqlQueryBuilder>();
		Collection<URL> resources = ResourceList.listDirectoryResources(classpathDirectory);
		for (URL url : resources) {
			try {
				InputStream is = url.openStream();
				result.add(new SparqlQueryBuilder(is));
				is.close();
			} catch (IOException e) {
				throw new Error(e);
			}
		}
		
		return result;
	}
	
	public static List<SparqlQueryBuilder> fromResources(Class<?> owner, List<String> resource) {
		if(owner == null) {
			return null;
		}
		
		ArrayList<SparqlQueryBuilder> result = new ArrayList<SparqlQueryBuilder>();
		for (String aString : resource) {
			result.add(new SparqlQueryBuilder(owner, aString));
		}
		return result;
	}
	
}