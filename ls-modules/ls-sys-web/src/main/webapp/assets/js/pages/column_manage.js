var treeObj;
var flag = 0;
var isEdit = 0;
var selectCode = '';
var selectContentModelCode = '';

function getUnitIds() {
    Ls.openWin('/bbsPlate/getUnits?unitIds=' + cur.vm.unitIds, '400px', '500px', {
        id: 'bbs_unit',
        title: '选择接收单位',
        padding: 0,
    });
}

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

        var data = {
            $id: cur.$id,
            name: '',
            shortName: '',
            siteId: GLOBAL_SITEID,
            parentId: null,
            isParent: 0,
            indicatorId: null,
            columnConfigId: null,
            sortNum: null,
            uri: '',
            contentModelCode: '',
            synColumnNames: '',
            synColumnIds: '',
            genePageIds: '',
            genePageNames: '',
            transUrl: '',
            isStartUrl: 0,
            transWindow: 0,
            isEnableBeauty: 0,
            picWidth: 0,
            picHeight: 0,
            isEasyWord: 0,
            isHotWord: 0,
            isSensitiveWord: 0,
            isShow: 1,
            keyWords: '',
            description: '',
            type: 'CMS_Section',
            unitNames: '',
            unitIds: '',
            isLogo: 0,
            height: '',
            width: '',
            num: 0,
            linkCode: '',
            previewUrl: ''
        }

        cur.vm = avalon.vmodels[data.$id];
        if (!cur.vm) {
            cur.vm = avalon.define(data);
        }

        var isPush = false;

        function addDiyDom(treeId, node) {
            var sObj = $("#" + node.tId + "_a");
            if ($("#nodeOp_" + node.indicatorId).length > 0)return;
            var nodeOpStr = '<span id="nodeOp_' + node.indicatorId + '">';
            nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>' +
                ' <span id="update' + node.indicatorId + '"  class="button edit-a" title="修改">' + '</span>' +
                ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>' +
                '<span id="move' + node.indicatorId + '"  class="button move-a" title="移动">' + '</span>';
            if (node.type == 'CMS_Site') {
                nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>';
            }
            nodeOpStr = nodeOpStr + '</span>';
            sObj.after(nodeOpStr);
            // 绑定按钮事件,使用事件绑定的好处就是能传递复杂对象作为参数
            var addBtn = $('#add' + node.indicatorId), updateBtn = $('#update' + node.indicatorId), delBtn = $('#del' + node.indicatorId), moveBtn = $('#move' + node.indicatorId);
            addBtn.on('click', function () {
                columnManage.indicatorId = node.indicatorId;
                addNode(node);
                return false;
            });
            updateBtn.on('click', function () {
                columnManage.indicatorId = node.indicatorId;
                editNode(node);
                return false
            });
            delBtn.on('click', function () {
                delNode(node);
                return false;
            });
            moveBtn.on('click', function () {
                moveNode(node);
                return false;
            });

        }

        function addNode(node, event) {
            isEdit = 0;
            treeObj.selectNode(node);
            Ls.ajaxGet({
                type: "POST",
                url: "/columnConfig/getNewSortNum",
                data: {parentId: node.indicatorId, isCom: false}
            }).done(function (d) {
                data.parentId = node.indicatorId;
                if ('CMS_Site' == node.type) {
                    data.siteId = node.indicatorId;
                } else {
                    data.siteId = node.siteId;
                }
                //data.sortNum = d.data;
                data.contentModelCode = selectContentModelCode;
//                if (Ls.isEmpty(data.contentModelCode) && Ls.cookies("contentModelCode") != null && Ls.cookies("selectCode") != null) {
//                    data.contentModelCode = Ls.cookies("contentModelCode");
//                }
                Ls.assignVM(cur.vm, data);
                avalon.scan($("#columnForm").get(0), cur.vm);
                cur.vm.sortNum = d.data.sortNum;
                cur.vm.type = 'CMS_Section';
                if (selectCode == 'bbs') {
                    $("#bbsUnit").show();
                }
                if (!Ls.isEmpty(cur.vm.contentModelCode)) {
                    checkColumnType();
                } else {
                    $("#isStart").show();
                    if (cur.vm.isStartUrl == 1) {
                        $("#startUrl").show();
                    } else {
                        $("#startUrl").hide();
                    }
                    $("#synAndGener").hide();
                }
                $("#contentModelCode").html('');
                $("#contentModelCode").append(cur.contentModelStr);
                cur.isAddModel = true;
            });
        }

        function editNode(node) {
            isEdit = 1;
            treeObj.selectNode(node);
            Ls.ajaxGet({
                type: "POST",
                url: "/columnConfig/getColumnEO",
                data: {indicatorId: node.indicatorId}
            }).done(function (d) {
                if (!d.status) {
                    Ls.tipsErr(d.status);
                    return;
                }
                var data = d.data;
                data.contentModelCode = Ls.isEmpty(data.contentModelCode) ? "" : data.contentModelCode;
                if (!data.isHave) {
                    var cStr = "";
                    if (data.columnTypeCode == "redirect") {
                        cStr = cur.contentModelStr;
                    } else {
                        for (i = 0; i < cur.length; i++) {
                            if (cur.contentList[i].modelTypeCode == data.columnTypeCode) {
                                cStr += ' <option  value=' + cur.contentList[i].code + '>' + cur.contentList[i].name + '</option> ';
                            }
                        }
                    }
                    $("#contentModelCode").html('');
                    $("#contentModelCode").append(cStr);
                    cur.isAddModel = false;
                } else {
                    $("#contentModelCode").html('');
                    $("#contentModelCode").append(cur.contentModelStr);
                    cur.isAddModel = true;
                }
                cur.$id = "columnForm";

                if (data.columnTypeCode != "linksMgr") {
                    data.previewUrl = GLOBAL_SITEURI + "/" + node.indicatorId + ".html";
                } else {
                    data.previewUrl = "无";
                }

                Ls.assignVM(cur.vm, data);

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
                        // treeObj.removeNode(node,false);
                        var nodes = node.getParentNode().children;
                        if (nodes.length == 1) {
                            //node.getParentNode().isParent=false;
                            treeObj.removeNode(node, false);
                            editNode(node.getParentNode());
                        } else {
                            treeObj.reAsyncChildNodes(node.getParentNode(), "refresh");
                        }

                        //reloadPage();
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
                return;
            }
            editNode(node);
            columnManage.indicatorId = node.indicatorId;
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
                onExpand: onExpand
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


        ////


        function onAsyncSuccess(event, treeId, treeNode, msg) {
            columnManage.zTree = $.fn.zTree.getZTreeObj("column_tree");
            //加载列表
            if (!isInit) {
                isInit = true;
                //添加模拟滚动条
                App.initContentScroll();
            }

            if (isEdit == 1) {//修改
                var node = treeObj.getNodeByParam("indicatorId", columnManage.indicatorId, null);
                if (node != null) {
                    editNode(node);
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
                        addNode(nodes[0]);
                    }
                } else {//在根节点下添加栏目
                    var node = treeObj.getNodeByParam("indicatorId", columnManage.indicatorId, null);
                    if (node != null) {//添加
                        //editNode(node);
                        addNode(node.getParentNode());
                    }
                }
                return;
            } else {
                var node = treeObj.getNodeByParam("indicatorId", columnManage.indicatorId, null);
                if (node != null && isEdit == 0) {//添加,不在根节点下添加栏目
                    //editNode(node);
                    addNode(node.getParentNode());
                } else if (node == null) {//删除节点
                    if (treeNode.children != null) {
                        editNode(treeNode.children[0]);
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
                data.contentModelCode = Ls.isEmpty(data.contentModelCode) ? "" : data.contentModelCode;

                if (!data.isHave) {
                    var str = '';
                    if (data.columnTypeCode == "redirect") {
                        str = cur.contentModelStr;
                    } else {
                        for (i = 0; i < cur.length; i++) {
                            if (cur.contentList[i].modelTypeCode == data.columnTypeCode) {
                                str += ' <option  value=' + cur.contentList[i].code + '>' + cur.contentList[i].name + '</option> ';
                            }
                        }
                    }
                    $("#contentModelCode").html('');
                    $("#contentModelCode").append(str);
                    cur.vm.contentModelCode = data.contentModelCode;
                    cur.isAddModel = false;
                } else {
                    $("#contentModelCode").html('');
                    $("#contentModelCode").append(cur.contentModelStr);
                    cur.vm.contentModelCode = data.contentModelCode;
                    cur.isAddModel = true;
                }
                cur.$id = "columnForm";

                if (data.columnTypeCode != "linksMgr") {
                    data.previewUrl = GLOBAL_SITEURI + "/" + data.indicatorId + ".html";
                } else {
                    data.previewUrl = "无";
                }

                Ls.assignVM(cur.vm, data);

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

        $('#columnForm').validator({
            /*  rules: {
             mobile: [/^1[3458]\d{9}$/, '请检查手机号格式']
             },*/
            fields: {
                'name': '名称:required;',
                'sortNum': '序号:required;integer(+)',
                'siteId': '站点:required;',
                'indicatorId': '栏目:required;',
                'keyWords': '关键词:length[0~300];',
                'description': '描述:length[0~1000];',
                'width': '图片宽度:range[0~999]',
                'height': '图片高度:range[0~999]',
                'num': '条数:range[0~999]'
            },
            valid: function () {
                doSave();
            },
            msgClass: "n-top"
        });

        $("#doReset").on('click', function () {
            doReset();
        });

        function doReset() {
            var nodes = treeObj.getSelectedNodes();
            if (nodes != null && nodes.length > 0) {
                cur.vm.indicatorId = nodes[0].indicatorId;
                cur.vm.parentId = nodes[0].parentId;
                cur.vm.columnConfigId = nodes[0].columnConfigId;
                cur.vm.siteId = nodes[0].siteId;
                Ls.assignVM(cur.vm, data);
            }
        }

        function doSave() {
            var name = cur.vm.name;
            var pId = cur.vm.parentId;
            var indicatorId = cur.vm.indicatorId;
            if (cur.vm.keyWords != null && Ls.strLen(cur.vm.keyWords) > 300) {
                Ls.tipsInfo("关键词的字数长度应小于1000个字符");
                return;
            }
            if (cur.vm.description != null && Ls.strLen(cur.vm.description) > 1000) {
                Ls.tipsInfo("描述的字数长度应小于1000个字符");
                return;
            }

            if (cur.vm.isStartUrl != 1 && cur.vm.contentModelCode == '') {
                Ls.tipsInfo("请选择内容模型");
                return;
            } else {
                //检查站点名称是否重复
                Ls.ajaxGet({
                    url: "/columnConfig/checkColumnNameExist",
                    type: 'post',
                    data: {name: name, parentId: pId, indicatorId: indicatorId}
                }).done(function (text) {
                    if (text.data) {
                        saveColumnConfigEO();
                    } else {
                        Ls.tipsInfo("栏目名称已存在");
                        return;
                    }
                });
            }

        }

        /**
         * 保存栏目信息
         */
        function saveColumnConfigEO() {
            cur.vm.synColumnNames = $("#synColumnNames").val();
            cur.vm.synColumnIds = $("#synColumnIds").val();
            cur.vm.genePageNames = $("#genePageNames").val();
            cur.vm.genePageIds = $("#genePageIds").val();
            cur.vm.contentModelCode = $("#contentModelCode").val();
            cur.vm.siteId = GLOBAL_SITEID;
            cur.vm.type = 'CMS_Section';
            //Ls.cookies("contentModelCode", cur.vm.contentModelCode);
            //Ls.cookies("selectCode", selectCode);
            selectContentModelCode = cur.vm.contentModelCode;

            var value = Ls.toJSON(cur.vm.$model);
            Ls.ajax({
                type: "POST",
                url: "/columnConfig/saveColumnEO",
                data: value
            }).done(function (text) {
                if (!text.status) {
                    Ls.tipsInfo(text.desc);
                    return;
                } else {
                    flag = 1;
                    columnManage.indicatorId = text.data;
                    var nodes = treeObj.getSelectedNodes();
                    if (nodes.length > 0) {
                        if (isEdit == 1) {
                            treeObj.reAsyncChildNodes(nodes[0].getParentNode(), "refresh");
                            Ls.tipsOk("保存成功");
                            return;
                        } else {
                            if (nodes[0].children != null) {
                                treeObj.reAsyncChildNodes(nodes[0], "refresh");
                            } else {
                                var newNodes = {};
                                treeObj.addNodes(nodes[0], newNodes);
                                treeObj.reAsyncChildNodes(nodes[0], "refresh");
                            }
                        }
                    }
                    Ls.tipsOk("保存成功");
                    return;
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
