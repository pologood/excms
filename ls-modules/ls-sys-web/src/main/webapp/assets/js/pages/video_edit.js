function getModelConfig() {
    Ls.ajaxGet({
        data: {
            columnId: content_mgr.indicatorId
        },
        url: "/content/getModelConfig",
        success: function (text) {
            if (text.status == 1) {
                curr.model = text.data;
                if (curr.model.isSensitiveWord == 1 && curr.model.isEasyWord == 1 && curr.model.isHotWord == 1) {
                    $("#checkWords").removeClass("video_attr_ext");
                }
                if (curr.model.isSensitiveWord == 1) {
                    $("#senWords").remove();
                }
                if (curr.model.isEasyWord == 1) {
                    $("#errorWords").remove();
                }
                if (curr.model.isHotWord == 1) {
                    $("#hotWords").remove();
                }
                if (curr.model.isComment == 1) {
                    $("#isComment").removeClass("hide");
                }
            } else {
                Ls.tipsErr("内容模型获取错误");
            }
        }
    });
}

function getVideoFromData(){
    if (curr.vm.titleColor == "" || curr.vm.titleColor == null) {
        curr.vm.titleColor = '#000000';
    }
    if ($("#isTop").is(':checked')) {
        curr.vm.isTop = 1;
    } else {
        curr.vm.isTop = 0;
    }
    if ($("#isNew").is(':checked')) {
        curr.vm.isNew = 1;
    } else {
        curr.vm.isNew = 0;
    }
    if (curr.vm.videoPath == '' || curr.vm.videoPath == null) {
        curr.vm.videoPath = curr.vm.videoName;
    }
    if (flag == 1) {
        curr.vm.isPublish = 1;
        flag = 0;
    } else {
        curr.vm.isPublish = 0;
    }
    /*curr.vm.videoText = editor.text();
    curr.vm.article = editor.html();*/
    curr.vm.videoText = eweb.getText();
    curr.vm.article = eweb.getHtml();
    curr.vm.columnId = content_mgr.node.indicatorId;
    curr.vm.siteId = GLOBAL_SITEID;
    curr.vm.remarks = $("#remarks").val();
    curr.vm.typeCode = 'videoNews';
    if ($("#isJob").is(':checked')) {
        curr.vm.isJob = 1;
        curr.vm.jobIssueDate = $("#jobIssueDate").val();
    } else {
        curr.vm.isJob = 0;
        curr.vm.jobIssueDate = null;
    }
    return Ls.toJSON(curr.vm.$model);
}