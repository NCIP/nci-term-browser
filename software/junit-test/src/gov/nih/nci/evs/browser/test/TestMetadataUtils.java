package gov.nih.nci.evs.browser.test;


import gov.nih.nci.evs.browser.bean.*;
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
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
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


public class TestMetadataUtils extends ServiceTestCase {
    final static String testID = "TestMetadataUtils";

	private LexBIGService lbSvc;
	private CodingSchemeDataUtils codingSchemeDataUtils = null;
	private MetadataUtils metadataUtils = null; //new MetadataUtils();
    private	Vector csVec = null;

    @Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		metadataUtils = new MetadataUtils(lbSvc);
	}

    @Override
    protected String getTestID() {
        return testID;
    }

//public static final String  TERMINOLOGY_VALUE_SET = "Terminology Value Set";
//String TERMINOLOGY_VALUE_SET_ALT_NAME = gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET.replaceAll(" ", "_");

    @Test
    public void testMetadataUtils() throws Exception {
	    boolean bool_val = true;
        csVec = codingSchemeDataUtils.getCodingSchemes();
	    int m = 0;
        for (int i = 0; i < csVec.size(); i++) {
            String t = (String) csVec.get(i);
            Vector u = StringUtils.parseData(t);
            String scheme = (String) u.elementAt(0);
            if (scheme != null && !Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
				String version = (String) u.elementAt(1);
				System.out.println(scheme + " (" + version + ")");
				String urn = null;
				String displayName = metadataUtils.getMetadataValue(scheme, version, urn, gov.nih.nci.evs.testUtil.Constants.DISPLAY_NAME);
				//int n = m;
				if (displayName == null || displayName.length() == 0) {
					m++;
					System.out.println("WARNING: metadata " + gov.nih.nci.evs.testUtil.Constants.DISPLAY_NAME + " is not defined for " + scheme
					  + " (version: " + version + ")");
				} else {
					System.out.println("\tdisplayName: " + displayName);
				}
				String fullName = metadataUtils.getMetadataValue(scheme, version, urn, gov.nih.nci.evs.testUtil.Constants.FULL_NAME);
				if (fullName == null || fullName.length() == 0) {
					m++;
					System.out.println("WARNING: metadata " + gov.nih.nci.evs.testUtil.Constants.FULL_NAME + " is not defined for " + scheme
					  + " (version: " + version + ")");
				} else {
					System.out.println("\tfullName: " + fullName);
				}				String termBrowserVersion = metadataUtils.getMetadataValue(scheme, version, urn, gov.nih.nci.evs.testUtil.Constants.TERM_BROWSER_VERSION);
				if (termBrowserVersion == null || termBrowserVersion.length() == 0) {
					m++;
					System.out.println("WARNING: metadata " + gov.nih.nci.evs.testUtil.Constants.TERM_BROWSER_VERSION + " is not defined for " + scheme
					  + " (version: " + version + ")");
				} else {
					System.out.println("\ttermBrowserVersion: " + termBrowserVersion);
				}
				/*
				if (n == m) {
					System.out.println(scheme + " (" + version + ") " + displayName + " " + " " + fullName + " " + termBrowserVersion);
				}
				*/
		    }
		}
		assertTrue(m == 0);
    }

	public static void main(String [] args) {
		try {
			TestMetadataUtils test = new TestMetadataUtils();
			test.setUp();
			test.testMetadataUtils();

		} catch (Exception ex) {

		}
	}
}


