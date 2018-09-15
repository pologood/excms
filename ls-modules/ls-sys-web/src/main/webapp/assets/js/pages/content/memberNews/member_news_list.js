var member_manage = function () {

    var orderIDS, doc_list_body;

    var getOrderID = function (ui, attr) {
        return ui.item.parent().children().map(function () {
            var num = $(this).data(attr);
            return num;
        }).get().join(',');
    }

    //更新列表排序号
    var updateNum = function (docIDS, numIDS, ui) {
        Ls.ajax({
            url: "/content/updateNums",
            beforeSend: function () {
                doc_list_body.sortable("disable");
            },
            data: {
                ids: docIDS,
                nums: numIDS,
                columnId: cur.columnId
            }
        }).done(function (d) {
            if (d.status) {
                Ls.tipsOk("排序成功！");
            } else {
                Ls.tipsErr(d.desc);
            }
        }).always(function () {
            doc_list_body.sortable("enable");
            Ls.log("docIDS>>", docIDS)
            var ids = docIDS.toString().split(","),
                nums = numIDS.toString().split(",");
            for (var i = 0, l = ids.length; i < l; i++) {
                $("#doc_list_body").find('tr[data-id="' + ids[i] + '"]').attr("data-no", nums[i]);
            }
        })
    }

    //拉取数据
    function getData(pageIndex, key, condition, status) {
        var pageSize = cur.pageSize;
        return Ls.ajaxGet({
            url: "/memberNews/getPage",
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                title: key,
                condition: condition,
                status: status,
                startTime: cur.startTime,
                endTime: cur.endTime,
                resources: cur.resources,
                isMember:true
            }
        }).done(function (d) {
            console.info(d);
            //重置全选按钮
            $("#checkAll").data("checked", false);
            var data = d.data;
            var listHtml = Ls.template("member_list_template", d);
            doc_list_body = $("#doc_list_body").html(listHtml).sortable({
                items: '>tr',
                axis: "y",
                cursor: "move",
                handle: ".arrow_move",
                helper: function (e, ui) {
                    ui.children().each(function () {
                        var _this = $(this);
                        _this.width(_this.width());
                    });
                    return ui;
                },
                connectWith: ">tbody",
                forcePlaceholderSize: true,
                placeholder: 'must-have-class',
                start: function (e, ui) {
                    ui.placeholder.html('<td colspan="5"></td>');
                    !orderIDS && (orderIDS = getOrderID(ui, "no"));
                },
                update: function (e, ui) {
                    docIDS = getOrderID(ui, "id");
                    updateNum(docIDS, orderIDS);
                }
            });
            $(".publishBtn").attr("disabled", false).removeClass("disabled");
            $("#publishs:not(.disabled)").off().on('click', function () {
                publishs(1);
            });

            $("#unpublishs:not(.disabled)").off().on('click', function () {
                publishs(0);
            });

            $("#batchCopy_news:not(.disabled)").off().on('click', function () {
                batchCopy_news(false);
            });

            $("#batchCopyAndPublish_news:not(.disabled)").off().on('click', function () {
                batchCopy_news(true);
            });

            $("#batchUse_news:not(.disabled)").off().on('click', function () {
                batchUse_news(false);
            });

            $("#batchMoveAndPublish_news:not(.disabled)").off().on('click', function () {
                batchMove_news(true);
            });

            //批量删除
            $("#deletes:not(.disabled)").off().on('click', function () {
                var check = [];
                $("input[name='check']:checked").each(function (i, val) {
                    check.push(val.value);
                });

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
                                Ls.tipsOk("删除成功!")
                                // content_mgr.param.pageIndex = 0;
                                getList();
                            } else {
                                Ls.tipsErr(text.desc);
                            }

                        }
                    });
                }


            });
            var userName = GLOBAL_PERSON.name;
            if (GLOBAL_RIGHTS == 'normal') {
                //if (data != null && data != "") {
                //    for (var i = 0, l = data.length; i < l; i++) {
                //        if (userName != data[i].author) {
                //            $(".nots" + data[i].id).attr("disabled", true).addClass("disabled");
                //        } else {
                //            $(".nots" + data[i].id).attr("disabled", false).removeClass("disabled");
                //        }
                //    }
                //}
            }
        }).done(function (d) {
            cur.pageIndex = d.pageIndex;
            // Ls.pagination2({
            //     total: d.total,
            //     isTotal: true,
            //     pageCount: d.pageCount,
            //     pageIndex: d.pageIndex,
            //     callback: pageselectCallback
            // });

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

    function pageselectCallback(page_index, page_size) {
        cur.pageIndex = page_index;
        page_size && (cur.pageSize = page_size);
        orderIDS = "";
        getList();
        return false;
    }

    //datagrid 模板
    var init = function () {
        //加载列表
        getList();
    };


    return {
        init: init,
        getData: getData
    };

}();