var tpl_manage = function () {

    var isInit = false;

    var ztree_settings = {
        data: {
            key: {
                title: "id"
            }
        },
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
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    function editTpl(type, node) {
        cur.selectNode = node;
        cur.tpl_opt_type = type;
        var url = null;
        var title = '';
        if (type == 'add') {
            title = '添加模板';
            url = "/vrtpl/addOrEdit";
        } else {
            title = '编辑模板';
            url = "/vrtpl/addOrEdit";
        }
        cur.dialog.data('type', type);
        cur.dialog.data('node', node);
        Ls.openWin(url, '500px', '150px', {
            title: title
        });
    }

    function removeTpl(id, tid) {
        if (confirm('确定删除?')) {
            Ls.ajaxGet({
                url: '/tpl/delete',
                data: {
                    id: id
                },
                success: function (resp) {

                    if (resp.status == 1) {
                        //cur.treeObj.removeNode(null, "refresh");
                        var node = cur.treeObj.getNodeByTId(tid);
                        cur.treeObj.removeNode(node, false);
                    } else {
                        Ls.tipsErr(resp.desc);
                    }
                }
            });
        }
        ;
    }

    var tpl = function () {
        //初始化布局
        mini.parse();

        cur.map = new Map();
        function addDiyDom(treeId, node) {
            cur.map.put(node.tId, node);
            var aObj = $("#" + node.tId + "_a");
            if (node.id == -1) {
                var addStr = ' <span class="button add-a" nodeId=' + node.tId + ' id="addBtn_' + node.id + '" title="' + node.name + '"> </span>';
                aObj.after(addStr);
                var btn = $("#addBtn_" + node.id);
                btn && btn.on("click", function () {
                    editTpl("add", node);
                    return false;
                });
            } else if (node.leaf == 0) {
                return;
            } else {
                var editStr = ' <span class="button edit-a" nodeId=' + node.tId + ' id="editBtn_' + node.id + '" title="' + node.name + '"> </span>';
                editStr += ' <span class="button del-a" nodeTid=' + node.tId + ' nodeId=' + node.id + ' id="delBtn_' + node.id + '" title="' + node.name + '"> </span>';
                aObj.after(editStr);
                var editBtn_ = $("#editBtn_" + node.id);
                editBtn_ && editBtn_.on("click", function () {
                    var tId = $(this).attr("nodeId");
                    editTpl("edit", cur.map.get(tId));
                    return false;
                });

                var delBtn_ = $("#delBtn_" + node.id);
                delBtn_ && delBtn_.on("click", function () {
                    var id = $(this).attr("nodeId");
                    var tid = $(this).attr("nodeTid");
                    removeTpl(id, tid);
                    return false;
                });
            }

        }

        function dataFilter(treeId, parentNode, responseData) {

            var dataArr = Ls.treeDataFilter(responseData, Ls.treeDataType.TPL);

            if (Ls.isArray(dataArr)) {
                dataArr.push({
                    "id": -1,
                    "name": "模板列表",
                    "template": false,
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": true
                })
            }

            return dataArr;
        };

        function onClick(event, treeId, node) {

            if (node.isParent) {
                node.expand = node.open;
                cur.treeObj.expandNode(node, !node.expand, true, true);
                node.open = !node.expand;
            }

            cur.node = node;
            if (cur.node == null || !cur.node.template || cur.node.leaf == 1) {
                $('#tplContent').val('');
                $("#center_tab").css('display', 'none');
                return;
            } else {
                $("#center_tab").css('display', '');
                cur.historygrid.load({id: cur.node.id});
            }

            Ls.ajaxGet({
                url: '/tpl/getTplContent',
                data: {
                    id: cur.node.id
                },
                success: function (resp) {
                    $('#tplContent').val(resp.content);
                    mini.parse();
                }
            });

            Ls.mini_datagrid_height(cur.label_layout, 70)
            Ls.mini_datagrid_height(cur.historygrid, 32)
        }

        function onExpand(event, treeId, treeNode) {
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: '/tpl/getEOList?siteId=' + GLOBAL_SITEID + "&type=Virtual",
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    //cur.treeObj.expandAll(true);
                    var nodes = cur.treeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.treeObj.expandNode(nodes[0], true, false, true);
                    }

                    //加载列表
                    if (!isInit) {
                        isInit = true;
                        //添加模板列表模拟滚动条
                        //App.initContentScroll(160, "#label_tree");
                        //添加插入标签模拟滚动条
                        App.initContentScroll(null, "#ui_tree");
                    }

                }
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

    }

    var classify = function () {

        //初始化布局
        //mini.parse();

        function onClick(event, treeId, node) {
            cur.treeObj.cancelSelectedNode(node);
            cur.treeObj.expandNode(node);
            event.stopPropagation();
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                addDiyDom: addDiyDom
            },
            async: {
                url: "../data/site_classify.txt"
            },
            callback: {
                onClick: onClick
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);
        cur.treeObj.expandAll(true);
    }

    return {
        tpl: tpl
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