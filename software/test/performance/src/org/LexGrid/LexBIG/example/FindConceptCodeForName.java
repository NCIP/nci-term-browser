/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

/*
 * Copyright: (c) 2004-2007 Mayo Foundation for Medical Education and
 * Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the
 * triple-shield Mayo logo are trademarks and service marks of MFMER.
 *
 * Except as contained in the copyright notice above, or as used to identify
 * MFMER as the author of this software, the trade names, trademarks, service
 * marks, or product names of the copyright holder shall not be used in
 * advertising, promotion or otherwise in connection with this software without
 * prior written authorization of the copyright holder.
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.eclipse.org/legal/epl-v10.html
 *
 */

package org.LexGrid.LexBIG.example;

import gov.nih.nci.system.applicationservice.EVSApplicationService;


import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.util.RemoteServerUtil2;
import org.LexGrid.LexBIG.util.Util;

/**
 * Example showing how to find the concept code associated with a concept name.
 */
public class FindConceptCodeForName {
	public FindConceptCodeForName() {
		super();
	}

	/**
	 * Entry point for processing.
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
				"Example: FindConceptCodeForName \"Anatomy_Kind\"");
			return;
		};

		try {
			String name = args[0];
			new FindConceptCodeForName().run(name);
		} catch (Exception e) {
			Util.displayAndLogError("REQUEST FAILED !!!", e);
		}
	}

	public void run(String name) throws LBException{
		CodingSchemeSummary css = Util.promptForCodeSystem();
		if (css != null) {
			CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
			csvt.setVersion(css.getRepresentsVersion());
			EVSApplicationService lbs = RemoteServerUtil2.createLexBIGService();
			//String code = new ConvenienceMethods(LexBIGServiceImpl.defaultInstance()).nameToCode(name, css.getCodingSchemeURN(), csvt);
			String code = new ConvenienceMethods(lbs).nameToCode(name, css.getCodingSchemeURN(), csvt);

			Util.displayMessage(
				code == null ? "Code not found" : "Matching code: " + code);
		}
	}

}
