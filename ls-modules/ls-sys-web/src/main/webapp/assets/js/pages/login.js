var loginMgr = function () {

    var LoginForm = $('#LoginForm');
    var key;
    try {
        key = RSAUtils.getKeyPair(security.exponent, '', security.modulus);
    } catch (e) {
    }

    LoginForm.validator({
        fields: {
            'username': '用户名:required;',
            'password': '密码:required;',
            'code': '验证码:required;'
        },
        valid: function (form) {
            var me = this;
            me.holdSubmit();
            var data = $(form).serializeObject();
            if (key) {
                data.password = RSAUtils.encryptedString(key, data.password);
                data.isEncryption = true;
            }
            postLogin(me, data);
        }
    })

    var postLogin = function (me, data) {
        Ls.ajax({
            url: "/login",
            data: data
        }).done(function (text) {
            // if($('#remebers:checked').length){
            //     $.cookie('remebersEx8', JSON.stringify({userName:$('#username').val(),password:$('#password').val()}), { expires: 7, path: '/' });
            // }else{
            //     $.cookie('remebersEx8', null);
            // }
            if (text.status == 1) {// 登录成功
                window.location.replace(GLOBAL_CONTEXTPATH + text.data);
            } else {
                $("#codeImg").click();
                Ls.tipsErr(text.desc, function () {
                    me.holdSubmit(false);
                });
            }
        }).fail(function () {
            $("#codeImg").click();
            me.holdSubmit(false);
        });
    }

    $(document).ready(function () {

        if (Ls.browser.isIE8OrLower) {
            window.location.replace(GLOBAL_CONTEXTPATH + "/ie_update");
            return false;
        }

        $(".code-refresh").on('click', function () {
            $("#code").val('');
            $("#codeImg").attr('src', '/login/getCode?' + new Date().getTime());
        });

        setTimeout(function () {
            $(".code-refresh").trigger('click');
        }, 1000);

        var hideMask = function () {
            $('#loading_bd').fadeOut(1500, function () {
                $('#loading_mask').fadeOut(200, function () {
                    $(this).remove();
                    $("#username").focus();
                });
                $(this).remove();
            });
        }();
    })
}()