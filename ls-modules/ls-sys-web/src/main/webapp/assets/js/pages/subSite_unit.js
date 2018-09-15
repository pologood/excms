var unit_select_tree_ser = function () {
    var init = function () {
        var setting = {
            view: {
                nameIsHTML: true,
                showTitle: true,
                selectedMulti: false,
                dblClickExpand: false,
                expandSpeed: "fast"
            },
            async: {
                enable: true,
                type: "GET",
                dataType: "JSON",
                url: "/siteConfig/getUnits",
                dataFilter: dataFilter
            },
            check: {
                enable: false,
                //autoCheckTrigger: true,
                chkboxType: {"Y": "", "N": ""}
            },
            callback: {
                //onCheck: onCheck,
                onClick: onClick,
                beforeCheck: function (treeId, treeNode) {
                },
                onAsyncSuccess: function () {

                },
                onDblClick: onclick
            },
            data: {
                keep: {},
                key: {
                    title: "name",
                    name: "name"
                },
                simpleData: {
                    enable: true,
                    idKey: "organId",
                    pIdKey: "parentId"
                }
            }
        };

        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData, Ls.treeDataType.UNIT);
            return data;
        }

        function beforeClick(treeId, treeNode) {
        }


        function showSelect() {
            var zTree = jQuery.fn.zTree.getZTreeObj("treeDemo");
            var nodes = zTree.getCheckedNodes(true);
            var newNodes = [];
            var ids = "";
            var names = "";
            if (nodes != null && nodes != "") {
                for (var i = 0; i < nodes.length - 1; i++) {
                    newNodes.add(nodes[i]);
                    ids += nodes[i].organId + ",";
                    names += nodes[i].name + ",";
                }
                ids += nodes[nodes.length - 1].organId;
                names += nodes[nodes.length - 1].name;
            }
            var arr = {"ids": ids, "names": names};
            return arr;
        }

        function onCheck(e, treeId, treeNode) {
            var arr = showSelect();
            curr.vm.unitIds = arr.ids;
            curr.vm.unitNames = arr.names;
        }

        /* function onClick(e, treeId, treeNode){
         var zTree = jQuery.fn.zTree.getZTreeObj("treeDemo");
         zTree.checkNode(treeNode, true, true);
         onCheck(e, treeId, treeNode);
         }*/
        function onClick(e, treeId, treeNode) {
            curr.vm.unitIds = treeNode.organId;
            curr.vm.unitNames = treeNode.name;
            hideSerTree();
        }

        $.fn.zTree.init($("#treeDemo"), setting);
    }

    return {
        init: init
    }

}();

function showUnitTree(top, heigth) {
    var _top = 12;
    var _heigth = 300;
    if (top != null) {
        _top = top;
    }
    if (heigth != null) {
        _heigth = heigth;
    }

    var obj = $("#unitNames");
    var icon_btn = $("#icon_btn");
    var offset = $("#unitNames").offset();
    $("#menuContent").css({width: obj.outerWidth() + icon_btn.outerWidth() - 1}).slideDown("fast");
    $("body").bind("mousedown", onSerBodyDown);

}
function hideSerTree() {
    $("#menuContent").hide();
    $("body").unbind("mousedown", onSerBodyDown);
}
function onSerBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
        hideSerTree();
    }
}