var organ_select_tree = function () {
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
                    if (treeNode.isParent) {
                        return false;
                    }
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
            var id = $("#organId");
            var name = $("#organName");
            id.val(treeNode.recUnitId);
            name.val(treeNode.recUnitName);
            hideTree();
        }

        function dbClick(e, treeId, treeNode) {
            var id = $("#organId");
            var name = $("#organName");
            id.val(treeNode.recUnitId);
            name.val(treeNode.recUnitName);
            hideTree();
        }

        $.fn.zTree.init($("#treeDemo"), setting);
    }

    return {
        init: init
    }

}();

function showTree() {
    var obj = $("#organName");
    var icon_btn = $(".btn-select-organ");
    $("#menuContent").css({width: obj.outerWidth() + icon_btn.outerWidth() - 1}).slideDown("fast");
    $("body").bind("mousedown", onBodyDown);
}
function hideTree() {
    $("#menuContent").hide();
    $("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length > 0)) {
        hideTree();
    }
}
