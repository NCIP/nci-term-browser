package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;
import java.net.*;

public class ValueSet
{

// Variable declaration
	private String codingScheme;
	private String version;
	private String source;
	private ArrayList concepts;

// Default constructor
	public ValueSet() {
	}

// Constructor
	public ValueSet(
		String codingScheme,
		String version,
		String source,
		ArrayList concepts) {

		this.codingScheme = codingScheme;
		this.version = version;
		this.source = source;
		this.concepts = concepts;
	}

// Set methods
	public void setCodingScheme(String codingScheme) {
		this.codingScheme = codingScheme;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setConcepts(ArrayList concepts) {
		this.concepts = concepts;
	}


// Get methods
	public String getCodingScheme() {
		return this.codingScheme;
	}

	public String getVersion() {
		return this.version;
	}

	public String getSource() {
		return this.source;
	}

	public ArrayList getConcepts() {
		return this.concepts;
	}

}
