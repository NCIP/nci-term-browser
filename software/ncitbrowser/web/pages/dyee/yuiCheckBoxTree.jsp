<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>


    <meta http-equiv="content-type" content="text/html; charset=utf-8">
<title>Treeview Node Selection and Checkbox Example</title>

<style type="text/css">
/*margin and padding on body element
  can introduce errors in determining
  element position and are not recommended;
  we turn them off as a foundation for YUI
  CSS treatments. */
body {
    margin:0;
    padding:0;
}
</style>

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.9.0/build/treeview/assets/skins/sam/treeview.css" />
<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>

<script type="text/javascript" src="http://yui.yahooapis.com/2.9.0/build/treeview/treeview.js"></script>


<!--begin custom header content for this example-->
<!--Additional custom style rules for this example:-->
<style type="text/css">

.ygtvcheck0 { background: url(../treeview/assets/img/check/check0.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }
.ygtvcheck1 { background: url(../treeview/assets/img/check/check1.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }
.ygtvcheck2 { background: url(../treeview/assets/img/check/check2.gif) 0 0 no-repeat; width:16px; height:20px; float:left; cursor:pointer; }

.ygtv-edit-TaskNode  {  width: 190px;}
.ygtv-edit-TaskNode .ygtvcancel, .ygtv-edit-TextNode .ygtvok  { border:none;}
.ygtv-edit-TaskNode .ygtv-button-container { float: right;}
.ygtv-edit-TaskNode .ygtv-input  input{ width: 140px;}
.whitebg {
    background-color:white;
}
</style>

<!--end custom header content for this example-->

</head>

<body class="yui-skin-sam">


<h1>Treeview Node Selection and Checkbox Example</h1>

<div class="exampleIntro">
    <p>In this simple example you can see how to do node selection in the
<a href="http://developer.yahoo.com/yui/treeview/">TreeView Control</a>.</p>


            
</div>

<!--BEGIN SOURCE CODE FOR EXAMPLE =============================== -->


    <h3>Tree with highlight propagation and 'checkbox' skin</h3>

    <div id="treeDiv1"  class="whitebg ygtv-checkbox"></div>
    <button id="logHilit">Log selected</button>

    <hr/>
    <h3>Tree with single node highlighting and simple skin</h3>
    <div id="treeDiv2" class="whitebg ygtv-highlight"></div>


<script type="text/javascript">

//global variable to allow console inspection of tree:
var tree1, tree2;

//anonymous function wraps the remainder of the logic:
(function() {

    var makeBranch = function (parent,label) {
        label = label || '';
        var n = Math.random() * (6 - (label.length || 0));
        for (var i = 0;i < n;i++) {
            var tmpNode = new YAHOO.widget.TextNode('label' + label + '-' + i, parent, Math.random() > .5);
            makeBranch(tmpNode,label + '-' + i);
        }
    }


    var treeInit = function() {
        tree1 = new YAHOO.widget.TreeView("treeDiv1");
        makeBranch(tree1.getRoot());
        tree1.setNodesProperty('propagateHighlightUp',true);
        tree1.setNodesProperty('propagateHighlightDown',true);
        tree1.subscribe('clickEvent',tree1.onEventToggleHighlight);     
        tree1.render();

        YAHOO.util.Event.on('logHilit','click',function() {
            var hiLit = tree1.getNodesByProperty('highlightState',1);
            if (YAHOO.lang.isNull(hiLit)) { 
                YAHOO.log("None selected");
            } else {
                var labels = [];
                for (var i = 0; i < hiLit.length; i++) {
                    labels.push(hiLit[i].label);
                }
                YAHOO.log("Highlighted nodes:\n" + labels.join("\n"), "info", "example");
            }
        });


        tree2 = new YAHOO.widget.TreeView("treeDiv2");
        makeBranch(tree2.getRoot());
        tree2.singleNodeHighlight = true;
        tree2.subscribe('clickEvent',tree2.onEventToggleHighlight);     
        tree2.render();
        
        
    };

    //Add an onDOMReady handler to build the tree when the document is ready
    YAHOO.util.Event.onDOMReady(treeInit);

})();
</script>

<!--END SOURCE CODE FOR EXAMPLE =============================== -->


<!--MyBlogLog instrumentation-->
<script type="text/javascript" src="http://track2.mybloglog.com/js/jsserv.php?mblID=2007020704011645"></script>

</body>
</html>

<script type="text/javascript"
src="http://l.yimg.com/d/lib/rt/rto1_78.js"></script><script>var rt_page="2012403676:FRTMA"; var
rt_ip="165.112.132.123";
if ("function" == typeof(rt_AddVar) ){ rt_AddVar("ys", escape("19198B62")); rt_AddVar("cr", escape(""));
rt_AddVar("sg", escape("/SIG=13smjlgjt48ihljh7capdd&b=4&d=rS3uQpRpYEJNh.nH.Skp31WFhxW6r57e_GaUjQ--&s=bp&i=ilyfZGaP1KBwbGQZlt6y/1324315877/165.112.132.123/19198B62")); rt_AddVar("yd", escape("1592531100"));
}</script><noscript><img src="http://rtb.pclick.yahoo.com/images/nojs.gif?p=2012403676:FRTMA"></noscript>
<!-- SpaceID=2012403676 loc=FSRVY noad -->
<script language=javascript>
if(window.yzq_d==null)window.yzq_d=new Object();
window.yzq_d['THWPDmKL5NI-']='&U=12d4rana5%2fN%3dTHWPDmKL5NI-%2fC%3d-1%2fD%3dFSRVY%2fB%3d-1%2fV%3d0';

</script><noscript><img width=1 height=1 alt="" src="http://us.bc.yahoo.com/b?P=M8A74WKLGUlESjWcTsVlrRWUpXCEe07vdOUAANws&T=1824qpg6l%2fX%3d1324315877%2fE%3d2012403676%2fR%3ddev_net%2fK%3d5%2fV%3d2.1%2fW%3dH%2fY%3dYAHOO%2fF%3d2087317306%2fH%3dc2VydmVJZD0iTThBNzRXS0xHVWxFU2pXY1RzVmxyUldVcFhDRWUwN3ZkT1VBQU53cyIgc2l0ZUlkPSI0NDY1NTUxIiB0U3RtcD0iMTMyNDMxNTg3NzA2MTI0NCIg%2fQ%3d-1%2fS%3d1%2fJ%3d19198B62&U=12d4rana5%2fN%3dTHWPDmKL5NI-%2fC%3d-1%2fD%3dFSRVY%2fB%3d-1%2fV%3d0"></noscript><script language=javascript>
if(window.yzq_d==null)window.yzq_d=new Object();
window.yzq_d['SnWPDmKL5NI-']='&U=13egd4bos%2fN%3dSnWPDmKL5NI-%2fC%3d289534.9603437.10326224.9298098%2fD%3dFOOT%2fB%3d4123617%2fV%3d1';
</script><noscript><img width=1 height=1 alt="" src="http://us.bc.yahoo.com/b?P=M8A74WKLGUlESjWcTsVlrRWUpXCEe07vdOUAANws&T=182ectgqn%2fX%3d1324315877%2fE%3d2012403676%2fR%3ddev_net%2fK%3d5%2fV%3d2.1%2fW%3dH%2fY%3dYAHOO%2fF%3d3816985784%2fH%3dc2VydmVJZD0iTThBNzRXS0xHVWxFU2pXY1RzVmxyUldVcFhDRWUwN3ZkT1VBQU53cyIgc2l0ZUlkPSI0NDY1NTUxIiB0U3RtcD0iMTMyNDMxNTg3NzA2MTI0NCIg%2fQ%3d-1%2fS%3d1%2fJ%3d19198B62&U=13egd4bos%2fN%3dSnWPDmKL5NI-%2fC%3d289534.9603437.10326224.9298098%2fD%3dFOOT%2fB%3d4123617%2fV%3d1"></noscript><!--QYZ ,;;;2012403676;;-->
<!-- VER-3.0.205072 -->
<script language=javascript>
if(window.yzq_p==null)document.write("<scr"+"ipt language=javascript src=http://l.yimg.com/d/lib/bc/bc_2.0.5.js></scr"+"ipt>");
</script><script language=javascript>
if(window.yzq_p)yzq_p('P=M8A74WKLGUlESjWcTsVlrRWUpXCEe07vdOUAANws&T=17tuio41t%2fX%3d1324315877%2fE%3d2012403676%2fR%3ddev_net%2fK%3d5%2fV%3d1.1%2fW%3dJ%2fY%3dYAHOO%2fF%3d1092801793%2fH%3dc2VydmVJZD0iTThBNzRXS0xHVWxFU2pXY1RzVmxyUldVcFhDRWUwN3ZkT1VBQU53cyIgc2l0ZUlkPSI0NDY1NTUxIiB0U3RtcD0iMTMyNDMxNTg3NzA2MTI0NCIg%2fS%3d1%2fJ%3d19198B62');
if(window.yzq_s)yzq_s();
</script><noscript><img width=1 height=1 alt="" src="http://us.bc.yahoo.com/b?P=M8A74WKLGUlESjWcTsVlrRWUpXCEe07vdOUAANws&T=1824vkfoj%2fX%3d1324315877%2fE%3d2012403676%2fR%3ddev_net%2fK%3d5%2fV%3d3.1%2fW%3dJ%2fY%3dYAHOO%2fF%3d1893314691%2fH%3dc2VydmVJZD0iTThBNzRXS0xHVWxFU2pXY1RzVmxyUldVcFhDRWUwN3ZkT1VBQU53cyIgc2l0ZUlkPSI0NDY1NTUxIiB0U3RtcD0iMTMyNDMxNTg3NzA2MTI0NCIg%2fQ%3d-1%2fS%3d1%2fJ%3d19198B62"></noscript><script language=javascript>
(function(){window.xzq_p=function(R){M=R};window.xzq_svr=function(R){J=R};function F(S){var T=document;if(T.xzq_i==null){T.xzq_i=new Array();T.xzq_i.c=0}var R=T.xzq_i;R[++R.c]=new Image();R[R.c].src=S}window.xzq_sr=function(){var S=window;var Y=S.xzq_d;if(Y==null){return }if(J==null){return }var T=J+M;if(T.length>P){C();return }var X="";var U=0;var W=Math.random();var V=(Y.hasOwnProperty!=null);var R;for(R in Y){if(typeof Y[R]=="string"){if(V&&!Y.hasOwnProperty(R)){continue}if(T.length+X.length+Y[R].length<=P){X+=Y[R]}else{if(T.length+Y[R].length>P){}else{U++;N(T,X,U,W);X=Y[R]}}}}if(U){U++}N(T,X,U,W);C()};function N(R,U,S,T){if(U.length>0){R+="&al="}F(R+U+"&s="+S+"&r="+T)}function C(){window.xzq_d=null;M=null;J=null}function K(R){xzq_sr()}function B(R){xzq_sr()}function L(U,V,W){if(W){var R=W.toString();var T=U;var Y=R.match(new RegExp("\\\\(([^\\\\)]*)\\\\)"));Y=(Y[1].length>0?Y[1]:"e");T=T.replace(new RegExp("\\\\([^\\\\)]*\\\\)","g"),"("+Y+")");if(R.indexOf(T)<0){var X=R.indexOf("{");if(X>0){R=R.substring(X,R.length)}else{return W}R=R.replace(new RegExp("([^a-zA-Z0-9$_])this([^a-zA-Z0-9$_])","g"),"$1xzq_this$2");var Z=T+";var rv = f( "+Y+",this);";var S="{var a0 = '"+Y+"';var ofb = '"+escape(R)+"' ;var f = new Function( a0, 'xzq_this', unescape(ofb));"+Z+"return rv;}";return new Function(Y,S)}else{return W}}return V}window.xzq_eh=function(){if(E||I){this.onload=L("xzq_onload(e)",K,this.onload,0);if(E&&typeof (this.onbeforeunload)!=O){this.onbeforeunload=L("xzq_dobeforeunload(e)",B,this.onbeforeunload,0)}}};window.xzq_s=function(){setTimeout("xzq_sr()",1)};var J=null;var M=null;var Q=navigator.appName;var H=navigator.appVersion;var G=navigator.userAgent;var A=parseInt(H);var D=Q.indexOf("Microsoft");var E=D!=-1&&A>=4;var I=(Q.indexOf("Netscape")!=-1||Q.indexOf("Opera")!=-1)&&A>=4;var O="undefined";var P=2000})();
</script><script language=javascript>
if(window.xzq_svr)xzq_svr('http://csc.beap.ad.yieldmanager.net/');
if(window.xzq_p)xzq_p('i?bv=1.0.0&bs=(128lraoqc(gid$M8A74WKLGUlESjWcTsVlrRWUpXCEe07vdOUAANws,st$1324315877061244,v$1.0))&t=J_3-D_3');
if(window.xzq_s)xzq_s();
</script><noscript><img width=1 height=1 alt="" src="http://csc.beap.ad.yieldmanager.net/i?bv=1.0.0&bs=(128lraoqc(gid$M8A74WKLGUlESjWcTsVlrRWUpXCEe07vdOUAANws,st$1324315877061244,v$1.0))&t=J_3-D_3"></noscript>
<!-- p2.ydn.bf1.yahoo.com compressed/chunked Mon Dec 19 09:31:17 PST 2011 -->
