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
import org.apache.log4j.*;
import org.junit.*;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.DataModel.NCIHistory.*;
import org.LexGrid.LexBIG.DataModel.NCIHistory.types.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.*;
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


public class TestHistoryUtils extends ServiceTestCase {
    final static String testID = "TestHistoryUtils";
    private static DateFormat _dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private static Logger _logger = Logger.getLogger(TestHistoryUtils.class);

    private	Vector csVec = null;
    HistoryService hs = null;

	LexBIGService lbSvc;
	CodingSchemeDataUtils codingSchemeDataUtils = null;
	private TestCaseGenerator testCaseGenerator = null;
	private RandomVariateGenerator rvGenerator = null;

    @Before
	public void setUp(){
		lbSvc = LexEVSServiceUtil.getLexBIGService();
		codingSchemeDataUtils = new CodingSchemeDataUtils(lbSvc);
		csVec = codingSchemeDataUtils.getCodingSchemes();
		rvGenerator = new RandomVariateGenerator();
		testCaseGenerator = new TestCaseGenerator(lbSvc);
	}

    @Override
    protected String getTestID() {
        return testID;
    }


    @Test
    public void testHistory() throws Exception {
		int m = 0;
        for (int i = 0; i < csVec.size(); i++) {
            String t = (String) csVec.get(i);
            Vector u = StringUtils.parseData(t);
            String scheme = (String) u.elementAt(0);
            if (scheme != null) {
				String version = (String) u.elementAt(1);
				boolean available = isHistoryServiceAvailable(scheme);
				System.out.println(scheme + "(" + version + ") -- " + " history available? " + available);

				if (scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCIT_CS_NAME) == 0 && !available) {
					m++;
				} else if (scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCI_METATHESAURUS) == 0 && !available) {
					m++;
				}

				if (scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCIT_CS_NAME) == 0) {
					ResolvedConceptReferenceList rcrl = testCaseGenerator.generateTestCases(scheme, version, ServiceTestCase.SAMPLE_SIZE,
																							TestCaseGenerator.TYPE_TERMINOLOGY);

					int sel_case = rvGenerator.uniform(0, rcrl.getResolvedConceptReferenceCount()-1);
					ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(sel_case);
					String code = rcr.getCode();
					System.out.println("\t" + rcr.getEntityDescription().getContent() + " (" + code + ")");
					try {
						Vector<String> actions = getEditActions(scheme, version, null, code);
						if (actions.size() == 0) {
							System.out.println("\t\tNo edit action.");
						} else {
							for (int j=0; j<actions.size(); j++) {
								String s = (String) actions.elementAt(j);
								System.out.println("\t\t" + s);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						m++;
					}
				} else if (scheme.compareTo(gov.nih.nci.evs.browser.common.Constants.NCI_METATHESAURUS) == 0) {
					ResolvedConceptReference rcr = testCaseGenerator.generateNCIMResolvedConceptReference();
					String code = rcr.getCode();
					System.out.println("\t" + rcr.getEntityDescription().getContent() + " (" + code + ")");
					try {
						Vector<String> actions = getEditActions(scheme, version, null, code);
						if (actions.size() == 0) {
							System.out.println("\t\tNo edit action.");
						} else {
							for (int j=0; j<actions.size(); j++) {
								String s = (String) actions.elementAt(j);
								System.out.println("\t\t" + s);
							}
						}
					} catch (Exception ex) {
						ex.printStackTrace();
						m++;
					}
				}
			}
		}
		assertTrue(m == 0);
   }


    public Vector<String> getTableHeader() {
        Vector<String> v = new Vector<String>();
        v.add("Edit Actions");
        v.add("Date");
        v.add("Reference Concept");
        return v;
    }

    public boolean isHistoryServiceAvailable(String codingSchemeName) {
        //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        //HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
            if (hs != null) {
                return true;
			}
        } catch (Exception ex) {
            // _logger.error("Unable to getHistoryService for " +
            // codingSchemeName);
        }
        return false;
    }

    /*
     * public Vector<String> getEditActions(String codingSchemeName,
     * String vers, String ltag, String code) throws LBException { LexBIGService
     * lbSvc = RemoteServerUtil.createLexBIGService(); //HistoryService hs =
     * lbSvc.getHistoryService(CODING_SCHEME_NAME); HistoryService hs =
     * lbSvc.getHistoryService(codingSchemeName);
     *
     * try { NCIChangeEventList list = hs.getEditActionList(Constructors
     * .createConceptReference(code, null), null, null); return
     * getEditActions(codingSchemeName, vers, ltag, code, list); } catch
     * (Exception ex) { ex.printStackTrace(); } return null; }
     */

    public Vector<String> getEditActions(String codingSchemeName,
        String vers, String ltag, String code) throws LBException {
        HistoryService hs = lbSvc.getHistoryService(codingSchemeName);
        return getEditActions(hs, codingSchemeName, vers, ltag, code);
    }

    // [#23370] NCIt concept history shows wrong Code (KLO, 09/28/09)
    private Vector<String> getEditActions(
        HistoryService hs, String codingSchemeName, String vers, String ltag,
        String code) throws LBException {
        try {
            Entity c = new SearchUtils(lbSvc).getConceptByCode(codingSchemeName, vers, ltag, code);
            if (c == null)
                return null;
            NCIChangeEventList list =
                hs.getEditActionList(Constructors.createConceptReference(code,
                    null), null, null);
            /*
             * Boolean isActive = c.isIsActive(); NCIChangeEventList list =
             * null; if (isActive != null && isActive.equals(Boolean.FALSE)) {
             * list =
             * hs.getDescendants(Constructors.createConceptReference(code,
             * null)); } else { list =
             * hs.getEditActionList(Constructors.createConceptReference(code,
             * null), null, null); }
             */
            return getEditActions(codingSchemeName, vers, ltag, code, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private Vector<String> getEditActions(String codingSchemeName,
        String vers, String ltag, String code, NCIChangeEventList list) {
		//LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

        java.util.Enumeration<? extends NCIChangeEvent> enumeration = list.enumerateEntry();

        Vector<String> v = new Vector<String>();
        HashSet<String> hset = new HashSet<String>();

        DateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");

        while (enumeration.hasMoreElements()) {
            NCIChangeEvent event = (NCIChangeEvent) enumeration.nextElement();

            event =
                convertNCIChangeEvent(lbSvc, codingSchemeName, vers, ltag,
                    code, event);

            ChangeType type = event.getEditaction();
            Date date = event.getEditDate();
            String rCode = event.getReferencecode();
            String desc = "N/A";
            if (rCode != null && rCode.length() > 0
                && !rCode.equalsIgnoreCase("null")) {
                Entity c =
                    //new DataUtils().getConceptByCode(lbSvc, codingSchemeName, vers, ltag, rCode, false);
                    new SearchUtils(lbSvc).getConceptByCode(codingSchemeName, vers, ltag, rCode);
                // KLO
                if (c != null) {
                    String name = c.getEntityDescription().getContent();
                    desc = name + " (Code " + rCode + ")";
                } else {
                    desc = rCode;
                }
            }

            //String info = type + "|" + _dataFormatter.format(date) + "|" + desc;
            String info = type + "|" + dataFormatter.format(date) + "|" + desc;

            if (hset.contains(info))
                continue;
            v.add(info);
            hset.add(info);
        }
        return v;
    }

    public Vector<String> getAncestors(String codingSchemeName,
        String vers, String ltag, String code) {
        //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
        } catch (Exception ex) {
            _logger
                .error("Unable to getHistoryService for " + codingSchemeName);
            return null;
        }

        try {
            NCIChangeEventList list =
                hs
                    .getAncestors(Constructors.createConceptReference(code,
                        null));

            return getEditActions(codingSchemeName, vers, ltag, code, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Vector<String> getDescendants(String codingSchemeName,
        String vers, String ltag, String code) {
        //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
        } catch (Exception ex) {
            _logger
                .error("Unable to getHistoryService for " + codingSchemeName);
            return null;
        }

        try {
            NCIChangeEventList list =
                hs.getDescendants(Constructors.createConceptReference(code,
                    null));

            return getEditActions(codingSchemeName, vers, ltag, code, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public Vector<String> getDescendantCodes(String codingSchemeName,
        String vers, String ltag, String code) {
        //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
        } catch (Exception ex) {
            _logger
                .error("Unable to getHistoryService for " + codingSchemeName);
            return null;
        }

        Vector<String> v = new Vector<String>();
        try {
            NCIChangeEventList list =
                hs.getDescendants(Constructors.createConceptReference(code,
                    null));

            HashSet<String> hset = new HashSet<String>();
            Enumeration<? extends NCIChangeEvent> enumeration = list.enumerateEntry();
            while (enumeration.hasMoreElements()) {
                NCIChangeEvent event = (NCIChangeEvent) enumeration.nextElement();
                ChangeType type = event.getEditaction();
                Date date = event.getEditDate();
                String rCode = event.getReferencecode();
                String name = "unassigned";
                if (rCode != null && rCode.length() > 0
                    && !rCode.equalsIgnoreCase("null")) {
                    Entity c =
                        //new DataUtils().getConceptByCode(lbSvc, codingSchemeName, vers, ltag, rCode, false);
                        new SearchUtils(lbSvc).getConceptByCode(codingSchemeName, vers, ltag, rCode);

                    if (c != null) {
                        name = c.getEntityDescription().getContent();
                    }
                }
                String info = name + "|" + rCode;
                if (hset.contains(info))
                    continue;
                v.add(info);
                hset.add(info);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return v;
    }

    /*
     * C21344|Growth_Factor_Gene|merge|12-AUG-03|C21344|Growth_Factor_Gene
     * C18438
     * |Proto-Oncogene_Growth_Factor|merge|12-AUG-03|C21344|Growth_Factor_Gene
     * C18438|Proto-Oncogene_Growth_Factor|retire|12-AUG-03|(null)|(null)
     */

    private NCIChangeEvent convertNCIChangeEvent(LexBIGService lbSvc,
        String codingSchemeName, String vers, String ltag, String code,
        NCIChangeEvent event) {
        if (event == null)
            return null;
        ChangeType type = event.getEditaction();
        String type_str = type.toString();
        if (type_str.compareTo("merge") == 0) {
            Date date = event.getEditDate();
            String rCode = event.getReferencecode();
            if (rCode.compareTo(code) == 0) {

                _logger.debug("rCode." + rCode + " == code == " + code);

                try {
                    HistoryService hs =
                        lbSvc.getHistoryService(codingSchemeName);
                    if (hs == null) {
                        _logger.warn("Unable to getHistoryService for "
                            + codingSchemeName);
                        return null;
                    }
                    try {
                        _logger.debug("\tcheck Ancestors");

                        NCIChangeEventList list =
                            hs.getAncestors(Constructors
                                .createConceptReference(code, null));
                        Enumeration<? extends NCIChangeEvent> enumeration =
                            list.enumerateEntry();
                        //Vector<String> v = new Vector<String>();
                        //HashSet<String> hset = new HashSet<String>();
                        while (enumeration.hasMoreElements()) {
                            NCIChangeEvent event2 = (NCIChangeEvent) enumeration.nextElement();
                            String con_code = event2.getConceptcode();
                            String ref_code = event2.getReferencecode();

                            _logger.debug("\tAncestor -- con_code " + con_code
                                + "; ref_code == " + ref_code);

                            Date date2 = event2.getEditDate();
                            ChangeType type2 = event2.getEditaction();
                            String type_str2 = type2.toString();
                            if (type_str.compareTo("merge") == 0
                                && ref_code.compareTo(con_code) != 0
                                && ref_code.compareTo(code) == 0
                                && date.toString().compareTo(date2.toString()) == 0) {
                                // _logger.debug("(***) con_code: " + con_code +
                                // " ref_code: " + ref_code);

                                _logger.debug("\tsubstituting...");
                                event2.setConceptcode(ref_code);
                                event2.setReferencecode(con_code);
                                return event2;
                            }
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        _logger.error("getAncestors throws exception.");
                    }

                    try {
                        _logger.debug("\tcheck Descendants");

                        NCIChangeEventList list =
                            hs.getDescendants(Constructors
                                .createConceptReference(code, null));
                        Enumeration<? extends NCIChangeEvent> enumeration =
                            list.enumerateEntry();
                        //Vector<String> v = new Vector<String>();
                        //HashSet<String> hset = new HashSet<String>();
                        while (enumeration.hasMoreElements()) {
                            NCIChangeEvent event2 = (NCIChangeEvent) enumeration.nextElement();
                            String con_code = event2.getConceptcode();
                            String ref_code = event2.getReferencecode();

                            _logger.debug("\tDescendant con_code " + con_code
                                + "; ref_code == " + ref_code);

                            Date date2 = event2.getEditDate();
                            ChangeType type2 = event2.getEditaction();
                            String type_str2 = type2.toString();
                            if (type_str.compareTo("merge") == 0
                                && ref_code.compareTo(con_code) != 0
                                && ref_code.compareTo(code) == 0
                                && date.toString().compareTo(date2.toString()) == 0) {
                                // _logger.debug("(***) con_code: " + con_code +
                                // " ref_code: " + ref_code);

                                _logger.debug("\tsubstituting...");
                                event2.setConceptcode(ref_code);
                                event2.setReferencecode(con_code);
                                return event2;
                            }
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        _logger.error("getAncestors throws exception.");
                    }

                } catch (Exception ex) {
                    // ex.printStackTrace();
                    _logger.error("getAncestors throws exception.");
                }
            }

        }

        return event;
    }



	public static void main(String [] args) {
		try {
			TestHistoryUtils test = new TestHistoryUtils();
			test.setUp();
			test.testHistory();
		} catch (Exception ex) {

		}
	}
}


