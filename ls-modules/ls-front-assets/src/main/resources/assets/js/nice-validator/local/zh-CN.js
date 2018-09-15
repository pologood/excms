/*********************************
 * Themes, rules, and i18n support
 * Locale: Chinese; 中文
 *********************************/
(function (factory) {
    typeof module === "object" && module.exports ? module.exports = factory(require("jquery")) :
        typeof define === 'function' && define.amd ? define(['jquery'], factory) :
            factory(jQuery);
}(function ($) {

    /* Global configuration
     */
    $.validator.config({
        stopOnError: true,
        focusCleanup: true,
        theme: 'yellow_top',
        timely: 0,
        msgClass: "n-bottom",

        // Custom rules
        rules: {
            digits: [/^\d+$/, "请填写数字"],
            letters: [/^[a-z]+$/i, "请填写字母"],
            date: [/^\d{4}-\d{2}-\d{2}$/, "请填写有效的日期，格式:yyyy-mm-dd"],
            time: [/^([01]\d|2[0-3])(:[0-5]\d){1,2}$/, "请填写有效的时间，00:00到23:59之间"],
            email: [/^[\w\+\-]+(\.[\w\+\-]+)*@[a-z\d\-]+(\.[a-z\d\-]+)*\.([a-z]{2,4})$/i, "请填写有效的邮箱"],
            url: [/^(https?|s?ftp):\/\/\S+$/i, "请填写有效的网址"],
            qq: [/^[1-9]\d{4,}$/, "请填写有效的QQ号"],
            // IDcard: [/^\d{6}(19|2\d)?\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)?$/, "请填写正确的身份证号码"],
            tel: [/^(?:(?:0\d{2,3}[\-]?[1-9]\d{6,8})|(?:[48]00[\-]?[1-9]\d{6}))$/, "请填写有效的电话号码"],
            tel_1: [/^(?:(?:0\d{2,3}[1-9]\d{6,8})|(?:[48]00[1-9]\d{6}))$/, "请填写有效的电话号码"],
            shortTel: [/^\d{7,8}$/, "请填写有效的电话号码"],
            // mobile: [/^1[3-9]\d{9}$/, "请填写有效的手机号"],
            mobile: [/^1(3[0-9]|4[57]|5[0-35-9]|6[0-9]|7[0-7]|8[0-9]|9[89])\d{8}$/, "请填写有效的手机号"],
            zipcode: [/^\d{6}$/, "请检查邮政编码格式"],
            chinese: [/^[\u0391-\uFFE5]+$/, "请填写中文字符"],
            username: [/^\w{3,12}$/, "请填写3-12位数字、字母、下划线"],
            password: [/^[\S]{6,16}$/, "请填写6-16位字符，不能包含空格"],
            password2: [/^(?![a-zA-z]+$)(?!\d+$)(?![!_#%&@\*\.\?\+\$\^\[\]\(\)\{\}\|\/\\]+$).{6,16}/, "请填写6-16位[数字、字母、英文符号]任意两种组合"],
            money: [/^(?!0\.00)(?:0|[1-9]\d*)(?:\.\d{1,2})?$/, "请填写有效的金额"],
            ip: [/^((25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})\.){3}(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[0-9]{1,2})$/i, '请填写有效的 IP 地址'],
            accept: function (element, params) {
                if (!params) return true;
                var ext = params[0], value = $(element).val();
                return (ext === '*')
                    || (new RegExp(".(?:" + ext + ")$", "i")).test(value)
                    || this.renderMsg("只接受{1}后缀的文件", ext.replace(/\|/g, ','));
            },
            //身份证
            IDcard: function (element) {
                var value = element.value,
                    isValid = true;
                var cityCode = {
                    11: "北京",
                    12: "天津",
                    13: "河北",
                    14: "山西",
                    15: "内蒙古",
                    21: "辽宁",
                    22: "吉林",
                    23: "黑龙江 ",
                    31: "上海",
                    32: "江苏",
                    33: "浙江",
                    34: "安徽",
                    35: "福建",
                    36: "江西",
                    37: "山东",
                    41: "河南",
                    42: "湖北 ",
                    43: "湖南",
                    44: "广东",
                    45: "广西",
                    46: "海南",
                    50: "重庆",
                    51: "四川",
                    52: "贵州",
                    53: "云南",
                    54: "西藏 ",
                    61: "陕西",
                    62: "甘肃",
                    63: "青海",
                    64: "宁夏",
                    65: "新疆",
                    71: "台湾",
                    81: "香港",
                    82: "澳门",
                    91: "国外 "
                };

                /* 15位校验规则： (dddddd yymmdd xx g)    g奇数为男，偶数为女
                 * 18位校验规则： (dddddd yyyymmdd xxx p) xxx奇数为男，偶数为女，p校验位

                 校验位公式：C17 = C[ MOD( ∑(Ci*Wi), 11) ]
                 i----表示号码字符从由至左包括校验码在内的位置序号
                 Wi 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2 1
                 Ci 1 0 X 9 8 7 6 5 4 3 2
                 */
                var rFormat = /^\d{6}(18|19|20)\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}(\d|X)$|^\d{6}\d{2}(0[1-9]|1[012])(0[1-9]|[12]\d|3[01])\d{3}$/;    // 格式验证

                if (!rFormat.test(value) || !cityCode[value.substr(0, 2)]) {
                    isValid = false;
                }
                // 18位身份证需要验证最后一位校验位
                else if (value.length === 18) {
                    var Wi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1];    // 加权因子
                    var Ci = "10X98765432"; // 校验字符
                    // 加权求和
                    var sum = 0;
                    for (var i = 0; i < 17; i++) {
                        sum += value.charAt(i) * Wi[i];
                    }
                    // 计算校验值
                    var C17 = Ci.charAt(sum % 11);
                    // 与校验位比对
                    if (C17 !== value.charAt(17)) {
                        isValid = false;
                    }
                }
                return isValid || "请填写正确的身份证号码";
            },
            //银行卡
            bankcard: function (element) {
                var value = element.value.replace(/\s/g, ''),
                    isValid = true,
                    rFormat = /^[\d]{12,19}$/;

                if (!rFormat.test(value)) {
                    isValid = false;
                } else {
                    var arr = value.split('').reverse(),
                        i = arr.length,
                        temp,
                        sum = 0;

                    while (i--) {
                        if (i % 2 === 0) {
                            sum += +arr[i];
                        } else {
                            temp = +arr[i] * 2;
                            sum += temp % 10;
                            if (temp > 9) sum += 1;
                        }
                    }
                    if (sum % 10 !== 0) {
                        isValid = false;
                    }
                }
                return isValid || "请填写有效的银行卡号";
            },
            //信用卡
            creditcard: function (element, params) {
                var value = element.value,
                    validTypes = 0x0000,
                    types = {
                        mastercard: 0x0001,
                        visa: 0x0002,
                        amex: 0x0004,
                        dinersclub: 0x0008,
                        enroute: 0x0010,
                        discover: 0x0020,
                        jcb: 0x0040,
                        unknown: 0x0080
                    };

                if (/[^0-9\-]+/.test(value)) {
                    return false;
                }
                value = value.replace(/\D/g, "");

                if (!params) {
                    validTypes = 0x0001 | 0x0002 | 0x0004 | 0x0008 | 0x0010 | 0x0020 | 0x0040 | 0x0080;
                } else {
                    for (var i = 0; i < parmas.length; i++) {
                        validTypes |= types[params[i]];
                    }
                }

                if (validTypes & 0x0001 && /^(5[12345])/.test(value)) { //mastercard
                    return value.length === 16;
                }
                if (validTypes & 0x0002 && /^(4)/.test(value)) { //visa
                    return value.length === 16;
                }
                if (validTypes & 0x0004 && /^(3[47])/.test(value)) { //amex
                    return value.length === 15;
                }
                if (validTypes & 0x0008 && /^(3(0[012345]|[68]))/.test(value)) { //dinersclub
                    return value.length === 14;
                }
                if (validTypes & 0x0010 && /^(2(014|149))/.test(value)) { //enroute
                    return value.length === 15;
                }
                if (validTypes & 0x0020 && /^(6011)/.test(value)) { //discover
                    return value.length === 16;
                }
                if (validTypes & 0x0040 && /^(3)/.test(value)) { //jcb
                    return value.length === 16;
                }
                if (validTypes & 0x0040 && /^(2131|1800)/.test(value)) { //jcb
                    return value.length === 15;
                }
                if (validTypes & 0x0080) { //unknown
                    return true;
                }
                return "请填写有效的信用卡号";
            },
            //组织机构代码证
            orgcode: function (element) {
                var value = element.value,
                    isValid = true,
                    rFormat = /^[A-Z\d]{8}-[X\d]/;

                if (!rFormat.test(value)) {
                    isValid = false;
                } else {
                    var Wi = [3, 7, 9, 10, 5, 8, 4, 2];
                    var Ci = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    // 加权求和
                    var sum = 0;
                    for (var i = 0; i < 8; i++) {
                        sum += Ci.indexOf(value.charAt(i)) * Wi[i];
                    }
                    // 计算校验值： C9 = 11 - MOD ( ∑(Ci*Wi), 11 )
                    var C9 = 11 - (sum % 11);
                    if (C9 === 10) C9 = 'X';
                    else if (C9 === 11) C9 = 0;
                    C9 = '' + C9;
                    // 与校验位比对
                    if (C9 !== value.charAt(9)) {
                        isValid = false;
                    }
                }

                return isValid || "请填写正确的组织机构代码";
            },
            //营业执照号
            bizcode: function (element) {
                var value = element.value,
                    isValid = true,
                    rFormat = /^[1-6]\d{14}$/;

                // 共15位：6位首次登记机关代码 + 8位顺序码 + 校验位
                if (!rFormat.test(value)) {
                    isValid = false;
                } else {
                    var s = [],
                        p = [10];

                    for (var i = 0; i < 15; i++) {
                        s[i] = (p[i] % 11) + (+value.charAt(i));
                        p[i + 1] = (s[i] % 10 || 10) * 2;
                    }
                    if (1 !== s[14] % 10) {
                        isValid = false;
                    }
                }
                return isValid || "请填写正确的营业执照号";
            },
            // 统一社会信用代码
            unicode: function (element) {
                var value = element.value.replace(/^\s*|\s*$/g, ''),
                    isValid = true,
                    rFormat = /^[1-9A-GV][1239][1-9]\d{5}[A-Z\d]{8}[X\d][Y\d]/;

                if (!rFormat.test(value)) {
                    isValid = false;
                } else {
                    var code, Wi, Ci, sum, C9, C18;

                    // 计算组织机构代码校验位：C9 = 11 - MOD ( ∑(Ci*Wi), 11 )
                    code = value.slice(9, 17);
                    Wi = [3, 7, 9, 10, 5, 8, 4, 2];
                    Ci = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                    // 加权求和
                    sum = 0;
                    for (var i = 0; i < Wi.length; i++) {
                        sum += Ci.indexOf(code.charAt(i)) * Wi[i];
                    }
                    C9 = 11 - (sum % 11);
                    if (C9 === 10) C9 = 'X';
                    else if (C9 === 11) C9 = 0;
                    C9 = '' + C9;
                    // 与校验位比对
                    if (C9 !== code.charAt(9)) {
                        isValid = false;
                    }

                    if (isValid) {
                        // 计算最后校验位：C18 = 31 - MOD ( ∑(Ci*Wi), 31 )
                        code = value.slice(0, 17);
                        Wi = [1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28];
                        Ci = "0123456789ABCDEFGHJKLMNPQRTUWXY";
                        // 加权求和
                        sum = 0;
                        for (var i = 0; i < Wi.length; i++) {
                            sum += Ci.indexOf(code.charAt(i)) * Wi[i];
                        }
                        C18 = 31 - (sum % 31);
                        if (C18 === 30) C18 = 'Y';
                        else if (C18 === 31) C18 = 0;
                        C18 = '' + C18;
                        // 与校验位比对
                        if (C18 !== code.charAt(18)) {
                            isValid = false;
                        }
                    }
                }

                return isValid || "请填写正确的统一社会信用代码";
            }
        },

        // Default error messages
        messages: {
            0: "此处",
            fallback: "{0}格式不正确",
            loading: "正在验证...",
            error: "网络异常",
            timeout: "请求超时",
            required: "{0}不能为空",
            remote: "{0}已被使用",
            integer: {
                '*': "请填写整数",
                '+': "请填写正整数",
                '+0': "请填写正整数或0",
                '-': "请填写负整数",
                '-0': "请填写负整数或0"
            },
            match: {
                eq: "{0}与{1}不一致",
                neq: "{0}与{1}不能相同",
                lt: "{0}必须小于{1}",
                gt: "{0}必须大于{1}",
                lte: "{0}不能大于{1}",
                gte: "{0}不能小于{1}"
            },
            range: {
                rg: "请填写{1}到{2}的数",
                gte: "请填写不小于{1}的数",
                lte: "请填写最大{1}的数",
                gtlt: "请填写{1}到{2}之间的数",
                gt: "请填写大于{1}的数",
                lt: "请填写小于{1}的数"
            },
            checked: {
                eq: "请选择{1}项",
                rg: "请选择{1}到{2}项",
                gte: "请至少选择{1}项",
                lte: "请最多选择{1}项"
            },
            length: {
                eq: "请填写{1}个字符",
                rg: "请填写{1}到{2}个字符",
                gte: "请至少填写{1}个字符",
                lte: "请最多填写{1}个字符",
                eq_2: "",
                rg_2: "",
                gte_2: "",
                lte_2: ""
            }
        }
    });

    /* Themes
     */
    var TPL_ARROW = '<span class="n-arrow"><b>◆</b><i>◆</i></span>';
    $.validator.setTheme({
        'simple_right': {
            formClass: 'n-simple',
            msgClass: 'n-right'
        },
        'simple_bottom': {
            formClass: 'n-simple',
            msgClass: 'n-bottom'
        },
        'yellow_top': {
            formClass: 'n-yellow',
            msgClass: 'n-top',
            msgArrow: TPL_ARROW
        },
        'yellow_right': {
            formClass: 'n-yellow',
            msgClass: 'n-right',
            msgArrow: TPL_ARROW
        },
        'yellow_right_effect': {
            formClass: 'n-yellow',
            msgClass: 'n-right',
            msgArrow: TPL_ARROW,
            msgShow: function ($msgbox, type) {
                var $el = $msgbox.children();
                if ($el.is(':animated')) return;
                if (type === 'error') {
                    $el.css({left: '20px', opacity: 0})
                        .delay(100).show().stop()
                        .animate({left: '-4px', opacity: 1}, 150)
                        .animate({left: '3px'}, 80)
                        .animate({left: 0}, 80);
                } else {
                    $el.css({left: 0, opacity: 1}).fadeIn(200);
                }
            },
            msgHide: function ($msgbox, type) {
                var $el = $msgbox.children();
                $el.stop().delay(100).show()
                    .animate({left: '20px', opacity: 0}, 300, function () {
                        $msgbox.hide();
                    });
            }
        }
    });
}));
