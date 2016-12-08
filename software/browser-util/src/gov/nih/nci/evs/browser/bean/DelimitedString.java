package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;
import java.net.*;

public class DelimitedString
{

// Variable declaration
	private String line;
	private char delimiter;
	private Vector values;
	private int length;
	private int sortIndex;

// Default constructor
	public DelimitedString() {
	}

// Constructor
	public DelimitedString(
		String line,
		char delimiter,
		Vector values,
		int length) {

		this.line = line;
		this.delimiter = delimiter;
		this.values = values;
		this.length = length;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

// Set methods
	public void setLine(String line) {
		this.line = line;
	}

	public void setDelimiter(char delimiter) {
		this.delimiter = delimiter;
	}

	public void setValues(Vector values) {
		this.values = values;
	}

	public void setLength(int length) {
		this.length = length;
	}


// Get methods
	public int getSortIndex() {
		return this.sortIndex;
	}

	public String getLine() {
		return this.line;
	}

	public char getDelimiter() {
		return this.delimiter;
	}

	public Vector getValues() {
		return this.values;
	}

	public int getLength() {
		return this.length;
	}

}
