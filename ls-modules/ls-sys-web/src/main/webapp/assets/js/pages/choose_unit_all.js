var unitManage_all = function () {

    var ztree = {}, newTree = {};
    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast",
            addDiyDom: addDiyDom
        },
        async: {
            enable: false,
            type: "GET",
            dataType: "JSON",
            url: "",
            otherParam: {},
            autoParam: ""
        },
        callback: {},
        data: {
            simpleData: {
                enable: true,
                idKey: 'organId',
                pIdKey: 'parentId'
            }
        }
    };

    function addDiyDom(treeId, node) {
        var aObj = $("#" + node.tId + "_a");
        var checkBtn = ' <label><input type="checkbox" id="checkBtn_' + node.organId + '"> 全选</label>';

        if (node.isParent) {
            aObj.after(checkBtn);
            var $checkBtn = $("#checkBtn_" + node.organId);
            //添加
            $checkBtn && $checkBtn.on("click", function () {
                var $this = $(this);
                var nd = ztree.getNodeByParam("organId", node.organId, null);
                var nds = ztree.transformToArray(nd);
                for (var i = 0, l = nds.length; i < l; i++) {
                    ztree.checkNode(nds[i], $this.prop("checked"), true, true);
                }
            });
        }
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var getUnit = function () {
        //初始化布局
        mini.parse();

        //表单必须同时定义,且名称必须相同  id="organ_form" ms-controller="organ_form"
        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
            },
            check: {
                enable: true,
                autoCheckTrigger: false,
                chkboxType: {"Y": "", "N": ""}
            },
            async: {
                enable: false
            },
            callback: {
                onClick: onClick,
                onCheck: onCheck
            }
        });

        function onClick(event, treeId, node) {
            if (!node.checked) {
                Ls.log('增加1');
                ztree.checkNode(node, true, false);
                var nd = Ls.clone(node);
                nd.pid = null;
                nd.children = null;
                nd.isParent = false;
                newTree.addNodes(null, nd);
            } else {
                Ls.log('移除1');
                ztree.checkNode(node, false, false);
                var node1 = newTree.getNodeByParam("organId", node.organId, null);
                newTree.removeNode(node1);
            }
        }

        function onCheck(event, treeId, node) {
            if (!node.checked) {
                Ls.log('移除');
                ztree.checkNode(node, false, false);
                var node1 = newTree.getNodeByParam("organId", node.organId, null);
                newTree.removeNode(node1);
            } else {
                Ls.log('增加');
                var nd = Ls.clone(node);
                nd.pid = null;
                nd.children = null;
                nd.isParent = false;
                newTree.addNodes(null, nd);
            }
        }

        //已选树对象
        newTree = $.fn.zTree.getZTreeObj("unit_tree");

        var getData = function () {
            return Ls.ajax({
                url: "/contentModel/getOrgansByUnitId",
                success: function (json) {
                    if (json.data == null) {
                        return;
                    }
                    if (json.data != null) {
                        Ls.treeDataFilter(json.data, Ls.treeDataType.UNIT);
                    }
                    var data = json.data;
                    if (!Ls.isEmpty(recUnitIds)) {
                        recUnitIds = recUnitIds + ",";
                        for (var i = 0, l = data.length; i < l; i++) {
                            if ((recUnitIds).indexOf(data[i].organId + ",") >= 0) {
                                data[i].checked = true;
                            }
                        }
                    }

                    ztree = $.fn.zTree.init($("#unit_tree_all"), settings, data);
                    ztree.expandAll(true);
                    //App.initContentScroll(90);
                }
            });
        }();

    }

    return {
        getUnit: getUnit
    }

}();