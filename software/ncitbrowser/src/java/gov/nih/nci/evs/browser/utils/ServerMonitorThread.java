package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.apache.log4j.Logger;

public class ServerMonitorThread extends Thread {
    private static ServerMonitorThread _instance = null;
    private Boolean _isThreadRunning = false;
	private boolean _isLexEVSRunning = true;
	private long _interval = 1000 * 
	    NCItBrowserProperties.getIntProperty(
	        NCItBrowserProperties.PING_LEXEVS_INTERVAL, 600);
	private static Logger _logger = Logger.getLogger(ServerMonitorThread.class);
	
	private ServerMonitorThread() {
	    _interval = 1000; //DYEE
	}
	
	public static ServerMonitorThread getInstance() {
	    if (_instance == null)
	        _instance = new ServerMonitorThread();
	    return _instance;
	}
	
	public void run() {
	    synchronized(_isThreadRunning) {
    	    if (_isThreadRunning)
    	        return;
    	    _isThreadRunning = true;
	    }
	    
		while (true) {
			try {
				monitor();
				sleep(_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void monitor() {
        LexBIGService lbSvc = RemoteServerUtil.createLexBIGService();
        _isLexEVSRunning = lbSvc != null;
        _logger.debug("_isLexEVSRunning: " + _isLexEVSRunning);
	}
	
	public boolean isLexEVSRunning() {
	    return _isLexEVSRunning;
	}
	
	public boolean isServerRunning() {
	    return getInstance().isLexEVSRunning();
	}
}
