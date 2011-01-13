package gov.nih.nci.evs.browser.bean;


import java.util.*;
import java.net.URI;

import javax.faces.context.*;
import javax.faces.event.*;
import javax.faces.model.*;
import javax.servlet.http.*;

import gov.nih.nci.evs.browser.utils.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Impl.Extensions.GenericExtensions.*;
import org.apache.log4j.*;
import javax.faces.event.ValueChangeEvent;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.lexgrid.valuesets.dto.ResolvedValueSetDefinition;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;


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

public class ValueSetBean {
    private static Logger _logger = Logger.getLogger(ValueSetBean.class);
    private static List _rela_list = null;
    private static List _association_name_list = null;
    private static List _property_name_list = null;
    private static List _property_type_list = null;
    private static List _source_list = null;

    private static Vector _value_set_uri_vec = null;
    private static Vector _coding_scheme_vec = null;
    private static Vector _concept_domain_vec = null;



    public void ValueSetBean() {

	}


	private String selectedConceptDomain = null;
	private List conceptDomainList = null;
	private Vector<String> conceptDomainListData = null;


	public String getSelectedConceptDomain() {
		return this.selectedConceptDomain;
	}

	public void setSelectedConceptDomain(String selectedConceptDomain) {
		this.selectedConceptDomain = selectedConceptDomain;
	}

	public List getConceptDomainList() {
		if (conceptDomainList != null) return conceptDomainList;
		conceptDomainListData = DataUtils.getConceptDomainNames();
		conceptDomainList = new ArrayList();
		for (int i=0; i<conceptDomainListData.size(); i++) {
			String t = (String) conceptDomainListData.elementAt(i);
			conceptDomainList.add(new SelectItem(t));
		}

		if (conceptDomainList.size() > 0) {
			setSelectedConceptDomain(((SelectItem) conceptDomainList.get(0)).getLabel());
		}
		return conceptDomainList;
	}




	private String selectedOntology = null;
	private List ontologyList = null;
	private Vector<String> ontologyListData = null;


	public String getSelectedOntology() {
		return this.selectedOntology;
	}

	public void setSelectedOntology(String selectedOntology) {
		this.selectedOntology = selectedOntology;
	}

	public List getOntologyList() {
		if (ontologyList != null) return ontologyList;
		ontologyListData = DataUtils.getCodingSchemeFormalNames();

		ontologyList = new ArrayList();
		for (int i=0; i<ontologyListData.size(); i++) {
			String t = (String) ontologyListData.elementAt(i);
			ontologyList.add(new SelectItem(t));
		}

		if (ontologyList.size() > 0) {
			setSelectedOntology(((SelectItem) ontologyList.get(0)).getLabel());
		}
		return ontologyList;
	}



	private String selectedValueSetURI = null;
	private List valueSetURIList = null;
	private Vector<String> valueSetURIListData = null;


	public String getSelectedValueSetURI() {
		return this.selectedValueSetURI;
	}

	public void setSelectedValueSetURI(String selectedValueSetURI) {
		this.selectedValueSetURI = selectedValueSetURI;
		System.out.println("setSelectedValueSetURI: " + selectedValueSetURI);
	}

	public List getValueSetURIList() {
		if (valueSetURIList != null) return valueSetURIList;
		valueSetURIListData = DataUtils.getValueSetURIs();

		valueSetURIList = new ArrayList();
		for (int i=0; i<valueSetURIListData.size(); i++) {
			String t = (String) valueSetURIListData.elementAt(i);
			valueSetURIList.add(new SelectItem(t));
		}

		if (valueSetURIList.size() > 0) {
			setSelectedValueSetURI(((SelectItem) valueSetURIList.get(0)).getLabel());
		}
		return valueSetURIList;
	}

    public void valueSetURIChangedEvent(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();
        setSelectedValueSetURI(newValue);
	}

    public void ontologyChangedEvent(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();
        setSelectedOntology(newValue);
	}

    public void conceptDomainChangedEvent(ValueChangeEvent event) {
        if (event.getNewValue() == null)
            return;
        String newValue = (String) event.getNewValue();
        setSelectedConceptDomain(newValue);
	}

/*
    public String getValueSetDefinitionMetadata(ValueSetDefinition vsd) {
		if (vsd== null) return null;
		String uri = "";
		String description = "";
		String domain = "";
		String src_str = "";

		uri = vsd.getValueSetDefinitionURI();
		description = vsd.getValueSetDefinitionName();
		domain = vsd.getConceptDomain();

		java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

		while (sourceEnum.hasMoreElements()) {
			Source src = (Source) sourceEnum.nextElement();
			src_str = src_str + src.getContent() + ";";
		}
		if (src_str.length() > 0) {
			src_str = src_str.substring(0, src_str.length()-1);
		}

		if (vsd.getEntityDescription() != null) {
			description = vsd.getEntityDescription().getContent();
		}

		return uri + "|" + description + "|" + domain + "|" + src_str;
	}
*/



    public String valueSetSearchAction() {
		java.lang.String valueSetDefinitionRevisionId = null;
		String msg = null;

        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String selectValueSetSearchOption = (String) request.getParameter("selectValueSetSearchOption");

        String selectURI = (String) request.getParameter("selectedValueSetURI");
        if (selectURI == null) {
			selectURI = getSelectedValueSetURI();
		}
		System.out.println("(*) valueSetSearchAction selectURI: " + selectURI);

        String selectCodingScheme = getSelectedOntology(); //(String) request.getParameter("selectedOntology");
        String selectConceptDomain = getSelectedConceptDomain(); //(String) request.getParameter("selectConceptDomain");

        LexEVSValueSetDefinitionServices vsd_service = null;
        Vector v = new Vector();
		if (selectValueSetSearchOption.compareTo("URI") == 0) {
			vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			System.out.println("valueSetSearchAction selectURI: " + selectURI);

			if (selectURI != null) {

				try {
					ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(selectURI), valueSetDefinitionRevisionId);
					String metadata = DataUtils.getValueSetDefinitionMetadata(vsd);
					if (metadata != null) {
						v.add(metadata);
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					msg = "Unable to find any value set with URI " + selectURI;
					request.getSession().setAttribute("message", msg);
					return "message";
				}
		    }
			request.getSession().setAttribute("matched_vsds", v);
			return "value_set";
		}

		else if (selectValueSetSearchOption.compareTo("CodingScheme") == 0) {
			vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			System.out.println("valueSetSearchAction selectCodingScheme: " + selectCodingScheme);

			if (selectCodingScheme != null) {

				try {
                    List uri_list = vsd_service.getValueSetDefinitionURIsWithCodingScheme(selectCodingScheme,
                                                                           DataUtils.codingSchemeName2URI(selectCodingScheme));
					for (int i=0; i<uri_list.size(); i++) {
						String uri = (String) uri_list.get(i);
						ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
						String metadata = DataUtils.getValueSetDefinitionMetadata(vsd);
						if (metadata != null) {
							v.add(metadata);
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					msg = "Unable to find any value set with coding scheme " + selectCodingScheme;
					request.getSession().setAttribute("message", msg);
					return "message";
				}
		    }
			request.getSession().setAttribute("matched_vsds", v);
			return "value_set";
		}

		else if (selectValueSetSearchOption.compareTo("ConceptDomain") == 0) {
			vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
			System.out.println("valueSetSearchAction selectConceptDomain: " + selectConceptDomain);

			if (selectConceptDomain != null) {

				try {
                    List uri_list = vsd_service.getValueSetDefinitionURIsWithConceptDomain(selectConceptDomain,
                                                                           "http://lexevs.org/codingscheme/conceptdomain");
					for (int i=0; i<uri_list.size(); i++) {
						String uri = (String) uri_list.get(i);
						ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
						String metadata = DataUtils.getValueSetDefinitionMetadata(vsd);
						if (metadata != null) {
							v.add(metadata);
						}
					}

				} catch (Exception ex) {
					ex.printStackTrace();
					msg = "Unable to find any value set with concept domain " + selectConceptDomain;
					request.getSession().setAttribute("message", msg);
					return "message";
				}
		    }
			request.getSession().setAttribute("matched_vsds", v);
			return "value_set";
		}
		msg = "Unexpected error encountered.";
		request.getSession().setAttribute("message", msg);
		request.getSession().setAttribute("selectValueSetSearchOption",  selectValueSetSearchOption);
        return "message";
	}


    public String resolveValueSetAction() {

 System.out.println("(************* ) resolveValueSetAction ");


        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String selectedvalueset = (String) request.getParameter("valueset");


        System.out.println("resolveValueSetAction: selected value set " + selectedvalueset);
		request.getSession().setAttribute("selectedvalueset", selectedvalueset);
        return "resolve_value_set";

	}


    public String continueResolveValueSetAction() {

 System.out.println("(************* ) continueResolveValueSetAction ");


        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();

        String[] coding_scheme_ref = (String[]) request.getParameterValues("coding_scheme_ref");


        String vsd_uri = (String) request.getParameter("vsd_uri");
        if (vsd_uri == null) {
			vsd_uri = (String) request.getSession().getAttribute("vsd_uri");
		}

        request.getSession().setAttribute("vsd_uri", vsd_uri);


 System.out.println("(*) continueResolveValueSetAction " + vsd_uri);


        if (coding_scheme_ref == null || coding_scheme_ref.length == 0) {
			String msg = "No coding scheme reference is selected.";
			request.getSession().setAttribute("message", msg);
			return "resolve_value_set";
		}


 System.out.println("(*) continueResolveValueSetAction #1 ");

		AbsoluteCodingSchemeVersionReferenceList csvList = new AbsoluteCodingSchemeVersionReferenceList();
		/*
        for (int i=0; i<coding_scheme_ref.length; i++) {
			String t = coding_scheme_ref[i];
			System.out.println("(*) coding_scheme_ref: " + t);
			Vector u = DataUtils.parseData(t);
			String uri = (String) u.elementAt(0);
			String version = (String) u.elementAt(1);
			if (version == null || version.compareTo("null") == 0) {
				//version = DataUtils.getVocabularyVersionByTag(uri, "PRODUCTION");
				version = "1.0";
			}

            csvList.addAbsoluteCodingSchemeVersionReference(Constructors.createAbsoluteCodingSchemeVersionReference(uri, version));
		}
		*/


		//csvList.addAbsoluteCodingSchemeVersionReference(Constructors.createAbsoluteCodingSchemeVersionReference("urn:oid:11.11.1.1", "1.0"));
		csvList.addAbsoluteCodingSchemeVersionReference(Constructors.createAbsoluteCodingSchemeVersionReference("Automobiles", "1.0"));


System.out.println("(*) continueResolveValueSetAction #2 ");

        long time = System.currentTimeMillis();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
		ResolvedValueSetDefinition rvsd = null;
		try {
			ValueSetDefinition vsd = DataUtils.findValueSetDefinitionByURI(vsd_uri);
			rvsd = vsd_service.resolveValueSetDefinition(vsd, csvList, null, null);
			if(rvsd != null) {
				ResolvedConceptReferencesIterator itr = rvsd.getResolvedConceptReferenceIterator();
				request.getSession().setAttribute("ResolvedConceptReferencesIterator", itr);
				return "resolved_value_set";
		    }

		} catch (Exception ex) {
			System.out.println("??? vds.resolveValueSetDefinition throws exception");
		}

System.out.println("(*) continueResolveValueSetAction #3 ");


		String msg = "Unable to resolve the value set " + vsd_uri;
		request.getSession().setAttribute("message", msg);
        return "resolved_value_set";
	}


    public String exportValueSetAction() {
        HttpServletRequest request =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                .getExternalContext().getRequest();


        return "exported_value_set";

	}


    public String exportToXMLAction() {
		return "xml";
	}


    public String exportToCSVAction() {
		return "csv";
	}

}
