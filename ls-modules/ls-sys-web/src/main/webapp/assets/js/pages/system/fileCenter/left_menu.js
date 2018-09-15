var file_mgr = function () {

    //加载内容
    var getContentApp = function (url, options) {
        return App.getContentAjax(url, options).done(function (res) {
            $("#content_body").html(res)
        });
    }


    var init = function () {
        //初始化布局
        mini.parse();
    }

    return {
        init: init,
        getContentApp: getContentApp
    }

}();
