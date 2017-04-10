package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.net.URI;
import java.text.*;
import java.util.*;
import java.sql.*;

import gov.nih.nci.system.client.ApplicationServiceProvider;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.naming.Mappings;
import org.LexGrid.naming.SupportedCodingScheme;
import org.LexGrid.util.PrintUtility;
import org.LexGrid.valueSets.DefinitionEntry;
import org.LexGrid.valueSets.EntityReference;
import org.LexGrid.valueSets.PropertyMatchValue;
import org.LexGrid.valueSets.PropertyReference;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.valueSets.types.DefinitionOperator;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.lexgrid.valuesets.dto.ResolvedValueSetDefinition;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.*;
import org.LexGrid.LexBIG.Utility.Iterators.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.relations.Relations;
import org.LexGrid.versions.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.DataModel.Core.types.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;


import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Direction;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOption;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.MappingSortOptionName;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.QualifierSortOption;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;

import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.relations.AssociationPredicate;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;

import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;


import org.apache.log4j.*;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping;
import org.LexGrid.LexBIG.Extensions.Generic.MappingExtension.Mapping.SearchContext;

import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSApplicationService;
import org.lexgrid.valuesets.impl.LexEVSValueSetDefinitionServicesImpl;

import org.json.*;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


public class ValueSetDefUtils {
	private static Logger _logger = Logger.getLogger(ValueSetDefUtils.class);
	private List valueSetDefinitionURIList = null;
	LexBIGService lbSvc = null;
	LexEVSValueSetDefinitionServices vsd_service = null;
	HashMap vsdUri2NameMap = null;

    public ValueSetDefUtils(LexBIGService lbSvc, LexEVSValueSetDefinitionServices vsd_service) {
		this.lbSvc = lbSvc;
        this.vsd_service = vsd_service;
        valueSetDefinitionURIList = this.vsd_service.listValueSetDefinitionURIs();
        createVsdUri2NameMap();
	}

	public void createVsdUri2NameMap() {
		vsdUri2NameMap = new HashMap();
		for (int i=0; i<valueSetDefinitionURIList.size(); i++) {
			String vsd_uri = (String) valueSetDefinitionURIList.get(i);
			ValueSetDefinition vsd = findValueSetDefinitionByURI(vsd_uri);
			String vsd_name = vsd.getValueSetDefinitionName();
		    vsdUri2NameMap.put(vsd_uri, vsd_name);
		}
	}

	public HashMap getVsdUri2NameMap() {
		return vsdUri2NameMap;
	}

    public void exportValueSetDefinitions(String directory_name) {
		String user_dir = System.getProperty("user.dir");
		String dir_pathname = user_dir + File.separator + directory_name;
		File dir = new File(dir_pathname);
		if (!dir.exists()) {
			System.out.println("Creating directory: " + directory_name);
			boolean result = false;
			try{
				dir.mkdir();
				result = true;
			}
			catch(SecurityException ex){
				ex.printStackTrace();
				return;
			}
			if(result) {
				System.out.println("Directory " + directory_name + " created.");
			}
		}
		Vector error_vec = new Vector();
		PrintWriter pw = null;
		for (int i=0; i<valueSetDefinitionURIList.size(); i++) {
			String vsd_uri = (String) valueSetDefinitionURIList.get(i);
			String vsd_name = (String) vsdUri2NameMap.get(vsd_uri);
			vsd_name = vsd_name.replaceAll(" ", "_");
			vsd_name = vsd_name.replaceAll("/", "_");
			vsd_name = vsd_name.replaceAll(":", "_");
			String xmlfile = dir_pathname + File.separator + vsd_name + ".txt";
			String t = valueSetDefinition2XMLString(vsd_uri);
			try {
				pw = new PrintWriter(xmlfile, "UTF-8");
				pw.println(t);
				System.out.println("XML file " + xmlfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
				error_vec.add(xmlfile);
			} finally {
				try {
					pw.close();
					int j = i+1;
					System.out.println("(" + j + ") Output file " + xmlfile + " generated.");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		System.out.println("\nErrors: " );
		for (int i=0; i<error_vec.size(); i++) {
			int j = i+1;
			String t = (String) error_vec.elementAt(i);
			System.out.println("(" + j + ") " + t);
		}
	}

    public String valueSetDefinition2XMLString(String uri) {
        String s = null;
        String valueSetDefinitionRevisionId = null;
        try {
			URI valueSetDefinitionURI = new URI(uri);
			StringBuffer buf = vsd_service.exportValueSetDefinition(valueSetDefinitionURI, valueSetDefinitionRevisionId);
            s = buf.toString();
        } catch (Exception ex) {
           ex.printStackTrace();
        }
		return s;
	}


    public ValueSetDefinition findValueSetDefinitionByURI(String uri) {
		String valueSetDefinitionRevisionId = null;
		try {
			ValueSetDefinition vsd = vsd_service.getValueSetDefinition(new URI(uri), valueSetDefinitionRevisionId);
			return vsd;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


/*
    public static void main(String[] args) throws Exception {
		String output_dir = args[0];
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		LexEVSValueSetDefinitionServices vsd_service = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        ValueSetDefUtils valueSetDefUtils = new ValueSetDefUtils(lbSvc, vsd_service);

		long ms = System.currentTimeMillis();
		valueSetDefUtils.exportValueSetDefinitions(output_dir);
		System.out.println("exportValueSetDefinitions run time (ms): " + (System.currentTimeMillis() - ms));
    }
*/
}



