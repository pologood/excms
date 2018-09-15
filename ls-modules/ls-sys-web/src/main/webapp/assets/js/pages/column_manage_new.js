var treeObj;
var flag = 0;
var isEdit = 0;

var columnManage = function () {

    var isInit = false;

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
                // title: "indicatorId"
                title: "indicatorId"
            },
            data: {
                simpleData: {
                    enable: true,
                    idKey: "indicatorId",
                    pIdKey: "parentId"
                }
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var classify = function () {
        //初始化布局
        mini.parse();
        //var sId=siteId;
        var sId = GLOBAL_SITEID;

        cur.$id = "columnForm";


        var isPush = false;

        function addDiyDom(treeId, node) {
            var sObj = $("#" + node.tId + "_a");
            if ($("#nodeOp_" + node.indicatorId).length > 0) return;
            var nodeOpStr = '<span id="nodeOp_' + node.indicatorId + '">';
            nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>' +
                ' <span id="update' + node.indicatorId + '"  class="button edit-a" title="修改">' + '</span>' +
                ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>' +
                '<span id="move' + node.indicatorId + '"  class="button move-a" title="移动">' + '</span>' +
                '<span id="upload' + node.indicatorId + '"  class="button import-a" title="导入">' + '</span>'
            ;
            if (node.type == 'CMS_Site') {
                nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>';
            }
            nodeOpStr = nodeOpStr + '</span>';
            sObj.after(nodeOpStr);
            // 绑定按钮事件,使用事件绑定的好处就是能传递复杂对象作为参数
            var addBtn = $('#add' + node.indicatorId), updateBtn = $('#update' + node.indicatorId),
                delBtn = $('#del' + node.indicatorId),
                moveBtn = $('#move' + node.indicatorId), uploadBtn = $('#upload' + node.indicatorId);
            addBtn.on('click', function () {
                columnManage.indicatorId = node.indicatorId;
                addNode(node);
                return false;
            });
            updateBtn.on('click', function () {
                columnManage.indicatorId = node.indicatorId;
                editNode(node);
                return false;
            });
            delBtn.on('click', function () {
                delNode(node);
                return false;
            });
            moveBtn.on('click', function () {
                moveNode(node);
                return false;
            });
            uploadBtn.on('click', function () {
                uploadNode(node);
                return false;
            });

        }

        function addNode(node, event) {
            isEdit = 0;
            if (node.type != 'CMS_Site') {
                getColumnEO(node);
            } else {
                treeObj.selectNode(node);
            }
            Ls.openWin('/columnConfig/toEditColumn?parentId=' + node.indicatorId, '600px', '500px', {
                id: 'add_column',
                title: '添加栏目',
                padding: 0
            });

        }


        function editNode(node) {
            isEdit = 1;
            // treeObj.selectNode(node);
            getColumnEO(node);
            Ls.openWin('/columnConfig/toEditColumn?columnId=' + node.indicatorId, '600px', '500px', {
                id: 'edit_column',
                title: '编辑栏目',
                padding: 0
            });

        }

        function delNode(node) {
            isEdit = 0;
            treeObj.selectNode(node);
            if (confirm('确定要删除吗?')) {
                Ls.ajaxGet({
                    type: "POST",
                    url: "/columnConfig/delColumnEO",
                    data: {indicatorId: node.indicatorId}
                }).done(function (text) {
                    if (!text.status) {
                        Ls.tipsErr(text.desc);
                        return;
                    } else {

                        var nodes = node.getParentNode().children;
                        if (nodes.length == 1) {//父级栏目只有一个子栏目
                            treeObj.removeNode(node, false);
                            node.getParentNode().isParent = false;
                            node.getParentNode().children = null;
                            treeObj.updateNode(node.getParentNode(), false);
                            Ls.log(node.getParentNode().children);
                            treeObj.selectNode(node.getParentNode());
                            if (node.getParentNode().type == 'CMS_Site') {
                                $("#center").hide();
                            }
                        } else {//父栏目有多个子栏目
                            treeObj.reAsyncChildNodes(node.getParentNode(), "refresh");
                        }
                        Ls.tipsOk("删除成功");
                        return;
                    }
                });
            }
        }

        function moveNode(node) {
            isEdit = 0;
            treeObj.selectNode(node);
            Ls.openWin('/columnConfig/toMove?indicatorId=' + node.parentId + "&&columnId=" + node.indicatorId, '500px', '300px', {
                id: 'move_eo',
                title: '移动栏目',
                padding: 0
            });
        }


        function uploadNode(node) {
            isEdit = 0;
            treeObj.selectNode(node);
            Ls.uploadFile(function (filePath) {
                Ls.log('kllll');
                treeObj.reAsyncChildNodes(node.getParentNode(), "refresh");
                return true;
            }, {
                upload_url: '/columnConfig/uploadColumn',
                file_upload_limit: 1,
                post_params: {
                    "siteId": GLOBAL_SITEID,
                    "columnId": node.indicatorId
                },
                file_types: "*.xlsx;*.xls"
            });
        }

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = responseData.data;
            var responseData = Ls.treeDataFilter(responseData, Ls.treeDataType.SITE);
            if (!isPush) {
                //添加根节点
                responseData.push({
                    "indicatorId": GLOBAL_SITEID,
                    "parentId": 1,
                    "name": GLOBAL_SITENAME,
                    "type": 'CMS_Site',
                    "isParent": true,
                    "title": GLOBAL_SITEID,
                    "icon": GLOBAL_CONTEXTPATH + "/assets/images/site.png"
                });
                isPush = true;
            }
            return responseData;
        }

        function onClick(event, treeId, node) {
            if (node.isParent) {
                treeObj.expandNode(node);
            }
            if (node.type == 'CMS_Site') {
                $("#center").hide();
                return;
            }
            getColumnEO(node);
            columnManage.indicatorId = node.indicatorId;
        }

        function getColumnEO(node) {
            if (node.type == 'CMS_Site') {
                return;
            }
            treeObj.selectNode(node);
            Ls.ajaxGet({
                type: "POST",
                url: "/columnConfig/getColumnEO",
                data: {indicatorId: node.indicatorId}
            }).done(function (d) {
                if (!d.status) {
                    Ls.tipsErr(d.desc);
                    return;
                } else {
                    $(".dn").hide();
                    var data = d.data;
                    Ls.log(data.contentModelName);
                    data.contentModelName = Ls.isEmpty(data.contentModelName) ? "" : data.contentModelName;
                    cur.$id = "columnForm";

                    if (data.columnTypeCode != "linksMgr") {
                        if(data.isStartUrl == 1){
                            data.previewUrl = data.transUrl.indexOf('http') == 0? data.transUrl:GLOBAL_SITEURI + "/" +  data.transUrl;
                        }else{
                            data.previewUrl = GLOBAL_SITEURI + "/" + node.urlPath + "/index.html";
                        }
                    } else {
                        data.previewUrl = "无";
                    }

                    delete data.functions;
                    delete data.childs;
                    data.$id = "columnForm";
                    cur.$id = "columnForm";

                    cur.vm = avalon.vmodels[data.$id];
                    if (!cur.vm) {
                        cur.vm = avalon.define(data);
                    } else {
                        Ls.assignVM(cur.vm, data);

                    }
                    avalon.scan($("#columnForm").get(0), cur.vm);

                    changeUrl(data.isStartUrl);

                    if (data.columnTypeCode == "articleNews") {
                        $("#synAndGener").show();
                        $("#isStart").show();
                    } else {
                        $("#synAndGener").hide();
                        if (data.columnTypeCode == 'redirect' || data.columnTypeCode == 'sceneService'
                            || data.columnTypeCode == 'workGuide' || data.columnTypeCode == 'net_work' || data.columnTypeCode == 'InteractiveVirtual') {
                            $("#isStart").show();
                        } else {
                            $("#isStart").hide();
                        }
                    }
                    if (data.columnTypeCode == "linksMgr") {
                        $(".linksMgr").show();
                        if (cur.vm.isLogo == 1) {
                            $(".linksMgrY").show();
                        } else {
                            $(".linksMgrY").hide();
                        }
                    }
                    if (data.columnTypeCode == "bbs") {
                        $("#bbsUnit").show();
                    } else {
                        $("#bbsUnit").hide();
                    }
                    if ($("#center").is(":hidden")) {
                        $("#center").show();
                    }
                }
            });
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            edit: {
                drag: {
                    autoExpandTrigger: true,
                    prev: dropPrev,
                    inner: dropInner,
                    next: dropNext
                },
                enable: true,
                showRemoveBtn: false,
                showRenameBtn: false
            },
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/siteMain/getColumnTree",
                dataFilter: dataFilter,
                autoParam: ["indicatorId=indicatorId"],
                otherParam: {"indicatorId": sId, "siteId": sId}
                //autoParam: ["indicatorId"]
            },
            data: {
                simpleData: {
                    enable: true,
                    idKey: "indicatorId",
                    pIdKey: "parentId"
                }
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: onAsyncSuccess,
                beforeDrag: beforeDrag,
                beforeDrop: beforeDrop,
                beforeDragOpen: beforeDragOpen,
                onDrag: onDrag,
                onDrop: onDrop,
                onExpand: onExpand,
                beforeExpand: beforeExpand
            }
        });


        ////
        function dropPrev(treeId, nodes, targetNode) {
            Ls.log("dropPrev>>>")
            return true;
        }

        function dropInner(treeId, nodes, targetNode) {
            Ls.log("dropInner>>>")
            return targetNode.isParent;
        }

        function dropNext(treeId, nodes, targetNode) {
            Ls.log("dropNext>>>", targetNode)
            return true;
        }

        var log, className = "dark", curDragNodes, autoExpandNode;

        function beforeDrag(treeId, treeNodes) {
            return true;
        }

        function beforeDragOpen(treeId, treeNode) {
            autoExpandNode = treeNode;
            return true;
        }

        function beforeDrop(treeId, treeNodes, targetNode, moveType, isCopy) {
            className = (className === "dark" ? "" : "dark");
            return true;
        }

        function onDrag(event, treeId, treeNodes) {
            className = (className === "dark" ? "" : "dark");
        }

        function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
            targetNode && (function () {
                var arr = [], node = targetNode.getParentNode();
                for (var i = 0, l = node.children.length; i < l; i++) {
                    var el = node.children[i];
                    arr.push({
                        sortNum: i + 1,
                        indicatorId: el.indicatorId
                    });
                }
                Ls.tips("排序成功！");
                Ls.log("重组>>" + JSON.stringify(arr));
            }());
            className = (className === "dark" ? "" : "dark");
        }

        function onExpand(event, treeId, treeNode) {
            if (treeNode === autoExpandNode) {
                className = (className === "dark" ? "" : "dark");
            }
        }

        function beforeExpand(treeId, treeNode) {
            treeObj.selectNode(treeNode);
            columnManage.indicatorId = treeNode.indicatorId;
        }


        ////


        function onAsyncSuccess(event, treeId, treeNode, msg) {
            columnManage.zTree = $.fn.zTree.getZTreeObj("column_tree");
            //加载列表
            if (!isInit) {
                isInit = true;
                //添加模拟滚动条
                App.initContentScroll(null, '#column_tree', {right: true});
            }
            if (isEdit == 1) {//修改
                var node = treeObj.getNodeByParam("indicatorId", columnManage.indicatorId, null);
                if (node != null) {
                    getColumnEO(node);
                }
                return;
            }

            if (treeNode == null) {//根据站点搜索栏目树
                var nodes = treeObj.getNodes();
                if (nodes.length > 0 && flag == 0) {
                    treeObj.expandNode(nodes[0], true, false, true);
                    if (nodes[0].children != null) {
                        treeObj.selectNode(nodes[0].children[0]);
                        getFirstColumnEO(nodes[0].children[0].indicatorId);
                        isEdit = 1;
                    } else {
                        //站点下没有栏目
                        $("#center").hide();
                    }
                } else {//在根节点下添加栏目
                    var node = treeObj.getNodeByParam("indicatorId", columnManage.indicatorId, null);
                    if (node != null) {//添加
                        getColumnEO(node);
                    }
                }
                return;
            } else {
                var node = treeObj.getNodeByParam("indicatorId", columnManage.indicatorId, null);
                if (node != null && isEdit == 0) {
                    if (node.type == 'CMS_Site') {//添加,在根节点下添加栏目
                        $("#center").hide();
                    } else {
                        getColumnEO(node);
                    }
                } else if (node == null) {//删除节点
                    if (treeNode.type == 'CMS_Site') {
                        treeObj.selectNode(treeNode);
                        $('#center').hide();
                    } else {
                        if (treeNode.children != null) {
                            getColumnEO(treeNode.children[0]);
                        }
                    }
                }
            }

        }

        treeObj = $.fn.zTree.init($("#column_tree"), settings);

        function getFirstColumnEO(indicatorId) {
            Ls.ajaxGet({
                type: "POST",
                url: "/siteMain/getFirstColumnEO",
                data: {"indicatorId": indicatorId}
            }).done(function (data) {
                $(".dn").hide();
                cur.vm.processId = Ls.isEmpty(data.processId) ? "" : data.processId;
                cur.vm.processName = Ls.isEmpty(data.processName) ? "" : data.processName;

                getProcessList(cur.vm.processId);
                data.contentModelName = Ls.isEmpty(data.contentModelName) ? "" : data.contentModelName;

                if (data.columnTypeCode != "linksMgr") {
                    data.previewUrl = GLOBAL_SITEURI + "/" + data.urlPath + "/index.html";
                } else {
                    data.previewUrl = "无";
                }
                delete data.functions;
                delete data.childs;
                data.$id = "columnForm";
                cur.$id = "columnForm";
                cur.vm = avalon.define(data);
                avalon.scan($("#columnForm").get(0), cur.vm);

                changeUrl(data.isStartUrl);
                if (data.columnTypeCode == "articleNews") {
                    $("#synAndGener").show();
                    $("#isStart").show();
                } else {
                    $("#synAndGener").hide();
                    if (data.columnTypeCode == "redirect" || data.columnTypeCode == 'sceneService' ||
                        data.columnTypeCode == 'workGuide' || data.columnTypeCode == 'net_work' || data.columnTypeCode == 'InteractiveVirtual') {
                        $("#isStart").show();
                    } else {
                        $("#isStart").hide();
                    }
                }
                if (data.columnTypeCode == "linksMgr") {
                    $(".linksMgr").show();
                    if (cur.vm.isLogo == 1) {
                        $(".linksMgrY").show();
                    } else {
                        $(".linksMgrY").hide();
                    }
                }

                if (data.columnTypeCode == "bbs") {
                    $("#bbsUnit").show();
                } else {
                    $("#bbsUnit").hide();
                }
            });
        }


        function reloadPage() {
            var treeObj = $.fn.zTree.getZTreeObj("column_tree");
            var nodes = treeObj.getSelectedNodes();
            if (nodes.length > 0) {
                treeObj.reAsyncChildNodes(nodes[0].getParentNode(), "refresh");
            }

        }
    }

    function getProcessList(processId) {
        Ls.ajax({
            type: "POST",
            url: "/process/getProcessList",
            data: {moduleCode: "EX"}
        }).done(function (d) {
            var data = d.data;
            if (data && data.length > 0) {
                var _html = ' <option value="">请选择流程</option>';
                for (var i = 0, l = data.length; i < l; i++) {
                    if (!Ls.isEmpty(processId)) {
                        if (data[i].processId == processId) {
                            _html += "<option value='" + data[i].processId + "' selected>" + data[i].name + "</option>";
                        } else {
                            _html += "<option value='" + data[i].processId + "'>" + data[i].name + "</option>";
                        }

                    } else {
                        _html += "<option value='" + data[i].processId + "'>" + data[i].name + "</option>";
                    }

                }
                $("#processId").html(_html);
            }
        });
    }


    var linkModelApp = function (url) {
        App.getContentAjax(url).done(function (res) {
            $("#tab_water").html(res);
        });
    }

    return {
        classify: classify,
        //getData: getData
        linkModelApp: linkModelApp
    }

}();
