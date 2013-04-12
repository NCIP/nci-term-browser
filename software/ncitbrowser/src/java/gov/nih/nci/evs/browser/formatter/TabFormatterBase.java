/*L
 * Copyright Northrop Grumman Information Technology.
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
 */

package gov.nih.nci.evs.browser.formatter;

import java.io.File;

/**
 * 
 */

/**
 * @author EVS Team (David Yee)
 * @version 1.0
 */

public class TabFormatterBase implements TabFormatterInterface {
    private static final String TAB = "  ";
    private String _filename = "";
    private String _tab = TAB;
    private String _indentation = "";
    
    public TabFormatterBase(String filename) {
        _filename = filename;
    }

    public String getFilename() {
        File file = new File(_filename);
        String name = file.getName();
        return name;
    }

    public void setTab(String tab) {
        _tab = tab;
    }

    public String write(String text) throws Exception {
        if (text == null)
            text = "";
        text = _indentation + text;
        //text = String.format("%4d: ", _indentation.length()) + text;
        return text;
    }

    public void writeln(String text) throws Exception {
        write(text + "\n");
    }
    
    public void writeln() throws Exception {
        writeln("");
    }

    public String indent() {
        _indentation += _tab;
        return _indentation;
    }

    public String undent() {
        if (_indentation.length() > 0)
            _indentation = _indentation.substring(_tab.length());
        return _indentation;
    }

    public void writeln_indent(String text) throws Exception {
        indent();
        writeln(text);
    }

    public void writeln_undent(String text) throws Exception {
        writeln(text);
        undent();
    }

    public void writeln_normal(String text) throws Exception {
        writeln(text);
    }
    
    public void writeln_inden1(String text) throws Exception {
        indent();
        writeln(text);
        undent();
    }
}
