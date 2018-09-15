var href = $("#" + hrefId).attr("href");
var d = dialog({
    align: 'bottom left',
    autofocus:true,
    content: "<font style='color: red;' size='5'>【" + errCode + "】" + errDesc + "</font><br>" + href
});
d.show(document.getElementById(hrefId));
