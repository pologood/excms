var k=1;function showRes(){var obj=$("#resources");var icon_btn=$("#icon_btn");var offset=$("#resources").offset();$("#res").css({width:obj.outerWidth()+icon_btn.outerWidth()-1}).slideDown("fast");if(k==1){$("#res").show();$("#select_icon").addClass("fa-caret-up");$("#select_icon").removeClass("fa-caret-down");k=0;}else{$("#res").hide();$("#select_icon").addClass("fa-caret-down");$("#select_icon").removeClass("fa-caret-up");k=1;}}function getSource(){Ls.ajaxGet({url:"/content/newsSource",success:function(text){if(text.status==1){var ns=text.data;var ns_t="";for(var i=0;i<ns.length;i++){ns_t+='<tr style="margin:5px;cursor:pointer;font-size:14px;" onclick="getKey(\''+ns[i].key+"')\"><td>"+ns[i].key+"</td></tr>";}$("#ns_list").html(ns_t);}else{Ls.tips("加载文章来源失败",{icons:"error"});}},error:function(){Ls.tips("删除失败",{icons:"error"});}});}function getKey(key){$("#resources").val(key);$("#res").hide();$("#select_icon").addClass("fa-caret-down");$("#select_icon").removeClass("fa-caret-up");k=1;}