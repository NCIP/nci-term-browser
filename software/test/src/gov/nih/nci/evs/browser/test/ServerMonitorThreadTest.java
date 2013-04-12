/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.test;

import gov.nih.nci.evs.browser.test.utils.SetupEnv;
import gov.nih.nci.evs.browser.utils.ServerMonitorThread;

public class ServerMonitorThreadTest {
    public static void main(String[] args) throws Exception {
        args = SetupEnv.getInstance().parse(args);
        new ServerMonitorThreadTest().test();
    }
    
    private void debug(String text) {
        System.out.println(text);
    }
    
    public void test() throws Exception {
        ServerMonitorThread thread = ServerMonitorThread.getInstance();
        debug("Before start");
        thread.start();
        debug("After start");
        Thread.sleep(3000);
        thread.stop();
        debug("After stop");
    }
}
