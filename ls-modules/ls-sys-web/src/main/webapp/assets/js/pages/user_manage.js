var cur = {
    isLoadRoot: false,
    siteId: GLOBAL_SITEID,
    clickOrganId: '',
    organId: '',
    personId: '',
    ids: '',
    url: '',
    isUnit: '',
    ztree: '',
    clickId: '',
    systemFlag: '',
    systemFlagCiphertext: '',
    clickType: ''
};
var personManage = function () {

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
        ui_tree.attr("data-height", ui_layout.height() - 10)
        App.initSlimScroll(ui_tree);
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var user = function () {

        //初始化布局
        mini.parse();
        //实例化datagrid
        cur.grid = mini.get("datagrid1");
        //重置 datagrid 高度
        Ls.mini_datagrid_height(cur.grid, 55);
        cur.grid.setUrl("/person/getPersonsPage");
        // cur.grid.load({dataFlag: 1, organId: cur.organId,isUnit:cur.isUnit});
        cur.$id = "user_form";
        //初始化表单字段
        cur.init = Ls.initFORM(cur.$id, {
            disUid: false,
            upRoleIds: '',
            upRoleNames: '',
            viRoleIds: '',
            viRoleNames: '',
            updateRole: false,
            personType: ''
        });

        //如果模型已经绑定，不再绑定
        cur.vm = avalon.vmodels[cur.init.$id];
        if (!cur.vm) {
            //绑定模型
            cur.vm = avalon.define(cur.init);
        } else {
            Ls.assignVM(cur.vm, cur.init);
        }
        avalon.scan($("#user_form").get(0), cur.vm);

        var settings = $.extend(true, ztree_settings, {
            view: {
                addDiyDom: addDiyDom
            },
            async: {
                enable: true,
                url: '/organ/getInternalSubOrgansAndPersons',
                autoParam: ["id=parentId", "dn=parentDn"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    try {
                        var nodes = cur.ztree.getNodes();
                        if (nodes.length == 1) {
                            cur.ztree.expandNode(nodes[0], true, false, true);
                        }
                    } catch (e) {
                    }
                    //添加模拟滚动条
//                    initSlimScroll();
                    App.initContentScroll(null, '#ui_tree', {right: true});

                }
            }
        });

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = Ls.treeDataFilter(responseData.data, Ls.treeDataType.USER);
            return responseData;
        };

        function addDiyDom(treeId, node) {
            var aObj = $("#" + node.tId + "_a");
            var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="添加"> </span>';
            var exportBtn = ' <span class="button import-a" id="exportBtn_' + node.id + '"  title="导入"> </span> ';
            var count = "";
            if (node.count != '' && node.count != 0 && node.count != '0') {
                count = '<i title="子单位数">[' + node.count + ']</i> ';
            }
            var btns;
            if (node.type == "OrganUnit") {
                btns = addBtn + exportBtn;
            }
            if (node.type == "Organ") {
                btns = count + exportBtn;
            }
            aObj.append(btns);

            $addBtn = $("#addBtn_" + node.id);
            $exportBtn = $("#exportBtn_" + node.id);
            //添加
            $addBtn && $addBtn.on("click", function () {
                editUser(node.id, "");
                return false;
            });

            //导入
            $exportBtn && $exportBtn.on("click", function () {
                exportExl(node.id, node.pid, node.dn, node.name);
                return false;
            });
        }

        function exportExl(id, pid, dn, name) {
            var treeNode = cur.ztree.getNodeByParam("id", pid, null);
            Ls.openWin("/person/personExport?dn=" + dn + "&name=" + name + "&organId=" + id, "500px", "250px", {
                type: 2,
                title: '用户导入',
                maxmin: false,
                close: function (arg) {
                    if (pid == 0) {
                        cur.isLoadRoot = false;
                        personManage.user();
                    } else {
                        cur.ztree.reAsyncChildNodes(treeNode, "refresh");
                    }
                }
            });
        }

        function onClick(event, treeId, node) {
            if (node.id == 1) {
                cur.ztree.expandNode(node);
                event.stopPropagation();
            } else {
                $(".userAdd").hide();
                $(".userList").show();
                cur.organId = '';
                cur.personId = '';
                cur.clickOrganId = '';
                if (node.nodeType == "Person") {
                    cur.organId = node.organId;
                    cur.personId = node.personId
                    cur.clickOrganId = node.organId;
                    cur.userId = node.userId;
                    editUser(cur.organId, cur.personId)
                    menu_select.menu();
                    site_opt_tree.siteOpt();
                    info_open_opt_tree.init();
//                    $("#btn_add").hide();
                } else {
                    cur.organId = node.id;
                    if (node.nodeType == "OrganUnit") {
                        cur.clickOrganId = node.id;
//                        $("#btn_add").show();
                    } else {
//                        $("#btn_add").hide();
                    }
                    cur.grid.load({dataFlag: 1, organId: cur.organId, personId: cur.personId});
                }
            }
        }

        cur.ztree = $.fn.zTree.init($("#ui_tree"), settings);

        function editUser(organId, personId) {
            cur.organId = organId;
            cur.personId = personId;
            $(".userList").hide();
            $(".userAdd").show();

            Ls.ajaxGet({
                url: "/ldap/getPerson",
                data: {
                    organId: cur.organId,
                    personId: cur.personId
                }
            }).done(function (d) {

                data = d.data;
                if (cur.personId == '') {
                    data.disUid = false;
                } else {
                    data.disUid = true;
                }
//                data.$id = "user_form";

//                cur.vm = avalon.define(data)
//				avalon.scan($("#ID").get(0),cur.vm);
                if (data.updateRole) {
                    $("#saveButton").hide();
                } else {
                    $("#saveButton").show();
                }
                if (data.ids == null) {
                    $("#password").val("");
                    $("#rePass").val("");
                }

                if (data.ids != null) {
                    cur.ids = data.ids;
                    $(".ssoPass").hide();
                    $(".ssoEdit").attr('disabled', true);
                    $("#updatePass").show();
                    cur.systemFlag = data.systemFlag;
                    cur.systemFlagCiphertext = data.systemFlagCiphertext;
                } else {
                    $(".ssoPass").show();
                    $("#updatePass").hide();
                    $(".ssoEdit").attr('disabled', false);
                }
                cur.vm = avalon.vmodels[cur.$id];
                if (cur.vm) {
                    //重新更新模型数据
                    Ls.assignVM(cur.vm, data);
                }

            })
//            Ls.openWin("/person/userEditPage?organId=" + organId, "500px", "530px", {
//                type: 2,
//                title: '用户添加',
//                maxmin: false,
//                close: function () {
//                    cur.grid.reload();
//                }
//            });
        }

        $('#user_form').validator({
            timely: 2,
            rules: {
                passRule: function (el, param, field) {
                    var isTrue = (cur.personId == "" ? true : false);
                    if (!isTrue) {
                        isTrue = ($("#password").val() == "" ? false : true);
                    }
                    return isTrue || '密码不能为空';
                }
            },
            fields: {
                'sortNum': '排序号:required;integer;range[0~99999]',
                'name': '姓名:required;length[2~10]',
                'uid': '账号:required;length[2~20]',
                'password': '密码:required(passRule);password2',
                'rePass': "确认密码:required(passRule);match(password);"
                // 'positions': '职务:length[~30]',
                // 'mobile': '手机:mobile',
                // 'officePhone': '办公电话:length[~20]',
                // 'officeAddress': '办公地址:length[~60]'
            },
            valid: function () {
                addPost()
            }
        })

        var addPost = function () {
            var data = Ls.toJSON(cur.vm.$model);
            data.password = $("#password").val();
            var url = "/person/updatePersonAndUser";
            if (cur.personId == '') url = "/person/savePersonAndUser";
            var roleIds = "";
            if (data.viRoleIds != null && data.viRoleIds != '' && data.viRoleIds != 'null') {
                roleIds = data.viRoleIds;
            }
            if (data.upRoleIds != null && data.upRoleIds != '' && data.upRoleIds != 'null') {
                roleIds = (roleIds == '' ? '' : roleIds + ",") + data.upRoleIds;
            }

            data.roleIds = roleIds;
            Ls.ajax({
                url: url,
                data: data
            }).done(function (d) {
                if (d.status == 1) {
                    Ls.tips("保存成功！", {
                        icons: "success", callback: function () {
                            var data = d.data;
                            data.nodeType = "Person";
                            data.icon = "assets/images/person.gif";
                            try {
                                data.organId = cur.organId;
                                data.personId = data.id;
                                cur.userId = data.userId;
                                editNodeAfterFnUser(data);
                            } catch (e) {
                            }
                            editUser(cur.organId, cur.personId)
                        }
                    })

                } else {
                    Ls.tipsErr(d.desc)
                }
            })
        }

        // 添加节点后的处理
        function editNodeAfterFnUser(treeNode) {
            var me = this;
            if (treeNode == null) return;
            // edit
            if (Ls.isArray(treeNode)) {
                for (var i = 0; l = treeNode.length, i < l; i++) {
                    var el = treeNode[i],
                        id = el.id,
                        name = el.name,
                        pid = el.pid,
                        isUpdate = el.update,
                        isPluralistic = el.isPluralistic,
                        positions = el.positions;

                    var curNode = cur.ztree.getNodeByParam("id", id, null);
                    if (isPluralistic) {
                        curNode.name = name + "(兼)";
                    } else {
                        curNode.name = name;
                    }
                    curNode.pid = pid;
                    cur.ztree.updateNode(curNode);
                }
            } else { // add
                var curNode = cur.ztree.getNodeByParam("id", cur.organId, null);
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
                        cur.ztree.addNodes(null, treeNode);
                    }
                }
            }
        }

    }

    return {
        user: user
    }

}();