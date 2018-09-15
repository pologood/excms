//@ sourceUrl=column_config.js
/** 复制引用新闻 */
var copyRefer_mgr = function () {
    // 公共属性
    var setting = {
        view: {
            nameIsHTML: true,
            selectedMulti: true,
            dblClickExpand: false,
            expandSpeed: "fast"
        },
        check: {
            enable: true
        },
        data: {
            simpleData: {
                enable: true// 简单模式
            }
        },
        callback: {
            // onClick:function (event, treeId, treeNode) {
            //     var tree = $.fn.zTree.getZTreeObj(treeId);
            //     //此处treeNode 为当前节点
            //     if(treeNode.isParent){
            //         getAllChildrenNodes(tree, treeNode);
            //     }else{
            //         tree.checkNode(treeNode, !treeNode.checked);
            //     }
            // }
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
    //递归，得到叶子节点的数据
    function getAllChildrenNodes(tree, treeNode){
        if (treeNode.isParent) {
            var childrenNodes = treeNode.children;
            if (childrenNodes) {
                var checked = !childrenNodes[0].checked;
                var isFirstLeaf = true;
                for (var i = 0; i < childrenNodes.length; i++) {
                    if(childrenNodes[i].isParent){
                        getAllChildrenNodes(tree, childrenNodes[i]);
                    }else{
                        if(isFirstLeaf){
                            checked = !childrenNodes[i].checked;
                            isFirstLeaf = false;
                        }
                        tree.checkNode(childrenNodes[i], checked);
                    }
                }
            }
        }
    }

    // 栏目树，跨站点，传入的siteId、columnId是当前操作的文章所属站点和栏目
    // parentCheck ： 父节点是否可选
    // 当为引用时，需要查询当前文章的引用关系
    var columnInit = function (treeId, siteId, parentCheck) {

        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                simpleData: {
                    enable: true,
                    idKey: 'indicatorId',
                    pIdKey: 'parentId'
                }
            },
            async: {
                enable: false,
                type: "GET",
                dataType: "JSON",
                url: "",
                otherParam: {},
                autoParam: ""
            },
            check:{
                enable:true,
                chkStyle:'checkbox'
            },
            view: {
                addDiyDom: function (id, node) {
                    //if (!node.isParent) {
                    //        var synColumnIds = cur.synColumnIds.split(",");
                    //        for (var i = 0; i < synColumnIds.length; i++) {
                    //            if (node.indicatorId == synColumnIds[i]) {
                    //                var zTree = jQuery.fn.zTree.getZTreeObj("unit_tree");
                    //                    $("#" + thisId).click();
                    //
                    //
                    //            }
                    //        }
                    //
                    //}
                }
            }
        });
        // 加载树
        var tree = null;
        var json = Ls.ajax({
            async: false,// 同步
            url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/siteMain/getAllColumn?siteId=" + cur.W.GLOBAL_SITEID
        }).responseJSON;
        // 构造树
        //debugger
        if (json.status == 1) {// 成功
            if (json.data != null && json.data.length > 0) {
                json.data.push({"indicatorId": cur.W.GLOBAL_SITEID,
                    "parentId": 1,
                    "name": cur.W.GLOBAL_SITENAME,
                    "type": 'CMS_Site',
                    "isParent": true,
                    "title": cur.W.GLOBAL_SITEID,
                    "icon": cur.W.GLOBAL_CONTEXTPATH + "/assets/images/site.png"})


                var data = Ls.treeDataFilter(json.data, Ls.treeDataType.SITE);
                var newData = [];
                var checkedColumnStrId = [];
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    //只显示设置为前台显示的节点
                    if (node.isShow != 1) continue;
                    //组合新的list
                    newData.push(node);
                    //如果参数中设置了父节点可选，则不做判断
                    if(parentCheck == true){
                        node.nocheck = false;
                    }else{
                        node.nocheck = true;
                        if (!node.isParent) {
                            node.nocheck = false;
                            node.isHidden = false;
                        }
                    }
                    //新增文章页同步信息回显
                    if (cur.synColumnIds.indexOf(node.indicatorId) >= 0) {
                        node.checked = true;
                        checkedColumnStrId.push(node.indicatorId);
                    }
                }
                tree = $.fn.zTree.init($("#" + treeId), ztree_settings, newData);
                if (checkedColumnStrId.length > 0) {//展开选中节点的父节点
                    for (var i = 0, l = checkedColumnStrId.length; i < l; i++) {
                        var node = tree.getNodeByParam("indicatorId", checkedColumnStrId[i], null);
                        tree.selectNode(node);
                        //选中所有父节点
                        var tId = node.parentTId;
                        while(true){
                            var parent = tree.getNodeByParam("tId", tId, null);
                            if(parent == undefined || parent == null){
                                break;
                            }
                            tree.checkNode(parent, true);
                            tId = parent.parentTId
                            if(tId == undefined || tId == null || tId == '' || tId == '1'){
                                break;
                            }
                        }
                    }
                }


                /*-_-_-_-_-_-_-_-_-_-_-*/

                cur.treeNodes = tree.transformToArray(tree.getNodes());

                $.each(cur.treeNodes, function (i, v) {
                    v.value = v.name; // 必要格式 or label
                })

                $( "#tags" ).autocomplete({
                    source: cur.treeNodes,
                    select: function (e, ui) {
                        var text = $(e.toElement).text();
                        $(this).val(ui.item.name);

                        var node = tree.getNodesByParam("indicatorId", ui.item.indicatorId, null)[0];
                        if (!node.isParent) {
                            node = node.getParentNode();
                        }

                        tree.expandAll();
                        setTimeout(function () {
                            tree.expandNode(node, true, false, true);
                        }, 500)

                        return false;
                    }
                }).data("ui-autocomplete")._renderItem = function (ul, item) {
                    var getPath = item.getPath(),
                        str = '';

                    $.each(getPath, function (i, v) {
                        str += v.name + '>'
                    })
                    str = str.substring(0, str.lastIndexOf('>'));

                    return $("<li>")
                        .append('<a href="javascript:void(0);">' + str + '</a>')
                        .appendTo(ul);
                };

            }
        }
        return tree;
    }
    // 信息公开树，不跨站点，传入当前操作的节点公开类型和目录id
    var checkedOrgan = [], selectedOrganId = [], selectedCatId = [];
    var publicInit = function (treeId, type, id, organId, parentCheck) {
        var tree = null;
        // 设置
        var ztree_settings = $.extend(true, {}, setting, {
            data: {
                simpleData: {
                    idKey: 'id',
                    pIdKey: 'parentId'
                },
                key: {
                    title: "name"
                }
            },
            view: {
                addDiyDom: function (id, node) {
                    //if (!node.isParent && node.type == type) {
                    //
                    //        var organCatId = node.organId + "_" + node.id;
                    //        var synOrganCatIds = cur.synOrganCatIds.split(",");
                    //        for (var i = 0; i < synOrganCatIds.length; i++) {
                    //            if (organCatId == synOrganCatIds[i]) {
                    //                if (synOrganIsPublishs[i] == "1") {
                    //                    $("#" + thisId).click();
                    //                }
                    //                break;
                    //            }
                    //        }
                    //
                    //
                    //}
                }
            },
            async: {
                enable: true,
                url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/public/catalog/getOrganCatalogTree",
                autoParam: ["id=parentId", "organId"],
                otherParam: ["catalog", "true", "all", "false"],
                dataFilter: function (treeId, parentNode, responseData) {
                    checkedOrgan = [], selectedOrganId = [], selectedCatId = [];//每次处理数据时置空，不然定位到节点会出错

                    for (var i = 0, l = responseData.length; i < l; i++) {
                        var node = responseData[i];
                        if (!node.isParent) {// 叶子节点
                            node.isHidden = true;
                            if (node.type == "DRIVING_PUBLIC") {// 类型必须一致，不能选择自己
                                node.nocheck = false;
                                node.isHidden = false;
                            }
                        }


                        //如果参数中设置了父节点可选，则不做判断
                        if(parentCheck == true){
                            node.nocheck = false;
                        }else{
                            node.nocheck = true;
                            if (!node.isParent) {
                                node.nocheck = false;
                                node.isHidden = false;
                            }
                        }
                        // node.nocheck = true;
                        // if (!node.isParent) {// 叶子节点
                        //         node.nocheck = false;
                        //         node.isHidden = false;
                        // }
                        //新增文章页同步信息回显
                        if (node.id == node.organId) {//单位节点
                            if (cur.synOrganCatIds.indexOf(node.organId) >= 0) {
                                checkedOrgan.push(node.organId);
                            }
                        } else {
                            var organCatId = node.organId + "_" + node.id;
                            if (cur.synOrganCatIds.indexOf(organCatId) >= 0) {
                                selectedOrganId.push(node.organId);
                                selectedCatId.push(node.id);
                                node.checked = true;
                            }
                        }
                    }
                    return responseData;
                }
            },
            callback: {
                onAsyncSuccess: function (event, treeId, treeNode) {
                    var treeObj = $.fn.zTree.getZTreeObj(treeId);
                    if (checkedOrgan.length > 0) {//展开选中节点的父节点
                        for (var i = 0; i < checkedOrgan.length; i++) {
                            var node = treeObj.getNodeByParam("id", checkedOrgan[i], null);
                            treeObj.expandNode(node, true, false, false);
                            if (node.isParent && !node.zAsync) {
                                treeObj.reAsyncChildNodes(node, "refresh", true);
                            }
                        }
                    } else {//定位到选中的子节点
                        if (selectedCatId.length > 0) {
                            var nodes = treeObj.getNodesByFilter(publictreefilter);
                            for (var i = 0; i < nodes.length; i++) {
                                treeObj.selectNode(nodes[i]);
                                //选中所有父节点
                                var tId = nodes[i].parentTId;
                                while(true){
                                    var parent = treeObj.getNodeByParam("tId", tId, null);
                                    if(parent == undefined || parent == null){
                                        break;
                                    }
                                    treeObj.checkNode(parent, true);
                                    tId = parent.parentTId
                                    if(tId == undefined || tId == null || tId == null || tId == '1'){
                                        break;
                                    }
                                }
                            }
                        }
                    }


                    // 异步加载全部
                    if (cur.flag) {// 第二次才有children属性
                        asyncNodes(treeNode.children);
                    } else {
                        var nodes = tree.getNodes();
                        cur.flag = true;
                        asyncNodes(nodes);
                    }

                }
            }
        });

        // 异步加载全部
        function asyncNodes(nodes) {
            if (!nodes) return;
            for (var i=0, l=nodes.length; i<l; i++) {
                if(nodes[i].isParent){
                    if (!nodes[i].zAsync&&nodes[i].parentId==0) {
                        tree.reAsyncChildNodes(nodes[i], "refresh", true);
                    }
                }
            }
            selectOrgan();
        }

        tree = $.fn.zTree.init($("#" + treeId), ztree_settings);




        function selectOrgan() {
            cur.treeNodes2 = tree.transformToArray(tree.getNodes());

            $.each(cur.treeNodes2, function (i, v) {
                v.value = v.name; // 必要格式 or label
            })

            $( "#tags2" ).autocomplete({
                source: cur.treeNodes2,
                select: function (e, ui) {
                    var text = $(e.toElement).text();
                    $(this).val(ui.item.name);
                    //debugger
                    var node = tree.getNodesByParam("id", ui.item.id, null)[0];
                    if (!node.isParent) {
                        node = node.getParentNode();
                    }

                    tree.expandAll();
                    setTimeout(function () {
                        tree.expandNode(node, true, false, true);
                    }, 500)

                    return false;
                }
            }).data("ui-autocomplete")._renderItem = function (ul, item) {
                var getPath = item.getPath(),
                    str = '';

                $.each(getPath, function (i, v) {
                    str += v.name + '>'
                })
                str = str.substring(0, str.lastIndexOf('>'));

                return $("<li>")
                    .append('<a href="javascript:void(0);">' + str + '</a>')
                    .appendTo(ul);
            };
        }




        return tree;
    }

    function publictreefilter(node) {
        if (selectedCatId.length > 0) {
            for (var i = 0; i < selectedCatId.length; i++) {
                if (selectedCatId[i] == node.id && selectedOrganId[i] == node.organId) {
                    return true;
                }
            }
        }
    }

    return {
        columnInit: columnInit,
        publicInit: publicInit,
        checkAllChild:getAllChildrenNodes
    }
}();
