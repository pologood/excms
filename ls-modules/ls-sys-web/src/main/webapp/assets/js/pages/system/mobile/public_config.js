/** APP配置 */
var mobile_mgr = function () {

    var treeObj = null;

    /**
     * 附加选项
     */
    // function addDiyDom(treeId, node) {
    //     if (node.isParent || cur.configType != "nav" || node.type != "CMS_Section") {
    //         return false;
    //     }
    //     var aObj = $("#" + node.tId + "_a");
    //     var disabled = "",
    //         chkBtn = "";
    //     chkBtn = ' <label for="chkBtn_' + node.id + '"><input type="checkbox" name="isChecked" id="chkBtn_' + node.id + '" value="' + node.id + '" ' + (node.isChecked ? "checked" : "") + '>导航显示</label>';
    //     aObj.after(chkBtn);
    //
    //     var $chkBtn = $("#chkBtn_" + node.id);
    //
    //     //生成首页
    //     $chkBtn && $chkBtn.on("click", function () {
    //         treeObj.selectNode(node, false, true);
    //         treeObj.checkNode(node, true, true);
    //     });
    // }

    function fnclick(event, treeId, node) {
        var treeObj = $.fn.zTree.getZTreeObj(treeId);
        if (node.nocheck) {
            treeObj.expandNode(node, !node.open, false, true, true);
            treeObj.cancelSelectedNode(node);
        } else {
            treeObj.checkNode(node, !node.checked, true, true);
        }
    }

    //公共属性
    var setting = {
        view: {
            // addDiyDom: addDiyDom,
            nameIsHTML: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast"
        },
        check: {
            enable: true
        },
        callback: {
            onClick: fnclick,
            onCheck: function (event, treeId, node) {
                var btn = $("#chkBtn_" + node.id);
                !node.checked ? btn.prop("checked", false) : "";
            }
        }
    };

    // 栏目树，跨站点，传入的siteId、columnId是当前操作的文章所属站点和栏目
    // 当为引用时，需要查询当前文章的引用关系
    var columnInit = function (treeId, columnType, siteId, columnId) {

        // 设置
        var ztree_settings = $.extend(true, {},
            setting, {
                data: {
                    key : {
                        title : "id"
                    },
                    simpleData: {
                        idKey: 'id',
                        pIdKey: 'parentId'
                    }
                }
            });

        // 加载树
        var json = Ls.ajax({
            async: false,
            // 同步
            url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/public/catalog/getCatalogsJSON",
            data: {
                siteId: siteId,
                codes: columnType
            }
        }).responseJSON;


        // 构造树
        if (json.status == 1) { // 成功
            if (json.data != null && json.data.length > 0) {
                // var data = Ls.treeDataFilter(json.data, Ls.treeDataType.SITE);
                var data = json.data;
                //加载已选项目
                var checkId = [], checkId2 = [];
                var configList = Ls.ajax({
                    async: false,
                    // 同步
                    url: Ls.getWin.win.GLOBAL_CONTEXTPATH + "/mobilecfg/getConfigList"
                }).responseJSON;

                if (configList.status == 1) {
                    var eo = configList.data;
                    if (eo != null) {
                        for (var i = 0, l = eo.length; i < l; i++) {
                            var el = eo[i];
                            if (el.type == cur.configType) {
                                checkId.push(el.indicatorId);
                                //未选中的加入数组
                                if (el.checked) {
                                    checkId2.push(el.indicatorId);
                                }
                            }
                        }
                    }
                }

                //初始数据状态
                for (var i = 0; i < data.length; i++) {
                    var node = data[i];
                    if(node.parentId==0){
                        node.nocheck = false;
                        node.isChecked = false;

                        if (checkId.indexOf(node.id) > -1) {
                            node.checked = true;
                            /*if (cur.configType == "nav") {
                             node.chkDisabled = true;
                             }*/
                        }
                        //选种的项目
                        // if (checkId2.indexOf(node.id) > -1) {
                        //     node.isChecked = true;
                        // }
                    }else{
                        node.nocheck = true;
                    }

                }

                treeObj = $.fn.zTree.init($("#" + treeId), ztree_settings, data);
                treeObj.expandAll(false);
                //App.initContentScroll(90);
            }
        }
        return treeObj;
    }

    return {
        columnInit: columnInit
    }
}();