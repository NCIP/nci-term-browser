package gov.nih.nci.evs.browser.utils;

import java.text.DecimalFormat;

public class Utils {
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
        
        public String getResult() {
            long time = duration();
            double timeSec = time/1000.0;
            double timeMin = timeSec/60.0;
            
            return "Run time: " + time + " ms, " + 
                _doubleFormatter.format(timeSec) + " sec, " + 
                _doubleFormatter.format(timeMin) + " min";
        }
    }
}