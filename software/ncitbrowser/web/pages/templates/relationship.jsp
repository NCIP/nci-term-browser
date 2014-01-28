<%@ page import="java.util.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="org.LexGrid.concepts.*" %>
<%
  if (type.compareTo("relationship") == 0 || type.compareTo("all") == 0)
  {


JSPUtils.JSPHeaderInfo relationship_info = new JSPUtils.JSPHeaderInfo(request);
Entity concept_curr = (Entity) request.getSession().getAttribute("concept");
String scheme_curr = relationship_info.dictionary;
String version_curr = relationship_info.version;
String version_parameter = "";
if (version_curr != null && ! version_curr.equalsIgnoreCase("null"))
    version_parameter = "&version=" + version_curr;


boolean isMapping = DataUtils.isMapping(scheme_curr, version_curr);
String code_curr = (String) request.getSession().getAttribute("code");

    String rel_display_name = DataUtils.getMetadataValue(scheme_curr, version_curr, "display_name");
    if (rel_display_name == null) rel_display_name = DataUtils.getLocalName(scheme_curr);

    if (code_curr == null) {
        code_curr = HTTPUtils.cleanXSS((String) request.getParameter("code"));
    }
  

    HashMap hmap = null;
    if (isMapping) {
            hmap = new MappingSearchUtils().getMappingRelationshipHashMap(scheme_curr, version_curr, code_curr);
    } else {
            hmap = new DataUtils().getRelationshipHashMap(scheme_curr, version_curr, code_curr);
/*    
	    hmap = (HashMap) request.getSession().getAttribute("RelationshipHashMap");
	    if (hmap == null) {
	      DataUtils util = new DataUtils();
     
	      hmap = util.getRelationshipHashMap(scheme_curr, version_curr, code_curr);
	    }
	    
*/	    
    }

    if (hmap != null) {

    request.getSession().setAttribute("RelationshipHashMap", hmap);
    
    

    ArrayList superconcepts = (ArrayList) hmap.get(DataUtils.TYPE_SUPERCONCEPT);
    ArrayList subconcepts = (ArrayList) hmap.get(DataUtils.TYPE_SUBCONCEPT);
    ArrayList roles = (ArrayList) hmap.get(DataUtils.TYPE_ROLE);
    ArrayList associations = (ArrayList) hmap.get(DataUtils.TYPE_ASSOCIATION);
    ArrayList inverse_roles = (ArrayList) hmap.get(DataUtils.TYPE_INVERSE_ROLE);
    ArrayList inverse_associations = (ArrayList) hmap.get(DataUtils.TYPE_INVERSE_ASSOCIATION);


    ArrayList concepts = null;
    String label = "";
    String rel = "";
    String score = "";
    String scheme_curr_0 = scheme_curr;
    scheme_curr = scheme_curr.replaceAll(" ", "%20");

%>
  <table border="0" width="708px">
    <tr>

    
    
      <td class="textsubtitle-blue" align="left">
      
      
<%			
if (type != null && type.compareTo("all") == 0) {
%>
    <A name="relationships">Relationships with other <%=rel_display_name%> Concepts</A>
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
%>

  <p>
    <%

      label = "Parent Concepts:";
      concepts = superconcepts;
      String scheme_curr_nm = DataUtils.getCSName(scheme_curr);
      
      if (concepts == null || concepts.size() <= 0)
      {
    %>
        <b><%=label%></b> <i>(none)</i>
    <%
      } else if (concepts != null && concepts.size() == 1) {
          String s = (String) concepts.get(0);
          Vector ret_vec = DataUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          
    %>
          <b><%=label%></b>
          <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&code=<%=cCode%>">
            <%=cName%>
          </a>
    <%
      } else if (concepts != null) {
    %>
        <b><%=label%></b>
        <table class="dataTable">
    <%
        for (int i=0; i<concepts.size(); i++) {
          String s = (String) concepts.get(i);
          Vector ret_vec = DataUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
         
    %>
          <tr class="<%=rowColor%>">
            <td class="dataCellText">
              <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&code=<%=cCode%>">
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
    %>
  </p>

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
          Vector ret_vec = DataUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
    %>
          <b><%=label%></b>
          <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&code=<%=cCode%>">
            <%=cName%>
          </a>
    <%
      } else if (concepts != null) {
    %>
        <b><%=label%></b>
        <table class="dataTable">
    <%
        for (int i=0; i<concepts.size(); i++) {
          String s = (String) concepts.get(i);
          Vector ret_vec = DataUtils.parseData(s, "|");
          String cName = (String) ret_vec.elementAt(0);
          String cCode = (String) ret_vec.elementAt(1);
          String rowColor = (i%2 == 0) ? "dataRowDark" : "dataRowLight";
    %>
          <tr class="<%=rowColor%>">
            <td class="dataCellText">
              <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=scheme_curr_nm%><%=version_parameter%>&code=<%=cCode%>">
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
    %>
  </p>



  <p>
  <%
     if (roles != null && roles.size() > 0)
     {
  %>
       <b><%=Constants.ROLE_LABEL%></b>&nbsp;<%=Constants.ROLE_LABEL_2%>:

  <br/>
  <i><%=Constants.ROLE_DESCRIPTION_LABEL%></i>
  <table class="dataTable">

    <%
      int n1 = 0;
      for (int i=0; i<roles.size(); i++) {
        String s = (String) roles.get(i);
        Vector ret_vec = DataUtils.parseData(s, "|");
        String role_name = (String) ret_vec.elementAt(0);
        String target_concept_name = (String) ret_vec.elementAt(1);
        String target_concept_code = (String) ret_vec.elementAt(2);
        String target_coding_scheme_name = (String) ret_vec.elementAt(3);
        
        if (target_coding_scheme_name != null) {
            target_coding_scheme_name = DataUtils.getFormalName(target_coding_scheme_name);
        }
        
        
        
        String qualifiers = null;
  rel = null;
  score = null;

        if (n1 % 2 == 0) {
          %>
            <tr class="dataRowDark">
          <%
        } else {
          %>
            <tr class="dataRowLight">
          <%
        }
        n1++;
        %>
              <td><%=role_name%></td>
              <td>
                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%><%=version_parameter%>&code=<%=target_concept_code%>">
                  <%=target_concept_name%>
                </a>
              </td>
            </tr>
      <%
      }
      %>
  </table>
</p>
<p>

  <%
     } else if (roles == null || roles.size() == 0) {
  %>
       <b><%=Constants.ROLE_LABEL%></b>&nbsp;<%=Constants.ROLE_LABEL_2%>: <i>(none)</i>
  <%
     }
  %>



<%
 }
%>


<p>
<%
  if (associations != null && associations.size() > 0) {
%>
    <b><%=Constants.ASSOCIATION_LABEL%></b>&nbsp;<%=Constants.ASSOCIATION_LABEL_2%>:
<br/>
<i><%=Constants.ASSOCIATION_DESCRIPTION_LABEL%></i>
<table class="dataTable">


  <%
   if (isMapping) {
   %>
       <th class="dataTableHeader" scope="col" align="left">Relationship</th>
       <th class="dataTableHeader" scope="col" align="left">Name</th>
       <th class="dataTableHeader" scope="col" align="left">Code</th>
       <th class="dataTableHeader" scope="col" align="left">Target</th>
       <th class="dataTableHeader" scope="col" align="left">REL</th>
       <th class="dataTableHeader" scope="col" align="left">Map Rank</th>
   <%
   }
 %>



  <%
    int n2 = 0;

    for (int i=0; i<associations.size(); i++) {
      String s = (String) associations.get(i);
     
      Vector ret_vec = DataUtils.parseData(s, "|");
      String role_name = (String) ret_vec.elementAt(0);
      String target_concept_name = (String) ret_vec.elementAt(1);
      String target_concept_code = (String) ret_vec.elementAt(2);
      String target_coding_scheme_name = (String) ret_vec.elementAt(3);

        if (target_coding_scheme_name != null) {
            target_coding_scheme_name = DataUtils.getFormalName(target_coding_scheme_name);
        }      
      
      String target_namespace = null;

  String qualifiers = null;
  rel = null;
  score = null;

  if (isMapping) {
  
  
      if (ret_vec.size() > 4) {
        qualifiers = (String) ret_vec.elementAt(4);
        Vector v = DataUtils.parseData(qualifiers, "$");
        
        for (int k=0; k<v.size(); k++) {
	  String t = (String) v.elementAt(k);
	  Vector nv_vec = DataUtils.parseData(t, ":");
	  if (nv_vec.size() > 1) {
	  
		  String qualifier_name = (String) nv_vec.elementAt(0);
		  String qualifier_value = (String) nv_vec.elementAt(1);
         
		  if (qualifier_name.compareToIgnoreCase("rel") == 0) {
		      rel = qualifier_value;
		  }
		  if (qualifier_name.compareToIgnoreCase("maprank") == 0 || qualifier_name.compareToIgnoreCase("score") == 0) {
		      score = qualifier_value;
		  }
		  
		  
	  }
        }
        
      }

      
      target_namespace = "NOT AVAILABLE";
      if(ret_vec.size() > 5) {
           target_namespace = (String) ret_vec.elementAt(5);
      }
  }

      if (n2 % 2 == 0) {
        %>
            <tr class="dataRowDark">
        <%
        } else {
        %>
            <tr class="dataRowLight">
        <%
        }
        n2++;
        %>
              <td scope="row"><%=role_name%></td>
              <td>
              <%
              if (!DataUtils.isNonConcept2ConceptAssociation(role_name)) {
              %>
                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%><%=version_parameter%>&code=<%=target_concept_code%>">
                  <%=target_concept_name%>
                </a>
              <%
              } else {
              %>
                <%=target_concept_name%>
              <%
              }
              %>
              </td>
              <%
              if (isMapping) {
              %>
              <td>
                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
                  <%=target_concept_code%>
                </a>
              </td>
              <td><%=target_namespace%></td>
              <td><%=rel%></td>
              <td><%=score%></td>
              <%
              }
              %>

            </tr>
        <%
        }
        %>
 </table>
</p>

<%
  } else if (associations == null || associations.size() == 0) {
    %>
    <b><%=Constants.ASSOCIATION_LABEL%></b>&nbsp;<%=Constants.ASSOCIATION_LABEL_2%>: <i>(none)</i>
    <%
  }
%>


  <p>
  <%
  
     String display_inverse_relationships_metadata_value = DataUtils.getMetadataValue(scheme_curr_0, version_curr, "display_inverse_relationships");
     boolean display_inverse_relationships = true;

     if (display_inverse_relationships_metadata_value != null && display_inverse_relationships_metadata_value.compareToIgnoreCase("false") == 0) {
         display_inverse_relationships = false;
     }
     


if (!isMapping) {


     if (inverse_roles != null && inverse_roles.size() > 0 && display_inverse_relationships)
     {
  %>
       <b><%=Constants.INVERSE_ROLE_LABEL%></b>&nbsp;<%=Constants.INVERSE_ROLE_LABEL_2%>:

  <br/>
  <i><%=Constants.INVERSE_ROLE_DESCRIPTION_LABEL%></i>
  <table class="dataTable">


    <%
      int n1 = 0;
      for (int i=0; i<inverse_roles.size(); i++) {
        String s = (String) inverse_roles.get(i);
        Vector ret_vec = DataUtils.parseData(s, "|");
        String role_name = (String) ret_vec.elementAt(0);
        String target_concept_name = (String) ret_vec.elementAt(1);
        String target_concept_code = (String) ret_vec.elementAt(2);
        String target_coding_scheme_name = (String) ret_vec.elementAt(3);

        if (target_coding_scheme_name != null) {
            target_coding_scheme_name = DataUtils.getCSName(target_coding_scheme_name);
        }

        if (n1 % 2 == 0) {
          %>
            <tr class="dataRowDark">
          <%
        } else {
          %>
            <tr class="dataRowLight">
          <%
        }
        n1++;
        %>
              <td>
              <%
              //if (role_name.compareTo("domain") != 0 && role_name.compareTo("range") != 0) {
              if (!DataUtils.isNonConcept2ConceptAssociation(role_name)) {
              %>
                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%><%=version_parameter%>&code=<%=target_concept_code%>">
                  <%=target_concept_name%>
                </a>
              <%
              } else {
              %>
                <%=target_concept_name%>
              <%
              }
              %>
              </td>


              <%
              if (isMapping) {
              %>
              <td>
                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
                  <%=target_concept_code%>
                </a>
              </td>
              <%
              }
              %>

              <td><%=role_name%></td>
              <%
              if (isMapping) {
              %>

              <td><%=rel%></td>
              <td><%=score%></td>
              <%
              }
              %>

           </tr>
      <%
      }
      %>
  </table>
</p>
<p>

  <%
     } else if (inverse_roles == null || inverse_roles.size() == 0 || !display_inverse_relationships) {
  %>
       <b><%=Constants.INVERSE_ROLE_LABEL%></b>&nbsp;<%=Constants.INVERSE_ROLE_LABEL_2%>: <i>(none)</i>
  <%
     }
  %>


<%
 }
%>


<p>
<%
  if (inverse_associations != null && inverse_associations.size() > 0 && display_inverse_relationships) {
%>
    <b><%=Constants.INVERSE_ASSOCIATION_LABEL%></b>&nbsp;<%=Constants.INVERSE_ASSOCIATION_LABEL_2%>:
<br/>
<i><%=Constants.INVERSE_ASSOCIATION_DESCRIPTION_LABEL%></i>
<table class="dataTable">

  <%
   if (isMapping) {
   %>
       <th class="dataTableHeader" scope="col" align="left">Name</th>
       <th class="dataTableHeader" scope="col" align="left">Code</th>
       <th class="dataTableHeader" scope="col" align="left">Source</th>
       <th class="dataTableHeader" scope="col" align="left">Relationship</th>
       <th class="dataTableHeader" scope="col" align="left">REL</th>
       <th class="dataTableHeader" scope="col" align="left">Map Rank</th>
   <%
   }
 %>


  <%
    int n2 = 0;

    for (int i=0; i<inverse_associations.size(); i++) {
      String s = (String) inverse_associations.get(i);
      Vector ret_vec = DataUtils.parseData(s, "|");
      String role_name = (String) ret_vec.elementAt(0);
      String target_concept_name = (String) ret_vec.elementAt(1);
      String target_concept_code = (String) ret_vec.elementAt(2);
      String target_coding_scheme_name = (String) ret_vec.elementAt(3);
      
        if (target_coding_scheme_name != null) {
            target_coding_scheme_name = DataUtils.getFormalName(target_coding_scheme_name);
        }
        
      
      String target_namespace = null;
  String qualifiers = null;
  rel = null;
  score = null;

  if (isMapping) {
      if (ret_vec.size() > 4) {
        qualifiers = (String) ret_vec.elementAt(4);
        Vector v = DataUtils.parseData(qualifiers, "$");
        
        for (int k=0; k<v.size(); k++) {
	  String t = (String) v.elementAt(k);
	  Vector nv_vec = DataUtils.parseData(t, ":");
	  if (nv_vec.size() > 1) {
	  
		  String qualifier_name = (String) nv_vec.elementAt(0);
		  String qualifier_value = (String) nv_vec.elementAt(1);
          
		  if (qualifier_name.compareToIgnoreCase("rel") == 0) {
		      rel = qualifier_value;
		  }
		  if (qualifier_name.compareToIgnoreCase("maprank") == 0 || qualifier_name.compareToIgnoreCase("score") == 0) {
		      score = qualifier_value;
		  }
	  }
        }
        
      }
      if (ret_vec.size() > 5) {
          target_namespace = (String) ret_vec.elementAt(5);
      }
  }

      if (n2 % 2 == 0) {
        %>
            <tr class="dataRowDark">
        <%
        } else {
        %>
            <tr class="dataRowLight">
        <%
        }
        n2++;
        %>
              <td scope="row">

              <%
              if (role_name.compareTo("domain") != 0 && role_name.compareTo("range") != 0) {
              %>

                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%><%=version_parameter%>&code=<%=target_concept_code%>">
                  <%=target_concept_name%>
                </a>
              <%
              } else {
              %>
                <%=target_concept_name%>
              <%
              }
              %>
              </td>

              <%
              if (isMapping) {
              %>
              <td>
                <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
                  <%=target_concept_code%>
                </a>
              </td>
              <td><%=target_namespace%></td>
              <%
              } 
              %>

              <td><%=role_name%></td>
              
              
              
              <%
              if (isMapping) {
              %>
              <td><%=rel%></td>
              <td><%=score%></td>
              <%
              }
              %>

            </tr>
        <%
        }
        %>
 </table>
</p>

<%
  } else if (inverse_associations == null || inverse_associations.size() == 0 || !display_inverse_relationships) {
    %>
    <b><%=Constants.INVERSE_ASSOCIATION_LABEL%></b>&nbsp;<%=Constants.INVERSE_ASSOCIATION_LABEL_2%>: <i>(none)</i>
    <%
  }
%>



<%
}

%>


<%
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

