package gov.nih.nci.evs.browser.test.valueset;

import gov.nih.nci.evs.browser.properties.*;
import gov.nih.nci.evs.browser.test.utils.*;
import gov.nih.nci.evs.browser.utils.*;

import java.net.*;
import java.util.*;

import org.LexGrid.LexBIG.DataModel.Collections.*;
import org.LexGrid.LexBIG.DataModel.Core.*;
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
        debug(Utils.SEPARATOR);
        debug("EVS_SERVICE_URL: " + evsServiceUrl);
        debug("");
        
        new Bar().test();
    }
    
    public void test() {
        List<String> vsdDefURIs = null;
        //vsdDefURIs = getVsdDefUris();
        
        //testGetValueSets("cell", MatchAlgorithms.LuceneQuery.name(), vsdDefURIs);
        testGetValueSets("cell", MatchAlgorithms.contains.name(), vsdDefURIs);
    }
    
    private List<String> getVsdDefUris() {
        List<String> vsdDefURIs = null;
        vsdDefURIs = new ArrayList<String>();
        vsdDefURIs.add("urn://evs.MultiDomainSPL");
        vsdDefURIs.add("urn://evs.MultiDomainSPL_Test2");
        return vsdDefURIs;
    }
    
    private void testGetValueSets(String matchText, String algorithm, List<String> vsdDefURIs) {
        debug(Utils.SEPARATOR);
        debug("* matchText: " + matchText);
        debug("* algorithm: " + algorithm);
        debug("");
        debug(Utils.SEPARATOR);
        LexEVSValueSetDefinitionServices vsdServ =
            RemoteServerUtil.getLexEVSValueSetDefinitionServices();
        CodedNodeSetImpl nodeSet = null;
        
        Date start = new Date();
        try {
            if (vsdDefURIs == null || vsdDefURIs.size() <= 0)
                vsdDefURIs = vsdServ.listValueSetDefinitionURIs();
            int i = 0;
            for (String uri : vsdDefURIs) {
                Date vsStart = new Date();
                String vsd_name = DataUtils.valueSetDefiniionURI2Name(uri);
                
                AbsoluteCodingSchemeVersionReferenceList csVersionList = null;
                csVersionList = getCsVersionList(vsd_name, uri);
                
                ResolvedValueSetCodedNodeSet nodes =
                    vsdServ.getValueSetDefinitionEntitiesForTerm(matchText,
                        algorithm, new URI(uri), csVersionList, null);
                Date vsStop = new Date();
                long vsTime = vsStop.getTime() - vsStart.getTime();
                debug(String.format("%5s", i++ + ")") 
                    + " " + String.format("%-30s", uri)
                    + " " + String.format("%-90s", vsd_name)
                    + " " + format(vsTime));
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

        debug("");
        debug(Utils.SEPARATOR);
        debug("elapsed time: " + format(timing(start, new Date())));
        debug("");
    }

    //--------------------------------------------------------------------------
    private AbsoluteCodingSchemeVersionReferenceList getCsVersionList(String vsd_name, String uri) {
        debug(Utils.SEPARATOR);
        debug("* vsd_name: " + vsd_name);
        
        AbsoluteCodingSchemeVersionReferenceList csVersionList = null;
        
        Date tmpStart = new Date();
        Vector cs_ref_vec = DataUtils.getCodingSchemeReferencesInValueSetDefinition(uri, "PRODUCTION");
        debug("* DataUtils.getCodingSchemeReferencesInValueSetDefinition: " + format(timing(tmpStart, new Date())));
        
        if (cs_ref_vec != null) {
            tmpStart = new Date();
            csVersionList = DataUtils.vector2CodingSchemeVersionReferenceList(cs_ref_vec);
            debug("* DataUtils.vector2CodingSchemeVersionReferenceList: " + format(timing(tmpStart, new Date())));
            debugCsVersionList(csVersionList);
        }
        return csVersionList;
    }
    
    private void debugCsVersionList(AbsoluteCodingSchemeVersionReferenceList csVersionList) {
        Enumeration<AbsoluteCodingSchemeVersionReference> enumeration = 
            (Enumeration<AbsoluteCodingSchemeVersionReference>) 
                csVersionList.enumerateAbsoluteCodingSchemeVersionReference();
        
        while (enumeration.hasMoreElements()) {
            AbsoluteCodingSchemeVersionReference ref = enumeration.nextElement();
            String urn = ref.getCodingSchemeURN();
            String version = ref.getCodingSchemeVersion();
            debug("  * urn=" + urn + ", version=" + version);
        }
    }
    
    //--------------------------------------------------------------------------
    private static void debug(String text) {
        System.out.println(text);
    }
    
    private static final String TIMING_FORMAT = "%6d";
    private static final String TIMING_DECIMAL_FORMAT = "%6.2f";
    private static final float SEC = 1000;
    private static final float MIN = 60 * SEC;
    private static final float HR = 60 * MIN;
    private static String format(long duration) {
        if (duration >= HR)
            return String.format(TIMING_DECIMAL_FORMAT, (float) (duration / HR)) + " hr  ";
        else if (duration >= MIN)
            return String.format(TIMING_DECIMAL_FORMAT, (float) (duration / MIN)) + " min ";
        else if (duration >= SEC)
            return String.format(TIMING_DECIMAL_FORMAT, (float) (duration / SEC)) + " sec ";
        else return String.format(TIMING_FORMAT, duration) + " msec"; 
    }
    
    private static long timing(Date start, Date stop) {
        long ta = start.getTime();
        long tb = stop.getTime();
        long diff = tb - ta;
        return diff;
    }
}
