var net_classify = function () {

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
                title: "name"
            },
            simpleData: {
                enable: true
            }
        }
    };

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    function editClassify(type, node) {
        cur.select_node = node;
        cur.type = type;
        if (type == 'add') {
            if (node.id == -1) {
                form_a_show();
                getSortNum(node);
                Ls.assignVM(cur.vm_a, cur.model);
                avalon.scan($("#" + cur.$id_a).get(0), cur.vm_a);
                cur.active_vm = cur.vm_a;
            } else {
                form_b_show();
                getSortNum(node);
                cur.model.pname = node.name;
                cur.model.pid = node.id;
                Ls.assignVM(cur.vm_b, cur.model);
                avalon.scan($("#" + cur.$id_b).get(0), cur.vm_b);
                cur.active_vm = cur.vm_b;
            }
        } else {
            if (node.pid == -1) {
                form_a_show();
                Ls.assignVM(cur.vm_a, node);
                avalon.scan($("#" + cur.$id_a).get(0), cur.vm_a);
                cur.active_vm = cur.vm_a;
            } else {
                form_b_show();
                Ls.assignVM(cur.vm_b, node);
                avalon.scan($("#" + cur.$id_b).get(0), cur.vm_b);
                cur.active_vm = cur.vm_b;
            }
        }

    }

    function form_a_show() {
        cur.active_form = $('#classify_form_a');
        cur.div_a.show();
        cur.div_b.hide();
    }

    function form_b_show() {
        cur.active_form = $('#classify_form_b');
        cur.div_b.show();
        cur.div_a.hide();
    }

    function removeClassify(tid) {
        var node = cur.treeObj.getNodeByTId(tid);
        if(confirm('确定删除?')) {
            Ls.ajaxGet({
                url: '/netClassify/delete',
                data: {
                    id: node.id,
                    pid: node.pid
                },
                success: function (resp) {
                    if (resp.status == 1) {
                        cur.treeObj.removeNode(node, false);
                        var pnode = cur.treeObj.getNodeByParam("id", node.pid, null);
                        if (pnode.children == null || pnode.children.length <= 0) {
                            var ppnode = cur.treeObj.getNodeByParam("id", pnode.pid, null);
                            if (ppnode != null) {
                                cur.treeObj.reAsyncChildNodes(ppnode, "refresh");
                            }
                        }
                    } else {
                        Ls.tipsErr(resp.desc);
                    }
                }
            });
        };
    }

    function getSortNum(node) {
        Ls.ajaxGet({
            url: '/netClassify/getSortNum',
            data: {
                pid: node.id
            },
            success: function (resp) {
                if (resp != null) {
                    if (node.id == -1) {
                        $('#sort_p').val(resp + 1);
                    } else {
                        $('#sort').val(resp + 1);
                    }
                }
            }
        });
    }

    var init = function () {
        cur.map = new Map();
        function addDiyDom(treeId, node) {
            cur.map.put(node.tId, node);
            var aObj = $("#" + node.tId + "_a");
            if (node.id == -1) {
                var addStr = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"> </span>';
                aObj.after(addStr);
                var btn = $("#addBtn_" + node.id);
                btn && btn.on("click", function () {
                    editClassify("add", node);
                    return false;
                });
            } else {
                var str = "";
                if (node.level < 3) {
                    str = ' <span class="button add-a" nodeTid="' + node.tId + '" id="addBtn_' + node.id + '" title="' + node.name + '"> </span>';
                }
                if (!node.isParent) {
                    str += ' <span class="button del-a" nodeTid=' + node.tId + ' id="delBtn_' + node.id + '" title="' + node.name + '"> </span>';
                }
                aObj.after(str);

                var addBtn_ = $("#addBtn_" + node.id);
                addBtn_ && addBtn_.on("click", function () {
                    var id = $(this).attr("nodeTid");
                    editClassify("add", cur.map.get(id));
                    return false;
                });

                var delBtn_ = $("#delBtn_" + node.id);
                delBtn_ && delBtn_.on("click", function () {
                    var tid = $(this).attr("nodeTid");
                    removeClassify(tid);
                    return false;
                });
            }

        }

        function dataFilter(treeId, parentNode, responseData) {
            var data = Ls.treeDataFilter(responseData, Ls.treeDataType.ROLE);
            return data;
        };

        function onClick(event, treeId, node) {
            cur.type = "edit";
            cur.select_node = node;
            if (node.isParent) {
                cur.treeObj.cancelSelectedNode(node);
                cur.treeObj.expandNode(node);
            }
            event.stopPropagation();

            if (node.id == -1) {
                cur.div_b.hide();
                cur.div_a.hide();
                return;
            }

            if (node.pid == -1) {
                form_a_show();
                Ls.assignVM(cur.vm_a, node);
                avalon.scan($("#" + cur.$id_a).get(0), cur.vm_a);
                cur.active_vm = cur.vm_a;
            } else {
                form_b_show();
                var pnode = cur.treeObj.getNodeByParam("id", node.pid, null);
                cur.vm_b.pname = pnode.name;
                Ls.assignVM(cur.vm_b, node);
                avalon.scan($("#" + cur.$id_b).get(0), cur.vm_b);
                cur.active_vm = cur.vm_b;
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
                url: "/netClassify/getClassifyEOs",
                dataFilter: dataFilter,
                autoParam: ["id"]
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    var nodes = cur.treeObj.getNodes();
                    if (nodes.length > 0) {
                        cur.treeObj.expandNode(nodes[0], true, false, true);
                    }
                    //添加模拟滚动条
                    initSlimScroll();
                }
            }
        });

        cur.treeObj = $.fn.zTree.init($("#ui_tree"), settings);

    }

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 10)
        App.initSlimScroll(ui_tree);
    }

    return {
        init: init
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