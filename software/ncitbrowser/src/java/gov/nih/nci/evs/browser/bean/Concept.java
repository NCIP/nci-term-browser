package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;
import java.net.*;

public class Concept
{
// Variable declaration
	private String ncitCode; //*
	private String sourcePreferredTerm;  //*
	private String ncitPreferredTerm; //*
	private ArrayList ncitSynonyms;
	private ArrayList sourceSynonyms; //*
	private ArrayList ncitDefinitions; //*
	private ArrayList sourceDefinitions;

// Default constructor
	public Concept() {
	}

// Constructor
	public Concept(
		String ncitCode,
		String sourcePreferredTerm,
		String ncitPreferredTerm,
		ArrayList sourceSynonyms,
		ArrayList ncitDefinitions) {

		this.ncitCode = ncitCode;
		this.ncitPreferredTerm = ncitPreferredTerm;
		this.sourcePreferredTerm = sourcePreferredTerm;
		this.ncitSynonyms = null;
		this.sourceSynonyms = sourceSynonyms;
		this.ncitDefinitions = ncitDefinitions;
		this.sourceDefinitions = null;
	}

	public Concept(
		String ncitCode,
		String ncitPreferredTerm,
		String sourcePreferredTerm,
		ArrayList ncitSynonyms,
		ArrayList sourceSynonyms,
		ArrayList ncitDefinitions,
		ArrayList sourceDefinitions) {

		this.ncitCode = ncitCode;
		this.ncitPreferredTerm = ncitPreferredTerm;
		this.sourcePreferredTerm = sourcePreferredTerm;
		this.ncitSynonyms = ncitSynonyms;
		this.sourceSynonyms = sourceSynonyms;
		this.ncitDefinitions = ncitDefinitions;
		this.sourceDefinitions = sourceDefinitions;
	}

// Set methods
	public void setNcitCode(String ncitCode) {
		this.ncitCode = ncitCode;
	}

	public void setNcitPreferredTerm(String ncitPreferredTerm) {
		this.ncitPreferredTerm = ncitPreferredTerm;
	}

	public void setSourcePreferredTerm(String sourcePreferredTerm) {
		this.sourcePreferredTerm = sourcePreferredTerm;
	}

	public void setNcitSynonyms(ArrayList ncitSynonyms) {
		this.ncitSynonyms = ncitSynonyms;
	}

	public void setSourceSynonyms(ArrayList sourceSynonyms) {
		this.sourceSynonyms = sourceSynonyms;
	}

	public void setNcitDefinitions(ArrayList ncitDefinitions) {
		this.ncitDefinitions = ncitDefinitions;
	}

	public void setSourceDefinitions(ArrayList sourceDefinitions) {
		this.sourceDefinitions = sourceDefinitions;
	}


// Get methods
	public String getNcitCode() {
		return this.ncitCode;
	}

	public String getNcitPreferredTerm() {
		return this.ncitPreferredTerm;
	}

	public String getSourcePreferredTerm() {
		return this.sourcePreferredTerm;
	}

	public ArrayList getNcitSynonyms() {
		return this.ncitSynonyms;
	}

	public ArrayList getSourceSynonyms() {
		return this.sourceSynonyms;
	}

	public ArrayList getNcitDefinitions() {
		return this.ncitDefinitions;
	}

	public ArrayList getSourceDefinitions() {
		return this.sourceDefinitions;
	}

}
