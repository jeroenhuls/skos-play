//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.24 at 08:01:59 PM CET 
//


package fr.sparna.rdf.skos.printer.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Representation of a KWIC index.
 * 			
 * 
 * <p>Java class for index complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="index">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="entry" type="{http://www.sparna.fr/thesaurus-display}indexEntry" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute ref="{http://www.sparna.fr/thesaurus-display}indexStyle"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "index", propOrder = {
    "entry"
})
public class Index {

    protected List<IndexEntry> entry;
    @XmlAttribute(namespace = "http://www.sparna.fr/thesaurus-display")
    protected String indexStyle;

    /**
     * Gets the value of the entry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link IndexEntry }
     * 
     * 
     */
    public List<IndexEntry> getEntry() {
        if (entry == null) {
            entry = new ArrayList<IndexEntry>();
        }
        return this.entry;
    }

    /**
     * Gets the value of the indexStyle property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIndexStyle() {
        return indexStyle;
    }

    /**
     * Sets the value of the indexStyle property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIndexStyle(String value) {
        this.indexStyle = value;
    }

}
