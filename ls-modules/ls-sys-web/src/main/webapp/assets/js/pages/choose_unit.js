var cur = {
    isLoadRoot: false,
    clickOrganId: '',
    organId: '',
    personId: '',
    isUnit: '',
    ztree: ''
};
var unitManage = function () {

    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast"
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

    var zTree = jQuery.fn.zTree.getZTreeObj("unit_tree");
    var getUnit = function () {
        //初始化布局
        mini.parse();

        //表单必须同时定义,且名称必须相同  id="organ_form" ms-controller="organ_form"
        var settings = $.extend(true, ztree_settings, {
            edit: {
                enable: true,
                showRemoveBtn: false,
                showRenameBtn: false,
                drag: {
                    isCopy: false,
                    isMove: true,
                    prev: true,
                    next: true,
                    inner: false


                }
            },
            view: {
                fontCss: getFont
            },
            check: {
                enable: true,
                autoCheckTrigger: false,
                chkboxType: {"Y": "p", "N": "p"}

            },
            async: {
                enable: false
            },
            callback: {
                onClick: onClick,
                beforeDrag: beforeDrag,
                beforeDrop: beforeDrop

            }
        });

        function onClick(event, treeId, node) {
            var zTree = jQuery.fn.zTree.getZTreeObj("unit_tree");
            if (!node.checked) {
                zTree.checkNode(node, true, false);
            } else {
                zTree.checkNode(node, false, false);
            }
        }


        function beforeDrag(treeId, treeNodes) {
            for (var i = 0, l = treeNodes.length; i < l; i++) {
                if (treeNodes[i].drag === false) {
                    return false;
                }
            }
            return true;
        }

        function beforeDrop(treeId, treeNodes, targetNode, moveType) {
            return targetNode ? targetNode.drop !== false : true;
        }

        function disableNodes(nodes) {
            var zTree = jQuery.fn.zTree.getZTreeObj("unit_tree");
            if (nodes.length > 0) {
                for (var i = 0; i < nodes.length; i++) {
                    if (nodes[i].type != "Organ") {
                        zTree.setChkDisabled(nodes[i], true);
                    }
                    if (nodes[i].children != null) {
                        disableNodes(nodes[i].children);
                    }
                }
            }
        }

        return Ls.ajax({
            url: "/contentModel/getChooseUnit",
            data: {recUnitIds: recUnitIds},
            success: function (json) {
                var data = json.data;
                var dd=[];
                if (data != null && data.length > 0) {
                    var j=0;
                    for (var i = 0, l = data.length; i < l; i++) {
                        if(data[i]!=null){
                            data[i].checked = true;
                            dd[j++]=data[i];

                        }
                    }

                    Ls.treeDataFilter(dd, Ls.treeDataType.UNIT);
                }
                jQuery.fn.zTree.init($("#unit_tree"), settings, dd);
                var zTree = jQuery.fn.zTree.getZTreeObj("unit_tree");
                zTree.expandAll(true);
                // App.initContentScroll(90);
            }
        });

    }

    return {
        getUnit: getUnit
    }

}();