/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.text.*;
import java.util.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.NCIHistory.*;
import org.LexGrid.LexBIG.DataModel.NCIHistory.types.*;
import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.History.*;
import org.LexGrid.LexBIG.LexBIGService.*;
import org.LexGrid.LexBIG.Utility.*;
import org.LexGrid.concepts.*;
import org.apache.log4j.*;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 *
 */

public class HistoryUtils {
    private static Logger _logger = Logger.getLogger(HistoryUtils.class);
    private static DateFormat _dataFormatter = new SimpleDateFormat("yyyy-MM-dd");

    public static Vector<String> getTableHeader() {
        Vector<String> v = new Vector<String>();
        v.add("Edit Actions");
        v.add("Date");
        v.add("Reference Concept");
        return v;
    }

    public static boolean isHistoryServiceAvailable(String codingSchemeName) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
            if (hs != null)
                return true;
        } catch (Exception ex) {
            // _logger.error("Unable to getHistoryService for " +
            // codingSchemeName);
        }
        return false;
    }

    /*
     * public static Vector<String> getEditActions(String codingSchemeName,
     * String vers, String ltag, String code) throws LBException { LexBIGService
     * lbSvc = RemoteServerUtil.createLexBIGService(); //HistoryService hs =
     * lbSvc.getHistoryService(CODING_SCHEME_NAME); HistoryService hs =
     * lbSvc.getHistoryService(codingSchemeName);
     *
     * try { NCIChangeEventList list = hs.getEditActionList(Constructors
     * .createConceptReference(code, null), null, null); return
     * getEditActions(codingSchemeName, vers, ltag, code, list); } catch
     * (Exception ex) { ex.printStackTrace(); } return null; }
     */

    public static Vector<String> getEditActions(String codingSchemeName,
        String vers, String ltag, String code) throws LBException {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = lbSvc.getHistoryService(codingSchemeName);
        return getEditActions(lbSvc, hs, codingSchemeName, vers, ltag, code);
    }

    // [#23370] NCIt concept history shows wrong Code (KLO, 09/28/09)
    private static Vector<String> getEditActions(LexBIGService lbSvc,
        HistoryService hs, String codingSchemeName, String vers, String ltag,
        String code) throws LBException {
        try {
            Entity c =
                DataUtils.getConceptByCode(codingSchemeName, vers, ltag, code);
            if (c == null)
                return null;
            NCIChangeEventList list =
                hs.getEditActionList(Constructors.createConceptReference(code,
                    null), null, null);
            /*
             * Boolean isActive = c.isIsActive(); NCIChangeEventList list =
             * null; if (isActive != null && isActive.equals(Boolean.FALSE)) {
             * list =
             * hs.getDescendants(Constructors.createConceptReference(code,
             * null)); } else { list =
             * hs.getEditActionList(Constructors.createConceptReference(code,
             * null), null, null); }
             */
            return getEditActions(codingSchemeName, vers, ltag, code, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Vector<String> getEditActions(String codingSchemeName,
        String vers, String ltag, String code, NCIChangeEventList list) {
		LexBIGService lbSvc = new RemoteServerUtil().createLexBIGService();

        java.util.Enumeration<? extends NCIChangeEvent> enumeration = list.enumerateEntry();

        Vector<String> v = new Vector<String>();
        HashSet<String> hset = new HashSet<String>();

        DateFormat dataFormatter = new SimpleDateFormat("yyyy-MM-dd");

        while (enumeration.hasMoreElements()) {
            NCIChangeEvent event = (NCIChangeEvent) enumeration.nextElement();

            event =
                convertNCIChangeEvent(lbSvc, codingSchemeName, vers, ltag,
                    code, event);

            ChangeType type = event.getEditaction();
            Date date = event.getEditDate();
            String rCode = event.getReferencecode();
            String desc = "N/A";
            if (rCode != null && rCode.length() > 0
                && !rCode.equalsIgnoreCase("null")) {
                Entity c =
                    DataUtils.getConceptByCode(codingSchemeName, vers, ltag,
                        rCode);
                // KLO
                if (c != null) {
                    String name = c.getEntityDescription().getContent();
                    desc = name + " (Code " + rCode + ")";
                } else {
                    desc = rCode;
                }
            }

            //String info = type + "|" + _dataFormatter.format(date) + "|" + desc;
            String info = type + "|" + dataFormatter.format(date) + "|" + desc;

            if (hset.contains(info))
                continue;
            v.add(info);
            hset.add(info);
        }
        return v;
    }

    public static Vector<String> getAncestors(String codingSchemeName,
        String vers, String ltag, String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
        } catch (Exception ex) {
            _logger
                .error("Unable to getHistoryService for " + codingSchemeName);
            return null;
        }

        try {
            NCIChangeEventList list =
                hs
                    .getAncestors(Constructors.createConceptReference(code,
                        null));

            return getEditActions(codingSchemeName, vers, ltag, code, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getDescendants(String codingSchemeName,
        String vers, String ltag, String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
        } catch (Exception ex) {
            _logger
                .error("Unable to getHistoryService for " + codingSchemeName);
            return null;
        }

        try {
            NCIChangeEventList list =
                hs.getDescendants(Constructors.createConceptReference(code,
                    null));

            return getEditActions(codingSchemeName, vers, ltag, code, list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Vector<String> getDescendantCodes(String codingSchemeName,
        String vers, String ltag, String code) {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = null;
        try {
            hs = lbSvc.getHistoryService(codingSchemeName);
        } catch (Exception ex) {
            _logger
                .error("Unable to getHistoryService for " + codingSchemeName);
            return null;
        }

        Vector<String> v = new Vector<String>();
        try {
            NCIChangeEventList list =
                hs.getDescendants(Constructors.createConceptReference(code,
                    null));

            HashSet<String> hset = new HashSet<String>();
            Enumeration<? extends NCIChangeEvent> enumeration = list.enumerateEntry();
            while (enumeration.hasMoreElements()) {
                NCIChangeEvent event = (NCIChangeEvent) enumeration.nextElement();
                ChangeType type = event.getEditaction();
                Date date = event.getEditDate();
                String rCode = event.getReferencecode();
                String name = "unassigned";
                if (rCode != null && rCode.length() > 0
                    && !rCode.equalsIgnoreCase("null")) {
                    Entity c =
                        DataUtils.getConceptByCode(codingSchemeName, vers,
                            ltag, rCode);

                    if (c != null) {
                        name = c.getEntityDescription().getContent();
                    }
                }
                String info = name + "|" + rCode;
                if (hset.contains(info))
                    continue;
                v.add(info);
                hset.add(info);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
        return v;
    }

    /*
     * C21344|Growth_Factor_Gene|merge|12-AUG-03|C21344|Growth_Factor_Gene
     * C18438
     * |Proto-Oncogene_Growth_Factor|merge|12-AUG-03|C21344|Growth_Factor_Gene
     * C18438|Proto-Oncogene_Growth_Factor|retire|12-AUG-03|(null)|(null)
     */

    private static NCIChangeEvent convertNCIChangeEvent(LexBIGService lbSvc,
        String codingSchemeName, String vers, String ltag, String code,
        NCIChangeEvent event) {
        if (event == null)
            return null;
        ChangeType type = event.getEditaction();
        String type_str = type.toString();
        if (type_str.compareTo("merge") == 0) {
            Date date = event.getEditDate();
            String rCode = event.getReferencecode();
            if (rCode.compareTo(code) == 0) {

                _logger.debug("rCode." + rCode + " == code == " + code);

                try {
                    HistoryService hs =
                        lbSvc.getHistoryService(codingSchemeName);
                    if (hs == null) {
                        _logger.warn("Unable to getHistoryService for "
                            + codingSchemeName);
                        return null;
                    }
                    try {
                        _logger.debug("\tcheck Ancestors");

                        NCIChangeEventList list =
                            hs.getAncestors(Constructors
                                .createConceptReference(code, null));
                        Enumeration<? extends NCIChangeEvent> enumeration =
                            list.enumerateEntry();
                        //Vector<String> v = new Vector<String>();
                        //HashSet<String> hset = new HashSet<String>();
                        while (enumeration.hasMoreElements()) {
                            NCIChangeEvent event2 = (NCIChangeEvent) enumeration.nextElement();
                            String con_code = event2.getConceptcode();
                            String ref_code = event2.getReferencecode();

                            _logger.debug("\tAncestor -- con_code " + con_code
                                + "; ref_code == " + ref_code);

                            Date date2 = event2.getEditDate();
                            ChangeType type2 = event2.getEditaction();
                            String type_str2 = type2.toString();
                            if (type_str.compareTo("merge") == 0
                                && ref_code.compareTo(con_code) != 0
                                && ref_code.compareTo(code) == 0
                                && date.toString().compareTo(date2.toString()) == 0) {
                                // _logger.debug("(***) con_code: " + con_code +
                                // " ref_code: " + ref_code);

                                _logger.debug("\tsubstituting...");
                                event2.setConceptcode(ref_code);
                                event2.setReferencecode(con_code);
                                return event2;
                            }
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        _logger.error("getAncestors throws exception.");
                    }

                    try {
                        _logger.debug("\tcheck Descendants");

                        NCIChangeEventList list =
                            hs.getDescendants(Constructors
                                .createConceptReference(code, null));
                        Enumeration<? extends NCIChangeEvent> enumeration =
                            list.enumerateEntry();
                        //Vector<String> v = new Vector<String>();
                        //HashSet<String> hset = new HashSet<String>();
                        while (enumeration.hasMoreElements()) {
                            NCIChangeEvent event2 = (NCIChangeEvent) enumeration.nextElement();
                            String con_code = event2.getConceptcode();
                            String ref_code = event2.getReferencecode();

                            _logger.debug("\tDescendant con_code " + con_code
                                + "; ref_code == " + ref_code);

                            Date date2 = event2.getEditDate();
                            ChangeType type2 = event2.getEditaction();
                            String type_str2 = type2.toString();
                            if (type_str.compareTo("merge") == 0
                                && ref_code.compareTo(con_code) != 0
                                && ref_code.compareTo(code) == 0
                                && date.toString().compareTo(date2.toString()) == 0) {
                                // _logger.debug("(***) con_code: " + con_code +
                                // " ref_code: " + ref_code);

                                _logger.debug("\tsubstituting...");
                                event2.setConceptcode(ref_code);
                                event2.setReferencecode(con_code);
                                return event2;
                            }
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                        _logger.error("getAncestors throws exception.");
                    }

                } catch (Exception ex) {
                    // ex.printStackTrace();
                    _logger.error("getAncestors throws exception.");
                }
            }

        }

        return event;
    }

}
