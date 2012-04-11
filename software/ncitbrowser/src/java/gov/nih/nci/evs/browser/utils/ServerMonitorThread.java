package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;

import java.util.Date;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.apache.log4j.Logger;

/**
 * <!-- LICENSE_TEXT_START -->
 * Copyright 2008,2009 NGIT. This software was developed in conjunction
 * with the National Cancer Institute, and so to the extent government
 * employees are co-authors, any rights in such works shall be subject
 * to Title 17 of the United States Code, section 105.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *   1. Redistributions of source code must retain the above copyright
 *      notice, this list of conditions and the disclaimer of Article 3,
 *      below. Redistributions in binary form must reproduce the above
 *      copyright notice, this list of conditions and the following
 *      disclaimer in the documentation and/or other materials provided
 *      with the distribution.
 *   2. The end-user documentation included with the redistribution,
 *      if any, must include the following acknowledgment:
 *      "This product includes software developed by NGIT and the National
 *      Cancer Institute."   If no such end-user documentation is to be
 *      included, this acknowledgment shall appear in the software itself,
 *      wherever such third-party acknowledgments normally appear.
 *   3. The names "The National Cancer Institute", "NCI" and "NGIT" must
 *      not be used to endorse or promote products derived from this software.
 *   4. This license does not authorize the incorporation of this software
 *      into any third party proprietary programs. This license does not
 *      authorize the recipient to use any trademarks owned by either NCI
 *      or NGIT
 *   5. THIS SOFTWARE IS PROVIDED "AS IS," AND ANY EXPRESSED OR IMPLIED
 *      WARRANTIES, (INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 *      OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE) ARE
 *      DISCLAIMED. IN NO EVENT SHALL THE NATIONAL CANCER INSTITUTE,
 *      NGIT, OR THEIR AFFILIATES BE LIABLE FOR ANY DIRECT, INDIRECT,
 *      INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 *      BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *      CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 *      LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 *      ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 *      POSSIBILITY OF SUCH DAMAGE.
 * <!-- LICENSE_TEXT_END -->
 */

/**
 * @author EVS Team
 * @version 1.0
 */

public class ServerMonitorThread extends Thread {
    @SuppressWarnings("unused")
    private static Logger _logger = Logger.getLogger(ServerMonitorThread.class);
    private static ServerMonitorThread _instance = null;
	private long _interval = 1000 * 
	    NCItBrowserProperties.getIntProperty(
	        NCItBrowserProperties.PING_LEXEVS_INTERVAL, 600);
	private String _message = "";
	private static boolean _enabled = 
	    NCItBrowserProperties.getBooleanProperty(
	        NCItBrowserProperties.PING_LEXEVS_ENABLED, true);
    private Boolean _isThreadRunning = false;
    private boolean _isLexEVSRunning = true;
    private boolean _debug = false;  //DYEE_DEBUG (Default: false)
	
	static {
	    if (_enabled) //DYEE_DEBUG (Default: _enabled)
	        ServerMonitorThread.getInstance().start();
	}
	
	private ServerMonitorThread() {
	}
	
	public static ServerMonitorThread getInstance() {
	    if (_instance == null)
	        _instance = new ServerMonitorThread();
	    return _instance;
	}
	
	public void run() {
	    synchronized(_isThreadRunning) {
	        //Note: Ensures only one instance of this method is running.
    	    if (_isThreadRunning)
    	        return;
    	    _isThreadRunning = true;
	    }
	    
		while (true) {
			try {
		        monitor(RemoteServerUtil.createLexBIGService(), "ServerMonitorThread");
				sleep(_interval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
    public boolean isLexEVSRunning() {
        if (! _enabled) {
            // Note: Pretend it is running fine to avoid displaying warning message.
            return true;
        }
        
        //Quick test.
        monitor(RemoteServerUtil.createLexBIGService(), "ServerMonitorThread2");
        return _isLexEVSRunning;
    }

	public void setLexEVSRunning(boolean isRunning, String msg) {
        if (! _enabled)
            return;

	    if (_debug && msg != null && msg.length() > 0)
	        _logger.debug("isRunning(" + isRunning + "): " + msg);
        boolean prevIsRunning = _isLexEVSRunning;
        if (isRunning == prevIsRunning)
            return;

        _isLexEVSRunning = isRunning;
        updateMessage(isRunning);
        _logger.debug("_isLexEVSRunning: " + _isLexEVSRunning);
	}
	
    public String getMessage() {
        return _message;
    }

    private void updateMessage(boolean isRunning) {
        if (isRunning) {
            _message = "";
            return;
        } 
        
        _message = "*** The server is temporarily not available, as of "
            + new Date() + ". ***";
	}

    public void monitor(LexBIGService service, String msg) {
        if (! _enabled)
            return;
        try {
            boolean isRunning = service != null;
            service.getLastUpdateTime();
            setLexEVSRunning(isRunning, msg);
        } catch (Exception e) {
            _logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            setLexEVSRunning(false, msg);
        }
    }
    
    public void monitor(LexEVSDistributed service, String msg) {
        if (! _enabled)
            return;
        try {
            boolean isRunning = service != null;
            service.getLastUpdateTime();
            setLexEVSRunning(isRunning, msg);
        } catch (Exception e) {
            _logger.error(e.getClass().getSimpleName() + ": " + e.getMessage());
            setLexEVSRunning(false, msg);
        }
    }
}
