var user_select = function () {
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
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var user = function () {
        var isExpand;
        function addDiyDom(treeId, node) {
            var aObj = $("#" + node.tId + "_a");
            //
            // if(node.isParent) {
            //     var opt = '&nbsp<label><span><input id="check_' + node.id + '" type="checkbox" checked><span style="color: #1e4217">全选</span></label></span>';
            //     aObj.after(opt);
            //     var btn = $("#check_" + node.id);
            //     btn && btn.on("click", function () {
            //         checkNode(node);
            //         return false;
            //     });
            //
            //     return;
            // }

            if (curr.roleCode == "root") {
                if(node.roleCode != null) {
                    var addStr = ' <span class="button del-role-a" id="delBtn_' + node.id + '" title="移除角色"> </span>';
                    aObj.after(addStr);
                    var btn = $("#delBtn_" + node.id);
                    btn && btn.on("click", function () {
                        delUserRole(node);
                        return false;
                    });
                }
            } else if(curr.roleCode == "superAdmin") {
                if(node.roleCode != null && node.roleCode.indexOf("super_admin") == -1 && node.roleCode.indexOf("root") == -1) {
                    var addStr = '<span class="button del-role-a" id="delBtn_' + node.id + '" title="移除角色"> </span>';
                    aObj.after(addStr);
                    var btn = $("#delBtn_" + node.id);
                    btn && btn.on("click", function () {
                        delUserRole(node);
                        return false;
                    });
                }
            } else if(curr.roleCode == "siteAdmin") {
                if(node.roleCode != null && node.roleCode.indexOf("super_admin") == -1 && node.roleCode.indexOf("root") == -1 && node.roleCode.indexOf("site_admin") == -1) {
                    var addStr = '<span class="button del-role-a" id="delBtn_' + node.id + '" title="移除角色"> </span>';
                    aObj.after(addStr);
                    var btn = $("#delBtn_" + node.id);
                    btn && btn.on("click", function () {
                        delUserRole(node);
                        return false;
                    });
                }
            }
        }

        function checkNode(node) {
            curr.treeObj.checkNode(node,true,true);
        }

        function delUserRole(node) {
            if(confirm("确认删除角色?")) {
                Ls.ajaxGet({
                    url: '/system/roleAsg/removeUserRoles',
                    data: {
                        roleId: curr.roleId,
                        userId:node.userId,
                        organId:node.organId
                    },
                    success: function (resp) {
                        if (resp.status == '1') {
                            Ls.tips("删除成功!", {times: 2});
                            curr.win.callback_user_role_del();
                            node.name = node.realName;
                            node.chkDisabled = false;
                            node.checked = false;
                            $("#delBtn_" + node.id).hide();
                            curr.treeObj.updateNode(node);
                        } else {
                            Ls.tips("删除失败!", {icons:"error"});
                        }

                    }
                });
            }
            //search();
        }

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData,Ls.treeDataType.USER);
            var temp = [];
            for(var i=0;i<responseData.length;i++) {
                var node = responseData[i];
                node.realName = node.name;
                if(node.type != "Person") {
                    // responseData[i].nocheck = true;
                } else {
                    if(node.roleName != null && node.roleName != "") {
                        node.name = node.name + ":<span style='color: #f88897'>" + node.roleName + "</span>";
                    }
                    if(node.enabled == false && node.checked == false) {
                        node.chkDisabled = true;
                    }

                    if(node.checked) {
                        node.chkDisabled = true;
                    }
                }
                if(curr.roleCode != "root" && curr.roleCode != "superAdmin" && curr.roleCode != "siteAdmin") {
                    if(node.unitId == curr.win.GLOBAL_PERSON.unitId && node.type == "Person") {
                        temp.push(node);
                    } else if(node.type != "Person") {
                        if(node.id == "OrganEO" + curr.win.GLOBAL_PERSON.unitId) {
                            temp.push(node);
                            curr.unitPid = node.pid;
                        } else if(node.id == "OrganEO" + curr.win.GLOBAL_PERSON.organId) {
                            temp.push(node);
                        }
                    }
                } else {
                    temp.push(node);
                }
            }

            for(var i=0;i<responseData.length;i++) {
                var node = responseData[i];
                if(node.id == curr.unitPid) {
                    temp.push(node);
                }
            }
            return temp;
        };

        /*function onClick(event, treeId, node) {
            if (node.pId > 0) {
            } else {
                curr.treeObj.cancelSelectedNode(node);
                curr.treeObj.expandNode(node);
            }
            event.stopPropagation();
        }*/

        function onClick(event, treeId, node) {
            if (node.isParent) {
                node.expand = node.open;
                curr.treeObj.expandNode(node, !node.expand, false, true);
                node.open = !node.expand;
            } else {
                curr.treeObj.checkNode(node, !node.checked, true);
            }
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            check: {
                enable: true
            },
            async: {
                url: "/system/roleAsg/getTreeNodes",
                dataFilter: dataFilter,
                otherParam: {"name":curr.ser_key.val(),"roleId":curr.roleId}
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function (event, treeId, treeNode, msg) {
                    var ckNodes = curr.treeObj.getCheckedNodes(true);
                    if (ckNodes != null) {
                        for(var i=0;i<ckNodes.length;i++) {
                            curr.treeObj.checkNode(ckNodes[i], true, true);
                        }
                    }

                    var nodes = curr.treeObj.getNodes();

                    var x = curr.treeObj.getNodesByFilter(function(node){
                        return (node.name == '演示用户' );
                    }, true)
                    console.log(x.getParentNode());
                    curr.treeObj.expandNode(x.getParentNode(), true, false, true, true);
                    if (nodes.length > 0) {
                        // curr.treeObj.expandNode(nodes[0], true, true, true);
                    }
                }
            }
        });
        curr.treeObj = $.fn.zTree.init($("#ui_tree"), settings);
    }

    return {
        user: user
    }

}();