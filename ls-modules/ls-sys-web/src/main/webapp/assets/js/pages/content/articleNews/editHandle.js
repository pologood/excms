function initData() {
    if (article.id != "") {
        Ls.ajaxGet({
            data: {
                id: article.id, recordStatus: article.recordStatus
            },
            url: "/articleNews/getArticleContent",
            success: function (text) {
                var ac = text.data.article;
                article.createOrganId = ac.createOrganId;
                article.siteId = ac.siteId;
                article.workFlowStatus = ac.workFlowStatus;
                if(article.workFlowStatus==2){
                    $(".reportBtn").addClass("hide")
                }
                if (Ls.isEmpty(article.columnId)) {
                    article.columnId = ac.columnId;
                }
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
                $("#hit").val(ac.hit);
                $("#subTitle").val(ac.subTitle);
                $("#redirectLink").val(ac.redirectLink);
                if (ac.redirectLink != null && ac.redirectLink != "") {
                    $("#isLink").val(ac.isLink == 1).prop("checked", true);
                    $("#redirectLink").val(ac.redirectLink);
                    $("#OutLink").removeClass("hide");
                    $("#edit_content").addClass("hide");
                }
                if (ac.remarks != null) {
                    $("#remarks").val(ac.remarks);
                    $("#attr_ext").removeClass("hide");
                    expand = 1;
                }
                //if (ac.author != null && ac.author != "") {
                $("#author").val(ac.author);
                //}


                if ($("#responsibilityEditor")) {
                    article.responsibilityEditor = ac.responsibilityEditor;
                    $("#responsibilityEditor").val(ac.responsibilityEditor);
                }

                if ($("#editor")) {
                    article.editor = ac.editor;
                    if (ac.editor == "导入") {
                    } else {
                        $("#editor").val(ac.editor);
                    }
                }

                if (ac.createDate != null) $("#createDate").val(ac.createDate);
                if (ac.publishDate != null) $("#publishDate").val(ac.publishDate);
                if (ac.isTop == 1) {
                    $("#isTop").prop("checked", true);
                    if (ac.topValidDate != null) {
                        $("#topValidDate").val(ac.topValidDate);
                    }
                    $("#TopDate").removeClass("hide");
                }
                if (ac.isJob == 1) {
                    $("#isJob").prop("checked", true);
                    if (ac.jobIssueDate != null)
                        $("#jobIssueDate").val(Ls.dateFormat(ac.jobIssueDate, "yyyy-MM-dd hh:mm"));
                    $("#JobDate").removeClass("hide");
                }
                if (ac.isNew == 1) $("#isNew").prop("checked", true);
                if (ac.isTitle == 1) $("#isTitle").prop("checked", true);
                if (ac.isAllowComments == 1) $("#isAllowComments").prop("checked", true);
                if (ac.imageLink != null&&ac.imageLink != '') {
                    if (ac.imageLink.indexOf("/") != -1) {
                        $("#imageLink").attr('src', ac.imageLink);
                    } else if (ac.imageLink.indexOf(".") != -1) {
                        $("#imageLink").attr('src', GLOBAL_FILESERVERNAMEPATH  + ac.imageLink);
                    } else {
                        $("#imageLink").attr('src', GLOBAL_FILESERVERPATH  + ac.imageLink);
                    }
                    article.imageLink = ac.imageLink;
                }
                article.num = ac.num;
                //editor.html(text.data.content);
                eweb.setHtml(text.data.content);
                article.quoteStatus = ac.quoteStatus;
                $("#public_synOrganCatIds").attr("disabled", "disabled");
                $("#public_synOrganCatNames").attr("disabled", "disabled");
                $("#synMsgCatIds").attr("disabled", "disabled");

                /**办事办件状态**/
                if (article.typeCode == "handleItems") {
                    $(":radio[name='handleStatus'][value= '" + ac.handleStatus + "']").attr("checked", "checked")
                }
                var title_new=ac.title;
                if(title_new.indexOf("<br>")!= -1){
                    title_new=title_new.replace("<br>","\n");
                }
                ac.title=title_new;
                $("#title").val(ac.title);

                var title_new=ac.title;
                if(title_new.indexOf("<br>")!= -1){
                    title_new=title_new.replaceAll("<br>","\n");
                }
                ac.title=title_new;
                $("#title").val(ac.title);
                article.member = ac.member;
                article.memberWant = ac.memberWant;
                article.memberConStu = ac.memberConStu;
                article.backReason =  ac.backReason;
                article.memPutDate =  ac.memPutDate;
            }
        });
    }
}

function getModelConfig() {
    if (!Ls.isEmpty(cur.columnId)) {
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
}

function getRelation() {
    if (article.id != null) {
        Ls.ajaxGet({
            data: {
                causeId: article.id,
                modelCode: ""
            },
            url: "/referRelation/getRelation",
            success: function (text) {
                if (text.status == 1) {
                    var data = text.data;
                    var publicRel = [];
                    var msgSubRel = [];
                    var catName = "";
                    var catId = "";
                    if (data != null) {
                        for (var i = 0; i < data.length; i++) {
                            if (data[i].modelCode == "PUBLIC") {
                                publicRel.push(data[i]);
                            } else if (data[i].modelCode == "MSGSUBMIT") {
                                msgSubRel.push(data[i]);
                            }
                        }

                        for (var i = 0; i < publicRel.length; i++) {
                            if (i == 0) {
                                catName += publicRel[i].referName;
                                catId += publicRel[i].createOrganId + "_" + publicRel[i].columnId;
                            } else {
                                catName += "," + publicRel[i].referName;
                                catId += "," + publicRel[i].createOrganId + "_" + publicRel[i].columnId;
                            }
                        }
                    }
                    var msgId = "";
                    if (msgSubRel.length > 0) {
                        msgId = msgSubRel[0].columnId;
                    }
                    getClassifyModel(msgId);
                    $("#public_synOrganCatNames").val(catName);
                    organ_catalog_select_tree.init('public_synOrganCat', catId);
                }
            }
        });
    } else {
        organ_catalog_select_tree.init('public_synOrganCat', "");
    }
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
});

//上传缩略图
$("#uploadImg,.thumb").click(function () {
    if (config && config.model && config.model.isEnableBeauty == 1) {
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
        file_size_limit: "500 KB",
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
});

$("#tilt").click(function () {
    var T = $("#title");
    if (article.isTilt == 0) {
        T.css("font-style", "italic");
        article.isTilt = 1;
    } else {
        T.css("font-style", "normal");
        article.isTilt = 0;
    }
});

$("#underline").click(function () {
    var T = $("#title");
    if (article.isUnderline == 0) {
        T.css("text-decoration", "underline");
        article.isUnderline = 1;
    } else {
        T.css("text-decoration", "none");
        article.isUnderline = 0;
    }
});

Date.prototype.Format = function (fmt) { //author: meizz
    var o = {
        "M+": this.getMonth() + 1, //月份
        "d+": this.getDate(), //日
        "H+": this.getHours(), //小时
        "m+": this.getMinutes(), //分
        "s+": this.getSeconds(), //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds() //毫秒
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


function titleExist(title) {
    var flag = 1;
    Ls.ajax({
        url: "/articleNews/titleExist",
        data: {
            id: article.id,
            columnId: article.columnId,
            title: title
        },
        async: false
    }).done(function (d) {
        flag = d.data;
    });
    return flag;
}


function saveContent() {
    article.title = $("#title").val();

    if (article.title == "") {
        Ls.tipsInfo("标题不能为空");
        return 0;
    } else {
        if (article.title.trim().length > 200) {
            Ls.tipsInfo("标题长度不能超过200");
            return 0;
        }
    }
    article.title = article.title.trim();

    // if (titleExist(article.title) == 0) {
    //     if (confirm("当前栏目下存在相同标题的新闻，您是否确定要继续添加？")) {
    //
    //     } else {
    //         return 0;
    //     }
    // }

    article.subTitle = $("#subTitle").val();
    if (article.subTitle != "") {
        if (article.subTitle.trim().length > 200) {
            Ls.tipsInfo("副标题长度不能超过200");
            return 0;
        }
    }

    article.author = $("#author").val();
    //if (article.author == "") {
    //    Ls.tipsInfo("作者不能为空");
    //    return 0;
    //}

    if (document.getElementById("responsibilityEditor")) {
        article.responsibilityEditor = $("#responsibilityEditor").val();
        // if (article.responsibilityEditor == "") {
        //     Ls.tipsInfo("责任编辑不能为空");
        //     return 0;
        // }
    }

    if (document.getElementById("editor")) {
        if (article.editor == "导入") {

        } else {
            article.editor = $("#editor").val();
        }
    }


    article.resources = $("#resources").val();

    article.redirectLink = $("#redirectLink").val();
    article.publishDate = $("#publishDate").val();
    if ($("#isTop").is(':checked')) {
        article.isTop = 1;
        article.topValidDate = $("#topValidDate").val();
    }
    if ($("#isLink").is(':checked')) {
        article.isLink = 1;
        if (article.redirectLink == "") {
            Ls.tipsInfo("跳转地址不能为空!");
            return 0;
        }else{
            var reg=/(http|ftp|https):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
            if(!reg.test(article.redirectLink)){
                Ls.tipsInfo("请输入正确的网址!");
                return 0;
            }
        }
    } else {
        article.isLink = 0;
        article.redirectLink = "";
    }
    if ($("#isJob").is(':checked')) {
        article.isJob = 1;
        article.jobIssueDate = $("#jobIssueDate").val();
        if (article.jobIssueDate != null && article.jobIssueDate != "") {

        } else {
            article.isJob = 0;
        }

    }
    if ($("#isNew").is(':checked')) article.isNew = 1;
    if ($("#isTitle").is(':checked')) article.isTitle = 1;
    if ($("#isAllowComments").is(':checked')) article.isAllowComments = 1;
    article.content = eweb.getHtml();
    article.articleText = eweb.getText();
    article.remarks = $("#remarks").val();
    if (article.remarks == "") {
        var content = article.articleText;
        content = content.replace(/<\/?[^>]*>/g, ''); //去除HTML tag
        content = content.replace(/[ | ]*\n/g, '\n'); //去除行尾空白
        content = content.replace(/\n[\s| | ]*\r/g, '\n'); //去除多余空行
        content = content.replace(/&nbsp;/ig, '');//去掉&nbsp;
        content = content.replace(" ", "");//去掉空格
        article.remarks = content.substring(0, 100);
    }
    if ($("#hit").val() == null || $("#hit").val() == "") {
        article.hit = 0;
    }else{
        article.hit = $("#hit").val();
    }

    /**办事办件状态**/
    if (article.typeCode == "handleItems") {
        article.handleStatus = $('#handleStatus input:radio:checked').val();
    }
    /**************/
    return 1;
}


function getFromData() {

    article.title = $("#title").val();
    article.author = $("#author").val();
    if ($("#responsibilityEditor")) {
        article.responsibilityEditor = $("#responsibilityEditor").val();
    }
    article.resources = $("#resources").val();
    article.subTitle = $("#subTitle").val();
    article.redirectLink = $("#redirectLink").val();
    article.publishDate = $("#publishDate").val();
    if ($("#isTop").is(':checked')) {
        article.isTop = 1;
        article.topValidDate = $("#topValidDate").val();
    }
    if ($("#isJob").is(':checked')) {
        article.isJob = 1;
        article.jobIssueDate = $("#jobIssueDate").val();
    }
    if ($("#isNew").is(':checked')) article.isNew = 1;
    if ($("#isTitle").is(':checked')) article.isTitle = 1;
    if ($("#isAllowComments").is(':checked')) article.isAllowComments = 1;
    article.remarks = $("#remarks").val();
    article.content = eweb.getHtml();

    article.articleText = eweb.getText();

    return article;
}

/*保存按钮对应操作*/
$("#saveArticle").click(function () {
    article.isPublish = 0;
    if (saveContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            var content = eweb.getHtml();
            var title = $("#title").val() || "";
            var author = $("#author").val() || "";
            var resources = $("#resources").val() || "";
            var subTitle = $("#subTitle").val() || "";
            var remarks = $("#remarks").val() || "";

            //新闻内容验证
            Ls.ajax({
                type: "POST",
                data: {
                    content: content + title + author + resources + subTitle + remarks,
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

/*报审按钮对应操作*/
$("#report").click(function () {
    if (saveContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            var content = eweb.getHtml();
            //新闻内容验证
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
                            report();
                        }
                    }
                }
            });
        } else {
            report();
        }
    }
});

/*保存并发布按钮对应操作*/
$("#saveAndPublish").click(function () {
    article.isPublish = 1;
    if (saveContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            var content = eweb.getHtml();
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
    }
});

/*保存并添加按钮对应操作*/
$("#saveAndAdd").click(function () {
    article.isPublish = 0;
    article.openAdd = 1;
    if (saveContent() == 1) {
        if (config.model.isSensitiveWord == 1 || config.model.isEasyWord == 1 || config.model.isHotWord == 1) {
            var types = getTypes();
            var content = eweb.getHtml();
            //新闻内容验证
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
    }
});


//保存信息与后端数据相连
function save() {
    var tips = Ls.tips("正在提交...", {icons: "loading", autoClose: false});
    article.content = eweb.getHtml();
    article.articleText = eweb.getText();
    Ls.ajax({
        data: article,
        url: "/articleNews/saveArticleNews",
        success: function (text) {
            if (text.status == 1) {
                tips.hide();
                if (article.openAdd == 1) {
                    goAdd();
                } else {
                    goBack();
                }
                if(article.isPublish == 1){
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


//报审
function report() {
    var tips = Ls.tips("正在报审...", {icons: "loading", autoClose: false});
    article.content = eweb.getHtml();
    article.articleText = eweb.getText();
    Ls.ajax({
        data: article,
        url: "/articleNews/saveArticleNews",
        success: function (text) {
            if (text.status == 1) {
                tips.hide();
                cur.processConfig = getProcessConfig(content_mgr.node.indicatorId);
                startProcess1("EX", content_mgr.node.indicatorId, "ArticleNews", text.data, cur.processConfig.processId, cur.processConfig.processName);
                // goBack();
                // // }
                // Ls.tipsOk("报审成功");
            } else {
                tips.hide();
                Ls.tipsErr(text.desc);
            }
        }
    });
}


function Syncto() {
    var r = "/content/toCopyRefer?contentId=0&source=1&type=1&synColumnIds=" + article.synColumnIds + "&synOrganCatIds=" + article.synOrganCatIds
        + "&synColumnIsPublishs=" + article.synColumnIsPublishs + "&synOrganIsPublishs=" + article.synOrganIsPublishs;
    +"&synMsgCatIds=" + article.synMsgCatIds + "&synWeixin=" + article.synWeixin

    Ls.openWin(r, "400px", "400px", {
        id: "copy_refer_page",
        title: "同步到",
        ok: function () {
            iframe = this.iframe.contentWindow;
            data = iframe.ok();
            article.synColumnIds = data.synColumnIds;
            article.synColumnIsPublishs = data.synColumnIsPublishs;
            article.synOrganCatIds = data.synOrganCatIds;
            article.synOrganIsPublishs = data.synOrganIsPublishs;
            article.synMsgCatIds = data.synMsgCatIds;
            article.synWeixin = data.synWeixin;
            article.publicSiteId = data.publicSiteId;

            var sysctoView = "";
            if (article.synColumnIds != null && article.synColumnIds != "") {
                sysctoView += "内容管理：" + data.synColumnNames + "\n";
            }
            if (article.synOrganCatIds != null && article.synOrganCatIds != "") {
                sysctoView += "信息公开：" + data.synOrganCatNames + "\n";
            }
            if (article.synMsgCatIds != null && article.synMsgCatIds != "") {
                sysctoView += "信息报送：" + data.synMsgCatNames + "\n";
            }
            if (article.synWeixin != null && article.synWeixin != "") {
                sysctoView += "两微一端";
            }

            $("#synctoView").val(sysctoView);
        }
    })
}


//返回按钮对应操作
function goBack() {
    content_mgr.getContentApp("/" + article.typeCode + "/index?indicatorId=" + article.columnId + "&pageIndex=" + config.pageIndex).done(function () {
        setTimeout(function () {
            $('#hidebtn2').click();
        }, 500)
    });
}

//打开新增页面
function goAdd() {
    content_mgr.getContentApp("/articleNews/articleNewsEdit?pageIndex=" + config.pageIndex + "&typeCode=articleNews")
}

//移除缩略图
$("#delImg").click(function () {
    $("#imageLink").attr("src", GLOBAL_CONTEXTPATH + '/assets/images/no.photo.jpg');
    article.imageLink = '';
    article.flag = true;
    $("#showImg").hide();
});

//缩略图路径
function changeImg(path) {
    if(path.indexOf('.') != -1){
        $("#imageLink").attr("src", GLOBAL_FILESERVERNAMEPATH  + path);
    }else{
        $("#imageLink").attr("src", GLOBAL_FILESERVERPATH  + path);
    }
    article.imageLink = path;
}

function getClassifyModel(id) {
    Ls.ajaxGet({
        url: "/msg/submit/classify/getEOs"
    }).done(function (response) {
        var str = '<option value=""> 请选择类别 </option> ';
        if (response != null) {
            var data = response;
            var length = data.length;
            if (length > 0) {
                for (i = 0; i < length; i++) {
                    str += ' <option value=' + data[i].id + '>' + data[i].name + '</option> '
                }
            }
        }
        $("#synMsgCatIds").empty().append(str).val(id);
    });
};

//置顶点击事件
$("#isTop").click(function () {
    if ($("#isTop").is(':checked')) {
        $("#TopDate").removeClass("hide");
    } else {
        $("#TopDate").addClass("hide");
    }

});

//跳转点击事件
$("#isLink").click(function () {
    if ($("#isLink").is(':checked')) {
        $("#OutLink").removeClass("hide");
        $("#edit_content").addClass("hide");
    } else {
        $("#OutLink").addClass("hide");
        $("#edit_content").removeClass("hide");
    }
});

//定时发送事件
$("#isJob").click(function () {
    if ($("#isJob").is(':checked')) {
        $("#JobDate").removeClass("hide");
    } else {
        $("#JobDate").addClass("hide");
    }

})

