package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;

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
import org.lexgrid.resolvedvalueset.LexEVSResolvedValueSetService;
import org.lexgrid.resolvedvalueset.impl.LexEVSResolvedValueSetServiceImpl;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.types.CodingSchemeVersionStatus;

import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.Impl.LexBIGServiceImpl;

import org.LexGrid.LexBIG.DataModel.Collections.CodingSchemeTagList;
import org.LexGrid.LexBIG.DataModel.InterfaceElements.RenderingDetail;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;

import org.LexGrid.LexBIG.Extensions.Generic.SupplementExtension;
import org.LexGrid.relations.Relations;
import org.LexGrid.LexBIG.DataModel.Collections.AbsoluteCodingSchemeVersionReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AbsoluteCodingSchemeVersionReference;
import org.LexGrid.concepts.Entity;

/**
 * @author EVS Team
 * @version 1.0
 *
 *      Modification history Initial implementation kim.ong@ngc.com
 *
 */


/**
 * The Class CodingSchemeUtils.
 */


public class CodingSchemeUtils {
	static int MAX_VERSION_LENGTH = 30;
	LexBIGService lbSvc = null;
	HashMap _localName2CodingSchemeNameHashMap = null;

	public CodingSchemeUtils(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
	}

	public void setLexBIGService(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
	}

    public HashMap getLocalName2CodingSchemeNameHashMap() {
		if (_localName2CodingSchemeNameHashMap != null) {
			return _localName2CodingSchemeNameHashMap;
		}
        _localName2CodingSchemeNameHashMap = new HashMap();
        try {
            CodingSchemeRenderingList csrl = null;
            try {
                csrl = lbSvc.getSupportedCodingSchemes();
            } catch (LBInvocationException ex) {
                ex.printStackTrace();
                return null;
            }
            CodingSchemeRendering[] csrs = csrl.getCodingSchemeRendering();
            for (int i = 0; i < csrs.length; i++) {
                int j = i + 1;
                CodingSchemeRendering csr = csrs[i];
                CodingSchemeSummary css = csr.getCodingSchemeSummary();
                Boolean isActive =
                        csr.getRenderingDetail().getVersionStatus().equals(
                            CodingSchemeVersionStatus.ACTIVE);

                String representsVersion = css.getRepresentsVersion();
                if (representsVersion.length() <= MAX_VERSION_LENGTH) {

					CodingSchemeVersionOrTag vt =
						new CodingSchemeVersionOrTag();
					vt.setVersion(representsVersion);

					try {
						CodingScheme cs = lbSvc.resolveCodingScheme(css.getFormalName(), vt);
						String[] localnames = cs.getLocalName();
						for (int m = 0; m < localnames.length; m++) {
							String localname = localnames[m];
							_localName2CodingSchemeNameHashMap.put(localname, cs.getCodingSchemeName());
						}

					} catch (Exception ex) {
						ex.printStackTrace();
					}
			    }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return _localName2CodingSchemeNameHashMap;
    }

    public void dumpHashMap(PrintWriter pw, String label, HashMap hmap) {
		pw.println(label);
		Vector key_vec = new Vector();
		Iterator it = hmap.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			key_vec.add(key);
		}
		key_vec = new SortUtils().quickSort(key_vec);
		for (int i=0; i<key_vec.size(); i++) {
			String key = (String) key_vec.elementAt(i);
			String value = (String) hmap.get(key);
			pw.println("\t" + key + " --> " + value);
		}

	}

    public static void main(String args[]) {
		/*
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		CodingSchemeUtils codingSchemeUtils = new CodingSchemeUtils(lbSvc);
		PrintWriter pw = null;
		String outputfile = "cs.txt";
		try {
			pw = new PrintWriter(outputfile, "UTF-8");
			HashMap localName2CodingSchemeNameHashMap = codingSchemeUtils.getLocalName2CodingSchemeNameHashMap();
            codingSchemeUtils.dumpHashMap(pw, "LocalName2CodingSchemeName", localName2CodingSchemeNameHashMap);

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				pw.close();
				System.out.println("Output file " + outputfile + " generated.");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		*/
	}
}
