<%@ page import="java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils"%>
<%@ page import="gov.nih.nci.evs.browser.utils.HTTPUtils"%>

<%@ page import="gov.nih.nci.evs.browser.utils.ValueSetHierarchy"%>

<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>
<%@ page import="gov.nih.nci.evs.browser.common.Constants" %>
<%@ page import="org.LexGrid.valueSets.ValueSetDefinition" %>

<%
String context_path = request.getContextPath();
String redirect_url = null;
String source_scheme = Constants.TERMINOLOGY_VALUE_SET_NAME;
String source_version = DataUtils.getValueSetHierarchy().getSourceSchemeVersion();

HashMap hmap = DataUtils.getValueSetHierarchy().getValueSetSourceHierarchy();
HashMap VSDURI2VSD_map = DataUtils.getValueSetHierarchy().getValueSetDefinitionURI2VSD_map();

String ontology_node_id = HTTPUtils.cleanXSS((String) request.getParameter("ontology_node_id"));

ontology_node_id = DataUtils.getValueSetHierarchy().getValueSetURI(ontology_node_id);


ValueSetDefinition vsd = DataUtils.getValueSetHierarchy().findValueSetDefinitionByURI(ontology_node_id);


if (vsd != null) {
    redirect_url = context_path + "/ajax?action=create_src_vs_tree&vsd_uri=" + ontology_node_id;

    
} else {
    if (DataUtils.getValueSetHierarchy().get_valueSetDefinitionSourceCode2Name_map().containsKey(ontology_node_id)) {
    /*
        redirect_url = context_path + "/pages/concept_details.jsf?dictionary="
            + source_scheme
            + "&version="
            + source_version
            + "&code=" 
            + ontology_node_id;
     */
           
        redirect_url = context_path + "/ajax?action=create_src_vs_tree&vsd_uri=" + ontology_node_id;

            
    } else {
        String formalname = DataUtils.getFormalName(ontology_node_id);
        String version = DataUtils.getVocabularyVersionByTag(formalname, "PRODUCTION");
        formalname = formalname.replaceAll(" ", "%20");
        redirect_url = context_path + "/pages/vocabulary.jsf?dictionary=" 
            + HTTPUtils.cleanXSS(formalname)
            + "&version="
            + version;
    }
    


}

String url = response.encodeRedirectURL(redirect_url);
response.sendRedirect(url);

%>     
