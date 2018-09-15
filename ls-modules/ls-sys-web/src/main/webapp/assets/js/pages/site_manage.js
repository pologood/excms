var curr = {
    vm: {},
    tabs: {},
    isSubmit: false
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

    function addClassify(indicatorId) {
    }

    var site = function () {
        //初始化布局
        curr.$id = "siteForm";

        var data = {
            $id: curr.$id,
            name: '',
            siteTitle: '',
            isParent: 0,
            parentId: null,
            indicatorId: null,
            siteConfigId: null,
            sortNum: null,
            uri: '',
            keyWords: '',
            description: '',
            isVideoTrans: 0,
            videoTransUrl: '',
            isDirectPublishEdited: 0,
            isTriggerPage: 0,
            unitIds: '',
            unitNames: '',
            indexTempId: '',
            publicTempId: '',
            commentTempId: '',
            errorTempId: '',
            searchTempId: '',
            stationId: '',
            stationPwd: '',
            memberId: '',
            wapMemberId: '',
            type: 'CMS_Site',
            isWap: 0,
            wapTempId: '',
            wapPublicTempId: '',
            phoneTempId: '',
            parent_SiteId: '',
            parent_SiteName: '',
            siteIDCode:'',
            //站点类型
            siteType: 0,
            //专题模板ID
            themeId: ''
        }

        curr.vm = avalon.vmodels[data.$id];
        if (!curr.vm) {
            curr.vm = avalon.define(data);
        }
        avalon.scan($("#siteForm").get(0), curr.vm);

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/siteMain/getSiteTree",
                dataFilter: dataFilter,
                //autoParam: ["indicatorId"],
                autoParam: ["indicatorId=indicatorId"]
                // otherParam: {"indicatorId":1}
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

        if (GLOBAL_SITEID == "" || GLOBAL_SITEID == null) {
            tree = $.fn.zTree.init($("#ui_tree"), settings);
        } else {
            $("#sortNumId").hide();
            getData();
        }

        function getSpecialThumb(specialId) {
            if (Ls.isEmpty(specialId)) {
                Ls.tipsErr("获取模板缩略图失败！");
                return false;
            }
            //先获取对应主题ID,再获取对应模板的缩略图
            Ls.ajaxGet({
                url: "/special/getSpecial",
                data: {
                    id: specialId
                }
            }).done(function (d) {
                var data = d.data;
                if (d.status && data) {
                    var themeId = data.themeId;
                    curr.vm.themeId = themeId;
                    Ls.ajaxGet({
                        url: "/specialTheme/getSpecialTheme",
                        data: {
                            id: themeId
                        }
                    }).done(function (d) {
                        var data1 = d.data;
                        if (d.status) {
                            var imgPath = JSON.parse(data1.imgPath);
                            for (var i = 0, l = imgPath.length; i < l; i++) {
                                var el = imgPath[i];
                                if (el.defaults) {
                                    setSpecialVal((el.path.indexOf(".") != -1 ?GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH ) + el.path, themeId, specialId, data.templateRootId, data.indexId);
                                    break;
                                }
                            }
                        }
                    });
                }
            });
        }

        function getData() {
            clearSelect();
            return Ls.ajaxGet({
                url: "/siteConfig/getSiteEO",
                data: {
                    indicatorId: GLOBAL_SITEID
                }
            }).done(function (d) {
                var dd = d.data;
                dd.indexTempId = Ls.isEmpty(dd.indexTempId) ? "" : dd.indexTempId;
                dd.publicTempId = Ls.isEmpty(dd.publicTempId) ? "" : dd.publicTempId;
                dd.searchTempId = Ls.isEmpty(dd.searchTempId) ? "" : dd.searchTempId;
                dd.commentTempId = Ls.isEmpty(dd.commentTempId) ? "" : dd.commentTempId;
                dd.errorTempId = Ls.isEmpty(dd.errorTempId) ? "" : dd.errorTempId;
                dd.memberId = Ls.isEmpty(dd.memberId) ? "" : dd.memberId;
                dd.wapMemberId = Ls.isEmpty(dd.wapMemberId) ? "" : dd.wapMemberId;
                dd.wapTempId = Ls.isEmpty(dd.wapTempId) ? "" : dd.wapTempId;
                dd.wapPublicTempId = Ls.isEmpty(dd.wapPublicTempId) ? "" : dd.wapPublicTempId;

                curr.formData = dd;

                //加载缩略图
                if (dd.siteType && dd.siteType == 1) {
                    getSpecialThumb(dd.specialId);
                }

                Ls.assignVM(curr.vm, dd);
            });
        }

        function onAsyncSuccess(event, treeId, treeNode, msg) {
            if (treeNode == null) {
                var nodes = tree.getNodes();
                if (nodes.length > 0) {
                    //tree.expandNode(nodes[0], true, false, true);
                    if (nodes[0].children != null && flag != 1) {
                        tree.selectNode(nodes[0].children[0], false, true);
                        getFirstSiteEO(nodes[0].children[0].indicatorId);
                    }
                    if (flag == 1) {
                        var node = tree.getNodeByParam("indicatorId", siteManage.indicatorId, null);
                        tree.selectNode(node, false, true);
                    }
                }
            }
            flag = 0;

            //加载列表
            if (!isInit) {
                isInit = true;
                //添加模拟滚动条
                // App.initContentScroll(null, "#ui_tree");
                App.initContentScroll(null, '#ui_tree', {right: true});
            }
        }

        function addDiyDom(treeId, node) {
            var sObj = $("#" + node.tId + "_a");
            if ($("#nodeOp_" + node.indicatorId).length > 0)return;
            var opt = node.opt;
            var nodeOpStr = '<span id="nodeOp_' + node.indicatorId + '">';
            if (opt != null) {
                if (opt == 'super') {
                    if (node.parentId == 1) {
                        nodeOpStr += ' <span id="add' + node.indicatorId + '"  class="button add-a" title="添加">' + '</span>' +
                            ' <span id="update' + node.indicatorId + '"  class="button edit-a" title="修改">' + '</span>' +
                            ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>';
                    } else {
                        nodeOpStr =
                            ' <span id="update' + node.indicatorId + '"  class="button edit-a" title="修改">' + '</span>' +
                            ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>';
                    }

                } else {
                    OpStr = '<span id="nodeOp_' + node.indicatorId + '">';

                    if (opt.indexOf("add") > -1) {
                        nodeOpStr = nodeOpStr + ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>';
                    }

                    if (opt.indexOf("edit") > -1) {
                        nodeOpStr = nodeOpStr + ' <span id="update' + node.indicatorId + '"  class="button deit-a" title="修改">' + '</span>';
                    }

                    if (opt.indexOf("delete") > -1) {
                        nodeOpStr = nodeOpStr + ' <span id="del' + node.indicatorId + '"  class="button del-a" title="删除">' + '</span>';
                    }
                }
            } else {
                //nodeOpStr = ' <span id="add' + node.indicatorId + '"  class="button add-a"  title="添加">' + '</span>' ;
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
            clearSelect();
            tree.selectNode(node, false, true);
            Ls.ajaxGet({
                url: "/siteConfig/getNewSortNum",
                data: {
                    parentId: node.indicatorId,
                    isSub: false
                }
            }).done(function (d) {
                data.parentId = node.indicatorId;
                Ls.assignVM(curr.vm, data);
                avalon.scan($("#columnForm").get(0), curr.vm);
                curr.vm.sortNum = d.data;
                curr.vm.type = 'CMS_Site';
            });
            //阻止冒泡事件，防止点击添加按钮，执行添加操作后，又执行修改操作
            event.stopImmediatePropagation();
        }

        function editNode(node) {
            clearSelect();
            tree.selectNode(node, false, true);
            Ls.ajaxGet({
                url: "/siteConfig/getSiteEO",
                data: {
                    indicatorId: node.indicatorId
                }
            }).done(function (d) {
                var data = d.data;

                //加载缩略图
                if (data.siteType && data.siteType == 1) {
                    getSpecialThumb(data.specialId);
                }

                curr.vm = avalon.vmodels[curr.$id];
                if (curr.vm) {
                    Ls.assignVM(curr.vm, data);
                    avalon.scan($("#siteForm").get(0), curr.vm);
                }
            });
        }

        function delNode(node) {
            tree.selectNode(node, false, true);
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
            return responseData;
        }

        /**
         * 点击树节点事件
         * @param event
         * @param treeId
         * @param node
         */
        function onClick(event, treeId, node) {
            /*if (node.isParent) {
             tree.expandNode(node);
             }*/
            // flag1=false;
            // flag2=false;
            //$(".nav-tabs>a:first").trigger("click");
            //$('.nav-tabs a:first').trigger("click");
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
            }).done(function (data) {
                Ls.assignVM(curr.vm, data);
                //扫描模型
                avalon.scan($("#siteForm").get(0), curr.vm);
            });
        }

        $('#siteForm').validator({
            /*  rules: {
             mobile: [/^1[3458]\d{9}$/, '请检查手机号格式']
             },*/
            fields: {
                'name': '站点简称:required;length[1~20]',
                'siteTitle': '站点全称:length[0~128]',
                'sortNum': '序号:required;',
                'unitId': '单位:required;',
                'siteIDCode':'站点标识:required;length[0~128]',
                'uri': '绑定域名:required;url;',
                'keyWords': '关键词:length[0~300];',
                'description': '描述:lenth[0~1000];'
                //'optionsRadios': '单选框:checked;'
            },
            valid: function () {
                if (!curr.isSubmit) {
                    doSave();
                } else {
                    Ls.tipsInfo("正在处理中，请稍等");
                }
            }
        });


        $("#doReset").on('click', function () {
            var vm = avalon.vmodels[curr.$id];
            Ls.assignVM(vm, curr.formData);
            // doReset();
        });

        $("#site_classify").on('click', function () {
            var nodes = tree.getSelectedNodes();
            addClassify(nodes[0].indicatorId);
        });

        $("#doReset1").on('click', function () {
            doReset1();
        });

        $("#doReset2").on('click', function () {
            doReset2();
        });

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

        function doReset1() {
            curr.vm.indexTempId = '';
            curr.vm.publicTempId = '';
            curr.vm.errorTempId = '';
            curr.vm.commentTempId = '';
            curr.vm.searchTempId = '';
            curr.vm.memberId = '';
            curr.vm.wapMemberId = '';
        }

        function doReset2() {
            curr.vm.stationId = '';
            curr.vm.stationPwd = '';
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

            if (curr.vm == null) {
                Ls.tipsErr("实体为空");
                return;
            }
            /* if(curr.vm.stationPwd!=null&&curr.vm.stationPwd.length>0&&curr.vm.stationPwd.length<31){
             Ls.tips("查询密码的长度应大于31",{icons:"info"});
             return;
             }*/
            var name = curr.vm.name;
            var pId = curr.vm.parentId;
            var indicatorId = curr.vm.indicatorId;
            //检查站点名称是否重复
            curr.isSubmit = true;
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
                    Ls.tipsErr("站点名称已存在");
                    return;
                }
            }).always(function () {
                curr.isSubmit = false;
            })
        }

        /**
         * 保存站点信息
         */
        function saveSiteConfigEO() {
            var value = Ls.toJSON(curr.vm.$model);

            var url = "/siteConfig/saveSiteConfigEO";
            if (value.siteType == 1) {
                url = "/special/saveSpecialSite";
            }
            Ls.ajax({
                url: url,
                data: value
            }).done(function (text) {
                if (!text.status) {
                    Ls.tipsErr(text.desc);
                    return;
                } else {
                    Ls.tipsOk("保存成功");
                    var tt = text.data;
                    if (GLOBAL_SITEID == "" || GLOBAL_SITEID == null) {
                        siteManage.indicatorId = tt.indicatorId;
                        flag = 1;
                        tree.reAsyncChildNodes(null, "refresh");
                    } else {
                        // getData();
                        curr.vm.indicatorId == tt.indicatorId;
                        curr.vm.siteConfigId = tt.siteConfigId;
                        curr.vm.parentId = tt.parentId;
                    }
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
                    tree.selectNode(nodes[0].children[0], false, true);
                    getFirstSiteEO(nodes[0].children[0].indicatorId);
                }
            }
        }
    }

    function setSpecialVal(imgSrc, themeId, specialId, indexId, columnId) {
        $("#siteDesign").attr("href", themeId ? "/design/index?themeId=" + themeId + "&specialId=" + specialId + "&tplId=" + indexId + "&columnId=" + columnId : "javascript:void(0)")[themeId ? "removeClass" : "addClass"]("hide");
        $("#themeHref").attr({
            href: themeId ? "/design/previewThumbnail?id=" + themeId : "javascript:void(0)",
            target: themeId ? "_blank" : "_self"
        });
        $("#themeImg").attr("src", themeId ? imgSrc : "/assets/images/no.photo.jpg");
        $("#themeId").val(themeId);
    }

    function clearSelect() {
        setSpecialVal('', '');
    }

    function specialSelect(id) {
        Ls.openWin("/special/specialSelect", '920px', '450px', {
            title: "选择子站模板",
            close: function (data) {
                if (data) {
                    setSpecialVal(data.imgSrc, data.themeId)
                }
            }
        });
    }

    return {
        site: site,
        clearSelect: clearSelect,
        specialSelect: specialSelect
    }

}();