<%
  String source_header = DataUtils.getMetadataValue(
      dictionary, null, null, "source_header");
  String term_type_header = DataUtils.getMetadataValue(
      dictionary, null, null, "term_type_header");


String formalName = DataUtils.getFormalName(dictionary);
Vector localname_vec = DataUtils.getLocalNames(formalName);

  if (type.compareTo("synonym") == 0 || type.compareTo("all") == 0)
  {
    %>
  <table border="0" width="100%">
    <tr>
      <td class="textsubtitle-blue" align="left">
      
<%			
if (type != null && type.compareTo("all") == 0) {
%>
    <A name="synonyms">Synonym Details</A>
<%    
} else {
%>
    Synonym Details
<%    
}
%>         
      </td>
    </tr>
  </table>
    <div>
      <table class="datatable_960" border="0" width="100%">
        <tr>
          <th class="dataTableHeader" scope="col" align="left">Term</th>
          <th class="dataTableHeader" scope="col" align="left">Source
            <% if (source_header != null && source_header.length() > 0) { %>
              <a href="#" onclick="javascript:window.open('<%=request.getContextPath()%>/pages/source_help_info.jsf?dictionary=<%=dictionary%>',
                  '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                <img src="<%= request.getContextPath() %>/images/help.gif" alt="Source Definitions" border="0">
              </a>
            <% } %>
          </th>
          <th class="dataTableHeader" scope="col" align="left">Type
            <% if (term_type_header != null && term_type_header.length() > 0) { %>
              <a href="#" onclick="javascript:window.open('<%=request.getContextPath()%>/pages/term_type_help_info.jsf?dictionary=<%=dictionary%>',
                  '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
                <img src="<%= request.getContextPath() %>/images/help.gif" alt="Term Type Definitions" border="0">
              </a>
            <% } %>
          </th>
          <th class="dataTableHeader" scope="col" align="left">Code</th>
        </tr>

        <%

          Entity concept_syn = (Entity) request.getSession().getAttribute("concept");
          //Vector synonyms = new DataUtils().getSynonyms(dictionary, concept_syn);
          Vector synonyms = new DataUtils().getSynonyms(concept_syn);
          HashSet hset = new HashSet();
          int n = -1;
          for (int lcv=0; lcv<synonyms.size(); lcv++)
          {
            String s = (String) synonyms.elementAt(lcv);
            if (!hset.contains(s)) {
                    hset.add(s);
                    n++;
        Vector synonym_data = StringUtils.parseData(s, "|");
        String term_name = (String) synonym_data.elementAt(0);
        //term_name = term_name.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
        String term_type = (String) synonym_data.elementAt(1);
        String term_source = (String) synonym_data.elementAt(2);
        String term_source_formal_name = DataUtils.getFormalNameByDisplayName(term_source);
      
        
//Test case: NCIt, ADCS-ADL MCI - Balance Checkbook (Code C106898)
        if (term_source_formal_name == null)
            term_source_formal_name = DataUtils.getFormalName(term_source);
           
        if (term_source.equalsIgnoreCase("nci"))
            term_source_formal_name = "NCI Thesaurus";

        //GF#33194 Empty string source codes break Synonym Details and View All 
        String term_source_code = null;
        if (synonym_data.size() > 3) {
            term_source_code = (String) synonym_data.elementAt(3);
        } 
        
        String rowColor = (n%2 == 0) ? "dataRowDark" : "dataRowLight";
    %>
        <tr class="<%=rowColor%>">
          <td class="dataCellText" scope="row"><%=DataUtils.encodeTerm(term_name)%></td>
          <td class="dataCellText"><%=term_source%></td>
          <td class="dataCellText"><%=term_type%></td>
              <%
                //String formalname_term_source = DataUtils.getFormalName(term_source);
                boolean hyperlink = false;
                if (term_source != null && !localname_vec.contains(term_source)) {
                    hyperlink = true;
                }
                             
                if (!DataUtils.isNull(term_source_formal_name) && !DataUtils.isNull(term_source_code) && hyperlink) {
                  String term_source_nm = DataUtils.getCSName(term_source_formal_name);
                  String url_str = request.getContextPath() +
                      "/pages/concept_details.jsf?dictionary=" +
                      term_source_nm + "&code=" + term_source_code;
              %>
                <td><a href="<%= url_str %>"><%= term_source_code %></a></td>
              <%} else if (!DataUtils.isNull(term_source_code)) {%>
            <td class="dataCellText"><%=term_source_code%></td>
              <%} else { %>
            <td class="dataCellText">&nbsp;</td>  
              <%} %>
        </tr>
    <%
            }
          }
        %>
      </table>
    </div>
    <%
  }
%>
