var curz = {
    isLoadRoot: false,
    clickOrganId: '',
    organId: '',
    personId: '',
    isUnit: '',
    ztree: ''
};
var leaderManage = function () {

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
            key: {
                title: 'name',
                name: 'name'
            },
            simpleData: {
                enable: true,
                /*idKey : 'personId',
                 pIdKey : 'parentId'*/
                idKey: 'leaderInfoId',
                pIdKey: 'columnId'
            }
        }
    };

    function initSlimScroll() {
        //添加模拟滚动条
        var leader_tree = $('#leader_tree');
        var ui_layout = $(".mini-layout-region-body");
        leader_tree.attr("data-height", ui_layout.height() - 10)
        App.initSlimScroll(leader_tree);
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }


    var getUser = function (siteId) {
        //初始化布局
        mini.parse();

        //表单必须同时定义,且名称必须相同  id="organ_form" ms-controller="organ_form"
        var settings = $.extend(true, ztree_settings, {
            edit:{
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
                chkboxType: {"Y": "", "N": ""}
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
        SetData();
        function SetData() {
            Ls.ajax({
                url: "/contentModel/getLeaderInfo?siteId="+siteId,
                success: function (json) {
                    if (json.data == null) {
                        return;
                    }
                    var data = json.data;
                    jQuery.fn.zTree.init($("#leader_tree"), settings, data);
                    var zTree = jQuery.fn.zTree.getZTreeObj("leader_tree");
                    if (!Ls.isEmpty(recUserIds)) {
                        var recUserIdArr = recUserIds.split(",");
                        for (var i = 0; i < recUserIdArr.length; i++) {
                            var node = zTree.getNodeByParam("leaderInfoId", recUserIdArr[i], null);
                            if(node!=null){
                                zTree.checkNode(node, true, true);
                            }
                        }
                        // for (var i = 0, l = data.length; i < l; i++) {
                        //     if (recUserIds.contains(data[i].code)) {
                        //         data[i].checked = true;
                        //     }
                        // }
                    }
                    App.initContentScroll(90);

                }
            });
        }


        function onClick(event, treeId, node) {
            var zTree = jQuery.fn.zTree.getZTreeObj("leader_tree");
            if (node.isParent) {
                zTree.expandNode(node);
            }else{
                zTree.checkNode(node, true, true);
            }
        }

        function beforeDrag(treeId, treeNodes) {
            for (var i=0,l=treeNodes.length; i<l; i++) {
                if (treeNodes[i].drag === false) {
                    return false;
                }
            }
            return true;
        }
        function beforeDrop(treeId, treeNodes, targetNode, moveType) {
            return targetNode ? targetNode.drop !== false : true;
        }

        //初始化树
        curz.ztree = $.fn.zTree.init($("#leader_tree"), settings);

        $('#transmit').on('click', function () {
            var uiTree = jQuery.fn.zTree.getZTreeObj("ui_tree");
                var uiCheckTree = uiTree.getCheckedNodes(true);
                var uiNodes = [];
                var zTree = jQuery.fn.zTree.getZTreeObj("leader_tree");
                var nodes = zTree.getCheckedNodes(true);


                var ids = "";
                var names = "";
                if (uiCheckTree != null && uiCheckTree != "") {
                    if (uiCheckTree.length == 1) {
                        /* ids=nodes[0].personId;
                         names=nodes[0].name;*/
                        ids = uiCheckTree[0].code+ ",";;
                        names = uiCheckTree[0].key+ ",";;
                    } else {
                        for (var i = 0; i < uiCheckTree.length; i++) {
                            uiNodes.add(uiCheckTree[i]);
                            ids += uiCheckTree[i].code + ",";
                            names += uiCheckTree[i].key + ",";
                        }
                        if (nodes == null || nodes == "") {
                            ids = ids.substring(0,ids.length-1);
                            names = names.substring(0,names.length-1);


                        }


                    }
                }
                var parent = art.dialog.opener;



            //var zTree = $.fn.zTree.init($("#leader_tree"), settings);           // var  nodes = zTree.getSelectedNodes();

            var newNodes = [];
            if (nodes != null && nodes != "") {
                if (nodes.length == 1) {
                    /* ids=nodes[0].personId;
                     names=nodes[0].name;*/
                    ids += nodes[0].leaderInfoId;
                    names += nodes[0].name;;
                } else {
                    for (var i = 0; i < nodes.length; i++) {
                        newNodes.add(nodes[i]);
                        ids += nodes[i].leaderInfoId + ",";
                        names += nodes[i].name + ",";
                    }
                    ids = ids.substring(0,ids.length-1);
                    names = names.substring(0,names.length-1);


                }
            }
            parent.curr.vm.recUserIds = ids;
            parent.curr.vm.recUserNames = names;
            art.dialog.close();
        });

    }

    return {
        getUser: getUser
    }

}();