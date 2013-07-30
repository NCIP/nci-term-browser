<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ page import="gov.nih.nci.evs.browser.utils.*" %>

<%@ page contentType="text/html;charset=windows-1252"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">


<%
String code = HTTPUtils.cleanXSS((String) request.getParameter("code"));
String id = HTTPUtils.cleanXSS((String) request.getParameter("id"));
%>


<html lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>BioPortal Ontology Visualization</title>
	<script src="<%= request.getContextPath() %>/js/flexoviz.js" language="javascript" type="text/javascript"></script>
</head>

<body scroll="no" style="margin:0px;overflow:hidden;">

<script language="JavaScript" type="text/javascript">
<!--
// Globals
// Major version of Flash required
var requiredMajorVersion = 9;
// Minor version of Flash required
var requiredMinorVersion = 0;
// Minor version of Flash required
var requiredRevision = 28;
// -->
</script>
<script src="<%= request.getContextPath() %>/js/AC_OETags.js" language="javascript" type="text/javascript"></script>

<!--  BEGIN Browser History required section -->
<style type="text/css">
#ie_historyFrame { width: 0px; height: 0px; display:none }
#firefox_anchorDiv { width: 0px; height: 0px; display:none }
#safari_formDiv { width: 0px; height: 0px; display:none }
#safari_rememberDiv { width: 0px; height: 0px; display:none }
</style>
<script src="<%= request.getContextPath() %>/history/history.js" language="javascript"></script>
<!--  END Browser History required section -->

<script language="javascript" type="text/javascript">
<!--

function focusApp() {
	var app = document.getElementById("BasicFlexoViz");
	if (app) {
		app.focus();
	}
}

// Version check for the Flash Player that has the ability to start Player Product Install (6.0r65)
var hasProductInstall = DetectFlashVer(6, 0, 65);

// Version check based upon the values defined in globals
var hasRequestedVersion = DetectFlashVer(requiredMajorVersion, requiredMinorVersion, requiredRevision);

if ( hasProductInstall && !hasRequestedVersion ) {
	// DO NOT MODIFY THE FOLLOWING FOUR LINES
	// Location visited after installation is complete if installation is required
	var MMPlayerType = (isIE == true) ? "ActiveX" : "PlugIn";
	var MMredirectURL = window.location;
    document.title = document.title.slice(0, 47) + " - Flash Player Installation";
    var MMdoctitle = document.title;

	AC_FL_RunContent(
		"src", "playerProductInstall",
		"FlashVars", "MMredirectURL="+MMredirectURL+'&MMplayerType='+MMPlayerType+'&MMdoctitle='+MMdoctitle+"",
		"width", "100%",
		"height", "100%",
		"align", "middle",
		"id", "BasicFlexoViz",
		"quality", "high",
		"bgcolor", "#ffffff",
		"name", "BasicFlexoViz",
		"allowScriptAccess","sameDomain",
		"type", "application/x-shockwave-flash",
		"pluginspage", "http://www.adobe.com/go/getflashplayer"
	);
} else if (hasRequestedVersion) {
	// if we've detected an acceptable version embed the Flash Content SWF when all tests are passed
	AC_FL_RunContent(
			"src", "BasicFlexoViz?v=2.3.4.1",
			"width", "100%",
			"height", "100%",
			"align", "middle",
			"id", "BasicFlexoViz",
			"quality", "high",
			"bgcolor", "#ffffff",
			"name", "BasicFlexoViz",
			"allowScriptAccess","always",
			"type", "application/x-shockwave-flash",
			"flashVars", "server=&redirecturl=&ontology=<%=id%>&virtual=false&nodeid=<%=code%>",
			"pluginspage", "http://www.adobe.com/go/getflashplayer"
	);
  } else {
  	// flash is too old or we can't detect the plugin
    var alternateContent = '<br/>This website requires the Adobe Flash Player.  '
    + 'Please download and install the Flash plug-in and try again.  '
   	+ '<a href=http://www.adobe.com/go/getflash/>Get Flash</a><br/>';
    document.write(alternateContent);
  }
// -->
</script>


<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-768205-8");
pageTracker._trackPageview();
} catch(err) {}
</script>

<f:view>

<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" id="FlexoViz" width="100%" height="100%"
     codebase="http://fpdownload.macromedia.com/get/flashplayer/current/swflash.cab">
    <param name="movie" value="http://bioportal.bioontology.org/flex/BasicFlexoViz.swf" />
    <param name="quality" value="high" />
    <param name="bgcolor" value="#ffffff" />
    <param name="allowScriptAccess" value="always" />
    
    <embed src="http://bioportal.bioontology.org/flex/BasicFlexoViz.swf" bgcolor="#ffffff" width="100%"
           height="100%" name="FlexoViz" align="middle" play="true" loop="false" quality="high" allowScriptAccess="always"
           type="application/x-shockwave-flash"
           flashVars="widget=true&ontology=<%=id%>&nodeid=<%=code%>&server=http://rest.bioontology.org/bioportal"
           pluginspage="http://www.adobe.com/go/getflashplayer">
    </embed>
</object>

</f:view>

</body>
</html>
