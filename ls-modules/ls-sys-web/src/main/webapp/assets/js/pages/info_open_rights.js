var info_open_opt_tree = function () {
    var ztree_settings = {
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
            url: "",
            otherParam: {},
            autoParam: ""
        },
        callback: {},
        data: {
            key: {
                title: "id"
            },
            simpleData: {
                enable: true,
                idKey: "id",
                pIdKey: "parentId",
                rootPId: 0
            }
        }
    };

    function checkNodeByTId(node) {
        cur.infoOpenOptTreeObj.checkNode(node,true,true);
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var init = function () {

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData,Ls.treeDataType.CATALOG);
            var temp = [];
            for(var i=0;i<responseData.length;i++) {
                if(responseData[i].type == "CMS_Site") {
                    responseData[i].icon= GLOBAL_CONTEXTPATH + "/assets/images/site.png";
                } else {
                    temp.push(responseData[i]);
                }
            }
            return temp;
        };

        function addFunction(treeId, treeNode) {

            if(treeNode.checked) {
                checkNodeByTId(treeNode.tId);
            }

            var ids = [];
            var sObj = $("#" + treeNode.tId + "_a");
            var functions = treeNode.functions;
            var opt = "";
            if(functions != null) {
                for(var i=0; i<functions.length; i++) {
                    var id = treeNode.parentId + "_" + treeNode.id + '_' + functions[i].action;
                    ids.push(id);
                    var checked = functions[i].checked;
                    if(checked) {
                        opt = opt + '&nbsp<label><span><input id="'+id+'" value="'+treeNode.tId+'" type="checkbox" action="'+functions[i].action+'" checked><span style="color: #da9264"> '+functions[i].name+'</span></label></span>';
                        continue;
                    }
                    opt = opt + '&nbsp<label><span><input id="'+id+'" value="'+treeNode.tId+'" type="checkbox" action="'+functions[i].action+'"><span style="color: #da9264"> '+functions[i].name+'</span></label></span>';
                }
            }
            sObj.after(opt);

            for(var j = 0;j<ids.length;j++) {
                var ckEvt = $("#" + ids[j] + "");
                ckEvt.on('click',function() {
                    var tree_tid = $(this).val();
                    var checked = $(this).is(':checked');
                    var node = cur.infoOpenOptTreeObj.getNodeByTId(tree_tid);
                    if(checked) {
                        checkNodeByTId(node);
                    }

                    var action = $(this).attr("action");
                    var nodes = cur.infoOpenOptTreeObj.transformToArray(node);
                    for(var i=0;i<nodes.length;i++) {
                        var functions = nodes[i].functions;
                        for(var j=0;j<functions.length;j++) {
                            if(functions[j] != null) {
                                if(functions[j].action == action) {
                                    functions[j].checked = checked;
                                }
                            }
                        }
                        $("input[id*="+ nodes[i].parentId +"_" + nodes[i].id+"_"+action+"]").prop("checked",checked);
                    }
                });
            }
        }

        function onCheck(event, treeId, node) {
            if(cur.roleId == null || cur.roleId == '') {
                Ls.tips('请选择角色',{times:2});
            }
            cur.selectAll = false;
            if(node.type=='Organ') {
                cur.infoOpenOptTreeObj.expandNode(node);
                cur.organNodeTid = node.tId;
                cur.selectAll = true;
            }

            //cur.infoOpenOptTreeObj.expandNode(node, true, true, true);
            var nodes = cur.infoOpenOptTreeObj.transformToArray(node);
            for(var i=0;i<nodes.length;i++) {
                $("input[id*="+ nodes[i].parentId +"_" + nodes[i].id+ "]").prop("checked",nodes[i].checked);
                var functions = nodes[i].functions;
                if(functions != null) {
                    for(var j=0;j<functions.length;j++) {
                        if(functions[j] != null) {
                            functions[j].checked = true;
                        }
                    }
                }
            }

            if(node.type == 'Organ') {
                cur.organIds.push(node.organId);
            }
        }

        function onClick(event, treeId, node) {
            if(node.isParent) {
                cur.infoOpenOptTreeObj.cancelSelectedNode(node);
                cur.infoOpenOptTreeObj.expandNode(node);
                event.stopPropagation();
            }

            if(node.type == 'Organ') {
                cur.organIds.push(node.organId);
            }
        }

        function onExpand(event, treeId, node) {
            if(node.type == 'Organ') {
                cur.organIds.push(node.organId);
            }
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addFunction
            },
            check: {
                enable: true
            },
            async: {
                url: "/info/open/getInfoOpenRights",
                autoParam:['organId','siteId'],
                otherParam:['roleId',cur.roleId],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onCheck:onCheck,
                onExpand:onExpand,
                onAsyncSuccess: function () {
                    if(cur.selectAll && cur.organNodeTid != null) {
                        var node = cur.infoOpenOptTreeObj.getNodeByTId(cur.organNodeTid);
                        cur.infoOpenOptTreeObj.checkNode(node,node.checked,true);

                        var nodes = cur.infoOpenOptTreeObj.transformToArray(node);
                        for(var i=0;i<nodes.length;i++) {
                            $("input[id*="+ nodes[i].parentId +"_" + nodes[i].id+"]").prop("checked",nodes[i].checked);
                            var functions = nodes[i].functions;
                            if(functions != null) {
                                for(var j=0;j<functions.length;j++) {
                                    if(functions[j] != null) {
                                        functions[j].checked = true;
                                    }
                                }
                            }
                        }
                    }
                    cur.selectAll = false;
                    cur.organNodeTid == null;

                    var nodes = cur.infoOpenOptTreeObj.getNodes();
                    if (nodes.length > 0) {
                        for(var i=0;i<nodes.length;i++) {
                            if(nodes[i].checked) {
                                cur.infoOpenOptTreeObj.expandNode(nodes[i], true, false, true);
                                cur.organIds.push(nodes[i].organId);
                            }
                        }
                        //cur.infoOpenOptTreeObj.expandNode(nodes[0], true, false, true);
                    }
                    initSlimScroll();
                }
            }
        });

        function initSlimScroll() {
            //添加模拟滚动条
            var ui_tree = $('#info_open_opt_tree_wrap');
            var ui_layout = $(".mini-layout-region-body");
            ui_tree.attr("data-height", ui_layout.height() - 180);
            App.initSlimScroll(ui_tree);
        }

        cur.infoOpenOptTreeObj = $.fn.zTree.init($("#info_open_opt_tree"), settings);
    }
    return {
        init: init
    }

}();