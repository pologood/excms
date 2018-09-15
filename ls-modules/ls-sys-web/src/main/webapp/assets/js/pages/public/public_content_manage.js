// 公开内容管理
var public_mgr = function () {

    var isInit = false;

    // 根据单位id和目录id查找树节点
    var getNodeByOrganCatId = function (organId, catId) {
        return cur.tree.getNodesByFilter(function (node) {
            return node.organId == organId && node.id == catId;
        }, true); // 仅查找一个节点
    }

    var flushNodeCount = function (node, status, increment, cascade) {
        if (node.isParent || node.type != "DRIVING_PUBLIC") {//非主动公开叶子节点
            return;
        }
        var c = node.publishCount, uc = node.unPublishCount;
        if (status == 1) {//发布
            if (cascade) {
                c >= 0 && (c += (increment || 1));
            } else {
                c >= 0 && (c += (increment || 1));
                uc >= 0 && (uc += (increment || -1));
            }
        } else if (status == 0) {//取消发布
            if (cascade) {
                uc >= 0 && (uc += (increment || 1));
            } else {
                c >= 0 && (c += (increment || -1));
                uc >= 0 && (uc += (increment || 1));
            }
        }
        var hasData = false;
        var name = node.tempName || node.name;
        node.tempName = name;
        // if (c && c > 0) {
        //     hasData = true;
        //     name += "<span style='color:blue;'>[" + c + "]</span>";
        // }
        // if (uc && uc > 0) {
        //     hasData = true;
        //     name += "<span style='color:red;'>[" + uc + "]</span>";
        // }
        // if (!hasData) {
        //     name += "<span style='color:red;'>[空]</span>";
        // }
        node.publishCount = c;
        node.unPublishCount = uc;
        node.name = name;
    }

    // 加载内容
    var getContentApp = function (url, options) {
        return App.getContentAjax(url, options).done(function (res) {
            $("#public_content_body").html(res);
        });
    }

    // 站点树数据过滤器
    function dataFilter(treeId, parentNode, responseData) {
        var responseData = Ls.treeDataFilter(responseData, Ls.treeDataType.PUBLIC_CONTENT);
        // 设置目录名称
        if (responseData && responseData.length > 0) {
            for (var i = 0, len = responseData.length; i < len; i++) {
                flushNodeCount(responseData[i]);
            }
        }
        return responseData;
    }

    var init = function () {
        // 初始化布局
        mini.parse();
        // 设置《
        var ztree_settings = {
            view: {
                nameIsHTML: true
            },
            async: {
                enable: true,
                url: "/public/catalog/getOrganCatalogTree",
                autoParam: ["id=parentId", "organId"],
                otherParam: ["catalog", "true", "all", "false"],
                dataFilter: dataFilter
            },
            data: {
                simpleData: {
                    idKey: 'id',
                    pIdKey: 'parentId'
                }
            },
            callback: {
                onClick: function (event, treeId, node) {
                    var organId = node.organId;// 当前单位id
                    if (!cur.organId || cur.organId != organId) {// 设置单位id和内容模型
                        cur.organId = organId;
                        if (node.data) {
                            cur.contentModel = node.data;
                        } else {
                            cur.contentModel = cur.tree.getNodeByParam("id", organId).data;
                        }
                    }
                    // 判断节点展开
                    if (node.isParent) {
                        cur.tree.expandNode(node, !node.open, false, true, true);
                        cur.tree.cancelSelectedNode(node);
                        $("#public_content_body").empty();
                    } else {
                        // 加载文章
                        cur.type = node.type;
                        cur.node = node;
                        //goBack();
                        public_mgr.getContentApp("/public/content/list?type=" + cur.type + "&organId=" + cur.organId + "&catId=" + cur.node.id);
                    }
                    return false;
                },
                onAsyncSuccess: function () {

                    // 加载列表
                    if (!isInit) {
                        isInit = true;
                        // 添加模拟滚动条
                        App.initContentScroll();
                    }

                }
            }
        };
        cur.tree = $.fn.zTree.init($("#" + cur.organ_catalog_tree_id), ztree_settings);
    }

    return {
        init: init,// 初始化
        getContentApp: getContentApp,
        flushNodeCount: flushNodeCount,
        getNodeByOrganCatId: getNodeByOrganCatId
    }
}();

//消息回调
indexMgr.publish = function (d) {
    cur.grid.findRows(function (row) {
        if ($.inArray(row.contentId, d.contentIds) >= 0) return true;
    }).filter(function (v, i) {
        v.isPublish = d.isPublish;
        cur.grid.updateRow(v);
    });
}

function renderOp(e) {
    var str = "",
        rec = e.record, isPublish = rec.isPublish, disabledStr = Ls.publishStatus(isPublish);
    var toggleButton = '', hasEdit_ = cur.authList.hasEdit && (isPublish == 0 || cur.authList.hasPublish);
    if (cur.type == "PUBLIC_APPLY") {// 依申请公开
        if (hasEdit_) {
            str += '' +
                '<div class="btn-group" role="group">' +
                '   <button ' + disabledStr + ' type="button" class="btn btn-default btn-sm" onclick="doReply(' + rec.id + ')">回复</button>' +
                '   <button  ' + disabledStr + ' type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
                '       <i class="fa fa-angle-down"></i>' +
                '   </button>' +
                '   <ul class="dropdown-menu pull-right">' +
                (rec.isPublish == '1' ? '<li><a href="javascript:void(0)" onclick="publish(' + rec.contentId + ',0)">重新发布</a></li>' : '') +
                '       <li><a href="javascript:void(0)" onclick="doDetail(' + rec.id + ')">详情</a></li>' +
                '       <li><a href="javascript:void(0)" onclick="deleteData(' + rec.id + ',' + rec.contentId + ',' + rec.isPublish + ')">删除</a></li>' +
                '   </ul>' +
                '</div>';
        } else {
            str += '<button type="button" class="btn btn-default btn-sm" onclick="doDetail(' + rec.id + ')">详情</button>';
        }
    } else {
        var redirectLink = !rec.redirectLink ? "" : rec.redirectLink;
        // 已发布的文章，没有发布权限的话，不可以编辑修改删除，未发布的文章不做要求
        if (hasEdit_) {
            toggleButton = '<button ' + disabledStr + ' type="button" class="btn btn-default btn-sm" onclick="edit(\'' + rec.id + '\',\'' + redirectLink + '\')">修 改</button>';
        } else {
            toggleButton = '<button ' + disabledStr + ' type="button" class="btn btn-default btn-sm" onclick="Ls.copyRefer(' + rec.contentId + ',2)">复制到</button>';
        }
        str += '<div class="btn-group" role="group">' + toggleButton +
            '   <button ' + disabledStr + ' type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">' +
            '       <i class="fa fa-angle-down"></i>' +
            '   </button>' +
            '   <ul class="dropdown-menu pull-right">';
        if (hasEdit_) {
            str += (rec.isPublish == '1' ? '<li><a href="javascript:void(0)" onclick="publish(' + rec.contentId + ',0)">重新发布</a></li>' : '');
            if (cur.type == "DRIVING_PUBLIC") {
                if(!referNews){
                    str += '<li><a href="javascript:void(0)" onclick="setInvalid(' + rec.id + ')">设置标注</a></li> ';
                    if (cur.node.attribute == "ATTRIBUTE_FILE") {// 文件解读类型
                        str += '<li><a href="javascript:void(0)" onclick="relationFile(' + rec.id + ',' + rec.contentId + ',' + rec.organId + ',' + rec.relContentId + ')">解读</a></li> ';
                    } else if (cur.node.attribute == "ATTRIBUTE_READING") {// 文件解读类型
                        str += '<li><a href="javascript:void(0)" onclick="relationFile(' + rec.id + ',' + rec.contentId + ',' + rec.organId + ',' + rec.relContentId + ')">文件</a></li> ';
                    }
                }

            }
            var referNews = rec.referNews;
            if(!referNews){
                str += '    <li><a href="javascript:void(0)" onclick="Ls.copyRefer(' + rec.contentId + ',2)">复制到</a></li>' ;
                str +=    '       <li><a href="javascript:void(0)" onclick="batchMove(' + rec.id + ',' + rec.contentId + ')">移动到</a></li>';

            }
            str += '    <li><a href="javascript:void(0)" onclick="Ls.copyRefer(' + rec.contentId + ',2,2)">引用到</a></li>' ;

            var referedNews = rec.referedNews;
            if(referedNews){
                str +=  '       <li><a href="javascript:void(0)" id="delBtn' + rec.id + '" isRefer = "'+referNews+'"  isRefered="true" onclick="deleteData(' + rec.id + ',' + rec.contentId + ',' + rec.isPublish + ')">删除</a></li>';
            }else{
                str +=  '       <li><a href="javascript:void(0)" id="delBtn' + rec.id + '" isRefer = "'+referNews+'"  isRefered="false" onclick="deleteData(' + rec.id + ',' + rec.contentId + ',' + rec.isPublish + ')">删除</a></li>';
            }


        } else {
            str += '<li><a href="javascript:void(0)" onclick="batchMove(' + rec.id + ',' + rec.contentId + ')">移动到</a></li>';
        }
        str += '</ul></div>';
    }
    return str;
}

function relationFile(id, contentId, organId, relContentId) {
    cur.contentId = id;// 这里放信息公开的文章id
    Ls.openWin('/public/content/relationFile?id=' + id + '&contentId=' + contentId + '&organId=' + organId + '&relContentId=' + relContentId, '1000px', '500px', {
        id: 'relationFile_1',
        title: '关联文件',
        padding: 0,
    });
}

function setInvalid(id) {
    Ls.openWin('/public/content/toInvalid?id=' + id, '500px', '300px', {
        id: 'to_invalid',
        title: '标注设置',
        padding: 0,
    });
}

function renderReply(e) {
    var str = "";
    var rec = e.record;
    str += '<button class="btn btn-default btn-sm btn-edit" onclick="doReply(' + rec.id + ')">回复</button> ';
    return str;
}

function doReply(id) {
    Ls.openWin('/public/apply/doReply?id=' + id, '600px', '480px', {
        id: 'reply_1',
        title: '回复',
        padding: 0,
    });
}

function doDetail(id) {
    Ls.openWin('/public/apply/getPublicApplyDetail?id=' + id, '700px', '550px', {
        id: 'public_apply_detail',
        title: '详情',
        padding: 0,
    });
}

function IsPublish(e) {
    var str = "",
        rec = e.record,
        isPublish = rec.isPublish,
        disabledStr = Ls.publishStatus(rec.isPublish);
    if (disabledStr == '') {
        disabledStr = cur.authList.hasPublish ? "" : "disabled";
    }
    if (isPublish == '1') {
        str = '<button ' + disabledStr + ' type="button" class="btn btn-sm btn-default btn-ok" ' +
            'onclick="publish(' + rec.contentId + ',' + rec.isPublish + ')" title="取消发布"><span class="glyphicon glyphicon-ok"></span></button>';
    } else {
        str = '<button ' + disabledStr + ' type="button" class="btn btn-sm btn-default btn-remove" ' +
            'onclick="publish(' + rec.contentId + ',' + rec.isPublish + ')" title="发布"><span class="glyphicon glyphicon-ok"></span></button>';
    }
    return str;
}

function IsTop(e) {
    var str = "",
        rec = e.record,
        isTop = rec.isTop,
        isPublish = rec.isPublish,
        disabledStr = Ls.publishStatus(rec.isPublish);
    var hasEdit_ = cur.authList.hasEdit && (isPublish == 0 || cur.authList.hasPublish);
    if (disabledStr == '') {
        disabledStr = hasEdit_ ? "" : "disabled";
    }
    if (isTop == '1') {
        str = '<button ' + disabledStr + ' type="button" class="btn btn-sm btn-default btn-ok" ' +
            'onclick="changeTop(\'' + rec.contentId + '\',\'' + rec.isTop + '\')" title="取消置顶"><span class="glyphicon glyphicon-ok"></span></button>';
    } else {
        str = '<button ' + disabledStr + ' type="button" class="btn btn-sm btn-default btn-remove" ' +
            'onclick="changeTop(\'' + rec.contentId + '\',\'' + rec.isTop + '\')" title="置顶"><span class="glyphicon glyphicon-ok"></span></button>';
    }
    return str;
}

function changeTop(contentId, isTop) {
    var title = "置顶";
    var _status = 1;
    if (isTop == 1) {
        title = "取消置顶";
        _status = 0;
    }
    if (!confirm('确定' + title + '吗？')) {
        return false;
    }
    Ls.ajaxGet({
        url: "/public/content/changeTop",
        data: {
            ids: [contentId],
            columnId: cur.organId,
            isTop: _status
        }
    }).done(function (d) {
        Ls.tipsInfo(title + '完成，正在生成处理中');
        cur.grid.reload();
    });
}

// 添加
function add() {
    public_mgr.getContentApp("/public/content/edit?type=" + cur.type + "&verify=" + cur.verify + "&catId=" + params.columnId);
}

// 编辑
function edit(id, redirectLink) {
    var isLink = false;
    if (redirectLink && redirectLink != "") {
        isLink = true;
    }
    public_mgr.getContentApp("/public/content/edit?contentId=" + id + "&type=" + cur.type + "&verify=" + cur.verify +
        "&isLink=" + isLink + "&catId=" + params.columnId);
}

// 检查内容模型
function checkContentMode() {
    if (cur.contentModel.config.isSensitiveWord == 1 && cur.contentModel.config.isEasyWord == 1 && cur.contentModel.config.isHotWord == 1) {
        $("#checkWords").addClass("hide");
    } else {
        $("#checkWords").removeClass("hide");
    }
    if (cur.contentModel.config.isSensitiveWord == 1) {
        $("#senWords").remove();
    } else {
        $("#senWords").show();
    }
    if (cur.contentModel.config.isEasyWord == 1) {
        $("#errorWords").remove();
    } else {
        $("#errorWords").show();
    }
    if (cur.contentModel.config.isHotWord == 1) {
        $("#hotWords").remove();
    } else {
        $("#hotWords").show();
    }
}

function searchContent() {
    if (cur.type == "DRIVING_PUBLIC" || cur.type == "PUBLIC_INSTITUTION") {
        var q = {
            "siteId": GLOBAL_SITEID,
            "organId": cur.organId,
            "type": cur.type,
            "catId": cur.node.id,
            "key": $("#searchKey").val(),
            "startDate": $("#startTime").val(),
            "endDate": $("#endTime").val()
        };
        if (cur.type == "PUBLIC_INSTITUTION" && cur.node.parentId == cur.organId) {
            q.catId = "";// 当公开制度查询时，没有配置等级分类的要按照类型查出所有
        }
        // datagrid重新渲染
        cur.grid.load(q);
    } else if (cur.type == "PUBLIC_APPLY") {
        // datagrid重新渲染
        cur.grid.load({
            "siteId": GLOBAL_SITEID,
            "organId": cur.organId,
            "type": $("#public_apply_type").val(),
            "title": $("#searchKey").val()
        });
    } else {
        // datagrid重新渲染
        cur.grid.load({
            "siteId": GLOBAL_SITEID,
            "organId": cur.organId,
            "type": cur.type,
            "key": $("#searchKey").val()
        });
    }
}

function resetContent() {
    $("#searchForm")[0].reset();
    searchContent();
}

function searchByStatus(status) {
    if (cur.type == "DRIVING_PUBLIC" || cur.type == "PUBLIC_INSTITUTION") {// 主动公开
        var q = {
            "siteId": GLOBAL_SITEID,
            "organId": cur.organId,
            "type": cur.type,
            "catId": cur.node.id,
            "isPublish": status,
            "key": $("#searchKey").val()
        };
        if (cur.type == "PUBLIC_INSTITUTION" && cur.node.parentId == cur.organId) {
            q.catId = "";// 当公开制度查询时，没有配置等级分类的要按照类型查出所有
        }
        // datagrid重新渲染
        cur.grid.load(q);
    } else if (cur.type == "PUBLIC_APPLY") {
        // datagrid重新渲染
        cur.grid.load({
            "siteId": GLOBAL_SITEID,
            "organId": cur.organId,
            "isPublish": status,
            "type": $("#public_apply_type").val(),
            "title": $("#searchKey").val()
        });
    } else {
        // datagrid重新渲染
        cur.grid.load({
            "siteId": GLOBAL_SITEID,
            "organId": cur.organId,
            "type": cur.type,
            "isPublish": status,
            "key": $("#searchKey").val()
        });
    }
}

function publish(id, status, flag) {
    var title = "发布";
    var _status = 1;
    var c = 0, uc = 0;
    if (status == 1) {
        title = "取消发布";
        _status = 0;
    }
    var ids = [];
    if (id != '') {
        ids.push(id);
        if (_status == 1) {
            c++;
            uc--
        } else if (_status == 0) {
            c--;
            uc++
        }
    } else {
        var rows = cur.grid.getSelecteds();
        if (rows.length == 0) {
            Ls.tipsInfo("至少选择一项!");
            return;
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            ids[i] = rows[i].contentId;
            if (rows[i].isPublish == 1 && status == 1) {// 原先发布状态执行取消发布操作，发布数-1，未发布数+1
                c--;
                uc++
            } else if (rows[i].isPublish == 0 && status == 0) {// 发现取消发布状态执行发布操作，发布数+1，未发布数-1
                c++;
                uc--;
            }
        }
    }

    if (Ls.isEmpty(flag)) {
        if (!confirm('确定' + title + '吗？')) {
            return false;
        }
    }

    Ls.ajaxGet({
        url: "/public/content/publish",
        data: {
            ids: ids,
            columnId: cur.organId,
            catId: cur.node.id,
            isPublish: _status,
            type: cur.type
        }
    }).done(function (d) {
        if (d.status) {
            Ls.tipsInfo(title + '完成，正在生成处理中');
            cur.grid.reload();
            c != 0 && public_mgr.flushNodeCount(cur.node, 1, c, true);
            uc != 0 && public_mgr.flushNodeCount(cur.node, 0, uc, true);
            cur.tree.updateNode(cur.node);
        } else {
            /*if (_status == 1) {// 发布时需要判断
                Ls.tipsErr(title + "的文章字段缺失，" + d.desc);
            } else {*/
            Ls.tipsErr(d.desc);
            //}
        }
    });
}

function batchCopy(id) {// 批量复制
    var ids = [];
    if (id != '') {
        ids.push(id);
    } else {
        var rows = cur.grid.getSelecteds();
        if (rows.length == 0) {
            Ls.tipsInfo("至少选择一项!");
            return;
        }
        var referFlag = false;
        for (var i = 0, l = rows.length; i < l; i++) {
            ids[i] = rows[i].contentId;
            var referNews = $("#delBtn"+rows[i].id).attr("isRefer");
            if(referNews==true||referNews=="true"){
                referFlag = true;
            }
        }
        if(referFlag){
            Ls.tipsInfo("选中的文章中含有引用的新闻，不能进行复制操作");
            return;
        }
    }
    var url = '/content/toCopyRefer?contentId=' + ids + '&source=2&type=1';//信息公开复制
    Ls.openWin(url, '400px', '400px', {
        id: 'copy_refer_page',
        title: '复制到',
        ok: function () {
            var iframe = this.iframe.contentWindow;
            var data = iframe.ok();
            data.contentId = ids.toString();
            data.type = 1;
            // 提交
            Ls.ajax({
                url: "/content/copyRefer",
                data: data
            }).done(function (d) {
                if (d.status) {
                    Ls.tipsInfo('复制完成，正在生成处理中', function () {
                        // 处理信息公开的
                        var synOrganCatIds = data.synOrganCatIds;
                        var synOrganIsPublishs = data.synOrganIsPublishs;
                        if (synOrganCatIds) {
                            var organCatIds = synOrganCatIds.split(",");
                            var organIsPublishs = synOrganIsPublishs.split(",");
                            if (organCatIds && organCatIds.length > 0) {
                                for (var i = 0; i < organCatIds.length; i++) {
                                    var organIdCatId = organCatIds[i].split("_");
                                    var node = public_mgr.getNodeByOrganCatId(organIdCatId[0], organIdCatId[1]);
                                    if (node) {
                                        public_mgr.flushNodeCount(node, organIsPublishs[i], ids.length, true);
                                        cur.tree.updateNode(node);
                                    }
                                }
                            }
                        }
                    });
                } else {
                    Ls.tipsErr(d.desc);
                }
            });
        }
    });
}

function batchMove(id, contentId, isPublish) {// 批量移动
    var ids = [];
    var contentIds = [];
    var c = 0, uc = 0;
    if (id != null && id != "") {
        ids.push(id);
        contentIds.push(contentId);
        isPublish == 1 && c--;
        isPublish == 0 && uc--;
    } else {
        var rows = cur.grid.getSelecteds();
        if (rows.length == 0) {
            if (!confirm('确定移动所有文章吗？')) {
                return;
            }
        }
        var referFlag = false;
        for (var i = 0, l = rows.length; i < l; i++) {
            if (rows[i].changeable) {//是否可编辑
                ids.push(rows[i].id);
                contentIds.push(rows[i].contentId);
                rows[i].isPublish == 1 && c--;
                rows[i].isPublish == 0 && uc--;


            }
            var referNews = $("#delBtn"+rows[i].id).attr("isRefer");
            if(referNews==true||referNews=="true"){
                referFlag = true;
            }
        }

        if(referFlag){
            Ls.tipsInfo("选中的文章中含有引用的新闻，不能进行移动操作");
            return;
        }
    }
    Ls.openWin("/public/content/toMove", '400px', '400px', {
        id: 'move_page',
        title: '移动到',
        ok: function () {
            var iframe = this.iframe.contentWindow;
            var data = iframe.ok();
            if (data.targetId) {// 如果选中节点了
                if (contentIds.length > 0) {
                    data.ids = ids;
                    data.contentIds = contentIds;
                }
                // 提交
                Ls.ajax({
                    url: "/public/content/move",
                    data: data
                }).done(function (d) {
                    if (d.status == 1) {
                        Ls.tipsInfo('移动完成，正在生成处理中', function () {
                            c < 0 && public_mgr.flushNodeCount(cur.node, 1, c);
                            uc < 0 && public_mgr.flushNodeCount(cur.node, 0, uc);
                            cur.tree.updateNode(cur.node);

                            var other = public_mgr.getNodeByOrganCatId(data.targetOrganId, data.targetId);
                            if (other) {
                                public_mgr.flushNodeCount(other, data.isPublish, contentIds.length, true);
                                cur.tree.updateNode(other);
                            }
                        });
                        cur.grid.reload();
                    } else {
                        Ls.tipsErr(d.desc);
                    }
                });
            }
        }
    });
}

// 返回
function goBack(isUpdate) {
    if (isUpdate && cur.vm) {
        var id = cur.vm.id;
        if (id) {//修改，如果状态不发生改变的话，不更新节点信息
            if (cur.isPublish != cur.vm.isPublish) {// 状态不相等
                public_mgr.flushNodeCount(cur.node, cur.isPublish);
                cur.tree.updateNode(cur.node);
            }
        } else {// 新增
            public_mgr.flushNodeCount(cur.node, cur.isPublish, 1, true);
            cur.tree.updateNode(cur.node);
        }
    }
    public_mgr.getContentApp("/public/content/list?type=" + cur.type + "&organId=" + cur.organId + "&catId=" + cur.node.id);
}

function deleteData(id, contentId, isPublish) {
    var ids = [];
    var contentIds = [];
    var c = 0, uc = 0;

    var msg = "确认要删除吗？";

    if (id != '') {
        if (isPublish == 1 && !cur.authList.hasPublish) {
            Ls.tipsInfo("没有发布权限不能删除已发布的文章！");
            return;
        }
        ids.push(id);
        contentIds.push(contentId);
        isPublish == 1 && c--;
        isPublish == 0 && uc--;
        var referedNews = $("#delBtn"+id).attr("isRefered");
        if(referedNews==true||referedNews=="true"){
            msg = "引用后的所有新闻将同步删除，是否确认删除？"
        }
    } else {
        var rows = cur.grid.getSelecteds();
        if (rows.length == 0) {
            Ls.tipsErr("至少选择一项！");
            return;
        }
        var referedFlag = false;
        for (var i = 0, l = rows.length; i < l; i++) {
            if (rows[i].isPublish == 1 && !cur.authList.hasPublish) {
                Ls.tipsInfo("没有发布权限不能删除已发布的文章！");
                return;
            }
            ids[i] = rows[i].id;
            contentIds[i] = rows[i].contentId;
            rows[i].isPublish == 1 && c--;
            rows[i].isPublish == 0 && uc--;
            var referedNews = $("#delBtn"+rows[i].id).attr("isRefered");
            if(referedNews==true||referedNews=="true"){
                referedFlag = true;
            }
        }
        if(referedFlag){
            msg = "选中记录中含有被引用的新闻，所有引用后的新闻将同步删除，是否确认删除？"
        }
    }
    var url = "/public/content/delete";
    if (cur.type == "PUBLIC_APPLY") {// 依申请公开
        url = "/public/apply/delete";
    }
    if (confirm(msg)) {
        Ls.ajaxGet({
            url: url,
            data: {
                columnId: cur.organId,
                ids: ids,
                contentIds: contentIds
            }
        }).done(function (d) {
            Ls.tipsInfo('删除完成，正在生成处理中', function () {
                c < 0 && public_mgr.flushNodeCount(cur.node, 1, c, true);
                uc < 0 && public_mgr.flushNodeCount(cur.node, 0, uc, true);
                cur.tree.updateNode(cur.node);
            });
            cur.grid.reload();
        });
    }
}