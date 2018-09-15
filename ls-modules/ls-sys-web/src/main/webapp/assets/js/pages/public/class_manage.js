// 组配分类管理
var classManage = function () {

    var isInit = false;

    // 获取对象实体
    var getEntity = function (id, callback) {
        Ls.ajaxGet({
            url: "/public/class/getPublicClass",
            data: {
                id: cur.type == 1 ? "" : id
            }
        }).done(function (data) {
            // 判断父子关系
            if (cur.type == 1) {
                data.siteId = GLOBAL_SITEID;// 站点id
                data.parentId = id;
            }
            // 给默认序号
            if (cur.type == 1) {
                var children = cur.node.children;
                if (!children || children.length == 0) {
                    data.sortNum = 1;
                } else {
                    data.sortNum = parseInt(children[children.length - 1].sortNum) + 2;
                }
            }
            callback(data);
        });
    }

    // 保存方法
    var post = function (data, callback) {
        Ls.ajax({
            url: "/public/class/saveOrUpdate",
            data: data
        }).done(function (d) {
            if (d.status == 1) {
                Ls.tipsOk('操作成功!');
                if (cur.type == 1) { // 新增
                    var parentId = d.data.parentId;
                    var parentNode = cur.tree.getNodeByParam("id", parentId, null);
                    cur.tree.addNodes(parentNode, d.data);
                } else if (cur.type == 2) { // 修改
                    var oldnode = cur.tree.getNodeByParam("id", d.data.id, null);
                    oldnode.name = d.data.name;
                    oldnode.code = d.data.code;
                    oldnode.sortNum = d.data.sortNum;
                    oldnode.link = d.data.link;
                    oldnode.description = d.data.description;
                    cur.tree.updateNode(oldnode);
                }
            } else {
                Ls.tipsErr(d.desc);
            }
            if (callback) {
                callback();
            }
        })
    }

    var deleteNode = function (node) {
        if (confirm('真的要删除吗？')) {
            Ls.ajax({
                url: "/public/class/delete?id=" + node.id
            }).done(function (d) {
                Ls.tipsOk('删除成功!');
                // 删除本节点
                cur.tree.removeNode(node);
            });
        }
    }

    // 过滤器
    function dataFilter(treeId, parentNode, responseData) {
        // 添加根节点
        responseData.push({
            id: '0',
            parentId: '-1',
            name: '分类列表',
            open: true
        });
        return Ls.treeDataFilter(responseData, Ls.treeDataType.CATALOG);
    }

    // 设置
    var ztree_settings = {
        view: {
            addDiyDom: addDiyDom
        },
        async: {
            enable: true,
            url: cur.class_url,
            dataFilter: dataFilter
        },
        data: {
            key: {
                title: "id"
            },
            simpleData: {
                enable: true,
                idKey: 'id',
                pIdKey: 'parentId'
            }
        },
        callback: {
            onClick: function (event, treeId, node) {
                cur.tree.expandNode(node, !node.open, false, false);
                cur.tree.cancelSelectedNode(node);
            },
            onAsyncSuccess: function () {

                //加载列表
                if (!isInit) {
                    isInit = true;
                    //添加模拟滚动条
                    App.initContentScroll(null, '#class_tree', {right: true});
                }

            }
        }
    };

    /**
     * 目录编辑
     */
    function addDiyDom(treeId, node) {
        var aObj = $("#" + node.tId + "_a");
        var addBtn = ' <span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"></span>';
        var importBtn = ' <span class="button import-a" id="importBtn_' + node.id + '" title="' + node.name + '"></span>';
        var editBtn = ' <span class="button edit-a" id="editBtn_' + node.id + '" title="' + node.name + '"></span>';
        var delBtn = ' <span class="button del-a" id="delBtn_' + node.id + '" title="' + node.name + '"></span>';
        // 如果是父节点，不显示删除按扭
        if (node.isParent || !node.parentId) {
            delBtn = "";
        }
        if (node.id == '0') {// 人为加上的
            addBtn = addBtn;// + importBtn;
        }
        // 说明有父节点
        else if (node.id != null) {
            addBtn = addBtn + editBtn + delBtn;
        }
        aObj.after(addBtn);

        var addBtn = $("#addBtn_" + node.id);
        var importBtn = $("#importBtn_" + node.id);
        var editBtn = $("#editBtn_" + node.id);
        var delBtn = $("#delBtn_" + node.id);
        // 添加
        addBtn && addBtn.on("click", function () {
            cur.type = 1;
            cur.node = node;
            cur.tree.selectNode(node, false, true);
            cur.getData(node);
            return false;
        });
        // 导入
        importBtn && importBtn.on("click", function () {
            return false;
        });
        // 修改
        editBtn && editBtn.on("click", function () {
            cur.type = 2;
            cur.node = node;
            cur.tree.selectNode(node, false, true);
            cur.getData(node);
            return false;
        });
        // 删除
        delBtn && delBtn.on("click", function () {
            deleteNode(node);
            return false;
        });
    }

    // 公共目录管理
    var init = function () {

        // 初始化布局
        mini.parse();

        // 设置值
        cur.getData = function (node) {
            $(".portlet").show();
            // 获取参数
            getEntity(node.id, function (data) {
                data.$id = cur.id;
                // 如果模型已经绑定，不再绑定
                cur.vm = avalon.vmodels[data.$id];
                if (!cur.vm) {
                    // 绑定模型
                    cur.vm = avalon.define(data);
                } else {
                    Ls.assignVM(cur.vm, data);
                }
                avalon.scan(document.body, cur.vm);
            });
        }

        // 提交表单
        $('#' + cur.id).validator({
            fields: {
                'name': '名称:required;length[~40]',
                'sortNum': '排序号:required;integer'
            },
            valid: function () {
                post(Ls.toJSON(cur.vm.$model));
            }
        });

        var settings = $.extend(true, ztree_settings, {});
        // 树
        cur.tree = $.fn.zTree.init($("#" + cur.class_tree_id), settings);
    }
    return {
        init: init
    }
}();