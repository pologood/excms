var cur = {
    vm: "",
    editor: "",
    opt: []
};

var eweb;
var pic_news = function () {

    //拉取数据
    function getData(pageIndex, columnId, key, condition, status) {
        var pageSize = cur.pageSize;
        return Ls.ajaxGet({
            url: "/content/getPage",
            data: {
                pageIndex: pageIndex,
                pageSize: pageSize,
                dataFlag: 1,
                columnId: columnId,
                title: key,
                condition: condition,
                status: status
            }
        }).done(function (d) {
            var listHtml = doc_list_template(d);
            $("#doc_list_body").html(listHtml);
        }).done(function (d) {
            if (d.total > 0) {
                pagination(d.pageCount, d.pageIndex);
            }
        })
    };

    var init = function () {

        cur.columnId = content_mgr.indicatorId;
        if(content_mgr.node.opt!=null){
            cur.opt = content_mgr.node.opt.split("\|");
        }
        var synColumnIds = content_mgr.node.synColumnIds;
        if (synColumnIds != null) {
            cur.synColumnIds = synColumnIds.split(",");
        }

        eweb = Ls.editor("content", {cusdir: "/" + GLOBAL_SITEID + "/" + cur.columnId});

        $("#mycolor").colorpicker({
            color: "#000",
            hideButton: true,
            strings: " , , 无颜色 , , , , "
        }).on('change.color', function(evt, color){
            $('#title').css('color', color);
            pic.titleColor = color;
        });
        $("#colorpicker").on("click", function (evt) {
            evt.stopImmediatePropagation();
            $("#mycolor").colorpicker("showPalette");
        });
        /*editor = KindEditor.create('textarea[name="content"]', {
            uploadJson: '/articleNews/upload?siteId=' + GLOBAL_SITEID + "&columnId=" + cur.columnId,//上传文件
            fileManagerJson: '',//文件空间
            allowImageUpload: true,
            allowFileManager: false,
            allowFlashUpload: false,
            allowMediaUpload: false,
            filterMode: false,
            siteId: GLOBAL_SITEID,
            columnId: cur.columnId,
            fileServerPath: GLOBAL_FILESERVERNAMEPATH,
            afterCreate: function () {
                this.sync();
            },
            afterBlur: function () {
                this.sync();
            },
            resizeType: 1
        });

        //#*取色器*#
        var K = KindEditor;
        var colorpicker;
        K('#colorpicker').bind('click', function (e) {
            e.stopPropagation();
            if (colorpicker) {
                colorpicker.remove();
                colorpicker = null;
                return;
            }
            var colorpickerPos = K('#colorpicker').pos();
            colorpicker = K.colorpicker({
                x: colorpickerPos.x,
                y: colorpickerPos.y + K('#colorpicker').height(),
                z: 19811214,
                selectedColor: 'default',
                noColor: '无颜色',
                click: function (color) {
                    K('#title').css('color', color);
                    colorpicker.remove();
                    pic.titleColor = color;
                    colorpicker = null;
                }
            });
        });

        K(document).click(function () {
            if (colorpicker) {
                colorpicker.remove();
                colorpicker = null;
            }
        });*/
    }

    var getResource = function () {
        Ls.getResource('resMark', function (e, treeId, node) {
            $("#resources").val(node.key);
            $("#dropdowns2").hide();
        })
    }

    return {
        init: init,
        getResource: getResource
    }

}();