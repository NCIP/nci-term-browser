package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;
import java.net.*;

public class HTMLTableSpec
{

// Variable declaration
	private String description;
	private String firstColumnHeading;
	private String secondColumnHeading;
	private int firstPercentColumnWidth;
	private int secondPercentColumnWidth;
	private int qualifierColumn;
	private Vector keyVec;
	private HashMap qualifierHashMap;

// Default constructor
	public HTMLTableSpec() {
	}

// Constructor
	public HTMLTableSpec(
		String description,
		String firstColumnHeading,
		String secondColumnHeading,
		int firstPercentColumnWidth,
		int secondPercentColumnWidth,
		int qualifierColumn,
		Vector keyVec,
		HashMap qualifierHashMap) {

		this.description = description;
		this.firstColumnHeading = firstColumnHeading;
		this.secondColumnHeading = secondColumnHeading;
		this.firstPercentColumnWidth = firstPercentColumnWidth;
		this.secondPercentColumnWidth = secondPercentColumnWidth;
		this.qualifierColumn = qualifierColumn;
		this.keyVec = keyVec;
		this.qualifierHashMap = qualifierHashMap;
	}

// Set methods
	public void setDescription(String description) {
		this.description = description;
	}

	public void setFirstColumnHeading(String firstColumnHeading) {
		this.firstColumnHeading = firstColumnHeading;
	}

	public void setSecondColumnHeading(String secondColumnHeading) {
		this.secondColumnHeading = secondColumnHeading;
	}

	public void setFirstPercentColumnWidth(int firstPercentColumnWidth) {
		this.firstPercentColumnWidth = firstPercentColumnWidth;
	}

	public void setSecondPercentColumnWidth(int secondPercentColumnWidth) {
		this.secondPercentColumnWidth = secondPercentColumnWidth;
	}

	public void setQualifierColumn(int qualifierColumn) {
		this.qualifierColumn = qualifierColumn;
	}

	public void setKeyVec(Vector keyVec) {
		this.keyVec = keyVec;
	}

	public void setQualifierHashMap(HashMap qualifierHashMap) {
		this.qualifierHashMap = qualifierHashMap;
	}


// Get methods
	public String getDescription() {
		return this.description;
	}

	public String getFirstColumnHeading() {
		return this.firstColumnHeading;
	}

	public String getSecondColumnHeading() {
		return this.secondColumnHeading;
	}

	public int getFirstPercentColumnWidth() {
		return this.firstPercentColumnWidth;
	}

	public int getSecondPercentColumnWidth() {
		return this.secondPercentColumnWidth;
	}

	public int getQualifierColumn() {
		return this.qualifierColumn;
	}

	public Vector getKeyVec() {
		return this.keyVec;
	}

	public HashMap getQualifierHashMap() {
		return this.qualifierHashMap;
	}

}
