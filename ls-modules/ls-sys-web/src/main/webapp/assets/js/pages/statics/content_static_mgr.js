var static_mgr = function () {

    var vm = {};
    //两颗树的dom结构
    var siteTreeEle = "site_static_tree", publicTreeEle = "public_static_tree";
    var h = $("#page_content").outerHeight() - 70;
    //扫描的controller
    var ctrl = "staticCtrl";

    var ztree_settings = {
        view: {
            nameIsHTML: true,
            selectedMulti: false,
            dblClickExpand: false,
            showTitle: true
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

    var initData = {
        siteBtn: true,
        columnBtn: true,
        infoBtn: true,
        isInit: false,
        isSiteRoot: false,
        isPublicTree: false,
        staticType: 1,//1 内容协同，2 信息公开
        activeTab: "tab_1",
        siteName: GLOBAL_SITENAME,
        columnName: "全站",
        overNum: 0,//终止的任务ID
        ids: [],
        list: [],
        //删除单条任务
        funDel: function (e, id, status) {
            if (!id) {
                Ls.tipsInfo("至少选择一项");
            }
            if (!confirm("确定删除选中的任务？")) {
                return;
            }
            Ls.ajaxGet({
                data: {
                    id: id
                },
                url: "/staticTask/deleteTask",
                success: function (d) {
                    vm.ids.push(id);
                    //重新构建dom
                    vm.list = regroupItem();
                    Ls.tipsOk("任务删除成功！");
                }
            });
        },
        //删除已完成的任务
        funDelCompleted: function () {
            if (!confirm("确定清除已完成的任务？")) {
                return;
            }
            Ls.ajaxGet({
                url: "/staticTask/clearTask",
                success: function (d) {
                    var data = d.data;
                    if (data && data.length > 0) {
                        var item = [];
                        for (var i = 0, l = data.length; i < l; i++) {
                            vm.ids.push(data[i].id)
                        }
                        //重新构建dom
                        vm.list = regroupItem();
                    }
                    Ls.tipsOk("成功清除已完成的任务！");
                }
            });
        },
        //重新执行当前任务
        funPublish: function (e, scope) {
            if (!confirm("确定执行生成任务吗？")) {
                return;
            }
            checkTask(scope).done(function (d) {

                if (d.status == 1) {
                    Ls.tipsErr(d.desc);
                } else {
                    publish(scope).done(function () {
                        getData();
                        Ls.tipsOk("创建任务成功！");
                    });
                }

            })
        },
        //创建任务
        funRun: function (e, el, index) {
            checkTask(el.scope).done(function (d) {

                if (d.status == 1) {
                    Ls.tipsOk(d.desc);
                } else {
                    publish(el.scope, el.id, 3).done(function () {
                        var items = vm.list[index];
                        items.status = 2;
                        items.percent = "0%";
                        items.count = 0;
                        items.doneCount = 0;
                        items.failCount = 0;
                        Ls.tipsOk("任务已重新开始！");
                    })
                }
            })

        },
        //停止当前任务
        funOver: function (e, el, index) {
            if (!confirm("取消后任务将被删除！")) {
                return;
            }
            publish(el.scope, el.id, 2).done(function (d) {
                $("#progress_" + el.id).remove();
            })
        },
        //失败条数列表
        funErrList: function (e, id) {
            Ls.openWin('/taskInfo/index?taskId=' + id, {
                width: '1000px',
                height: '600px',
                lock: true,
                title: '错误日志'
            });
        }
    }

    //检测任务是否重复
    function checkTask(scope) {
        return Ls.ajaxGet({
            url: "/staticTask/checkTask",
            data: {
                site: GLOBAL_SITEID,
                scope: scope,
                source: vm.staticType,
                columnID: (vm.staticType == 1 ? static_mgr.indicatorId : static_mgr.organId)
            }
        })
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    //添加生成按扭
    function addDiyDom(treeId, node) {
        var aObj = $("#" + node.tId + "_a");
        var id = node.plateId || node.indicatorId;
        var siteBtn = ' <span class="button site-a" id="siteBtn_' + id + '" title="生成首页"> </span>';
        var columnBtn = ' <span class="button column-a" id="columnBtn_' + id + '" title="生成栏目页"> </span>';
        var pageBtn = ' <span class="button page-a" id="pageBtn_' + id + '" title="生成文章页"> </span>';

        if (node.nodeType != "site") {
            siteBtn = "";
        }
        aObj.after(siteBtn + columnBtn + pageBtn);
        var $siteBtn = $("#siteBtn_" + id);
        var $columnBtn = $("#columnBtn_" + id);
        var $pageBtn = $("#pageBtn_" + id);

        //生成首页
        $siteBtn && $siteBtn.on("click", function () {
            static_mgr.publish('1');
            static_mgr.siteTree.selectNode(node, false, true);
        });

        //生成栏目页
        $columnBtn && $columnBtn.on("click", function () {
            static_mgr.indicatorId = id;
            static_mgr.publish('2');
            static_mgr.siteTree.selectNode(node, false, true);
        });

        //生成文章页
        $pageBtn && $pageBtn.on("click", function () {
            static_mgr.indicatorId = id;
            static_mgr.publish('3');
            static_mgr.siteTree.selectNode(node, false, true);
        });

    }

    // 站点树数据过滤器
    function dataFilter(treeId, parentNode, responseData) {
        var responseData = Ls.treeDataFilter(responseData.data, Ls.treeDataType.SITE);
        if (responseData) {
            for (var i = 0, l = responseData.length; i < l; i++) {
                var node = responseData[i];
                if (node.isStartUrl == 1) {
                    // node.url = node.transUrl;
                    // node.target = "_blank";
                }
            }
        }

        if (!vm.isSiteRoot) {
            //添加根节点
            var responseData = {
                "plateId": 1,
                "parentId": -1,
                "name": GLOBAL_SITENAME,
                "nodeType": "site",
                "font": {
                    "font-weight": "bold",
                    "color": "#666666"
                },
                "isParent": true,
                "open": true,
                "icon": GLOBAL_CONTEXTPATH + "/assets/images/site.png",
                "children": responseData
            }
            vm.isSiteRoot = true;
        }

        return responseData;
    }

    // 加载站点栏目
    function getSiteColumnTree() {
        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont
                //addDiyDom: addDiyDom
            },
            async: {
                url: "/siteMain/getColumnTree",
                otherParam: {
                    "indicatorId": GLOBAL_SITEID,
                    "siteId": GLOBAL_SITEID
                },
                autoParam: ["indicatorId=indicatorId"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: function (event, treeId, node) {
                    //static_mgr.siteTree.expandNode(treeNode);

                    // 当前栏目页ID
                    static_mgr.indicatorId = node.indicatorId;

                    //判断是否显示生成首页文件夹
                    vm.siteBtn = (node.nodeType == "site" ? true : false);

                    if (node.columnTypeCode == "workGuide" || node.columnTypeCode == "linksMgr") {
                        vm.infoBtn = false;
                    } else {
                        vm.infoBtn = true;
                    }

                    //写入栏目名称
                    vm.columnName = node.name;
                },
                onAsyncSuccess: function () {

                    //加载列表
                    if (!vm.isInit) {
                        getData();
                        vm.isInit = true;
                        //添加模拟滚动条
                        App.initContentScroll(h, "#site_static_tree");
                    }

                    static_mgr.siteTree = $.fn.zTree.getZTreeObj(siteTreeEle);

                }
            }
        });

        $.fn.zTree.init($("#" + siteTreeEle), settings);

    };

    //加载信息公开目录
    var getPublicTree = function () {

        return Ls.ajaxGet({
            url: "/public/catalog/getOrgansBySiteId"
        }).done(function (d) {

            var data = [];
            // 过滤，排除没有配置单位目录的单位
            for (var i in d) {
                if (d[i] && d[i].config) {
                    data.push(d[i]);
                }
            }

            // 过滤单位
            data = Ls.treeDataFilter(data, Ls.treeDataType.UNIT);
            var settings = {
                callback: {
                    onClick: function (event, treeId, node) {
                        //写入单位ID
                        static_mgr.organId = node.organId;
                        //信息公开显示生成首页按扭
                        vm.siteBtn = false;
                        //隐藏栏目页按扭
                        vm.columnBtn = false;
                        //写入栏目名称
                        vm.columnName = node.name;
                    }
                }
            }

            static_mgr.publicTree = $.fn.zTree.init($("#" + publicTreeEle), settings, data);
            App.initContentScroll(h, '#public_static_tree');
        });

    }

    //创建任务的方法
    var publish = function (scope, taskId, type) {

        var columnId = (vm.staticType == 1 ? static_mgr.indicatorId : static_mgr.organId);

        // 渲染表单对象
        return Ls.ajaxGet({
            data: {
                taskId: taskId,
                siteId: GLOBAL_SITEID,
                columnId: columnId || '',
                scope: scope,  //1首页 2栏目页 3文章页
                type: type || 1,// 发布
                source: vm.staticType
                // 内容协同
            },
            url: "/static/publish"
        })
    }

    //初步化avalon
    function initAvalon() {
        vm = avalon.vmodels[ctrl];

        initData.$id = ctrl;

        if (!vm) {
            vm = avalon.define(initData);
        } else {
            !static_mgr.avalon && Ls.assignVM(vm, initData);
        }

        static_mgr.avalon = true;

        avalon.scan($("#" + ctrl).get(0), vm);
    }

    //拉取数据
    function getData() {
        return Ls.ajaxGet({
            url: "/staticTask/getPage",
            data: {
                pageIndex: 0,
                pageSize: 100
                //dataFlag: 1,
            }
        }).done(function (d) {
            vm.list = d.data;
        }).done(function () {
            //initAvalon()
        });

    };

    //更新页面进度条
    var refurbish = function (json) {
        var eo = json.data;
        var taskId = eo.taskId;
        var index = getListIndex(taskId);
        var items = vm.list[index];
        if (eo.status == 4 || eo.status == 5) {
            items.status = 4;
        } else {
            if (eo.total == 0) {
                percent = 100;
            } else {
                var percent = (((eo.complete + eo.error) / eo.total) * 100).toFixed(2);
            }
            items.count = eo.total || 0;
            items.doneCount = eo.complete;
            items.failCount = eo.error;
            if (percent == 100) {
                items.percent = "100%";
                items.status = 3;
                items.active = false;
                items.time = eo.time || 0;
            } else {
                items.percent = percent + "%";
                items.status = 2;
                items.active = true;
            }
        }
    };

    //计算list下标
    var getListIndex = function (taskId) {
        var list = Ls.toJSON(vm.$model.list);
        for (var i = 0, l = list.length; i < l; i++) {
            var el = list[i];
            if (el.id == taskId) {
                return i;
                break
            }
        }
        return -1;
    }

    var checkIds = function (taskId) {
        var arr = vm.ids;
        if (arr && Ls.isArray(arr)) {
            for (var i = 0, l = arr.length; i < l; i++) {
                if (arr[i] == taskId) {
                    return true;
                    break
                }
            }
        }
    }

    //删除数组
    function regroupItem() {
        var items = [];
        var list = Ls.toJSON(vm.$model.list);
        for (var i = 0, l = list.length; i < l; i++) {
            var el = list[i];
            if (!checkIds(el.id)) {
                items.push(vm.list[i]);
            }
        }
        return items;
    }

    var init = function () {
        // 初始化布局
        mini.parse();

        //初始化列表
        initAvalon();

        //监控属性变动
        vm.$watch("activeTab", function (a, b) {
            if (vm.activeTab == '#tab_2') {
                if (!vm.isPublicTree) {
                    getPublicTree()
                } else {
                    static_mgr.publicTree.cancelSelectedNode();
                }
                vm.isPublicTree = true;
                vm.siteBtn = true;
                vm.columnBtn = false;
                vm.columnName = "信息公开";
                vm.staticType = 2;
                static_mgr.organId = "";

                //x轴问题
                $('#public_static_tree').mCustomScrollbar("scrollTo", "left",{
                    scrollInertia: 50
                });
            } else {
                vm.siteBtn = true;
                vm.columnBtn = true;
                vm.columnName = "全站";
                vm.staticType = 1;
                static_mgr.indicatorId = "";
            }
        });

        //加载栏目树
        getSiteColumnTree()

        //绑定标签事件
        $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {

            //切换时取消当前树的选中状态
            static_mgr.siteTree.cancelSelectedNode();

            //获取已激活的标签页的名称
            vm.activeTab = $(e.target).attr("href");

        })

    }

    return {
        vm: vm,
        init: init,
        publish: publish,
        refurbish: refurbish
    }
}();

$(document).ready(function () {
    static_mgr.init();
});