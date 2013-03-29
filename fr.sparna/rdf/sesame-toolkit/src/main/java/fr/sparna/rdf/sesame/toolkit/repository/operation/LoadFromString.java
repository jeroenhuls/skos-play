package fr.sparna.rdf.sesame.toolkit.repository.operation;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openrdf.model.vocabulary.RDF;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.sesame.toolkit.util.RepositoryTransaction;

/**
 * Reads RDF from an inline String. The RDF format to use to parse the string can be supplied
 * along with the string; if not supplied, this operation will try in order all the known
 * RDF formats in order : TURTLE, RDF/XML, N3, NTRIPLES, TRIG, TRIX.
 * If all fail, it will throw an exception. 
 * 
 * @author Thomas Francart
 *
 */
public class LoadFromString extends AbstractLoadOperation implements RepositoryOperationIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	protected String data;
	protected String rdfFormat;

	public LoadFromString(String data, String rdfFormat) {
		super();
		this.data = data;
		this.rdfFormat = rdfFormat;
	}

	public LoadFromString(String data) {
		this(data, null);
	}

	@Override
	public void execute(Repository repository)
	throws RepositoryOperationException {
		RepositoryConnection connection = null;
		try {
			connection = repository.getConnection();
			List<RDFFormat> formats = new ArrayList<RDFFormat>(Arrays.asList(new RDFFormat[] {
					RDFFormat.TURTLE,
					RDFFormat.RDFXML,
					RDFFormat.N3,
					RDFFormat.NTRIPLES,
					RDFFormat.TRIG,
					RDFFormat.TRIX
			}));
			if(this.rdfFormat != null) {
				log.debug("Will use this RDF format : "+this.rdfFormat);
				formats.retainAll(Collections.singletonList(RDFFormat.valueOf(this.rdfFormat)));
			} else {
				log.debug("No RDF format specified. Will use all formats : "+formats);
			}
			
			boolean success = false;
			for (RDFFormat aFormat : formats) {
				try {
					log.debug("Trying to parse String with format : "+aFormat);
					if(this.targetGraph == null) {
						connection.add(
								new ByteArrayInputStream(this.data.getBytes()),
								(this.defaultNamespace != null)?this.defaultNamespace:RDF.NAMESPACE,
								aFormat
						);
					} else {
						connection.add(
								new ByteArrayInputStream(this.data.getBytes()),
								(this.defaultNamespace != null)?this.defaultNamespace:RDF.NAMESPACE,
								aFormat,
								repository.getValueFactory().createURI(this.targetGraph.toString())
						);
					}
					log.debug("Parsing with format "+aFormat+" suceeded.");
					success = true;
					break;
				} catch (Exception e) {
					log.debug("Parsing with format "+aFormat+" failed (reason : "+e.getMessage()+")");
				}
			}
			
			if(!success) {
				throw new RepositoryOperationException("Unable to parse input RDF in one of the formats "+formats);
			}

		} catch (RepositoryException e) {
			// happens if repository.getConnection throws an Exception
			throw new RepositoryOperationException(e);
		} finally {
			RepositoryTransaction.closeQuietly(connection);
		}
	}	

}