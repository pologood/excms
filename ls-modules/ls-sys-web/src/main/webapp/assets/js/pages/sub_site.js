var curr = {
    vm: {},
    tabs: {},
    comColumnList:{},
    comColumnStr:'',
    length: 0
};
var siteManage = function () {

    var tree;
    var flag = 0;
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
            key: {
                title: "name"
            },
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

    var site = function () {

        var data = {
            $id: "sub_formId",
            indicatorId: null,
            parentId: null,
            isParent: 0,
            siteConfigId: null,
            name: '',
            siteTitle: '',
            sortNum: null,
            unitIds: null,
            unitNames: '',
            comColumnId: '',
            siteTempId: '',
            keyWords: '',
            description: ''
        }

        curr.vm = avalon.vmodels[data.$id];
        if (!curr.vm) {
            curr.vm = avalon.define(data);
        }

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/subSite/getSiteTree",
                dataFilter: dataFilter,
                //autoParam: ["indicatorId"],
                // autoParam: ["indicatorId=indicatorId"]
                otherParam: {"indicatorId": GLOBAL_SITEID}
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

        tree = $.fn.zTree.init($("#ui_tree"), settings);

        function onAsyncSuccess(event, treeId, treeNode, msg) {
            if (treeNode == null) {
                var nodes = tree.getNodes();
                if (nodes.length > 0) {
                    //tree.expandNode(nodes[0], true, false, true);
                    if (nodes[0].children != null && flag != 1) {
                        tree.selectNode(nodes[0].children[0]);
                        getFirstSiteEO(nodes[0].children[0].indicatorId);
                    } else if (nodes[0].children == null && flag != 1) {
                        addNode(nodes[0]);
                    }
                    if (flag == 1) {
                        var node = tree.getNodeByParam("indicatorId", siteManage.indicatorId, null);
                        tree.selectNode(node);
                    }
                }
            }
            flag = 0;

            //加载列表
            if (!isInit) {
                isInit = true;
                //添加模拟滚动条
               //App.initContentScroll();
                App.initContentScroll(null, '#ui_tree', {right: true});
            }
        }

        var isPush = false;

        function addDiyDom(treeId, node) {
            var sObj = $("#" + node.tId + "_a");
            if ($("#nodeOp_" + node.indicatorId).length > 0)return;
            var opt = node.opt;
            var nodeOpStr = '<span id="nodeOp_' + node.indicatorId + '">';
            if (opt != null) {
                if (opt == 'super') {
                    nodeOpStr =
                        ' <span id="update' + node.indicatorId + '"  class="button edit-a" title="修改">' + '</span>' +
                        ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>';
                } else {
                    OpStr = '<span id="nodeOp_' + node.indicatorId + '">';


                    if (opt.indexOf("edit") > -1) {
                        nodeOpStr = nodeOpStr + ' <span id="update' + node.indicatorId + '"  class="button deit-a" title="修改">' + '</span>';
                    }

                    if (opt.indexOf("delete") > -1) {
                        nodeOpStr = nodeOpStr + ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>';
                    }
                }
            } else {
                nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>';
            }
            nodeOpStr = nodeOpStr + '</span>';
            sObj.after(nodeOpStr);
            // 绑定按钮事件,使用事件绑定的好处就是能传递复杂对象作为参数
            var addBtn = $('#add' + node.indicatorId), updateBtn = $('#update' + node.indicatorId), delBtn = $('#del' + node.indicatorId);
            addBtn.on('click', function (event) {
                siteManage.indicatorId = node.indicatorId;
                addNode(node, event);
                return false;
            });
            updateBtn.on('click', function () {
                siteManage.indicatorId = node.indicatorId;
                editNode(node);
                return false
            });
            delBtn.on('click', function () {
                delNode(node);
                return false;
            });
        }

        function addNode(node, event) {
            tree.selectNode(node);
            Ls.ajaxGet({
                url: "/siteConfig/getNewSortNum",
                data: {
                    parentId: node.indicatorId,
                    isSub: true
                }
            }).done(function (d) {
                data.parentId = node.indicatorId;
                data.sortNum = d.data;
                Ls.assignVM(curr.vm, data);
                avalon.scan($("#sub_formId").get(0), curr.vm);
                $("#comColumnId").html('');
                $("#comColumnId").append(curr.comColumnStr);

            });
            //阻止冒泡事件，防止点击添加按钮，执行添加操作后，又执行修改操作
            //event.stopImmediatePropagation();
        }

        function editNode(node) {
            tree.selectNode(node);
            Ls.ajaxGet({
                url: "/siteConfig/getSiteEO",
                data: {
                    indicatorId: node.indicatorId
                }
            }).done(function (d) {
                var data = d.data;
                if (!data.isHave) {
                    var cStr = "";
                    for (i = 0; i < curr.length; i++) {
                        if (curr.comColumnList[i].indicatorId == data.comColumnId) {
                            cStr += ' <option  value=' + curr.comColumnList[i].indicatorId + '>' + curr.comColumnList[i].name + '</option> ';
                            break;
                        }
                    }
                    $("#comColumnId").html('');
                    $("#comColumnId").append(cStr);
                } else {
                    $("#comColumnId").html('');
                    $("#comColumnId").append(curr.comColumnStr);
                }
                Ls.assignVM(curr.vm, data);
                avalon.scan($("#sub_formId").get(0), curr.vm);
            });
        }

        function delNode(node) {
            tree.selectNode(node);
            if (confirm('确定要删除吗?')) {
                Ls.ajaxGet({
                    url: "/siteConfig/delSiteNode",
                    data: {
                        indicatorId: node.indicatorId
                    }
                }).done(function (text) {
                    if (!text.status) {
                        Ls.tipsErr(text.desc);
                        return;
                    } else {
                        //tree.removeNode(node, false);
                        isPush = false;
                        reloadPage();
                        Ls.tipsOk("删除成功");
                        return;
                    }
                });
            }
        }

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = responseData.data;
            var responseData = Ls.treeDataFilter(responseData, Ls.treeDataType.SITE);
            if (!isPush) {
                //添加根节点
                responseData.push({
                    "indicatorId": GLOBAL_SITEID,
                    "name": GLOBAL_SITENAME,
                    "type": 'SUB_Site',
                    "isParent": true,
                    "title": GLOBAL_SITENAME,
                    "icon": GLOBAL_CONTEXTPATH + "/assets/images/site.png"
                });
                isPush = true;
            }
            return responseData;
        }

        /**
         * 点击树节点事件
         * @param event
         * @param treeId
         * @param node
         */
        function onClick(event, treeId, node) {
            if(node.indicatorId==GLOBAL_SITEID){
                treeObj.expandNode(node);
                return;
            }
            editNode(node);
            event.stopImmediatePropagation();
        }

        function onExpand(event, treeId, treeNode) {
        }

        function getFirstSiteEO(indicatorId) {
            Ls.ajaxGet({
                url: "/siteMain/getFirstSiteEO",
                data: {
                    "indicatorId": indicatorId
                }
            }).done(function (d) {
                Ls.assignVM(curr.vm, d);
                //扫描模型
                avalon.scan($("#sub_formId").get(0), curr.vm);
                if (!d.isHave) {
                    var cStr = "";
                    for (i = 0; i < curr.length; i++) {
                        if (curr.comColumnList[i].indicatorId == d.comColumnId) {
                            cStr += ' <option  value=' + curr.comColumnList[i].indicatorId + '>' + curr.comColumnList[i].name + '</option> ';
                            break;
                        }
                    }
                    $("#comColumnId").html('');
                    $("#comColumnId").append(cStr);
                } else {
                    $("#comColumnId").html('');
                    $("#comColumnId").append(curr.comColumnStr);
                }
            });
        }


        $('#sub_formId').validator({
            /*  rules: {
             mobile: [/^1[3458]\d{9}$/, '请检查手机号格式']
             },*/
            fields: {
                'name': '站点简称:required;length[1~20]',
                'siteTitle': '站点全称:length[0~128]',
                'sortNum': '序号:required;',
                'comColumnId': '绑定公共栏目:required;',
                'siteTempId': '站点模板:required;',
                'keyWords': '关键词:length[0~300];',
                'description': '描述:lenth[0~1000];'
                //'optionsRadios': '单选框:checked;'
            },
            valid: function () {
                //alert('验证成功！');
                doSave();
            }
        });


        $("#doReset").on('click', function () {
            doReset();
        })
        function doReset() {
            if (GLOBAL_SITEID == "" || GLOBAL_SITEID == null) {
                var nodes = tree.getSelectedNodes();
                Ls.assignVM(curr.vm, data);
                curr.vm.indicatorId = nodes[0].indicatorId;
                curr.vm.parentId = nodes[0].parentId;
                curr.vm.siteConfigId = nodes[0].siteConfigId;
                curr.vm.sortNum = nodes[0].sortNum;
            } else {
                curr.vm.name = '';
                curr.vm.sortNum = '';
                curr.vm.uri = '';
                curr.vm.unitId = '';
                curr.vm.unitName = '';
                curr.vm.keyWords = '';
                curr.vm.description = '';
            }
        }


        function doSave() {
            if (curr.vm.keyWords != null && Ls.strLen(curr.vm.keyWords) > 300) {
                Ls.tipsInfo("关键词的字数长度应小于300个字符");
                return;
            }
            if (curr.vm.description != null && Ls.strLen(curr.vm.description) > 1000) {
                Ls.tipsInfo("描述的字数长度应小于1000个字符");
                return;
            }
            curr.vm.comColumnId = $("#comColumnId").val();
            if (curr.vm.comColumnId == null) {
                Ls.tipsInfo("绑定公共栏目不能为空");
                return;
            }

            var name = curr.vm.name;
            var pId = curr.vm.parentId;
            var indicatorId = curr.vm.indicatorId;
            //检查站点名称是否重复
            Ls.ajaxGet({
                url: "/siteConfig/checkSiteNameExist",
                data: {
                    siteName: name,
                    parentId: pId,
                    indicatorId: indicatorId
                }
            }).done(function (text) {
                if (text.data) {
                    saveSiteConfigEO();
                } else {
                    Ls.tipsInfo("站点名称已存在");
                    return;
                }
            });
        }

        /**
         * 保存站点信息
         */
        function saveSiteConfigEO() {
            var value = curr.vm.$model;
            value = Ls.toJSON(value);
            Ls.ajax({
                url: "/subSite/saveSiteConfigEO",
                data: value
            }).done(function (text) {
                if (!text.status) {
                    Ls.tipsErr(text.desc);
                    return;
                } else {
                    isPush = false;
                    Ls.tipsOk("保存成功");
                    var tt = text.data;
                    siteManage.indicatorId = tt.indicatorId;
                    flag = 1;
                    tree.reAsyncChildNodes(null, "refresh");
                    return;
                }

            });
        }

        function reloadPage() {
            tree.reAsyncChildNodes(null, "refresh");
            var nodes = tree.getNodes();
            if (nodes.length > 0) {
                tree.expandNode(nodes[0], true, false, true);
                if (nodes[0].children != null) {
                    tree.selectNode(nodes[0].children[0]);
                    getFirstSiteEO(nodes[0].children[0].indicatorId);
                }
            }
        }
    }

    return {
        site: site
    }

}();