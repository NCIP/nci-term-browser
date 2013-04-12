/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package org.LexGrid.LexBIG.example.remote2;


import java.util.Iterator;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.util.AppService;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;
import org.LexGrid.LexBIG.util.Util;
import org.LexGrid.commonTypes.EntityDescription;

/**
 * Example showing how to determine and display an unsorted list of root and
 * subsumed nodes, up to a specified depth, for hierarchical relationships.
 *
 * This program accepts two parameters:
 *
 * The first parameter indicates the depth to display for
 * the hierarchy.  If 1, nodes immediately subsumed by the root are displayed.
 * If 2, grandchildren are displayed, etc.  If absent or < 0, a default
 * depth of 3 is assumed.
 *
 * The second parameter optionally indicates a specific hierarchy to navigate.
 * If provided, this must match a registered identifier in the coding scheme
 * supported hierarchy metadata.  If left unspecified, all hierarchical
 * associations are navigated.  If an incorrect value is specified, a list of
 * supported values will be output for future reference.
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
public class ListHierarchy2 implements Constants {
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
            new ListHierarchy2().run();
        } catch (Exception e) {
            Util.displayAndLogError("REQUEST FAILED !!!", e);
        }
    }
    
    public ListHierarchy2() throws LBException {
        AppService service = AppService.getInstance();
        _scheme = service.getScheme();
        _csvt = service.getCSVT();
        _lbscm = service.getLBSCM();
    }

    public void run() throws LBException {
        String code = "C28428";
        code = "C32221"; //DYEE
        boolean displayConcepts = true;
        int maxDepth = 3;
        
        while (true) {
            code = Prompt.prompt("Code (q to Quit)", code).toUpperCase();
            if (code.equalsIgnoreCase("q"))
                break;
            displayConcepts = Prompt.prompt("Display Concepts", displayConcepts);
            maxDepth = Prompt.prompt("Max Depth", maxDepth);
            process(code, maxDepth, displayConcepts);
            Util.displayMessage("");
            Util.displayMessage(SEPARATOR);
        }
        Util.displayMessage("Quit");
    }
    
    public void run2() throws LBException {
        int maxDepth = 3;
        boolean displayConcepts = Prompt.prompt("Display Concepts", true);
        String codes[] = new String[] {
            "C32221", "C38626", "C13043", "C32949", "C12275",
            "C25763", "C34070", "C62484", "C34022",
            "C13024", "C13091", "C32224",
        };
        
        for (int i=0; i<codes.length; ++i) {
            if (i > 0) {
                Util.displayMessage("");
                Util.displayMessage(SEPARATOR);
            }
            process(codes[i], maxDepth, displayConcepts);
        }
        Util.displayMessage("Done");
    }
    
    private void process(String code, int maxDepth, boolean displayConcepts)
        throws LBException {
        _buffer.delete(0, _buffer.length());
        Util.StopWatch stopWatch = new Util.StopWatch();
        printHierarchies(code, maxDepth, displayConcepts);
        _buffer.append("* Concept code: " + code + "\n");
        _buffer.append("* " + stopWatch.getResult() + "\n");
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Discovers all registered hierarchies for the coding scheme and
     * display each in turn.
     * @param code
     * @param maxDepth
     * @param displayConcepts
     * @throws LBException
     */
    protected void printHierarchies(
        String code, int maxDepth, boolean displayConcepts) throws LBException
    {
        CodedNodeSet cns = _lbscm.createCodeNodeSet(new String[] {code}, 
            _scheme, _csvt);
        ResolvedConceptReferenceList roots = cns.resolveToList(null, null, null, 1);
        
        // Print all branches from root ...
        //DYEE: ResolvedConceptReferenceList roots = lbscm.getHierarchyRoots(scheme, csvt, hierarchyID);
        for (int j = 0; j < roots.getResolvedConceptReferenceCount(); j++) {
            ResolvedConceptReference root = roots.getResolvedConceptReference(j);
            printHierarchyBranch(1, null, root, 1, maxDepth, null, displayConcepts);
        }
    }

    /**
     * Handles recursive display of hierarchy for the given start node,
     * up to the maximum specified depth.
     * @param hierarchyID
     * @param branchRoot
     * @param currentDepth
     * @param maxDepth
     * @param assocName
     * @param displayConcepts
     * @throws LBException
     */
    protected void printHierarchyBranch(
            int ctr,
            String hierarchyID,
            ResolvedConceptReference branchRoot,
            int currentDepth,
            int maxDepth,
            String assocName,
            boolean displayConcepts) throws LBException
    {
        // Print the referenced node; indent based on current depth ...
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < currentDepth-1; i++)
            indent.append(INDENT);

        String code = branchRoot.getConceptCode();
        EntityDescription desc = branchRoot.getEntityDescription();
        String conceptInfo = code + ":" + (desc != null ? desc.getContent() : "");
        if (displayConcepts)
            _buffer.append(indent + Integer.toString(ctr) + ") " + 
                (assocName != null ? (assocName+"->") : "") +
                conceptInfo + "\n");

        // Print each association and recurse ...
        if (currentDepth < maxDepth) {
            AssociationList assocList = _lbscm.getHierarchyLevelNext(
                _scheme, _csvt, hierarchyID, code, false, null);
            if (assocList == null)
                return;
            int j = 0;
            for (int i = 0; i < assocList.getAssociationCount(); i++) {
                Association assoc = assocList.getAssociation(i);
                AssociatedConceptList nodes = assoc.getAssociatedConcepts();
                for (Iterator<AssociatedConcept> subsumed = nodes.iterateAssociatedConcept(); subsumed.hasNext(); ) {
                    printHierarchyBranch(++j, hierarchyID, subsumed.next(), currentDepth + 1, maxDepth, assoc.getDirectionalName(), displayConcepts);
                }
            }
            if (currentDepth == 1 || displayConcepts)
                _buffer.append(indent + "* " + conceptInfo + " (children=" + j + ")\n");
        }
    }
}
