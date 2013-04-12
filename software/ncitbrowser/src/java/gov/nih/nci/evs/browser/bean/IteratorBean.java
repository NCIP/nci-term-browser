/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.bean;


import java.io.*;
import java.util.*;

import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.concepts.Concept;

import gov.nih.nci.evs.browser.common.Constants;
import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public class IteratorBean extends Object {
    static int DEFAULT_MAX_RETURN = 100;
    ResolvedConceptReferencesIterator iterator = null;
    int size = 0;
    List list = null;

    int pageNumber;
    int pageSize;
    int startIndex;
    int endIndex;
    int numberOfPages;

    int lastResolved;
    int maxReturn = 100;
    String message = null;

    String key = null;
    boolean timeout = false;

	public IteratorBean(ResolvedConceptReferencesIterator iterator) {
		this.iterator = iterator;
		this.maxReturn = DEFAULT_MAX_RETURN;
		initialize();
	}


    public IteratorBean(ResolvedConceptReferencesIterator iterator, int maxReturn) {
		this.iterator = iterator;
		this.maxReturn = maxReturn;
		initialize();
	}

	public int getNumberOfPages() {
		return this.numberOfPages;
	}


    public void setIterator(ResolvedConceptReferencesIterator iterator) {
		this.iterator = iterator;
		this.maxReturn = DEFAULT_MAX_RETURN;
		initialize();
	}

    public ResolvedConceptReferencesIterator getIterator() {
		return this.iterator;
	}

	public boolean getTimeout() {
		return timeout;
	}

	public void initialize() {
		try {
			if (iterator == null) {
				this.size = 0;
			} else {
				this.size = iterator.numberRemaining();
		    }
			this.pageNumber = 1;
			this.list = new ArrayList(size);
			for (int i=0; i<size; i++) {
			    list.add(null);
			}
			this.pageSize = Constants.DEFAULT_PAGE_SIZE;
			this.numberOfPages = size / pageSize;
			if (this.pageSize * this.numberOfPages < size) {
				this.numberOfPages = this.numberOfPages + 1;
			}
			this.lastResolved = -1;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public int getMumberOfPages() {
		return this.numberOfPages;
	}

	public int getSize() {
		return this.size;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getLastResolved() {
		return this.lastResolved;
	}

	public int getStartIndex(int pageNumber) {
		startIndex = (pageNumber-1) * pageSize;
		if (startIndex < 0) startIndex = 0;
		return startIndex;
	}


	public int getEndIndex(int pageNumber) {
		endIndex = pageNumber * pageSize - 1;
		if (endIndex > (size-1)) endIndex = size-1;
		return endIndex;
	}


	public List getData(int pageNumber) {
		int idx1 = getStartIndex(pageNumber);
		int idx2 = getEndIndex(pageNumber);
		return getData(idx1, idx2);
	}


	public List getData(int idx1, int idx2) {
		System.out.println("Retrieving data (from: " + idx1 + " to: " + idx2 + ")");
		long ms = System.currentTimeMillis();
		long dt = 0;
		long total_delay = 0;
		timeout = false;
        try {
			while(iterator != null && iterator.hasNext() && lastResolved < idx2) {
				ResolvedConceptReference[] refs = iterator.next(maxReturn).getResolvedConceptReference();
				for(ResolvedConceptReference ref : refs) {
					//displayRef(ref);
					lastResolved++;
					this.list.set(lastResolved, ref);
				}
				dt = System.currentTimeMillis() - ms;
				ms = System.currentTimeMillis();
				total_delay = total_delay + dt;
				if (total_delay > NCItBrowserProperties.getPaginationTimeOut() * 60 * 1000) {
					timeout = true;
					System.out.println("Time out at: " + lastResolved);
					break;
				}
			}

		} catch (Exception ex) {
			//ex.printStackTrace();
		}


		/*
		for (int i=idx1; i<=idx2; i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) this.list.get(i);
			rcr_list.add(rcr);
			if (i > lastResolved) break;
		}
		*/

		Vector temp_vec = new Vector();
		for (int i=idx1; i<=idx2; i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) this.list.get(i);
			temp_vec.add(rcr);
			if (i > lastResolved) break;
		}
		List rcr_list = new ArrayList();
		for (int i=0; i<temp_vec.size(); i++) {
			rcr_list.add(null);
		}

		for (int i=0; i<temp_vec.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) temp_vec.elementAt(i);
			rcr_list.set(i, rcr);
		}

		System.out.println("getData Run time (ms): "
					+ (System.currentTimeMillis() - ms));
		return rcr_list;
	}


	protected void displayRef(ResolvedConceptReference ref){
		System.out.println(ref.getConceptCode() + ":" + ref.getEntityDescription().getContent());
	}

	protected void displayRef(int k, ResolvedConceptReference ref){
		System.out.println("(" + k + ") " + ref.getCodingSchemeName() + " " + ref.getConceptCode() + ":" + ref.getEntityDescription().getContent());
	}

	protected void displayRef(OutputStreamWriter osWriter, int k, ResolvedConceptReference ref){
		try {
			osWriter.write("(" + k + ") " + ref.getConceptCode() + ":" + ref.getEntityDescription().getContent() + "\n");
		} catch (Exception ex) {

		}
	}

	public void dumpData(List list) {
		if (list == null) {
			System.out.println("WARNING: dumpData list = null???");
			return;
		}
		for (int i=0; i<list.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
			int j = i+1;
			displayRef(j, rcr);
		}
	}

	public void dumpData(OutputStreamWriter osWriter, List list) {
		if (list == null) {
			System.out.println("WARNING: dumpData list = null???");
			return;
		}
		for (int i=0; i<list.size(); i++) {
			ResolvedConceptReference rcr = (ResolvedConceptReference) list.get(i);
			int j = i+1;
			displayRef(osWriter, j, rcr);
		}
	}

    public void setKey(String key) {
		this.key = key;
	}


    public String getKey() {
		return this.key;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return this.message;
	}

}