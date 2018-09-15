var cur = {
    ztree: '',
    columnId: '',
    pageIndex: 0,
    pageSize: 5,
    key: "",
    addDate:"",
    isPublish: "",
    receiveUnitId: '',
    recUserId: '',
    receiveDepartId:'',
    dealStatus: "",
    classCode:'',
    st:'',
    ed:'',
    c: 0,
    columnThor:false,
    opt: []
};
var message_board_manage = function () {

    //拉取数据
    function getData(pageIndex, columnId, key, isPublish, dealStatus, classCode,receiveId, recUserId,startDate,endDate,receiveDepartId) {
        var pageSize = cur.pageSize;
        var columnId = cur.columnId;
        return Ls.ajaxGet({
            url: "/messageBoard/getPage",
            data: {
                //附加请求数据
                //pageIndex:0,pageSize:15,dataFlag:1,columnId:columnId,organId:organId
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                title: key,
                isPublish: isPublish,
                dealStatus: dealStatus,
                classCode:classCode,
                receiveUnitId: receiveId,
                receiveUserCode: recUserId,
                receiveDepartId:receiveDepartId,
                st: startDate,
                ed: endDate
            }
        }).done(function (d) {
            $.each(d.data,function(i,item){
                if(!Ls.isEmpty(item.attachId)){
                    var attachIdArr=item.attachId.split(',');
                    item.attachIdArr=attachIdArr;

                    if(!Ls.isEmpty(item.attachName)){
                        var attachNameArr=item.attachName.split(',');
                        item.attachNameArr=attachNameArr;
                    }else{
                        var names='';
                        for (var i = 0; i < attachIdArr.length; i++) {
                            var names=names+'未命名'+',';
                        }
                        var attachNameArr=names.split(',');
                        item.attachNameArr=attachNameArr;

                    }

                }
            });
            var listHtml = Ls.template("message_board_list_template", d);
            $("#message_board_list_body").html(listHtml);

            $.each(d.data,function(i,item){
                var isSuper=item.isSuper;
                if(item.replyVOList !=null && item.replyVOList.length>0) {
                    for (var i = 0; i < item.replyVOList.length; i++) {
                        var attachId = item.replyVOList[i].attachId;
                        var replyId =  item.replyVOList[i].id;
                        var content ='';
                        var s ="";
                        var imageUrl="";
                        var edit = '';
                        var receiveName = item.replyVOList[i].receiveName;
                        var username = item.replyVOList[i].username;
                        if (item.recType == 0||item.recType==3) {
                            content = (receiveName==null?'':receiveName)+'&nbsp;&nbsp;&nbsp;' + item.replyVOList[i].createDate;
                        }
                        if (item.recType == 1) {
                            content = (username==null?'':username)+'&nbsp;&nbsp;&nbsp;' + item.replyVOList[i].createDate;
                        }
                        if(item.recType ==2){
                            content = '&nbsp;&nbsp;&nbsp;' + item.replyVOList[i].createDate;
                        }
                        if(item.replyVOList[i].isSuper==1){
                            s = "<span style='color:red;'>汇总&nbsp;&nbsp;&nbsp;</span>";

                        }
                        if(!Ls.isEmpty(attachId)){
                            imageUrl += '<a href="javascript:void(0);" onclick="downLoad('+ "'"+item.replyVOList[i].attachId+"'" +')"><span  style="color:#117CEA;">附件查看</span></a>'
                        }
                        if(isSuper==1){
                            edit+='<a href="javascript:void(0);" onclick="updateReply('+ replyId +')"><span  style="color:#117CEA;">修改</span></a>';
                            edit+='&nbsp;&nbsp;&nbsp;<a href="javascript:void(0);" onclick="deleteReply('+item.baseContentId+','+replyId+')"><span  style="color:#117CEA;">删除</span></a>';
                        }
                        $(".reply"+item.replyVOList[i].messageBoardId).append(
                            '<div class="message-reply">'
                            +'<div class="replyUser">'
                            +s +content
                            +'</div>'
                            +'<div class="replyContent">'
                            +'<div>'+item.replyVOList[i].replyContent+'</div>'
                            +'</div>'
                            +'<div class="clearfix">'
                            +'<div style="float: left">'
                            +imageUrl
                            +'</div>'
                            +'<div style="float: right;">'
                                +edit
                            +'</div>'
                            +'</div>'
                            +'</div>'
                            +'<br />');
                    }
                }
            });
            if ( cur.opt.length > 0) {
                for (var i = 0; i < cur.opt.length; i++) {
                    if (cur.opt[i] == "publish" || cur.opt[i] == "super"  ||cur.columnThor) {
                        $(".publishBtn").attr("disabled", false);
                        $(".publishBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "edit" || cur.opt[i] == "super"||cur.columnThor) {
                        $(".editBtn").attr("disabled", false);
                        $(".editBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "delete" || cur.opt[i] == "super"||cur.columnThor) {
                        $(".deleteBtn").attr("disabled", false);
                        $(".deleteBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "forward" || cur.opt[i] == "super"||cur.columnThor) {
                        $(".forwardBtn").attr("disabled", false);
                        $(".forwardBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "deal" || cur.opt[i] == "super"||cur.columnThor) {
                        $(".dealBtn").attr("disabled", false);
                        $(".dealBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "forward" || cur.opt[i] == "super"||cur.columnThor) {
                        $(".forwardBtn").attr("disabled", false);
                        $(".forwardBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "statistics" || cur.opt[i] == "super"||cur.columnThor) {
                        $(".statistics").show();
                    }
                }
                $(".disabled_1").addClass("disabled");
            }

            $("#publishs:not(.disabled)").off().on('click', function () {
                batchPublish(1);
            });

            $("#unpublishs:not(.disabled)").off().on('click', function () {
                batchPublish(0);
            });

            //批量删除
            $("#deletes:not(.disabled)").off().on('click', function () {
                deletes();
            });

        }).done(function (d) {
           // Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);
            var pIndex=d.pageIndex;
            if(pIndex==0){
                pIndex=1;
            }else{
                pIndex=pIndex+1;
            }
            Ls.pagination3("#pagination", function (pageIndex) {
                pageselectCallback(pageIndex-1);
            }, {
                currPage: pIndex,
                pageCount: d.pageCount,
                total:d.total,
                isTotal:true
            });
        })

    };











    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.columnId, cur.key, cur.isPublish, cur.dealStatus,cur.classCode,cur.receiveUnitId,null,cur.st,cur.ed);
        cur.pageIndex = page_index;
        return false;
    }

    function checkAll() {
        Ls.openWin("/content/checkPage", '600px', '460px', {
                id: 'checkAll',
                title: '内容检测'
            }
        );
    }

    var getModelConfig = function getModelConfig() {
        Ls.ajaxGet({
            data: {
                columnId: cur.columnId
            },
            url: "/content/getModelConfig",
            success: function (text) {
                if (text.status == 1) {
                    var config = text.data;
                    if (config.isSensitiveWord == 1 || config.isEasyWord == 1 || config.isHotWord == 1) {

                        var types = function () {
                            var types = [];
                            if (config.isEasyWord == 1) {
                                types.push(1);
                            }
                            if (config.isSensitiveWord == 1) {
                                types.push(2);
                            }
                            if (config.isHotWord) {
                                types.push(3);
                            }
                            return types;
                        }();
                        var content = guestbook_manage.vm.$model.list;
                        content = content[0].guestBookContent + content[0].title + content[0].responseContent;
                        Ls.ajax({
                            type: "POST",
                            data: {
                                content: content,
                                types: types
                            },
                            url: "/content/isCheck",
                            success: function (text) {
                                if (text.status == 1) {
                                    if (text.data == 1) {
                                        checkAll();
                                    } else {
                                        save();
                                    }
                                }
                            }
                        });
                    } else {
                        save();
                    }
                } else {
                    Ls.tipsErr("内容模型获取错误");
                }
            }
        });
    }
    //提交数据
    function addPost() {
        var data = Ls.toJSON(cur.vm.$model);
        var organId = data.organId;
        var url = "/organ/updateOrgan";
        if (organId == null) {
            url = "/organ/saveOrgan"
        }
        Ls.ajax({
            url: url,
            data: data
        }).done(function (d) {

        })
        return false;
    }


    var init = function () {
        //初始化布局
        mini.parse();
        cur.columnId = content_mgr.indicatorId;
        cur.power = content_mgr.node.opt;
        if (cur.power == "super") {
            cur.opt.push("super");
        } else {
            cur.functions = content_mgr.node.functions;
            for (var i = 0; i < cur.functions.length; i++) {
                cur.opt.push(cur.functions[i].action);
            }
        }
        getData(cur.pageIndex, content_mgr.indicatorId, '', '', '','',null,null);
    };

    return {
        init: init,
        getModelConfig: getModelConfig,
        getData: getData
    };

}();