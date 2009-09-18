package gov.nih.nci.evs.browser.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Vector;

import org.LexGrid.LexBIG.DataModel.Collections.NCIChangeEventList;
import org.LexGrid.LexBIG.DataModel.NCIHistory.NCIChangeEvent;
import org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.concepts.Concept;

import static gov.nih.nci.evs.browser.common.Constants.*;

public class HistoryUtils {

	private static DateFormat _dataFormatter = new SimpleDateFormat(
			"yyyy-MM-dd");

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
			if (hs != null) return true;
		} catch (Exception ex) {
			System.out.println("Unable to getHistoryService for " + codingSchemeName);
		}
		return false;
	}

	public static Vector<String> getEditActions(String codingSchemeName,
			String vers, String ltag, String code) throws LBException {
		LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
		//HistoryService hs = lbSvc.getHistoryService(CODING_SCHEME_NAME);
		HistoryService hs = lbSvc.getHistoryService(codingSchemeName);

		try {
			NCIChangeEventList list = hs.getEditActionList(Constructors
					.createConceptReference(code, null), null, null);
			return getEditActions(codingSchemeName, vers, ltag, code, list);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}


    private static Vector<String> getEditActions(LexBIGService lbSvc, HistoryService hs, String codingSchemeName,
            String vers, String ltag, String code) throws LBException {
        try {
			NCIChangeEventList list = hs.getEditActionList(Constructors
				.createConceptReference(code, null), null, null);
			return getEditActions(codingSchemeName, vers, ltag, code, list);
	    } catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
    }


	private static Vector<String> getEditActions(String codingSchemeName,
			String vers, String ltag, String code, NCIChangeEventList list) {
		Enumeration<NCIChangeEvent> enumeration = list.enumerateEntry();
		Vector<String> v = new Vector<String>();
		HashSet<String> hset = new HashSet<String>();
		while (enumeration.hasMoreElements()) {
			NCIChangeEvent event = enumeration.nextElement();
			ChangeType type = event.getEditaction();
			Date date = event.getEditDate();
			String rCode = event.getReferencecode();
			String desc = "N/A";
			if (rCode != null && rCode.length() > 0
					&& !rCode.equalsIgnoreCase("null")) {
				Concept c = DataUtils.getConceptByCode(codingSchemeName, vers,
						ltag, rCode);
				//KLO
				if (c != null) {
					String name = c.getEntityDescription().getContent();
					desc = name + " (Code " + rCode + ")";
				} else {
					desc = rCode;
				}
			}

			String info = type + "|" + _dataFormatter.format(date) + "|" + desc;
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
			System.out.println("Unable to getHistoryService for " + codingSchemeName);
			return null;
		}

        try {
 			NCIChangeEventList list = hs.getAncestors(Constructors
                     .createConceptReference(code, null));

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
			System.out.println("Unable to getHistoryService for " + codingSchemeName);
			return null;
		}

        try {
 			NCIChangeEventList list = hs.getDescendants(Constructors
                     .createConceptReference(code, null));

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
			System.out.println("Unable to getHistoryService for " + codingSchemeName);
			return null;
		}

        Vector<String> v = new Vector<String>();
        try {
 			NCIChangeEventList list = hs.getDescendants(Constructors
                     .createConceptReference(code, null));

			HashSet<String> hset = new HashSet<String>();
			Enumeration<NCIChangeEvent> enumeration = list.enumerateEntry();
			while (enumeration.hasMoreElements()) {
				NCIChangeEvent event = enumeration.nextElement();
				ChangeType type = event.getEditaction();
				Date date = event.getEditDate();
				String rCode = event.getReferencecode();
				String name = "unassigned";
				if (rCode != null && rCode.length() > 0
						&& !rCode.equalsIgnoreCase("null")) {
					Concept c = DataUtils.getConceptByCode(codingSchemeName, vers,
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

}
