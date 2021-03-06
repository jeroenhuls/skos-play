package fr.sparna.rdf.skos.toolkit;

import org.eclipse.rdf4j.model.IRI;

public class SKOSTreeNode {

	public static enum NodeType {
		CONCEPT,
		CONCEPT_SCHEME,
		COLLECTION,
		COLLECTION_AS_ARRAY,
		UNKNOWN
	}
	
	// Corresponding Concept URI
	protected IRI iri;
	// Type of node (Concept vs. Collection)
	protected NodeType nodeType = NodeType.UNKNOWN;
	// the criteria on which to sort
	protected String sortCriteria;
	protected double weight = 1.0d;

	
	public SKOSTreeNode(IRI iri, String sortCriteria, NodeType nodeType) {
		super();
		this.iri = iri;
		this.sortCriteria = sortCriteria;
		this.nodeType = nodeType;
	}

	public IRI getIri() {
		return iri;
	}

	public void setIri(IRI iri) {
		this.iri = iri;
	}

	public String getSortCriteria() {
		return sortCriteria;
	}

	public void setSortCriteria(String sortCriteria) {
		this.sortCriteria = sortCriteria;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}	
	
}
