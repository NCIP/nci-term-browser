<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.Vector"%>
<%@ page import="org.LexGrid.concepts.Entity" %>
<%@ page import="gov.nih.nci.evs.browser.utils.*" %>
<%@ page import="gov.nih.nci.evs.browser.utils.DataUtils" %>
<%@ page import="gov.nih.nci.evs.browser.properties.NCItBrowserProperties" %>

<%@ page import="javax.imageio.ImageIO" %>
<%@ page import="java.awt.image.BufferedImage" %>
<%@ page import="java.io.*" %>


<%@ page import="nl.captcha.Captcha" %>
<%@ page import="nl.captcha.audio.AudioCaptcha" %>

<html>
  <head>
    <title>NCI Term Browser</title>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styleSheet.css" />
    <link rel="shortcut icon" href="<%= request.getContextPath() %>/favicon.ico" type="image/x-icon" />
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/script.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/search.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/dropdown.js"></script>

    <script>
    /* Add bogus req param to url so that reloading will (hopefully) load a different .wav file. */

    function getContextPath() {
	return "<%=request.getContextPath()%>";
    }

    function loadAudio() {
        var path = getContextPath() + "/audio.wav?bogus=";
        document.getElementById("audioCaptcha").src = path + new Date().getTime();
        document.getElementById("audioSupport").innerHTML = document.createElement('audio').canPlayType("audio/wav");
    }
    
    </script>


  </head>
  <%
    boolean audio_captcha_background_noise_on = true;
    String audio_captcha_str = "audio.wav";
    if (!NCItBrowserProperties.isAudioCaptchaBackgroundNoiseOn()) {
        audio_captcha_background_noise_on = false;
        audio_captcha_str = "nci.audio.wav";
    }
    
    String captcha_option = "default";
    String alt_captcha_option = "audio";
    String opt = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("captcha_option"));
    if (opt != null && opt.compareTo("audio") == 0) {
          captcha_option = "audio";
          alt_captcha_option = "default";
    }    

  Captcha captcha = (Captcha) request.getSession().getAttribute("captcha");
  AudioCaptcha ac = null;  
  
    String errorMsg = (String) request.getSession().getAttribute("errorMsg");
    if (errorMsg != null) {
        request.getSession().removeAttribute("errorMsg");
    }  
  
  String retry = (String) request.getSession().getAttribute("retry");
  if (retry != null && retry.compareTo("true") == 0) {
        request.getSession().removeAttribute("retry");
  }
  
if (captcha_option.compareTo("default") == 0) {
  	captcha = new Captcha.Builder(200, 50)
	        .addText()
	        .addBackground()
	        //.addNoise()
		.gimp()
		//.addBorder()
                .build();
	request.getSession().setAttribute(Captcha.NAME, captcha);
} else {
/*
	ac = new AudioCaptcha.Builder()
	     .addAnswer()
	     .addNoise()
	     .build(); 
	request.getSession().setAttribute(AudioCaptcha.NAME, ac);     
*/
}


    
/*    
    String alt_captcha_option = "audio";
    String opt = HTTPUtils.cleanXSS((String) request.getParameter("opt"));
    if (opt != null && opt.compareTo("audio") == 0) {
        captcha_option = "audio";
        alt_captcha_option = "default";
    }
*/    
    
    System.out.println("captcha_option: " + captcha_option);
    System.out.println("alt_captcha_option: " + alt_captcha_option);
    
    
    String ncicb_contact_url = new DataUtils().getNCICBContactURL();
    String answer  = "";
    String subject = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("subject"));
    String message = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("message"));
    String emailaddress = HTTPUtils.cleanXSS((String) request.getSession().getAttribute("emailaddress"));
    
    String refresh = (String) request.getParameter("refresh");
    if (refresh != null) {
	     answer = "";
	     subject = HTTPUtils.cleanXSS((String) request.getParameter("subject"));
	     message = HTTPUtils.cleanXSS((String) request.getParameter("message"));
	     emailaddress = HTTPUtils.cleanXSS((String) request.getParameter("emailaddress"));
    }
    
    
    if (answer == null) answer = "";
    if (subject == null) subject = "";
    if (message == null) message = "";
    if (emailaddress == null) emailaddress = "";
    
    String errorType = (String) request.getAttribute("errorType");
    if (errorType == null) errorType = "";
    boolean userError = errorType.equalsIgnoreCase("user");
    //String errorMsg = (String) request.getAttribute("errorMsg");
    if (errorMsg == null) errorMsg = "";
  %>
    
  <body onload="loadAudio()">
  
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/wz_tooltip.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_centerwindow.js"></script>
    <script type="text/javascript" src="<%= request.getContextPath() %>/js/tip_followscroll.js"></script>
    <f:view>
    <!-- Begin Skip Top Navigation -->
      <a href="#evs-content" class="hideLink" accesskey="1" title="Skip repetitive navigation links">skip navigation links</A>
    <!-- End Skip Top Navigation -->     
      <%@ include file="/pages/templates/header.jsp" %>
      
      
      <span id="ctx" style="display:none;"><%=request.getContextPath()%></span>
      
      
      
      <div class="center-page">
        <%@ include file="/pages/templates/sub-header.jsp" %>
        <div id="main-area">
        <%@ include file="/pages/templates/content-header-no-searchbox.jsp" %>
          <div class="pagecontent">
            <a name="evs-content" id="evs-content"></a>
            <div class="texttitle-blue">Contact Us</div>
            <hr></hr>
            <p><b>NCI Enterprise Vocabulary Services (EVS)</b> provides 
              terminology content, tools, and services to NCI and the 
              biomedical research community.
              <ul>
                <li>For other EVS resources, services and contacts see 
                  the EVS Web site <a href="http://evs.nci.nih.gov/" target="_blank">http://evs.nci.nih.gov/</a>.
                </li>
                <li>For browser support or suggestions, try the options
                  on this page.
                </li>
              </ul>
            </p>
            <p><b>You can request help or make suggestions by filling out the
              online form below, or by using any one of these contact points:
            </b></p>
            <table class="textbody">
              <tr>
                <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                <td>Telephone:</td>
                <td>240-276-5541 or Toll-Free: 1-888-478-4423</td>
              </tr>
              <tr>
                <td/>
                <td>Email:</td>
                <td><a href="mailto:ncicbiit@mail.nih.gov">ncicbiit@mail.nih.gov</a></td>
              </tr>
              <tr>
                <td/>
                <td>Web Page:</td>
                <td><a href="http://ncicb.nci.nih.gov/support" target="_blank" alt="NCICB Support">http://ncicb.nci.nih.gov/support</a></td>
              </tr>
            </table>
            <p>

              Telephone support is available Monday to Friday, 8 am to 8 pm
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
                Cancer.gov help page</a>.
            </p>

            <%
            
           
              String color = "";
              if (userError) {
                  color = "style=\"color:#FF0000;\"";
              }
              else if (errorMsg != null && errorMsg.length() > 0) {
                  color = "style=\"color:#FF0000;\"";
              }
            %>
            <p><b>Online Form</b></p>
                 To use this web form, please fill in every box below and then click on Submit.
            </p>
            <p <%= color %>>
           
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

            
            <h:form id="contact_form" >
            
<p>            
      <table> 
      
      
<%
String answer_label = "Enter the characters appearing in the above image";

if (captcha_option.compareTo("default") == 0) {
%>
      <tr>
      <td class="textbody">
             <img src="<c:url value="/simpleCaptcha.png"  />" alt="simpleCaptcha.png">
             
       &nbsp;<h:commandLink value="Unable to read this image?" action="#{userSessionBean.regenerateCaptchaImage}" />
       <br/>             
      </td>
      </tr>

       

<%
} else {
      answer_label = "Enter the numbers you hear from the audio";
%>

<tr>
<td>
<p class="textbody">Click 


<a href="<%=request.getContextPath()%>/<%=audio_captcha_str%> ">here</a> to listen to the audio. 
</td>
</tr>


<%
} 
%>

      <tr>
      <td class="textbody"> 
          <%=answer_label%>: <i class="warningMsgColor">*</i> 
          <input type="text" id="answer" name="answer" value="<%=HTTPUtils.cleanXSS(answer)%>"/>&nbsp;
      </td>
      </tr> 
      
      
      <tr>
      <td class="textbody">
       <h:commandLink value="Prefer an alternative form of CAPTCHA?" action="#{userSessionBean.switchCaptchaMode}" />
       <br/>             
      </td>
      </tr>
      
      

      </table>              
</p>            
            
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
              &nbsp;&nbsp;



              <h:commandButton
                id="reset"
                value="Clear"
                alt="Clear"
                action="#{userSessionBean.resetContactUsForm}" >
              </h:commandButton>
              
              <!--<INPUT type="reset" value="Clear" alt="Clear">-->



<input type="hidden" name="captcha_option" id="captcha_option" value="<%=alt_captcha_option%>">


              
            </h:form>
            
            
            <a href="http://www.cancer.gov/global/web/policies/page2" target="_blank"
                alt="National Cancer Institute Policies">
              <i>Privacy Policy on E-mail Messages Sent to the NCI Web Site</i>
            </a>
            <%@ include file="/pages/templates/nciFooter.jsp" %>
          </div>
          <!-- end Page content -->
        </div>
        <div class="mainbox-bottom"><img src="<%=basePath%>/images/mainbox-bottom.gif" width="745" height="5" alt="Mainbox Bottom" /></div>
        <!-- end Main box -->
      </div>
    </f:view>
  </body>
</html>
