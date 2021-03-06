package fr.sparna.rdf.skos.toolkit;

import java.util.function.Supplier;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQueryResultHandlerException;
import org.eclipse.rdf4j.query.impl.SimpleBinding;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

import fr.sparna.rdf.rdf4j.toolkit.query.Perform;
import fr.sparna.rdf.rdf4j.toolkit.query.SelfTupleQueryHelper;
import fr.sparna.rdf.rdf4j.toolkit.query.SimpleSparqlOperation;
import fr.sparna.rdf.rdf4j.toolkit.query.TupleQueryHelperIfc;
import fr.sparna.rdf.rdf4j.toolkit.repository.RepositoryBuilder;

/**
 * Queries for the labels (pref and alt) of concepts in a given concept scheme (or in
 * the entire repository), in a given language. Results are _not_ ordered and should be ordered with a Collator.
 * 
 * @author Thomas Francart
 */
@SuppressWarnings("serial")
public abstract class GetLabelsInSchemeHelper extends SelfTupleQueryHelper implements TupleQueryHelperIfc {

	/**
	 * @param lang				a 2-letters ISO-code of the language to read labels in.
	 * @param conceptSchemeURI 	the URI of the concept scheme to read labels from (can be null to read all labels)
	 */
	public GetLabelsInSchemeHelper(String lang, final IRI conceptSchemeIri) {
		super(
				new SimpleSparqlOperation(new QuerySupplier(lang, conceptSchemeIri)).withBinding(
						(conceptSchemeIri != null)
						?new SimpleBinding("scheme", conceptSchemeIri)
						:null
				)	
		);
	}
	
	/**
	 * Same as this(lang, null)
	 * @param lang				a 2-letters ISO-code of the language to read labels in.
	 */
	public GetLabelsInSchemeHelper(String lang) {
		this(lang, null);
	}

	@Override
	public void handleSolution(BindingSet binding)
	throws TupleQueryResultHandlerException {
		Literal label = (Literal)binding.getValue("label");
		Literal prefLabel = (Literal)binding.getValue("prefLabel");
		Resource concept = (Resource)binding.getValue("concept");
		this.handleLabel(label, prefLabel, concept);
	}
	
	protected abstract void handleLabel(Literal label, Literal prefLabel, Resource concept)
	throws TupleQueryResultHandlerException;
	
	public static class QuerySupplier implements Supplier<String> {

		private String lang = null;
		private IRI conceptScheme = null;

		/**
		 * @param lang 				2-letter ISO-code of a language to select labels in
		 * @param conceptScheme		optionnal URI of a concept scheme to select labels in
		 */
		public QuerySupplier(String lang, IRI conceptScheme) {
			this.lang = lang;
			this.conceptScheme = conceptScheme;
		}
		
		/**
		 * Same as this(lang, null)
		 * @param lang 				2-letter ISO-code of a language to select labels in
		 */
		public QuerySupplier(String lang) {
			this(lang, null);
		}

		@Override
		public String get() {
			String sparql = "" +
					"SELECT ?label ?prefLabel ?concept"+"\n" +
					"WHERE {"+"\n" +
					"	?concept a <"+SKOS.CONCEPT+"> ." +
					((this.conceptScheme != null)?"?concept <"+SKOS.IN_SCHEME+"> ?scheme . ":"") +
					"	{ " +
					"		{ " +
							"   ?concept <"+SKOS.PREF_LABEL+"> ?label FILTER(langMatches(lang(?label), '"+this.lang+"')) " +
							" }" +
							" UNION {" +
							"	?concept <"+SKOS.ALT_LABEL+"> ?label ." +
							"	?concept <"+SKOS.PREF_LABEL+"> ?prefLabel ." +
							"	FILTER(langMatches(lang(?label), '"+this.lang+"') && langMatches(lang(?prefLabel), '"+this.lang+"')) " +
							" }" +
							" UNION {" +
							// il faut qu'on ait au moins un critere positif sinon ca ne fonctionne pas
							"	?concept a <"+SKOS.CONCEPT+"> . " +
							"	FILTER NOT EXISTS { ?concept <"+SKOS.PREF_LABEL+"> ?nopref . FILTER(langMatches(lang(?nopref), '"+this.lang+"')) }" +
							"   BIND(str(?concept) as ?label)" +
							" }" +
					"	}" +
					"}";
					
					return sparql;
		}		
	}
	
	public static void main(String... args) throws Exception {
		Repository r = RepositoryBuilder.fromRdf(
				"@prefix skos: <"+SKOS.NAMESPACE+"> ."+"\n" +
				"@prefix test: <http://www.test.fr/skos/> ."+"\n" +
				"test:_1 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"C-1-pref\"@fr; skos:altLabel \"A-1-alt\"@fr ." +
				"test:_2 a skos:Concept ; skos:inScheme test:_scheme ; skos:prefLabel \"B-2-pref\"@fr ." +
				"test:_3 a skos:Concept ; skos:inScheme test:_anotherScheme ; skos:prefLabel \"D-3-pref\"@fr ."
		);
		GetLabelsInSchemeHelper helper = new GetLabelsInSchemeHelper(
				"fr",
				SimpleValueFactory.getInstance().createIRI("http://www.test.fr/skos/_scheme")
		) {
			
			@Override
			protected void handleLabel(
					Literal label,
					Literal prefLabel,
					Resource concept
			) throws TupleQueryResultHandlerException {
				System.out.println(label.getLabel()+" / "+((prefLabel != null)?prefLabel.getLabel():"null")+" / "+concept.stringValue());
			}
		};
		try(RepositoryConnection c = r.getConnection()) {
			Perform.on(c).select(helper);
		}
		
	}
	
}
