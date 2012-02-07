package gov.nih.nci.evs.browser.utils;

import java.util.List;

import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.apache.log4j.Logger;
import org.lexevs.tree.evstree.EvsTreeConverterFactory;
import org.lexevs.tree.model.LexEvsTree;
import org.lexevs.tree.model.LexEvsTreeNode;
import org.lexevs.tree.service.TreeService;
import org.lexevs.tree.service.TreeServiceFactory;

public class LexEVSTreeTest {
    private static Logger _logger = Logger.getLogger(LexEVSTreeTest.class);
    
    private static void test(String codingScheme, String version, String code) {
        try {
            _logger.debug("");
            _logger.debug(Utils.SEPARATOR);
            _logger.debug("Note: Please ignore the following debug/exception.");
            _logger.debug("Method: LexEVSTreeTest.test:");
            _logger.debug("  * codingScheme: " + codingScheme);
            _logger.debug("  * version: " + version);
            _logger.debug("  * code: " + code);
            
            CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
            if (version != null)
                versionOrTag.setVersion(version);
    
            TreeService treeService =
                TreeServiceFactory.getInstance().getTreeService(
                    RemoteServerUtil.createLexBIGService());
    
            LexEvsTree tree = treeService.getTree(codingScheme, versionOrTag, code);
            _logger.debug("tree.getCurrentFocus().getCode(): " + tree.getCurrentFocus().getCode());
            
            List<LexEvsTreeNode> listEvsTreeNode =
                EvsTreeConverterFactory.getEvsTreeConverter()
                    .buildEvsTreePathFromRootTree(tree.getCurrentFocus());
    
            for (int i = 0; i < listEvsTreeNode.size(); i++) {
                LexEvsTreeNode root = listEvsTreeNode.get(i);
                _logger.debug("Root: " + root.getCode() + " "
                    + root.getEntityDescription());
            }
            _logger.debug("Note: Completed");
        } catch (Exception e) {
            e.printStackTrace();
            _logger.debug("Note: Completed with errors.");
        }
    }
    
    public static void test() {
        test("ChEBI", "March2011", "CHEBI:33582");
        test("NCI Thesaurus", "11.09d", "C37927");
    }
}