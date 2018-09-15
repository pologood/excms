var survey_mgr = function () {

    //拉取数据
    function getData(pageIndex, columnId, title) {
        return Ls.ajaxGet({
            url: "/data/dataform.txt",
            data: {
                pageSize: 15,
                pageIndex: pageIndex,
                dataFlag: 1,
                columnId: columnId,
                title: title
            }
        }).done(function (d) {

            var listHtml = survey_list_template(d);
            $("#survey_list").html(listHtml);

        }).done(function () {
            survey_mgr.pageIndex = d.pageIndex;
            Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
        })
    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, survey_mgr.columnId, survey_mgr.key, survey_mgr.condition, survey_mgr.status);
        survey_mgr.pageIndex = page_index;
        //获取数据
        return false;
    }

    //datagrid 模板
    var survey_list_template = Ls.compile(
        '<table class="table table-hover doc-list">' +
        '<thead>' +
        '<tr>' +
        '  <th class="w50">排 序</th>' +
        '  <th class="w30">' +
        '    <input type="checkbox">' +
        '  </th>' +
        '  <th>标 题</th>' +
        '  <th class="w140">属 性</th>' +
        '  <th class="w90">操 作</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row">' +
        '    <div class="rows text-center">↑ ↓</div>' +
        '  </th>' +
        '  <td>' +
        '    <div class="title">' +
        '      <input type="checkbox">' +
        '    </div>' +
        '  </td>' +
        '  <td colspan="3">' +
        '    <div class="title rows">只要改变一些基本的标记，就能把按钮变成下拉菜单的开关。</div>' +
        '    <div class="attr l">' +
        '      <span class="w170">发布日期：2015-11-02</span>' +
        '      <span class="w120">发布人：管理员</span>' +
        '      <span>点击次数：1029</span>' +
        '    </div>' +
        '    <div class="r">' +
        '      <div class="l mr42">' +
        '        <button type="button" class="btn btn-default btn-xs green-meadow">发</button>' +
        '        <button type="button" class="btn btn-default btn-xs">顶</button>' +
        '        <button type="button" class="btn btn-default btn-xs blue">标</button>' +
        '        <button type="button" class="btn btn-default btn-xs blue">新</button>' +
        '      </div>' +
        '      <div class="input-group">' +
        '        <div class="btn-group">' +
        '          <button type="button" class="btn btn-default btn-xs">修 改</button>' +
        '          <button type="button" class="btn btn-default btn-xs dropdown-toggle" data-toggle="dropdown"' +
        '                  aria-haspopup="true" aria-expanded="false">' +
        '            <span class="caret"></span>' +
        '            <span class="sr-only"> 修 改 </span>' +
        '          </button>' +
        '          <ul class="dropdown-menu dropdown-menu-right">' +
        '            <li><a href="#"> 复 制 </a></li>' +
        '            <li><a href="#"> 分 享 </a></li>' +
        '            <li><a href="#"> 引 用 </a></li>' +
        '            <li role="separator" class="divider"></li>' +
        '            <li><a href="#"> 删 除 </a></li>' +
        '          </ul>' +
        '        </div>' +
        '      </div>' +
        '    </div>' +
        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="pagination" class="pagination pull-right"></div>'
    );

    var init = function () {

    }

    return {
        init: init
    }

}();

$(document).ready(function () {
    survey_mgr.init();
})