package gov.nih.nci.evs.browser.test;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.*;

public class UrlDataTest {
    private static Logger _logger = Logger.getLogger(UrlDataTest.class);
    private static final String FTP_URL = "http://evs.nci.nih.gov/ftp1";
    private static final String CDRH_REPORT_URL = FTP_URL
        + "/FDA/CDRH/FDA-CDRH_NCIt_Subsets.txt";

    public Vector<String> readUrlFile(String urlFile) {
        Vector<String> list = new Vector<String>();

        try {
            URL url = new URL(urlFile);
            InputStream is = url.openStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
    
            String line;
            while ((line = br.readLine()) != null)
                list.add(line);
            
            br.close();
            isr.close();
            is.close();
        } catch (Exception e) {
            list.add(e.getMessage());
        }
        return list;
    }

    public static void main(String[] args) {
        String url = CDRH_REPORT_URL;
        Vector<String> list = new UrlDataTest().readUrlFile(url);
        Iterator<String> iterator = list.iterator();
        int i=0;
        _logger.debug("Url: " + url);
        while (iterator.hasNext()) {
            _logger.debug((i++) + ") " + iterator.next());
        }
    }
}
