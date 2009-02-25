var combodropoffsetY=2 //offset of drop down menu vertically from default location (in px)
var combozindex=100

function dhtmlselect(selectid, selectwidth, optionwidth){
	var selectbox=document.getElementById(selectid)
	document.write('<div id="dhtml_'+selectid+'" class="dhtmlselect"><div class="dropdown">')
	for (var i=0; i<selectbox.options.length; i++)
		document.write('<a href="'+selectbox.options[i].value+'">'+selectbox.options[i].text+'</a>')
	document.write('</div></div>')
	selectbox.style.display="none"
	var dhtmlselectbox=document.getElementById("dhtml_"+selectid)
	dhtmlselectbox.style.zIndex=combozindex
	combozindex--
	if (typeof selectwidth!="undefined")
		dhtmlselectbox.style.width=selectwidth
	if (typeof optionwidth!="undefined")
		dhtmlselectbox.getElementsByTagName("div")[0].style.width=optionwidth
	dhtmlselectbox.getElementsByTagName("div")[0].style.top=dhtmlselectbox.offsetHeight-combodropoffsetY+"px"
	dhtmlselectbox.onmouseover=function(){
		this.getElementsByTagName("div")[0].style.display="block"
	}
	dhtmlselectbox.onmouseout=function(){
		this.getElementsByTagName("div")[0].style.display="none"
	}
}