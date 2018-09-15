var cur = {
    ztree: '',
    columnId: '',
    pageIndex: 0,
    pageSize: 5,
    key: "",
    isPublish: "",
    dealStatus: "",
    purposeCode: '',
    categoryCode: '',
    c: 0
};
var petition_manage = function () {

    //拉取数据
    function getData(pageIndex, columnId, key, isPublish, dealStatus, purposeCode, categoryCode) {
        //var title=$("#searchKey").val();
        var pageSize = cur.pageSize;
        var columnId = cur.columnId;
        //var organId=cur.organId;
        return Ls.ajaxGet({
            url: "/onlinePetition/getPage",
            data: {
                //附加请求数据
                //pageIndex:0,pageSize:15,dataFlag:1,columnId:columnId,organId:organId
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                title: key,
                purposeCode: purposeCode,
                categoryCode: categoryCode,
                isPublish: isPublish,
                dealStatus: dealStatus
            }
        }).done(function (d) {
            var listHtml = guestbook_list_template(d);
            $("#guestbook_list_body").html(listHtml);

        }).done(function (d) {
             Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
        })

    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.columnId, cur.key, cur.isPublish, cur.dealStatus, cur.purposeCode, cur.categoryCode);
        cur.pageIndex = page_index;
        return false;
    }

    //提交数据
    function addPost() {
        var data = Ls.toJSON(cur.vm.$model);
        var organId = data.organId;
        var url = "/organ/updateOrgan";
        if (organId == null) {
            url = "/organ/saveOrgan"
        }
        Ls.ajax({
            url: url,
            data: data
        }).done(function (d) {

        })
        return false;
    }


    var guestbook_list_template = Ls.compile(
        '<table class="table guestbook-list2">' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row" class="w40">' +
        '    <input type="checkbox" name="check" value="<?=el.id?>" >' +
        '  </th>' +
        '  <td>' +
        '    <div class="message-title">' +
        '      <a onclick="showVO(\'<?=el.id||""?>\')"><?=el.title?></a>' +
        '    </div>' +
        '    <div class="message-attr">' +
        '      <?=el.author?>&nbsp;&nbsp;&nbsp;<?=el.createDate?>&nbsp;&nbsp;&nbsp;[来自：<?=el.ip?>]' +
        '    </div>' +
        '    <div class="message-body">' +
        '        <?==el.content?>' +
        '    </div>' +
        '   <?if(el.attachId!=null){?>' +
        '        <div class="message-attach">' +
        '           <img src="<?=GLOBAL_CONTEXTPATH?>/assets/images/attachment.gif" /> <a onclick="downAttach(\'<?=el.attachId||""?>\')">附件下载：<?=el.attachName?></a></>' +
        '        </div>' +
        '   <?}?>' +
        '   <?if(el.replyContent!=null){?>' +
        '     <div class="message-reply">' +
        '        <div class="reply_user"><?=el.replyUserName?>&nbsp;&nbsp;&nbsp;<?=el.replyDate?></div>' +
        '        <div class="reply_content">' +
        '         <?==el.replyContent?>' +
        '        </div>' +
        '     </div>' +
        '   <?}?>' +

        '    <div class="message-bottom">' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '          <li><button type="button" class="btn btn-default btn-xs <? if(el.isPublish==1){?> green-meadow<?}?> <? if(el.isPublish==2){?>disabled green<?}?>" id="p_<?=el.id?>" onclick="changePublish(<?=el.id?>)">发布</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick=doReply(<?=el.petitionId?>,<?=el.replyId||\'""\'?>)>办理</button></li>' +
        '   <?if(el.recType==0){?>' +
        '          <li><button type="button" class="btn btn-default btn-xs"  <? if(el.isPublish==1){?>disabled="true"<?}?> onclick="transfer(<?=el.petitionId?>)">转办</button></li>' +
        '   <?}?>' +
        '          <li><button type="button" class="btn btn-default btn-xs" id="p_<?=el.contentId?>" onclick="deleteVO(<?=el.id?>)">删除</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="record(<?=el.petitionId?>)">转办记录</button></li>' +
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
        getData(cur.pageIndex, content_mgr.indicatorId, '', '', '', '', '');
    };

    return {
        init: init,
        getData: getData
    };

}();