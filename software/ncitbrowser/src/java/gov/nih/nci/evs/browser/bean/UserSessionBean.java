package gov.nih.nci.evs.browser.bean;

import gov.nih.nci.evs.browser.utils.MailUtils;
import gov.nih.nci.evs.browser.utils.SearchUtils;
import gov.nih.nci.evs.browser.utils.UserInputException;
import gov.nih.nci.evs.browser.utils.Utils;
import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;
import gov.nih.nci.evs.browser.common.Constants;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.LexGrid.concepts.Concept;

import gov.nih.nci.evs.browser.utils.DataUtils;
import gov.nih.nci.evs.browser.utils.SortOption;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction with the National Cancer Institute,
 * and so to the extent government employees are co-authors, any rights in such works shall be subject to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the disclaimer of Article 3, below. Redistributions
 * in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution.
 * 2. The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * "This product includes software developed by NGIT and the National Cancer Institute."
 * If no such end-user documentation is to be included, this acknowledgment shall appear in the software itself,
 * wherever such third-party acknowledgments normally appear.
 * 3. The names "The National Cancer Institute", "NCI" and "NGIT" must not be used to endorse or promote products derived from this software.
 * 4. This license does not authorize the incorporation of this software into any third party proprietary programs. This license does not authorize
 * the recipient to use any trademarks owned by either NCI or NGIT
 * 5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 * NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 * Modification history
 *     Initial implementation kim.ong@ngc.com
 *
 */

public class UserSessionBean extends Object {
    private static String contains_warning_msg = "(WARNING: Only a subset of results may appear due to current limits in the terminology server (see Known Issues on the Help page).)";
    private String selectedQuickLink = null;
    private List quickLinkList = null;


    public List<SelectItem> ontologyList = null;
    public List<String> ontologiesToSearchOn = null;

    //public List<SelectItem> ontologyList = null;
    //public String[] ontologiesToSearchOn = null;

    public UserSessionBean() {
        //this.ontologyList = getOntologyList();
        ontologiesToSearchOn = new ArrayList<String>();

        //ontologiesToSearchOn = new String[20];
    }


    public void setSelectedQuickLink(String selectedQuickLink) {
        this.selectedQuickLink = selectedQuickLink;
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute("selectedQuickLink",
                selectedQuickLink);
    }

    public String getSelectedQuickLink() {
        return this.selectedQuickLink;
    }

    public void quickLinkChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();

        //System.out.println("quickLinkChanged; " + newValue);
        setSelectedQuickLink(newValue);

        HttpServletResponse response = (HttpServletResponse) FacesContext
                .getCurrentInstance().getExternalContext().getResponse();

        String targetURL = null;//"http://nciterms.nci.nih.gov/";
        if (selectedQuickLink.compareTo("NCI Terminology Browser") == 0) {
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
        quickLinkList = new ArrayList();
        quickLinkList.add(new SelectItem("Quick Links"));
        quickLinkList.add(new SelectItem("NCI Terminology Browser"));
        quickLinkList.add(new SelectItem("NCI MetaThesaurus"));
        quickLinkList.add(new SelectItem("EVS Home"));
        quickLinkList.add(new SelectItem("NCI Terminology Resources"));
        return quickLinkList;
    }

/*
    public String searchAction() {
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute("contains_warning_msg", "");
        String matchText = (String) request.getParameter("matchText");


        //[#19965] Error message is not displayed when Search Criteria is not provided
        if (matchText == null || matchText.length() == 0) {
            String message = "Please enter a search string.";
            request.getSession().setAttribute("message", message);
            return "message";
        }
        matchText = matchText.trim();
        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = (String) request.getParameter("algorithm");
        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
            String message = "Warning: Search algorithm parameter is not set.";
            request.getSession().setAttribute("message", message);
            return "message";
        }
        setSelectedAlgorithm(matchAlgorithm);

        String scheme = CODING_SCHEME_NAME;
        String vocabulary = (String) request.getParameter("vocabulary");
        if (vocabulary != null) scheme = vocabulary;

        System.out.println("vocabulary: " + scheme);

        String version = null;

        String max_str = null;
        int maxToReturn = -1;//1000;
        try {
            max_str = NCItBrowserProperties
                    .getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {
            // Do nothing
        }

        request.getSession().setAttribute("vocabulary", scheme);
        Utils.StopWatch stopWatch = new Utils.StopWatch();
        Vector<org.LexGrid.concepts.Concept> v = new SearchUtils()
                .searchByName(scheme, version, matchText, matchAlgorithm, maxToReturn);

        if (NCItBrowserProperties.debugOn) {
            System.out.println("scheme: " + scheme);
            System.out.println("version: " + version);
            System.out.println("keyword(s): " + matchText);
            System.out.println("algorithm: " + matchAlgorithm);
            try {
                System.out.println("sort.by.score: " + NCItBrowserProperties.
                    getProperty(NCItBrowserProperties.SORT_BY_SCORE));
            } catch (Exception e) {
            }
            System.out.println(stopWatch.getResult());
        }

        if (v != null && v.size() > 1) {
            String match_size = Integer.toString(v.size());
            request.getSession().setAttribute("search_results", v);
            request.getSession().setAttribute("match_size", match_size);
            request.getSession().setAttribute("page_string", "1");
            request.getSession().setAttribute("new_search", Boolean.TRUE);

            if (matchText.length() < 4
                    && matchAlgorithm.compareTo("contains") == 0) {
                request.getSession().setAttribute("contains_warning_msg",
                        contains_warning_msg);
            } else if (matchText.length() == 1
                    && matchAlgorithm.compareTo("startsWith") == 0) {
                request.getSession().setAttribute("contains_warning_msg",
                        contains_warning_msg);
            }


            request.setAttribute("search_results", v);
            request.setAttribute("match_size", match_size);
            request.setAttribute("page_string", "1");
            request.setAttribute("new_search", Boolean.TRUE);

            if (matchText.length() < 4
                    && matchAlgorithm.compareTo("contains") == 0) {
                request.setAttribute("contains_warning_msg",
                        contains_warning_msg);
            } else if (matchText.length() == 1
                    && matchAlgorithm.compareTo("startsWith") == 0) {
                request.setAttribute("contains_warning_msg",
                        contains_warning_msg);
            }

            return "search_results";
        }

        else if (v != null && v.size() == 1) {
            request.getSession().setAttribute("singleton", "true");
            //request.getSession().setAttribute("dictionary", CODING_SCHEME_NAME);
            request.getSession().setAttribute("dictionary", scheme);
            Concept c = (Concept) v.elementAt(0);
            request.getSession().setAttribute("code", c.getEntityCode());

            request.setAttribute("singleton", "true");
            request.setAttribute("dictionary", scheme);
            //Concept c = (Concept) v.elementAt(0);
            request.setAttribute("code", c.getEntityCode());

            return "concept_details";
        }
        String message = "No match found.";
        request.getSession().setAttribute("message", message);
        return "message";

    }
*/

/*
    public String searchAction() {
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        request.setAttribute("contains_warning_msg", "");
        String matchText = (String) request.getParameter("matchText");


        //[#19965] Error message is not displayed when Search Criteria is not provided
        if (matchText == null || matchText.length() == 0) {
            String message = "Please enter a search string.";
            request.setAttribute("message", message);
            return "message";
        }
        matchText = matchText.trim();
        request.setAttribute("matchText", matchText);

        String matchAlgorithm = (String) request.getParameter("algorithm");
        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
            String message = "Warning: Search algorithm parameter is not set.";
            request.setAttribute("message", message);
            return "message";
        }
        setSelectedAlgorithm(matchAlgorithm);

        String scheme = CODING_SCHEME_NAME;
        String vocabulary = (String) request.getParameter("vocabulary");
        if (vocabulary != null) scheme = vocabulary;

        System.out.println("vocabulary: " + scheme);

        String version = null;

        String max_str = null;
        int maxToReturn = -1;//1000;
        try {
            max_str = NCItBrowserProperties
                    .getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {
            // Do nothing
        }

        request.setAttribute("vocabulary", scheme);
        Utils.StopWatch stopWatch = new Utils.StopWatch();
        Vector<org.LexGrid.concepts.Concept> v = new SearchUtils()
                .searchByName(scheme, version, matchText, matchAlgorithm, maxToReturn);

        if (NCItBrowserProperties.debugOn) {
            System.out.println("scheme: " + scheme);
            System.out.println("version: " + version);
            System.out.println("keyword(s): " + matchText);
            System.out.println("algorithm: " + matchAlgorithm);
            try {
                System.out.println("sort.by.score: " + NCItBrowserProperties.
                    getProperty(NCItBrowserProperties.SORT_BY_SCORE));
            } catch (Exception e) {
            }
            System.out.println(stopWatch.getResult());
        }

        if (v != null && v.size() > 1) {
            String match_size = Integer.toString(v.size());
            request.setAttribute("search_results", v);
            request.setAttribute("match_size", match_size);
            request.setAttribute("page_string", "1");
            request.setAttribute("new_search", Boolean.TRUE);

            if (matchText.length() < 4
                    && matchAlgorithm.compareTo("contains") == 0) {
                request.setAttribute("contains_warning_msg",
                        contains_warning_msg);
            } else if (matchText.length() == 1
                    && matchAlgorithm.compareTo("startsWith") == 0) {
                request.setAttribute("contains_warning_msg",
                        contains_warning_msg);
            }


            request.setAttribute("search_results", v);
            request.setAttribute("match_size", match_size);
            request.setAttribute("page_string", "1");
            request.setAttribute("new_search", Boolean.TRUE);

            if (matchText.length() < 4
                    && matchAlgorithm.compareTo("contains") == 0) {
                request.setAttribute("contains_warning_msg",
                        contains_warning_msg);
            } else if (matchText.length() == 1
                    && matchAlgorithm.compareTo("startsWith") == 0) {
                request.setAttribute("contains_warning_msg",
                        contains_warning_msg);
            }

            return "single_search_results";
        }

        else if (v != null && v.size() == 1) {
            request.setAttribute("singleton", "true");
            //request.setAttribute("dictionary", CODING_SCHEME_NAME);
            request.setAttribute("dictionary", scheme);
            Concept c = (Concept) v.elementAt(0);
            request.setAttribute("code", c.getEntityCode());

            request.setAttribute("singleton", "true");
            request.setAttribute("dictionary", scheme);
            //Concept c = (Concept) v.elementAt(0);
            request.setAttribute("code", c.getEntityCode());

            return "concept_details";
        }
        String message = "No match found.";
        request.setAttribute("message", message);
        return "message";

    }
*/

    public String searchAction() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

        String matchText = (String) request.getParameter("matchText");
        if (matchText != null) matchText = matchText.trim();
        //[#19965] Error message is not displayed when Search Criteria is not proivded
        if (matchText == null || matchText.length() == 0)
        {
            String message = "Please enter a search string.";
            request.getSession().setAttribute("message", message);
            return "message";
        }
        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = (String) request.getParameter("algorithm");
        setSelectedAlgorithm(matchAlgorithm);

        /*
        String matchtype = (String) request.getParameter("matchtype");
        if (matchtype == null) matchtype = "string";
        */

        String searchTarget = (String) request.getParameter("searchTarget");
        request.getSession().setAttribute("searchTarget", searchTarget);
        System.out.println("searchTarget: " + searchTarget);

        if (searchTarget.compareTo("names") != 0) {
			String msg = "To be implemented.";
			request.getSession().setAttribute("message", msg);
			return "message";
		}

        //String rankingStr = (String) request.getParameter("ranking");
        //boolean ranking = rankingStr != null && rankingStr.equals("on");

        boolean ranking = true;

        request.getSession().setAttribute("ranking", Boolean.toString(ranking));
        String source = (String) request.getParameter("source");
        if (source == null) {
            source = "ALL";
        }
        //request.getSession().setAttribute("source", source);
        //setSelectedSource(source);

        if (NCItBrowserProperties.debugOn) {
            try {
                System.out.println(Utils.SEPARATOR);
                System.out.println("* criteria: " + matchText);
                //System.out.println("* matchType: " + matchtype);
                System.out.println("* source: " + source);
                System.out.println("* ranking: " + ranking);
               // System.out.println("* sortOption: " + sortOption);
            } catch (Exception e) {
            }
        }

        String scheme = request.getParameter("scheme");
        if (scheme == null) {
            scheme = (String) request.getAttribute("scheme");
        }

        if (scheme == null) {
            scheme = (String) request.getParameter("dictionary");
        }

        if (scheme == null) scheme = Constants.CODING_SCHEME_NAME;
        //String scheme = Constants.CODING_SCHEME_NAME;

System.out.println("* scheme: " + scheme);

        String version = null;
        String max_str = null;
        int maxToReturn = -1;//1000;
        try {
            max_str = NCItBrowserProperties.getInstance().getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {

        }
        Utils.StopWatch stopWatch = new Utils.StopWatch();
        Vector<org.LexGrid.concepts.Concept> v = null;

        //v = new SearchUtils().searchByName(scheme, version, matchText, source, matchAlgorithm, sortOption, maxToReturn);
        ResolvedConceptReferencesIterator iterator = new SearchUtils().searchByName(scheme, version, matchText, source, matchAlgorithm, ranking, maxToReturn);

        request.getSession().setAttribute("vocabulary", scheme);
        //request.getSession().setAttribute("matchtype", matchtype);

        request.getSession().removeAttribute("neighborhood_synonyms");
        request.getSession().removeAttribute("neighborhood_atoms");
        request.getSession().removeAttribute("concept");
        request.getSession().removeAttribute("code");
        request.getSession().removeAttribute("codeInNCI");
        request.getSession().removeAttribute("AssociationTargetHashMap");
        request.getSession().removeAttribute("type");

        //if (v != null && v.size() > 1)
        if (iterator != null) {

int numberRemaining = 0;
try {
    numberRemaining = iterator.numberRemaining();
} catch (Exception ex) {
    ex.printStackTrace();
}
System.out.println("* numberRemaining: " + numberRemaining);

            IteratorBean iteratorBean = (IteratorBean) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBean");

            if (iteratorBean == null) {
                iteratorBean = new IteratorBean(iterator);
                FacesContext.getCurrentInstance().getExternalContext()
                   .getSessionMap().put("iteratorBean", iteratorBean);
            } else {
                iteratorBean.setIterator(iterator);
            }

            int size = iteratorBean.getSize();
System.out.println("* size: " + size);

            if (size > 1) {

                request.getSession().setAttribute("search_results", v);

                String match_size = Integer.toString(size);;//Integer.toString(v.size());
                request.getSession().setAttribute("match_size", match_size);
                request.getSession().setAttribute("page_string", "1");

                request.getSession().setAttribute("new_search", Boolean.TRUE);
                request.getSession().setAttribute("dictionary", scheme);
                return "search_results";
            } else if (size == 1) {
                request.getSession().setAttribute("singleton", "true");
                request.getSession().setAttribute("dictionary", scheme);//Constants.CODING_SCHEME_NAME);
                //Concept c = (Concept) v.elementAt(0);
                int pageNumber = 1;
                List list = iteratorBean.getData(1);
                ResolvedConceptReference ref = (ResolvedConceptReference) list.get(0);

                Concept c = null;
                if (ref == null) {
                    String msg = "Error: Null ResolvedConceptReference encountered.";
                    request.getSession().setAttribute("message", msg);
                    return "message";

                } else {
                    c = ref.getReferencedEntry();
                    if (c == null) {
                        //c = DataUtils.getConceptByCode(Constants.CODING_SCHEME_NAME, null, null, ref.getConceptCode());
                        c = DataUtils.getConceptByCode(scheme, null, null, ref.getConceptCode());
                    }

System.out.println("(*) singleton concept found " + scheme + " " + c.getEntityDescription().getContent() + " " + c.getEntityCode());

                }

                request.getSession().setAttribute("code", ref.getConceptCode());
                request.getSession().setAttribute("concept", c);
                request.getSession().setAttribute("type", "properties");

                request.getSession().setAttribute("new_search", Boolean.TRUE);

                if (scheme.compareTo("NCI Thesaurus") == 0 || scheme.compareTo("NCI%20Thesaurus") == 0) {
                    return "concept_details";

                } else if (scheme.indexOf("NCI Thesaurus") != -1 || scheme.indexOf("NCI%20Thesaurus") != -1 ) {
                    return "concept_details";

                } else {
                    return "concept_details_other_term";
                }
            }
        }

        String message = "No match found.";
        if (matchAlgorithm.compareTo("exactMatch") == 0) {
            message = "No match found. Please try 'Beings With' or 'Contains' search instead.";
        }
        request.getSession().setAttribute("message", message);
        request.getSession().setAttribute("dictionary", scheme);
        return "message";
    }


    private String selectedResultsPerPage = null;
    private List resultsPerPageList = null;

    public List getResultsPerPageList() {
        resultsPerPageList = new ArrayList();
        resultsPerPageList.add(new SelectItem("10"));
        resultsPerPageList.add(new SelectItem("25"));
        resultsPerPageList.add(new SelectItem("50"));
        resultsPerPageList.add(new SelectItem("75"));
        resultsPerPageList.add(new SelectItem("100"));
        resultsPerPageList.add(new SelectItem("250"));
        resultsPerPageList.add(new SelectItem("500"));

        selectedResultsPerPage = ((SelectItem) resultsPerPageList.get(2))
                .getLabel(); // default to 50
        return resultsPerPageList;
    }

    public void setSelectedResultsPerPage(String selectedResultsPerPage) {
        if (selectedResultsPerPage == null)
            return;
        this.selectedResultsPerPage = selectedResultsPerPage;
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute("selectedResultsPerPage",
                selectedResultsPerPage);
    }

    public String getSelectedResultsPerPage() {
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        String s = (String) request.getSession().getAttribute(
                "selectedResultsPerPage");
        if (s != null) {
            this.selectedResultsPerPage = s;
        } else {
            this.selectedResultsPerPage = "50";
            request.getSession().setAttribute("selectedResultsPerPage", "50");
        }
        return this.selectedResultsPerPage;
    }

    public void resultsPerPageChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null) {
            return;
        }
        String newValue = (String) event.getNewValue();
        setSelectedResultsPerPage(newValue);
    }

    public String linkAction() {
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        return "";
    }

    private String selectedAlgorithm = null;
    private List algorithmList = null;

    public List getAlgorithmList() {
        algorithmList = new ArrayList();
        algorithmList.add(new SelectItem("exactMatch", "exactMatch"));
        algorithmList.add(new SelectItem("startsWith", "Begins With"));
        algorithmList.add(new SelectItem("contains", "Contains"));
        selectedAlgorithm = ((SelectItem) algorithmList.get(0)).getLabel();
        return algorithmList;
    }

    public void algorithmChanged(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();

        //System.out.println("algorithmChanged; " + newValue);
        setSelectedAlgorithm(newValue);
    }

    public void setSelectedAlgorithm(String selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute("selectedAlgorithm",
                selectedAlgorithm);
    }

    public String getSelectedAlgorithm() {
        return this.selectedAlgorithm;
    }

    public String contactUs() throws Exception {
        String msg = "Your message was successfully sent.";
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();

        try {
            String subject = request.getParameter("subject");
            String message = request.getParameter("message");
            String from = request.getParameter("emailaddress");
            String recipients[] = MailUtils.getRecipients();
            MailUtils.postMail(from, recipients, subject, message);
        } catch (UserInputException e) {
            msg = e.getMessage();
            request.setAttribute("errorMsg", Utils.toHtml(msg));
            request.setAttribute("errorType", "user");
            return "error";
        } catch (Exception e) {
            msg = "System Error: Your message was not sent.\n";
            msg += "    (If possible, please contact NCI systems team.)\n";
            msg += "\n";
            msg += e.getMessage();
            request.setAttribute("errorMsg", Utils.toHtml(msg));
            request.setAttribute("errorType", "system");
            e.printStackTrace();
            return "error";
        }

        request.getSession().setAttribute("message", Utils.toHtml(msg));
        return "message";
    }

////////////////////////////////////////////////////////////////////////////////////////////
// ontologies

    public List getOntologiesToSearchOn() {
        if (ontologyList == null) {
            ontologyList = DataUtils.getOntologyList();
            SelectItem item = (SelectItem) ontologyList.get(0);
            ontologiesToSearchOn.add(item.getLabel());
        } else if (ontologiesToSearchOn.size() == 0) {
            SelectItem item = (SelectItem) ontologyList.get(0);
            ontologiesToSearchOn.add(item.getLabel());
        }
        return ontologiesToSearchOn;
    }


    public List getOntologyList() {
        if (ontologyList == null) {
            ontologyList = DataUtils.getOntologyList();
            //System.out.println("--- Number of ontologies: " + ontologyList.size());
        }
/*
        HttpServletRequest request = (HttpServletRequest) FacesContext
            .getCurrentInstance().getExternalContext().getRequest();

        List list = (List) request.getSession().getAttribute("ontologyList");
        if (list == null) {
            request.getSession().setAttribute("ontologyList", ontologyList);
        }
*/
        return ontologyList;
    }

    public void setOntologiesToSearchOn(List<String> newValue) {
        ontologiesToSearchOn = new ArrayList<String>();
        for (int i=0; i<newValue.size(); i++)
        {
             Object obj = newValue.get(i);
             //System.out.println(obj);
             ontologiesToSearchOn.add((String) obj);
        }
    }

    public void ontologiesToSearchOnChanged(ValueChangeEvent event) {
         if (event.getNewValue() == null) {
            return;
         }

         List newValue = (List)event.getNewValue();
         setOntologiesToSearchOn(newValue);
    }


    public List<SelectItem> ontologySelectionList = null;
    public String ontologyToSearchOn = null;

    public List getOntologySelectionList() {
        if (ontologySelectionList != null) return ontologySelectionList;
        List ontologies = getOntologyList();
        ontologySelectionList = new ArrayList<SelectItem>();
        String label = "Switch to another vocabulary (select one)";
        ontologySelectionList.add(new SelectItem(label, label));
        for (int i=0; i<ontologies.size(); i++)
        {
            SelectItem item = (SelectItem) ontologyList.get(i);
            ontologySelectionList.add(item);
        }
        return ontologySelectionList;
    }


    public void ontologySelectionChanged(ValueChangeEvent event) {

        if (event.getNewValue() == null) {
            //System.out.println("ontologySelectionChanged; event.getNewValue() == null ");
            return;
        }
        String newValue = (String) event.getNewValue();

        HttpServletResponse response = (HttpServletResponse) FacesContext
                .getCurrentInstance().getExternalContext().getResponse();

        String targetURL = null;//"http://nciterms.nci.nih.gov/";
        //if (selectedQuickLink.compareTo("NCI Terminology Browser") == 0) {
            targetURL = "http://nciterms.nci.nih.gov/";
        //}
        try {
            response.sendRedirect(response.encodeRedirectURL(targetURL));
        } catch (Exception ex) {
            ex.printStackTrace();
            // send error message
        }
    }

    public String getOntologyToSearchOn() {
        if (ontologySelectionList == null) {
            ontologySelectionList = getOntologySelectionList();
            SelectItem item = (SelectItem) ontologyList.get(1);
            ontologyToSearchOn = item.getLabel();
        }
        return ontologyToSearchOn;
    }

    public void setOntologyToSearchOn(String newValue) {
        ontologyToSearchOn = newValue;
    }


////////////////////////////////////////////////////////////////////////////////////////

/*
    public String multipleSearchAction() {

System.out.println( "************* multipleSearchAction *****************");

        String scheme = CODING_SCHEME_NAME;
        String version = null;
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance().getExternalContext().getRequest();
        request.getSession().setAttribute("contains_warning_msg", "");
        String matchText = (String) request.getParameter("matchText");

        Vector schemes = new Vector();
        Vector versions = new Vector();


        String[] ontology_list = request.getParameterValues("ontology_list");
        if (ontology_list == null) {
            String message = "UserSessionBean multipleSearchAction: ontology_list == null???";
            request.getSession().setAttribute("message", message);
            return "message";
        }

        List list = new ArrayList<String>();
        ontologiesToSearchOn = new ArrayList<String>();
        for (int i = 0; i < ontology_list.length; ++i) {
            System.out.println("( " + i + ")" + ontology_list[i]);
            list.add(ontology_list[i]);
            ontologiesToSearchOn.add(ontology_list[i]);
        }

        //List list = ontologiesToSearchOn;
String t = "";
        if (ontologiesToSearchOn.size() == 0) {
            String message = "Please select at least one vocabulary.";
            request.getSession().setAttribute("message", message);
            return "message";

        } else {

            for (int k=0; k<ontologiesToSearchOn.size(); k++) {
                String key = (String) list.get(k);
                System.out.println(key);

                if (key != null) {
// to be modified
                    scheme = DataUtils.key2CodingSchemeName(key);
                    version = DataUtils.key2CodingSchemeVersion(key);

                    if (scheme != null) {
                        schemes.add(scheme);
                        // to be modified (handling of versions)
                        versions.add(version);
        System.out.println("multipleSearchAction: " + scheme + " (" + version + ")");
        t = t + scheme + " (" + version + ")" + "\n";
                    } else {
                        System.out.println("Unable to identify " + key);
                    }
                }
            }
        }

        boolean debugging = false;
        if (debugging) {
            String message = t;
            request.getSession().setAttribute("message", message);
            return "message";
        }


        if (matchText == null || matchText.length() == 0) {
            String message = "Please enter a search string.";
            request.getSession().setAttribute("message", message);
            return "message";
        }


        matchText = matchText.trim();
        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = (String) request.getParameter("algorithm");
        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
            String message = "Warning: Search algorithm parameter is not set.";
            request.getSession().setAttribute("message", message);
            return "message";
        }

        setSelectedAlgorithm(matchAlgorithm);
        //String version = null;

        String max_str = null;
        int maxToReturn = -1;//1000;
        try {
            max_str = NCItBrowserProperties
                    .getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {
            // Do nothing
        }



        Utils.StopWatch stopWatch = new Utils.StopWatch();
        Vector<org.LexGrid.concepts.Concept> v = null;
        if (schemes.size() == 1) {
            request.getSession().setAttribute("vocabulary", scheme);
            v = new SearchUtils().searchByName(scheme, version, matchText, matchAlgorithm, maxToReturn);
        } else {
            SortOption sortOption = new SortOption(SortOption.Type.ALL);
            String source = null;
            v = new SearchUtils().searchByName(schemes, versions, matchText, source, matchAlgorithm, sortOption, maxToReturn);
            System.out.println("Multiple search found " + v.size() + " matches.");
        }


        if (v != null && v.size() > 1) {
            request.getSession().setAttribute("search_results", v);
            String match_size = Integer.toString(v.size());
            request.getSession().setAttribute("match_size", match_size);
            request.getSession().setAttribute("page_string", "1");
            request.getSession().setAttribute("new_search", Boolean.TRUE);

            if (matchText.length() < 4
                    && matchAlgorithm.compareTo("contains") == 0) {
                request.getSession().setAttribute("contains_warning_msg",
                        contains_warning_msg);
            } else if (matchText.length() == 1
                    && matchAlgorithm.compareTo("startsWith") == 0) {
                request.getSession().setAttribute("contains_warning_msg",
                        contains_warning_msg);
            }

            System.out.println("*** return to search_results " );

            return "search_results";
        }

        else if (v != null && v.size() == 1) {
            request.getSession().setAttribute("singleton", "true");
            request.getSession().setAttribute("dictionary", scheme);
            Concept c = (Concept) v.elementAt(0);
            request.getSession().setAttribute("code", c.getEntityCode());
            return "concept_details";
        }
        String message = "No match found.";
        request.getSession().setAttribute("message", message);
        return "message";

    }
*/

   private String[] getSelectedVocabularies(String ontology_list_str) {
       Vector v = DataUtils.parseData(ontology_list_str);
       String[] ontology_list = new String[v.size()];
       for (int i=0; i<v.size(); i++) {
           String s = (String) v.elementAt(i);
           ontology_list[i] = s;
       }
       return ontology_list;
   }


   public String multipleSearchAction() {
        String ontologiesToSearchOnStr = null;
		int knt = 0;

        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String matchText = (String) request.getParameter("matchText");

        if (matchText != null) matchText = matchText.trim();
        //[#19965] Error message is not displayed when Search Criteria is not proivded
        if (matchText == null || matchText.length() == 0)
        {
            String message = "Please enter a search string.";
            request.getSession().setAttribute("message", message);
            return "message";
        }
        request.getSession().setAttribute("matchText", matchText);

        String matchAlgorithm = (String) request.getParameter("algorithm");

		request.getSession().setAttribute("algorithm", matchAlgorithm);

        String searchTarget = (String) request.getParameter("searchTarget");
        request.getSession().setAttribute("searchTarget", searchTarget);
        System.out.println("searchTarget: " + searchTarget);

        if (searchTarget.compareTo("names") != 0) {
			String msg = "To be implemented.";
			request.getSession().setAttribute("message", msg);
			return "message";
		}

/*
        //setSelectedAlgorithm(matchAlgorithm);
        String matchtype = (String) request.getParameter("matchtype");
        if (matchtype == null) matchtype = "string";
*/
        //String rankingStr = (String) request.getParameter("ranking");
        //boolean ranking = rankingStr != null && rankingStr.equals("on");

        boolean ranking = true;
        //request.getSession().setAttribute("ranking", Boolean.toString(ranking));
        String source = (String) request.getParameter("source");
        if (source == null) {
            source = "ALL";
        }
        //request.getSession().setAttribute("source", source);
        //setSelectedSource(source);

        if (NCItBrowserProperties.debugOn) {
            try {
                System.out.println(Utils.SEPARATOR);
                System.out.println("* criteria: " + matchText);
                //System.out.println("* matchType: " + matchtype);
                System.out.println("* source: " + source);
                System.out.println("* ranking: " + ranking);
               // System.out.println("* sortOption: " + sortOption);
            } catch (Exception e) {
            }
        }

	    String initial_search = (String) request.getParameter("initial_search");
        String[] ontology_list = request.getParameterValues("ontology_list");

        if (initial_search != null) {
			if (ontology_list == null || ontology_list.length == 0) {
					String message = "Please select at least one vocabulary.";
					request.getSession().setAttribute("message", message);
					return "message";
			}
		}

        String ontology_list_str = null;

        LicenseBean licenseBean = null;

        if (ontology_list == null) {
            ontology_list_str = (String) request.getParameter("ontology_list_str");

            if (ontology_list_str != null) {
                ontology_list = getSelectedVocabularies(ontology_list_str);
                String scheme = (String) request.getParameter("scheme");
                if (scheme.indexOf("%20") != -1) {
                    scheme = scheme.replaceAll("%20", " ");
                }
                String version = (String) request.getParameter("version");
                if (version.indexOf("%20") != -1) {
                    version = version.replaceAll("%20", " ");
                }
                licenseBean = (LicenseBean) request.getSession().getAttribute("licenseBean");
                if (licenseBean == null) {
                    licenseBean = new LicenseBean();
                    licenseBean.addLicenseAgreement(scheme);
                    request.getSession().setAttribute("licenseBean", licenseBean);

                } else {
                    licenseBean.addLicenseAgreement(scheme);
                    request.getSession().setAttribute("licenseBean", licenseBean);
                }


                if (ontology_list.length == 0) {
					String message = "Please select at least one vocabulary.";
					request.getSession().setAttribute("message", message);
					return "message";
				}
			}

        } else {
			//KLO testing
            knt = ontology_list.length;
            //request.getSession().removeAttribute("ontology_list_str");
        }

        if (ontology_list == null) {
            ontologiesToSearchOnStr = (String) request.getSession().getAttribute("ontologiesToSearchOn");
            if (ontologiesToSearchOnStr != null) {
                Vector ontologies_to_search_on = DataUtils.parseData(ontologiesToSearchOnStr);
                ontology_list = new String[ontologies_to_search_on.size()];
                knt = ontologies_to_search_on.size();
                for (int k=0; k<ontologies_to_search_on.size(); k++) {
                    String s = (String) ontologies_to_search_on.elementAt(k);
                    ontology_list[k] = s;
                }
            }
        }

        if (knt == 0) {
            String message = "Please select at least one vocabulary.";
            request.getSession().setAttribute("message", message);
            return "message";
        }

//searchForm-termbrowser.xhtml

        Vector schemes = new Vector();
        Vector versions = new Vector();

        List list = new ArrayList<String>();
        ontologiesToSearchOn = new ArrayList<String>();

        ontologiesToSearchOnStr = "|";
        for (int i = 0; i < ontology_list.length; ++i) {
            list.add(ontology_list[i]);
            ontologiesToSearchOn.add(ontology_list[i]);
            ontologiesToSearchOnStr = ontologiesToSearchOnStr + ontology_list[i] + "|";
        }

        if (ontology_list_str == null) {
            ontology_list_str = "";
            for (int i = 0; i < ontology_list.length; ++i) {
                ontology_list_str = ontology_list_str + ontology_list[i];
                if (i < ontology_list.length-1) {
                    ontology_list_str = ontology_list_str + "|";
                }
            }
        }

        String scheme = null;
        String version = null;

        //List list = ontologiesToSearchOn;
        String t = "";
        if (ontologiesToSearchOn.size() == 0) {
            String message = "Please select at least one vocabulary.";
            request.getSession().setAttribute("message", message);
            return "message";

        } else {
            request.getSession().setAttribute("ontologiesToSearchOn", ontologiesToSearchOnStr);
            for (int k=0; k<ontologiesToSearchOn.size(); k++) {
                String key = (String) list.get(k);
                if (key != null) {
                    scheme = DataUtils.key2CodingSchemeName(key);
                    version = DataUtils.key2CodingSchemeVersion(key);
                    if (scheme != null) {
                        schemes.add(scheme);
                        // to be modified (handling of versions)
                        versions.add(version);
                        t = t + scheme + " (" + version + ")" + "\n";
                        boolean isLicensed = LicenseBean.isLicensed(scheme, version);
                        if (licenseBean == null) {
                            licenseBean = new LicenseBean();
                            request.getSession().setAttribute("licenseBean", licenseBean);
                        }
                        boolean accepted = licenseBean.licenseAgreementAccepted(scheme);
                        if (isLicensed && !accepted) {
                            request.setAttribute("matchText", matchText);
                            request.setAttribute("algorithm", matchAlgorithm);
                            request.setAttribute("ontology_list_str", ontology_list_str);
                            request.setAttribute("scheme", scheme);
                            request.setAttribute("version", version);
                            return "license";
                        }

                    } else {
                        System.out.println("Unable to identify " + key);
                    }
                }
            }
        }
        boolean debugging = false;
        if (debugging) {
            String message = t;
            request.getSession().setAttribute("message", message);
            return "message";
        }


        if (matchText == null || matchText.length() == 0) {
            String message = "Please enter a search string.";
            request.getSession().setAttribute("message", message);
            return "message";
        }


        matchText = matchText.trim();
        request.getSession().setAttribute("matchText", matchText);

        if (matchAlgorithm == null || matchAlgorithm.length() == 0) {
            String message = "Warning: Search algorithm parameter is not set.";
            request.getSession().setAttribute("message", message);
            return "message";
        }

        //setSelectedAlgorithm(matchAlgorithm);
        //String version = null;

        String max_str = null;
        int maxToReturn = -1;//1000;
        try {
            max_str = NCItBrowserProperties
                    .getProperty(NCItBrowserProperties.MAXIMUM_RETURN);
            maxToReturn = Integer.parseInt(max_str);
        } catch (Exception ex) {
            // Do nothing
        }

        //v = new SearchUtils().searchByName(scheme, version, matchText, source, matchAlgorithm, sortOption, maxToReturn);
        ResolvedConceptReferencesIterator iterator = new SearchUtils().searchByName(schemes, versions, matchText, source, matchAlgorithm, ranking, maxToReturn);

        request.getSession().setAttribute("vocabulary", scheme);

        request.getSession().removeAttribute("neighborhood_synonyms");
        request.getSession().removeAttribute("neighborhood_atoms");
        request.getSession().removeAttribute("concept");
        request.getSession().removeAttribute("code");
        request.getSession().removeAttribute("codeInNCI");
        request.getSession().removeAttribute("AssociationTargetHashMap");
        request.getSession().removeAttribute("type");

        //if (v != null && v.size() > 1)
        if (iterator != null) {
            IteratorBean iteratorBean = (IteratorBean) FacesContext.getCurrentInstance().getExternalContext()
                .getSessionMap().get("iteratorBean");

            if (iteratorBean == null) {
                iteratorBean = new IteratorBean(iterator);

                FacesContext.getCurrentInstance().getExternalContext()
                   .getSessionMap().put("iteratorBean", iteratorBean);
            } else {
                iteratorBean.setIterator(iterator);
            }

            int size = iteratorBean.getSize();
            if (size > 1) {
                String match_size = Integer.toString(size);;//Integer.toString(v.size());
                request.getSession().setAttribute("match_size", match_size);
                request.getSession().setAttribute("page_string", "1");
                request.getSession().setAttribute("new_search", Boolean.TRUE);
                //route to multiple_search_results.jsp
                return "search_results";
            } else if (size == 1) {
                //Concept c = (Concept) v.elementAt(0);
                int pageNumber = 1;
                list = iteratorBean.getData(1);
                ResolvedConceptReference ref = (ResolvedConceptReference) list.get(0);

                String coding_scheme = ref.getCodingSchemeName();
                request.getSession().setAttribute("singleton", "true");
                request.getSession().setAttribute("dictionary", coding_scheme);

                Concept c = null;
                if (ref == null) {
                    //System.out.println("************ ref = NULL???");
                    String msg = "Error: Null ResolvedConceptReference encountered.";
                    request.getSession().setAttribute("message", msg);
                    return "message";

                } else {
                    // to be modified
                    c = ref.getReferencedEntry();
                    if (c == null) {
                        c = DataUtils.getConceptByCode(coding_scheme, null, null, ref.getConceptCode());
                    }
                }

                request.getSession().setAttribute("code", ref.getConceptCode());
                request.getSession().setAttribute("concept", c);
                request.getSession().setAttribute("type", "properties");
                request.getSession().setAttribute("new_search", Boolean.TRUE);

                request.setAttribute("algorithm", matchAlgorithm);

coding_scheme = (String) DataUtils.localName2FormalNameHashMap.get(coding_scheme);

                request.setAttribute("dictionary", coding_scheme);
                if (coding_scheme.compareTo("NCI Thesaurus") == 0 || coding_scheme.compareTo("NCI_Thesaurus") == 0) return "concept_details";
                return "concept_details_other_term";
            }
        }

//KLO, 093009
        if (ontologiesToSearchOn.size() == 0) {
			request.getSession().removeAttribute("vocabulary");
		} else if (ontologiesToSearchOn.size() == 1) {
			String msg_scheme = (String) ontologiesToSearchOn.get(0);
			request.getSession().setAttribute("vocabulary", msg_scheme);
		} else {
			request.getSession().removeAttribute("vocabulary");
		}

        String message = "No match found.";
        if (matchAlgorithm.compareTo("exactMatch") == 0) {
            message = "No match found. Please try 'Beings With' or 'Contains' search instead.";
        }
        request.getSession().setAttribute("message", message);
        return "message";
    }

/*
    public String acceptLicenseAgreementAction() {
       // update license agreement
       HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

       // update LicenseBean
       String scheme = (String) request.getParameter("scheme");
       String version = (String) request.getParameter("version");

       LicenseBean licenseBean = (LicenseBean) request.getSession().getAttribute("licenseBean");

       if (licenseBean == null) {
           licenseBean = new LicenseBean();
           licenseBean.addLicenseAgreement(scheme);
           request.getSession().setAttribute("licenseBean", licenseBean);

       } else {
           licenseBean.addLicenseAgreement(scheme);
           request.getSession().setAttribute("licenseBean", licenseBean);
       }
       return multipleSearchAction();
    }
*/

    public String acceptLicenseAgreement() {
        HttpServletRequest request = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

        // update LicenseBean
        String scheme = (String) request.getParameter("scheme");
        String version = (String) request.getParameter("version");

        LicenseBean licenseBean = (LicenseBean) request.getSession().getAttribute("licenseBean");

        if (licenseBean == null) {
            licenseBean = new LicenseBean();
            licenseBean.addLicenseAgreement(scheme);
            request.getSession().setAttribute("licenseBean", licenseBean);

        } else {
            licenseBean.addLicenseAgreement(scheme);
            request.getSession().setAttribute("licenseBean", licenseBean);
        }

        request.getSession().setAttribute("scheme", scheme);
        request.getSession().setAttribute("version", version);
        return "vocabulary_home";

    }





}
