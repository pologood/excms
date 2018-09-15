var cur = {
    isLoadRoot: false,
    siteId: GLOBAL_SITEID,
    clickOrganId: '',
    organId: '',
    personId: '',
    isRemoveNode: '',
    ztree: '',
    clickId: '',
    clickType: ''
};
var unitManage = function () {

    var isInit = false;

    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast",
        },
        async: {
            enable: true,
            type: "GET",
            dataType: "JSON",
            url: "",
            otherParam: {},
            autoParam: ""
        },
        callback: {}
    };

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 10);
        //App.initSlimScroll(ui_tree);
        App.initContentScroll(ui_layout.height() - 10, '#ui_tree', {right: true});
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var organ = function () {

        //初始化布局
        mini.parse();
        //表单必须同时定义,且名称必须相
        //
        // 同  id="organ_form" ms-controller="organ_form"
        cur.$id = "organ_form";
        cur.isRemoveNode = isRemoveNode;
        var url = '/organ/getInternalSubOrgans';
        //初始化表单字段
        cur.init = Ls.initFORM(cur.$id, {
            nodeType: "",
            isPublic: '',
            disDw: false,
            disBm: false,
            siteId: "",
            formReset: function () {
                Ls.assignVM(cur.vm, {
                    nodeType: cur.vm.nodeType,
                    isPublic: '0',
                    disDw: cur.vm.disDw,
                    disBm: cur.vm.disBm,
                    name: "",
                    simpleName: "",
                    officePhone: "",
                    officeAddress: "",
                    servePhone: "",
                    serveAddress: "",
                    organUrl: "",
                    description: ""
                });
            }
        });

        //如果模型已经绑定，不再绑定
        cur.vm = avalon.vmodels[cur.init.$id];
        if (!cur.vm) {
            //绑定模型
            cur.vm = avalon.define(cur.init);
        } else {
            Ls.assignVM(cur.vm, cur.init);
        }

        avalon.scan($("#organ_form").get(0), cur.vm);

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: url,
                autoParam: ["id=parentId", "dn=parentDn"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {

                    var nodes = cur.ztree.getNodes();
                    if (nodes.length > 0) {
                        cur.ztree.expandNode(nodes[0], true, false, true);
                        //cur.ztree.selectNode(nodes[0].children[0]);
                    }
                    //添加模拟滚动条
                    if (!isInit) {
                        //App.initContentScroll();
                        initSlimScroll()
                    }
                }
            }
        });

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData.data, Ls.treeDataType.UNIT);
            if (cur.isRemoveNode == '0') {
                if (!cur.isLoadRoot) {
                    //添加根节点
                    var responseData = {
                        "id": 0,
                        "pid": -1,
                        "name": "组织机构",
                        "nodeType": "OrganTop",
                        "icon": GLOBAL_CONTEXTPATH + "/assets/images/organ.gif",
                        "font": {
                            "font-weight": "bold",
                            "color": "#666666"
                        },
                        "isParent": true,
                        "children": responseData
                    }
                    cur.isLoadRoot = true;
                }
            }

            return responseData;
        };

        function addDiyDom(treeId, node) {

            var aObj = $("#" + node.tId + "_a");
            var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="添加"> </span>';
            var exportBtn = ' <span class="button import-a" id="exportBtn_' + node.id + '" title="导入"> </span>';
            var editBtn = ' <span class="button edit-a" id="editBtn_' + node.id + '" title="编辑"> </span>';
            var delBtn = ' <span class="button del-a" id="delBtn_' + node.id + '" title="删除"> </span>';
            var moveBtn = ' <span class="button move-a" id="moveBtn_' + node.id + '" title="移动"> </span>';
            var count = "";
            if(node.count != '' && node.count != 0 && node.count != '0'){
               count = '<i title="子单位数">['+node.count+']</i> ';
            }
            var btns = (node.type == "Organ")?count+addBtn+moveBtn+exportBtn:addBtn;
            // btns +=addBtn;
            if (!node.isParent && !node.hasOrgans && !node.hasPersons) {
                btns += delBtn
            }
            // if(node.type == "Organ"){
            //     btns +=moveBtn;
            //     btns +=exportBtn;
            // }
            aObj.after(btns)

            $addBtn = $("#addBtn_" + node.id);
            $editBtn = $("#editBtn_" + node.id);
            $exportBtn = $("#exportBtn_" + node.id);
            $delBtn = $("#delBtn_" + node.id);
            $moveBtn = $("#moveBtn_" + node.id);
            //添加
            $addBtn && $addBtn.on("click", function () {
                cur.clickId = node.id
                cur.clickType = node.nodeType
                getData("", node.id, node.nodeType);
                $(".portlet").show();
                return false;
            });

            //导入
            $exportBtn && $exportBtn.on("click", function () {
                exportExl(node.id, node.pid, node.name);
                return false;
            });
            //移动
            $moveBtn && $moveBtn.on("click", function () {
                moveOrgan(node.id, node.pid);
                return false;
            });

            //删除
            $delBtn && $delBtn.on("click", function () {
                deleteNode(node.id, node.pid);
                return false;
            });

        }

        function exportExl(id, pid, name) {
            var treeNode = cur.ztree.getNodeByParam("id", pid, null);
            Ls.openWin("/organ/unitExport?name=" + name + "&organId=" + id, "500px", "200px", {
                type: 2,
                title: '组织导入',
                maxmin: false,
                close: function (arg) {
                    if (treeNode != null && treeNode.id != 1) {
                        cur.ztree.reAsyncChildNodes(treeNode, "refresh");
                    } else {
                        cur.isLoadRoot = false;
                        unitManage.organ();
                    }
                }
            });
        }

        function moveOrgan(id, pid) {
            Ls.selectUnitUser(function(arg){
                if(arg && arg.list.length>0){
                    if(arg && arg.ids && arg.names){
                       // console.info(arg.ids);
                       // console.info(arg.names);
                        Ls.tipsOk("组织移动中,请稍后...");
                        Ls.ajaxGet({
                            url: "/organ/saveMoveOrgan",
                            data: {
                                selectOrganId: arg.ids,
                                clickOrganId: id,
                            }
                        }).done(function (d) {
                            if(d.status == 1){
                                Ls.tipsOk("操作成功!");
                                try{
                                    var treeNodeC = cur.ztree.getNodeByParam("id", pid, null);
                                    var isReload = false
                                    if (treeNodeC != null && treeNodeC.id != 1) {
                                        cur.ztree.reAsyncChildNodes(treeNodeC, "refresh");
                                    }else {
                                        isReload = true;
                                    }
                                    var treeNodeS = cur.ztree.getNodeByParam("id", arg.ids, null);
                                    if (treeNodeS != null && treeNodeS.id != 1) {
                                        cur.ztree.reAsyncChildNodes(treeNodeS,"refresh");
                                    }else {
                                        isReload = true;
                                    }
                                    if(isReload){
                                        cur.isLoadRoot = false;
                                        unitManage.organ();
                                    }
                                }catch (e){}
                            }else{
                                Ls.tipsErr(d.desc);
                            }
                        })
                    }
                }
            },{
                scope:3,
                scopeType:'single',
                organIds:'',
                data:{
                    list: {list:[]}
                }
            });
        }


        function onClick(event, treeId, node) {
            if (node.id == 1) {
                cur.ztree.cancelSelectedNode(node);
                cur.ztree.expandNode(node);
                event.stopPropagation();
            } else {
                cur.clickNode = node;
                getData(node.id, node.pid, "");
                return false
            }
        }

        //初始化树
        cur.ztree = $.fn.zTree.init($("#ui_tree"), settings);

        //拉取数据
        var getData = function (id, pid, nType) {
            pid = (pid == 1 ? "" : pid);
            if (id == "") {
                cur.clickNode = cur.ztree.getNodeByParam("id", pid, null);
            }
            // if (cur.isUnit != '1') {
            //     if (pid == "") {
            //         $(".siteId").removeClass("hide")
            //     } else {
            //         $(".siteId").addClass("hide")
            //     }
            // }
            Ls.ajaxGet({
                url: "/organ/getOrgan",
                data: {
                    organId: id,
                    parentId: pid
                }
            }).done(function (d) {

                $(".portlet").show();

                data = d.data;
                if (pid != '') {
                    data.pid = pid;
                }
                data.isParentFictitious = 0;
                data.fictitious = 0;
                if (id == '') {
                    data.disBm = false;
                    data.disDw = false;
                    data.isPublic = 0;
                    if (nType == "OrganTop") {
                        data.disBm = true;
                    } else if (nType == "OrganUnit") {
                        data.disDw = true;
                    }
                } else {
                    data.disBm = true;
                    data.disDw = true;
                }
                cur.vm = avalon.vmodels[cur.$id];

                if (cur.vm) {
                    //重新更新模型数据
                    Ls.assignVM(cur.vm, data);
                }

            })
        };

//        var getSites = function (siteId) {
//            Ls.ajaxGet({
//                url: "/siteMain/getAllSites",
//            }).done(function (text) {
//                var str = '<option value="">--选择站点--</option>';
//                if (text.length > 0) {
//                    for (i = 0; i < text.length; i++) {
//                        str += ' <option  value=' + text[i].indicatorId + '>' + text[i].name + '</option> '
//                    }
//                }
//                $("#siteId").html(str);
//                $("#siteId").val(siteId)
//            });
//        }

        var addPost = function () {
            var data = Ls.toJSON(cur.vm.$model)
            var organId = data.organId;
//            data.siteId = $("#siteId").val();
            var url = "/organ/updateOrgan";
            if (organId == null) {
                url = "/organ/saveOrgan"
            }
            Ls.ajax({
                url: url,
                data: data
            }).done(function (d) {
                if (d.status == 1) {
                    Ls.tipsOk("保存成功！")
                    try {
                        editNodeAfterFn(d.data);
                    } catch (e) {
                    }

                    if (organId == null) {
                        getData("", cur.clickId, cur.clickType);
                    } else {
                        getData(d.data.id, d.data.pid, "");
                    }
                } else {
                    Ls.tipsErr(d.desc)
                }
            })
            return false;
        }

        function editNodeAfterFn(treeNode) {
            var me = this;
            if (treeNode == null)
                return;
//            if (treeNode.pid == null) {
//                treeNode.pid = 1;
//            }
            var curNode = cur.clickNode;

            if (treeNode.nodeType == "OrganUnit") {
                treeNode.icon = GLOBAL_CONTEXTPATH + "/assets/images/organunit.gif";
            } else if (treeNode.nodeType == "Organ") {
                treeNode.icon = GLOBAL_CONTEXTPATH + "/assets/images/organ.gif";
            }
            if (treeNode.update) {
                curNode.name = treeNode.name;
                curNode.pid = treeNode.pid;
                cur.ztree.updateNode(curNode);
            } else {
                if ((treeNode.pId != null || treeNode.pId > 0) && curNode) {
                    var chkNode = cur.ztree.getNodeByParam("id", treeNode.pId, null);
                    if (chkNode) {
                        if (chkNode.open || !chkNode.isParent) {
                            cur.ztree.addNodes(curNode, treeNode);
                        } else {
                            cur.ztree.expandNode(chkNode, true);
                        }
                    }
                } else {
                    var chkNode = cur.ztree.getNodeByParam("id", '1', null)
                    cur.ztree.addNodes(chkNode, treeNode);
                }
                var delNode = $("#delBtn_" + treeNode.pId);
                delNode.hide();
            }
        }

        var deleteNode = function (id, pid) {
            if (confirm('真的要删除吗？')) {
                var treeNode = cur.ztree.getNodeByParam("id", id, null);
                Ls.ajaxGet({
                    url: "/organ/deleteOrgan",
                    data: {
                        organId: id
                    },
                    success: function (text) {
                        if (text.status == 1) {
                            Ls.tipsOk("删除成功！")
                            var preNode = treeNode.getPreNode();
                            var nextNode = treeNode.getNextNode();
                            var pNode = treeNode.getParentNode();
                            //存在兄弟节点或无父节点时，移除被删除的节点即可
                            if (preNode != null || nextNode != null || pNode == null) {
                                cur.ztree.removeNode(treeNode);
                            } else {
                                var ppNode = pNode.getParentNode();
                                if (ppNode != null && ppNode.id != 1) {
                                    cur.ztree.reAsyncChildNodes(ppNode, "refresh");
                                } else {
                                    cur.isLoadRoot = false;
                                    unitManage.organ();
                                }
                            }
//                            cur.ztree.removeNode(treeNode); // 删除树节点
//                            if (!treeNode.isParent) {
//                                var delNode = $("#delBtn_" + treeNode.pid);
//                                var pNode = cur.ztree.getNodeByParam("id", treeNode.pid, null);
//                                if (!pNode.isParent) {
//                                    if (delNode.length > 0) {
//                                        delNode.show();
//                                    } else {
//                                        if (treeNode.pid != 1) {
//                                            var addNode = $("#addBtn_" + treeNode.pid);
//                                            var delBtn = ' <span class="button del-a" id="delBtn_' + treeNode.id + '" title="' + treeNode.name + '"> </span>';
//                                            console.info(delBtn)
//                                            addNode.after(delBtn);
//                                        }
//                                    }
//                                }
//                            }
                        } else {
                            Ls.tipsErr(text.desc)
                        }
                    }
                });
            }
        }


        $('#organ_form').validator({
            fields: {
                'name': '名称:required;length[1~30]',
                'code': '编码:length[1~20]',
                'sortNum': '排序:required;integer;range[0~99999]',
                'headPerson': '单位负责人:length[~20];',
                'positions': '职务:length[~64];',
                'description': '简介:length[~200];',
                'officePhone': '办公电话:length[~20]',
                'officeAddress': '办公地址:length[~60]'
            },
            valid: function () {
                addPost()
            }
        })

    }
    return {
        organ: organ,
    }
}();
