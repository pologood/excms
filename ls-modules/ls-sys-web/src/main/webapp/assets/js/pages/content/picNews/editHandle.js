function initData() {
    if (pic.id != "") {
        Ls.ajaxGet({
            data: {
                id: pic.id, recordStatus: pic.recordStatus
            },
            url: "/pictureNews/getPicContent",
            success: function (text) {
                var ac = text.data.picture;
                $("#title").val(ac.title);
                if (ac.isBold == 1) {
                    $("#title").css("font-weight", "bold");
                    pic.isBold = 1;
                }
                if (ac.isTilt == 1) {
                    $("#title").css("font-style", "italic");
                    pic.isTilt = 1;
                }
                $("#hit").val(ac.hit);
                $("#resources").val(ac.resources);
                if (ac.isUnderline == 1) {
                    pic.isUnderline = ac.isUnderline;
                    $("#title").css("text-decoration", "underline");
                }
                if (ac.titleColor != null) {
                    $("#title").css("color", ac.titleColor);
                    pic.titleColor = ac.titleColor;
                }
                $("#subTitle").val(ac.subTitle);
                $("#redirectLink").val(ac.redirectLink);
                if (ac.remarks != null) {
                    $("#remarks").val(ac.remarks);
                    $("#attr_ext").removeClass("hide");
                    expand = 1;
                }
                if (ac.author != null && ac.author != "") {
                    $("#author").val(ac.author);
                }
                if($("#responsibilityEditor")){
                    $("#responsibilityEditor").val(ac.responsibilityEditor);
                }
                if (ac.publishDate != null) $("#publishDate").val(ac.publishDate);
                if (ac.isTop == 1) {
                    $("#isTop").prop("checked", true);
                    if (ac.topValidDate != null)
                        $("#topValidDate").val(ac.topValidDate);
                    $("#TopDate").removeClass("hide");
                }
                if (ac.isJob == 1) {
                    $("#isJob").prop("checked", true);
                    if (ac.jobIssueDate != null)
                        $("#jobIssueDate").val(ac.jobIssueDate);
                    $("#JobDate").removeClass("hide");
                }
                if (ac.isNew == 1) $("#isNew").prop("checked", true);
                if (ac.isTitle == 1) $("#isTitle").prop("checked", true);
                if (ac.isAllowComments == 1) $("#isAllowComments").prop("checked", true);
                if (ac.imageLink != null) {
                    if(ac.imageLink.indexOf("/")==-1){
                        if(ac.imageLink.indexOf("/mongo") != -1){
                            $("#imageLink").attr('src', ac.imageLink);
                            $("#imageLink").attr('src', GLOBAL_FILESERVERNAMEPATH + ac.imageLink);
                        }else if(ac.imageLink.indexOf(".") != -1){
                            $("#imageLink").attr('src', GLOBAL_FILESERVERNAMEPATH + ac.imageLink);
                        }else{
                            $("#imageLink").attr('src', GLOBAL_FILESERVERPATH + ac.imageLink);
                        }
                    }else{
                        $("#imageLink").attr('src', ac.imageLink);
                    }
                    pic.imageLink = ac.imageLink;
                }
                pic.num = ac.num;
                if(ac.workFlowStatus == 1){
                    $("#saveAndPublish").attr("style","display:none;")
                }
                pic.workFlowStatus = ac.workFlowStatus;
                /*editor.html(text.data.content);*/
                eweb.setHtml(text.data.content);
                var picList = text.data.picList;
                var d = {data: text.data.picList};
                d.GLOBAL_FILESERVERNAMEPATH = GLOBAL_FILESERVERNAMEPATH;
                d.GLOBAL_FILESERVERPATH = GLOBAL_FILESERVERPATH;
                var picHtml = rendePicList(d);
                $("#picList").html(picHtml);

                _orderFun();
            }
        });
    }
}
var rendePicList = Ls.compile(
    '<? for(var i=0,l=data.length; i<l; i++){ ?>' +
    '<? var el = data[i] ?>' +
    '<tr id="<?=el.picId?>" data-no="<?=el.num?>" >' +
    '  <td class="arrow_move">' +
    '   <div class="rows text-center"><a href="javascript:;"><i class="fa fa-arrows"></i></a></div>' +
    '  </td>' +
    '  <td>' +
    '    <div class="pic_thumb">' +

    '		<img id="thumb_<?=el.picId?>" class="mongoId" data-mogonId="<?=el.thumbPath?>" src="<?= (el.thumbPath.indexOf(".") != -1 ? GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH )+""+el.thumbPath?>"/>' +
    '    </div>' +
    '    <div class="pic_tools">' +
    '      <button class="btn btn-default btn-show-img" onclick="previewPic(<?=el.picId?>,\'<?=el.path?>\')" type="button" title="查看图片">' +
    '        <i class="fa fa-search-plus"></i></button>' +
    '     <button class="btn btn-default ml2 btn-anew-upload" onclick="reupload(<?=el.picId?>)" type="button" title="重新上传">' +
    '        <i class="fa fa-upload"></i></button>' +
    '      <button class="btn btn-default ml2 btn-thumb" id="cover_<?=el.picId?>" onclick="setCover(\'<?=el.thumbPath?>\')" type="button" title="设为封面">' +
    '        <i class="fa fa-picture-o"></i></button>' +
    '      <button class="btn btn-default ml2 btn-edit-img" id="beauty_<?=el.picId?>" onclick="beautifyPic(\'<?=el.path?>\',<?=el.picId?>)" type="button" title="编辑图片">' +
    '        <i class="fa fa-object-group"></i></button>' +
    '     <button class="btn btn-default ml2 btn-delete-img" onclick="deletePic(<?=el.picId?>)" type="button" title="删除图片">' +
    '        <i class="fa fa-trash"></i></button>' +
    '    </div>' +
    '  </td>' +
    '  <td><input type="hidden" class="picId" value="<?=el.picId?>"/>' +
    '    <div class="pic_title">' +
    '      <input value="<?=el.picTitle?>" type="text" class="form-control picTitle" placeholder="">' +
    '    </div>' +
    '    <div class="pic_info">' +
    '      <textarea class="form-control picDesc" rows="4"><?=el.description?></textarea>' +
    '    </div>' +
    '  </td>' +
    '  </tr>' +
    '<?}?>'
)

function getModelConfig() {
    return Ls.ajaxGet({
        data: {
            columnId: cur.columnId
        },
        url: "/content/getModelConfig",
        success: function (d) {
            if (d && d.status == 1 && d.data) {
                config.model = d.data;
                if (config.model.isSensitiveWord == 1 && config.model.isEasyWord == 1 && config.model.isHotWord == 1) {
                    $("#checkWords").show();
                }
                if (config.model.isSensitiveWord == 1) {
                    $("#senWords").remove();
                }
                if (config.model.isEasyWord == 1) {
                    $("#errorWords").remove();
                }
                if (config.model.isHotWord == 1) {
                    $("#hotWords").remove();
                }
                if (config.model.isComment == 1) {
                    $("#isComment").removeClass("hide");
                }
            }
        }
    });
}

//新闻图片上传
function uploadPic() {
    Ls.uploadFile(function (filePath) {
        var paths = [];
        for (var i = 0; i < filePath.length; i++) {
            paths.push(filePath[i].fileName);
        }
        //  console.log(paths);
        getPaths(paths);
        return true;
    }, {
        upload_url: '/pictureNews/saveNewsPic',
        post_params: {
            "siteId": pic.siteId,
            "columnId": pic.columnId,
            "desc": "图片新闻轮播图片",
            "contentId": pic.id,
        }
    })
}

function getPaths(paths) {
    Ls.ajax({
        data: {paths: paths},
        url: "/pictureNews/getListByPath",
        success: function (text) {
            if (text.status == 1) {
                $("#picList").append(rendePicList(text));
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}

//扩展信息
$("#btn_extend_picture").click(function () {
    var AE = $(".picture_attr_ext");
    if (config.expand == 0) {
        AE.removeClass("hide");
        config.expand = 1;
    } else {
        AE.addClass("hide");
        config.expand = 0;
    }
})

function getContent() {
    pic.title = $("#title").val();
    if (pic.title == "") {
        Ls.tipsInfo("标题不能为空");
        return 0;
    }
    pic.author = $("#author").val();
    if (pic.author == "") {
        Ls.tipsInfo("作者不能为空");
        return 0;
    }

    if(document.getElementById("responsibilityEditor")){
        pic.responsibilityEditor = $("#responsibilityEditor").val();
        if (pic.responsibilityEditor == "") {
            Ls.tipsInfo("责任编辑不能为空");
            return 0;
        }
    }

    var hasImg = false;
    if($("#imageLink").attr("src")==(GLOBAL_CONTEXTPATH + '/assets/images/no.photo.jpg')){

    }else{
        hasImg = true;
    }

    /*var content =  editor.html();*/
    var content =  eweb.getHtml();
    var regexp = /<img\b[^>]*>/ ;
    if(regexp.test(content)){
        hasImg = true;
    }

    if(!hasImg){//没有图片
        Ls.tipsInfo("请在缩略图或者内容中上传图片！");
        return 0;
    }

    pic.resources = $("#resources").val();
    pic.subTitle = $("#subTitle").val();
    pic.redirectLink = $("#redirectLink").val();
    pic.publishDate = $("#publishDate").val();
    if ($("#isTop").is(':checked')) {
        pic.isTop = 1;
        pic.topValidDate = $("#topValidDate").val();
    }
    if ($("#isNew").is(':checked')) pic.isNew = 1;
    if ($("#isTitle").is(':checked')) pic.isTitle = 1;
    if ($("#isAllowComments").is(':checked')) pic.isAllowComments = 1;
    pic.remarks = $("#remarks").val();
    /*pic.content = editor.html();
    pic.articleText = editor.text();*/
    pic.content = eweb.getHtml();
    pic.articleText = eweb.getText();
    pic.picList = [];
    pic.synColumnIds = cur.synColumnIds;
    $("#picList tr").each(function (index) {
        var _this = $(this);
        var imgList = {
            "picId": _this.find(".picId").val(),
            "picTitle": Ls.str2Json(_this.find(".picTitle").val()),
            "description": Ls.str2Json(_this.find(".picDesc").val()),
            "sortNum": (index + 1)
        }
        pic.picList.push(imgList);
    })
    if ($("#hit").val() == null || $("#hit").val() == "") {
        pic.hit = 0;
    }else{
        pic.hit = $("#hit").val();
    }
    pic.picList = JSON.stringify(pic.picList);
    if ($("#isJob").is(':checked')) {
        pic.isJob = 1;
        pic.jobIssueDate = $("#jobIssueDate").val();
    }
    return 1;
}

function getPictureFormData(){
    pic.title = $("#title").val();
    if (pic.title == "") {
        Ls.tipsInfo("标题不能为空");
        return 0;
    }
    pic.author = $("#author").val();
    if (pic.author == "") {
        Ls.tipsInfo("作者不能为空");
        return 0;
    }

    pic.resources = $("#resources").val();
    pic.subTitle = $("#subTitle").val();
    pic.redirectLink = $("#redirectLink").val();
    pic.publishDate = $("#publishDate").val();
    if ($("#isTop").is(':checked')) {
        pic.isTop = 1;
        pic.topValidDate = $("#topValidDate").val();
    }
    if ($("#isNew").is(':checked')) pic.isNew = 1;
    if ($("#isTitle").is(':checked')) pic.isTitle = 1;
    if ($("#isAllowComments").is(':checked')) pic.isAllowComments = 1;
    pic.remarks = $("#remarks").val();
    /*pic.content = editor.html();
    pic.articleText = editor.text();*/
    pic.content = eweb.getHtml();
    pic.articleText = eweb.getText();
    pic.picList = [];
    pic.synColumnIds = cur.synColumnIds;
    $("#picList tr").each(function (index) {
        var _this = $(this);
        var imgList = {
            "picId": _this.find(".picId").val(),
            "picTitle": Ls.str2Json(_this.find(".picTitle").val()),
            "description": Ls.str2Json(_this.find(".picDesc").val()),
            "sortNum": (index + 1)
        }
        pic.picList.push(imgList);
    })

    pic.picList = JSON.stringify(pic.picList);
    if ($("#isJob").is(':checked')) {
        pic.isJob = 1;
        pic.jobIssueDate = $("#jobIssueDate").val();
    }
    /*pic.content = editor.html();
    pic.articleText = editor.text();*/
    pic.content = eweb.getHtml();
    pic.articleText = eweb.getText();
    return pic;
}

$("#saveArticle").click(function () {
    pic.isPublish = 0;
    if (getContent() == 1) {
        getModelConfig().done(function (d) {
            if (d && d.status == 1 && d.data) {
                var model = d.data;
                if (model.isSensitiveWord == 1 || model.isEasyWord == 1 || model.isHotWord == 1) {
                    var types = getTypes();
                    /*var content = editor.html();*/
                    var content = eweb.getHtml();
                    Ls.ajax({
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
            }
        });
    }
})

$("#saveAndPublish").click(function () {
    pic.isPublish = 1;
    if (getContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            /*var content = editor.html();*/
            var content = eweb.getHtml();
            Ls.ajax({
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
    }
})

function save() {
    var tips = Ls.tipsLoading("正在提交...", {autoClose: false});
    /*pic.content = editor.html();
    pic.articleText = editor.text();*/
    pic.content = eweb.getHtml();
    pic.articleText = eweb.getText();
    Ls.ajax({
        data: pic,
        url: "/pictureNews/savePictureNews",
        success: function (text) {
            if (text.status == 1) {
                tips.hide();
                goBack();
                if(pic.isPublish == 1){
                    Ls.tipsOk("正在生成处理中");
                }else{
                    Ls.tipsOk("保存成功");
                }

            } else {
                tips.hide();
                Ls.tipsErr(text.desc);
            }
        }
    });
}

function goBack() {
    content_mgr.getContentApp("/" + pic.typeCode + "/index?indicatorId=" + pic.columnId + "&pageIndex=" + config.pageIndex);
}

//上传缩略图
$("#uploadImg").click(function () {
    if (config.model.isEnableBeauty == 1) {
        meiTuUpload();
    } else {
        normalUpload();
    }
});

//美图上传缩略图

function meiTuUpload() {
    art.dialog.data("imgPath", pic.imageLink);
    art.dialog.data("siteId", pic.siteId);
    art.dialog.data("columnId", pic.columnId);
    art.dialog.data("contentId", pic.id);
    art.dialog.data("width", config.model.picWidth);
    art.dialog.data("heigth", config.model.picHeight);
    Ls.openWin('/articleNews/thumbUpload', '650px', '410px', {
        lock: true,
        title: '缩略图'
    });
}
//普通上传缩略图
function normalUpload() {
    Ls.uploadFile(function (filePath) {
        changeImg(filePath[0].fileName);
        return true;
    }, {
        upload_url: '/content/normalUploadThumb',
        file_upload_limit: 1,
        post_params: {
            "siteId": pic.siteId,
            "columnId": pic.columnId,
            "contentId": pic.id,
            "imgPath": pic.imageLink,
            "desc": "上传缩略图"
        }
    });
}


//预览
function previewPic(picId, path) {
    art.dialog.data("path", path);
    art.dialog.open('/pictureNews/previewPic?picId=' + picId, {
        width: '780px',
        height: '640px',
        lock: true,
        title: '图片预览'
    });
}

$("#bold").click(function () {
    var T = $("#title");
    if (pic.isBold == 0) {
        T.css("font-weight", "bold");
        pic.isBold = 1;
    } else {
        T.css("font-weight", "normal");
        pic.isBold = 0;
    }
})

$("#tilt").click(function () {
    var T = $("#title");
    if (pic.isTilt == 0) {
        T.css("font-style", "italic");
        pic.isTilt = 1;
    } else {
        T.css("font-style", "normal");
        pic.isTilt = 0;
    }
})

$("#underline").click(function () {
    var T = $("#title");
    if (pic.isUnderline == 0) {
        T.css("text-decoration", "underline");
        pic.isUnderline = 1;
    } else {
        T.css("text-decoration", "none");
        pic.isUnderline = 0;
    }
})

//图片美化
function beautifyPic(path, picId) {
    art.dialog.data("path", path);
    art.dialog.data("siteId", pic.siteId);
    art.dialog.data("columnId", pic.columnId);
    art.dialog.data("contentId", pic.id);
    art.dialog.data("picId", picId);
    art.dialog.open('/pictureNews/beautifyPic?picId=' + picId, {
        width: '780px',
        height: '540px',
        lock: true,
        title: '编辑图片'
    });
}

function changeThumb(picId, path) {
    if(path.indexOf('.') != -1){
        $("#thumb_" + picId).attr("src", GLOBAL_FILESERVERNAMEPATH + path);
    }else{
        $("#thumb_" + picId).attr("src", GLOBAL_FILESERVERPATH + path);
    }
    $("#cover_" + picId).off().on('click', function () {
        setCover(path);
    })
}


//移除封面缩略图
$("#delImg").click(function () {
    $("#imageLink").attr("src", GLOBAL_CONTEXTPATH + '/assets/images/no.photo.jpg');
    pic.imageLink = '';
})

//缩略封面图路径
function changeImg(path) {
    if(path.indexOf('.') != -1){
        $("#imageLink").attr("src", GLOBAL_FILESERVERNAMEPATH + path);
    }else{
        $("#imageLink").attr("src", GLOBAL_FILESERVERPATH + path);
    }
    pic.imageLink = path;
}


//封面设置
function setCover(thumbPath) {
    if(thumbPath.indexOf('.') != -1){
        $("#imageLink").attr("src", GLOBAL_FILESERVERNAMEPATH + thumbPath);
    }else{
        $("#imageLink").attr("src", GLOBAL_FILESERVERPATH + thumbPath);
    }
    pic.imageLink = thumbPath;
    Ls.tipsOk("设置成功");
}


//删除图片
function deletePic(picId) {
    Ls.ajax({
        data: {
            picId: picId
        },
        url: "/pictureNews/delPic",
        success: function (text) {
            if (text.status == 1) {
                $("#" + picId).remove();
                Ls.tipsOk("删除成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}

//列表排序
_orderFun = function () {
    var fixHelper = function (e, ui) {
            ui.children().each(function () {
                var _this = $(this)
                _this.width(_this.width());
            });
            return ui;
        },
        uiOrder = function (ui, data) {
            return ui.item.parent().children().map(function () {
                var orderID = $(this).attr(data);
                return orderID;
            }).get().join(',');
        },
        IDS = 0, orderIDS = 0;

    //绑定排序事件
    _tblist = $("#picList").sortable({
        items: '>tr',
        axis: "y",
        cursor: "move",
        handle: ".arrow_move",
        helper: fixHelper,
        connectWith: ">tbody",
        forcePlaceholderSize: true,
        placeholder: 'must-have-class',
        start: function (event, ui) {
            ui.placeholder.html('<td colspan="10"></td>');
            orderIDS = uiOrder(ui, "data-no");
        },
        stop: function (e, ui) {
        },
        update: function (e, ui) {
            var docIDS = uiOrder(ui, "id");
            console.log('orderIDS', orderIDS)
            console.log('docIDS', docIDS)
            updateNum(docIDS, orderIDS);
        }
    })
}

//更新列表排序号
var updateNum = function (docIDS, numIDS) {
    console.log('docIDS', docIDS)
    console.log('numIDS', numIDS)
    Ls.ajax({
        url: "/pictureNews/updateNums",
        beforeSend: function () {
            $("#picList").sortable("disable");
        },
        data: {
            picIds: docIDS,
            sortNums: numIDS
        }
    }).done(function (d) {

        if (d.status) {
            Ls.tipsOk("排序完成，正在生成处理中");
        } else {
            Ls.tipsErr(d.desc);
        }
    }).always(function () {
        $("#picList").sortable("enable");
        var ids = docIDS.toString().split(","),
            nums = numIDS.toString().split(",");
        for (var i = 0, l = ids.length; i < l; i++) {
            $("#doc_list_body").find('tr[data-id="' + ids[i] + '"]').attr("data-no", nums[i]);
        }
    })
}

//重新上传
function reupload(picId) {

    Ls.uploadFile(function (filePath) {
        reloadPic(picId, filePath[0].fileName);
        return true;
    }, {
        upload_url: '/pictureNews/saveNewsPic',
        file_upload_limit: 1,
        post_params: {
            "siteId": pic.siteId,
            "columnId": pic.columnId,
            "contentId": pic.id,
            "picId": picId,
            "desc": "图片新闻轮播图片"
        }
    })
}

//重新加载图片
function reloadPic(picId, path) {
    if(path.indexOf('.') != -1){
        $("#thumb_" + picId).attr("src", GLOBAL_FILESERVERNAMEPATH + path);
    }else{
        $("#thumb_" + picId).attr("src", GLOBAL_FILESERVERPATH + path);
    }
}


//敏感词检测
function senWordsCheck() {
    /*article.content = editor.html();*/
    article.content = eweb.getHtml();
    Ls.ajax({
        data: {
            content: article.content,
            flag: "SENSITIVE"
        },
        url: "/content/errorWordsCheck",
        success: function (text) {
            if (text.status == 1) {
                Ls.tipsOk("敏感词检测成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}

//易错词检测
function errorWordsCheck() {
    /*article.content = editor.html();*/
    article.content = eweb.getHtml();
    Ls.ajax({
        data: {
            content: article.content,
            flag: "EASYERR"
        },
        url: "/content/errorWordsCheck",
        success: function (text) {
            if (text.status == 1) {
                Ls.tipsOk("易错词检测成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}

//热词检测
function hotWordsCheck() {
    /*article.content = editor.html();*/
    article.content = eweb.getHtml();
    Ls.ajax({
        data: {
            content: article.content,
            flag: "HOT"
        },
        url: "/content/errorWordsCheck",
        success: function (text) {
            if (text.status == 1) {
                Ls.tipsOk("热词检测成功");
            } else {
                Ls.tipsErr(text.desc);
            }
        }
    });
}
$("#isTop").click(function () {
    if ($("#isTop").is(':checked')) {
        $("#TopDate").removeClass("hide");
    } else {
        $("#TopDate").addClass("hide");
    }
})

$("#isJob").click(function () {
    if ($("#isJob").is(':checked')) {
        $("#JobDate").removeClass("hide");
    } else {
        $("#JobDate").addClass("hide");
    }

})