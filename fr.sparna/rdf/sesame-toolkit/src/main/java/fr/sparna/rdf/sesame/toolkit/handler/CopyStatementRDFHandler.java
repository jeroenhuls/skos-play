package fr.sparna.rdf.sesame.toolkit.handler;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import fr.sparna.rdf.sesame.toolkit.util.RepositoryTransaction;
import fr.sparna.rdf.sesame.toolkit.util.URIUtil;

/**
 * A handler that copies the resulting statements to a target repository. If the target repository
 * is set to be the same as the original/source repository on which the SPARQL queries are executed,
 * this means the resulting triples of a CONSTRUCT query will be inserted into the same repository.
 * This can form the base of a SPARQL-based inference engine or transformation engine.
 * 
 * @author Thomas Francart
 *
 */
public class CopyStatementRDFHandler implements RDFHandler {

	protected Repository targetRepository;
	
	// number of statements from a previous execution
	protected int previousResultStatementsCount = -1;
	// number of resulting statements
	protected int resultStatementsCount = 0;
	
	// target graphs to copy statements to
	protected Set<URI> targetGraphs;
	
	// used internally
	private RepositoryTransaction transaction;
	// used internally
	private Resource[] targetGraphsResources;
	
	/**
	 * Creates a CopyStatementRDFHandler that will copy statements in the target repository, in the given target graph
	 * 
	 * @param targetRepository the target repository in which the triples will be inserted.
	 * @param targetGraph the target graph in which statements will be inserted
	 */
	public CopyStatementRDFHandler(Repository targetRepository, URI targetGraph) {
		super();
		this.targetRepository = targetRepository;
		this.targetGraphs = Collections.singleton(targetGraph);
	}	
	
	/**
	 * Creates a CopyStatementRDFHandler that will copy statements in the target repository
	 * 
	 * @param targetRepository the target repository in which the triples will be inserted.
	 */
	public CopyStatementRDFHandler(Repository targetRepository) {
		super();
		this.targetRepository = targetRepository;
	}

	@Override
	public void startRDF() throws RDFHandlerException {
		try {
			RepositoryConnection connection = this.targetRepository.getConnection();
			this.transaction = new RepositoryTransaction(connection);
		} catch (RepositoryException e) {
			throw new RDFHandlerException(e);
		}
		// on traduit les URIs des graphes cibles en Value Sesame
		// une bonne fois pour toute
		this.targetGraphsResources = URIUtil.toResourceArray(this.targetGraphs, this.targetRepository.getValueFactory());
		// for the moment we don't have results, reset the number and keep it in previous count
		this.previousResultStatementsCount = this.resultStatementsCount;
		this.resultStatementsCount = 0;
	}
	
	@Override
	public void handleStatement(Statement s) throws RDFHandlerException {		
		try {
			if(this.targetGraphsResources == null) {
				this.transaction.add(s);
			} else {
				this.transaction.add(s, this.targetGraphsResources);
			}
			// increment statement count
			this.resultStatementsCount++;
		} catch (RepositoryException e) {
			throw new RDFHandlerException(e);
		}
	}	

	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			this.transaction.commit();
		} catch (RepositoryException e) {
			throw new RDFHandlerException(e);
		} finally {
			this.transaction.closeQuietly();
		}
	}

	/**
	 * Does nothing
	 */
	@Override
	public void handleComment(String c) throws RDFHandlerException {
		// nothing
	}

	/**
	 * Does nothing
	 */
	@Override
	public void handleNamespace(String arg0, String arg1) throws RDFHandlerException {
		// nothing
	}

	public Set<URI> getTargetGraphs() {
		return targetGraphs;
	}

	/**
	 * Sets the target graphs in which the resulting statements will be inserted. Be default
	 * the statements will be added in the default graph.
	 * 
	 * @param targetGraphs a set of URI containing all the graphs in which the statements will be inserted.
	 */
	public void setTargetGraphs(Set<URI> targetGraphs) {
		this.targetGraphs = targetGraphs;
	}

	/**
	 * After handling the result of a query, returns the number of statements processed.
	 * <p>This is used in the {@link fr.sparna.rdf.sesame.toolkit.util.SimpleSPARQLInferenceEngine SimpleSPARQLInferenceEngine}
	 * 
	 * @return the number of processed statements
	 */
	public int getResultStatementsCount() {
		return resultStatementsCount;
	}

	/**
	 * Returns the number of statements handled by this instance the previous time it was used.
	 * The previous number of statements is initiazed to the last number of statements processed in the
	 * startRDF method.
	 * <p><p>This is used in the {@link fr.sparna.rdf.sesame.toolkit.util.SimpleSPARQLInferenceEngine SimpleSPARQLInferenceEngine}
	 * 
	 * @return the number of processed statements the previous time this handler was used
	 */
	public int getPreviousResultStatementsCount() {
		return previousResultStatementsCount;
	}

	/**
	 * Returns the target repository in which the statements are copied.
	 * @return the target repository
	 */
	public Repository getTargetRepository() {
		return targetRepository;
	}
}