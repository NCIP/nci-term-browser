<%@ page import="java.util.*" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="gov.nih.nci.evs.browser.bean.*" %>
<%@ page import="gov.nih.nci.evs.browser.properties.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.common.*" %>
<%@ page import="org.LexGrid.concepts.*" %>
<%@ page import="org.LexGrid.LexBIG.Utility.Iterators.*" %>
<%@ page import="org.LexGrid.LexBIG.DataModel.Core.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.RemoteServerUtil" %>
<%@ page import="org.LexGrid.LexBIG.LexBIGService.LexBIGService"%>

<%

	HashMap def_map = null;

	int other_src_alt_def_count = 0;
	Vector nci_def_label_value = new Vector();
	Vector non_nci_def_label_value = new Vector();
	Vector other_label_value = new Vector();
	boolean is_definition = false;

	Vector ncim_metathesaurus_cui_vec = new Vector();
	HashSet ncim_metathesaurus_cui_hset = new HashSet();

	String ncim_cui_propName = "NCI_META_CUI";
	String umls_cui_propName = "UMLS_CUI";
	String ncim_cui_propName_label = null;
	String ncim_cui_prop_url = null;
	String ncim_cui_prop_linktext = null;
	Vector ncim_cui_code_vec = new Vector();

	List displayItemList = null;

	Entity curr_concept = null;
	String codingScheme = null;
	String namespace = null;
	Boolean isActive = null;
	String concept_status = null;
	Vector properties_to_display = null;
	Vector properties_to_display_label = null;
	Vector properties_to_display_url = null;
	Vector properties_to_display_linktext = null;
	Vector additionalproperties = null;
	Vector external_source_codes = null;
	Vector external_source_codes_label = null;
	Vector external_source_codes_url = null;
	Vector external_source_codes_linktext = null;
	Vector descendantCodes = null;
	HashMap propertyName2ValueHashMap = null;

	Vector displayed_properties = new Vector();
	Vector presentation_vec = new Vector();
	String concept_id = null;

	try {
		def_map = NCItBrowserProperties.getDefSourceMappingHashMap();
		propertyData.setDefSourceMapping(def_map);

		displayItemList = NCItBrowserProperties.getInstance().getDisplayItemList();
		propertyData.setDisplayItemList(displayItemList);

		curr_concept = (Entity) request.getSession().getAttribute("concept");
		if (curr_concept != null) {
			propertyData.setCurr_concept(curr_concept);
			request.getSession().setAttribute("code", curr_concept.getEntityCode());
			//curr_concept = propertyData.getCurr_concept();
			codingScheme = propertyData.getCodingScheme();
			version = propertyData.getVersion();
			code = propertyData.getCode();
			namespace = propertyData.getNamespace();
			isActive = propertyData.getIsActive();
			concept_status = propertyData.getConcept_status();
			properties_to_display = propertyData.getProperties_to_display();
			properties_to_display_label = propertyData.getProperties_to_display_label();
			properties_to_display_url = propertyData.getProperties_to_display_url();
			properties_to_display_linktext = propertyData.getProperties_to_display_linktext();
			additionalproperties = propertyData.getAdditionalproperties();
			external_source_codes = propertyData.getExternal_source_codes();
			external_source_codes_label = propertyData.getExternal_source_codes_label();
			external_source_codes_url = propertyData.getExternal_source_codes_url();
			external_source_codes_linktext = propertyData.getExternal_source_codes_linktext();
			descendantCodes = propertyData.getDescendantCodes();
			propertyName2ValueHashMap = propertyData.getPropertyName2ValueHashMap();
			concept_id = propertyData.get_concept_id();
			presentation_vec = propertyData.get_presentation_vec();
		}

	} catch (Exception ex) {
		// Do nothing
	}


  if ((type.compareTo("properties") == 0 || type.compareTo("all") == 0) &&
        displayItemList != null && curr_concept != null) {
  
%>
	<table border="0" width="708px">
		<tr>
			<td class="textsubtitle-blue" align="left">
		<%			
		if (type != null && type.compareTo("all") == 0) {
		%>
		    <A name="properties">Terms & Properties</A>
		<%    
		} else {
		%>
		    Terms & Properties
		<%    
		}
		%>
			</td>
		</tr>
	</table>
<%


boolean show_status = propertyData.get_show_status();
isActive = propertyData.getIsActive();

if ((isActive != null && !isActive.equals(Boolean.TRUE)  && concept_status != null) || (concept_status != null && concept_status.compareTo("null") != 0 && show_status)) {
%>
    <p class="textbody"><b>Concept Status:</b>&nbsp;<i class="textbodyred"><%=concept_status%></i>
<%
    if (descendantCodes != null) {
            if (descendantCodes != null && descendantCodes.size() > 0) {
		    String link = "&nbsp;(See:&nbsp;"; 

	%>            
		    <%=link%>
	<%            
	            String prop_dictionary_nm = dataUtils.getCSName(prop_dictionary); 
		    for (int i=0; i<descendantCodes.size(); i++) {
		    	   String t = (String) descendantCodes.elementAt(i);
		    	   Vector w = StringUtils.parseData(t);
		    	   String descendantName = (String) w.elementAt(0);
		    	   String descendantCode = (String) w.elementAt(1);
	%>
		      <a href="<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=prop_dictionary_nm%>&code=<%=descendantCode%>">
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
//[#26722] Support cross-linking of individual source vocabularies with NCI Metathesaurus.
  HashMap<String, String> label2URL = new HashMap<String, String>();
  HashMap<String, String> label2Linktext = new HashMap<String, String>();
  for (int m=0; m<properties_to_display.size(); m++) {
      String propName = (String) properties_to_display.elementAt(m);
      String prop_nm_label = (String) properties_to_display_label.elementAt(m);
      String prop_url = (String) properties_to_display_url.elementAt(m);
      String prop_linktext = (String) properties_to_display_linktext.elementAt(m);
      if (prop_url != null) {
	      label2URL.put(prop_nm_label, prop_url); 
	      label2Linktext.put(prop_nm_label, prop_linktext); 
      }
  }

  for (int i=0; i<properties_to_display.size(); i++) {
    String propName = (String) properties_to_display.elementAt(i);
    String propName_label = (String) properties_to_display_label.elementAt(i);

    if (propName_label.compareTo("NCI Thesaurus Code") == 0  && propName.compareTo("NCI_THESAURUS_CODE") != 0) {
        String formalName = dataUtils.getFormalName(dictionary);
        if (formalName == null) {
        	formalName = dictionary;
        }
	propName_label = formalName + " Code";
    }
    
    String propName_label2 = propName_label;
    String url = (String) properties_to_display_url.elementAt(i);
    String linktext = (String) properties_to_display_linktext.elementAt(i);

    //KLO 102611
    //if (propName.compareTo(ncim_cui_propName) == 0 || propName.compareTo(umls_cui_propName) == 0) {
    if (propName.compareTo(ncim_cui_propName) == 0) {
        ncim_cui_propName_label = propName_label;
        ncim_cui_prop_url = url;
        ncim_cui_prop_linktext = linktext;
        
        Vector ncim_cui_code_vec_temp = conceptDetails.getPropertyValues(
            curr_concept, "GENERIC", propName);
        if (ncim_cui_code_vec_temp != null) {
           for (int lcv=0; lcv<ncim_cui_code_vec_temp.size(); lcv++) {
               String t = (String) ncim_cui_code_vec_temp.elementAt(lcv);
               ncim_cui_code_vec.add(t);
           }
        } 
    }
    
    String qualifier = "";
    
    if (propName_label.indexOf("Synonyms") == -1) {
    
      displayed_properties.add(propName);
      propertyData.add_displayed_property(propName);
      
      Vector value_vec = (Vector) propertyName2ValueHashMap.get(propName);

      if (value_vec != null && value_vec.size() > 0) {
      
        int k = -1;  
        for (int j=0; j<value_vec.size(); j++) {
          String value = (String) value_vec.elementAt(j);
          k++;
          
          if (propName.compareTo("NCI_META_CUI") == 0) {
              ncim_cui_code_vec.add(value);
          }
          
	  if(propName_label.compareTo("Definition") == 0) {
	      String value_pre = value;
	      value = JSPUtils.reformatPDQDefinition(value);
	      String value_post = value;
	      if (value_pre.compareTo(value_post) != 0 && !value_post.endsWith("PDQ")) {
	           System.out.println("WARNING -- possible definition formatting issue with " + value_pre);
	      } 
	  }
          
          String value_wo_qualifier = value;
          int n = value.indexOf("|");

          is_definition = false;
          if (n != -1 && (propName_label.indexOf("Definition") != -1 || propName_label.indexOf("DEFINITION") != -1 || propName_label.indexOf("definition") != -1)) {
              is_definition = true;
	      Vector def_vec = StringUtils.parseData(value);
	      value_wo_qualifier = (String) def_vec.elementAt(0);
	      qualifier = "";
	      if (def_vec.size() > 1) {
	          qualifier = (String) def_vec.elementAt(1);
	      }
			  
              if (def_map != null && def_map.containsKey(qualifier)) {
	          String def_source_display_value = (String) def_map.get(qualifier);
	          value = value_wo_qualifier + " (" + qualifier + ")";
                  propName_label = def_source_display_value + " " + propName_label2;
                  
              } else {
		    if (qualifier.indexOf("PDQ") != -1) {
			//value = JSPUtils.reformatPDQDefinition(value);
		    } else if (qualifier.compareTo("NCI") != 0) {
	    
		      value = value_wo_qualifier;
		      propName_label = qualifier + " " + propName_label2;

		    } else if (qualifier.compareTo("NCI") == 0 && propName.compareTo("ALT_DEFINITION") == 0) {
	                 value = value_wo_qualifier;
	                 if (other_src_alt_def_count > 0) {
		         	propName_label = qualifier + " " + propName_label2;
		         } else {
		                propName_label = propName_label2;
		         }
		         
		    } else if (qualifier.compareTo("NCI") == 0 && propName.compareTo("ALT_DEFINITION") != 0) {
	                 value = value_wo_qualifier;
		         propName_label = propName_label2;
		         
		    } else {
		         value = value_wo_qualifier;
		    }
              }
	      if (qualifier.compareToIgnoreCase("NCI") == 0) {
		    nci_def_label_value.add(propName_label2 + "|" + value);
	      } else {
		    non_nci_def_label_value.add(propName_label + "|" + value);
	      }              
          }
         
          if (propName_label.indexOf("textualPresentation") == -1) {
              if (!is_definition) {
                   other_label_value.add(propName_label + "|" + value);             
              }
          }
      }
    }
  }
}
%>


<%
    for (int i_def = 0; i_def<other_label_value.size(); i_def++) {
        String label_value = (String) other_label_value.elementAt(i_def);
        Vector u = StringUtils.parseData(label_value);
        String propName_label = (String) u.elementAt(0);
        
        if (propName_label.compareToIgnoreCase("Preferred Name") == 0) {
            String value = (String) u.elementAt(1);
%>        
            <p>
              <b><%=propName_label%>:&nbsp;</b><%=value%>
              <% if (!DataUtils.isNCIT(prop_dictionary)) { %>
                   <%= PropertyData.getDisplayLink(label2URL, label2Linktext, propName_label, value) %>
              <% } %>
            </p> 
<%  
        }
    }
%>

<%
    for (int i_def = 0; i_def<nci_def_label_value.size(); i_def++) {
        String label_value = (String) nci_def_label_value.elementAt(i_def);
        Vector u = StringUtils.parseData(label_value);
        String propName_label = (String) u.elementAt(0);
        
        //System.out.println("===(0)=== propName_label: " + propName_label);
        
        
        String value = (String) u.elementAt(1);
%>        
            <p>
              <b><%=propName_label%>:&nbsp;</b><%=value%>
              <% if (!DataUtils.isNCIT(prop_dictionary)) { %>
                   <%= PropertyData.getDisplayLink(label2URL, label2Linktext, propName_label, value) %>
              <% } %>
            </p> 
<%            
    }
%>


<%
    for (int i_def = 0; i_def<non_nci_def_label_value.size(); i_def++) {
        String label_value = (String) non_nci_def_label_value.elementAt(i_def);
        
        Vector u = StringUtils.parseData(label_value);
        String propName_label = (String) u.elementAt(0);
        
        //System.out.println("===(1)=== propName_label: " + propName_label);
       
        String value = (String) u.elementAt(1);
%>        
            <p>
              <b><%=propName_label%>:&nbsp;</b><%=value%>
              <% if (!DataUtils.isNCIT(prop_dictionary)) { %>
                   <%= PropertyData.getDisplayLink(label2URL, label2Linktext, propName_label, value) %>
              <% } %>
            </p> 
<%            
    }
%>

<%
    for (int i_def = 0; i_def<other_label_value.size(); i_def++) {
        String label_value = (String) other_label_value.elementAt(i_def);
        Vector u = StringUtils.parseData(label_value);
        String propName_label = (String) u.elementAt(0);
        
        if (propName_label.compareToIgnoreCase("Preferred Name") != 0) {
            String value = (String) u.elementAt(1);
%>        
            <p>
              <b><%=propName_label%>:&nbsp;</b><%=value%>
              <% if (!DataUtils.isNCIT(prop_dictionary)) { %>
                   <%= PropertyData.getDisplayLink(label2URL, label2Linktext, propName_label, value) %>
              <% } else if (propName_label.equalsIgnoreCase("NCI Thesaurus Code")) { %>
             
                  <%= PropertyData.getDisplayLink(label2URL, label2Linktext, "caDSR metadata", value) %>
<%

                  if (ValueSetDefinitionConfig.isValueSetHeaderConcept(curr_concept.getEntityCode())) {
                      String vs_uri = ValueSetDefinitionConfig.getValueSetURI(curr_concept.getEntityCode());
                      String url_str = request.getContextPath() + "/ajax?action=create_src_vs_tree&vsd_uri=" + vs_uri;
                      url_str = url_str.replaceAll(":", "%3A");
                      String linktext = "see linked value set";
                  %>
                  &nbsp;<a href="<%= url_str %>">(<%=linktext%>)</a>
                  <%
                  }
                  %>                  
                  <%
                      //////////////////////////////
                      String vs_uri_2 = ValueSetDefinitionConfig.getValueSetURI(curr_concept.getEntityCode());
                      String url_str_2 = request.getContextPath() + "/ajax?action=search_all_value_sets&code=" + curr_concept.getEntityCode();
                      url_str_2 = url_str_2.replaceAll(":", "%3A");
                      String linktext_2 = "search value sets"; 
                   %>   
                  &nbsp;<a href="<%= url_str_2 %>">(<%=linktext_2%>)</a>                     
                  
              <% } %>
            </p> 
<%  
        }
    }
%>


<%
    ncim_metathesaurus_cui_vec = conceptDetails.getNCImCodes(curr_concept);
    //String ncimURL = new ConceptDetails().getNCImURL();
    String ncimURL = dataUtils.getNCImURL();
    if (ncim_metathesaurus_cui_vec.size() > 0) {
        if (ncim_metathesaurus_cui_vec.size() == 1) {
            String t = (String) ncim_metathesaurus_cui_vec.elementAt(0);
            String t0 = "NCI Metathesaurus Link";
            String t1 = t;
            String t2 = ncimURL + "ncimbrowser/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + t;
            if (ncimURL.endsWith("ncimbrowser")) {
                //t2 = new ConceptDetails().getNCImURL() + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + t;
                t2 = dataUtils.getNCImURL() + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + t;
            }
            String t3 = "see NCI Metathesaurus info";
%>
	  <p>
		  <b><%=t0%>:&nbsp;</b><%=t1%>&nbsp;
		  <a href="javascript:redirect_site('<%= t2 %>')">(<%=t3%>)</a>
	  </p>
<%         			  
        } else {
           
%>
            <table class="datatable_960">
           
                <b>NCI Metathesaurus CUI:</b>
<%                
		for (int k=0; k<ncim_metathesaurus_cui_vec.size(); k++) {
		    int lcv = k;
		
			if ((lcv++) % 2 == 0) {
		%>
			      <tr class="dataRowLight">
		<%
			} else {
		%>
			      <tr class="dataRowLight">
		<%
			}		
		
		    String t = (String) ncim_metathesaurus_cui_vec.elementAt(k);
		    String t0 = "NCI Metathesaurus Link";
		    String t1 = t;
		    String t2 = ncimURL + "ncimbrowser/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + t;
		    if (ncimURL.endsWith("ncimbrowser")) {
			//t2 = new ConceptDetails().getNCImURL() + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + t;
			t2 = dataUtils.getNCImURL() + "/ConceptReport.jsp?dictionary=NCI%20MetaThesaurus&code=" + t;
		    }		    
                    String t3 = "see NCI Metathesaurus info";
%>		    
		    <td><%=t1%>&nbsp;<a href="javascript:redirect_site('<%= t2 %>')">(<%=t3%>)</a></td>
		    
		    </tr>
<%
		}
%>		
           </table>
<%           
	}
    } 
%>
<p>
<b>Synonyms &amp; Abbreviations:</b>
<a href="<%=request.getContextPath() %>/pages/concept_details.jsf?dictionary=<%=scheme%>&code=<%=id%>&type=synonym">(see Synonym Details)</a>

<table class="datatable_960">
<%
    HashSet hset2 = new HashSet();
    Vector synonym_values = new Vector();
    
    for (int i=0; i<presentation_vec.size(); i++) {
        String t = (String) presentation_vec.elementAt(i);
        Vector w = StringUtils.parseData(t, "$");
        String presentation_name = (String) w.elementAt(0);
        String presentation_value = (String) w.elementAt(1);
        String isPreferred = (String) w.elementAt(2);

        displayed_properties.add(presentation_name);
        propertyData.add_displayed_property(presentation_name);
        
        if (!hset2.contains(presentation_value)) {
	    synonym_values.add(presentation_value);
	    hset2.add(presentation_value);
        }
        synonym_values = SortUtils.quickSort(synonym_values);
    }

    int row=0;
    for (int j=0; j<synonym_values.size(); j++) {
        String value = (String) synonym_values.elementAt(j);
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
		   <td><%=conceptDetails.encodeTerm(value)%></td>
	      </tr>
        <%
    }

%>
</table>
</p>
  
<p>

<%
int n = 0;
boolean hasExternalSourceCodes = false;
boolean display_UMLS_CUI = true;
String dict_name = (String) request.getSession().getAttribute("dictionary");
String vocab_format = dataUtils.getMetadataValue(dict_name, "format");
if (vocab_format != null && vocab_format.compareTo("RRF") == 0) {
 display_UMLS_CUI= false;
}
 

if (external_source_codes.size() != 0) {
    for (int i=0; i<external_source_codes.size(); i++) {
        String propName = (String) external_source_codes.elementAt(i);
        String propName_label = (String) external_source_codes_label.elementAt(i);
        String prop_url = (String) external_source_codes_url.elementAt(i);
        String prop_linktext = (String) external_source_codes_linktext.elementAt(i);
        
        if (propName.compareTo("UMLS_CUI") != 0 || display_UMLS_CUI) {
            Vector value_vec = (Vector) propertyName2ValueHashMap.get(propName);
            if (value_vec != null && value_vec.size() > 0) {
                hasExternalSourceCodes = true;
                break;
            }
        }
    }
}


if (!hasExternalSourceCodes) {
%>
<b>External Source Codes</b>:&nbsp;<i>None</i>
<%
} else {
%>
  <b>External Source Codes:&nbsp;</b>
  <table class="datatable_960">
  
   <col width="20%">
   <col width="80%">
  
  
    <%
      n = 0;
      for (int i=0; i<external_source_codes.size(); i++) {
        String propName = (String) external_source_codes.elementAt(i);
        String propName_label = (String) external_source_codes_label.elementAt(i);
        String prop_url = (String) external_source_codes_url.elementAt(i);
        String prop_linktext = (String) external_source_codes_linktext.elementAt(i);

        displayed_properties.add(propName);
        propertyData.add_displayed_property(propName);
        
        if (propName.compareTo("UMLS_CUI") != 0 || display_UMLS_CUI) {
        
        Vector value_vec = (Vector) propertyName2ValueHashMap.get(propName);
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
                <%=conceptDetails.encodeTerm(value)%>
                <%
                  if (propName.compareTo("UMLS_CUI") != 0 && prop_url != null && prop_url.compareTo("null") != 0) {
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
    }
    %>
    </table>
    
<%    
    }
%>    
    
</p>
<p>
    <%
      boolean hasOtherProperties = false;
      Vector other_prop_names = propertyData.findOtherPropertyNames();
      if (other_prop_names != null && other_prop_names.size() > 0) {
          hasOtherProperties = true;
      }
if (!hasOtherProperties) {
%>
<b>Other Properties</b>:&nbsp;<i>None</i>
<%
} else {
     String other_properties_str = propertyData.generatePropertyTable(curr_concept, 
         other_prop_names, "<b>Other Properties:</b>");
%> 
<p>
     <%=other_properties_str%> 
</p>     
<%
 }
 %>
</p>
<p>
    <%
      String concept_name = "";
      if (curr_concept.getEntityDescription() != null) {
          concept_name = curr_concept.getEntityDescription().getContent();
          concept_name = concept_name.replaceAll(" ", "_");
      }

      String concept_name_label = "Concept Name:";
      String dict = (String) request.getSession().getAttribute("dictionary");
      
      String primitive = null;
      String primitive_prop_name = "primitive";
      String primitive_label = "Defined Fully by Roles:";
      String defined_label = "Defined Fully by Roles:";
      
      dict = dataUtils.getFormalName(dict);
      
      String vocabulary_format = dataUtils.getMetadataValue(dict, "format");
      Boolean isDefined = null;
      String is_defined = "No";
      if (vocabulary_format != null && vocabulary_format.indexOf("OWL") != -1) {
	      isDefined = curr_concept.getIsDefined();
      }  
      String kind = "not available";
      String kind_prop_name = "Kind";
      String kind_label = "Kind:";
    %>
    
    <%
    if (isDefined != null) {
          if (isDefined.equals(Boolean.TRUE)) is_defined = "Yes";
    %>
	  <b>Additional Concept Data:</b>&nbsp;
	  <table class="datatable_960">
	    <tr class="dataRowLight">
	      <td><%=defined_label%>&nbsp;<%=is_defined%></td>
	      <td>&nbsp;</td>
	    </tr>
	  </table>  
    <%
    } else {
    %>
	  <b>Additional Concept Data:</b>&nbsp;<i>None</i>
    <%
    } 

    %>	  
</p>
<%
  String url = JSPUtils.getBookmarkUrl(request, dictionary, version, concept_id, namespace);
  String bookmark_title = prop_dictionary + "%20" + concept_id;
%>
<p>
   <table class="datatable_960" border="0" cellpadding="0" cellspacing="0" width="700px">
      <tr>
         <td class="dataRowLight">URL: <%=url%></td>
      </tr>
   </table>              
</p>
<%
}
%>
