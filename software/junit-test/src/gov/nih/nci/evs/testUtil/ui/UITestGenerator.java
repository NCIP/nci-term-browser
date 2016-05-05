package gov.nih.nci.evs.testUtil.ui;


import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.testUtil.*;
import java.io.*;
import java.net.*;
import java.util.*;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;


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


public class UITestGenerator extends BaseUITestGenerator {

   private String outputfile = null;
   private String packageName = null;
   private String className = null;
   private String testMethodName = null;
   private String baseUrl = null; //browser home
   private String remoteWebDriverURL = null; //browser home


   private HashMap rootHashMap = null;
   private String serviceUrl = null; // lexevsapi service url
   private RandomVariateGenerator rvGenerator = null;

   private Vector terminology_bean_vec = null;
   private HashMap label2TerminologyHashMap = null;
   private String CHROME_SERVER_URL = "http://localhost:9515";

   private Vector rootHashmapKeys = null;
   private CodeGeneratorConfiguration config = null;
   private Vector browserHomeDisplayLabels = null;
   private Vector mappingCodingSchemeDisplayLabels = null;
   private Vector mappingTestCases = null;
   private String mapping_display_label = null;
   private String mapping_coding_scheme_name = null;
   private String mapping_coding_scheme_version = null;

   private CodingSchemeDataUtils codingSchemeDataUtils = null;

   private int test_counter = 0;

   private String NCIt_PROD_Version = null;
   private String NCIt_PROD_Version_DisplayLabel = null;

   private LexBIGService lbSvc = null;

   private TestUtils testUtils = null;
   private Vector mappingDisplayLabels = null;
   private Vector RVSCSVec = null;
   private HashMap displayLabel2TerminologyBeanHashMap = null;
   private HashMap displayLabel2MappingBeanHashMap = null;

   private ConceptDetails conceptDetails = null;
   private TestCaseWriter testCaseWriter = null;

   private PrintWriter pw = null;
   private MetadataUtils metadataUtils = null;


   //private int SAMPLE_SIZE = 100;

   public UITestGenerator(CodeGeneratorConfiguration config) {
	   this.config = config;
       initialize();
   }

   public boolean validateBrowserHomeDisplayLabels() {
	   for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		   String label = (String) browserHomeDisplayLabels.elementAt(i);
		   int j=i+1;
		   System.out.println("(" + j + ") " + label);
	   }

	   for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		   String label = (String) browserHomeDisplayLabels.elementAt(i);
		   if (!displayLabel2TerminologyBeanHashMap.containsKey(label)) {
			   System.out.println("WARNING: browser label " + label + " cannot be identified.");
			   return false;
		   }
	   }
	   return true;
   }

   public static boolean isLicensed(String displayLabel) {
	   if (displayLabel == null) return false;
	   Vector u = StringUtils.parseData(displayLabel, ":");
	   if (u == null) return false;
	   String csn = (String) u.elementAt(0);
	   if (Arrays.asList(Constants.LICENSED_TERMINOLOGY).contains(csn)) {
		   return true;
	   }
	   return false;
   }


   public void initialize() {
	   test_counter = 0;
	   rvGenerator = new RandomVariateGenerator();

	   this.outputfile = config.getClassName() + ".java";
	   testUtils = new TestUtils(config.getServiceUrl());
	   remoteWebDriverURL = config.getRemoteWebDriverURL();
	   if (remoteWebDriverURL == null || remoteWebDriverURL.compareTo("") == 0) {
		   remoteWebDriverURL = CHROME_SERVER_URL;
	   } else {
		   CHROME_SERVER_URL = remoteWebDriverURL;
	   }

	   lbSvc = testUtils.getLexBIGService();
	   conceptDetails = new ConceptDetails(lbSvc);
	   metadataUtils = new MetadataUtils(lbSvc);

	   codingSchemeDataUtils = testUtils.getCodingSchemeDataUtils();
	   mappingDisplayLabels = testUtils.getMappingDisplayLabels();
	   displayLabel2TerminologyBeanHashMap = testUtils.getDisplayLabel2TerminologyBeanHashMap();
	   displayLabel2MappingBeanHashMap = testUtils.getDisplayLabel2MappingBeanHashMap();
	   RVSCSVec = testUtils.getResolvedValueSetCodingSchemes();
	   System.out.println("Generating Selenium test cases...");
	   browserHomeDisplayLabels = new TermBrowserHomeParser().getTerminologyDisplayLabelsUnsorted(config.getBaseUrl());

	   NCIt_PROD_Version = codingSchemeDataUtils.getVocabularyVersionByTag("NCI Thesaurus", "PRODUCTION");
	   System.out.println("NCIt_PROD_Version: " + NCIt_PROD_Version);
	   NCIt_PROD_Version_DisplayLabel = "NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")";
       System.out.println("NCIt_PROD_Version_DisplayLabel: " + NCIt_PROD_Version_DisplayLabel);

       boolean result = validateBrowserHomeDisplayLabels();
       if (!result) {
		   System.out.println("Validation of browser Home links failed -- program aborts.");
		   System.exit(0);
	   }
	   testMethodName = getTestMethodName();
   }

   public void validateTerminologyLinks() {
	   System.out.println("Number of links: " + browserHomeDisplayLabels.size());
	   for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		   String displayLabel = (String) browserHomeDisplayLabels.elementAt(i);
		   System.out.println("\tlink: " + displayLabel);
		   if (rootHashMap.containsKey(displayLabel)) {
			   System.out.println("\t" + displayLabel + " found on server.");
		   } else {
			   System.out.println("\tWARNING: " + displayLabel + " NOT found on server.");
		   }
	   }
   }

   public String getTestMethodName() {
	   String className = config.getClassName();
	   String firstChar = className.substring(0, 1);
	   firstChar = firstChar.toLowerCase();
	   return firstChar + className.substring(1, className.length());
   }

   public static void printSetUp(PrintWriter out, String CHROME_SERVER_URL, String baseUrl, String serviceUrl) {
	   out.println("  @Before");
       out.println("  public void setUp() throws Exception {");
       String meddra_token = ServiceTestCase.MEDDRA_TOKEN;
       out.println("    SimpleRemoteServerUtil lexEVSSvr = new SimpleRemoteServerUtil(\"" + serviceUrl + "\");");
       if (meddra_token != null && ServiceTestCase.MEDDRA_NAMES.length > 0) {
		   out.println("	Vector names = new Vector();");
		   out.println("	Vector values = new Vector();");
		   out.println("	String name = null;");
		   out.println("	String value = null;");
		   out.println("	String meddra_name = null;");
		   for (int i=0; i<ServiceTestCase.MEDDRA_NAMES.length; i++) {
			   String nm = ServiceTestCase.MEDDRA_NAMES[i];
			   out.println("	meddra_name = \"" + nm + "\";");
			   out.println("	names.add(meddra_name);");
			   out.println("	values.add(\"" + meddra_token + "\");");
		   }
		   out.println("	lexEVSSvr.setSecurityTokens(names, values);");
       }
       out.println("    lbSvc = lexEVSSvr.getLexBIGService(\"" + serviceUrl + "\");");
	   out.println("    simpleSearchUtils = new SimpleSearchUtils(lbSvc);");
	   out.println("    mappingSearchUtils = new MappingSearchUtils(lbSvc);");
	   out.println("    valueSetSearchUtils = new ValueSetSearchUtils(lbSvc);");
	   out.println("    conceptDetails = new ConceptDetails(lbSvc);");
	   out.println("    searchUtils = new SearchUtils(lbSvc);");
       out.println("\n");
       out.println("    driver = new RemoteWebDriver(new URL(\"" + CHROME_SERVER_URL + "\"), DesiredCapabilities.chrome());");
       out.println("    serviceUrl = \"" + serviceUrl + "\";");
       out.println("    baseUrl = \"" + baseUrl + "\";");
       out.println("    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);");
       out.println("    Thread.sleep(1000);");
       out.println("    driver.get(baseUrl + \"/ncitbrowser/pages/multiple_search.jsf\");");
       out.println("  }");
   }


   public void run() {
	   long ms = System.currentTimeMillis();
	   pw = null;
	   boolean success = true;
	   try {
   		   pw = new PrintWriter(outputfile, "UTF-8");
		   String browserHomePage = "/ncitbrowser/pages/multiple_search.jsf";
		   testCaseWriter = new TestCaseWriter(
			   pw, remoteWebDriverURL, serviceUrl, baseUrl, browserHomePage);
           testCaseWriter.setDelay(config.getDelay());
		   if (config.getPackageName() != null) {
		   	   printPackageStatement(pw, config.getPackageName());
		   }
		   printImportStatements(pw);
		   printLicenseStatement(pw);
		   printClassDefinition(pw, config.getClassName());
		   printSetUp(pw, CHROME_SERVER_URL, config.getBaseUrl(), config.getServiceUrl());

		   printWindowUtils(pw);
		   //printTestStatement(pw, "NCItHomeLinks");
		   //printTestNCItHomeLinks(pw);
		   //printTestStatement(pw, "HelpInfo");
		   //printTestHelpInfo(pw);


           System.out.println("generateAllTerminologySearchTests...");
           generateAllTerminologySearchTests(1);
           System.out.println("generateMultipleSearchTests...");
           generateMultipleSearchTests(ServiceTestCase.TERMINOLOGY_SAMPLE_SIZE);
           System.out.println("generateSimpleSearchTests...");
           generateSimpleSearchTests(ServiceTestCase.TERMINOLOGY_SAMPLE_SIZE);
           System.out.println("generateAdvancedSearchTests...");
           generateAdvancedSearchTests(ServiceTestCase.TERMINOLOGY_SAMPLE_SIZE);
           System.out.println("generateMappingSearchTests...");
           generateMappingSearchTests(ServiceTestCase.MAPPING_SAMPLE_SIZE);
           System.out.println("generateValueSetSearchTests...");
           generateValueSetSearchTests(ServiceTestCase.VALUE_SET_SAMPLE_SIZE);
           System.out.println("generateAllValueSetSearchTest...");
           generateAllValueSetSearchTest(ServiceTestCase.VALUE_SET_SAMPLE_SIZE);
           System.out.println("generateViewHierarchyTests...");
           generateViewHierarchyTests();
           System.out.println("generateViewInHierarchyTests...");
           generateViewInHierarchyTests();
           System.out.println("printBaseURLExternalLinksTest...");
           printBaseURLExternalLinksTest(pw);
           System.out.println("printTestFooters...");
           printTestFooters(pw);

		   printAfter(pw);

	   } catch (Exception ex) {
		   ex.printStackTrace();
		   success = false;
	   } finally {
		   try {
			   pw.close();
			   if (success) {
			   	   System.out.println("Output file " + outputfile + " generated.");
			   	   System.out.println("UITestGenerator run time (ms): " + (System.currentTimeMillis() - ms));
			   } else {
				   System.out.println("WARNING: Output file " + outputfile + " is incomplete.");
			   }

		   } catch (Exception ex) {
			   ex.printStackTrace();
		   }
	   }
   }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// All value set search tests
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


   public void generateAllValueSetSearchTest(int number) {
	   List list = generateValueSetLinks(number);
	   String target = null;
	   TestCase testCase = null;
	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String vs_str = (String) RVSCSVec.elementAt(int_obj.intValue());

		   String cs_name = StringUtils.getStringComponent(vs_str, 0);
		   String cs_version = StringUtils.getStringComponent(vs_str, 1);
		   String cs_formalName = StringUtils.getStringComponent(vs_str, 2);
		   String cs_uri = StringUtils.getStringComponent(vs_str, 3);

		   String scheme = cs_name;
		   String version = cs_version;

		   if (scheme != null && scheme.length()>0) {
	           ResolvedConceptReferenceList testCases = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE,
	                                                             TestCaseGenerator.TYPE_VALUE_SET);
			   int n = rvGenerator.uniform(0, testCases.getResolvedConceptReferenceCount()-1);
			   ResolvedConceptReference rcr = (ResolvedConceptReference) testCases.getResolvedConceptReference(n);
			   String code = rcr.getConceptCode();
			   String name = rcr.getEntityDescription().getContent();

			   test_counter++;
			   int testNumber = test_counter;
			   String browserLink = null;
			   String algorithm = "exactMatch";
			   String matchText = name;
			   target = "Name";
			   testCase = testCaseWriter.createAllVSSearchTestCase(testNumber, target, browserLink, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   browserLink = null;
			   algorithm = "exactMatch";
			   matchText = code;
			   target = "Code";
			   testCase = testCaseWriter.createAllVSSearchTestCase(testNumber, target, browserLink, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

		   }
	   }
   }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// simple search tests
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public List generateTerminologyLinks(int number) {
	   if (browserHomeDisplayLabels == null) {
			System.out.println("generateTerminologyLinks browserHomeDisplayLabels == null -- return.");
			return null;
	   }
	   /*
	   int max = browserHomeDisplayLabels.size()-1;
	   if (number >= browserHomeDisplayLabels.size()) {
		   //02262016, KLO
		   //number = browserHomeDisplayLabels.size();
		   number = browserHomeDisplayLabels.size()-1;
	   }
	   */
	   int max = browserHomeDisplayLabels.size();
	   if (number >= max) {
		    ArrayList<Integer> arrayList = new ArrayList<Integer>();
			for (int i=0; i<max; i++) {
				arrayList.add(new Integer(i));
			}
			return arrayList;
	   }
	   List list = rvGenerator.selectWithNoReplacement(number, max);
	   return list;
   }


   public void generateAllTerminologySearchTests(int number) {
	   List list = generateTerminologyLinks(number);
	   String target = null;
	   String source = null;
	   String property_name = null;
	   String rel_search_association = null;
	   String rel_search_rela = null;
	   String direction = null;
	   String propertyName = null;
	   String relationshipSourceName = null;
	   String relationshipSourceCode = null;
	   String relationshipName = null;
	   TestCase testCase = null;

	   StringBuffer scheme_name_buf = new StringBuffer();
	   StringBuffer scheme_version_buf = new StringBuffer();
	   String scheme_names = null;
	   String scheme_versions = null;

	   for (int k=0; k<browserHomeDisplayLabels.size(); k++) {
		   String label = (String) browserHomeDisplayLabels.elementAt(k);
		   String scheme = testUtils.getCodingSchemeName(label);
		   String version = testUtils.getCodingSchemeVersion(label);
		   scheme_name_buf.append(scheme);
		   scheme_version_buf.append(version);
		   if (k<browserHomeDisplayLabels.size()-1) {
			   scheme_name_buf.append("|");
			   scheme_version_buf.append("|");
		   }
	   }
	   scheme_names = scheme_name_buf.toString();
	   scheme_versions = scheme_version_buf.toString();

	   String browserLink = null;
	   String algorithm = null;
	   String matchText = null;
	   Vector properties = null;
	   int testNumber = 0;

	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String label = (String) browserHomeDisplayLabels.elementAt(int_obj.intValue());
		   String scheme = testUtils.getCodingSchemeName(label);
		   String version = testUtils.getCodingSchemeVersion(label);
		   if (scheme != null && scheme.length()>0) {
                   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;
				   source = null;

				   ResolvedConceptReferenceList rcrl = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
				   int n = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				   ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(n);
				   String code = rcr.getConceptCode();
				   String name = rcr.getEntityDescription().getContent();

				   // code search
				   test_counter++;
				   testNumber = test_counter;
				   browserLink = "All Terminologies";
				   algorithm = "exactMatch";
				   matchText = code;
				   target = "codes";
				   testCase = testCaseWriter.createAllTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   browserLink = "All Terminologies But NCIm";
				   testCase = testCaseWriter.createAllButNCImTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   browserLink = "All Terminologies";
				   algorithm = "contains";
				   matchText = name;
				   target = "names";
				   testCase = testCaseWriter.createAllTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   browserLink = "All Terminologies But NCIm";
				   testCase = testCaseWriter.createAllButNCImTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   properties = conceptDetails.getPresentationProperties(scheme, version, code);
				   int m = rvGenerator.uniform(0, properties.size()-1);
				   String t = (String) properties.elementAt(m);
				   Vector u = StringUtils.parseData(t, "$");
				   propertyName = (String) u.elementAt(0);
				   matchText = (String) u.elementAt(1);
				   target = "properties";
				   algorithm = "startsWith";
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createAllTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   browserLink = "All Terminologies But NCIm";
				   testCase = testCaseWriter.createAllButNCImTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   // relationships
				   Vector relationships = conceptDetails.getRelationshipSource(scheme, version, code);
				   if (relationships.size() > 0) {
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
					   //direction = "source";
				   } else {
					   relationships = conceptDetails.getRelationshipTarget(scheme, version, code);
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
				   }
				   target = "relationships";
				   algorithm = "exactMatch";
				   matchText = relationshipSourceName;
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createAllTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   browserLink = "All Terminologies But NCIm";
				   testCase = testCaseWriter.createAllButNCImTerminologySearchTestCase(testNumber, target, browserLink, scheme_names, scheme_versions, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

	       }
	   }
   }

   public boolean isNCIm(String scheme) {
       if (scheme == null) return false;
       if (scheme.compareTo("NCI Metathesaurus") == 0 || scheme.compareTo("NCI_Metathesaurus") == 0) return true;
       return false;
   }


   public void generateSimpleSearchTests(int number) {
	   List list = generateTerminologyLinks(number);
	   String target = null;
	   String source = null;
	   String property_name = null;
	   String rel_search_association = null;
	   String rel_search_rela = null;
	   String direction = null;
	   String propertyName = null;
	   String relationshipSourceName = null;
	   String relationshipSourceCode = null;
	   String relationshipName = null;
	   TestCase testCase = null;
	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String label = (String) browserHomeDisplayLabels.elementAt(int_obj.intValue());
		   String scheme = testUtils.getCodingSchemeName(label);
		   String version = testUtils.getCodingSchemeVersion(label);
		   if (scheme != null && scheme.length()>0 && !isNCIm(scheme)) {

                   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;
				   source = null;

				   ResolvedConceptReferenceList rcrl = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
				   int n = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				   ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(n);
				   String code = rcr.getConceptCode();
				   String name = rcr.getEntityDescription().getContent();

				   // code search
				   test_counter++;
				   int testNumber = test_counter;
				   String browserLink = label;
				   String algorithm = "exactMatch";
				   String matchText = code;
				   target = "codes";
				   testCase = testCaseWriter.createSimpleSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   algorithm = "contains";
				   matchText = name;
				   target = "names";
				   testCase = testCaseWriter.createSimpleSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   Vector properties = conceptDetails.getPresentationProperties(scheme, version, code);
				   int m = rvGenerator.uniform(0, properties.size()-1);
				   String t = (String) properties.elementAt(m);
				   Vector u = StringUtils.parseData(t, "$");
				   propertyName = (String) u.elementAt(0);
				   matchText = (String) u.elementAt(1);
				   target = "properties";
				   algorithm = "startsWith";
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createSimpleSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   // relationships
				   Vector relationships = conceptDetails.getRelationshipSource(scheme, version, code);
				   if (relationships.size() > 0) {
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
					   //direction = "source";
				   } else {
					   relationships = conceptDetails.getRelationshipTarget(scheme, version, code);
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
					   //direction = "target";
				   }
				   target = "relationships";
				   algorithm = "exactMatch";
				   matchText = relationshipSourceName;
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createSimpleSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);
	       }

	   }

   }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   public void generateMultipleSearchTests(int number) {
	   List list = generateTerminologyLinks(number);
	   //List list = generateTerminologyLinks(2);
	   String target = null;
	   String source = null;
	   String property_name = null;
	   String rel_search_association = null;
	   String rel_search_rela = null;
	   String direction = null;
	   String propertyName = null;
	   String relationshipSourceName = null;
	   String relationshipSourceCode = null;
	   String relationshipName = null;
	   TestCase testCase = null;
	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String label = (String) browserHomeDisplayLabels.elementAt(int_obj.intValue());
		   String scheme = testUtils.getCodingSchemeName(label);
		   String version = testUtils.getCodingSchemeVersion(label);
		   if (scheme != null && scheme.length()>0) {
                   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;
				   source = null;

				   ResolvedConceptReferenceList rcrl = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
				   int n = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				   ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(n);
				   String code = rcr.getConceptCode();
				   String name = rcr.getEntityDescription().getContent();

				   // code search
				   test_counter++;
				   int testNumber = test_counter;
				   String browserLink = label;
				   String algorithm = "exactMatch";
				   String matchText = code;
				   target = "codes";
				   testCase = testCaseWriter.createMultipleSearchTestCase(testNumber, int_obj, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   testNumber = test_counter;
				   algorithm = "contains";
				   matchText = name;
				   target = "names";
				   testCase = testCaseWriter.createMultipleSearchTestCase(testNumber, int_obj, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   Vector properties = conceptDetails.getPresentationProperties(scheme, version, code);
				   int m = rvGenerator.uniform(0, properties.size()-1);
				   String t = (String) properties.elementAt(m);
				   Vector u = StringUtils.parseData(t, "$");
				   propertyName = (String) u.elementAt(0);
				   matchText = (String) u.elementAt(1);
				   target = "properties";
				   algorithm = "startsWith";
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createMultipleSearchTestCase(testNumber, int_obj, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);

				   // relationships
				   Vector relationships = conceptDetails.getRelationshipSource(scheme, version, code);
				   if (relationships.size() > 0) {
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
					   //direction = "source";
				   } else {
					   relationships = conceptDetails.getRelationshipTarget(scheme, version, code);
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
				   }
				   target = "relationships";
				   algorithm = "exactMatch";
				   matchText = relationshipSourceName;
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createMultipleSearchTestCase(testNumber, int_obj, target, browserLink, scheme, version, algorithm, matchText);
				   testCaseWriter.writeTestCase(testCase);
	       }
	   }
   }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public List generateMappingLinks(int number) {
	   int max = mappingDisplayLabels.size()-1;
	   if (number >= mappingDisplayLabels.size()) {
		   number = mappingDisplayLabels.size();
	   }
	   return rvGenerator.selectWithNoReplacement(number, max);
   }


   public void generateMappingSearchTests(int number) {
	   List list = generateMappingLinks(number);
	   String target = null;
	   TestCase testCase = null;

	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String label = (String) mappingDisplayLabels.elementAt(int_obj.intValue());
		   String scheme = testUtils.getMappingCodingSchemeName(label);
		   String version = testUtils.getMappingCodingSchemeVersion(label);
		   if (scheme != null && scheme.length()>0) {
			   ResolvedConceptReferenceList testCases = testUtils.generateTestCases(scheme, version, 10, TestCaseGenerator.TYPE_MAPPING);
			   int n = rvGenerator.uniform(0, testCases.getResolvedConceptReferenceCount()-1);
			   ResolvedConceptReference rcr = (ResolvedConceptReference) testCases.getResolvedConceptReference(n);
			   String code = rcr.getConceptCode();
			   String name = rcr.getEntityDescription().getContent();

			   // code search
			   test_counter++;
			   int testNumber = test_counter;
			   String browserLink = label;
			   String algorithm = "exactMatch";
			   String matchText = code;
			   target = "codes";
			   testCase = testCaseWriter.createMappingSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   browserLink = label;
			   algorithm = "exactMatch";
			   matchText = code;
			   target = "codes";
			   testCase = testCaseWriter.createAltMappingSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   algorithm = "contains";
			   matchText = name;
			   target = "names";
			   testCase = testCaseWriter.createMappingSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   algorithm = "contains";
			   matchText = name;
			   target = "names";
			   testCase = testCaseWriter.createAltMappingSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   algorithm = "exactMatch";
			   matchText = name;
			   target = "relationships";
			   testCase = testCaseWriter.createMappingSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   algorithm = "exactMatch";
			   matchText = name;
			   target = "relationships";
			   testCase = testCaseWriter.createAltMappingSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);


			   Vector properties = conceptDetails.getPresentationProperties(scheme, version, code);
			   target = "properties";
			   int m = rvGenerator.uniform(0, properties.size()-1);
			   String t = (String) properties.elementAt(m);
			   Vector u = StringUtils.parseData(t, "$");
			   String propertyName = (String) u.elementAt(0);
			   matchText = (String) u.elementAt(1);
			   algorithm = "startsWith";
			   test_counter++;
			   testNumber = test_counter;
			   testCase = testCaseWriter.createMappingSearchTestCase(testNumber, "properties", browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   testCase = testCaseWriter.createAltMappingSearchTestCase(testNumber, "properties", browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);
	       }
	   }
   }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public List generateValueSetLinks(int number) {
	   if (RVSCSVec == null) {
			System.out.println("generateValueSetLinks RVSCSVec == null -- return.");
			return null;
	   }
	   int max = RVSCSVec.size()-1;
	   if (number >= RVSCSVec.size()) {
		   number = RVSCSVec.size();
	   }
	   return rvGenerator.selectWithNoReplacement(number, max);
   }

   public void generateValueSetSearchTests(int number) {
	   List list = generateValueSetLinks(number);
	   String target = null;
	   TestCase testCase = null;
	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String vs_str = (String) RVSCSVec.elementAt(int_obj.intValue());

		   String cs_name = StringUtils.getStringComponent(vs_str, 0);
		   String cs_version = StringUtils.getStringComponent(vs_str, 1);
		   String cs_formalName = StringUtils.getStringComponent(vs_str, 2);
		   String cs_uri = StringUtils.getStringComponent(vs_str, 3);

		   String scheme = cs_name;
		   String version = cs_version;

		   if (scheme != null && scheme.length()>0) {
	           ResolvedConceptReferenceList testCases = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE,
	                                                             TestCaseGenerator.TYPE_VALUE_SET);
			   int n = rvGenerator.uniform(0, testCases.getResolvedConceptReferenceCount()-1);
			   ResolvedConceptReference rcr = (ResolvedConceptReference) testCases.getResolvedConceptReference(n);
			   String code = rcr.getConceptCode();
			   String name = rcr.getEntityDescription().getContent();

			   // code search
			   test_counter++;
			   int testNumber = test_counter;
			   String browserLink = cs_formalName;
			   String algorithm = "exactMatch";
			   String matchText = code;
			   target = "Code";
			   testCase = testCaseWriter.createVSSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

			   test_counter++;
			   testNumber = test_counter;
			   algorithm = "contains";
			   matchText = name;
			   target = "Name";
			   testCase = testCaseWriter.createVSSearchTestCase(testNumber, target, browserLink, scheme, version, algorithm, matchText);
			   testCaseWriter.writeTestCase(testCase);

	       }

	   }
   }


   public void goToValueSet(PrintWriter out, String label) {
       out.println("    driver.get(baseUrl + \"/ncitbrowser/pages/multiple_search.jsf?nav_type=terminologies\");");
       out.println("    driver.findElement(By.name(\"tab_valuesets\")).click();");
       out.println("    driver.findElement(By.linkText(\"" + label + "\")).click();");
   }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void generateAdvancedSearchTests(int number) {
	   List list = generateTerminologyLinks(number);
	   String target = null;
	   String source = null;
	   String property_name = null;
	   String rel_search_association = null;
	   String rel_search_rela = null;
	   String direction = null;
	   String propertyName = null;
	   String relationshipSourceName = null;
	   String relationshipSourceCode = null;
	   String relationshipName = null;
	   TestCase testCase = null;
	   for (int i=0; i<list.size(); i++) {
		   Integer int_obj = (Integer) list.get(i);
		   String label = (String) browserHomeDisplayLabels.elementAt(int_obj.intValue());
		   String scheme = testUtils.getCodingSchemeName(label);
		   String version = testUtils.getCodingSchemeVersion(label);
		   if (scheme != null && scheme.length()>0 & !isNCIm(scheme)) {
				   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;

				   ResolvedConceptReferenceList rcrl = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
				   int n = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				   ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(n);
				   String code = rcr.getConceptCode();
				   String name = rcr.getEntityDescription().getContent();

				   // code search
				   test_counter++;
				   int testNumber = test_counter;
				   String browserLink = label;
				   String algorithm = "exactMatch";
				   String matchText = code;
				   target = "Code";
				   testCase = testCaseWriter.createAdvancedSearchTestCase(testNumber, target, browserLink, scheme, version,
				                                algorithm, matchText,
	                                            source, property_name,
	                                            rel_search_association, rel_search_rela, direction);
				   testCaseWriter.writeTestCase(testCase);

				   test_counter++;
				   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;

				   testNumber = test_counter;
				   algorithm = "startsWith";
				   matchText = name;
				   target = "Name";

				   testCase = testCaseWriter.createAdvancedSearchTestCase(testNumber, target, browserLink, scheme, version,
				                                algorithm, matchText,
	                                            source, property_name,
	                                            rel_search_association, rel_search_rela, direction);
				   testCaseWriter.writeTestCase(testCase);

				   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;

				   Vector properties = conceptDetails.getPresentationProperties(scheme, version, code);
				   int m = rvGenerator.uniform(0, properties.size()-1);
				   String t = (String) properties.elementAt(m);
				   Vector u = StringUtils.parseData(t, "$");
				   propertyName = (String) u.elementAt(0);
				   matchText = (String) u.elementAt(1);
				   target = "Property";
				   algorithm = "exactMatch";
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createAdvancedSearchTestCase(testNumber, target, browserLink, scheme, version,
				                                algorithm, matchText,
	                                            source, propertyName,
	                                            rel_search_association, rel_search_rela, direction);
				   testCaseWriter.writeTestCase(testCase);
				   // relationships
				   Vector relationships = conceptDetails.getRelationshipSource(scheme, version, code);

                   propertyName = null;
				   relationshipSourceName = null;
				   relationshipSourceCode = null;
				   relationshipName = null;
				   source = null;

				   if (relationships.size() > 0) {
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
					   direction = "source";
				   } else {
					   relationships = conceptDetails.getRelationshipTarget(scheme, version, code);
					   int m2 = rvGenerator.uniform(0, relationships.size()-1);
					   String t2 = (String) relationships.elementAt(m2);
					   Vector w = StringUtils.parseData(t2, "$");
					   relationshipSourceName = (String) w.elementAt(0);
					   relationshipSourceCode = (String) w.elementAt(1);
					   relationshipName = (String) w.elementAt(2);
					   direction = "target";
				   }
				   matchText = relationshipSourceName;
				   rel_search_association = relationshipName;
				   rel_search_rela = null;

				   target = "Relationship";
				   algorithm = "exactMatch";
				   test_counter++;
				   testNumber = test_counter;
				   testCase = testCaseWriter.createAdvancedSearchTestCase(testNumber, target, browserLink, scheme, version,
				                                algorithm, matchText,
	                                            source, propertyName,
	                                            rel_search_association, rel_search_rela, direction);
				   testCaseWriter.writeTestCase(testCase);
	       }

	   }

   }

//////////////////////////////////////////////////////////////////////////////////////////////////////////
// View Hierarchy
//////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void generateViewHierarchyTests() {
	    String target = null;
	    TestCase testCase = null;
	    for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		    String label = (String) browserHomeDisplayLabels.elementAt(i);
		    String scheme = testUtils.getCodingSchemeName(label);
		    String version = testUtils.getCodingSchemeVersion(label);
		    boolean skip = true;

            if (scheme != null && scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCI_METATHESAURUS) != 0 &&
                !Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
				if (metadataUtils.tree_access_allowed(scheme, version)) {
				   ResolvedConceptReferenceList rcrl = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
				   int n = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				   ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(n);
				   String code = rcr.getConceptCode();
				   String name = rcr.getEntityDescription().getContent();

				   // code search
				   test_counter++;
				   int testNumber = test_counter;
				   String browserLink = label;
				   String algorithm = "exactMatch";
				   String matchText = code;
				   target = "Code";
				   testCase = testCaseWriter.createTestCase(testNumber, TestCase.VIEW_HIERARCHY,
				   	                                      browserLink,
				   	                                      scheme, version, algorithm, target, matchText);
				   testCaseWriter.writeTestCase(testCase);
				   skip = false;
			    }
	        }

	        if (skip) {
		        System.out.println("\tView Hierarchy -- " + scheme + " " + version + " skipped.");
			}

	   }
   }


//////////////////////////////////////////////////////////////////////////////////////////////////////////
// View In Hierarchy
//////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void generateViewInHierarchyTests() {
	    String target = null;
	    TestCase testCase = null;
	    for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		    String label = (String) browserHomeDisplayLabels.elementAt(i);
		    String scheme = testUtils.getCodingSchemeName(label);
		    String version = testUtils.getCodingSchemeVersion(label);
		    boolean skip = true;
            if (scheme != null && scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCI_METATHESAURUS) != 0 &&
                !Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
				if (metadataUtils.tree_access_allowed(scheme, version)) {
				   ResolvedConceptReferenceList rcrl = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
				   int n = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
				   ResolvedConceptReference rcr = (ResolvedConceptReference) rcrl.getResolvedConceptReference(n);
				   String code = rcr.getConceptCode();
				   String name = rcr.getEntityDescription().getContent();

				   // code search
				   test_counter++;
				   int testNumber = test_counter;
				   String browserLink = label;
				   String algorithm = "exactMatch";
				   String matchText = code;
				   target = "codes";

				   testCase = testCaseWriter.createTestCase(testNumber, TestCase.VIEW_IN_HIERARCHY,
				   	                                      browserLink,
				   	                                      scheme, version, algorithm, target, matchText);
				   testCaseWriter.writeTestCase(testCase);
				   skip = false;
			    }
	        }
	        if (skip) {
		        System.out.println("\tView In Hierarchy -- " + scheme + " " + version + " skipped.");
			}
	   }
   }



///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   public void printLicenseAcceptance(PrintWriter out) {
	   out.println("    driver.get(baseUrl + \"/ncitbrowser/pages/multiple_search.jsf\");");
       out.println("    driver.findElement(By.name(\"selectAllButNCIm\")).click();");
       out.println("    driver.findElement(By.name(\"matchText\")).clear();");
       out.println("    driver.findElement(By.name(\"matchText\")).sendKeys(\"cell\");");
       out.println("    driver.findElement(By.id(\"searchTerm:multiple_search\")).click();");
       out.println("    driver.findElement(By.id(\"j_id_jsp_963990601_1:search\")).click();");
       out.println("\n");
   }



   public void printMultipleSearchTest(PrintWriter out) {
       out.println("    driver.findElement(By.name(\"selectAllButNCIm\")).click();");
       out.println("    driver.findElement(By.name(\"matchText\")).clear();");
       out.println("    driver.findElement(By.name(\"matchText\")).sendKeys(\"cell\");");
       out.println("    driver.findElement(By.id(\"searchTerm:multiple_search\")).click();");
       out.println("    driver.findElement(By.id(\"j_id_jsp_963990601_1:search\")).click();");
       out.println("    driver.findElement(By.linkText(\"Cellular Telephone\")).click();");
       out.println("    driver.findElement(By.name(\"sdTab\")).click();");
       out.println("    driver.findElement(By.name(\"relTab\")).click();");
       out.println("    driver.findElement(By.name(\"mapTab\")).click();");
       out.println("    driver.findElement(By.name(\"vaTab\")).click();");
       out.println("    driver.findElement(By.name(\"tab_terms\")).click();");
       out.println("    driver.findElement(By.id(\"exactMatch\")).click();");
       out.println("    driver.findElement(By.id(\"searchTerm:multiple_search\")).click();");
       out.println("    driver.findElement(By.linkText(\"cell\")).click();");
       out.println("    driver.findElement(By.name(\"tab_terms\")).click();");
       out.println("    driver.findElement(By.id(\"startsWith\")).click();");
       out.println("    driver.findElement(By.id(\"searchTerm:multiple_search\")).click();");
       out.println("    driver.findElement(By.linkText(\"Entire cell\")).click();");
       out.println("    driver.findElement(By.id(\"matchText\")).clear();");
   }


   public String getCodingSchemeNameByLabel(String displayLabel) {
	   TerminologyBean tb = (TerminologyBean) label2TerminologyHashMap.get(displayLabel);
	   if (tb == null) return null;
	   return tb.getCodingSchemeName();
   }

   public String getCodingSchemeVersionByLabel(String displayLabel) {
	   TerminologyBean tb = (TerminologyBean) label2TerminologyHashMap.get(displayLabel);
	   if (tb == null) return null;
	   return tb.getCodingSchemeVersion();
   }

   public void goToTerminology(PrintWriter out, String label) {
       out.println("	if (driver != null && driver.findElement(By.name(\"tab_terms\")) != null) {");
       out.println("		driver.findElement(By.name(\"tab_terms\")).click();");
       out.println("	}");
       out.println("	String linkText = \"" + label + "\";");
       out.println("	if (driver != null && driver.findElement(By.linkText(linkText)) != null) {");
       out.println("		driver.findElement(By.linkText(linkText)).click();");
       out.println("    } else {");
       out.println("		System.out.println(\"driver == null???\");");
       out.println("	}");


	   //out.println("    driver.findElement(By.name(\"tab_terms\")).click();");
       //out.println("    driver.findElement(By.linkText(\"" + label + "\")).click();");
       if (displayLabel2TerminologyBeanHashMap.containsKey(label)) {
		   TerminologyBean tb = (TerminologyBean) displayLabel2TerminologyBeanHashMap.get(label);
		   out.println("    scheme =" + "\"" + tb.getCodingSchemeName() + "\";");
		   out.println("    version =" + "\"" + tb.getCodingSchemeVersion() + "\";");
	   } else {
		   System.out.println(label + " not found.");
	   }
   }

   public void goToTab(PrintWriter out, String nav_type) {
       if (nav_type.compareTo(Constants.VALUESET_TAB) == 0) {
       	   out.println("    driver.findElement(By.name(\"tab_valuesets\")).click();");
       } else if (nav_type.compareTo(Constants.MAPPING_TAB) == 0) {
           out.println("    driver.findElement(By.name(\"tab_map\")).click();");
	   } else {
		   out.println("    driver.findElement(By.name(\"tab_terms\")).click();");
	   }
   }

   public void printMultipleTest(PrintWriter out) {
	   String label = "Multiple search";
	   String methodName = "testMultipleSearch";
	   out.println("  @Test //" + label);
	   out.println("  public void " + methodName + "() throws Exception {");
	   printMultipleSearchTest(out);
	   out.println("  }");
	   out.println("\n");
   }


   public void printHierarchyLinkTest(PrintWriter out, String displayLabel) {
       String codingSchemeName = testUtils.getCodingSchemeName(displayLabel);
       String codingSchemeVersion = testUtils.getCodingSchemeVersion(displayLabel);

       codingSchemeName = codingSchemeName.replaceAll(" ", "%20");
       codingSchemeVersion = codingSchemeVersion.replaceAll(" ", "%20");
       out.println("    driver.get(baseUrl + \"/ncitbrowser/hierarchy.jsf?dictionary=" + codingSchemeName + "&version=" + codingSchemeVersion + "\");");
   }


   public void printHierarchyTest(PrintWriter out) {
	   printMultipleTest(out);

	   testMethodName = getTestMethodName();

       int k = 0;
       for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		   String label = (String) browserHomeDisplayLabels.elementAt(i);
		   k++;
		   String methodName = testMethodName + "_Hierarchy_" + k;
		   out.println("  @Test //" + label + " (hierarchy)");
		   out.println("  public void " + methodName + "() throws Exception {");
		   printHierarchyLinkTest(out, label);
		   out.println("\n");
		   out.println("    " + "// <add validation code here>");
		   out.println("    " + "// <add assert statement here>");
		   out.println("    " + "assertTrue(true);");

		   out.println("  }");
		   out.println("\n");
	   }
   }


   public void printBaseURLExternalLinksTest(PrintWriter out) {
      out.println("    @Test //testBaseURLExternalLinks");
      out.println("    public void testBaseURLExternalLinks() throws Exception {");
      out.println("		try {");
      out.println("			driver.get(baseUrl);");
      out.println("			java.util.List<WebElement> links = driver.findElements(By.tagName(\"a\"));");
      out.println("			HashSet hset = new HashSet();");
      out.println("			int lcv = 0;");
      out.println("			for (int i=0; i<links.size(); i++) {");
      out.println("				String href = links.get(i).getAttribute(\"href\");");
      out.println("				if (href != null && href.length()>0) {");
      out.println("					if (!hset.contains(href)) {");
      out.println("						hset.add(href);");
      out.println("					}");
      out.println("			    }");
      out.println("			}");
      out.println("			Iterator it = hset.iterator();");
      out.println("			while (it.hasNext()) {");
      out.println("				String href = (String) it.next();");
      out.println("				if (!href.startsWith(baseUrl)) {");
      out.println("					lcv++;");
      out.println("					int responseCode = getHTTPResponseCode(href);");
      out.println("					System.out.println(\"(\" + lcv + \") \" + href + \" (response code: \" + responseCode + \")\");");
      out.println("					assertTrue(responseCode == 200);");
      out.println("				}");
      out.println("			}");
      out.println("	    } catch (Exception ex) {");
      out.println("            ex.printStackTrace();");
      out.println("            assertTrue(false);");
      out.println("		}");
      out.println("		assertTrue(true);");
      out.println("	}");
      out.println("\n");
  }



   public void printTestFooter(PrintWriter out, String linkLabel) {
	   String method_name = "test" + linkLabel + "Footer";
	   method_name = StringUtils.removeWhiteSpaces(method_name);//method_name.replaceAll(" ", "_");
	   out.println("    @Test // (" + method_name + ")");
	   out.println("    public void " + method_name + "() throws Exception {");
	   out.println("		String bodyText = null;");
	   out.println("		driver.get(baseUrl);");
       out.println("		Thread.sleep(1000);");
       out.println("		try {");
       out.println("		    popUpWindow(\"" + linkLabel + "\");");
       out.println("		    assertTrue(true);");
       out.println("		} catch (Exception ex) {");
       out.println("		    assertTrue(false);");
       out.println("        }");
       out.println("    }");
       out.println("");
   }

   public void printTestFooters(PrintWriter out) {
	   printTestFooter(out, "NCI Home");
	   printTestFooter(out, "Policies");
	   printTestFooter(out, "Accessibility");
	   printTestFooter(out, "FOIA");
	   printTestFooter(out, "Contact Us");
   }


   public void printTestHelpInfo(PrintWriter out) {
	   out.println("    public void testHelpInfoPages() throws Exception {");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
       System.out.println("NCIt_PROD_Version: " + NCIt_PROD_Version);
	   String ncit_display_label = "NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")";
	   System.out.println("ncit_display_label: " + ncit_display_label);

	   String scheme = testUtils.getCodingSchemeName(ncit_display_label);
       String version = testUtils.getCodingSchemeVersion(ncit_display_label);

	   ResolvedConceptReferenceList testCases = testUtils.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE, TestCaseGenerator.TYPE_TERMINOLOGY);
	   int n = rvGenerator.uniform(0, testCases.getResolvedConceptReferenceCount()-1);
	   ResolvedConceptReference rcr = (ResolvedConceptReference) testCases.getResolvedConceptReference(n);

	   if (rcr == null) {
	    	System.out.println("ResolvedConceptReference is NULL -- return ");
	   }
	   String code = rcr.getConceptCode();
	   out.println("		driver.findElement(By.linkText(\"" + ncit_display_label + "\")).click();");
       out.println("        driver.findElement(By.xpath(\"//input[@name='algorithm'][@value='exactMatch']\")).click();");
       out.println("        driver.findElement(By.xpath(\"//input[@name='selectValueSetSearchOption'][@value='codes']\")).click();");


	   out.println("		driver.findElement(By.name(\"matchText\")).clear();");
	   out.println("		driver.findElement(By.name(\"matchText\")).sendKeys(\"" + code + "\");");
	   out.println("		driver.findElement(By.name(\"searchTerm:search\")).click();");
	   out.println("		driver.findElement(By.name(\"sdTab\")).click();");
	   out.println("        popUpWindowByImage(\"Term Type Definitions\");");
	   out.println("        assertTrue(true);");
	   out.println("    }");
	   out.println("");
   }


   public void printTestNCItHomeLinks(PrintWriter out) {
	   out.println("    public void testNCItHomeLinks() throws Exception {");
	   out.println("		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
	   out.println("		driver.findElement(By.name(\"tab_terms\")).click();");
	   out.println("		driver.findElement(By.linkText(\"NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")\")).click();");
	   out.println("");
	   out.println("		popUpWindowByImage(\"EVS\");");
	   out.println("		popUpWindowByImage(\"NCIm\");");
	   out.println("		popUpWindowByImage(\"NCI Terminology Resources\");");
	   out.println("");
	   out.println("        driver.findElement(By.name(\"tab_terms\")).click();");
	   out.println("        driver.findElement(By.linkText(\"NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")\")).click();");
	   out.println("		popUpWindow(\"https://wiki.nci.nih.gov/display/VKC/NCI+Thesaurus+Terminology\");");
	   out.println("");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
	   out.println("		driver.findElement(By.name(\"tab_terms\")).click();");
	   out.println("        driver.findElement(By.linkText(\"NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")\")).click();");
	   out.println("    	driver.findElement(By.cssSelector(\"img.searchbox-btn\")).click();");
	   out.println("");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
	   out.println("		driver.findElement(By.name(\"tab_terms\")).click();");
	   out.println("        driver.findElement(By.linkText(\"NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")\")).click();");
	   out.println("    	driver.findElement(By.linkText(\"Help\")).click();");
	   out.println("");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
	   out.println("		driver.findElement(By.name(\"tab_terms\")).click();");
	   out.println("        driver.findElement(By.linkText(\"NCI Thesaurus: National Cancer Institute Thesaurus (" + NCIt_PROD_Version + ")\")).click();");
	   out.println("        assertTrue(true);");
	   out.println("	}");
	   out.println("");
   }


   public void printTestViewHierarchy(PrintWriter out) {
	   out.println("    public void testHierarchy() throws Exception {");
       for (int i=0; i<browserHomeDisplayLabels.size(); i++) {
		   String label = (String) browserHomeDisplayLabels.elementAt(i);
		   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
		   out.println("		driver.findElement(By.linkText(\"" + label + "\")).click();");
		   out.println("		popUpWindow(\"Hierarchy\");");
		   out.println("		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);");
		   out.println("        assertTrue(true);");
           out.println("\n");
	   }
	   out.println("	}");
	   out.println("");
   }



   public void printTestViewHierarchy(PrintWriter out, String displayLabel) {
	   out.println("    public void testHierarchy(String displayLabel) throws Exception {");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/\");");
	   out.println("		driver.findElement(By.linkText(displayLabel)).click();");
	   out.println("		popUpWindow(\"Hierarchy\");");
	   out.println("		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);");
	   out.println("        assertTrue(true);");
	   out.println("    }");
	   out.println("");
   }

   public void printTestValueSetSearch(PrintWriter out) {
	   out.println("    public void testValueSetSearch() throws Exception {");
	   out.println("		String baseUrl = \"http://nciterms-stage.nci.nih.gov/ncitbrowser\";");
	   out.println("		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);");
	   out.println("		driver.get(baseUrl + \"/ncitbrowser/pages/multiple_search.jsf\");");
	   out.println("");
	   out.println("		driver.findElement(By.name(\"tab_valuesets\")).click();");
	   out.println("		driver.findElement(By.linkText(\"Check all\")).click();");
	   out.println("		driver.findElement(By.name(\"matchText\")).clear();");
	   out.println("		driver.findElement(By.name(\"matchText\")).sendKeys(\"blue\");");
	   out.println("		driver.findElement(By.id(\"valueSetSearchForm:valueset_search\")).click();");
	   out.println("");

       out.println("        driver.findElement(By.xpath(\"//input[@name='valueset_search_algorithm'][@value='exactMatch']\")).click();");
       out.println("        driver.findElement(By.xpath(\"//input[@name='selectValueSetSearchOption'][@value='Code']\")).click();");

	   out.println("");
	   out.println("		driver.findElement(By.name(\"matchText\")).clear();");
	   out.println("		driver.findElement(By.name(\"matchText\")).sendKeys(\"C48333\");");
	   out.println("		driver.findElement(By.id(\"valueSetSearchForm:valueset_search\")).click();");
	   out.println("		driver.findElement(By.linkText(\"Blue\")).click();");
	   out.println("		driver.findElement(By.name(\"sdTab\")).click();");
	   out.println("		driver.findElement(By.name(\"relTab\")).click();");
	   out.println("		driver.findElement(By.name(\"mapTab\")).click();");
	   out.println("		driver.findElement(By.name(\"vaTab\")).click();");
	   out.println("		driver.findElement(By.linkText(\"Add to Cart\")).click();");
	   out.println("		driver.findElement(By.linkText(\"Cart\")).click();");
	   out.println("		driver.findElement(By.cssSelector(\"img[alt=\\"+ "\"Select All" + "\\\"]\")).click();");
	   out.println("		driver.findElement(By.cssSelector(\"img[alt=\\"+ "\"Export XML" + "\\\"]\")).click();");
	   out.println("");
	   out.println("		driver.findElement(By.id(\"resolveValueSetForm:continue_resolve\")).click();");
	   out.println("    }");
	   out.println("");
   }

   public static void printTestStatement(PrintWriter pw, String label) {
	   pw.println("   @Test " + "//" + label);
   }

   public static void main(String[] args) {
	   CodeGeneratorConfiguration config = new CodeGeneratorConfiguration();
	   config.setPackageName("selenium.webapps.termbrowser");
	   config.setClassName("TestTermBrowserTestCase");
	   config.setBaseUrl("http://nciterms-stage.nci.nih.gov/ncitbrowser");
	   config.setServiceUrl("http://lexevsapi62-stage.nci.nih.gov/lexevsapi62");
	   UITestGenerator generator = new UITestGenerator(config);
	   generator.run();
   }
}



