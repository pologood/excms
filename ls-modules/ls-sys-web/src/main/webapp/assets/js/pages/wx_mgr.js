var wx_mgr = function () {

    var vm = {};

    //扫描的controller
    var ctrl = "wxCtrl";

    var listData = {
        list: [{
            id: 1,
            title: '<strong>标题一</strong>',
            note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
            replyDate: '2016-04-05 16:44:00'
        }, {
            id: 2,
            title: '标题一',
            note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
            replyDate: '2016-04-05 16:44:00'
        }, {
            id: 3,
            title: '标题一',
            note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
            replyDate: '2016-04-05 16:44:00'
        }, {
            id: 4,
            title: '标题一',
            note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
            replyDate: '2016-04-05 16:44:00'
        }, {
            id: 5,
            title: '标题一',
            note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
            replyDate: '2016-04-05 16:44:00'
        }, {
            id: 6,
            title: '标题一',
            note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
            replyDate: '2016-04-05 16:44:00'
        }],
        page: function (e) {
            vm.list.push({
                id: 7,
                title: '标题一',
                note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
                replyDate: '2016-04-05 16:44:00'
            }, {
                id: 8,
                title: '标题一',
                note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
                replyDate: '2016-04-05 16:44:00'
            }, {
                id: 9,
                title: '标题一',
                note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
                replyDate: '2016-04-05 16:44:00'
            }, {
                id: 10,
                title: '标题一',
                note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
                replyDate: '2016-04-05 16:44:00'
            }, {
                id: 11,
                title: '标题一',
                note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
                replyDate: '2016-04-05 16:44:00'
            }, {
                id: 12,
                title: '标题一',
                note: '由各种物质组成的巨型球状天体，叫做星球。星球有一定的形状，有自己的运行轨道。',
                replyDate: '2016-04-05 16:44:00'
            })
        }
    }

    var postData = {
        title: "",
        title_warn: false,
        doSub: function (e) {
            if (vm.title == '') {
                vm.title_warn = true;
            } else {
                vm.title_warn = false;
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
            type: "POST",
            dataType: "JSON",
            url: "/wx/getPage",
            data: {
                pageIndex: 0,
                pageSize: 100
                //dataFlag: 1,
            }
        }).done(function (d) {
            vm.list = d.data;
        }).done(function () {
            //initAvalon()
        });

    };

    var list = function () {

        //初始化列表
        initAvalon(listData);

    }

    var postForm = function () {
        initAvalon(postData);
    }

    var article = function () {

    }

    return {
        list: list,
        postForm: postForm,
        article: article
    }
}();