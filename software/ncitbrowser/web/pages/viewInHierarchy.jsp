<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%!
  private static final int INDENT = 10;
%>

<%
  String basePath = request.getContextPath();
  String ICON_LEAF = basePath + "/images/yui/treeview/ln.gif";
  String ICON_EXPAND = basePath + "/images/yui/treeview/lp.gif";
  String ICON_COLLAPSE = basePath + "/images/yui/treeview/lm.gif";
  
  TreeItem root = HierarchyUtils.init();
  StringBuffer buffer = new StringBuffer();
  HierarchyUtils.html(buffer, root, "");
  String tree = buffer.toString();
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
    <%= tree %>
  </body>
</html>
