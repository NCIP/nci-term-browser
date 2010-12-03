
<%
  if (type.compareTo("mapping") == 0 || type.compareTo("all") == 0) {

  Entity concept_curr = (Entity) request.getSession().getAttribute("concept");
  String scheme_curr = (String) request.getSession().getAttribute("dictionary");
  if (scheme_curr == null) {
    scheme_curr = (String) request.getParameter("dictionary");
  }

  String version_curr = (String) request.getSession().getAttribute("version");
  String code_curr = (String) request.getSession().getAttribute("code");
        Vector mapping_uri_version_vec = DataUtils.getMappingCodingSchemesEntityParticipatesIn(code_curr, null);

 %>
  <table border="0" width="708px">
    <tr>
      <td class="textsubtitle-blue" align="left">Mapping relationships</td>
      <td align="right" class="texttitle-blue-rightJust">
        <h:form>
          <h:commandLink action="#{CartActionBean.addToCart}" value="Add to Cart">
            <f:setPropertyActionListener target="#{CartActionBean.entity}" value="concept" />
            <f:setPropertyActionListener target="#{CartActionBean.codingScheme}" value="dictionary" />
          </h:commandLink>
        </h:form>
      </td>
    </tr>
  </table>
<%
        if (mapping_uri_version_vec == null || mapping_uri_version_vec.size() == 0) {
%>
            <b>Mapping Relationships:</b> <i>(none)</i>
<%
        } else {
              DataUtils util = new DataUtils();
              
        
%>

            

        <b>Maps To:</b>
<%        
        for(int lcv=0; lcv<mapping_uri_version_vec.size(); lcv++) {
           String mapping_uri_version = (String) mapping_uri_version_vec.elementAt(lcv);
           Vector ret_vec = DataUtils.parseData(mapping_uri_version, "|");
           String mapping_cs_uri = (String) ret_vec.elementAt(0);
           String mapping_cs_version = (String) ret_vec.elementAt(1);
           String mapping_cs_name = DataUtils.uri2CodingSchemeName(mapping_cs_uri);
           
System.out.println("mapping_cs_uri: " + mapping_cs_uri);
System.out.println("mapping_cs_version: " + mapping_cs_version);
System.out.println("mapping_cs_name: " + mapping_cs_name);
System.out.println("code_curr: " + code_curr);

                       HashMap hmap = util.getRelationshipHashMap(mapping_cs_name, mapping_cs_version, code_curr);
                             ArrayList associations = null;
                             if (hmap.get(DataUtils.TYPE_ASSOCIATION) != null) {
                                associations = (ArrayList) hmap.get(DataUtils.TYPE_ASSOCIATION);
                             }


if (associations != null && associations.size() > 0) {

           
%>        
    <p></p>Mapping Source: <%=mapping_cs_name%>
    <table class="dataTable">
         <th class="dataTableHeader" scope="col" align="left">Relationship</th>
         <th class="dataTableHeader" scope="col" align="left">Name</th>
         <th class="dataTableHeader" scope="col" align="left">Code</th>
         <th class="dataTableHeader" scope="col" align="left">Target</th>
         
         <th class="dataTableHeader" scope="col" align="left">REL</th>
         <th class="dataTableHeader" scope="col" align="left">Map Rank</th>
    <%




          int n2 = 0;
          for (int i=0; i<associations.size(); i++) {
              String s = (String) associations.get(i);
              Vector ret_vec2 = DataUtils.parseData(s, "|");
              String role_name = (String) ret_vec2.elementAt(0);
              String target_concept_name = (String) ret_vec2.elementAt(1);
              String target_concept_code = (String) ret_vec2.elementAt(2);
              String target_coding_scheme_name = (String) ret_vec2.elementAt(3);
              String target_namespace = null;

              String qualifiers = null;
              String rel = null;
        String score = null;

if (ret_vec2.size() > 4) {
        qualifiers = (String) ret_vec2.elementAt(4);
        System.out.println(qualifiers);
        Vector v = DataUtils.parseData(qualifiers, "$");
        String rel_str = (String) v.elementAt(0);
        int m1 = rel_str.indexOf(":");
        rel = rel_str.substring(m1+1, rel_str.length());
        String score_str = (String) v.elementAt(1);
        int m2 = score_str.indexOf(":");
        score = score_str.substring(m2+1, score_str.length());
        target_namespace = (String) ret_vec2.elementAt(5);
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
            <td><%=role_name%></td>
            <td>
            <%
            if (!DataUtils.isNonConcept2ConceptAssociation(role_name)) {
            %>
        <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
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

            <td>
        <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
          <%=target_concept_code%>
        </a>
            </td>
            <td><%=target_namespace%></td>
            <td><%=rel%></td>
            <td><%=score%></td>


          </tr>
        <%
        }

      %>
     </table>
<%
    }
}     
%>     
     
     
    <hr></hr>
        <b>Maps From:</b>
      
<%      
      for(int lcv=0; lcv<mapping_uri_version_vec.size(); lcv++) {
           String mapping_uri_version = (String) mapping_uri_version_vec.elementAt(lcv);
           Vector ret_vec = DataUtils.parseData(mapping_uri_version, "|");
           String mapping_cs_uri = (String) ret_vec.elementAt(0);
           String mapping_cs_version = (String) ret_vec.elementAt(1);
           String mapping_cs_name = DataUtils.uri2CodingSchemeName(mapping_cs_uri);
           

System.out.println("mapping_cs_uri: " + mapping_cs_uri);
System.out.println("mapping_cs_version: " + mapping_cs_version);
System.out.println("mapping_cs_name: " + mapping_cs_name);
System.out.println("code_curr: " + code_curr);           

                       HashMap hmap = util.getRelationshipHashMap(mapping_cs_name, mapping_cs_version, code_curr);
                             ArrayList associations = null;
                             if (hmap.get(DataUtils.TYPE_INVERSE_ASSOCIATION) != null) {
                                associations = (ArrayList) hmap.get(DataUtils.TYPE_INVERSE_ASSOCIATION);
                             }


if (associations != null && associations.size() > 0) {

%>
      
    <p></p>Mapping Source: <%=mapping_cs_name%>
    <table class="dataTable">

           <th class="dataTableHeader" scope="col" align="left">Name</th>
           <th class="dataTableHeader" scope="col" align="left">Code</th>
           <th class="dataTableHeader" scope="col" align="left">Source</th>
           <th class="dataTableHeader" scope="col" align="left">Relationship</th>
           <th class="dataTableHeader" scope="col" align="left">REL</th>
           <th class="dataTableHeader" scope="col" align="left">Map Rank</th>


    <%








                int n2 = 0;


          for (int i=0; i<associations.size(); i++) {
              String s = (String) associations.get(i);
              Vector ret_vec2 = DataUtils.parseData(s, "|");
              String role_name = (String) ret_vec2.elementAt(0);
              String target_concept_name = (String) ret_vec2.elementAt(1);
              String target_concept_code = (String) ret_vec2.elementAt(2);
              String target_coding_scheme_name = (String) ret_vec2.elementAt(3);
              String target_namespace = null;

              String qualifiers = null;
              String rel = null;
        String score = null;

if (ret_vec2.size() > 4) {
        qualifiers = (String) ret_vec2.elementAt(4);
        System.out.println(qualifiers);
        Vector v = DataUtils.parseData(qualifiers, "$");
        String rel_str = (String) v.elementAt(0);
        int m1 = rel_str.indexOf(":");
        rel = rel_str.substring(m1+1, rel_str.length());
        String score_str = (String) v.elementAt(1);
        int m2 = score_str.indexOf(":");
        score = score_str.substring(m2+1, score_str.length());
        target_namespace = (String) ret_vec2.elementAt(5);
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

            <td>
            <%
            if (!DataUtils.isNonConcept2ConceptAssociation(role_name)) {
            %>
        <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
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

            <td>
        <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_coding_scheme_name%>&code=<%=target_concept_code%>">
          <%=target_concept_code%>
        </a>
            </td>
            <td><%=target_namespace%></td>
            <td><%=role_name%></td>
            <td><%=rel%></td>
            <td><%=score%></td>


          </tr>
        <%
        }
                 
      %>
     </table>

<%
                 }
            }
        }
    }
%>
