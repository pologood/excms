var cur = {
    ztree: '',
    columnId: '',
    pageSize: 10,
    key: "",
    condition: "",
    status: "",
    pageIndex: 0,
    opt: []
};

var doc_manage = function () {

    //拉取数据
    function getData(pageIndex, columnId, key, condition, status) {
        var pageSize = cur.pageSize;
        return Ls.ajaxGet({
            url: "/content/getPage",
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                title: key,
                condition: condition,
                status: status
            }
        }).done(function (d) {

            //重置全选按钮
            $("#checkAll").data("checked", false);
            d.GLOBAL_FILESERVERNAMEPATH = GLOBAL_FILESERVERNAMEPATH;
            d.GLOBAL_FILESERVERPATH = GLOBAL_FILESERVERPATH;
            // var listHtml = picture_list_template(d);
            var listHtml = Ls.template("picture_list_template", d);

            $("#picture_list").html(listHtml);
            //权限控制
            if (cur.opt.length > 0) {
                for (var i = 0; i < cur.opt.length; i++) {
                    if (cur.opt[i] == "publish" || cur.opt[i] == "super") {
                        $(".publishBtn").attr("disabled", false);
                        $(".publishBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "edit" || cur.opt[i] == "super") {
                        $(".editBtn").attr("disabled", false);
                        $(".editBtn").removeClass("disabled");
                    }
                }
            }
            //
            $("#publishs:not(.disabled)").off().on('click', function () {
                publishs(1);
            });

            $("#batchMove_news:not(.disabled)").off().on('click', function () {
                batchMove_news();
            });

            $("#unpublishs:not(.disabled)").off().on('click', function () {
                publishs(0);
            });

            //批量删除
            $("#deletes:not(.disabled)").off().on('click', function () {
                var check = [];
                $("input[name='picture_checkbox']:checked").each(function (i, val) {
                    check.push(val.value);
                })
                if (check.length <= 0) {
                    Ls.tipsInfo("至少选择一项");
                    return;
                }
                if (confirm("确定删除选中记录？")) {
                    Ls.ajaxGet({
                        data: {
                            ids: check
                        },
                        url: "/articleNews/delete",
                        success: function (text) {
                            if (text.status) {
                                doc_manage.getData(0, cur.columnId, cur.key, cur.condition, cur.status);
                                Ls.tipsOk("批量删除完成，正在生成处理中");
                            } else{
                                Ls.tipsErr(text.desc);
                            }
                        }
                    });
                }

            });


        }).done(function (d) {
            cur.pageIndex = d.pageIndex;
            var pIndex = d.pageIndex;
            if (pIndex == 0) {
                pIndex = 1;
            } else {
                pIndex = pIndex + 1;
            }
            //Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
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
        getData(page_index, cur.columnId, cur.key, cur.condition, cur.status);
        cur.pageIndex = page_index-1;
        return false;
    }*/

    function pageselectCallback(page_index, page_size) {
        cur.pageIndex = page_index;
        page_size && (cur.pageSize = page_size);
        getData(page_index, cur.columnId, cur.key, cur.condition, cur.status);
        return false;
    }

    var init = function () {
        cur.columnId = content_mgr.indicatorId;
        cur.columnId = content_mgr.node.indicatorId;
        cur.power = content_mgr.node.opt;
        if (cur.power == "super") {
            cur.opt.push("super");
        } else {
            cur.functions = content_mgr.node.functions;
            for (var i = 0; i < cur.functions.length; i++) {
                cur.opt.push(cur.functions[i].action);
            }
        }
        getData(cur.pageIndex, content_mgr.indicatorId, "", '', '');
    };

    return {
        init: init,
        getData: getData
    };

}();