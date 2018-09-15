var statusManage = function() {
    var init = function() {
        var setting = {
            view: {
                nameIsHTML: true,
                showTitle: true,
                selectedMulti: false,
                dblClickExpand: false,
                expandSpeed: "fast",
                showIcon:true
            },
            async: {
                enable: true,
                type: "GET",
                dataType: "JSON",
                url: "/contentModel/getStatusList",
                dataFilter: dataFilter
            },
            check: {
                enable: true,
                //autoCheckTrigger: true,
                chkboxType: {"Y": "", "N": ""}
            },
            callback: {
                onCheck: onCheck,
                onClick:onClick,
                beforeCheck: function (treeId, treeNode) {
                },
                onAsyncSuccess: function (){
                    if (!Ls.isEmpty(dealStatus)) {
                        var codesArr = dealStatus.split(",");
                        var zTree = jQuery.fn.zTree.getZTreeObj("code_tree");
                        for (var i = 0; i < codesArr.length; i++) {
                            var node = zTree.getNodeByParam("code", codesArr[i], null);
                            if(node!=null){
                                zTree.checkNode(node, true, true);
                            }
                        }
                    }
                },
                onDblClick: onclick
            },
            data: {
                keep: {},
                key: {
                    title: "key",
                    name:"key"
                },
                simpleData: {
                    enable: true,
                    idKey: "code",
                    pIdKey: "parentId"
                }
            }
        };
        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData, null);
            return data;
        }

        function beforeClick(treeId, treeNode) {
        }

        $('#transmit1').on('click', function () {
            var zTree = jQuery.fn.zTree.getZTreeObj("code_tree");
            var nodes = zTree.getCheckedNodes(true);
            var ids = "";
            var names = "";
            if (nodes != null && nodes != "") {
                for (var i = 0; i < nodes.length - 1; i++) {
                    ids += nodes[i].code + ",";
                    names += nodes[i].key + ",";
                }
                ids += nodes[nodes.length - 1].code;
                names += nodes[nodes.length - 1].key;
            }
            var parent = art.dialog.opener;
            parent.curr.vm.statusCode = ids;
            parent.curr.vm.statusName = names;
            art.dialog.close();
        });

        function onCheck(e, treeId, treeNode) {
            var zTree = jQuery.fn.zTree.getZTreeObj("code_tree");
            if (treeNode.isParent) {
                zTree.expandNode(node);
            }
        }
        function onClick(e, treeId, treeNode){
            var zTree = jQuery.fn.zTree.getZTreeObj("code_tree");
            zTree.checkNode(treeNode, true, true);
        }

        $.fn.zTree.init($("#code_tree"), setting);
    }

    return {
        init:init
    }

}();
