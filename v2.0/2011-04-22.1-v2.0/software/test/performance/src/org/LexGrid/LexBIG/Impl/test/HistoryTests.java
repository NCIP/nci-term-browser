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
 *      http://www.eclipse.org/legal/epl-v10.html
 * 
 */
package org.LexGrid.LexBIG.Impl.test;

import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.ObjectToString;
import org.LexGrid.LexBIG.util.AppService;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;

public class HistoryTests {
    private HistoryService _hs = null;
    private String _code = null;

    public HistoryTests() throws Exception {
        AppService service = AppService.getInstance();
        LexBIGService lbs = AppService.getLBSvc();
        String codingScheme = service.getScheme();
        _hs = lbs.getHistoryService(codingScheme);
    }

    private void println(String text) {
        System.out.println(text);
    }
    
    private Date getDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return cal.getTime();
    }

    public void test_getBaselines() throws Exception {
        // println(ObjectToString.toString(_hs.getBaselines(null, null)));

        Date after = getDate(2005, Calendar.OCTOBER, 25);
        Date before = getDate(2005, Calendar.DECEMBER, 25);

        println(ObjectToString.toString(_hs.getBaselines(after, before)));
        // println(ObjectToString.toString(_hs.getBaselines(after, null)));
        // println(ObjectToString.toString(_hs.getBaselines(null, before)));
        // println(ObjectToString.toString(_hs.getEarliestBaseline()));
        // println(ObjectToString.toString(_hs.getLatestBaseline()));
    }

    public void test_getSystemRelease() throws Exception {
        // println(ObjectToString.toString(_hs.getSystemRelease(
        // new URI("urn:oid:2.16.840.1.113883.3.26.1.1:03.12a"))));
    }

    public void test_getEditActionList() throws Exception {
        // CodingSchemeVersion temp = new CodingSchemeVersion();
        // temp.setVersion("29-JAN-04");
        // println(ObjectToString.toString(_hs.getEditActionList(temp)));
    }

    public void test_getConceptChangeVersions() throws Exception {
        _code = _code == null ? "C7696" : _code;
        while (true) {
            _code = Prompt.prompt("Code (q to Quit)", _code).toUpperCase();
            if (_code.equalsIgnoreCase("q"))
                break;
            test_getConceptChangeVersions(_code);
            println("");
            println(Constants.SEPARATOR);
        }
    }
    
    public void test_getConceptChangeVersions(String code) throws Exception {
        // Test Creation Version
        println(ObjectToString.toString(_hs
            .getConceptCreationVersion(Constructors.createConceptReference(
                code, null))));

        // Test Change Version
        println(ObjectToString.toString(_hs.getConceptChangeVersions(
            Constructors.createConceptReference(code, null), null, null)));

        Date after = getDate(2004, Calendar.APRIL, 25);
        Date before = getDate(2004, Calendar.AUGUST, 25);
        
        println(ObjectToString.toString(_hs.getConceptChangeVersions(
            Constructors.createConceptReference(code, null), after, before)));

        println(ObjectToString.toString(_hs.getConceptChangeVersions(
            Constructors.createConceptReference(code, null), null, before)));

        println(ObjectToString.toString(_hs.getConceptChangeVersions(
            Constructors.createConceptReference(code, null), after, null)));
    }
    
    public void test_getCodeEditActionList() throws Exception {
        _code = _code == null ? "C12799" : _code;
        while (true) {
            _code = Prompt.prompt("Code (q to Quit)", _code).toUpperCase();
            if (_code.equalsIgnoreCase("q"))
                break;
            test_getCodeEditActionList(_code);
            println("");
            println(Constants.SEPARATOR);
        }
    }
    
    public void test_getCodeEditActionList(String code) throws Exception {
        println(ObjectToString.toString(_hs.getEditActionList(Constructors
            .createConceptReference(code, null), null, null)));
    }

    public void test_getURIEditActionList() throws Exception {
        println(ObjectToString.toString(_hs.getEditActionList(Constructors
            .createConceptReference("", null), new URI(
            "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#:04.03n"))));
    }

    public static void main(String[] args) throws Exception {
        HistoryTests tests = new HistoryTests();
        // tests.test_getBaselines();
        // tests.test_getSystemRelease();
        // tests.test_getEditActionList();
        // tests.test_getConceptChangeVersions();
        tests.test_getCodeEditActionList();
        // tests.test_getURIEditActionList();
    }
}