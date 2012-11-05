package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;

/**
  * <!-- LICENSE_TEXT_START -->
* Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
* and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
* in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
* materials provided with the distribution.
* 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
* "This product includes software developed by NGIT and the National Cancer Institute."
* If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
* wherever such third-party acknowledgments normally appear.
* 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
* 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
* the recipient to use any trademarks owned by either NCI or NGIT
* 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
* NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
* PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
* WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
  * <!-- LICENSE_TEXT_END -->
  */

/**
  * @author EVS Team
  * @version 1.0
  *
  * Modification history
  *     Initial implementation kim.ong@ngc.com
  *
 */

public class MappingData
{
    public static final int COL_SOURCE_CODE = 1;
    public static final int COL_SOURCE_NAME = 2;
    public static final int COL_SOURCE_NAMESPACE = 3;
    public static final int COL_REL = 4;
    public static final int COL_SCORE = 5;
    public static final int COL_TARGET_NAME = 7;
    public static final int COL_TARGET_NAMESPACE = 8;


// Variable declaration
	private String sourceCode;
	private String sourceName;
	private String sourceCodingScheme;
	private String sourceCodingSchemeVersion;
	private String sourceCodeNamespace;
	private String associationName;
	private String rel;
	private int score;
	private String targetCode;
	private String targetName;
	private String targetCodingScheme;
	private String targetCodingSchemeVersion;
	private String targetCodeNamespace;

// Default constructor
	public MappingData() {
	}

// Constructor
	public MappingData(
		String sourceCode,
		String sourceName,
		String sourceCodingScheme,
		String sourceCodingSchemeVersion,
		String sourceCodeNamespace,
		String associationName,
		String rel,
		int score,
		String targetCode,
		String targetName,
		String targetCodingScheme,
		String targetCodingSchemeVersion,
		String targetCodeNamespace) {

		this.sourceCode = sourceCode;
		this.sourceName = sourceName;
		this.sourceCodingScheme = sourceCodingScheme;
		this.sourceCodingSchemeVersion = sourceCodingSchemeVersion;
		this.sourceCodeNamespace = sourceCodeNamespace;
		this.associationName = associationName;
		this.rel = rel;
		this.score = score;
		this.targetCode = targetCode;
		this.targetName = targetName;
		this.targetCodingScheme = targetCodingScheme;
		this.targetCodingSchemeVersion = targetCodingSchemeVersion;
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

	public void setSourceCodingSchemeVersion(String sourceCodingSchemeVersion) {
		this.sourceCodingSchemeVersion = sourceCodingSchemeVersion;
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

	public void setTargetCodingSchemeVersion(String targetCodingSchemeVersion) {
		this.targetCodingSchemeVersion = targetCodingSchemeVersion;
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

	public String getSourceCodingSchemeVersion() {
		return this.sourceCodingSchemeVersion;
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

	public String getTargetCodingSchemeVersion() {
		return this.targetCodingSchemeVersion;
	}

	public String getTargetCodeNamespace() {
		return this.targetCodeNamespace;
	}

}
