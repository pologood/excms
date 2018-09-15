var column_select_tree = function() {
    var init = function() {
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
                url: "/scene/service/getColumnTreeBySite",
                otherParam: {},
                autoParam: ["indicatorId"],
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
                simpleData: {
                    enable: true,
                    idKey: "indicatorId",
                    pIdKey: "parentId"
                }
            }
        };

        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData, Ls.treeDataType.SITE);
            return data;
        }

        function beforeClick(treeId, treeNode) {
        }

        function onClick(e, treeId, treeNode) {
        }

        function dbClick(e, treeId, treeNode) {
            var id = $("#columnId");
            var name = $("#columnName");
            id.val(treeNode.indicatorId);
            name.val(treeNode.name);
            hideTree();
        }

        $.fn.zTree.init($("#treeDemo"), setting);
    }

    return {
        init:init
    }

}();

function showTree() {
    var obj = $("#columnName");
    var icon_btn = $("#icon_btn");
    var offset = $("#columnName").offset();
    $("#menuContent").css({left:offset.left + "px", top:offset.top + obj.outerHeight() + "px",width:obj.outerWidth() + icon_btn.outerWidth() - 1}).slideDown("fast");
    $("body").bind("mousedown", onBodyDown);
}
function hideTree() {
    $("#menuContent").hide();
    $("body").unbind("mousedown", onBodyDown);
}
function onBodyDown(event) {
    if (!(event.target.id == "menuBtn" || event.target.id == "menuContent" || $(event.target).parents("#menuContent").length>0)) {
        hideTree();
    }
}
