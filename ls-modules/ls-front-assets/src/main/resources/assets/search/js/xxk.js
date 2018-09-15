function secBoard(td,tr,m,n,sec1,sec2) //m是选项卡数量,n是当前选项卡序号
{
	//更新选项卡样式
	for(i=0;i<m;i++) { 
	if (i==n) {
		document.getElementById(td+n).className=sec2;
	} else {
		document.getElementById(td+i).className=sec1;}
}

//隐藏和显示行
for(i=0;i<m;i++) {
	if (i==n) {
		document.getElementById(tr+n).style.display="";
	} else {
		document.getElementById(tr+i).style.display="none";}
	}
}
function secBoard_more(td,tr,m,n,sec1,sec2,A,URL) {//m是选项卡数量,n是当前选项卡序号
	if(URL != ""){
		URL_A = URL.split("|")
	}
	//更新选项卡样式
	for(i=0;i<m;i++) { 
		if (i==n) {
			document.getElementById(td+n).className=sec2;
			if(URL != ""){
				document.getElementById(A).href=URL_A[i];
			}
		} else {
			document.getElementById(td+i).className=sec1;
		}
	}
	
	//隐藏和显示行
	for(i=0;i<m;i++) {
		if (i==n) {
			document.getElementById(tr+n).style.display="";
		} else {
			document.getElementById(tr+i).style.display="none";
		}
	}
}
