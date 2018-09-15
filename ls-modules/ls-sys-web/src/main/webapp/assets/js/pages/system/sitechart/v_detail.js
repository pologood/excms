var cur = {
    pageSize: 20,
    pageIndex: 0,
    dataFlag: 1
};
var v_detail = function () {

    //拉取数据
    function getData() {
        return Ls.ajaxGet({
            url: "/siteChartMain/getVisitDetail",
            data: cur
        }).done(function (d) {

            var listHtml = visit_detail_template(d);
            $("#doc_list_body").html(listHtml);

        }).done(function (d) {
            cur.pageIndex = d.pageIndex;
            //Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
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
                sizeList: [10, 20, 50]
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

    //datagrid 模板
    var visit_detail_template = Ls.compile(
        '<table class="table table-hover doc-list">' +
        '<thead>' +
        '<tr>' +
        '  <th>受访页面标题</th>' +
        '  <th class="w320">来源页面地址</th>' +
        '  <th class="w170 tc" >浏览时间</th>' +
        '  <th class="w140">IP地址</th>' +
        '  <th class="w160">IP所属地区</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <td>' +
        '	 <div><a title="<?=el.url?>" target="_blank" href="<?=el.url?>" class="text-primary"><?=(el.title).replace(/<[^>]+>/g,"")?></a></div>' +
        '  </td>' +
        '  <td>' +
        '    <div> ' +
        '		<?if(el.referer==null){?><?=el.sourceType?><?}?>' +
        '		<?if(el.referer!=null){?><a title="<?=el.referer?>" target="_blank" href="<?=el.referer?>"><font color="blue"><?if(el.searchEngine!=null&&el.searchKey!=null){?><?=Ls.cutstr(el.searchKey,10)?><?}?><?if(el.searchEngine==null||el.searchKey==null){?><?=Ls.cutstr(el.referer,20)?><?}?></a></font><?}?>' +
        '		<?if(el.searchEngine!=null){?>(<?=el.searchEngine?>)<?}?>' +
        '     </div>' +
        '  </td>' +
        '  <td>' +
        '	<div class="tc"><?=el.createDate?></div>' +
        '  </td>' +
        '  <td>' +
        '	 <div><?=el.ip?></div>' +
        '  </td>' +
        '  <td>' +
        '	 <div><?=el.country?>-<?=el.province?>-<?=el.city?></div>' +
        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="pagination" class="pagination pull-right mr22"></div></br>'
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