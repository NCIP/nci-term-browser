package gov.nih.nci.evs.browser.utils;

import org.LexGrid.LexBIG.DataModel.Collections.ConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.apache.log4j.Logger;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * Search utility for the cart
 * 
 * @author garciawa2
 */
public class SearchCart {

	private static Logger _logger = Logger.getLogger(SearchUtils.class);	
	static LexBIGService lbSvc = null;
	
    /**
     * Constructor
     */
	public SearchCart() {
		if (lbSvc == null) {
			lbSvc = RemoteServerUtil.createLexBIGService();			
        }
	}
	
	/**
	 * Get concept Entity by code
	 * @param codingScheme
	 * @param code
	 * @return
	 */
	public ResolvedConceptReference getConceptByCode(String codingScheme, String code) {
		CodedNodeSet cns = null;
		ResolvedConceptReferencesIterator iterator = null;
		
		try {
			cns = lbSvc.getCodingSchemeConcepts(codingScheme, null);
			ConceptReferenceList crefs =
                createConceptReferenceList(new String[] { code }, codingScheme);
			cns.restrictToCodes(crefs);		
			iterator = cns.resolve(null, null, null);			
			if (iterator.numberRemaining() > 0) {				
				ResolvedConceptReference ref = (ResolvedConceptReference) iterator.next();
				return ref;
			} 
		} catch (LBException e) {
			_logger.info("Error: " + e.getMessage());
		}
		
		return null;
	}

	//
	// Internal utility methods
	//
	
	private ConceptReferenceList createConceptReferenceList(String[] codes,
			String codingSchemeName) {
		if (codes == null)
			return null;
		ConceptReferenceList list = new ConceptReferenceList();
		for (int i = 0; i < codes.length; i++) {
			ConceptReference cr = new ConceptReference();
			cr.setCodingSchemeName(codingSchemeName);
			cr.setConceptCode(codes[i]);
			list.addConceptReference(cr);
		}
		return list;
	}
	
} // End of SearchCart
