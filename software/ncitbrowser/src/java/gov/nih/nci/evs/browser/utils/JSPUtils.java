package gov.nih.nci.evs.browser.utils;

import javax.servlet.http.*;

import org.apache.log4j.*;

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

public class JSPUtils {
    private static Logger _logger = Logger.getLogger(JSPUtils.class);
    
    public static boolean isNull(String text) {
        return text == null || text.equalsIgnoreCase("null");
    }

    public static class JSPHeaderInfo {
        public String hdr_dictionary;
        public String hdr_version;
        public String hdr_version_deprecated;
        public String content_hdr_shortName;
        public String content_hdr_formalName;
        public String display_name;
        public String term_browser_version;
    }

    public static JSPHeaderInfo getJSPHeaderInfo(HttpServletRequest request) {
        JSPHeaderInfo info = new JSPHeaderInfo();

        info.hdr_dictionary =
            (String) request.getSession().getAttribute("dictionary");
        info.hdr_version = (String) request.getParameter("version");
        if (info.hdr_version == null
            || info.hdr_version.equalsIgnoreCase("null"))
            info.hdr_version = (String) request.getAttribute("version");

//        if (info.hdr_dictionary == null
//            || info.hdr_dictionary.compareTo("NCI Thesaurus") == 0) {
//            return info;
//        }

        request.getSession().setAttribute("dictionary", info.hdr_dictionary);
        info.content_hdr_shortName =
            DataUtils.getLocalName(info.hdr_dictionary);
        info.content_hdr_formalName =
            DataUtils.getFormalName(info.content_hdr_shortName);

        if (!DataUtils.isCodingSchemeLoaded(info.content_hdr_formalName,
            info.hdr_version)) {
            info.hdr_version_deprecated = info.hdr_version;
            info.hdr_version =
                DataUtils.getVocabularyVersionByTag(info.hdr_dictionary,
                    "PRODUCTION");
            _logger.debug(Utils.SEPARATOR);
            _logger.debug("hdr_dictionary: " + info.hdr_dictionary);
            _logger.debug("  * hdr_version: " + info.hdr_version);
            _logger.debug("  * hdr_version_deprecated: " + info.hdr_version_deprecated);
        }
        info.display_name =
            DataUtils.getMetadataValue(info.content_hdr_formalName,
                info.hdr_version, "display_name");
        info.term_browser_version =
            DataUtils.getMetadataValue(info.content_hdr_formalName,
                info.hdr_version, "term_browser_version");

        if (isNull(info.display_name))
            info.display_name = info.content_hdr_shortName;

        if (isNull(info.term_browser_version)) {
            info.term_browser_version =
                (String) request.getParameter("version");
            if (isNull(info.term_browser_version))
                info.term_browser_version =
                    (String) request.getAttribute("version");
        }
        return info;
    }

    public static class SimpleJSPHeaderInfo {
        public String dictionary;
        public String version;
        public String version_deprecated;
    }

    public static SimpleJSPHeaderInfo getSimpleJSPHeaderInfo(
        HttpServletRequest request) {
        SimpleJSPHeaderInfo info = new SimpleJSPHeaderInfo();

        info.dictionary = request.getParameter("dictionary");
        info.version = request.getParameter("version");

        if (isNull(info.version))
            info.version = (String) request.getAttribute("version");
        
        if (!DataUtils.isCodingSchemeLoaded(info.dictionary,
            info.version)) {
            info.version_deprecated = info.version;
            info.version =
                DataUtils.getVocabularyVersionByTag(info.dictionary,
                    "PRODUCTION");
            _logger.debug(Utils.SEPARATOR);
            _logger.debug("dictionary: " + info.dictionary);
            _logger.debug("  * version: " + info.version);
            _logger.debug("  * version_deprecated: " + info.version_deprecated);
        }
        return info;
    }
}
