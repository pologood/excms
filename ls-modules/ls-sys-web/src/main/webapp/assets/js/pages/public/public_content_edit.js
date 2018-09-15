// 编辑
var eweb = null;
// 公开内容编辑
var publicContentEdit = function () {

    // 保存
    var save = function (callback) {
        var data = Ls.toJSON(cur.vm.$model);
        if(null != data.id && '' != data.id){
            if (data.videoStatus != cur.videoStatus) {
                data.videoStatus = cur.videoStatus;
            }
        }
        // 设置内容
        data.isPublish = cur.isPublish;
        Ls.ajax({
            url: "/public/content/saveOrUpdate",
            data: data
        }).done(function (text) {
            if (text.status == 1) {
                Ls.tipsInfo(cur.isPublish == 1 ? "保存并发布完成，正在生成处理中" : "保存成功！");
                if (callback) {
                    callback();
                }
            } else {
                Ls.tipsErr(text.desc);
            }
        });
    }

    function checkFirst(callback) {
        if (cur.isLink) {
            save(callback);
            return;
        }
        if (cur.contentModel.isSensitiveWord == 1 || cur.contentModel.isEasyWord == 1 || cur.contentModel.isHotWord == 1) {
            var types = getTypes();

            var content = getContent();
            Ls.ajax({
                data: {
                    content: content,
                    types: types
                },
                url: "/content/isCheck"
            }).done(function (text) {

                if (text.status == 1) {
                    if (text.data == 1) {
                        check_all();
                    } else {
                        save(callback);
                    }
                }
            });
        } else {
            save(callback);
        }
    }

    //
    function check_all() {
        Ls.openWin("/content/content_check", '600px', '460px', {
            id: 'check_all',
            title: '内容检测'
        });
    }

    // 初始化
    var init = function () {
        /*editor = KindEditor.create('textarea[name="content"]', {
            uploadJson: GLOBAL_CONTEXTPATH + '/articleNews/upload?siteId=' + GLOBAL_SITEID,// 上传文件
            fileManagerJson: '/fileCenter/getPage',// 文件空间
            allowImageUpload: true,
            allowFileManager: false,
            allowFlashUpload: false,
            allowMediaUpload: false,
            filterMode: false,
            siteId: GLOBAL_SITEID,
            columnId: 0,
            fileServerPath: GLOBAL_FILESERVERNAMEPATH,
            afterCreate: function () {
                this.sync();
            },
            afterBlur: function () {
                this.sync();
            },
            resizeType: 1
        });*/
        eweb = Ls.editor("public-info-content", {cusdir: "/" + GLOBAL_SITEID + "/" + 0});
        // 保存
        $("#savePublicContent").click(function () {
            eweb.update();
            cur.isPublish = 0;
            $("#" + cur.id).trigger("validate");
        });

        // 保存并发布
        $("#saveAndPublish").click(function () {
            eweb.update();
            cur.isPublish = 1;
            $("#" + cur.id).trigger("validate");
        });
    }

    function getContent() {

        var content = eweb.getHtml() + $("#title").val() + $("#keyWords").val() + $("#summarize").val();
        for (var i = 0, l = ignoreArray.length; i < l; i++) {
            if (content.indexOf(ignoreArray[i]) > -1) {
                content = content.replaceAll(ignoreArray[i], '');
            }
        }

        return content;
    }

    var getResource = function () {
        Ls.getResource('resMark', function (e, treeId, node) {
            $("#resources").val(node.key);
            $("#dropdowns2").hide();
        })
    }

    return {
        getContent: getContent,
        init: init,
        save: checkFirst,
        getResource: getResource
    }
}();