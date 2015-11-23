package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;
import java.net.*;

public class TerminologyBean
{

// Variable declaration
	private String displayLabel;
	private String displayName;
	private String fullName;
	private String displayVersion;
	private String codingSchemeName;
	private String codingSchemeVersion;

// Default constructor
	public TerminologyBean() {
	}

// Constructor
	public TerminologyBean(
		String displayLabel,
		String displayName,
		String codingSchemeName,
		String codingSchemeVersion) {

		this.displayLabel = displayLabel;
		this.displayName = displayName;
		this.fullName = displayName;
		this.displayVersion = codingSchemeVersion;
		this.codingSchemeName = codingSchemeName;
		this.codingSchemeVersion = codingSchemeVersion;
	}


	public TerminologyBean(
		String displayLabel,
		String displayName,
		String fullName,
		String displayVersion,
		String codingSchemeName,
		String codingSchemeVersion) {

		this.displayLabel = displayLabel;
		this.displayName = displayName;
		this.fullName = fullName;
		this.displayVersion = displayVersion;
		this.codingSchemeName = codingSchemeName;
		this.codingSchemeVersion = codingSchemeVersion;
	}

// Set methods
	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public void setDisplayVersion(String displayVersion) {
		this.displayVersion = displayVersion;
	}

	public void setCodingSchemeName(String codingSchemeName) {
		this.codingSchemeName = codingSchemeName;
	}

	public void setCodingSchemeVersion(String codingSchemeVersion) {
		this.codingSchemeVersion = codingSchemeVersion;
	}


// Get methods
	public String getDisplayLabel() {
		return this.displayLabel;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getDisplayVersion() {
		return this.displayVersion;
	}

	public String getCodingSchemeName() {
		return this.codingSchemeName;
	}

	public String getCodingSchemeVersion() {
		return this.codingSchemeVersion;
	}

	public String toString() {
         StringBuffer buf = new StringBuffer();
         buf.append(displayLabel);
         buf.append("\n\tdisplayName: " + displayName);
         buf.append("\n\tfullName: " + fullName);
         buf.append("\n\tdisplayVersion: " + displayVersion);
         buf.append("\n\tcodingSchemeName: " + codingSchemeName);
         buf.append("\n\tcodingSchemeVersion: " + codingSchemeVersion);
         return buf.toString();
	}

}
