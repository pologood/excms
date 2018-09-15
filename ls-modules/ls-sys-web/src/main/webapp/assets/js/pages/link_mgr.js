// 链接管理
var link_mgr = function () {

    var saveOrUpdate = function (id) {
        // 保存id
        link_mgr.id = id;
        // 编辑页面要根据内容模型的区别来判断添加的类型
        Ls.openWin('/linksMgr/toLinkEdit?indicatorId=' + content_mgr.indicatorId, '460px', '330px', {
            id: 'link_button_saveOrUpdate',
            title: !id ? "新增链接" : "编辑链接",
            ok: function () {
                var iframe = this.iframe.contentWindow;
                if (!iframe.document.body) {
                    return false;
                }
                iframe.$("#" + iframe.cur.id).submit();
                return false;
            }
        });
    }

    var init = function () {
        // 初始化布局
        mini.parse();
        // 获取datagrid
        link_mgr.grid = mini.get("link_datagrid");
        // 重置 datagrid 高度
        Ls.mini_datagrid_height(link_mgr.grid);

        // 加载grid数据
        link_mgr.grid.load({
            columnId: content_mgr.node.indicatorId
        });

        $("#publishs:not(.disabled)").off().on('click', function () {
            publish(null, 0);
        });

        $("#unpublishs:not(.disabled)").off().on('click', function () {
            publish(null, 1);
        });


        $("#deletes:not(.disabled)").off().on('click', function () {
            deleteData();
        });
    }
    return {
        init: init,
        saveOrUpdate: saveOrUpdate
    }
}();

// 鼠标移动上面出现提示框
var tooltip = function () {
    var id = 'tt';
    var top = 3;
    var left = 3;
    var maxw = 550;
    var speed = 10;
    var timer = 20;
    var endalpha = 95;
    var alpha = 0;
    var tt, t, c, b, h;
    var ie = document.all ? true : false;
    return {
        show: function (v, w) {
            if (tt == null) {
                tt = document.createElement('div');
                tt.setAttribute('id', id);
                t = document.createElement('div');
                t.setAttribute('id', id + 'top');
                c = document.createElement('div');
                c.setAttribute('id', id + 'cont');
                b = document.createElement('div');
                b.setAttribute('id', id + 'bot');
                tt.appendChild(t);
                tt.appendChild(c);
                tt.appendChild(b);
                document.body.appendChild(tt);
                tt.style.opacity = 0;
                tt.style.filter = 'alpha(opacity=0)';
                document.onmousemove = this.pos;
            }
            tt.style.display = 'block';
            c.innerHTML = v;
            tt.style.position = "absolute";
            tt.style.zIndex = 99999;
            tt.style.width = w ? w + 'px' : 'auto';
            if (!w && ie) {
                t.style.display = 'none';
                b.style.display = 'none';
                tt.style.width = tt.offsetWidth;
                t.style.display = 'block';
                b.style.display = 'block';
            }
            if (tt.offsetWidth > maxw) {
                tt.style.width = maxw + 'px'
            }
            h = parseInt(tt.offsetHeight) + top;
            clearInterval(tt.timer);
            tt.timer = setInterval(function () {
                tooltip.fade(1)
            }, timer);
        },
        pos: function (e) {
            var u = ie ? event.clientY + document.documentElement.scrollTop : e.pageY;
            var l = ie ? event.clientX + document.documentElement.scrollLeft : e.pageX;
            tt.style.top = (u - h) + 'px';
            tt.style.left = (l + left) + 'px';
        },
        fade: function (d) {
            var a = alpha;
            if ((a != endalpha && d == 1) || (a != 0 && d == -1)) {
                var i = speed;
                if (endalpha - a < speed && d == 1) {
                    i = endalpha - a;
                } else if (alpha < speed && d == -1) {
                    i = a;
                }
                alpha = a + (i * d);
                tt.style.opacity = alpha * .01;
                tt.style.filter = 'alpha(opacity=' + alpha + ')';
            } else {
                clearInterval(tt.timer);
                if (d == -1) {
                    tt.style.display = 'none'
                }
            }
        },
        hide: function () {
            clearInterval(tt.timer);
            tt.timer = setInterval(function () {
                tooltip.fade(-1)
            }, timer);
        }
    };
}();


function logoOp(e) {
    var rec = e.record;
    var link = rec.imageLink;
    var name = rec.attachRealName;
    if (!link) {
        return rec.title;
    }
    if (name && name != "" && name.toLowerCase().indexOf(".swf") > -1) {
        return '<a href="javascript:void(0)" onclick="playSwf(\'' + link + '\')"><img src="/assets/images/play.jpg" height="20" /></a>';
    }
    var path = /\//.test(link) ? link : ((link.indexOf(".") != -1 ? GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH) + "" + link);
    var pic = '<a href="' + path + '" target="_blank" onmouseover="tooltip.show(\'<img src=' + path + ' width=120 height=90>\')"'
    pic += ' onmouseout="tooltip.hide();"><img src="' + path + '" height="20" style="max-width: 100%"></a>';
    return pic;
}

function playSwf(mongdbId) {
    var path = (mongdbId.indexOf(".") != -1 ? GLOBAL_FILESERVERNAMEPATH : GLOBAL_FILESERVERPATH) + mongdbId;
    var content = '<object width="600" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">';
    content += '<param name="movie" value="' + path
        + '" /><param name="quality" value="high" /><param name="wmode" value="transparent" /><param name="menu" value="false"/>';
    content += '<embed width="600" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" quality="high" src="' + path;
    content += '" wmode="transparent"></embed></object>';
    Ls.openWin("", 600, 300, {
        id: 'play_swf',
        title: '预览',
        padding: 20,
        content: content
    });
}

//消息回调
indexMgr.publish = function (d) {
    link_mgr.grid.findRows(function (row) {
        if ($.inArray(row.id, d.contentIds) >= 0) return true;
    }).filter(function (v, i) {
        v.isPublish = d.isPublish;
        link_mgr.grid.updateRow(v);
    });
}

function linkOp(e) {
    var rec = e.record;
    var link = rec.imageLink;
    var name = rec.attachRealName;
    var str = "";
    if (name && name != "" && name.toLowerCase().indexOf(".swf") > -1) {
        if (!link) {
            return;
        }
        str = '<a href="javascript:void(0)" onclick="playSwf(\'' + link + '\')"><u>' + rec.title + '</u></a>';
        str += '<span id="publish_' + rec.contentId + '" class="publish-status">';
        var status = Ls.publishStatus(rec.isPublish);
        if (status != "") {
            str += '[生成中...]';
        }
        str += '</span>';
        return str;
    }

    var url = GLOBAL_SITEURI + '/' + rec.redirectLink;
    if (rec.redirectLink.indexOf("http") >= 0) {
        url = rec.redirectLink;
    }

    str = '<a href="' + url + '" target="_blank"><u>' + rec.title + '</u></a>';
    str += '<span id="publish_' + rec.contentId + '" class="publish-status">';
    var status = Ls.publishStatus(rec.isPublish);
    if (status != "") {
        str += '[生成中...]';
    }
    str += '</span>';

    return str;
}

function renderOp(e) {
    // var str = "";
    // var rec = e.record;
    // str += '<button class="btn btn-default btn-sm btn-edit" onclick="edit(' + rec.id + ')">修 改</button> '
    // str += '<button class="btn btn-default btn-sm btn-delete" onclick="deleteData(' + rec.id + ')">删 除</button>'
    // return str;

    var rec = e.record, editStr = ''
    var disabledStr = Ls.publishStatus(rec.isPublish);
    var publishStr = '';
    if (rec.isPublish == '1') {
        publishStr = '       <li><a href="javascript:void(0)" onclick="publish(' + rec.id + ', 0 )">重新发布</a></li>';
    }
    var editStr =
        '<div class="btn-group" role="group">' +
        '   <button ' + disabledStr + ' type="button" class="btn btn-default btn-sm" onclick="edit(' + rec.id + ')">修 改</button> ' +
        '   <button ' + disabledStr + ' type="button" class="btn btn-default btn-sm dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">' +
        '       <i class="fa fa-angle-down"></i>' +
        '   </button>' +
        '   <ul class="dropdown-menu pull-right">' + publishStr +
        '       <li><a href="javascript:void(0)" onclick="deleteData(' + rec.id + ')">删 除</a></li>' +
        '   </ul>' +
        '</div>';


    return editStr;
}

function IsPublish(e) {
    // var rec = e.record, isPublish = rec.isPublish, str = "";
    // if (isPublish == '1') {
    //
    //     str = '<a onclick="publish(' + rec.id + ',' + rec.isPublish + ')"><img src="' + GLOBAL_CONTEXTPATH + '/assets/images/click.gif" border="0" title="取消发布" ></a>';
    // } else {
    //     str = '<a onclick="publish(' + rec.id + ',' + rec.isPublish + ')"><img src="' + GLOBAL_CONTEXTPATH + '/assets/images/noclick.gif" border="0" title="发布"></a>';
    // }
    //
    // return str
    var rec = e.record, isPublish = rec.isPublish, str = "";
    var disabledStr = Ls.publishStatus(isPublish);
    if (isPublish == '1') {
        //str = '<a onclick="issuedClick(' + rec.contentId + ',0)"><img src="' + GLOBAL_CONTEXTPATH + 'assets/images/click.gif" border="0" title="取消发布" ></a>';
        str = '<button ' + disabledStr + ' type="button" class="btn btn-sm btn-default btn-ok" onclick="publish(' + rec.id + ',' + rec.isPublish + ')" title="取消发布"><span class="glyphicon glyphicon-ok"></span></button>';
    } else {
        //str = '<a onclick="issuedClick(' + rec.contentId + ',1)"><img src="' + GLOBAL_CONTEXTPATH + 'assets/images/noclick.gif" border="0" title="发布"></a>';
        str = '<button ' + disabledStr + ' type="button" class="btn btn-sm btn-default btn-remove" onclick="publish(' + rec.id + ',' + rec.isPublish + ')" title="发布"><span class="glyphicon glyphicon-ok"></span></button>';

    }

    return str;
}

function publish(id, status) {
    var title = "发布";
    var _status = 1;
    if (status == 1) {
        title = "取消发布";
        _status = 0;
    }
    var ids = [];
    if (id) {
        ids.push(id);
    } else {
        var rows = link_mgr.grid.getSelecteds();
        if (rows.length == 0) {
            Ls.tips("至少选择一项", {
                icons: "info"
            });
            return;
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            ids[i] = rows[i].id;
        }
    }
    if (confirm('确定' + title + '吗？')) {
        Ls.ajaxGet({
            url: "/articleNews/publishs",
            data: {
                ids: ids,
                siteId: GLOBAL_SITEID,
                columnId: content_mgr.node.indicatorId,
                type: _status
            }
        }).done(function (d) {
            if (id) {
                Ls.tips('正在生成处理中', {
                    icons: "success"
                });
            } else {
                Ls.tips('批量' + title + '完成，正在生成处理中', {
                    icons: "success"
                });
            }
            link_mgr.grid.reload();
        });
    }
}

function add() {
    link_mgr.saveOrUpdate(null);
}

function edit(id) {
    link_mgr.saveOrUpdate(id);
}

function deleteData(id) {
    var ids = [];
    var msg = "删除完成，正在生成处理中";
    if (id) {
        ids.push(id);
    } else {
        msg = "批量删除完成，正在生成处理中";
        var rows = link_mgr.grid.getSelecteds();
        if (rows.length == 0) {
            Ls.tips("至少选择一项", {
                icons: "info"
            });
            return;
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            ids[i] = rows[i].id;
        }
    }
    if (confirm('真的要删除吗？')) {
        Ls.ajaxGet({
            url: "/content/delete",
            data: {
                ids: ids
            }
        }).done(function (d) {
            Ls.tips(msg, {
                icons: "success"
            });
            link_mgr.grid.reload();
        });
    }
}

$(document).ready(function () {
    link_mgr.init();
    link_mgr.grid.on("beforeselect", function (e) {
        var disabledStr = Ls.publishStatus(e.record.isPublish);
        if (disabledStr) {
            e.cancel = true
        }
    });
});