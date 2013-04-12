/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

/*
 * Copyright: (c) 2004-2009 Mayo Foundation for Medical Education and
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

package org.LexGrid.LexBIG.example;

import gov.nih.nci.evs.browser.utils.RemoteServerUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.LexGrid.LexBIG.DataModel.Collections.AssociatedConceptList;
import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.HierarchyPathResolveOption;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.codingSchemes.CodingScheme;
import org.LexGrid.commonTypes.EntityDescription;
import org.LexGrid.naming.SupportedHierarchy;
import org.apache.commons.lang.StringUtils;

/**
 * Attempts to provide a tree, based on a focus code, that includes the
 * following information:
 * <pre>
 * - All paths from the hierarchy root to one or more focus codes.
 * - Immediate children of every node in path to root
 * - Indicator to show whether any unexpanded node can be further expanded
 * </pre>
 *
 * This example accepts two parameters...
 * The first parameter is required, and must contain at least one code
 * in a comma-delimited list. A tree is produced for each code. Time to
 * produce the tree for each code is printed in milliseconds. In order
 * to factor out costs of startup and shutdown, resolving multiple
 * codes may offer a better overall estimate performance.
 *
 * The second parameter is optional, and can indicate a hierarchy ID to
 * navigate when resolving child nodes. If not provided, "is_a" is
 * assumed.
 */
public class BuildTreeForCode {
    LocalNameList noopList_ = Constructors.createLocalNameList("_noop_");

    public BuildTreeForCode() {
        super();
    }

    /**
     * Entry point for processing.
     *
     * @param args
     */


    public static void Util_displayAndLogError(String msg, Exception e) {
		System.out.println(msg);
		e.printStackTrace();
	}

    public static void Util_displayMessage(String msg) {
		System.out.println(msg);
	}


    /**
     * Prints the tree for an individual code.
     */
    public void run(LexBIGService lbsvc, LexBIGServiceConvenienceMethods lbscm, String scheme,
            CodingSchemeVersionOrTag csvt, SupportedHierarchy hierarchyDefn, String focusCode) throws LBException {

        // Print a header and define a new tree for the code being processed.
        Util_displayMessage("============================================================");
        Util_displayMessage("Focus code: " + focusCode);
        Util_displayMessage("============================================================");

        TreeItem ti = new TreeItem("<Root>", "Root node");
        long ms = System.currentTimeMillis();
        int pathsResolved = 0;
        try {

            // Resolve 'is_a' hierarchy info. This example will
            // need to make some calls outside of what is covered
            // by existing convenience methods, but we use the
            // registered hierarchy to prevent need to hard code
            // relationship and direction info used on lookup ...
            String hierarchyID = hierarchyDefn.getLocalId();
            //String[] associationsToNavigate = hierarchyDefn.getAssociationNames();
            String[] associationsToNavigate = hierarchyDefn.getAssociationIds();
            boolean associationsNavigatedFwd = hierarchyDefn.getIsForwardNavigable();

            // Identify the set of all codes on path from root
            // to the focus code ...
            Map<String, EntityDescription> codesToDescriptions = new HashMap<String, EntityDescription>();
            AssociationList pathsFromRoot = getPathsFromRoot(lbsvc, lbscm, scheme, csvt, hierarchyID, focusCode,
                    codesToDescriptions);

            // Typically there will be one path, but handle multiple just in
            // case.  Each path from root provides a 'backbone', from focus
            // code to root, for additional nodes to hang off of in our
            // printout. For every backbone node, one level of children is
            // printed, along with an indication of whether those nodes can
            // be expanded.
            for (Iterator<Association> paths = pathsFromRoot.iterateAssociation(); paths.hasNext();) {
                addPathFromRoot(ti, lbsvc, lbscm, scheme, csvt, paths.next(), associationsToNavigate, associationsNavigatedFwd,
                        codesToDescriptions);
                pathsResolved++;
            }

        } finally {
            System.out.println("Run time (milliseconds): " + (System.currentTimeMillis() - ms) +
                " to resolve " + pathsResolved + " paths from root.");
        }

        // Print the result ..
        printTree(ti, focusCode, 0);
    }

    /**
     * The given path represents a multi-tier association with associated
     * concepts and targets.  This method expands the association content
     * and adds results to the given tree item, recursing as necessary to
     * process each level in the path.
     * <p>
     * Nodes in the association acts as the backbone for the display.
     * For each backbone node, additional children are resolved one level
     * deep along with an indication of further expandability.
     */
    protected void addPathFromRoot(TreeItem ti,
            LexBIGService lbsvc, LexBIGServiceConvenienceMethods lbscm,
            String scheme, CodingSchemeVersionOrTag csvt,
            Association path, String[] associationsToNavigate, boolean associationsNavigatedFwd,
            Map<String, EntityDescription> codesToDescriptions)
        throws LBException {

        // First, add the branch point from the path ...
        ConceptReference branchRoot = path.getAssociationReference();
        String branchCode = branchRoot.getConceptCode();
        String branchCodeDescription = codesToDescriptions.containsKey(branchCode) ? codesToDescriptions
                .get(branchCode).getContent() : getCodeDescription(lbsvc, scheme, csvt, branchCode);

        TreeItem branchPoint = new TreeItem(branchCode, branchCodeDescription);
        String branchNavText = getDirectionalLabel(lbscm, scheme, csvt, path, associationsNavigatedFwd);

        // Now process elements in the branch ...
        AssociatedConceptList concepts = path.getAssociatedConcepts();
        for (int i = 0; i < concepts.getAssociatedConceptCount(); i++) {

            // Add all immediate leafs in the branch, and indication of
            // sub-nodes. Do not process codes already in the backbone here;
            // they will be printed in the recursive call ...
            AssociatedConcept concept = concepts.getAssociatedConcept(i);
            String code = concept.getConceptCode();
            TreeItem branchItem = new TreeItem(code, getCodeDescription(concept));
            branchPoint.addChild(branchNavText, branchItem);

            addChildren(branchItem, lbsvc, lbscm, scheme, csvt, code, codesToDescriptions.keySet(),
                    associationsToNavigate, associationsNavigatedFwd);

            // Recurse to process the remainder of the backbone ...
            AssociationList nextLevel = concept.getSourceOf();
            if (nextLevel != null) {
                if (nextLevel.getAssociationCount() != 0) {
                    // More levels left to process ...
                    for (int j = 0; j < nextLevel.getAssociationCount(); j++)
                        addPathFromRoot(branchPoint, lbsvc, lbscm, scheme, csvt, nextLevel.getAssociation(j), associationsToNavigate,
                                associationsNavigatedFwd, codesToDescriptions);
                } else {
                    // End of the line ...
                    // Always add immediate children ot the focus code.
                    addChildren(branchItem, lbsvc, lbscm, scheme,
                        csvt, concept.getConceptCode(), Collections.EMPTY_SET,
                        associationsToNavigate, associationsNavigatedFwd);
                }
            }
        }

        // Add the populated tree item to those tracked from root.
        ti.addChild(branchNavText, branchPoint);
    }

    /**
     * Populate child nodes for a single branch of the tree,
     * and indicates whether further expansion (to grandchildren)
     * is possible.
     */
    protected void addChildren(TreeItem ti,
            LexBIGService lbsvc, LexBIGServiceConvenienceMethods lbscm,
            String scheme, CodingSchemeVersionOrTag csvt,
            String branchRootCode, Set<String> codesToExclude, String[] associationsToNavigate,
            boolean associationsNavigatedFwd)
        throws LBException {

        // Resolve the next branch, representing children of the given
        // code, navigated according to the provided relationship and
        // direction.  Resolve the children as a code graph, looking 2
        // levels deep but leaving the final level unresolved.
        CodedNodeGraph cng = lbsvc.getNodeGraph(scheme, csvt, null);
        ConceptReference focus = Constructors.createConceptReference(branchRootCode, scheme);
        cng = cng.restrictToAssociations(Constructors.createNameAndValueList(associationsToNavigate), null);
        ResolvedConceptReferenceList branch = cng.resolveAsList(focus, associationsNavigatedFwd,
                !associationsNavigatedFwd, -1, 2, noopList_, null, null, null, -1, true);

        // The resolved branch will be represented by the first node in
        // the resolved list.  The node will be subdivided by source or
        // target associations (depending on direction). The associated
        // nodes define the children.
        for (Iterator<ResolvedConceptReference> nodes = branch.iterateResolvedConceptReference(); nodes.hasNext();) {
            ResolvedConceptReference node = nodes.next();
            AssociationList childAssociationList = associationsNavigatedFwd ? node.getSourceOf() : node.getTargetOf();

            // Process each association defining children ...
            for (Iterator<Association> pathsToChildren = childAssociationList.iterateAssociation(); pathsToChildren.hasNext();) {
                Association child = pathsToChildren.next();
                String childNavText = getDirectionalLabel(lbscm, scheme, csvt, child, associationsNavigatedFwd);

                // Each association may have multiple children ...
                AssociatedConceptList branchItemList = child.getAssociatedConcepts();
                for (Iterator<AssociatedConcept> branchNodes = branchItemList.iterateAssociatedConcept(); branchNodes
                        .hasNext();) {
                    AssociatedConcept branchItemNode = branchNodes.next();
                    String branchItemCode = branchItemNode.getConceptCode();

                    // Add here if not in the list of excluded codes.
                    // This is also where we look to see if another level
                    // was indicated to be available.  If so, mark the
                    // entry with a '+' to indicate it can be expanded.
                    if (!codesToExclude.contains(branchItemCode)) {
                        TreeItem childItem = new TreeItem(branchItemCode, getCodeDescription(branchItemNode));
                        AssociationList grandchildBranch =
                            associationsNavigatedFwd
                                ? branchItemNode.getSourceOf()
                                : branchItemNode.getTargetOf();
                        if (grandchildBranch != null)
                            childItem.expandable = true;
                        ti.addChild(childNavText, childItem);
                    }
                }
            }
        }
    }

    /**
     * Prints the given tree item, recursing through all branches.
     * @param ti
     */
    protected void printTree(TreeItem ti, String focusCode, int depth) {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < depth * 2; i++)
            indent.append("| ");

        StringBuffer codeAndText = new StringBuffer(indent)
            .append(focusCode.equals(ti.code) ? ">>>>" : "")
            .append(ti.code).append(':')
            .append(ti.text.length() > 64 ? ti.text.substring(0, 62) + "..." : ti.text)
            .append(ti.expandable ? " [+]" : "");
        Util_displayMessage(codeAndText.toString());

        indent.append("| ");
        for (String association : ti.assocToChildMap.keySet()) {
            Util_displayMessage(indent.toString() + association);
            List<TreeItem> children = ti.assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children)
                printTree(childItem, focusCode, depth + 1);
        }
    }

    ///////////////////////////////////////////////////////
    // Helper Methods
    ///////////////////////////////////////////////////////

    /**
     * Returns the entity description for the given code.
     */
    protected String getCodeDescription(LexBIGService lbsvc, String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {

        CodedNodeSet cns = lbsvc.getCodingSchemeConcepts(scheme, csvt);
        cns = cns.restrictToCodes(Constructors.createConceptReferenceList(code, scheme));
        ResolvedConceptReferenceList rcrl = cns.resolveToList(null, noopList_, null, 1);
        if (rcrl.getResolvedConceptReferenceCount() > 0) {
            EntityDescription desc = rcrl.getResolvedConceptReference(0).getEntityDescription();
            if (desc != null)
                return desc.getContent();
        }
        return "<Not assigned>";
    }

    /**
     * Returns the entity description for the given resolved concept reference.
     */
    protected String getCodeDescription(ResolvedConceptReference ref) throws LBException {
        EntityDescription desc = ref.getEntityDescription();
        if (desc != null)
            return desc.getContent();
        return "<Not assigned>";
    }

    /**
     * Returns the label to display for the given association and directional
     * indicator.
     */
    protected String getDirectionalLabel(LexBIGServiceConvenienceMethods lbscm,
            String scheme, CodingSchemeVersionOrTag csvt,
            Association assoc, boolean navigatedFwd)
        throws LBException {

        String assocLabel = navigatedFwd
            ? lbscm.getAssociationForwardName(assoc.getAssociationName(), scheme, csvt)
            : lbscm.getAssociationReverseName(assoc.getAssociationName(), scheme, csvt);
        if (StringUtils.isBlank(assocLabel))
            assocLabel = (navigatedFwd ? "" : "[Inverse]") + assoc.getAssociationName();
        return assocLabel;
    }

    /**
     * Resolves one or more paths from the hierarchy root to the given code
     * through a list of connected associations defined by the hierarchy.
     */
    protected AssociationList getPathsFromRoot(LexBIGService lbsvc, LexBIGServiceConvenienceMethods lbscm,
            String scheme, CodingSchemeVersionOrTag csvt, String hierarchyID, String focusCode,
            Map<String, EntityDescription> codesToDescriptions) throws LBException {

        // Get paths from the focus code to the root from the
        // convenience method.  All paths are resolved.  If only
        // one path is required, it would be possible to use
        // HierarchyPathResolveOption.ONE to reduce processing
        // and improve overall performance.
        AssociationList pathToRoot = lbscm.getHierarchyPathToRoot(scheme, csvt, null, focusCode, false,
                HierarchyPathResolveOption.ALL, null);

        // But for purposes of this example we need to display info
        // in order coming from root direction. Process the paths to root
        // recursively to reverse the order for processing ...
        AssociationList pathFromRoot = new AssociationList();
        for (int i = pathToRoot.getAssociationCount() - 1; i >= 0; i--)
            reverseAssoc(lbsvc, lbscm, scheme, csvt, pathToRoot.getAssociation(i), pathFromRoot, codesToDescriptions);

        return pathFromRoot;
    }

    /**
     * Returns a description of the hierarchy defined by the given coding
     * scheme and matching the specified ID.
     */
    protected static SupportedHierarchy getSupportedHierarchy(LexBIGService lbsvc, String scheme,
            CodingSchemeVersionOrTag csvt, String hierarchyID) throws LBException {

        CodingScheme cs = lbsvc.resolveCodingScheme(scheme, csvt);
        if (cs == null) {
            throw new LBResourceUnavailableException("Coding scheme not found: " + scheme);
        }
        for (SupportedHierarchy h : cs.getMappings().getSupportedHierarchy())
            if (h.getLocalId().equals(hierarchyID))
                return h;
        throw new LBResourceUnavailableException("Hierarchy not defined: " + hierarchyID);
    }

    /**
     * Recursive call to reverse order of the given association list, adding
     * results to the given list. In context of this program we use this
     * technique to determine the path from root, starting from the path to root
     * provided by the standard convenience method.
     */
    protected AssociationList reverseAssoc(LexBIGService lbsvc, LexBIGServiceConvenienceMethods lbscm, String scheme,
            CodingSchemeVersionOrTag csvt, Association assoc, AssociationList addTo,
            Map<String, EntityDescription> codeToEntityDescriptionMap) throws LBException {

        ConceptReference acRef = assoc.getAssociationReference();
        AssociatedConcept acFromRef = new AssociatedConcept();
        acFromRef.setCodingScheme(acRef.getCodingScheme());
        acFromRef.setConceptCode(acRef.getConceptCode());
        AssociationList acSources = new AssociationList();
        acFromRef.setIsNavigable(Boolean.TRUE);
        acFromRef.setSourceOf(acSources);

        // Use cached description if available (should be cached
        // for all but original root) ...
        if (codeToEntityDescriptionMap.containsKey(acRef.getConceptCode()))
            acFromRef.setEntityDescription(codeToEntityDescriptionMap.get(acRef.getConceptCode()));
        // Otherwise retrieve on demand ...
        else
            acFromRef.setEntityDescription(Constructors.createEntityDescription(getCodeDescription(lbsvc, scheme, csvt,
                    acRef.getConceptCode())));

        AssociatedConceptList acl = assoc.getAssociatedConcepts();
        for (AssociatedConcept ac : acl.getAssociatedConcept()) {
            // Create reverse association (same non-directional name)
            Association rAssoc = new Association();
            rAssoc.setAssociationName(assoc.getAssociationName());

            // On reverse, old associated concept is new reference point.
            ConceptReference ref = new ConceptReference();
            ref.setCodingScheme(ac.getCodingScheme());
            ref.setConceptCode(ac.getConceptCode());
            rAssoc.setAssociationReference(ref);

            // And old reference is new associated concept.
            AssociatedConceptList rAcl = new AssociatedConceptList();
            rAcl.addAssociatedConcept(acFromRef);
            rAssoc.setAssociatedConcepts(rAcl);

            // Set reverse directional name, if available.
            String dirName = assoc.getDirectionalName();
            if (dirName != null)
                try {
                    rAssoc.setDirectionalName(lbscm.isForwardName(scheme, csvt, dirName) ? lbscm
                            .getAssociationReverseName(assoc.getAssociationName(), scheme, csvt) : lbscm
                            .getAssociationReverseName(assoc.getAssociationName(), scheme, csvt));
                } catch (LBException e) {
                }

            // Save code desc for future reference when setting up
            // concept references in recursive calls ...
            codeToEntityDescriptionMap.put(ac.getConceptCode(), ac.getEntityDescription());

            AssociationList sourceOf = ac.getSourceOf();
            if (sourceOf != null)
                for (Association sourceAssoc : sourceOf.getAssociation()) {
                    AssociationList pos = reverseAssoc(lbsvc, lbscm, scheme, csvt, sourceAssoc, addTo,
                            codeToEntityDescriptionMap);
                    pos.addAssociation(rAssoc);
                }
            else
                addTo.addAssociation(rAssoc);
        }
        return acSources;
    }



    public void run(String code) throws LBException {
        //CodingSchemeSummary css = Util.promptForCodeSystem();
        long ms = System.currentTimeMillis();
        try {
            //if (css != null) {
                //LexBIGService lbSvc = LexBIGServiceImpl.defaultInstance();
                LexBIGService lbsvc = RemoteServerUtil.createLexBIGService();
                LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbsvc
                        .getGenericExtension("LexBIGServiceConvenienceMethods");
                lbscm.setLexBIGService(lbsvc);
                // Fetch the description for the specified code.
                // Not required to find path to root, just nice to display.
                String scheme = "NCI Thesaurus";//css.getCodingSchemeURI();
                CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
                //csvt.setVersion(css.getRepresentsVersion());
                String desc = null;
                try {
                    desc = lbscm.createCodeNodeSet(new String[] { code }, scheme, csvt).resolveToList(null, null, null,
                            1).getResolvedConceptReference(0).getEntityDescription().getContent();
                } catch (Exception e) {
                    desc = "<not found>";
                }
                Util_displayMessage("============================================================");
                Util_displayMessage("Focus code: " + code + ":" + desc);
                Util_displayMessage("============================================================");

                // Iterate through all hierarchies ...
                String[] hierarchyIDs = lbscm.getHierarchyIDs(scheme, csvt);
                for (int i = 0; i < hierarchyIDs.length; i++) {
                    String hierarchyID = hierarchyIDs[i];
                    Util_displayMessage("------------------------------------------------------------");
                    Util_displayMessage("Hierarchy ID: " + hierarchyID);
                    Util_displayMessage("------------------------------------------------------------");
                    AssociationList associations = lbscm.getHierarchyPathToRoot(scheme, csvt, hierarchyID, code, false,
                            LexBIGServiceConvenienceMethods.HierarchyPathResolveOption.ALL, null);
                    for (int j = 0; j < associations.getAssociationCount(); j++) {
                        Association association = associations.getAssociation(j);
                        printChain(association, 0);
                    }
                    Util_displayMessage("");
                }
            //}
        } finally {
            System.out.println("Run time (ms): " + (System.currentTimeMillis() - ms));
        }
    }

    /**
     * Displays the given concept chain, taking into account any branches that
     * might be imbedded.
     *
     * @param assoc
     * @param depth
     * @throws LBException
     */
    protected void printChain(Association assoc, int depth) throws LBException {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i <= depth; i++)
            indent.append("    ");

        AssociatedConceptList concepts = assoc.getAssociatedConcepts();
        for (int i = 0; i < concepts.getAssociatedConceptCount(); i++) {
            // Print focus of this branch ...
            AssociatedConcept concept = concepts.getAssociatedConcept(i);
            Util_displayMessage(new StringBuffer(indent).append(assoc.getAssociationName()).append("->").append(
                    concept.getConceptCode()).append(':').append(
                    concept.getEntityDescription() == null ? "NO DESCRIPTION" : concept.getEntityDescription()
                            .getContent()).toString());

            // Find and recurse printing for next batch ...
            AssociationList nextLevel = concept.getSourceOf();
            if (nextLevel != null && nextLevel.getAssociationCount() != 0)
                for (int j = 0; j < nextLevel.getAssociationCount(); j++)
                    printChain(nextLevel.getAssociation(j), depth + 1);
        }
    }



    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Example: BuildTreeForCode \"C0000,C0001\"");
            return;
        }

        try {
            // Prompt for a coding scheme to run against...
            //CodingSchemeSummary css = Util.promptForCodeSystem();
            //if (css != null) {

                // Declare common service classes for processing ...
                //LexBIGService lbsvc = LexBIGServiceImpl.defaultInstance();
                LexBIGService lbsvc = RemoteServerUtil.createLexBIGService();

                LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods) lbsvc
                        .getGenericExtension("LexBIGServiceConvenienceMethods");

                lbscm.setLexBIGService(lbsvc);
                // Pull the URI and version from the selected value ...
                String scheme = "NCI Thesaurus";//css.getCodingSchemeURI();
                CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();

                // For any given ontology, we know the hierarchy
                // information. But to keep the code as generic
                // as possible and protect against changes, we
                // resolve the supported hierarchy info from the
                // coding scheme...
                String hierarchyID = args.length > 1 ? args[1] : "is_a";
                SupportedHierarchy hierarchyDefn = getSupportedHierarchy(lbsvc, scheme, csvt, hierarchyID);

                // Loop through each provided code.
                // The time to produce the tree for each code is printed in
                // milliseconds. In order to factor out costs of startup
                // and shutdown, resolving multiple codes may offer a
                // better overall estimate performance.
                String[] codes = args[0].split(",");
                BuildTreeForCode test = new BuildTreeForCode();
                for (String code : codes)
                {
					long ms = System.currentTimeMillis();
					test.run(code);
					System.out.println("===========================================================");
                    test.run(lbsvc, lbscm, scheme, csvt, hierarchyDefn, code);
					System.out.println("Round trip -- Run time (milliseconds): " + (System.currentTimeMillis() - ms) );

				}
            //}
        } catch (Exception e) {
            Util_displayAndLogError("REQUEST FAILED !!!", e);
        }
    }



    ///////////////////////////////////////////////////////
    // Helper classes
    ///////////////////////////////////////////////////////

    /**
     * Inner class to hold tree items for printout.
     */
    protected class TreeItem implements Comparable<TreeItem> {
        public String code = null;
        public String text = null;
        public boolean expandable = false;
        public Map<String, List<TreeItem>> assocToChildMap = new TreeMap<String, List<TreeItem>>();
        public boolean equals(Object o) {
            return o instanceof TreeItem
                && code.compareTo(((TreeItem) o).code) == 0;
        }
        public int compareTo(TreeItem ti) {
            String c1 = code;
            String c2 = ti.code;
            if (c1.startsWith("@")) return 1;
            if (c2.startsWith("@")) return -1;
            return c1.compareTo(c2);
        }
        public TreeItem(String code, String text) {
            super();
            this.code = code;
            this.text = text;
        }
        public void addAll(String assocText, List<TreeItem> children) {
            for (TreeItem item : children)
                addChild(assocText, item);
        }
        public void addChild(String assocText, TreeItem child) {
            List<TreeItem> children = assocToChildMap.get(assocText);
            if (children == null) {
                children = new ArrayList<TreeItem>();
                assocToChildMap.put(assocText, children);
            }
            int i;
            if ((i = children.indexOf(child)) >= 0) {
                TreeItem existingTreeItem = children.get(i);
                for (String assoc : child.assocToChildMap.keySet()) {
                    List<TreeItem> toAdd = child.assocToChildMap.get(assoc);
                    if (!toAdd.isEmpty()) {
                        existingTreeItem.addAll(assoc, toAdd);
                        existingTreeItem.expandable = false;
                    }
                }
            } else
                children.add(child);
        }
    }

}


/*
Focus code: C12432
============================================================
Run time (milliseconds): 4821 to resolve 1 paths from root.
<Root>:Root node
| hasSubtype
| | C12219:Anatomic Structure, System, or Substance
| | | hasSubtype
| | | | C13018:Organ
| | | | | hasSubtype
| | | | | | C12328:Epididymis
| | | | | | C12329:Spermatic Cord
| | | | | | C12377:Gallbladder
| | | | | | C12389:Esophagus
| | | | | | C12391:Stomach
| | | | | | C12392:Liver
| | | | | | C12393:Pancreas
| | | | | | C12403:Fallopian Tube
| | | | | | C12405:Uterus
| | | | | | C12407:Vagina
| | | | | | C12408:Vulva
| | | | | | C12409:Penis
| | | | | | C12414:Bladder
| | | | | | C12415:Kidney
| | | | | | C12416:Ureter
| | | | | | C12417:Urethra
| | | | | | C12428:Trachea
| | | | | | C12431:Bone Marrow
| | | | | | >>>>C12432:Spleen
| | | | | | C12433:Thymus Gland
| | | | | | C12439:Brain
| | | | | | C12464:Spinal Cord
| | | | | | C12468:Lung
| | | | | | C12470:Skin
| | | | | | C12678:Biliary Tract
| | | | | | C12702:Diaphragm
| | | | | | C12725:Gonad
| | | | | | C12727:Heart
| | | | | | C12736:Intestine
| | | | | | C12745:Lymph Node
| | | | | | C12787:Seminal Vesicle
| | | | | | C12802:Tonsil
| | | | | | C12813:Vas Deferens
| | | | | | C12948:Duct
| | | | | | C12971:Breast
| | | | | | C13319:Gland
| | | | | | C32233:Bronchial Tree
| | | | | | C33224:Organ of Special Sense
| | | | | | C73468:Waldeyer's Tonsillar Ring

G:\NCItBrowser_Project\TestLexBIGAPI\Tree>set CLASSPATH=

G:\NCItBrowser_Project\TestLexBIGAPI\Tree>

*/


























