package gov.nih.nci.evs.browser.test;


import gov.nih.nci.evs.browser.common.*;
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


public class TestSearchUtils extends ServiceTestCase {
	final static String testID = "TestSearchUtils";

	private LexBIGService lbSvc = null;
	private CodingSchemeDataUtils codingSchemeDataUtils = null;
    private	Vector csVec = null;
	private TreeUtils treeUtils = null;
	private TestCaseGenerator testCaseGenerator = null;
	int NUMBER_OF_TEST_CASES = 3;

	SearchUtils searchUtils = null;


    //@Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		treeUtils = new TreeUtils(lbSvc);
		testCaseGenerator = new TestCaseGenerator(lbSvc);
		searchUtils = new SearchUtils(lbSvc);
	}

	public void setNumberOfTestCases(int number) {
		this.NUMBER_OF_TEST_CASES = number;
	}


    @Test
    public void testSearchUtils() throws Exception {
	    int m = 0;
	    csVec = codingSchemeDataUtils.getCodingSchemes(true); // excludeMappings
	    RandomVariateGenerator rvGenerator = new RandomVariateGenerator();
        for (int i = 0; i < csVec.size(); i++) {
            String t = (String) csVec.elementAt(i);
            Vector u = StringUtils.parseData(t);
            String scheme = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			System.out.println("\n" + scheme + " (" + version + ")");
			ResolvedConceptReferenceList rcrl = testCaseGenerator.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE,
			                                                                        TestCaseGenerator.TYPE_TERMINOLOGY);
			int sel_case = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
			ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(sel_case);
			String name = "";
			if (rcr.getEntityDescription() != null) {
			    name = rcr.getEntityDescription().getContent();
			}
			String code = rcr.getCode();

			String matchText = name;
			String matchAlgorithm = gov.nih.nci.evs.browser.common.Constants.EXACT_SEARCH_ALGORITHM; //"exactMatch";

			boolean ranking = true;
			int maxToReturn = -1;
			Vector schemes = new Vector();
			schemes.add(scheme);

			Vector versions = new Vector();
			versions.add(version);
			String source = null;

			ResolvedConceptReferencesIteratorWrapper wrapper = null;

            if (rcr.getEntityDescription() != null) {
			    wrapper = searchUtils.searchByNameAndCode(
				schemes, versions, matchText,
				source, matchAlgorithm, ranking, maxToReturn);
				if (wrapper != null) {
					ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
					try {
						while (iterator.hasNext()) {
							rcr = (ResolvedConceptReference) iterator.next();
							String pt = "";
							if (rcr.getEntityDescription() != null) {
								pt = rcr.getEntityDescription().getContent();
							}
							System.out.println("\tSearch by name:");
							System.out.println("\t" + pt + " (code: " + rcr.getCode() + ")");
							System.out.println("\n");
						}

					} catch (Exception ex) {
						ex.printStackTrace();
						System.out.println("\tException thrown.");
						m++;
					}
				} else {
					System.out.println("\tERROR: No match.");
					m++;
				}
		    } else {
				System.out.println("\tEntityDescription() = null (code: " + code + ") -- name search test skipped.");
			}

			matchText = code;
			wrapper = searchUtils.searchByNameAndCode(
				schemes, versions, matchText,
				source, matchAlgorithm, ranking, maxToReturn);

			if (wrapper != null) {
				ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
				try {
					while (iterator.hasNext()) {
						rcr = (ResolvedConceptReference) iterator.next();
						String pt = "";
						if (rcr.getEntityDescription() != null) {
							pt = rcr.getEntityDescription().getContent();
						}
						System.out.println("\tSearch by code:");
						System.out.println("\t" + pt + " (code: " + rcr.getCode() + ")");
						System.out.println("\n");
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("\tException thrown.");
					m++;
				}
			} else {
				System.out.println("\tERROR: No match.");
				m++;
			}

			boolean excludeDesignation = false;
			wrapper = searchUtils.searchByProperties(
				scheme, version, matchText,
				source, matchAlgorithm, excludeDesignation, ranking, maxToReturn);

			if (wrapper != null) {
				ResolvedConceptReferencesIterator iterator = wrapper.getIterator();
				try {
					while (iterator.hasNext()) {
						rcr = (ResolvedConceptReference) iterator.next();
						String pt = "";
						if (rcr.getEntityDescription() != null) {
							pt = rcr.getEntityDescription().getContent();
						}
						System.out.println("\tSearch by property:");
						System.out.println("\t" + pt + " (code: " + rcr.getCode() + ")");
						System.out.println("\n");
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					System.out.println("\tException thrown.");
					m++;
				}
			} else {
				System.out.println("\tERROR: No match.");
				m++;
			}
		}
		assertTrue(m == 0);
    }

}

