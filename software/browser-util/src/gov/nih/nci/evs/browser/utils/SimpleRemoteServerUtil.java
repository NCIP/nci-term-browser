package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;
import java.text.*;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeRenderingList;
import org.LexGrid.LexBIG.DataModel.Collections.ModuleDescriptionList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.CodingSchemeRendering;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.ModuleDescription;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;

import gov.nih.nci.evs.security.SecurityToken;
import gov.nih.nci.system.client.ApplicationServiceProvider;

import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.naming.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;


import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;

import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;


/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class SimpleRemoteServerUtil.
 */


public class SimpleRemoteServerUtil {
    private String serviceUrl = null;
    private NameAndValueList securityTokenList = null;

    public SimpleRemoteServerUtil(String serviceUrl) {
		this.serviceUrl = serviceUrl;
		securityTokenList = new NameAndValueList();
	}

    public String getServiceUrl() {
		return serviceUrl;
	}

    public LexBIGService getLexBIGService() {
		return getLexBIGService(serviceUrl);
	}

	public void setSecurityTokens(NameAndValueList nvl) {
		securityTokenList = nvl;
	}

	public void setSecurityTokens(Vector names, Vector values) {
		securityTokenList = createNameAndValueList(names, values);
	}

	public NameAndValueList createNameAndValueList(Vector names, Vector values) {
		if (names == null || values == null) return null;
		if (names.size() != values.size()) return null;
		NameAndValueList nvl = new NameAndValueList();
		for (int i=0; i<names.size(); i++) {
			String name = (String) names.elementAt(i);
			String value = (String) values.elementAt(i);
			NameAndValue nv = new NameAndValue();
			nv.setName(name);
			nv.setContent(value);
			nvl.addNameAndValue(nv);
		}
		return nvl;
	}

    public LexBIGService getLexBIGService(String serviceUrl) {
		if (serviceUrl == null || serviceUrl.compareTo("") == 0 || serviceUrl.compareToIgnoreCase("null") == 0) {
			LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
			return lbSvc;
		}
		LexEVSDistributed lbSvc = null;
		try {
			lbSvc = (LexEVSDistributed)ApplicationServiceProvider.getApplicationServiceFromUrl(serviceUrl, "EvsServiceInfo");
			for (int i=0; i<securityTokenList.getNameAndValueCount(); i++) {
				NameAndValue nv = (NameAndValue) securityTokenList.getNameAndValue(i);
				String name = nv.getName();
				String value = nv.getContent();
				SecurityToken token = new SecurityToken();
				token.setAccessToken(value);
				lbSvc.registerSecurityToken(name, token);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return lbSvc;
	}
}
