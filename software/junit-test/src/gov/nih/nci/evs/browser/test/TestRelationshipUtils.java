package gov.nih.nci.evs.browser.test;


import gov.nih.nci.evs.browser.bean.MappingData;
import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.common.Constants;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.testUtil.*;
import java.io.*;
import java.net.URI;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.Map;
import javax.faces.model.*;
import org.apache.commons.lang.*;
import org.apache.log4j.*;
import org.junit.*;
import org.lexevs.property.PropertyExtension;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.commonTypes.Properties;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.ActiveOption;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.naming.*;
import org.LexGrid.relations.AssociationPredicate;
import org.LexGrid.relations.Relations;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.versions.*;
import static gov.nih.nci.evs.browser.common.Constants.*;


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


public class TestRelationshipUtils extends ServiceTestCase {
    //private static Logger _logger = Logger.getLogger(TestRelationshipUtils.class);
    final static String testID = "TestRelationshipUtils";
	private LexBIGService lbSvc = null;
	private CodingSchemeDataUtils codingSchemeDataUtils = null;
    private	Vector csVec = null;
	private TreeUtils treeUtils = null;
	private TestCaseGenerator testCaseGenerator = null;
	int NUMBER_OF_TEST_CASES = 3;
    private LexBIGServiceConvenienceMethods lbscm = null;
    private RelationshipUtils relationshipUtils = null;

    //@Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		testCaseGenerator = new TestCaseGenerator(lbSvc);
		relationshipUtils = new RelationshipUtils(lbSvc);

        try {
            this.lbscm =
                (LexBIGServiceConvenienceMethods) lbSvc
                    .getGenericExtension("LexBIGServiceConvenienceMethods");
            lbscm.setLexBIGService(lbSvc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		this.treeUtils = new TreeUtils(lbSvc);
	}

	public void setNumberOfTestCases(int number) {
		this.NUMBER_OF_TEST_CASES = number;
	}


    public void dumpHashMap(HashMap hmap) {
		if (hmap == null) return;
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println("\t" + key + ":");
			ArrayList list = (ArrayList) hmap.get(key);
			if (list != null) {
				for (int i=0; i<list.size(); i++) {
					String t = (String) list.get(i);
					System.out.println("\t\t" + t);
				}
		    }
		}
	}


    @Test
    public void testRelationshipUtils() throws Exception {
		RandomVariateGenerator rvGenerator = new RandomVariateGenerator();
	    int m = 0;
	    csVec = codingSchemeDataUtils.getCodingSchemes(true); // excludeMappings
        for (int i = 0; i < csVec.size(); i++) {
            String t = (String) csVec.get(i);
            Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(t);
            String scheme = (String) u.elementAt(0);
            if (scheme != null && scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCI_METATHESAURUS) != 0 &&
                !Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
				String version = (String) u.elementAt(1);
				System.out.println("\n" + scheme + " (" + version + ")");
				ResolvedConceptReferenceList rcrl = testCaseGenerator.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE,
																						TestCaseGenerator.TYPE_TERMINOLOGY);

				int sel_case = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(sel_case);
				String name = rcr.getEntityDescription().getContent();
				String code = rcr.getCode();
				System.out.println("\t" + name + " (" + code + ")");

				String ns = rcr.getCodeNamespace();
				boolean useNamespace = true;

				HashMap hmap = relationshipUtils.getRelationshipHashMap(scheme, version, code, ns, useNamespace);
				if (hmap == null) {
					m++;
				} else {
					dumpHashMap(hmap);
				}
			}
		}
		assertTrue(m == 0);
	}
}
