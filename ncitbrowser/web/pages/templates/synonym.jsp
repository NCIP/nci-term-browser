<%
  List displayItemList2 = NCItBrowserProperties.getInstance().getDisplayItemList();
  String prop_url = "";
  for (int i=0; i<displayItemList2.size(); i++) {
    DisplayItem displayItem = (DisplayItem) displayItemList2.get(i);
    String propertyName = displayItem.getPropertyName();    
    if (propertyName.equals("MedDRA_Code"))
      prop_url = displayItem.getUrl();
  }

  if (type.compareTo("synonym") == 0 || type.compareTo("all") == 0)
  {
    %>
    <p class="textsubtitle-blue">Synonym Details</p>
    <div>
      <table class="dataTable" border="0">
        <tr>
          <th class="dataTableHeader" scope="col" align="left">Term</th>
          <th class="dataTableHeader" scope="col" align="left">Source</th>
          <th class="dataTableHeader" scope="col" align="left">Type
            <a href="#" onclick="javascript:window.open('<%=request.getContextPath() %>/pages/term_type_help_info.jsf',
                '_blank','top=100, left=100, height=740, width=780, status=no, menubar=no, resizable=yes, scrollbars=yes, toolbar=no, location=no, directories=no');">
              <img src="<%= request.getContextPath() %>/images/help.gif" alt="Term Type Definitions" border="0">
            </a>
          </th>
          <th class="dataTableHeader" scope="col" align="left">Code</th>
        </tr>

        <%
        
          Concept concept_syn = (Concept) request.getSession().getAttribute("concept");
          Vector synonyms = new DataUtils().getSynonyms(dictionary, concept_syn);
          HashSet hset = new HashSet();
          for (int n=0; n<synonyms.size(); n++)
          {
            String s = (String) synonyms.elementAt(n);
            if (!hset.contains(s)) {
                    hset.add(s);
		    Vector synonym_data = DataUtils.parseData(s, "|");
		    String term_name = (String) synonym_data.elementAt(0);
		    String term_type = (String) synonym_data.elementAt(1);
		    String term_source = (String) synonym_data.elementAt(2);
		    String term_source_code = (String) synonym_data.elementAt(3);
		    String rowColor = (n%2 == 0) ? "dataRowDark" : "dataRowLight";
		%>
		    <tr class="<%=rowColor%>">
		      <td class="dataCellText"><%=term_name%></td>
		      <td class="dataCellText"><%=term_source%></td>
		      <td class="dataCellText"><%=term_type%></td>
              <%
                if (prop_url != null && prop_url.compareTo("null") != 0) {
                  String url_str = prop_url + term_source_code;
              %>
                <td><a href="javascript:redirect_site('<%= url_str %>')"><%= term_source_code %></a></td>
              <%} else {%>
		        <td class="dataCellText"><%=term_source_code%></td>
              <%}%>
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