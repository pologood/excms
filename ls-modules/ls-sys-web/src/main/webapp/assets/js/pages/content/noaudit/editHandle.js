function initData() {
    if (article.id != "") {
        Ls.ajaxGet({
            data: {
                id: article.id
            },
            url: "/articleNews/getArticleContent",
            success: function (text) {
                var ac = text.data.article;
                article.siteId=ac.siteId;
                article.title=ac.title;
                article.subTitle=ac.subTitle;
                $("#title").val(ac.title);
                if (ac.isBold == 1) {
                    $("#title").css("font-weight", "bold");
                    article.isBold = 1;
                }
                if (ac.isTilt == 1) {
                    $("#title").css("font-style", "italic");
                    article.isTilt = 1;
                }
                $("#resources").val(ac.resources);
                if (ac.isUnderline == 1) {
                    article.isUnderline = ac.isUnderline;
                    $("#title").css("text-decoration", "underline");
                }
                if (ac.titleColor != null) {
                    $("#title").css("color", ac.titleColor);
                    article.titleColor = ac.titleColor;
                }
                $("#subTitle").val(ac.subTitle);
                $("#redirectLink").val(ac.redirectLink);
                if (ac.remarks != null) {
                    $("#remarks").val(ac.remarks);
                    $("#attr_ext").removeClass("hide");
                    expand = 1;
                }
                $("#author").val(ac.author);
                if (ac.publishDate != null) $("#publishDate").val(ac.publishDate);
                if (ac.isTop == 1) $("#isTop").prop("checked", true);
                if (ac.isNew == 1) $("#isNew").prop("checked", true);
                if (ac.isTitle == 1) $("#isTitle").prop("checked", true);
                if (ac.isAllowComments == 1) $("#isAllowComments").prop("checked", true);
                if (ac.imageLink != null) {
                    $("#imageLink").attr('src', '$!{fileServerPath}' + ac.imageLink);
                    article.imageLink = ac.imageLink;
                }
                article.num = ac.num;
                //editor.html(text.data.content);
                eweb.setHtml(text.data.content);
            }
        });
    }
}

function getModelConfig() {
    Ls.ajaxGet({
        data: {
            columnId: cur.columnId
        },
        url: "/content/getModelConfig",
        success: function (text) {
            if (text.status == 1) {
                config.model = text.data;
                if (config.model.isSensitiveWord == 1 && config.model.isEasyWord == 1 && config.model.isHotWord == 1) {
                    $("#checkWords").removeClass("article_attr_ext");
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
            } else {
                Ls.tipsErr("内容模型获取错误");
            }
        }
    });
}

$("#btn_extend_article").click(function () {
    var AE = $(".article_attr_ext");
    if (expand == 0) {
        AE.removeClass("hide");
        expand = 1;
    } else {
        AE.addClass("hide");
        expand = 0;
    }
})

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
    art.dialog.data("imgPath", article.imageLink);
    art.dialog.data("siteId", article.siteId);
    art.dialog.data("columnId", article.columnId);
    art.dialog.data("contentId", article.id);
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
            "siteId": article.siteId,
            "columnId": article.columnId,
            "contentId": article.id,
            "imgPath": article.imageLink,
            "desc": "上传缩略图"
        }
    });
}

$("#bold").click(function () {
    var T = $("#title");
    if (article.isBold == 0) {
        T.css("font-weight", "bold");
        article.isBold = 1;
    } else {
        T.css("font-weight", "normal");
        article.isBold = 0;
    }
})

$("#tilt").click(function () {
    var T = $("#title");
    if (article.isTilt == 0) {
        T.css("font-style", "italic");
        article.isTilt = 1;
    } else {
        T.css("font-style", "normal");
        article.isTilt = 0;
    }
})

$("#underline").click(function () {
    var T = $("#title");
    if (article.isUnderline == 0) {
        T.css("text-decoration", "underline");
        article.isUnderline = 1;
    } else {
        T.css("text-decoration", "none");
        article.isUnderline = 0;
    }
})

function saveContent() {
    article.title = $("#title").val();
    if (article.title == "") {
        Ls.tipsInfo("标题不能为空");
        return 0;
    }
    article.author = $("#author").val();
    if (article.author == "") {
        Ls.tipsInfo("作者不能为空");
        return 0;
    }
    article.resources = $("#resources").val();
    article.subTitle = $("#subTitle").val();
    article.redirectLink = $("#redirectLink").val();
    article.publishDate = $("#publishDate").val();
    if ($("#isTop").is(':checked')) article.isTop = 1;
    if ($("#isNew").is(':checked')) article.isNew = 1;
    if ($("#isTitle").is(':checked')) article.isTitle = 1;
    if ($("#isAllowComments").is(':checked')) article.isAllowComments = 1;
    article.remarks = $("#remarks").val();
    /*article.content = editor.html();
    article.articleText = editor.text();*/
    article.content = eweb.getHtml();
    article.articleText = eweb.getText();
    article.synColumnIds = cur.synColumnIds;
    return 1;
}

$("#saveArticle").click(function () {
    if (saveContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            //var content = editor.html();
            var content = eweb.getHtml();
            Ls.ajax({
            	type:"POST",
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

$("#saveAndPublish").click(function () {
    article.isPublish = 1;
    if (saveContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            //var content = editor.html();
            var content = eweb.getHtml();
            Ls.ajax({
            	type:"POST",
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

function save() {
    var tips = Ls.tipsLoading("正在提交...", {autoClose: false});
    /*article.content = editor.html();
    article.articleText = editor.text();*/
    article.content = eweb.getHtml();
    article.articleText = eweb.getText();
    Ls.ajax({
        data: article,
        url: "/articleNews/saveArticleNews",
        success: function (text) {
            if (text.status == 1) {
                tips.hide();
                //goBack();
                Ls.tipsOk("保存成功");
                Ls.winClose();
                Ls.getWin.win.grid.reload();
            } else {
                tips.hide();
                Ls.tipsErr(text.desc);
            }
        }
    });
}


function goBack() {
    content_mgr.getContentApp("/" + article.typeCode + "/index?indicatorId=" + article.columnId + "&pageIndex=" + config.pageIndex);
}

//移除缩略图
$("#delImg").click(function () {
    $("#imageLink").attr("src", GLOBAL_CONTEXTPATH + '/assets/images/no.photo.jpg');
    article.imageLink = '';
});

//缩略图路径
function changeImg(path) {
    $("#imageLink").attr("src", (path.indexOf(".") != -1 ?GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH ) + path);
    article.imageLink = path;
}