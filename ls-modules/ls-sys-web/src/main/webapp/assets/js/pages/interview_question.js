var cur = {
    indicatorId: content_mgr.indicatorId,
    interviewId: '',
    pageIndex: 0,
    pageSize: 10,
    key: "",
    condition: "",
    status: "",
    c: 0
};
var interview_manage = function () {

    //拉取数据
    function getData(pageIndex, interviewId, key, condition, status) {
        var pageSize = cur.pageSize;
        return Ls.ajaxGet({
            url: "/interviewQuestion/getPage",
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                interviewId: interviewId,
                title: key,
                status: status
            }
        }).done(function (d) {

            var listHtml = guestbook_list_template(d);
            $("#guestbook_list_body").html(listHtml);
            //doOpt();

        }).done(function (d) {
            Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
        })
    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.interviewId, cur.key, cur.condition, cur.status);
        cur.pageIndex = page_index;
        return false;
    }

    var guestbook_list_template = Ls.compile(
        '<table class="table guestbook-list2">' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row" class="w40">' +
        '    <input type="checkbox" name="check" value="<?=el.questionId?>" >' +
        '  </th>' +
        '  <td>' +
        '    <div class="message-title">' +
        '      <?=el.name?>&nbsp;&nbsp;&nbsp;<?=el.createDate?>&nbsp;&nbsp;&nbsp;[来自：<?=el.ip?>]' +
        '    </div>' +
        '    <div class="message-body">' +
        '        <?=el.content?>' +'<div class="question-pic"><img src="<?=el.questionPic?>"></div>'+
        '    </div>' +

        '   <?if(el.isReply == 1){?>' +
        '     <div class="message-reply">' +
        '        <div class="reply_user"><?=el.replyName?>&nbsp;&nbsp;&nbsp;<?=el.replyTime?></div>' +
        '        <div class="reply_content">' +
        '         <?==el.replyContent?>' +'<div class="reply-pic"><img src="<?=el.replyPic?>"></div>'+
        '        </div>' +
        '     </div>' +
        '   <?}?>' +
        '    <div class="message-bottom">' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '          <li><button type="button"  class="btn btn-default btn-xs editOpt" onclick="add(<?=el.questionId?>)">修改</button></li>' +
        '          <li><button type="button"  class="btn btn-default btn-xs publishOpt <? if(el.issued==1){?> green-meadow<?}?>" onclick="issued(<?=el.questionId?>,<?=el.issued?>)">发布</button></li>' +
        '          <li><button type="button"  class="btn btn-default btn-xs answerOpt <? if(el.isReply==1){?> green-meadow<?}?>" onclick="reply(<?=el.questionId?>)">回复</button></li>' +
        '          <li><button type="button"  class="btn btn-default btn-xs deleteOpt" id="p_<?=el.base_content_id?>" onclick="del(<?=el.questionId?>)">删除</button></li>' +
        '        </ol>' +
        '       </span>' +
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

    var init = function (interviewId) {
        //初始化布局
        mini.parse();
        cur.interviewId = interviewId;
        getData(cur.pageIndex, interviewId, "", '', '');
    };

    return {
        init: init,
        getData: getData
    };

}();