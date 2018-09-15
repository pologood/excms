var doc_mgr = function () {

    var ctrl = "article_list", vm = {};

    var initData = {
        list: [],
        url: GLOBAL_HTMLFILEPATH,
        columnId: content_mgr.node.indicatorId,
        pageIndex: 0,
        key: "",
        status: "",
        condition: "",
        isEdit: true,
        isPublish: true,
        dropdown_text: "属性筛选",
        funAdd: function () {
            !vm.isEdit && content_mgr.getContentApp("/articleNews/articleNewsEdit?pageIndex=" + vm.pageIndex)
        },
        funEdit: function (e, id) {
            content_mgr.getContentApp("/articleNews/articleNewsEdit?id=" + id + "&pageIndex=" + vm.pageIndex)
        },
        funAttr: function (e, type, status) {
            var target = e.target;
            vm.condition = type;
            vm.status = status;
            vm.dropdown_text = target.innerHTML;
            getData();
        },
        funSearch: function () {
            getData()
        },
        funSearchClear: function () {
            vm.key = "";
            vm.status = "";
            vm.condition = "";
            vm.dropdown_text = "属性筛选";
            getData()
        },
        checkIds: [],
        funCheckAll: function (e) {
            vm.checkIds = $('input[name="checkIds"]').getCheckBoxArr(e.target.checked)
        },
        funDelete: function (e, id) {
            if (!vm.isEdit) {

                if (!id) {
                    checkIds = getCheckIds();
                } else {
                    checkIds = [id];
                }

                if (checkIds > 0) {
                    if (!confirm("确定删除选中记录？")) {
                        return;
                    }
                    Ls.ajaxGet({
                        data: {
                            ids: checkIds
                        },
                        url: "/articleNews/delete",
                        success: function (d) {
                            doc_manage.getData();
                        }
                    });
                } else {
                    Ls.tipsInfo("至少选择一项");
                }
            }
        },
        funPublish: function (e, id, type) {
            if (!vm.isPublish) {

                if (!id) {
                    checkIds = getCheckIds()
                } else {
                    type = type == 1 ? 0 : 1;
                    checkIds = [id];
                }

                if (checkIds.length > 0) {
                    Ls.ajaxGet({
                        data: {
                            ids: checkIds,
                            siteId: GLOBAL_SITEID,
                            columnId: vm.columnId,
                            type: type
                        },
                        url: "/articleNews/publishs",
                        success: function (d) {
                            doc_mgr.getData();
                        }
                    });
                } else {
                    Ls.tipsInfo("至少选择一项");
                }
            }
        },
        funTop: function (e, id) {
            Ls.ajaxGet({
                data: {
                    id: id
                },
                url: "/articleNews/changeTop",
                success: function (d) {
                    var isPublish = d.data;
                    Ls.tipsOk((isPublish == 1 ? "取消" : "") + "置顶成功");
                    doc_mgr.getData();
                }
            });
        },
        funTitle: function (e, id) {
            Ls.ajaxGet({
                data: {
                    id: id
                },
                url: "/articleNews/changeTitle",
                success: function (d) {
                    var isTitle = d.data;
                    Ls.tipsOk((isTitle == 1 ? "取消" : "") + "标题新闻成功");
                    doc_mgr.getData();
                }
            });
        },
        funNew: function (e, id) {
            Ls.ajaxGet({
                data: {
                    id: id
                },
                url: "/articleNews/changeNew",
                success: function (d) {
                    var isNew = d.data;
                    Ls.tipsOk((isNew == 1 ? "取消" : "") + "加新成功");
                    doc_mgr.getData();
                }
            });
        },
        funSort: function (e, id, type) {
        },
        funCopy: function (e, id) {

        }
    }

    function initAvalon() {
        doc_mgr.vm = vm = avalon.vmodels[ctrl];
        initData.$id = ctrl;

        if (!vm) {
            vm = avalon.define(initData);
        } else {
            !content_mgr.avalon && Ls.assignVM(vm, initData);
        }

        content_mgr.avalon = true;
        avalon.scan($("#" + ctrl).get(0), vm);
    }

    function getCheckIds(id, options) {
        var checkIds = Ls.toJSON(vm.checkIds);
        return checkIds;
    }

    //拉取数据
    function getData(pageIndex) {
        return Ls.ajaxGet({
            url: "/content/getPage",
            data: {
                pageIndex: vm.pageIndex,
                pageSize: 10,
                dataFlag: 1,
                columnId: vm.columnId,
                title: vm.key,
                condition: vm.condition,
                status: vm.status
            }
        }).done(function (d) {

                vm.list = d.data;
                vm.opt = content_mgr.node.opt;
                if (vm.opt == "super") {
                    vm.isEdit = false;
                    vm.isPublish = false;
                } else {

                    var rights = content_mgr.node.functions;
                    for (var i = 0, l = rights.length; i < l; i++) {
                        if (rights[i].action == "publish") {
                            vm.isEdit = false;
                        }
                        if (rights[i].action == "edit") {
                            vm.isEdit = false;
                        }
                    }
                }

            }
        ).done(function (d) {
            vm.pageIndex = d.pageIndex;
            Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
            initAvalon();
        });
    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.columnId, cur.key, cur.condition, cur.status);
        cur.pageIndex = page_index;
        return false;
    }


    var init = function () {

        //绑定组件
        initAvalon();

        //获取数据
        getData();
    };

    return {
        init: init,
        getData: getData
    };

}();
