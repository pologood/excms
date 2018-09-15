var doc_manage = function () {

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
                Ls.tipsOk("排序完成，正在生成处理中");
            } else {
                Ls.tipsErr(d.desc);
            }
        }).always(function () {
            doc_list_body.sortable("enable");
            var ids = docIDS.toString().split(","),
                nums = numIDS.toString().split(",");
            for (var i = 0, l = ids.length; i < l; i++) {
                $("#doc_list_body").find('tr[data-id="' + ids[i] + '"]').attr("data-no", nums[i]);
            }
        })
    }

    //拉取数据
    function getData(pageIndex, columnId, key, condition, status) {
        if (Ls.isEmpty(cur.processConfig)) {
            cur.processConfig = getProcessConfig(content_mgr.node.indicatorId);
        }

        var isSelf = 0;

        var url = "/content/getPage";
        //通过isParent来判断查询路径
        if (isParent == "1") {
            url = "/content/getPageByParentId";
        }
        var pageSize = cur.pageSize;
        return Ls.ajax({
            url: url,
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                columnIds : cur.columnName,
                resources:cur.resources,
                author:cur.author,
                title:key,
                // condition: condition,
                // status: status,
                conditionMap:cur.conditionMap,
                startTime: cur.startTime,
                endTime: cur.endTime,
                isSelf: isSelf
            }
        }).done(function (d) {

            //重置全选按钮
            $("#checkAll").data("checked", false);

            //向列表数据加入流程信息
            var data = d.data;
            if (!Ls.isEmpty(cur.processConfig.processId)) {
                for (var i = 0, l = data.length; i < l; i++) {
                    data[i].processId = cur.processConfig.processId;
                    data[i].processName = cur.processConfig.processName;
                }
            }
            d.hasPublish = hasPublish;
            d.hasEdit = hasEdit;
            var listHtml = Ls.template("doc_list_template"+content_mgr.indicatorId, d);

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

            //权限控制
            // if (cur.opt.length > 0) {
            //     for (var i = 0; i < cur.opt.length; i++) {
            //         if (cur.opt[i] == "publish" || cur.opt[i] == "super") {
            //             $(".publishBtn").attr("disabled", false).removeClass("disabled");
            //         }
            //         if (cur.opt[i] == "edit" || cur.opt[i] == "super") {
            //             $(".editBtn").attr("disabled", false).removeClass("disabled");
            //             $(".editBtn1").attr("disabled", false).removeClass("disabled");
            //             $(".editBtn2").attr("disabled", false).removeClass("disabled");
            //         }
            //         if (cur.opt[i] == "super") {
            //             $(".publishBtn1").attr("disabled", false).removeClass("disabled");
            //         }
            //     }
            // }

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

            $("#batchMove_news:not(.disabled)").off().on('click', function () {
                batchMove_news(false);
            });

            $("#batchMoveAndPublish_news:not(.disabled)").off().on('click', function () {
                batchMove_news(true);
            });

            //批量删除
            $("#deletes:not(.disabled)").off().on('click', function () {
                var check = [];


                var referedFlag = false;
                $("input[name='check']:checked").each(function (i, val) {
                    check.push(val.value);
                    var referedNews = $("#delBtn"+val.value).attr("isRefered");
                    if(referedNews==true||referedNews=="true"){
                        referedFlag = true;
                    }
                });

                if (check.length <= 0) {
                    Ls.tipsInfo("至少选择一项");
                    return;
                }
                var msg = "确定删除选中记录？";
                if(referedFlag){
                    msg = "选中记录中含有被引用的新闻，所有引用后的新闻将同步删除，是否确认删除？"
                }
                if (confirm(msg)) {
                    Ls.ajaxGet({
                        data: {
                            ids: check
                        },
                        url: "/articleNews/delete",
                        success: function (text) {
                            if (text.status) {
                                Ls.tipsOk("批量删除完成，正在生成处理中")
                                // content_mgr.param.pageIndex = 0;
                                getList();
                            } else {
                                Ls.tipsErr(text.desc);
                            }

                        }
                    });
                }
            });

            $(".pubDetails").on('click', function () {
                var me = $(this),
                    id = me.data("id");
                if (id) {
                    Ls.openWin('/content/opt/record/index?contentId=' + id, "1000px", "400px", {
                        id: "pub_details",
                        title: "发布详情"
                    })
                }
            })

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

            // 清除栏目名称
            /*$('#columnName').val('');
            cur.columnName = '';
            cur.treeObj.checkAllNodes(false);*/
        });
    };

    function pageselectCallback(page_index, page_size) {
        cur.pageIndex = page_index;
        page_size && (cur.pageSize = page_size);
        orderIDS = "";
        getList();
        return false;
    }

    //加载栏目
    function addColumn() {
        Ls.ajaxGet({
            url: "/content/getColumnMap",
            async:false,
            data: {
                columnId: cur.columnId
            }
        }).done(function (columnMap) {
            var data = columnMap.data;

            var setting = {
                check: {
                    enable: true
                },
                data: {
                    key: {
                        title: "name"
                    },
                    simpleData: {
                        enable: true
                    }
                },
                callback: {
                    onClick: zTreeOnClick,
                    onCheck: zTreeOnClick
                }
            };

            cur.treeObj = $.fn.zTree.init($("#treeDemo_ser"), setting, data);

            function zTreeOnClick(event, treeId, treeNode) {
                var str = '', nameIds = '';
                if (event.type === 'click') {
                    !treeNode.checked ? cur.treeObj.checkNode(treeNode, true) : cur.treeObj.checkNode(treeNode, false);
                } else {
                    treeNode.checked ? cur.treeObj.checkNode(treeNode, true) : cur.treeObj.checkNode(treeNode, false);
                }

                var nodes = cur.treeObj.getCheckedNodes(true);

                $.each(nodes, function(i, v){
                    str += v.name + ',';
                    nameIds += v.value + ',';
                })

                $('#columnName').val(str.substring(0, str.length - 1));
                cur.columnName = nameIds.substring(0, nameIds.length - 1);

            };


            /*var obj = columnMap.data;
            $('#columnName').attr('value', obj[0].value);

            $('#columnName').selectPage({
                showField : 'name',
                keyField : 'value',
                orderBy : ['value desc'],
                data : obj,
                listSize : 15,
                selectOnly : true,
                pagination : false,
                multiple : true,
                eSelect : function(data){
                    $("#columnName").trigger("hidemsg");
                }
            });
            cur.columnName = obj[0].value*/;
            // $("#columnName").empty();
            // var obj = columnMap.data;
            // for (var i = 0 ; i < obj.length ; i++) {
            //     $("#columnName").append("<option value='" + obj[i].value + "'>" + obj[i].name + "</option>");
            //
            //     //栏目名称
            //     if (i == 0) {
            //         cur.columnName = obj[i].value;
            //     }
            // }
        });
    }

    //datagrid 模板
    var init = function () {
        cur.columnId = content_mgr.node.indicatorId;
        cur.power = content_mgr.node.opt;
        cur.opt = [];

        //加载栏目
        addColumn();
        // if (cur.power == "super") {
        //     cur.opt.push("super");
        // } else {
        //     cur.functions = content_mgr.node.functions;
        //     for (var i = 0; i < cur.functions.length; i++) {
        //         cur.opt.push(cur.functions[i].action);
        //     }
        // }
        //流程配置
        cur.processConfig = getProcessConfig(content_mgr.node.indicatorId);

        //加载列表
        getList();
    };


    return {
        init: init,
        getData: getData
    };

}();