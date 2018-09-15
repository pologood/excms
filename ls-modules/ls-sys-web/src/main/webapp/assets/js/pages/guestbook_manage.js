var cur = {
    ztree: '',
    columnId: '',
    pageIndex: 0,
    pageSize: 5,
    key: "",
    isPublish: "",
    receiveId: '',
    recUserId: '',
    dealStatus: "",
    classCode: '',
    c: 0,
    opt: []
};
var guestbook_manage = function () {

    //拉取数据
    function getData(pageIndex, columnId, key, isPublish, dealStatus, classCode, receiveId, recUserId) {
        //var title=$("#searchKey").val();
        var pageSize = cur.pageSize;
        var columnId = cur.columnId;
        //var organId=cur.organId;
        return Ls.ajaxGet({
            url: "/guestBook/getPage",
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
                classCode: classCode,
                receiveId: receiveId,
                recUserId: recUserId
            }
        }).done(function (d) {

            var listHtml = Ls.template("guestbook_list_template", d);
            $("#guestbook_list_body").html(listHtml);

            if (cur.opt.length > 0) {
                for (var i = 0; i < cur.opt.length; i++) {
                    if (cur.opt[i] == "publish" || cur.opt[i] == "super") {
                        $(".publishBtn").attr("disabled", false);
                        $(".publishBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "edit" || cur.opt[i] == "super") {
                        $(".editBtn").attr("disabled", false);
                        $(".editBtn").removeClass("disabled");
                    }
                    if (cur.opt[i] == "deal" || cur.opt[i] == "super") {
                        $(".dealBtn").attr("disabled", false);
                        $(".dealBtn").removeClass("disabled");
                    }
                    $(".disabled_1").addClass("disabled");
                }
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
        });
        })

    };

    function pageselectCallback(page_index, jq) {
        getData(page_index, cur.columnId, cur.key, cur.isPublish, cur.dealStatus, cur.classCode,cur.receiveId, cur.recUserId);

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

    // var checkClick=function () {
    //     debugger;
    //     // if (saveContent() == 1) {
    //         if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
    //             var types = getTypes();
    //             var content = editor.html();
    //             Ls.ajax({
    //                 type:"POST",
    //                 data: {
    //                     content: content,
    //                     types: types
    //                 },
    //                 url: "/content/isCheck",
    //                 success: function (text) {
    //                     if (text.status == 1) {
    //                         if (text.data == 1) {
    //                             checkAll();
    //                         } else {
    //                             save();
    //                         }
    //                     }
    //                 }
    //             });
    //         } else {
    //             save();
    //         }
    //     // }
    // }
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
        getData(cur.pageIndex, content_mgr.indicatorId, '', '', '', '', null, null);
    };

    return {
        init: init,
        // checkClick:checkClick,
        getModelConfig: getModelConfig,
        getData: getData
    };

}();