package gov.nih.nci.evs.browser.bean;

import java.io.*;
import java.util.*;

import org.apache.log4j.*;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.naming.SupportedAssociation;
import org.LexGrid.relations.Relations;
import org.LexGrid.relations.AssociationPredicate;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

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

public class MappingIteratorBean
{
    private static Logger _logger = Logger.getLogger(MappingIteratorBean.class);

// Variable declaration
	//private Iterator iterator;
	private ResolvedConceptReferencesIterator iterator;
	private int numberRemaining;
	private int istart;
	private int iend;
	private int size;
	private int pageNumber;
	private int pageSize;
	private int numberPages;
	private List list;

// Default constructor
	public MappingIteratorBean() {
	}

// Constructor
	public MappingIteratorBean(
		ResolvedConceptReferencesIterator iterator,
		int numberRemaining,
		int istart,
		int iend,
		int size,
		int pageNumber,
		int numberPages) {

		this.iterator = iterator;
		this.numberRemaining = numberRemaining;
		this.istart = istart;
		this.iend = iend;
		this.size = size;
		this.pageNumber = pageNumber;
		this.numberPages = numberPages;
		this.pageSize = 50;
		this.list = new ArrayList();
	}

	public MappingIteratorBean(
		ResolvedConceptReferencesIterator iterator,
		int numberRemaining,
		int istart,
		int iend,
		int size,
		int pageNumber,
		int numberPages,
		List list) {

		this.iterator = iterator;
		this.numberRemaining = numberRemaining;
		this.istart = istart;
		this.iend = iend;
		this.size = size;
		this.pageNumber = pageNumber;
		this.numberPages = numberPages;
		this.pageSize = 50;
		this.list = list;
	}

	public void initialize(
		ResolvedConceptReferencesIterator iterator,
		int numberRemaining,
		int istart,
		int iend,
		int size,
		int pageNumber,
		int numberPages) {

		this.iterator = iterator;
		this.numberRemaining = numberRemaining;
		this.istart = istart;
		this.iend = iend;
		this.size = size;
		this.pageNumber = pageNumber;
		this.numberPages = numberPages;
		this.pageSize = 50;
		this.list = new ArrayList();
	}


// Set methods
	public void setIterator(ResolvedConceptReferencesIterator iterator) {
		this.iterator = iterator;
	}

	public void setNumberRemaining(int numberRemaining) {
		this.numberRemaining = numberRemaining;
	}

	public void setIstart(int istart) {
		this.istart = istart;
	}

	public void setIend(int iend) {
		this.iend = iend;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public void setNumberPages(int numberPages) {
		this.numberPages = numberPages;
	}

	public void setList(List list) {
		this.list = list;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

// Get methods
	public ResolvedConceptReferencesIterator getIterator() {
		return this.iterator;
	}

	public int getNumberRemaining() {
		return this.numberRemaining;
	}

	public int getIstart() {
		return this.istart;
	}

	public int getIend() {
		return this.iend;
	}

	public int getSize() {
		return this.size;
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public int getNumberPages() {
		return this.numberPages;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public List getList() {
		return this.list;
	}


    public List copyData(int idx1, int idx2) {
		List arrayList = new ArrayList();
		int upper = idx2;
		int max = list.size();
		if (max < idx2) upper = max;

		for (int i=idx1; i<upper; i++) {
			MappingData mappingData = (MappingData) list.get(i);
			arrayList.add(mappingData);
		}
		return arrayList;
	}


/*
Code: C3191, Description: Lip Neoplasm Hash: 22808536 Coding Scheme: NCI_Thesaurus, Version: 1.0, Namespace: NCI_Thesaurus
 -> Association: mapsTo Container: null
 ->  -> Code: 140, Description: Malignant neoplasm of lip Hash: 17244592 CodingScheme: ICD_9_CM, Version: 1.0, Namespace: ICD_9_CM

Code: C3490, Description: Lip Carcinoma Hash: 20230270 Coding Scheme: NCI_Thesaurus, Version: 1.0, Namespace: NCI_Thesaurus
 -> Association: mapsTo Container: null
 ->  -> Code: 140, Description: Malignant neoplasm of lip Hash: 3098834 Coding Scheme: ICD_9_CM, Version: 1.0, Namespace: ICD_9_CM

Code: C4042, Description: Lip Squamous Cell Carcinoma Hash: 5626173 Coding Scheme: NCI_Thesaurus, Version: 1.0, Namespace: NCI_Thesaurus
 -> Association: mapsTo Container: null
 ->  -> Code: 140, Description: Malignant neoplasm of lip Hash: 2715510 Coding Scheme: ICD_9_CM, Version: 1.0, Namespace: ICD_9_CM

Code: C4588, Description: Stage 0 Lip Cancer Hash: 5555373 Coding Scheme: NCI_Thesaurus, Version: 1.0, Namespace: NCI_Thesaurus
 -> Association: mapsTo Container: null
 ->  -> Code: 140, Description: Malignant neoplasm of lip Hash: 20738936 CodingScheme: ICD_9_CM, Version: 1.0, Namespace: ICD_9_CM
*/


    public MappingData convertMappingData(ResolvedConceptReference rcr) {

		return null;
	}





    public List getData(int idx1, int idx2) {

System.out.println("MappingIteratorBean idx1: " + idx1);
System.out.println("MappingIteratorBean idx2: " + idx2);


		MappingData mappingData = null;
        if (list.size() >= idx2) {
			return copyData(idx1, idx2);
		}

        _logger.debug("Retrieving mapping data (from: " + idx1 + " to: " + idx2 + ")");
        long ms = System.currentTimeMillis();
        long dt = 0;
        long total_delay = 0;

		String sourceCode = null;
		String sourceName = null;
		String sourceCodingScheme = null;
		String sourceCodingSchemeVesion = null;
		String sourceCodeNamespace = null;
		String associationName = null;
		String rel = null;
		int score = 0;
		String targetCode = null;
		String targetName = null;
		String targetCodingScheme = null;
		String targetCodingSchemeVesion = null;
		String targetCodeNamespace = null;

        try {
			if (iterator == null) {
				_logger.debug("iterator == null???");
			} else if (!iterator.hasNext()) {
				_logger.debug("iterator is empty???");
			}

			while (iterator.hasNext() && list.size() < idx2) {
				ResolvedConceptReference ref = (ResolvedConceptReference) iterator.next();
				int depth = 0;
				String description;

				if(ref.getEntityDescription() == null) {
					description = "NOT AVAILABLE";
				} else {
					description = ref.getEntityDescription().getContent();
				}
				//System.out.println("Code: " + ref.getCode() + ", Description: " + description + " Hash: " + ref.hashCode() + " " + "Coding Scheme: " + ref.getCodingSchemeName() + ", Version: " + ref.getCodingSchemeVersion()
				//	+ ", Namespace: " + ref.getCodeNamespace());

				sourceCode = ref.getCode();
				sourceName = description;
				sourceCodingScheme = ref.getCodingSchemeName();
				sourceCodingSchemeVesion = ref.getCodingSchemeVersion();
				sourceCodeNamespace = ref.getCodeNamespace();

				rel = null;
				score = 0;

				AssociationList assocs = ref.getSourceOf();
				if(assocs != null){
					for(Association assoc : assocs.getAssociation()){
						associationName = assoc.getAssociationName();

						//System.out.println("\tassociationName: " + associationName);
						int lcv = 0;
						for(AssociatedConcept ac : assoc.getAssociatedConcepts().getAssociatedConcept()){
							lcv++;
							if(ac.getEntityDescription() == null) {
								description = "NOT AVAILABLE";
							} else {
								description = ac.getEntityDescription().getContent();
							}
							//System.out.println("\t(" + lcv + ") Code: " + ac.getCode() + ", Description: " + description + " Hash: " + ac.hashCode() + " " +
							//   "Coding Scheme: " + ac.getCodingSchemeName() + ", Version: " + ac.getCodingSchemeVersion() + ", Namespace: " + ac.getCodeNamespace());
                            //System.out.println("====================================================");
							targetCode = ac.getCode();
							targetName = description;
							targetCodingScheme = ac.getCodingSchemeName();
							targetCodingSchemeVesion = ac.getCodingSchemeVersion();
							targetCodeNamespace = ac.getCodeNamespace();

                            if (ac.getAssociationQualifiers() != null && ac.getAssociationQualifiers().getNameAndValue() != null) {
								for (NameAndValue qual : ac.getAssociationQualifiers().getNameAndValue()) {
									String qualifier_name = qual.getName();
									String qualifier_value = qual.getContent();
									if (qualifier_name.compareTo("rel") == 0) {
										rel = qualifier_value;
									} else if (qualifier_name.compareTo("score") == 0) {
										score = Integer.parseInt(qualifier_value);
									}
								}
						    }

							//System.out.println("\t\tREL: " + rel + " score: " + score);
							mappingData = new MappingData(
								sourceCode,
								sourceName,
								sourceCodingScheme,
								sourceCodingSchemeVesion,
								sourceCodeNamespace,
								associationName,
								rel,
								score,
								targetCode,
								targetName,
								targetCodingScheme,
								targetCodingSchemeVesion,
								targetCodeNamespace);
							list.add(mappingData);

						}
					}
				}
			}


        } catch (Exception ex) {
            ex.printStackTrace();
        }

        _logger.debug("getData Run time (ms): "
            + (System.currentTimeMillis() - ms));
        return copyData(idx1, idx2);
    }

}
