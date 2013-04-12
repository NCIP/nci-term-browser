/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import java.text.DecimalFormat;

import org.LexGrid.LexBIG.util.Prompt;
import org.apache.log4j.Logger;

public class Util {
    private static Logger _logger = Logger.getLogger(Util.class);
	public static final String SEPARATOR = 
		"----------------------------------------" +
		"----------------------------------------";
    private static DecimalFormat _doubleFormatter = new DecimalFormat("0.00");

    public static class StopWatch {
        private long _startMS = 0;
        
        public StopWatch() {
            start();
        }
    
        public void start() {
            _startMS = System.currentTimeMillis();
        }
        
        public long duration() {
            return System.currentTimeMillis() - _startMS;
        }
        
        public String getResult(long time) {
            double timeSec = time/1000.0;
            double timeMin = timeSec/60.0;
            
            return "Run time: " + time + " ms, " + 
                _doubleFormatter.format(timeSec) + " sec, " + 
                _doubleFormatter.format(timeMin) + " min";
        }

        public String getResult() {
            return getResult(duration());
        }
        
        public String getSecondsString(long time) {
            double timeSec = time/1000.0;
            return _doubleFormatter.format(timeSec);
        }
    }
    
    public static String promptAlgorithm(String algorithm) {
        while (true) {
            algorithm = Prompt.prompt("algorithm", algorithm);
            
            if (algorithm.equalsIgnoreCase("exactMatch") ||
                    algorithm.equalsIgnoreCase("e")) {
                algorithm = "exactMatch";
                break;
            } else if (algorithm.equalsIgnoreCase("startsWith") ||
                    algorithm.equalsIgnoreCase("s")) {
                algorithm = "startsWith";
                break;
            } else if (algorithm.equalsIgnoreCase("contains") ||
                    algorithm.equalsIgnoreCase("c")) {
                algorithm = "contains";
                break;
            }
            _logger.debug("  Valid values: exactMatch, startsWith, contains");
        }
        return algorithm;
    }
}