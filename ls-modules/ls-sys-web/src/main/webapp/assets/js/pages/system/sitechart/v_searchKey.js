var cur = {
    pageSize: 15,
    pageIndex: 0,
    dataFlag: 1
};
var v_searchKey = function () {

    //拉取数据
    function getData(st, ed, searchKey) {
        cur.st = st;
        cur.ed = ed;
        cur.key = searchKey;
        return Ls.ajaxGet({
            url: "/visitSource/getSearchKeyPage",
            data: cur
        }).done(function (d) {

            var listHtml = visit_detail_template(d);
            $("#doc_list_body").html(listHtml);

        }).done(function (d) {
            cur.pageIndex = d.pageIndex;
            // Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
            var pIndex = cur.pageIndex;
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
                sizeList: [15, 25, 55]
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
        var st=$("#st").val();
        var ed=$("#ed").val();
        var key=$("#key").val();
        getData();
        return false;
    }

    //datagrid 模板
    var visit_detail_template = Ls.compile(
        '<table class="table table-hover doc-list">' +
        '<thead>' +
        '<tr>' +
        '  <th class="" ><div class="" style="width:30%;margin-left:20px">搜索关键词</div></th>' +
        '  <th style="width:12%">来访次数</th>' +
        '  <th style="width:12%">百度</th>' +
        '  <th style="width:12%">360搜索</th>' +
        '  <th style="width:12%">搜狗</th>' +
        '  <th style="width:12%">谷歌</th>' +
        '  <th style="width:12%">其他</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <td>' +
        '	<div class=""  style="margin-left:20px"><?=el.keyWord?></div>' +
        '  </td>' +
        '  <td>' +
        '	<div class=""><?=el.count?></div>' +
        '  </td>' +
        '  <td>' +
        '	<div class=""><?=el.baidu?></div>' +
        '  </td>' +
        '  <td>' +
        '	<div class=""><?=el.so?></div>' +
        '  </td>' +
        '  <td>' +
        '	<div class=""><?=el.soso?></div>' +
        '  </td>' +
        '  <td>' +
        '	<div class=""><?=el.google?></div>' +
        '  </td>' +
        '  <td>' +
        '	<div class=""><?=el.other?></div>' +
        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="pagination" class="pagination pull-right mr22"></div></br>'
    );

    var init = function () {
        mini.parse();
        getData(null, null, null);
    };

    return {
        init: init,
        getData: getData
    };

}();