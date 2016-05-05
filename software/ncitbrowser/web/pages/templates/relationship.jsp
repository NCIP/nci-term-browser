<%@ page import="java.util.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.LexGrid.concepts.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.RemoteServerUtil" %>
<%@ page import="org.LexGrid.LexBIG.LexBIGService.LexBIGService"%>


<%
  if (type.compareTo("relationship") == 0 || type.compareTo("all") == 0)
  {


JSPUtils.JSPHeaderInfo relationship_info = new JSPUtils.JSPHeaderInfo(request);
Entity concept_curr = (Entity) request.getSession().getAttribute("concept");
String scheme_curr = relationship_info.dictionary;
String version_curr = relationship_info.version;
String version_parameter = "";
String cNamespace = null;

boolean owl2_display = false;

LexBIGService lb_svc = RemoteServerUtil.createLexBIGService();
MappingSearchUtils mappingSearchUtils = new MappingSearchUtils(lb_svc);
RelationshipUtils relationshipUtils = new RelationshipUtils(lb_svc);


if (version_curr != null && ! version_curr.equalsIgnoreCase("null")) {
    version_parameter = "&version=" + version_curr;
}

boolean isMapping = DataUtils.isMapping(scheme_curr, version_curr);
String code_curr = (String) request.getSession().getAttribute("code");
String ns_curr = (String) request.getSession().getAttribute("ns");

    String rel_display_name = DataUtils.getMetadataValue(scheme_curr, version_curr, "display_name");
    if (rel_display_name == null) rel_display_name = DataUtils.getLocalName(scheme_curr);

    if (code_curr == null) {
        code_curr = HTTPUtils.cleanXSS((String) request.getParameter("code"));
        ns_curr = HTTPUtils.cleanXSS((String) request.getParameter("ns"));
    }
    
// Relationship tab failed to render correctly.    
if (ns_curr == null || ns_curr.compareTo("null") == 0 || ns_curr.compareTo("undefined") == 0) {
    ns_curr = new ConceptDetails(lb_svc).getNamespaceByCode(scheme_curr, version_curr, code_curr); 
}
    
    String key = scheme_curr + "$" + version_curr + "$" + code_curr;
    if (!DataUtils.isNullOrBlank(ns_curr)) {
        key = key + "$" + ns_curr;
    }
    
    HashMap hmap = null;
    String rel_key = (String) request.getSession().getAttribute("rel_key");
    if (rel_key == null || rel_key.compareTo(key) != 0) {
	    if (isMapping) {
		    hmap = mappingSearchUtils.getMappingRelationshipHashMap(scheme_curr, version_curr, code_curr);
	    } else {
		if (DataUtils.isNullOrBlank(ns_curr)) {
		    ns_curr = DataUtils.getNamespaceByCode(scheme_curr, version_curr, code_curr);
		}
		hmap = relationshipUtils.getRelationshipHashMap(scheme_curr, version_curr, code_curr, ns_curr, true);
	    }
	    request.getSession().setAttribute("RelationshipHashMap", hmap);
	    request.getSession().setAttribute("rel_key", rel_key);
    } else {
        hmap = (HashMap) request.getSession().getAttribute("RelationshipHashMap");
    }

    //if (hmap != null) {

    request.getSession().setAttribute("RelationshipHashMap", hmap);
        
    ArrayList superconcepts = (ArrayList) hmap.get(Constants.TYPE_SUPERCONCEPT);
    ArrayList subconcepts = (ArrayList) hmap.get(Constants.TYPE_SUBCONCEPT);
    ArrayList roles = (ArrayList) hmap.get(Constants.TYPE_ROLE);
    ArrayList associations = (ArrayList) hmap.get(Constants.TYPE_ASSOCIATION);
    ArrayList inverse_roles = (ArrayList) hmap.get(Constants.TYPE_INVERSE_ROLE);
    ArrayList inverse_associations = (ArrayList) hmap.get(Constants.TYPE_INVERSE_ASSOCIATION);

    ArrayList concepts = null;
    String label = "";
    String rel = "";
    String score = "";
    String scheme_curr_0 = scheme_curr;
    String scheme_curr_nm = DataUtils.getCSName(scheme_curr);
    //scheme_curr = scheme_curr.replaceAll(" ", "%20");

    String associationName = "subClassOf";
    boolean direction = true;
    ArrayList arrayList = null;
    String parent_table_str = null;
    String child_table_str = null;
        
%>
  <table class="datatable_960" border="0" width="100%">
    <tr>
      <td class="textsubtitle-blue" align="left">
      
      
<%			
if (type != null && type.compareTo("all") == 0) {
%>
    <A name="relationships">Relationships with other <%=DataUtils.encodeTerm(rel_display_name)%> Concepts</A>
<%    
} else {
%>
    Relationships with other <%=rel_display_name%> Concepts
<%    
}
%>     
      </td>
    </tr>
  </table>
<%
 if (!isMapping) {

    propertyData.setRelationshipHashMap(hmap);
    if (owl2_display) {
    arrayList = relationshipUtils.getRelationshipData(scheme_curr, version_curr, ns_curr, code_curr, associationName, direction);
    }
    if (owl2_display && arrayList != null) {    
         parent_table_str = propertyData.generateRelationshipTable(scheme_curr, version_curr, code_curr, ns_curr, Constants.TYPE_SUPERCONCEPT, true, arrayList);
	    %>
	    <p>
	    <%=parent_table_str%>
	    </p> 
    <%	    
    } else {
    %>
   <p>
    <%
      label = "Parent Concepts:";
      concepts = superconcepts;
      
      
      if (concepts == null || concepts.size() <= 0)
      {
    %>
        <b><%=label%></b> <i>(none)</i>
    <%
      } else if (concepts != null && concepts.size() == 1) {
          String s = (String) concepts.get(0);
          Vector ret_vec = StringUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          cNamespace = null;
          
          if (ret_vec.size() > 2) {
              cNamespace = (String) ret_vec.elementAt(2);
          }
          
    %>
          <b><%=label%></b>
          <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&ns=<%=cNamespace%>&code=<%=cCode%>">
            <%=cName%>
          </a>
    <%
      } else if (concepts != null) {
    %>
        <b><%=label%></b>
        <table class="datatable_960" border="0" width="100%">
    <%
        for (int i=0; i<concepts.size(); i++) {
          String s = (String) concepts.get(i);
          Vector ret_vec = StringUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          
          if (ret_vec.size() > 2) {
	       cNamespace = (String) ret_vec.elementAt(2);
	  }
          
          String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
         
    %>
          <tr class="<%=rowColor%>">
            <td class="dataCellText">
              <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&ns=<%=cNamespace%>&code=<%=cCode%>">
                <%=cName%>
              </a>
            </td>
          </tr>
    <%
        }
    %>
        </table>
    <%
      }
   }
    %>
  </p>
   <%
    propertyData.setRelationshipHashMap(hmap);
    direction = false;
    if (owl2_display) {
    arrayList = relationshipUtils.getRelationshipData(scheme_curr, version_curr, ns_curr, code_curr, associationName, direction);
    }
    if (owl2_display && arrayList != null) {    
         child_table_str = propertyData.generateRelationshipTable(scheme_curr, version_curr, code_curr, ns_curr, Constants.TYPE_SUBCONCEPT, true, arrayList);
	    %>
	    <p>
	    <%=child_table_str%>
	    </p> 
    <%	    
    } else {
    %>
  <p>
    <%
      label = "Child Concepts:";
      concepts = subconcepts;
      if (concepts == null || concepts.size() <= 0)
      {
    %>
        <b><%=label%></b> <i>(none)</i>
    <%
      } else if (concepts != null && concepts.size() == 1) {
          String s = (String) concepts.get(0);
          Vector ret_vec = StringUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          if (ret_vec.size() > 2) {
              cNamespace = (String) ret_vec.elementAt(2);
          }    
          %>
          <b><%=label%></b>
          <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&ns=<%=cNamespace%>&code=<%=cCode%>">
            <%=cName%>
          </a>
    <%
      } else if (concepts != null) {
    %>
        <b><%=label%></b>
        <table class="datatable_960" border="0" width="100%">
    <%
        for (int i=0; i<concepts.size(); i++) {
          String s = (String) concepts.get(i);
          Vector ret_vec = StringUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          if (ret_vec.size() > 2) {
          	cNamespace = (String) ret_vec.elementAt(2);
          }
          String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
    %>
          <tr class="<%=rowColor%>">
            <td class="dataCellText">
              <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&ns=<%=cNamespace%>&code=<%=cCode%>">
                <%=cName%>
              </a>
            </td>
          </tr>
    <%
        }
    %>
        </table>
    <%
      }
    }
    %>
    </p>
    <%
    propertyData.setRelationshipHashMap(hmap);
    String role_table_str = propertyData.generateRelationshipTable(scheme_curr, version_curr, code, cNamespace, Constants.TYPE_ROLE, true);
    %>
    <p>
    <%=role_table_str%>
    </p>

    <%
    propertyData.setRelationshipHashMap(hmap);
    String assoc_table_str = propertyData.generateRelationshipTable(scheme_curr, version_curr, code, cNamespace, Constants.TYPE_ASSOCIATION, true);
    %>
    <p>
    <%=assoc_table_str%>
    </p>
    <p>
    <%
  
     String display_inverse_relationships_metadata_value = DataUtils.getMetadataValue(scheme_curr_0, version_curr, "display_inverse_relationships");
     boolean display_inverse_relationships = true;
     if (display_inverse_relationships_metadata_value != null && display_inverse_relationships_metadata_value.compareToIgnoreCase("false") == 0) {
         display_inverse_relationships = false;
     }

     if (!isMapping) {
	    if (display_inverse_relationships) {
		    propertyData.setRelationshipHashMap(hmap);
		    String inv_role_table_str = propertyData.generateRelationshipTable(scheme_curr, version_curr, code, cNamespace, Constants.TYPE_INVERSE_ROLE, true);
		    %>
		    <p>
		    <%=inv_role_table_str%>
		    </p>
     <%
	    }
     }
    propertyData.setRelationshipHashMap(hmap);
    String inv_asso_table_str = propertyData.generateRelationshipTable(scheme_curr, version_curr, code, cNamespace, Constants.TYPE_INVERSE_ASSOCIATION, true);
    %>
    <p>
    <%=inv_asso_table_str%>
    </p>
<%
}
    if (!isMapping) {

%>
		<p>
		    <b>Mapping relationships:</b>
		<br/>
		<table class="dataTable">
			       <tr>
				 <td>
		
<a href="<%=request.getContextPath() %>/pages/concept_details.jsf?dictionary=<%=scheme_curr%>&version=<%=version_curr%>&code=<%=code_curr%>&type=mapping">see Mappings</a>
		
				 </td>
			       </tr>
			       
		</table>
<%

     } //ismapping
}
%>

