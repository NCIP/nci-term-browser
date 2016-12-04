package gov.nih.nci.evs.browser.utils;

import java.io.*;
import java.util.*;
import java.net.URI;
import javax.servlet.*;
import javax.servlet.http.*;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.bean.ValueSetConfig;
import gov.nih.nci.evs.browser.common.*;
import gov.nih.nci.evs.browser.utils.*;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
 *          Modification history Initial implementation kim.ong@ngc.com
 *
 */

public class VSDCExportUtils {
    static String CSVFILE = "value_set_report_config.csv";
    public VSDCExportUtils() {

	}

	public void exportValueSetDefinitionConfigToCSV(HttpServletRequest request, HttpServletResponse response) {
		String csvfile = CSVFILE;
		StringBuffer sb = valueSetDefinitionConfig2StringBuffer(ValueSetDefinitionConfig.valueSetConfigHashMap);
		exportToCSV(request, response, sb, csvfile);
	}

/*
NDFRT Mechanism of Action|http://ndfrt:MoA|ftp://ftp1.nci.nih.gov/pub/cacore/EVS/FDA/ndfrt/MechanismOfAction.xls|1:all
NDFRT Physiologic Effects|http://ndfrt:PE|ftp://ftp1.nci.nih.gov/pub/cacore/EVS/FDA/ndfrt/PhysiologicEffect.xls|1:all
NDFRT Structural Class|http://ndfrt:SC|ftp://ftp1.nci.nih.gov/pub/cacore/EVS/FDA/ndfrt/StructuralClass.xls|1:all
*/

	public StringBuffer valueSetDefinitionConfig2StringBuffer(HashMap valueSetConfigHashMap) {
        StringBuffer sb = new StringBuffer();
		Set<Map.Entry<String, String>> set = ValueSetDefinitionConfig.valueSetConfigHashMap.entrySet();
		Vector w = new Vector();
		for (Map.Entry entry : set) {
			StringBuffer buf = new StringBuffer();
			String uri = (String) entry.getKey();
			ValueSetConfig vsc = (ValueSetConfig) entry.getValue();
			buf.append(vsc.getName()).append("|");
			buf.append(vsc.getUri()).append("|");
        	buf.append(vsc.getReportURI()).append("|");
        	buf.append(vsc.getExtractionRule());
        	buf.append("\n");
        	sb.append(buf.toString());
		}
        return sb;
	}

    public void exportToCSV(HttpServletRequest request, HttpServletResponse response, StringBuffer sb, String csvfile) {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename="
				+ csvfile);

		response.setContentLength(sb.length());
		try {
			ServletOutputStream ouputStream = response.getOutputStream();
			ouputStream.write(sb.toString().getBytes("UTF-8"), 0, sb.length());
			ouputStream.flush();
			ouputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			sb.append("WARNING: Export to CVS action failed.");
		}
		FacesContext.getCurrentInstance().responseComplete();
		return;
	}
}
