var article_mgr = function () {

    var eweb;

    function initData() {
        return Ls.initFORM("article_form", {
            id: article_mgr.id,
            columnId: content_mgr.node.indicatorId,
            siteId: GLOBAL_SITEID,
            pageIndex: article_mgr.pageIndex,
            author: GLOBAL_PERSON.name,
            resources: GLOBAL_PERSON.organName,
            typeCode: "articleNews",
            imageLink: GLOBAL_CONTEXTPATH + "/assets/images/no.photo.jpg",
            isTop: 0,
            isNew: 0,
            isTitle: 0,
            isAllowComments: 0,
            topDs: 0,
            newDs: 0,
            titleDs: 0,
            commentDs: 0,
            isBold: 0,
            isUnderline: 0,
            isTilt: 0,
            titleBold: "",
            titleUnderline: "",
            titleTilt: "",
            titleColor: "",
            isExtend: 0,
            extend: function () {
                article_mgr.vm.isExtend = !article_mgr.vm.isExtend;
            },
            bold: function () {
                article_mgr.vm.titleBold = (article_mgr.vm.isBold = article_mgr.vm.isBold == 1 ? 0 : 1) == 1 ? "bold" : "";
            },
            tilt: function () {
                article_mgr.vm.titleTilt = (article_mgr.vm.isTilt = article_mgr.vm.isTilt == 1 ? 0 : 1) == 1 ? "italic" : "";
            },
            underline: function () {
                article_mgr.vm.titleUnderline = (article_mgr.vm.isUnderline = article_mgr.vm.isUnderline == 1 ? 0 : 1) == 1 ? "underline" : "";
            },
            errorWords: function () {
                //EASYERR
            },
            senWords: function () {
                //SENSITIVE
            },
            hotWords: function () {
                //HOT
            },
            save: function () {
                var data = Ls.toJSON(article_mgr.vm);
            },
            savePublish: function () {

            },
            goBack: function () {
                content_mgr.getContentApp("/" + article_mgr.typeCode + "/index?indicatorId=" + article_mgr.columnId + "&pageIndex=" + article_mgr.pageIndex);
            },
            delImg: function () {
                article_mgr.imageLink = GLOBAL_CONTEXTPATH + '/assets/images/no.photo.jpg';
            },
            uploadImg: function () {

                meiTuUpload();

                /*if (config.model.isEnableBeauty == 1) {
                 meiTuUpload();
                 } else {
                 normalUpload();
                 }*/
            }
        });
        //
    }

    //美图上传缩略图
    function meiTuUpload() {
        art.dialog.data("imgPath", article_mgr.imageLink);
        art.dialog.data("siteId", article_mgr.siteId);
        art.dialog.data("columnId", article_mgr.columnId);
        art.dialog.data("contentId", article_mgr.id);
        art.dialog.data("width", 196);
        art.dialog.data("heigth", 151);
        //art.dialog.data("width", config.model.picWidth);
        //art.dialog.data("heigth", config.model.picHeight);
        Ls.openWin('/articleNews/thumbUpload', '650px', '410px', {
            lock: true,
            title: '缩略图'
        });
    }

    //普通上传缩略图
    function normalUpload() {
        Ls.uploadFile(function (filePath) {
            article_mgr.imageLink = filePath[0].fileName;
            return true;
        }, {
            upload_url: '/content/normalUploadThumb',
            file_upload_limit: 1,
            post_params: {
                "siteId": article_mgr.siteId,
                "columnId": article_mgr.columnId,
                "contentId": article_mgr.id,
                "imgPath": article_mgr.imageLink,
                "desc": "上传缩略图"
            }
        });
    }

    //绑定模型
    function bindAvalon(data) {
        var data = $.extend(true, initData(), data);
        article_mgr.vm = avalon.vmodels[data.$id];
        if (!article_mgr.vm) {
            article_mgr.vm = avalon.define(data);
        } else {
            Ls.assignVM(article_mgr.vm, data);
        }
        avalon.scan(data.id, article_mgr.vm);

        //初始化组件
        initComponent();

    }

    //初始化编辑器
    function initEditor() {
        eweb = Ls.editor("article_content", {cusdir: "/" + GLOBAL_SITEID + "/" + article_mgr.columnId});
        /*editor = KindEditor.create('#article_content', {
            uploadJson: GLOBAL_CONTEXTPATH + '/articleNews/upload?siteId=' + GLOBAL_SITEID + "&columnId=" + article_mgr.columnId,//上传文件
            fileManagerJson: '/fileCenter/getPage',//文件空间
            allowImageUpload: true,
            allowFileManager: false,
            allowFlashUpload: false,
            allowMediaUpload: false,
            filterMode: false,
            siteId: GLOBAL_SITEID,
            columnId: article_mgr.columnId,
            fileServerPath: GLOBAL_FILESERVERNAMEPATH,
            afterCreate: function () {
                this.sync();
            },
            afterBlur: function () {
                this.sync();
            },
            resizeType: 1
        });*/
    }

    //初始化拾色器
    function initSelectColor() {

        /*取色器*/
        $("#mycolor").colorpicker({
            color: "#000",
            hideButton: true,
            strings: " , , 无颜色 , , , , "
        }).on('change.color', function(evt, color){
            $('#title').css('color', color);
        });
        $("#colorpicker").on("click", function (evt) {
            evt.stopImmediatePropagation();
            $("#mycolor").colorpicker("showPalette");
        });

    }

    //绑定表单验证
    function initValidator() {
        $('#' + article_mgr.$id).validator({
            fields: {
                'title': '标题:required;',
                'publishDate': '发布日期:required;'
            },
            valid: function () {
                eweb.update();
                addPost()
            }
        })
    }

    //初始化所有组件
    function initComponent() {
        initEditor();
        initSelectColor();
        initValidator();
    }

    //获取数据
    function getData() {
        Ls.ajaxGet({
            data: {
                id: article_mgr.id
            },
            url: "/articleNews/getArticleContent",
            success: function (d) {

                var data = d.data.article;
                data.content = d.data.content;
                data.titleBold = data.isBold == 1 ? "bold" : "";
                data.titleTilt = data.isTilt == 1 ? "italic" : "";
                data.titleUnderline = data.isUnderline == 1 ? "underline" : "";

                bindAvalon(data);

            }
        })
    }

    function init(cur) {
        //附加对象数据
        $.extend(true, article_mgr, initData());
        article_mgr.pageIndex = cur.pageIndex;
        article_mgr.id = cur.id;
        //绑定模型
        !cur.id ? bindAvalon(article_mgr) : getData();
    }

    return {
        init: init
    }

}();