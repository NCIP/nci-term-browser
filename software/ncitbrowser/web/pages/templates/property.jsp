<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ page import="gov.nih.nci.evs.browser.utils.FormatUtils" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>

<%
  HashMap def_map = NCItBrowserProperties.getDefSourceMappingHashMap();
  
  List displayItemList = null;
  Concept curr_concept = null;
  Boolean bool_obj = null;
  //String isActive = null;

  try {
    displayItemList = NCItBrowserProperties.getInstance().getDisplayItemList();
    curr_concept = (Concept) request.getSession().getAttribute("concept");
    request.getSession().setAttribute("code", curr_concept.getEntityCode());
    
    bool_obj = curr_concept.isIsActive();

  } catch (Exception ex) {
    // Do nothing
  }

  if ((type.compareTo("properties") == 0 || type.compareTo("all") == 0) &&
    displayItemList != null && curr_concept != null) {

    Vector propertytypes = new Vector();
    propertytypes.add("PRESENTATION");
    propertytypes.add("DEFINITION");
    propertytypes.add("GENERIC");
    //propertytypes.add("INSTRUCTION");
    propertytypes.add("COMMENT");

    Vector additionalproperties = new Vector();
    additionalproperties.add("CONCEPT_NAME");
    additionalproperties.add("primitive");

    String concept_status = null;
    try {
        concept_status = curr_concept.getStatus();
    } catch (Exception ex) {
        
    }

    if (concept_status != null) {
       concept_status = concept_status.replaceAll("_", " ");
       if (concept_status.compareToIgnoreCase("active") == 0 || concept_status.compareToIgnoreCase("reviewed") == 0) concept_status = null;
    }

    HashSet hset = new HashSet();
    HashMap hmap = new HashMap();
    Vector propertyvalues = null;

    for (int i=0; i<propertytypes.size(); i++) {
      String propertytype = (String) propertytypes.elementAt(i);
      Vector propertynames = DataUtils.getPropertyNamesByType(
        curr_concept, propertytype);

      for (int j=0; j<propertynames.size(); j++) {
        String propertyname = (String) propertynames.elementAt(j);

        if (!hset.contains(propertyname)) {
          hset.add(propertyname);
          propertyvalues = DataUtils.getPropertyValues(
            curr_concept, propertytype, propertyname);

          if (propertyvalues != null)
            hmap.put(propertyname, propertyvalues);
        }
      }
    }

    propertyvalues = new Vector();
    String concept_id = curr_concept.getEntityCode();
    propertyvalues.add(concept_id);
    hmap.put("Code", propertyvalues);
    Vector displayed_properties = new Vector();
    Vector properties_to_display = new Vector();
    Vector properties_to_display_label = new Vector();
    Vector properties_to_display_url = new Vector();
    Vector properties_to_display_linktext = new Vector();

    for (int i=0; i<displayItemList.size(); i++) {
      DisplayItem displayItem = (DisplayItem) displayItemList.get(i);
      if (!displayItem.getIsExternalCode()) {
        properties_to_display.add(displayItem.getPropertyName());
        properties_to_display_label.add(displayItem.getItemLabel());
        properties_to_display_url.add(displayItem.getUrl());
        properties_to_display_linktext.add(displayItem.getHyperlinkText());
      }
    }

    int num_definitions = 0;
    int num_alt_definitions = 0;
    Vector external_source_codes = new Vector();
    Vector external_source_codes_label = new Vector();
    Vector external_source_codes_url = new Vector();
    Vector external_source_codes_linktext = new Vector();
    for (int i=0; i<displayItemList.size(); i++) {
    DisplayItem displayItem = (DisplayItem) displayItemList.get(i);

    if (displayItem.getIsExternalCode()) {
      external_source_codes.add(displayItem.getPropertyName());
      external_source_codes_label.add(displayItem.getItemLabel());
      external_source_codes_url.add(displayItem.getUrl());
      external_source_codes_linktext.add(displayItem.getHyperlinkText());
    }
  }
%>
<p class="textsubtitle-blue">Terms and Properties</p>
<%
if (!bool_obj.equals(Boolean.TRUE) ||
  (concept_status != null &&
    concept_status.compareToIgnoreCase("Retired Concept") == 0)) // non-active
{
%>
    <p class="textbody"><b>Concept Status:</b>&nbsp;<i class="textbodyred">Retired Concept</i>
<%
    String prop_dictionary = (String) request.getSession().getAttribute("dictionary");
    
    Vector descendantCodes = HistoryUtils.getDescendantCodes(dictionary, null, null, curr_concept.getEntityCode());
    if (descendantCodes != null) {
            if (descendantCodes != null && descendantCodes.size() > 0) {
		    String link = "&nbsp;(See:&nbsp;"; 

	%>            
		    <%=link%>
	<%            
		    for (int i=0; i<descendantCodes.size(); i++) {
			String t = (String) descendantCodes.elementAt(i);
			Vector w = DataUtils.parseData(t);
			String descendantName = (String) w.elementAt(0);
			String descendantCode = (String) w.elementAt(1);
	%>

		      <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=prop_dictionary%>&code=<%=descendantCode%>">
			<%=descendantName%>
		      </a>
	<%              
		    }

		    link = ")"; 
	%>            
		    <%=link%></p>	
	<%    
            }
    }
    
%>    
    
<%
}
else if (concept_status != null && concept_status.compareToIgnoreCase("Retired Concept") != 0) {
%>
    <p class="textbody"><b>Concept Status:</b>&nbsp;<i class="textbody"><%=concept_status%></i></p>
<%
}
%>
<%

  for (int i=0; i<properties_to_display.size(); i++) {
    String propName = (String) properties_to_display.elementAt(i);
    String propName_label = (String) properties_to_display_label.elementAt(i);
    
    
 
if (propName_label.compareTo("NCI Thesaurus Code") == 0  && propName.compareTo("NCI_THESAURUS_CODE") != 0) {
    String formalName = DataUtils.getFormalName(dictionary);
    if (formalName == null)
        formalName = dictionary;
	propName_label = formalName + " Code";
}
    
    String propName_label2 = propName_label;
    String url = (String) properties_to_display_url.elementAt(i);
    
    String linktext = (String) properties_to_display_linktext.elementAt(i);
    String qualifier = "";
    if (url != null) {
    
      displayed_properties.add(propName);
      Vector value_vec = (Vector) hmap.get(propName);

      if (value_vec != null && value_vec.size() > 0) {
        String value = (String) value_vec.elementAt(0);
        String value_wo_qualifier = value;

        int n = value.indexOf("|");
        if (n != -1 && (propName_label.indexOf("Definition") != -1 || propName_label.indexOf("DEFINITION") != -1 || propName_label.indexOf("definition") != -1)) {
          value_wo_qualifier = value.substring(0, n);
          qualifier = value.substring(n+1, value.length());
          
          if (def_map != null && def_map.containsKey(qualifier)) {
              String def_source_display_value = (String) def_map.get(qualifier);
              value = value_wo_qualifier + " (" + qualifier + ")";
              propName_label = def_source_display_value + " " + propName_label2;
          } else {

		  if (qualifier.indexOf("PDQ") != -1) {
			value = FormatUtils.reformatPDQDefinition(value);
		  } else if (qualifier.compareTo("NCI") != 0) {
		      value = value_wo_qualifier;
		      propName_label = qualifier + " " + propName_label2;
		  } else
		      value = value_wo_qualifier;
	  }
        }

        String url_str = url + value;
%>
        <p>
          <b><%=propName_label%>:&nbsp;</b><%=value%>&nbsp;
          <a href="javascript:redirect_site('<%= url_str %>')">(<%=linktext%>)</a>
        </p>
<%
      }
    } else if (propName_label.indexOf("Synonyms") == -1) {
      displayed_properties.add(propName);
      Vector value_vec = (Vector) hmap.get(propName);

      if (value_vec != null && value_vec.size() > 0) {
        int k = 0;  
        for (int j=0; j<value_vec.size(); j++) {
          String value = (String) value_vec.elementAt(j);
if(propName_label.compareTo("Definition") == 0) {
    value = FormatUtils.reformatPDQDefinition(value);
}
          
          String value_wo_qualifier = value;
          int n = value.indexOf("|");

          if (n != -1 && (propName_label.indexOf("Definition") != -1 || propName_label.indexOf("DEFINITION") != -1 || propName_label.indexOf("definition") != -1)) {

              value_wo_qualifier = value.substring(0, n);
              qualifier = value.substring(n+1, value.length());
              
              if (def_map != null && def_map.containsKey(qualifier)) {
	          String def_source_display_value = (String) def_map.get(qualifier);
	          value = value_wo_qualifier + " (" + qualifier + ")";
                  propName_label = def_source_display_value + " " + propName_label2;
                  
                  
              } else {
              
		    if (qualifier.indexOf("PDQ") != -1) {
			value = FormatUtils.reformatPDQDefinition(value);
		    } else if (qualifier.compareTo("NCI") != 0) {
		      value = value_wo_qualifier;
		      propName_label = qualifier + " " + propName_label2;
		      
		    } else {
		      value = value_wo_qualifier;
		    }
              }
          }
          
          
          if (k == 0) {
%>
            <p><b><%=propName_label%>:&nbsp;</b><%=value%></p>
<%
          } else {
%>
            <p><%=value%></p>
<%
          }

      }
    }
  }
}
%>
<p>
<b>Synonyms &amp; Abbreviations:</b>
<a href="<%=request.getContextPath() %>/pages/concept_details.jsf?dictionary=<%=scheme%>&code=<%=id%>&type=synonym">(see Synonym Details)</a>

<table class="datatable">
<%
  HashSet hset2 = new HashSet();
  for (int i=0; i<properties_to_display.size(); i++) {
    String propName = (String) properties_to_display.elementAt(i);
    String propName_label = (String) properties_to_display_label.elementAt(i);
    if (propName_label.indexOf("Synonyms") != -1) {
      displayed_properties.add(propName);
      Vector value_vec = (Vector) hmap.get(propName);
      value_vec = SortUtils.quickSort(value_vec);
      if (value_vec != null && value_vec.size() > 0) {
        
        int row=0;
        for (int j=0; j<value_vec.size(); j++) {
          String value = (String) value_vec.elementAt(j);
          int n = value.indexOf("|");
          //if (n != -1) value = value.substring(0, n);
          
          if (n != -1) {
             Vector value_v = DataUtils.parseData(value, "|");
             value = (String) value_v.elementAt(0);
          }
         
          //String valueLC = value.toLowerCase();
          //if (hset2.contains(valueLC))
          //  continue;
          //hset2.add(valueLC);
          
          if (!hset2.contains(value)) {
              hset2.add(value);
		  if ((row++) % 2 == 0) {
		    %>
		      <tr class="dataRowDark">
		    <%
		  } else {
		    %>
		      <tr class="dataRowLight">
		    <%
		  }
		    %>
			<td><%=value%></td>
		      </tr>
		    <%
		 }
         }
      }
    }
  }
%>
</table>
</p>
<p>
  <b>External Source Codes:&nbsp;</b>
  <table class="datatable">
    <%
      int n = 0;
      for (int i=0; i<external_source_codes.size(); i++) {
        String propName = (String) external_source_codes.elementAt(i);
        String propName_label = (String) external_source_codes_label.elementAt(i);
        String prop_url = (String) external_source_codes_url.elementAt(i);
        String prop_linktext = (String) external_source_codes_linktext.elementAt(i);

        displayed_properties.add(propName);
        Vector value_vec = (Vector) hmap.get(propName);
        if (value_vec != null && value_vec.size() > 0) {
          for (int j=0; j<value_vec.size(); j++) {
            String value = (String) value_vec.elementAt(j);
            
            if (n % 2 == 0) {
              %>
                <tr class="dataRowDark">
              <%
            } else {
              %>
                <tr class="dataRowLight">
              <%
            }
            n++;
            %>
              <td><%=propName_label%></td>
              <td>
                <%=value%>
                <%
                  if (prop_url != null && prop_url.compareTo("null") != 0) {
                    String url_str = prop_url + value;
                    %>
                      <a href="javascript:redirect_site('<%= url_str %>')">(<%= prop_linktext %>)</a>
                    <%
                  }
                %>
              </td>
            </tr>
          <%
          }
       }
    }
    %>
    </table>
</p>
<p>
  <b>Other Properties:</b>
  <table class="datatable">
    <%
      Set keyset = hmap.keySet();
      Iterator iterator = keyset.iterator();
      n = 0;

      while (iterator.hasNext()) {
        String prop_name = (String) iterator.next();
        if (!displayed_properties.contains(prop_name) && !additionalproperties.contains(prop_name) ) {
          Vector value_vec = (Vector) hmap.get(prop_name);
          if (value_vec == null || value_vec.size() == 0) {
            if (n % 2 == 0) {
              %>
                <tr class="dataRowDark">
              <%
            } else {
              %>
                <tr class="dataRowLight">
              <%
            }
            n++;
            %>
                  <td><%=prop_name%></td>
                  <td>None</td>
                </tr>
            <%
          } else {
            for (int j=0; j<value_vec.size(); j++) {
              String value = (String) value_vec.elementAt(j);
              if (n % 2 == 0) {
                %>
                  <tr class="dataRowDark">
                <%
              } else {
                %>
                  <tr class="dataRowLight">
                <%
              }
              n++;
              %>
                  <td><%=prop_name%></td>
                  <td><%=value%></td>
                </tr>
              <%
            }
          }
        }
      }
    %>
  </table>
</p>
<p>
  <b>Additional Concept Data:&nbsp;</b>
  <table class="datatable">
    <%
      String concept_name = curr_concept.getEntityDescription().getContent();
      concept_name = concept_name.replaceAll(" ", "_");

      String concept_name_label = "Concept Name:";
      String dict = (String) request.getSession().getAttribute("dictionary");
      
      String primitive = null;
      String primitive_prop_name = "primitive";
      String primitive_label = "Defined Fully by Roles:";
      
      dict = DataUtils.getFormalName(dict);
      
      //Vector primitive_value_vec = (Vector) hmap.get(primitive_prop_name);
      String vocabulary_format = DataUtils.getMetadataValue(dict, "format");
      if (vocabulary_format != null && vocabulary_format.indexOf("OWL") != -1) {
	      Boolean isDefined = curr_concept.getIsDefined();
	      if (isDefined != null) {
		  if (isDefined.equals(Boolean.TRUE)) {
		      primitive = "No";
		  } else {
		      primitive = "Yes";
		  }
	      }
      }    
      
     
      // OWL
      /*
      if (vocabulary_format != null && vocabulary_format.indexOf("OWL") != -1) {
	      if (primitive_value_vec != null && primitive_value_vec.size() > 0) {
		primitive = (String) primitive_value_vec.elementAt(0);
		
		if (primitive.compareTo("true") == 0) primitive = "No";
		else primitive = "Yes";
		
	      }
      } 
      else if (vocabulary_format != null && vocabulary_format.indexOf("OWL") == -1) { 
              primitive_value_vec = (Vector) hmap.get("ISPRIMITIVE");
	      if (primitive_value_vec != null && primitive_value_vec.size() > 0) {
		primitive = (String) primitive_value_vec.elementAt(0);
		if (primitive.compareTo("true") == 0) primitive = "No";
		else primitive = "Yes";
	      }              
      }
      */
      
      String kind = "not available";
      String kind_prop_name = "Kind";
      String kind_label = "Kind:";
    %>
    
   
    <%
    if (primitive != null) {
    %>
    <tr class="dataRowLight">
      <td><%=primitive_label%>&nbsp;<%=primitive%></td>
      <td>&nbsp;</td>
    </tr>
    <%
    }
    %>
    
  </table>
</p>
<%
  String requestURL = request.getRequestURL().toString();
  int idx = requestURL.indexOf("pages");
  requestURL = requestURL.substring(0, idx);
  
  String prop_dictionary = dictionary.replace(" ", "%20");
  
  
  String url = requestURL + "ConceptReport.jsp?dictionary=" + prop_dictionary + "&code=" + concept_id;
  String url_text = "ConceptReport.jsp?dictionary=" + prop_dictionary + "&code=" + concept_id;
  String bookmark_title = prop_dictionary + "%20" + concept_id;
%>
<p>
  <b>URL</b>: <%= requestURL %><%= url_text %>
<%
}
%>