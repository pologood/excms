var chuzhou = {siteId:2653861};
chuzhou.www = (function () {
    return {
        checkLogin: function () {
            return Ls.ajax({url: "/member/isLogin", data: {}}).done(function (d) {
                var data = d.data;
                if (data.isLogin == 1) {
                    $("#member_info").append('<a href="/member/center/'+chuzhou.siteId+'">' + data.userName + '</a><a href="javascript:void(0)" id="logout">[退出]</a>').on("click", "#logout", function () {
                        Ls.ajax({url: "/member/logout"}).done(function () {
                            window.location = window.location;
                        });
                    });
                } else {
                    $("#member_info").append('<a class="a1" href="/member/login/'+chuzhou.siteId+'">登录</a><a class="a2" href="/member/register/'+chuzhou.siteId+'">注册</a>');
                }
                return false;
            }).fail(function (d) {
                Ls.tips("服务器未能响应请求，请重试！", {icons: "error"});
            });
        }
    };
})();
$(document).ready(function () {
    chuzhou.www.checkLogin();
});