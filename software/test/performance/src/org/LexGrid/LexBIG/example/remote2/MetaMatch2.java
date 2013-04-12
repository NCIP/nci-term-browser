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
 *      http://www.eclipse.org/legal/epl-v10.html
 *
 */
package org.LexGrid.LexBIG.example.remote2;

import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.DataModel.Collections.ResolvedConceptReferenceList;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.LexGrid.LexBIG.DataModel.Collections.SortOptionList;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeSummary;
import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.LexGrid.LexBIG.DataModel.Core.ResolvedConceptReference;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.PropertyType;
import org.LexGrid.LexBIG.LexBIGService.CodedNodeSet.SearchDesignationOption;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.Utility.LBConstants.MatchAlgorithms;
import org.LexGrid.LexBIG.util.AppService;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;
import org.LexGrid.LexBIG.util.Util;
import org.LexGrid.concepts.Concept;

/**
 * Example attempting to approximate some characteristics of the
 * Metaphrase search algorithm.  However, full Metaphrase compatibility
 * is not anticipated.
 */
public class MetaMatch2 implements Constants {
    private CodingSchemeSummary _css = null;
    private LexBIGService _lbSvc = null;
    private String _scheme = null;
    private CodingSchemeVersionOrTag _csvt = null;
    private StringBuffer _buffer = new StringBuffer();
    boolean _displayConcepts = true;
    
    // Identify common stop words (words to be ignored in most match circumstances).
    // This list extends from the LVG stop words ...
    static final List<String> STOP_WORDS = Arrays.asList(new String[] {
        "a", "an", "and", "by", "for", "of", "on", "in", "nos", "the", "to", "with"});

    /**
     * Entry point for processing.
     * @param args
     */
    public static void main(String[] args) {
        try {
            new MetaMatch2().run();
        } catch (Exception e) {
            Util.displayAndLogError("REQUEST FAILED !!!", e);
        }
    }

    /**
     * Default constructor.
     */
    public MetaMatch2() {
        AppService service = AppService.getInstance();
        _css = service.getCSS();
        _lbSvc = AppService.getLBSvc();
        _scheme = service.getScheme();
        _csvt = service.getCSVT();
    }

    public void run()throws LBException{
        if (_css == null)
            return;

        String word = "cell";
        while (true) {
            word = Prompt.prompt("Word (q to Quit)", word);
            if (word.equalsIgnoreCase("q"))
                break;
            
            _displayConcepts = Prompt.prompt("Display Concepts", _displayConcepts);
            process(word);

            Util.displayMessage("");
            Util.displayMessage(SEPARATOR);
        }
        Util.displayMessage("Quit");
    }
    
    public void run2() throws LBException {
        String words[] = new String[] {
            "cell", "blood"
        };
        
        _displayConcepts = Prompt.prompt("Display Concepts", _displayConcepts);
        for (int i=0; i<words.length; ++i) {
            if (i > 0) {
                Util.displayMessage("");
                Util.displayMessage(SEPARATOR);
            }
            Util.displayMessage("*** Keyword: " + words[i]);
            process(words[i]);
        }
        Util.displayMessage("Done");
    }
    
    public void process(String word) throws LBException {
        matchSynonyms(word);
        matchSpell(word);
        matchTermCompletion(word);
        matchWordCompletion(word);
        matchSubquery(word);
    }

    /**
     * Display concepts and related text strings matching the given string.
     * @param s The test string.
     * @throws LBException
     */
    protected void matchSynonyms(String s)
        throws LBException
    {
        _buffer.delete(0, _buffer.length());
        Util.StopWatch stopWatch = new Util.StopWatch();
        _buffer.append("*** Matching synonyms (incorporates partial normalization/stemming).\n");

        // An attempt is made to use a partial normalized form on search by
        // removing stop words and utilizing a stemmed query algorithm.
        // Query is performed over all text representations (preferred and
        // non-preferred) that contain every non-stop word in the given
        // string.
        //
        // On resolve, we sort by lucene score ('matchToQuery' sort algorithm).

        StringBuffer searchPhrase = new StringBuffer();
        String[] words = toWords(s, true);
        for (int i = 0; i < words.length; i++) {
            if (i > 0)
                searchPhrase.append(" AND ");
            searchPhrase.append(words[i]);
        }

        // Define the code set and add restrictions ...
        CodedNodeSet nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        nodes.restrictToMatchingDesignations(
            searchPhrase.toString(), SearchDesignationOption.ALL, "StemmedLuceneQuery", null);
        SortOptionList sortCriteria =
            Constructors.createSortOptionList(new String[]{"matchToQuery"});
        PropertyType[] fetchTypes =
            new PropertyType[] {PropertyType.PRESENTATION};

        // Resolve and analyze the result ...
        ResolvedConceptReferenceList matches =
            nodes.resolveToList(sortCriteria, null, fetchTypes, -1);

        // Found a match?  If so, sort according to relevance.
        if (matches.getResolvedConceptReferenceCount() > 0) {
            // NOTE that a match so far only indicates that all of the words in the
            // passed in string also exist in a term assigned to the resolved
            // concepts.  It does not, however, exclude results where the terms
            // also have additional words.  This code currently processes only
            // full matches by calling getReferenceWeight and taking only those
            // values with weight '1'.

            // List concept references with exact correlation of at least one
            // concept term to the compare string.
            final List<String> matchWords = Arrays.asList(words);
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                if (getReferenceWeight(ref, matchWords) == 1)
                    ctr = printText(ctr, ref);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Attempt to approximate spelling suggestions.
     * @param s The test string.
     * @throws LBException
     */
    protected void matchSpell(String s)
        throws LBException
    {
        _buffer.delete(0, _buffer.length());
        Util.StopWatch stopWatch = new Util.StopWatch();
        _buffer.append("\n*** Matching spell suggestions ...\n");

        // Note: Metaphrase contained an integrated spell check module;
        // LexBIG does not as of the 2.0 release.  Instead, spell
        // suggestion is based off entire phrase in comparison
        // to content stored in the ontology; no external dictionary
        // is utilized.  While a dedicated speller has advantages,
        // there are also advantages to this approach (e.g. automatic
        // tuning of the search to the ontology content).
        //
        // Two different algorithms are highlighted in this code
        // for consideration. The first utilizes the lucene-based
        // Levenshtein distance algorithm.  The second utilizes the
        // DoubleMetaphoneLuceneQuery (sounds-like) algorithm.
        //
        // Queries are currently made against all text representations
        // and not just the primary text representation for ,
        // but this could be narrowed.
        //
        // On resolve, we sort by lucene score ('matchToQuery' sort algorithm).

        // Levenshtein 'edit distance' processing ...
        String[] words = toWords(s, true);
        String prefix = words[0].substring(0, 1);

        CodedNodeSet nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        StringBuffer searchPhrase = new StringBuffer(s).append('~');
        nodes.restrictToMatchingDesignations(
            searchPhrase.toString(), SearchDesignationOption.ALL,
            MatchAlgorithms.LuceneQuery.toString(), null);

        SortOptionList sortCriteria =
            Constructors.createSortOptionList(new String[]{"matchToQuery"});

        // Resolve and analyze the result ...
        ResolvedConceptReferenceList matches =
            nodes.resolveToList(sortCriteria, null, new PropertyType[] {}, 5);

        // Found a match?
        _buffer.append("* Levenshtein distance (limited to 5 hits): \n");
        if (matches.getResolvedConceptReferenceCount() > 0) {
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                // OK, so we have it narrowed to a concept at this point but not a
                // specific term.  Narrow the choices to those that include the same
                // starting letter and number of words as our test phrase.
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                ctr = printText(ctr, ref, true, prefix, words.length);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());

        //----------------------------------------------------------------------
        // Sounds-like processing ...
        //----------------------------------------------------------------------
        _buffer.delete(0, _buffer.length());
        stopWatch.start();
        
        nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        nodes.restrictToMatchingDesignations(
            s, SearchDesignationOption.ALL,
            MatchAlgorithms.DoubleMetaphoneLuceneQuery.toString(), null);

        // Resolve and analyze the result ...
        matches = nodes.resolveToList(sortCriteria, null, new PropertyType[] {}, 5);

        // Found a match?
        _buffer.append("\n* Sounds-like/DoubleMetaphone (limited to 5 hits): \n");
        if (matches.getResolvedConceptReferenceCount() > 0) {
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                // Once again we have it narrowed to a concept at this point but not
                // a specific term.  Narrow the choices to those that include the same
                // starting letter and number of words as our test phrase.
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                ctr = printText(ctr, ref, true, prefix, words.length);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Attempt to approximate term completion.
     * @param s The test string.
     * @throws LBException
     */
    @SuppressWarnings("unchecked")
    protected void matchTermCompletion(String s)
        throws LBException
    {
        _buffer.delete(0, _buffer.length());
        Util.StopWatch stopWatch = new Util.StopWatch();
        _buffer.append("\n*** Term completion ...\n");

        // Term completion is basically 'startsWith'.  Two different
        // queries are tried to match.  The first utilizes the standard
        // startsWith match algorithm.  The second utilizes regular
        // expressions to try and accomplish short-circuiting more
        // efficiently.
        //
        // Queries are currently made against all text representations,
        // but this could be narrowed.
        //
        // On resolve, we sort by lucene score ('matchToQuery' sort algorithm).

        String prefix = s.toLowerCase();
        String[] words = toWords(s, false);
        CodedNodeSet nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        nodes.restrictToMatchingDesignations(
            prefix, SearchDesignationOption.ALL,
            "startsWith", null);
        SortOptionList sortCriteria =
            Constructors.createSortOptionList(new String[]{"matchToQuery"});

        // Resolve and analyze the result ...
        ResolvedConceptReferenceList matches =
            nodes.resolveToList(sortCriteria, null, new PropertyType[] {}, 5);

        // Found a match?
        _buffer.append("* startsWith (limited to 5 hits); short circuit after query...\n");
        if (matches.getResolvedConceptReferenceCount() > 0) {
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                ctr = printText(ctr, ref, false, prefix, words.length + 1);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());

        //----------------------------------------------------------------------
        // Try again with regular expression.
        //----------------------------------------------------------------------
        _buffer.delete(0, _buffer.length());
        stopWatch.start();
        nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        String regex = '^' + prefix + "\\W*\\w*\\Z";
        nodes.restrictToMatchingDesignations(
                regex, SearchDesignationOption.ALL,
                "RegExp", null);

        // Resolve and analyze the result ...
        matches = nodes.resolveToList(sortCriteria, null, new PropertyType[] {}, 5);

        // Found a match?
        _buffer.append("\n* RegExp match (limited to 5 hits); short circuit on query...\n");
        if (matches.getResolvedConceptReferenceCount() > 0) {
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                ctr = printText(ctr, ref, false, prefix, words.length + 1);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Attempt to approximate word completion.
     * @param s The test string.
     * @throws LBException
     */
    @SuppressWarnings("unchecked")
    protected void matchWordCompletion(String s)
        throws LBException
    {
        _buffer.delete(0, _buffer.length());
        Util.StopWatch stopWatch = new Util.StopWatch();
        _buffer.append("\n*** Word completion (based on '.') ...\n");

        // For word completion, we just use a regular expression and
        // substitute any alpha-numeric character for the substitution
        // character ('.').
        // Queries are currently made against all text representations,
        // but this could be narrowed.
        //
        // On resolve, we sort by lucene score ('matchToQuery' sort algorithm).

        String prefix = s.toLowerCase();
        String[] words = toWords(prefix, false);
        StringBuffer regex = new StringBuffer();
        regex.append('^');
        for (int i = 0; i < words.length; i++) {
            boolean lastWord = i == words.length - 1;
            String word = words[i];
            regex.append('(');
            if (word.charAt(word.length() - 1) == '.') {
                regex.append(word.substring(0, word.length()));
                regex.append("\\w*");
            }
            else
                regex.append(word);
            regex.append("\\s").append(lastWord ? '*' : '+');
            regex.append(')');
        }
        regex.append("\\Z");

        CodedNodeSet nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        nodes.restrictToMatchingDesignations(
                regex.toString(), SearchDesignationOption.ALL,
                "RegExp", null);
        SortOptionList sortCriteria =
            Constructors.createSortOptionList(new String[]{"matchToQuery"});

        // Resolve and analyze the result ...
        ResolvedConceptReferenceList matches =
            nodes.resolveToList(sortCriteria, null, new PropertyType[] {}, 5);

        // Found a match?
        _buffer.append("* Found via RegExp match (limited to 5 hits)...\n");
        if (matches.getResolvedConceptReferenceCount() > 0) {
            int dotIndex = prefix.indexOf('.');
            if (dotIndex < 0)
                dotIndex = prefix.length();
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                ctr = printText(ctr, ref, false, prefix.substring(0, dotIndex), -1);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Attempt to approximate compositional or sub-query match (e.g., "peptic ulcer"
     * will match the two separate concepts for "peptic" and "ulcer", in case the
     * ontology does not contain any concept matching the full text "peptic ulcer").
     * @param s The test string.
     * @throws LBException
     */
    protected void matchSubquery(String s)
        throws LBException
    {
        _buffer.delete(0, _buffer.length());
        Util.StopWatch stopWatch = new Util.StopWatch();
        _buffer.append("\n*** Subquery match (incorporates partial normalization/stemming).\n");

        // Similar to synonym match, only allow match on any single non-stop word
        // and only print presentations matching the query.
        //
        // On resolve, we sort by lucene score ('matchToQuery' sort algorithm).

        StringBuffer searchPhrase = new StringBuffer();
        String[] words = toWords(s, true);
        for (int i = 0; i < words.length; i++) {
            if (i > 0)
                searchPhrase.append(" OR ");
            searchPhrase.append(words[i]);
        }

        // Define the code set and add restrictions ...
        CodedNodeSet nodes = _lbSvc.getCodingSchemeConcepts(_scheme, _csvt);
        nodes.restrictToMatchingDesignations(
            searchPhrase.toString(), SearchDesignationOption.ALL, "StemmedLuceneQuery", null);
        SortOptionList sortCriteria =
            Constructors.createSortOptionList(new String[]{"matchToQuery"});
        PropertyType[] fetchTypes =
            new PropertyType[] {PropertyType.PRESENTATION};

        // Resolve and analyze the result ...
        ResolvedConceptReferenceList matches =
            nodes.resolveToList(sortCriteria, null, fetchTypes, -1);

        // Found a match?  If so, print each associated presentation containing at least
        // one word from the query expression (exclude non-matching synonyms).
        if (matches.getResolvedConceptReferenceCount() > 0) {
            int ctr = 0;
            for (int i = 0; i < matches.getResolvedConceptReferenceCount(); i++) {
                ResolvedConceptReference ref = matches.getResolvedConceptReference(i);
                ctr = printText(ctr, ref, words, 1);
            }
        } else {
            _buffer.append("\tNo match found.\n");
        }
        _buffer.append("* " + stopWatch.getResult());
        Util.displayMessage(_buffer.toString());
    }

    /**
     * Return a relative weight between 0 and 1 that indicates how well the
     * given reference maps to a set of words.
     * @param ref
     * @param matchWords
     * @return
     */
    protected float getReferenceWeight(ResolvedConceptReference ref, List<String> matchWords) {
        float weight = 0;
        Concept ce = ref.getReferencedEntry();
        int presCount = ce.getPresentationCount();
        for (int i = 0; i < presCount; i++) {
            weight = Math.max(weight, getTextWeight(ce.getPresentation(i).getText().getContent(), matchWords));
        }
        return weight;
    }

    /**
     * Return a relative weight between 0 and 1 that indicates how well the
     * given string maps to a set of words.
     * @param ref
     * @param matchWords
     * @return
     */
    protected float getTextWeight(String text, List<String> matchWords) {
        int matchedInTextWords = 0;
        int matchedInCompareWords = 0;
        String[] textWords = toWords(text, true);
        Set<String> textWordSet = new HashSet<String>();
        for (int i = 0; i < textWords.length; i++) {
            String word = textWords[i];
            textWordSet.add(word);
            if (matchWords.contains(word))
                matchedInCompareWords++;
        }
        for (Iterator<String> compareWords = matchWords.iterator(); compareWords.hasNext(); ) {
            String word = compareWords.next();
            if (textWordSet.contains(word))
                matchedInTextWords++;
        }
        float textToCompareRatio = matchedInCompareWords/textWordSet.size();
        float compareToTextRatio = matchedInTextWords/matchWords.size();
        return (textToCompareRatio + compareToTextRatio)/2;
    }

    /**
     * Print code and text for all text presentations associated
     * with the given concept reference.
     * @param ref
     */
    protected int printText(int ctr, ResolvedConceptReference ref) {
        return printText(ctr, ref, true, (String) null, -1);
    }

    /**
     * Print code and text for all text presentations associated
     * with the given concept reference.  If specified, any printed terms
     * will have stop words removed prior to comparison, must include
     * the given prefix, and can be constrained to those with a set
     * number of words.
     * @param ref
     * @param removeStopWords
     * @param prefix
     * @param wordCount
     */
    protected int printText(int ctr, ResolvedConceptReference ref, boolean removeStopWords, 
            String prefix, int wordCount) {
        // Identify the unique set of text presentations;
        // avoid duplicate registration by multiple sources, etc ...
        Concept concept = ref.getReferencedEntry();
        Set<String> presentations = new HashSet<String>();
        for (int j = 0; j < concept.getPresentationCount(); j++)
            presentations.add(concept.getPresentation(j).getText().getContent());

        // Print the result
        String code = "[" + ref.getConceptCode() + ']';
        for (Iterator<String> strings = presentations.iterator(); strings.hasNext(); ) {
            String term = strings.next();
            if ((prefix == null || term.toLowerCase().startsWith(prefix))
                    && (wordCount < 0 || toWords(term, removeStopWords).length == wordCount))
                if (_displayConcepts)
                    _buffer.append(INDENT + (++ctr) + ") " + code + term + "\n");
        }
        return ctr;
    }

    /**
     * Print code and text for all text presentations associated
     * with the given concept reference.  If specified, the given words
     * are compared against each printed presentation according to the
     * chosen match option.  Match option of 1 indicates to match at
     * least one word; match option of 2 indicates a presentation
     * must match all words.
     * @param ref
     * @param matchWords
     * @param matchOption
     */
    protected int printText(int ctr, ResolvedConceptReference ref, String[] matchWords, int matchOption) {
        // Identify the unique set of text presentations;
        // avoid duplicate registration by multiple sources, etc ...
        Concept concept = ref.getReferencedEntry();
        Set<String> presentations = new HashSet<String>();
        for (int j = 0; j < concept.getPresentationCount(); j++)
            presentations.add(concept.getPresentation(j).getText().getContent());

        // Print matching results
        String code = "[" + ref.getConceptCode() + ']';
        for (Iterator<String> strings = presentations.iterator(); strings.hasNext(); ) {
            String term = strings.next();
            final List<String> words = Arrays.asList(matchWords);
            float weight = getTextWeight(term, words);
            if (matchOption == 1 && weight > 0 || matchOption == 2 && weight == 1)
                if (_displayConcepts)
                    _buffer.append(INDENT + (++ctr) + ") " + code + term + "\n");
        }
        return ctr;
    }

    /**
     * Returns an array containing the individual white-space delimited words
     * contained by the given string, normalizing to lowercase and optionally
     * removing stop words.
     * @param s
     * @param removeStopWords
     * @return
     */
    protected String[] toWords(String s, boolean removeStopWords) {
        String[] words = s.split("\\s");
        List<String> adjusted = new ArrayList<String>();
        for (int i = 0; i < words.length; i++) {
            if (!removeStopWords || !STOP_WORDS.contains(words[i]))
                adjusted.add(words[i].toLowerCase());
        }
        return adjusted.toArray(new String[adjusted.size()]);
    }

}