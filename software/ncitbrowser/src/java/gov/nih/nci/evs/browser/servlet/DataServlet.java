package gov.nih.nci.evs.browser.servlet;

import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.common.*;

import java.io.*;
import java.util.*;
import java.text.*;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;

import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;

import org.lexevs.tree.json.JsonConverter;
import org.lexevs.tree.json.JsonConverterFactory;
import org.lexevs.tree.model.LexEvsTree;
import org.lexevs.tree.model.LexEvsTreeNode;
import org.lexevs.tree.model.LexEvsTreeNode.ExpandableStatus;
import org.lexevs.tree.service.TreeService;
import org.lexevs.tree.service.TreeServiceFactory;

import org.lexevs.tree.dao.iterator.ChildTreeNodeIterator;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;

import gov.nih.nci.evs.browser.utils.*;

import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;

import org.LexGrid.concepts.Entity;
import org.LexGrid.commonTypes.Property;
import org.LexGrid.concepts.Presentation;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.commonTypes.PropertyQualifier;

import org.LexGrid.concepts.Definition;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.json.simple.JSONValue;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.*;

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class DataServlet.
 */

public final class DataServlet extends HttpServlet {
    private static Logger _logger = Logger.getLogger(DataServlet.class);
	public static final String SOURCE = "source";
	public static final String SOURCE_CODE = "source_code";
	public static final String SOURCE_CONCEPT_NAME = "source_concept_name";
	public static final String TARGET = "target";
	public static final String TARGET_CODE = "target_code";
	public static final String TARGET_CONCEPT_NAME = "target_concept_name";
	public static final String DEFINITION = "definition";


    /**
     * local constants
     */
    private static final long serialVersionUID = 2L;
    //private static final int  STANDARD_VIEW = 1;
    //private static final int  TERMINOLOGY_VIEW = 2;

    /**
     * Validates the Init and Context parameters, configures authentication URL
     *
     * @throws ServletException if the init parameters are invalid or any other
     *         problems occur during initialisation
     */
    public void init() throws ServletException {

    }

    /**
     * Route the user to the execute method
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet exception occurs
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        execute(request, response);
    }

    /**
     * Route the user to the execute method
     *
     * @param request The HTTP request we are processing
     * @param response The HTTP response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a Servlet exception occurs
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        execute(request, response);
    }

    public void execute(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String action = HTTPUtils.cleanXSS(request.getParameter("action"));//
        if (action.compareTo("ncidef") == 0) {
			try {
				String source = HTTPUtils.cleanXSS(request.getParameter("source"));
				String code = HTTPUtils.cleanXSS(request.getParameter("code"));
                JSONObject obj = getJSONObjectForNCIDefinition(source, code);

				StringWriter out = new StringWriter();
				String jsonText = "No NCI definition available.";
				if (obj == null) {
	                response.getWriter().write(jsonText);
				} else {
					obj.writeJSONString(out);
				    jsonText = out.toString();
				    response.getWriter().write(jsonText);
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

    public Vector getMatchedMetathesaurusCUIs(LexBIGService lbSvc, String scheme, String version,
        String ltag, String code) {
		ConceptDetails conceptDetails = new ConceptDetails(lbSvc);
        Entity c = conceptDetails.getConceptByCode(scheme, version, code);
        if (c != null) {
            Vector v = conceptDetails.getConceptPropertyValues(c, "NCI_META_CUI");
            if (v == null || v.size() == 0) {
				return conceptDetails.getConceptPropertyValues(c, "UMLS_CUI");
			}        }
        return null;
    }



    public JSONObject nciDefinition2JSONObject(String src_abbrev, String src_code, String nci_code, String nci_concept_name, String def) {
		JSONObject obj = null;
		try {
			obj=new JSONObject();
			obj.put(SOURCE, src_abbrev);
			obj.put(TARGET, "NCI");
			obj.put(SOURCE_CODE, src_code);
			obj.put(TARGET_CODE, nci_code);
			obj.put(TARGET_CONCEPT_NAME, nci_concept_name);
			obj.put(DEFINITION, def);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
		return obj;
	}

	public JSONObject getJSONObjectForNCIDefinition(String src_abbrev, String src_code) { //"10009851"
	    String nci_code = null;
	    String nci_def = null;
	    String target_concept_name = "No match";
		try {
			LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
            Vector cui_vec = getMatchedMetathesaurusCUIs(lbSvc, src_abbrev, null, null, src_code);
            String cui = null;
            for (int i=0; i<cui_vec.size(); i++) {
				cui = (String) cui_vec.elementAt(i);
			}
			if (cui == null) return null;

			ConceptDetails conceptDetails = new ConceptDetails(lbSvc);
			Entity ncim_entity = conceptDetails.getConceptByCode(Constants.NCI_METATHESAURUS, null, cui, null, false);
			String syn_1 = null;
			String syn_2 = null;

			String code_1 = null;
			String code_2 = null;

			if (ncim_entity != null) {
				org.LexGrid.commonTypes.Property[] properties = ncim_entity.getPresentation();
				for (int i = 0; i < properties.length; i++) {
					Property p = (Property) properties[i];
					String t = p.getValue().getContent();
					Source[] sources = p.getSource();
					if (sources != null && sources.length > 0) {

						Source src = sources[0];
						String src_abbr = src.getContent();
						if (src_abbr.compareTo(src_abbrev) == 0) {
							syn_1 = p.getValue().getContent();
						} else if (src_abbr.compareTo("NCI") == 0) {
							syn_2 = p.getValue().getContent();
						}

						PropertyQualifier[] qualifiers = p.getPropertyQualifier();
						if (qualifiers != null && qualifiers.length > 0) {
							for (int j = 0; j < qualifiers.length; j++) {
								PropertyQualifier q = qualifiers[j];
								String qualifier_name = q.getPropertyQualifierName();
								String qualifier_value = q.getValue().getContent();
								if (qualifier_name.compareTo("source-code") == 0) {
									if (src_abbr.compareTo(src_abbrev) == 0) {
										 code_1 = qualifier_value;
									} else if (src_abbr.compareTo("NCI") == 0) {
										 code_2 = qualifier_value;
									}
								}
							}
						}
					}
				}
			}
			if (syn_1 != null && syn_2 != null) {
				nci_code = code_2;

				Entity ncit_entity = conceptDetails.getConceptByCode(Constants.NCIT_CS_NAME, null, code_2, null, false);
				if (ncit_entity != null) {
					target_concept_name = ncit_entity.getEntityDescription().getContent();
					//System.out.println(ncit_entity.getEntityDescription().getContent() + " (" + ncit_entity.getEntityCode() + ")");
					org.LexGrid.concepts.Definition[] properties = ncit_entity.getDefinition();
					for (int i = 0; i < properties.length; i++) {
						Definition p = (Definition) properties[i];
						String t = p.getValue().getContent();
						Source[] sources = p.getSource();
						if (sources != null && sources.length > 0) {
							Source src = sources[0];
							String src_abbr = src.getContent();
							if (src_abbr.compareTo("NCI") == 0) {
								//System.out.println(p.getPropertyName() + " " + p.getValue().getContent() + " " + src_abbr);
								nci_def = p.getValue().getContent();
							}
						}
					}
				}
				if (nci_def == null) {
					nci_def = "Not available.";
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
		if (nci_code != null) {
			return nciDefinition2JSONObject(src_abbrev, src_code, nci_code, target_concept_name, nci_def);
		} else {
			return nciDefinition2JSONObject(src_abbrev, src_code, "No match", target_concept_name, nci_def);
		}
	}
}