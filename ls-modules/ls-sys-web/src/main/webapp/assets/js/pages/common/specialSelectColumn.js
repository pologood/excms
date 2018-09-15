/** 栏目加载 */
var special_mgr = function () {

    /**
     * 附加选项
     */
    function addDiyDom(treeId, node) {
        var treeObj = cur.columnTree[treeId];
        if (node.isParent || cur.configType != "nav" || node.type != "CMS_Section") {
            return false;
        }
        var aObj = $("#" + node.tId + "_a");
        var disabled = "",
            chkBtn = "";
        chkBtn = ' <label for="chkBtn_' + node.indicatorId + '"><input type="checkbox" name="isChecked" id="chkBtn_' + node.indicatorId + '" value="' + node.indicatorId + '" ' + (node.isChecked ? "checked" : "") + '>导航显示</label>';
        aObj.after(chkBtn);

        var $chkBtn = $("#chkBtn_" + node.indicatorId);

        //生成首页
        $chkBtn && $chkBtn.on("click", function () {
            treeObj.selectNode(node, false, true);
            treeObj.checkNode(node, true, true);
        });
    }

    function fnClick(event, treeId, node) {
        var treeObj = cur.columnTree[treeId];
        if (node.nocheck) {
            treeObj.expandNode(node, !node.open, false, true, true);
            treeObj.cancelSelectedNode(node);
        } else {
            treeObj.checkNode(node, !node.checked, true, true);
        }
    }

    function fnCheck(event, treeId, node) {
        var btn = $("#chkBtn_" + node.indicatorId);
        !node.checked ? btn.prop("checked", false) : "";
    }

    //公共属性
    var setting = {
        view: {
            //addDiyDom: addDiyDom,
            nameIsHTML: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast"
        },
        check: {
            enable: true,
            chkStyle: "checkbox",
            radioType: "all",
            chkboxType: {"Y": "", "N": ""}
        },
        callback: {
            onClick: fnClick,
            onCheck: fnCheck
        }
    };

    var noCheckFilter = function (data) {
        if (data != null && data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i].isParent == 1 || ("," + columnType + ",").indexOf(data[i].columnTypeCode) == -1) {
                    data[i].nocheck = true;
                }
            }
        }
        return data;
    }


    // 栏目树，跨站点，传入的siteId、columnId是当前操作的文章所属站点和栏目
    // 当为引用时，需要查询当前文章的引用关系
    var columnInit = function (treeId, columnType, siteId, columnId) {

        // 设置
        var ztree_settings = $.extend(true, setting, {
            data: {
                simpleData: {
                    idKey: 'indicatorId',
                    pIdKey: 'parentId'
                }
            },
            check: {
                chkStyle: cur.chkStyle || "checkbox"
            }
        });

        var url = Ls.getWin.win.GLOBAL_CONTEXTPATH + "/siteMain/getColumnTreeByType";
        if (!Ls.isEmpty(columnId)) {
            url = Ls.getWin.win.GLOBAL_CONTEXTPATH + "/siteMain/getColumnByParentId";
        }

        // 加载树
        Ls.ajax({
            async: false,
            // 同步
            url: url,
            beforeSend: function () {
                cur.tips = Ls.tipsLoading("加载中...", {times: 999999});
            },
            data: {
                siteId: siteId,
                columnId: columnId,
                codes: columnType,
                columnTypeCode: columnType
            }
        }).done(function (json) {
            var data = json.data;

            // 构造树
            if (json.status == 1) { // 成功
                if (data != null && data.length > 0) {
                    data = Ls.treeDataFilter(data, Ls.treeDataType.SITE);

                    var vmData = W.$.Site.vm.$data;
                    var label = cur.targetId ? vmData.conf[cur.targetId].label : vmData.label;
                    var ids = [],items="";


                    if(!Ls.isEmpty(cur.curTabId)) {
                        items = label[cur.curTabId].id;
                        ids = items ? items.split(",") : "";
                    }else if(label&&label.id){
                        items = label.id;
                        ids = items ? items.split(",") : "";
                    }

                    for (var i = 0; i < data.length; i++) {
                        if (data[i].isParent == 1 || ("," + columnType + ",").indexOf(data[i].columnTypeCode) == -1) {
                            data[i].nocheck = true;
                        }
                        if(ids.length>0) {
                            var indicatorId = (data[i].indicatorId).toString();
                            if($.inArray(indicatorId,ids) !=-1){
                                data[i].checked = true;
                            }
                        }

                    }

                    cur.columnTree[treeId] = $.fn.zTree.init($("#" + treeId), ztree_settings, data);
                    //cur.columnTree[treeId].expandAll(false);
                    //仅展开已选栏目的节点
                    var checkedNode = cur.columnTree[treeId].getCheckedNodes();
                    if(checkedNode.length >0) {
                        cur.columnTree[treeId].selectNode(checkedNode[0], false);
                    }

                    // App.initContentScroll(90);
                    cur.tips && cur.tips.hide();
                } else {
                    $("#" + treeId).html('<span style="margin:auto" class="bold font-red-mint">未找到数据</span>');
                }
            } else {
                $("#" + treeId).html('<span style="margin:auto" class="bold font-red-mint">' + json.desc + '</span>');
            }
        }).always(function () {
            cur.tips && cur.tips.hide();
        });

    }

    return {
        columnInit: columnInit
    }
}();