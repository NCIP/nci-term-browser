
<%

  if (type.compareTo("mapping") == 0 || type.compareTo("all") == 0) {
  HashMap display_name_hmap = new HashMap();


  Entity concept_curr = (Entity) request.getSession().getAttribute("concept");
  JSPUtils.JSPHeaderInfo mapping_info = new JSPUtils.JSPHeaderInfo(request);
  String scheme_curr = mapping_info.dictionary;
  String version_curr = mapping_info.version;
  String code_curr = (String) request.getSession().getAttribute("code");
  
  boolean isMappingCS = DataUtils.isMapping(scheme_curr, version_curr);
  
   
  
  
  
  
  if(!isMappingCS) {
  
  Vector mapping_uri_version_vec = DataUtils.getMappingCodingSchemesEntityParticipatesIn(code_curr, null);


                String source_scheme = null;//"NCI_Thesaurus";
                String source_version = null;// "10.06e";
                String source_namespace = null;
                String target_scheme = null;// "ICD_9_CM";
                String target_version = null;// "2010";

                String source_code = null;
                String source_name = null;
                String rel = null;
                String score = null;
                String target_code = null;
                String target_name = null;
                String target_namespace = null;
                MappingData mappingData = null;


 %>
  <table border="0" width="708px">

 <%
 
         Entity con = (Entity) request.getSession().getAttribute("concept");
 	 Vector meta_cui_vec = DataUtils.getMatchedMetathesaurusCUIs(con);//scheme_curr, version_curr, null, code_curr);


%>  
  
    <tr>
      <td class="textsubtitle-blue" align="left">
      
<%			
if (type != null && type.compareTo("all") == 0) {
%>
    <A name="mappings">Mapping Details</A>
<%    
} else {
%>
    Mapping Details
<%    
}
%>        
      
      
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
              



         if (meta_cui_vec != null && meta_cui_vec.size() > 0)
         {
                        String ncim_url = NCItBrowserProperties.getNCIM_URL();
                        
%> 
          <b>Mapping through NCI Metathesaurus:</b>
          <table>
          <tr>
              <td>
                  <table>
<%                        
 			for(int lcv=0; lcv<meta_cui_vec.size(); lcv++) {
 			       String meta_cui = (String) meta_cui_vec.elementAt(lcv);
 			       String ncim_cs_name = "NCI Metathesaurus";
 
 			%>
 			       <tr>
 				 <td class="textbody">
  					    <a href="<%= ncim_url %>/ConceptReport.jsp?dictionary=<%=ncim_cs_name%>&code=<%=meta_cui%>&type=synonym" target="_blank">
 					      <i class="textbody"><%=meta_cui%></i>
 					      <img src="<%= request.getContextPath() %>/images/window-icon.gif" width="10" height="11" border="0" alt="<%=ncim_cs_name%>" />
 					    </a>
 
 				 </td>
 			       </tr>
 			<%
 			}
                 %> 
 
 		</table>
             </td>
         </tr>
         </table>
         <hr></hr>

 
 
 	  <%
 	}
    %>



            

        <b>Maps To:</b>
<%        
        for(int lcv=0; lcv<mapping_uri_version_vec.size(); lcv++) {
    
        
           String mapping_uri_version = (String) mapping_uri_version_vec.elementAt(lcv);
           Vector ret_vec = DataUtils.parseData(mapping_uri_version, "|");
           String mapping_cs_uri = (String) ret_vec.elementAt(0);
           String mapping_cs_version = (String) ret_vec.elementAt(1);
           String mapping_cs_name = DataUtils.uri2CodingSchemeName(mapping_cs_uri);

boolean show_rank_column = true;
String map_rank_applicable = DataUtils.getMetadataValue(mapping_cs_name, mapping_cs_version, "map_rank_applicable");
if (map_rank_applicable != null && map_rank_applicable.compareTo("false") == 0) {
    show_rank_column = false;
}   


           List list = new MappingSearchUtils().getMappingRelationship(
                       mapping_cs_uri, mapping_cs_version, code_curr, 1);


if (list != null && list.size() > 0) {

           
%>        
    <p></p>Mapping Source: <%=mapping_cs_name%>
          <table class="datatable">

          <th class="dataTableHeader" width="60px" scope="col" align="left">Source</th>


          <th class="dataTableHeader" scope="col" align="left">
                 Source Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Source Name
          </th>

          <th class="dataTableHeader" width="30px" scope="col" align="left">
                 REL
          </th>

<%
if (show_rank_column) {
%>
          <th class="dataTableHeader" width="35px" scope="col" align="left">
                 Map Rank
          </th>
<%
}
%>

          <th class="dataTableHeader" width="60px" scope="col" align="left">Target</th>

          <th class="dataTableHeader" scope="col" align="left">
                 Target Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Target Name
          </th>
    <%

          int n2 = 0;
          
          
          for (int k=0; k<list.size(); k++) {
                mappingData = (MappingData) list.get(k);
		source_code = mappingData.getSourceCode();
		source_name = mappingData.getSourceName();
		source_namespace = mappingData.getSourceCodeNamespace();
		
	
		
        if (display_name_hmap.containsKey(source_namespace)) {
            source_namespace = (String) display_name_hmap.get(source_namespace);
        } else {
            String short_name = DataUtils.getMappingDisplayName(mapping_cs_name, source_namespace);
            display_name_hmap.put(source_namespace, short_name);
            source_namespace = short_name;
        }

		rel = mappingData.getRel();
		score = new Integer(mappingData.getScore()).toString();
		target_code = mappingData.getTargetCode();
		target_name = mappingData.getTargetName();
		target_namespace = mappingData.getTargetCodeNamespace();
		

        if (display_name_hmap.containsKey(target_namespace)) {
            target_namespace = (String) display_name_hmap.get(target_namespace);
        } else {
            String short_name = DataUtils.getMappingDisplayName(mapping_cs_name, target_namespace);
            display_name_hmap.put(target_namespace, short_name);
            target_namespace = short_name;
        }    
        

		source_scheme = DataUtils.getFormalName(mappingData.getSourceCodingScheme());
		source_version = mappingData.getSourceCodingSchemeVersion();
		target_scheme = DataUtils.getFormalName(mappingData.getTargetCodingScheme());
		target_version = mappingData.getTargetCodingSchemeVersion();         
          
        %>
        
        <tr>
        
        
        <td class="datacoldark" scope="row"><%=source_namespace%></td>
        <td class="datacoldark">
<a href="#"
      onclick="javascript:window.location='<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&version=<%=source_version%>&code=<%=source_code%>'">
      <%=source_code%>
</a>


        </td>
        <td class="datacoldark"><%=source_name%></td>


        <td class="textbody"><%=rel%></td>
        
<%
if (show_rank_column) {
%>        
        <td class="textbody"><%=score%></td>
<%
}
%>



        <td class="datacoldark"><%=target_namespace%></td>
        <td class="datacoldark">

<a href="#"
      onclick="javascript:window.location='<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&version=<%=target_version%>&code=<%=target_code%>'">
      <%=target_code%>
</a>

                    </td>
        <td class="datacoldark"><%=target_name%></td>


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
           
boolean show_rank_column = true;
String map_rank_applicable = DataUtils.getMetadataValue(mapping_cs_name, mapping_cs_version, "map_rank_applicable");
if (map_rank_applicable != null && map_rank_applicable.compareTo("false") == 0) {
    show_rank_column = false;
}   


           List list = new MappingSearchUtils().getMappingRelationship(
                       mapping_cs_uri, mapping_cs_version, code_curr, -1);


if (list != null && list.size() > 0) {

%>
      
    <p></p>Mapping Source: <%=mapping_cs_name%>
          <table class="datatable">

          <th class="dataTableHeader" width="60px" scope="col" align="left">Source</th>


          <th class="dataTableHeader" scope="col" align="left">
                 Source Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Source Name
          </th>

          <th class="dataTableHeader" width="30px" scope="col" align="left">
                 REL
          </th>

<%
if (show_rank_column) {
%>
          <th class="dataTableHeader" width="35px" scope="col" align="left">
                 Map Rank
          </th>
<%
}
%>

          <th class="dataTableHeader" width="60px" scope="col" align="left">Target</th>

          <th class="dataTableHeader" scope="col" align="left">
                 Target Code
          </th>

          <th class="dataTableHeader" scope="col" align="left">
                 Target Name
          </th>


    <%

          int n2 = 0;
          for (int k=0; k<list.size(); k++) {
                mappingData = (MappingData) list.get(k);
		source_code = mappingData.getSourceCode();
		source_name = mappingData.getSourceName();
		source_namespace = mappingData.getSourceCodeNamespace();

		if (display_name_hmap.containsKey(source_namespace)) {
		    source_namespace = (String) display_name_hmap.get(source_namespace);
		} else {
		    String short_name = DataUtils.getMappingDisplayName(mapping_cs_name, source_namespace);
		    display_name_hmap.put(source_namespace, short_name);
		    source_namespace = short_name;
		}
		rel = mappingData.getRel();
		score = new Integer(mappingData.getScore()).toString();
		target_code = mappingData.getTargetCode();
		target_name = mappingData.getTargetName();
		target_namespace = mappingData.getTargetCodeNamespace();

		if (display_name_hmap.containsKey(target_namespace)) {
		    target_namespace = (String) display_name_hmap.get(target_namespace);
		} else {
		    String short_name = DataUtils.getMappingDisplayName(mapping_cs_name, target_namespace);
		    display_name_hmap.put(target_namespace, short_name);
		    target_namespace = short_name;
		} 
		
		source_scheme = DataUtils.getFormalName(mappingData.getSourceCodingScheme());
		source_version = mappingData.getSourceCodingSchemeVersion();
		target_scheme = DataUtils.getFormalName(mappingData.getTargetCodingScheme());
		target_version = mappingData.getTargetCodingSchemeVersion();         


        %>

        <tr>
        <td class="datacoldark" scope="row"><%=source_namespace%></td>
        <td class="datacoldark">
<a href="#"
      onclick="javascript:window.location='<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=source_scheme%>&version=<%=source_version%>&code=<%=source_code%>'">
      <%=source_code%>
</a>

        </td>
        <td class="datacoldark"><%=source_name%></td>


        <td class="textbody"><%=rel%></td>
        
<%
if (show_rank_column) {
%>        
        <td class="textbody"><%=score%></td>
<%
}
%>



        <td class="datacoldark"><%=target_namespace%></td>
        <td class="datacoldark">

<a href="#"
      onclick="javascript:window.location='<%= request.getContextPath() %>/ConceptReport.jsp?dictionary=<%=target_scheme%>&version=<%=target_version%>&code=<%=target_code%>'">
      <%=target_code%>
</a>

                    </td>
        <td class="datacoldark"><%=target_name%></td>

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
    }
%>
