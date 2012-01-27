<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<%!
  private static final int INDENT = 10;
%>

<%
  String basePath = request.getContextPath();
  String ICON_LEAF = basePath + "/images/yui/treeview/ln.gif";
  String ICON_EXPAND = basePath + "/images/yui/treeview/lp.gif";
  String ICON_COLLAPSE = basePath + "/images/yui/treeview/lm.gif";
%>

<html>
  <head>
    <script language="javascript">
      function toggle(node)
      {
        // Unfold the branch if it isn't visible
        if (node.nextSibling.style.display == 'none')
        {
          // Change the image (if there is an image)
          if (node.children.length > 0)
          {
            if (node.children.item(0).tagName == "IMG")
            {
              node.children.item(0).src = "<%=ICON_COLLAPSE%>";
            }
          }
    
          node.nextSibling.style.display = '';
        }
    
        // Collapse the branch if it IS visible
        else
        {
          // Change the image (if there is an image)
          if (node.children.length > 0)
          {
            if (node.children.item(0).tagName == "IMG")
            {
              node.children.item(0).src = "<%=ICON_EXPAND%>";
            }
          }
    
          node.nextSibling.style.display = 'none';
        }
      }
    </script>
  </head>
  
  <body>
    <table border=0>
      <tr>
        <td>
          <table border=0>
            <tr>
              <td>
                <a onclick="toggle(this)"><img src="<%=ICON_COLLAPSE%>">Implementation</a><div> <%-- div must be on same line as toggle --%>
                <table border=0>
                  <tr>
                    <td width="<%=INDENT%>"></td>
                    <td>
                      <img src="<%=ICON_LEAF%>">PHP<div>
                      </div>
                    </td>
                  </tr>
                </table>
                
                <table border=0>
                  <tr>
                    <td width="<%=INDENT%>"></td>
                    <td><a onclick="toggle(this)"><img src="<%=ICON_COLLAPSE%>"> Visual C++</a><div>

                      <table border=0>
                        <tr>
                          <td width="<%=INDENT%>"></td>
                          <td>
                            <img src="<%=ICON_LEAF%>"> Memory Leak problems<div>
                            </div>
                          </td>
                        </tr>
                      </table></div>
                    </td>
                  </tr>
                </table></div>
                
              </td>
            </tr>
          </table>
        </td>
      </tr>
    </table>
  </body>
</html>
