// 公开目录管理
var catalogManage = function () {

    var isInit = false;
    var loadData = function (url, data, callback) {// 加载数据
        Ls.ajax({
            url: url,
            data: data
        }).done(function (d) {
            callback && callback(d);
        });
    }
    var deleteNode = function (node) { // 删除数据
        if (confirm('真的要删除吗？')) {
            Ls.ajaxGet({
                url: "/public/catalog/delete?id=" + node.id + "&organId=" + (cur.organId | "")
            }).done(function (d) {
                if (d.status == 1) {
                    Ls.tipsOk('删除成功！');
                    cur.tree.removeNode(node);// 删除本节点
                } else {
                    Ls.tipsErr(d.desc);// 删除失败
                }
            });
        }
    }
    var updateCatalog = function (data) {// 更新节点数据
        if (cur.type == 1) { // 新增
            var parentId = data.parentId;
            var parentNode = cur.tree.getNodeByParam("id", parentId, null);
            cur.tree.addNodes(parentNode, data);
        } else if (cur.type == 2) { // 修改
            var oldNode = cur.tree.getNodeByParam("id", data.id, null);
            oldNode.name = data.name;
            oldNode.code = data.code;
            oldNode.sortNum = data.sortNum;
            oldNode.link = data.link;
            oldNode.description = data.description;
            cur.tree.updateNode(oldNode);
        }
    }
    var editCatalog = function () { // 编辑目录
        Ls.openWin(cur.edit_url, '460px', '440px', {
            id: 'organ_config_id',
            title: cur.type == 1 ? "新增目录" : "编辑目录",
            close: function (data, iframe) {
                var data = iframe.cur.data;
                if (data) {
                    Ls.tipsOk("保存成功！");
                    updateCatalog(data);
                }
            }
        });
    }
    var addOrEditNode = function (node, type) {// 添加或者编辑节点
        cur.type = type;
        cur.node = node;
        cur.tree.selectNode(node, false, true);
        if (cur.openWin) {
            editCatalog();
        } else {
            cur.getData(node);
        }
        return false;
    }

    var hideOrShowNode = function (node, prefix) {// 影藏或者显示节点
        if (node.isParent) {// 如果是父节点
            if (!confirm('所有子节点也会进行相同操作！')) {
                return;// 返回
            }
        }

        /*var relEOS = [];
        var parentNode = node.getParentNode();
        console.log(parentNode);
        if(!node.isParent&&!node.isShow){
            var relEO = {
                organId: cur.organId, isShow: true, catId: node.id
            };
            var parentRelEO = {
                organId: cur.organId, isShow: true, catId: parentNode.id
            }
            relEOS.push(relEO);
            relEOS.push(parentRelEO);
            Ls.ajaxGet({
                url: "/public/catalog/showsOrHide2",
                data: {
                    relEOs :JSON.stringify(relEOS)
                }
            }).done(function (d) {
                if (d.status == 1) {
                    var keyId = "#" + prefix + node.id;
                    $(keyId).attr("title", "隐藏").removeClass(hideClazz).addClass(showClazz);
                    var keyParentId = "#" + prefix + parentNode.id;
                    $(keyParentId).attr("title", "隐藏").removeClass(hideClazz).addClass(showClazz);
                    Ls.tipsInfo('操作成功!');
                } else {
                    Ls.tipsErr('系统异常，请稍后再试!');
                }
            });
            return false;
        }*/

        var hideClazz = 'hide-a', showClazz = 'show-a';
        Ls.ajaxGet({
            url: "/public/catalog/showOrHide",
            data: {organId: cur.organId, isShow: !node.isShow, catId: node.id}
        }).done(function (d) {
            if (d.status == 1) {
                var idArr = d.data;
                for (var index = 0; index < idArr.length; index++) {
                    var n = null;
                    if (idArr[index] == node.id) {
                        n = node;
                    } else {
                        n = cur.tree.getNodeByParam("id", idArr[index], null);
                    }
                    if (n) {
                        n.isShow = !n.isShow;
                        cur.tree.updateNode(n);
                        var keyId = "#" + prefix + n.id;
                        if (n.isShow) {// 显示
                            $(keyId).attr("title", "隐藏").removeClass(hideClazz).addClass(showClazz);
                        } else {
                            $(keyId).attr("title", "显示").removeClass(showClazz).addClass(hideClazz);
                        }
                    }
                }
                Ls.tipsInfo('操作成功!');
            } else {
                Ls.tipsErr('系统异常，请稍后再试!');
            }
        });
        return false;
    }

    var addDiyDom = function (treeId, node) { // 目录右侧操作
        var aObj = $("#" + node.tId + "_a"), operate = '';
        var addBtn = '<span class="button add-a" id="addBtn_' + node.id + '" title="' + node.name + '"></span>';
        var importBtn = '<span class="button import-a" id="importBtn_' + node.id + '" title="' + node.name + '"></span>';
        var editBtn = '<span class="button edit-a" id="editBtn_' + node.id + '" title="' + node.name + '"></span>';
        var delBtn = '<span class="button del-a" id="delBtn_' + node.id + '" title="' + node.name + '"></span>';
        var prefix = "showOrHide_";// 前缀，这里不管是隐藏或者显示的按钮id统一为showOrHide_id
        var btnId = prefix + node.id, showOrHideBth = '';
        showOrHideBth = '<span class="button ' + (node.isShow ? 'show-a' : 'hide-a') + '" id="' + btnId +
            '" title="当前状态：' + (node.isShow ? '显示' : '隐藏') + '"></span>';
        operate += addBtn;// 添加子节点按钮所有目录都有

        if (node.id == '0') {// 无单位顶级节点
            operate += importBtn;
        } else if (node.parentId != '-1') { // 排除顶级节点
            operate += editBtn; // 编辑按钮
            if (cur.organId && node.type == 2) {// 单位操作自己的目录，允许删除
                operate += delBtn;
            }
            operate += showOrHideBth;
        }

        if (node.isBlankCat === 1) {
            operate += '<span class="emptyNode">空白目录</span>';
        }

        aObj.find(":last").after(operate);
        $("#addBtn_" + node.id).on("click", function (e) {
            addOrEditNode(node, 1);
        });
        $("#importBtn_" + node.id).on("click", function (e) {
            Ls.openWin(cur.upload_url, '460px', '440px', {
                id: 'organ_config_id',
                title: "导入目录",
                close: function (data, iframe) {
                    var data = iframe.cur.data;
                    data && Ls.tipsOk("导入成功");
                }
            });
            return false;
        });
        $("#editBtn_" + node.id).on("click", function (e) {
            addOrEditNode(node, 2);
        });
        $("#delBtn_" + node.id).on("click", function (e) {
            deleteNode(node);
        });
        $("#" + btnId).on("click", function (e) {
            hideOrShowNode(node, prefix);
        });
    }

    // 获取栏目
    function getColumnClass() {
        $('#selectWrap').empty().append('<input id="columnTypeId" name="columnTypeId" class="form-control" placeholder="目录类别">');
        Ls.ajaxGet({
            url: "/columnType/getList",
            data: {}
        }).done(function (text) {
            var str = '<option value="">请选择栏目类别</option>';
            if (text != null && text.data != null) {
                var data = text.data,
                    val = '';
                $('#columnTypeId').attr('value', cur.vm.columnTypeIds)

            }
            $('#columnTypeId').selectPage({
                showField: 'typeName',
                keyField: 'id',
                orderBy: ['id desc'],
                data: data,
                listSize: 15,
                selectOnly: true,
                pagination: false,
                multiple: true,
                eSelect: function (data) {
                    cur.vm.columnTypeIds = $("#columnTypeId").val();
                    $("#columnTypeId").trigger("hidemsg");
                },
                eTagRemove: function (data) {
                    cur.vm.columnTypeIds = $("#columnTypeId").val();
                }
            });

            $('.sp_result_area').width($('#catalog_form').width() + 'px');
            // 多选自适应宽度
            $(window).resize(function () {
                setTimeout(function () {
                    $('#selectWrap').empty().append('<input id="columnTypeId" name="columnTypeId" class="form-control" placeholder="目录类别">');
                    $('#columnTypeId').selectPage({
                        showField: 'typeName',
                        keyField: 'id',
                        orderBy: ['id desc'],
                        data: data,
                        listSize: 15,
                        selectOnly: true,
                        pagination: false,
                        multiple: true,
                        eSelect: function (data) {
                            cur.vm.columnTypeIds = $("#columnTypeId").val();
                            $("#columnTypeId").trigger("hidemsg");
                        },
                        eTagRemove: function (data) {
                            cur.vm.columnTypeIds = $("#columnTypeId").val();
                        }
                    });
                    $('.sp_result_area').width($('#catalog_form').width() + 'px');
                }, 500)
            })
        });
    }

    var common = function () {

        // 初始化布局
        mini.parse();

        // 设置值
        cur.getData = function (node) {
            // 设置不需要弹出窗口
            cur.openWin = false;
            cur.selectOrgan = false;
            $(".portlet").show();
            // 获取参数
            loadData(cur.catalog_url, {
                id: cur.type == 1 ? "" : node.id
            }, function (data) {
                data.$id = cur.id;

                // 判断父子关系
                if (cur.type == 1) {
                    data.siteId = GLOBAL_SITEID;// 站点id
                    if (cur.selectOrgan) {// 选择部门
                        data.type = 2;// 私有目录
                    } else {
                        data.type = 1;// 公有目录
                    }
                    data.parentId = node.id;
                    // 给默认序号
                    var children = cur.node.children;
                    if (!children || children.length == 0) {
                        data.sortNum = 1;
                    } else {
                        data.sortNum = parseInt(children[children.length - 1].sortNum) + 2;
                    }
                }

                // 如果模型已经绑定，不再绑定
                cur.vm = avalon.vmodels[data.$id];
                if (!cur.vm) {
                    // 绑定模型
                    cur.vm = avalon.define(data);
                } else {
                    Ls.assignVM(cur.vm, data);
                }
                avalon.scan(document.body, cur.vm);

                getColumnClass();
            });
        }

        // 提交表单
        $('#' + cur.id).validator({
            target: function (elem) {
                var selectP = $(elem).parent(),
                    msgbox = selectP.find('span.msg-box');
                if (!msgbox.length) {
                    if (selectP.hasClass('sp_container')) {
                        msgbox = $('<span class="msg-box"></span>').prependTo(selectP);
                    } else {
                        msgbox = $(elem).before('<span class="msg-box"></span>');
                    }

                }
                return msgbox;
            },
            fields: {
                'name': '名称:required;length(~40)',
                'code': '编码:required;',
                'sortNum': '排序号:required;integer',
                'columnTypeId': 'required'
            },
            valid: function () {
                loadData(cur.save_url, Ls.toJSON(cur.vm.$model), function (d) {
                    if (d.status == 1) {
                        // 关闭弹框
                        Ls.tipsOk("保存成功");
                        updateCatalog(d.data);
                    } else {
                        Ls.tipsErr(d.desc);
                    }
                });
            }
        });

        loadData(cur.catalogs_url, null, function (data) {
            // 添加根节点
            data.push({
                id: '0',
                parentId: '-1',
                name: '目录列表',
                open: true
            });
            var settings = {
                view: {
                    addDiyDom: addDiyDom
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
                        cur.tree.cancelSelectedNode(node);
                        cur.tree.expandNode(node, !node.open, false, false, true);
                        return false;
                    }
                }
            }
            // 树
            cur.tree = $.fn.zTree.init($("#" + cur.catalog_tree_id), settings, data);
            // 添加模拟滚动条
            App.initContentScroll(null, "#catalog_tree", {right: true});
        });
    }

    // 单位与目录对应关系
    var rel = function () {
        // 初始化布局
        mini.parse();
        // 设置值
        cur.getData = function (id, catId) {
            // 设置需要弹出窗口
            cur.openWin = true;
            cur.selectOrgan = true;
            loadData(cur.catalogs_url, {
                "organId": id,
                "parentId": catId
            }, function (d) {
                // 设置
                var settings_ = {
                    data: {
                        key: {
                            title: "name"
                        }
                    },
                    view: {
                        addDiyDom: addDiyDom
                    },
                    callback: {
                        beforeCollapse: function (treeId, treeNode) {
                            if (treeNode.parentId == -1) {
                                return false;
                            }
                        },
                        beforeExpand: function (treeId, treeNode) {
                            if (treeNode.parentId == -1) {
                                return false;
                            }
                            if (treeNode.isParent && !treeNode.children) {
                                loadData(cur.catalogs_url, {
                                    "organId": id,
                                    "parentId": treeNode.id
                                }, function (d) {
                                    cur.tree.addNodes(treeNode, d.data);
                                });
                            }
                            return treeNode.isParent;
                        },
                        onClick: function (event, treeId, node) {
                            cur.tree.cancelSelectedNode(node);
                        }
                    }
                };

                // 添加根节点
                var data = {
                    id: catId,
                    parentId: '-1',
                    name: '目录列表',
                    open: true,
                    children: d.data
                };
                // 添加模拟滚动条
                App.destroyScroll("#ztreeScrollbar"); // 反复加载树结构滚动条需改变绑定对象
                App.initContentScroll($("#page_content").outerHeight() - 100, "#ztreeScrollbar");
                // 初始化树
                cur.tree = $.fn.zTree.init($("#" + cur.catalog_tree_id), settings_, data);
            });
        }

        // 加载单位
        loadData(cur.organ_url, null, function (data) {
            // 过滤单位
            data = Ls.treeDataFilter(data, Ls.treeDataType.UNIT);
            var settings = {
                data: {
                    key: {
                        title: "name"
                    }
                },
                view: {
                    addDiyDom: function (treeId, node) {
                        var aObj = $("#" + node.tId + "_a");
                        var breakBtn = ' <span class="button link-a" id="breakBtn_' + node.organId + '" title="未配置信息公开目录"></span>';
                        var editBtn = ' <span class="button edit-a" id="editBtn_' + node.organId + '" title="修改"></span>';
                        // 已经配置过单位目录
                        if (node.config) {
                            editBtn = editBtn;
                        } else {
                            editBtn = breakBtn + editBtn;
                        }
                        aObj.after(editBtn);
                        var editBtn = $("#editBtn_" + node.organId);
                        // 修改
                        editBtn && editBtn.on("click", function () {
                            // 设置配置信息
                            cur.data = node.data;
                            cur.node = node;
                            cur.organId = node.organId;// 单位id
                            Ls.openWin(cur.config_url, '460px', '400px', {
                                id: 'organ_config_id',
                                title: "单位目录配置",
                                close: function (data, iframe) {
                                    if (iframe.cur.data) {
                                        // 重新设置节点数据
                                        if (!node.config) {
                                            $("#breakBtn_" + node.organId).remove();
                                        }
                                        node.config = true;
                                        node.data = iframe.cur.data;
                                        Ls.tipsInfo("保存成功");
                                        // 销毁目录树
                                        $.fn.zTree.destroy(cur.catalog_tree_id);
                                    }
                                }
                            });
                            return false;
                        });
                    }
                },
                callback: {
                    onClick: function (event, treeId, node) {
                        if (node.config) {
                            cur.organId = node.organId;// 单位id
                            cur.catId = node.data.catId;
                            cur.getData(node.organId, node.data.catId);
                            $('.tabbable-line').show();
                            $('a[data-toggle="tab"]:first').trigger('click');
                        } else {
                            Ls.tipsErr("本单位没有配置信息公开目录");
                            // 销毁目录树
                            $.fn.zTree.destroy(cur.catalog_tree_id);
                        }
                    }
                }
            }
            // 这棵树不进行增删改查
            catalogManage.leftZtree = $.fn.zTree.init($("#" + cur.organ_tree_id), settings, data);
            // 添加模拟滚动条
            App.initContentScroll(null, "#" + cur.organ_tree_id, {right: true});

        });

        // 加载内容模型
        loadData("/columnConfig/getContentModel", {
            siteId: GLOBAL_SITEID
        }, function (d) {
            var data = d.data;
            if (null != data) {
                var length = data.length;
                var str = '<option value="">请选择内容模型</option>';
                if (length > 0) {
                    for (i = 0; i < length; i++) {
                        str += ' <option  value=' + data[i].code + '>' + data[i].name + '</option> '
                    }
                }
                cur.contentModeStr = str;
            }
        });

        // 加载顶层目录
        loadData("/public/catalog/getTopCatalogList", null, function (data) {
            if (null != data) {
                var length = data.length;
                var str = '<option value="">请选择目录</option>';
                if (length > 0) {
                    for (i = 0; i < length; i++) {
                        str += ' <option  value=' + data[i].id + '>' + data[i].name + '</option> '
                    }
                }
                cur.catalogStr = str;
            }
        });
    }

    // 空单位
    var emptyRel = function () {
        $('#datagrid1').hide();
        $('.toggleWrap>span').click(function () {
            if ($(this).index() == 0) {
                $(this).addClass('on1').next().removeClass('on2');
                $('#datagrid1').show().prev().hide();

                cur.grid = mini.get("datagrid1");
                var param = {
                    organId: cur.organId,
                    //parentId : cur.catId,
                    blankCat: true,
                    allCat: true,
                    dataFlag: 1
                };
                cur.grid.load(param, function () {
                    $("#record").html(cur.grid.totalCount);
                });

                Ls.mini_datagrid_height(cur.grid, 120);
                $.fn.zTree.destroy(cur.catalog_tree_withEmpty_id);
            } else {
                cur.getData2(cur.organId, cur.catId);
                $(this).addClass('on2').prev().removeClass('on1');
                $('#datagrid1').hide().prev().show();
            }
        });

        // 设置值
        cur.getData2 = function (id, catId) {
            // 设置需要弹出窗口
            cur.openWin = true;
            cur.selectOrgan = true;
            loadData(cur.catalogs_url, {
                "organId": id,
                "parentId": catId,
                "blankCat": true
            }, function (d) {
                //debugger
                // 设置
                var settings_ = {
                    data: {
                        key: {
                            title: "name"
                        },
                        simpleData: {
                            enable: true,
                            idKey: "id",
                            pIdKey: "parentId"
                        }
                    },
                    view: {
                        addDiyDom: function (treeId, node) { // 目录右侧操作
                            var hideNode = (!node.children && node.isBlankCat !== 1) ? 'hideNode' : '';

                            var aObj = $("#" + node.tId + "_a"), operate = '';
                            var addBtn = '<span class="button add-a ' + hideNode + '" id="addBtn1_' + node.id + '" title="' + node.name + '"></span>';
                            var importBtn = '<span class="button import-a" id="importBtn1_' + node.id + '" title="' + node.name + '"></span>';
                            var editBtn = '<span class="button edit-a" id="editBtn1_' + node.id + '" title="' + node.name + '"></span>';
                            var delBtn = '<span class="button del-a" id="delBtn1_' + node.id + '" title="' + node.name + '"></span>';
                            var prefix = "showOrHide1_";// 前缀，这里不管是隐藏或者显示的按钮id统一为showOrHide_id
                            var btnId = prefix + node.id, showOrHideBth = '';
                            showOrHideBth = '<span class="button ' + (node.isShow ? 'show-a' : 'hide-a') + '" id="' + btnId +
                                '" title="当前状态：' + (node.isShow ? '显示' : '隐藏') + '"></span>';
                            operate += addBtn;// 添加子节点按钮所有目录都有

                            if (node.id == '0') {// 无单位顶级节点
                                operate += importBtn;
                            } else if (node.parentId != '-1') { // 排除顶级节点
                                operate += editBtn; // 编辑按钮
                                if (cur.organId && node.type == 2) {// 单位操作自己的目录，允许删除
                                    operate += delBtn;
                                }
                                operate += showOrHideBth;
                            }

                            if (node.isBlankCat === 1) {
                                operate += '<span class="emptyNode">空白目录</span>';
                            }

                            aObj.find(":last").after(operate);

                            $("#addBtn1_" + node.id).on("click", function (e) {
                                addOrEditNode(node, 1);
                            });
                            $("#importBtn1_" + node.id).on("click", function (e) {//
                                Ls.openWin(cur.upload_url, '460px', '440px', {
                                    id: 'organ_config_id',
                                    title: "导入目录",
                                    close: function (data, iframe) {
                                        var data = iframe.cur.data;
                                        data && Ls.tipsOk("导入成功");
                                    }
                                });
                                return false;
                            });
                            $("#editBtn1_" + node.id).on("click", function (e) {
                                addOrEditNode(node, 2);
                            });
                            $("#delBtn1_" + node.id).on("click", function (e) {
                                deleteNode(node);
                            });
                            $("#" + btnId).on("click", function (e) {
                                hideOrShowNode(node, prefix);
                            });

                            $(".hideNode").each(function (i, v) {
                                //$(v).closest('li').hide();
                            })
                        }
                    },
                    callback: {
                        beforeCollapse: function (treeId, treeNode) {
                            if (treeNode.parentId == -1) {
                                return false;
                            }
                        },
                        onClick: function (event, treeId, node) {
                            cur.tree2.cancelSelectedNode(node);
                        }
                    }
                };


                // 添加模拟滚动条
                App.destroyScroll("#emptyWrap"); // 反复加载树结构滚动条需改变绑定对象
                App.initContentScroll($("#page_content").outerHeight() - 200, "#emptyWrap");
                // 初始化树

                $.each(d, function (i, v) {
                    if (!v.isBlankCat) {
                        v.isHidden = true;
                    }
                    /*if (!v.isParent && !v.isBlankCat) {
                        v.isHidden = true;
                    }*/
                    //cur.tree2.showNode(v.getParentNode());
                })

                // 添加根节点
                d.unshift({
                    id: catId,
                    parentId: '-1',
                    name: '目录列表',
                    open: true
                });


                cur.tree2 = $.fn.zTree.init($("#" + cur.catalog_tree_withEmpty_id), settings_, d);


                var showNodes = cur.tree2.getNodesByParam('isBlankCat', '1', null),
                    showArr = [];

                $.each(showNodes, function (i, v) {
                    /*var pathArr = v.catPath.split(' > '),
                        selfNode = pathArr.pop(),
                        parentPathArr = pathArr.reverse(),
                        len = parentPathArr.length;
                   */
                    findParent(v);
                })

                function findParent(node) {
                    var parentNode = node.getParentNode();
                    if (parentNode.level == 0) {
                        return;
                    } else {
                        showArr.push(parentNode);
                        findParent(parentNode);
                    }
                }

                cur.tree2.showNodes(showArr);
                cur.tree2.expandAll(true);

            });
        }

    }


    function renderOpt(e) {
        var str = "";
        var rec = e.record;
        str = rec.isShow == true ? '<button class="btn btn-default btn-sm btn-edit" onclick="catalogManage.hide(' + e.record.id + ', ' + e.record.isShow + ')">隐 藏</button>' :
            '<button class="btn btn-default btn-sm btn-edit" onclick="catalogManage.hide(' + e.record.id + ', ' + e.record.isShow + ')">显 示</button>';
        return str;
    }

    function hideColumn(e) {
        var str = "";
        var rec = e.record;
        str = rec.isShow == true ? '<span style="color: red;">未隐藏</span>' : '<span>隐藏</span>';
        return str;
    }

    function hideAllColumn() {
        if(confirm("您确定要隐藏全部空白目录吗？")){
            Ls.ajaxGet({
                url: "/public/catalog/showsOrHides",
                data: {
                    organId: cur.organId,
                    parentId: cur.catId
                }
            }).done(function (text) {
                if (text.status === 1) {
                    Ls.tipsOk("一键隐藏成功", function () {
                        if ($('#datagrid1').css('display') === 'none') {
                            $('.toggleWrap>span:last').click();
                        }
                    });
                }
                cur.grid.reload();
            });
        }


    }

    function hide(id, status) {
        if (status) {
            status = false;
        } else {
            status = true;
        }
        Ls.ajaxGet({
            url: "/public/catalog/showOrHide",
            data: {
                catId: id,
                organId: cur.organId,
                isShow: status
            }
        }).done(function (text) {
            if (text.status === 1 && status) {
                Ls.tipsOk("显示成功");
            } else if (text.status === 1 && !status) {
                Ls.tipsOk("隐藏成功");
            }
            cur.grid.reload();
        });
    }

    return {
        common: common,// 公共目录
        rel: rel,// 单位目录配置
        loadData: loadData,
        emptyRel: emptyRel,
        renderOpt: renderOpt,
        hideColumn: hideColumn,
        hideAllColumn: hideAllColumn,
        hide: hide
    }
}();