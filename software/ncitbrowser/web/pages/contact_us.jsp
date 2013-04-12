<%--L
  Copyright Northrop Grumman Information Technology.

  Distributed under the OSI-approved BSD 3-Clause License.
  See http://ncip.github.com/nci-term-browser/LICENSE.txt for details.
L--%>

<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
  <head>
    <title>NCI Thesaurus</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  </head>
  <%
    String ncicb_contact_url = new DataUtils().getNCICBContactURL();
    String subject = HTTPUtils.cleanXSS(request.getParameter("subject"));
    String message = HTTPUtils.cleanXSS(request.getParameter("message"));
    String emailaddress = HTTPUtils.cleanXSS(request.getParameter("emailaddress"));
    if (subject == null) subject = "";
    if (message == null) message = "";
    if (emailaddress == null) emailaddress = "";
    String errorType = (String) request.getAttribute("errorType");
    if (errorType == null) errorType = "";
    boolean userError = errorType.equalsIgnoreCase("user");
    String errorMsg = (String) request.getAttribute("errorMsg");
    if (errorMsg == null) errorMsg = "";
  %>
  <body>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
    <f:view>
      <%@ include file="/pages/templates/header.jsp" %>
      <div class="center-page">
        <%@ include file="/pages/templates/sub-header.jsp" %>
        <div id="main-area">
<%          
String contact_dictionary = (String) request.getSession().getAttribute("dictionary");
if (contact_dictionary == null) {
%>
   <%@ include file="/pages/templates/content-header-no-searchbox.jsp" %>
<%   
}
else if (contact_dictionary.compareTo("NCI Thesaurus") == 0) {
%>
   <%@ include file="/pages/templates/content-header.jsp" %>
<%
} else {
%>
   <%@ include file="/pages/templates/content-header-other.jsp" %>
<%   
}
%>
          
          <div class="pagecontent">
            <div class="texttitle-blue">Contact Us</div>
            <hr></hr>
            <p><b>You can request help or make suggestions by filling out the
              online form below, or by using any one of these contact points:
            </b></p>
            <table class="textbody">
              <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td>Telephone:</td>
                <td>301.451.4384 or Toll-Free: 888.478.4423</td>
              </tr>
              <tr>
                <td/>
                <td>Email:</td>
                <td><a href="mailto:ncicb@pop.nci.nih.gov">ncicb@pop.nci.nih.gov</a></td>
              </tr>
              <tr>
                <td/>
                <td>Web Page:</td>
                <td><a href="http://ncicb.nci.nih.gov/support" target="_blank" alt="NCICB Support">http://ncicb.nci.nih.gov/support</a></td>
              </tr>
            </table>
            <p>

              Telephone support is available Monday to Friday, 8 am – 8 pm
              Eastern Time, excluding government holidays. You may leave a
              message, send an email, or submit a support request via the Web
              at any time.  Please include:
              <ul>
                <li>Your contact information;</li>
                <li>Reference to the NCIt Browser; and</li>
                <li>A detailed description of your problem or suggestion.</li>
              </ul>

              For questions related to NCI’s Cancer.gov Web site,
              see the
              <a href="http://www.cancer.gov/help" target="_blank" alt="Cancer.gov help">
                Cancer.gov help page</a>. &nbsp;
              For help and other questions concerning NCI Enterprise Vocabulary
              Services (EVS),
              see the <a href="http://evs.nci.nih.gov/" target="_blank" alt="EVS">
                EVS Web site</a>.
            </p>

            <%
              String color = "";
              if (userError)
                color = "style=\"color:#FF0000;\"";
            %>
            <p><b>Online Form</b></p>
            <p <%= color %>>
              To use this web form, please fill in every box below and then click on “Submit”.
              <%
                if (errorMsg != null && errorMsg.length() > 0) {
                    errorMsg = errorMsg.replaceAll("&lt;br/&gt;", "\n");
                    String[] list = Utils.toStrings(errorMsg, "\n", false, false);
                    for (int i=0; i<list.length; ++i) {
                      String text = list[i];
                      text = Utils.toHtml(text); // For leading spaces (indentation)
              %>
                      <br/><i style="color:#FF0000;"><%= text %></i>
              <%
                    }
                }
              %>
            </p>
            <form method="post">
              <p>
                <% if (userError) %> <i style="color:#FF0000;">* Required)</i>
                <i>Subject of your email:</i>
              </p>
              <input class="textbody" size="100" name="subject" alt="Subject" value="<%= subject %>" onFocus="active = true" onBlur="active = false" onKeyPress="return ifenter(event,this.form)">
              <p>
                <% if (userError) %> <i style="color:#FF0000;">* Required)</i>
                <i>Detailed description of your problem or suggestion (no attachments):</i>
              </p>
              <TEXTAREA class="textbody" Name="message" alt="Message" rows="4" cols="98"><%= message %></TEXTAREA>
              <p>
                <% if (userError) %> <i style="color:#FF0000;">* Required)</i>
                <i>Your e-mail address:</i>
              </p>
              <input class="textbody" size="100" name="emailaddress" alt="Email Address" value="<%= emailaddress %>" onFocus="active = true" onBlur="active = false" onKeyPress="return ifenter(event,this.form)">
              <br/><br/>

              <h:commandButton
                id="mail"
                value="Submit"
                alt="Submit"
                action="#{userSessionBean.contactUs}" >
              </h:commandButton>
              &nbsp;&nbsp;<INPUT type="reset" value="Clear" alt="Clear">
            </form>
            <a href="http://www.cancer.gov/policies/page3" target="_blank"
                alt="National Cancer Institute Policies">
              <i>Privacy Policy on E-mail Messages Sent to the NCI Web Site</i>
            </a>
            <%@ include file="/pages/templates/nciFooter.html" %>
          </div>
          <!-- end Page content -->
        </div>
        <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
        <!-- end Main box -->
      </div>
    </f:view>
  </body>
</html>
