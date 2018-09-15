var cur = {
    ztree: '',
    columnId: '',
    pageIndex: 0,
    pageSize: 10,
    content: "",
    condition: "",
    status: "",
    c: 0
};
var guestbook_manage = function () {

    //拉取数据
    function getData(pageIndex, key, condition, status) {
        var pageSize = cur.pageSize;
        return Ls.ajaxGet({
            url: "/commentMgr/getCommentsByContentId",
            data: {
                contentId: cmm.contentId,
                //附加请求数据
                //pageIndex:0,pageSize:15,dataFlag:1,columnId:columnId,organId:organId
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                content: key,
                isPublish: status,
                status: status
            }
        }).done(function (d) {
            var listHtml = guestbook_list_template(d);
            $("#guestbook_list_body").html(listHtml);

        }).done(function (d) {
            Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
        })

    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.content, cur.condition, cur.status);
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
        '      <?=el.title?>' +
        '    </div>' +
        '    <div class="message-attr">' +
        '      <?=el.createUserName?>&nbsp;&nbsp;&nbsp;<?=el.createDate?>&nbsp;&nbsp;&nbsp;[来自：<?=el.ip?>]' +
        '    </div>' +
        '    <div class="message-body">' +
        '        <?=el.content?>' +
        '    </div>' +

        '    <div class="message-bottom">' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '          <li><button type="button" class="btn btn-default btn-xs <? if(el.isPublish==1){?> green-meadow<?}?>" id="p_<?=el.id?>" onclick="changePublish(<?=el.id?>)">发布</button></li>' +
        '          <li><button type="button"<? if(el.isPublish==1){?>disabled<?}?> class="btn btn-default btn-xs" id="p_<?=el.id?>" onclick="deleteById(<?=el.id?>)">删除</button></li>' +
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
        //getData(0, content_mgr.indicatorId);
        getData(cur.pageIndex, "", '', '');
    };

    return {
        init: init,
        getData: getData
    };

}();