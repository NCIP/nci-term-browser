package org.LexGrid.LexBIG.Impl.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

import org.LexGrid.LexBIG.DataModel.Collections.NCIChangeEventList;
import org.LexGrid.LexBIG.DataModel.NCIHistory.NCIChangeEvent;
import org.LexGrid.LexBIG.DataModel.NCIHistory.types.ChangeType;
import org.LexGrid.LexBIG.History.HistoryService;
import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.Utility.Constructors;
import org.LexGrid.LexBIG.util.AppService;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;
import org.LexGrid.LexBIG.util.Util;

public class ConceptHistory implements Constants {
    private HistoryService _hs = null;
    private String _code = "C12799";
    private DateFormat _dataFormatter = 
        new SimpleDateFormat("yyyy-MM-dd");

    public ConceptHistory() throws Exception {
        AppService service = AppService.getInstance();
        LexBIGService lbs = AppService.getLBSvc();
        String codingScheme = service.getScheme();
        _hs = lbs.getHistoryService(codingScheme);
    }

    public static void main(String[] args) throws Exception {
        ConceptHistory history = new ConceptHistory();
        history.run2();
    }

    public void run() throws Exception {
        while (true) {
            _code = Prompt.prompt("Code (q to Quit)", _code).toUpperCase();
            if (_code.equalsIgnoreCase("q"))
                break;
            getEditActions(_code);
            println("");
            println(SEPARATOR);
        }
    }
    
    public void run2() throws Exception {
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
            getEditActions(codes[i]);
        }
        Util.displayMessage("Done");
    }
    
    private void println(String text) {
        System.out.println(text);
    }
    
    private void print(String text) {
        System.out.print(text);
    }

    public void getEditActions(String code) throws Exception {
//        println(ObjectToString.toString(_hs.getEditActionList(Constructors
//            .createConceptReference(code, null), null, null)));

        Date beginDate = null;
        Date endDate = null;
       
        NCIChangeEventList list = _hs.getEditActionList(Constructors
            .createConceptReference(code, null), beginDate, endDate);
        Enumeration<NCIChangeEvent> enumeration = list.enumerateEntry();
        int i=0;
        while (enumeration.hasMoreElements()) {
            NCIChangeEvent event = enumeration.nextElement();
            ChangeType type = event.getEditaction();
            print(Integer.toString(++i) + ") ");
            print(type.toString());
            
            Date date = event.getEditDate();
            print(", " + _dataFormatter.format(date));
            
            String refCode = event.getReferencecode();
            String refName = event.getReferencename();
            print(", refCode=" + refCode);
            print(", refName=" + refName);
            
            String cCode = event.getConceptcode();
            String cName = event.getConceptName();
            print(", cCode=" + cCode);
            print(", cName=" + cName);
            
            println("");
        }
    }
}
