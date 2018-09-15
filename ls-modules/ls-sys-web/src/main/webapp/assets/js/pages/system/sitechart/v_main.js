var v_main = function () {

    //拉取数据
    function getData() {
        return Ls.ajaxGet({
            url: "/siteChartMain/getMainList",
        }).done(function (d) {

            var   listHtml = imgTemplate(d);
            $("#doc_list_body").html(listHtml);

        }).done(function (d) {

        });
    };

    //datagrid 模板
    var doc_list_template = Ls.compile(
        '<table class="table table-hover doc-list">' +
        '<thead>' +
        '<tr>' +
        '  <th class="title" style="width:100%">文件名</th>' +
        '  <th class="w90 tc"></th>' +
        '  <th class="w90 tc">上传IP</th>' +
        '  <th class="w230 tc">上传时间</th>' +
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