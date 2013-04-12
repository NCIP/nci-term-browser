/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.utils;

import gov.nih.nci.evs.browser.properties.NCItBrowserProperties;

import java.util.Date;

import org.LexGrid.LexBIG.LexBIGService.LexBIGService;
import org.LexGrid.LexBIG.caCore.interfaces.LexEVSDistributed;
import org.apache.log4j.Logger;

/**
 * 
 */

/**
 * @author EVS Team
 * @version 1.0
 */

public class ServerMonitorThread extends Thread {
    @SuppressWarnings("unused")
    private static Logger _logger = null; //Logger.getLogger(ServerMonitorThread.class);
    private static String _className = ServerMonitorThread.class.getSimpleName();
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

	private static void debug(String text) {
	    if (_logger == null) {
	        //System.out.println(_className + ": " + text);
	    } else {
	        _logger.debug(text);
	    }
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
			    LexBIGService service = RemoteServerUtil.createLexBIGService();
		        monitor(service, "ServerMonitorThread");
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
        LexBIGService service = RemoteServerUtil.createLexBIGService();
        monitor(service, "ServerMonitorThread2");
        return _isLexEVSRunning;
    }

	public void setLexEVSRunning(boolean isRunning, String msg) {
        if (! _enabled)
            return;

	    if (_debug && msg != null && msg.length() > 0)
	        debug("isRunning(" + isRunning + "): " + msg);
        boolean prevIsRunning = _isLexEVSRunning;
        if (isRunning == prevIsRunning)
            return;

        _isLexEVSRunning = isRunning;
        updateMessage(isRunning);
        debug("_isLexEVSRunning: " + _isLexEVSRunning);
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
        if (service == null) {
            setLexEVSRunning(false, msg);
            return;
        }
        try {
            service.getLastUpdateTime();
            setLexEVSRunning(true, msg);
        } catch (Exception e) {
            error(e);
            setLexEVSRunning(false, msg);
        }
    }

    public void monitor(LexEVSDistributed service, String msg) {
        if (! _enabled)
            return;
        if (service == null) {
            setLexEVSRunning(false, msg);
            return;
        }
        try {
            service.getLastUpdateTime();
            setLexEVSRunning(true, msg);
        } catch (Exception e) {
            error(e);
            setLexEVSRunning(false, msg);
        }
    }

    private void error(Exception e) {
        //Note: Trying to solve Kim's problem with this method.
        //  He is getting exceptions when log4j tries to print an error message.
        String msg = "";
        if (e != null)
            msg = e.getClass().getSimpleName() + ": " + e.getMessage();
        else {
			//msg = "Exception e == " + e;

			msg = "Exception thrown ";

		}

        try {
            debug(msg);
        } catch (Exception e1) {
            //System.out.println(_className + ": " + e1.getMessage());
            //System.out.println(_className + ": " + msg);
            e1.printStackTrace();
        }
    }
}
