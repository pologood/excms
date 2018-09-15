/*
 * version v1.0.1
 * Date: 2016-1-6
 * author:doocal
 */
var indexMgr = function () {

    var isHash = false;

    // 刷新所有表单hash值
    var setHashToMenu = function () {
        var menus = $('.page-sidebar a.ajaxify');
        menus.each(function (index, item) {
            var _link = $(item).data('id');
            if (_link != undefined && _link != "") {
                var encodedUrl = Ls.base64.encode(_link);
                $(item).attr('hash', encodedUrl);
            }
        });
    }

    // 站点切换
    function flushSite(id, name, uri, type) {
        var me = this, _dfd = $.Deferred();
        $(".top-more").find('ul').empty();
        if (id != GLOBAL_SITEID) {
            GLOBAL_SITEID = id;
            GLOBAL_SITENAME = name;
            GLOBAL_SITEURI = uri;
            GLOBAL_HTMLFILEPATH = uri;
            GLOBAL_SITETYPE = type;
            // 保存站点id
            return Ls.ajaxGet({
                url: "/saveSiteId?siteId=" + id
            }).done(function () {
                var site_menu = $("#site_menu");
                site_menu.attr("title", "站点ID：" + id).find("span").text("当前站点：" + name);
            }).done(function () {
                Ls.injectTplVal();
                getSysMenu();
            });
        } else {
            _dfd.resolve(me);
            return _dfd.promise();
        }
    }

    // 刷新菜单
    function refreshMenu() {
        if (window.location.hash) {
            if (window.location.hash != "#") {
                var sidebar = $(".page-sidebar a[hash='" + window.location.hash.substring(1) + "']");
                var sidebarMenu = sidebar.closest(".menu-item");
                var menuId = sidebarMenu.data("pid");

                if (GLOBAL_RIGHTS == "root") {
                    sidebar.click();
                } else {
                    // 选种顶部对应的主菜单
                    $("#top_menu_" + menuId).addClass("active").siblings().removeClass("active");
                    // 显示同级菜单
                    $(".menu_" + menuId).show();
                    // 选种当前菜单
                    sidebar.click();
                }
            }
        } else {
            if (GLOBAL_RIGHTS == "root") {
                $('.page-sidebar-menu').find('li > a.ajaxify:first').click();
            } else {
                // 默认刷新第一个菜单
                $('.hor-menu-top a:first').click();
            }
        }
    }

    // 消息模板
    var msg_list_tpl = Ls.compile(
        '<? if(data && Ls.isArray(data)){?>' +
        '   <? for(var i=0,l=data.length; i<l; i++){ ?>' +
        '   <? var el = data[i].messageSystemEO ?>' +
        '   <li>' +
        '       <a href="javascript:;" data-msgId="<?=data[i].id?>" data-infoId="<?=el.resourceId?>" data-code="<?=el.modeCode?>" data-link="<?=el.link?>">' +
        '           <span class="time"><?=data[i].dateDiff?></span>' +
        '           <span class="details">' +
        '           <span class="label label-sm label-icon label-<?=el.messageStatus?>"></span>' +
        '               <?=el.content?>' +
        '           </span>' +
        '       </a>' +
        '   </li>' +
        '   <? } ?>' +
        '<?} else {?>' +
        '   <? var el = messageSystemEO ?>' +
        '   <li>' +
        '       <a href="javascript:void(0);" data-msgId="<?=id?>" data-infoId="<?=el.resourceId?>" data-code="<?=el.modeCode?>" data-link="<?=el.link?>">' +
        '           <span class="time"><?=dateDiff?></span>' + '           <span class="details">' +
        '           <span class="label label-sm label-icon label-<?=el.messageStatus?>"></span>' +
        '               <?=el.content?>' +
        '           </span>' +
        '       </a>' +
        '   </li>' +
        '<? } ?>'
    );

    // 消息处理主核心方法
    var moduleMsg = function () {
        return {
            init: function (json) {
                var _this = this, eo = json.messageSystemEO;
                // mid, id, code, url
                _this.id = json.id;
                _this.modeCode = json.modeCode;
                _this.link = eo.link;
                _this.resourceId = eo.resourceId;
                _this.popup = eo.data ? 1 : 0;
                _this.data = json;
                return _this;
            },
            readMsg: function () {
                var _this = this;
                return Ls.ajaxGet({
                    url: "/message/readMessage",
                    data: {
                        ids: _this.id
                    }
                }).done(function () {
                    getMsgList();
                })
            },
            after: function () {
                var _this = this;
                try {
                    _this[_this.modeCode + "Status"]();
                } catch (e) {
                }
                return _this;
            },
            /* msgCount: function (type) {
                 var badge = $(".badge-notification:first"), badgeNum = parseInt(badge.text());
                 if (!Ls.isNumber(badgeNum)) {
                     badgeNum = 0;
                 }
                 $(".badge-notification").html(type ? parseInt(badgeNum) + 1 : parseInt(badgeNum) - 1);
                 return this;
             },*/

            msgCount: function (type) {
                var badge = $(".unReadInfo:first"), badgeNum = parseInt(badge.text());
                if (!Ls.isNumber(badgeNum)) {
                    badgeNum = 0;
                }
                $(".unReadInfo").html(type ? parseInt(badgeNum) + 1 : parseInt(badgeNum) - 1);
                return this;
            },
            createMsgLi: function () {
                var _this = this;
                var li = $(".dropdown-menu-list>li:first");
                _this.data.messageSystemEO.content = _this.data.messageSystemEO.content.replace(/<[^>]+>/g,"");
                if (li.length == 0) {
                    $(".dropdown-menu-list").append(msg_list_tpl(_this.data));
                } else {
                    $(".dropdown-menu-list>li:first").before(msg_list_tpl(_this.data));
                }
                return _this;
            },
            videoNews: function () {
                var _this = this;
                _this.readMsg().done(function () {
                    Ls.openWin(GLOBAL_CONTEXTPATH + _this.link, '600px', '400px', {
                        id: 'videoPlayer',
                        title: '播放视频',
                        padding: 2
                    });
                });
                return this;
            },
            videoNewsStatus: function () {
                var _this = this;
                $("#video_status_" + _this.resourceId).remove();
                return this;
            },
            weiboStatus: function () {
                try {
                    msg_weibo();
                } catch (e) {
                }
                return this;
            },
            dbCollectStatus: function () {
                try {
                    msg_db_collect();
                } catch (e) {
                }
                return this;
            },
            wordsStatus: function () {
                try {
                    indexMgr.winName.msg_words();
                } catch (e) {
                }
                return this;
            },
            toastr: function () {
                var _this = this;
                var len = $("#toast-container .toast").length;
                if(len < 4) { //最多显示4条消息提醒
                    toastr.show(_this.data);
                }
                _this.after();
                return _this;
            }
        }
    }();

    //获取发布中消息列表
    function getPublishProblemCount() {
        Ls.ajaxGet({
            url: "/publishProblem/getPage/",
            data: {
                isPublish: 2
            }
        }).done(function (d) {
            $("#publish_problem").text(d.total);
        })
    }

    // 获取消息
    function getMsgList() {
        Ls.ajaxGet({
            url: "/message/getPage/",
            data: {
                pageSize: 8,
                messageStatus: 1
            }
        }).done(function (d) {
            /*$(".badge-notification").text(d.total);*/
            $(".unReadInfo").text(d.total);

            var html = msg_list_tpl(d);
            $(".dropdown-menu-list").html(html).off('.msg').on('click.msg', 'a', function () {
                var $this = $(this);

                var json = {
                    id: mid = $this.data("msgid"),
                    modeCode: $this.data("code"),
                    messageSystemEO: {
                        link: $this.data("link"),
                        resourceId: $this.data("infoid")
                    }
                };

                try {
                    moduleMsg.init(json)[json.modeCode]();
                } catch (e) {
                    moduleMsg.readMsg();
                }

            });

        })
    }

    // 获取系统菜单
    function getSysMenu() {
        return Ls.ajaxGet({
            url: "/system/menu/getMenu"
        }).done(function (data) {

            var sysMenu = {
                topMenu: data,
                leftMenu: []
            };

            // 等于root用户权限特殊判断
            if (GLOBAL_RIGHTS != "root") {
                $.each(data, function (i, v) {
                    if (v.children) {
                        $.each(v.children, function (i, v) {
                            sysMenu.leftMenu.push(v);
                        })
                    }
                });

                var topMenu = Ls.template("top_menu_tpl", sysMenu);

                $("#top_menu_el").html(topMenu);

                /*// 根据页面大小隐藏部分顶部导航
                var hor = $(".hor-menu-light"),
                    nav = $(".hor-menu-top"),
                    top = $(".top-menu"),
                    more = $(".top-more"),
                    logo = $(".page-logo"),
                    //winW = $(window).width(),
                    winW = $(".page-header-inner.container").width(),
                    logoW = logo.outerWidth(),
                    horW = hor.outerWidth(),
                    navW = nav.outerWidth(),
                    topW = top.outerWidth(),
                    moreW = more.outerWidth(),
                    lisW = nav.find('li').width(),
                    lisN = nav.find('li').size();

                if (logo.css('display') === 'none') {
                    currW = winW - (horW + topW + moreW);
                } else {
                    currW = winW - (logoW + horW + topW + moreW);
                }

                var num = lisN - 2 - Math.ceil(Math.abs(currW - navW) / lisW);
                more.hide();
                if (currW < navW) {
                    more.show();
                    more.find('.dropdown-menu').prepend($(".hor-menu-top li:gt(" + num + ")"));
                    more.find('span').first().text('更 多');
                }*/
            } else {
                sysMenu.leftMenu = data;
            }
            var leftMenu = Ls.template("left_menu_tpl", sysMenu);
            $("#sidebar_menu_el").html(leftMenu);

            // 设置hash
            setHashToMenu();

        }).done(function () {
            var hashCode = window.location.hash.substring(1);
            var sidebar = $(".page-sidebar a[hash='" + hashCode + "']");
            if (sidebar.length > 0) {
                var sidebar = $(".page-sidebar a[hash='" + hashCode + "']");
                var sidebarMenu = sidebar.closest(".menu-item");
                var menuId = sidebarMenu.data("pid");
                if (GLOBAL_RIGHTS == "root") {
                    sidebar.click();
                } else {
                    // 选种顶部对应的主菜单
                    $("#top_menu_" + menuId).addClass("active").siblings().removeClass("active");
                    // 显示同级菜单
                    $(".menu_" + menuId).show();
                    // 选种当前菜单
                    $(window).resize();
                    sidebar.click();
                }
            } else {
                if (GLOBAL_RIGHTS == "root") {
                    $('.page-sidebar-menu').find('li > a.ajaxify:first').click();
                } else {
                    // 默认刷新第一个菜单
                    $('.hor-menu-top a:first').click();
                }
            }

        });
    }


    function testMsg(count) {
        var testJson = {
            id: 6133476,
            messageId: 6133476,
            createDate: "2018-04-10 13:44:10",
            updateDate: "2018-04-10 13:44:10",
            dateDiff:"0秒前",
            messageStatus:1,
            messageSystemEO:
                {
                    content: "生成栏目[部门动态]栏目页成功.",
                    siteId: 2653861,
                    id: 6133475,
                    data:{
                        complete:1,
                        error: 0,
                        status: 3,
                        taskId: "6133474",
                        time: 409,
                        total:1
                    },
                    isPublish: 1,
                    messageStatus: "success",
                    messageType: 1,
                    recUserIds: "2661207",
                    title: "生成静态成功",
                    todb: false
                },
            messageType:1,
            recUserId:2661207
        }

        var errorJson = {
            id: 6133477,
            messageId: 6133477,
            createDate: "2018-04-10 13:44:10",
            updateDate: "2018-04-10 13:44:10",
            dateDiff:"0秒前",
            messageStatus:1,
            messageSystemEO:
                {
                    content: "文章[美好滁州]视频转换失败",
                    siteId: 2653861,
                    id: 6133474,
                    data:{
                        complete:1,
                        error: 0,
                        status: 3,
                        taskId: "6133473",
                        time: 409,
                        total:1
                    },
                    isPublish: 1,
                    messageStatus: "error",
                    messageType: 1,
                    recUserIds: "2661207",
                    title: "视频转换失败",
                    todb: false
                },
            messageType:1,
            recUserId:2661207
        }

        if(count % 2 == 0) {
            testJson = errorJson;
        }
        moduleMsg.init(testJson).toastr();
    }

    $(document).ready(function () {

        // 重新登录
        Ls.reload = function () {
            if (confirm('是否重新登录？')) {
                top.location.replace(Ls.getLocation.url + GLOBAL_CONTEXTPATH + "/logout");
            }
        }

        // 站点样式
        var dropLis = $(".classic-menu-dropdown>.dropdown-menu li");
        if (dropLis.size() > 20) {
            dropLis.parent().css('min-width', '780px');
            $.each(dropLis, function (i, v) {
                $(v).css({
                    float: 'left',
                    width: '33%'
                });
            })
        } else if (dropLis.size() > 10) {
            dropLis.parent().css('min-width', '520px');
            $.each(dropLis, function (i, v) {
                $(v).css({
                    float: 'left',
                    width: '50%'
                });
            })
        } else {
        }

        App.initSlimScroll(".classic-menu-dropdown>.dropdown-menu")

        /*// 隐藏部分顶部导航
        $(window).resize(function () {
            var hor = $(".hor-menu-light"),
                nav = $(".hor-menu-top"),
                top = $(".top-menu"),
                more = $(".top-more"),
                logo = $(".page-logo"),
                // winW = $(window).width(),
                winW = $(".page-header-inner.container").width(),
                logoW = logo.outerWidth(),
                horW = hor.outerWidth(),
                navW = nav.outerWidth(),
                topW = top.outerWidth(),
                moreW = more.outerWidth(),
                lisW = nav.find('li').width(),
                lisN = nav.find('li').size();

            if (logo.css('display') === 'none') {
                currW = winW - (horW + topW + moreW);
                currW2 = winW - (horW + topW + moreW + navW);
            } else {
                currW = winW - (logoW + horW + topW + moreW);
                currW2 = winW - (logoW + horW + topW + navW + moreW);
            }

            if (navW <= 0) {
                more.hide();
                return false;
            }else {
                //more.show();
            }

            var num = lisN - 2 - Math.ceil(Math.abs(currW - navW) / lisW);
            if (currW < navW) {
                more.show();
                more.find('.dropdown-menu').prepend($(".hor-menu-top li:gt(" + num + ")"));
                more.find('span').first().text('更 多');
            }
            if (currW2 >= 200) {
                more.hide();
                if (more.find('li').length > 0) {
                    nav.find('#top_menu_el').append(more.find('ul').html());
                    more.find('ul').empty()
                }
            }

        });

        $(".top-more").on('click', 'li', function (e) {
            var $this = $(this), $menuId = $this.data("id");
            $(".hor-menu-top li").removeClass("active");
            $this.parent().prev().find('span').text($this.text());
            $this.addClass("active").siblings().removeClass("active");
            $this.parent().prev().addClass("active");
            //隐藏左侧所有菜单
            $(".menu-item").hide();
            //找到当前菜单的子菜单
            var sidebar_menu = $(".menu_" + $menuId);
            //默认点击第一个菜单并显示
            sidebar_menu.first().find('.ajaxify:first').click();
            sidebar_menu.show();

        });*/


        // 获取系统菜单
        getSysMenu();

        // 拉取信息
        getMsgList();

        // var count = 0;
        // setInterval(function () {
        //     testMsg(count);
        //     count++;
        // },3000)


        //获取发布中条数
        getPublishProblemCount();

        // 绑定打开消息弹层
        // $(".dropdown-notification").on('click', ">a", function () {
        // isMsg && getMsgList();
        // })

        // 打开消息中心
        $("#message_show_all").on('click', function () {
            Ls.openWin('/message/index', {
                width: '1000px',
                height: '600px',
                lock: true,
                title: '消息中心'
            });
        });

        // 打开发布中列表
        $("#publish_problem_list").on('click', function () {
            Ls.openWin('/publishProblem/index', {
                id: "publish_problem",
                width: '1000px',
                height: '600px',
                lock: true,
                title: '发布中文章列表',
                close: function (args) {
                    getPublishProblemCount();
                }
            });
        });

        // 绑定退出按扭
        $("#logout").on('click', function () {
            Ls.ajaxGet({
                url: "/logout"
            }).done(function (d) {
                if (d.status == 1) {// 登出成功
                    window.location.replace(Ls.getLocation.url + GLOBAL_CONTEXTPATH + d.data);
                } else {
                    Ls.tips(d.desc, {
                        icons: "error"
                    });
                }
            });
        });

        /*// 全局搜索
        $("#header_search_bar").on('click', function () {
            $('#global_search_modal').modal({
                keyboard: true
                // backdrop: false
            }).on('shown.bs.modal', function () {
                $('#global_search_words').focus()
            })
        });

        // 禁用全局搜索回车事件
        $(document).on('keypress', '#global_search_form', function (e) {
            if (e.which == 13) {

                // 隐藏搜索
                $('#global_search_modal').modal('hide').on('hidden.bs.modal', function (e) {
                    globalSearch();
                })

                return false;
            }
        });

        // 绑定搜索按扭事件
        $("#global_search_submit").on('click', function (e) {

            // 隐藏搜索
            $('#global_search_modal').modal('hide').on('hidden.bs.modal', function (e) {
                globalSearch();
            })

        });

        // 搜索框enter事件
        $('body').on("keypress", '.search-toolbar input', function (e) {
            if (e.which == 13) {
                $(".btn-search").trigger("click");
                return false;
            }
        })*/

        // 隐藏系统加载提示框
        var hideMask = function () {
            $('#loading_bd').fadeOut(1000, function () {
                $('#loading_mask').fadeOut(200, function () {
                    $(this).remove()
                });
                $(this).remove();
            });
        }();

    });

    // 新搜索框
    $("#global_search_btns").click(function () {
        globalSearch();
    })
    // 搜索框enter事件
    $('body').on("keypress", '#global_search_words', function (e) {
        if (e.which == 13) {
            $("#global_search_btns").trigger("click");
            return false;
        }
    })

    // 全局搜索
    function globalSearch() {
        /*var height = $(window).height() - 50;
        Ls.openWin('/search', {
            width: '1000px',
            height: height + 'px',
            lock: true,
            title: '当前站点搜索',
            init: function () {
            },
            close: function () {
                $('#global_search_modal').off();
            }
        });*/
        App.globalSearch();
    }

    // 修改个人信息
    function editPersonalInfo() {
        Ls.openWin('/personalInfo/editPersonalInfo', {
            width: '650px',
            height: '410px',
            lock: true,
            title: '修改个人信息'
        });
    }

    // 推送js函数
    function reversCallBack(json) {
        // debugger
        var type = json.messageSystemEO.messageType;
        if (type == 4) { // session强制下线
            alert(json.messageSystemEO.content);
            location.href = json.messageSystemEO.link;
            return;
        }

        if (type == 5) {//视频转换
            var contentId = json.messageSystemEO.resourceId;
            if (json.messageSystemEO.data) {//进度
                $("#video_convert_complete_" + contentId).html("视频转换中，" + json.messageSystemEO.data.complete);
            } else {
                // 消息回调，更新页面禁用的控件
                moduleMsg.init(json).msgCount(true).createMsgLi().toastr();
                indexMgr.publish.call(this, json.messageSystemEO);
            }
            return;
        }

        // 弹出toastr
        if (type != 3) {
            moduleMsg.init(json).msgCount(true).createMsgLi().toastr();
            //console.log(json.messageSystemEO)
            if (json.messageSystemEO.title.indexOf('专题') > -1) {
                indexMgr.updateCloud();
            }
        }

        // 刷新弹窗的回调
        var $dialog = art.dialog.get("publish_problem");
        if ($dialog) {
            $dialog.iframe.contentWindow.indexMgr.publish.call(this, json.messageSystemEO);
        }

        // 消息回调，更新页面禁用的控件
        indexMgr.publish.call(this, json.messageSystemEO);

        if (json.messageSystemEO.data) {
            // 更新进度条
            try {
                typeof(static_mgr) != "undefined" && static_mgr.refurbish(json.messageSystemEO);
            } catch (e) {
                Ls.log('DWR:', e)
            }
        }
    }

    // 消息提醒
    toastr.options = {
        closeButton: false,
        progressBar: true,
        timeOut: "35000",
        //timeOut: "5000",
        hideEasing: "linear",
        hideMethod: "fadeOut",
        hideDuration: 100,
        extendedTimeOut: 200,
        closeOnHover: false,
        stopOnHover: true, //自定义--鼠标移入提醒框区域时停止计时及消息进入
        closeContainer: true, //自定义--关闭整个消息提醒框
        onCloseContainerClick: function (e) {

        }
    };

    try {
        // 这个方法用来启动该页面的ReverseAjax功能
        dwr.engine.setActiveReverseAjax(true);

        // 设置在页面关闭时，通知服务端销毁会话
        dwr.engine.setNotifyServerOnPageUnload(true);
    } catch (e) {
    }

    return {
        flushSite: flushSite,
        editPersonalInfo: editPersonalInfo,
        reversCallBack: reversCallBack,
        getMsgList: getMsgList,
        getPublishProblemCount: getPublishProblemCount,
        publish: $.noop,
        updateCloud: $.noop
    }
}();