<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Concept" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
  <head>
    <title>NCI Thesaurus Browser Home</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
  </head>
  <%
    String ncicb_contact_url = new DataUtils().getNCICBContactURL();
  %>
  <body>
    <f:view>
      <%@ include file="/pages/templates/header.xhtml" %>
      <div class="center-page">
        <%@ include file="/pages/templates/sub-header.xhtml" %>
        <div id="main-area">
          <%@ include file="/pages/templates/content-header.xhtml" %>
          <div class="pagecontent">
            <div class="texttitle-blue">Contact Us</div>
            <hr></hr>
            <p><b>If you would like information immediately, please call:</b></p>
            <p>
            &nbsp;&nbsp;&nbsp;&nbsp;<b>1-800-4-CANCER (1-800-422-6237)</b><br/>
            &nbsp;&nbsp;&nbsp;&nbsp;<i>9:00 a.m. to 4:30 p.m. local time, Monday through Friday</i><br/>
            &nbsp;&nbsp;&nbsp;&nbsp;TTY 1-800-332-8615<br/>
            </p>
            <p>
              <b>Cras lobortis tincidunt nisi. In hac habitasse platea dictumst. In a orci. Cum sociis natoque
              penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer sed arcu. Mauris luctus
              nisi sed dolor. Vestibulum consequat pellentesque eros. Proin malesuada congue massa. Maecenas
              facilisis leo ut purus. Fusce orci leo, commodo sed, pellentesque eleifend, euismod ac, justo.
              Morbi convallis varius urna.</b>
            </p>
            <p><b>You must fill in every box below</b></p>
            <form method="post" action="mailto:<%=ncicb_contact_url%>" enctype="text/plain">
              <p><i>Enter the subject of your email</i></p>
              <input CLASS="input.formField" size="100" name="subject" onFocus="active = true" onBlur="active = false" onKeyPress="return ifenter(event,this.form)">
              <p>
                <i>Enter your message.<br/>
                  &nbsp;&nbsp;&nbsp;&nbsp;Please include all pertinent details within the contact message box.<br/>
                  &nbsp;&nbsp;&nbsp;&nbsp;We do not open attachments to e-mail messages.
                </i>
              </p>
              <TEXTAREA Name="message" rows="4" cols="75"></TEXTAREA>
              <p>
                <i>E-mail address<br/>
                  &nbsp;&nbsp;&nbsp;&nbsp;For example, jdoe@yahoo.com
                </i>
              </p>
              <input CLASS="input.formField" size="100" name="emailaddress" onFocus="active = true" onBlur="active = false" onKeyPress="return ifenter(event,this.form)">
              <br/><br/>
              <INPUT  type="submit" value="Submit">&nbsp;&nbsp;<INPUT type="reset" value="Clear">
            </form>
            <a href="http://www.cancer.gov/policies/page3" ><i>Privacy Policy on E-mail Messages Sent to the NCI Web Site</i></a>
            <%@ include file="/pages/templates/nciFooter.html" %>
          </div>
          <!-- end Page content -->
        </div>
        <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="" /></div>
        <!-- end Main box -->
      </div>
    </f:view>
  </body>
</html>
