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
        debug("EVS_SERVICE_URL: " + evsServiceUrl);
        debug("");
        debug(Utils.SEPARATOR);
        new Bar().testGetValueSets();
    }

    public void testGetValueSets() {
        debug("Before VSDS init");
        LexEVSValueSetDefinitionServices vsdServ =
            RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        CodedNodeSetImpl nodeSet = null;
        Date start = new Date();
        try {
            List<String> vsdDefURIs = vsdServ.listValueSetDefinitionURIs();
            int i = 0;
            for (String uri : vsdDefURIs) {
                Date vsStart = new Date();
                ResolvedValueSetCodedNodeSet nodes =
                    vsdServ.getValueSetDefinitionEntitiesForTerm("cell",
                        MatchAlgorithms.LuceneQuery.name(), new URI(uri), null,
                        null);
                Date vsStop = new Date();
                long vsTime = vsStop.getTime() - vsStart.getTime();
                String vsd_name = DataUtils.valueSetDefiniionURI2Name(uri);
                debug(String.format("%5s", i++ + ")") 
                    + " " + String.format("%-30s", uri)
                    + " " + String.format("%-90s", vsd_name)
                    + " Resolve time: " + formatTiming(vsTime));
                if (nodes != null) {
                    if (nodeSet != null) {
                        // nodeSet = (CodedNodeSetImpl)
                        // nodeSet.union(nodes.getCodedNodeSet());
                    } else {
                        // nodeSet = (CodedNodeSetImpl) nodes.getCodedNodeSet();
                    }
                }

            }
            // nodeSet.resolve(null,null,null);
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
        debug("elapsed time: " + timing(diff));
    }

    private static void debug(String text) {
        System.out.println(text);
    }
    
    private static String formatTiming(long duration) {
        if (duration < 1000)
            return String.format("%5s", duration) + " msec";
        else return String.format("%5s", duration/1000) + " sec ";
    }
    
    private static String timing(long duration) {
        return (duration < 1000 ? duration + " msecs" : duration / 1000 + " seconds");
    }
}
