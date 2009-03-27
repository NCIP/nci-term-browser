package org.LexGrid.LexBIG.example.remote2;

import gov.nih.nci.evs.browser.utils.SearchUtils;

import org.LexGrid.LexBIG.Impl.test.ConceptHistory;
import org.LexGrid.LexBIG.util.Constants;
import org.LexGrid.LexBIG.util.Prompt;

public class Main2 implements Constants {
    public static void main(String[] args) {
        new Main2();
    }
    
    public Main2() {
        int i = 1;
        while (true) {
            println(SEPARATOR2);
            println("Main Menu:");
            println(INDENT + "1) " + "FindRelatedCodes2");
            println(INDENT + "2) " + "ListHierarchy2");
            println(INDENT + "3) " + "ListHierarchyPathToRoot2");
            println(INDENT + "4) " + "MetaMatch2");
            println(INDENT + "5) " + "SearchUtils") ;
            println(INDENT + "6) " + "BuildTreeForCode2") ;
            println(INDENT + "7) " + "ConceptHistory") ;
            println(INDENT + "0) " + "Quit");
            println(SEPARATOR2);
            i = Prompt.prompt("Choose", i);
            if (i == 0)
                break;
            try {
                switch (i) {
                case 1: new FindRelatedCodes2().run(); break;
                case 2: new ListHierarchy2().run(); break;
                case 3: new ListHierarchyPathToRoot2().run(); break;
                case 4: new MetaMatch2().run(); break;
                case 5: SearchUtils.main(new String[] { null }); break;
                case 6: BuildTreeForCode2.run(); break;
                case 7: new ConceptHistory().run(); break;
                default: println("Invalid choice.  Try again."); break;
                }
                println("");
            } catch (Exception e) {
            }
        }
        println("Quit");
    }
    
    private void println(String text) {
        System.out.println(text);
    }
}
