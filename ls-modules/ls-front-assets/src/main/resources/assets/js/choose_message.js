var messageColumn_all = function () {

var ztree_settings_ex8 = {
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
        dataType: "JSON"
    },
    data: {
        keep: {},
        key: {
            title: "indicatorId",
            name: "name"
        },
        simpleData: {
            idKey: 'indicatorId',
            pIdKey: 'parentId'
        }
    },
    callback: {
        onClick: function (event, treeId, node) {
            //cur.ex8_selectNode = node;
            return false;
        }
    }
};


    var getMessage = function () {

        //表单必须同时定义,且名称必须相同  id="organ_form" ms-controller="organ_form"
        var settings = $.extend(true, ztree_settings_ex8, {
            view: {
                addDiyDom: function (id, node) {
                    if(node.isParent==1){
                        node.chkDisabled=true;
                        node.nocheck=true;
                    }
                }
            },
            check: {
                enable: true,
                autoCheckTrigger: false,
                chkStyle:"radio",
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

        }

        function onCheck(event, treeId, node) {

        }


        var getData = function () {
            return Ls.ajax({
                type:'get',
                url: "/search/getMessageColumn?siteId="+currContext.siteId,
                success: function (json) {
                    if (json.data == null) {
                        return;
                    }
                    if (json.data != null) {
                        //Ls.treeDataFilter(json.data, Ls.treeDataType.UNIT);
                    }
                    var data = json.data;

                    currContext.messageTree = $.fn.zTree.init($("#meassge_tree"), settings, data);
                    currContext.messageTree.expandAll(false);
                    //App.initContentScroll(90);
                }
            });
        }();

    }

    return {
        getMessage: getMessage
    }
}();