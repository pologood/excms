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
        cur.$id = "columnInfoForm";

        var data = {
            $id: cur.$id,
            name: '',
            siteId: GLOBAL_SITEID,
            parentId: null,
            isParent: 0,
            indicatorId: null,
            columnConfigId: null,
            sortNum: null,
            uri: '',
            contentModelCode: '',
            transUrl: '',
            isStartUrl: 0,
            transWindow: 0,
            keyWords: '',
            description: '',
            synColumnNames: '',
            synColumnIds: '',
            genePageIds: '',
            genePageNames: '',
            type: 'CMS_Section',
            relId: null,
            isLogo: 0,
            height: '',
            width: '',
            num: 0
        }

        cur.vm = avalon.vmodels[data.$id];
        if (!cur.vm) {
            cur.vm = avalon.define(data);
        }


        var isPush = false;

        function addDiyDom(treeId, node) {
            var sObj = $("#" + node.tId + "_a");
            if ($("#nodeOp_" + node.indicatorId).length > 0)return;
            var nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>' +
                ' <span id="update' + node.indicatorId + '"  class="button edit-a" title="修改">' + '</span>' +
                ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>';
            if (node.type != 'COM_Secton') {
                nodeOpStr += ' <span id="move' + node.indicatorId + '"  class="button move-a" title="移动">' + '</span>';
            }
            if (node.type == 'SUB_Site') {
                nodeOpStr = '';
            }
            if (node.parentId == GLOBAL_SITEID && node.type == 'COM_Section') {
                nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>';
            }
            nodeOpStr = nodeOpStr + '</span>';
            sObj.after(nodeOpStr);
            // 绑定按钮事件,使用事件绑定的好处就是能传递复杂对象作为参数
            var addBtn = $('#add' + node.indicatorId), updateBtn = $('#update' + node.indicatorId),
                delBtn = $('#del' + node.indicatorId), moveBtn = $('#move' + node.indicatorId);
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
            $("#column_tab").css('display', '');
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
                if (Ls.isEmpty(data.contentModelCode) && Ls.cookies("contentModelCode") != null) {
                    data.contentModelCode = Ls.cookies("contentModelCode");
                }
                Ls.assignVM(cur.vm, data);
                avalon.scan($("#columnInfoForm").get(0), cur.vm);
                cur.vm.sortNum = d.data;
                cur.vm.type = 'CMS_Section';
                changeUrl();
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
            });
        }

        function editNode(node) {
            isEdit = 1;
            treeObj.selectNode(node);
            if (node.parentId == GLOBAL_SITEID && node.type == 'COM_Section') {
                $("#column_tab").css('display', 'none');
            } else {
                $("#column_tab").css('display', '');
                Ls.ajaxGet({
                    type: "POST",
                    url: "/siteMain/getFirstColumnEO",
                    data: {indicatorId: node.indicatorId, isCom: false}
                }).done(function (d) {
                    var data = d;
                    data.contentModelCode = Ls.isEmpty(data.contentModelCode) ? "" : data.contentModelCode;

                    if (!data.isHave) {
                        var cStr = '';
                        for (i = 0; i < cur.length; i++) {
                            if (cur.contentList[i].modelTypeCode == data.columnTypeCode) {
                                cStr += ' <option  value=' + cur.contentList[i].code + '>' + cur.contentList[i].name + '</option> ';
                            }
                        }
                        $("#contentModelCode").html('');
                        $("#contentModelCode").append(cStr);
                    } else {
                        $("#contentModelCode").html('');
                        $("#contentModelCode").append(cur.contentModelStr);

                    }

                    cur.$id = "columnInfoForm";
                    Ls.assignVM(cur.vm, data);
                    avalon.scan($("#columnInfoForm").get(0), cur.vm);
                    changeUrl();
                    if (data.columnTypeCode == "articleNews") {
                        $("#synAndGener").show();
                        $("#isStart").show();
                    } else {
                        $("#synAndGener").hide();
                        if (data.columnTypeCode == "redirect" || data.columnTypeCode == 'sceneService' || data.columnTypeCode == 'workGuide') {
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

                });
            }
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
                        Ls.tipsInfo(text.desc);
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
                var root = {
                    "indicatorId": GLOBAL_SITEID,
                    "name": GLOBAL_SITENAME,
                    "type": 'SUB_Site',
                    "isParent": true,
                    "title": GLOBAL_SITENAME,
                    "icon": GLOBAL_CONTEXTPATH + "/assets/images/site.png",
                    "siteId": null,
                    children: responseData
                }
                isPush = true;
                return root;
            } else {
                return responseData;
            }

        }

        function onClick(event, treeId, node) {
            if (node.isParent) {
                treeObj.expandNode(node);
            }
            if (node.type == 'SUB_Site') {
            } else {
                editNode(node);
                columnManage.indicatorId = node.indicatorId;
            }
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/siteMain/getColumnTree",
                dataFilter: dataFilter,
                autoParam: ["indicatorId=indicatorId"],
                otherParam: {"indicatorId": GLOBAL_SITEID, "siteId": GLOBAL_SITEID}
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
                onAsyncSuccess: onAsyncSuccess
            }
        });

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
                        if (nodes[0].children[0].parentId == GLOBAL_SITEID && nodes[0].children[0].type == 'COM_Section') {

                        } else {
                            getFirstColumnEO(nodes[0].children[0].indicatorId);
                        }
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
                data: {indicatorId: indicatorId, isCom: false}
            }).done(function (data) {
                data.contentModelCode = Ls.isEmpty(data.contentModelCode) ? "" : data.contentModelCode;
                Ls.assignVM(cur.vm, data);
                //cur.vm.contentModelCode = data.contentModelCode;
                //扫描模型
                avalon.scan($("#columnInfoForm").get(0), cur.vm);
                changeUrl();

                //重新美化表单
                //avalon.nextTick(App.initUniform);
                if (!data.isHave) {
                    var str = '';
                    for (i = 0; i < cur.length; i++) {
                        if (cur.contentList[i].modelTypeCode == data.columnTypeCode) {
                            str += ' <option  value=' + cur.contentList[i].code + '>' + cur.contentList[i].name + '</option> ';
                        }
                    }
                    $("#contentModelCode").html('');
                    $("#contentModelCode").append(str);
                } else {
                    $("#contentModelCode").html('');
                    $("#contentModelCode").append(cur.contentModelStr);
                }

            });
        }


        $('#columnInfoForm').validator({
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
                Ls.tips("关键词的字数长度应小于1000个字符", {icons: "info", times: 30000});
                return;
            }
            if (cur.vm.description != null && Ls.strLen(cur.vm.description) > 1000) {
                Ls.tips("描述的字数长度应小于1000个字符", {icons: "info", times: 30000});
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
            cur.vm.siteId = GLOBAL_SITEID;
            cur.vm.contentModelCode = $("#contentModelCode").val();
            Ls.cookies("contentModelCode", cur.vm.contentModelCode);
            var value = Ls.toJSON(cur.vm.$model);

            Ls.ajaxGet({
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
