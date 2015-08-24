package gov.nih.nci.evs.browser.test;


import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.testUtil.*;
import java.util.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.commons.codec.language.*;
import org.apache.log4j.*;
import org.junit.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.naming.*;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;


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


public class TestSimpleSearchUtils extends ServiceTestCase {
	final static String testID = "TestSimpleSearchUtils";

	private LexBIGService lbSvc = null;
	private CodingSchemeDataUtils codingSchemeDataUtils = null;
    private	Vector csVec = null;
	private TreeUtils treeUtils = null;
	private TestCaseGenerator testCaseGenerator = null;
	int NUMBER_OF_TEST_CASES = 3;
	LexEVSResolvedValueSetService lrvs = null;

    //@Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		treeUtils = new TreeUtils(lbSvc);
		testCaseGenerator = new TestCaseGenerator(lbSvc);
	}

	public void setNumberOfTestCases(int number) {
		this.NUMBER_OF_TEST_CASES = number;
	}


/*
HL7 (V3 R2.36)
	  (10458:BRTH)
*/

    @Test
    public void testSimpleSearchUtilsByCode() throws Exception {
        int m = 0;
		String scheme = "HL7";
		String version = "V3 R2.36";
		System.out.println(scheme + " (" + version + ")");
		String code = "10458:BRTH";
		System.out.println("search by code: " + code);

		int searchOption = SimpleSearchUtils.BY_CODE;
		String matchText = code;
		String matchAlgorithm = "exactMatch";

		try {
			ResolvedConceptReferencesIterator iterator = new SimpleSearchUtils(lbSvc).search(scheme, version,
				matchText, searchOption, matchAlgorithm);
			int numberRemaining = iterator.numberRemaining();
			while (iterator.hasNext()) {
				ResolvedConceptReference rcr = (ResolvedConceptReference) iterator.next();
				if (rcr == null) {
					System.out.println("rcr == null..");
				} else {
					EntityDescription ed = rcr.getEntityDescription();
					String pt = "";
					if (ed == null) {
						System.out.println("EntityDescription is null");
					} else {
						pt = rcr.getEntityDescription().getContent();
					}
					System.out.println(pt + "(code: " + rcr.getCode() + ")");
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			m++;
		}
		assertTrue(m == 0);
	}


    @Test
    public void testSimpleSearchUtils() throws Exception {
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

			if (rcr.getEntityDescription() != null) {
				int searchOption = SimpleSearchUtils.BY_NAME;
				String matchText = name;
				String matchAlgorithm = gov.nih.nci.evs.browser.common.Constants.EXACT_SEARCH_ALGORITHM; //"exactMatch";

				ResolvedConceptReferencesIterator iterator = new SimpleSearchUtils(lbSvc).search(scheme, version,
					matchText, searchOption, matchAlgorithm);

				if (iterator != null) {
					try {
						while (iterator.hasNext()) {
							rcr = (ResolvedConceptReference) iterator.next();
							String pt = "";
							if (rcr.getEntityDescription() != null) {
								pt = rcr.getEntityDescription().getContent();
							}
							System.out.println("\t" + pt + " (code: " + rcr.getCode() + ")");
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
		}
		assertTrue(m == 0);
    }


}

