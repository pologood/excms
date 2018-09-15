var content_mgr = function () {

    var elem_tree = "content_tree", isInit = false;
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

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    //站点树数据过滤器
    function dataFilter(treeId, parentNode, responseData) {
        var responseData = Ls.treeDataFilter(responseData.data, Ls.treeDataType.SITE);
        /*if (responseData) {
         for (var i = 0, l = responseData.length; i < l; i++) {
         var node = responseData[i];
         if (node.isStartUrl == 1) {
         node.url = node.transUrl;
         node.target = "_blank";
         }
         }
         }*/
        return responseData;
    };

    //加载站点树
    function GetSiteColumnTree() {
        var url = "", tmpSiteID = "";
        if (columnTypeCode == "net_work" || columnTypeCode == "InteractiveVirtual") {
            url = "getVirtualColumn";
        } else {
            url = "getColumnTreeBySite";
            tmpSiteID = GLOBAL_SITEID;
        }
        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: "/siteMain/" + url,
                otherParam: {"indicatorId": tmpSiteID, "columnTypeCode": columnTypeCode},
                autoParam: ["indicatorId=indicatorId"],
                dataFilter: dataFilter
            },
            callback: {
                beforeExpand: beforeExpand,
                onClick: onClick,
                beforeClick: function (treeId, treeNode, clickFlag) {
                    // if (treeNode.isStartUrl == 1) {
                    //     return false;
                    // }
                },
                onAsyncSuccess: function () {
                    content_mgr.ztree = $.fn.zTree.getZTreeObj(elem_tree);
                    //添加模拟滚动条
                    if (!isInit) {
                        App.initContentScroll();
                        isInit = true;
                        // 处理 hidebtn 定位问题
                        $("#hidebtn").show();
                    }

                }
            }
        });

        function addDiyDom(treeId, node) {
            try {
                if (node.isCount != "" && node.isCount == '1') {
                    var aObj = $("#" + node.tId + "_a");
                    aObj.after("<i style='color:brown' title='文章总数'>(" + node.count + ")</i>");
                }
            } catch (e) {
            }
        }

        function beforeExpand(treeId, treeNode) {
            //if (treeNode.indicatorId == 47730) {
            //  getModule(treeNode);
            //}
        }

        function onClick(event, treeId, treeNode) {
            if (treeNode.isParent) {
                //content_mgr.ztree.cancelSelectedNode(treeNode);
                //content_mgr.ztree.expandNode(treeNode);

                //展开的同时.也加在数据
                setContent(treeNode);

                event.stopPropagation();
            } else {
                if (treeNode.isStartUrl == 1) {
                    return;
                }
                setContent(treeNode);
            }
        }

        //初始化树
        $.fn.zTree.init($("#" + elem_tree), settings);
    }

    //加载面板内容
    function setContent(treeNode) {
        //重置avalon 加载状态，用于业务逻辑判断是否需要更新vm
        content_mgr.avalon = false;

        //写入当前节点对象，用于其它模块获取方便
        content_mgr.node = treeNode;

        //此方法以后不再使用
        content_mgr.indicatorId = treeNode.indicatorId;

        //转换成JSON
        //content_mgr.attr = $.parseJSON(content_mgr.content);

        /*if (content_mgr.indicatorId == "54039") {
         getContentApp("/test/list");
         return false
         }*/

        content_mgr.param = {
            key: "",
            condition: "",
            status: "",
            columnId: '',
            pageSize: 10,
            pageIndex: 0,
            startTime: '',
            endTime: ''
        };

        //父节点
        if (treeNode.isParent) {
            //加载内容
            getContentApp("/articleNews/index?indicatorId=" + treeNode.indicatorId);
        }else {
            //加载内容
            getContentApp("/" + treeNode.columnTypeCode + "/index?indicatorId=" + treeNode.indicatorId);
        }
    }


    function getModule(treeNode) {
        return Ls.ajaxGet({
            url: "/net/service/getClassifyEOs",
        }).done(function (d) {

            content_mgr.ztree.addNodes(treeNode, d);

        });
    }

    //加载内容
    var getContentApp = function (url, options) {
        return App.getContentAjax(url, options).done(function (res) {
            $("#content_body").html(res)
        });
    }

    //动态创建datagrid
    function createGrid() {
        var grid = new mini.DataGrid();
        grid.set({
            allowCellEdit: true,
            allowCellSelect: true,
            width: "100%",
            // style: "width:400%;",
            columns: [
                {field: "columnName", header: "所属栏目", headerAlign: "center", align: "center", width: "120"},
                {field: "title", header: "信息标题", headerAlign: "center", align: "left", width: "100%"},
                {field: "updateDate", header: "更新时间", headerAlign: "center", align: "center", width: "150"},
                {header: "操作", headerAlign: "center", align: "center", width: "120"}
            ],
            data: [{a: 1, b: 2}, {a: 2, b: 3}, {a: 3, b: 4}]
        })
        grid.render($("#portlet_body").get(0));
    }

    function runSearch() {
        var content_search_words = $("#content_search_words").val();
        App.getContentAjax("/searchBody").done(function (res) {
            $("#content_body").html(res)
            global_search.init(content_search_words);
        });
    }

    var init = function () {
        //初始化布局
        mini.parse();
        GetSiteColumnTree();
        /*var content_body = $("#content_body");
         content_body.width(content_body.parent().width()).height(content_body.parent().height());*/

        //获取焦点
        var content_search_words = $("#content_search_words").focus();

        //禁用搜索回车事件
        $(document).on('keypress', '#content_search_form', function (e) {
            if (e.which == 13) {
                runSearch();
                return false;
            }
        });

        //绑定搜索按扭
        $("#content_search_submit").on('click', function () {
            runSearch();
        })

        /*var flag = true;
       ('#hidebtn').on('click', function () {
           var _this = $(this),
               west = _this.parents('#west'),
               cont = _this.parents('.mini-layout-border').children().last();

           if (flag) {
               _this.animate({
                   'right': '0px'
               })
               west.animate({
                   'width': '8px'
               },function () {
                   west.css('border-right-color', 'transparent');
               })
               cont.animate({
                   'left': '0px',
                   'width': $('#content_layout').width() + 'px'
               },function () {
                   // $("#" + elem_tree).mCustomScrollbar("disable");

               })


           } else {
               _this.animate({
                   'right': '-3px'
               })
               _this.parents('#west').animate({
                   'width': '200px'
               },function () {
                   _this.parents('#west').css('border-right-color', '#e5e5e5');
               })
               _this.parents('.mini-layout-border').children().last().animate({
                   'left': '200px',
                   'width': $('#content_layout').width()- 200 + 'px'
               })

           }
           flag = !flag;
           _this.toggleClass('active');
       })*/

        var layout = mini.get('content_layout');
        $('#hidebtn').on('click', function () {
            layout.updateRegion("west", { expanded: false });
            $('#hidebtn2').show();
        })

        $('#hidebtn2').on('click', function () {
            layout.updateRegion("west", { expanded: true });
            $('#hidebtn2').hide();
        })
    }

    var timer = null;
    var fn = function () {
        timer = setTimeout(function () {
            $('#hidebtn').click();
        }, 300)
    }

    return {
        init: init,
        param: {
            key: "",
            condition: "",
            status: "",
            columnId: '',
            pageSize: 11,
            pageIndex: 0,
            startTime: '',
            endTime: '',
            resources: ''
        },
        getContentApp: getContentApp,
        timer: timer,
        click: fn
    }

}();