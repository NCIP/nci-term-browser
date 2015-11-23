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
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.Entity;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Impl.Extensions.tree.dao.iterator.ChildTreeNodeIterator;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverter;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverterFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTree;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTreeNode.ExpandableStatus;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTreeNode;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeService;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeServiceFactory;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;
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


public class TestViewInHierarchyUtils extends ServiceTestCase {
    final static String testID = "TestViewInHierarchyUtils";
    private int MAX_CHILDREN = 5;

	LexBIGService lbSvc = null;
	CodingSchemeDataUtils codingSchemeDataUtils = null;
	TestCaseGenerator testCaseGenerator = null;
    private	Vector csVec = null;
    ViewInHierarchyUtils vihu = null;//new ViewInHierarchyUtils(lbSvc);
    ConceptDetails cd = null;
    MetadataUtils metadataUtils = null;

    @Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		testCaseGenerator = new TestCaseGenerator(lbSvc);
		csVec = codingSchemeDataUtils.getCodingSchemes();
		vihu = new ViewInHierarchyUtils(lbSvc);
		cd = new ConceptDetails(lbSvc);
		metadataUtils = new MetadataUtils(lbSvc);

	}

    @Override
    protected String getTestID() {
        return testID;
    }

    @Test
    public void testViewInHierarchy() throws Exception {
	    int m = 0;
	    csVec = codingSchemeDataUtils.getCodingSchemes(true); // excludeMappings
	    RandomVariateGenerator rvGenerator = new RandomVariateGenerator();

	    System.out.println("Number of coding schemes: " + csVec.size());
        int lcv = 0;
        for (int i = 0; i < csVec.size(); i++) {
            String t = (String) csVec.get(i);
            System.out.println(t);
            Vector u = StringUtils.parseData(t);
            String scheme = (String) u.elementAt(0);

            if (scheme != null && scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCI_METATHESAURUS) != 0 &&
                !Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
				String version = (String) u.elementAt(1);
				if (metadataUtils.tree_access_allowed(scheme, version)) {
					lcv++;
					System.out.println("(" + lcv + ") " + scheme + " (" + version + ")");
					long ms = System.currentTimeMillis();
					ResolvedConceptReferenceList rcrl = testCaseGenerator.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE,
														TestCaseGenerator.TYPE_TERMINOLOGY);
					if (rcrl == null || rcrl.getResolvedConceptReferenceCount() == 0) {
						m++;
						System.out.println("\tERROR: generateTestCases failed.");
					} else {
						int sel_case = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
						ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(sel_case);
						String name = rcr.getEntityDescription().getContent();
						String code = rcr.getCode();
						String namespace = rcr.getCodeNamespace();

						System.out.println("code: " + code);
						System.out.println("namespace: " + namespace);

						try {
							String jsonstring = getJsonString(scheme, version, code, namespace);
							System.out.println("\t" + jsonstring);
						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("\tERROR: Unable to create View in Hierarchy JSON string.");
							m++;
						}
					}
					System.out.println("Total run time (ms): " + (System.currentTimeMillis() - ms));
				}
				System.out.println("\n");
		    } else {
				System.out.println("(*) " + scheme + " -- Tree not available.");
			}
		}
		assertTrue(m == 0);
	}


    public String getJsonString(String scheme, String version, String code, String namespace) throws Exception {
		long ms = System.currentTimeMillis();
	    boolean bool_val = true;

		if (version == null) {
			version = cd.getVocabularyVersionByTag(scheme, "PRODUCTION");
		}

		if (namespace == null) {
			 Entity e = cd.getConceptByCode(scheme, version, code);
			 namespace = e.getEntityCodeNamespace();
		}



		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) versionOrTag.setVersion(version);

		TreeService treeService =
			TreeServiceFactory.getInstance().getTreeService(lbSvc);//RemoteServerUtil.createLexBIGService());
		System.out.println("TreeServiceFactory.getInstance().getTreeService run time (ms): " + (System.currentTimeMillis() - ms));
		ms = System.currentTimeMillis();

		LexEvsTree lexEvsTree = null;
		if (StringUtils.isNullOrBlank(namespace)) {
			lexEvsTree = treeService.getTree(scheme, versionOrTag, code);
			System.out.println("treeService.getTree #1 run time (ms): " + (System.currentTimeMillis() - ms));

		} else {
			lexEvsTree = treeService.getTree(scheme, versionOrTag, code, namespace);
			System.out.println("treeService.getTree #2 run time (ms): " + (System.currentTimeMillis() - ms));
		}

		String jsonString =
		treeService.getJsonConverter().buildJsonPathFromRootTree(lexEvsTree.getCurrentFocus());
		System.out.println(jsonString);
		return jsonString;

		//TreeItem ti = JSON2TreeItem.json2TreeItem(jsonString);
		//TreeItem.printTree(ti, 0);
	}


    public List<LexEvsTreeNode> getChildren(String codingScheme, String version, String parent_code, boolean from_root) {
		return vihu.getChildren(codingScheme, version, parent_code, from_root);
    }

    public HashMap getRemainingSubconcepts(String codingScheme, String version, String focus_code, boolean from_root) {
		return vihu.getRemainingSubconcepts(codingScheme, version, focus_code, from_root);
    }


	public static void main(String [] args) {
		try {
			TestViewInHierarchyUtils test = new TestViewInHierarchyUtils();
			test.setUp();
			test.testViewInHierarchy();
		} catch (Exception ex) {

		}
	}
}

