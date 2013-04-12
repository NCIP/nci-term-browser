/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public interface TabFormatterInterface {
    public String getFilename();
    public void setTab(String tab);
    public String write(String text) throws Exception;
    public void writeln(String text) throws Exception;
    public void writeln() throws Exception;
    public String indent();
    public String undent();
    public void writeln_indent(String text) throws Exception;
    public void writeln_undent(String text) throws Exception;
    public void writeln_normal(String text) throws Exception;
    public void writeln_inden1(String text) throws Exception;
}
