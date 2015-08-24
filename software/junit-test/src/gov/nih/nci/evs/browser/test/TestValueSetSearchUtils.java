package gov.nih.nci.evs.browser.test;


import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.evs.testUtil.*;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import java.io.*;
import java.text.*;
import java.util.*;
import org.junit.*;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.naming.*;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008-2015 NGIT. This software was developed in conjunction
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
 * @author EVS Team
 * @version 1.0
 *
 * Modification history:
 *     Initial implementation kim.ong@ngc.com
 *
 */


public class TestValueSetSearchUtils extends ServiceTestCase {
    final static String testID = "TestValueSetSearchUtils";

	LexBIGService lbSvc = null;
	CodingSchemeDataUtils codingSchemeDataUtils = null;
    private	Vector csVec = null;
	TreeUtils treeUtils = null;
	TestCaseGenerator testCaseGenerator = null;
	ValueSetSearchUtils vssu = null;
    int NUMBER_OF_TEST_CS = 1;
    int NUMBER_OF_TEST_CASES = 1;
    String serviceUrl = null;

    //@Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		treeUtils = new TreeUtils(lbSvc);
		testCaseGenerator = new TestCaseGenerator(lbSvc);
		vssu = new ValueSetSearchUtils(lbSvc);
		serviceUrl = LexEVSServiceUtil.getServiceUrl();
		System.out.println("ServiceUrl: " + serviceUrl);
		vssu.setServiceUrl(serviceUrl);
	}

	public void setNumberOfTestCSs(int number) {
		this.NUMBER_OF_TEST_CS = number;
	}

	public void setNumberOfTestCases(int number) {
		this.NUMBER_OF_TEST_CASES = number;
	}

    @Test
    public void testValueSetSearch() throws Exception {
	    java.util.List<java.lang.String> list = vssu.listValueSetDefinitionURIs();
	    if (list == null) {
			System.out.println("listValueSetDefinitionURIs returns NULL???");
		} else {
	    	System.out.println("Number of value set definitions: " + list.size());
	    }

	    int m = 0;
	    csVec = codingSchemeDataUtils.getResolvedValueSetCodingSchemes();

	    System.out.println("Number of resolved value set coding schemes: " + csVec.size());

	    RandomVariateGenerator rvGenerator = new RandomVariateGenerator();
        List selectedCSs = rvGenerator.selectWithNoReplacement(NUMBER_OF_TEST_CS, csVec.size()-1);

        System.out.println("selectedCSs.size(): " + selectedCSs.size());

        for (int i = 0; i < selectedCSs.size(); i++) {
			Integer int_obj = (Integer) selectedCSs.get(i);
            String t = (String) csVec.elementAt(int_obj.intValue());

            System.out.println(t);

            Vector u = StringUtils.parseData(t);
            String scheme = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			String formalname = (String) u.elementAt(2);
			String vsd_uri = (String) u.elementAt(3);

			System.out.println("(*) " + scheme + " (" + version + ")" + " " + vsd_uri);
			ResolvedConceptReferenceList rcrl = testCaseGenerator.generateResolvedConceptReferences(scheme, version, NUMBER_OF_TEST_CASES);

            System.out.println("rcrl.getResolvedConceptReferenceCount(): " + rcrl.getResolvedConceptReferenceCount());

			int sel_case = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);

			System.out.println("sel_case: " + sel_case);

			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(sel_case);
			String name = rcr.getEntityDescription().getContent();
			String code = rcr.getCode();

			System.out.println("name: " + name);
			System.out.println("code: " + code);

			//String vsd_uri = "http://ncit:C54453";
			String matchText = name;
			String matchAlgorithm = "exactMatch";
			int maxToReturn = -1;

			System.out.println("(*) search by " + matchText + " using " + matchAlgorithm);

			ResolvedConceptReferencesIterator iterator = vssu.searchByName(
				vsd_uri, matchText, matchAlgorithm, maxToReturn);

			if (iterator == null || !iterator.hasNext()) {
				System.out.println("No match found.");
				m++;
			} else {
				while (iterator.hasNext()) {
					ResolvedConceptReference resovledCR = (ResolvedConceptReference) iterator.next();
					System.out.println(resovledCR.getEntityDescription().getContent()
									   + " (" + resovledCR.getConceptCode() + ")");
			    }
			}

			System.out.println("=======================================");

			matchText = code;
			System.out.println("(*) search by " + matchText + " using " + matchAlgorithm);

			iterator = vssu.searchByCode(
				vsd_uri, matchText, maxToReturn);

			if (iterator == null || !iterator.hasNext()) {
				System.out.println("No match found.");
				m++;
			} else {
				while (iterator.hasNext()) {
					ResolvedConceptReference resovledCR = (ResolvedConceptReference) iterator.next();
					System.out.println(resovledCR.getEntityDescription().getContent()
									   + " (" + resovledCR.getConceptCode() + ")");
			    }
			}

			System.out.println("=======================================");

			matchText = name;
			matchAlgorithm = "contains";
			System.out.println("(*) search by " + matchText + " using " + matchAlgorithm);


			iterator = vssu.searchByName(
				vsd_uri, matchText, matchAlgorithm, maxToReturn);

			if (iterator == null || !iterator.hasNext()) {
				System.out.println("No match found.");
				m++;
			} else {
				while (iterator.hasNext()) {
					ResolvedConceptReference resovledCR = (ResolvedConceptReference) iterator.next();
					System.out.println(resovledCR.getEntityDescription().getContent()
									   + " (" + resovledCR.getConceptCode() + ")");
			    }
			}
			System.out.println("=======================================");

			String checked_vocabularies = vsd_uri;//"http://ncit:C54453";;
			int searchOption = 2;
			matchText = name;
			String algorithm = "contains";
			System.out.println("(*) searchResolvedValueSetCodingSchemes by " + matchText + " using " + matchAlgorithm);

			iterator = vssu.searchResolvedValueSetCodingSchemes(checked_vocabularies,
				matchText, searchOption, algorithm);

			if (iterator == null || !iterator.hasNext()) {
				System.out.println("No match found.");
				m++;
			} else {
				while (iterator.hasNext()) {
					ResolvedConceptReference resovledCR = (ResolvedConceptReference) iterator.next();
					System.out.println(resovledCR.getEntityDescription().getContent()
									   + " (" + resovledCR.getConceptCode() + ")");
			    }
			}
			System.out.println("=======================================");
        }
        assertTrue(m == 0);
    }

    public static void main(String [] args) {
		try {
			TestValueSetSearchUtils test = new TestValueSetSearchUtils();
			test.setUp();
			test.testValueSetSearch();

		} catch (Exception ex) {

		}
	}

}

/*
Number of value set definitions: 11
(1) http://evs.nci.nih.gov/Valueset/C97181
(2) http://evs.nci.nih.gov/valueset/C63923
(3) http://ncit:C54452
(4) http://ncit:C54453 Structured Product Labeling Color Terminology (Code C54453)
(5) http://ncit:C54454
(6) http://ncit:C81222
(7) http://ncit:C81223
(8) http://ncit:C90259
(9) http://ndfrt:MoA
(10) http://ndfrt:PE
(11) http://ndfrt:SC


(*) valueSetSearchAction red
(*) calling ValueSetSearchUtils matchText red
(*) calling ValueSetSearchUtils checked_vocabularies http://ncit:C54453
(*) calling ValueSetSearchUtils searchOption 2
(*) calling ValueSetSearchUtils algorithm contains
(*) ValueSetSearchUtils returns NULL


*/

