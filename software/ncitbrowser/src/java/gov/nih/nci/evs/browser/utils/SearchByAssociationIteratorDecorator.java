/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.LexGrid.LexBIG.DataModel.Collections.AssociationList;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;
import org.LexGrid.LexBIG.DataModel.Core.AssociatedConcept;
import org.LexGrid.LexBIG.DataModel.Core.Association;
import org.LexGrid.LexBIG.DataModel.Core.ConceptReference;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.Exceptions.LBResourceUnavailableException;
import org.LexGrid.LexBIG.Impl.helpers.IteratorBackedResolvedConceptReferencesIterator;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeGraph;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.Iterators.ResolvedConceptReferencesIterator;
import org.lexevs.paging.AbstractPageableIterator;

import org.LexGrid.LexBIG.DataModel.Core.NameAndValue;
import org.LexGrid.LexBIG.DataModel.Collections.NameAndValueList;

import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Collections.LocalNameList;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;


import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;


/**
 * 
 */

/**
 * The Class SearchByAssociationIteratorDecorator. Decorates a
 * ResolvedConceptReferencesIterator to provide paging support for Associated
 * Concept-type searches. As the iterator advances, subconcepts are queried from
 * the decorated iterator on demand, rather than all at once. This elminates the
 * need to resolve large CodedNodeGraphs.
 */
public class SearchByAssociationIteratorDecorator extends
        IteratorBackedResolvedConceptReferencesIterator {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4126716487618136771L;

    /** The lbs. */
    private static LexBIGService lbs = RemoteServerUtil.createLexBIGService();

    /** The quick iterator. */
    private static ResolvedConceptReferencesIterator _quickIterator;

    /** The resolve forward. */
    private static boolean _resolveForward;

    /** The resolve backward. */
    private static boolean _resolveBackward;

    /** The resolve association depth. */
    private static int _resolveAssociationDepth;

    /** The max to return. */
    private static int _maxToReturn;


    private static NameAndValueList _associationNameAndValueList;
    private static NameAndValueList _associationQualifierNameAndValueList;


    /**
     * Instantiates a new search by association iterator decorator.
     *
     * @param quickIterator the quick iterator
     * @param resolveForward the resolve forward
     * @param resolveBackward the resolve backward
     * @param resolveAssociationDepth the resolve association depth
     * @param maxToReturn the max to return
     * @throws LBResourceUnavailableException
     */
    public SearchByAssociationIteratorDecorator(
        ResolvedConceptReferencesIterator quickIterator) throws LBResourceUnavailableException {

    	super(doBuildIterator(quickIterator), quickIterator.numberRemaining());
        _quickIterator = quickIterator;
    }


    public void setQuickIterator(ResolvedConceptReferencesIterator quickIterator) {
		_quickIterator = quickIterator;
	}

    public void setResolveForward(boolean resolveForward) {
		_resolveForward = resolveForward;
	}

    public void setResolveBackward(boolean resolveBackward) {
		_resolveBackward = resolveBackward;
	}

    public void setResolveAssociationDepth(int resolveAssociationDepth) {
		_resolveAssociationDepth = resolveAssociationDepth;
	}

    public void setMaxToReturn(int maxToReturn) {
		_maxToReturn = maxToReturn;
	}

    public void setAssociationNameAndValueList(NameAndValueList associationNameAndValueList) {
		_associationNameAndValueList = associationNameAndValueList;
	}

    public void setAssociationQualifierNameAndValueList(NameAndValueList associationQualifierNameAndValueList) {
		_associationQualifierNameAndValueList = associationQualifierNameAndValueList;
	}

	private static Iterator<ResolvedConceptReference> doBuildIterator(
			ResolvedConceptReferencesIterator quickIterator) {
		return new SearchAssociationResolvedConceptReference(quickIterator);
	}

	private static class SearchAssociationResolvedConceptReference extends
		AbstractPageableIterator<ResolvedConceptReference> {

		private static final long serialVersionUID = 2158463303566749525L;

		private ResolvedConceptReferencesIterator quickIterator;

		private SearchAssociationResolvedConceptReference(ResolvedConceptReferencesIterator quickIterator){
			super();
			this.quickIterator = quickIterator;
		}

		@Override
		protected List<? extends ResolvedConceptReference> doPage(
				int position,
				int pageSize) {


			List<ResolvedConceptReference> returnList = new ArrayList<ResolvedConceptReference>();
			try {
				while(quickIterator.hasNext() && returnList.size() < pageSize){
					returnList.addAll(this.resolveOneHit(quickIterator.next()));
				}
			} catch (LBException e) {
				throw new RuntimeException(e);
			}
			return returnList;
		}

        // IMPORTANT: Apply restrictions to associations for supporting advanced search (to be implemented later)

		protected List<? extends ResolvedConceptReference> resolveOneHit(ResolvedConceptReference hit) throws LBException{
			List<ResolvedConceptReference> returnList = new ArrayList<ResolvedConceptReference>();

			CodingSchemeVersionOrTag tagOrVersion = new CodingSchemeVersionOrTag();
			if (hit.getCodingSchemeVersion() != null) tagOrVersion.setVersion(hit.getCodingSchemeVersion());

            CodedNodeGraph cng = null;
 			cng = lbs.getNodeGraph(
					hit.getCodingSchemeName(),
					tagOrVersion,
					null);

            Boolean restrictToAnonymous = Boolean.FALSE;
            cng = cng.restrictToAnonymous(restrictToAnonymous);

            LocalNameList localNameList = new LocalNameList();
            localNameList.addEntry("concept");

            cng = cng.restrictToEntityTypes(localNameList);


			if (_associationNameAndValueList != null) {
				cng =
					cng.restrictToAssociations(
						_associationNameAndValueList,
						_associationQualifierNameAndValueList);
			}

			else {
				String scheme = hit.getCodingSchemeName();
				boolean isMapping = DataUtils.isMapping(scheme, null);
				if (isMapping) {
					NameAndValueList navl = DataUtils.getMappingAssociationNames(scheme, null);
					if (navl != null) {
						cng = cng.restrictToAssociations(navl, null);
					}
				}
			}

			ConceptReference focus = new ConceptReference();
			focus.setCode(hit.getCode());
			focus.setCodingSchemeName(hit.getCodingSchemeName());
			focus.setCodeNamespace(hit.getCodeNamespace());

			LocalNameList propertyNames = new LocalNameList();
			CodedNodeSet.PropertyType[] propertyTypes = null;
			SortOptionList sortCriteria = null;

			ResolvedConceptReferenceList list =
				cng.resolveAsList(focus,
					_resolveForward, _resolveBackward, 0,
					_resolveAssociationDepth, propertyNames, propertyTypes, sortCriteria,
					_maxToReturn);

			for(ResolvedConceptReference ref : list.getResolvedConceptReference()){
				if(ref.getSourceOf() != null){
					returnList.addAll(this.getAssociations(ref.getSourceOf()));
				}
				if(ref.getTargetOf() != null){
					returnList.addAll(this.getAssociations(ref.getTargetOf()));
				}
			}
			return returnList;
		}

		protected List<? extends ResolvedConceptReference> getAssociations(AssociationList list){
			List<ResolvedConceptReference> returnList = new ArrayList<ResolvedConceptReference>();

			for(Association assoc : list.getAssociation()){
				for(AssociatedConcept ac :
					assoc.getAssociatedConcepts().getAssociatedConcept()){

//System.out.println("(*) SearchByAssociationIteratorDecorator " + ac.getEntityDescription().getContent() + " (" + ac.getConceptCode() + ")");

					returnList.add(ac);
				}
			}

			return returnList;
		}


	}
}
