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



public class MappingIteratorBean
{
    private static Logger _logger = Logger.getLogger(MappingIteratorBean.class);

// Variable declaration
	private Iterator iterator;
	private int numberRemaining;
	private int istart;
	private int iend;
	private int size;
	private int pageNumber;
	private int numberPages;
	private List list;

// Default constructor
	public MappingIteratorBean() {
	}

// Constructor
	public MappingIteratorBean(
		Iterator iterator,
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
		this.list = new ArrayList();
	}

	public MappingIteratorBean(
		Iterator iterator,
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
		this.list = list;
	}

// Set methods
	public void setIterator(Iterator iterator) {
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


// Get methods
	public Iterator getIterator() {
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


    public List getData(int idx1, int idx2) {
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
			while (iterator.hasNext() && list.size() < idx2) {
				ResolvedConceptReference ref = (ResolvedConceptReference) iterator.next();
				int depth = 0;
				String description;
				if(ref.getEntityDescription() == null) {
					description = "NOT AVAILABLE";
				} else {
					description = ref.getEntityDescription().getContent();
				}
				System.out.println("Code: " + ref.getCode() + ", Description: " + description + " Hash: " + ref.hashCode() + " " + "Coding Scheme: " + ref.getCodingSchemeName() + ", Version: " + ref.getCodingSchemeVersion()
					+ ", Namespace: " + ref.getCodeNamespace());

				sourceCode = ref.getCode();
				sourceName = description;
				sourceCodingScheme = ref.getCodingSchemeName();
				sourceCodingSchemeVesion = ref.getCodingSchemeVersion();
				sourceCodeNamespace = ref.getCodeNamespace();

				AssociationList assocs = ref.getSourceOf();
				if(assocs != null){
					for(Association assoc : assocs.getAssociation()){
						associationName = assoc.getAssociationName();
						for(AssociatedConcept ac : assoc.getAssociatedConcepts().getAssociatedConcept()){
							if(ac.getEntityDescription() == null) {
								description = "NOT AVAILABLE";
							} else {
								description = ac.getEntityDescription().getContent();
							}
							System.out.println("Code: " + ac.getCode() + ", Description: " + description + " Hash: " + ac.hashCode() + " " +
							   "Coding Scheme: " + ac.getCodingSchemeName() + ", Version: " + ac.getCodingSchemeVersion() + ", Namespace: " + ac.getCodeNamespace());

							targetCode = ac.getCode();
							targetName = description;
							targetCodingScheme = ac.getCodingSchemeName();
							targetCodingSchemeVesion = ac.getCodingSchemeVersion();
							targetCodeNamespace = ac.getCodeNamespace();

							for (NameAndValue qual : ac.getAssociationQualifiers()
								.getNameAndValue()) {
								String qualifier_name = qual.getName();
								String qualifier_value = qual.getContent();
								if (qualifier_name.compareTo("rel") == 0) {
									rel = qualifier_value;
								} else if (qualifier_name.compareTo("score") == 0) {
									score = Integer.parseInt(qualifier_value);
								}
							}

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
            // ex.printStackTrace();
        }

        _logger.debug("getData Run time (ms): "
            + (System.currentTimeMillis() - ms));
        return copyData(idx1, idx2);
    }

}
