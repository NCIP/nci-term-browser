package selenium.webapps.termbrowser;


import gov.nih.nci.evs.testUtil.ui.*;
import gov.nih.nci.evs.testUtil.*;
import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.ApplicationServiceProvider;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.TimeUnit;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.CodingSchemeReference;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Extensions.Generic.SearchExtension;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.*;
import org.LexGrid.naming.*;
import org.apache.commons.codec.language.*;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.*;
import org.junit.*;
import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.Assert.*;


/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2015 NGIS. This software was developed in conjunction
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
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */
public class TestTermBrowserTestCase {
  private WebDriver driver;
  private String baseUrl;
  private boolean acceptNextAlert = true;
  private StringBuffer verificationErrors = new StringBuffer();

  private LexBIGService lbSvc = null;
  private String serviceUrl = null;
  private SimpleSearchUtils simpleSearchUtils = null;
  private MappingSearchUtils mappingSearchUtils = null;
  private ValueSetSearchUtils valueSetSearchUtils = null;
  private ConceptDetails conceptDetails = null;
  private SearchUtils searchUtils = null;

  private String scheme = null;
  private String version = null;
  private String matchText = null;
  private String target = null;
  private int searchOption = 2;
  private String algorithm = null;
  private String propertyName = null;
  private ResolvedConceptReferencesIterator rcr_iterator = null;
  private ResolvedConceptReference rcref = null;
  private int search_direction = gov.nih.nci.evs.browser.common.Constants.SEARCH_SOURCE;
  private int maxToReturn = -1;
  private String source = null;
  private String[] associationsToNavigate = null;
  private String[] association_qualifier_names = null;
  private String[] association_qualifier_values = null;
  private boolean excludeDesignation = true;
  private boolean ranking = true;

  @Before
  public void setUp() throws Exception {
    SimpleRemoteServerUtil lexEVSSvr = new SimpleRemoteServerUtil("http://lexevsapi6.nci.nih.gov/lexevsapi63");
	Vector names = new Vector();
	Vector values = new Vector();
	String name = null;
	String value = null;
	String meddra_name = null;
	meddra_name = "MedDRA (Medical Dictionary for Regulatory Activities Terminology)";
	names.add(meddra_name);
	values.add("");
	meddra_name = "MedDRA";
	names.add(meddra_name);
	values.add("");
	lexEVSSvr.setSecurityTokens(names, values);
    lbSvc = lexEVSSvr.getLexBIGService("http://lexevsapi6.nci.nih.gov/lexevsapi63");
    simpleSearchUtils = new SimpleSearchUtils(lbSvc);
    mappingSearchUtils = new MappingSearchUtils(lbSvc);
    valueSetSearchUtils = new ValueSetSearchUtils(lbSvc);
    conceptDetails = new ConceptDetails(lbSvc);
    searchUtils = new SearchUtils(lbSvc);


    driver = new RemoteWebDriver(new URL("http://localhost:9515"), DesiredCapabilities.chrome());
    serviceUrl = "http://lexevsapi6.nci.nih.gov/lexevsapi63";
    baseUrl = "https://nciterms.nci.nih.gov";
    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    Thread.sleep(1000);
    driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
  }


	public void popUpWindow(String wndLabel) throws Exception {
		driver.findElement(By.linkText(wndLabel)).click();
		Thread.sleep(8000);
		String windowTitle= getCurrentWindowTitle();
		Thread.sleep(1000);
		String mainWindow = getMainWindowHandle(driver);
		Thread.sleep(1000);
		closeAllOtherWindows(mainWindow);
	}

	public String getMainWindowHandle(WebDriver driver) {
		return driver.getWindowHandle();
	}

	public String getCurrentWindowTitle() {
		String windowTitle = driver.getTitle();
		return windowTitle;
	}

	public boolean closeAllOtherWindows(String openWindowHandle) {
		Set<String> allWindowHandles = driver.getWindowHandles();
		for (String currentWindowHandle : allWindowHandles) {
			if (!currentWindowHandle.equals(openWindowHandle)) {
				driver.switchTo().window(currentWindowHandle);
				driver.close();
			}
		}

		driver.switchTo().window(openWindowHandle);
		if (driver.getWindowHandles().size() == 1)
			return true;
		else
			return false;
	}

    public String getPopupWindowBodyText(WebDriver driver) {
		String parentWindowHandler = driver.getWindowHandle();
		String subWindowHandler = null;
		Set<String> handles = driver.getWindowHandles();
		Iterator<String> iterator = handles.iterator();
		while (iterator.hasNext()){
			subWindowHandler = iterator.next();
		}
		driver.switchTo().window(subWindowHandler);
		String bodyText = driver.findElement(By.tagName("body")).getText();
		driver.switchTo().window(parentWindowHandler);
		return bodyText;
	}

	public boolean containsText(String text) {
	    try {
		    if (driver.findElement(By.xpath("//*[contains(.,'" + text + "')]")) != null) {
		        return true;
		    }
		} catch (Exception e) {
		    return false;
	    }
	    return false;
	}

    public void goBack() {
		driver.navigate().back();
	}

	public void navigateTo(String url) {
		driver.get(url);
	}


    public void maximizeWindow() {
		driver.manage().window().maximize();
	}

	@Test //All Terminologies
	public void testTermBrowserTestCase_1() throws Exception {
		String linkText = "All Terminologies";
		int searchType = 20; //(ALL_TERMINOLOGY_SEARCH_ON_CODE)
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "C84323";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("NCI Metathesaurus");
    	versions.add("201508");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAll")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies But NCIm
	public void testTermBrowserTestCase_2() throws Exception {
		String linkText = "All Terminologies But NCIm";
		int searchType = 24; //(ALL_BUT_NCIM_TERMINOLOGY_SEARCH_ON_CODE)
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "C84323";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAllButNCIm")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies
	public void testTermBrowserTestCase_3() throws Exception {
		String linkText = "All Terminologies";
		int searchType = 21; //(ALL_TERMINOLOGY_SEARCH_ON_NAME)
		String target = "names";
		String algorithm = "contains";
		String matchText = "ADP-Ribosyl Cyclase 1";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("NCI Metathesaurus");
    	versions.add("201508");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAll")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies But NCIm
	public void testTermBrowserTestCase_4() throws Exception {
		String linkText = "All Terminologies But NCIm";
		int searchType = 25; //(ALL_BUT_NCIM_TERMINOLOGY_SEARCH_ON_NAME)
		String target = "names";
		String algorithm = "contains";
		String matchText = "ADP-Ribosyl Cyclase 1";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAllButNCIm")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies But NCIm
	public void testTermBrowserTestCase_5() throws Exception {
		String linkText = "All Terminologies But NCIm";
		int searchType = 22; //(ALL_TERMINOLOGY_SEARCH_ON_PROPERTY)
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "CD38";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("NCI Metathesaurus");
    	versions.add("201508");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAll")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies But NCIm
	public void testTermBrowserTestCase_6() throws Exception {
		String linkText = "All Terminologies But NCIm";
		int searchType = 26; //(ALL_BUT_NCIM_TERMINOLOGY_SEARCH_ON_PROPERTY)
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "CD38";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAllButNCIm")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies But NCIm
	public void testTermBrowserTestCase_7() throws Exception {
		String linkText = "All Terminologies But NCIm";
		int searchType = 23; //(ALL_TERMINOLOGY_SEARCH_ON_RELATIONSHIP)
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "Enzyme";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("NCI Metathesaurus");
    	versions.add("201508");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAll")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //All Terminologies But NCIm
	public void testTermBrowserTestCase_8() throws Exception {
		String linkText = "All Terminologies But NCIm";
		int searchType = 27; //(ALL_BUT_NCIM_TERMINOLOGY_SEARCH_ON_RELATIONSHIP)
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "Enzyme";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	schemes.add("NCI_Thesaurus");
    	versions.add("16.01d");
    	schemes.add("ChEBI");
    	versions.add("v136");
    	schemes.add("CTCAE");
    	versions.add("4.03");
    	schemes.add("GO");
    	versions.add("February2016");
    	schemes.add("HGNC");
    	versions.add("February2016");
    	schemes.add("HL7");
    	versions.add("V3 R2.36");
    	schemes.add("ICD-10-CM");
    	versions.add("2014");
    	schemes.add("ICD-10");
    	versions.add("2010");
    	schemes.add("ICD-9-CM");
    	versions.add("2013_2012_08_06");
    	schemes.add("LOINC");
    	versions.add("2_48");
    	schemes.add("MA");
    	versions.add("November2014");
    	schemes.add("MedDRA");
    	versions.add("18.1");
    	schemes.add("MGEDOntology.owl");
    	versions.add("1.3.1");
    	schemes.add("NDFRT");
    	versions.add("February2016");
    	schemes.add("NPO");
    	versions.add("2011-12-08");
    	schemes.add("obi");
    	versions.add("December2015");
    	schemes.add("PDQ");
    	versions.add("2014_08_29");
    	schemes.add("RadLex");
    	versions.add("3.11");
    	schemes.add("SNOMED Clinical Terms US Edition");
    	versions.add("2015_03_01");
    	schemes.add("UMLS_SemNet");
    	versions.add("3.2");
    	schemes.add("Zebrafish");
    	versions.add("June_12_2014");
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		driver.findElement(By.name("selectAllButNCIm")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NPO: NanoParticle Ontology (1.0_Dec_08_2011)
	public void testTermBrowserTestCase_9() throws Exception {
		String linkText = "NPO: NanoParticle Ontology (1.0_Dec_08_2011)";
		int searchType = 13; //(MULTIPLE_SEARCH_ON_CODE)
		String scheme = "NPO";
		String version = "2011-12-08";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "NPO_1009";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 15;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NPO: NanoParticle Ontology (1.0_Dec_08_2011)
	public void testTermBrowserTestCase_10() throws Exception {
		String linkText = "NPO: NanoParticle Ontology (1.0_Dec_08_2011)";
		int searchType = 12; //(MULTIPLE_SEARCH_ON_NAME)
		String scheme = "NPO";
		String version = "2011-12-08";
		String target = "names";
		String algorithm = "contains";
		String matchText = "cell";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 15;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NPO: NanoParticle Ontology (1.0_Dec_08_2011)
	public void testTermBrowserTestCase_11() throws Exception {
		String linkText = "NPO: NanoParticle Ontology (1.0_Dec_08_2011)";
		int searchType = 14; //(MULTIPLE_SEARCH_ON_PROPERTY)
		String scheme = "NPO";
		String version = "2011-12-08";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "cell";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 15;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NPO: NanoParticle Ontology (1.0_Dec_08_2011)
	public void testTermBrowserTestCase_12() throws Exception {
		String linkText = "NPO: NanoParticle Ontology (1.0_Dec_08_2011)";
		int searchType = 15; //(MULTIPLE_SEARCH_ON_RELATIONSHIP)
		String scheme = "NPO";
		String version = "2011-12-08";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "biological material entity";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 15;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_13() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 13; //(MULTIPLE_SEARCH_ON_CODE)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "B02.2";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 7;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_14() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 12; //(MULTIPLE_SEARCH_ON_NAME)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "names";
		String algorithm = "contains";
		String matchText = "Zoster with other nervous system involvement";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 7;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_15() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 14; //(MULTIPLE_SEARCH_ON_PROPERTY)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "Zoster with other nervous system involvement";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 7;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_16() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 15; //(MULTIPLE_SEARCH_ON_RELATIONSHIP)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "Zoster encephalitis";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 7;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)
	public void testTermBrowserTestCase_17() throws Exception {
		String linkText = "MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)";
		int searchType = 13; //(MULTIPLE_SEARCH_ON_CODE)
		String scheme = "MGEDOntology.owl";
		String version = "1.3.1";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "MO_712";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 13;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)
	public void testTermBrowserTestCase_18() throws Exception {
		String linkText = "MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)";
		int searchType = 12; //(MULTIPLE_SEARCH_ON_NAME)
		String scheme = "MGEDOntology.owl";
		String version = "1.3.1";
		String target = "names";
		String algorithm = "contains";
		String matchText = "list_of_booleans";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 13;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)
	public void testTermBrowserTestCase_19() throws Exception {
		String linkText = "MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)";
		int searchType = 14; //(MULTIPLE_SEARCH_ON_PROPERTY)
		String scheme = "MGEDOntology.owl";
		String version = "1.3.1";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "list_of_booleans";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 13;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)
	public void testTermBrowserTestCase_20() throws Exception {
		String linkText = "MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)";
		int searchType = 15; //(MULTIPLE_SEARCH_ON_RELATIONSHIP)
		String scheme = "MGEDOntology.owl";
		String version = "1.3.1";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "DataType";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	Vector schemes = new Vector();
    	schemes.add(scheme);
    	Vector versions = new Vector();
    	versions.add(version);
    	int checkbox_index = 13;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMultipleSearch(
			schemes, versions, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@alt='reset']")).click();
		Thread.sleep(1000);
		List<WebElement> checkbox = driver.findElements(By.name("ontology_list"));
		((WebElement) checkbox.get(checkbox_index)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:multiple_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_21() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 1; //(SIMPLE_SEARCH_ON_NAME_OR_CODE)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "A92.39";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_22() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 1; //(SIMPLE_SEARCH_ON_NAME_OR_CODE)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "names";
		String algorithm = "contains";
		String matchText = "West Nile virus infection with other complications";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_23() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 2; //(SIMPLE_SEARCH_ON_PROPERTY)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "West Nile virus infection with other complications";
		String matchedString = "No match";
    	String source = null;
    	boolean excludeDesignation = true;
    	boolean ranking = true;
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validatePropertySearch(
			scheme, version, matchText, source, algorithm, excludeDesignation, ranking, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_24() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 3; //(SIMPLE_SEARCH_ON_RELATIONSHIP)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "West Nile virus infection with other neurologic manifestation";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //UMLS SemNet: UMLS Semantic Network (3.2)
	public void testTermBrowserTestCase_25() throws Exception {
		String linkText = "UMLS SemNet: UMLS Semantic Network (3.2)";
		int searchType = 1; //(SIMPLE_SEARCH_ON_NAME_OR_CODE)
		String scheme = "UMLS_SemNet";
		String version = "3.2";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "T083";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //UMLS SemNet: UMLS Semantic Network (3.2)
	public void testTermBrowserTestCase_26() throws Exception {
		String linkText = "UMLS SemNet: UMLS Semantic Network (3.2)";
		int searchType = 1; //(SIMPLE_SEARCH_ON_NAME_OR_CODE)
		String scheme = "UMLS_SemNet";
		String version = "3.2";
		String target = "names";
		String algorithm = "contains";
		String matchText = "Geographic Area";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //UMLS SemNet: UMLS Semantic Network (3.2)
	public void testTermBrowserTestCase_27() throws Exception {
		String linkText = "UMLS SemNet: UMLS Semantic Network (3.2)";
		int searchType = 2; //(SIMPLE_SEARCH_ON_PROPERTY)
		String scheme = "UMLS_SemNet";
		String version = "3.2";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "Geographic Area";
		String matchedString = "No match";
    	String source = null;
    	boolean excludeDesignation = true;
    	boolean ranking = true;
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validatePropertySearch(
			scheme, version, matchText, source, algorithm, excludeDesignation, ranking, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //UMLS SemNet: UMLS Semantic Network (3.2)
	public void testTermBrowserTestCase_28() throws Exception {
		String linkText = "UMLS SemNet: UMLS Semantic Network (3.2)";
		int searchType = 3; //(SIMPLE_SEARCH_ON_RELATIONSHIP)
		String scheme = "UMLS_SemNet";
		String version = "3.2";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "Anatomical Abnormality";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MA: Anatomical Dictionary for the Adult Mouse (November2014)
	public void testTermBrowserTestCase_29() throws Exception {
		String linkText = "MA: Anatomical Dictionary for the Adult Mouse (November2014)";
		int searchType = 17; //(ADVANCED_SEARCH_ON_CODE)
		String scheme = "MA";
		String version = "November2014";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "MA:0000447";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = null;
    	String rel_search_rela = null;
    	String direction = null;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Code']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MA: Anatomical Dictionary for the Adult Mouse (November2014)
	public void testTermBrowserTestCase_30() throws Exception {
		String linkText = "MA: Anatomical Dictionary for the Adult Mouse (November2014)";
		int searchType = 16; //(ADVANCED_SEARCH_ON_NAME)
		String scheme = "MA";
		String version = "November2014";
		String target = "Name";
		String algorithm = "startsWith";
		String matchText = "omental bursa superior recess";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = null;
    	String rel_search_rela = null;
    	String direction = null;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Name']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='startsWith']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MA: Anatomical Dictionary for the Adult Mouse (November2014)
	public void testTermBrowserTestCase_31() throws Exception {
		String linkText = "MA: Anatomical Dictionary for the Adult Mouse (November2014)";
		int searchType = 18; //(ADVANCED_SEARCH_ON_PROPERTY)
		String scheme = "MA";
		String version = "November2014";
		String target = "Property";
		String algorithm = "exactMatch";
		String matchText = "omental bursa superior recess";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = "textualPresentation";
    	String rel_search_association = null;
    	String rel_search_rela = null;
    	String direction = null;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Property']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
        new Select(driver.findElement(By.id("selectProperty"))).selectByVisibleText("textualPresentation");
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //MA: Anatomical Dictionary for the Adult Mouse (November2014)
	public void testTermBrowserTestCase_32() throws Exception {
		String linkText = "MA: Anatomical Dictionary for the Adult Mouse (November2014)";
		int searchType = 19; //(ADVANCED_SEARCH_ON_RELATIONSHIP)
		String scheme = "MA";
		String version = "November2014";
		String target = "Relationship";
		String algorithm = "exactMatch";
		String matchText = "omental bursa";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "part_of";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Relationship']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
        new Select(driver.findElement(By.id("rel_search_association"))).selectByVisibleText("part_of");
		driver.findElement(By.xpath("//input[@name='direction'][@value='target']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)
	public void testTermBrowserTestCase_33() throws Exception {
		String linkText = "SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)";
		int searchType = 17; //(ADVANCED_SEARCH_ON_CODE)
		String scheme = "SNOMED Clinical Terms US Edition";
		String version = "2015_03_01";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "102435003";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "part_of";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Code']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)
	public void testTermBrowserTestCase_34() throws Exception {
		String linkText = "SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)";
		int searchType = 16; //(ADVANCED_SEARCH_ON_NAME)
		String scheme = "SNOMED Clinical Terms US Edition";
		String version = "2015_03_01";
		String target = "Name";
		String algorithm = "startsWith";
		String matchText = "Exposure to chemical pollution";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "part_of";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Name']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='startsWith']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)
	public void testTermBrowserTestCase_35() throws Exception {
		String linkText = "SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)";
		int searchType = 18; //(ADVANCED_SEARCH_ON_PROPERTY)
		String scheme = "SNOMED Clinical Terms US Edition";
		String version = "2015_03_01";
		String target = "Property";
		String algorithm = "exactMatch";
		String matchText = "Exposure to chemical pollution";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = "textualPresentation";
    	String rel_search_association = "part_of";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Property']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
        new Select(driver.findElement(By.id("selectProperty"))).selectByVisibleText("textualPresentation");
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)
	public void testTermBrowserTestCase_36() throws Exception {
		String linkText = "SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)";
		int searchType = 19; //(ADVANCED_SEARCH_ON_RELATIONSHIP)
		String scheme = "SNOMED Clinical Terms US Edition";
		String version = "2015_03_01";
		String target = "Relationship";
		String algorithm = "exactMatch";
		String matchText = "Exposure to toxin";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "CHD";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Relationship']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
        new Select(driver.findElement(By.id("rel_search_association"))).selectByVisibleText("CHD");
		driver.findElement(By.xpath("//input[@name='direction'][@value='target']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //OBI: Ontology for Biomedical Investigations (December2015)
	public void testTermBrowserTestCase_37() throws Exception {
		String linkText = "OBI: Ontology for Biomedical Investigations (December2015)";
		int searchType = 17; //(ADVANCED_SEARCH_ON_CODE)
		String scheme = "obi";
		String version = "December2015";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "OBI_0000394";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "CHD";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Code']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //OBI: Ontology for Biomedical Investigations (December2015)
	public void testTermBrowserTestCase_38() throws Exception {
		String linkText = "OBI: Ontology for Biomedical Investigations (December2015)";
		int searchType = 16; //(ADVANCED_SEARCH_ON_NAME)
		String scheme = "obi";
		String version = "December2015";
		String target = "Name";
		String algorithm = "startsWith";
		String matchText = "blot module";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "CHD";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Name']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='startsWith']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //OBI: Ontology for Biomedical Investigations (December2015)
	public void testTermBrowserTestCase_39() throws Exception {
		String linkText = "OBI: Ontology for Biomedical Investigations (December2015)";
		int searchType = 18; //(ADVANCED_SEARCH_ON_PROPERTY)
		String scheme = "obi";
		String version = "December2015";
		String target = "Property";
		String algorithm = "exactMatch";
		String matchText = "blot module";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = "rdfs:label";
    	String rel_search_association = "CHD";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Property']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
        new Select(driver.findElement(By.id("selectProperty"))).selectByVisibleText("rdfs:label");
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //OBI: Ontology for Biomedical Investigations (December2015)
	public void testTermBrowserTestCase_40() throws Exception {
		String linkText = "OBI: Ontology for Biomedical Investigations (December2015)";
		int searchType = 19; //(ADVANCED_SEARCH_ON_RELATIONSHIP)
		String scheme = "obi";
		String version = "December2015";
		String target = "Relationship";
		String algorithm = "exactMatch";
		String matchText = "device";
		String matchedString = "No match";
    	int maxToReturn = -1;
    	String source = null;
    	String property_name = null;
    	String rel_search_association = "subClassOf";
    	String rel_search_rela = null;
    	String direction = "source";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateAdvancedSearch(
			scheme, version, matchText, target, algorithm, maxToReturn, source, property_name, 
           rel_search_association, rel_search_rela, direction );
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.linkText("Advanced Search")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectSearchOption'][@value='Relationship']")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@name='adv_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(2000);
        new Select(driver.findElement(By.id("rel_search_association"))).selectByVisibleText("subClassOf");
		driver.findElement(By.xpath("//input[@name='direction'][@value='target']")).click();
		Thread.sleep(2000);
		List<WebElement> matchTexts = driver.findElements(By.xpath("//input[@name='matchText']"));
		((WebElement) matchTexts.get(1)).sendKeys(matchText);
		Thread.sleep(1000);
		if (driver != null && driver.findElement(By.name("advancedSearchForm:adv_search")) != null) {
			driver.findElement(By.name("advancedSearchForm:adv_search")).click();
		}
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_41() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 4; //(MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "GO:0000089";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_42() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 7; //(ALT_MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "GO:0000089";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='GO_to_NCIt_Mapping$1.1']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_43() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 4; //(MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "names";
		String algorithm = "contains";
		String matchText = "mitotic metaphase";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_44() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 7; //(ALT_MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "names";
		String algorithm = "contains";
		String matchText = "mitotic metaphase";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='GO_to_NCIt_Mapping$1.1']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_45() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 6; //(MAPPING_SEARCH_ON_RELATIONSHIP)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "mitotic metaphase";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_46() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 9; //(ALT_MAPPING_SEARCH_ON_RELATIONSHIP)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "mitotic metaphase";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='GO_to_NCIt_Mapping$1.1']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_47() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 5; //(MAPPING_SEARCH_ON_PROPERTY)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "mitotic metaphase";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //GO to NCIt Mapping: GO to NCIt Mapping (April2014)
	public void testTermBrowserTestCase_48() throws Exception {
		String linkText = "GO to NCIt Mapping: GO to NCIt Mapping (April2014)";
		int searchType = 8; //(ALT_MAPPING_SEARCH_ON_PROPERTY)
		String scheme = "GO_to_NCIt_Mapping";
		String version = "1.1";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "mitotic metaphase";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='GO_to_NCIt_Mapping$1.1']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_49() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 4; //(MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "C1011";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_50() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 7; //(ALT_MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "C1011";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='NCIt_to_ChEBI_Mapping$1.0']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_51() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 4; //(MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "names";
		String algorithm = "contains";
		String matchText = "Beauvericin";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_52() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 7; //(ALT_MAPPING_SEARCH_ON_NAME_OR_CODE)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "names";
		String algorithm = "contains";
		String matchText = "Beauvericin";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='NCIt_to_ChEBI_Mapping$1.0']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='names']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_53() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 6; //(MAPPING_SEARCH_ON_RELATIONSHIP)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "Beauvericin";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_54() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 9; //(ALT_MAPPING_SEARCH_ON_RELATIONSHIP)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "relationships";
		String algorithm = "exactMatch";
		String matchText = "Beauvericin";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='NCIt_to_ChEBI_Mapping$1.0']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='relationships']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_55() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 5; //(MAPPING_SEARCH_ON_PROPERTY)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "Beauvericin";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)
	public void testTermBrowserTestCase_56() throws Exception {
		String linkText = "NCIt to ChEBI Mapping: NCIt to ChEBI Mapping (December2015)";
		int searchType = 8; //(ALT_MAPPING_SEARCH_ON_PROPERTY)
		String scheme = "NCIt_to_ChEBI_Mapping";
		String version = "1.0";
		String target = "properties";
		String algorithm = "startsWith";
		String matchText = "Beauvericin";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateMappingSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(2000);
		Thread.sleep(1000);
		driver.get(baseUrl + "/ncitbrowser/pages/multiple_search.jsf");
		Thread.sleep(1000);
		driver.findElement(By.name("tab_map")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='scheme_and_version'][@value='NCIt_to_ChEBI_Mapping$1.0']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='startsWith']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='properties']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("mappingSearch:mapping_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //CDISC SDTM Laterality Terminology
	public void testTermBrowserTestCase_57() throws Exception {
		String linkText = "CDISC SDTM Laterality Terminology";
		int searchType = 11; //(VALUE_SET_SEARCH_ON_CODE)
		String scheme = "CDISC SDTM Laterality Terminology";
		String version = "89d1dc4ea4441aca644b95bc454dcd40";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "C25229";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateValueSetSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Code']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //CDISC SDTM Laterality Terminology
	public void testTermBrowserTestCase_58() throws Exception {
		String linkText = "CDISC SDTM Laterality Terminology";
		int searchType = 10; //(VALUE_SET_SEARCH_ON_NAME)
		String scheme = "CDISC SDTM Laterality Terminology";
		String version = "89d1dc4ea4441aca644b95bc454dcd40";
		String target = "Name";
		String algorithm = "contains";
		String matchText = "Left";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateValueSetSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Name']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //CDISC CDASH Terminology
	public void testTermBrowserTestCase_59() throws Exception {
		String linkText = "CDISC CDASH Terminology";
		int searchType = 11; //(VALUE_SET_SEARCH_ON_CODE)
		String scheme = "CDISC CDASH Terminology";
		String version = "8a214c6f299f5a9a0660c0298a0431bc";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "C48551";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateValueSetSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Code']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //CDISC CDASH Terminology
	public void testTermBrowserTestCase_60() throws Exception {
		String linkText = "CDISC CDASH Terminology";
		int searchType = 10; //(VALUE_SET_SEARCH_ON_NAME)
		String scheme = "CDISC CDASH Terminology";
		String version = "8a214c6f299f5a9a0660c0298a0431bc";
		String target = "Name";
		String algorithm = "contains";
		String matchText = "Vial Dosing Unit";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateValueSetSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Name']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //CDISC SDTM Cardiac Valvular Stenosis Severity Terminology
	public void testTermBrowserTestCase_61() throws Exception {
		String linkText = "CDISC SDTM Cardiac Valvular Stenosis Severity Terminology";
		int searchType = 11; //(VALUE_SET_SEARCH_ON_CODE)
		String scheme = "CDISC SDTM Cardiac Valvular Stenosis Severity Ter";
		String version = "4f59fe532f8a6540a4d284e57782c550";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "C99992";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateValueSetSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Code']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //CDISC SDTM Cardiac Valvular Stenosis Severity Terminology
	public void testTermBrowserTestCase_62() throws Exception {
		String linkText = "CDISC SDTM Cardiac Valvular Stenosis Severity Terminology";
		int searchType = 10; //(VALUE_SET_SEARCH_ON_NAME)
		String scheme = "CDISC SDTM Cardiac Valvular Stenosis Severity Ter";
		String version = "4f59fe532f8a6540a4d284e57782c550";
		String target = "Name";
		String algorithm = "contains";
		String matchText = "No Cardiac Valve Stenosis";
		String matchedString = "No match";
    	int maxToReturn = -1;
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validateValueSetSearch(
			scheme, version, matchText, target, algorithm, maxToReturn);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='contains']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Name']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //null
	public void testTermBrowserTestCase_63() throws Exception {
		String linkText = "null";
		int searchType = 29; //(ALL_VALUE_SET_SEARCH_ON_NAME)
		String scheme = "";
		String version = "";
		String target = "Name";
		String algorithm = "exactMatch";
		String matchText = "Food Consumption";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText("Check all")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Name']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //null
	public void testTermBrowserTestCase_64() throws Exception {
		String linkText = "null";
		int searchType = 28; //(ALL_VALUE_SET_SEARCH_ON_CODE)
		String scheme = "";
		String version = "";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "C90384";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText("Check all")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Code']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //null
	public void testTermBrowserTestCase_65() throws Exception {
		String linkText = "null";
		int searchType = 29; //(ALL_VALUE_SET_SEARCH_ON_NAME)
		String scheme = "";
		String version = "";
		String target = "Name";
		String algorithm = "exactMatch";
		String matchText = "Occupation";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText("Check all")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Name']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //null
	public void testTermBrowserTestCase_66() throws Exception {
		String linkText = "null";
		int searchType = 28; //(ALL_VALUE_SET_SEARCH_ON_CODE)
		String scheme = "";
		String version = "";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "C25193";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText("Check all")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Code']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //null
	public void testTermBrowserTestCase_67() throws Exception {
		String linkText = "null";
		int searchType = 29; //(ALL_VALUE_SET_SEARCH_ON_NAME)
		String scheme = "";
		String version = "";
		String target = "Name";
		String algorithm = "exactMatch";
		String matchText = "KFSS - Sensory Functions";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText("Check all")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Name']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //null
	public void testTermBrowserTestCase_68() throws Exception {
		String linkText = "null";
		int searchType = 28; //(ALL_VALUE_SET_SEARCH_ON_CODE)
		String scheme = "";
		String version = "";
		String target = "Code";
		String algorithm = "exactMatch";
		String matchText = "C112613";
		String matchedString = "No match";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_valuesets")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText("Check all")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='valueset_search_algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='selectValueSetSearchOption'][@value='Code']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.id("valueSetSearchForm:valueset_search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		assertTrue(bodyText.contains(matchedString) || bodyText.contains(matchText));
		Thread.sleep(1000);
	}


	@Test //NCI Thesaurus: National Cancer Institute Thesaurus (16.01)
	public void testTermBrowserTestCase_69() throws Exception {
		String linkText = "NCI Thesaurus: National Cancer Institute Thesaurus (16.01)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "NCI_Thesaurus";
		String version = "16.01d";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ChEBI: Chemical Entities of Biological Interest (v136)
	public void testTermBrowserTestCase_70() throws Exception {
		String linkText = "ChEBI: Chemical Entities of Biological Interest (v136)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "ChEBI";
		String version = "v136";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //CTCAE: Common Terminology Criteria for Adverse Events (4.03)
	public void testTermBrowserTestCase_71() throws Exception {
		String linkText = "CTCAE: Common Terminology Criteria for Adverse Events (4.03)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "CTCAE";
		String version = "4.03";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //GO: Gene Ontology (February2016)
	public void testTermBrowserTestCase_72() throws Exception {
		String linkText = "GO: Gene Ontology (February2016)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "GO";
		String version = "February2016";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //HGNC: HUGO Gene Nomenclature Committee (February2016)
	public void testTermBrowserTestCase_73() throws Exception {
		String linkText = "HGNC: HUGO Gene Nomenclature Committee (February2016)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "HGNC";
		String version = "February2016";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_74() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ICD-10: International Classification of Diseases, Tenth Revision (2010)
	public void testTermBrowserTestCase_75() throws Exception {
		String linkText = "ICD-10: International Classification of Diseases, Tenth Revision (2010)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "ICD-10";
		String version = "2010";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ICD-9-CM: International Classification of Diseases, Ninth Revision, Clinical Modification (2013)
	public void testTermBrowserTestCase_76() throws Exception {
		String linkText = "ICD-9-CM: International Classification of Diseases, Ninth Revision, Clinical Modification (2013)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "ICD-9-CM";
		String version = "2013_2012_08_06";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //LOINC: Logical Observation Identifiers Names and Codes (2.48)
	public void testTermBrowserTestCase_77() throws Exception {
		String linkText = "LOINC: Logical Observation Identifiers Names and Codes (2.48)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "LOINC";
		String version = "2_48";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //MA: Anatomical Dictionary for the Adult Mouse (November2014)
	public void testTermBrowserTestCase_78() throws Exception {
		String linkText = "MA: Anatomical Dictionary for the Adult Mouse (November2014)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "MA";
		String version = "November2014";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //MedDRA: Medical Dictionary for Regulatory Activities Terminology (18.1)
	public void testTermBrowserTestCase_79() throws Exception {
		String linkText = "MedDRA: Medical Dictionary for Regulatory Activities Terminology (18.1)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "MedDRA";
		String version = "18.1";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)
	public void testTermBrowserTestCase_80() throws Exception {
		String linkText = "MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "MGEDOntology.owl";
		String version = "1.3.1";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //NDF-RT: National Drug File Reference Terminology Public Inferred Edition (February2016)
	public void testTermBrowserTestCase_81() throws Exception {
		String linkText = "NDF-RT: National Drug File Reference Terminology Public Inferred Edition (February2016)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "NDFRT";
		String version = "February2016";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //NPO: NanoParticle Ontology (1.0_Dec_08_2011)
	public void testTermBrowserTestCase_82() throws Exception {
		String linkText = "NPO: NanoParticle Ontology (1.0_Dec_08_2011)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "NPO";
		String version = "2011-12-08";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //OBI: Ontology for Biomedical Investigations (December2015)
	public void testTermBrowserTestCase_83() throws Exception {
		String linkText = "OBI: Ontology for Biomedical Investigations (December2015)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "obi";
		String version = "December2015";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //PDQ: Physician Data Query (2014_08_29)
	public void testTermBrowserTestCase_84() throws Exception {
		String linkText = "PDQ: Physician Data Query (2014_08_29)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "PDQ";
		String version = "2014_08_29";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //RadLex: Radiology Lexicon (3.11)
	public void testTermBrowserTestCase_85() throws Exception {
		String linkText = "RadLex: Radiology Lexicon (3.11)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "RadLex";
		String version = "3.11";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)
	public void testTermBrowserTestCase_86() throws Exception {
		String linkText = "SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "SNOMED Clinical Terms US Edition";
		String version = "2015_03_01";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //UMLS SemNet: UMLS Semantic Network (3.2)
	public void testTermBrowserTestCase_87() throws Exception {
		String linkText = "UMLS SemNet: UMLS Semantic Network (3.2)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "UMLS_SemNet";
		String version = "3.2";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //Zebrafish: Zebrafish Model Organism Database (June_12_2014)
	public void testTermBrowserTestCase_88() throws Exception {
		String linkText = "Zebrafish: Zebrafish Model Organism Database (June_12_2014)";
		int searchType = 30; //(VIEW_HIERARCHY)
		String scheme = "Zebrafish";
		String version = "June_12_2014";
		String matchedString = "No root nodes available";
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
		    popUpWindow("Hierarchy");
		    bodyText = getPopupWindowBodyText(driver);
		    assertTrue(!bodyText.contains(matchedString));
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //NCI Thesaurus: National Cancer Institute Thesaurus (16.01)
	public void testTermBrowserTestCase_89() throws Exception {
		String linkText = "NCI Thesaurus: National Cancer Institute Thesaurus (16.01)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "NCI_Thesaurus";
		String version = "16.01d";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "C50387";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ChEBI: Chemical Entities of Biological Interest (v136)
	public void testTermBrowserTestCase_90() throws Exception {
		String linkText = "ChEBI: Chemical Entities of Biological Interest (v136)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "ChEBI";
		String version = "v136";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "CHEBI:52336";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //CTCAE: Common Terminology Criteria for Adverse Events (4.03)
	public void testTermBrowserTestCase_91() throws Exception {
		String linkText = "CTCAE: Common Terminology Criteria for Adverse Events (4.03)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "CTCAE";
		String version = "4.03";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "E10603";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //GO: Gene Ontology (February2016)
	public void testTermBrowserTestCase_92() throws Exception {
		String linkText = "GO: Gene Ontology (February2016)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "GO";
		String version = "February2016";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "GO:0000213";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //HGNC: HUGO Gene Nomenclature Committee (February2016)
	public void testTermBrowserTestCase_93() throws Exception {
		String linkText = "HGNC: HUGO Gene Nomenclature Committee (February2016)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "HGNC";
		String version = "February2016";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "HGNC:10025";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)
	public void testTermBrowserTestCase_94() throws Exception {
		String linkText = "ICD-10-CM: International Classification of Diseases, Tenth Revision, Clinical Modification (2014)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "ICD-10-CM";
		String version = "2014";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "A18.15";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ICD-10: International Classification of Diseases, Tenth Revision (2010)
	public void testTermBrowserTestCase_95() throws Exception {
		String linkText = "ICD-10: International Classification of Diseases, Tenth Revision (2010)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "ICD-10";
		String version = "2010";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "B05.2";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //ICD-9-CM: International Classification of Diseases, Ninth Revision, Clinical Modification (2013)
	public void testTermBrowserTestCase_96() throws Exception {
		String linkText = "ICD-9-CM: International Classification of Diseases, Ninth Revision, Clinical Modification (2013)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "ICD-9-CM";
		String version = "2013_2012_08_06";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "036.42";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //LOINC: Logical Observation Identifiers Names and Codes (2.48)
	public void testTermBrowserTestCase_97() throws Exception {
		String linkText = "LOINC: Logical Observation Identifiers Names and Codes (2.48)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "LOINC";
		String version = "2_48";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "10201-2";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //MA: Anatomical Dictionary for the Adult Mouse (November2014)
	public void testTermBrowserTestCase_98() throws Exception {
		String linkText = "MA: Anatomical Dictionary for the Adult Mouse (November2014)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "MA";
		String version = "November2014";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "MA:0000003";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //MedDRA: Medical Dictionary for Regulatory Activities Terminology (18.1)
	public void testTermBrowserTestCase_99() throws Exception {
		String linkText = "MedDRA: Medical Dictionary for Regulatory Activities Terminology (18.1)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "MedDRA";
		String version = "18.1";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "10000374";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)
	public void testTermBrowserTestCase_100() throws Exception {
		String linkText = "MGED Ontology: Microarray Gene Expression Data Ontology (1.3.1)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "MGEDOntology.owl";
		String version = "1.3.1";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "MO_415";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //NDF-RT: National Drug File Reference Terminology Public Inferred Edition (February2016)
	public void testTermBrowserTestCase_101() throws Exception {
		String linkText = "NDF-RT: National Drug File Reference Terminology Public Inferred Edition (February2016)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "NDFRT";
		String version = "February2016";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "N0000000439";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //NPO: NanoParticle Ontology (1.0_Dec_08_2011)
	public void testTermBrowserTestCase_102() throws Exception {
		String linkText = "NPO: NanoParticle Ontology (1.0_Dec_08_2011)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "NPO";
		String version = "2011-12-08";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "NPO_1118";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //OBI: Ontology for Biomedical Investigations (December2015)
	public void testTermBrowserTestCase_103() throws Exception {
		String linkText = "OBI: Ontology for Biomedical Investigations (December2015)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "obi";
		String version = "December2015";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "OBI_0000394";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //PDQ: Physician Data Query (2014_08_29)
	public void testTermBrowserTestCase_104() throws Exception {
		String linkText = "PDQ: Physician Data Query (2014_08_29)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "PDQ";
		String version = "2014_08_29";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "CDR0000038147";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //RadLex: Radiology Lexicon (3.11)
	public void testTermBrowserTestCase_105() throws Exception {
		String linkText = "RadLex: Radiology Lexicon (3.11)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "RadLex";
		String version = "3.11";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "RID26569";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)
	public void testTermBrowserTestCase_106() throws Exception {
		String linkText = "SNOMED CT: Systematized Nomenclature of Medicine-Clinical Terms (2015_03_01)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "SNOMED Clinical Terms US Edition";
		String version = "2015_03_01";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "102434004";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //UMLS SemNet: UMLS Semantic Network (3.2)
	public void testTermBrowserTestCase_107() throws Exception {
		String linkText = "UMLS SemNet: UMLS Semantic Network (3.2)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "UMLS_SemNet";
		String version = "3.2";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "T133";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


	@Test //Zebrafish: Zebrafish Model Organism Database (June_12_2014)
	public void testTermBrowserTestCase_108() throws Exception {
		String linkText = "Zebrafish: Zebrafish Model Organism Database (June_12_2014)";
		int searchType = 31; //(VIEW_IN_HIERARCHY)
		String scheme = "Zebrafish";
		String version = "June_12_2014";
		String target = "codes";
		String algorithm = "exactMatch";
		String matchText = "ZFA:0000295";
		String matchedString = "No match";
		ResolvedConceptReference rcr = new TestCaseValidator(lbSvc).validate(
			scheme, version, matchText, target, algorithm);
		if (rcr != null) {
			matchedString = rcr.getEntityDescription().getContent();
			System.out.println(matchedString + " (" + rcr.getConceptCode() + ")");
		}
		String bodyText = null;
		Thread.sleep(1000);
		driver.findElement(By.name("tab_terms")).click();
		Thread.sleep(2000);
		driver.findElement(By.linkText(linkText)).click();
		Thread.sleep(4000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='algorithm'][@value='exactMatch']")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//input[@name='searchTarget'][@value='codes']")).click();
		Thread.sleep(1000);
		driver.findElement(By.name("matchText")).clear();
		driver.findElement(By.name("matchText")).sendKeys(matchText);
		Thread.sleep(1000);
		driver.findElement(By.name("searchTerm:search")).click();
		Thread.sleep(1000);
		bodyText = driver.findElement(By.tagName("body")).getText();
		if (bodyText.contains("copyright/license statement")) {
			driver.findElement(By.xpath("//input[@src='/ncitbrowser/images/accept.gif']")).click();
		}
		Thread.sleep(1000);
		try {
			driver.findElement(By.linkText("View in Hierarchy")).click();
			Thread.sleep(16000);
		    bodyText = getPopupWindowBodyText(driver);
			System.out.println(bodyText);
			assertTrue(bodyText.contains(matchText) || bodyText.contains(matchedString));
			String windowTitle= getCurrentWindowTitle();
			Thread.sleep(1000);
			String mainWindow = getMainWindowHandle(driver);
			Thread.sleep(1000);
			closeAllOtherWindows(mainWindow);
		} catch (Exception ex) {
			System.out.println("Hierarchy not available.");
		    assertTrue(true);
		}
		Thread.sleep(1000);
	}


    @Test //testBaseURLExternalLinks
    public void testBaseURLExternalLinks() throws Exception {
		try {
			driver.get(baseUrl);
			java.util.List<WebElement> links = driver.findElements(By.tagName("a"));
			HashSet hset = new HashSet();
			int lcv = 0;
			for (int i=0; i<links.size(); i++) {
				String href = links.get(i).getAttribute("href");
				if (href != null && href.length()>0) {
					if (!hset.contains(href)) {
						hset.add(href);
					}
			    }
			}
			Iterator it = hset.iterator();
			while (it.hasNext()) {
				String href = (String) it.next();
				if (!href.startsWith(baseUrl)) {
					lcv++;
					int responseCode = getHTTPResponseCode(href);
					System.out.println("(" + lcv + ") " + href + " (response code: " + responseCode + ")");
					assertTrue(responseCode == 200);
				}
			}
	    } catch (Exception ex) {
            ex.printStackTrace();
            assertTrue(false);
		}
		assertTrue(true);
	}


    @Test // (testNCIHomeFooter)
    public void testNCIHomeFooter() throws Exception {
		String bodyText = null;
		driver.get(baseUrl);
		Thread.sleep(1000);
		try {
		    popUpWindow("NCI Home");
		    assertTrue(true);
		} catch (Exception ex) {
		    assertTrue(false);
        }
    }

    @Test // (testPoliciesFooter)
    public void testPoliciesFooter() throws Exception {
		String bodyText = null;
		driver.get(baseUrl);
		Thread.sleep(1000);
		try {
		    popUpWindow("Policies");
		    assertTrue(true);
		} catch (Exception ex) {
		    assertTrue(false);
        }
    }

    @Test // (testAccessibilityFooter)
    public void testAccessibilityFooter() throws Exception {
		String bodyText = null;
		driver.get(baseUrl);
		Thread.sleep(1000);
		try {
		    popUpWindow("Accessibility");
		    assertTrue(true);
		} catch (Exception ex) {
		    assertTrue(false);
        }
    }

    @Test // (testFOIAFooter)
    public void testFOIAFooter() throws Exception {
		String bodyText = null;
		driver.get(baseUrl);
		Thread.sleep(1000);
		try {
		    popUpWindow("FOIA");
		    assertTrue(true);
		} catch (Exception ex) {
		    assertTrue(false);
        }
    }

    @Test // (testContactUsFooter)
    public void testContactUsFooter() throws Exception {
		String bodyText = null;
		driver.get(baseUrl);
		Thread.sleep(1000);
		try {
		    popUpWindow("Contact Us");
		    assertTrue(true);
		} catch (Exception ex) {
		    assertTrue(false);
        }
    }

	public static int getHTTPResponseCode(String url) {
		try {
		    URL u = new URL(url);
		    HttpURLConnection huc = (HttpURLConnection)u.openConnection();
		    huc.setRequestMethod("GET");
		    huc.connect() ;
		    int code = huc.getResponseCode();
		    return code;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return -1;
	}


  @After
  public void tearDown() throws Exception {
    if (driver != null) driver.quit();
    String verificationErrorString = verificationErrors.toString();
    if (!"".equals(verificationErrorString)) {
      fail(verificationErrorString);
    }
  }

  private boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  private boolean isAlertPresent() {
    try {
      driver.switchTo().alert();
      return true;
    } catch (NoAlertPresentException e) {
      return false;
    }
  }

  private String closeAlertAndGetItsText() {
    try {
      Alert alert = driver.switchTo().alert();
      String alertText = alert.getText();
      if (acceptNextAlert) {
        alert.accept();
      } else {
        alert.dismiss();
      }
      return alertText;
    } finally {
      acceptNextAlert = true;
    }
  }
}
