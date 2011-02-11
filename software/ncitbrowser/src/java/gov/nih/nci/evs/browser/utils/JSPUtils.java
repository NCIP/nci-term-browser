package gov.nih.nci.evs.browser.utils;

import javax.servlet.http.*;

public class JSPUtils {
    public static class JSPHeaderInfo {
        public String hdr_dictionary;
        public String hdr_version;
        public String hdr_version_deprecated;
        public String content_hdr_shortName;
        public String content_hdr_formalName;
        public String display_name;
        public String term_browser_version;
    }

    public static boolean isNull(String text) {
        return text == null || text.equalsIgnoreCase("null");
    }

    public static JSPHeaderInfo getJSPHeaderInfo(HttpServletRequest request) {
        JSPHeaderInfo info = new JSPHeaderInfo();

        info.hdr_dictionary =
            (String) request.getSession().getAttribute("dictionary");
        info.hdr_version = (String) request.getParameter("version");
        if (info.hdr_version == null
            || info.hdr_version.equalsIgnoreCase("null"))
            info.hdr_version = (String) request.getAttribute("version");

        if (info.hdr_dictionary == null
            || info.hdr_dictionary.compareTo("NCI Thesaurus") == 0) {
            return info;
        }

        request.getSession().setAttribute("dictionary", info.hdr_dictionary);
        info.content_hdr_shortName =
            DataUtils.getLocalName(info.hdr_dictionary);
        info.content_hdr_formalName =
            DataUtils.getFormalName(info.content_hdr_shortName);
        info.display_name =
            DataUtils.getMetadataValue(info.content_hdr_formalName,
                info.hdr_version, "display_name");
        info.term_browser_version =
            DataUtils.getMetadataValue(info.content_hdr_formalName,
                info.hdr_version, "term_browser_version");

        if (isNull(info.display_name) && isNull(info.term_browser_version)) {
            info.hdr_version_deprecated = info.hdr_version;
            info.hdr_version = DataUtils.getVocabularyVersionByTag(
                info.hdr_dictionary, "PRODUCTION");
            info.display_name =
                DataUtils.getMetadataValue(info.content_hdr_formalName,
                    info.hdr_version, "display_name");
            info.term_browser_version =
                DataUtils.getMetadataValue(info.content_hdr_formalName,
                    info.hdr_version, "term_browser_version");
        }

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
}
