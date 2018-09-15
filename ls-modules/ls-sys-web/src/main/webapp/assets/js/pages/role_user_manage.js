var userManage = function () {

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
                title: "title"
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 10)
        App.initSlimScroll(ui_tree);
    }

    var organ = function () {

        //初始化布局
        mini.parse();

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/organ/getInternalSubOrgans",
                autoParam: ["id=parentId", "dn=parentDn"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {

                    var nodes = treeObj.getNodes();
                    if (nodes.length > 0) {
                        treeObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    initSlimScroll();
                }
            }
        });

        treeObj = $.fn.zTree.init($("#ui_tree"), settings);


        function dataFilter(treeId, parentNode, responseData) {
            var responseData = responseData.data;
            /*if (responseData) {
             for (var i = 0; i < responseData.length; i++) {
             var node = responseData[i];
             if (node.pId > 0) {
             node.icon = "../assets/images/site.gif";
             } else {
             node.icon = "../assets/images/structure.gif";
             }
             }
             }*/
            if (isPush) {
                if (Ls.isArray(responseData)) {
                    for (var i = 0, l = responseData.length; i < l; i++) {
                        var el = responseData[i];
                        el.pId = 1
                        el.pid = 1
                    }
                }
                //添加根节点
                responseData.push({
                    "id": 1,
                    "pId": null,
                    "name": "机构列表",
                    "nodeType": "OrganTop",
                    "icon": "assets/images/virtual_organ.gif",
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": true
                })
                isPush = false;
            }
            if (Ls.isArray(responseData)) {
                for (var i = 0, l = responseData.length; i < l; i++) {
                    var el = responseData[i];
                    if (el.nodeType == 'Organ') {
                        el.icon = "assets/images/organ.gif";
                    } else if (el.nodeType == 'OrganUnit') {
                        el.icon = "assets/images/organunit.gif";
                    }

                }
            }
            return responseData;
        };


        function onExpand(event, treeId, treeNode) {
        }

        var treeObj, isExpand, isPush = true, clickNode;

        function addDiyDom(treeId, node) {

            var aObj = $("#" + node.tId + "_a");
            var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"> </span>';
            var editBtn = ' <span class="button edit-a" id="editBtn_' + node.id + '" title="' + node.name + '"> </span>';
            var delBtn = ' <span class="button del-a" id="delBtn_' + node.id + '" title="' + node.name + '"> </span>';

            // var addStr = '<span class="button icon_add" title="" id="addNode' + node.id + '" onclick="addNode(this,' + node.id + ',\'' + node.nodeType + '\'' + ',event)"></span>';

            if (!node.isParent && !node.hasOrgans && !node.hasPersons) {
                addBtn = addBtn + delBtn
            }
            aObj.after(addBtn)

            $addBtn = $("#addBtn_" + node.id);
            $editBtn = $("#editBtn_" + node.id);
            $delBtn = $("#delBtn_" + node.id);
            //添加
            $addBtn && $addBtn.on("click", function () {
                getData("", node.id, node.nodeType);
                $(".portlet").show();
                return false;
            });

//      //修改
//      $editBtn && $editBtn.on("click", function () {
//        //获取数据绑定
//        getData(node.id,"","");
//        return false;
//      });

            //删除
            $delBtn && $delBtn.on("click", function () {
                deleteNode(node.id, node.pid);
                return false;
            });

        }

        //拉取数据
        var getData = function (id, pid, nType) {
            if (id == "") {
                clickNode = treeObj.getNodeByParam("id", pid, null);
                pid = (pid == 1 ? "" : pid);
            }
            Ls.ajaxGet({
                url: "/organ/getOrgan",
                data: {
                    organId: id,
                    parentId: pid
                }
            }).done(function (d) {

                $(".portlet").show();
                //所有数据格式遵循EOA的标准,或看data1.txt
                data = d.data;
                if (pid != '') {
                    data.pid = pid;
                }
                //定义唯一ID，和页面上的 ms-controller 相同
                data.$id = "organ_form";
                data.disDw = false;
                data.disBm = false;
                data.isParentFictitious = 0;
                data.fictitious = 0;
                if (id == '') {
                    if (nType == "OrganTop") {
                        data.disBm = true;
                    } else if (nType == "OrganUnit") {
                        data.disDw = true;
                    }
                } else {
                    data.disBm = true;
                    data.disDw = true;
                }

                //重置表单
                data.resetForm = function () {
                    $("#organ_form")[0].reset();
                }

                //绑定模型
                if (!avalon.vmodels[data.$id]) {
                    cur.model = avalon.define(data);
                    cur.isModel = true;
                } else {
                    Ls.assignVM(cur.model, data);
                }

                //扫描模型
                avalon.scan();
            }).fail(function () {
                alert('失败!')
            })
        };

        function onClick(event, treeId, node) {
            if (node.id == 1) {
                treeObj.cancelSelectedNode(node);
                treeObj.expandNode(node);
                event.stopPropagation();
            } else {
                clickNode = node;
                getData(node.id, "", "");
                return false
            }
        }

        var addPost = function () {
            var value = cur.model.$model
            var data = Ls.toJSON(value);
            var organId = value.organId;
            var url = "/organ/updateOrgan";
            if (organId == null) {
                url = "/organ/saveOrgan"
            }
            Ls.ajax({
                url: url,
                data: JSON.parse(data)
            }).done(function (d) {
                if (d.status == 1) {
                    alert("操作成功");
                    $("#organ_form")[0].reset();
                    editNodeAfterFn(d.data);
                } else {
                    alert(d.desc);
                }
            }).fail(function () {
                alert('失败!');
            })
            return false;
        }

        function editNodeAfterFn(treeNode) {
            var me = this;
            if (treeNode == null)
                return;

            if (treeNode.pid == null) {
                treeNode.pid = 1;
            }
            var curNode = clickNode;
            if (treeNode.nodeType == "OrganUnit") {
                icon = "";
            } else if (treeNode.nodeType == "Organ") {
                icon = "";
            }
            if (treeNode.update) {
                curNode.name = treeNode.name;
                curNode.pid = treeNode.pid;
                treeObj.updateNode(curNode);
            } else {
                if ((treeNode.pid != null || treeNode.pid > 0) && curNode) {
                    var chkNode = treeObj.getNodeByParam("id", treeNode.pid, null);
                    if (chkNode) {
                        if (chkNode.open || !chkNode.isParent) {
                            treeObj.addNodes(curNode, treeNode);
                        } else {
                            treeObj.expandNode(chkNode, true);
                        }
                    }
                } else {
                    treeObj.addNodes(null, treeNode);
                }
//			var delNode = $("#delNode" + treeNode.pid);
//            delNode.hide();
            }
        }

        var deleteNode = function (id, pid) {
            if (confirm('真的要删除吗？')) {
                var treeNode = treeObj.getNodeByParam("id", id, null);
                Ls.ajaxGet({
                    url: "/organ/deleteOrgan",
                    data: {
                        organId: id
                    },
                    success: function (text) {
                        if (text.status == 1) {
                            alert("删除成功");
                            var preNode = treeNode.getPreNode();
                            var nextNode = treeNode.getNextNode();
                            var pNode = treeNode.getParentNode();
                            //存在兄弟节点或无父节点时，移除被删除的节点即可
                            if (preNode != null || nextNode != null || pNode == null) {
                                treeObj.removeNode(treeNode);
                            } else {
                                var ppNode = pNode.getParentNode();
                                if (ppNode != null) {
                                    treeObj.reAsyncChildNodes(ppNode, "refresh");
                                } else {
                                    treeObj.refresh();
                                }
                            }
                        } else {
                            alert(text.desc);
                        }
                    }
                });
            }
        }

        $('#organ_form').validator({
            fields: {
                'name': '名称:required;',
                'sortNum': '名称:required;'
            },
            valid: function () {
                addPost()
            }
        })

    }


    //--------------------------------------------------------------user---------------------------------
    var user = function () {

        //初始化布局
        mini.parse();

        //实例化datagrid
        cur.grid = mini.get("datagrid1");
        cur.grid.load({dataFlag: 1, organId: cur.organId});

        var treeObj, isPushUser = true;

        var settings = $.extend(true, ztree_settings, {
            view: {
                addDiyDom: addDiyDom
            },
            async: {
                enable: true,
                url: "/organ/getInternalSubOrgansAndPersons",
                autoParam: ["id=parentId", "dn=parentDn"],
                dataFilter: dataFilterUser
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    var nodes = treeObj.getNodes();
                    if (nodes.length > 0) {
                        treeObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    initSlimScroll();

                }
            }
        });

        treeObj = $.fn.zTree.init($("#ui_tree"), settings);

        function dataFilterUser(treeId, parentNode, responseData) {
            var responseData = responseData.data;
            if (isPushUser) {
                if (Ls.isArray(responseData)) {
                    for (var i = 0, l = responseData.length; i < l; i++) {
                        var el = responseData[i];
                        el.pid = 1
                    }
                }
                //添加根节点
                responseData.push({
                    "id": 1,
                    "pId": null,
                    "name": "机构列表",
                    "nodeType": "OrganTop",
                    "icon": "assets/images/virtual_organ.gif",
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    }
                    // "isParent": true
                })
                isPushUser = false;
            }
            if (Ls.isArray(responseData)) {
                for (var i = 0, l = responseData.length; i < l; i++) {
                    var el = responseData[i];
                    if (el.nodeType == 'Organ') {
                        el.icon = "assets/images/organ.gif";
                    } else if (el.nodeType == 'OrganUnit') {
                        el.icon = "assets/images/organunit.gif";
                    } else if (el.nodeType == 'Person') {
                        el.icon = "assets/images/person.gif";
                    }

                }
            }
            return responseData;
        };

        function addDiyDom(treeId, node) {
            var aObj = $("#" + node.tId + "_a");
            var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"> </span>';
            var addBtn_, editBtn_, delBtn_;

            if (node.type == "OrganUnit") {
                aObj.append(addBtn);
            }

            $addBtn = $("#addBtn_" + node.id);
            //添加
            $addBtn && $addBtn.on("click", function () {
                editUser();
                return false;
            });

        }

        function onClick(event, treeId, node) {
            if (node.id == 1) {
                treeObj.expandNode(node);
                event.stopPropagation();
            } else {
                cur.organId = node.id;
                cur.grid.load({dataFlag: 1, organId: cur.organId});
            }
        }

    }

    function editUser(id) {
        Ls.openWin("user_add.html?id=" + (id ? "" : id), {
            type: 2,
            title: '用户管理',
            maxmin: false,
            area: ['500px', '520px']
        });
    }

    return {
        organ: organ,
        user: user
    }

}();