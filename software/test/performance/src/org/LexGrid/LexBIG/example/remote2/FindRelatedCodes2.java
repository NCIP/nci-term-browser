/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package org.LexGrid.LexBIG.example.remote2;


import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Utility.ConvenienceMethods;
import org.LexGrid.LexBIG.util.AppService;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;
import org.LexGrid.LexBIG.util.Util;
import org.LexGrid.commonTypes.EntityDescription;

public class FindRelatedCodes2 implements Constants {
    
    private final int MAX_RETURN = 16384;
    private CodingSchemeSummary _css = null;
    private LexBIGService _lbSvc = null;
    private String _scheme = "";
    private CodingSchemeVersionOrTag _csvt = null;
    private boolean _displayConcepts = true;
    
    public static void main(String[] args) {
        try {
            new FindRelatedCodes2().run();
        } catch (Exception e) {
            org.LexGrid.LexBIG.util.Util.displayAndLogError("REQUEST FAILED !!!", e);
        }
    }
    
    public FindRelatedCodes2() throws LBException {
        AppService service = AppService.getInstance();
        _css = service.getCSS();
        _lbSvc = AppService.getLBSvc();
        _scheme = service.getScheme();
        _csvt = service.getCSVT();
    }

    public void run() throws LBException {
        if (_css == null)
            return;

        String code = "C28428"; //Retired Concepts
        code = "C26548"; //Gene Product
        code = "C12219"; //Anatomic Structure, System, or Substance
        code = "C12275"; //Returns nothing
        code = "C32221"; //Default
        
        while (true) {
            code = Prompt.prompt("Code (q to Quit)", code).toUpperCase();
            if (code.equalsIgnoreCase("q"))
                break;
            _displayConcepts = Prompt.prompt("Display Concepts", _displayConcepts);
            process(code);
            Util.displayMessage("");
            Util.displayMessage(SEPARATOR);
        }
        Util.displayMessage("Quit");
    }
    
    public void run2() throws LBException {
        _displayConcepts = Prompt.prompt("Display Concepts", _displayConcepts);
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
            process(codes[i]);
        }
        Util.displayMessage("Done");
    }
    
    private void process(String code) throws LBException {
        Util.StopWatch stopWatch = new Util.StopWatch();
//        printConcepts(code, getMatches(code, HAS_SUBTYPE, true), true);
//        Util.displayMessage(INDENT + "* " + stopWatch.getResult() + "\n"); 
        
        stopWatch.start();
        printConcepts(code, getMatches(code, HAS_SUBTYPE, false), false);
        Util.displayMessage(INDENT + "* " + stopWatch.getResult()); 
    }
    
    protected ResolvedConceptReferenceList getMatches(
            String code, String relation, boolean resolveForward)
            throws LBException {
        NameAndValue nv = new NameAndValue();
        NameAndValueList nvList = new NameAndValueList();
        nv.setName(relation);
        nvList.addNameAndValue(nv);

        boolean resolveBackward = ! resolveForward;
        CodedNodeGraph cng = _lbSvc.getNodeGraph(_scheme, _csvt, null);
        cng = cng.restrictToAssociations(nvList, null);
        ResolvedConceptReferenceList matches =
            cng.resolveAsList(
            ConvenienceMethods.createConceptReference(code, _scheme),
            resolveBackward, resolveForward, 1, 1, 
            new LocalNameList(), null, null, MAX_RETURN);
        return matches;
    }
    
    protected void printConcepts(String code, ResolvedConceptReferenceList list, 
            boolean isTargetList) throws LBException {
        StringBuffer buffer = new StringBuffer();
        if (isTargetList)
            buffer.append("* Pointed at by ...\n");
        else buffer.append("* Pointed to ...\n");

        if (list.getResolvedConceptReferenceCount() <= 0) {
            buffer.append(INDENT + "* " + code + ": 0 results.");
            Util.displayMessage(buffer.toString());
            return;
        }

        ResolvedConceptReference concept = (ResolvedConceptReference) 
            list.enumerateResolvedConceptReference().nextElement();
        AssociationList aList = isTargetList ? concept.getTargetOf() : concept.getSourceOf();
        Association[] associations = aList.getAssociation();
        int ctr = 0;
        for (int i = 0; i < associations.length; i++) {
            Association assoc = associations[i];
            AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
            for (int j = 0; j < acl.length; j++) {
                AssociatedConcept ac = acl[j];
                String childCode = ac.getConceptCode();
                EntityDescription ed = ac.getEntityDescription();
                String content = ed == null? "** No Description **" : ed.getContent(); 
                String msg = INDENT + (++ctr) + ") " + childCode + ": " + content;
                
                int childCount = numOfChildren(childCode);
                msg += "; children: " + childCount;
                if (_displayConcepts)
                    buffer.append(msg + "\n");
            }
        }
        String parentCode = concept.getConceptCode();
        EntityDescription desc = concept.getEntityDescription();
        if (desc != null)
            buffer.append(INDENT + "* Concept name: " + desc.getContent() + "\n");
        buffer.append(INDENT + "* " + parentCode + " (children=" + ctr + ")");
        Util.displayMessage(buffer.toString());
    }
    
    protected int numOfChildren(String code) throws LBException {
        ResolvedConceptReferenceList list = getMatches(code, HAS_SUBTYPE, false);
        if (list.getResolvedConceptReferenceCount() <= 0)
            return 0;
        
        ResolvedConceptReference ref = (ResolvedConceptReference) 
            list.enumerateResolvedConceptReference().nextElement();

        AssociationList aList = ref.getSourceOf();
        Association[] associations = aList.getAssociation();
        int n = 0;
        for (int i = 0; i < associations.length; i++) {
            Association assoc = associations[i];
            AssociatedConcept[] acl = assoc.getAssociatedConcepts().getAssociatedConcept();
            n += acl.length;
        }
        return n;
    }
}
