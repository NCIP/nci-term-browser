package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.common.*;

import java.util.*;
import java.util.Map;
import java.util.Map.Entry;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Extensions.Generic.*;
import org.LexGrid.LexBIG.Extensions.Generic.LexBIGServiceConvenienceMethods.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.codingSchemes.*;
import org.LexGrid.commonTypes.*;
import org.LexGrid.naming.*;
import org.LexGrid.concepts.*;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.*;
import org.apache.log4j.*;

import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverter;
import org.LexGrid.LexBIG.Impl.Extensions.tree.json.JsonConverterFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeService;
import org.LexGrid.LexBIG.Impl.Extensions.tree.service.TreeServiceFactory;
import org.LexGrid.LexBIG.Impl.Extensions.tree.dao.iterator.ChildTreeNodeIterator;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTreeNode;
import org.LexGrid.LexBIG.Impl.Extensions.tree.model.LexEvsTree;

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
 * Note: This class is created based on Mayo's BuildTreForCode.java sample code
 *
 * Modification history
 *     Initial modification kim.ong@ngc.com
 *
 */

/**
 * Attempts to provide a tree, based on a focus code, that includes the
 * following information:
 *
 * <pre>
 * - All paths from the hierarchy root to one or more focus codes.
 * - Immediate children of every node in path to root
 * - Indicator to show whether any unexpanded node can be further expanded
 * </pre>
 *
 * This example accepts two parameters... The first parameter is required, and
 * must contain at least one code in a comma-delimited list. A tree is produced
 * for each code. Time to produce the tree for each code is printed in
 * milliseconds. In order to factor out costs of startup and shutdown, resolving
 * multiple codes may offer a better overall estimate performance.
 *
 * The second parameter is optional, and can indicate a hierarchy ID to navigate
 * when resolving child nodes. If not provided, "is_a" is assumed.
 */
public class TreeUtils {
    private static Logger _logger = Logger.getLogger(TreeUtils.class);
    private static LocalNameList _noopList = new LocalNameList();

    private LexBIGService lbSvc = null;
    private LexBIGServiceConvenienceMethods lbscm = null;
    private ConceptDetails conceptDetails = null;

    private TreeService treeService = null;



    public TreeUtils() {

    }

    public TreeUtils(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;

		try {
			this.conceptDetails = new ConceptDetails(lbSvc);
			this.lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
			this.lbscm.setLexBIGService(lbSvc);
			this.treeService = TreeServiceFactory.getInstance().getTreeService(lbSvc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
    }

    public void setLexBIGService(LexBIGService lbSvc) {
		this.lbSvc = lbSvc;
		try {
			this.lbscm = (LexBIGServiceConvenienceMethods) lbSvc.getGenericExtension("LexBIGServiceConvenienceMethods");
			this.lbscm.setLexBIGService(lbSvc);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

    public HashMap getTreePathData(String scheme, String version,
        String hierarchyID, String code) throws LBException {
        return getTreePathData(scheme, version, hierarchyID, code, -1);
    }

    public HashMap getTreePathData(String scheme, String version,
        String hierarchyID, String code, int maxLevel) throws LBException {

        if (hierarchyID == null)
            hierarchyID = "is_a";
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        SupportedHierarchy hierarchyDefn =
            getSupportedHierarchy(scheme, csvt, hierarchyID);
        return getTreePathData(scheme, csvt, hierarchyDefn, code,
            maxLevel);
    }

    public HashMap getTreePathData(String scheme,
        CodingSchemeVersionOrTag csvt, SupportedHierarchy hierarchyDefn,
        String focusCode) throws LBException {

        return getTreePathData(scheme, csvt, hierarchyDefn,
            focusCode, -1);
    }

    public HashMap getTreePathData(String scheme,
        CodingSchemeVersionOrTag csvt, SupportedHierarchy hierarchyDefn,
        String focusCode, int maxLevel) throws LBException {

        HashMap hmap = new HashMap();
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
            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();
            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

            // Identify the set of all codes on path from root
            // to the focus code ...
            Map<String, EntityDescription> codesToDescriptions =
                new HashMap<String, EntityDescription>();
            AssociationList pathsFromRoot =
                getPathsFromRoot(scheme, csvt, hierarchyID,
                    focusCode, codesToDescriptions, maxLevel);

            // Typically there will be one path, but handle multiple just in
            // case. Each path from root provides a 'backbone', from focus
            // code to root, for additional nodes to hang off of in our
            // printout. For every backbone node, one level of children is
            // printed, along with an indication of whether those nodes can
            // be expanded.

            for (Iterator<? extends Association> paths =
                pathsFromRoot.iterateAssociation(); paths.hasNext();) {
                addPathFromRoot(ti, scheme, csvt, paths.next(),
                    associationsToNavigate, associationsNavigatedFwd,
                    codesToDescriptions);
                pathsResolved++;
            }

        } finally {
            _logger.debug("Run time (milliseconds): "
                + (System.currentTimeMillis() - ms) + " to resolve "
                + pathsResolved + " paths from root.");
        }

        hmap.put(focusCode, ti);
        return hmap;

    }

    public void run(String scheme, String version, String hierarchyId,
        String focusCode) throws LBException {
        HashMap hmap = getTreePathData(scheme, version, hierarchyId, focusCode);
        Set keyset = hmap.keySet();
        Object[] objs = keyset.toArray();
        String code = (String) objs[0];
        TreeItem ti = (TreeItem) hmap.get(code);
        printTree(ti, focusCode, 0);
    }

    public void printTree(HashMap hmap) {
        if (hmap == null) {
            _logger.error("ERROR printTree -- hmap is null.");
            return;
        }
        Object[] objs = hmap.keySet().toArray();
        String code = (String) objs[0];

        System.out.println("Tree root: " + code);
        TreeItem ti = (TreeItem) hmap.get(code);
        printTree(ti, code, 0);
    }

    protected void addPathFromRoot(TreeItem ti, String scheme,
        CodingSchemeVersionOrTag csvt, Association path,
        String[] associationsToNavigate, boolean associationsNavigatedFwd,
        Map<String, EntityDescription> codesToDescriptions) throws LBException {

        // First, add the branch point from the path ...
        ConceptReference branchRoot = path.getAssociationReference();
        // =======================================================================v.50
        String branchCode = branchRoot.getConceptCode();
        String branchCodeNamespace = branchRoot.getCodeNamespace();
        String branchCodeDescription =
            codesToDescriptions.containsKey(branchCode) ? codesToDescriptions
                .get(branchCode).getContent() : getCodeDescription(
                scheme, csvt, branchCode);
        TreeItem branchPoint = new TreeItem(branchCode, branchCodeDescription, branchCodeNamespace, null);
        String branchNavText =
            getDirectionalLabel(lbscm, scheme, csvt, path,
                associationsNavigatedFwd);

        // Now process elements in the branch ...
        AssociatedConceptList concepts = path.getAssociatedConcepts();
        for (int i = 0; i < concepts.getAssociatedConceptCount(); i++) {

            // Determine the next concept in the branch and
            // add a corresponding item to the tree.
            AssociatedConcept concept = concepts.getAssociatedConcept(i);
            String code = concept.getConceptCode();
            TreeItem branchItem =
                new TreeItem(code, getCodeDescription(concept), concept.getCodeNamespace(), null);
            branchPoint.addChild(branchNavText, branchItem);

            // Recurse to process the remainder of the backbone ...
            AssociationList nextLevel = concept.getSourceOf();
            if (nextLevel != null) {
                if (nextLevel.getAssociationCount() != 0) {
                    // Add immediate children of the focus code with an
                    // indication of sub-nodes (+). Codes already
                    // processed as part of the path are ignored since
                    // they are handled through recursion.
                    addChildren(branchItem, scheme, csvt, code,
                        codesToDescriptions.keySet(), associationsToNavigate,
                        associationsNavigatedFwd);

                    // More levels left to process ...
                    for (int j = 0; j < nextLevel.getAssociationCount(); j++)
                        addPathFromRoot(branchPoint, scheme,
                            csvt, nextLevel.getAssociation(j),
                            associationsToNavigate, associationsNavigatedFwd,
                            codesToDescriptions);
                } else {
                    // End of the line ...
                    // Always add immediate children of the focus code,
                    // in this case with no exclusions since we are moving
                    // beyond the path to root and allowed to duplicate
                    // nodes that may have occurred in the path to root.
                    addChildren(branchItem, scheme, csvt, code,
                        Collections.EMPTY_SET, associationsToNavigate,
                        associationsNavigatedFwd);
                }
            } else {
                // Add immediate children of the focus code with an
                // indication of sub-nodes (+). Codes already
                // processed as part of the path are ignored since
                // they are handled through recursion.
                addChildren(branchItem, scheme, csvt, code,
                    codesToDescriptions.keySet(), associationsToNavigate,
                    associationsNavigatedFwd);
            }
        }

        // Add immediate children of the node being processed,
        // and indication of sub-nodes.
        addChildren(branchPoint, scheme, csvt, branchCode,
            codesToDescriptions.keySet(), associationsToNavigate,
            associationsNavigatedFwd);

        // Add the populated tree item to those tracked from root.
        ti.addChild(branchNavText, branchPoint);
    }

    /**
     * Populate child nodes for a single branch of the tree, and indicates
     * whether further expansion (to grandchildren) is possible.
     */


    protected void addChildren(TreeItem ti, String scheme,
        CodingSchemeVersionOrTag csvt, String branchRootCode,
        Set<String> codesToExclude, String[] associationsToNavigate,
        boolean associationsNavigatedFwd) throws LBException {
		addChildren(ti, scheme, csvt, branchRootCode, null, codesToExclude, associationsToNavigate, associationsNavigatedFwd);
	}


    protected void addChildren(TreeItem ti, String scheme,
        CodingSchemeVersionOrTag csvt, String branchRootCode, String namespace,
        Set<String> codesToExclude, String[] associationsToNavigate,
        boolean associationsNavigatedFwd) throws LBException {

        // Resolve the next branch, representing children of the given
        // code, navigated according to the provided relationship and
        // direction. Resolve the children as a code graph, looking 2
        // levels deep but leaving the final level unresolved.
        CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
        Boolean restrictToAnonymous = Boolean.FALSE;
        cng = cng.restrictToAnonymous(restrictToAnonymous);


        ConceptReference focus =
            Constructors.createConceptReference(branchRootCode, namespace, scheme);
        cng =
            cng.restrictToAssociations(Constructors
                .createNameAndValueList(associationsToNavigate), null);
        ResolvedConceptReferenceList branch =
            cng.resolveAsList(focus,
                associationsNavigatedFwd,
                // !associationsNavigatedFwd, -1, 2, noopList_, null, null,
                // null, -1, true);
                !associationsNavigatedFwd, -1, 2, _noopList, null, null, null,
                -1, false);

        // The resolved branch will be represented by the first node in
        // the resolved list. The node will be subdivided by source or
        // target associations (depending on direction). The associated
        // nodes define the children.
        for (Iterator<? extends ResolvedConceptReference> nodes =
            branch.iterateResolvedConceptReference(); nodes.hasNext();) {
            ResolvedConceptReference node = nodes.next();
            AssociationList childAssociationList =
                associationsNavigatedFwd ? node.getSourceOf() : node
                    .getTargetOf();

            // KLO 091509
            if (childAssociationList == null)
                return;

            // Process each association defining children ...
            for (Iterator<? extends Association> pathsToChildren =
                childAssociationList.iterateAssociation(); pathsToChildren
                .hasNext();) {
                Association child = pathsToChildren.next();
                String childNavText =
                    getDirectionalLabel(lbscm, scheme, csvt, child,
                        associationsNavigatedFwd);

                // Each association may have multiple children ...
                AssociatedConceptList branchItemList =
                    child.getAssociatedConcepts();
                for (Iterator<? extends AssociatedConcept> branchNodes =
                    branchItemList.iterateAssociatedConcept(); branchNodes
                    .hasNext();) {
                    AssociatedConcept branchItemNode = branchNodes.next();
                    String branchItemCode = branchItemNode.getConceptCode();

                    // Add here if not in the list of excluded codes.
                    // This is also where we look to see if another level
                    // was indicated to be available. If so, mark the
                    // entry with a '+' to indicate it can be expanded.
                    // if
                    // (!branchItemNode.getReferencedEntry().getIsAnonymous()) {
                    if (!branchItemCode.startsWith("@")) {
                        if (!codesToExclude.contains(branchItemCode)) {
                            TreeItem childItem =
                                new TreeItem(branchItemCode,
                                    getCodeDescription(branchItemNode), branchItemNode.getCodeNamespace(), null);
                            AssociationList grandchildBranch =
                                associationsNavigatedFwd ? branchItemNode
                                    .getSourceOf() : branchItemNode
                                    .getTargetOf();
                            if (grandchildBranch != null)
                                childItem._expandable = true;
                            ti.addChild(childNavText, childItem);
                        }
                    }
                }
            }
        }
    }

    /**
     * Prints the given tree item, recursing through all branches.
     *
     * @param ti
     */

    // protected void printTree(TreeItem ti, String focusCode, int depth) {
    public void printTree(TreeItem ti, String focusCode, int depth) {
        StringBuffer indent = new StringBuffer();
        for (int i = 0; i < depth * 2; i++)
            indent.append("| ");

            StringBuffer codeAndText = null;
            if (ti._ns != null) {

				codeAndText =
					new StringBuffer(indent).append(
						focusCode.equals(ti._code) ? ">>>>" : "").append(ti._code + " " + ti._ns)
						.append(':').append(
							ti._text.length() > 64 ? ti._text.substring(0, 62) + "..."
								: ti._text).append(ti._expandable ? " [+]" : "");


			} else {
				codeAndText =
					new StringBuffer(indent).append(
						focusCode.equals(ti._code) ? ">>>>" : "").append(ti._code)
						.append(':').append(
							ti._text.length() > 64 ? ti._text.substring(0, 62) + "..."
								: ti._text).append(ti._expandable ? " [+]" : "");

			}


        System.out.println(codeAndText.toString());
        _logger.debug(codeAndText.toString());


        indent.append("| ");
        for (String association : ti._assocToChildMap.keySet()) {
            _logger.debug(indent.toString() + association);
            System.out.println(indent.toString() + association);
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children)
                printTree(childItem, focusCode, depth + 1);
        }
    }

    // /////////////////////////////////////////////////////
    // Helper Methods
    // /////////////////////////////////////////////////////

    /**
     * Returns the entity description for the given code.
     */
    protected  String getCodeDescription(
        String scheme, CodingSchemeVersionOrTag csvt, String code)
            throws LBException {
		return 	getCodeDescription(scheme, csvt, code, null);

	}

    protected  String getCodeDescription(
        String scheme, CodingSchemeVersionOrTag csvt, String code, String namespace)
            throws LBException {

        CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(scheme, csvt);
        cns =
            cns.restrictToCodes(Constructors.createConceptReferenceList(code, namespace, scheme));
        ResolvedConceptReferenceList rcrl = null;
        try {
            rcrl = cns.resolveToList(null, _noopList, null, 1);
        } catch (Exception ex) {
            _logger
                .error("WARNING: TreeUtils getCodeDescription cns.resolveToList throws exceptions");
            return "null";
        }

        if (rcrl != null && rcrl.getResolvedConceptReferenceCount() > 0) {
            EntityDescription desc =
                rcrl.getResolvedConceptReference(0).getEntityDescription();
            if (desc != null)
                return desc.getContent();
        }
        return "<Not assigned>";
    }

    /**
     * Returns the entity description for the given resolved concept reference.
     */
    protected  String getCodeDescription(ResolvedConceptReference ref)
            throws LBException {
        EntityDescription desc = ref.getEntityDescription();
        if (desc != null)
            return desc.getContent();
        return "<Not assigned>";
    }

    public boolean isBlank(String str) {
        if ((str == null) || str.matches("^\\s*$")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the label to display for the given association and directional
     * indicator.
     */
    protected  String getDirectionalLabel(
        LexBIGServiceConvenienceMethods lbscm, String scheme,
        CodingSchemeVersionOrTag csvt, Association assoc, boolean navigatedFwd)
            throws LBException {

        String assocLabel =
            navigatedFwd ? lbscm.getAssociationForwardName(assoc
                .getAssociationName(), scheme, csvt) : lbscm
                .getAssociationReverseName(assoc.getAssociationName(), scheme,
                    csvt);
        // if (StringUtils.isBlank(assocLabel))
        if (isBlank(assocLabel))
            assocLabel =
                (navigatedFwd ? "" : "[Inverse]") + assoc.getAssociationName();
        return assocLabel;
    }

    /**
     * Resolves one or more paths from the hierarchy root to the given code
     * through a list of connected associations defined by the hierarchy.
     */
    protected AssociationList getPathsFromRoot(String scheme,
        CodingSchemeVersionOrTag csvt, String hierarchyID, String focusCode,
        Map<String, EntityDescription> codesToDescriptions) throws LBException {
        return getPathsFromRoot(scheme, csvt, hierarchyID,
            focusCode, codesToDescriptions, -1);

    }

    protected AssociationList getPathsFromRoot(String scheme,
        CodingSchemeVersionOrTag csvt, String hierarchyID, String focusCode,
        Map<String, EntityDescription> codesToDescriptions, int maxLevel)
            throws LBException {
        // Get paths from the focus code to the root from the
        // convenience method. All paths are resolved. If only
        // one path is required, it would be possible to use
        // HierarchyPathResolveOption.ONE to reduce processing
        // and improve overall performance.
        AssociationList pathToRoot =
            lbscm.getHierarchyPathToRoot(scheme, csvt, null, focusCode, false,
                HierarchyPathResolveOption.ALL, null);

        // But for purposes of this example we need to display info
        // in order coming from root direction. Process the paths to root
        // recursively to reverse the order for processing ...
        AssociationList pathFromRoot = new AssociationList();
        for (int i = pathToRoot.getAssociationCount() - 1; i >= 0; i--)
            reverseAssoc(scheme, csvt, pathToRoot
                .getAssociation(i), pathFromRoot, codesToDescriptions,
                maxLevel, 0);

        return pathFromRoot;
    }

    /**
     * Returns a description of the hierarchy defined by the given coding scheme
     * and matching the specified ID.
     */
    protected  SupportedHierarchy getSupportedHierarchy(
        String scheme, CodingSchemeVersionOrTag csvt,
        String hierarchyID) throws LBException {

        CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
        if (cs == null) {
            throw new LBResourceUnavailableException(
                "Coding scheme not found: " + scheme);
        }
        for (SupportedHierarchy h : cs.getMappings().getSupportedHierarchy())
            if (h.getLocalId().equals(hierarchyID))
                return h;
        throw new LBResourceUnavailableException("Hierarchy not defined: "
            + hierarchyID);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean hasSubconcepts(String scheme, String version, String code) {
		return hasSubconcepts(scheme, version, code, null);
	}

	public boolean isExpandable(String scheme, String version, String code, String namespace) {
		CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
		if (version != null) {
			versionOrTag.setVersion(version);
		}
		LexEvsTreeNode node = null;
		if (namespace == null) {
		    node = treeService.getSubConcepts(scheme, versionOrTag, code);
	    } else {
			node = treeService.getSubConcepts(scheme, versionOrTag, code, namespace);
		}

        if (node != null && node.getExpandableStatus().toString().compareTo("IS_EXPANDABLE") == 0) return true;
        return false;
	}

    public boolean hasSubconcepts(String scheme, String version, String code, String namespace) {
        /*
        HashMap hmap = getSubconcepts(scheme, version, code, namespace);
        if (hmap == null)
            return false;
        TreeItem item = (TreeItem) hmap.get(code);
        return item._expandable;
        */

        return isExpandable(scheme, version, code, namespace);
    }

    /*
     * public HashMap getSubconcepts(String scheme, String version,
     * String code) { if (scheme.compareTo("NCI Thesaurus") == 0) { return
     * getAssociatedConcepts(scheme, version, code, "subClassOf", false); }
     *
     * else if (scheme.indexOf("MedDRA") != -1) { return
     * getAssociatedConcepts(scheme, version, code, "CHD", true); }
     *
     * CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag(); if
     * (version != null) csvt.setVersion(version);
     *
     * try { LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
     * LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods)
     * lbSvc .getGenericExtension("LexBIGServiceConvenienceMethods");
     * lbscm.setLexBIGService(lbSvc);
     *
     * CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt); if (cs ==
     * null) return null; Mappings mappings = cs.getMappings();
     * SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy(); if
     * (hierarchies == null || hierarchies.length == 0) return null;
     *
     * SupportedHierarchy hierarchyDefn = hierarchies[0]; String hier_id =
     * hierarchyDefn.getLocalId();
     *
     * String[] associationsToNavigate = hierarchyDefn.getAssociationNames();
     * //String assocName = hier_id;//associationsToNavigate[0]; String
     * assocName = associationsToNavigate[0];
     *
     * if (assocName.compareTo("part_of") == 0) assocName = "is_a";
     *
     * boolean associationsNavigatedFwd = hierarchyDefn.getIsForwardNavigable();
     *
     * if (assocName.compareTo("PAR") == 0) associationsNavigatedFwd = false;
     * //if (assocName.compareTo("subClassOf") == 0) associationsNavigatedFwd =
     * false; //return getAssociatedConcepts(scheme, version, code, assocName,
     * associationsNavigatedFwd); return getAssociatedConcepts(lbSvc, lbscm,
     * scheme, version, code, assocName, associationsNavigatedFwd); } catch
     * (Exception ex) { return null; } }
     *
     *
     * public HashMap getSuperconcepts(String scheme, String version,
     * String code) { if (scheme.compareTo("NCI Thesaurus") == 0) { return
     * getAssociatedConcepts(scheme, version, code, "subClassOf", true); }
     *
     * else if (scheme.indexOf("MedDRA") != -1) { return
     * getAssociatedConcepts(scheme, version, code, "CHD", false); }
     *
     * CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag(); if
     * (version != null) csvt.setVersion(version);
     *
     * try { LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
     * LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods)
     * lbSvc .getGenericExtension("LexBIGServiceConvenienceMethods");
     * lbscm.setLexBIGService(lbSvc);
     *
     * CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt); if (cs ==
     * null) return null; Mappings mappings = cs.getMappings();
     * SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy(); if
     * (hierarchies == null || hierarchies.length == 0) return null;
     *
     * SupportedHierarchy hierarchyDefn = hierarchies[0]; String[]
     * associationsToNavigate = hierarchyDefn.getAssociationNames(); //String
     * assocName = hier_id;//associationsToNavigate[0]; String assocName =
     * associationsToNavigate[0];
     *
     * if (assocName.compareTo("part_of") == 0) assocName = "is_a";
     *
     * boolean associationsNavigatedFwd = hierarchyDefn.getIsForwardNavigable();
     *
     * if (assocName.compareTo("PAR") == 0) associationsNavigatedFwd = false;
     *
     * return getAssociatedConcepts(scheme, version, code, assocName,
     * !associationsNavigatedFwd); } catch (Exception ex) { return null; } }
     */
    public HashMap getSubconcepts(String scheme, String version, String code) {
        return getSubconcepts(scheme, version, code, null);

    }

    public HashMap getSubconcepts(String scheme, String version, String code, String namespace) {
		/*
        if (scheme.compareTo("NCI Thesaurus") == 0 || scheme.compareTo("NCI_Thesaurus") == 0) {
            return getAssociatedConcepts(scheme, version, code, namespace, "subClassOf",
                false);
        }
        */


        if (namespace == null) {
			List<String> namespaces = conceptDetails.getDistinctNamespacesOfCode(
				scheme, version, code);
			if (namespaces != null && namespaces.size() == 1) {
				namespace = namespaces.get(0);
			}
		}
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
            csvt.setVersion(version);
		}

        try {
            CodingScheme cs = null;
            try {
				cs = lbSvc.resolveCodingScheme(scheme, csvt);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

            if (cs == null) {
				return null;
			}

            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0) {
                return null;
			}

            SupportedHierarchy hierarchyDefn = hierarchies[0];
            String hier_id = hierarchyDefn.getLocalId();
            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();

            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

            if (associationsToNavigate != null
                && associationsToNavigate.length == 1) {
                if (associationsToNavigate[0].compareTo("subClassOf") == 0) {
                    return getAssociatedConcepts(scheme, version, code, namespace,
                        "subClassOf", false);
                }
            }

            return getAssociatedConcepts(scheme, version, code, namespace,
                associationsToNavigate, associationsNavigatedFwd);
        } catch (Exception ex) {
            return null;
        }
    }

    public HashMap getSuperconcepts(String scheme, String version, String code) {
		return getSuperconcepts(scheme, version, code, null);
	}


    public HashMap getSuperconcepts(String scheme, String version, String code, String namespace) {

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            if (cs == null)
                return null;
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0)
                return null;

            SupportedHierarchy hierarchyDefn = hierarchies[0];
            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();

            for (int i = 0; i < associationsToNavigate.length; i++) {
                _logger.debug("(*) associationsToNavigate: "
                    + associationsToNavigate[i]);
            }

            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

			if (associationsToNavigate.length == 1) {
                if (associationsToNavigate[0].compareTo("subClassOf") == 0) {
                    return getAssociatedConcepts(scheme, version, code,
                        "subClassOf", true);
                }
            }
            return getAssociatedConcepts(scheme, version, code, namespace,
                associationsToNavigate, !associationsNavigatedFwd);
        } catch (Exception ex) {
            return null;
        }
    }
/*
    public HashMap getSuperconcepts(String scheme, String version, String code, String namespace) {

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            if (cs == null)
                return null;
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0)
                return null;

            SupportedHierarchy hierarchyDefn = hierarchies[0];
            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();

            for (int i = 0; i < associationsToNavigate.length; i++) {
                _logger.debug("(*) associationsToNavigate: "
                    + associationsToNavigate[i]);
            }

            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

			if (associationsToNavigate.length == 1) {
                if (associationsToNavigate[0].compareTo("subClassOf") == 0) {
                    return getAssociatedConcepts(scheme, version, code,
                        "subClassOf", true);
                }
            }

            return getAssociatedConcepts(scheme, version, code, namespace,
                associationsToNavigate, !associationsNavigatedFwd);



        } catch (Exception ex) {
            return null;
        }
    }
*/

    /*
     * public HashMap getSuperconcepts(String scheme, String version,
     * String code) { CodingSchemeVersionOrTag csvt = new
     * CodingSchemeVersionOrTag(); if (version != null)
     * csvt.setVersion(version);
     *
     * try { LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
     * LexBIGServiceConvenienceMethods lbscm = (LexBIGServiceConvenienceMethods)
     * lbSvc .getGenericExtension("LexBIGServiceConvenienceMethods");
     * lbscm.setLexBIGService(lbSvc);
     *
     * CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt); if (cs ==
     * null) return null; Mappings mappings = cs.getMappings();
     * SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy(); if
     * (hierarchies == null || hierarchies.length == 0) return null;
     * SupportedHierarchy hierarchyDefn = hierarchies[0]; String hier_id =
     * hierarchyDefn.getLocalId(); String[] associationsToNavigate =
     * hierarchyDefn.getAssociationNames(); String assocName =
     * associationsToNavigate[0]; boolean associationsNavigatedFwd =
     * hierarchyDefn.getIsForwardNavigable(); return
     * getAssociatedConcepts(scheme, version, code, assocName,
     * !associationsNavigatedFwd); } catch (Exception ex) { return null; } }
     */

    protected  Association processForAnonomousNodes(Association assoc) {
        if (assoc == null)
            return null;
        // clone Association except associatedConcepts
        Association temp = new Association();
        temp.setAssociatedData(assoc.getAssociatedData());
        temp.setAssociationName(assoc.getAssociationName());
        temp.setAssociationReference(assoc.getAssociationReference());
        temp.setDirectionalName(assoc.getDirectionalName());
        temp.setAssociatedConcepts(new AssociatedConceptList());

        for (int i = 0; i < assoc.getAssociatedConcepts()
            .getAssociatedConceptCount(); i++) {
            // Conditionals to deal with anonymous nodes and UMLS top nodes
            // "V-X"
            // The first three allow UMLS traversal to top node.
            // The last two are specific to owl anonymous nodes which can act
            // like false
            // top nodes.

            /*
             * if(assoc.getAssociatedConcepts().getAssociatedConcept(i).
             * getReferencedEntry() != null) {
             * _logger.debug(assoc.getAssociatedConcepts
             * ().getAssociatedConcept(i)
             * .getReferencedEntry().getEntityDescription().getContent() +
             * " === IsAnonymous? " +
             * assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry().getIsAnonymous()); } else {_logger.debug(
             * "assoc.getAssociatedConcepts().getAssociatedConcept(i).getReferencedEntry() == null"
             * ); }
             */
            /*
             * if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry() != null &&
             * assoc.getAssociatedConcepts().getAssociatedConcept(i)
             * .getReferencedEntry().getIsAnonymous() != false) { // do nothing
             * (NCI Thesaurus) }
             */
            if (assoc.getAssociatedConcepts().getAssociatedConcept(i)
                .getReferencedEntry() != null
                && assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getReferencedEntry().getIsAnonymous() != null
                && assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getReferencedEntry().getIsAnonymous() != false
                && !assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getConceptCode().equals("@")
                && !assoc.getAssociatedConcepts().getAssociatedConcept(i)
                    .getConceptCode().equals("@@")) {
                // do nothing
            } else {
                temp.getAssociatedConcepts().addAssociatedConcept(
                    assoc.getAssociatedConcepts().getAssociatedConcept(i));
            }
        }
        return temp;
    }

    /*
     * public HashMap getAssociatedConcepts(String scheme, String
     * version, String code, String assocName, boolean direction) { HashMap hmap
     * = new HashMap(); TreeItem ti = null; long ms =
     * System.currentTimeMillis();
     *
     * Set<String> codesToExclude = Collections.EMPTY_SET;
     *
     * CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag(); if
     * (version != null) csvt.setVersion(version); ResolvedConceptReferenceList
     * matches = null; Vector v = new Vector(); try { LexBIGService lbSvc =
     * RemoteServerUtil.createLexBIGService(); LexBIGServiceConvenienceMethods
     * lbscm = (LexBIGServiceConvenienceMethods) lbSvc
     * .getGenericExtension("LexBIGServiceConvenienceMethods");
     * lbscm.setLexBIGService(lbSvc);
     *
     * String name = getCodeDescription(lbSvc, scheme, csvt, code); ti = new
     * TreeItem(code, name); ti.expandable = false;
     *
     * CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
     * ConceptReference focus = Constructors.createConceptReference(code,
     * scheme); cng = cng.restrictToAssociations(Constructors
     * .createNameAndValueList(assocName), null); boolean
     * associationsNavigatedFwd = direction;
     *
     * // To remove anonymous classes (KLO, 091009), the resolveCodedEntryDepth
     * parameter cannot be set to -1.
     *
     * ResolvedConceptReferenceList branch = null; try { branch =
     * cng.resolveAsList(focus, associationsNavigatedFwd,
     * //!associationsNavigatedFwd, -1, 2, noopList_, null, null, null, -1,
     * true); !associationsNavigatedFwd, 1, 2, noopList_, null, null, null, -1,
     * false);
     *
     * } catch (Exception e) {
     * _logger.error("TreeUtils getAssociatedConcepts throws exceptions.");
     * return null; }
     *
     * for (Iterator<ResolvedConceptReference> nodes = branch
     * .iterateResolvedConceptReference(); nodes.hasNext();) {
     * ResolvedConceptReference node = nodes.next(); AssociationList
     * childAssociationList = null;
     *
     * //AssociationList childAssociationList = associationsNavigatedFwd ?
     * node.getSourceOf(): node.getTargetOf();
     *
     * if (associationsNavigatedFwd) { childAssociationList =
     * node.getSourceOf(); } else { childAssociationList = node.getTargetOf(); }
     *
     * if (childAssociationList != null) { // Process each association defining
     * children ... for (Iterator<Association> pathsToChildren =
     * childAssociationList .iterateAssociation(); pathsToChildren.hasNext();) {
     * Association child = pathsToChildren.next(); //KLO 091009 remove anonymous
     * nodes
     *
     * child = processForAnonomousNodes(child);
     *
     *
     * String childNavText = getDirectionalLabel(lbscm, scheme, csvt, child,
     * associationsNavigatedFwd);
     *
     * // Each association may have multiple children ... AssociatedConceptList
     * branchItemList = child .getAssociatedConcepts();
     *
     *
     *
     * List child_list = new ArrayList(); for (Iterator<AssociatedConcept>
     * branchNodes = branchItemList .iterateAssociatedConcept();
     * branchNodes.hasNext();) { AssociatedConcept branchItemNode =
     * branchNodes.next(); child_list.add(branchItemNode); }
     *
     * SortUtils.quickSort(child_list);
     *
     * for (int i = 0; i < child_list.size(); i++) { AssociatedConcept
     * branchItemNode = (AssociatedConcept) child_list .get(i); String
     * branchItemCode = branchItemNode.getConceptCode(); // Add here if not in
     * the list of excluded codes. // This is also where we look to see if
     * another level // was indicated to be available. If so, mark the // entry
     * with a '+' to indicate it can be expanded. if
     * (!codesToExclude.contains(branchItemCode)) { TreeItem childItem = new
     * TreeItem(branchItemCode, getCodeDescription(branchItemNode));
     * ti.expandable = true; AssociationList grandchildBranch =
     * associationsNavigatedFwd ? branchItemNode .getSourceOf() :
     * branchItemNode.getTargetOf(); if (grandchildBranch != null)
     * childItem.expandable = true; ti.addChild(childNavText, childItem); } } }
     * } else { _logger.warn("WARNING: childAssociationList == null."); } }
     * hmap.put(code, ti); } catch (Exception ex) { ex.printStackTrace(); }
     * _logger.debug("Run time (milliseconds) getSubconcepts: " +
     * (System.currentTimeMillis() - ms) + " to resolve "); return hmap; }
     */

    public HashMap getAssociatedConcepts(String scheme, String version,
        String code, String assocName, boolean direction) {
        return getAssociatedConcepts(scheme, version, code, null, assocName, direction);

    }

    public HashMap getAssociatedConcepts(String scheme, String version,
        String code, String namespace, String assocName, boolean direction) {

        HashMap hmap = new HashMap();
        TreeItem ti = null;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = Collections.EMPTY_SET;
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        //Vector v = new Vector();
        try {

			Entity concept = null;
			if (namespace == null) {
				concept = conceptDetails.getConceptByCode(scheme, version, code);
		    } else {
				concept = conceptDetails.getConceptByCode(scheme, version, code, namespace, true);
			}
			if (concept == null) return null;
			String entityCodeNamespace = concept.getEntityCodeNamespace();

			ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
			focus.setCodingSchemeName(scheme);
			if (namespace == null) {
                List<String> list = lbscm.getDistinctNamespacesOfCode(scheme, csvt, code);
                if (list.size() == 1) {
					// do nothing???
				}
			} else {
				if (!Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
					focus.setCodeNamespace(namespace);
				}
			}

			//focus.setCodeNamespace(entityCodeNamespace);
            String name = "";
            if (concept.getEntityDescription() != null) {
                name = concept.getEntityDescription().getContent();//getCodeDescription(lbSvc, scheme, csvt, code);
			}
            //String name = getCodeDescription(lbSvc, scheme, csvt, code);
            ti = new TreeItem(code, name, entityCodeNamespace, null);
            ti._expandable = false;

            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            Boolean restrictToAnonymous = Boolean.FALSE;
            cng = cng.restrictToAnonymous(restrictToAnonymous);

            //ConceptReference focus =
            //    Constructors.createConceptReference(code, scheme);
            cng =
                cng.restrictToAssociations(Constructors
                    .createNameAndValueList(assocName), null);


            boolean associationsNavigatedFwd = direction;

            // To remove anonymous classes (KLO, 091009), the
            // resolveCodedEntryDepth parameter cannot be set to -1.
            // Alternative -- use code to determine whether the class is
            // anonymous

            ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        !associationsNavigatedFwd, -1, 2, new LocalNameList(), null,
                        null, null, -1, false);

            } catch (Exception e) {
				e.printStackTrace();
                //System.out.println("(*) TreeUtils getAssociatedConcepts throws exceptions. " + scheme);
                return null;
            }

            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;

                // AssociationList childAssociationList =
                // associationsNavigatedFwd ? node.getSourceOf():
                // node.getTargetOf();

                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();

                } else {
                    childAssociationList = node.getTargetOf();
                }

                if (childAssociationList != null) {
                    // Process each association defining children ...
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();
                        // KLO 091009 remove anonymous nodes

                        child = processForAnonomousNodes(child);

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, csvt, child,
                                associationsNavigatedFwd);

                        // Each association may have multiple children ...
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();
                        /*
                         * for (Iterator<AssociatedConcept> branchNodes =
                         * branchItemList.iterateAssociatedConcept();
                         * branchNodes .hasNext();) { AssociatedConcept
                         * branchItemNode = branchNodes.next();
                         */

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();

                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();

                            if (!branchItemCode.startsWith("@")) {
                                // Add here if not in the list of excluded
                                // codes.
                                // This is also where we look to see if another
                                // level
                                // was indicated to be available. If so, mark
                                // the
                                // entry with a '+' to indicate it can be
                                // expanded.
                                if (!codesToExclude.contains(branchItemCode)) {
                                    TreeItem childItem =
                                        new TreeItem(branchItemCode,
                                            getCodeDescription(branchItemNode),
                                            branchItemNode.getCodeNamespace(), null);

                                    ti._expandable = true;

                                    AssociationList grandchildBranch =
                                        associationsNavigatedFwd ? branchItemNode
                                            .getSourceOf()
                                            : branchItemNode.getTargetOf();
                                    if (grandchildBranch != null)
                                        childItem._expandable = true;

                                    ti.addChild(childNavText, childItem);
                                }
                            }
                        }
                    }
                } else {
                   //System.out.println("WARNING: childAssociationList == null.\n");
                }
            }
            hmap.put(code, ti);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //System.out.println("Run time (milliseconds) getSubconcepts: " + (System.currentTimeMillis() - ms) + " to resolve ");
        return hmap;
    }


    public HashMap getAssociatedConcepts(String scheme, String version,
        String code, String[] assocNames, boolean direction) {
		return getAssociatedConcepts(scheme, version, code, null, assocNames, direction);
	}


    public HashMap getAssociatedConcepts(String scheme, String version,
        String code, String namespace, String[] assocNames, boolean direction) {

        HashMap hmap = new HashMap();
        TreeItem ti = null;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = Collections.EMPTY_SET;

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null) {
            csvt.setVersion(version);
		}
        ResolvedConceptReferenceList matches = null;
        //Vector v = new Vector();
        try {
			Entity concept = null;
			if (namespace == null) {
				concept = conceptDetails.getConceptByCode(scheme, version, code);
		    } else {
				concept = conceptDetails.getConceptByCode(scheme, version, code, namespace, true);
			}

			if (concept == null) return null;
			String entityCodeNamespace = concept.getEntityCodeNamespace();
			ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
			focus.setCodingSchemeName(scheme);
			focus.setCodeNamespace(namespace);
            String name = "";
            if (concept.getEntityDescription() != null) {
				name = concept.getEntityDescription().getContent();//getCodeDescription(lbSvc, scheme, csvt, code);
			}
            ti = new TreeItem(code, name, namespace, null);
            ti._expandable = false;

            CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
            Boolean restrictToAnonymous = Boolean.FALSE;
            cng = cng.restrictToAnonymous(restrictToAnonymous);

            //ConceptReference focus =
            //    Constructors.createConceptReference(code, scheme);
            cng =
                cng.restrictToAssociations(Constructors
                    .createNameAndValueList(assocNames), null);
            boolean associationsNavigatedFwd = direction;

            // To remove anonymous classes (KLO, 091009), the
            // resolveCodedEntryDepth parameter cannot be set to -1.
            // Alternative -- use code to determine whether the class is
            // anonymous

            ResolvedConceptReferenceList branch = null;
            try {
                branch =
                    cng.resolveAsList(focus,
                        associationsNavigatedFwd,
                        !associationsNavigatedFwd, -1, 2, _noopList, null,
                        null, null, -1, false);

            } catch (Exception e) {
				//e.printStackTrace();
                _logger
                    .error("#2 TreeUtils getAssociatedConcepts throws exceptions.");

                return null;
            }

            for (Iterator<? extends ResolvedConceptReference> nodes =
                branch.iterateResolvedConceptReference(); nodes.hasNext();) {
                ResolvedConceptReference node = nodes.next();
                AssociationList childAssociationList = null;

                // AssociationList childAssociationList =
                // associationsNavigatedFwd ? node.getSourceOf():
                // node.getTargetOf();

                if (associationsNavigatedFwd) {
                    childAssociationList = node.getSourceOf();

                } else {
                    childAssociationList = node.getTargetOf();
                }

                if (childAssociationList != null) {
                    // Process each association defining children ...
                    for (Iterator<? extends Association> pathsToChildren =
                        childAssociationList.iterateAssociation(); pathsToChildren
                        .hasNext();) {
                        Association child = pathsToChildren.next();
                        // KLO 091009 remove anonymous nodes

                        //child = processForAnonomousNodes(child);

                        String childNavText =
                            getDirectionalLabel(lbscm, scheme, csvt, child,
                                associationsNavigatedFwd);

                        // Each association may have multiple children ...
                        AssociatedConceptList branchItemList =
                            child.getAssociatedConcepts();

                        /*
                         * for (Iterator<AssociatedConcept> branchNodes =
                         * branchItemList.iterateAssociatedConcept();
                         * branchNodes .hasNext();) { AssociatedConcept
                         * branchItemNode = branchNodes.next();
                         */

                        List child_list = new ArrayList();
                        for (Iterator<? extends AssociatedConcept> branchNodes =
                            branchItemList.iterateAssociatedConcept(); branchNodes
                            .hasNext();) {
                            AssociatedConcept branchItemNode =
                                branchNodes.next();
                            child_list.add(branchItemNode);
                        }

                        SortUtils.quickSort(child_list);

                        for (int i = 0; i < child_list.size(); i++) {
                            AssociatedConcept branchItemNode =
                                (AssociatedConcept) child_list.get(i);
                            String branchItemCode =
                                branchItemNode.getConceptCode();

                            if (!branchItemCode.startsWith("@")) {
                                // Add here if not in the list of excluded
                                // codes.
                                // This is also where we look to see if another
                                // level
                                // was indicated to be available. If so, mark
                                // the
                                // entry with a '+' to indicate it can be
                                // expanded.
                                if (!codesToExclude.contains(branchItemCode)) {
                                    TreeItem childItem =
                                        new TreeItem(branchItemCode,
                                            getCodeDescription(branchItemNode),
                                            branchItemNode.getCodeNamespace(), null);

                                    ti._expandable = true;
                                    ti._ns = namespace;

                                    AssociationList grandchildBranch =
                                        associationsNavigatedFwd ? branchItemNode
                                            .getSourceOf()
                                            : branchItemNode.getTargetOf();
                                    if (grandchildBranch != null)
                                        childItem._expandable = true;

                                    ti.addChild(childNavText, childItem);
                                }
                            }
                        }
                    }
                } else {
                    //_logger.warn("WARNING: childAssociationList == null.");
                }
            }
            hmap.put(code, ti);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        return hmap;
    }

    public HashMap getAssociationSources(String scheme, String version,
        String code, String assocName) {
        return getAssociatedConcepts(scheme, version, code, null, assocName, false);
    }

    public HashMap getAssociationTargets(String scheme, String version,
        String code, String assocName) {
        return getAssociatedConcepts(scheme, version, code, null, assocName, true);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Configurable tree size (MAXIMUM_TREE_LEVEL)
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public ConceptReferenceList createConceptReferenceList(
        String[] codes, String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.length; i++) {
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(codes[i]);
            list.addConceptReference(cr);
        }
        return list;
    }

    public ConceptReferenceList createConceptReferenceList(
        String[] codes, String namespace, String codingSchemeName) {
        if (codes == null) {
            return null;
        }
        ConceptReferenceList list = new ConceptReferenceList();
        for (int i = 0; i < codes.length; i++) {
            ConceptReference cr = new ConceptReference();
            cr.setCodingSchemeName(codingSchemeName);
            cr.setConceptCode(codes[i]);
            cr.setCodeNamespace(namespace);
            list.addConceptReference(cr);
        }
        return list;
    }

    public Entity getConceptByCode(String codingSchemeName,
        String vers, String ltag, String code) {
		String namespace = null;
		return getConceptByCode(codingSchemeName, vers, ltag, code, namespace);
	}


    public Entity getConceptByCode(String codingSchemeName,
        String vers, String ltag, String code, String namespace) {
        try {
           if (lbSvc == null) {
                _logger.warn("lbSvc == null???");
                return null;
            }

            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(vers);

            ConceptReferenceList crefs =
                Constructors.createConceptReferenceList(new String[] { code }, namespace, codingSchemeName);

            CodedNodeSet cns = null;

            try {
                cns =
                    lbSvc.getCodingSchemeConcepts(codingSchemeName,
                        versionOrTag);
                if (cns == null) return null;
            } catch (Exception e1) {
                e1.printStackTrace();
                return null;
            }

            cns = cns.restrictToCodes(crefs);
            ResolvedConceptReferenceList matches =
                cns.resolveToList(null, null, null, 1);

            if (matches == null) {
                _logger.warn("Concept not found.");
                return null;
            }

            // Analyze the result ...
            if (matches.getResolvedConceptReferenceCount() > 0) {
                ResolvedConceptReference ref =
                    (ResolvedConceptReference) matches
                        .enumerateResolvedConceptReference().nextElement();

                Entity entry = ref.getReferencedEntry();
                return entry;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }


    public static NameAndValueList createNameAndValueList(String[] names,
        String[] values) {
        NameAndValueList nvList = new NameAndValueList();
        for (int i = 0; i < names.length; i++) {
            NameAndValue nv = new NameAndValue();
            nv.setName(names[i]);
            if (values != null) {
                nv.setContent(values[i]);
            }
            nvList.addNameAndValue(nv);
        }
        return nvList;
    }

    protected AssociationList reverseAssoc(String scheme,
        CodingSchemeVersionOrTag csvt, Association assoc,
        AssociationList addTo,
        Map<String, EntityDescription> codeToEntityDescriptionMap,
        int maxLevel, int currLevel) throws LBException {

        if (maxLevel != -1 && currLevel >= maxLevel)
            return addTo;

        ConceptReference acRef = assoc.getAssociationReference();
        AssociatedConcept acFromRef = new AssociatedConcept();
        // ===============================================================================v5.0
        acFromRef.setConceptCode(acRef.getConceptCode());
        AssociationList acSources = new AssociationList();
        acFromRef.setIsNavigable(Boolean.TRUE);
        acFromRef.setSourceOf(acSources);

        // Use cached description if available (should be cached
        // for all but original root) ...
        if (codeToEntityDescriptionMap.containsKey(acRef.getConceptCode()))
            acFromRef.setEntityDescription(codeToEntityDescriptionMap.get(acRef
                .getConceptCode()));
        // Otherwise retrieve on demand ...
        else
            acFromRef.setEntityDescription(Constructors
                .createEntityDescription(getCodeDescription(scheme,
                    csvt, acRef.getConceptCode())));

        AssociatedConceptList acl = assoc.getAssociatedConcepts();
        for (AssociatedConcept ac : acl.getAssociatedConcept()) {
            // Create reverse association (same non-directional name)
            Association rAssoc = new Association();
            rAssoc.setAssociationName(assoc.getAssociationName());

            // On reverse, old associated concept is new reference point.
            ConceptReference ref = new ConceptReference();
            // ===============================================================================v5.0
            // ref.setCodingSchemeName(ac.getCodingSchemeName());
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
                    rAssoc.setDirectionalName(lbscm.getAssociationReverseName(assoc.getAssociationName(), scheme, csvt));
                } catch (LBException e) {
                }

            // Save code desc for future reference when setting up
            // concept references in recursive calls ...
            codeToEntityDescriptionMap.put(ac.getConceptCode(), ac
                .getEntityDescription());

            AssociationList sourceOf = ac.getSourceOf();
            if (sourceOf != null)
                for (Association sourceAssoc : sourceOf.getAssociation()) {
                    AssociationList pos =
                        reverseAssoc(scheme, csvt, sourceAssoc,
                            addTo, codeToEntityDescriptionMap, maxLevel,
                            currLevel + 1);
                    pos.addAssociation(rAssoc);
                }
            else
                addTo.addAssociation(rAssoc);
        }
        return acSources;
    }

    public Vector getHierarchyAssociationId(String scheme, String version) {

        Vector association_vec = new Vector();
        try {
            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            versionOrTag.setVersion(version);
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, versionOrTag);
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();

            for (int k = 0; k < hierarchies.length; k++) {
                java.lang.String[] ids = hierarchies[k].getAssociationNames();

                for (int i = 0; i < ids.length; i++) {
                    if (!association_vec.contains(ids[i])) {
                        association_vec.add(ids[i]);
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return association_vec;
    }

    public String getHierarchyAssociationId(String scheme, String version,
        int index) {
        try {
            Vector hierarchicalAssoName_vec =
                getHierarchyAssociationId(scheme, version);
            if (hierarchicalAssoName_vec != null
                && hierarchicalAssoName_vec.size() > 0) {
                String hierarchicalAssoName =
                    (String) hierarchicalAssoName_vec.elementAt(0);
                return hierarchicalAssoName;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List getTopNodes(TreeItem ti) {
        List list = new ArrayList();
        getTopNodes(ti, list, 0, 1);
        return list;
    }

    public void getTopNodes(TreeItem ti, List list, int currLevel, int maxLevel) {

        if (list == null)
            list = new ArrayList();
        if (currLevel > maxLevel)
            return;
        if (ti._assocToChildMap.keySet().size() > 0) {
            if (ti._text.compareTo("Root node") != 0) {
                ResolvedConceptReference rcr = new ResolvedConceptReference();
                rcr.setConceptCode(ti._code);
                EntityDescription entityDescription = new EntityDescription();
                entityDescription.setContent(ti._text);
                rcr.setEntityDescription(entityDescription);
                // _logger.debug("Root: " + ti.text);
                list.add(rcr);
            }
        }

        for (String association : ti._assocToChildMap.keySet()) {
            List<TreeItem> children = ti._assocToChildMap.get(association);
            Collections.sort(children);
            for (TreeItem childItem : children) {
                getTopNodes(childItem, list, currLevel + 1, maxLevel);
            }
        }
    }

    public void dumpTree(HashMap hmap, String focusCode, int level) {
        try {
            Set keyset = hmap.keySet();
            Object[] objs = keyset.toArray();
            String code = (String) objs[0];
            TreeItem ti = (TreeItem) hmap.get(code);
            for (String association : ti._assocToChildMap.keySet()) {
                _logger.debug("\nassociation: " + association);
                List<TreeItem> children = ti._assocToChildMap.get(association);
                for (TreeItem childItem : children) {
                    _logger.debug(childItem._text + "(" + childItem._code + ")");
                    int knt = 0;
                    if (childItem._expandable) {
                        knt = 1;
                        _logger.debug("\tnode.expandable");

                        printTree(childItem, focusCode, level);

                        List list = getTopNodes(childItem);
                        for (int i = 0; i < list.size(); i++) {
                            Object obj = list.get(i);
                            String nd_code = "";
                            String nd_name = "";
                            if (obj instanceof ResolvedConceptReference) {
                                ResolvedConceptReference node =
                                    (ResolvedConceptReference) list.get(i);
                                nd_code = node.getConceptCode();
                                nd_name = "";
                                if (node.getEntityDescription() != null) {
                                    nd_name = node.getEntityDescription().getContent();
								}
                            } else if (obj instanceof Entity) {
                                Entity node = (Entity) list.get(i);
                                nd_code = node.getEntityCode();
                                nd_name = "";
                                if (node.getEntityDescription() != null) {
                                    nd_name = node.getEntityDescription().getContent();
								}
                            }
                            _logger.debug("TOP NODE: " + nd_name + " ("
                                + nd_code + ")");
                        }

                    } else {
                        _logger.debug("\tnode.NOT expandable");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ====================================================================================================================

    public String[] getHierarchyIDs(
		String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        String[] hier = null;
        Set<String> ids = new HashSet<String>();
        SupportedHierarchy[] sh = null;
        try {
            sh = getSupportedHierarchies(codingScheme, versionOrTag);
            if (sh != null) {
                for (int i = 0; i < sh.length; i++) {
                    ids.add(sh[i].getLocalId());
                }

                // Cache and return the new value ...
                hier = ids.toArray(new String[ids.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hier;
    }
/*
    protected SupportedHierarchy[] getSupportedHierarchies(
        String codingScheme, CodingSchemeVersionOrTag versionOrTag)
            throws LBException {

        CodingScheme cs = null;
        try {
            cs = getCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {

        }
        if (cs == null) {
            throw new LBResourceUnavailableException(
                "Coding scheme not found -- " + codingScheme);
        }
        Mappings mappings = cs.getMappings();
        return mappings.getSupportedHierarchy();
    }
*/
    protected CodingScheme getCodingScheme(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {
        CodingScheme cs = null;
        try {
            cs = lbSvc.resolveCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cs;
    }


/*
    public String[] getHierarchyIDs(String codingScheme,
        CodingSchemeVersionOrTag versionOrTag) throws LBException {

        String[] hier = null;
        Set<String> ids = new HashSet<String>();
        SupportedHierarchy[] sh = null;
        try {
            sh = getSupportedHierarchies(codingScheme, versionOrTag);
            if (sh != null) {
                for (int i = 0; i < sh.length; i++) {
                    ids.add(sh[i].getLocalId());
                }

                // Cache and return the new value ...
                hier = ids.toArray(new String[ids.size()]);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return hier;
    }
*/

    protected  SupportedHierarchy[] getSupportedHierarchies(
        String codingScheme, CodingSchemeVersionOrTag versionOrTag)
            throws LBException {

        CodingScheme cs = null;
        try {
            cs = getCodingScheme(codingScheme, versionOrTag);
        } catch (Exception ex) {

        }
        if (cs == null) {
            throw new LBResourceUnavailableException(
                "Coding scheme not found -- " + codingScheme);
        }
        Mappings mappings = cs.getMappings();
        return mappings.getSupportedHierarchy();
    }


    public String getHierarchyID(String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            String[] ids = getHierarchyIDs(codingScheme, versionOrTag);
            if (ids.length > 0)
                return ids[0];
        } catch (Exception e) {

        }
        return null;
    }

    public ResolvedConceptReferenceList getHierarchyRoots(
        String codingScheme, String version) {
        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);
        try {
            String hierarchyID = getHierarchyID(codingScheme, version);

            ResolvedConceptReferenceList rcrl = lbscm.getHierarchyRoots(codingScheme, versionOrTag,
                hierarchyID);

            return rcrl;

        } catch (Exception ex) {
			ex.printStackTrace();
            return null;
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    public void traverseUp(java.lang.String codingScheme,
        CodingSchemeVersionOrTag versionOrTag, java.lang.String hierarchyID,
        java.lang.String conceptCode, String namespace, TreeItem root, TreeItem ti,
        Set<String> codesToExclude, String[] associationsToNavigate,
        boolean associationsNavigatedFwd, Set<String> visited_links,
        HashMap<String, TreeItem> visited_nodes, int maxLevel, int currLevel) {

        if (maxLevel != -1 && currLevel >= maxLevel) {
            root.addChild("CHD", ti);
            root._expandable = true;
            return;
        }

        boolean resolveConcepts = false;
        NameAndValueList associationQualifiers = null;
        try {
            AssociationList list = null;
            list =
                lbscm.getHierarchyLevelPrev(codingScheme, versionOrTag,
                    hierarchyID, conceptCode, resolveConcepts,
                    associationQualifiers);
            int parent_knt = 0;
            if (list != null && list.getAssociationCount() > 0) {
                Association[] associations = list.getAssociation();
                for (int k = 0; k < associations.length; k++) {
                    Association association = associations[k];
                    AssociatedConceptList acl =
                        association.getAssociatedConcepts();
                    for (int i = 0; i < acl.getAssociatedConceptCount(); i++) {
                        // child_knt = child_knt +
                        // acl.getAssociatedConceptCount();
                        // Determine the next concept in the branch and
                        // add a corresponding item to the tree.
                        AssociatedConcept concept = acl.getAssociatedConcept(i);
                        String parentCode = concept.getConceptCode();
                        String link = conceptCode + "|" + parentCode;
                        if (!visited_links.contains(link)) {
                            visited_links.add(link);
                            // _logger.debug( getCodeDescription(concept) + "("
                            // + parentCode + ")");
                            TreeItem branchItem = null;
                            if (visited_nodes.containsKey(parentCode)) {
                                branchItem =
                                    (TreeItem) visited_nodes.get(parentCode);
                            } else {
                                branchItem =
                                    new TreeItem(parentCode,
                                        getCodeDescription(concept), concept.getCodeNamespace(), null);
                                branchItem._expandable = false;
                                visited_nodes.put(parentCode, branchItem);
                            }

                            try {
                                codesToExclude.add(conceptCode);
                                addChildren(branchItem,
                                    codingScheme, versionOrTag, parentCode,
                                    codesToExclude, associationsToNavigate,
                                    associationsNavigatedFwd);
                            } catch (Exception ex) {

                            }

                            branchItem.addChild("CHD", ti);
                            parent_knt++;

                            // ti.addChild("PAR", branchItem);
                            branchItem._expandable = true;

                            traverseUp(codingScheme,
                                versionOrTag, hierarchyID, parentCode, namespace, root,
                                branchItem, codesToExclude,
                                associationsToNavigate,
                                associationsNavigatedFwd, visited_links,
                                visited_nodes, maxLevel, currLevel + 1);
                        }
                    }
                }
            }

            if (parent_knt == 0) {
                root.addChild("CHD", ti);
                root._expandable = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HashMap getTreePathData2(String scheme, String version, String code, int maxLevel) {
		return getTreePathData2(scheme, version, code, null, maxLevel);
	}


    public HashMap getTreePathData2(String scheme, String version, String code, String namespace, int maxLevel) {

        HashMap hmap = new HashMap();
        TreeItem root = new TreeItem("<Root>", "Root node");
        root._expandable = false;
        long ms = System.currentTimeMillis();

        Set<String> codesToExclude = new HashSet<String>();
        HashMap<String, TreeItem> visited_nodes =
            new HashMap<String, TreeItem>();
        Set<String> visited_links = new HashSet<String>();

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        ResolvedConceptReferenceList matches = null;
        //Vector v = new Vector();
        try {
            CodingSchemeVersionOrTag versionOrTag =
                new CodingSchemeVersionOrTag();
            if (version != null)
                versionOrTag.setVersion(version);

            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            if (cs == null)
                return null;
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0)
                return null;
            SupportedHierarchy hierarchyDefn = hierarchies[0];
            String hierarchyID = hierarchyDefn.getLocalId();
            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();
            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

            String name = getCodeDescription(scheme, csvt, code);
            TreeItem ti = new TreeItem(code, name, namespace, null);
            // ti.expandable = false;
            ti._expandable = hasSubconcepts(scheme, version, code, namespace);

            _logger.debug(name + "(" + code + ")");

            traverseUp(scheme, csvt, hierarchyID, code, namespace, root, ti,
                codesToExclude, associationsToNavigate,
                associationsNavigatedFwd, visited_links, visited_nodes,
                maxLevel, 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            _logger.debug("Run time (milliseconds): "
                + (System.currentTimeMillis() - ms) + " to resolve "
                // + pathsResolved + " paths from root.");
                + " paths from root.");
        }

        hmap.put(code, root);
        return hmap;
    }

    // ////////////////////////////////////////////////////////////////////////////////////////////////////

    public List getHierarchyRoots(String scheme, String version,
        String hierarchyID) throws LBException {
        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        // HL7
        // scheme = DataUtils.searchFormalName(scheme);
        return getHierarchyRoots(scheme, csvt, hierarchyID);
    }

    public List getHierarchyRoots(String scheme, CodingSchemeVersionOrTag csvt,
        String hierarchyID) throws LBException {

        /*
         * // HL7 patch if (scheme.indexOf("HL7") != -1) { return
         * getTreeRoots(scheme, csvt, hierarchyID); }
         */
        int maxDepth = 1;
        ResolvedConceptReferenceList roots =
            lbscm.getHierarchyRoots(scheme, csvt, hierarchyID);

        for (int i = 0; i < roots.getResolvedConceptReferenceCount(); i++) {
            ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
            // _logger.debug("getHierarchyRoots rcr.getConceptCode(): " +
            // rcr.getConceptCode());

            Entity c = rcr.getReferencedEntry();
            if (c != null) {
                rcr.setConceptCode(c.getEntityCode());
            } else {
                _logger
                    .debug("getHierarchyRoots rcr.getReferencedEntry() returns null.");
            }

            if (rcr.getEntityDescription() == null) {
                _logger
                    .debug("getHierarchyRoots rcr.getEntityDescription() == null.");
                String name =
                        getCodeDescription(scheme, csvt, rcr.getConceptCode());
                if (name == null)
                    name = rcr.getConceptCode();// HL7
                EntityDescription e = new EntityDescription();
                e.setContent(name);
                rcr.setEntityDescription(e);
            } else if (rcr.getEntityDescription().getContent() == null) {
                _logger
                    .debug("getHierarchyRoots rcr.getEntityDescription().getContent() == null.");
                String name =
                    getCodeDescription(scheme, csvt, rcr.getConceptCode());
                if (name == null)
                    name = rcr.getConceptCode();// HL7
                EntityDescription e = new EntityDescription();
                e.setContent(name);
                rcr.setEntityDescription(e);
            }
        }

        List list = resolvedConceptReferenceList2List(roots);
        SortUtils.quickSort(list);
        return list;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////
    public List search(String codingSchemeName,
        String t, String algorithm) throws Exception {
        CodedNodeSet cns = lbSvc.getCodingSchemeConcepts(codingSchemeName, null);
        cns =
            cns.restrictToMatchingDesignations(t, SearchDesignationOption.ALL,
                algorithm, null);
        ResolvedConceptReferenceList list = null;
        int knt = 0;
        try {
            list = cns.resolveToList(null, null, null, -1);
            if (list == null)
                return null;
            return resolvedConceptReferenceList2List(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (knt == 0)
            _logger.debug("No match.");
        _logger.debug("\n\n");
        return new ArrayList();
    }

    // For HL7 (not yet used, pending Mayo resolution)
    public List getTreeRoots(String scheme, CodingSchemeVersionOrTag csvt,
        String hierarchyID) throws LBException {

        int maxDepth = 1;
        ResolvedConceptReferenceList roots =
            lbscm.getHierarchyRoots(scheme, csvt, hierarchyID);
        ResolvedConceptReferenceList modified_roots =
            new ResolvedConceptReferenceList();

        int knt0 = 0;
        int knt = 0;
        for (int i = 0; i < roots.getResolvedConceptReferenceCount(); i++) {
            ResolvedConceptReference rcr = roots.getResolvedConceptReference(i);
            // _logger.debug("getHierarchyRoots rcr.getConceptCode(): " +
            // rcr.getConceptCode());

            Entity c = rcr.getReferencedEntry();
            if (c != null) {
                rcr.setConceptCode(c.getEntityCode());
            } else {
                _logger
                    .debug("WARNING: getHierarchyRoots rcr.getReferencedEntry() returns null.");
            }

            if (rcr.getEntityDescription() == null) {
                String name =
                    getCodeDescription(scheme, csvt, rcr
                        .getConceptCode());

                if (name == null) {
                    name =
                        "<Not Assigned> (code: " + rcr.getConceptCode() + ")";// HL7
                }

                EntityDescription e = new EntityDescription();
                e.setContent(name);
                rcr.setEntityDescription(e);
            } else if (rcr.getEntityDescription().getContent() == null) {
                _logger
                    .debug("getHierarchyRoots rcr.getEntityDescription().getContent() == null.");
                String name =
                    getCodeDescription(scheme, csvt, rcr.getConceptCode());
                if (name == null) {
                    name =
                        "<Not Assigned> (code: " + rcr.getConceptCode() + ")";// HL7
                }
                EntityDescription e = new EntityDescription();
                e.setContent(name);
                rcr.setEntityDescription(e);
            }
            List list = new ArrayList();
            try {
                list =
                    search(scheme, rcr.getEntityDescription()
                        .getContent(), "exactMatch");

            } catch (Exception ex) {

            }
            if (list.size() == 0)
                knt0++;
            else if (list.size() > 1)
                knt++;

            if (list.size() != 0) {
                ResolvedConceptReference rc =
                    (ResolvedConceptReference) list.get(0);
                modified_roots.addResolvedConceptReference(rc);
            } else {
                modified_roots.addResolvedConceptReference(rcr);
            }
        }
        List list = resolvedConceptReferenceList2List(modified_roots);
        SortUtils.quickSort(list);
        return list;
    }

    public List resolvedConceptReferenceList2List(
        ResolvedConceptReferenceList rcrl) {
        ArrayList list = new ArrayList();
        for (int i = 0; i < rcrl.getResolvedConceptReferenceCount(); i++) {
            ResolvedConceptReference rcr = rcrl.getResolvedConceptReference(i);
            list.add(rcr);
        }
        return list;
    }

    public String[] getAssociationsToNavigate(String scheme,
        String version) {

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);

        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            if (cs == null)
                return null;
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0)
                return null;

            SupportedHierarchy hierarchyDefn = hierarchies[0];
            //String hier_id = hierarchyDefn.getLocalId();

            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();
            return associationsToNavigate;
        } catch (Exception ex) {

        }
        return null;
    }

/*
    public ArrayList getSubconceptNamesAndCodes(String scheme, String version, String code, String namespace) {
        ArrayList list = new ArrayList();

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        long ms = System.currentTimeMillis();
        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            if (cs == null)
                return null;
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0)
                return null;

            SupportedHierarchy hierarchyDefn = hierarchies[0];
            //String hier_id = hierarchyDefn.getLocalId();

            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();
            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

            NameAndValueList nameAndValueList =
                createNameAndValueList(associationsToNavigate, null);

            ResolvedConceptReferenceList matches = null;
            try {
                CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
           		Boolean restrictToAnonymous = Boolean.FALSE;
                cng = cng.restrictToAnonymous(restrictToAnonymous);

                NameAndValueList nameAndValueList_qualifier = null;

                cng =
                    cng.restrictToAssociations(nameAndValueList,
                        nameAndValueList_qualifier);

				Entity concept = null;
				if (namespace == null) {
					concept = conceptDetails.getConceptByCode(scheme, version, code);
				} else {
					concept = conceptDetails.getConceptByCode(scheme, version, code, namespace, true);
				}

				if (concept != null) {
					String entityCodeNamespace = concept.getEntityCodeNamespace();

					ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
					focus.setCodingSchemeName(scheme);
					focus.setCodeNamespace(entityCodeNamespace);


					matches =
						cng.resolveAsList(focus, associationsNavigatedFwd,
							!associationsNavigatedFwd, 1, 1, new LocalNameList(),
							null, null, -1);
				}
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Analyze the result ...
            if (matches != null
                && matches.getResolvedConceptReferenceCount() > 0) {
                ResolvedConceptReference ref =
                    (ResolvedConceptReference) matches
                        .enumerateResolvedConceptReference().nextElement();
                if (ref != null) {
                    AssociationList sourceof = ref.getSourceOf();
                    if (!associationsNavigatedFwd)
                        sourceof = ref.getTargetOf();

                    if (sourceof != null) {
                        Association[] associations = sourceof.getAssociation();
                        if (associations != null) {
                            for (int i = 0; i < associations.length; i++) {
                                Association assoc = associations[i];
                                if (assoc != null) {
                                    if (assoc.getAssociatedConcepts() != null) {
                                        AssociatedConcept[] acl =
                                            assoc.getAssociatedConcepts()
                                                .getAssociatedConcept();
                                        if (acl != null) {
                                            for (int j = 0; j < acl.length; j++) {
                                                AssociatedConcept ac = acl[j];
                                                // KLO, 030110
                                                if (ac != null
                                                    && ac.getConceptCode()
                                                        .indexOf("@") == -1) {
                                                    EntityDescription ed =
                                                        ac.getEntityDescription();
                                                    if (ed != null) {
                                                        list
                                                            .add(ed.getContent()
                                                                + "|"
                                                                + ac.getConceptCode()
                                                                + "|"
                                                                + ac.getCodeNamespace()
                                                                );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        SortUtils.quickSort(list);
        return list;
    }
*/
    public ArrayList getSubconceptNamesAndCodes(String scheme, String version, String code) {
		return getSubconceptNamesAndCodes(scheme, version, code, null);
	}


    public ArrayList getSubconceptNamesAndCodes(String scheme, String version, String code, String namespace) {
        // returned bar delimited name|code
        ArrayList list = new ArrayList();

        CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
        if (version != null)
            csvt.setVersion(version);
        long ms = System.currentTimeMillis();
        try {
            CodingScheme cs = lbSvc.resolveCodingScheme(scheme, csvt);
            if (cs == null)
                return null;
            Mappings mappings = cs.getMappings();
            SupportedHierarchy[] hierarchies = mappings.getSupportedHierarchy();
            if (hierarchies == null || hierarchies.length == 0)
                return null;

            SupportedHierarchy hierarchyDefn = hierarchies[0];
            //String hier_id = hierarchyDefn.getLocalId();

            String[] associationsToNavigate =
                hierarchyDefn.getAssociationNames();
            boolean associationsNavigatedFwd =
                hierarchyDefn.getIsForwardNavigable();

            NameAndValueList nameAndValueList =
                createNameAndValueList(associationsToNavigate, null);

            ResolvedConceptReferenceList matches = null;
            try {
                CodedNodeGraph cng = lbSvc.getNodeGraph(scheme, csvt, null);
           		Boolean restrictToAnonymous = Boolean.FALSE;
                cng = cng.restrictToAnonymous(restrictToAnonymous);

                NameAndValueList nameAndValueList_qualifier = null;

                cng =
                    cng.restrictToAssociations(nameAndValueList,
                        nameAndValueList_qualifier);

				Entity concept = null;
				if (namespace == null) {
					concept = conceptDetails.getConceptByCode(scheme, version, code);
				} else {
					concept = conceptDetails.getConceptByCode(scheme, version, code, namespace, true);
				}


				//if (concept != null) {
					//String entityCodeNamespace = concept.getEntityCodeNamespace();

					ConceptReference focus = ConvenienceMethods.createConceptReference(code, scheme);
					focus.setCodingSchemeName(scheme);

			if (namespace == null) {
                List<String> ns_list = lbscm.getDistinctNamespacesOfCode(scheme, csvt, code);
                if (ns_list.size() == 1) {
					// do nothing???
				}
			} else {
				if (!Arrays.asList(gov.nih.nci.evs.browser.common.Constants.TERMINOLOGY_VALUE_SET_NAMES).contains(scheme)) {
					focus.setCodeNamespace(namespace);
				}
			}

					//focus.setCodeNamespace(entityCodeNamespace);

					matches =
						cng.resolveAsList(focus, associationsNavigatedFwd,
							!associationsNavigatedFwd, 1, 1, new LocalNameList(),
							null, null, -1);
				//}
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Analyze the result ...
            if (matches != null
                && matches.getResolvedConceptReferenceCount() > 0) {
                ResolvedConceptReference ref =
                    (ResolvedConceptReference) matches
                        .enumerateResolvedConceptReference().nextElement();
                if (ref != null) {
                    AssociationList sourceof = ref.getSourceOf();
                    if (!associationsNavigatedFwd)
                        sourceof = ref.getTargetOf();

                    if (sourceof != null) {
                        Association[] associations = sourceof.getAssociation();
                        if (associations != null) {
                            for (int i = 0; i < associations.length; i++) {
                                Association assoc = associations[i];
                                if (assoc != null) {
                                    if (assoc.getAssociatedConcepts() != null) {
                                        AssociatedConcept[] acl =
                                            assoc.getAssociatedConcepts()
                                                .getAssociatedConcept();
                                        if (acl != null) {
                                            for (int j = 0; j < acl.length; j++) {
                                                AssociatedConcept ac = acl[j];
                                                // KLO, 030110
                                                if (ac != null
                                                    && ac.getConceptCode()
                                                        .indexOf("@") == -1) {
                                                    EntityDescription ed =
                                                        ac.getEntityDescription();
                                                    if (ed != null) {
                                                        list
                                                            .add(ed.getContent()
                                                                + "|"
                                                                + ac.getConceptCode()
                                                                + "|"
                                                                + ac.getCodeNamespace()
                                                                );
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        _logger.debug("Run time (milliseconds) getSubconcepts: "
            + (System.currentTimeMillis() - ms) + " to resolve ");
        SortUtils.quickSort(list);
        return list;
    }



    public static void relabelTreeNodes(HashMap hmap) {
		//Iterator it = hmap.keySet().iterator();
		Iterator it = hmap.entrySet().iterator();
		while (it.hasNext()) {
            Entry thisEntry = (Entry) it.next();
			String key = (String) thisEntry.getKey();
			//TreeItem ti = (TreeItem) hmap.get(key);
			TreeItem ti = (TreeItem) thisEntry.getValue();

			for (String association : ti._assocToChildMap.keySet()) {
				List<TreeItem> children = ti._assocToChildMap.get(association);
				for (TreeItem childItem : children) {
					String code = childItem._code;
					String text = childItem._text;
					//childItem._text = childItem._code + " (" + text + ")";
					childItem._text = childItem._code;
				}

			}
		}
	}

	public static HashMap combine(HashMap hmap1, HashMap hmap2) {
		if (hmap1 == null && hmap2 == null) return null;
		if (hmap1 == null && hmap2 != null) return hmap2;
		if (hmap2 == null && hmap1 != null) return hmap1;

		TreeItem ti = new TreeItem("<Root>", "Root node");
		ti._expandable = false;

		TreeItem root = null;
		Iterator it = null;
		if (hmap1 != null) {
			it = hmap1.entrySet().iterator();
			while (it.hasNext()) {
				Entry thisEntry = (Entry) it.next();
				String key = (String) thisEntry.getKey();
				root = (TreeItem) thisEntry.getValue();
				if (root != null) {
					for (String association : root._assocToChildMap.keySet()) {
						List<TreeItem> children = root._assocToChildMap.get(association);
						for (TreeItem childItem : children) {
							ti.addChild(association, childItem);
							ti._expandable = true;
						}
					}
				}
			}
	    }

	    if (hmap2 != null) {
			it = hmap2.entrySet().iterator();
			while (it.hasNext()) {
				Entry thisEntry = (Entry) it.next();
				String key = (String) thisEntry.getKey();
				root = (TreeItem) thisEntry.getValue();

				if (root != null) {
					for (String association : root._assocToChildMap.keySet()) {
						List<TreeItem> children = root._assocToChildMap.get(association);
						for (TreeItem childItem : children) {
							ti.addChild(association, childItem);
							ti._expandable = true;
						}
					}
				}
			}
	    }
		HashMap hmap = new HashMap();
		hmap.put("<Root>", ti);
		return hmap;
	}


    public Vector getSuperconceptCodes(String scheme, String version, String code) {
        long ms = System.currentTimeMillis();
        Vector v = new Vector();
        try {
            CodingSchemeVersionOrTag csvt = new CodingSchemeVersionOrTag();
            csvt.setVersion(version);
            // Iterate through all hierarchies and levels ...
            String[] hierarchyIDs = lbscm.getHierarchyIDs(scheme, csvt);
            for (int k = 0; k < hierarchyIDs.length; k++) {
                String hierarchyID = hierarchyIDs[k];
                AssociationList associations =
                    lbscm.getHierarchyLevelPrev(scheme, csvt, hierarchyID,
                        code, false, null);
                for (int i = 0; i < associations.getAssociationCount(); i++) {
                    Association assoc = associations.getAssociation(i);
                    AssociatedConceptList concepts =
                        assoc.getAssociatedConcepts();
                    for (int j = 0; j < concepts.getAssociatedConceptCount(); j++) {
                        AssociatedConcept concept =
                            concepts.getAssociatedConcept(j);
                        String nextCode = concept.getConceptCode();
                        v.add(nextCode);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            _logger
                .debug("Run time (ms): " + (System.currentTimeMillis() - ms));
        }
        return v;
    }
}

