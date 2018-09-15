var role_manage = function () {

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
                title: "id"
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    function editRole(type, node) {
        cur.dialog.data('type', type);
        cur.selectNode = node;
        cur.role_opt_type = type;
        var url = "/role/conf/addOrEdit?type=" + type;
        var str = JSON.stringify(node).replace(/\"/g, "'");
        var title = '添加角色';
        if (type == 'edit') {
            cur.dialog.data('node', node);
            title = '修改角色';
            url = "/role/conf/addOrEdit?node=" + str + "&type=" + type;
        }

        cur.node_edit = node;//设置默认值
        Ls.openWin(url, '500px', '360px', {
            title: title
        });
    }

    function removeRole(tid, roleId) {
        if (confirm('确定删除?')) {
            Ls.ajaxGet({
                url: '/role/conf/delete',
                data: {
                    roleId: roleId
                },
                success: function (resp) {

                    if (resp.status == 1) {
                        var node = cur.treeObj.getNodeByTId(tid);
                        cur.treeObj.removeNode(node, false);
                    } else {
                        Ls.tips(resp.desc, {times: 2});
                    }
                }
            });
        }
        ;
    }

    function user_select() {

        if (Ls.isEmpty(cur.roleId)) {
            alert("请选择角色!");
            return;
        }

        /*var url = '/system/roleAsg/userAdd?roleId=' + cur.roleId;

        Ls.openWin(url, '450px', '400px', {
            title: '添加用户'
        });*/

        var url = '/system/roleAsg/userAdd?roleId=' + cur.roleId + '&scope=0&scopeType=multi';
        cur.dialog.data('addRole', 1);
        Ls.openWin(url, '790px', '516px', {
            title: '添加用户',
            close: function (data) {

            }
        });

    }

    var role = function () {
        //初始化布局
        mini.parse();

        //实例化datagrid
        cur.grid.load();

        $("#adduser_btn").on('click', function () {
            user_select();
        })
        cur.map = new Map();
        function addDiyDom(treeId, node) {
            cur.map.put(node.roleId, node);
            var aObj = $("#" + node.tId + "_a");
            if (node.id == -2) return;
            if (node.id == -1) {
                var addStr = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"> </span>';
                aObj.after(addStr);
                var btn = $("#addBtn_" + node.id);
                btn && btn.on("click", function () {
                    editRole("add", node);
                    return false;
                });
            } else {
                //去掉超管编辑角色权限
                var editStr = "";
                var delStr = "";
                if ((node.code != 'site_admin' && node.code != 'super_admin' && !node.isParent) || node.type == 'Column') {
                    editStr = ' <span class="button edit-a" roleId="' + node.id + '" id="editBtn_' + node.id + '" title="' + node.name + '"> </span>';
                    delStr += ' <span class="button del-a" nodeTid=' + node.tId + ' roleId="' + node.id + '" id="delBtn_' + node.id + '" title="' + node.name + '"> </span>';
                }
                aObj.after(delStr);
                aObj.after(editStr);
                var editBtn_ = $("#editBtn_" + node.id);
                editBtn_ && editBtn_.on("click", function () {
                    var id = $(this).attr("roleId");
                    editRole("edit", cur.map.get(id));
                    return false;
                });

                var delBtn_ = $("#delBtn_" + node.id);
                delBtn_ && delBtn_.on("click", function () {
                    var id = $(this).attr("roleId");
                    var tid = $(this).attr("nodeTid");
                    removeRole(tid, id);
                    return false;
                });
            }

        }

        function dataFilter(responseData) {
            var data = responseData.data;

            if (data) {
                for (var i = 0; i < data.length; i++) {
                    if (data[i].type == 'Column') {
                        data[i].code = "site_admin";
                    }
                }
            }

            cur.superadmin = responseData.desc;
            var data = Ls.treeDataFilter(data, Ls.treeDataType.ROLE);
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    if (data[i].type != 'Column' && data[i].type != 'PublicInfo' && (node.code == 'site_admin' || node.code == 'super_admin')) {
                        node.pid = -2;
                    } else if (data[i].type == 'Organ') {
                        node.pid = -1;
                        data[i].code = "";
                        data[i].icon = GLOBAL_CONTEXTPATH + "/assets/images/organ.gif";
                    }

                    if (node.type == 'PublicInfo') {
                        data[i].icon = GLOBAL_CONTEXTPATH + "/assets/images/site_admin.gif";
                    }
                }
            }

            if (cur.superadmin == 'true') {
                //添加根节点
                data.push({
                    "id": -2,
                    "name": "管理角色",
                    "roleId": '',
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": true
                })
            } else {
                //添加根节点
                data.push({
                    "id": -1,
                    "name": "普通角色",
                    "roleId": '',
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": true
                })
            }

            return data;
        };

        function onClick(event, treeId, node) {
            cur.role_code = node.code;
            if (node.isParent) {
                node.expand = node.open;
                cur.treeObj.expandNode(node, !node.expand, true, true);
                node.open = !node.expand;
                return;
            }

            if (node.code == 'super_admin' && node.type != 'Column') {
                cur.grid.hideColumn('site');
                cur.grid.hideColumn('siteRights');
                cur.grid.hideColumn('roleCt');
                $('#menu_tab').hide();
                $('#site_tab').hide();
                $('#info_open_tab').hide();
                $('#role_tab a[href="#tab_1_1"]').tab('show');
            } else if (node.code == 'site_admin' && node.type != 'Column') {
                cur.grid.showColumn('site');
                cur.grid.showColumn('siteRights');
                cur.grid.showColumn('roleCt');
                $('#user_right').show();
                $('#menu_tab').show();
                $('#site_tab').hide();
                $('#info_open_tab').hide();
                $('#role_tab a[href="#tab_1_1"]').tab('show');
            } else {
                cur.grid.hideColumn('site');
                cur.grid.hideColumn('siteRights');
                cur.grid.hideColumn('roleCt');
                $('#user_right').hide();
                $('#menu_tab').show();
                $('#site_tab').show();
                if (!cur.isRoot) {
                    $('#info_open_tab').show();
                }
            }

            mini.parse();
            cur.node_edit = node;

            if (node.id == -1) {
                $('#center_tab').hide();
                return;
            }
            else {
                $('#center_tab').show();
            }
            cur.roleId = node.roleId;
            cur.grid.load({roleId: node.roleId});
            menu_select.menu();
            site_opt_tree.siteOpt();
            info_open_opt_tree.init();
            Ls.mini_datagrid_height(cur.grid, 60);
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/role/conf/getTreeByScope?scope=CMS",
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    var nodes = cur.treeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.treeObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    /*if (!isInit) {
                        App.initContentScroll();
                        isInit = true;
                    }*/
                }
            }
        });

        // cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

        Ls.ajax({
            url: "/role/conf/getTreeByScope?scope=CMS",
            data: {},
            success: function (resp) {
                var resut = dataFilter(resp);
                //添加模拟滚动条
                cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings, resut);
                if (!isInit) {
                    //App.initContentScroll();
                    App.initContentScroll(null, "#ui_tree", {right: true});
                    isInit = true;
                }
            }
        });
    }

    return {
        role: role
    }

}();


function Map() {

    var mapObj = {};

    this.put = function (key, value) {
        mapObj[key] = value;
    };

    this.remove = function (key) {
        if (mapObj.hasOwnProperty(key)) {
            delete mapObj[key];
        }
    };

    this.get = function (key) {
        if (mapObj.hasOwnProperty(key)) {
            return mapObj[key];
        }
        return null;
    };

    this.getKeys = function () {
        var keys = [];
        for (var k in mapObj) {
            keys.push(k);
        }
        return keys;
    };

    // 遍历map
    this.each = function (fn) {
        for (var key in mapObj) {
            fn(key, mapObj[key]);
        }
    };

    this.toString = function () {
        var str = "{";
        for (var k in mapObj) {
            str += "\"" + k + "\" : \"" + mapObj[k] + "\",";
        }
        str = str.substring(0, str.length - 1);
        str += "}";
        return str;
    }

}