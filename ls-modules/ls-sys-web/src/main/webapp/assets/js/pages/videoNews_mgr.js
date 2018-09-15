var cur = {
    columnId: content_mgr.indicatorId,
    siteId: GLOBAL_SITEID,
    pageSize: 12,
    opt: []
};
var video_mgr = function () {

    // function pageselectCallback(page_index, jq) {
    //     if (page_index != 0) {
    //         page_index = page_index - 1
    //     }
    //     getData(page_index, cur.columnId, cur.key, cur.condition, cur.status);
    //     cur.pageIndex = page_index;
    //     //获取数据
    //     return false;
    // }

    function pageselectCallback(page_index, page_size) {
        cur.pageIndex = page_index;
        page_size && (cur.pageSize = page_size);
        getData(page_index, cur.columnId, cur.key, cur.condition, cur.status);
        return false;
    }

    var orderIDS, video_list, getOrderID = function (ui, attr) {
        return ui.item.parent().find("li").map(function () {
            var num = $(this).data(attr);
            return num;
        }).get().join(',');
    }

    //更新列表排序号
    var updateNum = function (docIDS, numIDS, ui) {
        Ls.log("docIDS>>", docIDS, "numIDS>>", numIDS)
        Ls.ajax({
            url: "/content/updateNums",
            beforeSend: function () {
                video_list.sortable("disable");
            },
            data: {
                ids: docIDS,
                nums: numIDS,
                columnId: cur.columnId
            }
        }).done(function (d) {
            if (d.status) {
                Ls.tipsOk("排序完成，正在生成处理中");
            } else {
                Ls.tipsErr(d.desc);
            }
        }).always(function () {
            video_list.sortable("enable");
            Ls.log("docIDS>>", docIDS)
            var ids = docIDS.toString().split(","),
                nums = numIDS.toString().split(",");
            for (var i = 0, l = ids.length; i < l; i++) {
                $("#video_list>ul").find('li[data-id="' + ids[i] + '"]').attr("data-no", nums[i]);
            }
        })
    }

    //拉取数据
    var getData = function (pageIndex, columnId, key, condition, status) {
        var pageSize = cur.pageSize;
        var data;
        return Ls.ajaxGet({
            url: "/videoNews/getPage",
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                siteId: GLOBAL_SITEID,
                title: key,
                condition: condition,
                status: status
            }
        }).done(function (d) {

            var v = $("#doc_main");
            //根据当前页同大小，动态调整显示条数。目前只是大概的计算，不准确；还要优化。
            var cols = Math.round(Math.min((v.width() - 50) / 240));
            d.titleNum = 22;
            d.cols = cols;
            d.fileServerPath = GLOBAL_FILESERVERPATH;
            d.GLOBAL_FILESERVERPATH = GLOBAL_FILESERVERPATH;
            d.GLOBAL_FILESERVERNAMEPATH = GLOBAL_FILESERVERNAMEPATH;

            // var listHtml = video_list_template(d);
            var listHtml = Ls.template("video_list_template", d);

            video_list = $("#video_list").html(listHtml).find("ul").sortable({
                handle: ".thumb",
                connectWith: ".video-news",
                placeholder: "ui-state-highlight",
                start: function (e, ui) {
                    ui.placeholder.html('<li></li>');
                    !orderIDS && (orderIDS = getOrderID(ui, "no"));
                },
                update: function (e, ui) {
                    docIDS = getOrderID(ui, "id");
                    updateNum(docIDS, orderIDS);
                }
            });

            //初始化视频播放按扭
            (new play()).init();

            //权限控制
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
                }
            }

            $("#publishs:not(.disabled)").off().on('click', function () {
                publishs(1);
            });

            $("#unpublishs:not(.disabled)").off().on('click', function () {
                publishs(0);
            });

            $("#batchCopy_news:not(.disabled)").off().on('click', function () {
                batchCopy(false);
            });

            $("#batchMove_news:not(.disabled)").off().on('click', function () {
                batchMove(false);
            });


            //批量删除
            $("#deletes:not(.disabled)").off().on('click', function () {
                deletes();
            });

        }).done(function (d) {
            /*cur.pageIndex = d.pageIndex + 1;
            Ls.pagination2({
                total: d.total,
                isTotal: true,
                pageCount: d.pageCount,
                pageIndex: d.pageIndex,
                callback: pageselectCallback
            });*/
            // Ls.pagination(d.pageCount, d.pageIndex, pageselectCallback);


            cur.pageIndex = d.pageIndex;
            var pIndex = cur.pageIndex;
            if (pIndex == 0) {
                pIndex = 1;
            } else {
                pIndex = pIndex + 1;
            }
            Ls.pagination3("#pagination", function (pageIndex) {
                pageselectCallback(pageIndex - 1);
            }, {
                changeCallBack: function (pageSize) {
                    pageselectCallback(0, pageSize);
                },
                pageSize: cur.pageSize,
                currPage: pIndex,
                pageCount: d.pageCount,
                sizeList: [12, 24, 36]
            });
        })

    };

    var play = function () {
        this.id = "";
        this.thumb = "";
        this.playBtn = 0;
    }

    play.prototype = {
        create: function (id) {
            var _this = this;

            _this.playBtn = $("#thumb_" + id);
            _this.status = $("#video_status_" + id);

            if (_this.playBtn.length == 0 && _this.status.length == 0) {
                _this.playBtn = $("<div />").attr({"id": "thumb_" + id, "data-id": id}).data("id", id).css({
                    top: "0",
                    left: "0",
                    position: "absolute",
                    //border: "solid 1px #ff0000",
                    width: _this.thumb.width(),
                    height: _this.thumb.height()
                })
                    .addClass("payer transparent")
                    //.html('<img src="'+GLOBAL_CONTEXTPATH+'/assets/images/play.png">')
                    .appendTo(_this.thumb)
                    .off()
                    .on('click.payer', function (e) {
                        var $this = $(this);
                        var id = $this.attr("data-id");
                        _this.player(id);
                        return false;
                    });
            } else {
                _this.show();
            }

        },
        hide: function () {
            this.playBtn.hide();
        },
        show: function () {
            this.playBtn.show();
        },
        init: function () {
            var _this = this;

            $(".thumb", "#video_list").hover(function () {
                _this.thumb = $(this);
                var id = _this.thumb.attr("data-id");
                _this.create(id);
            }, function () {
                _this.hide();
            })

        },
        player: function (id) {
            Ls.ajaxGet({
                url: "/videoNews/getStatusById",
                data: {
                    id: id
                }
            }).done(function (text) {
                if (text.data == -1) {
                    Ls.tipsErr("视频转换失败，请重新上传");
                }
                if (text.data != 100) {
                    Ls.tipsInfo("视频正在转换，请稍后...");
                } else {
                    Ls.openWin('/videoNews/videoPlayer?id=' + id, '600px', '400px', {
                        id: 'video_player',
                        title: '播放视频',
                        padding: 2
                    });
                }
            });
        }
    }

    var init = function () {
        //初始化布局
        //mini.parse();
        var columnId = content_mgr.node.indicatorId;
        cur.power = content_mgr.node.opt;
        if (cur.power == "super") {
            cur.opt.push("super");
        } else {
            cur.functions = content_mgr.node.functions;
            for (var i = 0; i < cur.functions.length; i++) {
                cur.opt.push(cur.functions[i].action);
            }
        }
        //拉取数据
        getData(cur.pageIndex, columnId, null, null, null).done(function () {


            /*$("#video_list").off().on('click.thumb', '.thumb', function () {
             var $this = $(this);
             var id = $this.attr("data-id");
             var p = new play();
             p.player(id);
             return false;
             })*/

        });
    }

    function editVideoNews(i, id) {
        var url = '/videoNews/editVideo?pageIndex=' + cur.pageIndex;
        if (i == 1) {
            url = '/videoNews/editVideo?id=' + id + '&&pageIndex=' + cur.pageIndex;
        }
        content_mgr.getContentApp(url);
    }

    return {
        init: init,
        editVideoNews: editVideoNews,
        getData: getData
    }

}();
