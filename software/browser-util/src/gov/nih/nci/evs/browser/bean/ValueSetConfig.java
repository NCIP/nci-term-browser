package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;
import java.net.*;

public class ValueSetConfig
{

// Variable declaration
	private String name;
	private String uri;
	private String reportURI;
	private String extractionRule;

// Default constructor
	public ValueSetConfig() {
	}

// Constructor
	public ValueSetConfig(
		String name,
		String uri,
		String reportURI,
		String extractionRule) {

		this.name = name;
		this.uri = uri;
		this.reportURI = reportURI;
		this.extractionRule = extractionRule;
	}

// Set methods
	public void setName(String name) {
		this.name = name;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setReportURI(String reportURI) {
		this.reportURI = reportURI;
	}

	public void setExtractionRule(String extractionRule) {
		this.extractionRule = extractionRule;
	}


// Get methods
	public String getName() {
		return this.name;
	}

	public String getUri() {
		return this.uri;
	}

	public String getReportURI() {
		return this.reportURI;
	}

	public String getExtractionRule() {
		return this.extractionRule;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append("name: ").append(name);
		buf.append("\n\turi: ").append(uri);
		String report_uri = reportURI.replaceAll(" ", "%20");
		buf.append("\n\treportURI: ").append(report_uri);
		buf.append("\n\textractionRule: ").append(extractionRule);
		return buf.toString();
	}
}
