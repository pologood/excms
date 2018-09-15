/** 复制引用新闻 */
var copyRefer_mgr = function () {
    // 公共属性
    var setting = {
        view: {
            nameIsHTML: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast"
        },
        check: {
            chkStyle:'radio',
            enable: true
        },
        data: {
            simpleData: {
                enable: true// 简单模式
            }
        },
        callback: {
            onClick: function (event, treeId, node) {
                var tree = $.fn.zTree.getZTreeObj(treeId);
                if (node.nocheck) {
                    tree.expandNode(node, !node.open, false, true, true);
                    tree.cancelSelectedNode(node);
                } else {
                    tree.checkNode(node, !node.checked);
                }
            }
        }
    };

    // 栏目树，跨站点，传入的siteId、columnId是当前操作的文章所属站点和栏目
    // 当为引用时，需要查询当前文章的引用关系
    var columnInit = function (treeId, columnType, siteId, columnId, contentId, type) {
        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                simpleData: {
                    idKey: 'indicatorId',
                    pIdKey: 'parentId'
                }
            },
            view: {
                addDiyDom: function (id, node) {
                    if (!node.isParent) {

                    }
                }
            }
        });
        // 加载树
        var tree = null;
        var json = Ls.ajax({
            async: false,// 同步
            url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/siteMain/getByColumnTypeCodes?columnTypeCode=" + cur.articleNews+","+cur.pictureNews+"&siteId="+siteId
        }).responseJSON;
        // 构造树
        if (json.status == 1) {// 成功
            if (json.data != null && json.data.length > 0) {
                var data = Ls.treeDataFilter(json.data, Ls.treeDataType.SITE);
                var checkedColumnStrId = [];
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    node.nocheck = true;
                    if (!node.isParent) {// 叶子节点
                        node.isHidden = true;


                            if ((node.columnTypeCode == "articleNews" || node.columnTypeCode == "pictureNews")) {// 图片新闻和文章新闻可以相互复制，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;

                            }

                    }
                    //新增文章页同步信息回显

                    //if (cur.bindColumnId==node.indicatorId) {
                    //    node.checked = true;
                    //    checkedColumnStrId.push(node.indicatorId);
                    //}
                }
                tree = $.fn.zTree.init($("#" + treeId), ztree_settings, data);

                //if (checkedColumnStrId.length > 0) {//展开选中节点的父节点
                //    for (var i = 0, l = checkedColumnStrId.length; i < l; i++) {
                //        var node = tree.getNodeByParam("indicatorId", checkedColumnStrId[i], null);
                //        tree.selectNode(node);
                //    }
                //}
                // App.initContentScroll(90);
            }
        }
        return tree;
    }




    return {
        columnInit: columnInit

    }
}();