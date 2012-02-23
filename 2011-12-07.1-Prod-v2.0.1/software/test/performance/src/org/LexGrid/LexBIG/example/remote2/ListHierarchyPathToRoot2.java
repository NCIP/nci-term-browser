/*
 * Copyright: (c) 2004-2008 Mayo Foundation for Medical Education and
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

package org.LexGrid.LexBIG.example.remote2;


import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.util.AppService;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;
import org.LexGrid.LexBIG.util.Util;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;


import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;

/**
 * Example showing how to determine and display paths from a given
 * concept back to defined root nodes through any hierarchies registered
 * for the coding scheme.

 * This program accepts one parameter (required), indicating the code
 * to evaluate.
 *
 * BACKGROUND: From a database perspective, LexBIG stores relationships
 * internally in a forward direction, source to target.  Due to differences
 * in source formats, however, a wide variety of associations may be
 * used ('PAR', 'CHD', 'isa', 'hasSubtype', etc).  In addition, the
 * direction of navigation may vary ('isa' expands in a reverse direction
 * whereas 'hasSubtype' expands in a forward direction.
 *
 * The intent of the getHierarchy* methods on the LexBIGServiceConvenienceMethods
 * interface is to simplify the process of hierarchy discovery and navigation.
 * These methods significantly reduce the need to understand conventions for root
 * nodes, associations, and direction of navigation for a specific source format.
 *
 */
public class ListHierarchyPathToRoot2 implements Constants {
    
    private CodingSchemeSummary _css = null;
    private String _scheme = "";
    private CodingSchemeVersionOrTag _csvt = null;
    private LexBIGServiceConvenienceMethods _lbscm = null;
    private StringBuffer _buffer = new StringBuffer();
    
    /**
     * Entry point for processing.
     * @param args
     */
    public static void main(String[] args) {
        try {
            new ListHierarchyPathToRoot2().run();
        } catch (Exception e) {
            Util.displayAndLogError("REQUEST FAILED !!!", e);
        }
    }

    public ListHierarchyPathToRoot2() throws LBException {
        AppService service = AppService.getInstance();
        _css = service.getCSS();
        _scheme = service.getScheme();
        _csvt = service.getCSVT();
        _lbscm = service.getLBSCM();
    }

    public void run() throws LBException {
        if (_css == null) 
            return;

        String code = "C32949"; //DYEE
        boolean displayConcepts = true;
        while (true) {
            code = Prompt.prompt("Code (q to Quit)", code).toUpperCase();
            if (code.equalsIgnoreCase("q"))
                break;
            displayConcepts = Prompt.prompt("Display Concepts", displayConcepts);
            process(code, displayConcepts);
            Util.displayMessage("");
            Util.displayMessage(SEPARATOR);
        }
        Util.displayMessage("Quit");
    }
    
    public void run2() throws LBException {
        String codes[] = new String[] {
            //"C13043", "C32949", "C12275", "C12753", "C38366", "C69305"

            //"C32221", "C38626", "C13043", "C32949", "C12275",
            //"C25763", "C34070", "C62484", "C34022",
            //"C13024", "C13091", "C32224",
            
            "C40312", "C40291", "C6840", "C66933", "C6217", "C9370",
            "C6207"
        };
        
        boolean displayConcepts = Prompt.prompt("Display Concepts", true);
        for (int i=0; i<codes.length; ++i) {
            if (i > 0) {
                Util.displayMessage("");
                Util.displayMessage(SEPARATOR);
            }
            process(codes[i], displayConcepts);
        }
        Util.displayMessage("Done");
    }
    
    private void process(String code, boolean displayConcepts) throws LBException {
        _buffer.delete(0, _buffer.length());
        String desc = null;
        try {
            CodedNodeSet cns = _lbscm.createCodeNodeSet(
                    new String[] {code}, _scheme, _csvt);
            ResolvedConceptReferenceList rcrl = cns.resolveToList(null, null, null, 1);
            desc = rcrl.getResolvedConceptReference(0)
                .getEntityDescription().getContent();
        } catch (Exception e) {
            desc = "<not found>";
        }

        _buffer.append("* Focus code: " + code + ":" + desc + "\n");
        // Iterate through all hierarchies ...
        Util.StopWatch stopWatch = new Util.StopWatch();
        String[] hierarchyIDs = _lbscm.getHierarchyIDs(_scheme, _csvt);
        for (int i = 0; i < hierarchyIDs.length; i++) {
            String hierarchyID = hierarchyIDs[i];
            if (displayConcepts)
                _buffer.append("* Hierarchy ID: " + hierarchyID + "\n");
            AssociationList associations = _lbscm.getHierarchyPathToRoot(
                _scheme, _csvt, hierarchyID, code, false,
                LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL, null);
            for (int j = 0; j < associations.getAssociationCount(); j++) {
                Association association = associations.getAssociation(j);
                printChain(association, 0, displayConcepts);
            }
        }
        _buffer.append("* " + stopWatch.getResult() + "\n");
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Displays the given concept chain, taking into account any branches
     * that might be imbedded.
     * @param assoc
     * @param depth
     * @throws LBException
     */
    protected void printChain(Association assoc, int depth, boolean displayConcepts)
        throws LBException {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i <= depth; i++)
            indent.append(INDENT);

        AssociatedConceptList concepts = assoc.getAssociatedConcepts();
        for (int i = 0; i < concepts.getAssociatedConceptCount(); i++) {
            // Print focus of this branch ...
            AssociatedConcept concept = concepts.getAssociatedConcept(i);
            String desc = concept.getEntityDescription() == null ? 
                    "NO DESCRIPTION" : concept.getEntityDescription().getContent();
            if (displayConcepts)
                _buffer.append(indent + assoc.getAssociationName() + "->" +
                    concept.getConceptCode() + ":" + desc + "\n");

            // Find and recurse printing for next batch ...
            AssociationList nextLevel = concept.getSourceOf();
            if (nextLevel != null && nextLevel.getAssociationCount() != 0)
                for (int j = 0; j < nextLevel.getAssociationCount(); j++)
                    printChain(nextLevel.getAssociation(j), depth + 1, displayConcepts);
        }
    }
}

