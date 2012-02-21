package gov.nih.nci.evs.browser.test.valueset;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.test.utils.*;
import gov.nih.nci.evs.browser.utils.*;

import java.net.*;
import java.util.*;

import org.LexGrid.LexBIG.Exceptions.*;
import org.LexGrid.LexBIG.Impl.*;
import org.LexGrid.LexBIG.Utility.LBConstants.*;
import org.lexgrid.valuesets.*;
import org.lexgrid.valuesets.dto.*;

public class Bar {
    public static void main(String args[]) throws Exception {
        args = SetupEnv.getInstance().parse(args);
        String evsServiceUrl =
            NCItBrowserProperties
                .getProperty(NCItBrowserProperties.EVS_SERVICE_URL);
        System.out.println("EVS_SERVICE_URL: " + evsServiceUrl);
        System.out.println("");
        System.out.println(Utils.SEPARATOR);
        new Bar().testGetValueSets();
    }

    public void testGetValueSets(){
        System.out.println("Before VSDS init");
         //LexEVSValueSetDefinitionServices vsdServ = LexEVSServiceHolder.instance().getLexEVSAppService().getLexEVSValueSetDefinitionServices();
         LexEVSValueSetDefinitionServices vsdServ = RemoteServerUtil.getLexEVSValueSetDefinitionServices();
          CodedNodeSetImpl nodeSet = null;
         Date start = new Date();       
         try {
             List<String> vsdDefURIs = vsdServ.listValueSetDefinitionURIs();
             for(String s: vsdDefURIs){
                 System.out.println(s);
            Date vsStart = new Date();
              ResolvedValueSetCodedNodeSet nodes = vsdServ.getValueSetDefinitionEntitiesForTerm("cell", MatchAlgorithms.LuceneQuery.name(), new URI(s), null, null);
            Date vsStop = new Date();
            long vsTime = vsStop.getTime() - vsStart.getTime();
            System.out.println("Value Set resolve time: " + (vsTime < 1000? vsTime + " milliseconds": vsTime / 1000 + " seconds"));
              if(nodes != null){
           if(nodeSet != null){
        //  nodeSet = (CodedNodeSetImpl) nodeSet.union(nodes.getCodedNodeSet());
           }
           else{
        //     nodeSet = (CodedNodeSetImpl) nodes.getCodedNodeSet();
           }
              }

             }
        //   nodeSet.resolve(null,null,null);
        } catch (LBException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
         Date stop = new Date();
         long ta = start.getTime();
         long tb = stop.getTime();
         
         long diff = tb - ta;
         System.out.println("elapsed time: " + (diff < 1000? diff + " milliseconds": diff / 1000 + " seconds"));
    }
}
