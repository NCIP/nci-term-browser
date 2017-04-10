package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.bean.*;

import java.util.*;
import java.net.URI;
import java.io.*;


import javax.faces.context.*;
import javax.faces.event.*;
import javax.faces.model.*;
import javax.servlet.http.*;

import gov.nih.nci.evs.browser.utils.*;
import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.common.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Impl.Extensions.GenericExtensions.*;
import org.apache.log4j.*;
import javax.faces.event.ValueChangeEvent;

import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.lexgrid.valuesets.LexEVSValueSetDefinitionServices;
import org.LexGrid.valueSets.ValueSetDefinition;
import org.LexGrid.commonTypes.Source;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.lexgrid.valuesets.dto.ResolvedValueSetDefinition;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import javax.servlet.ServletOutputStream;
import org.LexGrid.concepts.*;
import org.lexgrid.valuesets.dto.ResolvedValueSetCodedNodeSet;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.concepts.Definition;
import org.LexGrid.commonTypes.PropertyQualifier;
import org.LexGrid.commonTypes.Property;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;

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

public class ValueSetMetadataUtils {
    public HashMap vsdURI2MetadataHashMap = null;
    LexEVSValueSetDefinitionServices vsd_service = null;

    public ValueSetMetadataUtils(LexEVSValueSetDefinitionServices vsd_service) {
        this.vsd_service = vsd_service;
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

	public HashMap getValueSetDefinitionMetadata() {
		if (vsdURI2MetadataHashMap != null) return vsdURI2MetadataHashMap;
		HashMap hmap = new HashMap();
		try {
			List list = vsd_service.listValueSetDefinitionURIs();
			if (list == null || list.size() == 0) return null;
			for (int i=0; i<list.size(); i++) {
				String uri = (String) list.get(i);
				ValueSetDefinition vsd = findValueSetDefinitionByURI(uri);
				String metadata = getValueSetDefinitionMetadata(vsd);
				hmap.put(uri, metadata);
			}
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return hmap;
	}


	public String getValueSetDefinitionMetadata(String vsd_uri) {
		if (vsd_uri == null) return null;
		if (vsdURI2MetadataHashMap != null) {
			return (String) vsdURI2MetadataHashMap.get(vsd_uri);
		}
		ValueSetDefinition vsd = findValueSetDefinitionByURI(vsd_uri);
		if (vsd == null) return null;
		return getValueSetDefinitionMetadata(vsd);
	}


    public String getValueSetDefinitionMetadata(ValueSetDefinition vsd) {
		if (vsd== null) return null;
		String name = "";
		String uri = "";
		String description = "";
		String domain = "";
		String src_str = "";

		//String supportedSourceStr = "";
		StringBuffer buf = new StringBuffer();

		uri = vsd.getValueSetDefinitionURI();
		name = vsd.getValueSetDefinitionName();
		if (name == null || name.compareTo("") == 0) {
			name = "<NOT ASSIGNED>";
		}

		domain = vsd.getConceptDomain();
		if (domain == null || domain.compareTo("") == 0) {
			domain = "<NOT ASSIGNED>";
		}

		java.util.Enumeration<? extends Source> sourceEnum = vsd.enumerateSource();

		while (sourceEnum.hasMoreElements()) {
			Source src = (Source) sourceEnum.nextElement();
			src_str = src_str + src.getContent() + ";";
		}
		if (src_str.length() > 0) {
			src_str = src_str.substring(0, src_str.length()-1);
		}

		if (src_str == null || src_str.compareTo("") == 0) {
			src_str = "<NOT ASSIGNED>";
		}

		if (vsd.getEntityDescription() != null) {
			description = vsd.getEntityDescription().getContent();
			if (description == null || description.compareTo("") == 0) {
				description = "<NO DESCRIPTION>";
			}
		} else {
			description = "<NO DESCRIPTION>";
		}
		Mappings mappings = vsd.getMappings();
        java.util.Enumeration<? extends SupportedSource> supportedSourceEnum = mappings.enumerateSupportedSource();

		while (supportedSourceEnum.hasMoreElements()) {
			SupportedSource src = (SupportedSource) supportedSourceEnum.nextElement();
			buf.append(src.getContent() + ";");
		}
		String supportedSourceStr = buf.toString();

		if (supportedSourceStr.length() > 0) {
			supportedSourceStr = supportedSourceStr.substring(0, supportedSourceStr.length()-1);
		}
		if (supportedSourceStr == null || supportedSourceStr.compareTo("") == 0) {
			supportedSourceStr = "<NOT ASSIGNED>";
		}

		String defaultCodingScheme = vsd.getDefaultCodingScheme();
		return name + "|" + uri + "|" + description + "|" + domain + "|" + src_str + "|" + supportedSourceStr + "|" + defaultCodingScheme;
	}

	public static String describeMetadata() {
		return "name|uri|description|domain|sources|supportedSources|defaultCodingScheme";
	}

    public String getValueSetDefaultCodingScheme(String vsd_uri) {
		String metadata = getValueSetDefinitionMetadata(vsd_uri);
		Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(metadata);
		String defaultCodingScheme = (String) u.elementAt(6);
		return defaultCodingScheme;
	}

    public String getValueSetSupportedSource(String vsd_uri) {
		ValueSetDefinition vsd = findValueSetDefinitionByURI(vsd_uri);
		Mappings mappings = vsd.getMappings();
		SupportedSource[] supporetedSources = mappings.getSupportedSource();
		for (int i=0; i<supporetedSources.length; i++) {
			SupportedSource supportedSource = supporetedSources[i];
			return supportedSource.getContent();
		}
        return null;
	}

    public String getValueSetName(String vsd_uri) {
		String metadata = getValueSetDefinitionMetadata(vsd_uri);
		Vector u = gov.nih.nci.evs.browser.utils.StringUtils.parseData(metadata);
		String name = (String) u.elementAt(0);
		return name;
	}

    public static boolean isNCIT(String scheme) {
        if (scheme == null) return true;
        return Arrays.asList(Constants.NCIT_NAMES).contains(scheme);
    }
}
