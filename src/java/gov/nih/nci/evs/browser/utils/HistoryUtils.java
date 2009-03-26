package gov.nih.nci.evs.browser.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.LexGrid.LexBIG.DataModel.Collections.NCIChangeEventList;
import org.LexGrid.LexBIG.DataModel.NCIHistory.NCIChangeEvent;
import org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType;
import org.LexGrid.LexBIG.Exceptions.LBException;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;

public class HistoryUtils {
    private final String CODING_SCHEME = "NCI Thesaurus";
    private DateFormat _dataFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    public NCIChangeEventList getEditActions(String code) throws LBException {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        HistoryService hs = lbSvc.getHistoryService(CODING_SCHEME);
        
        NCIChangeEventList list = hs.getEditActionList(Constructors
            .createConceptReference(code, null), null, null);
        debug(list);
        return list;
    }
    
    private void debug(NCIChangeEventList list) {
        Enumeration<NCIChangeEvent> enumeration = list.enumerateEntry();
        int i = 0;
        while (enumeration.hasMoreElements()) {
            NCIChangeEvent event = enumeration.nextElement();
            ChangeType type = event.getEditaction();
            Date date = event.getEditDate();
            String refCode = event.getReferencecode();
            String refName = event.getReferencename();
            String cCode = event.getConceptcode();
            String cName = event.getConceptName();

            System.out.println(Integer.toString(++i) + ") " + 
                type.toString() + 
                ", " + _dataFormatter.format(date) +
                ", refCode=" + refCode +
                ", refName=" + refName +
                ", cCode=" + cCode +
                ", cName=" + cName);
        }
    }
}
