package fr.sparna.rdf.sesame.toolkit.util;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.parser.ParsedBooleanQuery;
import org.openrdf.query.parser.ParsedGraphQuery;
import org.openrdf.query.parser.ParsedOperation;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.query.parser.ParsedUpdate;
import org.openrdf.query.parser.QueryParserUtil;

/**
 * A final class to determine the type a SPARQL query String.
 * @author Thomas Francart
 */
public final class SPARQLQueryType {

	/**
	 * Possible types of a SPARQL query. Possible values are
	 * <ul>
	 *   <li>GRAPH : for a CONSTRUCT or DESCRIBE QUERY</li>
	 *   <li>SELECT : for a SELECT query</li>
	 *   <li>ASK : for an ASK query</li>
	 *   <li>UPDATE : for a DELETE, INSERT, DELETE DATA or INSERT DATA query</li>
	 * </ul>
	 * 
	 * @author Thomas Francart
	 */
	public enum QUERYTYPE {
		GRAPH,
		SELECT,
		ASK,
		UPDATE
	}
	
	/**
	 * Dynamically determine the type of a SPARQL query base on its content
	 * 
	 * @return
	 * @throws MalformedQueryException
	 */
	public static QUERYTYPE getQueryType(String sparql) throws MalformedQueryException {
		ParsedOperation parsedOperation = QueryParserUtil.parseOperation(QueryLanguage.SPARQL, sparql, null);
		if (parsedOperation instanceof ParsedTupleQuery) {
			return QUERYTYPE.SELECT;
		} else if (parsedOperation instanceof ParsedGraphQuery) {
			return QUERYTYPE.GRAPH;
		} else if (parsedOperation instanceof ParsedBooleanQuery) {
			return QUERYTYPE.ASK;
		} else if (parsedOperation instanceof ParsedUpdate) {
			return QUERYTYPE.UPDATE;
		} else {
			throw new MalformedQueryException("Unexpected query type "+ parsedOperation.getClass() + " for query " + sparql);
		}
	}
	
}