var site_opt_tree = function () {
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
                title: ""
            },
            simpleData: {
                enable: true,
                idKey: "indicatorId",
                pIdKey: "parentId",
                rootPId: 0
            }
        }
    };

    function checkNodeByTId(node) {
        cur.siteOptTreeObj.checkNode(node,true,true);
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var siteOpt = function () {

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData,Ls.treeDataType.SITE);
            if(null != responseData && responseData.length == 1 && responseData[0].type == 'CMS_Site') {
                responseData[0].isParent = 0;
            }
            return responseData;
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
                    var id = treeNode.indicatorId + '_' + functions[i].action;
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
                    var site_tree_tid = $(this).val();
                    var checked = $(this).is(':checked');
                    var node = cur.siteOptTreeObj.getNodeByTId(site_tree_tid);
                    if(checked) {
                        checkNodeByTId(node);
                    }

                    var action = $(this).attr("action");
                    var nodes = cur.siteOptTreeObj.transformToArray(node);
                    for(var i=0;i<nodes.length;i++) {
                        var functions = nodes[i].functions;
                        for(var j=0;j<functions.length;j++) {
                            if(functions[j] != null) {
                                if(functions[j].action == action) {
                                    functions[j].checked = checked;
                                }
                            }
                        }
                        $("input[id*="+nodes[i].indicatorId+"_"+action+"]").prop("checked",checked);
                    }
                });
            }
        }

        function onCheck(event, treeId, node) {
            var nodes = cur.siteOptTreeObj.transformToArray(node);
            for(var i=0;i<nodes.length;i++) {
                $("input[id*="+nodes[i].indicatorId+"]").prop("checked",nodes[i].checked);
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

        function onClick(event, treeId, node) {
            if(node.isParent) {
                cur.siteOptTreeObj.cancelSelectedNode(node);
                cur.siteOptTreeObj.expandNode(node);
                event.stopPropagation();
            }
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addFunction
            },
            check: {
                enable: true,
                chkboxType: { "Y": "ps", "N": "s" }
            },
            async: {
                url: "/user/site/rights/getSiteRights?dataFlag=1&userId=" + cur.userId,
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onCheck:onCheck,
                onAsyncSuccess: function () {
                    var nodes = cur.siteOptTreeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.siteOptTreeObj.expandNode(nodes[0], true, false, true);
                    }
                    initSlimScroll();
                }
            }
        });

        function initSlimScroll() {
            //添加模拟滚动条
            var ui_tree = $('#site_opt_tree_wrap');
            var ui_layout = $(".mini-layout-region-body");
            ui_tree.attr("data-height", ui_layout.height() - 180);
            App.initSlimScroll(ui_tree);
        }

        cur.siteOptTreeObj = $.fn.zTree.init($("#site_opt_tree"), settings);
    }
    return {
        siteOpt: siteOpt
    }

}();