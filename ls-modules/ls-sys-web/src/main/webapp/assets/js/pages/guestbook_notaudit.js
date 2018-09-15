var cur = {
    ztree: '',
    //columnId: '',
    pageIndex: 0,
    pageSize: 5,
    key: "",
    condition: "",
    status: "",
    c: 0
};
var guestbook_manage = function () {

    //拉取数据
    function getData(pageIndex, key, condition, status) {
        //var title=$("#searchKey").val();
        var pageSize = cur.pageSize;
        //var columnId = cur.columnId;
        //var organId=cur.organId;
        return Ls.ajaxGet({
            url: "/noAudit/getNoAuditPage",
            data: {
                //附加请求数据
                //pageIndex:0,pageSize:15,dataFlag:1,columnId:columnId,organId:organId
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                //columnId: columnId,
                title: key,
                condition: condition,
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
        getData(page_index, cur.key, cur.condition, cur.status);
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

    //datagrid 模板
    var guestbook_list_template2 = Ls.compile(
        '<table class="table guestbook-list">' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row" class="w40">' +
        '    <input type="checkbox" name="check" value="<?=el.base_content_id?>" >' +
        '  </th>' +
        '  <td>' +
        '    <div class="message-top">' +
        '      <span class="pull-left"><?=el.person_name?>&nbsp;&nbsp;&nbsp;<?=el.create_date?>&nbsp;&nbsp;&nbsp;[来自：<?=el.person_ip?>]</span>' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="modify(<?=el.id?>)">修改</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs <? if(el.is_publish==1){?> green-meadow<?}?>" id="p_<?=el.base_content_id?>" onclick="changePublish(<?=el.base_content_id?>)">发布</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="reply(<?=el.id?>)">回复</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs"  <? if(el.is_publish==1){?>disabled="true"<?}?> onclick="forwardById(<?=el.id?>)">转办</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" id="p_<?=el.base_content_id?>" onclick="deleteById(<?=el.base_content_id?>)">删除</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="record(<?=el.id?>)">转办记录</button></li>' +
        '        </ol>' +
        '      </span>' +
        '     <div class="clearfix"></div>' +
        '    </div>' +
        '    <div class="message-body">' +
        '      <div class="title">标题：<?=el.title?>&nbsp;&nbsp;[来自：<?=el.name?>]</div>' +
        '      <div class="body">' +
        '        <?=el.guestbook_content?>' +
        '      </div>' +
        '    </div>' +
        '    <?if(el.response_content!=null){?><div class="message-bottom">' +
        '      <div class="reply">' +
        '         <div class="reply_user"><?=el.user_name?>&nbsp;&nbsp;&nbsp;<?=el.reply_date?></div>' +
        '         <div class="reply_content">' +
        '          <?=el.response_content?>' +
        '         </div>' +
        '      </div>' +
        '    </div><?}?>' +
        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="pagination" class="pagination pull-right"></div>' +
        '<div class="clearfix"></div>'
    );

    var guestbook_list_template = Ls.compile(
        '<table class="table guestbook-list2">' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row" class="w40">' +
        '    <input type="checkbox" name="check" value="<?=el.base_content_id?>" >' +
        '  </th>' +
        '  <td>' +
        '    <div class="message-title">' +
        '      <?=el.title?>&nbsp;&nbsp;[来自：<?=el.name?>]' +
        '    </div>' +
        '    <div class="message-attr">' +
        '      <?=el.person_name?>&nbsp;&nbsp;&nbsp;<?=el.create_date?>&nbsp;&nbsp;&nbsp;[来自：<?=el.person_ip?>]' +
        '    </div>' +
        '    <div class="message-body">' +
        '        <?=el.guestbook_content?>' +
        '    </div>' +

        '   <?if(el.response_content!=null){?>' +
        '     <div class="message-reply">' +
        '        <div class="reply_user"><?=el.user_name?>&nbsp;&nbsp;&nbsp;<?=el.reply_date?></div>' +
        '        <div class="reply_content">' +
        '         <?=el.response_content?>' +
        '        </div>' +
        '     </div>' +
        '   <?}?>' +

        '    <div class="message-bottom">' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '		   <li><button type="button" class="btn btn-default btn-xs" onclick="modify(<?=el.id?>)">修改</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs <? if(el.is_publish==1){?> green-meadow<?}?>" id="p_<?=el.base_content_id?>" onclick="changePublish(<?=el.base_content_id?>)">发布</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="reply(<?=el.id?>)">回复</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs"  <? if(el.is_publish==1){?>disabled="true"<?}?> onclick="forwardById(<?=el.id?>)">转办</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" id="p_<?=el.base_content_id?>" onclick="deleteById(<?=el.base_content_id?>)">删除</button></li>' +
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
        //cur.columnId = content_mgr.indicatorId;
        //getData(0, content_mgr.indicatorId);
        getData(cur.pageIndex, "", '', '');
    };

    return {
        init: init,
        getData: getData
    };

}();