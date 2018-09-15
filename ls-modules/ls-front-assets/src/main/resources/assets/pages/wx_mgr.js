var wx_mgr = function () {

    var vm = {};

    var pageIndex = 0;

    //扫描的controller
    var ctrl = "wxCtrl";

    var listData = {
        list: [],
        page: function (e) {
            getList();
        }
    }

    var postData = {
        uid: "",
        siteId: "",
        columnId: "",
        title: "",
        receiveId: "",
        classCode: "",
        guestBookContent: "",
        title_warn: false,
        doSub: function (e) {

            vm.siteId = cur.siteId;
            vm.columnId = cur.columnId;

            if (vm.siteId == "" || vm.columnId == "") {
                alert('缺少参数！');
                return false;
            }

            if (vm.title == '') {
                vm.title_warn = true;
            } else {
                vm.title_warn = false;

                try {
                    Android.getJSONFromServiceUrls(JSON.stringify(vm.$model), "/mobile/saveGuestBook");
                } catch (e) {
                    alert("出错提示：" + e)
                }

            }
        }
    }

    function initAvalon(data) {
        vm = avalon.vmodels[ctrl];

        data.$id = ctrl;

        if (!vm) {
            vm = avalon.define(data);
        } else {
            !wx_mgr.avalon && Ls.assignVM(vm, data);
        }
        wx_mgr.avalon = true;
        avalon.scan($("#" + ctrl).get(0), vm);
    }

    //拉取数据
    function getList() {
        return $.ajax({
            url: "/webAuth/interactList",
            dataType: "json",
            data: {
                pageIndex: pageIndex,
                pageSize: 5,
                dataFlag: 1,
                siteId: siteId,
                title: title,
                st: st,
                ed: ed
            }
        }).done(function (d) {
            for (var i = 0; i < d.data.length; i++) {
                vm.list.push(d.data[i]);
            }
            pageIndex = pageIndex + 1;
            if (pageIndex >= d.pageCount) {
                $("#getmore").hide();
            } else {
                $("#getmore").show();
            }
        }).done(function () {
            //initAvalon()
        });
    };

    var list = function () {
        getList();
        //初始化列表
        initAvalon(listData);

    }

    var postForm = function () {
        initAvalon(postData);
    }

    return {
        list: list,
        postForm: postForm
    }
}();