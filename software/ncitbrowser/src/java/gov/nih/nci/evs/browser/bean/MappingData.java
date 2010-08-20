package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;

public class MappingData
{

// Variable declaration
	private String sourceCode;
	private String sourceName;
	private String sourceCodingScheme;
	private String sourceCodingSchemeVesion;
	private String sourceCodeNamespace;
	private String associationName;
	private String rel;
	private int score;
	private String targetCode;
	private String targetName;
	private String targetCodingScheme;
	private String targetCodingSchemeVesion;
	private String targetCodeNamespace;

// Default constructor
	public MappingData() {
	}

// Constructor
	public MappingData(
		String sourceCode,
		String sourceName,
		String sourceCodingScheme,
		String sourceCodingSchemeVesion,
		String sourceCodeNamespace,
		String associationName,
		String rel,
		int score,
		String targetCode,
		String targetName,
		String targetCodingScheme,
		String targetCodingSchemeVesion,
		String targetCodeNamespace) {

		this.sourceCode = sourceCode;
		this.sourceName = sourceName;
		this.sourceCodingScheme = sourceCodingScheme;
		this.sourceCodingSchemeVesion = sourceCodingSchemeVesion;
		this.sourceCodeNamespace = sourceCodeNamespace;
		this.associationName = associationName;
		this.rel = rel;
		this.score = score;
		this.targetCode = targetCode;
		this.targetName = targetName;
		this.targetCodingScheme = targetCodingScheme;
		this.targetCodingSchemeVesion = targetCodingSchemeVesion;
		this.targetCodeNamespace = targetCodeNamespace;
	}

// Set methods
	public void setSourceCode(String sourceCode) {
		this.sourceCode = sourceCode;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public void setSourceCodingScheme(String sourceCodingScheme) {
		this.sourceCodingScheme = sourceCodingScheme;
	}

	public void setSourceCodingSchemeVesion(String sourceCodingSchemeVesion) {
		this.sourceCodingSchemeVesion = sourceCodingSchemeVesion;
	}

	public void setSourceCodeNamespace(String sourceCodeNamespace) {
		this.sourceCodeNamespace = sourceCodeNamespace;
	}

	public void setAssociationName(String associationName) {
		this.associationName = associationName;
	}

	public void setRel(String rel) {
		this.rel = rel;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void setTargetCode(String targetCode) {
		this.targetCode = targetCode;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public void setTargetCodingScheme(String targetCodingScheme) {
		this.targetCodingScheme = targetCodingScheme;
	}

	public void setTargetCodingSchemeVesion(String targetCodingSchemeVesion) {
		this.targetCodingSchemeVesion = targetCodingSchemeVesion;
	}

	public void setTargetCodeNamespace(String targetCodeNamespace) {
		this.targetCodeNamespace = targetCodeNamespace;
	}


// Get methods
	public String getSourceCode() {
		return this.sourceCode;
	}

	public String getSourceName() {
		return this.sourceName;
	}

	public String getSourceCodingScheme() {
		return this.sourceCodingScheme;
	}

	public String getSourceCodingSchemeVesion() {
		return this.sourceCodingSchemeVesion;
	}

	public String getSourceCodeNamespace() {
		return this.sourceCodeNamespace;
	}

	public String getAssociationName() {
		return this.associationName;
	}

	public String getRel() {
		return this.rel;
	}

	public int getScore() {
		return this.score;
	}

	public String getTargetCode() {
		return this.targetCode;
	}

	public String getTargetName() {
		return this.targetName;
	}

	public String getTargetCodingScheme() {
		return this.targetCodingScheme;
	}

	public String getTargetCodingSchemeVesion() {
		return this.targetCodingSchemeVesion;
	}

	public String getTargetCodeNamespace() {
		return this.targetCodeNamespace;
	}

}
