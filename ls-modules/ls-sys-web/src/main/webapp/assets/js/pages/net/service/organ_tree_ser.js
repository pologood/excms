var organ_select_tree_ser = function () {
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
                url: "/resources/getOrgans",
                otherParam: {columnId: content_mgr.indicatorId},
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                beforeCheck: function (treeId, treeNode) {
                },
                onAsyncSuccess: function () {
                },
                onDblClick: dbClick
            },
            data: {
                keep: {},
                key: {
                    title: "recUnitName",
                    name: "recUnitName"
                },
                simpleData: {
                    enable: true,
                    idKey: "recUnitId",
                    pIdKey: ""
                }
            }
        };

        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData, Ls.treeDataType.GUIDE_ORGAN);
            return data;
        }

        function beforeClick(treeId, treeNode) {
        }

        function onClick(e, treeId, treeNode) {
            if (treeNode.isParent) return false;
            var id = $("#organId_ser");
            var name = $("#organName_ser");
            id.val(treeNode.recUnitId);
            name.val(treeNode.recUnitName);
            hideSerTree();
        }

        function dbClick(e, treeId, treeNode) {
            if (treeNode.isParent) return false;
            var id = $("#organId_ser");
            var name = $("#organName_ser");
            id.val(treeNode.recUnitId);
            name.val(treeNode.recUnitName);
            hideSerTree();
        }

        $.fn.zTree.init($("#treeDemo_ser"), setting);
    }

    return {
        init: init
    }

}();
function showSerTree() {
    var organName = $("#organName_ser");
    var organName_iconBtn = $("#icon_btn");
    $("#menuContent_ser").css({width: organName.outerWidth() + organName_iconBtn.outerWidth() - 1}).slideDown("fast");
    $("body").bind("mousedown", onSerBodyDown);

}
function hideSerTree() {
    $("#menuContent_ser").hide();
    $("body").unbind("mousedown", onSerBodyDown);
}
function onSerBodyDown(event) {
    if (!(event.target.id == "menuContent_ser" || $(event.target).parents("#menuContent_ser").length > 0)) {
        hideSerTree();
    }
}
