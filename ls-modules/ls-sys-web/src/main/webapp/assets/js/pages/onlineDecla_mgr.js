var cur = {
    ztree: '',
    columnId: '',
    pageIndex: 0,
    pageSize: 5,
    key: "",
    dealStatus: "",
    isPublish: '',
    c: 0
};
var decla_manage = function () {

    //拉取数据
    function getData(pageIndex, columnId, key, isPublish, dealStatus) {
        //var title=$("#searchKey").val();
        var pageSize = cur.pageSize;
        var columnId = cur.columnId;
        //var organId=cur.organId;
        return Ls.ajaxGet({
            url: "/onlineDeclaration/getPage",
            data: {
                //附加请求数据
                //pageIndex:0,pageSize:15,dataFlag:1,columnId:columnId,organId:organId
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                title: key,
                isPublish: isPublish,
                dealStatus: dealStatus
            }
        }).done(function (d) {
            // var listHtml = declaration_list_template(d);
            var listHtml = Ls.template("onlinedecla_list_template", d);
            $("#declaration_list_body").html(listHtml);

        }).done(function (d) {
            Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
        })

    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.columnId, cur.key, cur.isPublish, cur.dealStatus);
        cur.pageIndex = page_index;
        return false;
    }


    var declaration_list_template = Ls.compile(
        '<table class="table guestbook-list2">' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row" class="w40">' +
        '    <input type="checkbox" name="check" value="<?=el.baseContentId?>" >' +
        '  </th>' +
        '  <td>' +
        '    <div class="message-title">' +
        '      <a onclick="showVO(\'<?=el.baseContentId||""?>\')"><?=el.title?></a>' +
        '    </div>' +
        '    <div class="message-attr">' +
        '      <?=el.personName?>&nbsp;&nbsp;&nbsp;<?=el.createDate?>&nbsp;&nbsp;&nbsp;' +
        '    </div>' +
        '    <div class="message-body">' +
        '        <?==el.factReason?>' +
        '    </div>' +
        '   <?if(el.attachId!=null){?>' +
        '        <div class="message-attach">' +
        '           <img src="<?=GLOBAL_CONTEXTPATH?>/assets/images/attachment.gif" /> <a onclick="downAttach(\'<?=el.attachId||""?>\')">附件下载：<?=el.attachName?></a></>' +
        '        </div>' +
        '   <?}?>' +
        '   <?if(el.replyContent!=null){?>' +
        '     <div class="message-reply">' +
        '        <div class="reply_user"><?=el.replyUnitName?>&nbsp;&nbsp;&nbsp;<?=el.replyDate?></div>' +
        '        <div class="reply_content">' +
        '         <?==el.replyContent?>' +
        '        </div>' +
        '     </div>' +
        '   <?}?>' +

        '    <div class="message-bottom">' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '          <li><button type="button" class="btn btn-default btn-xs <? if(el.isPublish==1){?> green-meadow<?}?>" id="p_<?=el.id?>" onclick="changePublish(<?=el.baseContentId?>)">发布</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="doReply(<?=el.id?>)">办理</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs"  <? if(el.isPublish==1){?>disabled="true"<?}?> onclick="transfer(<?=el.id?>)">转办</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" id="p_<?=el.baseContentId?>" onclick="deleteVO(<?=el.baseContentId?>)">删除</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="record(<?=el.id?>)">转办记录</button></li>' +
        '        </ol>' +
        '      </span>' +
        '     <div class="clearfix"></div>' +
        '    </div>' +


        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="pagination" class="pagination pull-right"></div>' +
        '<div class="clearfix"></div>'
    );

    var init = function () {
        //初始化布局
        mini.parse();
        cur.columnId = content_mgr.indicatorId;
        //getData(0, content_mgr.indicatorId);
        getData(cur.pageIndex, content_mgr.indicatorId, '', '', '');
    };

    return {
        init: init,
        getData: getData
    };

}();