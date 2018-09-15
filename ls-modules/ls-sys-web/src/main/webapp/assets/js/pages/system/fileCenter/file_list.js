var file_manage = function () {

    //拉取数据
    function getData() {
        return Ls.ajaxGet({
            url: "/fileCenter/getPage",
            data: cur
        }).done(function (d) {

            var listHtml = '';
            if (file_manage.showStatus == 2) {
                listHtml = imgTemplate(d);
            } else {
                listHtml = pageTemplate(d.data);
            }
            $("#doc_list_body").slideDown("slow");
            $("#doc_list_body").html(listHtml);

            // 弹窗调用
            if (Ls.getWin.win != window) {
                $('.winHide').hide();
                $('th.winTc').text('');
                $('td.winTc').text('');
                $('#center .table-toolbar .col-md-2').show();
            }

        }).done(function (d) {
            //cur.pageIndex = d.pageIndex;
            //Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
            if ($('#pagination').length ) {}

            var pIndex = d.pageIndex;
            if (pIndex == 0) {
                pIndex = 1;
            } else {
                pIndex = pIndex + 1;
            }
            Ls.pagination3("#pagination", function (pageIndex) {
                pageselectCallback(pageIndex - 1);
            }, {
                changeCallBack: function (pageSize) {
                    pageselectCallback(0, pageSize);
                },
                pageSize: cur.pageSize,
                currPage: pIndex,
                pageCount: d.pageCount,
                sizeList: [27, 40, 50]
            });
        });
    };

    /*function pageselectCallback(page_index, jq) {
        cur.pageIndex = page_index;
        getData();
        return false;
    }*/
    function pageselectCallback(page_index, page_size) {
        cur.pageIndex = page_index;
        page_size && (cur.pageSize = page_size);
        getData();
        return false;
    }

    function imgTemplate(data) {
        var tbHtml = '<div ><div style="height:40px;width:100%;background:#F5F5F5;margin:5px;border-top:1px solid #EDEDED;border-bottom:2px solid #EDEDED"><input style="margin-left:10px;margin-top:14px;" type="checkbox" id="checkAll" onclick="checkAll()">' +
            '<div style="font-size:14px;float:right;line-height:40px;margin-right:20px" id="checkNum"></div></div>';
        var d = data.data;
        var more = "";
        var imgHtml = "";
        if (cur.pageIndex < data.pageCount - 1) {
            more = '<div onclick="getMoreImg()" title="加载更多" style="float:left;margin:15px;height:108px;width:108px;text-align:center;border:1px solid #E0E0E0;" class="hand"><div style=line-height:107px;"><img style="margin:auto;opacity:0.2;" height="64" width="64" src="' + GLOBAL_CONTEXTPATH + '/assets/images/more.png"/></div></div>';
        }
        for (var i = 0; i < d.length; i++) {
            var el = d[i];
            var img = "";
            var filePath =  GLOBAL_FILESERVERNAMEPATH + el.mongoName;

            // var filePath = ( el.mongoId.indexOf(".") != -1 ? GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH ) + el.mongoId;
            if (el.suffix.toLowerCase() == "jpg" || el.suffix.toLowerCase() == "jpeg" || el.suffix.toLowerCase() == "png" || el.suffix.toLowerCase() == "gif") {
                img = filePath;
            } else {
                img = fileTypeThumb(el);
            }
            var title = "";
            if (el.suffix.toLowerCase() == "jpg" || el.suffix.toLowerCase() == "png" || el.suffix.toLowerCase() == "jpeg" || el.suffix.toLowerCase() == "gif" || el.suffix.toLowerCase() == "txt" || el.suffix.toLowerCase() == "xml" || el.suffix.toLowerCase() == "html") {
                title = '<span> <a target="_blank" href="' + filePath + '"><u style="color:">' + el.fileName + '.' + el.suffix + '</u></a></span>';
            } else if (el.suffix.toLowerCase() == "flv") {
                title = '<span> <a href="javascript:" onclick="palyVideo(' + el.id + ')"><u style="color:">' + el.fileName + '.' + el.suffix + '</u></a></span>';
            } else {
                title = '<span style="cursor:default">' + el.fileName + '.' + el.suffix + '</span>';
            }
            imgHtml += '<div onMouseOver="showCheckBox(' + el.id + ',\'' + el.mongoId + '\')" onMouseOut="hideCheckBox(' + el.id + ',\'' + el.mongoId + '\')" style="width:108px;float:left;margin:15px;"><input name="check" id="' + el.mongoId + '" type="checkbox" style="display: none;" value="' + el.id + '"/>' +
                '<div  title="名称：' + el.fileName + '.' + el.suffix + '&#xd;日期：' + el.createDate + '&#xd;引用：' + (el.status == 1 ? "已引用" : "未引用") + '"' +
                ' id="img_' + el.id + '" class="unCheckImg imgBox hand" style="position:relative;background:#FCFCFC">' +
                '<div id="showCk_' + el.id + '" onclick="checkImg(' + el.id + ',\'' + el.mongoId + '\')" class="unCheckFile hide showCk" style="position:absolute;height:18px;width:18px;top:-1px;left:-1px;cursor:default"></div>' +
                '<img onclick="downloadFile(\'' + el.mongoId + '\')" style="margin:1px;" height="104" width="104" src="' + img + '"/></div>' +
                '<div style="width:107px;margin:10px 0 0 0;text-align:center;white-space:nowrap;text-overflow:ellipsis;overflow:hidden; ">' + title + '</div></div>';
        }
        tbHtml += imgHtml + more + '</div>';
        return tbHtml;
    }

    function pageTemplate(d) {
        var tbHtml = '<table class="table table-hover doc-list">' +
            '<thead>' +
            '<tr>' +
            '  <th class="w30">' +
            '    <input type="checkbox" id="checkAll" onclick="checkAll()">' +
            '  </th>' +
            '  <th class="title">文件名</th>' +
            '  <th class="w100 tc winTc">是否引用</th>' +
            '  <th class="w110">文件大小</th>' +
            '  <th class="w120 tc">上传IP</th>' +
            '  <th class="w180 tc">上传时间</th>' +
            '</tr>' +
            '</thead>' +
            '<tbody>';
        for (var i = 0; i < d.length; i++) {
            var el = d[i];
            var fn = FileName(el);
            var fs = SizeTran(el);
            var iu = IsUsed(el);
            var delSt = "";
            if (el.status != 1) delSt = ' <img class="hand ml8" onclick="deleteFile(' + el.id + ')" title="删除" src="' + GLOBAL_CONTEXTPATH + '/assets/images/delete.png"/>';
            tbHtml += '<tr id="tr_' + el.id + '" onMouseOver="msOver(' + el.id + ')" onMouseOut="msOut(' + el.id + ')">' +
                '  <td>' +
                '    <div class="">' +
                '      <input type="checkbox" status = "' + el.status + '" name="check" id="' + el.mongoId + '" value="' + el.id + '" data-name="'+ el.mongoName +'" data-name="'+ el.mongoName +'">' +
                '    </div>' +
                '  </td>' +
                '  <td>' +
                '	<div style="width:100%;overflow:auto; white-space:nowrap; text-overflow:ellipsis;">' +
                '    <div class="title"  style="float:left;">' + fn + '</div>' +
                '    <div id="opr_' + el.id + '" class="hide winHide" style="float:right"><img onclick="downloadFile(\'' + el.mongoId + '\')" class="hand" title="下载" src="' + GLOBAL_CONTEXTPATH + '/assets/images/download.png"/>' +
                delSt +
                '		<img class="hand ml8" onclick="checkFile(' + el.id + ')" title="检测" src="' + GLOBAL_CONTEXTPATH + '/assets/images/check.png"/></div>' +
                '	</div>' +
                '  </td>' +
                '  <td class="tc winTc">' +
                '	 <div>' + iu + '</div>' +
                '  </td>' +
                '  <td class="">' +
                '	 <div>' + fs + '</div>' +
                '  </td>' +
                '  <td class="tc">' +
                '	 <div>' + (el.ip == null ? "--" : el.ip) + '</div>' +
                '  </td>' +
                '  <td class="tc">' +
                '	 <div>' + el.createDate + '</div>' +
                '  </td>' +
                '</tr>';
        }
        tbHtml += '</tbody>' +
            '</table>' +
            '<div class="clearfix"></div>';
        return tbHtml;
    }

    function fileTypeThumb(rec) {
        var img = "", size = "";
        switch (rec.suffix.toLowerCase()) {
            case "jpg":
            case "jpeg":
                img = "jpg";
                break;
            case "png":
                img = "png";
                break;
            case "gif":
                img = "gif";
                break;
            case "zip":
                img = "zip";
                break;
            case "rar":
                img = "rar";
                break;
            case "flv":
                img = "mp4";
                break;
            case "xls":
            case "csv":
                img = "xls";
                break;
            case "xlsx":
                img = "xlsx";
                break;
            case "ppt":
                img = "ppt";
                break;
            case "html":
                img = "html";
                break;
            case "txt":
                img = "txt";
                break;
            case "pdf":
                img = "pdf";
                break;
            case "mp3":
                img = "mp3";
                break;
            case "wma":
                img = "wma";
                break;
            case "doc":
                img = "doc";
                break;
            case "docx":
                img = "docx";
                break;
            default:
                var img = "null";
        }
        if (file_manage.showStatus == 1) {
            size = "_32";
        }
        return GLOBAL_CONTEXTPATH + "/assets/images/files/" + img + size + ".png";
    }

    //
    function FileName(rec) {
        // debugger
        var filename = "";
        var img = fileTypeThumb(rec);
        var name = rec.fileName + '.' + rec.suffix;
        var sbName = name;
        if (name.length > 50) {
            sbName = name.substring(0, 20) + " ... " + name.substring(name.length - 20, name.length);
        }
        var filePath = ( rec.mongoId.indexOf(".") != -1 ?GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH ) + rec.mongoId;
        if (rec.suffix.toLowerCase() == "jpg" || rec.suffix.toLowerCase() == "png" || rec.suffix.toLowerCase() == "gif" || rec.suffix.toLowerCase() == "jpeg" || rec.suffix.toLowerCase() == "txt" || rec.suffix.toLowerCase() == "xml" || rec.suffix.toLowerCase() == "html") {
            filename = '<div title="' + name + '"><img height="32" width="32" src="' + img + '"/> <a target="_blank" href="' + filePath + '"><u style="color:">' + sbName + '</u></a></div>';
        } else if (rec.suffix.toLowerCase() == "flv") {
            filename = '<div title="' + name + '"><img height="32" width="32" src="' + img + '"/> <a href="javascript:" onclick="palyVideo(' + rec.id + ')"><u style="color:">' + sbName + '</u></a></div>';
        } else {
            filename = '<div title="' + name + '"><img height="32" width="32" src="' + img + '"/> <a href="javascript:" style="cursor:default">' + sbName + '</a></div>';
        }
        return filename;
    }

    function SizeTran(e) {
        var size = "0KB";
        var rec = e.fileSize;
        if (rec > 1024 * 1024 * 1024) {
            size = (rec / (1024 * 1024 * 1024)).toFixed(2) + "G";
        } else if (rec > 1024 * 1024) {
            size = (rec / (1024 * 1024)).toFixed(2) + "M";
        } else if (rec > 1024) {
            size = (rec / 1024).toFixed(2) + "KB"
        } else {
            size = rec + "B"
        }
        return size;
    }

    function IsUsed(e) {
        var isUsed = "否";
        var rec = e.status;
        if (rec == 1) isUsed = "<font color='#FA0000'>是</font>";
        return isUsed;
    }

    //datagrid 模板
    var doc_list_template = Ls.compile(
        '<table class="table table-hover doc-list">' +
        '<thead>' +
        '<tr>' +
        '  <th class="w30">' +
        '    <input type="checkbox" id="checkAll" onclick="checkAll()">' +
        '  </th>' +
        '  <th class="title">文件名</th>' +
        '  <th class="w110 tc">文件大小</th>' +
        '  <th class="w120 tc">上传IP</th>' +
        '  <th class="w180 tc">上传时间</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <td>' +
        '    <div class="">' +
        '      <input type="checkbox" name="check" value="<?=el.id?>">' +
        '    </div>' +
        '  </td>' +
        '  <td>' +
        '    <div class=""><?=el.fileName?>.<?=el.suffix?></div>' +
        '  </td>' +
        '  <td class="tc">' +
        '	 <div><?=el.fileSize?></div>' +
        '  </td>' +
        '  <td class="tc">' +
        '	 <div><?=el.ip?></div>' +
        '  </td>' +
        '  <td class="tc">' +
        '	 <div><?=el.createDate?></div>' +
        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>'
        //'<div id="pagination" class="pagination pull-right mr22"></div>' +
    );

    var init = function () {
        mini.parse();
        getData();
    };

    return {
        init: init,
        getData: getData
    };

}();