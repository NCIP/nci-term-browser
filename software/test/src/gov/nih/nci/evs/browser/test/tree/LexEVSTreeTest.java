package gov.nih.nci.evs.browser.test.tree;

//package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.test.utils.SetupEnv;
import gov.nih.nci.evs.browser.utils.RemoteServerUtil;

import java.util.List;
import java.util.HashSet;


import org.LexGrid.LexBIG.DataModel.Core.CodingSchemeVersionOrTag;
import org.lexevs.tree.evstree.EvsTreeConverterFactory;
import org.lexevs.tree.model.LexEvsTree;
import org.lexevs.tree.model.LexEvsTreeNode;
import org.lexevs.tree.service.TreeService;
import org.lexevs.tree.service.TreeServiceFactory;

import org.lexevs.tree.dao.iterator.ChildTreeNodeIterator;



public class LexEVSTreeTest {


    public static void printNode(LexEvsTreeNode node) {
		System.out.println(node.getEntityDescription() + "(" + node.getCode() + ")");
		LexEvsTreeNode.ExpandableStatus node_status = node.getExpandableStatus();
		if (node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
			System.out.println("\t" + node.getCode() + " " + "expandable");

			ChildTreeNodeIterator itr = node.getChildIterator();
			HashSet hset = new HashSet();

			while(itr.hasNext()){
				LexEvsTreeNode child = itr.next();
				String child_code = child.getCode();
				if (!hset.contains(child_code)) {
					hset.add(child_code);
                    printNode(child);
				} else {
					break;
				}
		    }

		} else {
			System.out.println("\t" + node.getCode() + " " + "not expandable");
		}


	}



    public static void test() {
        String codingScheme, version, code;

//        codingScheme = "ChEBI";
//        version = "March2011";
//        code = "CHEBI:33582";

        codingScheme = "NCI Thesaurus";
        version = "11.09d";
        code = "C37927";

        CodingSchemeVersionOrTag versionOrTag = new CodingSchemeVersionOrTag();
        if (version != null)
            versionOrTag.setVersion(version);

        TreeService treeService =
            TreeServiceFactory.getInstance().getTreeService(
                RemoteServerUtil.createLexBIGService());

        LexEvsTree tree = treeService.getTree(codingScheme, versionOrTag, code);


        LexEvsTreeNode top_node = tree.findNodeInTree("@@");
		LexEvsTreeNode.ExpandableStatus top_node_status = top_node.getExpandableStatus();

		if (top_node_status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
			System.out.println("\t" + top_node.getCode() + " " + "expandable");

			ChildTreeNodeIterator itr = top_node.getChildIterator();
			HashSet hset = new HashSet();


			while(itr.hasNext()){
				LexEvsTreeNode child = itr.next();

				String child_code = child.getCode();

				if (!hset.contains(child_code)) {
					hset.add(child_code);
                    /*
								System.out.println("Root: " + child.getCode() + " "
									+ child.getEntityDescription());
                    */
                    printNode(child);
				} else {
					break;
				}
		    }

		} else {
			System.out.println("\t" + top_node.getCode() + " " + "not expandable");
		}




        System.out.println(tree.getCurrentFocus().getCode());

        /*
        List<LexEvsTreeNode> listEvsTreeNode =
            EvsTreeConverterFactory.getEvsTreeConverter()
                .buildEvsTreePathFromRootTree(tree.getCurrentFocus());

        for (int i = 0; i < listEvsTreeNode.size(); i++) {
            LexEvsTreeNode root = listEvsTreeNode.get(i);
            System.out.println("Root: " + root.getCode() + " "
                + root.getEntityDescription());
        }
        */


        ChildPagingEvsTreeConverter childPagingEvsTreeConverter = new ChildPagingEvsTreeConverter();

        System.out.println("Calling childPagingEvsTreeConverter.buildEvsTreePathFromRootTree...");
        List<LexEvsTreeNode> listEvsTreeNode = childPagingEvsTreeConverter.buildEvsTreePathFromRootTree(tree.getCurrentFocus());
        System.out.println("Exiting childPagingEvsTreeConverter.buildEvsTreePathFromRootTree");


        for (int i = 0; i < listEvsTreeNode.size(); i++) {
            LexEvsTreeNode root = listEvsTreeNode.get(i);
            System.out.println("Root: " + root.getCode() + " "
                + root.getEntityDescription());

            LexEvsTreeNode.ExpandableStatus status = root.getExpandableStatus();

            if (status == LexEvsTreeNode.ExpandableStatus.IS_EXPANDABLE) {
				System.out.println("\texpandable");
			} else {
				System.out.println("\tNot expandable");
			}
        }
    }


    public static void main(String[] args) throws Exception {
        args = SetupEnv.getInstance().parse(args);
		LexEVSTreeTest.test();
	}

}