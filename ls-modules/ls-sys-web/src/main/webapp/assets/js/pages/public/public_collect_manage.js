// 任务管理
var publicCollectManage = function () {

    var init = function () {
        // 初始化布局
        mini.parse();
        // 获取datagrid
        cur.grid = mini.get("public_collect_datagrid");
        cur.grid.load({
            siteId: GLOBAL_SITEID
        });
        // 重置 datagrid 高度
        Ls.mini_datagrid_height(cur.grid);
    }
    return {
        init: init
    }
}();

function renderOp(e) {
    var str = "";
    var rec = e.record;
    str += '<button class="btn btn-default btn-sm btn-edit" onclick="runNow(' + rec.id + ')">立即执行</button> ';
    str += '<button class="btn btn-default btn-sm btn-edit" onclick="edit(' + rec.id + ')">修改</button> ';
    str += '<button class="btn btn-default btn-sm btn-delete" onclick="deleteData(' + rec.id + ')">删除</button>';
    return str;
}

function runNow(id) {
    Ls.ajax({
        url: "/public/collect/runNow?id=" + id,
        data: $("#public_collect_form").serializeObject()
    }).done(function (text) {
        if (text.status == 1) {
            Ls.tipsOk("执行成功！");
        } else {
            Ls.tipsErr(text.desc);
        }
    });
}

function edit(id) {
    cur.id = id;
    Ls.openWin('/public/collect/edit', '460px', '350px', {
        id: 'public_collect_button_saveOrUpdate',
        title: '修改任务',
        padding: 0,
    });
}

function add() {
    cur.id = "";
    Ls.openWin('/public/collect/edit', '460px', '350px', {
        id: 'public_collect_button_saveOrUpdate',
        title: '新增任务',
        padding: 0,
    });
}

function deleteData(id) {
    Ls.ajax({
        url: "/public/collect/delete?id=" + id
    }).done(function (text) {
        if (text.status == 1) {
            Ls.tipsOk("删除成功！");
            cur.grid.reload();
        } else {
            Ls.tipsErr(text.desc);
        }
    });
}