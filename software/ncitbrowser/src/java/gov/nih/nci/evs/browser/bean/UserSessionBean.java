package gov.nih.nci.evs.browser.bean;

import gov.nih.nci.evs.searchlog.*;

import java.util.*;
import java.net.URI;

import javax.faces.context.*;
import javax.faces.event.*;
import javax.faces.model.*;
import javax.servlet.http.*;

import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;

import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.bean.*;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.searchlog.*;
import org.apache.log4j.*;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;
import nl.captcha.Captcha;
//import nl.captcha.audio.*;

import nl.captcha.audio.AudioCaptcha;
import javax.servlet.ServletOutputStream;


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
 * @author EVS Team
 * @version 1.0
 *
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class UserSessionBean extends Object {
    private static Logger _logger = Logger.getLogger(UserSessionBean.class);

    private static String _contains_warning_msg =
        "(WARNING: Only a subset of results may appear due to current limits in the terminology server (see Known Issues on the Help page).)";
    private String _selectedQuickLink = null;
    private List _quickLinkList = null;

    public List<SelectItem> _ontologyList = null;
    public List<String> _ontologiesToSearchOn = null;


    private HashMap<String, OntologyInfo> _ontologyInfo_map = null;
    private Vector display_name_vec = new Vector();


    public String contextPath = null;
    public String action_coding_scheme = null;

    private String _selectedPageSize = null;
    private List _pageSizeList = null;


    public UserSessionBean() {
        _ontologiesToSearchOn = new ArrayList<String>();
        contextPath = getContextPath();
        _ontologyInfo_map = new HashMap<String, OntologyInfo>();

        _selectedPageSize = "50";
    }


    public String getContextPath() {
        if (contextPath == null) {
			HttpServletRequest request =
				(HttpServletRequest) FacesContext.getCurrentInstance()
					.getExternalContext().getRequest();

			contextPath = request.getContextPath();
		}
		return contextPath;
    }


    public void setSelectedQuickLink(String selectedQuickLink) {
        _selectedQuickLink = selectedQuickLink;
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        request.getSession().setAttribute("selectedQuickLink",
            selectedQuickLink);
    }

    public String getSelectedQuickLink() {
        return _selectedQuickLink;
    }

    public void quickLinkChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();

        // _logger.debug("quickLinkChanged; " + newValue);
        setSelectedQuickLink(newValue);

        HttpServletResponse response =
            (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();

        String targetURL = null;// "http://nciterms.nci.nih.gov/";
        if (_selectedQuickLink.compareTo("NCI Terminology Browser") == 0) {
            targetURL = "http://nciterms.nci.nih.gov/";
        }
        try {
            response.sendRedirect(response.encodeRedirectURL(targetURL));
        } catch (Exception ex) {
            ex.printStackTrace();
            // send error message
        }

    }

    public List getQuickLinkList() {
        _quickLinkList = new ArrayList();
        _quickLinkList.add(new SelectItem("Quick Links"));
        _quickLinkList.add(new SelectItem("NCI Terminology Browser"));
        _quickLinkList.add(new SelectItem("NCI MetaThesaurus"));
        _quickLinkList.add(new SelectItem("EVS Home"));
        _quickLinkList.add(new SelectItem("NCI Terminology Resources"));
        return _quickLinkList;
    }



    public String searchAction() {

HttpServletResponse response =
	(HttpServletResponse) FacesContext.getCurrentInstance()
		.getExternalContext().getResponse();
response.setContentType("text/html;charset=utf-8");

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

boolean mapping_search = false;
String single_mapping_search = HTTPUtils.cleanXSS((String) request.getParameter("single_mapping_search"));

if (single_mapping_search != null && single_mapping_search.compareTo("true") == 0) {
	mapping_search = true;

    request.getSession().setAttribute("nav_type", "terminologis");
	String cs_dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
	String cs_version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
}

        String max_str = null;
        int maxToReturn = -1;// 1000;
        try {
            max_str =
                NCItBrowserProperties.getInstance().getProperty(
                    NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {

        }
        ResolvedConceptReferencesIterator iterator = null;

        String matchAlgorithm = HTTPUtils.cleanXSS((String) request.getParameter("algorithm"));
        // 051512 KLO AppScan
        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
			matchAlgorithm = "exactMatch";
		}

        String searchTarget = HTTPUtils.cleanXSS((String) request.getParameter("searchTarget"));
        // 051512 KLO AppScan
        if (searchTarget == null || searchTarget.length() == 0) {
			searchTarget = "names";
		}

        request.getSession().setAttribute("searchTarget", searchTarget);
        request.getSession().setAttribute("algorithm", matchAlgorithm);

        String matchText = HTTPUtils.cleanMatchTextXSS((String) request.getParameter("matchText"));
        if (matchText != null) {
            matchText = matchText.trim();
            request.getSession().setAttribute("matchText", matchText);
		}

        // [#19965] Error message is not displayed when Search Criteria is not
        // proivded
        if (matchText == null || matchText.length() == 0) {
            String message = "Please enter a search string.";
            request.getSession().setAttribute("message", message);
            request.removeAttribute("matchText");

            if (mapping_search) {
				request.getSession().setAttribute("navigation_type", "mappings");
                return "return_to_mapping_home";
			}
            return "message";
        }


//[NCITERM-613] Remove the minimum 3-character search string length restriction on all name searches.
        //if (matchText != null && matchText.length() < 3
//        if (matchText.length() < 3
              //&& matchAlgorithm.compareTo("contains") == 0) {
//			  && matchAlgorithm.compareTo("exactMatch") != 0) {
//            String message = "Please enter a search string of length no less than 3.";
//            request.getSession().setAttribute("message", message);
//            request.removeAttribute("matchText");

//            if (mapping_search) {
//                request.getSession().setAttribute("navigation_type", "mappings");
//                return "return_to_mapping_home";
//			}
//            return "message";
//        }


        boolean ranking = true;
        String scheme = null;
        String version = null;

		String scheme_and_version = HTTPUtils.cleanXSS((String) request.getParameter("scheme_and_version"));

		if (scheme_and_version != null) {
			Vector cs_version_vec = DataUtils.parseData(scheme_and_version, "$");
			scheme = (String) cs_version_vec.elementAt(0);
			version = (String) cs_version_vec.elementAt(1);
			request.getSession().setAttribute("scheme_and_version", scheme_and_version);

			request.getSession().setAttribute("dictionary", scheme);
			request.getSession().setAttribute("version", version);

		} else {
			scheme = HTTPUtils.cleanXSS((String)request.getParameter("scheme"));

			//String searchaction_dictionary = request.getParameter("dictionary");

			if (scheme == null) {
				scheme = (String) request.getSession().getAttribute("scheme");
			}
			if (scheme == null) {
				scheme = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
			}

            if (mapping_search) {
				String msg = "No mapping data set is selected.";
				request.getSession().setAttribute("message", msg);
				return "return_to_mapping_home";

			} else {
				if (scheme == null) {
					scheme = Constants.CODING_SCHEME_NAME;
				}

if (scheme != null) {
	String check_formal_name = DataUtils.getFormalName(scheme);
	if (check_formal_name == null) {
		String message = "Unable to identify vocabulary: " + scheme;
		request.getSession().setAttribute("message", message);
	    return "message";
	}
}

				version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
				if (version == null) {
					version = DataUtils.getVocabularyVersionByTag(scheme, "PRODUCTION");
				}

				else {
                    if (!DataUtils.validateCodingSchemeVersion(scheme, version)) {
						String message = "Unable to identify vocabulary " + scheme + " version: " + version;
						request.getSession().setAttribute("message", message);
						return "message";
					}
				}

		    }
	    }

        request.setAttribute("dictionary", scheme);
	    request.setAttribute("version", version);

		boolean isMapping = DataUtils.isMapping(scheme, version);

        _logger.debug("UserSessionBean scheme: " + scheme);
        _logger.debug("searchAction version: " + version);




boolean retval = HTTPUtils.validateRequestParameters(request);
if (!retval) {
	System.out.println("searchAction returns invalid_parameter");
	return "invalid_parameter";
}



        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		if (isMapping) {
				if (searchTarget.compareTo("names") == 0) {
					/*
					ResolvedConceptReferencesIteratorWrapper wrapper = new MappingSearchUtils().searchByCode(
						scheme, version, matchText,
						matchAlgorithm, maxToReturn);
					*/

					//ResolvedConceptReferencesIteratorWrapper wrapper = new MappingSearchUtils().searchByNameOrCode(
					//	scheme, version, matchText,
					//	matchAlgorithm, maxToReturn, SearchUtils.SEARCH_BY_NAME_ONLY);

					iterator = new MappingSearchUtils(lbSvc).searchByNameOrCode(
						scheme, version, matchText,
						matchAlgorithm, maxToReturn, SearchUtils.SEARCH_BY_NAME_ONLY);


					//if (wrapper != null) {
					//	iterator = wrapper.getIterator();
					//}

                    if (iterator != null) {
						try {
							int numberRemaining = iterator.numberRemaining();
							if (numberRemaining == 0) {
								iterator = null;
							}

						} catch (Exception ex) {
							//ex.printStackTrace();
						}
				    }

                    /*
					if (iterator == null) {
						wrapper = new MappingSearchUtils().searchByName(
							scheme, version, matchText,
							matchAlgorithm, maxToReturn);
						if (wrapper != null) {
							iterator = wrapper.getIterator();
						} else {
							iterator = null;
						}
					}
					*/

					if (iterator == null) {

						iterator = new MappingSearchUtils(lbSvc).searchByName(
							scheme, version, matchText,
							matchAlgorithm, maxToReturn);
					}
				} else if (searchTarget.compareTo("codes") == 0) {
					//ResolvedConceptReferencesIteratorWrapper wrapper = new MappingSearchUtils().searchByCode(
					iterator = new MappingSearchUtils(lbSvc).searchByNameOrCode(
						scheme, version, matchText,
						matchAlgorithm, maxToReturn, SearchUtils.SEARCH_BY_CODE_ONLY);
/*
					if (wrapper != null) {
						iterator = wrapper.getIterator();
					}
*/
                    if (iterator != null) {
						try {
							int numberRemaining = iterator.numberRemaining();
							if (numberRemaining == 0) {
								iterator = null;
							}

						} catch (Exception ex) {
							//ex.printStackTrace();
						}
				    }

					if (iterator == null) {
						iterator = new MappingSearchUtils(lbSvc).searchByCode(
							scheme, version, matchText,
							matchAlgorithm, maxToReturn);
							/*
						if (wrapper != null) {
							iterator = wrapper.getIterator();
						} else {
							iterator = null;
						}
						*/
					}


				} else if (searchTarget.compareTo("properties") == 0) {

					iterator = new MappingSearchUtils(lbSvc).searchByProperties(
						scheme, version, matchText,
						matchAlgorithm, maxToReturn);
						/*
					if (wrapper != null) {
						iterator = wrapper.getIterator();
					} else {
						iterator = null;
					}
					*/


				} else if (searchTarget.compareTo("relationships") == 0) {

					iterator = new MappingSearchUtils(lbSvc).searchByRelationships(
						scheme, version, matchText,
						matchAlgorithm, maxToReturn);
						/*
					if (wrapper != null) {
						iterator = wrapper.getIterator();
					} else {
						iterator = null;
					}
					*/

				}

				if (iterator == null) {
					String msg = "No match.";
					if (matchAlgorithm.compareTo(Constants.EXACT_SEARCH_ALGORITHM) == 0) {
						String t = searchTarget.toLowerCase();
						if (t.indexOf("code") != -1) {
							msg = Constants.ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE;
						} else {
							msg = Constants.ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS;
						}
					}
					request.getSession().setAttribute("message", msg);
					request.getSession().setAttribute("dictionary", scheme);
					request.getSession().setAttribute("version", version);

					if (mapping_search) {
						return "return_to_mapping_home";
					} else {
						return "message";
				    }
				}

				int numberRemaining = 0;
				try {
					numberRemaining = iterator.numberRemaining();
					if (numberRemaining == 0) {
						String msg = "No match.";
						if (matchAlgorithm.compareTo(Constants.EXACT_SEARCH_ALGORITHM) == 0) {
							String t = searchTarget.toLowerCase();
							if (t.indexOf("code") != -1) {
								msg = Constants.ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE;
							} else {
								msg = Constants.ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS;
							}
						}

						request.getSession().setAttribute("message", msg);
						request.getSession().setAttribute("dictionary", scheme);
						request.getSession().setAttribute("version", version);

						if (mapping_search) {
							return "return_to_mapping_home";
						} else {
							return "message";
						}
					}


				} catch (Exception ex) {
					ex.printStackTrace();
					String msg = "An unexpected exception encountered.";
					request.getSession().setAttribute("message", msg);
					request.getSession().setAttribute("dictionary", scheme);
					request.getSession().setAttribute("version", version);

					if (mapping_search) {
						return "return_to_mapping_home";
					} else {
						return "message";
					}
				}

				MappingIteratorBean mappingIteratorBean = new MappingIteratorBean(
				iterator,
				numberRemaining, // number remaining
				0,    // istart
				50,   // iend,
				//numberRemaining, // size,
				0,    // pageNumber,
				1);   // numberPages
/*
				mappingIteratorBean.initialize(
				iterator,
				numberRemaining, // number remaining
				0,    // istart
				50,   // iend,
				numberRemaining, // size,
				0,    // pageNumber,
				1);   // numberPages
*/
				mappingIteratorBean.initialize();
				request.getSession().setAttribute("mapping_search_results", mappingIteratorBean);
				request.getSession().setAttribute("dictionary", scheme);
				//request.getSession().setAttribute("scheme", scheme);
				request.getSession().setAttribute("version", version);
				return "mapping_search_results";
		}

        // KLO, 012610
        if (searchTarget.compareTo("relationships") == 0
            && matchAlgorithm.compareTo("contains") == 0) {
            String text = matchText.trim();
            if (text.length() < NCItBrowserProperties
                .getMinimumSearchStringLength()) {
                String msg = Constants.ERROR_REQUIRE_MORE_SPECIFIC_QUERY_STRING;
                request.getSession().setAttribute("message", msg);
                request.getSession().setAttribute("vocabulary", scheme);
                return "message";
            }
        }

        request.getSession().setAttribute("ranking", Boolean.toString(ranking));
        String source = HTTPUtils.cleanXSS((String) request.getParameter("source"));
        if (source == null) {
            source = "ALL";
        }

        if (NCItBrowserProperties.get_debugOn()) {
            try {
                _logger.debug(Utils.SEPARATOR);
                _logger.debug("* criteria: " + matchText);
                // _logger.debug("* matchType: " + matchtype);
                _logger.debug("* source: " + source);
                _logger.debug("* ranking: " + ranking);
                // _logger.debug("* sortOption: " + sortOption);
            } catch (Exception e) {
            }
        }

        Vector schemes = new Vector();
        schemes.add(scheme);

        //String version = null;
        Vector versions = new Vector();
        versions.add(version);


        //Utils.StopWatch stopWatch = new Utils.StopWatch();
        Vector<org.LexGrid.concepts.Entity> v = null;

        boolean excludeDesignation = true;
        boolean designationOnly = false;

        // check if this search has been performance previously through
        // IteratorBeanManager
        IteratorBeanManager iteratorBeanManager =
            (IteratorBeanManager) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap()
                .get("iteratorBeanManager");

        if (iteratorBeanManager == null) {
            iteratorBeanManager = new IteratorBeanManager();
            FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap()
                .put("iteratorBeanManager", iteratorBeanManager);
        }

        IteratorBean iteratorBean = null;
        String key =
            iteratorBeanManager.createIteratorKey(schemes, versions, matchText,
                searchTarget, matchAlgorithm, maxToReturn);

        if (searchTarget.compareTo("names") == 0) {
            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
                ResolvedConceptReferencesIteratorWrapper wrapper = null;

                try {
					// temporary fix for: [NCITERM-682] Contains search failed on search strings containing a colon character.
					String matchTextStr = matchText;
					if (matchAlgorithm.compareTo("contains") == 0) {
						matchTextStr = matchTextStr.replaceAll(":", " ");
					}
					if (SimpleSearchUtils.isSimpleSearchSupported(matchAlgorithm, SimpleSearchUtils.NAMES)) {
						iterator = new SimpleSearchUtils(lbSvc).search(schemes, versions, matchTextStr, SimpleSearchUtils.BY_NAME, matchAlgorithm);

						wrapper = null;
						if (iterator != null) {
							wrapper = new ResolvedConceptReferencesIteratorWrapper(iterator);
						}
				    } else {

						wrapper = new SearchUtils(lbSvc)
							.searchByNameOrCode(schemes, versions, matchTextStr, source,
								matchAlgorithm, ranking, maxToReturn, SearchUtils.SEARCH_BY_NAME_ONLY);
					}
				} catch (Exception ex) {
                    ex.printStackTrace();
				}

                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                    if (iterator != null) {
                        iteratorBean = new IteratorBean(iterator);
                        iteratorBean.setKey(key);
                        iteratorBeanManager.addIteratorBean(iteratorBean);
                    }
                } else {
					System.out.println("(*) search by names returns wrapper = NULL???");
				}
            }

        } else if (searchTarget.compareTo("codes") == 0) {
            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
                ResolvedConceptReferencesIteratorWrapper wrapper = null;
                try {
					/*
                    wrapper = new SearchUtils()
                        //.searchByName(schemes, versions, matchText, source,
                        .searchByNameOrCode(schemes, versions, matchText, source,
                            matchAlgorithm, ranking, maxToReturn, SearchUtils.SEARCH_BY_CODE_ONLY);
                    */

					wrapper = new CodeSearchUtils(lbSvc).searchByCode(
						schemes, versions, matchText,
						source, matchAlgorithm, ranking, maxToReturn, false);

				} catch (Exception ex) {
                    //ex.printStackTrace();
				}

                if (wrapper != null) {
                    iterator = wrapper.getIterator();

                    if (iterator != null) {
                        iteratorBean = new IteratorBean(iterator);
                        iteratorBean.setKey(key);
                        iteratorBeanManager.addIteratorBean(iteratorBean);
                    }
                }
            }

        } else if (searchTarget.compareTo("properties") == 0) {
            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
                ResolvedConceptReferencesIteratorWrapper wrapper = null;
                try {
                    wrapper =
                    new SearchUtils(lbSvc).searchByProperties(schemes, versions,
                        matchText, source, matchAlgorithm, excludeDesignation,
                        ranking, maxToReturn);
				} catch (Exception ex) {
                    //ex.printStackTrace();
				}

                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                    if (iterator != null) {
                        iteratorBean = new IteratorBean(iterator);
                        iteratorBean.setKey(key);
                        iteratorBeanManager.addIteratorBean(iteratorBean);
                    }
                }
            }

        } else if (searchTarget.compareTo("relationships") == 0) {
            designationOnly = true;
            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
				ResolvedConceptReferencesIteratorWrapper wrapper = null;
                try {
                    wrapper =
                    new SearchUtils(lbSvc).searchByAssociations(schemes, versions,
                        matchText, source, matchAlgorithm, designationOnly,
                        ranking, maxToReturn);

				} catch (Exception ex) {
                    //ex.printStackTrace();
				}


                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                    if (iterator != null) {
						try {
							int numberOfMatches = iterator.numberRemaining();
						} catch (Exception ex) {
                            //ex.printStackTrace();
						}
                        iteratorBean = new IteratorBean(iterator);
                        iteratorBean.setKey(key);
                        iteratorBeanManager.addIteratorBean(iteratorBean);
                    }
                }
            }
        }

        request.getSession().setAttribute("vocabulary", scheme);
        request.getSession().removeAttribute("neighborhood_synonyms");
        request.getSession().removeAttribute("neighborhood_atoms");
        request.getSession().removeAttribute("concept");
        request.getSession().removeAttribute("code");
        request.getSession().removeAttribute("codeInNCI");
        request.getSession().removeAttribute("AssociationTargetHashMap");
        request.getSession().removeAttribute("type");

		request.getSession().setAttribute("key", key);
		_logger.debug("searchAction Iterator key: " + key);

        if (iterator != null) {
            int size = iteratorBean.getSize();
            List list = null;
            // LexEVS API itersator.numberRemaining is inaccurate, and can cause issues.
            // the following code is a work around.
            if (size == 1) {
            	list = iteratorBean.getData(0, 0);
            	if (size != iteratorBean.getSize()) {
					size = iteratorBean.getSize();
				}
			}

            if (size > 1) {


                request.getSession().setAttribute("search_results", v);
                String match_size = Integer.toString(size);
                request.getSession().setAttribute("match_size", match_size);
                request.getSession().setAttribute("page_string", "1");

                request.getSession().setAttribute("new_search", Boolean.TRUE);

                request.getSession().setAttribute("dictionary", scheme);
                _logger
                    .debug("UserSessionBean request.getSession().setAttribute dictionary: "
                        + scheme);

				//KLO, 051012
				request.getSession().removeAttribute("n");
				request.getSession().removeAttribute("b");
				request.getSession().removeAttribute("m");
                return "search_results";
            } else if (size == 1) {

                request.getSession().setAttribute("singleton", "true");
                request.getSession().setAttribute("dictionary", scheme);// Constants.CODING_SCHEME_NAME);
                int pageNumber = 1;

                //List list = iteratorBean.getData(1);

                //List list = iteratorBean.getData(0, 0);

                if (list != null && list.size() > 0) {

					ResolvedConceptReference ref =
						(ResolvedConceptReference) list.get(0);

					Entity c = null;
					if (ref == null) {
						String msg =
							"Error: Null ResolvedConceptReference encountered.";
						request.getSession().setAttribute("message", msg);

						request.getSession().setAttribute("dictionary", scheme);
						return "message";

					} else {
						if (ref.getConceptCode() == null) {
							String message =
								"Code has not been assigned to the concept matches with '"
									+ matchText + "'";
							_logger.warn("WARNING: " + message);
							request.getSession().setAttribute("message", message);
							request.getSession().setAttribute("dictionary", scheme);
							return "message";
						} else {
							request.getSession().setAttribute("code",
								ref.getConceptCode());
						}

						c = ref.getReferencedEntry();

						if (c == null) {

							c =
								DataUtils.getConceptByCode(scheme, null, null, ref
									.getConceptCode());
							if (c == null) {
								String message =
									"Unable to find the concept with a code '"
										+ ref.getConceptCode() + "'";
								_logger.warn("WARNING: " + message);
								request.getSession().setAttribute("message",
									message);
								request.getSession().setAttribute("dictionary",
									scheme);
								return "message";
							}

						} else {
							request.getSession().setAttribute("code",
								c.getEntityCode());
						}
					}

					request.getSession().setAttribute("concept", c);
					request.getSession().setAttribute("type", "properties");
					request.getSession().setAttribute("new_search", Boolean.TRUE);

					//KLO, 051012
					request.getSession().removeAttribute("n");
					request.getSession().removeAttribute("b");
					request.getSession().removeAttribute("m");
					response.setContentType("text/html;charset=utf-8");

					return "concept_details";
			    }
            }
        }

        String message = "No match found.";
        //int minimumSearchStringLength =
        //    NCItBrowserProperties.getMinimumSearchStringLength();

		if (matchAlgorithm.compareTo(Constants.EXACT_SEARCH_ALGORITHM) == 0) {
			String t = searchTarget.toLowerCase();
			if (t.indexOf("code") != -1) {
				message = Constants.ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE;
			} else {
				message = Constants.ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS;
			}
		}

        //else if (matchAlgorithm.compareTo(Constants.STARTWITH_SEARCH_ALGORITHM) == 0
        //    && matchText.length() < minimumSearchStringLength) {
        //    message = Constants.ERROR_ENCOUNTERED_TRY_NARROW_QUERY;
        //}

        request.getSession().setAttribute("message", message);
        request.getSession().setAttribute("dictionary", scheme);
        return "message";
    }

    private String _selectedResultsPerPage = null;
    private List _resultsPerPageList = null;


    public static List getResultsPerPageValues() {
        List resultsPerPageList = new ArrayList();
        resultsPerPageList.add("10");
        resultsPerPageList.add("25");
        resultsPerPageList.add("50");
        resultsPerPageList.add("75");
        resultsPerPageList.add("100");
        resultsPerPageList.add("250");
        resultsPerPageList.add("500");
        return resultsPerPageList;
    }


    public List getResultsPerPageList() {
        _resultsPerPageList = new ArrayList();
        _resultsPerPageList.add(new SelectItem("10"));
        _resultsPerPageList.add(new SelectItem("25"));
        _resultsPerPageList.add(new SelectItem("50"));
        _resultsPerPageList.add(new SelectItem("75"));
        _resultsPerPageList.add(new SelectItem("100"));
        _resultsPerPageList.add(new SelectItem("250"));
        _resultsPerPageList.add(new SelectItem("500"));

        _selectedResultsPerPage =
            ((SelectItem) _resultsPerPageList.get(2)).getLabel(); // default to
                                                                 // 50
        return _resultsPerPageList;
    }

    public void setSelectedResultsPerPage(String selectedResultsPerPage) {
        if (selectedResultsPerPage == null) {
            return;
		}
        _selectedResultsPerPage = selectedResultsPerPage;
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        request.getSession().setAttribute("selectedResultsPerPage",
            selectedResultsPerPage);

    }

    public String getSelectedResultsPerPage() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        String s =
            (String) request.getSession()
                .getAttribute("selectedResultsPerPage");
        if (s != null) {
            _selectedResultsPerPage = s;
        } else {
            _selectedResultsPerPage = "50";
            request.getSession().setAttribute("selectedResultsPerPage", "50");
        }
        return _selectedResultsPerPage;
    }

    public void resultsPerPageChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null) {
            return;
        }
        String newValue = (String) event.getNewValue();

        setSelectedResultsPerPage(newValue);
    }

    public String linkAction() {
		/*
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        */
        return "";
    }

    private String _selectedAlgorithm = null;
    private List _algorithmList = null;

    public List getAlgorithmList() {
        _algorithmList = new ArrayList();
        _algorithmList.add(new SelectItem("exactMatch", "exactMatch"));
        _algorithmList.add(new SelectItem("startsWith", "Begins With"));
        _algorithmList.add(new SelectItem("contains", "Contains"));
        _selectedAlgorithm = ((SelectItem) _algorithmList.get(0)).getLabel();
        return _algorithmList;
    }

    public void algorithmChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();
        setSelectedAlgorithm(newValue);
    }

    public void setSelectedAlgorithm(String selectedAlgorithm) {
        _selectedAlgorithm = selectedAlgorithm;
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        request.getSession().setAttribute("algorithm", selectedAlgorithm);
    }

    public String getSelectedAlgorithm() {
        return _selectedAlgorithm;
    }

    // //////////////////////////////////////////////////////////////////////////////////////////
    // ontologies

    public List getOntologiesToSearchOn() {
        if (_ontologyList == null) {
            _ontologyList = DataUtils.getOntologyList();
            SelectItem item = (SelectItem) _ontologyList.get(0);
            _ontologiesToSearchOn.add(item.getLabel());

        } else if (_ontologiesToSearchOn.size() == 0) {
            SelectItem item = (SelectItem) _ontologyList.get(0);
            _ontologiesToSearchOn.add(item.getLabel());
        }
        return _ontologiesToSearchOn;
    }

    public List getOntologyList() {
        if (_ontologyList == null) {
            _ontologyList = DataUtils.getOntologyList();
        }
        return _ontologyList;
    }

    public void setOntologiesToSearchOn(List<String> newValue) {
        _ontologiesToSearchOn = new ArrayList<String>();
        for (int i = 0; i < newValue.size(); i++) {
            Object obj = newValue.get(i);
            _ontologiesToSearchOn.add((String) obj);
        }
    }

    public void ontologiesToSearchOnChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null) {
            return;
        }

        List newValue = (List) event.getNewValue();
        setOntologiesToSearchOn(newValue);
    }

    public List<SelectItem> _ontologySelectionList = null;
    public String _ontologyToSearchOn = null;

    public List getOntologySelectionList() {
        if (_ontologySelectionList != null)
            return _ontologySelectionList;
        List ontologies = getOntologyList();
        _ontologySelectionList = new ArrayList<SelectItem>();
        String label = "Switch to another vocabulary (select one)";
        _ontologySelectionList.add(new SelectItem(label, label));
        for (int i = 0; i < ontologies.size(); i++) {
            SelectItem item = (SelectItem) _ontologyList.get(i);
            _ontologySelectionList.add(item);
        }
        return _ontologySelectionList;
    }

    public void ontologySelectionChanged(ValueChangeEvent event) {

        if (event.getNewValue() == null) {
            // _logger.warn("ontologySelectionChanged; event.getNewValue() == null ");
            return;
        }
        //String newValue = (String) event.getNewValue();

        HttpServletResponse response =
            (HttpServletResponse) FacesContext.getCurrentInstance()
                .getExternalContext().getResponse();

        String targetURL = null;// "http://nciterms.nci.nih.gov/";
        targetURL = "http://nciterms.nci.nih.gov/";
        try {
            response.sendRedirect(response.encodeRedirectURL(targetURL));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getOntologyToSearchOn() {
        if (_ontologySelectionList == null) {
            _ontologySelectionList = getOntologySelectionList();
            SelectItem item = (SelectItem) _ontologyList.get(1);
            _ontologyToSearchOn = item.getLabel();
        }
        return _ontologyToSearchOn;
    }

    public void setOntologyToSearchOn(String newValue) {
        _ontologyToSearchOn = newValue;
    }

    // //////////////////////////////////////////////////////////////////////////////////////

    private String[] getSelectedVocabularies(String ontology_list_str) {
        Vector v = DataUtils.parseData(ontology_list_str);
        String[] ontology_list = new String[v.size()];
        for (int i = 0; i < v.size(); i++) {
            String s = (String) v.elementAt(i);
            ontology_list[i] = s;
        }
        return ontology_list;
    }

    public String acceptLicenseAction() {

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        String dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
        String code = HTTPUtils.cleanXSS((String) request.getParameter("code"));

        if (dictionary != null && code != null) {
            LicenseBean licenseBean =
                (LicenseBean) request.getSession().getAttribute("licenseBean");
            if (licenseBean == null) {
                licenseBean = new LicenseBean();
            }
            licenseBean.addLicenseAgreement(dictionary);
            request.getSession().setAttribute("licenseBean", licenseBean);

            Entity c =
                DataUtils.getConceptByCode(dictionary, null, null, code);
            request.getSession().setAttribute("code", code);
            request.getSession().setAttribute("concept", c);
            request.getSession().setAttribute("type", "properties");


			HttpServletResponse response =
				(HttpServletResponse) FacesContext.getCurrentInstance()
					.getExternalContext().getResponse();

			response.setContentType("text/html;charset=utf-8");


            return "concept_details";
        } else {
            String message = "Unidentifiable vocabulary name, or code";
            request.getSession().setAttribute("warning", message);
            return "message";
        }
    }




    public String multipleSearchAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("error_msg");

/*
boolean retval = HTTPUtils.validateRequestParameters(request);
if (!retval) {
	System.out.println("multipleSearchAction returns invalid_parameter");
	return "invalid_parameter";
}
*/
		String selected_vocabularies = HTTPUtils.cleanXSS((String) request.getParameter("selected_vocabularies"));

        String[] ontology_list = request.getParameterValues("ontology_list");

        if ( ontology_list  == null ) {
			 //ontology_list = (String[]) request.getSession().getAttribute("ontology_list");
		}

        // Called from license.jsp
        String acceptedLicensesStr = HTTPUtils.cleanXSS((String) request.getParameter("acceptedLicenses"));
        if (acceptedLicensesStr != null) {
            LexEVSUtils.CSchemes acceptedLicenses =
                LexEVSUtils.CSchemes.toSchemes(acceptedLicensesStr);
            if (acceptedLicenses != null) {
                LicenseUtils.acceptLicenses(request, acceptedLicenses);
			}
        }

        String matchText = HTTPUtils.cleanMatchTextXSS((String) request.getParameter("matchText"));
        if (matchText != null) {
            matchText = matchText.trim();
           request.getSession().setAttribute("matchText", matchText);
		}

		/*
        String matchText = HTTPUtils.cleanXSS((String) request.getParameter("matchText"));
        if (matchText != null) {
            matchText = matchText.trim();
            request.getSession().setAttribute("matchText", matchText);
        } else {
            matchText = (String) request.getSession().getAttribute("matchText");
        }
        */


        String multiple_search_error =
            (String) request.getSession().getAttribute(
                "multiple_search_no_match_error");
        request.getSession().removeAttribute("multiple_search_no_match_error");

        String matchAlgorithm = HTTPUtils.cleanXSS((String) request.getParameter("algorithm"));


        //KLO 051512 AppScan
        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
			matchAlgorithm = "exactMatch";
		}
        request.getSession().setAttribute("algorithm", matchAlgorithm);

        String searchTarget = HTTPUtils.cleanXSS((String) request.getParameter("searchTarget"));
        //KLO 051512 AppScan
        if (searchTarget == null || searchTarget.length() == 0) {
			searchTarget = "names";
		}

        request.getSession().setAttribute("searchTarget", searchTarget);

        String initial_search = HTTPUtils.cleanXSS((String) request.getParameter("initial_search"));

        //String[] ontology_list = request.getParameterValues("ontology_list");

Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");

StringBuffer buf = new StringBuffer();
String ontologiesToSearchOnStr = null;
HashSet hset = new HashSet();
if (selected_vocabularies != null) { // hidden variable (subsequent search from search results page)
	ontologiesToSearchOnStr = selected_vocabularies;
} else {



	if (ontology_list != null) {
		List list = Arrays.asList(ontology_list);//.contains("any");
	    for (int i=0; i<display_name_vec.size(); i++) {
		    OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
		    if (info.getVisible() && !list.contains(info.getLabel())) {
				info.setSelected(false);
			} else if (info.getVisible() && list.contains(info.getLabel())) {
				info.setSelected(true);
			}
		}
		for (int i = 0; i < ontology_list.length; ++i) {
			if (!hset.contains(ontology_list[i])) {
				hset.add(ontology_list[i]);
			    buf.append(ontology_list[i] + "|");
			}
		}
	}

	buf.append("|");
	for (int i=0; i<display_name_vec.size(); i++) {
		 OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
		 if (info.getSelected()) {
			 if (!hset.contains(info.getLabel())) {
				 buf.append(info.getLabel() + "|");
				 hset.add(info.getLabel());
			 }
		 }
	}


	ontologiesToSearchOnStr = buf.toString();
}


/*
String ontologiesToSearchOnStr = selected_vocabularies;
if (DataUtils.isNull(selected_vocabularies)) {
	// check if selection status has been changed.

	buf.append("|");
	//String ontologiesToSearchOnStr = "|";
	if (ontology_list != null) {
		for (int i = 0; i < ontology_list.length; ++i) {
			//ontologiesToSearchOnStr =
			//ontologiesToSearchOnStr + ontology_list[i] + "|";
			buf.append(ontology_list[i] + "|");
		}
	}
	ontologiesToSearchOnStr = buf.toString();
}
*/


/*
String new_ontologiesToSearchOnStr = "";
for (int i = 0; i < display_name_vec.size(); i++) {
	 OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);

	 if (ontologiesToSearchOnStr.indexOf(info.getLabel()) != -1) { // visible and checked by the user
		 info.setSelected(true);
		 //KLO
	 } else {//if (info.getVisible() && ontologiesToSearchOnStr.indexOf(info.getLabel()) == -1) {
		 info.setSelected(false);
	 }
	 if (info.getSelected()) {
		 new_ontologiesToSearchOnStr = new_ontologiesToSearchOnStr + "|" + info.getLabel();
	 }
}
ontologiesToSearchOnStr = new_ontologiesToSearchOnStr;
*/
String ontologiesToExpandStr = getOntologiesToExpandStr(display_name_vec);

request.getSession().setAttribute("display_name_vec", display_name_vec);
request.getSession().setAttribute("ontologiesToSearchOnStr", ontologiesToSearchOnStr);
request.getSession().setAttribute("ontologiesToExpandStr", ontologiesToExpandStr);


        List list = new ArrayList<String>();

        //String ontologiesToSearchOnStr = null;
        String ontology_list_str = null;
        List<String> ontologiesToSearchOn = null;
        int knt = 0;

        if (ontology_list != null) {
			List checked_list = Arrays.asList(ontology_list);
			for (int i = 0; i < display_name_vec.size(); i++) {
				 OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
				 if (checked_list.contains(info.getLabel())) {
					 info.setSelected(true);
				 }
			}
		}


		int selected_knt = 0;
		Vector ontologies_to_search_on = new Vector();
		StringBuffer buff = new StringBuffer();
		buff.append("|");
		for (int i = 0; i < display_name_vec.size(); i++) {
			 OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
			 if (info.getSelected()) {
				 selected_knt++;
				 ontologies_to_search_on.add(info.getLabel());
				 buff.append(info.getLabel() + "|");
			 }
		}
		ontologiesToSearchOnStr = buff.toString();
		ontology_list = new String[ontologies_to_search_on.size()];
		ontologiesToSearchOn = new ArrayList<String>();

		for (int k = 0; k < ontologies_to_search_on.size(); k++) {
			String s =
				(String) ontologies_to_search_on.elementAt(k);
			ontology_list[k] = s;
			ontologiesToSearchOn.add(s);
		}



        if (initial_search != null) { // from home page
            if (ontology_list == null || ontology_list.length == 0) {
                String message = Constants.ERROR_NO_VOCABULARY_SELECTED;// "Please select at least one vocabulary.";
                request.getSession().setAttribute("warning", message);
                request.getSession().setAttribute("message", message);
                request.getSession().removeAttribute("ontologiesToSearchOn");
                request.getSession().setAttribute("defaultOntologiesToSearchOnStr", "|");
                return "multiple_search";
            } else {

                if (ontology_list_str == null) {
                    //ontology_list_str = "";
                    StringBuffer sb = new StringBuffer();

                    for (int i = 0; i < ontology_list.length; ++i) {
                        ontology_list_str = ontology_list_str + ontology_list[i];
                        sb.append(ontology_list[i]);
                        if (i < ontology_list.length - 1) {
                            //ontology_list_str = ontology_list_str + "|";
                            sb.append("|");
                        }
                    }
                    ontology_list_str = sb.toString();
                }
                request.getSession().setAttribute("ontologiesToSearchOn",
                    ontologiesToSearchOnStr);
            }
        }


        String hide_ontology_list = "false";
        // [#19965] Error message is not displayed when Search Criteria is not
        // proivided
        if (matchText == null || matchText.length() == 0) {
            String message = Constants.ERROR_NO_SEARCH_STRING_ENTERED;

            if (initial_search == null) {
                hide_ontology_list = "true";
            }
            request.getSession().setAttribute("hide_ontology_list",
                hide_ontology_list);
            request.getSession().setAttribute("warning", message);
            request.getSession().setAttribute("message", message);
            request.getSession().setAttribute("matchText",
                HTTPUtils.convertJSPString(matchText));
            return "multiple_search";
        }



        // KLO, 012610
        else if (searchTarget.compareTo("relationships") == 0
            && matchAlgorithm.compareTo("contains") == 0) {
            String text = matchText.trim();
            if (text.length() < NCItBrowserProperties
                .getMinimumSearchStringLength()) {
                String msg = Constants.ERROR_REQUIRE_MORE_SPECIFIC_QUERY_STRING;
                request.getSession().setAttribute("warning", msg);
                request.getSession().setAttribute("message", msg);
                return "multiple_search";
            }
        }

        boolean ranking = true;
        String source = HTTPUtils.cleanXSS((String) request.getParameter("source"));
        if (source == null) {
            source = "ALL";
        }

        if (NCItBrowserProperties.get_debugOn()) {
            try {
                _logger.debug(Utils.SEPARATOR);
                _logger.debug("* criteria: " + matchText);
                _logger.debug("* source: " + source);
                _logger.debug("* ranking: " + ranking);
                _logger.debug("* ontology_list: ");
                for (int i = 0; i < ontology_list.length; ++i) {
                    _logger.debug("  " + i + ") " + ontology_list[i]);
                }
            } catch (Exception e) {
            }
        }

        if (ontology_list == null) {
            ontology_list_str =
                HTTPUtils.cleanXSS((String) request.getParameter("ontology_list_str")); // from
                                                                    // multiple_search_results
                                                                    // (hidden
                                                                    // variable)
            if (ontology_list_str != null) {
                ontology_list = getSelectedVocabularies(ontology_list_str);
            }

        } else {
            knt = ontology_list.length;
            if (knt == 0) {
                String message = Constants.ERROR_NO_VOCABULARY_SELECTED;// "Please select at least one vocabulary.";
                request.getSession().setAttribute("warning", message);
                request.getSession().setAttribute("message", message);
                request.getSession().setAttribute("hide_ontology_list", "true");
                request.getSession().removeAttribute("ontologiesToSearchOn");

                // String defaultOntologiesToSearchOnStr =
                // ontologiesToSearchOnStr;
                request.getSession().setAttribute(
                    "defaultOntologiesToSearchOnStr", "|");
                return "multiple_search";
            }
        }

        ontologiesToSearchOn = new ArrayList<String>();
        buf = new StringBuffer();
        //ontologiesToSearchOnStr = "|";
        for (int i = 0; i < ontology_list.length; ++i) {
            list.add(ontology_list[i]);
            ontologiesToSearchOn.add(ontology_list[i]);
            //ontologiesToSearchOnStr = ontologiesToSearchOnStr + ontology_list[i] + "|";
            buf.append(ontology_list[i] + "|");
        }
        ontologiesToSearchOnStr = buf.toString();

        if (ontology_list_str == null) {
            //ontology_list_str = "";
            buf = new StringBuffer();
            for (int i = 0; i < ontology_list.length; ++i) {
                //ontology_list_str = ontology_list_str + ontology_list[i];
                buf.append(ontology_list[i]);
                if (i < ontology_list.length - 1) {
                    //ontology_list_str = ontology_list_str + "|";
                    buf.append("|");
                }
            }
            ontology_list_str = buf.toString();
        }

        if (ontologiesToSearchOn.size() == 0) {
            String message = Constants.ERROR_NO_VOCABULARY_SELECTED;// "Please select at least one vocabulary.";
            request.getSession().setAttribute("warning", message);
            request.getSession().setAttribute("message", message);
            request.getSession().removeAttribute("ontologiesToSearchOn");
            return "multiple_search";
        }

        request.getSession().setAttribute("ontologiesToSearchOn",
            ontologiesToSearchOnStr);

        // [#25270] Set "all but NCIm selected" as the default for the TB
        // home page.
        String defaultOntologiesToSearchOnStr = ontologiesToSearchOnStr;
        request.getSession().setAttribute("defaultOntologiesToSearchOnStr",
            defaultOntologiesToSearchOnStr);

        LexEVSUtils.CSchemes unacceptedLicensesCS =
            LicenseUtils.getUnacceptedLicenses(request, ontologiesToSearchOn);
        if (unacceptedLicensesCS.size() > 0) {
            //request.getSession().setAttribute("matchText", matchText);
            request.setAttribute("searchTarget", searchTarget);
            request.setAttribute("algorithm", matchAlgorithm);
            request.setAttribute("ontology_list_str", ontology_list_str);
            request.setAttribute("scheme", null);
            request.setAttribute("version", null);
            request.setAttribute("unacceptedLicensesCS", unacceptedLicensesCS);


            request.getSession().setAttribute("ontology_list", ontology_list);


            return "license";
        }

        LexEVSUtils.CSchemes cSchemes =
            LexEVSUtils.getCSchemes(request, ontologiesToSearchOn);
        Vector<String> schemes = cSchemes.getCodingSchemes();
        Vector<String> versions = cSchemes.getVersions();

        String max_str = null;
        int maxToReturn = -1;// 1000;
        try {
            max_str =
                NCItBrowserProperties
                    .getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {
            // Do nothing
        }
        boolean designationOnly = false;
        boolean excludeDesignation = true;
        ResolvedConceptReferencesIterator iterator = null;


		for (int lcv=0; lcv<schemes.size(); lcv++) {
			String check_formal_name = (String) schemes.elementAt(lcv);
			check_formal_name = DataUtils.getFormalName(check_formal_name);
			if (check_formal_name == null) {
				String message = "Unable to identify vocabulary: " + check_formal_name;
				request.getSession().setAttribute("message", message);
				return "multiple_search";
			}
		}


boolean retval = HTTPUtils.validateRequestParameters(request);
if (!retval) {
	System.out.println("multipleSearchAction returns invalid_parameter");
	return "invalid_parameter";
}


        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        if (searchTarget.compareTo("names") == 0) {
			// temporary fix for: [NCITERM-682] Contains search failed on search strings containing a colon character.
            long ms = System.currentTimeMillis();
            long delay = 0;
            _logger.debug("Calling SearchUtils().searchByNameAndCode " + matchText);
            ResolvedConceptReferencesIteratorWrapper wrapper = null;
            //062013 KLO
            if (SimpleSearchUtils.isSimpleSearchSupported(matchAlgorithm, SimpleSearchUtils.NAMES)) {
				try {
					String matchTextStr = matchText;
					if (matchAlgorithm.compareTo("contains") == 0) {
						matchTextStr = matchText.replaceAll(":", " ");
					}


					iterator = new SimpleSearchUtils(lbSvc).search(schemes, versions, matchTextStr, SimpleSearchUtils.BY_NAME, matchAlgorithm);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else {
				String matchTextStr = matchText;
				if (matchAlgorithm.compareTo("contains") == 0) {
					matchTextStr = matchText.replaceAll(":", " ");
				}

				wrapper = new SearchUtils(lbSvc).searchByNameOrCode(
						schemes, versions, matchTextStr,
						source, matchAlgorithm, ranking, maxToReturn, SearchUtils.SEARCH_BY_NAME_ONLY);

				if (wrapper != null) {
					iterator = wrapper.getIterator();
				}

			}

            delay = System.currentTimeMillis() - ms;
            _logger.debug("searchByNameAndCode delay (millisec.): " + delay);



        } else if (searchTarget.compareTo("codes") == 0) {
            long ms = System.currentTimeMillis();
            long delay = 0;
            _logger.debug("Calling CodeSearchUtils().searchByCode " + matchText);

            ResolvedConceptReferencesIteratorWrapper wrapper =
                new CodeSearchUtils(lbSvc).searchByCode(schemes, versions, matchText,
                    source, matchAlgorithm, ranking, maxToReturn);

            if (wrapper != null) {
                iterator = wrapper.getIterator();
            }
            delay = System.currentTimeMillis() - ms;
            _logger.debug("searchByCode delay (millisec.): " + delay);

        } else if (searchTarget.compareTo("properties") == 0) {
			String matchTextStr = matchText.replaceAll(":", " ");
            ResolvedConceptReferencesIteratorWrapper wrapper =
                new SearchUtils(lbSvc).searchByProperties(schemes, versions,
                    matchTextStr, source, matchAlgorithm, excludeDesignation,
                    ranking, maxToReturn);
            if (wrapper != null) {
                iterator = wrapper.getIterator();
            }
        } else if (searchTarget.compareTo("relationships") == 0) {
            designationOnly = true;

            ResolvedConceptReferencesIteratorWrapper wrapper =
                new SearchUtils(lbSvc).searchByAssociations(schemes, versions,
                    matchText, source, matchAlgorithm, designationOnly,
                    ranking, maxToReturn);
            if (wrapper != null) {
                iterator = wrapper.getIterator();
           }
        }


        request.getSession().setAttribute("searchTarget", searchTarget);
        request.getSession().setAttribute("algorithm", matchAlgorithm);

        request.getSession().removeAttribute("neighborhood_synonyms");
        request.getSession().removeAttribute("neighborhood_atoms");
        request.getSession().removeAttribute("concept");
        request.getSession().removeAttribute("code");
        request.getSession().removeAttribute("codeInNCI");
        request.getSession().removeAttribute("AssociationTargetHashMap");
        request.getSession().removeAttribute("type");


        IteratorBeanManager iteratorBeanManager =
            (IteratorBeanManager) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap()
                .get("iteratorBeanManager");

        if (iteratorBeanManager == null) {
            iteratorBeanManager = new IteratorBeanManager();
            FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap()
                .put("iteratorBeanManager", iteratorBeanManager);
        }

        if (iterator != null) {

            int size = 0;

            IteratorBean iteratorBean =
                (IteratorBean) FacesContext.getCurrentInstance()
                    .getExternalContext().getSessionMap().get("iteratorBean");

            iteratorBean = new IteratorBean(iterator);


            String itr_key = IteratorBeanManager.createIteratorKey(ontologiesToSearchOnStr, matchText, searchTarget, matchAlgorithm);
            iteratorBean.setKey(itr_key);


            FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().put("iteratorBean", iteratorBean);


			try {
				size = iterator.numberRemaining();

			} catch (Exception ex) {
				ex.printStackTrace();
			}

            if (size == 1) {
            	list = iteratorBean.getData(0, 0);
            	if (list == null || list.size() == 0) {
						String msg =
							"No match found.";
						request.getSession().setAttribute("message", msg);


						//request.getSession().setAttribute("matchText",
						//	HTTPUtils.convertJSPString(matchText));


						hide_ontology_list = "false";

						request.getSession().setAttribute("hide_ontology_list",
							hide_ontology_list);
						request.getSession().setAttribute("warning", msg);
						request.getSession().setAttribute("message", msg);
						request.getSession().setAttribute("ontologiesToSearchOn",
							ontologiesToSearchOnStr);
						request.getSession().setAttribute("multiple_search_no_match_error",
							"true");

						return "multiple_search";

						//return "message";
				}
            	if (size != iteratorBean.getSize()) {
					size = iteratorBean.getSize();
				}
			}

            if (size == 1) {
                int pageNumber = 1;
                list = iteratorBean.getData(1);

                if (list != null) {

					ResolvedConceptReference ref =
						(ResolvedConceptReference) list.get(0);

					//ResolvedConceptReference ref = getFirstResolvedConceptReference(iterator);
					String coding_scheme = ref.getCodingSchemeName();
					String ref_version = ref.getCodingSchemeVersion();

					if (coding_scheme.compareToIgnoreCase("NCI Metathesaurus") == 0) {
						String match_size = Integer.toString(size);
						;// Integer.toString(v.size());
						request.getSession().setAttribute("match_size", match_size);
						request.getSession().setAttribute("page_string", "1");
						request.getSession().setAttribute("new_search",
							Boolean.TRUE);
						// route to multiple_search_results.jsp



        //KLO, 051012
        request.getSession().removeAttribute("n");
        request.getSession().removeAttribute("b");
        request.getSession().removeAttribute("m");


						return "search_results";
					}

					request.getSession().setAttribute("singleton", "true");
					request.getSession().setAttribute("dictionary", coding_scheme);
					request.getSession().setAttribute("version", ref_version);
					Entity c = null;
					if (ref == null) {
						String msg =
							"Error: Null ResolvedConceptReference encountered.";
						request.getSession().setAttribute("message", msg);
						return "message";

					} else {
						c = ref.getReferencedEntry();
						if (c == null) {
							//c = DataUtils.getConceptByCode(coding_scheme, ref_version,
							c = new SearchUtils(lbSvc).getConceptByCode(coding_scheme, ref_version,
									null, ref.getConceptCode());
						}
					}

					request.getSession().setAttribute("code", ref.getConceptCode());
					request.getSession().setAttribute("concept", c);
					request.getSession().setAttribute("type", "properties");
					request.getSession().setAttribute("new_search", Boolean.TRUE);

					request.setAttribute("algorithm", matchAlgorithm);
					coding_scheme =
						(String) DataUtils.get_localName2FormalNameHashMap()
							.get(coding_scheme);

					String convertJSPString = HTTPUtils.convertJSPString(matchText);
					request.setAttribute("dictionary", coding_scheme);
					request.setAttribute("version", ref_version);


        //KLO, 051012
        request.getSession().removeAttribute("n");
        request.getSession().removeAttribute("b");
        request.getSession().removeAttribute("m");

HttpServletResponse response =
	(HttpServletResponse) FacesContext.getCurrentInstance()
		.getExternalContext().getResponse();

response.setContentType("text/html;charset=utf-8");

					return "concept_details";


				} else if (list != null && list.size() > 0) {
					String match_size = Integer.toString(size);
					;// Integer.toString(v.size());
					request.getSession().setAttribute("match_size", match_size);
					request.getSession().setAttribute("page_string", "1");
					request.getSession().setAttribute("new_search", Boolean.TRUE);
					_logger.debug("Start to render search_results ... ");


        //KLO, 051012
        request.getSession().removeAttribute("n");
        request.getSession().removeAttribute("b");
        request.getSession().removeAttribute("m");


					return "search_results";
				}
		    } else if (size > 1) {
				String match_size = Integer.toString(size);
				;// Integer.toString(v.size());
				request.getSession().setAttribute("match_size", match_size);
				request.getSession().setAttribute("page_string", "1");
				request.getSession().setAttribute("new_search", Boolean.TRUE);
				// route to multiple_search_results.jsp
				_logger.debug("Start to render search_results ... ");


        //KLO, 051012
        request.getSession().removeAttribute("n");
        request.getSession().removeAttribute("b");
        request.getSession().removeAttribute("m");
				return "search_results";
			}


        }

        // int minimumSearchStringLength =
        //    NCItBrowserProperties.getMinimumSearchStringLength();
        if (ontologiesToSearchOn.size() == 0) {
            request.getSession().removeAttribute("vocabulary");
        } else if (ontologiesToSearchOn.size() == 1) {
            String msg_scheme = (String) ontologiesToSearchOn.get(0);
            request.getSession().setAttribute("vocabulary", msg_scheme);
        } else {
            request.getSession().removeAttribute("vocabulary");
        }

        String message = Constants.ERROR_NO_MATCH_FOUND;
		if (matchAlgorithm.compareTo(Constants.EXACT_SEARCH_ALGORITHM) == 0) {
			String t = searchTarget.toLowerCase();
			if (t.indexOf("code") != -1) {
				message = Constants.ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE;
			} else {
				message = Constants.ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS;
			}
		}

        //else if (matchAlgorithm.compareTo(Constants.STARTWITH_SEARCH_ALGORITHM) == 0
        //    && matchText.length() < minimumSearchStringLength) {
        //    message = Constants.ERROR_ENCOUNTERED_TRY_NARROW_QUERY;
        //}

        hide_ontology_list = "false";

        request.getSession().setAttribute("hide_ontology_list",
            hide_ontology_list);
        request.getSession().setAttribute("warning", message);
        request.getSession().setAttribute("message", message);
        request.getSession().setAttribute("ontologiesToSearchOn",
            ontologiesToSearchOnStr);
        request.getSession().setAttribute("multiple_search_no_match_error",
            "true");
        return "multiple_search";
    }

    public String acceptLicenseAgreement() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        // update LicenseBean
        String dictionary = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
        String version = HTTPUtils.cleanXSS((String) request.getParameter("version"));

        LicenseBean licenseBean =
            (LicenseBean) request.getSession().getAttribute("licenseBean");
        if (licenseBean == null) {
            licenseBean = new LicenseBean();
        }
        licenseBean.addLicenseAgreement(dictionary);
        request.getSession().setAttribute("licenseBean", licenseBean);

        request.getSession().setAttribute("dictionary", dictionary);
        request.getSession().setAttribute("scheme", dictionary);
        request.getSession().setAttribute("version", version);
        return "vocabulary_home";
    }

    public String advancedSearchAction() {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        ResolvedConceptReferencesIteratorWrapper wrapper = null;
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("error_msg");
/*
        boolean retval = HTTPUtils.validateRequestParameters(request);
        if (!retval) {
			System.out.println("advancedSearchAction returns invalid_parameter");
			return "invalid_parameter";
		}
*/
        String scheme = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
	    if (scheme == null || DataUtils.getFormalName(scheme) == null) {
			String message = "Invalid vocabulary name.";
			request.setAttribute("message", message);
			return "message";
		}

        String version = HTTPUtils.cleanXSS((String) request.getParameter("version"));
        if (version != null) {
			boolean isVersionValid = DataUtils.validateCodingSchemeVersion(scheme, version);
			if (!isVersionValid) {
				String message = "Invalid vocabulary version.";
				request.setAttribute("message", message);
				return "message";
			}
		}

        SearchStatusBean bean =
            (SearchStatusBean) FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap().get("searchStatusBean");

        if (bean == null) {
            bean = new SearchStatusBean(scheme, version);
            //KLO 041411
            //request.setAttribute("searchStatusBean", bean);
            request.getSession().setAttribute("searchStatusBean", bean);
        }


        String direction = HTTPUtils.cleanXSS((String) request.getParameter("direction"));
        //051512 KLO AppScan
        if (direction == null || direction.length() == 0) {
			direction = "source";
		}

		bean.setDirection(direction);

		//request.getSession().setAttribute("direction", direction);

        String matchType = HTTPUtils.cleanXSS((String) request.getParameter("adv_search_type"));
        //051512 KLO AppScan
        if (matchType == null || matchType.length() == 0) {
			matchType = "Name";
		}
        bean.setSearchType(matchType);

        String matchAlgorithm =
            HTTPUtils.cleanXSS((String) request.getParameter("adv_search_algorithm"));
        //051512 KLO AppScan
        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
			matchAlgorithm = "exactMatch";
		}
        bean.setAlgorithm(matchAlgorithm);

        String source = HTTPUtils.cleanXSS((String) request.getParameter("adv_search_source"));
        //051512 KLO AppScan
        if (source == null || source.length() == 0) {
			source = "ALL";
		}
        bean.setSelectedSource(source);

        String selectSearchOption =
            HTTPUtils.cleanXSS((String) request.getParameter("selectSearchOption"));
        if (selectSearchOption == null || selectSearchOption.length() == 0) {
			selectSearchOption = "Name";
		}
        bean.setSelectedSearchOption(selectSearchOption);
        //request.getSession().setAttribute("selectSearchOption", selectSearchOption);

        String selectProperty = HTTPUtils.cleanXSS((String) request.getParameter("selectProperty"));
        bean.setSelectedProperty(selectProperty);

        String rel_search_association =
            HTTPUtils.cleanXSS((String) request.getParameter("rel_search_association"));

        bean.setSelectedAssociation(rel_search_association);

        String rel_search_rela =
            HTTPUtils.cleanXSS((String) request.getParameter("rel_search_rela"));
        bean.setSelectedRELA(rel_search_rela);

        FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
            .put("searchStatusBean", bean);

        //request.setAttribute("searchStatusBean", bean);
        //KLO 041411
        request.getSession().setAttribute("searchStatusBean", bean);

        String searchTarget = HTTPUtils.cleanXSS((String) request.getParameter("searchTarget"));
        String matchText = HTTPUtils.cleanMatchTextXSS((String) request.getParameter("matchText"));

        if (matchText == null || matchText.length() == 0) {
            String message = "Please enter a search string.";
            // request.getSession().setAttribute("message", message);
            request.setAttribute("message", message);
            return "message";
        }

        matchText = matchText.trim();
        bean.setMatchText(matchText);


        if (NCItBrowserProperties.get_debugOn()) {
            _logger.debug(Utils.SEPARATOR);
            _logger.debug("* criteria: " + matchText);
            _logger.debug("* source: " + source);
        }

        // String scheme = Constants.CODING_SCHEME_NAME;
        Vector schemes = new Vector();
        schemes.add(scheme);

        String max_str = null;
        int maxToReturn = -1;// 1000;
        try {
            max_str =
                NCItBrowserProperties.getInstance().getProperty(
                    NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {
        }
        //Utils.StopWatch stopWatch = new Utils.StopWatch();
        Vector<org.LexGrid.concepts.Entity> v = null;

        boolean excludeDesignation = true;
        boolean designationOnly = false;

        // check if this search has been performance previously through
        // IteratorBeanManager
        IteratorBeanManager iteratorBeanManager =
            (IteratorBeanManager) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap()
                .get("iteratorBeanManager");

        if (iteratorBeanManager == null) {
            iteratorBeanManager = new IteratorBeanManager();
            FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap()
                .put("iteratorBeanManager", iteratorBeanManager);
        }

        IteratorBean iteratorBean = null;
        ResolvedConceptReferencesIterator iterator = null;
        boolean ranking = true;

        SearchFields searchFields = null;
        String key = null;

        String searchType = HTTPUtils.cleanXSS((String) request.getParameter("selectSearchOption"));
        searchTarget = searchType;

        _logger.debug("SearchUtils.java searchType: " + searchType);



boolean retval = HTTPUtils.validateRequestParameters(request);
if (!retval) {
	System.out.println("advancedSearchAction returns invalid_parameter");
	return "invalid_parameter";
}



        if (searchType != null && searchType.compareTo("Property") == 0) {
            /*
             * _logger.debug("Advanced Search: "); _logger.debug("searchType: "
             * + searchType); _logger.debug("matchText: " + matchText);
             * _logger.debug("adv_search_algorithm: " + adv_search_algorithm);
             * _logger.debug("adv_search_source: " + adv_search_source);
             */

            String property_type =
                HTTPUtils.cleanXSS((String) request.getParameter("selectPropertyType"));
            if (property_type != null && property_type.compareTo("ALL") == 0) {
                property_type = null;
            }

            String property_name = selectProperty;
            if (property_name != null) {
                property_name = property_name.trim();
                // if (property_name.length() == 0) property_name = null;
                if (property_name.compareTo("ALL") == 0)
                    property_name = null;
            }

            searchFields =
                SearchFields.setProperty(schemes, matchText, searchTarget,
                    property_type, property_name, source, matchAlgorithm,
                    maxToReturn);
            key = searchFields.getKey();

            _logger.debug("advancedSearchAction " + key);

            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
                String[] property_types = null;
                if (property_type != null)
                    property_types = new String[] { property_type };
                String[] property_names = null;
                if (property_name != null)
                    property_names = new String[] { property_name };
                excludeDesignation = false;
                wrapper =
                    new SearchUtils(lbSvc).searchByProperties(scheme, version,
                        matchText, property_types, property_names, source,
                        matchAlgorithm, excludeDesignation, ranking,
                        maxToReturn);
                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                }
                if (iterator != null) {
                    iteratorBean = new IteratorBean(iterator);
                    iteratorBean.setKey(key);
                    iteratorBean.setMatchText(matchText);
                    iteratorBeanManager.addIteratorBean(iteratorBean);
                }
            }

        } else if (searchType != null && searchType.compareToIgnoreCase("Relationship") == 0) {

			//request.getSession().setAttribute("selectSearchOption", "Relationship");

            if (rel_search_association != null
                && rel_search_association.compareTo("ALL") == 0) {
                rel_search_association = null;
			}

            if (rel_search_rela != null) {
                rel_search_rela = rel_search_rela.trim();
                if (rel_search_rela.length() == 0)
                    rel_search_rela = null;
            }

            /*
             * String rel_search_direction = (String)
             * request.getParameter("rel_search_direction");
             *
             * //boolean direction = false; int search_direction =
             * Constants.SEARCH_BOTH_DIRECTION; if (rel_search_direction != null
             * && rel_search_direction.compareTo("source") == 0) {
             * search_direction = Constants.SEARCH_SOURCE; //direction = true; }
             * else if (rel_search_direction != null &&
             * rel_search_direction.compareTo("target") == 0) { search_direction
             * = Constants.SEARCH_TARGET; //direction = true; }
             */

/*
            int search_direction = Constants.SEARCH_SOURCE;
            if (direction != null && direction.compareToIgnoreCase("target") == 0) {
				search_direction = Constants.SEARCH_TARGET;
			}
*/
            int search_direction = Constants.SEARCH_SOURCE;
            if (direction != null && direction.compareToIgnoreCase("source") == 0) {
				search_direction = Constants.SEARCH_TARGET;
			}

            _logger.debug("AdvancedSearchAction search_direction "
                + search_direction);

/*
            searchFields =
                SearchFields.setRelationship(schemes, matchText, searchTarget,
                    rel_search_association, rel_search_rela, source,
                    matchAlgorithm, maxToReturn);

*/
            searchFields =
                SearchFields.setRelationship(schemes, matchText, searchTarget,
                    rel_search_association, rel_search_rela, source,
                    matchAlgorithm, direction, maxToReturn);

            key = searchFields.getKey();

            _logger.debug("AdvancedSearchAction key " + key);

            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);

                iterator = iteratorBean.getIterator();
            } else {

                String[] associationsToNavigate = null;
                String[] association_qualifier_names = null;
                String[] association_qualifier_values = null;


		if (rel_search_association != null) {
			String assocName = rel_search_association;
			//_logger.debug("Converting " + rel_search_association + " to " + assocName);
			associationsToNavigate =
				new String[] { assocName };

		} else {
			System.out.println("(*) associationsToNavigate == null");
		}

		if (rel_search_rela != null) {
			association_qualifier_names = new String[] { "rela" };
			association_qualifier_values =
				new String[] { rel_search_rela };

			if (associationsToNavigate == null) {
				Vector w = new CodingSchemeDataUtils(lbSvc).getSupportedAssociationNames(scheme, null);
				if (w == null || w.size() == 0) {
					//_logger
					//	.warn("OntologyBean.getAssociationNames() returns null, or nothing???");
				} else {
					associationsToNavigate = new String[w.size()];
					for (int i = 0; i < w.size(); i++) {
						String nm = (String) w.elementAt(i);
						associationsToNavigate[i] = nm;
					}
				}
			}
		}

/*
System.out.println("(*) advancedSearchAction SearchUtils(lbSvc).searchByAssociations ...");

		    wrapper =
			new SearchUtils(lbSvc).searchByAssociations(scheme, version,
				matchText, associationsToNavigate,
				association_qualifier_names,
				association_qualifier_values, search_direction, source,
				matchAlgorithm, excludeDesignation, ranking,
				maxToReturn);
*/
                wrapper =
                    new SearchUtils(lbSvc).searchByAssociations(scheme, version,
                        matchText, associationsToNavigate,
                        association_qualifier_names,
                        association_qualifier_values, search_direction, source,
                        matchAlgorithm, excludeDesignation, ranking,
                        maxToReturn);

                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                }
                if (iterator != null) {

						try {
							int numberRemaining = iterator.numberRemaining();
							if (numberRemaining == 0) {
								iterator = null;
							} else {
           					    if (iterator.hasNext()) {
									iteratorBean = new IteratorBean(iterator);
									iteratorBean.setKey(key);
									iteratorBean.setMatchText(matchText);
									iteratorBeanManager.addIteratorBean(iteratorBean);
									request.getSession().setAttribute("key", key);
								} else {
									iterator = null;
								}
						    }

						} catch (Exception ex) {
							//ex.printStackTrace();
							//020713 KLO
							iterator = null;
						}
/*
                    iteratorBean = new IteratorBean(iterator);
                    iteratorBean.setKey(key);
                    iteratorBean.setMatchText(matchText);
                    iteratorBeanManager.addIteratorBean(iteratorBean);

                    request.getSession().setAttribute("key", key);
*/
                }
            }

        } else if (searchType != null && searchType.compareTo("Name") == 0) {
			// temporary fix for: [NCITERM-682] Contains search failed on search strings containing a colon character.
			String matchTextStr = matchText;
			if (matchAlgorithm.compareTo("contains") == 0) {
				matchTextStr = matchTextStr.replaceAll(":", " ");
			}

            searchFields =
                SearchFields.setName(schemes, matchTextStr, searchTarget, source,
                    matchAlgorithm, maxToReturn);
            key = searchFields.getKey();
            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
                //LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
				if (new SimpleSearchUtils(lbSvc).searchAllSources(source) && SimpleSearchUtils.isSimpleSearchSupported(matchAlgorithm, SimpleSearchUtils.NAMES)) {
					try {
						iterator = new SimpleSearchUtils(lbSvc).search(scheme, version, matchTextStr, SimpleSearchUtils.BY_NAME, matchAlgorithm);
						wrapper = new ResolvedConceptReferencesIteratorWrapper(iterator);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					wrapper =
						new SearchUtils(lbSvc).searchByName(scheme, version, matchTextStr,
							source, matchAlgorithm, ranking, maxToReturn,
							SearchUtils.NameSearchType.Name);
				}

                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                    if (iterator != null) {
                        iteratorBean = new IteratorBean(iterator);
                        iteratorBean.setKey(key);
                        iteratorBean.setMatchText(matchText);
                        iteratorBeanManager.addIteratorBean(iteratorBean);

                        request.getSession().setAttribute("key", key);
                    }
                }
            }

        } else if (searchType != null && searchType.compareTo("Code") == 0) {
            searchFields =
                SearchFields.setCode(schemes, matchText, searchTarget, source,
                    matchAlgorithm, maxToReturn);
            key = searchFields.getKey();
            if (iteratorBeanManager.containsIteratorBean(key)) {
                iteratorBean = iteratorBeanManager.getIteratorBean(key);
                iterator = iteratorBean.getIterator();
            } else {
                /*
                 * wrapper = new SearchUtils().searchByName(scheme, version,
                 * matchText, source, matchAlgorithm, ranking, maxToReturn,
                 * SearchUtils.NameSearchType.Code);
                 */
			    schemes = new Vector();
			    Vector versions = new Vector();
			    schemes.add(scheme);
			    versions.add(version);

                wrapper = new CodeSearchUtils(lbSvc).searchByCode(schemes, versions, matchText, source, matchAlgorithm, ranking, maxToReturn, false);

                /*
                wrapper =
                    new SearchUtils().searchByCode(scheme, version, matchText,
                        source, matchAlgorithm, ranking, maxToReturn);
                */

                if (wrapper != null) {
                    iterator = wrapper.getIterator();
                    if (iterator != null) {
                        iteratorBean = new IteratorBean(iterator);
                        iteratorBean.setKey(key);

                        iteratorBean.setMatchText(matchText);


                        iteratorBeanManager.addIteratorBean(iteratorBean);

                        request.getSession().setAttribute("key", key);
                    }
                }
            }
        }

        request.getSession().setAttribute("key", key);
        request.getSession().setAttribute("matchText", matchText);

        request.getSession().removeAttribute("neighborhood_synonyms");
        request.getSession().removeAttribute("neighborhood_atoms");
        request.getSession().removeAttribute("concept");
        request.getSession().removeAttribute("code");
        request.getSession().removeAttribute("codeInNCI");
        request.getSession().removeAttribute("AssociationTargetHashMap");
        request.getSession().removeAttribute("type");

        if (iterator != null) {

            int size = iteratorBean.getSize();

            // LexEVS API iterator.numberRemaining is inaccurate, and can cause issues.
            // the following code is a work around.
            if (size == 1) {
            	List list = iteratorBean.getData(0, 0);
            	if (size != iteratorBean.getSize()) {
					size = iteratorBean.getSize();
				}
			}

            _logger.debug("AdvancedSearchActon size: " + size);

            // Write a search log entry
            SearchLog.writeEntry(searchFields, size, HTTPUtils
                .getRefererParmDecode(request));

            if (size > 1) {
                request.getSession().setAttribute("search_results", v);
                String match_size = Integer.toString(size);
                ;// Integer.toString(v.size());
                request.getSession().setAttribute("match_size", match_size);
                request.getSession().setAttribute("page_string", "1");

                request.setAttribute("version", version);
                request.getSession().setAttribute("new_search", Boolean.TRUE);
                return "search_results";

            } else if (size == 1) {
                request.getSession().setAttribute("singleton", "true");
                request.getSession().setAttribute("dictionary", scheme);
                // Concept c = (Concept) v.elementAt(0);
                int pageNumber = 1;

                List list = null;
                ResolvedConceptReference ref = null;
                try {
					list = iteratorBean.getData(1);
                    ref = (ResolvedConceptReference) list.get(0);

				} catch (Exception ex) {
					String message = "No match found.";
					if (matchAlgorithm.compareTo(Constants.EXACT_SEARCH_ALGORITHM) == 0) {
						String t = searchTarget.toLowerCase();
						if (t.indexOf("code") != -1) {
							message = Constants.ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE;
						} else {
							message = Constants.ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS;
						}
					}
					request.setAttribute("message", message);
					return "no_match";
				}

                Entity c = null;
                if (ref == null) {
                    String msg =
                        "Error: Null ResolvedConceptReference encountered.";
                    request.getSession().setAttribute("message", msg);
                    return "message";

                } else {
                    c = ref.getReferencedEntry();
                    if (c == null) {
                        c =
                            DataUtils.getConceptByCode(scheme, null, null, ref
                                .getConceptCode());
                    }
                }

                request.getSession().setAttribute("code", ref.getConceptCode());
                request.getSession().setAttribute("concept", c);
                request.getSession().setAttribute("type", "properties");
                request.getSession().setAttribute("version", version);
                request.getSession().setAttribute("new_search", Boolean.TRUE);

HttpServletResponse response =
	(HttpServletResponse) FacesContext.getCurrentInstance()
		.getExternalContext().getResponse();

response.setContentType("text/html;charset=utf-8");

                return "concept_details";
            }
        }
        String message = "No match found.";
		if (matchAlgorithm.compareTo(Constants.EXACT_SEARCH_ALGORITHM) == 0) {
			String t = searchTarget.toLowerCase();
			if (t.indexOf("code") != -1) {
				message = Constants.ERROR_NO_MATCH_FOUND_CODE_IS_CASESENSITIVE;
			} else {
				message = Constants.ERROR_NO_MATCH_FOUND_TRY_OTHER_ALGORITHMS;
			}
		}
        request.setAttribute("message", message);
        return "no_match";

    }


	//resolveValueSetAction

    public String resolveValueSetAction() {
		/*
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
        */
        return "resolve_value_set";

	}


    public String continueResolveValueSetAction() {
		/*
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        */
        return "resolved_value_set";

	}


    public String exportValueSetAction() {
		/*
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        */
        return "exported_value_set";

	}

	public String showOtherVersions() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String matchText = HTTPUtils.cleanMatchTextXSS((String) request.getParameter("matchText"));
        if (matchText != null)
            matchText = matchText.trim();

        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = HTTPUtils.cleanXSS((String) request.getParameter("algorithm"));
        String searchTarget = HTTPUtils.cleanXSS((String) request.getParameter("searchTarget"));

        request.getSession().setAttribute("searchTarget", searchTarget);
        request.getSession().setAttribute("algorithm", matchAlgorithm);

        String[] ontology_list = request.getParameterValues("ontology_list");

        StringBuffer buf = new StringBuffer();
        buf.append("|");
        if (ontology_list != null) {
			for (int i = 0; i < ontology_list.length; ++i) {
				//ontologiesToSearchOnStr = ontologiesToSearchOnStr + ontology_list[i] + "|";
				buf.append(ontology_list[i] + "|");
			}
	    }
	    String ontologiesToSearchOnStr = buf.toString();


	    Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");
		String action_cs = action_coding_scheme;
		for (int i = 0; i < display_name_vec.size(); i++) {
		     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);

			 if (info.getVisible()) {
				 info.setSelected(false);
				 if (ontologiesToSearchOnStr.indexOf(info.getLabel()) != -1) {
					 info.setSelected(true);
				 }
				 if (ontology_list == null) {
					 info.setSelected(false);
				 }
		     }

			 if (action_cs != null && action_cs.compareTo(info.getCodingScheme()) == 0 && info.getHasMultipleVersions()) {
			     info.setExpanded(true);
			 } else if (action_cs != null && action_cs.compareTo(info.getCodingScheme()) == 0 && !info.isProduction()) {
				 info.setVisible(true);
			 }
		}
        // [NCITERM-641] Tomcat session is mixed up.
		String ontologiesToExpandStr = getOntologiesToExpandStr(display_name_vec);
		request.getSession().setAttribute("ontologiesToExpandStr", ontologiesToExpandStr);
        request.getSession().setAttribute("display_name_vec", display_name_vec);
        request.getSession().setAttribute("ontologiesToSearchOnStr", ontologiesToSearchOnStr);
		return "multiple_search";
	}

	public String hideOtherVersions() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String matchText = HTTPUtils.cleanMatchTextXSS((String) request.getParameter("matchText"));
        if (matchText != null)
            matchText = matchText.trim();

        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = HTTPUtils.cleanXSS((String) request.getParameter("algorithm"));
        String searchTarget = HTTPUtils.cleanXSS((String) request.getParameter("searchTarget"));

        request.getSession().setAttribute("searchTarget", searchTarget);
        request.getSession().setAttribute("algorithm", matchAlgorithm);

        String[] ontology_list = request.getParameterValues("ontology_list");
        //String action_cs = (String) request.getParameter("cs_name");

        StringBuffer buf = new StringBuffer();
        buf.append("|");
        //String ontologiesToSearchOnStr = "|";
        if (ontology_list != null) {
			for (int i = 0; i < ontology_list.length; ++i) {
				//ontologiesToSearchOnStr = ontologiesToSearchOnStr + ontology_list[i] + "|";
				buf.append(ontology_list[i] + "|");
			}
	    }
	    String ontologiesToSearchOnStr = buf.toString();

	    Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");

		String action_cs = action_coding_scheme;

		for (int i = 0; i < display_name_vec.size(); i++) {
		     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
			 if (info.getVisible()) {
				 info.setSelected(false);
				 if (ontologiesToSearchOnStr.indexOf(info.getLabel()) != -1) {
					 info.setSelected(true);
				 }
				 if (ontology_list == null) {
					 info.setSelected(false);
				 }
		     }

			 if (action_cs != null && action_cs.compareTo(info.getCodingScheme()) == 0 && info.getHasMultipleVersions()) {
			     info.setExpanded(false);
			 } else if (action_cs != null && action_cs.compareTo(info.getCodingScheme()) == 0 && !info.isProduction()) {
				 info.setVisible(false);
			 }


		}
		// [NCITERM-641] Tomcat session is mixed up.
		String ontologiesToExpandStr = getOntologiesToExpandStr(display_name_vec);
		request.getSession().setAttribute("ontologiesToExpandStr", ontologiesToExpandStr);

        request.getSession().setAttribute("display_name_vec", display_name_vec);
        request.getSession().setAttribute("ontologiesToSearchOnStr", ontologiesToSearchOnStr);
		return "multiple_search";
	}


	public String clearAll() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String matchText = HTTPUtils.cleanMatchTextXSS((String) request.getParameter("matchText"));
        if (matchText != null)
            matchText = matchText.trim();

        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = HTTPUtils.cleanXSS((String) request.getParameter("algorithm"));
        String searchTarget = HTTPUtils.cleanXSS((String) request.getParameter("searchTarget"));

        request.getSession().setAttribute("searchTarget", searchTarget);
        request.getSession().setAttribute("algorithm", matchAlgorithm);


	    Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");
		for (int i = 0; i < display_name_vec.size(); i++) {
		     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
             info.setSelected(false);
		}
        request.getSession().setAttribute("display_name_vec", display_name_vec);
        request.getSession().setAttribute("ontologiesToSearchOnStr", "|");


        //KLO, 102611
     	request.getSession().removeAttribute("ontology_list");
		return "multiple_search";
	}


     private void show_other_versions(HttpServletRequest request, String action_cs, boolean show) {

		//System.out.println("action_cs: " + action_cs + "; show or hide: " + show);


	    String ontologiesToSearchOnStr = (String) request.getSession().getAttribute("ontologiesToSearchOnStr");
	    if (ontologiesToSearchOnStr == null) {

			String[] ontology_list = request.getParameterValues("ontology_list"); // checkboxes, value = info.getLabel();
			if ( ontology_list  == null ) {
				 ontology_list = (String[]) request.getSession().getAttribute("ontology_list");
			}

			StringBuffer buf = new StringBuffer();
			buf.append("|");
			if (ontology_list != null) {
				//System.out.println("(*) UserSessionBean ontology_list.length: " + ontology_list.length);
				for (int i = 0; i < ontology_list.length; ++i) {

					//System.out.println("(" + i + ") " + ontology_list[i]);

					buf.append(ontology_list[i] + "|");
				}
			}
			ontologiesToSearchOnStr = buf.toString();
		}

String s = "|";
	    Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");
		for (int i = 0; i < display_name_vec.size(); i++) {
		     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);

			 //if (info.getVisible()) {
				 info.setSelected(false);
				 if (ontologiesToSearchOnStr.indexOf(info.getLabel()) != -1) {
					 info.setSelected(true);
					 s = s + info.getLabel() + "|";

				 }


		     //}

			 if (action_cs != null && action_cs.compareTo(info.getCodingScheme()) == 0 && info.getHasMultipleVersions()) {
			     info.setExpanded(show);
			 } else if (action_cs != null && action_cs.compareTo(info.getCodingScheme()) == 0 && !info.isProduction()) {
				 info.setVisible(show);
			 }
		}


ontologiesToSearchOnStr = s;

        // [NCITERM-641] Tomcat session is mixed up.
		String ontologiesToExpandStr = getOntologiesToExpandStr(display_name_vec);
		request.getSession().setAttribute("ontologiesToExpandStr", ontologiesToExpandStr);
        request.getSession().setAttribute("display_name_vec", display_name_vec);
        request.getSession().setAttribute("ontologiesToSearchOnStr", ontologiesToSearchOnStr);

     }


    public void showListener(ActionEvent evt) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String action_cs_index_str = (String) ctx.getExternalContext().getRequestParameterMap().get("action_cs_index");

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();


        int k = Integer.parseInt(action_cs_index_str);
        int show_counter = 0;
	    Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");
		for (int i = 0; i < display_name_vec.size(); i++) {
		     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
             if (info.isProduction() && info.getHasMultipleVersions() && !info.getExpanded()) {
				 show_counter++;
				 if (show_counter == k) {
					 action_coding_scheme = info.getCodingScheme();
					 show_other_versions(request, action_coding_scheme, true);
					 break;
				 }
			 }
		}

		String t0 = (String) request.getSession().getAttribute("ontologiesToSearchOnStr");

		//System.out.println("EXIT show_other_versions: " + t0);

    }

    public void hideListener(ActionEvent evt) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        String action_cs_index_str = (String) ctx.getExternalContext().getRequestParameterMap().get("action_cs_index");
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        int k = Integer.parseInt(action_cs_index_str);
        int hide_counter = 0;
	    Vector display_name_vec = (Vector) request.getSession().getAttribute("display_name_vec");
		for (int i = 0; i < display_name_vec.size(); i++) {
		     OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
		     if (info.isProduction() && info.getHasMultipleVersions() && info.getExpanded()) {
				 hide_counter++;
				 if (hide_counter == k) {
					 action_coding_scheme = info.getCodingScheme();
					 show_other_versions(request, action_coding_scheme, false);
					 break;
				 }
			 }
		}
		//System.out.println("EXIT show_other_versions");

    }


    public List getPageSizeList() {
		if (_pageSizeList != null) return _pageSizeList;
        _pageSizeList = new ArrayList();
        _pageSizeList.add(new SelectItem("25"));
        _pageSizeList.add(new SelectItem("50"));
        _pageSizeList.add(new SelectItem("75"));
        _pageSizeList.add(new SelectItem("100"));
        _pageSizeList.add(new SelectItem("150"));
        _pageSizeList.add(new SelectItem("200"));
        _pageSizeList.add(new SelectItem("250"));
        _pageSizeList.add(new SelectItem("500"));
        return _pageSizeList;
    }

    public void setSelectedPageSize(String selectedPageSize) {
        _selectedPageSize = selectedPageSize;

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().setAttribute("selectedPageSize",
            _selectedPageSize);
    }

    public String getSelectedPageSize() {
        return _selectedPageSize;
    }


    public void pageSizeChanged(ValueChangeEvent event) {

        if (event.getNewValue() == null) {

            return;
		}
        String newValue = (String) event.getNewValue();

        setSelectedPageSize(newValue);


    }

    public String selectPageSize() {
		return "mapping";
	}


    public ResolvedConceptReference getFirstResolvedConceptReference(ResolvedConceptReferencesIterator iterator) {
		if (iterator == null) {
			return null;
		}
		try {
			int numberRemaining = iterator.numberRemaining();
			while (iterator.hasNext()) {
				//ResolvedConceptReference[] refs = iterator.next(1).getResolvedConceptReference();
				//return refs[0];
				ResolvedConceptReference ref = (ResolvedConceptReference) iterator.next();
				if (ref == null) {
					System.out.println("(UserSessionBean getFirstResolvedConceptReference returns null???");
				}
				return ref;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
    }



/////////////////////////////////////////////////////////////////////////////
// CAPTCHA
/////////////////////////////////////////////////////////////////////////////

    public String regenerateCaptchaImage() throws Exception {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();
			String answer = HTTPUtils.cleanXSS((String) request.getParameter("answer"));
            String subject = HTTPUtils.cleanXSS((String) request.getParameter("subject"));
            String message = HTTPUtils.cleanXSS((String) request.getParameter("message"));
            String from    = HTTPUtils.cleanXSS((String) request.getParameter("emailaddress"));

		request.getSession().setAttribute("answer", answer);
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("message", message);
		request.getSession().setAttribute("emailaddress", from);

		request.getSession().setAttribute("retry", "true");

        String captcha_option = HTTPUtils.cleanXSS((String) request.getParameter("captcha_option"));
        boolean captchaOptionValid = DataUtils.isCaptchaOptionValid(captcha_option);
        if (!captchaOptionValid) {
			String msg = Constants.INVALID_CAPTCHA_OTPION;
			request.getSession().setAttribute("errorMsg", msg);
		}
        return "retry";
	}

	public boolean isNull(String s) {
		if (s == null) return true;
		s = s.trim();
		if (s.compareTo("") == 0) return true;
		return false;
	}


	public String switchCaptchaMode() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

		String answer = HTTPUtils.cleanXSS((String) request.getParameter("answer"));
		String subject = HTTPUtils.cleanXSS((String) request.getParameter("subject"));
		String message = HTTPUtils.cleanXSS((String) request.getParameter("message"));
		String from    = HTTPUtils.cleanXSS((String) request.getParameter("emailaddress"));

		request.getSession().setAttribute("answer", answer);
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("message", message);
		request.getSession().setAttribute("emailaddress", from);

        String captcha_option = HTTPUtils.cleanXSS((String) request.getParameter("captcha_option"));
        if (isNull(captcha_option)) {
			captcha_option = "default";
		}

        boolean captchaOptionValid = DataUtils.isCaptchaOptionValid(captcha_option);
        if (!captchaOptionValid) {
			String msg = Constants.INVALID_CAPTCHA_OTPION;
			request.getSession().setAttribute("errorMsg", msg);
			return "retry";
		}


		if (captcha_option.compareTo("default") == 0) {
			captcha_option = "audio";
		} else {
			captcha_option = "default";
		}

        request.getSession().setAttribute("captcha_option", captcha_option);
        return "retry";
	}


    public String contactUs() throws Exception {

        String msg = "Your message was successfully sent.";
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        request.getSession().removeAttribute("errorMsg");
        request.getSession().removeAttribute("errorType");
        request.getSession().removeAttribute("retry");

		String answer = HTTPUtils.cleanXSS((String) request.getParameter("answer"));
		String subject = HTTPUtils.cleanXSS((String) request.getParameter("subject"));
		String message = HTTPUtils.cleanXSS((String) request.getParameter("message"));
		String from    = HTTPUtils.cleanXSS((String) request.getParameter("emailaddress"));

		request.getSession().setAttribute("answer", answer);
		request.getSession().setAttribute("subject", subject);
		request.getSession().setAttribute("message", message);
		request.getSession().setAttribute("emailaddress", from);


        String captcha_option = HTTPUtils.cleanXSS((String) request.getParameter("captcha_option"));
        if (isNull(captcha_option)) {
			captcha_option = "default";
		}
		request.getSession().setAttribute("captcha_option", captcha_option);

        boolean captchaOptionValid = DataUtils.isCaptchaOptionValid(captcha_option);
        if (!captchaOptionValid) {
			msg = Constants.INVALID_CAPTCHA_OTPION;
			request.getSession().setAttribute("errorMsg", msg);
			return "retry";
		}

		if (isNull(answer) || isNull(subject) || isNull(message) || isNull(from)) {
			msg = Constants.PLEASE_COMPLETE_DATA_ENTRIES;
			request.getSession().setAttribute("errorMsg", msg);
			request.getSession().setAttribute("retry", "true");
			return "retry";
		}

		boolean emailAddressValid = MailUtils.isValidEmailAddress(from);
		if (!emailAddressValid) {
			msg = Constants.INVALID_EMAIL_ADDRESS;
			request.getSession().setAttribute("errorMsg", msg);
			request.getSession().setAttribute("retry", "true");
			return "retry";
		}

        try {
    		String retstr = null;
    		if (captcha_option.compareTo("audio") == 0) {
				retstr = validateAudioCaptcha(request, "incomplete");
			} else {
				retstr = validateCaptcha(request, "incomplete");
			}

            //String recipients[] = MailUtils.getRecipients();
            //MailUtils.postMail(from, recipients, subject, message);

            String recipientStr = NCItBrowserProperties.getNCICB_CONTACT_URL();
            String mail_smtp_server = NCItBrowserProperties.getMAIL_SMTP_SERVER();
            MailUtils.postMail(from, recipientStr, subject, message, mail_smtp_server);

			request.getSession().setAttribute("message", msg);

        } catch (NoReloadException e) {
            msg = e.getMessage();
            request.getSession().setAttribute("errorMsg", Utils.toHtml(msg));
            request.getSession().setAttribute("errorType", "user");
            return "retry";

        } catch (InvalidCaptChaInputException e) {
            msg = e.getMessage();
            request.getSession().setAttribute("errorMsg", Utils.toHtml(msg));
            request.getSession().setAttribute("answer", "");
            request.getSession().setAttribute("errorType", "user");
            return "retry";

        } catch (Exception e) {
            msg = "Your message was not sent.\n";
            msg += "    (If possible, please contact NCI systems team.)\n";
            msg += "\n";
            msg += e.getMessage();
            request.getSession().setAttribute("errorMsg", Utils.toHtml(msg));
            request.getSession().setAttribute("errorType", "system");
            e.printStackTrace();
            return "error";
        }
        return "message";
    }

    private String validateCaptcha(HttpServletRequest request,
        String returnIncompleteState) throws Exception {
        Captcha captcha = (Captcha) request.getSession().getAttribute(Captcha.NAME);
        if (captcha == null) {
            captcha = new Captcha.Builder(200, 50).addText().addBackground()
                // .addNoise()
                .gimp()
                // .addBorder()
                .build();
            request.getSession().setAttribute(Captcha.NAME, captcha);
        }

        // Do this so we can capture non-Latin chars
        request.setCharacterEncoding("UTF-8");
        String answer = HTTPUtils.cleanXSS((String) request.getParameter("answer"));
        if (answer == null || answer.length() == 0) {
            throw new NoReloadException(
                "Please enter the characters appearing in the image. ");
        }

        request.getSession().removeAttribute("reload");
        if (!captcha.isCorrect(answer)) {
            throw new InvalidCaptChaInputException(
                "WARNING: The string you entered does not match"
                    + " with what is shown in the image. Please try again.");
		} else {
		}        request.getSession().removeAttribute(Captcha.NAME);
        return null;
    }

    private String validateAudioCaptcha(HttpServletRequest request,
        String returnIncompleteState) throws Exception {
        AudioCaptcha captcha = (AudioCaptcha) request.getSession().getAttribute(AudioCaptcha.NAME);

        // Do this so we can capture non-Latin chars
        request.setCharacterEncoding("UTF-8");
        String answer = HTTPUtils.cleanXSS((String) request.getParameter("answer"));

        if (answer == null || answer.length() == 0) {
            throw new NoReloadException(
                "Please enter the numbers you heard in the audio.");
        }

        request.getSession().removeAttribute("reload");
        if (!captcha.isCorrect(answer)) {
            throw new InvalidCaptChaInputException(
                "WARNING: The numbers you entered does not match"
                    + " with what is set in the audio. Please try again.");
		}

        request.getSession().removeAttribute(AudioCaptcha.NAME);
        return null;
    }


    private static class NoReloadException extends Exception {
        private static final long serialVersionUID = 1L;
        public NoReloadException(String text) {
            super(text);
        }
    }


    private static class InvalidCaptChaInputException extends Exception {
        private static final long serialVersionUID = 2L;
        public InvalidCaptChaInputException(String text) {
            super(text);
        }
    }

    public String clearContactUs() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

		request.getSession().setAttribute("errorMsg", "");
		request.getSession().setAttribute("answer", "");
		request.getSession().setAttribute("subject", "");
		request.getSession().setAttribute("message", "");
		request.getSession().setAttribute("emailaddress", "");

        return "retry";

    }

    // [NCITERM-641] Tomcat session is mixed up.
    public String getOntologiesToExpandStr(Vector display_name_vec) {
		StringBuffer buf = new StringBuffer();
		String ontologiesToExpandStr = null;
		buf.append("|");
		if (display_name_vec != null) {
			for (int i = 0; i < display_name_vec.size(); i++) {
				 OntologyInfo info = (OntologyInfo) display_name_vec.elementAt(i);
			     if (info.getExpanded()) {
					 buf.append(info.getLabel() + "|");
				 }
			}
		}
		return buf.toString();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void export_mapping_search_results(HttpServletRequest request, HttpServletResponse response) {
        String mapping_schema = HTTPUtils.cleanXSS((String) request.getParameter("dictionary"));
        String mapping_version = HTTPUtils.cleanXSS((String) request.getParameter("version"));

        //to be modified
        ResolvedConceptReferencesIterator iterator = DataUtils.getMappingDataIterator(mapping_schema, mapping_version);
		int numRemaining = 0;
		if (iterator != null) {
			try {
				numRemaining = iterator.numberRemaining();

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
        StringBuffer sb = new StringBuffer();
        try {
			sb.append("Source Code,");
			sb.append("Source Name,");
			sb.append("Source Coding Scheme,");
			sb.append("Source Coding Scheme Version,");
			sb.append("Source Coding Scheme Namespace,");

			sb.append("Association Name,");
			sb.append("REL,");
			sb.append("Map Rank,");

			sb.append("Target Code,");
			sb.append("Target Name,");
			sb.append("Target Coding Scheme,");
			sb.append("Target Coding Scheme Version,");
			sb.append("Target Coding Scheme Namespace");
			sb.append("\r\n");

			MappingIteratorBean bean = new MappingIteratorBean(iterator);
            List list = bean.getData(0, numRemaining-1);
            for (int k=0; k<list.size(); k++) {
				MappingData mappingData = (MappingData) list.get(k);
				sb.append("\"" + mappingData.getSourceCode() + "\",");
				sb.append("\"" + mappingData.getSourceName() + "\",");
				sb.append("\"" + mappingData.getSourceCodingScheme() + "\",");
				sb.append("\"" + mappingData.getSourceCodingSchemeVersion() + "\",");
				sb.append("\"" + mappingData.getSourceCodeNamespace() + "\",");

				sb.append("\"" + mappingData.getAssociationName() + "\",");
				sb.append("\"" + mappingData.getRel() + "\",");
				sb.append("\"" + mappingData.getScore() + "\",");

				sb.append("\"" + mappingData.getTargetCode() + "\",");
				sb.append("\"" + mappingData.getTargetName() + "\",");
				sb.append("\"" + mappingData.getTargetCodingScheme() + "\",");
				sb.append("\"" + mappingData.getTargetCodingSchemeVersion() + "\",");
				sb.append("\"" + mappingData.getTargetCodeNamespace() + "\"");
				sb.append("\r\n");
			}
		} catch (Exception ex)	{
			sb.append("WARNING: Export to CVS action failed.");
			ex.printStackTrace();
		}

		String filename = mapping_schema + "_" + mapping_version;
		filename = filename.replaceAll(" ", "_");
		filename = filename + ".csv";

		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ filename);

		response.setContentLength(sb.length());

		try {
			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(sb.toString().getBytes("UTF-8"), 0, sb.length());
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("WARNING: Export to CVS action failed.");
		}
		FacesContext.getCurrentInstance().responseComplete();
		return;
	}

}
