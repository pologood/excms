var cur = {
    isLoadRoot: false,
    siteId: GLOBAL_SITEID,
    ztree: '',
    parentIds:'',
    plateId:'',
    pageIndex: 0,
    pageSize: 5,
    check:0,
    canThread:$("#canThread"),
    canPost:$("#canPost"),
    canUpload:$("#canUpload"),
    userCanView: $("#userCanView"),
    managerCanView: $("#managerCanView"),
    status: $("#status")
};
function getUnitIds() {
    Ls.openWin('/bbsPlate/getUnits?unitIds='+cur.vm.unitIds, '400px', '500px', {
        id: 'bbs_unit',
        title: '选择接收单位',
        padding: 0,
    });
}

var bbsManage = function () {
    var ztree_settings = {
        view: {
            nameIsHTML: true,
            showTitle: true,
            selectedMulti: false,
            dblClickExpand: false,
            expandSpeed: "fast",
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
                title: "plateId"
            },
            simpleData:{
                idKey:"plateId",
                pidKey:"parentId"
            }
        }
    };

    function initSlimScroll() {
        //添加模拟滚动条
        var ui_tree = $('#ui_tree');
        var ui_layout = $(".mini-layout-region-body");
        ui_tree.attr("data-height", ui_layout.height() - 10)
        App.initSlimScroll(ui_tree);
    }

    function getFont(treeId, node) {
        return node.font ? node.font : {};
    }

    var bbsPlate = function () {
        //初始化布局
        mini.parse();
        cur.$id = "bbsPlate_form";
        var url = '/bbsPlate/getTrees?siteId=' + cur.siteId;
        //初始化表单字段
        cur.init = Ls.initFORM(cur.$id, {
            contentModelCode: "",
            contentModels:[],
            sortMode:"",
            sortField:"",
            canThreadC:false,
            canPostC:false,
            canUploadC:false,
            // userCanViewC:false,
            // managerCanViewC:false,
            statusC:false
        });

        //如果模型已经绑定，不再绑定
        cur.vm = avalon.vmodels[cur.init.$id];
        if (!cur.vm) {
            //绑定模型
            cur.vm = avalon.define(cur.init);
        } else {
            Ls.assignVM(cur.vm, cur.init);
        }
        avalon.scan($("#ID").get(0),cur.vm);

        var settings = $.extend(true, ztree_settings, {
            view: {
                fontCss: getFont,
                addDiyDom: addDiyDom
            },
            async: {
                url: url,
                autoParam: ["plateId=parentId"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {

                    var nodes = cur.ztree.getNodes();
                    if (nodes.length > 0) {
                        cur.ztree.expandNode(nodes[0], true, false, true);
                    }
                    App.initContentScroll(null, '#ui_tree', {right: true});
                }
            }
        });

        function dataFilter(treeId, parentNode, responseData) {
            var responseData = responseData.data;
            if (!cur.isLoadRoot) {

                //添加根节点
                var responseData = {
                    "plateId": 1,
                    "parentId": -1,
                    "name": "版块管理",
                    "nodeType": "OrganTop",
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": responseData != null?true:false,
                    "children": responseData
                }
                cur.isLoadRoot = true;
            }
            return responseData;
        };

        function addDiyDom(treeId, node) {
            var aObj = $("#" + node.tId + "_a");
            var addBtn = ' <span class="button add-a" id="addBtn_' + node.plateId + '" title="添加"> </span>';
            var editBtn = ' <span class="button edit-a" id="editBtn_' + node.plateId + '" title="编辑"> </span>';
            var delBtn = ' <span class="button del-a" id="delBtn_' + node.plateId + '" title="删除"> </span>';
            if(node.plateId != 1){
                if(!node.isParent && !node.hasChild && !node.hasPost) {
                    if(parseInt(node.plateId) > 5258839){
                        aObj.after(delBtn)
                    }

                }
            }
            aObj.after(addBtn)

            $addBtn = $("#addBtn_" + node.plateId);
            $editBtn = $("#editBtn_" + node.plateId);
            $delBtn = $("#delBtn_" + node.plateId);
            //添加
            $addBtn && $addBtn.on("click", function () {
                cur.plateId = node.plateId;
                getData("", node.plateId).done(function(){
                    getContentModel();
                })
                $(".portlet").show();
                return false;
            });

            //删除
            $delBtn && $delBtn.on("click", function () {
                deleteNode(node.plateId, node.parentId);
                return false;
            });

        }

        //初始化树
        cur.ztree = $.fn.zTree.init($("#ui_tree"), settings);

        var getData = function (id, pid) {
            pid = (pid == 1 ? "" : pid);
            if (id == "") {
                cur.clickNode = cur.ztree.getNodeByParam("plateId", pid, null);
            }
            return Ls.ajaxGet({
                url: "/bbsPlate/getPlate",
                data: {
                    plateId: id,
                    parentId: pid,
                    siteId: cur.siteId
                }
            }).done(function (d) {
                $(".portlet").show();
                data = d.data;
                console.info(data);
                if (pid != '') {
                    data.parentId = pid;
                }
                data.canThreadC = (data.canThread == 1)?true:false;
                data.canPostC = (data.canPost == 1)?true:false;
                data.canUploadC = (data.canUpload == 1)?true:false;
                // data.userCanViewC = (data.userCanView == 1)?true:false;
                // data.managerCanViewC = (data.managerCanView == 1)?true:false;
                data.statusC = (data.status == 1)?true:false;
                data.siteId = cur.siteId;
                cur.vm = avalon.vmodels[cur.$id];
                if (cur.vm) {
                    //重新更新模型数据
                    Ls.assignVM(cur.vm, data);
                }
            })
        };

        function getContentModel(){
            return Ls.ajaxGet({
                url: "/columnConfig/getContentModel",
                data:{siteId:cur.siteId}
            }).done(function (d) {
                var models = [], datas = d.data,contentModelCode = "";
                if(datas != null){
                    for(var i =0;i<datas.length;i++){
                        if(datas[i].modelTypeCode == 'govbbs'){
                            contentModelCode = datas[i].code;
                            models.push(datas[i]);
                        }
                    }
                }
                if(Ls.isEmpty(cur.vm.plateId)){
                    cur.vm.contentModelCode = contentModelCode;
                }
                cur.vm.contentModels = models;
// 				var str = '<option value="">请选择内容模型</option>';
// 				if (length > 0) {
// 					for (i = 0; i < length; i++) {
// 						str += ' <option  value=' + data[i].code + '>' + data[i].name + '</option> '
// 					}
// 				}
//                 $("#contentModelCode").html(str);

            });
        }

        function onClick(event, treeId, node) {
            if (node.plateId == 1) {
                cur.ztree.cancelSelectedNode(node);
                cur.ztree.expandNode(node);
                event.stopPropagation();
            } else {
                cur.clickNode = node;
                getData(node.plateId, node.parentId).done(function(){
                    getContentModel();
                })
                return false
            }
        }
        var addPost = function () {
            var data = Ls.toJSON(cur.vm.$model);
            if( cur.canThread.is(":checked")){data.canThread = 1;}else{data.canThread = 0;}
            if( cur.canPost.is(":checked")){data.canPost = 1;}else{data.canPost = 0;}
            if(  cur.canUpload.is(":checked")){data.canUpload = 1;}else{data.canUpload = 0;}
            if( cur.userCanView.is(":checked")){data.userCanView = 1;}else{data.userCanView = 0;}
            if(  cur.managerCanView.is(":checked")){data.managerCanView = 1;}else{data.managerCanView = 0;}
            if( cur.status.is(":checked")){data.status = 1;}else{data.status = 0;}
            var url = "/bbsPlate/save"
            Ls.ajax({
                url: url,
                data: data
            }).done(function (d) {
                if (d.status == 1) {
                    Ls.tipsOk("保存成功！");
                    d.data.update = false;
                    if (data.plateId == null) {
                        getData("", cur.plateId);
                    } else {
                        getData(d.data.plateId, d.data.parentId);
                        d.data.update = true;
                    }
                    try {
                        editNodeAfterFn(d.data);
                    } catch (e) {}

                } else {
                    Ls.tipsErr(d.desc)
                }
            })
            return false;
        }

        function screenOpt() {
            alert("");
            var data = Ls.toJSON(cur.vm.$model);
            data.isScreen = 1;
            var url = "/bbsPlate/save";
            Ls.ajax({
                url: url,
                data: data
            }).done(function (d) {
                if (d.status == 1) {
                    Ls.tipsOk("屏蔽成功！");
                    d.data.update = false;
                    if (data.plateId == null) {
                        getData("", cur.plateId);
                    } else {
                        getData(d.data.plateId, d.data.parentId);
                        d.data.update = true;
                    }
                    try {
                        editNodeAfterFn(d.data);
                    } catch (e) {}

                } else {
                    Ls.tipsErr(d.desc)
                }
            })
            return false;
        }

        function editNodeAfterFn(treeNode) {
            var me = this;
            if (treeNode == null)
                return;
            if (treeNode.parentId == null) {
                treeNode.parentId = 1;
            }
            var curNode = cur.clickNode;
            if (treeNode.update) {
                curNode.name = treeNode.name;
                curNode.parentId = treeNode.parentId;
                cur.ztree.updateNode(curNode);
            } else {
                if ((treeNode.parentId != null || treeNode.parentId > 0) && curNode) {
                    var chkNode = cur.ztree.getNodeByParam("plateId", treeNode.parentId, null);
                    if (chkNode) {
                        if (chkNode.open || !chkNode.isParent) {
                            cur.ztree.addNodes(curNode, treeNode);
                        } else {
                            cur.ztree.expandNode(chkNode, true);
                        }
                    }
                } else {
                    var chkNode = cur.ztree.getNodeByParam("plateId", '1', null)
                    cur.ztree.addNodes(chkNode, treeNode);
                }
                var delNode = $("#delBtn_" + treeNode.parentId);
                delNode.hide();
            }
        }
        var deleteNode = function (id, pid) {
            if (confirm('真的要删除吗？')) {
                var treeNode = cur.ztree.getNodeByParam("plateId", id, null);
                Ls.ajaxGet({
                    url: "/bbsPlate/delete",
                    data: {
                        plateId: id
                    },
                    success: function (text) {
                        if (text.status == 1) {
                            Ls.tipsOk("删除成功！")
                            cur.ztree.removeNode(treeNode); // 删除树节点
                            if (!treeNode.isParent) {
                                var delNode = $("#delBtn_" + treeNode.parentId);
                                var pNode = cur.ztree.getNodeByParam("plateId", treeNode.parentId, null);
                                if (!pNode.isParent) {
                                    if (delNode.length > 0) {
                                        delNode.show();
                                    } else {
                                        if (treeNode.parentId != 1) {
                                            var addNode = $("#addBtn_" + treeNode.parentId);
                                            var delBtn = ' <span class="button del-a" id="delBtn_' + treeNode.plateId + '" onclick = "deleteNode("' + treeNode.plateId + ',"' + treeNode.parentId + '")" title="' + treeNode.name + '"> </span>';
                                            addNode.after(delBtn);
                                        }
                                    }
                                }
                            }
                        } else {
                            Ls.tipsErr(text.desc)
                        }
                    }
                });
            }
        }

        $('#bbsPlate_form').validator({
            fields: {
                'name': '名称:required;length[1~20]',
                'sortNum': '排序:required;integer;range[0~99999]',
                // 'contentModelCode':'内容模型:required;',
                'description': '简介:length[~1000];'
            },
            valid: function () {
                addPost()
            }
        })

    }

    //--------------------------------------------------------------postTree---------------------------------
    var postPlate = function () {


        var settings = $.extend(true, ztree_settings, {
            view: {
                addDiyDom: addDiyDom
            },
            async: {
                enable: true,
                url: '/bbsPlate/getTrees?siteId=' + cur.siteId,
                autoParam: ["plateId=parentId"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    var nodes = cur.ztree.getNodes();
                    if (nodes.length > 0) {
                        cur.ztree.expandNode(nodes[0], true, false, true);
                    }
                    App.initContentScroll();

                }
            }
        });

        function dataFilter(treeId, parentNode, responseData) {
            var responseData =responseData.data;
            if (!cur.isLoadRoot) {
                //添加根节点
                var responseData = {
                    "plateId": 1,
                    "parentId": -1,
                    "name": "版块管理",
                    "nodeType": "OrganTop",
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": responseData != null?true:false,
                    "children": responseData
                }
                cur.isLoadRoot = true;
            }
            return responseData;
        };

        function addDiyDom(treeId, node) {
            var aObj = $("#" + node.tId + "_a");
            var addBtn = '';
            var addBtn_, editBtn_, delBtn_;

//                aObj.append(addBtn);

//            $addBtn = $("#addBtn_" + node.id);
//            //添加
//            $addBtn && $addBtn.on("click", function () {
//                editUser(node.id);
//                return false;
//            });

        }

        function onClick(event, treeId, node) {
//        	&& !node.isParent && !node.hasChild
            if (node.plateId != 1) {
                $(".portlet").show();
                cur.parentIds = node.parentIds;
                cur.plateId = node.plateId;
                cur.grid.load({dataFlag: 1,parentIds:node.parentIds});
            } else {
                cur.ztree.expandNode(node);
                event.stopPropagation();
            }
        }
        cur.ztree = $.fn.zTree.init($("#ui_tree"), settings);
    }

    //--------------------------------------------------------------replyTree---------------------------------
    var replyPlate = function () {

        //初始化布局
        mini.parse();

//        //实例化datagrid
//        cur.grid = mini.get("datagrid1");
//        cur.grid.setUrl("/bbsPost/getPage");
//        cur.grid.load();
//        //重置 datagrid 高度
//        Ls.mini_datagrid_height(cur.grid);

        var settings = $.extend(true, ztree_settings, {
            view: {
                addDiyDom: addDiyDom
            },
            async: {
                enable: true,
                url: '/bbsPlate/getTrees?siteId=' + cur.siteId,
                autoParam: ["plateId=parentId"],
                dataFilter: dataFilter
            },
            callback: {
                onClick: onClick,
                onAsyncSuccess: function () {
                    var nodes = cur.ztree.getNodes();
                    if (nodes.length > 0) {
                        cur.ztree.expandNode(nodes[0], true, false, true);
                    }
                    App.initContentScroll();

                }
            }
        });

        function dataFilter(treeId, parentNode, responseData) {
            var responseData =responseData.data;
            if (!cur.isLoadRoot) {
                //添加根节点
                var responseData = {
                    "plateId": 1,
                    "parentId": -1,
                    "name": "版块管理",
                    "nodeType": "OrganTop",
                    "font": {
                        "font-weight": "bold",
                        "color": "#666666"
                    },
                    "isParent": responseData != null?true:false,
                    "children": responseData
                }
                cur.isLoadRoot = true;
            }
            return responseData;
        };

        function addDiyDom(treeId, node) {
            var aObj = $("#" + node.tId + "_a");
            var addBtn = '';
            var addBtn_, editBtn_, delBtn_;

        }

        function onClick(event, treeId, node) {
            if (node.plateId != 1) {
                $(".portlet").show();
                cur.parentIds = node.parentIds;
                getReplyData(cur.pageIndex,cur.parentIds,'','');
            } else {
                cur.ztree.expandNode(node);
                event.stopPropagation();
            }
        }
        cur.ztree = $.fn.zTree.init($("#ui_tree"), settings);
    }


    //拉取数据
    function getReplyData(pageIndex, parentIds,searchText,isPublish) {
        var pageSize = cur.pageSize;
        return Ls.ajaxGet({
            url: "/bbsReply/getPage",
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                parentIds: parentIds,
                searchText:searchText,
                isPublish:isPublish
            }
        }).done(function (d) {

            var listHtml = reply_list_template(d);
            $("#reply_list_body").html(listHtml);

        }).done(function (d) {
            Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
        })
    };

    function pageselectCallback(page_index, jq) {
        getReplyData(page_index,cur.parentIds,'','');
        cur.pageIndex = page_index;
        return false;
    }

    var reply_list_template = Ls.compile(
        '<table class="table guestbook-list2">' +
        '<tbody>' +
        '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '<? var el = data[i] ?>' +
        '<tr>' +
        '  <th scope="row" class="w40">' +
        '    <input type="checkbox" name="check" value="<?=el.replyId?>" >' +
        '  </th>' +
        '  <td>' +
        '    <div class="message-title">' +
        '      <?=el.title?>' +
        '    </div>' +
        '    <div class="message-attr">' +
        '      <?=el.postMemberName?>&nbsp;&nbsp;&nbsp;<?=el.postCreateTime?>&nbsp;&nbsp;&nbsp;[来自：<?=el.postIp?>]' +
        '    </div>' +
        '    <div class="message-attr"></div>' +
        '     <div class="message-reply">' +
        '        <div class="reply_user"><?=el.replyMemberName?>&nbsp;&nbsp;&nbsp;<?=el.replyCreateTime?>&nbsp;&nbsp;&nbsp;[来自：<?=el.replyIp?>]</div>' +
        '        <div class="reply_content">' +
        '         <?==el.replyContent?>' +
        '        </div>' +
        '     </div>' +
        '    <div class="message-bottom">' +
        '      <span class="pull-right">' +
        '        <ol class="tools-bar">' +
        '          <li><button type="button" class="btn btn-default btn-xs" onclick="editReply(<?=el.replyId?>)">修改</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs <? if(el.isPublish==1){?> green-meadow<?}?>" onclick="setStatus(<?=el.isPublish?>,<?=el.replyId?>)">发布</button></li>' +
        '          <li><button type="button" class="btn btn-default btn-xs" id="p_<?=el.replyId?>" onclick="deleteAll(<?=el.replyId?>)">删除</button></li>' +
        '        </ol>' +
        '      </span>' +
        '     <div class="clearfix"></div>' +
        '    </div>' +
        '  </td>' +
        '</tr>' +
        '<? } ?>' +
        '</tbody>' +
        '</table>' +
        '<div id="Pagination" class="pagination pull-right"></div>' +
        '<div class="clearfix"></div>'
    );
    //--------------------------------------------------------------replyTree---------------------------------
    return {
        bbsPlate: bbsPlate,
        postPlate:postPlate,
        replyPlate:replyPlate,
        getReplyData:getReplyData
    }

}();