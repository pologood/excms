var unitManage_all = function () {

    var ztree = {}
    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast",
            addDiyDom: null
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
                enable: false,
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
            } else {
                Ls.log('移除1');
                ztree.checkNode(node, false, false);

            }
        }

        function onCheck(event, treeId, node) {
            if (!node.checked) {
                Ls.log('移除');
                ztree.checkNode(node, false, false);

            } else {
                Ls.log('增加');
                var nd = Ls.clone(node);
                nd.pid = null;
                nd.children = null;
                nd.isParent = false;

        }
        }

        //已选树对象

        var getData = function () {
            return Ls.ajax({
                url: "/weChatDeal/getUints",
                success: function (json) {
                    if (json.data == null) {
                        return;
                    }
                    if (json.data != null) {
                        Ls.treeDataFilter(json.data, Ls.treeDataType.UNIT);
                    }
                    var data = json.data;
                    ztree = $.fn.zTree.init($("#unit_tree_all"), settings, data);
                    ztree.expandAll(false);
                    App.initContentScroll(90);
                }
            });
        }();

    }

    return {
        getUnit: getUnit
    }

}();