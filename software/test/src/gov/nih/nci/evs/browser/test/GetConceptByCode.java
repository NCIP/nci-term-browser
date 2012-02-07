package gov.nih.nci.evs.browser.test;

import org.LexGrid.concepts.Entity;

import gov.nih.nci.evs.browser.test.utils.SetupEnv;
import gov.nih.nci.evs.browser.utils.DataUtils;

public class GetConceptByCode {
    public void search(String codingScheme, String version, String code) {
        System.out.println("-------------------------------------------------");
        System.out.println("Retrieving: " + codingScheme + ", " + version + ", " + code);
        String tag = null;
        Entity entity =
            DataUtils.getConceptByCode(codingScheme, version, tag, code);
        String info = "";
        if (entity != null)
            info =
                entity.getEntityDescription().getContent() + ":"
                    + entity.getEntityCode();
        else info = "[Could not find: " + code + "]";
        System.out.println("Entity: " + info);
    }

    public void search() {
//        search("NCI Thesaurus", "10.08e", "C12434");
//        search("GO", "September2010", "GO:0000003");
//        search("GO", "September2010", "GO:0000075");
//        search("GO", "September2010", "GO:0005102");
//        search("GO", "September2010", "GO:0006281");
//        search("GO_to_NCIt_Mapping", "1.0", "GO:0005102");

//        search("NCI Thesaurus", "10.12c", "C12434");
//        search("NCI Thesaurus", "10.11e", "C12434");
        
        search("NCI Thesaurus", "11.09d", "C12434");
    }

    public static void main(String[] args) {
        args = SetupEnv.getInstance().parse(args);
        new GetConceptByCode().search();
    }
}
