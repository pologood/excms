var v_lp = function () {

    //拉取数据
    function getData(cur) {
        return Ls.ajaxGet({
            url: "/visitSource/getLocationPage",
            data: cur
        }).done(function (d) {

            var listHtml = visit_detail_template(d);
            $("#locationPage").html(listHtml);

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
        getData(cur);
        return false;
    }*/

    function pageselectCallback(page_index, page_size) {
        cur.pageIndex = page_index;
        page_size && (cur.pageSize = page_size);
        getData(cur);
        return false;
    }

    //datagrid 模板
    var visit_detail_template = Ls.compile(
        '<table class="table table-hover doc-list">' +
        '<thead>' +
        '<tr>' +
        '  <th style="width:35%;"><div style="margin-left:30px">来访城市</div></th>' +
        '  <th style="width:15%;" align="right"><div style="text-align:right">浏览次数(PV)<div></th>' +
        '  <th style="width:15%;" align="left"><span class="spl"> | </span>占比</th>' +
        '  <th style="width:15%;"><div style="text-align:right">访问次数(SV)<div></th> ' +
        '  <th style="width:20%;" align="left"><span class="spl"> | </span>占比</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <td>' +
        '    <div style="margin-left:30px"> ' +
        '		<?=el.city?> - <?=el.province?> - <?=el.country?>' +
        '     </div>' +
        '  </td>' +
        '  <td align="right">' +
        '	 <div><?=el.pv?></div>' +
        '  </td>' +
        '  <td align="left"><span class="spl"> | </span><?=el.pvCent?>%</div></td>' +
        '  <td align="right">' +
        '	 <div><?=el.sv?></div>' +
        '  </td>' +
        '  <td><span class="spl"> | </span><?=el.svCent?><?if(el.svCent!=0){?>%<?}?></div>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="pagination" class="pagination pull-right mr22"></div></br>'
    );

    var init = function () {
        mini.parse();
        getData(cur);
    };

    return {
        init: init,
        getData: getData
    };

}();