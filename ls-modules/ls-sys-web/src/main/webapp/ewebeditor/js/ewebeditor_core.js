/*
*######################################
* eWebEditor V11.5 - Advanced online web based WYSIWYG HTML editor.
* Copyright (c) 2003-2018 eWebSoft.com
*
* For further information go to http://www.ewebeditor.net/
* This copyright notice MUST stay intact for use.
*######################################
*/

/*自定义事件开始*/
function DoMyFunction() {
    console.log(parent.EWEBEDITOR.aV);
    console.log(aE.uU('<div class="video-player" data-url="http://ex8dev.5397078.cn/mongo/111" data-width="500" data-height="400" type="application/x-shockwave-flash" width="500" height="400" quality="high"></div>'));
}

function getPic(e) {
    var str = e.target.ownerDocument.URL,
        name = str.slice(str.indexOf('?id=') + 4, str.indexOf('&')),
        maxZ = 7000;
    var ewebObj = parent.EWEBEDITOR.Instances[name],
        para = ewebObj.Config.cusdir.substr(1).split('/');
    if (parent.$('#' + ewebObj.IframeId).css('z-index') !== 'auto') maxZ = parseInt(parent.$('#' + ewebObj.IframeId).css('z-index')) + 10;
    parent.Ls.openWin('/articleNews/mtxxUpload?mType=1&siteId=' + para[0] + '&columnId=' + para[1] + "&isCompress=" + parent.isCompress + "&imgWidth=" + parent.imgWidth + "&imgHeight=" + parent.imgHeight, '885px', '500px', {
        id: 'mtxxUpload',
        lock: true,
        title: '美图秀秀',
        zIndex: maxZ,
        close: function (data, iframe) {
            if (iframe) {
                var g = iframe.$("#mongoId").val();
                if (!parent.Ls.isEmpty(g)) {
                    var p = parent.GLOBAL_FILESERVERNAMEPATH + g;
                    ewebObj.insertHTML('<img src="' + p + '" alt="" />');
                }
            }
        }
    })
}

function getVideo(e) {
    var str = e.target.ownerDocument.URL,
        name = str.slice(str.indexOf('?id=') + 4, str.indexOf('&')),
        maxZ = 7000;
    var ewebObj = parent.EWEBEDITOR.Instances[name],
        para = ewebObj.Config.cusdir.substr(1).split('/');
    if (parent.$('#' + ewebObj.IframeId).css('z-index') !== 'auto') maxZ = parseInt(parent.$('#' + ewebObj.IframeId).css('z-index')) + 10;
    parent.Ls.openWin('/articleNews/videoUpload?siteId=' + para[0] + '&columnId=' + para[1], '500px', '280px', {
        id: 'videoUpload',
        lock: true,
        title: '视频上传',
        zIndex: maxZ,
        close: function (data, iframe) {
            if (iframe) {
                var videoName = iframe.$("#videoName").val(),
                    weightV = iframe.$("#weightV").val(),
                    heightV = iframe.$("#heightV").val(),
                    mongoId = iframe.$("#mongoId").val();
                if (!parent.Ls.isEmpty(videoName)) {
                    var videoPath = "";
                    if (!parent.Ls.isEmpty(mongoId)) {
                        videoPath = parent.GLOBAL_FILESERVERNAMEPATH + mongoId;
                    } else {
                        videoPath = videoName;
                    }
                    ewebObj.insertHTML('<div class="video-player" data-url="' + videoPath + '" data-width="' + weightV + '" data-height="' + heightV + '" type="application/x-shockwave-flash" width="' + weightV + '" height="' + heightV + '" quality="high"></div>');
                }
            }
        }
    });
}

function getFile(e) {
    var str = e.target.ownerDocument.URL,
        name = str.slice(str.indexOf('?id=') + 4, str.indexOf('&')),
        maxZ = 7000;
    var ewebObj = parent.EWEBEDITOR.Instances[name],
        para = ewebObj.Config.cusdir.substr(1).split('/');
    if (parent.$('#' + ewebObj.IframeId).css('z-index') !== 'auto') maxZ = parseInt(parent.$('#' + ewebObj.IframeId).css('z-index')) + 10;
    parent.Ls.openWin('fileCenter/winIndex?siteId=' + para[0] + '&columnId=' + para[1], '1200px', '680px', {
        id: 'videoUpload',
        lock: true,
        title: '文件资源',
        zIndex: maxZ,
        close: function (data, iframe) {
            if (iframe) {
                var videoName = iframe.$("#videoName").val(),
                    weightV = iframe.$("#weightV").val(),
                    heightV = iframe.$("#heightV").val(),
                    mongoId = iframe.$("#mongoId").val();
                if (!parent.Ls.isEmpty(videoName)) {
                    var videoPath = "";
                    if (!parent.Ls.isEmpty(mongoId)) {
                        videoPath = parent.GLOBAL_FILESERVERNAMEPATH + mongoId;
                    } else {
                        videoPath = videoName;
                    }
                    ewebObj.insertHTML('<div class="video-player" data-url="' + videoPath + '" data-width="' + weightV + '" data-height="' + heightV + '" type="application/x-shockwave-flash" width="' + weightV + '" height="' + heightV + '" quality="high"></div>');
                }
            }
        }
    });
}

/*自定义事件结束*/
//////////////////////////////////////////////////////////
String.prototype.Contains = function (s) {
    return (this.indexOf(s) > -1);
};

String.prototype.StartsWith = function (s) {
    return (this.substr(0, s.length) == s);
};

String.prototype.EndsWith = function (s, ignoreCase) {
    var L1 = this.length;
    var L2 = s.length;

    if (L2 > L1) {
        return false;
    }

    if (ignoreCase) {
        var oRegex = new RegExp(s + '$', 'i');
        return oRegex.test(this);
    } else {
        return (L2 == 0 || this.substr(L1 - L2, L2) == s);
    }
};

String.prototype.Trim = function () {
    return this.replace(/(^[ \t\n\r]*)|([ \t\n\r]*$)/g, '');
};

Array.prototype.IndexOf = function (s) {
    for (var i = 0; i < this.length; i++) {
        if (this[i] == s) {
            return i;
        }
    }
    return -1;
};


(function () {
    var compliantExecNpcg = /()??/.exec("")[1] === undefined;
    var compliantLastIndexIncrement = function () {
        var x = /^/g;
        x.test("");
        return !x.lastIndex;
    }();

    var real = {
        exec: RegExp.prototype.exec,
        match: String.prototype.match,
        replace: String.prototype.replace,
        split: String.prototype.split,
        test: RegExp.prototype.test
    };

    var _indexOf = function (array, item, from) {
        for (var i = from || 0; i < array.length; i++) {
            if (array[i] === item) {
                return i;
            }
        }
        return -1;
    };

    var _getNativeFlags = function (regex) {
        return (regex.global ? "g" : "") +
            (regex.ignoreCase ? "i" : "") +
            (regex.multiline ? "m" : "") +
            (regex.extended ? "x" : "") +
            (regex.sticky ? "y" : "");
    };

    var _isRegExp = function (o) {
        return Object.prototype.toString.call(o) === "[object RegExp]";
    };

    RegExp.prototype.exec = function (str) {
        var match = real.exec.apply(this, arguments),
            name, r2;

        if (match) {
            if (!compliantExecNpcg && match.length > 1 && _indexOf(match, "") > -1) {
                r2 = RegExp("^" + this.source + "$(?!\\s)", _getNativeFlags(this));
                real.replace.call(match[0], r2, function () {
                    for (var i = 1; i < arguments.length - 2; i++) {
                        if (arguments[i] === undefined) {
                            match[i] = undefined;
                        }
                    }
                });
            }

            if (!compliantLastIndexIncrement && this.global && this.lastIndex > (match.index + match[0].length)) {
                this.lastIndex--;
            }
        }

        return match;
    };

    if (!compliantLastIndexIncrement) {
        RegExp.prototype.test = function (str) {
            var match = real.exec.call(this, str);
            if (match && this.global && this.lastIndex > (match.index + match[0].length)) {
                this.lastIndex--;
            }
            return !!match;
        };
    }

    String.prototype.match = function (regex) {
        if (!_isRegExp(regex)) {
            regex = RegExp(regex);
        }
        if (regex.global) {
            var result = real.match.apply(this, arguments);
            regex.lastIndex = 0;
            return result;
        }
        return regex.exec(this);
    };

    String.prototype.split = function (s, limit) {
        if (!_isRegExp(s)) {
            return real.split.apply(this, arguments);
        }

        var str = this + "",
            output = [],
            lastLastIndex = 0,
            match, lastLength;

        if (limit === undefined || +limit < 0) {
            limit = Infinity;
        } else {
            limit = Math.floor(+limit);
            if (!limit) {
                return [];
            }
        }

        while (match = s.exec(str)) {
            if (s.lastIndex > lastLastIndex) {
                output.push(str.slice(lastLastIndex, match.index));

                if (match.length > 1 && match.index < str.length) {
                    Array.prototype.push.apply(output, match.slice(1));
                }

                lastLength = match[0].length;
                lastLastIndex = s.lastIndex;

                if (output.length >= limit) {
                    break;
                }
            }

            if (!match[0].length) {
                s.lastIndex++;
            }
        }

        if (lastLastIndex === str.length) {
            if (!real.test.call(s, "") || lastLength) {
                output.push("");
            }
        } else {
            output.push(str.slice(lastLastIndex));
        }

        return output.length > limit ? output.slice(0, limit) : output;
    };

})();


var EWEBPunycode = (function () {
    var _maxInt = 2147483647;
    var _base = 36;
    var _tMin = 1;
    var _tMax = 26;
    var _skew = 38;
    var _damp = 700;
    var _initialBias = 72;
    var _initialN = 128;
    var _regexPunycode = /^xn--/;
    var _regexNonASCII = /[^\x20-\x7E]/;
    var _regexSeparators = /[\x2E\u3002\uFF0E\uFF61]/g;

    var _baseMinusTMin = _base - _tMin;
    var _floor = Math.floor;
    var _stringFromCharCode = String.fromCharCode;

    var _map = function (a, fn) {
        var l = a.length;
        var a_Ret = [];
        while (l--) {
            a_Ret[l] = fn(a[l]);
        }
        return a_Ret;
    };

    var _mapDomain = function (s, fn) {
        var a_parts = s.split('@');
        var s_Ret = '';
        if (a_parts.length > 1) {
            s_Ret = a_parts[0] + '@';
            s = a_parts[1];
        }
        var a_labels = s.split(_regexSeparators);
        var s_encoded = _map(a_labels, fn).join('.');
        return s_Ret + s_encoded;
    };

    var _ucs2decode = function (s) {
        var a_Output = [],
            n_counter = 0,
            l = s.length,
            v,
            n_extra;
        while (n_counter < l) {
            v = s.charCodeAt(n_counter++);
            if (v >= 0xD800 && v <= 0xDBFF && n_counter < l) {
                n_extra = s.charCodeAt(n_counter++);
                if ((n_extra & 0xFC00) == 0xDC00) {
                    a_Output.push(((v & 0x3FF) << 10) + (n_extra & 0x3FF) + 0x10000);
                } else {
                    a_Output.push(v);
                    counter--;
                }
            } else {
                a_Output.push(v);
            }
        }
        return a_Output;
    };

    var _ucs2encode = function (a) {
        return _map(a, function (v) {
            var s_Output = '';
            if (v > 0xFFFF) {
                v -= 0x10000;
                s_Output += _stringFromCharCode(v >>> 10 & 0x3FF | 0xD800);
                v = 0xDC00 | v & 0x3FF;
            }
            s_Output += _stringFromCharCode(v);
            return s_Output;
        }).join('');
    };

    var _basicToDigit = function (n_codePoint) {
        if (n_codePoint - 48 < 10) {
            return n_codePoint - 22;
        }
        if (n_codePoint - 65 < 26) {
            return n_codePoint - 65;
        }
        if (n_codePoint - 97 < 26) {
            return n_codePoint - 97;
        }
        return _base;
    };

    var _digitToBasic = function (n_digit, flag) {
        return n_digit + 22 + 75 * (n_digit < 26) - ((flag != 0) << 5);
    };

    var _adapt = function (n_delta, numPoints, firstTime) {
        var k = 0;
        n_delta = firstTime ? _floor(n_delta / _damp) : n_delta >> 1;
        n_delta += _floor(n_delta / numPoints);
        for (; n_delta > _baseMinusTMin * _tMax >> 1; k += _base) {
            n_delta = _floor(n_delta / _baseMinusTMin);
        }
        return _floor(k + (_baseMinusTMin + 1) * n_delta / (n_delta + _skew));
    };

    var _decode = function (s_Input) {
        var a_Output = [],
            n_InputLength = s_Input.length,
            out,
            i = 0,
            n = _initialN,
            n_bias = _initialBias,
            n_basic,
            j,
            n_index,
            n_oldi,
            w,
            k,
            n_digit,
            t,
            n_baseMinusT;

        n_basic = s_Input.lastIndexOf('-');
        if (n_basic < 0) {
            n_basic = 0;
        }

        for (j = 0; j < n_basic; ++j) {
            if (s_Input.charCodeAt(j) >= 0x80) {
                return s_Input;
            }
            a_Output.push(s_Input.charCodeAt(j));
        }

        for (n_index = n_basic > 0 ? n_basic + 1 : 0; n_index < n_InputLength;) {
            for (n_oldi = i, w = 1, k = _base; ; k += _base) {

                if (n_index >= n_InputLength) {
                    return s_Input;
                }

                n_digit = _basicToDigit(s_Input.charCodeAt(n_index++));

                if (n_digit >= _base || n_digit > _floor((_maxInt - i) / w)) {
                    return s_Input;
                }

                i += n_digit * w;
                t = k <= n_bias ? _tMin : (k >= n_bias + _tMax ? _tMax : k - n_bias);

                if (n_digit < t) {
                    break;
                }

                n_baseMinusT = _base - t;
                if (w > _floor(_maxInt / n_baseMinusT)) {
                    return s_Input;
                }

                w *= n_baseMinusT;

            }

            out = a_Output.length + 1;
            n_bias = _adapt(i - n_oldi, out, n_oldi == 0);

            if (_floor(i / out) > _maxInt - n) {
                return s_Input;
            }

            n += _floor(i / out);
            i %= out;

            a_Output.splice(i++, 0, n);
        }

        return _ucs2encode(a_Output);
    };

    var _encode = function (s_Input) {
        var n,
            n_delta,
            n_handledCPCount,
            n_basicLength,
            n_bias,
            j,
            m,
            q,
            k,
            t,
            n_CurrentValue,
            a_Output = [],
            n_InputLength,
            n_handledCPCountPlusOne,
            n_baseMinusT,
            n_qMinusT;

        s_Input = _ucs2decode(s_Input);
        n_InputLength = s_Input.length;

        n = _initialN;
        n_delta = 0;
        n_bias = _initialBias;

        for (j = 0; j < n_InputLength; ++j) {
            n_CurrentValue = s_Input[j];
            if (n_CurrentValue < 0x80) {
                a_Output.push(_stringFromCharCode(n_CurrentValue));
            }
        }

        n_handledCPCount = n_basicLength = a_Output.length;

        if (n_basicLength) {
            a_Output.push('-');
        }

        while (n_handledCPCount < n_InputLength) {
            for (m = _maxInt, j = 0; j < n_InputLength; ++j) {
                n_CurrentValue = s_Input[j];
                if (n_CurrentValue >= n && n_CurrentValue < m) {
                    m = n_CurrentValue;
                }
            }

            n_handledCPCountPlusOne = n_handledCPCount + 1;
            if (m - n > _floor((_maxInt - n_delta) / n_handledCPCountPlusOne)) {
                return s_Input;
            }

            n_delta += (m - n) * n_handledCPCountPlusOne;
            n = m;

            for (j = 0; j < n_InputLength; ++j) {
                n_CurrentValue = s_Input[j];

                if (n_CurrentValue < n && ++n_delta > _maxInt) {
                    return s_Input;
                }

                if (n_CurrentValue == n) {
                    for (q = n_delta, k = _base; ; k += _base) {
                        t = k <= n_bias ? _tMin : (k >= n_bias + _tMax ? _tMax : k - n_bias);
                        if (q < t) {
                            break;
                        }
                        n_qMinusT = q - t;
                        n_baseMinusT = _base - t;
                        a_Output.push(
                            _stringFromCharCode(_digitToBasic(t + n_qMinusT % n_baseMinusT, 0))
                        );
                        q = _floor(n_qMinusT / n_baseMinusT);
                    }

                    a_Output.push(_stringFromCharCode(_digitToBasic(q, 0)));
                    n_bias = _adapt(n_delta, n_handledCPCountPlusOne, n_handledCPCount == n_basicLength);
                    n_delta = 0;
                    ++n_handledCPCount;
                }
            }

            ++n_delta;
            ++n;
        }
        return a_Output.join('');
    };

    var _DecodeDomain = function (s_Input) {
        return _mapDomain(s_Input, function (s) {
            return _regexPunycode.test(s) ? _decode(s.slice(4).toLowerCase()) : s;
        });
    };

    var _EncodeDomain = function (s_Input) {
        return _mapDomain(s_Input, function (s) {
            return _regexNonASCII.test(s) ? 'xn--' + _encode(s) : s;
        });
    };

    var _ParseUrl = function (s_Url, b_IsEncode) {
        var n = s_Url.indexOf("://");
        var s_Left = "";
        if (n > 0) {
            s_Left = s_Url.substr(0, n + 3);
            s_Url = s_Url.substr(n + 3);
        } else {
            return s_Url;
        }

        var s_Right = "";
        n = s_Url.indexOf("/");
        if (n > 0) {
            s_Right = s_Url.substr(n);
            s_Url = s_Url.substr(0, n);
        }
        n = s_Url.indexOf(":");
        if (n > 0) {
            s_Right = s_Url.substr(n) + s_Right;
            s_Url = s_Url.substr(0, n);
        }

        return s_Left + (b_IsEncode ? _EncodeDomain(s_Url) : _DecodeDomain(s_Url)) + s_Right;
    };

    return {
        DecodeDomain: function (s_Input) {
            return _DecodeDomain(s_Input);
        },

        EncodeDomain: function (s_Input) {
            return _EncodeDomain(s_Input);
        },

        DecodeUrl: function (s_Input) {
            return _ParseUrl(s_Input, false);
        },

        EncodeUrl: function (s_Input) {
            return _ParseUrl(s_Input, true);
        }

    };

})();


//////////////////////////////////////////////////////////


var EWEBParam = (function () {
    var o_URLParams = new Object();
    var a_Params = document.location.search.substr(1).split("&");
    for (i = 0; i < a_Params.length; i++) {
        var a_Param = a_Params[i].split("=");
        o_URLParams[a_Param[0]] = a_Param[1];
    }

    var _GetPValue = function (s_Key, s_Default) {
        var r_Filter = new RegExp('[\<\>\;\"\'\%\|\&\t]+', 'gi');
        var v = o_URLParams[s_Key];
        if (v && r_Filter.test(v)) {
            v = "";
        }
        if (!v) {
            v = s_Default;
        }
        return v;
    };

    var s_LinkField = _GetPValue("id", "");
    var s_LinkOriginalFileName = _GetPValue("originalfilename", "");
    var s_LinkSaveFileName = _GetPValue("savefilename", "");
    var s_LinkSavePathFileName = _GetPValue("savepathfilename", "");
    var s_ExtCSS = _GetPValue("extcss", "");
    var s_FullScreen = _GetPValue("fullscreen", "");
    var s_StyleName = _GetPValue("style", "coolblue");
    var s_CusDir = _GetPValue("cusdir", "");
    var s_Skin = _GetPValue("skin", "");
    var s_FixWidth = _GetPValue("fixwidth", "");
    var s_Lang = _GetPValue("lang", "");
    var s_AreaCssMode = _GetPValue("areacssmode", "");
    var s_ReadOnly = _GetPValue("readonly", "");
    var s_SKey = _GetPValue("skey", "");
    var s_InstanceId = _GetPValue("instanceid", s_LinkField);
    var s_AutoGrow = _GetPValue("autogrow", "");
    var s_TitleImage = _GetPValue("titleimage", "");
    var s_AttachEv = _GetPValue("attachev", "");
    var s_EQType = _GetPValue("eqtype", "");

    var s_Host = window.location.hostname;
    var s_Port = window.location.port;
    var n;
    if (s_Host.indexOf(":") > 0 && s_Host.indexOf("]") < 0) {
        s_Host = "[" + s_Host + "]";
    }
    s_Host = EWEBPunycode.EncodeDomain(s_Host);

    var s_Protocol = document.location.protocol.toLowerCase();
    n = s_Protocol.indexOf(":");
    if (n > 0) {
        s_Protocol = s_Protocol.substr(0, n);
    }

    return {
        LinkField: s_LinkField,
        InstanceId: s_InstanceId,
        LinkOriginalFileName: s_LinkOriginalFileName,
        LinkSaveFileName: s_LinkSaveFileName,
        LinkSavePathFileName: s_LinkSavePathFileName,
        ExtCSS: s_ExtCSS,
        FullScreen: s_FullScreen,
        StyleName: s_StyleName,
        CusDir: s_CusDir,
        Skin: s_Skin,
        FixWidth: s_FixWidth,
        Lang: s_Lang,
        AreaCssMode: s_AreaCssMode,
        ReadOnly: s_ReadOnly,
        SKey: s_SKey,
        AutoGrow: s_AutoGrow,
        TitleImage: s_TitleImage,
        AttachEv: s_AttachEv,
        EQType: s_EQType,
        H: s_Host,
        HPort: s_Port,
        Proto: s_Protocol
    };

})();

//////////////////////////////////////////////////////////


var EWEBBrowser = (function () {
    var s = navigator.userAgent.toLowerCase();
    var b_IsIE = (/*@cc_on!@*/false);
    var n_IEVer = 0;
    if (b_IsIE) {
        try {
            n_IEVer = parseInt(s.match(/msie (\d+)/)[1], 10);
        } catch (e) {
            n_IEVer = 0;
        }
    }
    var b_IE11P = (document.documentMode >= 11);
    var b_IsAllIE = (b_IsIE || b_IE11P);
    var b_IsSafari = s.Contains('safari');
    var b_IsChrome = s.Contains('chrome/');
    var b_IsFirefox = s.Contains('firefox');
    var n_ChromeVer = 0;
    if (b_IsChrome) {
        var a_ms = s.match(/chrome\/(\d+)/);
        if (a_ms) {
            n_ChromeVer = parseInt(a_ms[1], 10);
        }
    }
    var n_FirefoxVer = 0;
    if (b_IsFirefox) {
        var a_ms = s.match(/firefox\/(\d+)/);
        if (a_ms) {
            n_FirefoxVer = parseInt(a_ms[1], 10);
        }
    }

    var b_IsEdge = s.Contains('edge/');
    var b_IsUseLS = false;
    var s_LSBrowser = "";
    var s_Platform = window.navigator.platform;
    var b_IsWindow = (s_Platform.indexOf("Win") == 0) ? true : false;
    var b_IsWin64 = (s_Platform == "Win64");

    if (b_IsEdge) {
        b_IsUseLS = true;
        s_LSBrowser = "edge";
    } else if (b_IsFirefox && b_IsWin64) {
        b_IsUseLS = true;
        s_LSBrowser = "firefox64";
    } else if (b_IsFirefox && n_FirefoxVer >= 52) {
        b_IsUseLS = true;
        s_LSBrowser = "firefox52p";
    } else if (s.Contains('qqbrowser')) {
        b_IsUseLS = false;
        s_LSBrowser = "qqbrowser";
        if (n_ChromeVer == 53) {
            var a_qv = s.match(/qqbrowser\/(\d+)\.(\d+)\.(\d+)/);
            if (a_qv) {
                var n_QQV1 = parseInt(a_qv[1], 10);
                var n_QQV2 = parseInt(a_qv[2], 10);
                var n_QQV3 = parseInt(a_qv[3], 10);
                if ((n_QQV1 == 9) && (n_QQV2 == 5) && (n_QQV3 < 9882)) {
                    b_IsUseLS = true;
                }
            }
        } else if (n_ChromeVer > 53) {
            b_IsUseLS = true;
        }
    } else if (n_ChromeVer >= 42) {
        b_IsUseLS = true;
        s_LSBrowser = "chrome";

        if ((s.Contains('lbbrowser') && (n_ChromeVer < 49)) || s.Contains('metasr') || s.Contains('2345explorer') || s.Contains('maxthon') || s.Contains('bidubrowser') || s.Contains('ubrowser')) {
            b_IsUseLS = false;
        }

        if (b_IsUseLS) {
            var o_FlashMimeType = navigator.mimeTypes["application/x-shockwave-flash"];
            if (o_FlashMimeType) {
                if (o_FlashMimeType.description.toLowerCase().indexOf("adobe") >= 0) {
                    b_IsUseLS = false;
                }
            }
        }
    } else if (!b_IsAllIE && !b_IsChrome && !b_IsFirefox && b_IsSafari) {
        b_IsUseLS = true;
        s_LSBrowser = "safari";
    }

    return {
        IsIE: b_IsIE,
        IsIE6: (n_IEVer == 6),
        IsIE6P: (n_IEVer >= 6),
        IsIE7P: (n_IEVer >= 7),
        IsIE10P: (document.documentMode >= 10),
        IsIE11P: b_IE11P,
        IsAllIE: b_IsAllIE,
        IsIE9: (b_IsIE && (s.indexOf("trident/5") > -1)),
        IsIE10: (b_IsIE && (s.indexOf("trident/6") > -1)),
        IsIE11: (b_IsIE && (s.indexOf("trident/7") > -1)),
        ie8p: !!document.documentMode,
        IsIE8Compat: document.documentMode == 8,
        IsIE9Compat: document.documentMode == 9,
        IsIE10Compat: document.documentMode == 10,
        IsGecko: s.Contains('gecko/'),
        IsSafari: b_IsSafari,
        IsOpera: s.Contains(' opr/'),
        IsMac: s.Contains('macintosh'),
        IsChrome: b_IsChrome,
        IsFirefox: b_IsFirefox,
        IsEdge: b_IsEdge,
        IsUseLS: b_IsUseLS,
        sLSBrowser: s_LSBrowser,
        IsWebkit: s.Contains(' applewebkit/'),
        IsWin64: b_IsWin64,
        IsWindow: b_IsWindow,
        IsStandardMode: ((!b_IsIE) || (b_IsIE && (document.documentMode >= 9))),
        IsCompatible: true
    };
})();


//////////////////////////////////////////////////////////


var lang = new Object();

lang.AvailableLangs = {
    "da": true,
    "de": true,
    "en": true,
    "fr": true,
    "it": true,
    "es": true,
    "ja": true,
    "nl": true,
    "no": true,
    "pt": true,
    "ru": true,
    "sv": true,
    "zh-cn": true,
    "zh-tw": true
};

lang.GetActiveLanguage = function () {
    if (EWEBParam.Lang) {
        return EWEBParam.Lang;
    }

    if (config.AutoDetectLanguage == "1") {
        var sUserLang;
        if (navigator.userLanguage) {
            sUserLang = navigator.userLanguage.toLowerCase();
        } else if (navigator.language) {
            sUserLang = navigator.language.toLowerCase();
        } else {
            return this.DefaultLanguage;
        }

        if (this.AvailableLangs[sUserLang]) {
            return sUserLang;
        } else if (sUserLang.length > 2) {
            sUserLang = sUserLang.substr(0, 2);
            if (this.AvailableLangs[sUserLang]) {
                return sUserLang;
            }
        }
    }
    return this.DefaultLanguage;
};


lang.TranslatePage = function (o_Doc) {
    var aInputs = o_Doc.getElementsByTagName("INPUT");
    for (i = 0; i < aInputs.length; i++) {
        if (aInputs[i].getAttribute("lang")) {
            aInputs[i].value = lang[aInputs[i].getAttribute("lang")];
        }
    }

    var aSpans = o_Doc.getElementsByTagName("SPAN");
    for (i = 0; i < aSpans.length; i++) {
        if (aSpans[i].getAttribute("lang")) {
            aSpans[i].innerHTML = lang[aSpans[i].getAttribute("lang")];
        }
    }

    var aOptions = o_Doc.getElementsByTagName("OPTION");
    for (i = 0; i < aOptions.length; i++) {
        if (aOptions[i].getAttribute("lang")) {
            aOptions[i].innerHTML = lang[aOptions[i].getAttribute("lang")];
        }
    }
};

lang.Init = function () {
    if (this.AvailableLangs[config.DefaultLanguage]) {
        this.DefaultLanguage = config.DefaultLanguage;
    } else {
        this.DefaultLanguage = "en";
    }

    this.ActiveLanguage = this.GetActiveLanguage();
};


//////////////////////////////////////////////////////////


var EWEBMenu = (function () {
    var _BaseZindex = 9999789;
    var _Document, _Popup, _IFrame;
    var _MenuHeader, _MenuHr, _Menu1, _Menu2;
    var _Pos = {x: 0, y: 0, ew: 0, rel: null};
    var _IsStrict;
    var _MainNode;
    var _IsOpened = false;
    var _TopWindow;


    var _InitMenu = function () {
        if (_Document) {
            return;
        }

        _Menu1 = "<table border=0 cellpadding=0 cellspacing=0 class='Menu_Box' id=Menu_Box><tr><td class='Menu_Box'><table border=0 cellpadding=0 cellspacing=0 class='Menu_Table'>";
        _MenuHr = "<tr><td class='Menu_Sep'><table border=0 cellpadding=0 cellspacing=0 class='Menu_Sep'><tr><td></td></tr></table></td></tr>";
        _Menu2 = "</table></td></tr></table>";
        _MenuHeader = "<html><head>"
            + "<link href='" + EWEB.RootPath + "/language/" + lang.ActiveLanguage + ".css' type='text/css' rel='stylesheet'>"
            + "<link href='" + EWEB.RootPath + "/skin/" + config.Skin + "/menuarea.css' type='text/css' rel='stylesheet'>"
            + "</head>"
            + "<body scroll='no'>";

        if (EWEBBrowser.IsIE) {
            _Popup = window.createPopup();

            _Document = _Popup.document;
            _Document.open();
            _Document.write(_MenuHeader);
            _Document.close();
        } else {
            _TopWindow = EWEBTools.GetTopWindow();
            var o_TopDocument = _TopWindow.document;

            _IFrame = o_TopDocument.createElement('iframe');
            EWEBTools.ResetStyles(_IFrame);
            _IFrame.src = 'javascript:void(0)';
            _IFrame.allowTransparency = true;
            _IFrame.frameBorder = '0';
            _IFrame.scrolling = 'no';
            _IFrame.style.width = _IFrame.style.height = '0px';
            EWEBTools.SetElementStyles(_IFrame,
                {
                    position: 'absolute',
                    zIndex: _BaseZindex
                });

            _IFrame._DialogArguments = window;

            o_TopDocument.body.appendChild(_IFrame);

            var o_IFrameWindow = _IFrame.contentWindow;
            _Document = o_IFrameWindow.document;

            _Document.open();
            _Document.write(_MenuHeader);
            _Document.close();

            _IsStrict = EWEBTools.IsStrictMode(_Document);
            EWEBTools.DisableSelection(_Document.body);

            EWEBTools.AddEventListener(o_IFrameWindow, 'focus', _EWEBMenu_Win_OnFocus);
            EWEBTools.AddEventListener(o_IFrameWindow, 'blur', _EWEBMenu_Win_OnBlur);

        }

        _MainNode = _Document.body.appendChild(_Document.createElement('DIV'));
        _MainNode.style.cssFloat = 'left';

        EWEBTools.AddEventListener(_Document, 'contextmenu', EWEBTools.CancelEvent);
        EWEBTools.AddEventListener(_Document, 'dragstart', EWEBTools.CancelEvent);
        EWEBTools.AddEventListener(_Document, 'selectstart', EWEBTools.CancelEvent);
        EWEBTools.AddEventListener(_Document, 'select', EWEBTools.CancelEvent);

    };


    var _GetMenuRow = function (s_Disabled, s_Event, s_Image, s_Html) {
        var s_MenuRow = "";

        var s_Click;
        if (EWEBBrowser.IsIE) {
            s_Click = "var w=parent;w." + s_Event + ";w.EWEBMenu.Hide();";
        } else {
            s_Click = "var w=frameElement._DialogArguments;w.EWEBMenu.Hide();w." + s_Event + ";"
        }

        if (s_Disabled == "") {
            s_MenuRow += "<tr><td class='Menu_Item'><table border=0 cellpadding=0 cellspacing=0 width='100%'><tr><td valign=middle class=MouseOut onMouseOver=\"this.className='MouseOver'\" onMouseOut=\"this.className='MouseOut'\" onclick=\"" + s_Click + "\">";
        } else {
            s_MenuRow += "<tr><td class='Menu_Item'><table border=0 cellpadding=0 cellspacing=0 width='100%'><tr><td valign=middle class=MouseDisabled>";

        }

        s_Disabled = (s_Disabled) ? "_Disabled" : "";

        s_MenuRow += "<table border=0 cellpadding=0 cellspacing=0><tr><td class=Menu_Image_TD>";

        if (typeof(s_Image) == "number") {
            var s_Img = "skin/" + config.Skin + "/buttons.gif";
            var n_Top = 16 - s_Image * 16;
            s_MenuRow += "<div class='Menu_Image" + s_Disabled + "'><img src='" + s_Img + "' style='top:" + n_Top + "px'></div>";
        } else if (s_Image != "") {
            var s_Img = "skin/" + config.Skin + "/" + s_Image;
            s_MenuRow += "<img class='Menu_Image" + s_Disabled + "' src='" + s_Img + "'>";
        }
        s_MenuRow += "</td><td class='Menu_Label" + s_Disabled + "'>" + s_Html + "</td></tr></table>";
        s_MenuRow += "</td></tr></table></td><\/tr>";
        return s_MenuRow;
    };

    var _GetStandardMenuRow = function (s_Disabled, s_Code, s_Lang) {
        var a_Button = Buttons[s_Code];
        if (!s_Lang) {
            s_Lang = lang[s_Code];
        } else {
            s_Lang = lang[s_Lang];
        }
        var s_Fn = a_Button[1] ? a_Button[1] : "exec('" + s_Code + "')";
        return _GetMenuRow(s_Disabled, s_Fn, a_Button[0], s_Lang);
    };

    var _GetFormatMenuRow = function (s_Code, s_Cmd) {
        var s_Disabled = "";
        if (!s_Cmd) {
            s_Cmd = s_Code;
        }
        try {
            if (!EWEB.EditorDocument.queryCommandEnabled(s_Cmd)) {
                s_Disabled = "disabled";
            }
        } catch (e) {
        }

        if (s_Disabled) {
            if (s_Cmd == "Copy" || s_Cmd == "Cut") {
                if (config.AutoDetectPaste == "1") {
                    //if (EWEBActiveX.IsInstalled(false)){
                    s_Disabled = "";
                    //}
                }
            }
        }

        return _GetStandardMenuRow(s_Disabled, s_Code);
    };


    var _GetTableMenuRow = function (what) {
        var s_Menu = "";
        var s_Disabled = "disabled";
        switch (what) {
            case "TableInsert":
                if (!EWEBTable.IsTableSelected()) {
                    s_Disabled = "";
                }
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableInsert");
                break;
            case "TableProp":
                if (EWEBTable.IsTableSelected() || EWEBTable.IsCursorInCell()) {
                    s_Disabled = "";
                }
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableProp");
                break;
            case "TableCell":
                if (EWEBTable.IsCursorInCell()) {
                    s_Disabled = "";
                }
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableCellProp");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableCellSplit");
                s_Menu += _MenuHr;
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableRowProp");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableRowInsertAbove");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableRowInsertBelow");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableRowMerge");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableRowSplit");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableRowDelete");
                s_Menu += _MenuHr;
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableColInsertLeft");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableColInsertRight");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableColMerge");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableColSplit");
                s_Menu += _GetStandardMenuRow(s_Disabled, "TableColDelete");
                break;
        }
        return s_Menu;
    };


    var _IsControlSelected = function (s_Tag, s_AttrName, s_AttrValue) {
        if (EWEBSelection.GetType() == "Control") {
            if (s_Tag) {
                var el = EWEBSelection.GetSelectedElement();
                if (el.tagName.toUpperCase() == s_Tag) {
                    if ((s_AttrName) && (s_AttrValue)) {
                        if (el.getAttribute(s_AttrName, 2).toLowerCase() == s_AttrValue.toLowerCase()) {
                            return true;
                        }
                    } else {
                        return true;
                    }
                }
            } else {
                return true;
            }
        }
        return false;
    };


    _LoadComplete = function () {
        if (_Document.readyState != "complete" && _Document.readyState != "interactive") {
            return false;
        }

        if (_Document.images) {
            for (var i = 0; i < _Document.images.length; i++) {
                var img = _Document.images[i];
                //if (img.readyState!="complete"){
                if (!img.complete) {
                    return false;
                }
            }
        }

        return true;
    };


    return {

        Show: function () {
            if (EWEBBrowser.IsIE) {
                if (!_LoadComplete()) {
                    window.setTimeout("EWEBMenu.Show()", 50);
                    return;
                }

                var w = _Document.body.scrollWidth;
                var h = _Document.body.scrollHeight;

                if (_Pos.x + w > document.body.clientWidth) {
                    _Pos.x = _Pos.x - w + _Pos.ew;
                }

                _Popup.show(_Pos.x, _Pos.y, w, h, _Pos.rel);
            } else {
                var w = _MainNode.offsetWidth;
                var h = _MainNode.offsetHeight;
                var x = _Pos.x;
                var y = _Pos.y;

                //if (_Document.readyState!="complete"){
                if (h > 1000) {
                    window.setTimeout("EWEBMenu.Show()", 50);
                    return;
                }

                EWEBTools.SetElementStyles(_IFrame,
                    {
                        width: w + 'px',
                        height: h + 'px',
                        left: x + 'px',
                        top: y + 'px'
                    });

                _IsOpened = true;

            }
        },


        ShowToolMenu: function (e, s_Flag) {
            if (EWEB.CurrMode != "EDIT") {
                return EWEBTools.CancelEvent(e);
            }

            _InitMenu();
            if (_IsOpened) {
                this.Hide();
            }
            EWEB.Focus();
            EWEBSelection.Save(true);

            //FocusEditarea();
            var s_Menu = "";

            switch (s_Flag) {
                case "font":
                    s_Menu += _GetFormatMenuRow("Bold");
                    s_Menu += _GetFormatMenuRow("Italic");
                    s_Menu += _GetFormatMenuRow("UnderLine");
                    s_Menu += _GetFormatMenuRow("StrikeThrough");
                    s_Menu += _MenuHr;
                    s_Menu += _GetFormatMenuRow("SuperScript");
                    s_Menu += _GetFormatMenuRow("SubScript");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "UpperCase");
                    s_Menu += _GetStandardMenuRow("", "LowerCase");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "ForeColor");
                    s_Menu += _GetStandardMenuRow("", "BackColor");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "Big");
                    s_Menu += _GetStandardMenuRow("", "Small");
                    break;
                case "paragraph":
                    s_Menu += _GetFormatMenuRow("JustifyLeft");
                    s_Menu += _GetFormatMenuRow("JustifyCenter");
                    s_Menu += _GetFormatMenuRow("JustifyRight");
                    s_Menu += _GetFormatMenuRow("JustifyFull");
                    s_Menu += _MenuHr;
                    s_Menu += _GetFormatMenuRow("OrderedList", "insertorderedlist");
                    s_Menu += _GetFormatMenuRow("UnOrderedList", "insertunorderedlist");
                    s_Menu += _GetFormatMenuRow("Indent");
                    s_Menu += _GetFormatMenuRow("Outdent");
                    s_Menu += _MenuHr;
                    s_Menu += _GetFormatMenuRow("Paragraph", "insertparagraph");
                    s_Menu += _GetStandardMenuRow("", "BR");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow((_IsParagraphRelativeSelection()) ? "" : "disabled", "ParagraphAttr", "CMenuParagraph");
                    break;
                case "edit":
                    var s_Disabled = "";
                    if (!EWEBHistory.QueryUndoState()) {
                        s_Disabled = "disabled";
                    }
                    s_Menu += _GetStandardMenuRow(s_Disabled, "UnDo");
                    if (!EWEBHistory.QueryRedoState()) {
                        s_Disabled = "disabled";
                    }
                    s_Menu += _GetStandardMenuRow(s_Disabled, "ReDo");
                    s_Menu += _MenuHr;
                    s_Menu += _GetFormatMenuRow("Cut");
                    s_Menu += _GetFormatMenuRow("Copy");
                    //s_Menu += _GetFormatMenuRow("Paste");
                    s_Menu += _GetStandardMenuRow("", "Paste");
                    s_Menu += _GetStandardMenuRow("", "PasteText");
                    s_Menu += _GetStandardMenuRow("", "PasteWord");
                    s_Menu += _MenuHr;
                    s_Menu += _GetFormatMenuRow("Delete");
                    s_Menu += _GetFormatMenuRow("RemoveFormat");
                    s_Menu += _MenuHr;
                    s_Menu += _GetFormatMenuRow("SelectAll");
                    s_Menu += _GetFormatMenuRow("UnSelect");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "FindReplace");
                    s_Menu += _GetStandardMenuRow("", "QuickFormat");
                    break;
                case "object":
                    s_Menu += _GetStandardMenuRow("", "BgColor");
                    s_Menu += _GetStandardMenuRow("", "BackImage");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "absolutePosition");
                    s_Menu += _GetStandardMenuRow("", "zIndexForward");
                    s_Menu += _GetStandardMenuRow("", "zIndexBackward");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "ShowBorders");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "Quote");
                    s_Menu += _GetStandardMenuRow("", "Code");
                    break;
                case "component":
                    s_Menu += _GetStandardMenuRow("", "Image");
                    s_Menu += _GetStandardMenuRow("", "Flash");
                    s_Menu += _GetStandardMenuRow("", "Media");
                    s_Menu += _GetStandardMenuRow("", "File");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "RemoteUpload");
                    s_Menu += _GetStandardMenuRow("", "LocalUpload");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "Fieldset");
                    s_Menu += _GetStandardMenuRow("", "Iframe");
                    s_Menu += _GetFormatMenuRow("HorizontalRule", "InsertHorizontalRule");
                    s_Menu += _GetStandardMenuRow("", "Marquee");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "CreateLink");
                    s_Menu += _GetStandardMenuRow("", "Anchor");
                    s_Menu += _GetStandardMenuRow("", "Map");
                    s_Menu += _GetFormatMenuRow("Unlink");
                    break;
                case "tool":
                    s_Menu += _GetStandardMenuRow("", "Template");
                    s_Menu += _GetStandardMenuRow("", "Symbol");
                    s_Menu += _GetStandardMenuRow("", "Excel");
                    s_Menu += _GetStandardMenuRow("", "Emot");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "MathFlowEQ");
                    s_Menu += _GetStandardMenuRow("", "Art");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "NowDate");
                    s_Menu += _GetStandardMenuRow("", "NowTime");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "ImportWord");
                    s_Menu += _GetStandardMenuRow("", "ImportExcel");
                    s_Menu += _GetStandardMenuRow("", "ImportPPT");
                    s_Menu += _GetStandardMenuRow("", "ImportPDF");
                    s_Menu += _GetStandardMenuRow("", "Capture");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "Pagination");
                    s_Menu += _GetStandardMenuRow("", "PaginationInsert");
                    break;
                case "file":
                    s_Menu += _GetStandardMenuRow("", "Refresh");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "ModeCode");
                    s_Menu += _GetStandardMenuRow("", "ModeEdit");
                    s_Menu += _GetStandardMenuRow("", "ModeText");
                    s_Menu += _GetStandardMenuRow("", "ModeView");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "SizePlus");
                    s_Menu += _GetStandardMenuRow("", "SizeMinus");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "Print");
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "About");
                    s_Menu += _GetStandardMenuRow("", "Site");
                    height = 208;
                    break;
                case "table":
                    s_Menu += _GetTableMenuRow("TableInsert");
                    s_Menu += _GetTableMenuRow("TableProp");
                    s_Menu += _MenuHr;
                    s_Menu += _GetTableMenuRow("TableCell");
                    break;
                case "form":
                    s_Menu += _GetStandardMenuRow("", "FormText");
                    s_Menu += _GetStandardMenuRow("", "FormTextArea");
                    s_Menu += _GetStandardMenuRow("", "FormRadio");
                    s_Menu += _GetStandardMenuRow("", "FormCheckbox");
                    s_Menu += _GetStandardMenuRow("", "FormDropdown");
                    s_Menu += _GetStandardMenuRow("", "FormButton");
                    break;
                case "gallery":
                    s_Menu += _GetStandardMenuRow("", "GalleryImage");
                    s_Menu += _GetStandardMenuRow("", "GalleryFlash");
                    s_Menu += _GetStandardMenuRow("", "GalleryMedia");
                    s_Menu += _GetStandardMenuRow("", "GalleryFile");
                    break;
                case "zoom":
                    for (var i = 0; i < EWEBCommandZoom.Options.length; i++) {
                        if (EWEBCommandZoom.Options[i] == EWEBCommandZoom.CurrScale) {
                            s_Menu += _GetMenuRow("", "EWEBCommandZoom.Execute(" + EWEBCommandZoom.Options[i] + ")", 120, EWEBCommandZoom.Options[i] + "%");
                        } else {
                            s_Menu += _GetMenuRow("", "EWEBCommandZoom.Execute(" + EWEBCommandZoom.Options[i] + ")", 119, EWEBCommandZoom.Options[i] + "%");
                        }
                    }
                    break;
                case "fontsize":
                    var v = EWEBSelection.QueryFontSize();
                    for (var i = 0; i < lang["FontSizeItem"].length; i++) {
                        if (lang["FontSizeItem"][i][0] == v) {
                            s_Menu += _GetMenuRow("", "formatFont('size','" + lang["FontSizeItem"][i][0] + "')", 120, lang["FontSizeItem"][i][1]);
                        } else {
                            s_Menu += _GetMenuRow("", "formatFont('size','" + lang["FontSizeItem"][i][0] + "')", 119, lang["FontSizeItem"][i][1]);
                        }
                    }
                    break;
                case "fontname":
                    var v = EWEB.EditorDocument.queryCommandValue("FontName");
                    for (var i = 0; i < lang["FontNameItem"].length; i++) {
                        var s_FontNameValue = lang["FontNameItem"][i];
                        var a_FontNameValue = s_FontNameValue.toLowerCase().split(",");
                        var s_FontNameText = s_FontNameValue.split(",")[0];
                        if (v && a_FontNameValue.IndexOf(v.toLowerCase()) >= 0) {
                            s_Menu += _GetMenuRow("", "formatFont('face','" + s_FontNameValue + "')", 120, s_FontNameText);
                        } else {
                            s_Menu += _GetMenuRow("", "formatFont('face','" + s_FontNameValue + "')", 119, s_FontNameText);
                        }
                    }
                    break;
                case "formatblock":
                    var v = EWEB.EditorDocument.queryCommandValue("FormatBlock");
                    if (v) {
                        v = v.toLowerCase();
                    } else {
                        v = "";
                    }
                    for (var i = 0; i < lang["FormatBlockItem"].length; i++) {
                        if (lang["FormatBlockItem"][i][0].toLowerCase() == v) {
                            s_Menu += _GetMenuRow("", "format('FormatBlock','" + lang["FormatBlockItem"][i][0] + "')", 120, lang["FormatBlockItem"][i][1]);
                        } else {
                            s_Menu += _GetMenuRow("", "format('FormatBlock','" + lang["FormatBlockItem"][i][0] + "')", 119, lang["FormatBlockItem"][i][1]);
                        }
                    }
                    break;
                case "lineheight":
                case "margintop":
                case "marginbottom":
                    var v = EWEBCommandParagraph._QueryCommandValue(s_Flag);
                    var a_Options = EWEBCommandParagraph._GetOptions(s_Flag);
                    for (var i = 0; i < a_Options.length; i++) {
                        var s_Text = a_Options[i] ? a_Options[i] : lang["DlgComDefault"];
                        if (v == a_Options[i]) {
                            s_Menu += _GetMenuRow("", "EWEBCommandParagraph._Execute('" + s_Flag + "','" + a_Options[i] + "')", 120, s_Text);
                        } else {
                            s_Menu += _GetMenuRow("", "EWEBCommandParagraph._Execute('" + s_Flag + "','" + a_Options[i] + "')", 119, s_Text);
                        }
                    }
                    break;
            }


            //_Document.body.innerHTML = _Menu1 + s_Menu + _Menu2;
            _MainNode.innerHTML = _Menu1 + s_Menu + _Menu2;
            EWEBTools.DisableSelection(_MainNode);

            if (_Popup) {
                //preload
                _Popup.show(0, 0, 0, 0, document.body);

                e = window.event;
                e.returnValue = false;

                var el = e.srcElement;
                var x = e.clientX - e.offsetX;
                var y = e.clientY - e.offsetY;
                if (el.style.top) {
                    y = y - parseInt(el.style.top);
                }


                if (el.tagName.toLowerCase() == "img") {
                    el = el.parentNode;
                    x = x - el.offsetLeft - el.clientLeft;
                    y = y - el.offsetTop - el.clientTop;
                }
                if (el.className == "TB_Btn_Image") {
                    el = el.parentNode;
                    x = x - el.offsetLeft - el.clientLeft;
                    y = y - el.offsetTop - el.clientTop;
                }

                y = y + el.offsetHeight;

                var ew = parseInt(el.offsetWidth);

                _Pos.x = x;
                _Pos.y = y;
                _Pos.ew = ew;
                _Pos.rel = EWEBTools.IsStrictMode(document) ? document.documentElement : document.body;
                EWEBMenu.Show();

            } else {
                var el = e.target;

                if (el.tagName.toLowerCase() == "img" || el.className == "TB_Btn_Image") {
                    el = el.parentNode;
                }

                var x = 0;
                var y = 0;
                var ew = el.offsetWidth;

                if (EWEBBrowser.IsSafari) {
                    x = e.clientX;
                    y = e.clientY;
                } else {
                    x = e.pageX;
                    y = e.pageY;
                }


                var oPos = EWEBTools.GetDocumentPosition(_TopWindow, el);
                x = oPos.x;
                y = oPos.y + el.offsetHeight;

                _Pos.x = x;
                _Pos.y = y;
                _Pos.ew = ew;

                _IFrame.contentWindow.focus();

                window.setTimeout("EWEBMenu.Show()", 1);
            }


        },

        ShowContextMenu: function (e) {
            if (EWEB.CurrMode != "EDIT") {
                return EWEBTools.CancelEvent(e);
            }

            _InitMenu();
            if (_IsOpened) {
                EWEBMenu.Hide();
            }
            EWEBSelection.Save(true);

            var s_Menu = "";

            s_Menu += _GetFormatMenuRow("Cut");
            s_Menu += _GetFormatMenuRow("Copy");
            //s_Menu += _GetFormatMenuRow("Paste");
            s_Menu += _GetStandardMenuRow("", "Paste");
            s_Menu += _GetFormatMenuRow("Delete");
            s_Menu += _GetFormatMenuRow("SelectAll");
            s_Menu += _MenuHr;

            if (EWEBTable.IsCursorInCell()) {
                s_Menu += _GetTableMenuRow("TableProp");
                s_Menu += _GetTableMenuRow("TableCell");
                s_Menu += _MenuHr;
            }

            if (_IsControlSelected("TABLE")) {
                s_Menu += _GetTableMenuRow("TableProp");
                s_Menu += _MenuHr;
            }

            if (_IsControlSelected("IMG")) {
                var s_FakeTag = EWEBFake.GetTag();
                if (!s_FakeTag) {
                    s_Menu += _GetStandardMenuRow("", "Image", "CMenuImg");
                    if (config.TitleImage) {
                        s_Menu += _GetStandardMenuRow("", "TitleImage");
                    }
                    s_Menu += _MenuHr;
                    s_Menu += _GetStandardMenuRow("", "zIndexForward");
                    s_Menu += _GetStandardMenuRow("", "zIndexBackward");
                    s_Menu += _MenuHr;
                }
                if (s_FakeTag == "flash") {
                    s_Menu += _GetStandardMenuRow("", "Flash", "CMenuFlash");
                    s_Menu += _MenuHr;
                }

                if (s_FakeTag == "mediaplayer6" || s_FakeTag == "mediaplayer7" || s_FakeTag == "realplayer" || s_FakeTag == "quicktime" || s_FakeTag == "flv" || s_FakeTag == "vlc" || s_FakeTag == "video" || s_FakeTag == "audio") {
                    s_Menu += _GetStandardMenuRow("", "Media", "CMenuMedia");
                    s_Menu += _MenuHr;
                }

            }

            if (_IsControlSelected("HR")) {
                s_Menu += _GetStandardMenuRow("", "HorizontalRule", "CMenuHr");
                s_Menu += _MenuHr;
            }


            if (_IsParagraphRelativeSelection()) {
                s_Menu += _GetStandardMenuRow("", "ParagraphAttr", "CMenuParagraph");
                s_Menu += _MenuHr;
            }

            s_Menu += _GetStandardMenuRow("", "FindReplace");

            //_Document.body.innerHTML = _Menu1 + s_Menu + _Menu2;
            _MainNode.innerHTML = _Menu1 + s_Menu + _Menu2;
            EWEBTools.DisableSelection(_MainNode);

            if (_Popup) {
                _Popup.show(0, 0, 0, 0, document.body);

                e = eWebEditor.event;

                _Pos.x = e.clientX;
                _Pos.y = e.clientY;
                _Pos.ew = 0;
                _Pos.rel = EWEB.EditorDocument.documentElement;
                EWEBMenu.Show();

            } else {

                EWEBTools.CancelEvent(e);

                _Pos.x = e.pageX;
                _Pos.y = e.pageY;

                var el = EWEB.EditorDocument;
                var oPos = EWEBTools.GetDocumentPosition(_TopWindow, (EWEBTools.IsStrictMode(el) ? el.documentElement : el.body));
                _Pos.x += oPos.x;
                _Pos.y += oPos.y;


                _Pos.ew = 0;
                _IFrame.contentWindow.focus();

                window.setTimeout("EWEBMenu.Show()", 1);
            }


            return false;
        },

        Hide: function () {
            if (_Popup) {
                _Popup.hide();
            } else {
                if (!_IsOpened) {
                    return;
                }
                _IFrame.style.width = _IFrame.style.height = '0px';
                _IsOpened = false;
                EWEBSelection.Restore(true);
                EWEBSelection.Release();
            }
        }


    };

})();


function _EWEBMenu_Win_OnFocus(e) {


};

function _EWEBMenu_Win_OnBlur(e) {
    EWEBMenu.Hide();
};


//////////////////////////////////////////////////////////


var EWEBDialog = (function () {
    var _TopDialog;
    var _BaseZIndex = 9999789;
    var _Cover;
    var _Blocker;

    var _TopWindow;
    var _TopDocument;

    var _GetZIndex = function () {
        return ++_BaseZIndex;
    };

    var _ResizeHandler = function () {
        if (!_Cover) {
            return;
        }

        var o_relElement = EWEBTools.IsStrictMode(_TopDocument) ? _TopDocument.documentElement : _TopDocument.body;

        EWEBTools.SetElementStyles(_Cover,
            {
                'width': Math.max(o_relElement.scrollWidth,
                    o_relElement.clientWidth,
                    _TopDocument.scrollWidth || 0) - 1 + 'px',
                'height': Math.max(o_relElement.scrollHeight,
                    o_relElement.clientHeight,
                    _TopDocument.scrollHeight || 0) - 1 + 'px'
            });
    };

    var _ToTop = function (el) {
        EWEBTools.SetElementStyles(el,
            {
                'zIndex': _GetZIndex()
            });
    };

    var _DisplayMainCover = function () {
        if (!_TopWindow) {
            _TopWindow = EWEBTools.GetTopWindow();
            _TopDocument = _TopWindow.document;
        }

        _Cover = _TopDocument.createElement("div");
        EWEBTools.ResetStyles(_Cover);
        EWEBTools.SetElementStyles(_Cover,
            {
                "position": "absolute",
                "zIndex": _GetZIndex(),
                "top": "0px",
                "left": "0px",
                "backgroundColor": "#ffffff"
            });
        EWEBTools.SetOpacity(_Cover, 0.50);

        if (EWEBBrowser.IsIE && !EWEBBrowser.IsIE7P) {
            _Blocker = _TopDocument.createElement("iframe");
            EWEBTools.ResetStyles(_Blocker);
            _Blocker.hideFocus = true;
            _Blocker.frameBorder = 0;
            _Blocker.src = EWEBTools.GetVoidUrl();
            EWEBTools.SetElementStyles(_Blocker,
                {
                    "width": "100%",
                    "height": "100%",
                    "position": "absolute",
                    "left": "0px",
                    "top": "0px",
                    "filter": "progid:DXImageTransform.Microsoft.Alpha(opacity=0)"
                });
            _Cover.appendChild(_Blocker);
        }

        EWEBTools.AddEventListener(_TopWindow, "resize", _ResizeHandler);
        _ResizeHandler();

        _TopDocument.body.appendChild(_Cover);
    };


    var _HideMainCover = function () {
        EWEBTools.RemoveNode(_Cover);
    };


    return {

        OpenDialog: function (s_Page, b_Hide, o_CommandValue) {
            EWEBSelection.Save(true);

            if (!_TopDialog) {
                _DisplayMainCover();
            } else {
                _ToTop(_Cover);
            }

            var dialogInfo = {
                TopWindow: _TopWindow,
                EditorWindow: window,
                Page: s_Page,
                Hide: b_Hide,
                CV: o_CommandValue
            }

            var n_Width = 160;
            var n_Height = 100;

            var dialog = _TopDocument.createElement('iframe');
            EWEBTools.ResetStyles(dialog);

            dialog.frameBorder = 0;
            dialog.allowTransparency = true;
            var useAbsolutePosition = EWEBBrowser.IsIE && (!EWEBBrowser.IsIE7P || !EWEBTools.IsStrictMode(_TopWindow.document));

            EWEBTools.SetElementStyles(dialog,
                {
                    'position': (useAbsolutePosition) ? 'absolute' : 'fixed',
                    'width': n_Width + 'px',
                    'height': n_Height + 'px',
                    'zIndex': _GetZIndex()
                });

            if (b_Hide) {
                EWEBTools.SetElementStyles(dialog,
                    {
                        'top': '-10000px',
                        'left': '-10000px'
                    });
            } else {
                this.CenterDialog(dialog, n_Width, n_Height);
            }
            dialog.src = EWEB.SitePath + EWEB.RootPath + "/dialog/dialog.htm";

            dialog._DialogArguments = dialogInfo;
            _TopDocument.body.appendChild(dialog);
            dialog._ParentDialog = _TopDialog;

            _TopDialog = dialog;
        },

        OnDialogClose: function (o_DialogWindow, b_Ok, o_DoAfterClose) {
            EWEBActiveX.SetIsRun(false);
            var dialog = o_DialogWindow.frameElement;
            EWEBTools.RemoveNode(dialog);

            if (dialog._ParentDialog) {
                _TopDialog = dialog._ParentDialog;
                _ToTop(_TopDialog);
            } else {
                _HideMainCover();
                setTimeout(function () {
                    _TopDialog = null;
                }, 0);
                EWEBSelection.Release();

                if (EWEB.CurrMode != "EDIT") {
                    EWEB.Focus();
                }

                if (b_Ok) {
                    EWEBHistory.DoChange();
                    EWEBHistory.Save();
                }

                if (o_DoAfterClose) {
                    if (o_DoAfterClose.flag) {
                        EWEBTools.SetTimeout(_FireEvent, 200, null, [o_DoAfterClose]);
                    }
                }
                EWEBTools.SetTimeout(_FireEvent, 200, null, [{flag: "DialogClose", ok: b_Ok}]);

                if (dialog._DialogArguments) {
                    if (dialog._DialogArguments.CV && dialog._DialogArguments.CV["callback"] && typeof(dialog._DialogArguments.CV["callback"]) == "function") {
                        EWEBTools.SetTimeout(dialog._DialogArguments.CV["callback"], 200, null, [b_Ok]);
                    }
                }

                if (b_Ok) {
                    _FireChange();
                }
            }
        },


        CenterDialog: function (dialog, n_Width, n_Height) {
            if (!n_Width) {
                n_Width = parseInt(dialog.style.width, 10);
            }
            if (!n_Height) {
                n_Height = parseInt(dialog.style.height, 10);
            }

            var viewSize = EWEBTools.GetViewPaneSize(_TopWindow);
            var scrollPosition = {'X': 0, 'Y': 0};
            var b_UseAbsolutePosition = EWEBBrowser.IsIE && (!EWEBBrowser.IsIE7P || !EWEBTools.IsStrictMode(_TopWindow.document));
            if (b_UseAbsolutePosition) {
                scrollPosition = EWEBTools.GetScrollPosition(_TopWindow);
            }
            var n_Top = Math.max(scrollPosition.Y + (viewSize.Height - n_Height - 20) / 2, 0);
            var n_Left = Math.max(scrollPosition.X + (viewSize.Width - n_Width - 20) / 2, 0);

            EWEBTools.SetElementStyles(dialog,
                {
                    'top': n_Top + 'px',
                    'left': n_Left + 'px'
                });

        },

        GetCover: function () {
            return _Cover;
        },

        GetTopDialog: function () {
            return _TopDialog;
        }


    };


})();


//////////////////////////////////////////////////////////


var EWEBToolbar = (function () {

    var _ExpandIndex = 1;
    var _ExpandState = true;

    var _InitBtn = function (btn) {
        btn.onmouseover = _BtnMouseOver;
        btn.onmouseout = _BtnMouseOut;
        btn.onmousedown = _BtnMouseDown;
        btn.onmouseup = _BtnMouseUp;
        btn.ondragstart = EWEBTools.CancelEvent;
        btn.onselectstart = EWEBTools.CancelEvent;
        btn.onselect = EWEBTools.CancelEvent;
        btn.YINITIALIZED = true;
        return true;
    };

    var _GetBtnEventElement = function (e) {
        if (!e) {
            e = window.event;
        }
        var el = e.srcElement || e.target;
        if (el.tagName == "IMG") {
            el = el.parentNode;
        }
        if (el.className == "TB_Btn_Image") {
            el = el.parentNode;
        }
        return el;
    };


    var _BtnMouseOver = function (e) {
        var el = _GetBtnEventElement(e);
        el.className = "TB_Btn_Over";
    };

    var _BtnMouseOut = function (e) {
        var el = _GetBtnEventElement(e);
        if (el.QCV) {
            el.className = "TB_Btn_Down";
        } else {
            el.className = "TB_Btn";
        }
    };

    var _BtnMouseDown = function (e) {
        var el = _GetBtnEventElement(e);
        el.className = "TB_Btn_Down";
    };

    var _BtnMouseUp = function (e) {
        var el = _GetBtnEventElement(e);
        if (el.className = "TB_Btn_Down") {
            el.className = "TB_Btn_Over";
        } else {
            if (el.QCV) {
                el.className = "TB_Btn_Down";
            } else {
                el.className = "TB_Btn";
            }
        }
    };


    var _CacheBtnGroup;
    var _InitCacheBtnGroup = function () {
        if (_CacheBtnGroup) {
            return;
        }

        var a_CmdGroup = {
            normal: ["Bold", "Italic", "UnderLine", "StrikeThrough", "SuperScript", "SubScript", "JustifyLeft", "JustifyCenter", "JustifyRight", "JustifyFull", "TextIndent"],
            mode: ["ModeCode", "ModeEdit", "ModeView", "ModeText"],
            other: ["ShowBlocks", "ShowBorders", "Maximize", "FormatBrush", "ExpandToolbar"]
        };

        var els = $("eWebEditor_Toolbar").getElementsByTagName("DIV");
        _CacheBtnGroup = new Object;
        for (s_GroupName in a_CmdGroup) {
            var a_Cmd = a_CmdGroup[s_GroupName];
            var o_CacheBtn = new Object;
            for (var i = 0; i < a_Cmd.length; i++) {
                var s_Cmd = a_Cmd[i];
                o_CacheBtn[s_Cmd] = new Array();
                for (var j = 0; j < els.length; j++) {
                    var el = els[j];
                    if (el.getAttribute("name") == "TB_Name_" + s_Cmd) {
                        o_CacheBtn[s_Cmd][o_CacheBtn[s_Cmd].length] = el;
                    }
                }
            }
            _CacheBtnGroup[s_GroupName] = o_CacheBtn;
        }
    };


    var _CheckTBStatusBtns = function (b_IsControl) {
        _InitCacheBtnGroup();

        var s_CurJustify = "";
        if (EWEBBrowser.IsIE && EWEBSelection.GetType() != "Control") {
            var p = EWEBSelection.GetParentElementByTags(["P", "DIV", "TD", "TH"]);
            if (p) {
                s_CurJustify = p.style.textAlign;
                if (!s_CurJustify) {
                    s_CurJustify = p.getAttribute("align");
                }
            }
        }

        var o_BtnGroup = _CacheBtnGroup["normal"];
        for (s_Cmd in o_BtnGroup) {
            var v;
            if (s_Cmd == "TextIndent") {
                v = EWEBCommandParagraph._QueryCommandValue("textindent");
            } else if (s_CurJustify && s_Cmd.substring(0, 7) == "Justify") {
                var s1 = s_Cmd.substr(7).toLowerCase();
                if (s1 == "full") {
                    s1 = "justify";
                }
                if (s_CurJustify.toLowerCase() == s1) {
                    v = s_CurJustify;
                } else {
                    v = "";
                }
            } else {
                v = EWEB.EditorDocument.queryCommandState(s_Cmd);
            }
            var els = o_BtnGroup[s_Cmd];
            for (var j = 0; j < els.length; j++) {
                var el = els[j];
                el.QCV = v;
                if (b_IsControl) {
                    el.className = "TB_Btn";
                } else {
                    if (v) {
                        el.className = "TB_Btn_Down";
                    } else {
                        el.className = "TB_Btn";
                    }
                }
            }
        }
    };


    var _RefreshModeBtnStatus = function () {
        _InitCacheBtnGroup();

        var o_BtnGroup = _CacheBtnGroup["mode"];
        for (s_Cmd in o_BtnGroup) {
            var els = o_BtnGroup[s_Cmd];
            for (var j = 0; j < els.length; j++) {
                var el = els[j];
                var s_Name = el.getAttribute("name");
                s_Name = s_Name.substr(s_Name.length - 4).toUpperCase();
                if (s_Name == EWEB.CurrMode) {
                    el.QCV = "on";
                    el.className = "TB_Btn_Down";
                } else {
                    el.QCV = "";
                    el.className = "TB_Btn";
                }
            }
        }
    };


    var _CheckTBStatusDrops = function (b_IsControl) {
        var cmd, v;

        cmd = "FontName";
        v = EWEBSelection._QueryFontName();
        _CheckTBStatusDrop(cmd, v);

        cmd = "FontSize";
        v = EWEBSelection.QueryFontSize();
        _CheckTBStatusDrop(cmd, v);
    };

    var _CheckTBStatusDrop = function (cmd, v) {
        var els = document.getElementsByName("TB_Name_" + cmd);
        for (var i = 0; i < els.length; i++) {
            var el = els[i];
            if (v) {
                v = v.toLowerCase();
                v = v.replace(/[\'\"]/gi, '');
                var b = false;
                for (var j = 0; j < el.options.length; j++) {
                    var s_OptValue = el.options[j].value.toLowerCase();
                    if (s_OptValue == v || s_OptValue.split(',').IndexOf(v) >= 0 || (cmd == "FontSize" && _CompareFontSize(s_OptValue, v))) {
                        el.selectedIndex = j;
                        b = true;
                        break;
                    }
                }
                if (!b && v != "ewebeditor_temp_fontname") {
                    el.options[el.options.length] = new Option(v, v);
                    el.selectedIndex = el.options.length - 1;
                }
            } else {
                el.selectedIndex = 0;
            }
        }
    };

    var _CompareFontSize = function (s1, s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();
        if (s1 == s2) {
            return true;
        }

        if (s1.EndsWith("pt") && s2.EndsWith("px")) {
            var n_DotLen = 0;
            s1 = parseFloat(s1) + "";
            var n = s1.indexOf(".");
            if (n >= 0) {
                var s = s1.substr(n + 1);
                n_DotLen = s.length;
            }
            if (n_DotLen > 0) {
                if (s1 == Math.round(parseFloat(s2) * 3 / 4 * Math.pow(10, n_DotLen) + 0.01) / Math.pow(10, n_DotLen)) {
                    return true;
                }
            } else {
                if (s1 == parseInt(parseFloat(s2) * 3 / 4 + 0.01)) {
                    return true;
                }
            }
        }
        return false;
    };


    return {

        InitTB: function () {
            var i, els, el, p;

            p = $("eWebEditor_Toolbar");
            els = p.getElementsByTagName("div");

            for (i = 0; i < els.length; i++) {
                el = els[i];
                if (el.className == "TB_Btn") {
                    if (el.YINITIALIZED == null) {
                        if (!_InitBtn(el)) {
                            alert("Problem initializing:" + el.id);
                            return false;
                        }
                    }
                }
            }
            return true;
        },


        CheckTBStatus: function () {
            if (EWEB.CurrMode != "EDIT") {
                return;
            }

            var b = (EWEBSelection.GetType() == "Control") ? true : false;
            _CheckTBStatusBtns(b);
            _CheckTBStatusDrops(b);
        },

        RefreshModeBtnStatus: function () {
            _RefreshModeBtnStatus();
        },

        GetBtns: function (s_GroupName, s_BtnName) {
            _InitCacheBtnGroup();
            return _CacheBtnGroup[s_GroupName][s_BtnName];
        },

        SetExpandIndex: function (n_Index) {
            _ExpandIndex = n_Index;
            _ExpandState = false;
        },

        Expand: function () {
            if (EWEB.CurrMode != "EDIT") {
                return;
            }
            if (config.Toolbars.length <= 1) {
                return;
            }

            _ExpandState = (!_ExpandState);
            var s_Display = ((_ExpandState) ? "" : "none");
            for (var i = _ExpandIndex; i < config.Toolbars.length; i++) {
                $("eWebEditor_Toolbar_Edit_TR" + i).style.display = s_Display;
            }
            if (s_Display == "none" && EWEBBrowser.IsIE11P) {
                $("eWebEditor_ToolarTREdit").style.display = s_Display;
                window.setTimeout(function () {
                    $("eWebEditor_ToolarTREdit").style.display = "";
                }, 100);
            }
            _StandardModeResize();

            this.SetBtnsStatus("other", "ExpandToolbar", _ExpandState);
        },

        SetBtnsStatus: function (s_GroupName, s_BtnName, b_IsOn, n_OffIcoIndex, n_OnIcoIndex) {
            var a_Btn = this.GetBtns(s_GroupName, s_BtnName);
            for (var j = 0; j < a_Btn.length; j++) {
                var el = a_Btn[j];
                var n_IcoIndex;
                if (b_IsOn) {
                    el.QCV = "on";
                    el.className = "TB_Btn_Down";
                    n_IcoIndex = n_OnIcoIndex;
                } else {
                    el.QCV = null;
                    el.className = "TB_Btn";
                    n_IcoIndex = n_OffIcoIndex;
                }

                if (n_IcoIndex) {
                    var o_Img = el.getElementsByTagName("IMG");
                    if (o_Img) {
                        var n_Top = 16 - n_IcoIndex * 16;
                        if (EWEBBrowser.IsIE) {
                            o_Img[0].style.top = n_Top + "px";
                        } else {
                            o_Img[0].style.backgroundPosition = "0px " + n_Top + "px";
                        }
                    }
                }
            }
        }


    };

})();


//////////////////////////////////////////////////////////


var EWEBTable = (function () {
    var _SelectedTD;
    var _SelectedTR;
    var _SelectedTBODY;
    var _SelectedTable;

    var _InitGridCell = function (o_Table) {
        var n_RowCount = o_Table.rows.length;
        var a_Cell = new Array();
        var a_Grid = new Array();
        for (var i = 0; i < n_RowCount; i++) {
            a_Grid[i] = new Array();
            a_Cell[i] = new Array();
        }
        var n_MaxColSpans = 0;

        for (var i = 0; i < n_RowCount; i++) {
            var o_Row = o_Table.rows[i];
            var n_CellCount = o_Row.cells.length;
            var n_ColIndex = 0;

            for (var j = 0; j < n_CellCount; j++) {
                var o_Cell = o_Row.cells[j];
                var o_Now = new Object();
                o_Now._CellObj = o_Cell;
                o_Now._IsFirstRow = false;
                o_Now._IsFirstCol = false;
                o_Now._IsLastCol = false;

                o_Now._RowFrom = i;
                o_Now._RowTo = i + (o_Cell.rowSpan - 1);

                if (i == 0) {
                    o_Now._IsFirstRow = true;
                    if (j == 0) {
                        o_Now._IsFirstCol = true;
                    }
                    if (j == (n_CellCount - 1)) {
                        o_Now._IsLastCol = true;
                    }

                    n_MaxColSpans += o_Cell.colSpan;

                    o_Now._ColFrom = n_ColIndex;
                    o_Now._ColTo = n_ColIndex + (o_Cell.colSpan - 1);
                    n_ColIndex = n_ColIndex + o_Cell.colSpan;
                } else {
                    n_ColIndex = 0;
                    for (var k = 0; k < n_MaxColSpans; k++) {
                        if (!a_Grid[i][k]) {
                            n_ColIndex = k;
                            break;
                        }
                    }
                    o_Now._ColFrom = n_ColIndex;
                    o_Now._ColTo = n_ColIndex + (o_Cell.colSpan - 1);

                    if (o_Now._ColFrom == 0) {
                        o_Now._IsFirstCol = true;
                    }
                    if (o_Now._ColTo == (n_MaxColSpans - 1)) {
                        o_Now._IsLastCol = true;
                    }
                }

                for (var k = o_Now._ColFrom; k <= o_Now._ColTo; k++) {
                    for (var m = o_Now._RowFrom; m <= o_Now._RowTo; m++) {
                        a_Grid[m][k] = {_row: i, _col: j};
                    }
                }

                o_Now._IsLastRow = false;
                o_Now._IsOddRow = false;
                o_Now._IsEvenRow = false;
                if (o_Now._RowTo == (n_RowCount - 1)) {
                    o_Now._IsLastRow = true;
                }
                var m = i % 2;
                if (m == 0) {
                    o_Now._IsOddRow = true;
                }
                if (m == 1) {
                    o_Now._IsEvenRow = true;
                }

                a_Cell[i][j] = o_Now;
            }
        }

        return {_Grid: a_Grid, _Cell: a_Cell};
    };


    return {

        IsCursorInCell: function () {
            var el = EWEBSelection.GetParentElementByTags(["TD", "TH"])
            if (el) {
                _SelectedTD = el;
                _SelectedTR = _SelectedTD.parentNode;
                _SelectedTBODY = _SelectedTR.parentNode;
                _SelectedTable = _SelectedTBODY.parentNode;
                return true;
            } else {
                return false;
            }
        },

        IsTableSelected: function () {
            if (EWEBSelection.GetType() == "Control") {
                var el = EWEBSelection.GetSelectedElement();
                if (el.tagName.toUpperCase() == "TABLE") {
                    _SelectedTable = el;
                    return true;
                }
            }
            return false;
        },

        TableInsert: function () {
            if (!EWEBTable.IsTableSelected()) {
                showDialog('table.htm', true);
            }
        },

        TableProp: function () {
            if (EWEBTable.IsTableSelected() || EWEBTable.IsCursorInCell()) {
                showDialog('table.htm?action=modify', true);
            }
        },

        TableTemplate: function () {
            var o_Tables = EWEB.EditorDocument.getElementsByTagName("TABLE");
            if (o_Tables.length <= 0) {
                alert(lang["MsgNoTable"]);
                return;
            }

            showDialog('tabletemplate.htm', true);
        },

        CellProp: function () {
            if (EWEBTable.IsCursorInCell()) {
                showDialog('tablecell.htm', true);
            }
        },

        CellSplit: function () {
            if (EWEBTable.IsCursorInCell()) {
                showDialog('tablecellsplit.htm', true);
            }
        },

        RowProp: function () {
            if (EWEBTable.IsCursorInCell()) {
                showDialog('tablecell.htm?action=row', true);
            }
        },

        RowInsertAbove: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            var n_CurrRow = _SelectedTR.rowIndex;
            var o_NewTR = _SelectedTable.insertRow(n_CurrRow);
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;
                var o_Cell = a_Cell[n_RealRow][n_RealCol]._CellObj;
                var n_RowFrom = a_Cell[n_RealRow][n_RealCol]._RowFrom;
                var n_RowTo = a_Cell[n_RealRow][n_RealCol]._RowTo;
                var n_ColFrom = a_Cell[n_RealRow][n_RealCol]._ColFrom;
                var n_ColTo = a_Cell[n_RealRow][n_RealCol]._ColTo;

                if (n_RowFrom == n_CurrRow) {
                    var o_NewTD = o_NewTR.insertCell(-1);
                    o_NewTD.innerHTML = "&nbsp;";
                    var n_ColSpan = n_ColTo - n_ColFrom + 1;
                    if (n_ColSpan > 1) {
                        o_NewTD.colSpan = n_ColSpan;
                        i = i + (n_ColSpan - 1);
                    }
                    o_NewTD.style.cssText = o_Cell.style.cssText;
                } else if (n_RowFrom < n_CurrRow) {
                    o_Cell.rowSpan = (n_RowTo - n_RowFrom + 1) + 1;
                    var n_ColSpan = n_ColTo - n_ColFrom + 1;
                    if (n_ColSpan > 1) {
                        i = i + (n_ColSpan - 1);
                    }
                } else {
                    return;
                }
            }
            _FireChange();
        },

        RowInsertBelow: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            var n_CurrRow = _SelectedTR.rowIndex;
            n_CurrRow = n_CurrRow + _SelectedTD.rowSpan - 1;
            var o_NewTR = _SelectedTable.insertRow(n_CurrRow + 1);
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;
                var o_Cell = a_Cell[n_RealRow][n_RealCol]._CellObj;
                var n_RowFrom = a_Cell[n_RealRow][n_RealCol]._RowFrom;
                var n_RowTo = a_Cell[n_RealRow][n_RealCol]._RowTo;
                var n_ColFrom = a_Cell[n_RealRow][n_RealCol]._ColFrom;
                var n_ColTo = a_Cell[n_RealRow][n_RealCol]._ColTo;

                if (n_RowTo == n_CurrRow) {
                    var o_NewTD = o_NewTR.insertCell(-1);
                    o_NewTD.innerHTML = "&nbsp;";
                    var n_ColSpan = n_ColTo - n_ColFrom + 1;
                    if (n_ColSpan > 1) {
                        o_NewTD.colSpan = n_ColSpan;
                        i = i + (n_ColSpan - 1);
                    }
                    o_NewTD.style.cssText = o_Cell.style.cssText;
                } else if (n_RowTo > n_CurrRow) {
                    o_Cell.rowSpan = (n_RowTo - n_RowFrom + 1) + 1;
                    var n_ColSpan = n_ColTo - n_ColFrom + 1;
                    if (n_ColSpan > 1) {
                        i = i + (n_ColSpan - 1);
                    }
                } else {
                    return;
                }
            }
            _FireChange();
        },

        RowMerge: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;
            var o_CurrCellData = a_Cell[n_CurrRow][n_CurrCol];
            if (o_CurrCellData._IsLastRow) {
                return;
            }

            var n_GridX = 0;
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;

                if (n_RealRow == n_CurrRow && n_RealCol == n_CurrCol) {
                    n_GridX = i;
                    break;
                }
            }
            var n_CurrCellRowSpan = (o_CurrCellData._RowTo - o_CurrCellData._RowFrom) + 1;

            var n_NextCellRow = a_Grid[n_CurrRow + n_CurrCellRowSpan][n_GridX]._row;
            var n_NextCellCol = a_Grid[n_CurrRow + n_CurrCellRowSpan][n_GridX]._col;
            var o_NextCellData = a_Cell[n_NextCellRow][n_NextCellCol];

            if (o_CurrCellData._ColFrom == o_NextCellData._ColFrom && o_CurrCellData._ColTo == o_NextCellData._ColTo) {
                var o_NextCell = o_NextCellData._CellObj;
                _SelectedTD.innerHTML = _SelectedTD.innerHTML + o_NextCell.innerHTML;
                _SelectedTD.rowSpan = _SelectedTD.rowSpan + o_NextCell.rowSpan;
                _SelectedTable.rows[n_NextCellRow].deleteCell(n_NextCellCol);
            }
            _FireChange();
        },

        RowSplit: function (nRows) {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }
            if (nRows < 2) {
                return;
            }

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;
            var n_RowSpan = _SelectedTD.rowSpan;
            var n_ColSpan = _SelectedTD.colSpan;

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            var n_GridX = 0;
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;

                if (n_RealRow == n_CurrRow && n_RealCol == n_CurrCol) {
                    n_GridX = i;
                    break;
                }
            }

            var n_Row1 = 0;
            var n_Row2 = 0;
            if (n_RowSpan > 1) {
                if (nRows > n_RowSpan) {
                    n_Row1 = n_RowSpan;
                    n_Row2 = nRows - n_RowSpan;
                } else {
                    n_Row1 = nRows;
                }
            } else {
                n_Row2 = nRows - 1;
            }

            if (n_Row1 > 0) {
                n_Row1--;
                var n_Start = n_RowSpan - 1;
                var n_End = n_Start - n_Row1;
                for (var i = n_Start; i > n_End; i--) {
                    var n_NewCellIndex = 0;
                    var n_NewRow = n_CurrRow + i;
                    for (k = 0; k < n_GridX; k++) {
                        var n_LeftRow = a_Grid[n_NewRow][k]._row;
                        var n_LeftCol = a_Grid[n_NewRow][k]._col;
                        var o_LeftData = a_Cell[n_LeftRow][n_LeftCol];
                        var n_LeftColSpan = o_LeftData._ColTo - o_LeftData._ColFrom + 1;
                        if (a_Grid[n_NewRow][k]._row == n_NewRow) {
                            n_NewCellIndex++;
                            k = k + n_LeftColSpan - 1;
                        }
                    }

                    var o_NewTD = _SelectedTable.rows[n_NewRow].insertCell(n_NewCellIndex);
                    o_NewTD.innerHTML = "&nbsp;";
                    if (n_ColSpan > 1) {
                        o_NewTD.colSpan = n_ColSpan;
                    }
                    o_NewTD.style.cssText = _SelectedTD.style.cssText;
                    _SelectedTD.rowSpan = _SelectedTD.rowSpan - 1;
                }
            }

            if (n_Row2 > 0) {
                for (var j = 0; j < a_Grid[n_CurrRow].length; j++) {
                    if (j >= n_GridX && j < (n_GridX + n_ColSpan)) {
                        continue;
                    }
                    var n_RealRow = a_Grid[n_CurrRow][j]._row;
                    var n_RealCol = a_Grid[n_CurrRow][j]._col;
                    var o_RealData = a_Cell[n_RealRow][n_RealCol];
                    var n_RealColSpan = o_RealData._ColTo - o_RealData._ColFrom + 1;
                    var o_RealCell = o_RealData._CellObj;

                    o_RealCell.rowSpan = o_RealCell.rowSpan + n_Row2;
                    j = j + n_RealColSpan - 1;
                }

                for (var i = 1; i <= n_Row2; i++) {
                    var o_NewTR = _SelectedTable.insertRow(n_CurrRow + 1);
                    var o_NewTD = o_NewTR.insertCell(-1);
                    o_NewTD.innerHTML = "&nbsp;";
                    if (n_ColSpan > 1) {
                        o_NewTD.colSpan = n_ColSpan;
                    }
                    o_NewTD.style.cssText = _SelectedTD.style.cssText;
                }
            }
            _FireChange();
        },

        RowDelete: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;
            var n_RowSpan = _SelectedTD.rowSpan;
            var n_EndRow = n_CurrRow + (n_RowSpan - 1);

            var n_NextInsertCount = 0;

            for (var i = n_EndRow; i >= n_CurrRow; i--) {
                for (var j = 0; j < a_Grid[i].length; j++) {
                    if (i != n_CurrRow && i != n_EndRow) {
                        continue;
                    }
                    var n_RealRow = a_Grid[i][j]._row;
                    var n_RealCol = a_Grid[i][j]._col;
                    var o_Data = a_Cell[n_RealRow][n_RealCol];
                    var o_Cell = o_Data._CellObj;
                    var n_RowFrom = o_Data._RowFrom;
                    var n_RowTo = o_Data._RowTo;
                    var n_ColFrom = o_Data._ColFrom;
                    var n_ColTo = o_Data._ColTo;

                    if (i == n_CurrRow) {
                        if (n_RowFrom < i) {
                            o_Cell.rowSpan = (n_RowTo - n_RowFrom + 1) - (n_RowTo - i + 1);
                            j = j + (n_ColTo - n_ColFrom);
                        }
                    }
                    if (i == n_EndRow) {
                        if (n_RowTo > i) {
                            var n_NextInsertIndex = 0;
                            var n_NextRow = n_EndRow + 1;
                            for (var k = 0; k < j; k++) {
                                var n_NextRealRow = a_Grid[n_NextRow][k]._row;
                                var n_NextRealCol = a_Grid[n_NextRow][k]._col;
                                var o_NextData = a_Cell[n_NextRealRow][n_NextRealCol];
                                var o_NextCell = o_NextData._CellObj;
                                var n_NextRowFrom = o_NextData._RowFrom;
                                var n_NextRowTo = o_NextData._RowTo;
                                var n_NextColFrom = o_NextData._ColFrom;
                                var n_NextColTo = o_NextData._ColTo;
                                if (n_NextRealRow == n_NextRow) {
                                    n_NextInsertIndex++;
                                }
                                k = k + (n_NextColTo - n_NextColFrom);
                            }
                            n_NextInsertIndex = n_NextInsertIndex + n_NextInsertCount;
                            n_NextInsertCount++;
                            var o_NewTD = _SelectedTable.rows[n_NextRow].insertCell(n_NextInsertIndex);
                            o_NewTD.innerHTML = "&nbsp;";
                            var n_NewRowSpan = (n_RowTo - n_RowFrom + 1) - (n_EndRow - n_RowFrom + 1);
                            if (n_NewRowSpan > 1) {
                                o_NewTD.rowSpan = n_NewRowSpan;
                            }
                            var n_NewColSpan = (n_ColTo - n_ColFrom + 1);
                            if (n_NewColSpan > 1) {
                                o_NewTD.colSpan = n_NewColSpan;
                            }
                            o_NewTD.style.cssText = o_Cell.style.cssText;

                            j = j + (n_ColTo - n_ColFrom);
                        }
                    }
                }

                _SelectedTable.deleteRow(i);
            }

            if (_SelectedTable.rows.length == 0) {
                EWEBTools.RemoveNode(_SelectedTable);
            }
            _FireChange();
        },

        ColInsertLeft: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;

            var n_GridX = 0;
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;

                if (n_RealRow == n_CurrRow && n_RealCol == n_CurrCol) {
                    n_GridX = i;
                    break;
                }
            }

            for (var i = 0; i < a_Grid.length; i++) {
                var n_RealRow = a_Grid[i][n_GridX]._row;
                var n_RealCol = a_Grid[i][n_GridX]._col;
                var o_Data = a_Cell[n_RealRow][n_RealCol];
                var o_Cell = o_Data._CellObj;
                var n_RowFrom = o_Data._RowFrom;
                var n_RowTo = o_Data._RowTo;
                var n_ColFrom = o_Data._ColFrom;
                var n_ColTo = o_Data._ColTo;

                if (n_ColFrom == n_GridX) {
                    var o_NewTD = _SelectedTable.rows[i].insertCell(n_RealCol);
                    o_NewTD.innerHTML = "&nbsp;";
                    var n_RowSpan = n_RowTo - n_RowFrom + 1;
                    if (n_RowSpan > 1) {
                        o_NewTD.rowSpan = n_RowSpan;
                        i = i + (n_RowSpan - 1);
                    }
                    o_NewTD.style.cssText = o_Cell.style.cssText;
                } else if (n_ColFrom < n_GridX) {
                    o_Cell.colSpan = (n_ColTo - n_ColFrom + 1) + 1;
                    var n_RowSpan = n_RowTo - n_RowFrom + 1;
                    if (n_RowSpan > 1) {
                        i = i + (n_RowSpan - 1);
                    }
                } else {
                    alert("error");
                    return;
                }
            }
            _FireChange();
        },

        ColInsertRight: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;

            var n_GridX = 0;
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;

                if (n_RealRow == n_CurrRow && n_RealCol == n_CurrCol) {
                    n_GridX = i;
                    break;
                }
            }
            n_GridX = n_GridX + _SelectedTD.colSpan - 1;

            for (var i = 0; i < a_Grid.length; i++) {
                var n_RealRow = a_Grid[i][n_GridX]._row;
                var n_RealCol = a_Grid[i][n_GridX]._col;
                var o_Data = a_Cell[n_RealRow][n_RealCol];
                var o_Cell = o_Data._CellObj;
                var n_RowFrom = o_Data._RowFrom;
                var n_RowTo = o_Data._RowTo;
                var n_ColFrom = o_Data._ColFrom;
                var n_ColTo = o_Data._ColTo;

                if (n_ColTo == n_GridX) {
                    var o_NewTD = _SelectedTable.rows[i].insertCell(n_RealCol + 1);
                    o_NewTD.innerHTML = "&nbsp;";
                    var n_RowSpan = n_RowTo - n_RowFrom + 1;
                    if (n_RowSpan > 1) {
                        o_NewTD.rowSpan = n_RowSpan;
                        i = i + (n_RowSpan - 1);
                    }
                    o_NewTD.style.cssText = o_Cell.style.cssText;
                } else if (n_ColTo > n_GridX) {
                    o_Cell.colSpan = (n_ColTo - n_ColFrom + 1) + 1;
                    var n_RowSpan = n_RowTo - n_RowFrom + 1;
                    if (n_RowSpan > 1) {
                        i = i + (n_RowSpan - 1);
                    }
                } else {
                    alert("error");
                    return;
                }
            }
            _FireChange();
        },

        ColMerge: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;
            var o_CurrCellData = a_Cell[n_CurrRow][n_CurrCol];
            if (o_CurrCellData._IsLastCol) {
                return;
            }

            var n_GridX = 0;
            for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                var n_RealRow = a_Grid[n_CurrRow][i]._row;
                var n_RealCol = a_Grid[n_CurrRow][i]._col;

                if (n_RealRow == n_CurrRow && n_RealCol == n_CurrCol) {
                    n_GridX = i;
                    break;
                }
            }
            var n_CurrCellColSpan = (o_CurrCellData._ColTo - o_CurrCellData._ColFrom) + 1;

            var n_NextCellRow = a_Grid[n_CurrRow][n_GridX + n_CurrCellColSpan]._row;
            var n_NextCellCol = a_Grid[n_CurrRow][n_GridX + n_CurrCellColSpan]._col;
            var o_NextCellData = a_Cell[n_NextCellRow][n_NextCellCol];

            if (o_CurrCellData._RowFrom == o_NextCellData._RowFrom && o_CurrCellData._RowTo == o_NextCellData._RowTo) {
                var o_NextCell = o_NextCellData._CellObj;
                _SelectedTD.innerHTML = _SelectedTD.innerHTML + o_NextCell.innerHTML;
                _SelectedTD.colSpan = _SelectedTD.colSpan + o_NextCell.colSpan;
                _SelectedTable.rows[n_NextCellRow].deleteCell(n_NextCellCol);
            }
            _FireChange();
        },

        ColDelete: function () {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }

            var a_GridCell = _InitGridCell(_SelectedTable);
            var a_Grid = a_GridCell._Grid;
            var a_Cell = a_GridCell._Cell;

            if (a_Grid[0].length == 1) {
                EWEBTools.RemoveNode(_SelectedTable);
                return;
            }

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;
            var n_ColSpan = _SelectedTD.colSpan;

            var n_StartX = 0;
            for (var j = 0; j < a_Grid[n_CurrRow].length; j++) {
                if (a_Grid[n_CurrRow][j]._row == n_CurrRow && a_Grid[n_CurrRow][j]._col == n_CurrCol) {
                    n_StartX = j;
                    break;
                }
            }

            var n_EndX = n_StartX + (n_ColSpan - 1);
            var a_Done = new Array();
            for (var i = 0; i < a_Grid.length; i++) {
                a_Done[i] = new Array();
            }

            for (var i = 0; i < a_Grid.length; i++) {
                for (var j = n_EndX; j >= n_StartX; j--) {
                    if (a_Done[i][j]) {
                        continue;
                    }
                    var n_RealRow = a_Grid[i][j]._row;
                    var n_RealCol = a_Grid[i][j]._col;
                    var o_Data = a_Cell[n_RealRow][n_RealCol];
                    var o_Cell = o_Data._CellObj;
                    var n_RowFrom = o_Data._RowFrom;
                    var n_RowTo = o_Data._RowTo;
                    var n_ColFrom = o_Data._ColFrom;
                    var n_ColTo = o_Data._ColTo;

                    if (n_ColTo > j) {
                        var o_NewTD = _SelectedTable.rows[i].insertCell(n_RealCol + 1);
                        o_NewTD.innerHTML = "&nbsp;";
                        var n_NewRowSpan = (n_RowTo - n_RowFrom + 1);
                        if (n_NewRowSpan > 1) {
                            o_NewTD.rowSpan = n_NewRowSpan;
                        }
                        var n_NewColSpan = n_ColTo - n_EndX;
                        if (n_NewColSpan > 1) {
                            o_NewTD.colSpan = n_NewColSpan;
                        }
                        o_NewTD.style.cssText = o_Cell.style.cssText;
                    }

                    if (n_ColFrom < n_StartX) {
                        o_Cell.colSpan = n_StartX - n_ColFrom;
                    } else {
                        _SelectedTable.rows[i].deleteCell(n_RealCol);
                    }

                    for (var m = n_RowFrom; m <= n_RowTo; m++) {
                        for (var k = n_ColFrom; k <= n_ColTo; k++) {
                            a_Done[m][k] = true;
                        }
                    }
                }
            }

            _FireChange();
        },

        ColSplit: function (nCols) {
            if (!EWEBTable.IsCursorInCell()) {
                return;
            }
            if (nCols < 2) {
                return;
            }

            var n_CurrRow = _SelectedTR.rowIndex;
            var n_CurrCol = _SelectedTD.cellIndex;
            var n_RowSpan = _SelectedTD.rowSpan;
            var n_ColSpan = _SelectedTD.colSpan;

            var n_Col1 = 0;
            var n_Col2 = 0;
            if (n_ColSpan > 1) {
                if (nCols > n_ColSpan) {
                    n_Col1 = n_ColSpan
                    n_Col2 = nCols - n_ColSpan;
                } else {
                    n_Col1 = nCols;
                }
            } else {
                n_Col2 = nCols - 1;
            }

            if (n_Col1 > 0) {
                var n_NewAddColSpan = 1;
                var n = n_ColSpan / n_Col1;
                if ((n_ColSpan % n_Col1) == 0 && n > 1) {
                    n_NewAddColSpan = n;
                }

                n_Col1--;
                for (var i = 1; i <= n_Col1; i++) {
                    var o_NewTD = _SelectedTable.rows[n_CurrRow].insertCell(n_CurrCol + 1);
                    o_NewTD.innerHTML = "&nbsp;";
                    if (n_RowSpan > 1) {
                        o_NewTD.rowSpan = n_RowSpan;
                    }
                    if (n_NewAddColSpan > 1) {
                        o_NewTD.colSpan = n_NewAddColSpan;
                    }
                    o_NewTD.style.cssText = _SelectedTD.style.cssText;
                    _SelectedTD.colSpan = _SelectedTD.colSpan - n_NewAddColSpan;
                }
            }

            if (n_Col2 > 0) {
                var a_GridCell = _InitGridCell(_SelectedTable);
                var a_Grid = a_GridCell._Grid;
                var a_Cell = a_GridCell._Cell;

                var n_GridX = 0;
                for (var i = 0; i < a_Grid[n_CurrRow].length; i++) {
                    var n_RealRow = a_Grid[n_CurrRow][i]._row;
                    var n_RealCol = a_Grid[n_CurrRow][i]._col;

                    if (n_RealRow == n_CurrRow && n_RealCol == n_CurrCol) {
                        n_GridX = i;
                        break;
                    }
                }

                for (var i = 0; i < a_Grid.length; i++) {
                    if (i >= n_CurrRow && i < (n_CurrRow + n_RowSpan)) {
                        continue;
                    }
                    var n_RealRow = a_Grid[i][n_GridX]._row;
                    var n_RealCol = a_Grid[i][n_GridX]._col;
                    var o_Data = a_Cell[n_RealRow][n_RealCol];
                    var o_Cell = o_Data._CellObj;
                    var n_RowFrom = o_Data._RowFrom;
                    var n_RowTo = o_Data._RowTo;
                    var n_ColFrom = o_Data._ColFrom;
                    var n_ColTo = o_Data._ColTo;

                    o_Cell.colSpan = o_Cell.colSpan + n_Col2;
                    i = i + (n_RowTo - n_RowFrom);
                }

                for (var i = 0; i < n_Col2; i++) {
                    var o_NewTD = _SelectedTable.rows[n_CurrRow].insertCell(n_CurrCol + 1);
                    o_NewTD.innerHTML = "&nbsp;";
                    if (n_RowSpan > 1) {
                        o_NewTD.rowSpan = n_RowSpan;
                    }
                    o_NewTD.style.cssText = _SelectedTD.style.cssText;
                }

            }

            _FireChange();
        }

    };

})();


var EWEBTableResize = (function () {
    var _nTriggerAreaWidth = 4;
    var _nSepWidth = 5;

    var _bOnWidth = false;
    var _bOnHeight = false;
    var _bOnResizing = false;

    var _oSepDivV = null;
    var _nSepDivVLeft = 0;
    var _nSepDivVTop = 0;

    var _oSepDivH = null;
    var _nSepDivHLeft = 0;
    var _nSepDivHTop = 0;

    var _nStartX;
    var _nStartY;

    var _nScreenClientX;
    var _nScreenClientY;
    var _nCellMinX;
    var _nCellMinY;

    var _oOnCell = null;
    var _oOnTable = null;
    var _oOnRow = null;
    var _nOnCellWidth;
    var _nOnTableWidth;
    var _nOnTableHeight;
    var _nOnRowHeight;

    var _aDoWCellObj = new Array();
    var _aDoWCellWidth = new Array();

    var _aDoHCellObj = new Array();
    var _aDoHCellHeight = new Array();

    var _bInit = false;
    var _Init = function () {
        _oSepDivV = $("div_TableResizeSepV");
        _oSepDivV.className = "TableResizeSepV";
        _oSepDivH = $("div_TableResizeSepH");
        _oSepDivH.className = "TableResizeSepH";

        EWEBTools.AddEventListener(document, 'mousemove', EWEBTableResize.PMM);
        _bInit = true;
    };

    var _ResetVar = function () {
        _bOnWidth = false;
        _bOnHeight = false;
        _bOnResizing = false;

        _oOnCell = null;
        _oOnTable = null;
        _oOnRow = null;

        _aDoWCellObj.length = 0;
        _aDoWCellWidth.length = 0;
        _aDoHCellObj.length = 0;
        _aDoHCellHeight.length = 0;

        _oSepDivV.style.display = "none";
        _oSepDivH.style.display = "none";
    };

    var _GetCellWidth = function (o_Cell) {
        var n = o_Cell.offsetWidth;
        n = n - _ConvertToPx(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-left"));
        n = n - _ConvertToPx(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-right"));
        return n;
    };

    var _GetCellHeight = function (o_Cell) {
        var n = o_Cell.offsetHeight;
        n = n - _ConvertToPx(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-top"));
        n = n - _ConvertToPx(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-bottom"));
        return n;
    };

    var _GetRowSpan = function (o_Cell) {
        if (isNaN(parseInt(o_Cell.rowSpan))) {
            return 1;
        } else {
            return parseInt(o_Cell.rowSpan);
        }
    }

    var _ConvertToPx = function (s_Value) {
        if (!s_Value) {
            return 0;
        }
        if (isNaN(parseInt(s_Value))) {
            return 0;
        }

        s_Value = s_Value.toLowerCase();
        if (parseFloat(s_Value) == 0) {
            return 0;
        } else if (s_Value.EndsWith("px")) {
            return parseInt(s_Value);
        } else if (s_Value.EndsWith("pt")) {
            return parseInt(parseFloat(s_Value) * 4 / 3);
        } else if (s_Value.EndsWith("cm")) {
            //1cm=28.35pt
            return parseInt(parseFloat(s_Value) * 28.35 * 4 / 3);
        } else if (s_Value.EndsWith("mm")) {
            return parseInt(parseFloat(s_Value) * 0.001 * 28.35 * 4 / 3);
        } else {
            return 0;
        }
    };

    var _ShowSep = function () {
        _oSepDivV.style.display = "none";
        _oSepDivH.style.display = "none";

        if (_bOnWidth) {
            _oSepDivV.style.cursor = "e-resize";
            _oSepDivV.style.display = "";
        }

        if (_bOnHeight) {
            _oSepDivH.style.cursor = "s-resize";
            _oSepDivH.style.display = "";
        }

        if (_bOnWidth && _bOnHeight) {
            _oSepDivV.style.cursor = "se-resize";
            _oSepDivH.style.cursor = "se-resize";
        }

    };

    return {

        OnResizing: function () {
            return _bOnResizing;
        },

        MM: function (e) {
            if (_bOnResizing) {
                this.MM2(e);
                return;
            }

            if (!_bInit) {
                _Init();
            }

            _ResetVar();

            // is in cell?
            var el = e.srcElement || e.target;
            if (EWEBTools.GetElementDocument(el) != EWEB.EditorDocument) {
                _ResetVar();
                return;
            }

            while (el.tagName.toUpperCase() != "TD" && el.tagName.toUpperCase() != "TH") {
                if (el.tagName.toUpperCase() == "BODY" || el.tagName.toUpperCase() == "HTML") {
                    _ResetVar();
                    return;
                }
                el = el.parentNode;
                if (!el) {
                    _ResetVar();
                    return;
                }
            }
            _oOnCell = el;

            //find position
            var n_Left = 0;
            var n_Top = 0;
            while (el) {
                n_Left += el.offsetLeft;
                n_Top += el.offsetTop;
                el = el.offsetParent;
            }

            var o_ScrollPos = EWEBTools.GetScrollPosition(EWEB.EditorWindow);

            //is in right border
            var n_Right = n_Left + _oOnCell.offsetWidth - o_ScrollPos.X;
            _nScreenClientX = e.screenX - e.clientX;
            _nCellMinX = n_Right - _nTriggerAreaWidth;
            if (e.clientX > _nCellMinX) {
                _bOnWidth = true;
            }

            //is in bottom border
            var n_Bottom = n_Top + _oOnCell.offsetHeight - o_ScrollPos.Y;
            _nScreenClientY = e.screenY - e.clientY;
            _nCellMinY = n_Bottom - _nTriggerAreaWidth;
            if (e.clientY > _nCellMinY) {
                _bOnHeight = true;
            }

            if (_bOnWidth || _bOnHeight) {
                _nSepDivVLeft = n_Right + $("eWebEditor").offsetLeft + $("eWebEditor_Layout").offsetLeft;
                _nSepDivVTop = $("eWebEditor").offsetTop + $("eWebEditor_Layout").offsetTop + $("eWebEditor_ToolarPTR").offsetHeight;
                _oSepDivV.style.left = _nSepDivVLeft + "px";
                _oSepDivV.style.top = _nSepDivVTop + "px";
                _oSepDivV.style.width = _nSepWidth + "px";
                _oSepDivV.style.height = $("eWebEditor").offsetHeight + "px";

                _nSepDivHLeft = $("eWebEditor").offsetLeft + $("eWebEditor_Layout").offsetLeft;
                _nSepDivHTop = n_Bottom + $("eWebEditor").offsetTop + $("eWebEditor_Layout").offsetTop + $("eWebEditor_ToolarPTR").offsetHeight;
                _oSepDivH.style.left = _nSepDivHLeft + "px";
                _oSepDivH.style.top = _nSepDivHTop + "px";
                _oSepDivH.style.width = $("eWebEditor").offsetWidth + "px";
                _oSepDivH.style.height = _nSepWidth + "px";
            }

            _ShowSep();


        },

        MD2: function (e) {
            if (!e) {
                e = eWebEditor.event;
            }

            _bOnResizing = true;

            _nStartX = e.screenX;
            _nStartY = e.screenY;

            _oOnTable = EWEBTools.GetParentNodeByTag(_oOnCell, "TABLE");

            if (_bOnWidth) {
                _nOnTableWidth = _oOnTable.offsetWidth;
                _oOnTable.style.width = _oOnTable.offsetWidth;
                _nOnCellWidth = _oOnCell.offsetWidth;

                for (var i = 0; i < _oOnTable.rows.length; i++) {
                    for (var j = 0; j < _oOnTable.rows[i].cells.length; j++) {
                        var o_Cell = _oOnTable.rows[i].cells[j];
                        o_Cell.style.width = _GetCellWidth(o_Cell) + "px";
                        o_Cell.removeAttribute("width");
                    }
                }

                _aDoWCellObj.length = 0;
                _aDoWCellWidth.length = 0;
                var n = 0;
                for (var i = 0; i < _oOnTable.rows.length; i++) {
                    for (var j = 0; j < _oOnTable.rows[i].cells.length; j++) {
                        var o_Cell = _oOnTable.rows[i].cells[j];
                        if ((o_Cell.offsetLeft + o_Cell.offsetWidth) >= (_oOnCell.offsetLeft + _oOnCell.offsetWidth) && (o_Cell.offsetLeft < (_oOnCell.offsetLeft + _oOnCell.offsetWidth))) {
                            _aDoWCellObj[n] = o_Cell;
                            _aDoWCellWidth[n] = parseInt(o_Cell.style.width);
                            n++;
                            break;
                        }
                    }
                }
            }


            if (_bOnHeight) {
                _oOnRow = _oOnCell.parentNode;
                _nOnTableHeight = _oOnTable.offsetHeight;
                _oOnTable.style.height = _oOnTable.offsetHeight;
                _nOnRowHeight = _oOnCell.offsetHeight;
                for (var i = 0; i < _oOnTable.rows.length; i++) {
                    for (var j = 0; j < _oOnTable.rows[i].cells.length; j++) {
                        var o_Cell = _oOnTable.rows[i].cells[j];
                        o_Cell.style.height = _GetCellHeight(o_Cell) + "px";
                        o_Cell.removeAttribute("height");
                    }
                }

                _aDoHCellObj.length = 0;
                _aDoHCellHeight.length = 0;
                var n = 0;
                var n_Row = _oOnRow.rowIndex + _GetRowSpan(_oOnRow);
                for (var i = 0; i < n_Row; i++) {
                    for (var j = 0; j < _oOnTable.rows[i].cells.length; j++) {
                        var o_Cell = _oOnTable.rows[i].cells[j];
                        if ((_GetRowSpan(o_Cell) + i) >= n_Row && i < n_Row) {
                            _aDoHCellObj[n] = o_Cell;
                            _aDoHCellHeight[n] = parseInt(o_Cell.style.height);
                            n++;
                        }
                    }
                }
            }

        },

        MM2: function (e) {
            if (!e) {
                e = eWebEditor.event;
            }

            if (!_bOnResizing) {
                var b_Changed = false;
                if (e.screenX - _nScreenClientX > _nCellMinX) {
                    if (!_bOnWidth) {
                        b_Changed = true;
                        _bOnWidth = true;
                    }
                } else {
                    if (_bOnWidth) {
                        b_Changed = true;
                        _bOnWidth = false;
                    }
                }
                if (e.screenY - _nScreenClientY > _nCellMinY) {
                    if (!_bOnHeight) {
                        b_Changed = true;
                        _bOnHeight = true;
                    }
                } else {
                    if (_bOnHeight) {
                        b_Changed = true;
                        _bOnHeight = false;
                    }
                }
                if (b_Changed) {
                    _ShowSep();
                }

                return;
            }


            if (_bOnWidth) {
                var n_SepResize = e.screenX - _nStartX;
                var n_CellResize = n_SepResize;
                if (_oOnTable.align.toLowerCase() == "center") {
                    n_CellResize = 2 * n_CellResize;
                }
                var n_NewCellWidth = n_CellResize + _nOnCellWidth;
                var n_NewTableWidth = n_CellResize + _nOnTableWidth;
                if (n_NewCellWidth >= 2) {
                    _oOnTable.style.width = n_NewTableWidth + "px";
                    _oSepDivV.style.left = (_nSepDivVLeft + n_SepResize) + "px";

                    for (var i = 0; i < _aDoWCellObj.length; i++) {
                        try {
                            _aDoWCellObj[i].style.width = (n_CellResize + _aDoWCellWidth[i]) + "px";
                        } catch (er) {
                        }
                    }
                }
            }

            if (_bOnHeight) {
                var n_SepResize = e.screenY - _nStartY;
                var n_NewRowHeight = n_SepResize + _nOnRowHeight;
                var n_NewTableHeight = n_SepResize + _nOnTableHeight;

                if (n_NewRowHeight >= 2) {
                    _oOnTable.style.height = n_NewTableHeight + "px";
                    _oSepDivH.style.top = (_nSepDivHTop + n_SepResize) + "px";

                    for (var i = 0; i < _aDoHCellObj.length; i++) {
                        try {
                            _aDoHCellObj[i].style.height = (n_SepResize + _aDoHCellHeight[i]) + "px";
                        } catch (er) {
                        }
                    }
                }
            }

        },


        MU2: function (e) {
            if (!_bOnResizing) {
                return;
            }
            _ResetVar();
            _FireChange();
        },

        PMM: function (e) {
            if (!e) {
                e = window.event;
            }

            if (EWEBBrowser.IsIE && e.button != 1 && _bOnResizing) {
                _ResetVar();
            }

        }


    };

})();


var EWEBImageResize = (function () {
    var _bOnResizing = false;
    var _bOnShow = false;

    var _nStartX;
    var _nStartY;
    var _oOnImg;
    var _sOnDirFlag;
    var _nStartWidth;
    var _nStartHeight;

    var _oCornerLT, _oCornerLM, _oCornerLB, _oCornerCT, _oCornerCB, _oCornerRT, _oCornerRM, _oCornerRB;
    var _oLineL, _oLineR, _oLineT, _oLineB;

    var _bInit = false;
    var _Init = function () {
        _oLineL = $("ImgResize_Line_L");
        _oLineR = $("ImgResize_Line_R");
        _oLineT = $("ImgResize_Line_T");
        _oLineB = $("ImgResize_Line_B");
        _oCornerLT = $("ImgResize_C_LT");
        _oCornerLM = $("ImgResize_C_LM");
        _oCornerLB = $("ImgResize_C_LB");
        _oCornerCT = $("ImgResize_C_CT");
        _oCornerCB = $("ImgResize_C_CB");
        _oCornerRT = $("ImgResize_C_RT");
        _oCornerRM = $("ImgResize_C_RM");
        _oCornerRB = $("ImgResize_C_RB");

        EWEBTools.AddEventListener(document, 'mousedown', EWEBImageResize.PMD);

        _bInit = true;
    };


    var _Hide = function () {
        _bOnShow = false;
        _bOnResizing = false;
        _oOnImg = null;
        _SetDisplay("none");
    };

    var _SetDisplay = function (v) {
        var a_Obj = [_oLineL, _oLineR, _oLineT, _oLineB, _oCornerLT, _oCornerLM, _oCornerLB, _oCornerCT, _oCornerCB, _oCornerRT, _oCornerRM, _oCornerRB];
        for (var i = 0; i < a_Obj.length; i++) {
            a_Obj[i].style.display = v;
        }
    };

    var _SetPos = function (el, n_ImgWidth, n_ImgHeight) {
        var w = n_ImgWidth - 2;
        var h = n_ImgHeight - 2;

        var n_Left = el.clientLeft;
        var n_Top = el.clientTop;
        while (el) {
            n_Left += el.offsetLeft;
            n_Top += el.offsetTop;
            el = el.offsetParent;
        }

        var o_ScrollPos = EWEBTools.GetScrollPosition(EWEB.EditorWindow);

        n_Left = n_Left - o_ScrollPos.X;
        n_Top = n_Top - o_ScrollPos.Y;

        n_Left = n_Left + $("eWebEditor").offsetLeft + $("eWebEditor_Layout").offsetLeft + _ConvertToPx(EWEBTools.GetCurrentElementStyle($("eWebEditor_EditareaTD"), "border-left-width")) + _ConvertToPx(EWEBTools.GetCurrentElementStyle($("eWebEditor"), "border-left-width"));
        n_Top = n_Top + $("eWebEditor").offsetTop + $("eWebEditor_Layout").offsetTop + $("eWebEditor_ToolarPTR").offsetHeight + _ConvertToPx(EWEBTools.GetCurrentElementStyle($("eWebEditor_EditareaTD"), "border-top-width")) + _ConvertToPx(EWEBTools.GetCurrentElementStyle($("eWebEditor"), "border-top-width"));

        _oCornerLT.style.left = n_Left + "px";
        _oCornerLT.style.top = n_Top + "px";

        _oCornerCT.style.left = (n_Left + parseInt(w / 2)) + "px";
        _oCornerCT.style.top = n_Top + "px";

        _oCornerRT.style.left = (n_Left + w) + "px";
        _oCornerRT.style.top = n_Top + "px";

        _oCornerLM.style.left = n_Left + "px";
        _oCornerLM.style.top = (n_Top + parseInt(h / 2)) + "px";

        _oCornerRM.style.left = (n_Left + w) + "px";
        _oCornerRM.style.top = (n_Top + parseInt(h / 2)) + "px";

        _oCornerLB.style.left = n_Left + "px";
        _oCornerLB.style.top = (n_Top + h) + "px";

        _oCornerCB.style.left = (n_Left + parseInt(w / 2)) + "px";
        _oCornerCB.style.top = (n_Top + h) + "px";

        _oCornerRB.style.left = (n_Left + w) + "px";
        _oCornerRB.style.top = (n_Top + h) + "px";

        _oLineL.style.left = n_Left + "px";
        _oLineL.style.top = n_Top + "px";
        _oLineL.style.height = h + "px";

        _oLineR.style.left = (n_Left + w + 1) + "px";
        _oLineR.style.top = n_Top + "px";
        _oLineR.style.height = h + "px";

        _oLineT.style.left = n_Left + "px";
        _oLineT.style.top = n_Top + "px";
        _oLineT.style.width = w + "px";

        _oLineB.style.left = n_Left + "px";
        _oLineB.style.top = (n_Top + h + 1) + "px";
        _oLineB.style.width = w + "px";
    };

    var _ConvertToPx = function (s_Value) {
        if (!s_Value) {
            return 0;
        }
        if (isNaN(parseInt(s_Value))) {
            return 0;
        }

        s_Value = s_Value.toLowerCase();
        if (parseFloat(s_Value) == 0) {
            return 0;
        } else if (s_Value.EndsWith("px")) {
            return parseInt(s_Value);
        } else if (s_Value.EndsWith("pt")) {
            return parseInt(parseFloat(s_Value) * 4 / 3);
        } else if (s_Value.EndsWith("cm")) {
            //1cm=28.35pt
            return parseInt(parseFloat(s_Value) * 28.35 * 4 / 3);
        } else if (s_Value.EndsWith("mm")) {
            return parseInt(parseFloat(s_Value) * 0.001 * 28.35 * 4 / 3);
        } else {
            return 0;
        }
    };

    var _IsOkBrowser = function () {
        return (!EWEBBrowser.IsAllIE && !EWEBBrowser.IsFirefox);
    };


    return {

        _ReloadDoc: function () {
            if (_IsOkBrowser()) {
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'keydown', EWEBImageResize._End);
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'mousedown', EWEBImageResize._End);
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'dblclick', EWEBImageResize._End);
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'click', EWEBImageResize._Click);
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'scroll', EWEBImageResize._End);
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'mouseup', EWEBImageResize.MU2);
                EWEBTools.AddEventListener(EWEB.EditorDocument, 'mousemove', EWEBImageResize.MM2);
            }
        },

        _End: function (e) {
            if (_bOnShow) {
                _Hide();
            }
        },

        _Click: function (e) {
            if (e.button == 2 || !_IsOkBrowser()) {
                return;
            }

            var el = e.srcElement || e.target;
            if (!el) {
                return;
            }
            if (el.tagName != "IMG") {
                return;
            }
            _oOnImg = el;

            if (!_bInit) {
                _Init();
            }

            _nStartWidth = _ConvertToPx(EWEBTools.GetCurrentElementStyle(el, "width"));
            _nStartHeight = _ConvertToPx(EWEBTools.GetCurrentElementStyle(el, "height"));

            _SetPos(el, _nStartWidth, _nStartHeight);

            _SetDisplay("block");
            _bOnShow = true;
        },

        MD2: function (e) {
            if (_bOnResizing) {
                return;
            }

            _bOnResizing = true;

            _nStartX = e.screenX;
            _nStartY = e.screenY;

            var el = e.srcElement || e.target;
            _nStartWidth = _ConvertToPx(EWEBTools.GetCurrentElementStyle(_oOnImg, "width"));
            _nStartHeight = _ConvertToPx(EWEBTools.GetCurrentElementStyle(_oOnImg, "height"));
            _sOnDirFlag = el.id.substr(12).toLowerCase();
        },

        MM2: function (e) {
            if (!_bOnResizing) {
                return;
            }

            var x = e.screenX - _nStartX;
            var y = e.screenY - _nStartY;
            var w = _nStartWidth;
            var h = _nStartHeight;

            switch (_sOnDirFlag) {
                case "lt":
                    w = w - x;
                    h = h - y;
                    break;
                case "lm":
                    w = w - x;
                    break;
                case "lb":
                    w = w - x;
                    h = h + y;
                    break;
                case "ct":
                    h = h - y;
                    break;
                case "cb":
                    h = h + y;
                    break;
                case "rt":
                    w = w + x;
                    h = h - y;
                    break;
                case "rm":
                    w = w + x;
                    break;
                case "rb":
                    w = w + x;
                    h = h + y;
                    break;
            }

            if (w < 5) {
                w = 5;
            }
            if (h < 5) {
                h = 5;
            }

            _oOnImg.style.width = w + "px";
            _oOnImg.style.height = h + "px";

            _SetPos(_oOnImg, w, h);
        },


        MU2: function (e) {
            if (!_bOnResizing) {
                return;
            }

            _bOnResizing = false;
            _FireChange();
        },

        PMD: function (e) {
            if (!_bOnShow) {
                return;
            }

            var el = e.srcElement || e.target;
            if (el != _oLineL && el != _oLineR && el != _oLineT && el != _oLineB && el != _oCornerLT && el != _oCornerLM && el != _oCornerLB && el != _oCornerCT && el != _oCornerCB && el != _oCornerRT && el != _oCornerRM && el != _oCornerRB) {
                _Hide();
            }
        }

    };

})();


//////////////////////////////////////////////////////////


var EWEBHistory = (function () {
    var _data = [];
    var _position = 0;
    var _bookmark = [];
    var _saved = false;
    var _memmax = 10000000;
    var _memcount = 0;


    var _SetHistoryCursor = function () {
        var s_Bookmark = _bookmark[_position];
        if (s_Bookmark) {

            if (EWEBBrowser.IsIE) {
                eWebEditor_Layout.focus();
                if (s_Bookmark.substring(0, 8) != "[object]") {
                    var r = EWEB.EditorDocument.body.createTextRange();
                    if (r.moveToBookmark(_bookmark[_position])) {
                        //r.duplicate();
                        //r.collapse(false);
                        r.select();
                    }
                } else {
                    if (EWEB.CurrMode == "EDIT") {
                        var r = EWEB.EditorDocument.body.createControlRange();
                        var a = s_Bookmark.split("|");
                        var els = EWEB.EditorDocument.body.getElementsByTagName(a[1]);
                        var el = els[a[2]];
                        r.addElement(el);
                        r.select();
                    }
                }
            } else {

                //EWEB.EditorWindow.getSelection().removeAllRanges();
                //EWEB.EditorWindow.getSelection().addRange(_bookmark[_position]);
                //EWEBSelection.SelectNode(_bookmark[_position]);
            }
        }
    };


    var _GetElementTagIndex = function (el) {
        var els = EWEB.EditorDocument.body.getElementsByTagName(el.tagName);
        for (var i = 0; i < els.length; i++) {
            if (els[i] == el) {
                return i;
            }
        }
        return null;
    };


    return {

        QueryUndoState: function () {
            if (_data.length <= 1 || _position <= 0) {
                return false;
            }
            return true;
        },

        QueryRedoState: function () {
            if (_position >= _data.length - 1 || _data.length == 0) {
                return false;
            }
            return true;
        },


        DoChange: function () {
            if (EWEB.CurrMode != "EDIT") {
                return;
            }
            _saved = false;
        },

        Save: function () {
            if (EWEB.CurrMode != "EDIT") {
                return;
            }
            if (_saved) {
                return;
            }
            _saved = true;
            var s_Html = getHTML();
            if (_data[_position] == s_Html) {
                return;
            }

            var nPopLen = _data.length - _position;
            for (var i = 1; i < nPopLen; i++) {
                _memcount = _memcount - _data[_data.length - 1].length;
                _data.pop();
                _bookmark.pop();
            }

            _data[_data.length] = s_Html;
            _memcount = _memcount + s_Html.length;
            while (_memcount > _memmax && _data.length > 1) {
                _memcount = _memcount - _data[0].length;
                _data.shift();
                _bookmark.shift();
            }

            if (EWEBBrowser.IsIE) {

                if (EWEBSelection.GetType() != "Control") {
                    try {
                        _bookmark[_bookmark.length] = EWEB.EditorDocument.selection.createRange().getBookmark();
                    } catch (e) {
                        _bookmark[_bookmark.length] = "";
                    }
                } else {
                    var el = EWEBSelection.GetSelectedElement();
                    _bookmark[_bookmark.length] = "[object]|" + el.tagName + "|" + _GetElementTagIndex(el);
                }

            } else {
                try {
                    _bookmark[_bookmark.length] = EWEB.EditorWindow.getSelection().getRangeAt(0).endContainer;
                } catch (e) {
                    _bookmark[_bookmark.length] = "";
                }

            }

            _position = _data.length - 1;
        },


        Go: function (v) {
            if (EWEB.CurrMode != "EDIT") {
                return;
            }
            if (!_saved) {
                this.Save();
            }

            var n_pos = _position + v;
            if (n_pos >= 0 && n_pos < _data.length) {
                _position = n_pos;
                setHTML(_data[_position], true);
                _SetHistoryCursor();
            }

            EWEB.Focus();
            EWEBToolbar.CheckTBStatus();
        }

    };

})();


//////////////////////////////////////////////////////////


var EWEBActiveX = (function () {
    var _NowVersion = "7.8.0.0";
    var _VersionName = _NowVersion.replace(/\./gi, "");

    var _IsNewVersion = function (s_Version) {
        var a_Ver = s_Version.split(".");
        var a_Now = _NowVersion.split(".");
        if (a_Ver.length == 4) {
            for (var i = 0; i < 4; i++) {
                var n_Ver = parseInt(a_Ver[i]);
                var n_Now = parseInt(a_Now[i]);
                if (n_Ver > n_Now) {
                    return true;
                } else if (n_Ver < n_Now) {
                    return false;
                }
            }
            return true;
        }
        return false;
    };

    var _FFObjectHTML = '<object type="application/x-ewebeditor-' + _VersionName + '" id="obj_activex" height="0" width="0" progid="eWebSoft.eWebEditor' + _VersionName + '" ></object>';
    var _nLSInstallCheckTimes = 0;
    var _IsRun = false;
    var _AjaxPort = (EWEBParam.Proto == "https") ? "20036" : "20035";

    var _CallLS = function () {
        var s_Src = "ewebeditorls:port=" + _AjaxPort;
        var o_Div = $("div_LS");
        o_Div.innerHTML = "";
        o_Div.innerHTML = "<iframe id='ifr_LS' width=1 height=1 src='" + s_Src + "'></iframe>";
    };

    var _IsWaitAct = function (s_Act) {
        return ("|importstatus|cancel|version|error|style|body|originalfiles|savedfiles|".indexOf("|" + s_Act.toLowerCase() + "|") < 0);
    };

    var _AjaxPostData = function (o_PostData, f_Callback) {
        var s_Url = EWEBParam.Proto + "://127.0.0.1:" + _AjaxPort + "/";

        o_PostData["V"] = _VersionName;
        o_PostData["Lang"] = lang.ActiveLanguage;
        o_PostData["Charset"] = config.Charset;
        o_PostData["SendUrl"] = EWEB.SendUrl;
        o_PostData["LocalSize"] = config.AllowLocalSize;
        o_PostData["LocalExt"] = config.AllowLocalExt;
        o_PostData["Cookie"] = config.Cookie;
        o_PostData["CertIssuer"] = config.CertIssuer;
        o_PostData["CertSubject"] = config.CertSubject;

        EWEBAjax._GetData("post", s_Url, true, o_PostData,
            function (o_BackData) {
                f_Callback(o_BackData);
            }
        );

    };

    var _WaitOk = function (f_CallBack, f_WaitListCallBack, o_ActInfo) {
        var o_Data = {};
        var f_NextWait = function () {
            if (f_WaitListCallBack) {
                f_WaitListCallBack({"sucess": "yes"});
            } else {
                EWEBActiveX._AsynWaitExec();
            }
        };

        var s_Act = o_ActInfo["Act"];
        if (EWEBBrowser.IsUseLS) {
            _AjaxPostData(o_ActInfo,
                function (o_AjaxData) {
                    if (o_AjaxData["XhrStatus"] == 200 && o_AjaxData["Data"]["Status"] == "ok") {
                        if ("|capture|localupload|showeq|importword|pasteword|importexcelsheet|pasteexcel|importppt|pastefilelist|pasteimage|execasync|mfuupload|".indexOf("|" + s_Act + "|") >= 0) {
                            o_ActInfo["Act"] = "status";
                            EWEBTools.SetTimeout(_WaitOk, 100, null, [f_CallBack, f_WaitListCallBack, o_ActInfo]);
                            return;
                        }
                        o_Data = o_AjaxData["Data"];
                        if (s_Act == "isinstalled") {
                            if (o_Data["Ret"] == "yes") {
                                o_Data["Ret"] = true;
                                EWEBActiveX.ActiveX = "LS";
                            } else {
                                o_Data["Ret"] = false;
                                if (o_ActInfo["Param1"]) {
                                    showDialog("installactivex.htm?action=install", true);
                                }
                            }
                        }

                        if (_IsWaitAct(s_Act)) {
                            EWEBActiveX._AsynIsRun = false;
                        }

                        if (typeof(f_CallBack) == 'function') {
                            f_CallBack(o_Data);
                        }

                        if (_IsWaitAct(s_Act)) {
                            f_NextWait();
                        }

                    } else {

                        if (s_Act == "isinstalled") {
                            if (EWEBActiveX._AjaxFirstTime) {
                                EWEBActiveX._AjaxFirstTime = false;
                                _CallLS();
                            } else if ((new Date() - EWEBActiveX._AjaxBeginTime) > 5000) {
                                o_Data["Ret"] = false;
                                EWEBActiveX._AsynIsRun = false;
                                if (typeof(f_CallBack) == 'function') {
                                    f_CallBack(o_Data);
                                }
                                if (o_ActInfo["Param1"]) {
                                    showDialog("installactivex.htm?action=install", true);
                                }
                                f_NextWait();
                                return;
                            }
                        }
                        EWEBTools.SetTimeout(_WaitOk, 100, null, [f_CallBack, f_WaitListCallBack, o_ActInfo]);
                        return;
                    }
                }
            );
        } else {
            o_Data["Status"] = EWEBActiveX.ActiveX.Status;
            if (o_Data["Status"] != "ok") {
                EWEBTools.SetTimeout(_WaitOk, 100, null, [f_CallBack, f_WaitListCallBack, o_ActInfo]);
                return;
            } else {
                if (typeof(f_CallBack) == 'function') {
                    o_Data["Ret"] = "";
                    o_Data["Error"] = EWEBActiveX.ActiveX.Error;
                    o_Data["Style"] = EWEBActiveX.ActiveX.Style;
                    o_Data["Body"] = EWEBActiveX.ActiveX.Body;
                    o_Data["OriginalFiles"] = EWEBActiveX.ActiveX.OriginalFiles;
                    o_Data["SavedFiles"] = EWEBActiveX.ActiveX.SavedFiles;

                    if ((s_Act == "showeq") && (o_Data["Body"] == "") && (o_Data["Error"] == "")) {
                        EWEBTools.SetTimeout(_WaitOk, 100, null, [f_CallBack, f_WaitListCallBack, o_ActInfo]);
                        return;
                    }

                    f_CallBack(o_Data);
                }
                EWEBActiveX._AsynIsRun = false;
                f_NextWait();
            }
        }
    };


    return {

        IsInstalled: function (b_AutoInstall) {
            if (this.ActiveX) {
                this.ResetProperty();
                return true;
            }

            var o_ActiveX;
            var b = false;
            var s_Flag = "install";
            if (EWEBBrowser.IsAllIE) {
                try {
                    o_ActiveX = new ActiveXObject("eWebSoft.eWebEditor" + _VersionName);
                    var s_Version = o_ActiveX.Version;
                    if (_IsNewVersion(s_Version)) {
                        b = true;
                    } else {
                        o_ActiveX = null;
                        s_Flag = "update";
                    }
                } catch (e) {
                }

            } else {
                try {
                    o_ActiveX = $("obj_activex");
                    var s_Version = o_ActiveX.Version;
                    if (s_Version) {
                        if (_IsNewVersion(s_Version)) {
                            b = true;
                        } else {
                            s_Flag = "update";
                        }
                    }
                } catch (e) {
                }
            }
            if (b) {
                o_ActiveX.Lang = lang.ActiveLanguage;
                o_ActiveX.Charset = config.Charset;
                o_ActiveX.SendUrl = EWEB.SendUrl;
                o_ActiveX.LocalSize = config.AllowLocalSize;
                o_ActiveX.LocalExt = config.AllowLocalExt;
                o_ActiveX.Cookie = config.Cookie;
                o_ActiveX.CertIssuer = config.CertIssuer;
                o_ActiveX.CertSubject = config.CertSubject;
                this.ActiveX = o_ActiveX;
                return true;
            } else {
                if (b_AutoInstall) {
                    showDialog("installactivex.htm?action=" + s_Flag, true);
                } else {
                    if (!EWEBBrowser.IsAllIE) {
                        EWEBActiveX.RefreshFFObject();
                    }
                }
                return false;
            }
        },

        RefreshFFObject: function () {
            navigator.plugins.refresh(false);
            $("div_activex").innerHTML = "";
            $("div_activex").innerHTML = _FFObjectHTML;
        },

        IsNpCanCall: function () {
            var b = false;
            try {
                var o_ActiveX = $("obj_activex");
                var s_Version = o_ActiveX.Version;
                if (s_Version) {
                    b = true;
                }
            } catch (e) {
                b = false;
            }

            return b;
        },

        ResetProperty: function () {
            if (!EWEBBrowser.IsUseLS) {
                this.ActiveX.SendUrl = EWEB.SendUrl;
            }
        },

        IsPrinterInstalled: function (b_AutoInstall, f_CallBack) {
            if (!this.ActiveX) {
                f_CallBack(false);
                return;
            }

            EWEBActiveX.AsynCallBack("isprinterexist", [],
                function (o_Data) {
                    var b = ((o_Data["Ret"] + "") == "1");
                    if (!b && b_AutoInstall) {
                        EWEBDialog.OpenDialog("installprinter.htm");
                    }
                    f_CallBack(b);
                }
            );
        },

        IsError: function (s_Error) {
            if (s_Error != "") {
                var s_ErrorCode, s_ErrorDesc;
                if (s_Error.indexOf(":") >= 0) {
                    var a = s_Error.split(":");
                    s_ErrorCode = a[0];
                    s_ErrorDesc = a[1];
                } else {
                    s_ErrorCode = s_Error;
                    s_ErrorDesc = "";
                }

                switch (s_ErrorCode) {
                    case "L":
                        alert(lang["ErrLicense"]);
                        break;
                    case "FSL":
                        alert(lang["ErrFSL"]);
                        break;
                    case "InvalidFile":
                        alert(lang["ErrInvalidFile"] + ":" + s_ErrorDesc);
                        break;
                    case "HttpRequest":
                        alert(lang["ErrHttpRequest"] + s_ErrorDesc);
                        break;
                    default:
                        alert(s_Error);
                }
                return true;
            }
            return false;
        },

        SetIsRun: function (s_Flag) {
            _IsRun = s_Flag
        },

        CheckIsRun: function () {
            if (_IsRun) {
                return true;
            } else {
                _IsRun = true;
                return false;
            }
        },

        FFObjectHTML: _FFObjectHTML,

        _AsynWaitList: [],
        _AsynWaitExec: function () {
            if (EWEBActiveX._AsynWaitList.length == 0) {
                return;
            }
            EWEBActiveX.AsynCallBack(EWEBActiveX._AsynWaitList[0]["Act"], EWEBActiveX._AsynWaitList[0]["Param"], EWEBActiveX._AsynWaitList[0]["CallBack"], true,
                function (o_Data) {
                    if (o_Data["sucess"]) {
                        EWEBActiveX._AsynWaitList.shift();
                    }
                    window.setTimeout(EWEBActiveX._AsynWaitExec, 100);
                }
            );
        },

        AsynCallBack: function (s_Action, a_Param, f_CallBack, b_NotWait, f_WaitListCallBack) {
            if (!EWEBBrowser.IsWindow) {
                f_CallBack({"Ret": false});
                return;
            }

            if (s_Action == "isinstalled") {
                if (_nLSInstallCheckTimes == 0 && EWEBBrowser.IsUseLS) {
                    var o_MimeType = navigator.mimeTypes["application/x-ewebeditor-" + _VersionName];
                    if (o_MimeType) {
                        if (EWEBActiveX.IsNpCanCall()) {
                            EWEBBrowser.IsUseLS = false;
                        }
                    }
                    _nLSInstallCheckTimes = 1;
                }

                if (EWEBActiveX.ActiveX) {
                    if (!EWEBBrowser.IsUseLS) {
                        EWEBActiveX.ResetProperty();
                    }
                    f_CallBack({"Ret": true});
                    if (f_WaitListCallBack) {
                        f_WaitListCallBack({"sucess": "yes"});
                    } else {
                        EWEBActiveX._AsynWaitExec();
                    }
                    return;
                }
            }

            var o_BackData = {};

            if (_IsWaitAct(s_Action)) {
                if (EWEBActiveX._AsynIsRun) {
                    if (b_NotWait) {
                        if (f_WaitListCallBack) {
                            f_WaitListCallBack({"sucess": ""});
                            return;
                        } else {
                            f_CallBack({"sucess": ""});
                            window.setTimeout(EWEBActiveX._AsynWaitExec, 100);
                            return;
                        }
                    } else {
                        EWEBActiveX._AsynWaitList.push({"Act": s_Action, "Param": a_Param, "CallBack": f_CallBack});
                        window.setTimeout(EWEBActiveX._AsynWaitExec, 100);
                        return;
                    }
                } else {
                    EWEBActiveX._AsynIsRun = true;
                }
            }

            var o_ActInfo = {"Act": s_Action};
            for (var i = 0; i < a_Param.length; i++) {
                o_ActInfo["Param" + (i + 1)] = a_Param[i];
            }

            if (EWEBBrowser.IsUseLS) {
                if (s_Action == "isinstalled") {
                    EWEBActiveX._AjaxFirstTime = true;
                    EWEBActiveX._AjaxBeginTime = new Date();
                }

                EWEBTools.SetTimeout(_WaitOk, 100, null, [f_CallBack, f_WaitListCallBack, o_ActInfo]);

            } else {
                var v_Ret;
                switch (s_Action) {
                    case "isinstalled":
                        v_Ret = this.IsInstalled(a_Param[0]);
                        break;
                    case "capture":
                        this.ActiveX.Capture(a_Param[0]);
                        break;
                    case "localupload":
                        this.ActiveX.LocalUpload(a_Param[0]);
                        break;
                    case "setclipboard":
                        this.ActiveX.SetClipboard(a_Param[0], a_Param[1], a_Param[2]);
                        v_Ret = "ok";
                        break;
                    case "showeq":
                        this.ActiveX.ShowEQ(a_Param[0], a_Param[1]);
                        break;
                    case "getclipboard":
                        v_Ret = this.ActiveX.GetClipboard(a_Param[0]);
                        break;
                    case "isprinterexist":
                        v_Ret = this.ActiveX.IsPrinterExist();
                        break;
                    case "importword":
                        this.ActiveX.ImportWord(a_Param[0], a_Param[1]);
                        break;
                    case "execasync":
                        this.ActiveX.ExecAsync(a_Param[0], a_Param[1], a_Param[2], a_Param[3]);
                        break;
                    case "pasteword":
                        var s_Opt = a_Param[0];
                        this.ActiveX.PasteWord(s_Opt);
                        break;
                    case "dialogopen":
                        v_Ret = this.ActiveX.DialogOpen(a_Param[0], a_Param[1], a_Param[2], a_Param[3], "", "");
                        break;
                    case "importexcelsheet":
                        this.ActiveX.ImportExcelSheet(a_Param[0], a_Param[1], a_Param[2]);
                        break;
                    case "pasteexcel":
                        this.ActiveX.PasteExcel(a_Param[0]);
                        break;
                    case "getexcelworksheetname":
                        var v_Ret = this.ActiveX.GetExcelWorkSheetName(a_Param[0]);
                        break;
                    case "importppt":
                        this.ActiveX.ImportPPT(a_Param[0], a_Param[1]);
                        break;
                    case "pastefilelist":
                        this.ActiveX.PasteFileList(a_Param[0]);
                        break;
                    case "pasteimage":
                        this.ActiveX.PasteImage(a_Param[0]);
                        break;
                    case "quickformat":
                        this.ActiveX.QuickFormat();
                        v_Ret = "ok";
                        break;
                    case "getflashheader":
                        v_Ret = this.ActiveX.GetFlashHeader(a_Param[0]);
                        break;
                    case "mfu_fileinfo_fromto":
                        v_Ret = "";
                        for (var i = a_Param[0]; i <= a_Param[1]; i++) {
                            if (v_Ret) {
                                v_Ret += "\r";
                            }
                            v_Ret += this.ActiveX.MFUFileInfo(i);
                        }
                        break;
                    case "mfuupload":
                        this.ActiveX.MFUUpload();
                        break;
                    case "cancel":
                        this.ActiveX.Cancel();
                        v_Ret = "ok";
                        break;
                    case "remoteupload":
                        this.ActiveX.RemoteUpload(a_Param[0], a_Param[1]);
                        break;
                    case "importstatus":
                        v_Ret = this.ActiveX.ImportStatus;
                        break;
                    case "version":
                        v_Ret = this.ActiveX.Version;
                        break;
                    case "getinfo":
                        v_Ret = this.ActiveX.GetInfo(a_Param[0], a_Param[1], a_Param[2]);
                        break;
                    default:

                }

                if (typeof(v_Ret) == "undefined") {
                    EWEBTools.SetTimeout(_WaitOk, 100, null, [f_CallBack, f_WaitListCallBack, o_ActInfo]);
                } else {
                    if (_IsWaitAct(s_Action)) {
                        EWEBActiveX._AsynIsRun = false;
                        if (typeof(f_CallBack) == 'function') {
                            o_BackData["Ret"] = v_Ret;
                            if (s_Action != "isinstalled") {
                                o_BackData["Error"] = EWEBActiveX.ActiveX.Error;
                                o_BackData["Style"] = EWEBActiveX.ActiveX.Style;
                                o_BackData["Body"] = EWEBActiveX.ActiveX.Body;
                                o_BackData["OriginalFiles"] = EWEBActiveX.ActiveX.OriginalFiles;
                                o_BackData["SavedFiles"] = EWEBActiveX.ActiveX.SavedFiles;
                            }
                            f_CallBack(o_BackData);
                            if (f_WaitListCallBack) {
                                f_WaitListCallBack({"sucess": "yes"});
                            } else {
                                this._AsynWaitExec();
                            }
                        }
                    } else {
                        if (typeof(f_CallBack) == 'function') {
                            o_BackData["Ret"] = v_Ret;
                            f_CallBack(o_BackData);
                        }
                    }
                }
            }
        }

    };

})();


//////////////////////////////////////////////////////////


function exec(s_CommandName, s_CommandValue) {
    EWEB.Focus();
    EWEBHistory.Save();
    s_CommandName = s_CommandName.toLowerCase();
    var b_Change = true;
    switch (s_CommandName) {
        case 'undo':
            EWEBHistory.Go(-1);
            b_Change = false;
            break;
        case 'redo':
            EWEBHistory.Go(1);
            b_Change = false;
            break;
        case 'cut':
            EWEBCommandCopyCut.Execute('Cut');
            break;
        case 'copy':
            EWEBCommandCopyCut.Execute('Copy');
            b_Change = false;
            break;
        case 'paste':
            doPaste();
            break;
        case 'pastetext':
            pasteText();
            break;
        case 'pasteword':
            pasteWord();
            break;
        case 'delete':
            format('delete');
            break;
        case 'removeformat':
            format('RemoveFormat');
            break;
        case 'selectall':
            format('selectall');
            b_Change = false;
            break;
        case 'unselect':
            format('unselect');
            b_Change = false;
            break;
        case 'findreplace':
            _FindReplace();
            b_Change = false;
            break;
        case 'spellcheck':
            _SpellCheck();
            b_Change = false;
            break;
        case 'quickformat':
            _DoQuickFormat(s_CommandValue);
            b_Change = false;
            break;
        case 'formatbrush':
            EWEBCommandFormatBrush.Execute();
            b_Change = false;
            break;
        case 'bold':
            format('bold');
            break;
        case 'italic':
            format('italic');
            break;
        case 'underline':
            format('underline');
            break;
        case 'strikethrough':
            format('StrikeThrough');
            break;
        case 'superscript':
            format('superscript');
            break;
        case 'subscript':
            format('subscript');
            break;
        case 'uppercase':
            formatText('uppercase');
            break;
        case 'lowercase':
            formatText('lowercase');
            break;
        case 'forecolor':
            showDialog('selcolor.htm?action=forecolor', true);
            b_Change = false;
            break;
        case 'backcolor':
            showDialog('selcolor.htm?action=backcolor', true);
            b_Change = false;
            break;
        case 'big':
            insert('big');
            break;
        case 'small':
            insert('small');
            break;
        case 'justifyleft':
            EWEBCommandJustify.Execute('justifyleft');
            break;
        case 'justifycenter':
            EWEBCommandJustify.Execute('justifycenter');
            break;
        case 'justifyright':
            EWEBCommandJustify.Execute('justifyright');
            break;
        case 'justifyfull':
            EWEBCommandJustify.Execute('justifyfull');
            break;
        case 'orderedlist':
            format('insertorderedlist');
            break;
        case 'unorderedlist':
            format('insertunorderedlist');
            break;
        case 'indent':
            format('indent');
            break;
        case 'outdent':
            format('outdent');
            break;
        case 'br':
            insert('br');
            break;
        case 'paragraph':
            format('InsertParagraph');
            break;
        case 'paragraphattr':
            paragraphAttr();
            b_Change = false;
            break;
        case 'textindent':
        case 'lineheight':
        case 'margintop':
        case 'marginbottom':
            EWEBCommandParagraph._Execute(s_CommandName, s_CommandValue);
            break;
        case 'imagemargin':
            showDialog('imagemargin.htm', true);
            b_Change = false;
            break;
        case 'image':
            showDialog('img.htm', true);
            b_Change = false;
            break;
        case 'flash':
            showDialog('flash.htm', true);
            b_Change = false;
            break;
        case 'media':
            showDialog('media.htm', true);
            b_Change = false;
            break;
        case 'file':
            showDialog('file.htm', true);
            b_Change = false;
            break;
        case 'remoteupload':
            remoteUpload();
            b_Change = false;
            break;
        case 'localupload':
            localUpload();
            b_Change = false;
            break;
        case 'fieldset':
            showDialog('fieldset.htm', true);
            b_Change = false;
            break;
        case 'iframe':
            showDialog('iframe.htm', true);
            b_Change = false;
            break;
        case 'horizontalrule':
            showDialog('hr.htm', true);
            b_Change = false;
            break;
        case 'marquee':
            showDialog('marquee.htm', true);
            b_Change = false;
            break;
        case 'createlink':
            createLink();
            b_Change = false;
            break;
        case 'unlink':
            format('UnLink');
            break;
        case 'map':
            mapEdit();
            b_Change = false;
            break;
        case 'anchor':
            showDialog('anchor.htm', true);
            b_Change = false;
            break;
        case 'galleryimage':
            showDialog('browse.htm?type=image', true);
            b_Change = false;
            break;
        case 'galleryflash':
            showDialog('browse.htm?type=flash', true);
            b_Change = false;
            break;
        case 'gallerymedia':
            showDialog('browse.htm?type=media', true);
            b_Change = false;
            break;
        case 'galleryfile':
            showDialog('browse.htm?type=file', true);
            b_Change = false;
            break;
        case 'bgcolor':
            showDialog('selcolor.htm?action=bgcolor', true);
            b_Change = false;
            break;
        case 'backimage':
            showDialog('backimage.htm', true);
            b_Change = false;
            break;
        case 'absoluteposition':
            absPosition();
            break;
        case 'zindexbackward':
            _SetZIndex('backward');
            break;
        case 'zindexforward':
            _SetZIndex('forward');
            break;
        case 'showborders':
            EWEBCommandShowBorders.Execute();
            b_Change = false;
            break;
        case 'showblocks':
            EWEBCommandShowBlocks.Execute();
            b_Change = false;
            break;
        case 'quote':
            insert('quote');
            break;
        case 'code':
            insert('code');
            break;
        case 'symbol':
            showDialog('symbol.htm', true);
            b_Change = false;
            break;
        case 'printbreak':
            insert('printbreak');
            break;
        case 'excel':
            showDialog('owcexcel.htm', true, true);
            b_Change = false;
            break;
        case 'emot':
            showDialog('emot.htm', true);
            b_Change = false;
            break;
        case 'eq':
            return;
            //showDialog('eq.htm', true);
            //b_Change = false;
            break;
        case 'art':
            if (EWEBBrowser.IsIE11P) {
                alert("Don't support IE11+.");
            } else {
                showDialog('art.htm', true, true);
            }
            b_Change = false;
            break;
        case 'nowdate':
            insert('nowdate');
            break;
        case 'nowtime':
            insert('nowtime');
            break;
        case 'importword':
            DoImport('word');
            b_Change = false;
            break;
        case 'importexcel':
            DoImport('excel');
            b_Change = false;
            break;
        case 'importppt':
            DoImport('ppt');
            b_Change = false;
            break;
        case 'importpdf':
            DoImport('pdf');
            b_Change = false;
            break;
        case 'template':
            showDialog('template.htm', true);
            b_Change = false;
            break;
        case 'capture':
            doCapture();
            b_Change = false;
            break;
        case 'pagination':
            showDialog('pagination.htm', true);
            b_Change = false;
            break;
        case 'paginationinsert':
            EWEBPagination.Insert();
            break;
        case 'titleimage':
            _SetTitleImage();
            b_Change = false;
            break;
        case 'imagedoc':
            _DoImageDoc();
            b_Change = false;
            break;
        case 'formtext':
            EWEBCommandForm.Insert('inputtext');
            break;
        case 'formtextarea':
            EWEBCommandForm.Insert('textarea');
            break;
        case 'formradio':
            EWEBCommandForm.Insert('radio');
            break;
        case 'formcheckbox':
            EWEBCommandForm.Insert('checkbox');
            break;
        case 'formdropdown':
            EWEBCommandForm.Insert('select');
            break;
        case 'formbutton':
            EWEBCommandForm.Insert('button');
            break;
        case 'tableinsert':
            EWEBTable.TableInsert();
            b_Change = false;
            break;
        case 'tableprop':
            EWEBTable.TableProp();
            b_Change = false;
            break;
        case 'tablecellprop':
            EWEBTable.CellProp();
            b_Change = false;
            break;
        case 'tablecellsplit':
            EWEBTable.CellSplit();
            b_Change = false;
            break;
        case 'tablerowprop':
            EWEBTable.RowProp();
            b_Change = false;
            break;
        case 'tablerowinsertabove':
            EWEBTable.RowInsertAbove();
            break;
        case 'tablerowinsertbelow':
            EWEBTable.RowInsertBelow();
            break;
        case 'tablerowmerge':
            EWEBTable.RowMerge();
            break;
        case 'tablerowsplit':
            EWEBTable.RowSplit(2);
            break;
        case 'tablerowdelete':
            EWEBTable.RowDelete();
            break;
        case 'tablecolinsertleft':
            EWEBTable.ColInsertLeft();
            break;
        case 'tablecolinsertright':
            EWEBTable.ColInsertRight();
            break;
        case 'tablecolmerge':
            EWEBTable.ColMerge();
            break;
        case 'tablecolsplit':
            EWEBTable.ColSplit(2);
            break;
        case 'tablecoldelete':
            EWEBTable.ColDelete();
            break;
        case 'tabletemplate':
            EWEBTable.TableTemplate();
            b_Change = false;
            break;
        case 'refresh':
            setHTML('');
            b_Change = false;
            break;
        case 'modecode':
            setMode('CODE');
            b_Change = false;
            break;
        case 'modeedit':
            setMode('EDIT');
            b_Change = false;
            break;
        case 'modetext':
            setMode('TEXT');
            b_Change = false;
            break;
        case 'modeview':
            setMode('VIEW');
            b_Change = false;
            break;
        case 'sizeplus':
            sizeChange(300);
            b_Change = false;
            break;
        case 'sizeminus':
            sizeChange(-300);
            b_Change = false;
            break;
        case 'print':
            DoPrint();
            b_Change = false;
            break;
        case 'maximize':
            EWEBCommandMaximize.Execute();
            b_Change = false;
            break;
        case 'minimize':
            parent.Minimize();
            b_Change = false;
            break;
        case 'expandtoolbar':
            EWEBToolbar.Expand();
            b_Change = false;
            break;
        case 'help':
            showDialog('help.htm');
            b_Change = false;
            break;
        case 'about':
            showDialog('about.htm');
            b_Change = false;
            break;
        case 'site':
            window.open('http://www.ewebeditor.net');
            b_Change = false;
            break;
        case 'fontface':
            formatFont('face', s_CommandValue);
            break;
        case 'fontsize':
            formatFont('size', s_CommandValue);
            break;
        case 'formatblock':
            format('FormatBlock', s_CommandValue);
            break;
        case 'zoom':
            EWEBCommandZoom.Execute(s_CommandValue);
            b_Change = false;
            break;
        case "mathfloweq":
            ShowMathFlowEQ();
            b_Change = false;
            break;
        case "wordeq":
            ShowWordEQ();
            b_Change = false;
            break;
    }

    if (b_Change) {
        EWEBHistory.DoChange();
        EWEBHistory.Save();
    }
}


function _DoQuickFormat(o_CommandValue) {
    var b_AutoDone = (!!config.AutoDoneQuickFormat);
    if (o_CommandValue && typeof(o_CommandValue) == "object") {
        var b = o_CommandValue["hide"];
        if (typeof(b) != "undefined") {
            b_AutoDone = (!!b);
        }
    } else {
        o_CommandValue = null;
    }

    if (b_AutoDone) {
        showDialog("quickformat.htm?autodone=1", true, false, true, o_CommandValue);
    } else {
        showDialog("quickformat.htm", true, false, false, o_CommandValue);
    }
}


//////////////////////////////////////////////////////////


var EWEB = {
    EditorDocument: null,
    EditorWindow: null,
    CurrMode: null,
    LinkField: null,

    BaseHref: "",
    RootPath: "",
    SitePath: "",
    SendUrl: "",

    ReadyState: "",


    Focus: function () {
        if (EWEB.CurrMode == "CODE" || EWEB.CurrMode == "TEXT") {
            EWEB.EditorTextarea.focus();
            return;
        }

        EWEBSelection.Restore();
        if (EWEBBrowser.IsIE) {
            if (config.FixWidth) {
                if (document.activeElement.id != "eWebEditor") {
                    eWebEditor.focus();
                }
                try {
                    var rng = EWEB.EditorDocument.selection.createRange();
                    if (rng.parentElement().tagName == "BODY") {
                        rng.moveToElementText(EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV"));
                        rng.collapse(true);
                        rng.select();
                    } else {
                        rng.select();
                    }
                } catch (e) {
                }

            } else {
                eWebEditor.focus();
            }

        } else {

            var s = this.EditorWindow.getSelection();
            var o_Body = EWEBTools.GetBodyNode();
            if (s.rangeCount < 1) {
                var o_SelectNode = o_Body;
                var o_FirstChild = o_Body.firstChild;
                if (o_FirstChild) {
                    if (o_FirstChild.tagName == "P" || o_FirstChild.tagName == "DIV") {
                        o_SelectNode = o_FirstChild;
                    }
                }
                var r = this.EditorDocument.createRange();
                r.selectNodeContents(o_SelectNode);
                r.collapse(true);
                s.addRange(r);
            }

            if (EWEBBrowser.IsIE11P) {
                $("eWebEditor").focus();
            } else {
                this.EditorWindow.focus();
                this.EditorDocument.body.focus();
                if (config.FixWidth) {
                    o_Body.focus();
                }
            }
        }
    },


    Init: function () {
        if (!config.L) {
            return;
        }
        if (!EWEBBrowser.IsCompatible) {
            return;
        }

        this.SitePath = EWEBParam.Proto + "://" + EWEBParam.H + (EWEBParam.HPort ? ":" + EWEBParam.HPort : "");

        var s = document.location.pathname;
        this.RootPath = s.substr(0, s.length - 15);

        this.BaseHref = "";
        if (config.BaseHref != "") {
            this.BaseHref = "<base href='" + this.SitePath + config.BaseHref + "'></base>";
        }

        if (EWEBParam.ExtCSS) {
            this.ExtCSS = "<link href='" + _Relative2fullpath(EWEBParam.ExtCSS) + "' type='text/css' rel='stylesheet'>";
        } else {
            this.ExtCSS = "";
        }

        if (EWEBParam.Skin) {
            config.Skin = EWEBParam.Skin;
        }
        if (EWEBParam.FixWidth) {
            config.FixWidth = EWEBParam.FixWidth;
        }
        if (EWEBParam.AreaCssMode) {
            config.AreaCssMode = EWEBParam.AreaCssMode;
        }
        if (EWEBParam.TitleImage) {
            config.TitleImage = EWEBParam.TitleImage;
        }
        if (EWEBParam.ReadOnly) {
            config.InitMode = "VIEW";
        }

        this.ActFixParam = "?style=" + EWEBParam.StyleName + "&cusdir=" + EWEBParam.CusDir + "&sparams=" + config.SParams + "&skey=" + EWEBParam.SKey + "&h=" + EWEBParam.H;
        if (config.FSPath) {
            this.SendUrl = this.SitePath + this.RootPath + "/" + config.ServerExt + "/upload." + config.ServerExt + this.ActFixParam + "&ws=1&fs=" + config.FSPath;
            this.UploadAct = config.FSPath + config.ServerExt + "/upload." + config.ServerExt + this.ActFixParam + "&ws=" + this.SitePath + this.RootPath + "/";
            this.BrowseAct = config.FSPath + config.ServerExt + "/browse." + config.ServerExt + this.ActFixParam + "&ws=" + this.SitePath + this.RootPath + "/";
        } else {
            this.SendUrl = this.SitePath + this.RootPath + "/" + config.ServerExt + "/upload." + config.ServerExt + this.ActFixParam;
            this.UploadAct = this.SendUrl;
            this.BrowseAct = this.SitePath + this.RootPath + "/" + config.ServerExt + "/browse." + config.ServerExt + this.ActFixParam;
        }

        EWEBTools.AddEventListener(document, 'contextmenu', _Doc_OnCancel);
        EWEBTools.AddEventListener(document, 'dragstart', _Doc_OnCancel);
        EWEBTools.AddEventListener(document, 'selectstart', _Doc_OnCancel);
        EWEBTools.AddEventListener(document, 'select', _Doc_OnCancel);


        if (EWEBBrowser.IsStandardMode) {
            window.onresize = function (e) {
                _StandardModeResize();
            };
        }


    }

};

function focus() {
    EWEB.Focus();
}

function setReadOnly(s_Mode) {
    if (s_Mode != "1" && s_Mode != "2") {
        s_Mode = "";
    }
    EWEB.ReadOnly = EWEB.ReadOnly || "";
    if (EWEB.ReadOnly == s_Mode) {
        return;
    }

    var o_SB = $("eWebEditor_SB");
    var o_SBMode = $("eWebEditor_SB_Mode");
    if (o_SB) {
        switch (s_Mode) {
            case "1":
                o_SB.style.display = "";
                o_SBMode.style.display = "none";
                break;
            case "2":
                o_SB.style.display = "none";
                break;
            default:
                o_SB.style.display = "";
                o_SBMode.style.display = "";
        }
    }

    EWEB.ReadOnly = s_Mode;
    if (EWEB.ReadyState == "complete") {
        if (s_Mode == "") {
            setMode("EDIT");
        } else {
            setMode("VIEW");
        }
    }
}


EWEB.MakeEditable = function () {
    var o_Doc = this.EditorDocument;

    if (EWEBBrowser.IsIE || EWEBBrowser.IsIE11P) {
        if (config.FixWidth) {
            o_Doc.body.contentEditable = false;
            var o_Div = o_Doc.getElementById("eWebEditor_FixWidth_DIV");
            o_Div.contentEditable = true;
        } else {
            o_Doc.body.contentEditable = true;
        }

        o_Doc.execCommand("2D-Position", true, true);

    } else {
        try {
            var o_Body = o_Doc.body;
            if ('contentEditable' in o_Body) {
                o_Body.contentEditable = true;
            }
            if ('spellcheck' in o_Body) {
                o_Body.spellcheck = false;
            }

            o_Doc.execCommand("styleWithCSS", false, "true");
            o_Doc.execCommand("enableInlineTableEditing", false, "false");
            if (config.EnterMode == "2") {
                o_Doc.execCommand("insertBrOnReturn", false, "true");
            }
            if (config.FixWidth) {
                o_Body.contentEditable = false;
                var o_Div = o_Doc.getElementById("eWebEditor_FixWidth_DIV");
                o_Div.contentEditable = true;
            }
        } catch (e) {

        }

    }
};


function _Doc_OnCancel(e) {
    if (EWEB.CurrMode == "EDIT" || EWEB.CurrMode == "VIEW") {
        return EWEBTools.CancelEvent(e);
    }
}

function _StandardModeResize() {
    if (!EWEBBrowser.IsStandardMode || !EWEB.CurrMode) {
        return;
    }

    var ifr_Editarea = document.getElementById('eWebEditor');
    if (ifr_Editarea) {
        var o_Cell = ifr_Editarea.parentNode;
        if (o_Cell.clientWidth > 0) {
            var o_ViewSize = EWEBTools.GetViewPaneSize(window);
            var n_ViewW = o_ViewSize.Width - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "padding-left")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "padding-right")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "border-left-width")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "border-right-width"));
            var n_ViewH = o_ViewSize.Height - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "padding-top")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "padding-bottom")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "border-top-width")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(document.body, "border-bottom-width"));
            var o_LayoutTable = $("eWebEditor_Layout");
            o_LayoutTable.style.width = n_ViewW + "px";
            o_LayoutTable.style.height = n_ViewH + "px";

            var n_TBH = $("eWebEditor_Toolbar").offsetHeight;
            var n_SBH = 0;
            if (config.StateFlag) {
                n_SBH = $("eWebEditor_SB").offsetHeight;
            }
            var n_CellClientH = n_ViewH - n_TBH - n_SBH;
            var n_CellW = n_ViewW - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-left")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-right")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "border-left-width")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "border-right-width"));
            var n_CellH = n_CellClientH - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-top")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "padding-bottom")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "border-top-width")) - _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Cell, "border-bottom-width"));
            o_Cell.style.height = n_CellH + "px";

            if (EWEB.CurrMode == "EDIT" || EWEB.CurrMode == "VIEW") {
                var n_BorderTop = _ParseInt0(EWEBTools.GetCurrentElementStyle(ifr_Editarea, "border-top-width"));
                var n_BorderBottom = _ParseInt0(EWEBTools.GetCurrentElementStyle(ifr_Editarea, "border-bottom-width"));
                var n_BorderLeft = _ParseInt0(EWEBTools.GetCurrentElementStyle(ifr_Editarea, "border-left-width"));
                var n_BorderRight = _ParseInt0(EWEBTools.GetCurrentElementStyle(ifr_Editarea, "border-right-width"));
                ifr_Editarea.style.width = (n_CellW - n_BorderLeft - n_BorderRight) + 'px';
                ifr_Editarea.style.height = (n_CellH - n_BorderTop - n_BorderBottom) + 'px';
                _StandardModeSetMinHeight();
            } else {
                var n_Ajust = (n_ViewH) - (n_TBH + n_SBH + o_Cell.offsetHeight);
                if (n_Ajust != 0) {
                    o_Cell.style.height = (n_CellH + n_Ajust) + "px";
                }
            }

            window.scrollTo(0, 0);

            return;
        }
    }
    window.setTimeout(_StandardModeResize, 10);
}

function _StandardModeSetMinHeight() {
    var ifr_Editarea = $('eWebEditor');
    if (config.FixWidth) {
        if (EWEBBrowser.IsStandardMode) {
            var o_Doc = ifr_Editarea.contentWindow.document;
            var o_Body = o_Doc.body;
            var o_Div = o_Doc.getElementById("eWebEditor_FixWidth_DIV");
            if (!o_Div) {
                return;
            }
            var n_BodyPaddingTop = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Body, "padding-top"));
            var n_BodyPaddingBottom = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Body, "padding-bottom"));
            var n_DivBorderTop = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Div, "border-top-width"));
            var n_DivBorderBottom = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Div, "border-bottom-width"));
            var n_DivPaddingTop = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Div, "padding-top"));
            var n_DivPaddingBottom = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Div, "padding-bottom"));
            o_Div.style.minHeight = (ifr_Editarea.clientHeight - n_BodyPaddingTop - n_BodyPaddingBottom - n_DivBorderTop - n_DivBorderBottom - n_DivPaddingTop - n_DivPaddingBottom) + "px";
        }
    } else {
        if (EWEBBrowser.IsStandardMode && EWEBBrowser.IsIE) {
            var o_Body = ifr_Editarea.contentWindow.document.body;
            var n_BodyPaddingTop = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Body, "padding-top"));
            var n_BodyPaddingBottom = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Body, "padding-bottom"));
            o_Body.style.minHeight = (ifr_Editarea.clientHeight - n_BodyPaddingTop - n_BodyPaddingBottom) + "px";
        }
    }
}


//////////////////////////////////////////////////////////

var config = new Object();

//////////////////////////////////////////////////////////


window.onload = _Win_Onload;

function _Win_Onload() {
    if (parent == window) {
        return;
    }
    if (!config.L) {
        return;
    }
    if (EWEB.ReadyState) {
        return;
    }
    EWEB.ReadyState = "loading";

    if (!EWEBBrowser.IsWindow) {
        if (config.AutoDetectPaste == "1") {
            config.AutoDetectPaste = "0";
        }
        config.MFUEnable = "0";
    }

    EWEBTools.RegisterDollarFunction(window);

    if (!EWEBBrowser.IsCompatible) {
        return;
    }

    EWEB.LinkField = parent.document.getElementsByName(EWEBParam.LinkField)[0];
    if (!EWEB.LinkField) {
        EWEB.LinkField = parent.document.getElementById(EWEBParam.LinkField);
    }

    if (!EWEB.LinkField) {
        alert('[EWEBEDITOR] The element with id or name "' + EWEBParam.LinkField + '" was not found.');
        return;
    }

    if (EWEB.LinkField.tagName == "TEXTAREA" || EWEB.LinkField.tagName.substring(0, 5) == "INPUT") {
        EWEB.LinkType = "INPUT";
    } else {
        EWEB.LinkType = "OTHER";
    }


    if (EWEBBrowser.IsStandardMode) {
        _StandardModeResize();
    }
    //initHistory();
    EWEBToolbar.InitTB();

    EWEBTools.DisableSelection(document.body);
    if (EWEBBrowser.IsIE) {
        //$("eWebEditor").unselectable = "";
        //$("eWebEditorTextarea").unselectable = "";
    } else {
        $("eWebEditorTextarea").style.MozUserSelect = "text";
    }

    EWEB.EditorWindow = $("eWebEditor").contentWindow;
    EWEB.EditorDocument = EWEB.EditorWindow.document;
    EWEB.EditorTextarea = $("eWebEditorTextarea");

    if (!EWEBBrowser.IsCompatible) {
        config.InitMode = "TEXT";
    }

    if ($("D_ContentFlag").value == "0") {
        var s_LinkValue = (EWEB.LinkType == "INPUT") ? EWEB.LinkField.value : EWEB.LinkField.innerHTML;
        $("D_ContentEdit").value = s_LinkValue;
        $("D_ContentLoad").value = s_LinkValue;
        $("D_CurrMode").value = config.InitMode;
        $("D_ContentFlag").value = "1";
    }

    setReadOnly(EWEBParam.ReadOnly);
    setMode($("D_CurrMode").value, true);

    _SetLinkedField();

    if (EWEBParam.AutoGrow) {
        window.setInterval(_AutoGrow, 1000);
    }

    EWEBTools.AddEventListener(EWEB.EditorTextarea, 'keyup', _EditorTextarea_OnKeyUp);
    EWEBTools.AddEventListener(EWEB.EditorTextarea, 'paste', _EditorTextarea_OnPaste);
    EWEBTools.AddEventListener(EWEB.EditorTextarea, 'cut', _EditorTextarea_OnCut);
    EWEBTools.AddEventListener(window, 'scroll', _Win_Scroll);

    if (EWEBBrowser.IsIE) {
        _FireLoadComplete();
    } else {
        window.setTimeout(_FireLoadComplete, 200);
    }
}

function _FireLoadComplete() {
    EWEB.ReadyState = "complete";
    _FireEvent({flag: "LoadComplete"});
}

function _FireChange() {
    EWEBTools.SetTimeout(_FireEvent, 400, null, [{flag: "Change"}]);
}

function _FireEvent(o_Ev) {
    o_Ev.id = EWEBParam.InstanceId;
    o_Ev.linkid = EWEBParam.LinkField;
    o_Ev.win = window;
    o_Ev.doc = EWEB.EditorDocument;
    try {
        parent.EWEBEDITOR.EVENT(o_Ev);
    } catch (e) {
    }

    var o_Ret;
    try {
        o_Ret = parent.EWEBEDITOR_EVENT(o_Ev);
    } catch (e) {
    }

    if ((o_Ev.flag == "AfterImportWord") && (o_Ev.action == "paste")) {
        _FireEvent({flag: "Paste", type: "word"});
    }
    if ((o_Ev.flag == "AfterImportExcel") && (o_Ev.action == "paste")) {
        _FireEvent({flag: "Paste", type: "excel"});
    }

    return (o_Ret && typeof(o_Ret) == "object") ? o_Ret : {};
}

function _SetLinkedField() {
    if (EWEBParam.AttachEv == "0") {
        return;
    }
    if (!EWEB.LinkField) {
        return;
    }
    if (EWEB.LinkType != "INPUT") {
        return;
    }
    var o_Form = EWEB.LinkField.form;
    if (!o_Form) {
        return;
    }

    EWEBTools.AddEventListener(o_Form, 'submit', _AttachSubmit);

    if (!o_Form.submitEditor) {
        o_Form.submitEditor = new Array();
    }
    o_Form.submitEditor[o_Form.submitEditor.length] = _AttachSubmit;
    if (!o_Form.originalSubmit) {
        o_Form.originalSubmit = o_Form.submit;
        o_Form.submit = function () {
            if (this.submitEditor) {
                for (var i = 0; i < this.submitEditor.length; i++) {
                    try {
                        this.submitEditor[i]();
                    } catch (e) {
                    }
                }
            }
            this.originalSubmit();
        };
    }

    EWEBTools.AddEventListener(o_Form, 'reset', _AttachReset);

    if (!o_Form.resetEditor) o_Form.resetEditor = new Array();
    o_Form.resetEditor[o_Form.resetEditor.length] = _AttachReset;
    if (!o_Form.originalReset) {
        o_Form.originalReset = o_Form.reset;
        o_Form.reset = function () {
            if (this.resetEditor) {
                for (var i = 0; i < this.resetEditor.length; i++) {
                    try {
                        this.resetEditor[i]();
                    } catch (e) {
                    }
                }
            }
            this.originalReset();
        };
    }
}

function _AttachSubmit() {
    var o_Form = EWEB.LinkField.form;
    if (!o_Form) {
        return;
    }

    var s_Html = getHTML();

    if (config.PaginationMode != "0" && config.PaginationAutoFlag != "0") {
        if (EWEB.CurrMode != "EDIT") {
            setMode("EDIT");
        }

        var b = true;
        if (config.PaginationAutoFlag == "1") {
            var els = EWEB.EditorDocument.getElementsByTagName("IMG");
            for (var i = 0; i < els.length; i++) {
                var s_Attr = els[i].getAttribute("_ewebeditor_fake_tag", 2);
                if (s_Attr) {
                    if (s_Attr.toLowerCase() == "pagination") {
                        b = false;
                        break;
                    }
                }
            }
        }
        if (b) {
            EWEBPagination.Auto(config.PaginationAutoNum);
            s_Html = getHTML();
        }
    }

    $("D_ContentEdit").value = s_Html;
    _SplitTextField(EWEB.LinkField, s_Html);
}

function _AttachReset() {
    setHTML($("D_ContentLoad").value);
}


function _SubmitLinkForm() {
    if (EWEB.LinkType != "INPUT") {
        return;
    }

    var o_Form = EWEB.LinkField.form;
    if (!o_Form) {
        return;
    }
    if (o_Form.onsubmit) {
        if (o_Form.onsubmit()) {
            o_Form.submit();
        }
    } else {
        o_Form.submit();
    }
}


function _Win_Scroll() {
    try {
        var n = document.body.scrollTop;
        if (n && n > 0) {
            document.body.scrollTop = 0;
        }
    } catch (er) {
    }

    return true;
}

function _EditorTextarea_OnKeyUp(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    var n_KeyCode = e.keyCode || e.which;
    if (!(e.ctrlKey || e.altKey)) {
        //PageUp,PageDown,End,Home,Left,Up,Right,Down,ctrl,alt
        if (EWEBTools.ArrayIndexOf([33, 34, 35, 36, 37, 38, 39, 40, 17, 18], n_KeyCode) < 0) {
            _FireChange();
        }
    }

    return true;
}

function _EditorTextarea_OnPaste() {
    _FireChange();
    return true;
}

function _EditorTextarea_OnCut() {
    _FireChange();
    return true;
}

function _Iframe_Doc_OnHelp(e) {
    showDialog('about.htm');
    return EWEBTools.CancelEvent(e);
}


function _Iframe_Doc_OnPaste(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    if (EWEBBrowser.IsAllIE) {
        return EWEBTools.CancelEvent(e);
    } else {
        if (config.AutoDetectPaste == "0") {
            return true;
        } else {
            return EWEBTools.CancelEvent(e);
        }
    }
}

function _Iframe_Doc_OnCopy(e) {
    if (!EWEBBrowser.IsChrome) {
        return true;
    }

    try {
        var s_Html = getSelectedHTML();
        var s_Txt = getSelectedText();
        s_Txt = s_Txt.replace(/\n/gi, "\r\n");
        e.clipboardData.setData("text/html", s_Html);
        e.clipboardData.setData("text/plain", s_Txt);
        e.preventDefault();
    } catch (er) {
    }
}


function _Iframe_Doc_OnKeyUp(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    var n_KeyCode = e.keyCode || e.which;
    var s_Key = String.fromCharCode(n_KeyCode).toUpperCase();

    if (n_KeyCode == 8) {
        var o_Body = EWEBTools.GetBodyNode();
        if (o_Body.innerHTML == "") {
            o_Body.innerHTML = _GetInitEmptyHtml();
        }
    }

    //Enter,Backspace,Del
    //PageUp,PageDown,End,Home,Left,Up,Right,Down
    if (e.ctrlKey || ((n_KeyCode >= 33) && (n_KeyCode <= 40)) || (n_KeyCode == 13) || (n_KeyCode == 8) || (n_KeyCode == 46)) {
        EWEBToolbar.CheckTBStatus();
    }

    if (_IframeDocKeyDownFireChange) {
        _FireChange();
    }

    return true;
}

var _IframeDocKeyDownFireChange;

function _Iframe_Doc_OnKeyDown(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    var n_KeyCode = e.keyCode || e.which;
    var s_Key = String.fromCharCode(n_KeyCode).toUpperCase();

    var b_Cancel = false;
    var b_Change = false;


    if (n_KeyCode == 112 && !EWEBBrowser.IsIE) {		// F1
        exec("about");
        b_Cancel = true;
    } else if (n_KeyCode == 113) {						// F2
        exec("showborders");
        b_Cancel = true;
    } else if (n_KeyCode == 114) {						// F3
        exec("showblocks");
        b_Cancel = true;
    }


    if (!b_Cancel && e.ctrlKey) {

        if (n_KeyCode == 13) {		// Ctrl+Enter
            _SubmitLinkForm();
            b_Cancel = true;
        } else if (n_KeyCode == 187 || n_KeyCode == 107) {		// Ctrl++
            exec("sizeplus");
            b_Cancel = true;
        } else if (n_KeyCode == 189 || n_KeyCode == 109) {		// Ctrl+-
            exec("sizeminus");
            b_Cancel = true;
        } else if (s_Key == "1") {		// Ctrl+1
            exec("modecode");
            b_Cancel = true;
        } else if (s_Key == "2") {		// Ctrl+2
            exec("modeedit");
            b_Cancel = true;
        } else if (s_Key == "3") {		// Ctrl+3
            exec("modetext");
            b_Cancel = true;
        } else if (s_Key == "4") {		// Ctrl+4
            exec("modeview");
            b_Cancel = true;
        } else if (s_Key == "A") {		//Ctrl+A
            exec("selectall");
            b_Cancel = true;
        } else if (s_Key == "R") {		// Ctrl+R
            exec("findreplace");
            b_Cancel = true;
        } else if (s_Key == "Z") {		// Ctrl+Z
            exec("undo");
            b_Cancel = true;
        } else if (s_Key == "Y") {		// Ctrl+Y
            exec("redo");
            b_Cancel = true;
        }
    }

    if (!b_Cancel && EWEBBrowser.IsIE && (n_KeyCode == 8) && (EWEBSelection.GetType() == "Control")) {
        EWEBSelection.Delete();
        b_Cancel = true;
        b_Change = true;
    }

    if (!b_Cancel && EWEBSelection.GetType() == "Text") {
        if (!(e.ctrlKey && s_Key != "D" && s_Key != "V") && !e.metaKey && !e.shiftKey && !e.altKey) {
            var el_Src = EWEBSelection.GetParentElement();
            if (el_Src && el_Src.className == "ewebeditor_template") {
                el_Src.className = "";
            }
        }
    }

    if (!b_Cancel && e.ctrlKey) {
        if (s_Key == "D") {		// Ctrl+D
            exec("pasteword");
            b_Cancel = true;
        } else if (s_Key == "V") {	// Ctrl+V
            if (config.AutoDetectPaste != "0" || EWEBBrowser.IsAllIE) {
                window.setTimeout("exec('paste')", 10);
                b_Cancel = true;
            }
        }
    }


    if ((!b_Cancel) && (n_KeyCode == 8 || n_KeyCode == 9)) {
        var o_CursorParent = EWEBSelection.GetStartCursorParent();
        if (o_CursorParent) {
            var s_TextIndent = o_CursorParent.style.textIndent;
            //Backspace
            if (n_KeyCode == 8) {
                if (s_TextIndent) {
                    o_CursorParent.style.textIndent = "";
                    b_Cancel = true;
                    b_Change = true;
                }
            }
            //Tab
            if (n_KeyCode == 9) {
                if (!s_TextIndent) {
                    o_CursorParent.style.textIndent = "2em";
                    b_Cancel = true;
                    b_Change = true;
                }
            }
        }
    }

    if (!b_Cancel) {
        //Enter,Backspace,Del
        if ((n_KeyCode == 13) || (n_KeyCode == 8) || (n_KeyCode == 46)) {
            EWEBHistory.Save();
            EWEBHistory.DoChange();
            //PageUp,PageDown,End,Home,Left,Up,Right,Down
        } else if ((n_KeyCode >= 33) && (n_KeyCode <= 40)) {
            EWEBHistory.Save();
            //Ctrl+A,F
        } else if (!((e.ctrlKey && s_Key == "A") || (e.ctrlKey && s_Key == "F"))) {
            EWEBHistory.DoChange();
        }
    }


    if ((config.EnterMode == "2") && (n_KeyCode == 13) && EWEBBrowser.IsIE) {
        var rng = EWEB.EditorDocument.selection.createRange();
        if (e.shiftKey) {
            var p = rng.parentElement();
            if (p.tagName != "P" || p.innerHTML == "") {
                rng.pasteHTML("&nbsp;");
                rng.select();
                rng.collapse(false);
            }

            try {
                rng.pasteHTML("</P><P id=eWebEditor_Temp_P>");
            } catch (err) {
                return false;
            }

            e.cancelBubble = true;
            e.returnValue = false;

            var el = EWEB.EditorDocument.getElementById("eWebEditor_Temp_P");
            if (el.innerHTML == "") {
                el.innerHTML = "&nbsp;";
            }
            rng.moveToElementText(el);
            rng.select();
            rng.collapse(false);
            rng.select();
            el.removeAttribute("id");
        } else {
            try {
                rng.pasteHTML("<br>");
            } catch (err) {
                return false;
            }
            e.cancelBubble = true;
            e.returnValue = false;
            rng.select();
            rng.moveEnd("character", 1);
            rng.moveStart("character", 1);
            rng.collapse(false);
        }
        b_Cancel = true;
        b_Change = true;
    }

    if (n_KeyCode == 32) {
        b_Cancel = true;
        b_Change = true;
        insertHTML("&ensp;");
    }

    _IframeDocKeyDownFireChange = (!b_Cancel || b_Change);

    if (b_Cancel) {
        return EWEBTools.CancelEvent(e);
    } else {
        return true;
    }
}


function _Iframe_Doc_OnMouseDown(e) {
    if (!e) {
        e = window.event;
    }

    if (!EWEBBrowser.IsAllIE) {
        var el = e.srcElement || e.target;
        if (el.tagName == "IMG") {
            if (EWEBBrowser.IsEdge) {
                EWEBTools.SetTimeout(EWEBSelection.SelectNode, 200, EWEBSelection, [el]);
            } else {
                EWEBSelection.SelectNode(el);
            }
        }
    }
}

function _Iframe_Doc_OnMouseUp(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    EWEBTableResize.MU2(e);

    if (e.button != 2) {
        EWEBCommandFormatBrush.Brush();
    }

    EWEBHistory.Save();
    EWEBToolbar.CheckTBStatus();
}

function _Iframe_Doc_OnMouseMove(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    EWEBTableResize.MM(e);
}

function _Iframe_Doc_OnClick(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    var el = e.srcElement || e.target;

    if (EWEBBrowser.IsIE11P) {
        var el_Html = EWEB.EditorDocument.getElementsByTagName("HTML")[0];
        if (el_Html == el) {
            EWEB.EditorDocument.body.focus();
            return;
        }
    }

    if (el.className == "ewebeditor_template") {
        if (EWEBBrowser.IsIE) {
            var oRange = EWEB.EditorDocument.body.createTextRange();
            oRange.moveToElementText(el);
            oRange.select();
        } else {
            var oRange = EWEB.EditorDocument.createRange();
            oRange.selectNodeContents(el);
            var oSel = EWEBSelection.GetSelection();
            oSel.removeAllRanges();
            oSel.addRange(oRange);
        }
    }
}

function _Iframe_Doc_OnDblClick(e) {
    if (!e) {
        e = eWebEditor.event;
    }

    var el = e.srcElement || e.target;
    if (el.tagName == "IMG") {
        var a_Attr = el.getAttribute("_tag", 2);
        if (a_Attr == "math") {
            exec("mathfloweq");
        } else if (a_Attr == "wordeq") {
            exec("wordeq");
        } else {
            var s_FakeTag = el.getAttribute("_ewebeditor_fake_tag", 2);
            if (!s_FakeTag) {
                exec("image");
            } else if (s_FakeTag == "flash") {
                exec("flash");
            } else if (s_FakeTag == "mediaplayer6" || s_FakeTag == "mediaplayer7" || s_FakeTag == "realplayer" || s_FakeTag == "quicktime" || s_FakeTag == "flv" || s_FakeTag == "vlc" || s_FakeTag == "video" || s_FakeTag == "audio") {
                exec("media");
            }
        }
    }
}


function _Iframe_Doc_OnContextMenu(e) {
    if (EWEBCommandFormatBrush.IsOnState()) {
        EWEBCommandFormatBrush.Stop();
    } else {
        EWEBMenu.ShowContextMenu(e);
    }

    EWEBTools.CancelEvent(e);
    return false;
}


function _Iframe_Doc_OnDragEnd() {
    EWEBHistory.Save();
    return true;
}


function _Relative2fullpath(s_Url) {
    if (s_Url.indexOf("://") >= 0) {
        return s_Url;
    }
    if (s_Url.substr(0, 1) == "/") {
        return s_Url;
    }

    var s_Path = EWEB.RootPath;
    while (s_Url.substr(0, 3) == "../") {
        s_Url = s_Url.substr(3);
        s_Path = s_Path.substring(0, s_Path.lastIndexOf("/"));
    }
    return s_Path + "/" + s_Url;
}


function insertHTML(s_Html) {
    if (_IsModeView()) {
        return;
    }

    switch (EWEB.CurrMode) {
        case "EDIT":
            EWEBSelection.Restore();
            EWEB.Focus();

            s_Html = EWEBFake.Normal2Fake(s_Html);

            if (!window.getSelection) {
                if (EWEB.EditorDocument.selection.type.toLowerCase() == "control") {
                    EWEB.EditorDocument.selection.clear();
                }

                s_Html = '<span id="__ewebeditor_temp_remove__" style="display:none;">eWebEditor</span>' + s_Html;
                EWEB.EditorDocument.selection.createRange().pasteHTML(s_Html);
                EWEB.EditorDocument.getElementById('__ewebeditor_temp_remove__').removeNode(true);

            } else {
                var o_DocFrag = EWEB.EditorDocument.createDocumentFragment();
                var o_TmpDiv = EWEB.EditorDocument.createElement('div');
                o_TmpDiv.innerHTML = s_Html;

                var o_Child, o_LastNode;
                while ((o_Child = o_TmpDiv.firstChild)) {
                    o_LastNode = o_DocFrag.appendChild(o_TmpDiv.removeChild(o_Child));
                }

                var o_Sel = EWEB.EditorWindow.getSelection();
                var o_Range = o_Sel.getRangeAt(0);
                o_Range.deleteContents();

                var b_DoneInsert = false;
                if (/<(p|div|table|td|th|tr|h[1-9]|ul|ol)(?=[\s>])/gi.test(s_Html)) {
                    var o_RngParent = EWEBSelection.GetParentElement();
                    if (o_RngParent && o_RngParent != EWEBTools.GetBodyNode()) {
                        var o_RngRoot;
                        var o_RngParentParagraph = EWEBTools.GetParentNodeByTags(o_RngParent, ["P", "DIV", "TD", "TH", "TABLE", "UL", "OL", "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9"]);
                        if (o_RngParentParagraph) {
                            if (EWEBTools.ArrayIndexOf(["P", "DIV", "UL", "OL", "H1", "H2", "H3", "H4", "H5", "H6", "H7", "H8", "H9"], o_RngParentParagraph.tagName) >= 0) {
                                o_RngRoot = o_RngParentParagraph;
                            } else {
                                o_RngRoot = EWEBTools.GetParentNodeUnder(o_RngParent, o_RngParentParagraph);
                            }
                        } else {
                            o_RngRoot = EWEBTools.GetParentNodeUnder(o_RngParent, EWEBTools.GetBodyNode());
                        }

                        var s_RootInnerHtml = o_RngRoot.innerHTML;
                        if (/^(&nbsp;|\s|\u2002|\u2003|\u2009|&ensp;)*?$/gi.test(s_RootInnerHtml) || /^\s*?<br\/?>\s*?$/gi.test(s_RootInnerHtml)) {
                            o_Range.selectNode(o_RngRoot);
                            o_Range.deleteContents();
                        } else {
                            var o_TmpSpan = EWEB.EditorDocument.createElement("span");
                            o_Range.insertNode(o_TmpSpan);
                            var o_CloneRoot = o_RngRoot.cloneNode(true);
                            var n_Level = 0;
                            var a_Index = [];
                            var o_TmpSpanParent = o_TmpSpan;
                            while (o_TmpSpanParent && o_TmpSpanParent.tagName != "BODY" && o_TmpSpanParent != o_RngRoot) {
                                a_Index[n_Level] = 0;
                                var o_Sib = o_TmpSpanParent.previousSibling;
                                while (o_Sib) {
                                    a_Index[n_Level] = a_Index[n_Level] + 1;
                                    o_Sib = o_Sib.previousSibling;
                                }
                                o_TmpSpanParent = o_TmpSpanParent.parentNode;
                                n_Level++;
                            }

                            var o_Node1 = o_RngRoot;
                            var o_Node2 = o_CloneRoot;
                            for (var i = n_Level - 1; i >= 0; i--) {
                                o_Node1 = o_Node1.childNodes[a_Index[i]];
                                while (o_Node1.nextSibling) {
                                    EWEBTools.RemoveNode(o_Node1.nextSibling);
                                }
                                if (i == 0) {
                                    EWEBTools.RemoveNode(o_Node1);
                                }

                                o_Node2 = o_Node2.childNodes[a_Index[i]];
                                while (o_Node2.previousSibling) {
                                    EWEBTools.RemoveNode(o_Node2.previousSibling);
                                }
                                if (i == 0) {
                                    EWEBTools.RemoveNode(o_Node2);
                                }
                            }

                            if (o_CloneRoot.innerHTML != "") {
                                o_RngRoot.parentNode.insertBefore(o_CloneRoot, o_RngRoot.nextSibling);
                            }
                            o_RngRoot.parentNode.insertBefore(o_DocFrag, o_RngRoot.nextSibling);
                            if (o_RngRoot.innerHTML == "") {
                                EWEBTools.RemoveNode(o_RngRoot);
                            }

                            b_DoneInsert = true;
                        }

                    }
                }

                if (!b_DoneInsert) {
                    o_Range.insertNode(o_DocFrag);
                }

                if (o_LastNode) {
                    o_Range = EWEB.EditorDocument.createRange();
                    o_Range.setStartAfter(o_LastNode);
                    o_Range.collapse(true);
                    o_Sel.removeAllRanges();
                    o_Sel.addRange(o_Range);
                }

            }
            EWEBToolbar.CheckTBStatus();
            break;
        case "TEXT":
        case "CODE":
            var o_Txt = EWEB.EditorTextarea;
            o_Txt.focus();
            if (document.selection) {
                var o_Rng = document.selection.createRange();
                o_Rng.text = s_Html;
                o_Rng.select();
            } else if (o_Txt.selectionStart || o_Txt.selectionStart == '0') {
                var n_startPos = o_Txt.selectionStart;
                var n_endPos = o_Txt.selectionEnd;
                var n_restoreTop = o_Txt.scrollTop;
                o_Txt.value = o_Txt.value.substring(0, n_startPos) + s_Html + o_Txt.value.substring(n_endPos, o_Txt.value.length);
                if (n_restoreTop > 0) {
                    o_Txt.scrollTop = n_restoreTop;
                }
                o_Txt.selectionStart = n_startPos + s_Html.length;
                o_Txt.selectionEnd = n_startPos + s_Html.length;
            } else {
                o_Txt.value += s_Html;
            }
            break;
    }

    _FireChange();
};


function setHTML(s_Html, b_NotSaveHistory, b_NotFireChange) {
    $("D_ContentEdit").value = s_Html;
    switch (EWEB.CurrMode) {
        case "CODE":
            EWEB.EditorTextarea.value = s_Html;
            break;
        case "TEXT":
            s_Html = _Html2Text(s_Html);
            EWEB.EditorTextarea.value = s_Html;
            break;
        case "EDIT":
            EWEBSelection.Release();
            if (EWEBBrowser.IsIE10Compat && s_Html == "") {
                s_Html = "<p></p>";
            }
            EWEB.EditorDocument.open('text/html', 'replace');
            EWEB.EditorDocument.write(_GetHtmlWithHeader(EWEBFake.Normal2Fake(s_Html)));
            EWEB.EditorDocument.close();

            EWEB.MakeEditable();

            EWEBTools.AddEventListener(EWEB.EditorDocument.body, 'paste', _Iframe_Doc_OnPaste);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'help', _Iframe_Doc_OnHelp);
            EWEBTools.AddEventListener(EWEB.EditorDocument.body, 'dragend', _Iframe_Doc_OnDragEnd);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'keydown', _Iframe_Doc_OnKeyDown);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'keyup', _Iframe_Doc_OnKeyUp);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'contextmenu', _Iframe_Doc_OnContextMenu);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'mousedown', _Iframe_Doc_OnMouseDown);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'mouseup', _Iframe_Doc_OnMouseUp);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'mousemove', _Iframe_Doc_OnMouseMove);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'click', _Iframe_Doc_OnClick);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'dblclick', _Iframe_Doc_OnDblClick);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'copy', _Iframe_Doc_OnCopy);

            EWEBImageResize._ReloadDoc();

            if (EWEBBrowser.IsAllIE) {
                if (config.FixWidth) {
                    EWEBTools.AddEventListener(EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV"), 'beforedeactivate', function () {
                        EWEBSelection.Save();
                    });
                } else {
                    EWEBTools.AddEventListener(EWEB.EditorDocument, 'beforedeactivate', function () {
                        EWEBSelection.Save();
                    });
                }
            }

            _StandardModeSetMinHeight();

            break;
        case "VIEW":
            EWEB.EditorDocument.open('text/html', 'replace');
            EWEB.EditorDocument.write(_GetHtmlWithHeader(s_Html));
            EWEB.EditorDocument.close();

            EWEBTools.AddEventListener(EWEB.EditorDocument, 'help', _Iframe_Doc_OnHelp);
            EWEBTools.AddEventListener(EWEB.EditorDocument, 'contextmenu', EWEBTools.CancelEvent);
            break;
    }


    if (!b_NotSaveHistory && (EWEB.CurrMode == "EDIT")) {
        EWEBHistory.DoChange();
        EWEBHistory.Save();
    }

    if (!b_NotFireChange) {
        _FireChange();
    }
}


function getHTML() {
    var f_Empty = function (s) {
        var s_Tmp = s.replace(/\s+/gi, "");
        s_Tmp = s_Tmp.toLowerCase();
        if ((s_Tmp == "<p>&nbsp;</p>") || (s_Tmp == "<p></p>") || (s_Tmp == "<br>") || (s_Tmp == "<p><br></p>")) {
            return "";
        }
        return s;
    };

    var s_Html;
    switch (EWEB.CurrMode) {
        case "CODE":
            s_Html = EWEB.EditorTextarea.value;
            break;
        case "EDIT":
            EWEBPagination.Fix();
            if (config.FixWidth) {
                s_Html = EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV").innerHTML;
            } else {
                s_Html = EWEB.EditorDocument.body.innerHTML;
            }
            s_Html = EWEBFake.Fake2Normal(s_Html);
            s_Html = f_Empty(s_Html);
            break;
        case "VIEW":
            s_Html = $("D_ContentEdit").value;
            s_Html = f_Empty(s_Html);
            break;
        case "TEXT":
            s_Html = EWEB.EditorTextarea.value;
            s_Html = HTMLEncode(s_Html);
            break;
        default:
            s_Html = $("D_ContentEdit").value;
            s_Html = f_Empty(s_Html);
            break;
    }

    s_Html = EWEBCodeFormat.Format(s_Html);
    return s_Html;
}

function appendHTML(s_Html) {
    if (_IsModeView()) {
        return;
    }

    switch (EWEB.CurrMode) {
        case "EDIT":
            s_Html = EWEBFake.Normal2Fake(s_Html);
            var o_Body;
            if (config.FixWidth) {
                o_Body = EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV");
            } else {
                o_Body = EWEB.EditorDocument.body;
            }
            o_Body.innerHTML += s_Html;
            break;
        case "CODE":
        case "TEXT":
            EWEB.EditorTextarea.value += s_Html;
            break;
    }
}


function openUploadDialog(v, s_Mode, s_LinkID_SavePathFileName, s_LinkID_SaveFileName, s_LinkID_OriginalFileName, s_ReturnFlag) {
    var s_Type;
    if (typeof(v) == "string") {
        s_Type = v;
    } else {
        s_Type = v.type ? v.type : 'image';
        s_Mode = v.mode ? v.mode : '2';
        s_LinkID_SavePathFileName = v.savepathfilename ? v.savepathfilename : '';
        s_LinkID_SaveFileName = v.savefilename ? v.savefilename : '';
        s_LinkID_OriginalFileName = v.originalfilename ? v.originalfilename : '';
        s_ReturnFlag = v.returnflag ? v.returnflag : '';
    }
    var s_Url = 'i_upload.htm?type=' + s_Type + '&mode=' + s_Mode + '&savepathfilename=' + s_LinkID_SavePathFileName + '&savefilename=' + s_LinkID_SaveFileName + '&originalfilename=' + s_LinkID_OriginalFileName;
    if (s_ReturnFlag) {
        s_Url += '&returnflag=' + s_ReturnFlag;
    }

    EWEBDialog.OpenDialog(s_Url);
}

function Remove() {
    if (EWEBParam.AttachEv == "0") {
        return;
    }

    if (EWEB.LinkType == "INPUT") {
        var o_Form = EWEB.LinkField.form;
        if (o_Form) {
            EWEBTools.RemoveEventListener(o_Form, 'submit', _AttachSubmit);
            EWEBTools.RemoveEventListener(o_Form, 'reset', _AttachReset);
        }
    }
}


function _IsModeEdit() {
    if (EWEB.CurrMode == "EDIT") {
        return true;
    }
    alert(lang["MsgOnlyInEditMode"]);
    return false;
}

function _IsModeView() {
    if (EWEB.CurrMode == "VIEW") {
        alert(lang["MsgCanotSetInViewMode"]);
        return true;
    }
    return false;
}

function _IsInIE() {
    if (EWEBBrowser.IsIE || EWEBBrowser.IsIE11P) {
        return true;
    }

    alert(lang["MsgOnlyForIE"]);
    return false;
}

function _IsInWindow() {
    if (EWEBBrowser.IsWindow) {
        return true;
    }

    alert(lang["MsgOnlyForWindow"]);
    return false;
}

function format(s_CmdName, s_CmdValue) {
    if (!_IsModeEdit()) {
        return;
    }
    EWEB.Focus();
    if ((s_CmdName == "unselect") && (!EWEBBrowser.IsIE)) {
        EWEBSelection.Collapse(true);
    } else if ((s_CmdName == "selectall") && (config.FixWidth)) {
        _SelectAll_FixWidth();
    } else {
        EWEB.EditorDocument.execCommand(s_CmdName, false, s_CmdValue);
    }
    EWEBSelection.Release();
    EWEB.Focus();
    EWEBToolbar.CheckTBStatus();

    if (EWEBTools.ArrayIndexOf(["selectall", "unselect"], s_CmdName) < 0) {
        _FireChange();
    }
}

function _SelectAll_FixWidth() {
    var o_FixDiv = EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV");
    if (EWEBBrowser.IsIE) {
        var r = EWEB.EditorDocument.body.createTextRange();
        r.moveToElementText(o_FixDiv);
        r.select();
    } else {
        var r = EWEB.EditorDocument.createRange();
        r.selectNodeContents(o_FixDiv);
        var s = EWEBSelection.GetSelection();
        s.removeAllRanges();
        s.addRange(r);
    }
}


function _Fontsize2Css(s) {
    var r;
    switch (s + "") {
        case "1":
            r = "8pt";
            break;
        case "2":
            r = "10pt";
            break;
        case "3":
            r = "12pt";
            break;
        case "4":
            r = "14pt";
            break;
        case "5":
            r = "18pt";
            break;
        case "6":
            r = "24pt";
            break;
        case "7":
            r = "36pt";
            break;
        default:
            r = "";
            break;
    }
    return r;
}

function setMode(s_NewMode, b_NotFocus) {
    if (s_NewMode == EWEB.CurrMode) {
        return;
    }

    var b_NotFireChange = true;
    if (s_NewMode == "TEXT") {
        if (EWEB.CurrMode == $("D_CurrMode").value) {
            if (!confirm(lang["MsgHtmlToText"])) {
                return;
            } else {
                b_NotFireChange = false;
            }
        }
    }

    try {
        $("eWebEditor_CODE").className = "SB_Mode_BtnOff";
    } catch (e) {
    }
    try {
        $("eWebEditor_EDIT").className = "SB_Mode_BtnOff";
    } catch (e) {
    }
    try {
        $("eWebEditor_TEXT").className = "SB_Mode_BtnOff";
    } catch (e) {
    }
    try {
        $("eWebEditor_VIEW").className = "SB_Mode_BtnOff";
    } catch (e) {
    }
    try {
        $("eWebEditor_" + s_NewMode).className = "SB_Mode_BtnOn";
    } catch (e) {
    }

    var o_TBEdit = $("eWebEditor_ToolarTREdit");
    var o_TBText = $("eWebEditor_ToolarTRText");
    var o_TBView = $("eWebEditor_ToolarTRView");
    if (EWEB.ReadOnly) {
        o_TBEdit.style.display = "none";
        o_TBText.style.display = "none";
        o_TBView.style.display = "none";
    } else {
        if (s_NewMode == "EDIT") {
            o_TBEdit.style.display = "";
            o_TBText.style.display = "none";
            o_TBView.style.display = "none";
        } else {
            o_TBEdit.style.display = "none";
            if (config.TB2Flag == "1") {
                if (s_NewMode == "VIEW") {
                    o_TBText.style.display = "none";
                    o_TBView.style.display = "";
                } else {
                    o_TBText.style.display = "";
                    o_TBView.style.display = "none";
                }
            } else {
                o_TBText.style.display = "none";
                o_TBView.style.display = "none";
            }
        }
    }

    var s_Html = getHTML();


    if (s_NewMode == "EDIT" || s_NewMode == "VIEW") {
        $("eWebEditor").style.display = "";
        $("eWebEditorTextarea").style.display = "none";
    } else {
        $("eWebEditor").style.display = "none";
        $("eWebEditorTextarea").style.display = "";
        if (s_NewMode == "CODE") {
            $("eWebEditorTextarea").className = "codemode";
        } else {
            $("eWebEditorTextarea").className = "textmode";
        }
    }


    EWEB.CurrMode = s_NewMode;
    $("D_CurrMode").value = s_NewMode;

    _StandardModeResize();
    setHTML(s_Html, false, b_NotFireChange);

    if (s_NewMode == "EDIT") {
        EWEBCommandShowBlocks.RestoreState();
        EWEBCommandShowBorders.RestoreState();
    }
    EWEBToolbar.RefreshModeBtnStatus();

    if (!b_NotFocus) {
        EWEB.Focus();
    }
}


function _GetInnerTextGecko(el) {
    var a_BreakTag = ["p", "div", "h1", "h2", "h3", "h4", "h5", "h6", "pre", "ol", "ul", "fieldset", "form", "table", "tr", "blockquote", "dl", "li", "br"];
    var a_EmptyTag = ["script", "style", "object", "embed"];
    var s_Txt = "";
    var o_Nodes = el.childNodes;
    for (var i = 0; i < o_Nodes.length; i++) {
        if (o_Nodes[i].nodeType == 1) {
            var s_Tag = o_Nodes[i].tagName.toLowerCase();
            if (a_EmptyTag.IndexOf(s_Tag) < 0) {
                if (s_Tag == "pre") {
                    var s_Pre = o_Nodes[i].innerHTML;
                    s_Pre = s_Pre.replace(/<[^>]*?>/gi, "");
                    s_Txt += s_Pre;
                } else {
                    s_Txt += _GetInnerTextGecko(o_Nodes[i]);
                }
            }
            if (a_BreakTag.IndexOf(s_Tag) >= 0) {
                s_Txt += "\n";
            }
        } else if (o_Nodes[i].nodeType == 3) {
            var s_NodeValue = o_Nodes[i].nodeValue;
            s_NodeValue = s_NodeValue.replace(/\n/gi, "");
            s_Txt += s_NodeValue;
        }
    }
    return s_Txt;
}


function showDialog(s_Url, b_MustInModeEdit, b_MustInIE, b_Hide, o_CommandValue) {
    if (b_MustInModeEdit && !_IsModeEdit()) {
        return;
    }
    if (b_MustInIE && !_IsInIE()) {
        return;
    }

    if (s_Url.indexOf(".") < 0) {
        s_Url = s_Url + ".htm";
    }

    EWEBDialog.OpenDialog(s_Url, b_Hide, o_CommandValue);
}


function HTMLEncode(s_Html) {
    if (s_Html == null) {
        return "";
    }
    s_Html = s_Html.replace(/&/gi, "&amp;");
    s_Html = s_Html.replace(/\"/gi, "&quot;");
    s_Html = s_Html.replace(/</gi, "&lt;");
    s_Html = s_Html.replace(/>/gi, "&gt;");
    s_Html = s_Html.replace(/ (?= )/gi, "&nbsp;");
    s_Html = s_Html.replace(/\n/gi, "<br>");
    return s_Html;
}

function HTMLDecode(s_Html) {
    if (s_Html == null) {
        return "";
    }
    s_Html = s_Html.replace(/<br(?=[ \/>]).*?>/gi, "\n");
    s_Html = s_Html.replace(/&nbsp;;/gi, " ");
    s_Html = s_Html.replace(/&quot;/gi, "\"");
    s_Html = s_Html.replace(/&lt;/gi, "<");
    s_Html = s_Html.replace(/&gt;/gi, ">");
    s_Html = s_Html.replace(/&amp;/gi, "&");
    return s_Html;
}


function addUploadFiles(s_OriginalFiles, s_SavedFiles) {
    if (s_OriginalFiles) {
        var a_Original = s_OriginalFiles.split("|");
        var a_Saved = s_SavedFiles.split("|");
        for (var i = 0; i < a_Original.length; i++) {
            if (a_Saved[i]) {
                var s_OriginalFileName = a_Original[i];
                var s_SavePathFileName = a_Saved[i];
                addUploadFile(s_OriginalFileName, s_SavePathFileName);
            }
        }
    }
}

function addUploadFile(s_OriginalFileName, s_SavePathFileName) {
    var s_SaveFileName = s_SavePathFileName.substr(s_SavePathFileName.lastIndexOf("/") + 1);
    doInterfaceUpload(EWEBParam.LinkOriginalFileName, s_OriginalFileName);
    doInterfaceUpload(EWEBParam.LinkSaveFileName, s_SaveFileName);
    doInterfaceUpload(EWEBParam.LinkSavePathFileName, s_SavePathFileName);
    _FireEvent({flag: "AfterUploadOne", savepathfilename: s_SavePathFileName, savefilename: s_SaveFileName, originalfilename: s_OriginalFileName});
}

function doInterfaceUpload(s_LinkName, s_Value) {
    if (s_Value == "") {
        return;
    }

    if (s_LinkName) {
        var o_LinkUpload = parent.document.getElementById(s_LinkName);
        if (!o_LinkUpload) {
            o_LinkUpload = parent.document.getElementsByName(s_LinkName)[0];
        }
        if (o_LinkUpload) {
            if (o_LinkUpload.value != "") {
                o_LinkUpload.value = o_LinkUpload.value + "|";
            }
            o_LinkUpload.value = o_LinkUpload.value + s_Value;

            try {
                o_LinkUpload.onchange();
            } catch (e) {

            }
        }
    }
}

function _SplitTextField(o_LinkField, s_Html) {
    o_LinkField.value = s_Html;

}

function remoteUploadOK() {
    _HideProcessingMsg();
    if (EWEB.LinkField) {
        if (_gsEventUploadAfter) {
            if (typeof(_gsEventUploadAfter) == 'function') {
                _gsEventUploadAfter();
            } else {
                eval("parent." + _gsEventUploadAfter);
            }
        }
    }
    _FireEvent({flag: "AfterRemoteUpload"});
    _FireChange();
}

var _gsEventUploadAfter;

function remoteUpload(s_EventUploadAfter) {
    if (config.AutoRemote != "1") {
        return;
    }
    if (config.AllowRemoteExt == "") {
        return;
    }
    if (EWEB.CurrMode == "TEXT") {
        return;
    }

    _gsEventUploadAfter = s_EventUploadAfter;
    var objField = document.getElementsByName("eWebEditor_UploadText")[0];
    _SplitTextField(objField, getHTML());

    _showProcessingMsg(lang["MsgRemoteUploading"]);
    $("eWebEditor_UploadForm").submit();
}


function localUpload() {
    if (!_IsInWindow()) {
        return;
    }
    if (EWEB.CurrMode == "TEXT") {
        return;
    }
    if (EWEBActiveX.CheckIsRun()) {
        return;
    }

    EWEBActiveX.AsynCallBack("isinstalled", [true],
        function (o_Data) {
            if (!o_Data["Ret"]) {
                EWEBActiveX.SetIsRun(false);
                return;
            }

            _showProcessingMsg(lang["MsgLocalUploading"]);

            var s_HTML = getHTML();
            EWEBActiveX.AsynCallBack("localupload", [s_HTML],
                function (o_Data) {
                    _LocalUploadStatus(o_Data, "");
                }
            );
        }
    );
}


function _LocalUploadStatus(o_Data, s_InsertFlag, o_Event) {
    if (EWEBActiveX.IsError(o_Data["Error"])) {
        _HideProcessingMsg();
        EWEBActiveX.SetIsRun(false);
        return;
    }

    addUploadFiles(o_Data["OriginalFiles"], o_Data["SavedFiles"]);

    var s_HTML = o_Data["Body"];
    if (s_InsertFlag == "insert") {
        insertHTML(s_HTML);
    } else {
        setHTML(s_HTML);
    }

    _HideProcessingMsg();
    EWEBActiveX.SetIsRun(false);

    if (o_Event) {
        _FireEvent(o_Event);
    } else {
        _FireEvent({flag: "AfterLocalUpload"});
    }
}

function _showProcessingMsg(msg) {
    $("msgProcessing").innerHTML = msg;
    var o_Div = $("divProcessing");
    o_Div.style.top = (document.body.clientHeight - parseFloat(o_Div.style.height)) / 2 + "px";
    o_Div.style.left = (document.body.clientWidth - parseFloat(o_Div.style.width)) / 2 + "px";
}

function _HideProcessingMsg() {
    $("divProcessing").style.left = "-10000px";
}

function _GetInitEmptyHtml() {
    if (EWEBBrowser.IsIE11P) {
        return "<p></p>";
    } else if (!EWEBBrowser.IsAllIE) {
        if (config.EnterMode == "2") {
            return '<br>';
        } else {
            return '<p><br></p>';
        }
    }
    return "";
}

function _GetHtmlWithHeader(s_Html) {
    var f_FFEmptyHtml = function (s) {
        if (s == '') {
            return _GetInitEmptyHtml();
        } else {
            return s;
        }
    };

    var s_Header;
    if (EWEBBrowser.IsIE9Compat || EWEBBrowser.IsIE10P) {
        s_Header = '<!DOCTYPE html>';
    } else {
        s_Header = '<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">';
    }
    s_Header += '<html unselectable=on><head>';
    s_Header += '<link href="' + EWEB.RootPath + '/skin/' + config.Skin + '/editarea.css" type="text/css" rel="stylesheet">';
    if (config.AreaCssMode != '1') {
        s_Header += '<link href="' + EWEB.RootPath + '/language/' + lang.ActiveLanguage + '.editarea.css" type="text/css" rel="stylesheet">';
    }

    switch (EWEB.CurrMode) {
        case 'CODE':
            //s_Header += '<link href="' + EWEB.RootPath + '/skin/' + config.Skin + '/editarea.' + lang.ActiveLanguage + '.code.css" type="text/css" rel="stylesheet">';
            break;
        case 'TEXT':
            //s_Header += '<link href="' + EWEB.RootPath + '/skin/' + config.Skin + '/editarea.' + lang.ActiveLanguage + '.text.css" type="text/css" rel="stylesheet">';
            break;
        case 'EDIT':
        case 'VIEW':
            if (config.AreaCssMode != '1') {
                s_Header += '<link href="' + EWEB.RootPath + '/skin/' + config.Skin + '/editarea.' + lang.ActiveLanguage + '.css" type="text/css" rel="stylesheet">';
            }
            s_Header += EWEB.ExtCSS;
            break;
    }

    if (EWEB.CurrMode == "VIEW") {
        s_Header += '<base target="_blank">';
        s_Html = s_Html.replace(/(<a\s[^>]*?target\s*?=\s*?[\'\"]?)\_[a-zA-Z]+([\'\"]?[^>]*?>)/gi, '$1_blank$2');
    }

    s_Header += EWEB.BaseHref + '</head>';

    var s_Ret = s_Header + '<body';
    if (EWEBParam.AutoGrow) {
        s_Ret += ' style="overflow-y:hidden"';
    }

    if (config.FixWidth) {
        s_Ret += ' class="ewebeditor__fixwidth">'
            + '<div id="eWebEditor_FixWidth_DIV" style="width:' + config.FixWidth + ';" >';
        s_Ret += f_FFEmptyHtml(s_Html) + '</div></body></html>';
    } else {
        s_Ret += '>' + f_FFEmptyHtml(s_Html) + '</body></html>';
    }

    return s_Ret;
}

// n_Type  -  0:en; 1:chs; 2:en+chs(1);3,en+chs(2)
function getCount(n_Type) {
    var str = getText();
    str = str.replace(/\n/g, "");
    str = str.replace(/\r/g, "");

    var l = str.length;
    var n = 0;
    for (var i = 0; i < l; i++) {
        if (str.charCodeAt(i) < 0 || str.charCodeAt(i) > 255) {
            if (n_Type != 0) {
                n++;
                if (n_Type == 3) {
                    n++;
                }
            }
        } else {
            if (n_Type != 1) {
                n++;
            }
        }
    }
    return n;
}

function getText() {
    var s_Txt;
    if (EWEB.CurrMode == "TEXT") {
        s_Txt = EWEB.EditorTextarea.value;
    } else {
        s_Txt = _Html2Text(getHTML());
    }
    return s_Txt;
}

function _Html2Text(s_Html) {
    s_Html = s_Html.replace(/<script[^>]*?>(?:[^a]|a)*?<\/script>/gi, "");
    s_Html = s_Html.replace(/<style[^>]*?>(?:[^a]|a)*?<\/style>/gi, "");
    s_Html = s_Html.replace(/<object[^>]*?>(?:[^a]|a)*?<\/object>/gi, "");
    s_Html = s_Html.replace(/<embed[^>]*?>(?:[^a]|a)*?<\/embed>/gi, "");
    //s_Html = s_Html.replace(/<table[^>]*?>(?:[^a]|a)*?<\/table>/gi,"");
    s_Html = s_Html.replace(/(<br[\s]*?\/?>|<\/[a-zA-Z]+>)([\r\n]+)([^<])/gi, "$1$3");

    var el = $("eWebEditor_Temp_HTML");
    el.innerHTML = s_Html;
    if (EWEBBrowser.IsAllIE) {
        return el.innerText;
    } else {
        return _GetInnerTextGecko(el);
    }
}


function LoadScript(url) {
    document.write('<scr' + 'ipt type="text/javascript" src="' + url + '"><\/scr' + 'ipt>');
}


/////////////////////////////////////////////////////


function ShowEWEBEditorBody() {
    if (!config.L) {
        document.write("<table style='width:100%;height:100%;border-collapse:collapse' borderColor='#999999' bgColor='#ffffff' border='1px'><tr><td align='center' style='font-size:9pt'>eWebEditor!<br><br>" + lang["ErrLicense"] + "</td></tr></table>");
        return;
    }

    var s_Body = "";
    s_Body += "<table id='eWebEditor_Layout' border='0' cellpadding='0' cellspacing='0' width='100%' height='100%' style='table-layout:fixed'>";

    s_Body += "<tr id='eWebEditor_ToolarPTR'><td>";
    s_Body += "<table id='eWebEditor_Toolbar' border=0 cellpadding=0 cellspacing=0 width='100%'>";
    s_Body += "<tr id='eWebEditor_ToolarTRText' style='display:none'><td>";
    s_Body += _GetToolbar(_GetTextToolbarArr("Text"), "Text");
    s_Body += "</td></tr>";
    s_Body += "<tr id='eWebEditor_ToolarTRView' style='display:none'><td>";
    s_Body += _GetToolbar(_GetTextToolbarArr("View"), "View");
    s_Body += "</td></tr>";
    s_Body += "<tr id='eWebEditor_ToolarTREdit' style='display:'><td>";
    s_Body += _GetToolbar(config.Toolbars, "Edit");
    s_Body += "</td></tr>";
    s_Body += "</table>";
    s_Body += "</td></tr>";

    s_Body += "<tr><td id='eWebEditor_EditareaTD' height='100%'>";
    s_Body += "<textarea id='eWebEditorTextarea' style='display:none;width:100%;height:100%;' spellcheck='false' autocorrect='off'></textarea>";
    s_Body += '<iframe id="eWebEditor" style="width:100%;height:100%"  frameborder="0" allowTransparency="true" src="' + EWEBTools.GetVoidUrl() + '"></iframe>';
    s_Body += "</td></tr>";

    if (config.StateFlag) {
        s_Body += "<tr id='eWebEditor_SB'><td class='SB'>";
        s_Body += "	<table border='0' cellpadding='0' cellspacing='0' width='100%' class='SB'>";
        s_Body += "	<tr valign='middle'>";
        s_Body += "	<td>";
        s_Body += "		<table id='eWebEditor_SB_Mode' border='0' cellpadding='0' cellspacing='0' class='SB_Mode'>";
        s_Body += "		<tr>";
        s_Body += "		<td class='SB_Mode_Left'></td>";
        if (config.SBCode) {
            s_Body += "		<td class=SB_Mode_BtnOff id=eWebEditor_CODE onclick=\"setMode('CODE')\" unselectable=on><table border=0 cellpadding=0 cellspacing=0><tr><td class=SB_Mode_Btn_Img>" + _GetBtnImgHTML("ModeCode") + "</td><td class=SB_Mode_Btn_Text>" + lang["StatusModeCode"] + "</td></tr></table></td>";
            s_Body += "		<td class=SB_Mode_Sep></td>";
        }
        if (config.SBEdit) {
            s_Body += "		<td class=SB_Mode_BtnOff id=eWebEditor_EDIT onclick=\"setMode('EDIT')\" unselectable=on><table border=0 cellpadding=0 cellspacing=0><tr><td class=SB_Mode_Btn_Img>" + _GetBtnImgHTML("ModeEdit") + "</td><td class=SB_Mode_Btn_Text>" + lang["StatusModeEdit"] + "</td></tr></table></td>";
            s_Body += "		<td class=SB_Mode_Sep></td>";
        }
        if (config.SBText) {
            s_Body += "		<td class=SB_Mode_BtnOff id=eWebEditor_TEXT onclick=\"setMode('TEXT')\" unselectable=on><table border=0 cellpadding=0 cellspacing=0><tr><td class=SB_Mode_Btn_Img>" + _GetBtnImgHTML("ModeText") + "</td><td class=SB_Mode_Btn_Text>" + lang["StatusModeText"] + "</td></tr></table></td>";
            s_Body += "		<td class=SB_Mode_Sep></td>";
        }
        if (config.SBView) {
            s_Body += "		<td class=SB_Mode_BtnOff id=eWebEditor_VIEW onclick=\"setMode('VIEW')\" unselectable=on><table border=0 cellpadding=0 cellspacing=0><tr><td class=SB_Mode_Btn_Img>" + _GetBtnImgHTML("ModeView") + "</td><td class=SB_Mode_Btn_Text>" + lang["StatusModeView"] + "</td></tr></table></td>";
        }
        s_Body += "		</tr>";
        s_Body += "		</table>";
        s_Body += "	</td>";
        if (EWEBParam.FullScreen != "1" && config.SBSize) {
            s_Body += "	<td align=right>";
            s_Body += "		<table id='eWebEditor_SB_Size' border=0 cellpadding=0 cellspacing=0 class=SB_Size>";
            s_Body += "		<tr>";
            s_Body += "		<td class=SB_Size_Btn onclick='sizeChange(300)' title='" + lang["SizePlus"] + "'>" + _GetBtnImgHTML("SizePlus") + "</td>";
            s_Body += "		<td class=SB_Size_Sep></td>";
            s_Body += "		<td class=SB_Size_Btn onclick='sizeChange(-300)' title='" + lang["SizeMinus"] + "'>" + _GetBtnImgHTML("SizeMinus") + "</td>";
            s_Body += "		<td class=SB_Size_Right></td>";
            s_Body += "		</tr>";
            s_Body += "		</table>";
            s_Body += "	</td>";
        }
        s_Body += "	</tr>";
        s_Body += "	</Table>";
        s_Body += "</td></tr>";
    }
    s_Body += "</table>";

    s_Body += "<div id='eWebEditor_Temp_HTML' style='visibility:hidden;overflow:hidden;position:absolute;width:1px;height:1px'></div>";

    s_Body += "<div style='width:1px;height:1px;position:absolute;left:-10000'>";
    s_Body += "<input type='text' id='D_ContentEdit' value=''>";
    s_Body += "<input type='text' id='D_CurrMode' value=''>";
    s_Body += "<input type='text' id='D_ContentLoad' value=''>";
    s_Body += "<input type='text' id='D_ContentFlag' value='0'>";
    s_Body += "<input type='text' id='D_PaginationTitle' value=''>";
    s_Body += "</div>";

    s_Body += "<div style='position:absolute;display:none'>";
    s_Body += "<form id='eWebEditor_UploadForm' action='" + EWEB.UploadAct + "&action=remote&type=remote' method='post' target='eWebEditor_UploadTarget'>";
    s_Body += "<input type='hidden' name='eWebEditor_UploadText'>";
    s_Body += "</form>";
    s_Body += '<iframe name="eWebEditor_UploadTarget" width=0 height=0 src="' + EWEBTools.GetVoidUrl() + '"></iframe>';
    s_Body += "</div>";

    s_Body += "<div id=divProcessing style='width:200px;height:30px;position:absolute;left:-10000'>";
    s_Body += "<table border=0 cellpadding=0 cellspacing=1 bgcolor='#000000' width='100%' height='100%'><tr><td bgcolor=#3A6EA5><marquee id='msgProcessing' align='middle' behavior='alternate' scrollamount='5' style='font-size:9pt;color:#ffffff'></marquee></td></tr></table>";
    s_Body += "</div>";

    s_Body += "<div id='div_TableResizeSepV' style='position:absolute;display:none;background-color:transparent;overflow:hidden;' onmousedown='EWEBTableResize.MD2(event)' onmousemove='EWEBTableResize.MM2(event)' onmouseup='EWEBTableResize.MU2(event)'></div>";
    s_Body += "<div id='div_TableResizeSepH' style='position:absolute;display:none;background-color:transparent;overflow:hidden;' onmousedown='EWEBTableResize.MD2(event)' onmousemove='EWEBTableResize.MM2(event)' onmouseup='EWEBTableResize.MU2(event)'></div>";

    if (!EWEBBrowser.IsAllIE && EWEBBrowser.IsWindow) {
        s_Body += '<div id="div_activex" style="position:absolute">' + EWEBActiveX.FFObjectHTML + '</div>';
    }
    if (EWEBBrowser.IsUseLS && EWEBBrowser.IsWindow) {
        s_Body += '<div id="div_LS" style="position:absolute;left:-10000"></div>';
    }

    if (!EWEBBrowser.IsAllIE && !EWEBBrowser.IsFirefox) {
        s_Body += '<div id="ImgResize_Line_L" class="ImgResize_Line" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_Line_R" class="ImgResize_Line" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_Line_T" class="ImgResize_Line" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_Line_B" class="ImgResize_Line" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_LT" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_LM" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_LB" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_CT" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_CB" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_RT" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_RM" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>'
            + '<div id="ImgResize_C_RB" class="ImgResize_C" onmousedown="EWEBImageResize.MD2(event)" onmousemove="EWEBImageResize.MM2(event)" onmouseup="EWEBImageResize.MU2(event)"></div>';
    }

    document.write(s_Body);
}


function _GetToolbar(a_Toolbars, s_ID) {
    var b_HaveExpandBtn = false;
    var s_Ret = "<table border='0' cellpadding='0' cellspacing='0' width='100%' id='eWebEditor_Toolbar_" + s_ID + "' unselectable='on'>";
    for (var i = 0; i < a_Toolbars.length; i++) {
        s_Ret += "<tr id='eWebEditor_Toolbar_" + s_ID + "_TR" + i + "' style='display:" + (b_HaveExpandBtn ? "none" : "") + "'><td class='TB_Left'></td><td class='TB_Center'><table border='0' cellpadding='0' cellspacing='0'><tr>";
        var tb = a_Toolbars[i];
        for (var j = 0; j < tb.length; j++) {
            var s_Code = tb[j];
            if (s_Code == "ExpandToolbar") {
                if (!b_HaveExpandBtn) {
                    EWEBToolbar.SetExpandIndex(i + 1);
                    b_HaveExpandBtn = true;
                }
            }

            var a_Button = Buttons[s_Code];
            if (!a_Button) {
                alert("Invalid Button: " + s_Code);
                break;
            }
            var s_Fn = a_Button[1] ? a_Button[1] : "exec('" + s_Code + "')";

            if (s_Code == "TBSep") {
                s_Ret += "<td class='TB_Btn_Padding'><div class='TB_Sep'></div></td>";
            } else if (a_Button[3] == 0) {
                s_Ret += "<td class='TB_Btn_Padding'><div class='TB_Btn' name='TB_Name_" + s_Code + "' title=\"" + lang[s_Code] + "\" onclick=\"" + s_Fn + "\">";
                if (typeof(a_Button[0]) == "number") {
                    var s_Img = "skin/" + config.Skin + "/buttons.gif";
                    var n_Top = 16 - a_Button[0] * 16;

                    if (EWEBBrowser.IsIE && (!EWEBBrowser.IsIE9Compat) && (!EWEBBrowser.IsIE10)) {
                        s_Ret += "<div class='TB_Btn_Image'><img src='" + s_Img + "' style='top:" + n_Top + "px' /></div>";
                    } else {
                        s_Ret += "<img class='TB_Btn_Image' src='sysimage/space.gif' style='background-position: 0px " + n_Top + "px;background-image: url(" + s_Img + ");' />";
                    }

                } else {
                    var s_Img = "skin/" + config.Skin + "/" + a_Button[0];
                    s_Ret += "<img class='TB_Btn_Image' src='" + s_Img + "'>";
                }
                s_Ret += "</div></td>";
            } else if (a_Button[3] == 1) {
                var s_FixedWidth = "";
                var s_Options = "";
                switch (s_Code) {
                    case "FontName":
                        s_FixedWidth = " style='width:115px'";
                        for (var k = 0; k < lang[s_Code + "Item"].length; k++) {
                            s_Options += "<option value='" + lang[s_Code + "Item"][k] + "'>" + lang[s_Code + "Item"][k].split(",")[0] + "</option>";
                        }
                        break;
                    case "FontSize":
                        s_FixedWidth = " style='width:55px'";
                        for (var k = 0; k < lang[s_Code + "Item"].length; k++) {
                            s_Options += "<option value='" + lang[s_Code + "Item"][k][0] + "'>" + lang[s_Code + "Item"][k][1] + "</option>";
                        }
                        break;
                    case "FormatBlock":
                        s_FixedWidth = " style='width:90px'";
                        for (var k = 0; k < lang[s_Code + "Item"].length; k++) {
                            s_Options += "<option value='" + lang[s_Code + "Item"][k][0] + "'>" + lang[s_Code + "Item"][k][1] + "</option>";
                        }
                        break;
                    case "ZoomSelect":
                        s_FixedWidth = " style='width:55px'";
                        for (var k = 0; k < EWEBCommandZoom.Options.length; k++) {
                            s_Options += "<option value='" + EWEBCommandZoom.Options[k] + "'>" + EWEBCommandZoom.Options[k] + "%</option>";
                        }

                        break;
                }
                s_Ret += "<td class='TB_Btn_Padding'><select name='TB_Name_" + s_Code + "' onchange=\"" + s_Fn + "\" size=1 " + s_FixedWidth + "><option selected>" + lang[s_Code] + "</option>" + s_Options + "</select></td>";
            }

        }
        s_Ret += "</tr></table></td><td class='TB_Right'></td></tr>";
    }
    s_Ret += "</table>";
    if (a_Toolbars.length == 0) {
        s_Ret = "";
    }
    return s_Ret;
}


function _GetTextToolbarArr(s_Flag) {
    var a = new Array();
    var b = false;
    a.push("TBHandle");
    if (config.TB2Code == "1") {
        a.push("ModeCode");
        b = true;
    }
    if (config.TB2Edit == "1") {
        a.push("ModeEdit");
        b = true;
    }
    if (config.TB2Text == "1") {
        a.push("ModeText");
        b = true;
    }
    if (config.TB2View == "1") {
        a.push("ModeView");
        b = true;
    }

    if (s_Flag == "Text") {
        if (b) {
            a.push("TBSep");
        }
        a.push("FindReplace");
        b = true;
    }
    if (config.TB2Max == "1") {
        if (b) {
            a.push("TBSep");
        }
        a.push("Maximize");
    }
    return [a];
}


function _GetBtnImgHTML(s_Code, s_Class) {
    var a_Btn = Buttons[s_Code];
    var n_Top = 16 - a_Btn[0] * 16;
    var s_Img = "skin/" + config.Skin + "/buttons.gif";
    if (EWEBBrowser.IsIE) {
        return "<div><img src='" + s_Img + "' style='top:" + n_Top + "px'></div>";
    } else {
        return "<img class='SB_Btn_Image' src='sysimage/space.gif' style='background-position: 0px " + n_Top + "px;background-image: url(" + s_Img + ");' />";
    }
}


//////////////////////////////////////////////////

var EWEBPagination = new Object();

EWEBPagination.Insert = function () {
    if (config.PaginationMode == "0") {
        return false;
    }

    EWEB.Focus();
    var el;
    if (EWEBSelection.GetType() == "Control") {
        el = EWEBSelection.GetSelectedElement();
    } else {
        el = EWEBSelection.GetParentElement();
    }

    el = this._FindTopElementByElement(el);

    if (!el) {
        insertHTML("</P><P id=eWebEditor_Temp_P>");
        var p = EWEB.EditorDocument.getElementById("eWebEditor_Temp_P");
        p.removeAttribute("id");
        this._InsertPaginationElByEl(p, "beforeBegin");
    } else {
        this._InsertPaginationElByEl(el, "afterEnd");
    }
    _FireChange();
};

EWEBPagination._InsertPaginationElByEl = function (el, s_Pos) {
    var b_CreateP = false;
    if (s_Pos == "afterEnd" && (!el.nextSibling)) {
        b_CreateP = true;
    }
    var o_NewNode = EWEB.EditorDocument.createElement("img");
    o_NewNode.className = "ewebeditor__pagination";
    o_NewNode.setAttribute("_ewebeditor_fake_tag", "pagination");
    o_NewNode.setAttribute("src", EWEB.RootPath + "/sysimage/space.gif");
    if (EWEBBrowser.IsIE) {
        el.insertAdjacentElement(s_Pos, o_NewNode);
    } else {
        if (s_Pos == "beforeBegin") {
            el.parentNode.insertBefore(o_NewNode, el);
        } else {
            el.parentNode.insertBefore(o_NewNode, el.nextSibling);
        }
    }

    if (b_CreateP) {
        var p = EWEB.EditorDocument.createElement("p");
        el.parentElement.appendChild(p);
    }
};


EWEBPagination._FindTopElementByElement = function (el) {
    if (el.tagName == "HTML") {
        return null;
    }
    var te = null;

    if (config.FixWidth) {
        while (!((el.tagName.toUpperCase() == "DIV") && (el.getAttribute("id") == "eWebEditor_FixWidth_DIV"))) {
            te = el;
            el = el.parentNode;
            if (!el || !el.tagName) {
                break;
            }
        }
    } else {
        while (el.tagName.toUpperCase() != "BODY") {
            te = el;
            el = el.parentNode;
            if (!el || !el.tagName) {
                break;
            }
        }
    }

    return te;
};


EWEBPagination.Auto = function (s_Num) {
    if (config.PaginationMode == "0") {
        return false;
    }

    this.Empty();

    var n_Num = parseInt(s_Num);
    if (n_Num < 1) {
        return false;
    }
    if (getCount(2) <= n_Num) {
        return false;
    }

    if (EWEB.CurrMode != "EDIT") {
        setMode("EDIT");
    }

    var o_Body;
    if (config.FixWidth) {
        o_Body = EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV");
    } else {
        o_Body = EWEB.EditorDocument.body;
    }

    var o_Nodes = o_Body.childNodes;
    var l = 0;
    for (var i = 0; i < o_Nodes.length; i++) {
        var o_Node = o_Nodes[i];
        if (o_Node.nodeType == 1) {
            // 1: Element o_Node.
            var s = o_Node.innerText || o_Node.textContent;
            if (s) {
                l += s.length;
            }
        } else if (o_Node.nodeType == 3) {
            // 3: Text Node
            l += o_Node.length;
        }

        if (l >= n_Num) {
            if (o_Node.nextSibling) {
                if (o_Node.nodeType == 1) {
                    this._InsertPaginationElByEl(o_Node, "afterEnd");
                } else {
                    this._InsertPaginationElByEl(o_Node.nextSibling, "beforeBegin");
                }
                l = 0;
            }
        }
    }
};

EWEBPagination.Empty = function () {
    if (config.PaginationMode == "0") {
        return;
    }

    var els = EWEB.EditorDocument.getElementsByTagName("IMG");
    for (var i = els.length - 1; i >= 0; i--) {
        var el = els[i];
        var s_Attr = el.getAttribute("_ewebeditor_fake_tag", 2);
        if (s_Attr) {
            if (s_Attr.toLowerCase() == "pagination") {
                EWEBTools.RemoveNode(el);
            }
        }
    }
};

EWEBPagination.Fix = function () {
    if (config.PaginationMode == "0") {
        return false;
    }

    var els = EWEB.EditorDocument.getElementsByTagName("IMG");
    for (var i = els.length - 1; i >= 0; i--) {
        var el = els[i];
        var s_Attr = el.getAttribute("_ewebeditor_fake_tag", 2);
        if (s_Attr) {
            if (s_Attr.toLowerCase() == "pagination") {
                var te = this._FindTopElementByElement(el);
                if (te) {
                    var b = false;
                    if (te.tagName == "DIV" || te.tagName == "P") {
                        var s = te.innerText || te.textContent;
                        s = s.Trim();
                        if (!s) {
                            b = true;
                            this._InsertPaginationElByEl(te, "beforeBegin");
                            EWEBTools.RemoveNode(te);
                        }
                    }
                    if (!b) {
                        this._InsertPaginationElByEl(te, "afterEnd");
                        EWEBTools.RemoveNode(el);
                    }
                }
            }
        }
    }
};


//////////////////////////////////////////////////

var EWEBFake = new Object();

EWEBFake.Normal2Fake = function (s_Html) {
    s_Html = this._PaginationCode2Img(s_Html);
    //s_Html = this._Code2Img( s_Html, 'style', /<style[\s\S]*?<\/style>/gi );
    s_Html = this._Code2Img(s_Html, 'script', /<script[\s\S]*?<\/script>/gi);
    s_Html = this._Code2Img(s_Html, 'noscript', /<noscript[\s\S]*?<\/noscript>/gi);
    //s_Html = this._Code2Img( s_Html, 'comment', /<!--[\s\S]*?-->/g );
    s_Html = this._VideoCode2Img(s_Html);
    s_Html = this._AudioCode2Img(s_Html);
    s_Html = this._ObjectCode2Img(s_Html);
    s_Html = this._FlvCode2Img(s_Html);
    s_Html = this._EmbedCode2Img(s_Html);
    s_Html = this._ReplaceLtgtInQuot(s_Html, "img");
    s_Html = this._ProtectEvents(s_Html);
    s_Html = this._ProtectAttr(s_Html, "img", "src");
    s_Html = this._ProtectAttr(s_Html, "a", "href");
    s_Html = this._ProtectAttr(s_Html, "area", "href");
    s_Html = this._ProtectAttr(s_Html, "a", "name");
    s_Html = this._AddTempClass(s_Html);

    return s_Html;
};

EWEBFake.Fake2Normal = function (s_Html) {
    s_Html = this._Clear(s_Html);
    s_Html = this._RestoreSpecChar(s_Html);
    s_Html = this._PaginationImg2Code(s_Html);
    s_Html = this._RestoreEvents(s_Html);
    s_Html = this._ReplaceLtgtInQuot(s_Html, "img");
    s_Html = this._RestoreAttr(s_Html);
    s_Html = this._Img2Code(s_Html);
    s_Html = this._RestoreTempClass(s_Html);
    s_Html = this._FixLiDdDt(s_Html);
    return s_Html;
};


EWEBFake.GetTag = function () {
    var el = EWEBSelection.GetSelectedElement();
    return el.getAttribute("_ewebeditor_fake_tag", 2);
};

EWEBFake._RestoreSpecChar = function (s_Html) {
    s_Html = s_Html.replace(/\u2002/gi, "&ensp;");
    s_Html = s_Html.replace(/\u2003/gi, "&emsp;");
    s_Html = s_Html.replace(/\u2009/gi, "&thinsp;");
    return s_Html;
};

EWEBFake._Clear = function (s_Html) {
    var s_Ret = s_Html.replace(/<(span|font|strong|b|i|u|strike)(?=[\s>])[^>]*?><\/\1>/gi, "");
    if (s_Ret != s_Html) {
        return this._Clear(s_Ret);
    } else {
        return s_Ret;
    }
};

EWEBFake._FixLiDdDt = function (s_Html) {
    var s_Ret = s_Html;
    var a_Block = new Array();

    var _GetSepStr = function (s) {
        var s_SepStr = '__ewebeditor__sepstr__';
        while (true) {
            s_SepStr = s_SepStr + 'a';
            var re = new RegExp(s_SepStr + '[0-9]+', 'gi');
            if (!re.test(s)) {
                break;
            }
        }
        return s_SepStr;
    };
    var _SepStr = _GetSepStr(s_Html);

    var _Replace1 = function (m) {
        a_Block.push(m);
        return _SepStr + (a_Block.length - 1);
    };

    while (true) {
        var s_Tmp = s_Ret.replace(/<(ul|ol|dl)(?=[\s>])[^>]*>((?!<\/?(ul|ol|dl)>)[\s\S])*<\/\1>/gi, _Replace1);
        if (s_Tmp == s_Ret) {
            break;
        } else {
            s_Ret = s_Tmp;
        }
    }

    var _Replace2 = function (m, m1) {
        return m.replace(/[ \t\n\r]*$/g, '') + '</' + m1 + '>';
    };

    for (var i = 0; i < a_Block.length; i++) {
        a_Block[i] = a_Block[i].replace(/<(li|dd|dt)(?=[\s>])[^>]*>((?!<\/?(li|dd|dt)[\s>])[\s\S])*(?=<(li|dd|dt|\/ul|\/ol|\/dl)[\s>])/gi, _Replace2)
    }

    for (var i = a_Block.length - 1; i >= 0; i--) {
        s_Ret = s_Ret.replace(_SepStr + i, a_Block[i]);
    }

    return s_Ret;
};

EWEBFake._Code2Img = function (s_Html, s_Tag, o_Reg) {
    function _Replace(m) {
        return EWEBFake._GetImgHtml(s_Tag, m);
    };

    return s_Html.replace(o_Reg, _Replace);
};

EWEBFake._Img2Code = function (s_Html) {

    function _Replace(m, s_Tag) {

        function _Replace1(m, s_Value) {
            if (['flash', 'flv', 'mediaplayer6', 'mediaplayer7', 'realplayer', 'quicktime', 'vlc', 'video', 'audio', 'unknownobject'].IndexOf(s_Tag) >= 0) {
                var s_Style = EWEBFake._GetFullStyleFromHtml(m, 'img');
                if (s_Style != '') {
                    s_Style = ' style=' + s_Style;
                }

                var s_Width = (EWEBFake._GetStyleValueFromHtml(m, 'img', 'width') == '') ? EWEBFake._GetAttValueFromHtml(m, 'img', 'width') : '';
                var s_Height = (EWEBFake._GetStyleValueFromHtml(m, 'img', 'height') == '') ? EWEBFake._GetAttValueFromHtml(m, 'img', 'height') : '';

                var s_Align = EWEBFake._GetAttValueFromHtml(m, 'img', 'align');
                var s_Vspace = EWEBFake._GetAttValueFromHtml(m, 'img', 'vspace');
                var s_Hspace = EWEBFake._GetAttValueFromHtml(m, 'img', 'hspace');
                var s_FakeHtml = decodeURIComponent(s_Value);

                s_FakeHtml = EWEBFake._SetFullStyleToHtml(s_FakeHtml, "object", s_Style);
                s_FakeHtml = EWEBFake._SetFullStyleToHtml(s_FakeHtml, "embed", s_Style);
                s_FakeHtml = EWEBFake._SetFullStyleToHtml(s_FakeHtml, "video", s_Style);
                s_FakeHtml = EWEBFake._SetFullStyleToHtml(s_FakeHtml, "audio", s_Style);

                s_FakeHtml = EWEBFake._SetAttValueToHtml(s_FakeHtml,
                    ["object", "embed", "video", "audio"],
                    [["width", s_Width], ["height", s_Height], ["align", s_Align], ["vspace", s_Vspace], ["hspace", s_Hspace]]
                );
                return s_FakeHtml;

            } else {
                return decodeURIComponent(s_Value);
            }
        };

        return m.replace(/<img [^>]*?_ewebeditor_fake_value=\"([^\">]+?)\"[^>]*?>/gi, _Replace1);
    };

    return s_Html.replace(/<img [^>]*?_ewebeditor_fake_tag=\"(\w+?)\"[^>]*?>/gi, _Replace);

};


EWEBFake._PaginationCode2Img = function (s_Html) {
    if (config.PaginationMode == "0") {
        return s_Html;
    }

    var s_Ret = '';
    var s_Title = '';
    var s_Img = EWEBFake._GetImgHtml('pagination', "");

    if (config.PaginationMode == "1") {
        s_Ret = "";
        var re = /<!--ewebeditor:page title=\"([^\">]*)\"-->((?:[^a]|a)+?)<!--\/ewebeditor:page-->/gi;
        var m;
        var n_PageCount = 0;
        while ((m = re.exec(s_Html)) != null) {
            n_PageCount++;
            s_Title += HTMLDecode(m[1]) + "\n";

            if (s_Ret != "") {
                s_Ret += s_Img;
            }
            s_Ret += m[2];
        }

        if (n_PageCount == 0) {
            s_Ret = s_Html;
        }

    } else {
        var re = new RegExp(config.PaginationKey.replace(/([\[\]\{\}\.\(\)\*\+\?])/gi, "\\$1"), 'gi');
        s_Ret = s_Html.replace(re, s_Img);
    }

    $("D_PaginationTitle").value = s_Title;

    return s_Ret;
};

EWEBFake._PaginationImg2Code = function (s_Html) {
    if (config.PaginationMode == "0") {
        return s_Html;
    }
    var s_Ret = s_Html;

    //pagination
    var a = s_Html.split(/<img [^>]*?_ewebeditor_fake_tag=\"pagination\"[^>]*?>/gi);
    if (a.length > 1) {
        if (config.PaginationMode == "1") {
            s_Ret = "";
            var a_Title = $("D_PaginationTitle").value.split("\n");
            for (var i = 0; i < a.length; i++) {
                var s_Title = "";
                if (a_Title[i]) {
                    s_Title = HTMLEncode(a_Title[i]);
                }
                s_Ret += "<!--ewebeditor:page title=\"" + s_Title + "\"-->\r\n";
                s_Ret += a[i] + "\r\n";
                s_Ret += "<!--/ewebeditor:page-->\r\n\r\n";
            }
        } else {
            s_Ret = a[0];
            for (var i = 1; i < a.length; i++) {
                s_Ret += "\r\n" + config.PaginationKey + "\r\n" + a[i];
            }
        }
    }
    return s_Ret;
};

/*自定义视频图片*/
EWEBFake._FlvCode2Img = function (s_Html) {
    function _Replace(m) {
        return EWEBFake._GetImgHtml('video', m, 'div');
    };
    return s_Html.replace(/<div[^>]*class="video-player"[^>]*type="([^"]+)"[^>]*>(?:<\/div>)?/gi, _Replace);
};

EWEBFake._ObjectCode2Img = function (s_Html) {

    function _Replace(m) {
        var s_ClsID = m.replace(/<object [^>]*?classid\s*=\s*[\'\"]?clsid\s*:\s*([a-z0-9\-]+)[\'\"]?[^>]*?>[\s\S]*/gi, '$1');
        s_ClsID = s_ClsID.toUpperCase();

        if (s_ClsID == 'D27CDB6E-AE6D-11CF-96B8-444553540000') {

            if (/plugin\/flvplayer\.swf/.test(m)) {
                return EWEBFake._GetImgHtml('flv', m, 'object');
            } else {
                //flash TYPE="application/x-shockwave-flash"
                return EWEBFake._GetImgHtml('flash', m, 'object');
            }


        } else if (s_ClsID == '22D6F312-B0F6-11D0-94AB-0080C74C7E95') {
            //media player v6.4
            return EWEBFake._GetImgHtml('mediaplayer6', m, 'object');
        } else if (s_ClsID == '6BF52A52-394A-11D3-B153-00C04F79FAA6') {
            //media player v7+
            return EWEBFake._GetImgHtml('mediaplayer7', m, 'object');
        } else if (s_ClsID == 'CFCDAA03-8BE4-11CF-B84B-0020AFBBCCFA') {
            //real player
            return EWEBFake._GetImgHtml('realplayer', m, 'object');
        } else if (s_ClsID == '02BF25D5-8C17-4B23-BC80-D3488ABDDC6B') {
            //quicktime
            return EWEBFake._GetImgHtml('quicktime', m, 'object');
        } else if (s_ClsID == '9BE31822-FDAD-461B-AD51-BE1D1C159921') {
            //vlc
            return EWEBFake._GetImgHtml('vlc', m, 'object');
        } else if (s_ClsID == '0002E510-0000-0000-C000-000000000046' || s_ClsID == '0002E551-0000-0000-C000-000000000046' || s_ClsID == '0002E559-0000-0000-C000-000000000046') {
            //owc excel
            return m;
        } else {
            return EWEBFake._GetImgHtml('unknownobject', m, 'object');
        }
    };


    return s_Html.replace(/<object[\s\S]*?<\/object>/gi, _Replace);
};


EWEBFake._EmbedCode2Img = function (s_Html) {

    function _Replace(m) {
        var s_Type = m.replace(/<embed [^>]*?type\s*=\s*[\'\"]?([^\'\"\s]+)[\'\"]?[^>]*?>[\s\S]*/gi, '$1');
        s_Type = s_Type.toLowerCase();

        if (s_Type == 'application/x-shockwave-flash') {
            //flash
            return EWEBFake._GetImgHtml('flash', m, 'embed');
        } else if (['application/x-mplayer2', 'video/x-ms-asf', 'video/x-msvideo', 'video/mpeg', 'audio/mid', 'audio/mpeg', 'audio/wav', 'video/x-ms-wm', 'audio/x-ms-wma', 'video/x-ms-wmv', 'video/x-ms-wmp', 'video/x-ms-wmx'].IndexOf(s_Type) >= 0) {
            //media player v6.4   application/x-mplayer2
            //media player v7+    clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6
            return EWEBFake._GetImgHtml('mediaplayer6', m, 'embed');
        } else if (s_Type == 'video/quicktime') {
            //media player v6.4   application/x-mplayer2
            //media player v7+    clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6
            return EWEBFake._GetImgHtml('quicktime', m, 'embed');
        } else if (['audio/x-pn-realaudio', 'audio/x-pn-realaudio-plugin', 'application/vnd.rn-realmedia'].IndexOf(s_Type) >= 0) {
            //real player clsid:CFCDAA03-8BE4-11cf-B84B-0020AFBBCCFA
            return EWEBFake._GetImgHtml('realplayer', m, 'embed');
        } else if (['application/x-vlc-plugin'].IndexOf(s_Type) >= 0) {
            //vlc
            return EWEBFake._GetImgHtml('vlc', m, 'embed');
        } else {
            return EWEBFake._GetImgHtml('unknownobject', m, 'embed');
        }
    };

    return s_Html.replace(/<embed[\s\S]*?<\/embed>/gi, _Replace);
};

EWEBFake._VideoCode2Img = function (s_Html) {
    function _Replace(m) {
        return EWEBFake._GetImgHtml('video', m, 'video');
    };

    return s_Html.replace(/<video[\s\S]*?<\/video>/gi, _Replace);
};

EWEBFake._AudioCode2Img = function (s_Html) {
    function _Replace(m) {
        return EWEBFake._GetImgHtml('audio', m, 'audio');
    };

    return s_Html.replace(/<audio[\s\S]*?<\/audio>/gi, _Replace);
};

EWEBFake._GetImgHtml = function (s_Tag, s_Value, s_AttTag) {
    if (s_AttTag) {
        var s_Style = EWEBFake._GetFullStyleFromHtml(s_Value, s_AttTag);
        var s_Width = EWEBFake._GetAttValueFromHtml(s_Value, s_AttTag, 'width');
        var s_Height = EWEBFake._GetAttValueFromHtml(s_Value, s_AttTag, 'height');
        var s_Align = EWEBFake._GetAttValueFromHtml(s_Value, s_AttTag, 'align');
        var s_Vspace = EWEBFake._GetAttValueFromHtml(s_Value, s_AttTag, 'vspace');
        var s_Hspace = EWEBFake._GetAttValueFromHtml(s_Value, s_AttTag, 'hspace');

        if (s_Style != '') {
            s_Style = ' style=' + s_Style;
        }

        var s_Html = '<img src="' + EWEB.RootPath + '/sysimage/space.gif" class="ewebeditor__' + s_Tag + '" _ewebeditor_fake_tag="' + s_Tag + '" _ewebeditor_fake_value="' + encodeURIComponent(s_Value) + '"' + s_Style;
        if (s_Width != '') {
            s_Html += ' width="' + s_Width + '"';
        }
        if (s_Height != '') {
            s_Html += ' height="' + s_Height + '"';
        }
        if (s_Align != '') {
            s_Html += ' align="' + s_Align + '"';
        }
        if (s_Vspace != '') {
            s_Html += ' vspace="' + s_Vspace + '"';
        }
        if (s_Hspace != '') {
            s_Html += ' hspace="' + s_Hspace + '"';
        }
        s_Html += '>';

        return s_Html;
    } else {
        return '<img src="' + EWEB.RootPath + '/sysimage/space.gif" class="ewebeditor__' + s_Tag + '" _ewebeditor_fake_tag="' + s_Tag + '" _ewebeditor_fake_value="' + encodeURIComponent(s_Value) + '">';
    }
};

EWEBFake._GetFullStyleFromHtml = function (s_Html, s_Tag) {
    var re = new RegExp('^[\\s\\S]*?<' + s_Tag + '(?=[\\s>])[^>]*?\\sstyle\\s*?=\\s*?(\'[^\'>]+?\'|\"[^\">]+?\")[^>]*?>[\\s\\S]*$', 'gi');
    if (re.test(s_Html)) {
        return s_Html.replace(re, '$1');
    } else {
        return '';
    }
};

EWEBFake._GetStyleValueFromHtml = function (s_Html, s_Tag, s_AttName) {
    var re = new RegExp('^[\\s\\S]*?<' + s_Tag + '(?=[\\s>])[^>]*?\\sstyle\\s*?=\\s*?([\'\"])[^>]*?\\b' + s_AttName + '\\s*?:\\s*?(\\w+)(?=[\\s\;\'\"])[^>]*?\\1[^>]*?>[\\s\\S]*$', 'gi');
    if (re.test(s_Html)) {
        return s_Html.replace(re, '$2');
    } else {
        return '';
    }
};

EWEBFake._GetAttValueFromHtml = function (s_Html, s_Tag, s_AttName) {
    var re = new RegExp('^[\\s\\S]*?<' + s_Tag + '(?=[\\s>])[^>]*?\\s' + s_AttName + '\\s*?=\\s*?([\'\"]?)(\\w+)\\1[^>]*?>[\\s\\S]*$', 'gi');
    if (re.test(s_Html)) {
        return s_Html.replace(re, '$2');
    } else {
        return '';
    }
};


EWEBFake._SetFullStyleToHtml = function (s_Html, s_Tag, s_Style) {

    function _Replace(m) {
        var r = /\sstyle\s*?=\s*?([\'\"])[^>]*?\1/gi;
        if (r.test(m)) {
            s_Style = s_Style.replace('$', '\\$');
            return m.replace(r, s_Style);
        } else {
            return m.substring(0, m.length - 1) + s_Style + '>';
        }
    };

    var re = new RegExp('<' + s_Tag + '(?=[\\s>])[^>]*?>', 'gi');
    return s_Html.replace(re, _Replace);
};


EWEBFake._SetAttValueToHtml = function (s_Html, a_Tag, a_Att) {
    for (var i = 0; i < a_Tag.length; i++) {
        s_Html = this._SetAttValueToHtmlByTag(s_Html, a_Tag[i], a_Att);
    }
    return s_Html;
};


EWEBFake._SetAttValueToHtmlByTag = function (s_Html, s_Tag, a_Att) {

    function _Replace(m) {
        var s_AttName, s_AttValue;

        for (var i = 0; i < a_Att.length; i++) {
            s_AttName = a_Att[i][0];
            s_AttValue = a_Att[i][1];
            var s = '';
            if (s_AttValue != '') {
                s = ' ' + s_AttName + '="' + s_AttValue + '"';
            }
            var r = new RegExp('\\s' + s_AttName + '\\s*?=\\s*?([\'\"]?)\\w+\\1', 'gi');
            if (r.test(m)) {
                m = m.replace(r, s);
            } else {
                if (s_AttValue != '') {
                    m = m.substring(0, m.length - 1) + s + '>';
                }
            }
        }

        return m;
    };


    var re = new RegExp('<' + s_Tag + '[^>]*?>', 'gi');
    return s_Html.replace(re, _Replace);
};


EWEBFake._ProtectEvents = function (s_Html) {

    function _Replace(m) {
        function _Replace2(m, s_AttName) {
            return ' _ewebeditor_pe_' + s_AttName + '="' + encodeURIComponent(m) + '"';
        };

        return m.replace(/\s(on\w+)\s*=\s*?(\'|\")([\s\S]*?)\2/gi, _Replace2);
    };

    return s_Html.replace(/<[^\>]+ on\w+\s*=\s*?(\'|\")[\s\S]+?\>/gi, _Replace);
};

EWEBFake._RestoreEvents = function (s_Html) {

    function _Replace(m, m1) {
        return decodeURIComponent(m1);
    };

    return s_Html.replace(/\s_ewebeditor_pe_\w+=\"([^\"]+)\"/gi, _Replace);
};

EWEBFake._ReplaceLtgtInQuot = function (s_Html, s_Tag, s_AttName) {

    function _Replace(m, m1, m2) {
        var s2 = m2.replace(/</gi, "&lt;");
        s2 = s2.replace(/>/gi, "&gt;");
        return m1 + s2;
    };

    var s_Att;
    if (s_AttName) {
        s_Att = "(?:" + s_AttName + ")";
    } else {
        s_Att = "[a-zA-Z]";
    }
    var re = new RegExp('(<' + s_Tag + '\\s[^<>]*?' + s_Att + '\\s*?=\\s*?)(\"[^\"]*?[<>][^\"]*?\"|\'[^\']*?[<>][^\']*?\')', 'gi');
    var s_Ret = s_Html.replace(re, _Replace);
    if (s_Ret != s_Html) {
        return EWEBFake._ReplaceLtgtInQuot(s_Ret, s_Tag, s_AttName);
    } else {
        return s_Ret;
    }
};

EWEBFake._ProtectAttr = function (s_Html, s_Tag, s_AttName) {

    function _Replace(m, m1, m2, m3, m4, m5) {
        var r = new RegExp('_ewebeditor_pa_' + s_AttName, 'gi');
        if (r.test(m) || /_ewebeditor_fake_/.test(m)) {
            return m;
        } else {
            return m1 + m2 + ' _ewebeditor_pa_' + s_AttName + '="' + encodeURIComponent(m4.replace(/\"/gi, "'")) + '"' + m5;
        }
    };

    var re = new RegExp('(<' + s_Tag + '(?=\\s)[^>]*?)(\\s' + s_AttName + '\\s*?=\\s*?([\'\"])([^>]*?)\\3)([\\s\\S]*?>)', 'gi');
    return s_Html.replace(re, _Replace);
};

EWEBFake._RestoreAttr = function (s_Html) {

    function _Replace(m, s_AttName, s_Value) {
        var r = new RegExp('\\s' + s_AttName + '\\s*?=\\s*?([\'\"])[^>]*?\\1', 'gi');
        var s = m.replace(r, '');
        r = new RegExp('\\s' + s_AttName + '\\s*?=[^\\s\'\">]*', 'gi');
        s = s.replace(r, '');
        r = new RegExp('\\s_ewebeditor_pa_' + s_AttName + '+\\s*?=\\s*?\"[^\"]*?\"', 'gi');
        return s.replace(r, ' ' + s_AttName + '="' + decodeURIComponent(s_Value) + '"');
    };

    var s_Ret = "";
    while (true) {
        s_Ret = s_Html.replace(/<\w+[^>]*?_ewebeditor_pa_(\w+)\s*?=\s*?\"([^\">]*?)\"[^>]*>/gi, _Replace);
        if (s_Ret != s_Html) {
            s_Html = s_Ret;
        } else {
            break;
        }
    }
    return s_Ret;
};

EWEBFake._RestoreTempClass = function (s_Html) {
    s_Html = s_Html.replace(/(<\w+(?=\s)[^>]*?)\sclass\s*?=ewebeditor__\w+([^>]*?>)/gi, '$1$2');
    s_Html = s_Html.replace(/(<\w+(?=\s)[^>]*?\sclass\s*?=[^>]*?)(ewebeditor__\w+)([^>]*?>)/gi, '$1$3');
    s_Html = s_Html.replace(/(<\w+(?=\s)[^>]*?)(\sclass\s*?=\s*?\"\s*\")([^>]*>)/gi, '$1$3');
    s_Html = s_Html.replace(/(<\w+(?=\s)[^>]*?\sclass\s*?=\s*?\")\s*([^\"]+?)\s*(\"[^>]*>)/gi, '$1$2$3');
    return s_Html;
};

EWEBFake._AddTempClass = function (s_Html) {
    function _AddTagClass(s_TagHtml, s_Class) {
        var s = s_TagHtml.replace(/(\sclass=\"[^\"]*?)(\")/gi, '$1 ' + s_Class + '$2');
        if (s == s_TagHtml) {
            s = s_TagHtml.substring(0, s_TagHtml.length - 1) + ' class="' + s_Class + '">';
        }
        return s;
    };

    function _Replace(m, m1, m2, m3) {
        if (m2.length > 0) {
            return _AddTagClass(m1, "ewebeditor__anchorc") + m2 + m3;
        } else {
            return _AddTagClass(m1, "ewebeditor__anchor") + m2 + m3;
        }
    };

    var re = new RegExp('(<a\\s[^>]*?ewebeditor_pa_name[^>]*?>)([\\s\\S]*?)(</a>)', 'gi');
    return s_Html.replace(re, _Replace);
};

EWEBFake._GetProtectAttribute = function (el, s_AttName) {
    var s_ProtectAttName = "_ewebeditor_pa_" + s_AttName;
    var o_Att = el.attributes[s_ProtectAttName];

    if (o_Att == null || !o_Att.specified) {
        return GetAttribute(el, s_AttName);
    } else {
        return decodeURIComponent(el.getAttribute(s_ProtectAttName, 2));
    }
};

EWEBFake._IsFakeElement = function (el) {
    var s_Attr = el.getAttribute("_ewebeditor_fake_tag", 2);
    return ((s_Attr) ? true : false);
};


//////////////////////////////////////////////////


var EWEBCodeFormat = {

    Format: function (s_Html) {
        var s_FormatIndentator = this._GetIndent();
        if (s_FormatIndentator == '') {
            return s_Html;
        }
        var s_SepStr = this._GetSepStr(s_Html)
        var a_ProtectedData = new Array();

        var r_DecreaseIndent = /^\<\/(HTML|HEAD|BODY|FORM|TABLE|TBODY|THEAD|TR|UL|OL|DL)[ \>]/i;
        var r_IncreaseIndent = /^\<(HTML|HEAD|BODY|FORM|TABLE|TBODY|THEAD|TR|UL|OL|DL)[ \/\>]/i;
        var r_FormatIndentatorRemove = new RegExp('^' + s_FormatIndentator);

        var _Replace1 = function (m, m1, m2, m3, m4) {
            a_ProtectedData.push(m3);
            return m1 + s_SepStr + m4;
        };

        //protect data
        var re = /(<(style|script|pre)(?=[\s>])[^>]*?>)([\s\S]*?)(<\/\2>)/gi;
        s_Html = s_Html.replace(re, _Replace1);

        // Line breaks.
        s_Html = s_Html.replace(/\<(P|DIV|H1|H2|H3|H4|H5|H6|ADDRESS|PRE|OL|UL|LI|DL|DT|DD|TITLE|META|LINK|BASE|SCRIPT|LINK|TD|TH|AREA|OPTION)[^\>]*\>/gi, '\n$&');
        s_Html = s_Html.replace(/\<\/(P|DIV|H1|H2|H3|H4|H5|H6|ADDRESS|PRE|OL|UL|LI|DL|DT|DD|TITLE|META|LINK|BASE|SCRIPT|LINK|TD|TH|AREA|OPTION)[^\>]*\>/gi, '$&\n');
        s_Html = s_Html.replace(/\<(BR|HR)[^\>]*\>/gi, '$&\n');
        s_Html = s_Html.replace(/\<\/?(HTML|HEAD|BODY|FORM|TABLE|TBODY|THEAD|TR)[^\>]*\>/gi, '\n$&\n');

        var a_Lines = s_Html.split(/[\r\t\f ]*\n+[\r\t\f ]*/g);

        var s_Indentation = '';
        s_Html = '';

        for (var i = 0; i < a_Lines.length; i++) {
            var s_Line = a_Lines[i];

            if (s_Line.length == 0) {
                continue;
            }

            if (r_DecreaseIndent.test(s_Line)) {
                s_Indentation = s_Indentation.replace(r_FormatIndentatorRemove, '');
            }

            s_Html += s_Indentation + s_Line + '\n';

            if (r_IncreaseIndent.test(s_Line)) {
                s_Indentation += s_FormatIndentator;
            }
        }


        var _Replace2 = function (m, m1, m2) {
            return m1.toLowerCase() + m2;
        };

        // tag to lowercase
        s_Html = s_Html.replace(/(<\/?\w+(?=[\s>]))([^>]*?>)/gi, _Replace2);


        var _Replace3 = function (m, m1, m2, m3) {
            var a_pd = new Array();
            var _Replace31 = function (m, m1) {
                a_pd.push(m);
                return '"' + s_SepStr + '"';
            };
            var s3 = m2.replace(/([\'\"])[\s\S]*?\1/gi, _Replace31);

            var _Replace32 = function (m, m1, m2) {
                return ' ' + m1.toLowerCase() + '="' + m2 + '"';
            };
            s3 = s3.replace(/\s(\w+)\s*=([^\"\'\s]+)/gi, _Replace32);

            var a3 = s3.split('"' + s_SepStr + '"');
            s3 = a3[0];
            for (var i = 0; i < a_pd.length; i++) {
                s3 += a_pd[i] + a3[i + 1];
            }

            return m1 + s3 + m3;
        };

        // attribute to lowercase, add quot
        s_Html = s_Html.replace(/(<\w+(?=[\s]))([^>]*?)(>)/gi, _Replace3);


        // put back the protected data.
        var a_Html = s_Html.split(s_SepStr);
        s_Html = a_Html[0];
        for (var i = 0; i < a_ProtectedData.length; i++) {
            s_Html += a_ProtectedData[i] + a_Html[i + 1];
        }

        return s_Html.Trim();
    },

    _GetSepStr: function (s_Html) {
        var s_SepStr = '__ewebeditor__sepstr__';
        var i = 0;
        while (true) {
            i = i + 1;
            s_SepStr = s_SepStr + i;
            if (s_Html.indexOf(s_SepStr) < 0) {
                break;
            }
        }
        return s_SepStr;
    },

    _GetIndent: function () {
        var n = parseInt(config.CodeFormat);
        var s_Indent = '';
        for (var i = 0; i < n; i++) {
            s_Indent += ' ';
        }
        return s_Indent;
    }


};


//////////////////////////////////////////////////


function sizeChange(n_Size) {
    if (EWEBCommandMaximize.IsMaximized) {
        return;
    }

    var o_Frame = window.frameElement;
    var n_Height = o_Frame.clientHeight;
    var n_New = n_Height + n_Size;
    if (n_New >= config.UIMinHeight) {
        o_Frame.style.height = n_New + "px";
    } else {
        o_Frame.style.height = config.UIMinHeight + "px";
    }
}

function _AutoGrow() {
    var o_Div = EWEB.EditorDocument.createElement("div");
    o_Div.innerHTML = '<span style="margin:0;padding:0;border:0;clear:both;width:1px;height:1px;display:block;">' + (EWEBBrowser.IsWebkit ? '&nbsp;' : '') + '</span>';
    var o_Span = EWEBTools.RemoveNode(o_Div.firstChild);
    var o_Body;
    if (config.FixWidth) {
        o_Body = EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV");
    } else {
        o_Body = EWEB.EditorDocument.body;
    }
    o_Body.appendChild(o_Span);

    var n_NewHeight = EWEBTools.GetDocumentPosition(EWEB.EditorWindow, o_Span).y;
    if (!config.FixWidth) {
        n_NewHeight += o_Span.offsetHeight;
    }

    EWEBTools.RemoveNode(o_Span);

    var n_CurrHeight = EWEBTools.GetViewPaneSize(EWEB.EditorWindow).Height;
    var n_Diff = n_NewHeight - n_CurrHeight;

    if (EWEBBrowser.IsGecko) {
        n_Diff += 20;
    }
    if (Math.abs(n_Diff) < 3) {
        return;
    }

    var o_Iframe = EWEBTools.GetDocumentWindow(document).frameElement;
    var n_CurrIframeHeight = o_Iframe.offsetHeight;
    var n_NewIframeHeight = Math.max(n_CurrIframeHeight + n_Diff, parseInt(config.UIMinHeight));
    o_Iframe.height = n_NewIframeHeight;
}

function _SpellCheck() {
    if (!_IsInIE()) {
        return;
    }

    try {
        var tmpis = new ActiveXObject("ieSpell.ieSpellExtension");
        tmpis.CheckAllLinkedDocuments(EWEB.EditorDocument);
    } catch (exception) {
        if (confirm(lang["MsgIeSpellDownload"])) {
            window.open("http://www.iespell.com/download.php", "IeSpellDownload");
        }
    }
}


function _FindReplace() {
    if (EWEB.CurrMode == "EDIT") {
        showDialog('findreplace.htm', true);
    } else {
        EWEBDialog.OpenDialog('findreplacetext.htm', false);
    }
}


function paragraphAttr() {
    if (!_IsModeEdit()) {
        return;
    }
    EWEB.Focus();

    if (!_IsParagraphRelativeSelection()) {
        alert(lang["MsgNotParagraph"]);
        return;
    }

    showDialog('paragraph.htm', true);
}


function _IsParagraphRelativeSelection() {
    if (EWEBSelection.GetType() == "Control") {
        return false;
    }

    if (EWEBSelection.GetCrossElementsByTags(["P", "DIV"], true)) {
        return true;
    } else {
        return false;
    }
}


function mapEdit() {
    if (!_IsModeEdit()) {
        return;
    }

    var b = false;
    if (EWEBSelection.GetType() == "Control") {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (o_Control.tagName.toUpperCase() == "IMG") {
            if (!EWEBFake._IsFakeElement(o_Control)) {
                b = true;
            }
        }
    }
    if (!b) {
        alert(lang["MsgMapLimit"]);
        return;
    }

    showDialog("map.htm", true);
}


function createLink() {
    if (!_IsModeEdit()) {
        return;
    }

    if (EWEBSelection.GetType() == "Control") {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (o_Control.tagName.toUpperCase() != "IMG") {
            alert(lang["MsgHylnkLimit"]);
            return;
        }
    }

    showDialog("hyperlink.htm", true);
}


function insert(s_CmdName) {
    if (!_IsModeEdit()) {
        return;
    }
    EWEB.Focus();

    var s_Txt = getSelectedText();

    switch (s_CmdName) {
        case "nowdate":
            var d = new Date();
            insertHTML(d.toLocaleDateString());
            break;
        case "nowtime":
            var d = new Date();
            insertHTML(d.toLocaleTimeString());
            break;
        case "br":
            insertHTML("<br>");
            break;
        case "code":
            insertHTML('<table width=95% border="0" align="Center" cellpadding="6" cellspacing="0" style="border: 1px Dotted #CCCCCC; TABLE-LAYOUT: fixed"><tr><td bgcolor=#FDFDDF style="WORD-WRAP: break-word"><font style="color: #990000;font-weight:bold">' + lang["HtmlCode"] + '</font><br>' + HTMLEncode(s_Txt) + '</td></tr></table>');
            break;
        case "quote":
            insertHTML('<table width=95% border="0" align="Center" cellpadding="6" cellspacing="0" style="border: 1px Dotted #CCCCCC; TABLE-LAYOUT: fixed"><tr><td bgcolor=#F3F3F3 style="WORD-WRAP: break-word"><font style="color: #990000;font-weight:bold">' + lang["HtmlQuote"] + '</font><br>' + HTMLEncode(s_Txt) + '</td></tr></table>');
            break;
        case "big":
            insertHTML("<big>" + s_Txt + "</big>");
            break;
        case "small":
            insertHTML("<small>" + s_Txt + "</small>");
            break;
        case "printbreak":
            insertHTML("<div style=\"FONT-SIZE: 1px; PAGE-BREAK-BEFORE: always; VERTICAL-ALIGN: middle; HEIGHT: 1px; BACKGROUND-COLOR: #c0c0c0\">&nbsp; </div>");
            break;
        default:
            alert(lang["ErrParam"]);
            break;
    }
}


var EWEBCommandZoom = {
    Options: [10, 25, 50, 75, 100, 150, 200, 500],
    CurrScale: 100,

    Execute: function (n_Scale) {
        if (EWEBBrowser.IsIE) {
            EWEB.EditorDocument.body.runtimeStyle.zoom = n_Scale + "%";
        } else {
            EWEB.EditorDocument.body.style.transformOrigin = '0 0';
            EWEB.EditorDocument.body.style.transform = 'scale(' + (n_Scale / 100) + ')';
        }

        this.CurrScale = n_Scale;
    }
};


function absPosition() {
    if (EWEBSelection.GetType() != "Control") {
        return;
    }

    if (EWEBBrowser.IsIE) {
        var o_Range = EWEB.EditorDocument.selection.createRange();
        for (var i = 0; i < o_Range.length; i++) {
            var o_Control = o_Range.item(i);
            if (o_Control.style.position != 'relative') {
                o_Control.style.position = 'relative';
            } else {
                o_Control.style.position = 'static';
            }
        }
    } else {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (o_Control.style.position != 'relative') {
            o_Control.style.position = 'relative';
        } else {
            o_Control.style.position = 'static';
        }
    }
    _FireChange();
}


function _SetZIndex(s_Flag) {
    if (EWEBSelection.GetType() != "Control") {
        return;
    }

    if (EWEBBrowser.IsIE) {
        var o_Range = EWEB.EditorDocument.selection.createRange();
        for (var i = 0; i < o_Range.length; i++) {
            var o_Control = o_Range.item(i);
            if (s_Flag == 'forward') {
                o_Control.style.zIndex += 1;
            } else {
                o_Control.style.zIndex -= 1;
            }
            o_Control.style.position = 'relative';
        }
    } else {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (s_Flag == 'forward') {
            o_Control.style.zIndex += 1;
        } else {
            o_Control.style.zIndex -= 1;
        }
        o_Control.style.position = 'relative';
    }
    _FireChange();
}


function formatText(what) {
    EWEBSelection.Restore();
    EWEB.EditorWindow.focus();

    //EWEB.Focus();
    if (EWEBSelection.GetType() != "Text") {
        return;
    }

    if (EWEBBrowser.IsIE) {
        var sel = EWEB.EditorDocument.selection;
        var rng = sel.createRange();

        var r = EWEB.EditorDocument.body.createTextRange();
        var n_Start = 0;
        while (r.compareEndPoints("StartToStart", rng) < 0) {
            r.moveStart("character", 1);
            n_Start++;
        }
        var n_End = 0;
        while (r.compareEndPoints("EndToEnd", rng) > 0) {
            r.moveEnd("character", -1);
            n_End--;
        }

        var a = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"];
        var l, u, s_Search, s_Replace;
        for (var i = 0; i < 26; i++) {
            l = a[i];
            u = a[i].toUpperCase();
            switch (what) {
                case "uppercase":
                    s_Search = l;
                    s_Replace = u;
                    break;
                case "lowercase":
                    s_Search = u;
                    s_Replace = l;
                    break;
            }

            r = rng.duplicate();
            while (r.findText(s_Search, 0, 4)) {
                r.text = s_Replace;
                r = rng.duplicate();
            }
        }

        r = EWEB.EditorDocument.body.createTextRange();
        r.moveStart("character", n_Start);
        r.moveEnd("character", n_End);
        r.select();

    } else {
        _Gecko_FormatText._FormatText(what);
    }
    _FireChange();
}


function formatFont(s_CmdName, s_CmdValue) {
    EWEBSelection.Restore();
    EWEB.EditorWindow.focus();

    if (EWEBSelection.GetType() != "Text") {
        return;
    }

    if (EWEBBrowser.IsIE) {
        var r = EWEB.EditorDocument.selection.createRange();
        if (r.text == "") {
            var b_New = true;
            var o_Par = r.parentElement();
            if (o_Par.tagName != "BODY" && o_Par.getAttribute("id") != "eWebEditor_FixWidth_DIV") {
                if (o_Par.innerHTML == "") {
                    b_New = false;
                }
            }
            if (b_New) {
                var s_TmpID = EWEBTools.GetOnlyTempID(EWEB.EditorDocument);
                insertHTML("<span id='" + s_TmpID + "'>&nbsp;</span>");
                o_Par = EWEB.EditorDocument.getElementById(s_TmpID);
            }

            EWEBSelection.SelectNode(o_Par);
            EWEBSelection.Collapse(false);
            _SetStyleInFont(o_Par, s_CmdName, s_CmdValue);
            if (b_New) {
                o_Par.removeAttribute("id");
            }
            o_Par.innerHTML = "";
            EWEBToolbar.CheckTBStatus();
            return;
        }

        var s_TempKey = "eWebEditor_Temp_FontName";
        EWEB.EditorDocument.execCommand("fontname", "", s_TempKey);

        //var bk = r.getBookmark();
        var a_Font = EWEB.EditorDocument.body.getElementsByTagName("FONT");
        var arr = new Array();
        for (var i = 0; i < a_Font.length; i++) {
            var o_Font = a_Font[i];
            if (o_Font.getAttribute("face") == s_TempKey) {
                arr[arr.length] = a_Font[i];
            }
        }

        for (var i = 0; i < arr.length; i++) {
            var o_Font = arr[i];
            _DelStyleInFont(o_Font, s_CmdName);
            _DelEmptyNodeInFont(o_Font);
            _SetStyleInFont(o_Font, s_CmdName, s_CmdValue);
            o_Font.removeAttribute("face");

            var o_Parent = o_Font.parentElement;
            if (o_Parent.tagName == "FONT") {
                _FontAttribute2Style(o_Parent);
            }
            if ((o_Parent.tagName == "FONT") || (o_Parent.tagName == "SPAN")) {
                if (o_Parent.innerText == o_Font.innerText) {
                    o_Parent.style.cssText = o_Parent.style.cssText + ";" + o_Font.style.cssText;
                    o_Parent.innerHTML = o_Font.innerHTML;
                    arr[i] = o_Parent;
                    continue;
                }
            }
        }

        var f1, f2;
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] && arr[i].innerText) {
                if (!f1) {
                    f1 = arr[i];
                }
                f2 = arr[i];
            }
        }

        var r1 = EWEB.EditorDocument.body.createTextRange();
        r1.moveToElementText(f1);
        var r2 = EWEB.EditorDocument.body.createTextRange();
        r2.moveToElementText(f2);

        r.setEndPoint("StartToStart", r1);
        r.setEndPoint("EndToEnd", r2);

        //r.moveToBookmark(bk);
        r.select();
    } else {
        if (["uppercase", "lowercase", "size"].IndexOf(s_CmdName) >= 0) {
            _Gecko_FormatText._FormatText(s_CmdName, s_CmdValue);
        } else {
            if (s_CmdName == "face") {
                s_CmdName = "fontName";
            }
            EWEB.EditorDocument.execCommand(s_CmdName, false, s_CmdValue);
        }

    }
    _FireChange();
}


function _DelStyleInFont(obj, s_CmdName) {
    _SetFontStyleValue(obj, s_CmdName, "");
    var o_Children = obj.children;
    for (var j = 0; j < o_Children.length; j++) {
        _DelStyleInFont(o_Children[j], s_CmdName);
        if (o_Children[j].tagName == "FONT") {
            _FontAttribute2Style(o_Children[j]);
        }
    }
}

function _DelEmptyNodeInFont(obj) {
    var o_Children = obj.children;
    for (var j = 0; j < o_Children.length; j++) {
        _DelEmptyNodeInFont(o_Children[j]);
        if ((o_Children[j].tagName == "FONT") || (o_Children[j].tagName == "SPAN")) {
            if ((o_Children[j].style.cssText == "" && o_Children[j].className == "") || (o_Children[j].innerHTML == "")) {
                //o_Children[j].outerHTML = o_Children[j].innerHTML;
                o_Children[j].removeNode(false);
                _DelEmptyNodeInFont(obj);
                return;
            }
        }
    }
}

function _SetStyleInFont(obj, s_CmdName, v) {
    _SetFontStyleValue(obj, s_CmdName, v);
    var o_Children = obj.children;
    for (var j = 0; j < o_Children.length; j++) {
        if ((o_Children[j].tagName == "SPAN") || (o_Children[j].tagName == "FONT")) {
            _SetStyleInFont(o_Children[j], s_CmdName, v);
        }
    }
}

function _SetFontStyleValue(obj, s_CmdName, v) {
    try {
        switch (s_CmdName) {
            case "face":
                obj.style.fontFamily = v;
                break;
            case "size":
                obj.style.fontSize = v;
                break;
            case "color":
                obj.style.color = v;
                break;
            default:
                break;
        }
    } catch (e) {
    }
}


function _FontAttribute2Style(el) {
    if (el.style.fontFamily == "") {
        var s = el.getAttribute("face");
        if (s) {
            el.style.fontFamily = s;
        }
    }
    el.removeAttribute("face");

    if (el.style.fontSize == "") {
        var s = el.getAttribute("size");
        s = _Fontsize2Css(s);
        if (s) {
            el.style.fontSize = s;
        }
    }
    el.removeAttribute("size");

    if (el.style.color == "") {
        var s = el.getAttribute("color");
        if (s) {
            el.style.color = s;
        }
    }
    el.removeAttribute("color");
}


var _Gecko_FormatText = (function () {
    var _Stop, _StartFound, _Cmd, _CmdValue;
    var _Nodes = [];
    var _StartContainer, _EndContainer, _StartOffset, _EndOffset, _IsOnlyTextChange, _SameNode;
    var _StartInNodes, _EndInNodes;

    return {

        _FormatText: function (s_Cmd, s_CmdValue) {
            if (EWEBSelection.GetType() != "Text") {
                return;
            }

            _StartFound = false;
            _Stop = false;
            _Nodes.length = 0;
            _Cmd = s_Cmd;
            _CmdValue = s_CmdValue;
            _IsOnlyTextChange = (_Cmd == "uppercase" || _Cmd == "lowercase") ? true : false;
            _SameNode = false;
            _StartInNodes = false;
            _EndInNodes = false;

            var sel = EWEB.EditorWindow.getSelection();
            var rng = sel.getRangeAt(0);
            if (rng.collapsed) {
                if (_Cmd == "size") {
                    var o_NewNode = this._CreateNewSpanNode("&nbsp;", true);
                    rng.insertNode(o_NewNode);
                    EWEBSelection.SelectNode(o_NewNode.firstChild);
                    return;
                } else {
                    return;
                }
            }

            _StartContainer = rng.startContainer;
            _EndContainer = rng.endContainer;
            _StartOffset = rng.startOffset;
            _EndOffset = rng.endOffset;

            if (_StartContainer.nodeType == 1) {
                _StartContainer = this._GetFirstTextNode(_StartContainer);
                if (!_StartContainer) {
                    return;
                }
                _StartOffset = 0;
            }
            if (_EndContainer.nodeType == 1) {
                _EndContainer = this._GetLastTextNode(_EndContainer);
                if (!_EndContainer) {
                    return;
                }
                _EndOffset = _EndContainer.nodeValue.length;
            }

            if (_StartContainer == _EndContainer) {
                _SameNode = true;
                var s_NodeValue = _StartContainer.nodeValue;
                if (_IsOnlyTextChange) {
                    _StartContainer.nodeValue = s_NodeValue.substring(0, _StartOffset) + this._GetFormatValue(s_NodeValue.substring(_StartOffset, _EndOffset)) + s_NodeValue.substring(_EndOffset);
                } else {
                    var o_ParentNode = _StartContainer.parentNode;
                    var t1 = s_NodeValue.substring(0, _StartOffset);
                    var t2 = s_NodeValue.substring(_StartOffset, _EndOffset);
                    var t3 = s_NodeValue.substring(_EndOffset);

                    if (o_ParentNode.tagName.toUpperCase() == "SPAN" && o_ParentNode.innerHTML == t2) {
                        this._SetFormatValue(o_ParentNode);
                    } else {
                        var o_NewNode = this._CreateNewSpanNode(t2);
                        o_NewNode.setAttribute("id", "eWebEditor_Temp_Span_FontSize", 0);
                        o_ParentNode.insertBefore(o_NewNode, _StartContainer);
                        _StartContainer.nodeValue = t3;
                        if (t1) {
                            var o_NewTextNode = EWEB.EditorDocument.createTextNode(t1);
                            o_ParentNode.insertBefore(o_NewTextNode, o_NewNode);
                        }
                    }
                }
            } else {
                this._GoThroughElements(rng.commonAncestorContainer);
            }

            if (_IsOnlyTextChange) {
                rng.setStart(_StartContainer, _StartOffset);
                rng.setEnd(_EndContainer, _EndOffset);
            } else {
                if (_SameNode) {
                    var o_SpanNode = EWEB.EditorDocument.getElementById("eWebEditor_Temp_Span_FontSize");
                    if (o_SpanNode) {
                        o_SpanNode.removeAttribute("id");
                        rng.selectNodeContents(o_SpanNode.firstChild);
                    }
                } else {
                    rng.setStart(_StartContainer, _StartOffset);
                    rng.setEnd(_EndContainer, _EndOffset);
                }
            }

            sel.removeAllRanges();
            sel.addRange(rng);

            EWEB.Focus();
        },

        _GoThroughElements: function (el) {
            if (el == _StartContainer) {
                _StartFound = true;
            }

            if (_StartFound) {
                if (el == _StartContainer) {
                    if (_IsOnlyTextChange) {
                        el.nodeValue = el.nodeValue.substring(0, _StartOffset) + this._GetFormatValue(el.nodeValue.substring(_StartOffset));
                    } else {
                        _Nodes[_Nodes.length] = el;
                        _StartInNodes = true;
                    }
                } else if (el == _EndContainer) {
                    if (_IsOnlyTextChange) {
                        el.nodeValue = this._GetFormatValue(el.nodeValue.substring(0, _EndOffset)) + el.nodeValue.substring(_EndOffset);
                    } else {
                        _Nodes[_Nodes.length] = el;
                        _EndInNodes = true;

                        for (var i = 0; i < _Nodes.length; i++) {
                            if (!_Nodes[i].nodeValue || _Nodes[i].nodeValue == "\n") {
                                continue;
                            }
                            if (i == 0) {
                                var o_ParentNode = _Nodes[i].parentNode;
                                if ((o_ParentNode.tagName.toUpperCase() == "SPAN") && (_Nodes[i].nodeValue.substring(_StartOffset) == o_ParentNode.innerHTML)) {
                                    this._SetFormatValue(o_ParentNode);
                                } else {
                                    var v = _Nodes[i].nodeValue.substring(_StartOffset);
                                    if (v) {
                                        var o_NewNode = this._CreateNewSpanNode(v);
                                        o_ParentNode.insertBefore(o_NewNode, _Nodes[i].nextSibling);
                                        _Nodes[i].nodeValue = _Nodes[i].nodeValue.substring(0, _StartOffset);
                                        if (_StartInNodes) {
                                            _StartContainer = o_NewNode.childNodes[0];
                                            _StartOffset = 0;
                                        }
                                    }
                                }

                            } else if (i == _Nodes.length - 1) {
                                var o_ParentNode = _Nodes[i].parentNode;
                                if ((o_ParentNode.tagName.toUpperCase() == "SPAN") && (_Nodes[i].nodeValue.substring(0, _EndOffset) == o_ParentNode.innerHTML)) {
                                    this._SetFormatValue(o_ParentNode);
                                } else {
                                    var v = _Nodes[i].nodeValue.substring(0, _EndOffset);
                                    if (v) {
                                        var o_NewNode = this._CreateNewSpanNode(v);
                                        o_ParentNode.insertBefore(o_NewNode, _Nodes[i]);
                                        if (_EndInNodes) {
                                            _EndContainer = o_NewNode.childNodes[0];
                                        }
                                        _Nodes[i].nodeValue = _Nodes[i].nodeValue.substring(_EndOffset);
                                    }
                                }

                            } else {
                                var o_ParentNode = _Nodes[i].parentNode;
                                if ((o_ParentNode.tagName.toUpperCase() == "SPAN") && (_Nodes[i].nodeValue == o_ParentNode.innerHTML)) {
                                    this._SetFormatValue(o_ParentNode);
                                } else {
                                    var o_NewNode = this._CreateNewSpanNode(_Nodes[i].nodeValue);
                                    o_ParentNode.replaceChild(o_NewNode, _Nodes[i]);
                                }
                            }
                        }
                    }

                    _Stop = true;

                } else if (el.nodeType == 3) {
                    if (_IsOnlyTextChange) {
                        el.nodeValue = this._GetFormatValue(el.nodeValue);
                    } else {
                        _Nodes[_Nodes.length] = el;
                    }
                }
            }

            if (el.hasChildNodes()) {
                for (var i = 0; i < el.childNodes.length; i++) {
                    if (_Stop) {
                        return;
                    }

                    this._GoThroughElements(el.childNodes[i]);
                }
            }
        },

        _GetFormatValue: function (str) {
            switch (_Cmd) {
                case "uppercase":
                    return str.toUpperCase();
                    break;
                case "lowercase":
                    return str.toLowerCase();
                    break;
                case "size":
                    return "<span id=\"eWebEditor_Temp_Span_FontSize\" style=\"font-size:" + _CmdValue + "\">" + str + "</span>";
                    break;
            }
        },

        _CreateNewSpanNode: function (s_Txt, b_IsHtml) {
            var o_NewSpanNode = EWEB.EditorDocument.createElement("span");
            if (b_IsHtml) {
                o_NewSpanNode.innerHTML = s_Txt;
            } else {
                var o_NewTextNode = EWEB.EditorDocument.createTextNode(s_Txt);
                o_NewSpanNode.appendChild(o_NewTextNode);
            }

            this._SetFormatValue(o_NewSpanNode);
            return o_NewSpanNode;
        },

        _SetFormatValue: function (el) {
            switch (_Cmd) {
                case "size":
                    el.style.fontSize = _CmdValue;
                    break;
                case "face":
                    el.style.fontName = _CmdValue;
                    break;
            }
        },

        _GetFirstTextNode: function (el) {
            if (el.nodeType == 3) {
                return el;
            }
            if (el.hasChildNodes()) {
                for (var i = 0; i < el.childNodes.length; i++) {
                    var o_Node = this._GetFirstTextNode(el.childNodes[i]);
                    if (o_Node) {
                        return o_Node;
                    }
                }
            }
            return null;
        },

        _GetLastTextNode: function (el) {
            var o_Node;
            if (el.nodeType == 3) {
                o_Node = el;
            }
            if (el.hasChildNodes()) {
                for (var i = 0; i < el.childNodes.length; i++) {
                    var o_Node2 = this._GetLastTextNode(el.childNodes[i]);
                    if (o_Node2) {
                        o_Node = o_Node2;
                    }
                }
            }

            if (o_Node) {
                return o_Node;
            } else {
                return null;
            }
        }


    };

})();


///////////////////////////////////////////////////


var EWEBCommandJustify = {

    Execute: function (s_CmdName) {
        if (EWEBSelection.GetType() == "Control") {
            var o_Control = EWEBSelection.GetSelectedElement();
            if (s_CmdName == "justifyfull") {
                o_Control.removeAttribute("align");
            } else {
                o_Control.align = s_CmdName.substr(7);
            }

            var p = EWEBTools.GetParentNodeByTags(o_Control, ["P", "DIV", "TD", "TH"]);
            var p1, p2;
            if (p) {
                if (EWEBTools.ArrayIndexOf(["TD", "TH"], p.tagName) >= 0) {
                    p2 = p;
                } else {
                    p1 = p;
                }
            } else {
                p2 = EWEBTools.GetBodyNode();
            }

            if (p2) {
                var c1 = EWEBTools.GetParentNodeUnder(o_Control, p2);
                var o_NewP = EWEB.EditorDocument.createElement("p");
                c1.parentNode.insertBefore(o_NewP, c1);
                o_NewP.appendChild(c1.parentNode.removeChild(c1));
                p1 = o_NewP;
            }

            if (p1) {
                p1.removeAttribute("align");
                if (s_CmdName == "justifyfull") {
                    p1.style.textAlign = "justify";
                    p1.style.textJustify = "inter-ideograph";
                } else {
                    p1.style.textAlign = s_CmdName.substr(7);
                    p1.style.textJustify = "";
                }
            }

        } else {
            var a = EWEBSelection.GetCrossElementsByTags(["P", "DIV", "TD", "TH"]);
            if (a.length > 0) {
                for (var i = 0; i < a.length; i++) {
                    var p = a[i];
                    if (s_CmdName == "justifyfull") {
                        //p.align = "justify";
                        p.style.textAlign = "justify";
                        p.style.textJustify = "inter-ideograph";
                    } else {
                        //p.align = s_CmdName.substr(7);
                        p.style.textAlign = s_CmdName.substr(7);
                        p.style.textJustify = "";
                    }
                    p.removeAttribute("align");

                }
            } else {
                EWEB.EditorDocument.execCommand(s_CmdName, false, null);
            }
        }
        EWEB.Focus();
        EWEBToolbar.CheckTBStatus();
        _FireChange();
    }

};

/////////////////////////////////////////////////////////

var EWEBCommandCopyCut = new Object();

EWEBCommandCopyCut.Execute = function (s_CmdName) {
    var b_Enabled = false;

    if (EWEBBrowser.IsIE) {

        var onEvent = function () {
            b_Enabled = true;
        };

        var s_EventName = 'on' + s_CmdName.toLowerCase();

        EWEB.EditorDocument.body.attachEvent(s_EventName, onEvent);
        EWEB.EditorDocument.execCommand(s_CmdName, false, null);
        EWEB.EditorDocument.body.detachEvent(s_EventName, onEvent);
    } else {
        try {
            b_Enabled = EWEB.EditorDocument.execCommand(s_CmdName, false, null);
        } catch (e) {
        }
    }

    if (!b_Enabled) {
        if (config.AutoDetectPaste == "1") {
            EWEBActiveX.AsynCallBack("isinstalled", [true],
                function (o_Data) {
                    if (!o_Data["Ret"]) {
                        return;
                    } else {
                        var s_Html = getSelectedHTML();
                        var s_Txt = getSelectedText();
                        EWEBActiveX.AsynCallBack("setclipboard", ["html", s_Html, s_Txt]);
                        if (s_CmdName == "Cut") {
                            exec("delete");
                        }
                    }
                }
            );
        } else {
            alert(lang['MsgSafe' + s_CmdName]);
        }
    } else {
        if (s_CmdName == "Cut") {
            _FireChange();
        }
    }
};

/////////////////////////////////////////////////////////


var EWEBCommandMaximize = new Object();

EWEBCommandMaximize.Execute = function () {

    var o_Frame = window.frameElement;
    var o_ParentDocEl = parent.document.documentElement;
    var o_ParentBodyStyle = parent.document.body.style;
    var o_Parent;


    if (!this.IsMaximized) {
        EWEBTools.AddEventListener(parent, 'resize', _EWEBCommandMaximize_Resize);

        this._ScrollPos = EWEBTools.GetScrollPosition(parent);
        this._EditorFrameStyles = EWEBTools.SaveStyles(o_Frame);

        o_Parent = o_Frame;
        while ((o_Parent = o_Parent.parentNode)) {
            if (o_Parent.nodeType == 1) {
                o_Parent._ewebSavedStyles = EWEBTools.SaveStyles(o_Parent);
            }
        }

        if (EWEBBrowser.IsIE) {
            this._Overflow = o_ParentDocEl.style.overflow;
            o_ParentDocEl.style.overflow = 'hidden';
            o_ParentBodyStyle.overflow = 'hidden';
        } else {
            o_ParentBodyStyle.overflow = 'hidden';
            o_ParentBodyStyle.width = '0px';
            o_ParentBodyStyle.height = '0px';
        }

        var o_ViewPaneSize = EWEBTools.GetViewPaneSize(parent);
        o_Frame.style.position = "absolute";
        o_Frame.offsetLeft;
        o_Frame.style.zIndex = 9999777;
        o_Frame.style.left = "0px";
        o_Frame.style.top = "0px";
        o_Frame.style.width = o_ViewPaneSize.Width + "px";
        o_Frame.style.height = o_ViewPaneSize.Height + "px";
        o_Frame.style.boxSizing = "content-box";

        if (!EWEBBrowser.IsIE) {
            o_Frame.style.borderRight = o_Frame.style.borderBottom = "9999px solid white";
            o_Frame.style.backgroundColor = "white";
        }

        parent.scrollTo(0, 0);

        var editorPos = EWEBTools.GetWindowPosition(parent, o_Frame);
        var n_FrameBorderLeft = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Frame, "border-left-width"));
        var n_FrameBorderTop = _ParseInt0(EWEBTools.GetCurrentElementStyle(o_Frame, "border-top-width"));
        var n_PosX = -1 * editorPos.x - n_FrameBorderLeft;
        var n_PosY = -1 * editorPos.y - n_FrameBorderTop;
        if (n_PosX != 0) {
            o_Frame.style.left = n_PosX + "px";
        }
        if (n_PosY != 0) {
            o_Frame.style.top = n_PosY + "px";
        }

        this.IsMaximized = true;

    } else {

        EWEBTools.RemoveEventListener(parent, 'resize', _EWEBCommandMaximize_Resize);

        o_Parent = o_Frame;
        while ((o_Parent = o_Parent.parentNode)) {
            if (o_Parent._ewebSavedStyles) {
                EWEBTools.RestoreStyles(o_Parent, o_Parent._ewebSavedStyles);
                o_Parent._ewebSavedStyles = null;
            }
        }

        if (EWEBBrowser.IsIE) {
            o_ParentDocEl.style.overflow = this._Overflow;
        }

        EWEBTools.RestoreStyles(o_Frame, this._EditorFrameStyles);
        parent.scrollTo(this._ScrollPos.X, this._ScrollPos.Y);

        this.IsMaximized = false;
    }

    var o_SB_Size = $("eWebEditor_SB_Size");
    if (o_SB_Size) {
        o_SB_Size.style.display = this.IsMaximized ? "none" : "";
    }

    this._RefreshState();

    if (EWEB.CurrMode == "EDIT" && !EWEBBrowser.IsIE) {
        EWEB.MakeEditable();
    }

    EWEB.Focus();

};


EWEBCommandMaximize._RefreshState = function () {
    EWEBToolbar.SetBtnsStatus("other", "Maximize", this.IsMaximized);
};

function _EWEBCommandMaximize_Resize() {
    var o_ViewPaneSize = EWEBTools.GetViewPaneSize(parent);
    var o_Frame = window.frameElement;
    o_Frame.style.width = o_ViewPaneSize.Width + 'px';
    o_Frame.style.height = o_ViewPaneSize.Height + 'px';
}


/////////////////////////////////////////////////////////

function ShowMathFlowEQ() {
    if (!_IsInWindow()) {
        return;
    }
    if (EWEBActiveX.CheckIsRun()) {
        return;
    }

    EWEBActiveX.AsynCallBack("isinstalled", [true],
        function (o_Data) {
            if (!o_Data["Ret"]) {
                EWEBActiveX.SetIsRun(false);
                return;
            }
            _ShowMathFlowEQ2();
        }
    );
}

function _ShowMathFlowEQ2() {
    var s_Type = "[mathfloweditor]";
    if (EWEBParam.EQType) {
        s_Type = EWEBParam.EQType;
    }

    var s_Opt = "type:" + s_Type + ";";
    switch (s_Type) {
        case "simpleeditor":
            s_Opt += "width:536;height:300;";
            break;
        case "styleeditor":
            s_Opt += "width:570;height:300;";
            break;
        case "structureeditor":
            s_Opt += "width:570;height:600;";
            break;
        default:
            alert("Not Installed!");
            EWEBActiveX.SetIsRun(false);
            return;
            break;
    }

    var s_Action = "insert";
    var s_PointSize = "";
    var s_MathML = "";
    var n_MinSize = 9;
    var n_DefaultSize = 11;
    var s_FontSize = "";
    if (EWEBSelection.GetType() == "Control") {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (o_Control.tagName == "IMG" && o_Control.getAttribute("_tag", 2) == "math") {
            s_Action = "edit";
            s_MathML = o_Control.getAttribute("_mathml", 2);
            if (!s_MathML) {
                s_MathML = ""
            }
            s_PointSize = o_Control.getAttribute("_pointsize", 2);
            if (!s_PointSize) {
                s_PointSize = ""
            }
        } else {
            s_FontSize = EWEBTools.GetCurrentElementStyle(o_Control.parentNode, "font-size");
            var s_Tmp = _Fontsize2Css(s_FontSize);
            if (s_Tmp) {
                s_FontSize = s_Tmp;
            }
        }
    } else {
        var s_FontSize = EWEBSelection.QueryFontSize();
    }

    if (s_Action == "insert") {
        s_FontSize = s_FontSize.toLowerCase();
        var n_Size = parseFloat(s_FontSize);
        if (isNaN(n_Size)) {
            s_PointSize = n_DefaultSize + "";
        } else {
            if (s_FontSize.indexOf("px") > 0) {
                n_Size = n_Size * 3 / 4;
            }
            n_Size = parseInt(n_Size + 0.5);
            if (n_Size < n_MinSize) {
                n_Size = n_MinSize;
            }
            s_PointSize = n_Size + "";
        }
    }

    s_Opt += "action:" + s_Action + ";pointsize:" + s_PointSize + ";";

    EWEBActiveX.AsynCallBack("showeq", [s_MathML, s_Opt],
        function (o_Data) {

            if (o_Data["Error"] == "nojre") {
                EWEBActiveX.SetIsRun(false);
                showDialog("installjre.htm", true);
                return;
            }
            if (o_Data["Error"] == "cancel") {
                EWEBActiveX.SetIsRun(false);
                return;
            }
            if (EWEBActiveX.IsError(o_Data["Error"])) {
                EWEBActiveX.SetIsRun(false);
                return;
            }

            var s_HTML = o_Data["Body"];
            insertHTML(s_HTML);

            EWEBActiveX.SetIsRun(false);

            EWEBHistory.DoChange();
            EWEBHistory.Save();

            var s_Src = s_HTML.replace(/.*<img\s[^>]*?src=\"([^\"]+?)\"[^>]*?>.*/gi, "$1");
            if (s_Src != s_HTML) {
                addUploadFile("", s_Src);
            }

        }
    );
}


/////////////////////////////////////////////////////////


function ShowWordEQ() {
    if (!_IsInWindow()) {
        return;
    }
    if (EWEBActiveX.CheckIsRun()) {
        return;
    }

    EWEBActiveX.AsynCallBack("isinstalled", [true],
        function (o_Data) {
            if (!o_Data["Ret"]) {
                EWEBActiveX.SetIsRun(false);
                return;
            }
            _ShowWordEQ2();
        }
    );
}

function _ShowWordEQ2() {
    var s_Ver = config.WordEqVersion;
    if (s_Ver == "") {
        alert("Not Installed!");
        EWEBActiveX.SetIsRun(false);
        return;
    }

    EWEBActiveX.AsynCallBack("getinfo", ["wordeqinstall", s_Ver, ""],
        function (o_Data) {
            if (!o_Data["Ret"]) {
                EWEBActiveX.SetIsRun(false);
                return;
            }

            if (o_Data["Ret"] != "yes") {
                EWEBActiveX.SetIsRun(false);
                showDialog("installwordeq.htm", true);
                return;
            } else {
                _ShowWordEQ3();
            }
        }
    );
}

function _ShowWordEQ3() {
    var s_Action = "insert";
    var s_EqCode = "";
    var s_EqType = "";
    var n_MinSize = 9;
    var n_DefaultSize = 11;
    var s_FontSize = "";
    var s_PointSize = "";
    if (EWEBSelection.GetType() == "Control") {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (o_Control.tagName == "IMG" && o_Control.getAttribute("_tag", 2) == "wordeq") {
            s_Action = "edit";
            s_EqCode = o_Control.getAttribute("_eqcode", 2);
            if (!s_EqCode) {
                s_EqCode = ""
            }
            s_EqType = o_Control.getAttribute("_eqtype", 2);
            if (!s_EqType) {
                s_EqType = ""
            }
        } else {
            s_FontSize = EWEBTools.GetCurrentElementStyle(o_Control.parentNode, "font-size");
            var s_Tmp = _Fontsize2Css(s_FontSize);
            if (s_Tmp) {
                s_FontSize = s_Tmp;
            }
        }
    } else {
        var s_FontSize = EWEBSelection.QueryFontSize();
    }

    if (s_Action == "insert") {
        s_FontSize = s_FontSize.toLowerCase();
        var n_Size = parseFloat(s_FontSize);
        if (isNaN(n_Size)) {
            s_PointSize = n_DefaultSize + "";
        } else {
            if (s_FontSize.indexOf("px") > 0) {
                n_Size = n_Size * 3 / 4;
            }
            n_Size = parseInt(n_Size + 0.5);
            if (n_Size < n_MinSize) {
                n_Size = n_MinSize;
            }
            s_PointSize = n_Size + "";
        }
    }

    var s_Opt = "wordeq:1;width:880;height:500;action:" + s_Action + ";pointsize:" + s_PointSize + ";eqtype:" + s_EqType + ";ver:" + config.WordEqVersion + ";";

    EWEBActiveX.AsynCallBack("showeq", [s_EqCode, s_Opt],
        function (o_Data) {
            if (o_Data["Error"] == "cancel") {
                EWEBActiveX.SetIsRun(false);
                return;
            }
            if (EWEBActiveX.IsError(o_Data["Error"])) {
                EWEBActiveX.SetIsRun(false);
                return;
            }

            var s_HTML = o_Data["Body"];
            EWEBActiveX.SetIsRun(false);

            if (!s_HTML) {
                return;
            }

            insertHTML(s_HTML);

            EWEBHistory.DoChange();
            EWEBHistory.Save();

            var s_Src = s_HTML.replace(/.*<img\s[^>]*?src=\"([^\"]+?)\"[^>]*?>.*/gi, "$1");
            if (s_Src != s_HTML) {
                addUploadFile("", s_Src);
            }

        }
    );
}


/////////////////////////////////////////////////////////


function doCapture() {
    if (!_IsInWindow()) {
        return;
    }
    if (EWEBActiveX.CheckIsRun()) {
        return;
    }

    EWEBActiveX.AsynCallBack("isinstalled", [true],
        function (o_Data) {
            if (!o_Data["Ret"]) {
                EWEBActiveX.SetIsRun(false);
                return;
            }

            EWEBActiveX.AsynCallBack("capture", [""],
                function (o_Data) {
                    if (o_Data["Error"] == "cancel") {
                        EWEBActiveX.SetIsRun(false);
                        return;
                    }
                    if (EWEBActiveX.IsError(o_Data["Error"])) {
                        EWEBActiveX.SetIsRun(false);
                        return;
                    }

                    var s_HTML = o_Data["Body"];
                    s_HTML = s_HTML.replace(/(<img\s[^>]*?src=\"[^\"]+?)(\[~~\][^\"]+?)(\"[^>]*?>)/gi, "$1$3");
                    insertHTML(s_HTML);

                    EWEBActiveX.SetIsRun(false);

                    EWEBHistory.DoChange();
                    EWEBHistory.Save();

                    var s_Src = s_HTML.replace(/.*<img\s[^>]*?src=\"([^\"]+?)\"[^>]*?>.*/gi, "$1");
                    if (s_Src != s_HTML) {
                        addUploadFile("", s_Src);
                    }
                }
            );
        }
    );
}


/////////////////////////////////////////////////////////


function doPaste() {
    if (config.AutoDetectPaste == "2") {
        window.setTimeout("pasteText()", 10);
        return;
    }

    if (config.AutoDetectPaste == "1") {
        EWEBActiveX.AsynCallBack("isinstalled", [true],
            function (o_Data) {
                if (!o_Data["Ret"]) {
                    return false;
                }

                _CheckClipboardFlag();
            }
        );
    } else {
        if (EWEBBrowser.IsAllIE) {
            IEPasteHTML();
        } else {
            showDialog("pastegecko.htm");
        }
    }
}

function _CheckClipboardFlag() {
    EWEBActiveX.AsynCallBack("getclipboard", ["flag"],
        function (o_Data) {
            var s_Flag = o_Data["Ret"];
            var a_Flag = s_Flag.split("|");
            if (a_Flag[5] == "1") {
                pasteWord();
                return;
            } else if (a_Flag[5] == "2") {
                pasteExcel();
                return;
            } else if (a_Flag[5] == "3") {
                if (EWEBActiveX.CheckIsRun()) {
                    return;
                }
                EWEBActiveX.AsynCallBack("getclipboard", ["wps"],
                    function (o_Data) {
                        var s_HTML = o_Data["Ret"];
                        EWEBActiveX.AsynCallBack("localupload", [s_HTML],
                            function (o_Data) {
                                _LocalUploadStatus(o_Data, "insert", {flag: "Paste", type: "wps"});
                            }
                        );
                    }
                );
                return;
            } else if (a_Flag[5] == "4") {
                if (EWEBActiveX.CheckIsRun()) {
                    return;
                }
                EWEBActiveX.AsynCallBack("getclipboard", ["et"],
                    function (o_Data) {
                        var s_HTML = o_Data["Ret"];
                        EWEBActiveX.AsynCallBack("localupload", [s_HTML],
                            function (o_Data) {
                                _LocalUploadStatus(o_Data, "insert", {flag: "Paste", type: "et"});
                            }
                        );
                    }
                );
                return;
            } else if (a_Flag[0] == "1" || a_Flag[3] == "1" || a_Flag[5] == "5") {
                PasteOption(s_Flag);
                return;
            } else if (a_Flag[4] == "1") {
                EWEBActiveX.AsynCallBack("getclipboard", ["html"],
                    function (o_Data) {
                        var s_HTML = o_Data["Ret"];
                        insertHTML(s_HTML);
                        _FireEvent({flag: "Paste", type: "html"});
                    }
                );
                return;
            } else if (a_Flag[1] == "1") {
                EWEBActiveX.AsynCallBack("getclipboard", ["text"],
                    function (o_Data) {
                        var s_HTML = HTMLEncode(o_Data["Ret"]);
                        insertHTML(s_HTML);
                        _FireEvent({flag: "Paste", type: "text"});
                    }
                );
                return;
            } else {
                return;
            }

        }
    );
}

function IEPasteHTML() {
    var s_Html = _IEGetClipboardHTML();
    insertHTML(s_Html);
    _FireEvent({flag: "Paste", type: "html"});
}

function PasteOption(s_Flag) {
    s_Flag = s_Flag.replace(/\|/gi, ",");
    if (config.AutoDonePasteOption) {
        showDialog("pasteoption.htm?autodone=1&flag=" + s_Flag, true, false, true);
    } else {
        showDialog("pasteoption.htm?flag=" + s_Flag, true);
    }
}

function pasteWord() {
    if (config.AutoDonePasteWord) {
        showDialog("importword.htm?action=paste&autodone=1", true, false, true);
    } else {
        showDialog("importword.htm?action=paste", true);
    }

    //	showDialog("pastegecko.htm?action=word");
}

function pasteExcel() {
    if (config.AutoDonePasteExcel) {
        showDialog("importexcel.htm?action=paste&autodone=1", true, false, true);
    } else {
        showDialog("importexcel.htm?action=paste", true);
    }
}

function pasteText() {
    if (EWEBBrowser.IsAllIE) {
        var s_HTML = HTMLEncode(clipboardData.getData("Text"));
        insertHTML(s_HTML);
        _FireEvent({flag: "Paste", type: "text"});
    } else {
        if (config.AutoDetectPaste == "1") {
            EWEBActiveX.AsynCallBack("isinstalled", [true],
                function (o_Data) {
                    if (o_Data["Ret"]) {
                        EWEBActiveX.AsynCallBack("getclipboard", ["text"],
                            function (o_Data) {
                                var s_HTML = HTMLEncode(o_Data["Ret"]);
                                insertHTML(s_HTML);
                                _FireEvent({flag: "Paste", type: "text"});
                            }
                        );
                    } else {
                        return;
                    }
                }
            );
        } else {
            if (EWEBBrowser.IsUseLS) {
                showDialog("pastegecko.htm?action=text");
            } else {
                EWEBActiveX.AsynCallBack("isinstalled", [false],
                    function (o_Data) {
                        if (o_Data["Ret"]) {
                            EWEBActiveX.AsynCallBack("getclipboard", ["text"],
                                function (o_Data) {
                                    var s_HTML = HTMLEncode(o_Data["Ret"]);
                                    insertHTML(s_HTML);
                                    _FireEvent({flag: "Paste", type: "text"});
                                }
                            );
                        } else {
                            showDialog("pastegecko.htm?action=text");
                        }
                    }
                );
            }
        }
    }
}


function _IEGetClipboardHTML() {
    var o_Div = $("eWebEditor_Temp_HTML");
    o_Div.innerHTML = "";

    var o_TextRange = document.body.createTextRange();
    o_TextRange.moveToElementText(o_Div);
    o_TextRange.execCommand("Paste");

    var s_Html = o_Div.innerHTML;
    o_Div.innerHTML = "";

    return s_Html;
}

/////////////////////////////////////////////////////////

var EWEBCommandShowBorders = {

    Execute: function () {
        this._InitStateFromConfig();
        this._IsOnState = !this._IsOnState;
        this._RefreshState();
    },

    RestoreState: function () {
        this._InitStateFromConfig();
        if (this._IsOnState) {
            this._RefreshState();
        }
    },

    _InitStateFromConfig: function () {
        if (typeof(this._IsOnState) == "undefined") {
            this._IsOnState = (config.ShowBorder == "0") ? false : true;
        }
    },

    _RefreshState: function () {
        var els = EWEB.EditorDocument.getElementsByTagName("TABLE");
        for (var i = 0; i < els.length; i++) {
            var el = els[i];
            if (this._IsOnState) {
                el.className += ' ewebeditor__showtableborders';
            } else {
                el.className = el.className.replace(/(^| )ewebeditor__showtableborders/gi, '');
            }
        }

        EWEBToolbar.SetBtnsStatus("other", "ShowBorders", this._IsOnState);
    }

};


/////////////////////////////////////////////////////////


var EWEBCommandShowBlocks = {

    Execute: function () {
        this._InitStateFromConfig();
        this._IsOnState = !this._IsOnState;
        this._RefreshState();
    },

    RestoreState: function () {
        this._InitStateFromConfig();
        if (this._IsOnState) {
            this._RefreshState();
        }
    },

    _InitStateFromConfig: function () {
        if (typeof(this._IsOnState) == "undefined") {
            this._IsOnState = (config.ShowBlock == "1") ? true : false;
        }
    },


    _RefreshState: function () {
        var o_Body = EWEB.EditorDocument.body;
        if (this._IsOnState) {
            o_Body.className += ' ewebeditor__showblocks';
        } else {
            o_Body.className = o_Body.className.replace(/(^| )ewebeditor__showblocks/gi, '');
        }

        EWEBToolbar.SetBtnsStatus("other", "ShowBlocks", this._IsOnState);
    }

};


/////////////////////////////////////////////////////////


var EWEBCommandFormatBrush = (function () {
    var _IsOnState = false;
    var _FontName;
    var _FontSize;
    var _Bold;
    var _Italic;
    var _UnderLine;
    var _StrikeThrough;
    var _SuperScript;
    var _SubScript;
    var _ForeColor;
    var _BackColor;

    var _RefreshBtnState = function () {
        EWEBToolbar.SetBtnsStatus("other", "FormatBrush", _IsOnState);
    };

    var _QueryCmdValue = function (s_Cmd) {
        var v;
        switch (s_Cmd) {
            case "fontsize":
                v = EWEBSelection.QueryFontSize();
                break;
            case "bold":
            case "italic":
            case "underline":
            case "strikethrough":
            case "superscript":
            case "subscript":
                v = EWEB.EditorDocument.queryCommandState(s_Cmd);
                break;

            case "fontname":
                v = EWEB.EditorDocument.queryCommandValue(s_Cmd);
                break;
            case "forecolor":
            case "backcolor":
                if (!EWEBBrowser.IsIE && !EWEBBrowser.IsChrome && s_Cmd == "backcolor") {
                    s_Cmd = "hiliteColor";
                }

                v = EWEB.EditorDocument.queryCommandValue(s_Cmd);
                if (EWEBBrowser.IsIE) {
                    v = _N2Color(v);
                }
                break;
        }

        if (v == null) {
            v = "";
        }

        return v;
    };

    var _N2Color = function (n_Color) {
        if (n_Color == null) {
            return "";
        }

        var s_Color = n_Color.toString(16);
        switch (s_Color.length) {
            case 1:
                s_Color = "0" + s_Color + "0000";
                break;
            case 2:
                s_Color = s_Color + "0000";
                break;
            case 3:
                s_Color = s_Color.substring(1, 3) + "0" + s_Color.substring(0, 1) + "00";
                break;
            case 4:
                s_Color = s_Color.substring(2, 4) + s_Color.substring(0, 2) + "00";
                break;
            case 5:
                s_Color = s_Color.substring(3, 5) + s_Color.substring(1, 3) + "0" + s_Color.substring(0, 1);
                break;
            case 6:
                s_Color = s_Color.substring(4, 6) + s_Color.substring(2, 4) + s_Color.substring(0, 2);
                break;
            default:
                s_Color = "";
        }
        return '#' + s_Color;
    };

    var _BrushSimpleFormat = function (b_FromValue, s_Cmd) {
        var b_ToValue = _QueryCmdValue(s_Cmd);
        if (b_FromValue != b_ToValue) {
            format(s_Cmd);
        }
        if (!b_FromValue && !b_ToValue) {
            format(s_Cmd);
            format(s_Cmd);
        }
    };

    return {

        IsOnState: function () {
            return _IsOnState;
        },

        Execute: function () {
            if (!_IsOnState) {
                _FontName = _QueryCmdValue("fontname");
                _FontSize = _QueryCmdValue("fontsize");
                _Bold = _QueryCmdValue("bold");
                _Italic = _QueryCmdValue("italic");
                _UnderLine = _QueryCmdValue("underline");
                _StrikeThrough = _QueryCmdValue("strikethrough");
                _SuperScript = _QueryCmdValue("superscript");
                _SubScript = _QueryCmdValue("subscript");
                _ForeColor = _QueryCmdValue("forecolor");
                _BackColor = _QueryCmdValue("backcolor");
            }
            _IsOnState = !_IsOnState;
            _RefreshBtnState();
        },

        Stop: function () {
            if (!_IsOnState) {
                return;
            }
            _IsOnState = false;
            _RefreshBtnState();
        },

        Brush: function () {
            if (!_IsOnState) {
                return;
            }

            if (EWEBSelection.GetType() != "Text") {
                return;
            }

            var s_SelText;
            if (EWEBBrowser.IsIE) {
                s_SelText = EWEBSelection.GetSelection().createRange().text;
            } else {
                s_SelText = EWEBSelection.GetSelection() + "";
            }
            if (!s_SelText) {
                return;
            }

            if (_FontName && _FontName != _QueryCmdValue("fontname")) {
                formatFont('face', _FontName);
            }

            _BrushSimpleFormat(_Bold, "bold");
            _BrushSimpleFormat(_Italic, "italic");
            _BrushSimpleFormat(_UnderLine, "underline");
            _BrushSimpleFormat(_StrikeThrough, "strikethrough");
            _BrushSimpleFormat(_SuperScript, "superscript");
            _BrushSimpleFormat(_SubScript, "subscript");

            if (_ForeColor && _ForeColor != _QueryCmdValue("forecolor")) {
                if (EWEBBrowser.IsIE) {
                    formatFont('color', _ForeColor);
                } else {
                    formatFont('forecolor', _ForeColor);
                }
            }

            if (_BackColor && _BackColor != _QueryCmdValue("backcolor")) {
                if (EWEBBrowser.IsIE) {
                    format('backcolor', _BackColor);
                } else {
                    format('hiliteColor', _BackColor);
                }
            }

            if (_FontSize && _FontSize != _QueryCmdValue("fontsize")) {
                formatFont('size', _FontSize);
            }

            EWEBHistory.DoChange();
            EWEBHistory.Save();

            _FireChange();
        }

    };

})();


/////////////////////////////////////////////////////////

function _DoImageDoc() {
    if (!_IsInIE()) {
        return;
    }
    showDialog('imagedoc.htm', true);
}


function DoImport(s_Flag) {
    if (!_IsInWindow()) {
        return;
    }
    switch (s_Flag) {
        case "word":
            showDialog('importword.htm', true);
            break;
        case "excel":
            showDialog('importexcel.htm', true);
            break;
        case "ppt":
            showDialog('importppt.htm', true);
            break;
        case "pdf":
            showDialog('importpdf.htm', true);
            break;
    }
}

/////////////////////////////////////////////////////////

var EWEBCommandForm = {

    Insert: function (s_Flag) {
        switch (s_Flag) {
            case "inputtext":
                insertHTML('<input type="text">');
                break;
            case "textarea":
                insertHTML('<textarea></textarea>');
                break;
            case "radio":
                insertHTML('<input type="radio">');
                break;
            case "checkbox":
                insertHTML('<input type="checkbox">');
                break;
            case "select":
                insertHTML('<select></select>');
                break;
            case "button":
                insertHTML('<input type="button">');
                break;
        }
    }

};

/////////////////////////////////////////////////////////


function DoPrint() {
    EWEB.EditorWindow.print();
}

/////////////////////////////////////////////////////////


var EWEBTools = new Object();

EWEBTools.Clone = function (obj) {
    var _clone;

    // Array.
    if (obj && (obj instanceof Array)) {
        _clone = [];

        for (var i = 0; i < obj.length; i++) {
            _clone[i] = this.Clone(obj[i]);
        }

        return _clone;
    }

    // "Static" types.
    if (obj === null
        || (typeof(obj) != 'object')
        || (obj instanceof String)
        || (obj instanceof Number)
        || (obj instanceof Boolean)
        || (obj instanceof Date)
        || (obj instanceof RegExp)) {
        return obj;
    }

    // Objects.
    _clone = new obj.constructor();

    for (var s_PropertyName in obj) {
        var property = obj[s_PropertyName];
        _clone[s_PropertyName] = this.Clone(property);
    }

    return _clone;
};


EWEBTools.IsStrictMode = function (o_Doc) {
    // There is no compatMode in Safari, but it seams that it always behave as
    // CSS1Compat, so let's assume it as the default for that browser.
    return ("CSS1Compat" == (o_Doc.compatMode || (EWEBBrowser.IsSafari ? "CSS1Compat" : null)));
};


EWEBTools.ResetStyles = function (o_Element) {
    o_Element.style.cssText = "margin:0;" +
        "padding:0;" +
        "border:0;" +
        "background-color:transparent;" +
        "background-image:none;";
};


EWEBTools.GetElementDocument = function (o_Element) {
    return o_Element.ownerDocument || o_Element.document;
};


EWEBTools.GetElementWindow = function (o_Element) {
    return this.GetDocumentWindow(this.GetElementDocument(o_Element));
};

EWEBTools.GetDocumentWindow = function (o_Doc) {
    return o_Doc.parentWindow || o_Doc.defaultView;
};

EWEBTools.GetTopWindow = function () {
    var o_TopWindow = window.parent;

    while (o_TopWindow.parent && o_TopWindow.parent != o_TopWindow) {
        try {
            if (o_TopWindow.parent.document.domain != document.domain) {
                break;
            }
            if (o_TopWindow.parent.document.getElementsByTagName("frameset").length > 0) {
                break;
            }
        } catch (e) {
            break;
        }
        o_TopWindow = o_TopWindow.parent;
    }

    return o_TopWindow;
};


EWEBTools.GetDocumentPosition = function (w, node) {
    var x = 0;
    var y = 0;
    var curNode = node;
    var prevNode = null;
    var curWindow = EWEBTools.GetElementWindow(curNode);
    while (curNode && !(curWindow == w && (curNode == w.document.body || curNode == w.document.documentElement))) {
        x += curNode.offsetLeft - curNode.scrollLeft;
        y += curNode.offsetTop - curNode.scrollTop;

        if (!EWEBBrowser.IsOpera) {
            var scrollNode = prevNode;
            while (scrollNode && scrollNode != curNode) {
                x -= scrollNode.scrollLeft;
                y -= scrollNode.scrollTop;
                scrollNode = scrollNode.parentNode;
            }
        }

        prevNode = curNode;
        if (curNode.offsetParent) {
            curNode = curNode.offsetParent;
        } else {
            if (curWindow != w) {
                curNode = curWindow.frameElement;
                prevNode = null;
                if (curNode) {
                    curWindow = curNode.contentWindow.parent;
                }
            } else {
                curNode = null;
            }
        }
    }

    if (EWEBTools.GetCurrentElementStyle(w.document.body, 'position') != 'static' || (EWEBBrowser.IsIE && EWEBTools.GetPositionedAncestor(node) == null)) {
        x += w.document.body.offsetLeft;
        y += w.document.body.offsetTop;
    }

    return {"x": x, "y": y};
};

EWEBTools.GetWindowPosition = function (o_Win, o_Node) {
    var o_Pos = this.GetDocumentPosition(o_Win, o_Node);
    var o_Scroll = EWEBTools.GetScrollPosition(o_Win);
    o_Pos.x -= o_Scroll.X;
    o_Pos.y -= o_Scroll.Y;
    return o_Pos;
};

EWEBTools.ScrollIntoView = function (o_Element, b_AlignTop) {
    var o_Win = this.GetElementWindow(o_Element);
    var n_WindowHeight = this.GetViewPaneSize(o_Win).Height;

    var n_Offset = n_WindowHeight * -1;

    if (b_AlignTop === false) {
        n_Offset += o_Element.offsetHeight || 0;
        n_Offset += parseInt(this.GetCurrentElementStyle(o_Element, 'marginBottom') || 0, 10) || 0;
    }

    var elementPosition = this.GetDocumentPosition(o_Win, o_Element);
    n_Offset += elementPosition.y;

    var currentScroll = this.GetScrollPosition(o_Win).Y;
    if (n_Offset > 0 && (n_Offset > currentScroll || n_Offset < currentScroll - n_WindowHeight)) {
        o_Win.scrollTo(0, n_Offset);
    }
};

EWEBTools.ProtectFormStyles = function (o_formNode) {
    if (!o_formNode || o_formNode.nodeType != 1 || o_formNode.tagName.toLowerCase() != 'form') {
        return [];
    }
    var a_HijackRecord = [];
    var a_HijackNames = ['style', 'className'];
    for (var i = 0; i < a_HijackNames.length; i++) {
        var s_Name = a_HijackNames[i];
        if (o_formNode.elements.namedItem(s_Name)) {
            var o_HijackNode = o_formNode.elements.namedItem(s_Name);
            a_HijackRecord.push([o_HijackNode, o_HijackNode.nextSibling]);
            o_formNode.removeChild(o_HijackNode);
        }
    }
    return a_HijackRecord;
};

EWEBTools.RestoreFormStyles = function (o_FormNode, a_HijackRecord) {
    if (!o_FormNode || o_FormNode.nodeType != 1 || o_FormNode.tagName.toLowerCase() != 'form') {
        return;
    }
    if (a_HijackRecord.length > 0) {
        for (var i = a_HijackRecord.length - 1; i >= 0; i--) {
            var o_Node = a_HijackRecord[i][0];
            var o_Sibling = a_HijackRecord[i][1];
            if (o_Sibling) {
                o_FormNode.insertBefore(o_Node, o_Sibling);
            } else {
                o_FormNode.appendChild(o_Node);
            }
        }
    }
};

EWEBTools.GetPositionedAncestor = function (o_Element) {
    var o_CurrentElement = o_Element;

    while (o_CurrentElement != EWEBTools.GetElementDocument(o_CurrentElement).documentElement) {
        if (this.GetCurrentElementStyle(o_CurrentElement, 'position') != 'static') {
            return o_CurrentElement;
        }

        o_CurrentElement = o_CurrentElement.parentNode;
    }

    return null;
};

EWEBTools.SetTimeout = function (o_func, n_Milliseconds, o_ThisObject, a_ParamsArray, o_TimerWindow) {
    return (o_TimerWindow || window).setTimeout(
        function () {
            if (a_ParamsArray) {
                o_func.apply(o_ThisObject, [].concat(a_ParamsArray));
            } else {
                o_func.apply(o_ThisObject);
            }
        },
        n_Milliseconds);
};

EWEBTools.RunFunction = function (o_func, o_ThisObject, a_ParamsArray, o_TimerWindow) {
    if (o_func) {
        this.SetTimeout(o_func, 0, o_ThisObject, a_ParamsArray, o_TimerWindow);
    }
};

EWEBTools.SetElementStyles = function (o_Element, o_StyleDict) {
    var o_Style = o_Element.style;
    for (var s_StyleName in o_StyleDict) {
        o_Style[s_StyleName] = o_StyleDict[s_StyleName];
    }
};

EWEBTools.SetChildrenStyles = function (o_Element, o_StyleDict, a_Tag) {
    if (o_Element.hasChildNodes()) {
        for (var i = 0; i < o_Element.childNodes.length; i++) {
            var o_Node = o_Element.childNodes[i];
            if (o_Node.nodeType == 1) {
                if (!a_Tag || (a_Tag && o_Node.tagName && this.ArrayIndexOf(a_Tag, o_Node.tagName) >= 0)) {
                    this.SetElementStyles(o_Node, o_StyleDict);
                }
                this.SetChildrenStyles(o_Node, o_StyleDict, a_Tag);
            }
        }
    }
};

EWEBTools.ArrayIndexOf = function (a, s) {
    for (var i = 0; i < a.length; i++) {
        if (a[i] == s) {
            return i;
        }
    }
    return -1;
};

EWEBTools.RemoveNode = function (o_Node, o_ExcludeChildren) {
    if (o_ExcludeChildren) {
        var o_Child;
        while ((o_Child = o_Node.firstChild)) {
            o_Node.parentNode.insertBefore(o_Node.removeChild(o_Child), o_Node);
        }
    }

    return o_Node.parentNode.removeChild(o_Node);
};

EWEBTools.GetParentNodeByTag = function (o_Node, s_TagName) {
    return EWEBTools.GetParentNodeByTags(o_Node, [s_TagName]);
};

EWEBTools.GetParentNodeByTags = function (o_Node, a_TagName) {
    while (o_Node && o_Node.tagName && o_Node.tagName != "BODY" && o_Node.getAttribute("id") != "eWebEditor_FixWidth_DIV") {
        var s_TagName = o_Node.tagName.toUpperCase();
        if (EWEBTools.ArrayIndexOf(a_TagName, s_TagName) >= 0) {
            return o_Node;
        }
        o_Node = o_Node.parentNode;
    }
    return null;
};

EWEBTools.GetParentNodeUnder = function (o_OriNode, o_UnderNode) {
    var o_Node = o_OriNode;
    while (o_Node && o_Node.tagName && o_Node.tagName != "BODY") {
        if (o_Node.parentNode == o_UnderNode) {
            return o_Node;
        } else {
            o_Node = o_Node.parentNode;
        }
    }
    return o_OriNode;
};

EWEBTools.GetBodyNode = function () {
    return (config.FixWidth ? EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV") : EWEB.EditorDocument.body);
};

EWEBTools.IsBodyNode = function (o_Node) {
    if (o_Node && o_Node.tagName && ((o_Node.tagName == "BODY") || ((o_Node.tagName == "DIV") && (o_Node.getAttribute("id") == "eWebEditor_FixWidth_DIV")))) {
        return true;
    } else {
        return false;
    }
};

EWEBTools.GetOnlyTempID = function (o_Doc) {
    if (!this._onlynum) {
        this._onlynum = 1;
    }
    var s_ID = "eWebEditor_tmp_" + this._onlynum;
    if (o_Doc.getElementById(s_ID)) {
        this._onlynum++;
        return this.GetOnlyTempID(o_Doc);
    }
    this._onlynum++;
    return s_ID;
};

//////////ie_gecko/////////////

EWEBTools.RegisterDollarFunction = function (o_TargetWindow) {
    o_TargetWindow.$ = function (s_Id) {
        return o_TargetWindow.document.getElementById(s_Id);
    };
};


EWEBTools.GetVoidUrl = function () {
    if (EWEBBrowser.IsIE) {
        if (EWEBBrowser.IsIE6) {
            return "javascript: '';";
        } else {
            return "";
        }
    } else {
        if (EWEBBrowser.IsIE11P) {
            return "";
        } else {
            return "javascript: void(0);";
        }
    }
};


EWEBTools.CancelEvent = function (e) {
    if (EWEBBrowser.IsIE) {
        try {
            e.returnValue = false;
            e.cancelBubble = true;
            e.keyCode = 0;
        } catch (er) {
        }
        return false;
    } else {
        if (e) {
            e.preventDefault();
            e.stopPropagation();
        }
    }
};


EWEBTools.AddEventListener = function (o_SourceObject, s_EventName, v_Listener) {
    if (EWEBBrowser.IsIE) {
        o_SourceObject.attachEvent("on" + s_EventName, v_Listener);
    } else {
        o_SourceObject.addEventListener(s_EventName, v_Listener, false);
    }
};


EWEBTools.RemoveEventListener = function (o_SourceObject, s_EventName, v_Listener) {
    if (EWEBBrowser.IsIE) {
        o_SourceObject.detachEvent("on" + s_EventName, v_Listener);
    } else {
        o_SourceObject.removeEventListener(s_EventName, v_Listener, false);
    }
};


// Returns and object with the "Width" and "Height" properties.
EWEBTools.GetViewPaneSize = function (o_Win) {
    if (EWEBBrowser.IsIE) {
        var oSizeSource;

        var oDoc = o_Win.document.documentElement;
        if (oDoc && oDoc.clientWidth) {				// IE6 Strict Mode
            oSizeSource = oDoc;
        } else {
            oSizeSource = o_Win.document.body;		// Other IEs
        }

        if (oSizeSource) {
            return {Width: oSizeSource.clientWidth, Height: oSizeSource.clientHeight};
        } else {
            return {Width: 0, Height: 0};
        }
    } else {
        return {Width: o_Win.innerWidth, Height: o_Win.innerHeight};
    }
};


EWEBTools.GetScrollPosition = function (o_RelativeWindow) {
    if (EWEBBrowser.IsIE) {
        var oDoc = o_RelativeWindow.document;

        var oPos = {X: oDoc.documentElement.scrollLeft, Y: oDoc.documentElement.scrollTop};

        if (oPos.X > 0 || oPos.Y > 0) {
            return oPos;
        }

        return {X: oDoc.body.scrollLeft, Y: oDoc.body.scrollTop};
    } else {
        return {X: o_RelativeWindow.pageXOffset, Y: o_RelativeWindow.pageYOffset};
    }
};


EWEBTools.SetOpacity = function (o_Element, n_Opacity) {
    if (EWEBBrowser.IsIE && !EWEBBrowser.IsIE10P) {
        n_Opacity = Math.round(n_Opacity * 100);
        o_Element.style.filter = (n_Opacity > 100 ? "" : "progid:DXImageTransform.Microsoft.Alpha(opacity=" + n_Opacity + ")");
    } else {
        o_Element.style.opacity = n_Opacity;
    }
};


EWEBTools.GetCurrentElementStyle = function (o_Element, s_PropertyName) {
    if (EWEBBrowser.IsIE) {
        s_PropertyName = s_PropertyName.replace(/\-(\w)/g, function (strMatch, p1) {
            return p1.toUpperCase();
        });

        return o_Element.currentStyle[s_PropertyName];
    } else {
        try {
            return o_Element.ownerDocument.defaultView.getComputedStyle(o_Element, "").getPropertyValue(s_PropertyName);
        } catch (e) {
        }
    }
};


EWEBTools.DisableSelection = function (o_Element) {

    if (EWEBBrowser.IsAllIE) {
        o_Element.unselectable = 'on';
        o_Element.setAttribute("unselectable", "on");

        var e, i = 0;
        while ((e = o_Element.children[i++])) {
            if (e.getAttribute('eweb_donotdisableselect', 2) == 'true') {
                continue;
            }
            switch (e.tagName) {
                case 'IFRAME' :
                case 'TEXTAREA' :
                case 'INPUT' :
                case 'SELECT' :

                    break;
                default :
                    //e.unselectable = 'on' ;
                    //e.onselectstart = function(){ return(false); };
                    //-ms-user-select: none;  IE10
                    this.DisableSelection(e);
            }
        }
    } else {
        if (EWEBBrowser.IsGecko) {
            //o_Element.style.MozUserSelect		= 'none' ;	// Gecko only.
            o_Element.style.MozUserSelect = '-moz-none';
        } else if (EWEBBrowser.IsIE11P) {
            o_Element.style.msUserSelect = 'none';	// IE11
        } else if (EWEBBrowser.IsSafari) {
            o_Element.style.KhtmlUserSelect = 'none';	// WebKit only.
        } else {
            o_Element.style.userSelect = 'none';	// CSS3 (not supported yet).
        }

        var els = o_Element.getElementsByTagName("*");
        for (var i = 0; i < els.length; i++) {
            switch (els[i].tagName) {
                case "INPUT":
                case "TEXTAREA":
                    if (EWEBBrowser.IsGecko) {
                        els[i].style.MozUserSelect = 'text';
                    } else if (EWEBBrowser.IsIE11P) {
                        els[i].style.msUserSelect = 'text';
                    } else if (EWEBBrowser.IsSafari) {
                        els[i].style.KhtmlUserSelect = 'text';
                    } else {
                        els[i].style.userSelect = 'text';
                    }

                    break;
                default:

            }
        }
    }
};


EWEBTools.SaveStyles = function (o_Element) {
    var a_Data = EWEBTools.ProtectFormStyles(o_Element);
    var o_SavedStyles = new Object();
    if (o_Element.className.length > 0) {
        o_SavedStyles.Class = o_Element.className;
        o_Element.className = '';
    }

    if (EWEBBrowser.IsIE) {
        var sInlineStyle = o_Element.style.cssText;

        if (sInlineStyle.length > 0) {
            o_SavedStyles.Inline = sInlineStyle;
            o_Element.style.cssText = '';
        }

        o_Element.style.position = 'static';

    } else {
        var sInlineStyle = o_Element.getAttribute('style');

        if (sInlineStyle && sInlineStyle.length > 0) {
            o_SavedStyles.Inline = sInlineStyle;
            o_Element.setAttribute('style', '', 0);	// 0 : Case Insensitive
        }
        try {
            o_Element.style.position = 'static';
        } catch (e) {
        }
    }

    EWEBTools.RestoreFormStyles(o_Element, a_Data);
    return o_SavedStyles;
};


EWEBTools.RestoreStyles = function (o_Element, o_SavedStyles) {
    var a_Data = EWEBTools.ProtectFormStyles(o_Element);
    if (EWEBBrowser.IsIE) {
        o_Element.className = o_SavedStyles.Class || '';
        o_Element.style.cssText = o_SavedStyles.Inline || '';

    } else {
        o_Element.className = o_SavedStyles.Class || '';

        if (o_SavedStyles.Inline) {
            o_Element.setAttribute('style', o_SavedStyles.Inline, 0);	// 0 : Case Insensitive
        } else {
            o_Element.removeAttribute('style', 0);
        }
    }

    EWEBTools.RestoreFormStyles(o_Element, a_Data);
};


EWEBTools.FormatDate = function (v_Date, s_Format) {
    var o_Match = {
        "M+": v_Date.getMonth() + 1,
        "d+": v_Date.getDate(),
        "h+": v_Date.getHours(),
        "m+": v_Date.getMinutes(),
        "s+": v_Date.getSeconds(),
        "q+": Math.floor((v_Date.getMonth() + 3) / 3),
        "S+": v_Date.getMilliseconds()
    };
    if (/(y+)/i.test(s_Format)) {
        s_Format = s_Format.replace(RegExp.$1, (v_Date.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o_Match) {
        if (new RegExp("(" + k + ")").test(s_Format)) {
            var s = "00" + o_Match[k];
            var n = RegExp.$1.length;
            var v = (n == 1) ? o_Match[k] : s.substr(s.length - n);
            s_Format = s_Format.replace(RegExp.$1, v);
        }
    }
    return s_Format;
};


//////////////////////////////////////////////////


var EWEBSelection = new Object();

EWEBSelection.GetParentElementByTags = function (a_Tag) {
    var _IndexOf = function (a, s) {
        for (var i = 0; i < a.length; i++) {
            if (a[i] == s) {
                return i;
            }
        }
        return -1;
    };

    var el = null;
    if (EWEBSelection.GetType() != "Control") {
        el = EWEBSelection.GetParentElement();
        if (!el || el.tagName == "HTML") {
            return null;
        }
        while (_IndexOf(a_Tag, el.tagName.toUpperCase()) < 0) {
            if (el.tagName.toUpperCase() == "BODY") {
                el = null;
                break;
            }
            el = el.parentNode;
        }
    }

    if (el && el.tagName.toUpperCase() == "DIV" && el.getAttribute("id") == "eWebEditor_FixWidth_DIV") {
        el = null;
    }

    return el;
};

EWEBSelection.GetStartCursorParent = function () {
    var p = null;
    if (EWEBBrowser.IsIE) {
        var o_Sel = EWEB.EditorDocument.selection;
        if (o_Sel && o_Sel.type && o_Sel.type == "None") {
            var o_Rng = o_Sel.createRange();
            if (o_Rng) {
                var o_RngParent = o_Rng.parentElement();
                var o_Node = EWEBTools.GetParentNodeByTags(o_RngParent, ["P", "DIV", "TD", "TH", "TABLE"]);
                if (o_Node) {
                    var s_Tag = o_Node.tagName.toUpperCase();
                    if (EWEBTools.ArrayIndexOf(["P", "DIV"], s_Tag) >= 0) {
                        if ((config.FixWidth) && (s_Tag == "DIV") && (o_Node.getAttribute("id") == "eWebEditor_FixWidth_DIV")) {
                        } else {
                            var o_TmpRng = EWEB.EditorDocument.body.createTextRange();
                            o_TmpRng.moveToElementText(o_Node);
                            if (o_TmpRng.collapse) {
                                o_TmpRng.collapse(true);
                                if (o_Rng.isEqual(o_TmpRng)) {
                                    p = o_Node;
                                }
                            }
                        }
                    }
                }
            }
        }

    } else {
        var o_Sel = EWEB.EditorWindow.getSelection();
        if (o_Sel && o_Sel.rangeCount == 1) {
            var o_Rng = o_Sel.getRangeAt(0);
            if (o_Rng.collapsed && o_Rng.startOffset == 0) {
                var o_Node = o_Rng.startContainer;
                while (o_Node) {
                    if (o_Node.nodeType == 1) {
                        var s_Tag = o_Node.tagName.toUpperCase();
                        if (EWEBTools.ArrayIndexOf(["P", "DIV"], s_Tag) >= 0) {
                            if ((config.FixWidth) && (s_Tag == "DIV") && (o_Node.getAttribute("id") == "eWebEditor_FixWidth_DIV")) {
                                o_Node = null;
                            } else {
                                p = o_Node;
                                o_Node = null;
                            }
                        } else if (EWEBTools.ArrayIndexOf(["TABLE", "TD", "TH", "BODY"], s_Tag) >= 0) {
                            o_Node = null;
                        } else if (o_Node.previousSibling) {
                            o_Node = null;
                        } else {
                            o_Node = o_Node.parentNode;
                        }
                    } else {
                        if (o_Node.previousSibling) {
                            o_Node = null;
                        } else {
                            o_Node = o_Node.parentNode;
                        }
                    }
                }
            }
        }
    }
    return p;
};

EWEBSelection.GetType = function () {
    if (EWEBBrowser.IsIE) {
        try {
            var s_ieType = EWEBSelection.GetSelection().type;
            if (s_ieType == 'Control' || s_ieType == 'Text') {
                return s_ieType;
            }

            if (this.GetSelection().createRange().parentElement) {
                return 'Text';
            }
        } catch (e) {
            // Nothing to do
        }

        return 'None';

    } else {

        var s_Type = 'Text';
        var o_StyleObjectElements = {img: 1, hr: 1, li: 1, table: 1, tr: 1, td: 1, embed: 1, object: 1, ol: 1, ul: 1};

        var o_Sel;
        try {
            o_Sel = this.GetSelection();
        } catch (e) {
        }

        if (o_Sel && o_Sel.rangeCount == 1) {
            var o_Rng = o_Sel.getRangeAt(0);
            this._IE11ImgRange(o_Rng);
            if (o_Rng.startContainer == o_Rng.endContainer
                && (o_Rng.endOffset - o_Rng.startOffset) == 1
                && o_Rng.startContainer.nodeType == 1
                && o_StyleObjectElements[o_Rng.startContainer.childNodes[o_Rng.startOffset].nodeName.toLowerCase()]) {
                s_Type = 'Control';
            }

        }

        return s_Type;
    }
};

EWEBSelection._IE11ImgRange = function (o_Rng) {
    if (EWEBBrowser.IsIE11P && (!o_Rng.collapsed)) {
        var o_Start = o_Rng.startContainer;
        var o_Start2;
        if (o_Start.nodeType == 3) {
            if (o_Rng.startOffset == o_Start.nodeValue.length) {
                o_Start2 = o_Start.nextSibling;
            }
        } else {
            o_Start2 = o_Start.childNodes[o_Rng.startOffset];
        }

        var o_End = o_Rng.endContainer;
        var o_End2;
        if (o_End.nodeType == 3) {
            if (o_Rng.endOffset == 0) {
                o_End2 = o_End.previousSibling;
            }
        } else {
            if (o_Rng.endOffset > 0) {
                o_End2 = o_End.childNodes[o_Rng.endOffset - 1];
            }
        }
        if (o_Start2 && o_Start2.nodeType == 1 && o_Start2 == o_End2 && o_Start2.tagName == "IMG") {
            o_Rng.selectNode(o_Start2);
        }
    }
};


EWEBSelection.GetSelectedElement = function () {
    if (EWEBBrowser.IsIE) {
        if (this.GetType() == 'Control') {
            var oRange = this.GetSelection().createRange();

            if (oRange && oRange.item) {
                return this.GetSelection().createRange().item(0);
            }
        }
        return null;
    } else {
        var selection = !!EWEB.EditorWindow && this.GetSelection();
        if (!selection || selection.rangeCount < 1) {
            return null;
        }

        var range = selection.getRangeAt(0);
        this._IE11ImgRange(range);
        if (range.startContainer != range.endContainer || range.startContainer.nodeType != 1 || range.startOffset != range.endOffset - 1) {
            return null;
        }

        var node = range.startContainer.childNodes[range.startOffset];
        if (node.nodeType != 1) {
            return null;
        }

        return node;

    }
};


EWEBSelection.GetParentElement = function () {
    if (EWEBBrowser.IsIE) {
        switch (this.GetType()) {
            case 'Control' :
                var el = EWEBSelection.GetSelectedElement();
                return el ? el.parentElement : null;

            case 'None' :
                return null;

            default :
                if (EWEBBrowser.IsIE9 || EWEBBrowser.IsIE10) {
                    return this.GetSelection().createRange().parentElement();
                } else {
                    var o_SelRange = this.GetSelection().createRange();
                    var el = o_SelRange.parentElement();
                    var o_ParentRange = EWEB.EditorDocument.body.createTextRange();
                    o_ParentRange.moveToElementText(el);
                    if (o_SelRange.text.length > o_ParentRange.text.length) {
                        return el.parentNode;
                    } else {
                        return el;
                    }
                }
        }

    } else {
        if (this.GetType() == 'Control') {
            return EWEBSelection.GetSelectedElement().parentNode;
        } else {
            var oSel = this.GetSelection();
            if (oSel && oSel.rangeCount > 0) {
                var oRange = oSel.getRangeAt(0);
                if (oSel.anchorNode && oSel.anchorNode == oSel.focusNode) {

                    if (oSel.anchorNode.nodeType == 3) {
                        return oSel.anchorNode.parentNode;
                    } else {
                        return oSel.anchorNode;
                    }
                }

                return oRange.commonAncestorContainer;

            }
        }
        return null;
    }
};


EWEBSelection.GetBoundaryParentElement = function (b_startBoundary) {
    if (EWEBBrowser.IsIE) {
        switch (this.GetType()) {
            case 'Control' :
                var el = EWEBSelection.GetSelectedElement();
                return el ? el.parentElement : null;

            case 'None' :
                return null;

            default :
                var o_Doc = EWEB.EditorDocument;

                var o_Range = o_Doc.selection.createRange();
                o_Range.collapse(b_startBoundary !== false);

                var el = o_Range.parentElement();
                return EWEBTools.GetElementDocument(el) == o_Doc ? el : null;
        }

    } else {
        if (!EWEB.EditorWindow) {
            return null;
        }
        if (this.GetType() == 'Control') {
            return EWEBSelection.GetSelectedElement().parentNode;
        } else {
            var oSel = this.GetSelection();
            if (oSel && oSel.rangeCount > 0) {
                var o_Range = oSel.getRangeAt(b_startBoundary ? 0 : (oSel.rangeCount - 1));

                var o_Element = b_startBoundary ? o_Range.startContainer : o_Range.endContainer;

                return (o_Element.nodeType == 1 ? o_Element : o_Element.parentNode);
            }
        }
        return null;
    }
};


EWEBSelection.SelectNode = function (o_Node) {
    if (EWEBBrowser.IsIE) {
        EWEB.Focus();
        this.GetSelection().empty();
        var oRange;
        try {
            oRange = EWEB.EditorDocument.body.createControlRange();
            oRange.addElement(o_Node);
            oRange(0).select();
        } catch (e) {
            oRange = EWEB.EditorDocument.body.createTextRange();
            oRange.moveToElementText(o_Node);
            oRange.select();
        }
    } else {
        var oRange = EWEB.EditorDocument.createRange();
        oRange.selectNode(o_Node);

        var oSel = this.GetSelection();
        oSel.removeAllRanges();
        oSel.addRange(oRange);
    }
};

EWEBSelection.Empty = function () {
    if (EWEBBrowser.IsIE) {
        this.GetSelection().empty();
    } else {
        this.GetSelection().removeAllRanges();
    }
};


EWEBSelection.Collapse = function (b_ToStart) {
    if (EWEBBrowser.IsIE) {
        EWEB.Focus();
        if (this.GetType() == 'Text') {
            var oRange = this.GetSelection().createRange();
            oRange.collapse(b_ToStart == null || b_ToStart === true);
            oRange.select();
        }
    } else {
        var oSel = this.GetSelection();

        if (b_ToStart == null || b_ToStart === true) {
            oSel.collapseToStart();
        } else {
            oSel.collapseToEnd();
        }
    }
};


// The "nodeTagName" parameter must be Upper Case.
EWEBSelection.HasAncestorNode = function (s_nodeTagName) {
    if (EWEBBrowser.IsIE) {
        var oContainer;

        if (this.GetSelection().type == "Control") {
            oContainer = this.GetSelectedElement();
        } else {
            var oRange = this.GetSelection().createRange();
            oContainer = oRange.parentElement();
        }

        while (oContainer) {
            if (oContainer.nodeName.IEquals(s_nodeTagName)) return true;
            oContainer = oContainer.parentNode;
        }

        return false;

    } else {
        var oContainer = this.GetSelectedElement();
        if (!oContainer && EWEB.EditorWindow) {
            try {
                oContainer = this.GetSelection().getRangeAt(0).startContainer;
            }
            catch (e) {
            }
        }

        while (oContainer) {
            if (oContainer.nodeType == 1 && oContainer.nodeName.IEquals(s_nodeTagName)) return true;
            oContainer = oContainer.parentNode;
        }

        return false;

    }
};


// The "nodeTagName" parameter must be UPPER CASE.
// It can be also an array of names
EWEBSelection.MoveToAncestorNode = function (s_nodeTagName) {
    if (EWEBBrowser.IsIE) {
        var oNode, oRange;

        if (!EWEB.EditorDocument)
            return null;

        if (this.GetSelection().type == "Control") {
            oRange = this.GetSelection().createRange();
            for (i = 0; i < oRange.length; i++) {
                if (oRange(i).parentNode) {
                    oNode = oRange(i).parentNode;
                    break;
                }
            }
        } else {
            oRange = this.GetSelection().createRange();
            oNode = oRange.parentElement();
        }

        while (oNode && !oNode.nodeName.Equals(s_nodeTagName)) {
            oNode = oNode.parentNode;
        }

        return oNode;
    } else {

        var oNode;

        var oContainer = this.GetSelectedElement();
        if (!oContainer) {
            oContainer = this.GetSelection().getRangeAt(0).startContainer;
        }

        while (oContainer) {
            if (oContainer.nodeName.IEquals(s_nodeTagName)) {
                return oContainer;
            }

            oContainer = oContainer.parentNode;
        }
        return null;

    }
};


EWEBSelection.Delete = function () {
    var oSel = this.GetSelection();
    if (EWEBBrowser.IsIE) {
        if (oSel.type.toLowerCase() != "none") {
            oSel.clear();
        }
    } else {
        for (var i = 0; i < oSel.rangeCount; i++) {
            oSel.getRangeAt(i).deleteContents();
        }
    }
    return oSel;
};


EWEBSelection.GetSelection = function () {
    if (EWEBBrowser.IsIE) {
        this.Restore();
        return EWEB.EditorDocument.selection;
    } else {
        return EWEB.EditorWindow.getSelection();
    }
};


EWEBSelection.Save = function (b_Lock) {
    if (!EWEBBrowser.IsAllIE) {
        return;
    }

    var o_Win, o_Doc;
    if (EWEB.CurrMode == "EDIT") {
        o_Win = EWEB.EditorWindow;
        o_Doc = EWEB.EditorDocument;
    } else {
        o_Win = window;
        o_Doc = document;
    }

    if (!o_Doc) {
        return;
    }

    if (this.locked) {
        return;
    }
    this.locked = !!b_Lock;

    var o_Sel;
    var o_Rng;


    if (EWEBBrowser.IsIE11P) {
        o_Sel = o_Win.getSelection();
        if (o_Sel.rangeCount > 0) {
            o_Rng = o_Sel.getRangeAt(0);

        }
    } else {
        o_Sel = o_Doc.selection;
        if (o_Sel) {
            o_Rng = o_Sel.createRange();
        }
    }

    // Ensure that the range comes from the editor document.
    if (this._GetSelectionDocument(o_Rng) != o_Doc) {
        o_Rng = null;
    }

    this.SelectionData = o_Rng;
};

EWEBSelection._GetSelectionDocument = function (o_Rng) {
    if (!o_Rng) {
        return null;
    } else if (o_Rng.item) {
        return EWEBTools.GetElementDocument(o_Rng.item(0));
    } else if (o_Rng.parentElement) {
        return EWEBTools.GetElementDocument(o_Rng.parentElement());
    } else if (o_Rng.startContainer) {
        return EWEBTools.GetElementDocument(o_Rng.startContainer);
    }
};

EWEBSelection.Restore = function (b_NotCheckExist) {
    if (!EWEBBrowser.IsAllIE) {
        return;
    }

    if (this.SelectionData) {
        EWEB.IsSelectionChangeLocked = true;

        try {
            var o_Win, o_Doc, o_Sel, o_Rng;
            if (EWEB.CurrMode == "EDIT") {
                o_Win = EWEB.EditorWindow;
                o_Doc = EWEB.EditorDocument;
            } else {
                o_Win = window;
                o_Doc = document;
            }
            if (EWEBBrowser.IsIE11P) {
                o_Sel = o_Win.getSelection();
                if (o_Sel.rangeCount > 0) {
                    o_Rng = o_Sel.getRangeAt(0);
                }
            } else {
                o_Sel = o_Doc.selection;
                if (o_Sel) {
                    o_Rng = o_Sel.createRange();
                }
            }

            if (!b_NotCheckExist && this._GetSelectionDocument(o_Rng) == o_Doc) {
                EWEB.IsSelectionChangeLocked = false;
                return;
            }

            if (EWEBBrowser.IsIE11P) {
                o_Sel.removeAllRanges();
                o_Sel.addRange(this.SelectionData);
            } else {
                this.SelectionData.select();
            }

        }
        catch (e) {
        }

        EWEB.IsSelectionChangeLocked = false;
    }
};


EWEBSelection.Release = function () {
    if (!EWEBBrowser.IsAllIE) {
        return;
    }

    this.locked = false;
    delete this.SelectionData;
};


EWEBSelection.IsCross = function (el) {
    if (EWEBBrowser.IsIE) {
        var o_SourceRng, o_TempRng;
        o_SourceRng = EWEB.EditorDocument.selection.createRange();
        o_TempRng = EWEB.EditorDocument.body.createTextRange();

        o_TempRng.moveToElementText(el);
        if (o_SourceRng.inRange(o_TempRng)) {
            return true;
        } else {
            if (((o_SourceRng.compareEndPoints("StartToEnd", o_TempRng) < 0) && (o_SourceRng.compareEndPoints("StartToStart", o_TempRng) > 0)) || ((o_SourceRng.compareEndPoints("EndToStart", o_TempRng) > 0) && (o_SourceRng.compareEndPoints("EndToEnd", o_TempRng) < 0))) {
                return true;
            }
        }
        return false;
    } else {
        var o_Sel = EWEBSelection.GetSelection();
        if (o_Sel.rangeCount < 1) {
            return false;
        }
        var o_SourceRng = o_Sel.getRangeAt(0);
        var o_TempRng = EWEB.EditorDocument.createRange();

        o_TempRng.selectNodeContents(el);
        if ((o_TempRng.compareBoundaryPoints(Range.START_TO_END, o_SourceRng) >= 0) && (o_TempRng.compareBoundaryPoints(Range.END_TO_START, o_SourceRng) <= 0)) {
            return true;
        }
        return false;

    }
};

EWEBSelection.GetCrossElementsByTags = function (a_Tag, b_OnlyOne) {
    var o_FixDiv = config.FixWidth ? EWEB.EditorDocument.getElementById("eWebEditor_FixWidth_DIV") : null;
    var a_Ret = new Array();
    var o_Parent = EWEBSelection.GetParentElementByTags(a_Tag);
    if (o_Parent == o_FixDiv) {
        o_Parent = null;
    }

    var o_ParentBody = (o_Parent) ? o_Parent : EWEB.EditorDocument.body;

    for (var i = 0; i < a_Tag.length; i++) {
        var a = o_ParentBody.getElementsByTagName(a_Tag[i]);
        for (var j = 0; j < a.length; j++) {
            if (EWEBSelection.IsCross(a[j])) {
                if (a[j] != o_FixDiv) {
                    a_Ret[a_Ret.length] = a[j];
                    if (b_OnlyOne) {
                        return a_Ret;
                    }
                }
            }
        }
    }

    if (a_Ret.length <= 0) {
        if (o_Parent) {
            a_Ret[0] = o_Parent;
        }
    }

    return a_Ret;
};

EWEBSelection._QueryFontName = function () {
    if (EWEBSelection.GetType() == "Control") {
        return "";
    }

    var r = "";
    if (EWEBBrowser.IsAllIE) {
        try {
            r = EWEB.EditorDocument.queryCommandValue("FontName");
        } catch (e) {
            r = "";
        }
    } else {
        var rngParent = EWEBSelection.GetParentElement();
        if (!rngParent) {
            return "";
        }

        var sourceRange = EWEBSelection.GetSelection().getRangeAt(0);
        var rngTemp = EWEB.EditorDocument.createRange();
        r = EWEBTools.GetCurrentElementStyle(rngParent, "font-family");
        var els = rngParent.getElementsByTagName("*");
        for (var i = 0; i < els.length; i++) {
            var el = els[i];
            rngTemp.selectNodeContents(el);
            if ((rngTemp.compareBoundaryPoints(Range.START_TO_END, sourceRange) >= 0) && (rngTemp.compareBoundaryPoints(Range.END_TO_START, sourceRange) <= 0)) {
                var v = EWEBTools.GetCurrentElementStyle(el, "font-family");
                if (r != v) {
                    r = "";
                    break;
                }
            }
        }
    }

    return r;
};

EWEBSelection.QueryFontSize = function () {
    if (EWEBSelection.GetType() == "Control") {
        return "";
    }

    var _GetSeletionFontSizeCss = function () {
        if (EWEBSelection.GetType() == "Control") {
            return "";
        }

        var r = "";
        if (EWEBBrowser.IsIE) {
            var rng = EWEBSelection.GetSelection().createRange();
            if (rng.text.length <= 1) {
                return rng.parentElement().currentStyle.fontSize;
            }

            var html = rng.htmlText;
            html = html.replace(/<[^>]+>/gi, " ");
            html = html.replace(/(&nbsp\;|\s)+/gi, " ");
            html = html.replace(/(^\s*)|(\s*$)/gi, "");
            var a_Txt = html.split(" ");

            var s_BookMark = rng.getBookmark();
            rng.collapse();
            for (var i = 0; i < a_Txt.length; i++) {
                if (!a_Txt[i]) {
                    continue;
                }
                var b = rng.findText(a_Txt[i]);
                if (b) {
                    var v = rng.parentElement().currentStyle.fontSize;
                    if ((r != "") && (r != v)) {
                        r = "";
                        break;
                    }
                    r = v;
                    rng.collapse(false);
                } else {
                    break;
                }
            }

            rng.moveToBookmark(s_BookMark);
        } else {

            var rngParent = EWEBSelection.GetParentElement();
            if (!rngParent) {
                return "";
            }

            var sourceRange = EWEBSelection.GetSelection().getRangeAt(0);
            var rngTemp = EWEB.EditorDocument.createRange();
            r = EWEBTools.GetCurrentElementStyle(rngParent, "font-size");
            var els = rngParent.getElementsByTagName("*");
            for (var i = 0; i < els.length; i++) {
                var el = els[i];
                rngTemp.selectNodeContents(el);
                if ((rngTemp.compareBoundaryPoints(Range.START_TO_END, sourceRange) >= 0) && (rngTemp.compareBoundaryPoints(Range.END_TO_START, sourceRange) <= 0)) {
                    var v = EWEBTools.GetCurrentElementStyle(el, "font-size");
                    if (r != v) {
                        r = "";
                        break;
                    }
                }
            }

            if (r != "") {
                //px to pt
                //r = (parseFloat(r)*3/4) + "pt";
                //alert(r);
            }
        }

        return r;
    };


    var v;
    if (EWEBBrowser.IsIE) {
        v = EWEB.EditorDocument.queryCommandValue("FontSize");
        if (v) {
            v = _Fontsize2Css(v);
        }
    }
    if (!v) {
        v = _GetSeletionFontSizeCss();
    }

    return v;
};


function getSelectedHTML() {
    switch (EWEB.CurrMode) {
        case "EDIT":
            var s_Html = "";
            if (window.getSelection) {
                var n_RangeCount = EWEB.EditorWindow.getSelection().rangeCount;
                for (var i = 0; i < n_RangeCount; i++) {
                    var r = EWEB.EditorWindow.getSelection().getRangeAt(i);
                    var c = document.createElement('div');
                    c.appendChild(r.cloneContents());
                    s_Html += c.innerHTML;
                }
            } else if (document.getSelection) {
                var r = EWEB.EditorDocument.getSelection().getRangeAt(0);
                var c = document.createElement('div');
                c.appendChild(r.cloneContents());
                s_Html = c.innerHTML;
            } else if (document.selection) {
                s_Html = EWEB.EditorDocument.selection.createRange().htmlText;
            }
            s_Html = EWEBFake.Fake2Normal(s_Html);
            return s_Html;
            break;
        case "TEXT":
            var o_Textarea = EWEB.EditorTextarea;
            var s_Txt = "";
            if (window.getSelection) {
                s_Txt = o_Textarea.value.substring(o_Textarea.selectionStart, o_Textarea.selectionEnd);
            } else {
                o_Textarea.focus();
                var o_Sel = document.selection.createRange();
                s_Txt = o_Sel.text;
            }
            return HTMLEncode(s_Txt);
            break;
        default:
            return "";
    }
}

function getSelectedText() {
    switch (EWEB.CurrMode) {
        case "EDIT":
            if (window.getSelection) {
                return EWEB.EditorWindow.getSelection().toString();
            } else if (document.getSelection) {
                return EWEB.EditorDocument.getSelection().toString();
            } else if (document.selection) {
                return EWEB.EditorDocument.selection.createRange().text;
            }
            break;
        case "TEXT":
            var o_Textarea = EWEB.EditorTextarea;
            if (window.getSelection) {
                return o_Textarea.value.substring(o_Textarea.selectionStart, o_Textarea.selectionEnd);
            } else {
                o_Textarea.focus();
                var o_Sel = document.selection.createRange();
                return o_Sel.text;
            }
            break;
        default:
            return "";
    }
}

function getSelectedElement() {
    var o_Ret;
    if (EWEB.CurrMode == "EDIT") {
        o_Ret = EWEBSelection.GetParentElement();
    }
    if (o_Ret) {
        var s_Attr = o_Ret.getAttribute("_ewebeditor_fake_tag", 2);
        if (s_Attr) {
            o_Ret = null;
        }
    }

    return o_Ret;
}

function getAllImageSrc() {
    var s_Html = getHTML();
    var re = /<img(?=[\s])[^>]*?\ssrc\s*=\s*([\'\"])([^\'\">]+?)\1[^>]*?>/gi;
    var m;
    var a_Src = new Array();
    while ((m = re.exec(s_Html)) != null) {
        a_Src.push(m[2]);
    }

    return a_Src;
}

function getMode() {
    return EWEB.CurrMode;
}

function _SetTitleImage() {
    if (EWEB.CurrMode != "EDIT") {
        return;
    }
    if (EWEBSelection.GetType() == "Control") {
        var o_Control = EWEBSelection.GetSelectedElement();
        if (o_Control.tagName.toUpperCase() == "IMG") {
            if (!EWEBFake._IsFakeElement(o_Control)) {
                var s_Src = EWEBFake._GetProtectAttribute(o_Control, "src");
                _FireEvent({flag: "SetTitleImage", src: s_Src, obj: o_Control});
            }
        }
    }

}

function update() {
    var s_Html = getHTML();
    if (EWEB.LinkType == "INPUT") {
        EWEB.LinkField.value = s_Html;
    } else {
        EWEB.LinkField.innerHTML = s_Html;
    }
}

function _ParseInt0(v) {
    var n = parseInt(v);
    if (isNaN(n)) {
        n = 0;
    }
    return n;
}


var EWEBAjax = (function () {

    var _CreateXHR = function () {
        try {
            return new window.XMLHttpRequest();
        } catch (e) {
            try {
                return new window.ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) {
                alert("XHR is not supported!");
            }
        }
    };

    var _EncodeFormData = function (o_Data) {
        if (!o_Data) return '';
        var a_Pairs = [];
        for (var s_Name in o_Data) {
            var s_Value = o_Data[s_Name] + "";
            s_Name = s_Name;
            s_Value = encodeURIComponent(s_Value);
            a_Pairs.push(s_Name + '=' + s_Value);
        }
        return a_Pairs.join('&');
    };

    return {

        _GetData: function (s_Type, s_Url, b_Async, o_PostData, f_Callback) {
            var xhr = _CreateXHR();
            if (!xhr) {
                return;
            }

            xhr.open(s_Type, s_Url, b_Async);

            var _callback = function () {
                if (xhr.readyState == 4) {
                    if (!_callback) {
                        return
                    }
                    _callback = null;
                    var o_Ret = {};
                    o_Ret["XhrStatus"] = xhr.status;
                    if (xhr.status == 200) {
                        o_Ret["Data"] = eval("(" + xhr.responseText + ")");
                    } else {
                        o_Ret["Data"] = {};
                    }

                    if (!b_Async) {
                        return o_Ret;
                    } else {
                        if (f_Callback) {
                            f_Callback(o_Ret);
                        }
                    }
                }
            };

            if (b_Async) {
                xhr.onreadystatechange = _callback;
            }

            try {
                if (s_Type == "post") {
                    xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
                    xhr.send(_EncodeFormData(o_PostData));
                } else {
                    xhr.send(null);
                }
            } catch (e) {
                //console.log(e);
            }

            if (!b_Async || xhr.readyState == 4) {
                return _callback();
            }
        }

    }
})();


var EWEBCommandParagraph = (function () {

    var _GetCrossParagraph = function (s_Cmd, b_IsQuery) {
        s_Cmd = s_Cmd.toLowerCase();

        var b_OnlyOne = false;
        if (b_IsQuery && s_Cmd == "textindent") {
            b_OnlyOne = true;
        }

        var a;
        if (EWEBSelection.GetType() == "Control") {
            a = new Array();
            var el = EWEBTools.GetParentNodeByTags(EWEBSelection.GetSelectedElement(), ["P", "DIV"]);
            if (el) {
                a[0] = el;
            }
        } else {
            a = EWEBSelection.GetCrossElementsByTags(["P", "DIV"], b_OnlyOne);
        }

        var s_CommandValue = "";
        if (a.length > 0) {
            switch (s_Cmd) {
                case "textindent":
                    if (a[0].style.textIndent) {
                        s_CommandValue = "yes";
                    }
                    break;
                case "lineheight":
                case "margintop":
                case "marginbottom":
                    s_CommandValue = _GetStyleValue(a[0], s_Cmd);
                    for (var i = 1; i < a.length; i++) {
                        if (_GetStyleValue(a[i], s_Cmd) != s_CommandValue) {
                            s_CommandValue = "unknow";
                            break;
                        }
                    }
                    break;
            }
        }

        return {"V": s_CommandValue, "P": a};
    };

    var _GetStyleValue = function (el, s_Cmd) {
        switch (s_Cmd) {
            case "lineheight":
                return el.style.lineHeight;
                break;
            case "margintop":
                return el.style.marginTop;
                break;
            case "marginbottom":
                return el.style.marginBottom;
                break;
        }
    };

    var _Options = {
        "lineheight": ["", "1", "1.5", "1.75", "2", "3", "4", "5"],
        "margintop": ["", "5px", "10px", "15px", "20px", "25px", "30px"],
        "marginbottom": ["", "5px", "10px", "15px", "20px", "25px", "30px"]
    };

    return {

        _GetOptions: function (s_Cmd) {
            return _Options[s_Cmd.toLowerCase()];
        },

        _QueryCommandValue: function (s_Cmd) {
            var o_Ret = _GetCrossParagraph(s_Cmd, true);
            return o_Ret["V"];
        },

        _Execute: function (s_Cmd, s_Value) {
            s_Cmd = s_Cmd.toLowerCase();
            var o_Ret = _GetCrossParagraph(s_Cmd);
            var a = o_Ret["P"];
            if (a.length <= 0) {
                return;
            }

            if (s_Cmd == "textindent") {
                if (s_Value === undefined) {
                    s_Value = (o_Ret["V"]) ? "" : "2em";
                }
            }
            for (var i = 0; i < a.length; i++) {
                if (s_Cmd == "textindent") {
                    a[i].style.textIndent = s_Value;
                } else if (s_Cmd == "lineheight") {
                    a[i].style.lineHeight = s_Value;
                    EWEBTools.SetChildrenStyles(a[i], {"lineHeight": ""}, ["SPAN"]);
                } else if (s_Cmd == "margintop") {
                    a[i].style.marginTop = s_Value;
                } else if (s_Cmd == "marginbottom") {
                    a[i].style.marginBottom = s_Value;
                }
            }

            if (s_Cmd == "textindent") {
                EWEB.Focus();
                EWEBToolbar.CheckTBStatus();
            }
            _FireChange();
        }
    }
})();
